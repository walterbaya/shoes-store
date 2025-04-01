package com.shoesstore.shoesstore.repository;

import com.shoesstore.shoesstore.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}