package com.shoesstore.shoesstore.service;

import com.shoesstore.shoesstore.model.Sale;
import com.shoesstore.shoesstore.repository.SaleRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

        // 1) Rango por defecto: última semana completa
        LocalDateTime endDate = LocalDateTime.now().with(LocalTime.MAX);
        LocalDateTime startDate = endDate.minusWeeks(1).with(LocalTime.MIN);

        // 2) Parseo de fechas de parámetro si vienen
        if (StringUtils.hasText(startDateString)) {
            startDate = LocalDate.parse(startDateString).atStartOfDay();
        }
        if (StringUtils.hasText(endDateString)) {
            endDate = LocalDate.parse(endDateString).atTime(LocalTime.MAX);
        }

        // 3) Ajustes específicos por tipo de reporte
        switch (reportType) {
            case "weekly-user-sales":
                // ya quedan startDate y endDate en la última semana
                break;
            case "daily":
            case "daily-user-sales":
                LocalDate day = startDate.toLocalDate();
                startDate = day.atStartOfDay();
                endDate   = day.atTime(LocalTime.MAX);
                break;
            case "byUser":
                startDate = startDate.with(LocalTime.MIN);
                break;
            case "byProduct":
                startDate = endDate.minusMonths(1)
                        .withDayOfMonth(1)
                        .with(LocalTime.MIN);
                break;
            case "period":
                // no cambia nada
                break;
            default:
                // fallback a weekly-user-sales
                reportType = "weekly-user-sales";
                startDate = endDate.minusWeeks(1).with(LocalTime.MIN);
        }

        Map<String, Object> result = new HashMap<>();

        // === NUEVA LÓGICA para weekly-user-sales y daily-user-sales ===
        if ("weekly-user-sales".equals(reportType) || "daily-user-sales".equals(reportType)) {
            // fetchSalesByUser devuelve List<Object[]> con { usuario, totalVentas }
            List<Object[]> raw = saleRepository.fetchSalesByUser(startDate, endDate);

            List<String> labels = new ArrayList<>();
            List<Number> data  = new ArrayList<>();
            List<Map<String, Object>> rows = new ArrayList<>();

            for (Object[] fila : raw) {
                String usuario = (String) fila[0];
                Number total   = (Number) fila[1];
                labels.add(usuario);
                data.add(total);
                rows.add(Map.of(
                        "Usuario", usuario,
                        "TotalVentas", total
                ));
            }

            result.put("labels", labels);
            result.put("datasets", List.of(
                    Map.of(
                            "label", "Ventas por Usuario",
                            "data", data,
                            "borderWidth", 1
                    )
            ));
            result.put("tableRows", rows);
            return result;
        }

        // === CASOS EXISTENTES ===

        if ("byUser".equals(reportType)) {
            List<Object[]> raw = saleRepository.fetchSalesByUser(startDate, endDate);
            List<String> labels = new ArrayList<>();
            List<Number> values = new ArrayList<>();
            List<Map<String, Object>> rows = new ArrayList<>();
            for (Object[] o : raw) {
                labels.add((String) o[0]);
                values.add((Number) o[1]);
                rows.add(Map.of(
                        "Usuario", o[0],
                        "Ventas", "$ " + String.format("%.2f", o[1])
                ));
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

        // Agregación genérica por periodos (weekly, daily genérico, period, etc.)
        List<Sale> sales = saleRepository.findBySaleDateBetween(startDate, endDate);
        Map<String, Double> agg = new LinkedHashMap<>();
        List<Map<String, Object>> periodRows = new ArrayList<>();
        for (Sale s : sales) {
            String key = formatPeriod(s.getSaleDate(), reportType);
            agg.put(key, agg.getOrDefault(key, 0.0) + s.getTotal());
        }
        List<String> periodLabels = new ArrayList<>(agg.keySet());
        List<Number> periodData = new ArrayList<>(agg.values());
        for (Map.Entry<String, Double> e : agg.entrySet()) {
            periodRows.add(Map.of(
                    "Periodo", e.getKey(),
                    "Total en Ventas", "$ " + String.format("%.2f", e.getValue()),
                    "Cantidad de Ventas", Collections.frequency(new ArrayList<>(agg.values()), e.getValue())
            ));
        }
        result.put("labels", periodLabels);
        result.put("datasets", List.of(
                Map.of("label", "Total de Ventas", "data", periodData, "borderWidth", 1)
        ));
        result.put("tableRows", periodRows);
        return result;
    }

    /**
     * Formatea LocalDateTime a DD/MM/YYYY; para weekly pone el lunes ISO.
     */
    private String formatPeriod(LocalDateTime date, String reportType) {
        switch (reportType) {
            case "weekly":
            case "weekly-user-sales":
                LocalDate startOfWeek = date.toLocalDate()
                        .with(java.time.temporal.WeekFields.ISO.getFirstDayOfWeek());
                return startOfWeek.format(DMY);
            case "daily":
            case "daily-user-sales":
                return date.format(DMY);
            default:
                return date.format(DMY);
        }
    }
}