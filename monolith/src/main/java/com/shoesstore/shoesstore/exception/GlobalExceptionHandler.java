package com.shoesstore.shoesstore.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InsufficientStockException.class)
    public String handleStockError(InsufficientStockException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "error/stock-error";
    }
}