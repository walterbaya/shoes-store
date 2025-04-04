package com.shoesstore.shoesstore.controller;

import com.shoesstore.shoesstore.model.Sale;
import com.shoesstore.shoesstore.service.ReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Controller
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/sales")
    public String salesReport(
            @RequestParam(defaultValue = "weekly") String reportType,
            Model model
    ) {
        model.addAllAttributes(reportService.generateSalesReport(reportType));
        // Se asume que el layout renderizará la vista "reports/salesReport"
        model.addAttribute("view", "reports/salesReport");
        return "layout";  // Layout Thymeleaf que incluye el contenido de 'view'
    }
}