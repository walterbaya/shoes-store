package com.shoesstore.shoesstore.controller;

import com.shoesstore.shoesstore.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/reports/sales")
@Tag(name = "Reports API", description = "Endpoints para la generación de reportes de ventas")
public class ReportRestController {

    private final ReportService reportService;

    public ReportRestController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/period")
    @Operation(summary = "Reporte de ventas por período", description = "Genera un reporte de ventas agrupado por un período de tiempo.")
    @ApiResponse(responseCode = "200", description = "Reporte generado exitosamente")
    public Map<String, Object> byPeriod(
            @Parameter(description = "Fecha de inicio (YYYY-MM-DD)", required = true) @RequestParam String startDate,
            @Parameter(description = "Fecha de fin (YYYY-MM-DD)", required = true) @RequestParam String endDate) {
        return reportService.generateSalesReport("period", startDate, endDate);
    }


    @GetMapping("/daily")
    @Operation(summary = "Reporte de ventas diario", description = "Genera un reporte de ventas para un día específico.")
    @ApiResponse(responseCode = "200", description = "Reporte generado exitosamente")
    public Map<String, Object> daily(
            @Parameter(description = "Fecha (YYYY-MM-DD)", required = true) @RequestParam String startDate,
            @Parameter(description = "Fecha (YYYY-MM-DD)", required = true) @RequestParam String endDate) {
        return reportService.generateSalesReport("daily", startDate, endDate);
    }

    @GetMapping("/by-user")
    @Operation(summary = "Reporte de ventas por usuario", description = "Genera un reporte de ventas agrupado por usuario.")
    @ApiResponse(responseCode = "200", description = "Reporte generado exitosamente")
    public Map<String, Object> byUser(
            @Parameter(description = "Fecha de inicio (YYYY-MM-DD)", required = true) @RequestParam String startDate,
            @Parameter(description = "Fecha de fin (YYYY-MM-DD)", required = true) @RequestParam String endDate) {
        return reportService.generateSalesReport("byUser", startDate, endDate);
    }

    @GetMapping("/by-product")
    @Operation(summary = "Reporte de ventas por producto", description = "Genera un reporte de los productos más vendidos.")
    @ApiResponse(responseCode = "200", description = "Reporte generado exitosamente")
    public Map<String, Object> byProduct(
            @Parameter(description = "Fecha de inicio (YYYY-MM-DD)", required = true) @RequestParam String startDate,
            @Parameter(description = "Fecha de fin (YYYY-MM-DD)", required = true) @RequestParam String endDate) {
        return reportService.generateSalesReport("byProduct", startDate, endDate);
    }

    @GetMapping("/weekly-user-sales")
    @Operation(summary = "Reporte semanal de ventas por usuario", description = "Genera un reporte de ventas por usuario para la última semana.")
    @ApiResponse(responseCode = "200", description = "Reporte generado exitosamente")
    public Map<String, Object> weeklyUserSales(
            @Parameter(description = "Fecha de inicio (YYYY-MM-DD)", required = true) @RequestParam String startDate,
            @Parameter(description = "Fecha de fin (YYYY-MM-DD)", required = true) @RequestParam String endDate) {
        return reportService.generateSalesReport("weekly-user-sales", startDate, endDate);
    }

}
