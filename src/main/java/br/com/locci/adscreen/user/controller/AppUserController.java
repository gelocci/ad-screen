package br.com.locci.adscreen.user.controller;

import br.com.locci.adscreen.user.dto.AppUserResponse;
import br.com.locci.adscreen.user.dto.CreateAppUserRequest;
import br.com.locci.adscreen.user.entity.AppUser;
import br.com.locci.adscreen.user.service.AppUserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class AppUserController {

    private final AppUserService appUserService;
    private final PasswordEncoder passwordEncoder;

    public AppUserController(
            final AppUserService appUserService,
            final PasswordEncoder passwordEncoder
    ) {
        this.appUserService = appUserService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppUserResponse create(@RequestBody final CreateAppUserRequest request) {
        AppUser saved = appUserService.create(
                AppUser.create(request.name(), request.email(), passwordEncoder.encode(request.password()))
        );

        return new AppUserResponse(
                saved.getId(),
                saved.getName(),
                saved.getEmail(),
                saved.getActive()
        );
    }
}
