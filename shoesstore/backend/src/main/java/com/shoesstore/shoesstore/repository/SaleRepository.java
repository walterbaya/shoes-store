package com.shoesstore.shoesstore.repository;

import com.shoesstore.shoesstore.model.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {

	@Query("SELECT s FROM Sale s WHERE s.saleDate BETWEEN :start AND :end")
	List<Sale> findBySaleDateBetween(LocalDateTime start, LocalDateTime end);

	@Query("SELECT s.user.username, SUM(s.total) FROM Sale s WHERE s.saleDate BETWEEN :start AND :end GROUP BY s.user.username")
	List<Object[]> findDailySalesByUser(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

	@Query("SELECT s.user.username, SUM(s.total) as total FROM Sale s WHERE s.saleDate BETWEEN :start AND :end GROUP BY s.user.username ORDER BY total DESC")
	List<Object[]> findTopSellers(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
