package com.example.productScanner.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("shoes-store")
public interface ProductsFeignClient {

    @PatchMapping(value = "/products/add-one-to-stock/{id}",consumes = "application/json")
    public ResponseEntity<Void> addOneToStock(@RequestParam Long id);

}
