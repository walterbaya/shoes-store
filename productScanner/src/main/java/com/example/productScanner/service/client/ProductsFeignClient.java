package com.example.productScanner.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.example.productScanner.dto.ScanRequest;

//ProductsFeignClient.java (SOLUCIÓN DEFINITIVA)

@FeignClient(name = "shoes-store", path = "/rest/products") // AÑADE EL PATH BASE AQUÍ
public interface ProductsFeignClient {

 @PostMapping("/add-one-to-stock") // Solo el path del método
 public ResponseEntity<Void> addOneToStock(@RequestBody ScanRequest scanRequest);

}