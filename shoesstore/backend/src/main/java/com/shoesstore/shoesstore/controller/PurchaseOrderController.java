package com.shoesstore.shoesstore.controller;

import com.shoesstore.shoesstore.model.*;
import com.shoesstore.shoesstore.service.ProductService;
import com.shoesstore.shoesstore.service.PurchaseOrderService;
import com.shoesstore.shoesstore.service.SupplierService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/orders")
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;
    private final SupplierService supplierService;
    private final ProductService productService;

    public PurchaseOrderController(PurchaseOrderService purchaseOrderService,
                                   SupplierService supplierService,
                                   ProductService productService) {
        this.purchaseOrderService = purchaseOrderService;
        this.supplierService = supplierService;
        this.productService = productService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("orders", purchaseOrderService.findAll());
        model.addAttribute("view", "orders/list");
        return "layout";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        List<Supplier> suppliers = supplierService.findAll();

        Map<Supplier, List<SupplierProduct>> productsToBuyBySupplier = suppliers.stream()
                .collect(Collectors.toMap(
                        s -> s,
                        s -> new ArrayList<>(s.getSupplierProducts())
                ));

        model.addAttribute("productsToBuyBySupplier", productsToBuyBySupplier);
        model.addAttribute("view", "orders/create");

        return "layout";
    }

    @PostMapping("/create")
    public String createOrder(@RequestParam Long supplierId,
                              @RequestParam Map<String, String> allRequestParams) {
        Map<Long, Integer> productsQuantities = allRequestParams.entrySet().stream()
                .filter(e -> e.getKey().startsWith("product_"))
                .collect(Collectors.toMap(
                        e -> Long.parseLong(e.getKey().substring("product_".length())),
                        e -> Integer.parseInt(e.getValue())
                ));

        purchaseOrderService.createOrder(supplierId, productsQuantities);
        return "redirect:/orders";
    }

    @GetMapping("/{id}")
    public String viewOrder(@PathVariable Long id, Model model) {
        PurchaseOrder order = purchaseOrderService.findById(id);

        // Filtrar items con cantidad > 0 y calcular total
        List<PurchaseOrderItem> filteredItems = order.getItems().stream()
                .filter(item -> item.getQuantity() > 0)
                .collect(Collectors.toList());

        BigDecimal total = filteredItems.stream()
                .map(item -> item.getPurchasePrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("order", order);
        model.addAttribute("filteredItems", filteredItems);
        model.addAttribute("total", total);
        model.addAttribute("view", "orders/detail");

        return "layout";
    }


    @PostMapping("/{id}/complete")
    public String complete(@PathVariable Long id) {
        purchaseOrderService.completeOrder(id);
        return "redirect:/orders/{id}";
    }
}
