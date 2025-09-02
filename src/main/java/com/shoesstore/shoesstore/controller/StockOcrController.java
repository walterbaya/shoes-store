package com.shoesstore.shoesstore.controller;

import com.shoesstore.shoesstore.dto.OcrResultDTO;
import com.shoesstore.shoesstore.service.OcrStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stock")
public class StockOcrController {

    private final OcrStockService ocrStockService;

    // Ejemplo: curl -F "file=@/ruta/foto.jpg" "http://localhost:8080/api/stock/ocr?dryRun=false"
    @PostMapping(value = "/ocr", consumes = {"multipart/form-data"})
    public ResponseEntity<List<OcrResultDTO>> ocr(
            @RequestParam("file") MultipartFile file,
            @RequestParam(name = "dryRun", defaultValue = "false") boolean dryRun
    ) throws Exception {
        List<OcrResultDTO> results = ocrStockService.processImage(file, dryRun);
        return ResponseEntity.ok(results);
    }
}