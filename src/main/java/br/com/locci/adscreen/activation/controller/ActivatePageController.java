package br.com.locci.adscreen.activation.controller;

import br.com.locci.adscreen.activation.dto.ActivationStatusResponse;
import br.com.locci.adscreen.activation.entity.ActivationSession;
import br.com.locci.adscreen.activation.service.ActivationSessionService;
import br.com.locci.adscreen.activation.service.QrCodeService;
import br.com.locci.adscreen.screen.entity.Screen;
import br.com.locci.adscreen.screen.service.ScreenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;
import java.util.UUID;

@Controller
public class ActivatePageController {

    private static final String SCREEN_COOKIE_NAME = "screenId";
    private static final int SCREEN_COOKIE_MAX_AGE_SECONDS = 60 * 60 * 24 * 365;

    private final ScreenService screenService;
    private final ActivationSessionService activationSessionService;
    private final QrCodeService qrCodeService;

    public ActivatePageController(
            ScreenService screenService,
            ActivationSessionService activationSessionService,
            QrCodeService qrCodeService
    ) {
        this.screenService = screenService;
        this.activationSessionService = activationSessionService;
        this.qrCodeService = qrCodeService;
    }

    @GetMapping("/activate")
    public String activate(
            HttpServletRequest request,
            HttpServletResponse response,
            Model model
    ) {
        Screen screen = resolveOrCreateScreen(request, response);

        if (screen.getOrganization() != null) {
            model.addAttribute("status", "ACTIVE");
            model.addAttribute("screenName", screen.getName());
            return "activate/activate";
        }

        ActivationSession session = activationSessionService
                .findPendingByScreenId(screen.getId())
                .orElseGet(() -> activationSessionService.createPending(screen.getId()));

        String pairUrl = buildPairUrl(request, session.getToken());
        String qrCodeBase64 = qrCodeService.generateBase64Png(pairUrl, 320, 320);

        model.addAttribute("status", session.getStatus().name());
        model.addAttribute("screenName", screen.getName());
        model.addAttribute("sessionId", session.getId().toString());
        model.addAttribute("pairUrl", pairUrl);
        model.addAttribute("qrCodeBase64", qrCodeBase64);

        return "activate/activate";
    }

    @GetMapping("/activate/status")
    public ResponseEntity<ActivationStatusResponse> status(@RequestParam("sessionId") UUID sessionId) {
        ActivationSession session = activationSessionService.findById(sessionId);
        return ResponseEntity.ok(new ActivationStatusResponse(session.getStatus().name()));
    }

    private Screen resolveOrCreateScreen(HttpServletRequest request, HttpServletResponse response) {
        Optional<UUID> cookieScreenId = readScreenIdFromCookie(request);

        if (cookieScreenId.isPresent()) {
            try {
                return screenService.findById(cookieScreenId.get());
            } catch (IllegalArgumentException ignored) {
            }
        }

        String generatedName = "Tela " + UUID.randomUUID().toString().substring(0, 8);
        Screen screen = screenService.createPendingScreen(
                generatedName,
                "Screen criada automaticamente na ativação."
        );

        Cookie cookie = new Cookie(SCREEN_COOKIE_NAME, screen.getId().toString());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(SCREEN_COOKIE_MAX_AGE_SECONDS);
        response.addCookie(cookie);

        return screen;
    }

    private Optional<UUID> readScreenIdFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return Optional.empty();
        }

        for (Cookie cookie : request.getCookies()) {
            if (SCREEN_COOKIE_NAME.equals(cookie.getName())) {
                try {
                    return Optional.of(UUID.fromString(cookie.getValue()));
                } catch (IllegalArgumentException ignored) {
                    return Optional.empty();
                }
            }
        }

        return Optional.empty();
    }

    private String buildPairUrl(HttpServletRequest request, String token) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();

        boolean defaultPort = ("http".equalsIgnoreCase(scheme) && serverPort == 80)
                || ("https".equalsIgnoreCase(scheme) && serverPort == 443);

        String baseUrl = defaultPort
                ? scheme + "://" + serverName
                : scheme + "://" + serverName + ":" + serverPort;

        return baseUrl + "/pair?token=" + token;
    }
}
