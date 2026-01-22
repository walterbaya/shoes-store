package com.shoesstore.shoesstore.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataAccessException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Manejo específico para excepciones de negocio de Productos
    @ExceptionHandler(ProductServiceException.class)
    public String handleProductServiceException(ProductServiceException ex, RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/products";
    }

    // Manejo específico para excepciones de negocio de Ventas
    @ExceptionHandler(SaleServiceException.class)
    public String handleSaleServiceException(SaleServiceException ex, RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("error", "Error en venta: " + ex.getMessage());
        return "redirect:/sales";
    }

    // Recurso no encontrado (ej. ID inválido en URL)
    @ExceptionHandler(EntityNotFoundException.class)
    public String handleNotFound(EntityNotFoundException ex, Model model){
        model.addAttribute("error", "Recurso no encontrado: " + ex.getMessage());
        model.addAttribute("status", 404);
        return "error"; // Asegúrate de tener una vista 'error.html' o 'layout' con mensaje
    }

    // Argumentos inválidos (ej. parámetros mal formados)
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleBadRequest(IllegalArgumentException ex, Model model){
        model.addAttribute("error", "Solicitud inválida: " + ex.getMessage());
        model.addAttribute("status", 400);
        return "error";
    }

    // Error de base de datos
    @ExceptionHandler(DataAccessException.class)
    public String handleDatabaseException(DataAccessException ex, Model model) {
        model.addAttribute("error", "Error de conexión con la base de datos. Intente más tarde.");
        model.addAttribute("details", ex.getMessage());
        model.addAttribute("status", 503);
        return "error";
    }

    // Manejador genérico para cualquier otra excepción no controlada (Error 500)
    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception ex, Model model){
        model.addAttribute("error", "Ha ocurrido un error inesperado.");
        model.addAttribute("details", ex.getMessage()); // Ojo: en producción quizás no quieras mostrar esto
        model.addAttribute("status", 500);
        return "error";
    }
}
