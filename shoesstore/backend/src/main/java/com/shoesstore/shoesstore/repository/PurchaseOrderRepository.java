package com.shoesstore.shoesstore.repository;

import com.shoesstore.shoesstore.model.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    List<PurchaseOrder> findByGeneratedDate(LocalDate date);
}