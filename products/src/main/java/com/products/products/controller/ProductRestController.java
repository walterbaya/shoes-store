package com.products.products.controller;

import com.shoesstore.shoesstore.dto.ResponseDto;
import com.shoesstore.shoesstore.dto.ScanRequest;
import com.shoesstore.shoesstore.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//ProductRestController.java (VERIFICACIÓN)

@AllArgsConstructor
@RestController
@RequestMapping("/rest/products") // Path base
public class ProductRestController {

 private final ProductService productService;

 @PostMapping("/add-one-to-stock") // Path del método
 public ResponseEntity<ResponseDto> addOneToStock(@RequestBody ScanRequest scanRequest) {

	 productService.addOneToStock(scanRequest.getId());
     return ResponseEntity.ok(new ResponseDto("200", "Stock incrementado correctamente"));
 }
}