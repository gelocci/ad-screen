package br.com.locci.adscreen.auth.controller;

import br.com.locci.adscreen.auth.dto.LoginRequest;
import br.com.locci.adscreen.auth.dto.LoginResponse;
import br.com.locci.adscreen.auth.entity.RefreshToken;
import br.com.locci.adscreen.auth.service.JwtService;
import br.com.locci.adscreen.auth.service.RefreshTokenService;
import br.com.locci.adscreen.user.entity.AppUser;
import br.com.locci.adscreen.user.service.AppUserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AppUserService appUserService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthController(
            AppUserService appUserService,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            RefreshTokenService refreshTokenService
    ) {
        this.appUserService = appUserService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/login")
    public LoginResponse login(
            @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        AppUser user = appUserService.findByEmail(request.email());

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Credenciais inválidas.");
        }

        String accessToken = jwtService.generateAccessToken(user.getEmail());
        RefreshToken refreshToken = refreshTokenService.create(user);

        addAccessTokenCookie(response, accessToken);
        addRefreshTokenCookie(response, refreshToken.getToken());

        return new LoginResponse(user.getEmail(), user.getName());
    }

    @PostMapping("/refresh")
    public void refresh(HttpServletRequest request, HttpServletResponse response) {
        String token = extractCookie(request, "refresh_token");

        if (token == null) {
            throw new IllegalArgumentException("Refresh token não encontrado.");
        }

        RefreshToken refreshToken = refreshTokenService.findByToken(token);

        if (!refreshToken.isValid()) {
            throw new IllegalArgumentException("Refresh token inválido ou expirado.");
        }

        String newAccessToken = jwtService.generateAccessToken(
                refreshToken.getUser().getEmail()
        );

        addAccessTokenCookie(response, newAccessToken);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String token = extractCookie(request, "refresh_token");

        if (token != null) {
            refreshTokenService.findByToken(token);
            AppUser user = refreshTokenService.findByToken(token).getUser();
            refreshTokenService.revokeAll(user);
        }

        clearCookie(response, "access_token");
        clearCookie(response, "refresh_token");
    }

    private void addAccessTokenCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("access_token", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(900);
        response.addCookie(cookie);
    }

    private void addRefreshTokenCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("refresh_token", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/auth/refresh");
        cookie.setMaxAge(604800);
        response.addCookie(cookie);
    }

    private void clearCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    private String extractCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(c -> name.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
