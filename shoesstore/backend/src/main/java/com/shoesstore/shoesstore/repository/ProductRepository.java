package com.shoesstore.shoesstore.repository;

import com.shoesstore.shoesstore.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.suppliers")
    List<Product> findAllWithSuppliers();
}