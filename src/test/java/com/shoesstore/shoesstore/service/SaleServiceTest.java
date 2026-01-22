package com.shoesstore.shoesstore.service;

import com.shoesstore.shoesstore.model.*;
import com.shoesstore.shoesstore.repository.SaleDetailsRepository;
import com.shoesstore.shoesstore.repository.SaleRepository;
import com.shoesstore.shoesstore.dto.SaleItemForm;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaleServiceTest {

    @Mock
    private SaleRepository saleRepository;
    @Mock private ProductService productService;
    @Mock private SaleDetailsRepository saleDetailsRepository;
    @Mock private CustomUserDetailsService customUserDetailsService;
    @Mock private UserService userService;

    @InjectMocks
    private SaleService saleService;

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testUser");

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void processSale_shouldProcessCorrectly() {

        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        Product product = new Product();
        product.setId(1L);
        product.setPrice(10.0);

        Sale sale = new Sale();
        SaleItemForm itemForm = new SaleItemForm(1L, 2);
        List<SaleItemForm> items = List.of(itemForm);

        when(userService.getUserByUsername("testUser")).thenReturn(Optional.of(user));
        when(productService.getProductById(1L)).thenReturn(product);
        when(saleRepository.save(any(Sale.class))).thenAnswer(inv -> {
            Sale saved = inv.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        Sale result = saleService.processSale(sale, items);

        assertEquals(20.0, result.getTotal());
        assertEquals("testUser", result.getUser().getUsername());
        verify(productService).updateStock(1L, 2);
        verify(saleDetailsRepository).save(any(SaleDetails.class));
    }

    @Test
    void getSalesByDate_shouldHandleNullDates() {
        List<Sale> mockSales = List.of(new Sale(), new Sale());
        when(saleRepository.findBySaleDateBetween(any(), any())).thenReturn(mockSales);

        List<Sale> result = saleService.getSalesByDate(null, null);
        assertEquals(2, result.size());
    }

    @Test
    void getSaleById_shouldReturnSale() {
        Sale sale = new Sale();
        when(saleRepository.findById(1L)).thenReturn(Optional.of(sale));
        assertEquals(sale, saleService.getSaleById(1L));
    }

    @Test
    void getSaleById_shouldThrowIfNotFound() {
        when(saleRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> saleService.getSaleById(1L));
    }

    @Test
    void deleteSale_shouldDeleteSaleWithoutClaim() {
        Product product = new Product();
        product.setId(1L);

        SaleDetails detail = new SaleDetails();
        detail.setProduct(product);
        detail.setQuantity(2);

        Sale sale = new Sale();
        sale.setId(1L);
        sale.setDetails(List.of(detail));

        when(saleRepository.findById(1L)).thenReturn(Optional.of(sale));

        saleService.deleteSale(1L);

        verify(productService).updateStock(1L, 2);
        verify(saleDetailsRepository).delete(detail);
        verify(saleRepository).delete(sale);
    }

    @Test
    void deleteSale_shouldThrowIfClaimExists() {
        Sale sale = new Sale();
        sale.setClaim(new Claim());
        when(saleRepository.findById(1L)).thenReturn(Optional.of(sale));

        assertThrows(RuntimeException.class, () -> saleService.deleteSale(1L));
        verify(saleRepository, never()).delete(any());
    }

    @Test
    void getDailySalesData_shouldCallRepository() {
        LocalDateTime now = LocalDateTime.now();

        // 1. Crear datos de prueba simulados (mockData)
        List<Object[]> mockData = Arrays.asList(new Object[]{LocalDate.now(), 150.0}, new Object[]{LocalDate.now(), 200.0});

        // 2. Configurar el mock del repositorio
        when(saleRepository.findDailySalesByUser(now.minusDays(7), now))
                .thenReturn(mockData);

        // 3. Ejecutar el método del servicio
        List<Object[]> result = saleService.getDailySalesData(now.minusDays(7), now);

        // 4. Verificaciones
        assertEquals(2, result.size()); // Verifica que la lista tiene 1 elemento
        assertEquals(LocalDate.now(), result.get(0)[0]); // Verifica la fecha
        assertEquals(150.0, result.get(0)[1]); // Verifica el total de ventas
        assertEquals(LocalDate.now(), result.get(1)[0]); // Verifica la fecha
        assertEquals(200.0, result.get(1)[1]); // Verifica el total de ventas

        // 5. Verificar que el repositorio fue llamado correctamente
        verify(saleRepository).findDailySalesByUser(now.minusDays(7), now);
    }

    @Test
    void getTopSellers_shouldCallRepository() {
        LocalDateTime now = LocalDateTime.now();

        // Crear una lista con datos de ejemplo (simulando lo que devolvería el repositorio)
        List<Object[]> mockData = Arrays.asList(
                new Object[]{"Seller1", 100L},  // Ejemplo: [nombreVendedor, cantidadVentas]
                new Object[]{"Seller2", 50L}    // Segundo vendedor (opcional)
        );

        // Configurar el mock del repositorio
        when(saleRepository.findTopSellers(now.minusDays(7), now)).thenReturn(mockData);

        // Llamar al servicio y verificar
        List<Object[]> result = saleService.getTopSellers(now.minusDays(7), now);

        // Verificaciones
        assertEquals(mockData.size(), result.size()); // Compara tamaños
        verify(saleRepository).findTopSellers(now.minusDays(7), now); // Verifica que se llamó al repositorio
    }

    @Test
    void getAllSales_shouldReturnAllSales() {
        List<Sale> sales = List.of(new Sale(), new Sale());
        when(saleRepository.findAll()).thenReturn(sales);
        assertEquals(2, saleService.getAllSales().size());
    }

    @Test
    void getSalesWithoutClaims_shouldUseOptimizedRepositoryMethod() {
        Sale s1 = new Sale();
        s1.setTotal(100);
        List<Sale> mockSales = List.of(s1);

        // Configuramos el mock para el NUEVO método optimizado
        when(saleRepository.findSalesWithTotalAndNoClaim()).thenReturn(mockSales);

        List<Sale> result = saleService.getSalesWithoutClaims();

        assertEquals(1, result.size());
        
        // Verificamos que se llame al método optimizado
        verify(saleRepository).findSalesWithTotalAndNoClaim();
        // Verificamos que NO se llame al método ineficiente
        verify(saleRepository, never()).findAll();
    }
}
