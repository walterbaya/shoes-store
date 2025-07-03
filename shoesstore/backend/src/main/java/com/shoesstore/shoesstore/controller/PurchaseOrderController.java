package com.shoesstore.shoesstore.controller;

import com.shoesstore.shoesstore.model.*;
import com.shoesstore.shoesstore.service.ProductService;
import com.shoesstore.shoesstore.service.PurchaseOrderService;
import com.shoesstore.shoesstore.service.SupplierService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/orders")
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;
    private final SupplierService      supplierService;
    private final ProductService       productService;

    public PurchaseOrderController(PurchaseOrderService purchaseOrderService,
                                   SupplierService supplierService,
                                   ProductService productService) {
        this.purchaseOrderService = purchaseOrderService;
        this.supplierService      = supplierService;
        this.productService       = productService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("title", "Compras");
        model.addAttribute("orders", purchaseOrderService.findAll());
        model.addAttribute("completedOrders", purchaseOrderService.findAll().stream().filter(PurchaseOrder::isCompleted).toList());
        model.addAttribute("sumOfTotals", purchaseOrderService.findAll().stream().map(PurchaseOrder::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add));
        model.addAttribute("view", "orders/list");
        return "layout";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        List<Supplier> suppliers = supplierService.findAll();
        Map<Supplier, List<?>> map = suppliers.stream()
                .collect(Collectors.toMap(
                        s -> s,
                        s -> new ArrayList<>(s.getSupplierProducts())
                ));
        model.addAttribute("productsToBuyBySupplier", map);
        model.addAttribute("order", new PurchaseOrder());
        model.addAttribute("priorities", PriorityCondition.values());
        model.addAttribute("view", "orders/create");
        return "layout";
    }

    @PostMapping("/create")
    public String createOrder(
            @RequestParam Long supplierId,
            @RequestParam Map<String,String> allParams,
            @ModelAttribute("order") PurchaseOrder purchaseOrder,
            @RequestParam(name="attachmentFile", required=false) MultipartFile attachment,
            @RequestParam(name="discountPct") BigDecimal discountPct,
            @RequestParam(name="shippingCost") BigDecimal shippingCost,
            RedirectAttributes redirectAttributes
    ) {
        try {
            // Extraigo cantidades de productos
            Map<Long,Integer> qtys = allParams.entrySet().stream()
                    .filter(e -> e.getKey().startsWith("product_"))
                    .collect(Collectors.toMap(
                            e -> Long.parseLong(e.getKey().substring(8)),
                            e -> Integer.parseInt(e.getValue())
                    ));

            // Llamo al servicio pasando el objeto completo
            purchaseOrderService.createOrder(
                    purchaseOrder,
                    supplierId,
                    qtys,
                    discountPct,
                    shippingCost,
                    attachment
            );
            return "redirect:/orders";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear orden: " + e.getMessage());
            return "redirect:/orders/create";
        }
    }

    @GetMapping("/{id}")
    public String viewOrder(@PathVariable Long id, Model model) {
        PurchaseOrder order = purchaseOrderService.findById(id);
        if (order == null) {
            return "redirect:/orders";
        }

        var filtered = order.getItems().stream()
                .filter(i -> i.getQuantity()>0)
                .toList();
        BigDecimal total = filtered.stream()
                .map(i -> i.getPurchasePrice()
                        .multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("order", order);
        model.addAttribute("filteredItems", filtered);
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