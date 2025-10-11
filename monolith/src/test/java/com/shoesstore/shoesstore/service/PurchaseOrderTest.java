package com.shoesstore.shoesstore.service;

import com.shoesstore.shoesstore.model.*;
import com.shoesstore.shoesstore.repository.ProductRepository;
import com.shoesstore.shoesstore.repository.PurchaseOrderRepository;
import com.shoesstore.shoesstore.repository.SupplierRepository;
import com.shoesstore.shoesstore.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.*;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


class PurchaseOrderServiceTest {

    @Mock
    private PurchaseOrderRepository orderRepo;
    @Mock private ProductRepository productRepo;
    @Mock private SupplierRepository supplierRepo;
    @Mock private UserRepository userRepository;
    @Mock private FileStorageService fileStorageService;

    @InjectMocks
    private PurchaseOrderService purchaseOrderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAllOrders() {
        List<PurchaseOrder> mockOrders = List.of(new PurchaseOrder());
        when(orderRepo.findAll()).thenReturn(mockOrders);
        assertEquals(1, purchaseOrderService.findAll().size());
    }

    @Test
    void testFindByIdExists() {
        PurchaseOrder order = new PurchaseOrder();
        when(orderRepo.findById(1L)).thenReturn(Optional.of(order));
        assertNotNull(purchaseOrderService.findById(1L));
    }

    @Test
    void testFindByIdNotFound() {
        when(orderRepo.findById(1L)).thenReturn(Optional.empty());
        assertNull(purchaseOrderService.findById(1L));
    }

    @Test
    void testCompleteOrderSuccess() {
        PurchaseOrder order = new PurchaseOrder();
        order.setCompleted(false);

        Product product = new Product();
        product.setStock(10);

        PurchaseOrderItem item = new PurchaseOrderItem();
        item.setProduct(product);
        item.setQuantity(5);

        order.setItems(List.of(item));

        when(orderRepo.findById(1L)).thenReturn(Optional.of(order));
        when(productRepo.save(any())).thenReturn(product);
        when(orderRepo.save(any())).thenReturn(order);

        purchaseOrderService.completeOrder(1L);

        assertTrue(order.isCompleted());
        assertEquals(15, product.getStock());
        verify(productRepo).save(product);
        verify(orderRepo).save(order);
    }

    @Test
    void testDeleteOrderWithAttachment() throws Exception {
        PurchaseOrder order = new PurchaseOrder();
        PurchaseOrderAttachment att = new PurchaseOrderAttachment();
        att.setStoragePath("files/attachment.pdf");
        order.setAttachment(att);

        when(orderRepo.findById(1L)).thenReturn(Optional.of(order));

        purchaseOrderService.deleteOrder(1L);

        verify(fileStorageService).deleteFile("files/attachment.pdf");
        verify(orderRepo).delete(order);
    }

    @Test
    void testDeleteOrderNotFound() {
        when(orderRepo.findById(1L)).thenReturn(Optional.empty());
        Exception ex = assertThrows(Exception.class, () -> purchaseOrderService.deleteOrder(1L));
        assertEquals("Orden no encontrada", ex.getMessage());
    }

    @Test
    void testCreateOrderFailsWithNegativeDiscount() {
        PurchaseOrder po = new PurchaseOrder();
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            purchaseOrderService.createOrder(po, 1L, Map.of(), new BigDecimal("-5"), BigDecimal.ZERO, null);
        });
        assertEquals("El descuento no puede ser negativo", ex.getMessage());
    }

    @Test
    void testCreateOrderFailsWithEmptyItems() {
        Supplier supplier = new Supplier();
        supplier.setSupplierProducts(new HashSet<>());

        when(supplierRepo.findById(1L)).thenReturn(Optional.of(supplier));
        mockUserAuthentication();

        PurchaseOrder po = new PurchaseOrder();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            purchaseOrderService.createOrder(po, 1L, Map.of(1L, 0), BigDecimal.ZERO, BigDecimal.ZERO, null);
        });
        assertEquals("La orden debe contener al menos un producto", ex.getMessage());
    }

    private void mockUserAuthentication() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testuser");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        User user = new User();
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
    }
}
