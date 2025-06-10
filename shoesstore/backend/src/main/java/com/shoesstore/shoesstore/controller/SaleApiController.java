package com.shoesstore.shoesstore.controller;

import com.shoesstore.shoesstore.model.Product;
import com.shoesstore.shoesstore.model.SaleDetails;
import com.shoesstore.shoesstore.repository.SaleDetailsRepository;
import lombok.Data;
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
    private SaleDetailsRepository saleDetailsRepository;

    @GetMapping("/{saleId}/details")
    public List<SaleDetailResponse> getSaleDetails(@PathVariable Long saleId) {
        return saleDetailsRepository.findById(saleId).stream()
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