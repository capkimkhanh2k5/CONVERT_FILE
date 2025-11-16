package com.convertfile.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class googleLoginController {

    @GetMapping("/")
    public String index(@AuthenticationPrincipal OAuth2User principal) {
        if (principal != null) {
            return "redirect:/home";
        }
        return "index";
    }

    @GetMapping("/home")
    public String home(@AuthenticationPrincipal OAuth2User principal, HttpSession session, Model model) {
        if (principal != null) {
            String name = principal.getAttribute("name");
            String email = principal.getAttribute("email");
            String picture = principal.getAttribute("picture");
            
            // Lưu vào session
            session.setAttribute("username", name);
            session.setAttribute("useremail", email);
            session.setAttribute("userpicture", picture);
            
            // Lưu vào model
            model.addAttribute("name", name);
            model.addAttribute("email", email);
            model.addAttribute("picture", picture);
        }
        return "home";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/login")
    public String login(@AuthenticationPrincipal OAuth2User principal) {
        if (principal != null) {
            return "redirect:/home";
        }
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}

