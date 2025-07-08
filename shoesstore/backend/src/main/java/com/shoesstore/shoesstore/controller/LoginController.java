package com.shoesstore.shoesstore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/recover")
    public String recover(){
        return "auth/recover";
    }

    @GetMapping("/signup")
    public String signup(){return "auth/signup";}
}