package com.shoesstore.shoesstore.controller;

import com.shoesstore.shoesstore.model.Product;
import com.shoesstore.shoesstore.model.Sale;
import com.shoesstore.shoesstore.model.SaleDetails;
import com.shoesstore.shoesstore.repository.SaleDetailsRepository;
import com.shoesstore.shoesstore.repository.SaleRepository;
import com.shoesstore.shoesstore.service.SaleService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sales")
public class SaleApiController {

    @Autowired
    private SaleService saleService;


    @GetMapping("/{saleId}/details")
    public List<SaleDetailResponse> getSaleDetails(@PathVariable Long saleId) {

        Sale sale = saleService.getSaleById(saleId);

        return sale.getDetails().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private SaleDetailResponse convertToResponse(SaleDetails detail) {
        SaleDetailResponse response = new SaleDetailResponse();
        response.setId(detail.getId());
        response.setProduct(detail.getProduct());
        response.setQuantity(detail.getQuantity());
        response.setSubtotal(detail.getSubtotal());
        return response;
    }

    @Data
    private static class SaleDetailResponse {
        private Long id;
        private Product product;
        private int quantity;
        private double subtotal;
    }
}