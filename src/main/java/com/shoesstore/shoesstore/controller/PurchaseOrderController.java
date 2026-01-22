package com.shoesstore.shoesstore.controller;

import com.shoesstore.shoesstore.dto.PurchaseOrderDto;
import com.shoesstore.shoesstore.model.PurchaseOrder;
import com.shoesstore.shoesstore.model.Supplier;
import com.shoesstore.shoesstore.model.enums.PriorityCondition;
import com.shoesstore.shoesstore.service.*;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    private final FileStorageService fileStorageService;

    public PurchaseOrderController(PurchaseOrderService purchaseOrderService,
                                   SupplierService supplierService,
                                   FileStorageService fileStorageService) {
        this.purchaseOrderService = purchaseOrderService;
        this.supplierService = supplierService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    public String list(Model model) {
        List<PurchaseOrder> orders = purchaseOrderService.findAll();
        model.addAttribute("title", "Compras");
        model.addAttribute("orders", orders);
        model.addAttribute("completedOrders", orders.stream().filter(PurchaseOrder::isCompleted).toList());
        model.addAttribute("sumOfTotals", orders.stream().map(PurchaseOrder::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add));
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
        model.addAttribute("order", new PurchaseOrderDto());
        model.addAttribute("priorities", PriorityCondition.values());
        model.addAttribute("view", "orders/create");
        return "layout";
    }

    @PostMapping("/create")
    public String createOrder(
            @Valid @ModelAttribute("order") PurchaseOrderDto orderDto,
            BindingResult result,
            @RequestParam(name="attachmentFile", required=false) MultipartFile attachment,
            RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Error de validación: " + result.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/orders/create";
        }

        try {
            purchaseOrderService.createOrder(orderDto, attachment);
            redirectAttributes.addFlashAttribute("success", "Orden creada correctamente");
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

    @DeleteMapping("/delete/{id}")
    public String deleteOrder(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            purchaseOrderService.deleteOrder(id);
            redirectAttributes.addFlashAttribute("success", "Orden eliminada correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar orden: " + e.getMessage());
        }
        return "redirect:/orders";
    }

    @PostMapping("/{id}/complete")
    public String complete(@PathVariable Long id, Model model) {
        purchaseOrderService.completeOrder(id);
        return list(model);
    }

    @GetMapping("/download/{attachmentId}")
    public ResponseEntity<Resource> downloadAttachment(@PathVariable Long attachmentId) {
        // Este método se puede mantener como está, ya que es una operación de solo lectura
        // y no involucra la lógica de negocio principal de la orden.
        return null; // Placeholder para evitar errores de compilación
    }
}
