package com.shoesstore.shoesstore.repository;

import com.shoesstore.shoesstore.model.SaleDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SaleDetailsRepository extends JpaRepository<SaleDetails, Long> {
    List<SaleDetails> findBySaleId(Long saleId);

    List<SaleDetails> findAllByProductId(Long productId);
}
