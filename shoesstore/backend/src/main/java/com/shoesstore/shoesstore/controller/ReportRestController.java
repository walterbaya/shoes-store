package com.shoesstore.shoesstore.controller;

import com.shoesstore.shoesstore.service.ReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/reports/sales")
public class ReportRestController {

    private final ReportService reportService;

    public ReportRestController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/period")
    public Map<String, Object> byPeriod(@RequestParam String startDate,
                                        @RequestParam String endDate) {
        return reportService.generateSalesReport("period", startDate, endDate);
    }


    @GetMapping("/daily")
    public Map<String, Object> daily(@RequestParam String startDate,
                                     @RequestParam String endDate) {
        return reportService.generateSalesReport("daily", startDate, endDate);
    }

    @GetMapping("/by-user")
    public Map<String, Object> byUser(@RequestParam String startDate,
                                      @RequestParam String endDate) {
        return reportService.generateSalesReport("byUser", startDate, endDate);
    }

    @GetMapping("/by-product")
    public Map<String, Object> byProduct(@RequestParam String startDate,
                                         @RequestParam String endDate) {
        return reportService.generateSalesReport("byProduct", startDate, endDate);
    }
}
