package br.com.locci.adscreen.admin.controller;

import br.com.locci.adscreen.screen.service.ScreenService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/screens")
public class AdminScreenController {

    private final ScreenService screenService;

    public AdminScreenController(ScreenService screenService) {
        this.screenService = screenService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("screens", screenService.findAll());
        return "admin/screens/list";
    }
}
