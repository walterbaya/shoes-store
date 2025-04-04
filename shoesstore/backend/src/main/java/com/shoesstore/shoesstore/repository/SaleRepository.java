package com.shoesstore.shoesstore.repository;

import com.shoesstore.shoesstore.model.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    @Query("SELECT s FROM Sale s WHERE s.saleDate BETWEEN :start AND :end")
    List<Sale> findBySaleDateBetween(LocalDateTime start, LocalDateTime end);

}
