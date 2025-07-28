package com.shoesstore.shoesstore.service;

import com.shoesstore.shoesstore.exception.InsufficientStockException;
import com.shoesstore.shoesstore.model.*;
import com.shoesstore.shoesstore.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private PurchaseOrderItemsRepository purchaseOrderItemsRepository;
    @Mock
    private ClaimRepository claimRepository;
    @Mock
    private ClaimDetailsRepository claimDetailsRepository;
    @Mock
    private SaleDetailsRepository saleDetailsRepository;

    @InjectMocks
    private ProductService productService;

    private Product producto;

    @BeforeEach
    void setUp() {
        producto = new Product();
        producto.setId(1L);
        producto.setStock(10);
        producto.setDescription("Zapatilla deportiva");
        producto.setSuppliers(new HashSet<>());
        producto.setSupplierProducts(new HashSet<>());
    }

    @Test
    void testGetAllProducts() {
        when(productRepository.findAll()).thenReturn(List.of(producto));
        List<Product> result = productService.getAllProducts();
        assertEquals(1, result.size());
        verify(productRepository).findAll();
    }

    @Test
    void testUpdateStockSuccess() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(producto));
        productService.updateStock(1L, 5);
        assertEquals(5, producto.getStock());
        verify(productRepository).save(producto);
    }

    @Test
    void testUpdateStockInsufficient() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(producto));
        assertThrows(InsufficientStockException.class, () -> productService.updateStock(1L, 20));
    }

    @Test
    void testGetProductByIdFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(producto));
        Product result = productService.getProductById(1L);
        assertEquals("Zapatilla deportiva", result.getDescription());
    }

    @Test
    void testGetProductByIdNotFound() {
        when(productRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> productService.getProductById(2L));
    }

    @Test
    void testSaveProductSuccess() {
        producto.setId(2L);
        when(productRepository.existsById(2L)).thenReturn(false);
        when(productRepository.save(producto)).thenReturn(producto);
        Product result = productService.saveProduct(producto);
        assertEquals(producto, result);
    }

    @Test
    void testSaveProductAlreadyExists() {
        producto.setId(1L);
        when(productRepository.existsById(1L)).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> productService.saveProduct(producto));
    }

    @Test
    void testUpdateProductSuccess() {
        when(productRepository.existsById(1L)).thenReturn(true);
        when(productRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productRepository.save(any())).thenReturn(producto);
        Product result = productService.updateProduct(producto);
        assertNotNull(result);
    }

    @Test
    void testUpdateProductNonexistent() {
        producto.setId(99L);
        when(productRepository.existsById(99L)).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> productService.updateProduct(producto));
    }

    @Test
    void testDeleteProductWithClaims() {
        SaleDetails saleDetail = new SaleDetails();
        Claim claim = new Claim();
        claim.setId(10L);
        saleDetail.setClaim(claim);

        when(productRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(saleDetailsRepository.findAllByProductId(1L)).thenReturn(List.of(saleDetail));

        assertThrows(IllegalArgumentException.class, () -> productService.deleteProduct(1L));
    }

    @Test
    void testDeleteProductWithPurchaseOrders() {
        PurchaseOrderItem item = new PurchaseOrderItem();
        item.setId(20L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(saleDetailsRepository.findAllByProductId(1L)).thenReturn(Collections.emptyList());
        when(purchaseOrderItemsRepository.findAllByProductId(1L)).thenReturn(List.of(item));

        assertThrows(IllegalArgumentException.class, () -> productService.deleteProduct(1L));
    }

    @Test
    void testDeleteProductWithSales() {
        SaleDetails saleDetail = new SaleDetails();
        Sale sale = new Sale();
        sale.setId(30L);
        saleDetail.setSale(sale);

        when(productRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(saleDetailsRepository.findAllByProductId(1L)).thenReturn(List.of(saleDetail));
        when(purchaseOrderItemsRepository.findAllByProductId(1L)).thenReturn(Collections.emptyList());

        assertThrows(IllegalArgumentException.class, () -> productService.deleteProduct(1L));
    }

    @Test
    void testDeleteProductSuccess() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(producto)).thenReturn(Optional.of(producto));
        when(saleDetailsRepository.findAllByProductId(1L)).thenReturn(Collections.emptyList());
        when(purchaseOrderItemsRepository.findAllByProductId(1L)).thenReturn(Collections.emptyList());

        assertDoesNotThrow(() -> productService.deleteProduct(1L));
        verify(productRepository).deleteById(1L);
    }

    @Test
    void testDeleteProductNotFound() {
        when(productRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> productService.deleteProduct(2L));
    }
}
