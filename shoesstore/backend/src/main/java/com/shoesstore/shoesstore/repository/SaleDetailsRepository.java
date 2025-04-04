package com.shoesstore.shoesstore.repository;

import com.shoesstore.shoesstore.model.Sale;
import com.shoesstore.shoesstore.model.SaleDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

public interface SaleDetailsRepository extends JpaRepository<SaleDetails, Long> {

}
