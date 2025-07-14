package com.shoesstore.shoesstore.repository;

import com.shoesstore.shoesstore.model.PurchaseOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseOrderItemsRepository extends JpaRepository<PurchaseOrderItem, Long> {
    List<PurchaseOrderItem> findAllByProductId(Long productId);
}
