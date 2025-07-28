package com.shoesstore.shoesstore.service;

import com.shoesstore.shoesstore.model.Sale;
import com.shoesstore.shoesstore.repository.ProductRepository;
import com.shoesstore.shoesstore.repository.SaleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ReportService reportService;

    @Test
    void generateSalesReport_weeklyUserSales_shouldReturnCorrectData() {
        LocalDateTime now = LocalDateTime.now();
        List<Object[]> rawData = List.of(
                new Object[]{"juan", 120.5},
                new Object[]{"maria", 99.9}
        );
        when(saleRepository.fetchSalesByUser(any(), any())).thenReturn(rawData);

        Map<String, Object> report = reportService.generateSalesReport("weekly-user-sales", null, null);

        assertEquals(List.of("juan", "maria"), report.get("labels"));
        assertEquals(1, ((List<?>) report.get("datasets")).size());
        assertTrue(report.containsKey("tableRows"));
    }

/*    @Test
    void generateSalesReport_byUser_shouldFormatDataCorrectly() {
        List<Object[]> rawData = List.of(new Object[]{"carlos", 100.0}, new Object[]{"carlos", 100.0});
        when(saleRepository.fetchSalesByUser(any(), any())).thenReturn(rawData);

        Map<String, Object> report = reportService.generateSalesReport("byUser", "2024-01-01", "2024-01-31");

        assertEquals(List.of("carlos"), report.get("labels"));
        assertTrue(report.containsKey("datasets"));
        assertTrue(report.containsKey("tableRows"));
    }*/

/*
    @Test
    void generateSalesReport_byProduct_shouldReturnDetailedRows() {
        List<Object[]> raw = List.of(new Object[]{1L, 5, 250.0});
        Product product = new Product();
        product.setId(1L);
        product.setType("Zapatilla");
        product.setBrand("Nike");
        product.setMaterial("Cuero");

        when(saleRepository.fetchSalesByProduct(any(), any())).thenReturn(raw);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Map<String, Object> report = reportService.generateSalesReport("byProduct", "2024-06-01", "2024-06-30");

        assertEquals(List.of("Zapatilla Nike Cuero"), report.get("labels"));
        assertTrue(report.containsKey("tableRows"));
    }
*/

    @Test
    void generateSalesReport_period_shouldAggregateSalesByDay() {
        Sale sale1 = new Sale();
        sale1.setSaleDate(LocalDateTime.of(2024, 6, 10, 12, 0));
        sale1.setTotal(150.0);

        Sale sale2 = new Sale();
        sale2.setSaleDate(LocalDateTime.of(2024, 6, 10, 15, 0));
        sale2.setTotal(200.0);

        when(saleRepository.findBySaleDateBetween(any(), any()))
                .thenReturn(List.of(sale1, sale2));

        Map<String, Object> report = reportService.generateSalesReport("period", "2024-06-10", "2024-06-10");

        assertTrue(report.containsKey("labels"));
        assertTrue(report.containsKey("datasets"));
        assertEquals(1, ((List<?>) report.get("labels")).size()); // solo un día
        assertEquals(350.0, ((List<?>) report.get("datasets")).get(0) instanceof Map<?, ?> dataset
                ? ((List<?>) ((Map<?, ?>) dataset).get("data")).get(0)
                : null);
    }
}
