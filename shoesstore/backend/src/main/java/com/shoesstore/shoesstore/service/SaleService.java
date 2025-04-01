package com.shoesstore.shoesstore.service;

import com.shoesstore.shoesstore.model.Sale;
import com.shoesstore.shoesstore.model.SaleDetails;
import com.shoesstore.shoesstore.repository.SaleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final ProductService productService;

    public SaleService(SaleRepository saleRepository, ProductService productService) {
        this.saleRepository = saleRepository;
        this.productService = productService;
    }

    @Transactional
    public Sale processSale(Sale sale) {
        for (SaleDetails detail : sale.getDetails()) {
            productService.updateStock(detail.getProduct().getId(), detail.getQuantity());
        }
        return saleRepository.save(sale);
    }

    public List<Sale> getFilteredSales(LocalDateTime start, LocalDateTime end) {
        return saleRepository.findBySaleDateBetween(start, end);
    }
}