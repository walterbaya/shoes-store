package com.example.productScanner.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.example.productScanner.dto.ScanRequest;
import com.example.productScanner.service.IProductScannerService;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class productScannerController {
	
	IProductScannerService productScannerService;
	
	@PostMapping(path = "productScanner/scanProduct")
	ResponseEntity<String> scanProduct(@RequestBody ScanRequest scanRequest){
		
		productScannerService.increaseStockInOneUnit(scanRequest);
		
		return ResponseEntity.ok("Producto escaneado");
		
	}
	
}
