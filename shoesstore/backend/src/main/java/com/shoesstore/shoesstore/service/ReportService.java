package com.shoesstore.shoesstore.service;

import com.shoesstore.shoesstore.model.Sale;
import com.shoesstore.shoesstore.repository.SaleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    private final SaleRepository saleRepository;
    private static final DateTimeFormatter DMY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ReportService(SaleRepository saleRepository) {
        this.saleRepository = saleRepository;
    }

    public Map<String, Object> generateSalesReport(String reportType,
                                                   String startDateString,
                                                   String endDateString) {

        // Rango por defecto: última semana completa
        LocalDateTime endDate = LocalDateTime.now()
                .withHour(23)
                .withMinute(59)
                .withSecond(59)
                .withNano(999_999_999);
        LocalDateTime startDate = endDate.minusWeeks(1)
                .with(LocalTime.MIN);

        // Si vienen fechas por parámetro, las parseamos
        if (startDateString != null && !startDateString.isEmpty()) {
            LocalDate parsedStart = LocalDate.parse(startDateString);
            startDate = parsedStart.atStartOfDay();
        }
        if (endDateString != null && !endDateString.isEmpty()) {
            LocalDate parsedEnd = LocalDate.parse(endDateString);
            endDate = parsedEnd.atTime(LocalTime.MAX);
        }

        // Ajuste especial: si es daily, igualamos inicio y fin
        if ("daily".equals(reportType)) {
            LocalDate day = startDate.toLocalDate();
            startDate = day.atStartOfDay();
            endDate = day.atTime(LocalTime.MAX);
        }

        // Ajustes de rango según tipo
        switch (reportType) {
            case "byUser":
            case "byProduct":
                startDate = endDate.minusMonths(1)
                        .withDayOfMonth(1)
                        .with(LocalTime.MIN);
                break;
            case "period":
                break;
            case "weekly":
            case "weekly-user-sales":
                startDate = endDate.minusWeeks(1)
                        .with(LocalTime.MIN);
                break;
            case "daily":
                // ya está ajustado
                break;
            default:
                reportType = "weekly";
                startDate = endDate.minusWeeks(1)
                        .with(LocalTime.MIN);
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
                rows.add(Map.of("Usuario", o[0], "Ventas", "$ " +  String.format("%.2f", o[1])));
            }
            result.put("labels", labels);
            result.put("datasets", List.of(
                    Map.of("label", "Ventas por Usuario", "data", values, "borderWidth", 1)
            ));
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
                rows.add(Map.of(
                        "Producto", o[0],
                        "Cantidad", o[1],
                        "Total", "$ " + String.format("%.2f", o[2])
                ));
            }
            result.put("labels", labels);
            result.put("datasets", List.of(
                    Map.of("label", "Top Productos", "data", quantities, "borderWidth", 1)
            ));
            result.put("tableRows", rows);
            return result;
        }

        List<Sale> sales = saleRepository.findBySaleDateBetween(startDate, endDate);
        Map<String, Double> agg = new LinkedHashMap<>();
        List<Map<String, Object>> rows = new ArrayList<>();
        for (Sale s : sales) {
            String key = formatPeriod(s.getSaleDate(), reportType);
            agg.put(key, agg.getOrDefault(key, 0.0) + s.getTotal());
        }
        List<String> labels = new ArrayList<>(agg.keySet());
        List<Number> data = new ArrayList<>(agg.values());
        for (Map.Entry<String, Double> e : agg.entrySet()) {
            rows.add(Map.of(
                    "periodo", e.getKey(),
                    "Total en Ventas", "$ " + String.format("%.2f", e.getValue()),
                    "Cantidad de Ventas", Collections.frequency(new ArrayList<>(agg.values()), e.getValue())
            ));
        }
        result.put("labels", labels);
        result.put("datasets", List.of(
                Map.of("label", "Total de Ventas", "data", data, "borderWidth", 1)
        ));
        result.put("tableRows", rows);
        return result;
    }

    /**
     * Formatea cualquier LocalDateTime a DD/MM/YYYY.
     * En weekly calcula el inicio de la semana ISO (lunes).
     */
    private String formatPeriod(LocalDateTime date, String reportType) {
        switch (reportType) {
            case "weekly":
            case "weekly-user-sales":
                LocalDate startOfWeek = date.toLocalDate()
                        .with(WeekFields.ISO.getFirstDayOfWeek());
                return startOfWeek.format(DMY);
            case "daily":
                return date.format(DMY);
            default:
                return date.format(DMY);
        }
    }
}
