package com.shoesstore.shoesstore.controller;

import com.shoesstore.shoesstore.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/rest/products")
public class ProductRestController {

    private final ProductService productService;

    @PatchMapping("/add-one-to-stock/{id}")
    public ResponseEntity<ResponseDto> addOneToStock(
            @RequestParam Long id) {

        productService.addOneToStock(id);
        return ResponseEntity.ok(new ResponseDto("200", "Stock incrementado correctamente"));

    }

}