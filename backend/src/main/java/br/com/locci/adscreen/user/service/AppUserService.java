package br.com.locci.adscreen.user.service;

import br.com.locci.adscreen.user.entity.AppUser;
import br.com.locci.adscreen.user.repository.AppUserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AppUserService {

    private final AppUserRepository appUserRepository;

    public AppUserService(final AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    public AppUser create(final AppUser appUser) {
        if (appUserRepository.existsByEmail(appUser.getEmail())) {
            throw new IllegalArgumentException("User email already exists.");
        }

        return appUserRepository.save(appUser);
    }

    public AppUser findById(final UUID id) {
        return appUserRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
    }

    public AppUser findByEmail(final String email) {
        return appUserRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
    }

    public List<AppUser> findAll() {
        return appUserRepository.findAll();
    }
}
