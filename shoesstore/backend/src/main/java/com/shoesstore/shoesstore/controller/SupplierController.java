package com.shoesstore.shoesstore.controller;

import com.shoesstore.shoesstore.model.Supplier;
import com.shoesstore.shoesstore.service.SupplierService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    // Only ADMIN can create a supplier
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Supplier> createSupplier(@RequestBody @Valid Supplier supplier) {
        Supplier created = supplierService.registerSupplier(supplier);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // Only ADMIN can update a supplier
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Supplier> updateSupplier(
            @PathVariable Long id,
            @RequestBody @Valid Supplier supplier) {
        Supplier updated = supplierService.updateSupplier(id, supplier);
        return ResponseEntity.ok(updated);
    }
}
