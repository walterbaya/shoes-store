package com.shoesstore.shoesstore.advices;

import com.shoesstore.shoesstore.service.CustomUserDetailsService;
import lombok.AllArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@AllArgsConstructor
public class GlobalControllerAdvice {

    private final CustomUserDetailsService customUserDetailsService;

    @ModelAttribute
    public void addUserInfo(Model model) {
        model.addAttribute("username", customUserDetailsService.getCurrentUserName());
     }

}
