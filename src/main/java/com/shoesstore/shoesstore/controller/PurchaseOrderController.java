package com.shoesstore.shoesstore.controller;

import com.shoesstore.shoesstore.model.*;
import com.shoesstore.shoesstore.model.enums.PriorityCondition;
import com.shoesstore.shoesstore.repository.PurchaseOrderAttachmentRepository;
import com.shoesstore.shoesstore.service.*;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;
import java.time.LocalDate;
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
    private final UserService userService;
    private final PurchaseOrderAttachmentRepository purchaseOrderAttachmentRepository;
    private final FileStorageService fileStorageService;

    public PurchaseOrderController(PurchaseOrderService purchaseOrderService,
                                   SupplierService supplierService,
                                   ProductService productService, UserService userService, PurchaseOrderAttachmentRepository purchaseOrderAttachmentRepository, FileStorageService fileStorageService) {
        this.purchaseOrderService = purchaseOrderService;
        this.supplierService      = supplierService;
        this.productService       = productService;
        this.userService = userService;
        this.purchaseOrderAttachmentRepository = purchaseOrderAttachmentRepository;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("title", "Compras");
        model.addAttribute("username", "username");
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
            @ModelAttribute("order") PurchaseOrder order,
            @RequestParam(name="attachmentFile", required=false) MultipartFile attachment,
            @RequestParam(name="discountPct") BigDecimal discountPct,
            @RequestParam(name="shippingCost") BigDecimal shippingCost,
            RedirectAttributes redirectAttributes
    ) {
        try {
            // Completar campos que no vienen del form directamente o que requieren lógica
            order.setDiscount(discountPct);
            order.setShippingCost(shippingCost);
            order.setGeneratedDate(LocalDate.now());

            
            // Usuario autenticado
            User user = new User();
            order.setUser(user);

            // Mapear productos
            Map<Long,Integer> qtys = allParams.entrySet().stream()
                    .filter(e -> e.getKey().startsWith("product_"))
                    .collect(Collectors.toMap(
                            e -> Long.parseLong(e.getKey().substring(8)),
                            e -> Integer.parseInt(e.getValue())
                    ));

            purchaseOrderService.createOrder(
                    order,
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
        // Buscá la entidad Attachment por ID (asumo que tiene orderId y relativePath)
        PurchaseOrderAttachment att = purchaseOrderAttachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Resource resource = fileStorageService.loadFileAsResource(att.getStoragePath());
        String filename = att.getFileName(); // o extraelo desde la entidad

        return ResponseEntity.ok()
                .contentType(MediaTypeFactory.getMediaType(filename)
                        .orElse(MediaType.APPLICATION_OCTET_STREAM))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }


}