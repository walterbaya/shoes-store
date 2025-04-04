package com.shoesstore.shoesstore.service;

import com.shoesstore.shoesstore.model.Sale;
import com.shoesstore.shoesstore.repository.SaleRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    private final SaleRepository saleRepository;

    public ReportService(SaleRepository saleRepository) {
        this.saleRepository = saleRepository;
    }

    public Map<String, Object> generateSalesReport(String reportType) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate;

        // Determinar rango según el tipo de reporte
        if ("weekly".equalsIgnoreCase(reportType)) {
            startDate = endDate.minusWeeks(1);
        } else {
            // Para reporte mensual: inicia el día 1 del mes a las 00:00
            startDate = endDate.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        }

        // Obtener ventas en el rango
        List<Sale> sales = saleRepository.findBySaleDateBetween(startDate, endDate);

        // Procesar datos para el reporte
        Map<String, Object> reportData = new HashMap<>();
        List<String> labels = new ArrayList<>();
        List<Double> data = new ArrayList<>();
        List<Map<String, Object>> reportList = new ArrayList<>();

        sales.forEach(sale -> {
            String periodKey = getPeriodKey(sale.getSaleDate(), reportType);
            Double saleTotal = sale.getTotal();

            // Actualizar datos del gráfico
            if (!labels.contains(periodKey)) {
                labels.add(periodKey);
                data.add(saleTotal);
            } else {
                int index = labels.indexOf(periodKey);
                data.set(index, data.get(index) + saleTotal);
            }

            // Actualizar datos de la tabla
            updateReportList(reportList, periodKey, saleTotal);
        });

        reportData.put("chartLabels", labels);
        reportData.put("chartData", data);
        reportData.put("reports", reportList);

        return reportData;
    }

    private String getPeriodKey(LocalDateTime date, String reportType) {
        if ("weekly".equalsIgnoreCase(reportType)) {
            return "Semana " + date.getDayOfWeek().getValue();
        } else {
            return date.getMonth().toString() + " " + date.getYear();
        }
    }

    private void updateReportList(List<Map<String, Object>> reportList, String period, Double total) {
        boolean found = false;
        for (Map<String, Object> item : reportList) {
            if (item.get("period").equals(period)) {
                item.put("totalSales", (Double) item.get("totalSales") + total);
                item.put("transactionCount", (Integer) item.get("transactionCount") + 1);
                found = true;
                break;
            }
        }
        if (!found) {
            Map<String, Object> newItem = new HashMap<>();
            newItem.put("period", period);
            newItem.put("totalSales", total);
            newItem.put("transactionCount", 1);
            reportList.add(newItem);
        }
    }
}