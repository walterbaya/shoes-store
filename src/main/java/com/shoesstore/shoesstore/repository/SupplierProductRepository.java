package com.shoesstore.shoesstore.repository;

import com.shoesstore.shoesstore.model.SupplierProduct;
import com.shoesstore.shoesstore.model.SupplierProductId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierProductRepository extends JpaRepository<SupplierProduct, SupplierProductId> {}