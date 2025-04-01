package com.shoesstore.shoesstore.controller;

import com.shoesstore.shoesstore.service.ReportService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
        return "reports/sales";
    }
}