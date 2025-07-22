package com.joao.authproject.controller;

import com.joao.authproject.service.UserService;
import com.joao.authproject.model.User; // se existir
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService service;

    @GetMapping("/register")
    public String showRegister(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user) {
        service.registerUser(user);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLogin(@RequestParam(required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", "Credenciais inválidas");
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String senha, Model model) {
        boolean authenticated = service.authenticate(email, senha);
        if (authenticated) {
            return "redirect:/home";
        } else {
            model.addAttribute("error", "Email ou senha incorretos");
            return "login";
        }
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }
}
