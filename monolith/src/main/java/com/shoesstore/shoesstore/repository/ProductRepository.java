package com.shoesstore.shoesstore.repository;

import com.shoesstore.shoesstore.model.Product;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.suppliers")
    List<Product> findAllWithSuppliers();

    @Transactional
    @Modifying
    @Query("update Product p set p.stock = p.stock + 1 where p.id = :id")
    int addOneToStock(@Param("id") Long id);
}