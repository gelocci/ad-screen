package br.com.locci.adscreen.common.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorPageController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());

            if (statusCode == HttpStatus.FORBIDDEN.value()) {
                model.addAttribute("code", "403");
                model.addAttribute("title", "Acesso negado");
                model.addAttribute("message", "Você não tem permissão para acessar esta página.");
            } else if (statusCode == HttpStatus.NOT_FOUND.value()) {
                model.addAttribute("code", "404");
                model.addAttribute("title", "Página não encontrada");
                model.addAttribute("message", "A página que você procura não existe.");
            } else {
                model.addAttribute("code", String.valueOf(statusCode));
                model.addAttribute("title", "Algo deu errado");
                model.addAttribute("message", "Ocorreu um erro inesperado. Tente novamente.");
            }
        } else {
            model.addAttribute("code", "?");
            model.addAttribute("title", "Algo deu errado");
            model.addAttribute("message", "Ocorreu um erro inesperado. Tente novamente.");
        }

        return "error";
    }
}
