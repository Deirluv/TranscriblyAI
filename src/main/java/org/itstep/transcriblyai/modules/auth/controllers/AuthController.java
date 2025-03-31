package org.itstep.transcriblyai.modules.auth.controllers;

import lombok.RequiredArgsConstructor;
import org.itstep.transcriblyai.modules.auth.services.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String showRegisterForm() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username, @RequestParam String password, Model model) {
        if (userService.findByUsername(username) != null) {
            model.addAttribute("error", "User already exists!");
            return "auth/register";
        }
        userService.registerUser(username, password);
        return "redirect:/auth/login";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "auth/login";
    }
}

