package com.shoesstore.shoesstore.controller;

import com.shoesstore.shoesstore.model.Product;
import com.shoesstore.shoesstore.model.Supplier;
import com.shoesstore.shoesstore.service.ProductService;
import com.shoesstore.shoesstore.service.PurchaseOrderService;
import com.shoesstore.shoesstore.service.SupplierService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

        Map<Supplier, List<Product>> productsToBuyBySupplier = suppliers.stream()
                .collect(Collectors.toMap(
                        s -> s,
                        s -> s.getProducts().stream()
                                .filter(p -> p.getStock() <= p.getReorderLevel())
                                .collect(Collectors.toList())
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
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("order", purchaseOrderService.findById(id));
        model.addAttribute("view", "orders/detail");
        return "layout";
    }

    @PostMapping("/{id}/complete")
    public String complete(@PathVariable Long id) {
        purchaseOrderService.completeOrder(id);
        return "redirect:/orders/{id}";
    }
}
