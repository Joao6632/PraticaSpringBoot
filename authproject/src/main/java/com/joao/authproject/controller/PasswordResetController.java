package com.joao.authproject.controller;

import com.joao.authproject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class PasswordResetController {

    private final UserService userService;

    // Tela para pedir o email
    @GetMapping("/forgot-password")
    public String forgotPasswordForm() {
        return "forgot-password";
    }

    // Recebe email e gera token
    @PostMapping("/forgot-password")
    public String forgotPasswordSubmit(@RequestParam String email, Model model) {
        userService.createPasswordResetToken(email);
        model.addAttribute("message", "Se o email existir, um link foi enviado.");
        return "forgot-password";
    }

    // Tela para resetar a senha (via link)
    @GetMapping("/reset-password")
    public String resetPasswordForm(@RequestParam String token, Model model) {
        model.addAttribute("token", token);
        return "reset-password";
    }

    // Recebe token e nova senha, faz o reset
    @PostMapping("/reset-password")
    public String resetPasswordSubmit(@RequestParam String token,
                                      @RequestParam String senha,
                                      Model model) {
        boolean success = userService.resetPassword(token, senha);
        if (success) {
            return "redirect:/login?resetSuccess";
        } else {
            model.addAttribute("error", "Token inválido ou expirado");
            model.addAttribute("token", token);
            return "reset-password";
        }
    }
}
