package com.shoesstore.shoesstore.service;

import com.shoesstore.shoesstore.model.Product;
import com.shoesstore.shoesstore.model.Supplier;
import com.shoesstore.shoesstore.model.SupplierProduct;
import com.shoesstore.shoesstore.repository.ProductRepository;
import com.shoesstore.shoesstore.repository.SupplierRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SupplierServiceTest {

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private SupplierService supplierService;

    @Test
    void testSaveSupplierWithProductsAndPrices() {
        Supplier supplier = new Supplier();
        supplier.setId(1L);

        List<Long> productIds = List.of(100L, 101L);
        List<BigDecimal> prices = List.of(new BigDecimal("10.50"), new BigDecimal("20.75"));

        Product p1 = new Product(); p1.setId(100L);
        Product p2 = new Product(); p2.setId(101L);
        List<Product> products = List.of(p1, p2);

        when(supplierRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(productRepository.findAllById(productIds)).thenReturn(products);

        Supplier result = supplierService.save(supplier, productIds, prices);

        assertEquals(2, result.getSupplierProducts().size());
        assertTrue(result.getSupplierProducts().stream().anyMatch(sp -> sp.getPrice().equals(new BigDecimal("10.50"))));
        assertTrue(result.getSupplierProducts().stream().anyMatch(sp -> sp.getProduct().getId().equals(101L)));
    }

    @Test
    void testUpdateSupplierAndProducts() {
        Supplier existing = new Supplier();
        existing.setId(1L);
        existing.setSupplierProducts(new HashSet<>());

        Product p1 = new Product(); p1.setId(100L);
        Product p2 = new Product(); p2.setId(101L);

        SupplierProduct sp1 = new SupplierProduct(existing, p1, new BigDecimal("5.0"));
        existing.getSupplierProducts().add(sp1);

        Supplier updatedData = new Supplier();
        updatedData.setName("Nuevo nombre");

        List<Long> productIds = List.of(101L);
        List<BigDecimal> prices = List.of(new BigDecimal("99.99"));

        when(supplierRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productRepository.findById(101L)).thenReturn(Optional.of(p2));

        supplierService.update(1L, updatedData, productIds, prices);

        assertEquals("Nuevo nombre", existing.getName());
        assertEquals(1, existing.getSupplierProducts().size());

        SupplierProduct updatedSP = existing.getSupplierProducts().iterator().next();
        assertEquals(new BigDecimal("99.99"), updatedSP.getPrice());
        assertEquals(101L, updatedSP.getProduct().getId());
    }

    @Test
    void testFindAll() {
        Supplier s1 = new Supplier(); s1.setId(1L);
        Supplier s2 = new Supplier(); s2.setId(2L);
        when(supplierRepository.findAll()).thenReturn(List.of(s1, s2));

        List<Supplier> result = supplierService.findAll();

        assertEquals(2, result.size());
    }

    @Test
    void testFindByIdFound() {
        Supplier s = new Supplier(); s.setId(1L);
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(s));

        Supplier found = supplierService.findById(1L);
        assertEquals(1L, found.getId());
    }

    @Test
    void testFindByIdNotFound() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.empty());

        Supplier result = supplierService.findById(1L);
        assertNull(result);
    }

    @Test
    void testDeleteSuccess() {
        when(supplierRepository.existsById(1L)).thenReturn(true);

        supplierService.delete(1L);

        verify(supplierRepository).deleteById(1L);
    }

    @Test
    void testDeleteNotFound() {
        when(supplierRepository.existsById(1L)).thenReturn(false);

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> {
            supplierService.delete(1L);
        });

        assertEquals("Supplier not found with ID: 1", ex.getMessage());
    }
}


