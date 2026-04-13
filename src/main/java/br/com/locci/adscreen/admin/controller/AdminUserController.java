package br.com.locci.adscreen.admin.controller;

import br.com.locci.adscreen.user.entity.AppUser;
import br.com.locci.adscreen.user.service.AppUserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    private final AppUserService appUserService;
    private final PasswordEncoder passwordEncoder;

    public AdminUserController(
            AppUserService appUserService,
            PasswordEncoder passwordEncoder
    ) {
        this.appUserService = appUserService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("users", appUserService.findAll());
        return "admin/users/list";
    }

    @GetMapping("/new")
    public String newForm() {
        return "admin/users/form";
    }

    @PostMapping
    public String create(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password,
            RedirectAttributes redirectAttributes
    ) {
        try {
            appUserService.create(
                    AppUser.create(name, email, passwordEncoder.encode(password))
            );
            redirectAttributes.addFlashAttribute("success", "Usuário criado com sucesso.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/users/new";
        }
        return "redirect:/admin/users";
    }
}
