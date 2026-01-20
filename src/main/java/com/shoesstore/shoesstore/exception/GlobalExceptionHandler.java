package com.shoesstore.shoesstore.exception;

import com.shoesstore.shoesstore.dto.ResponseDto;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InsufficientStockException.class)
    public String handleStockError(InsufficientStockException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "error/stock-error";
    }

    @ExceptionHandler(ProductServiceException.class)
    public String handleProductServiceException(ProductServiceException ex, Model model, RedirectAttributes redirectAttributes){
        if(redirectAttributes != null){
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/products";
        }
        else{
            model.addAttribute("error", ex.getMessage());
            return "layout";
        }
    }

    //Exceptions para products
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseDto> handleBadRequest(IllegalArgumentException ex){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ResponseDto("400", ex.getMessage()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ResponseDto> handleNotFound(EntityNotFoundException ex){
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ResponseDto("404", ex.getMessage()));
    }

    public ResponseEntity<ResponseDto> handleInternalServerError(Exception ex){
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseDto("500", ex.getMessage()));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ResponseDto> handleServiceUnavailable(DataAccessException ex) {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ResponseDto("503", "Error en la base de datos"));
    }

}