package com.shoesstore.shoesstore.repository;

import com.shoesstore.shoesstore.model.Claim;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClaimRepository extends JpaRepository<Claim, Long> {
    // Buscar reclamos por estado
    List<Claim> findByState(Claim.State state);

    // Verificar si una venta ya tiene reclamo
    boolean existsBySaleId(Long saleId);

    // Obtener todos los reclamos
    List<Claim> findAll();
}