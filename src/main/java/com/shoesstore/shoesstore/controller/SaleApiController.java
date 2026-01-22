package com.shoesstore.shoesstore.controller;

import com.shoesstore.shoesstore.dto.ProductDto;
import com.shoesstore.shoesstore.dto.SaleDetailResponse;
import com.shoesstore.shoesstore.model.Sale;
import com.shoesstore.shoesstore.model.SaleDetails;
import com.shoesstore.shoesstore.service.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sales")
public class SaleApiController {

    @Autowired
    private SaleService saleService;
    
    public SaleApiController(SaleService saleService) {
    	this.saleService = saleService;
    }

    @GetMapping("/{saleId}/details")
    public List<SaleDetailResponse> getSaleDetails(@PathVariable Long saleId) {
        Sale sale = saleService.getSaleById(saleId);
        return sale.getDetails().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private SaleDetailResponse convertToResponse(SaleDetails detail) {
        // Mapeo SaleDetails → SaleDetailResponse
        SaleDetailResponse resp = new SaleDetailResponse();
        resp.setId(detail.getId());
        resp.setQuantity(detail.getQuantity());
        resp.setSubtotal(detail.getSubtotal());

        // Mapeo Product → ProductDto
        var p = detail.getProduct();
        ProductDto dto = new ProductDto();
        dto.setId(p.getId());;
        dto.setDescription(p.getDescription());
        dto.setSize(p.getSize());
        dto.setPrice(p.getPrice());
        dto.setStock(p.getStock());

        resp.setProduct(dto);
        return resp;
    }
}