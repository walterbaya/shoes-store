package com.shoesstore.shoesstore.repository;

import com.shoesstore.shoesstore.model.Sale;
import com.shoesstore.shoesstore.model.enums.SaleChannel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    @Query("SELECT u.username, SUM(s.total) FROM Sale s JOIN s.user u WHERE s.saleDate BETWEEN :start AND :end GROUP BY u.username")
    List<Object[]> fetchSalesByUser(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT p.id, SUM(d.quantity), SUM(d.subtotal) FROM SaleDetails d JOIN d.product p WHERE d.sale.saleDate BETWEEN :start AND :end GROUP BY p.id ORDER BY SUM(d.quantity) DESC")
    List<Object[]> fetchSalesByProduct(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
	
	@Query("SELECT s FROM Sale s WHERE s.saleDate BETWEEN :start AND :end")
	List<Sale> findBySaleDateBetween(LocalDateTime start, LocalDateTime end);

	@Query("SELECT s.user.username, SUM(s.total) FROM Sale s WHERE s.saleDate BETWEEN :start AND :end GROUP BY s.user.username")
	List<Object[]> findDailySalesByUser(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

	@Query("SELECT s.user.username, SUM(s.total) as total FROM Sale s WHERE s.saleDate BETWEEN :start AND :end GROUP BY s.user.username ORDER BY total DESC")
	List<Object[]> findTopSellers(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT s FROM Sale s WHERE s.total > 0 AND s.claim IS NULL")
    List<Sale> findSalesWithTotalAndNoClaim();

}
