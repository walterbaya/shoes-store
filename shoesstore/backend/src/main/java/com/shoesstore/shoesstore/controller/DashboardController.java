package com.shoesstore.shoesstore.controller;

import com.shoesstore.shoesstore.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("title", "Dashboard");
        model.addAttribute("view", "dashboard"); // Fragmento
        model.addAttribute("username", customUserDetailsService.getCurrentUserName());
        return "layout";  // Usa el archivo layout.html
    }
}