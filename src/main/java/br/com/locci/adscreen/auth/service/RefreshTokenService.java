package br.com.locci.adscreen.auth.service;

import br.com.locci.adscreen.auth.entity.RefreshToken;
import br.com.locci.adscreen.auth.repository.RefreshTokenRepository;
import br.com.locci.adscreen.user.entity.AppUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.HexFormat;

@Service
@Transactional
public class RefreshTokenService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final RefreshTokenRepository refreshTokenRepository;
    private final long refreshTokenExpiration;

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository,
            @Value("${app.jwt.refresh-token-expiration}") long refreshTokenExpiration
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public RefreshToken create(AppUser user) {
        revokeAll(user);

        String token = generateToken();
        Instant expiresAt = Instant.now().plusSeconds(refreshTokenExpiration);

        RefreshToken refreshToken = RefreshToken.create(user, token, expiresAt);
        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional(readOnly = true)
    public RefreshToken findByToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Refresh token não encontrado."));
    }

    public void revokeAll(AppUser user) {
        refreshTokenRepository.revokeAllByUserId(user.getId());
    }

    private String generateToken() {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }
}
