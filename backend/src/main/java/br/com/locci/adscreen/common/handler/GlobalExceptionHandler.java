package br.com.locci.adscreen.common.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice(assignableTypes = {
    br.com.locci.adscreen.activation.controller.ActivationSessionController.class,
    br.com.locci.adscreen.organization.controller.OrganizationController.class,
    br.com.locci.adscreen.screen.controller.ScreenController.class,
    br.com.locci.adscreen.user.controller.AppUserController.class,
    br.com.locci.adscreen.user.controller.OrganizationUserController.class
})
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(final IllegalArgumentException ex) {
        return new ErrorResponse(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage()
        );
    }

    public record ErrorResponse(
            Instant timestamp,
            int status,
            String error,
            String message
    ) {
    }
}
