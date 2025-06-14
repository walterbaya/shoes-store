package com.shoesstore.shoesstore.controller;

import com.shoesstore.shoesstore.model.Sale;
import com.shoesstore.shoesstore.service.CustomUserDetailsService;
import com.shoesstore.shoesstore.service.ReportService;
import com.shoesstore.shoesstore.service.SaleService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Controller
@AllArgsConstructor
public class DashboardController {

    private final CustomUserDetailsService customUserDetailsService;
    private final SaleService saleService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay(); // hoy a las 00:00
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX); // hoy a las 23:59:59.999999999
        List<Sale> todaySales = saleService.getSalesByDate(startOfDay, endOfDay);
        List<Sale> pastFiveDaysFromNowSales = saleService.getSalesByDate(today.minusDays(7).atStartOfDay(),endOfDay);
        double dailyEarnings = todaySales.stream().mapToDouble(Sale::getTotal).sum();
        model.addAttribute("title", "Dashboard");
        model.addAttribute("view", "dashboard"); // Fragmento
        model.addAttribute("username", customUserDetailsService.getCurrentUserName());
        model.addAttribute("dailySales", todaySales.size());
        model.addAttribute("dailyEarnings", dailyEarnings);
        model.addAttribute("ultimasCompras", pastFiveDaysFromNowSales);

        return "layout";  // Usa el archivo layout.html
    }
}