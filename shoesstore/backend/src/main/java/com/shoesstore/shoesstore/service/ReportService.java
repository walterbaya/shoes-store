package com.shoesstore.shoesstore.service;

import com.shoesstore.shoesstore.model.Sale;
import com.shoesstore.shoesstore.repository.SaleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    private final SaleRepository saleRepository;

    public ReportService(SaleRepository saleRepository) {
        this.saleRepository = saleRepository;
    }

    public Map<String, Object> generateSalesReport(String reportType, String startDateString, String endDateString) {

        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.withHour(0).withMinute(0).withSecond(0).withNano(0);

    	if(!startDateString.isEmpty()) {
            startDate = LocalDate.parse(startDateString).atStartOfDay();
            endDate = LocalDate.parse(endDateString).atStartOfDay();
    	}
        else if(!endDateString.isEmpty()){
            endDate = LocalDate.parse(endDateString).atStartOfDay();
        }

        switch (reportType) {
            case "byUser":
            case "byProduct":
                startDate = endDate.minusMonths(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
                break;
            case "period":
            	break;
            case "weekly":
            default:
                startDate = endDate.minusWeeks(1);
                reportType = "weekly";
                break;
        }

        Map<String, Object> result = new HashMap<>();

        if ("byUser".equals(reportType)) {
            List<Object[]> raw = saleRepository.fetchSalesByUser(startDate, endDate);
            List<String> labels = new ArrayList<>();
            List<Number> values = new ArrayList<>();
            List<Map<String, Object>> rows = new ArrayList<>();
            for (Object[] o : raw) {
                labels.add((String) o[0]);
                values.add((Number) o[1]);
                rows.add(Map.of("Usuario", o[0], "Ventas", o[1]));
            }
            result.put("labels", labels);
            result.put("datasets", List.of(Map.of("label", "Ventas por Usuario", "data", values, "borderWidth", 1)));
            result.put("tableRows", rows);
            return result;
        }

        if ("byProduct".equals(reportType)) {
            List<Object[]> raw = saleRepository.fetchSalesByProduct(startDate, endDate);
            
            List<String> labels = new ArrayList<>();
            List<Number> quantities = new ArrayList<>();
            List<Map<String, Object>> rows = new ArrayList<>();
            for (Object[] o : raw) {
                labels.add((String) o[0]);
                quantities.add((Number) o[1]);
                rows.add(Map.of("Producto", o[0], "Cantidad", o[1], "Total", String.format("%.2f", o[2])));
            }
            result.put("labels", labels);
            result.put("datasets", List.of(Map.of("label", "Top Productos", "data", quantities, "borderWidth", 1)));
            result.put("tableRows", rows);
            return result;
        }

        List<Sale> sales = saleRepository.findBySaleDateBetween(startDate, endDate);
        Map<String, Double> agg = new LinkedHashMap();
        List<Map<String, Object>> rows = new ArrayList<>();
        for (Sale s : sales) {
            String key = formatPeriod(s.getSaleDate(), reportType);
            agg.put(key, agg.getOrDefault(key, 0.0) + s.getTotal());
        }
        List<String> labels = new ArrayList<>(agg.keySet());
        List<Number> data = new ArrayList<>(agg.values());
        for (Map.Entry<String, Double> e : agg.entrySet()) {
            rows.add(Map.of("period", e.getKey(), "totalSales", e.getValue(), "transactionCount", Collections.frequency(new ArrayList<>(agg.values()), e.getValue())));
        }
        result.put("labels", labels);
        result.put("datasets", List.of(Map.of("label", "Total de Ventas", "data", data, "borderWidth", 1)));
        result.put("tableRows", rows);
        return result;
    }

    private String formatPeriod(LocalDateTime date, String reportType) {
        if ("weekly".equals(reportType)) {
            return "Semana " + date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        }
        return date.getMonth().toString() + " " + date.getYear();
    }
}
