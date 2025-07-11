package com.shoesstore.shoesstore.controller;


import com.shoesstore.shoesstore.service.ReportService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/reports/sales")
public class ReportController {

    @GetMapping
    public String salesReport(Model model) {
        model.addAttribute("title", "Reporte de Ventas");
        model.addAttribute("view", "reports/salesReport");
        return "layout";
    }

}
