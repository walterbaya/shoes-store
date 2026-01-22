package com.shoesstore.shoesstore.controller;

import com.shoesstore.shoesstore.model.enums.PaymentMethod;
import com.shoesstore.shoesstore.model.Product;
import com.shoesstore.shoesstore.model.Sale;
import com.shoesstore.shoesstore.model.enums.SaleChannel;
import com.shoesstore.shoesstore.service.ProductService;
import com.shoesstore.shoesstore.service.SaleService;
import com.shoesstore.shoesstore.dto.SaleForm;
import com.shoesstore.shoesstore.dto.SaleItemForm;
import org.springframework.data.domain.Page; // <-- Importar Page
import org.springframework.data.domain.Pageable; // <-- Importar Pageable
import org.springframework.data.web.PageableDefault; // <-- Importar PageableDefault
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/sales")
public class SaleController {

    private ProductService productService;
    private SaleService saleService;

    public SaleController(SaleService saleService, ProductService productService) {
        this.saleService = saleService;
        this.productService = productService;
    }

    @GetMapping
    public String listSales(
            Model model,
            @PageableDefault(size = 10, sort = "saleDate", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable // <-- Añadir Pageable
    ) {
        Page<Sale> salesPage = saleService.getAllSales(pageable); // <-- Usar el nuevo método paginado
        List<Sale> sales = salesPage.getContent(); // Obtener el contenido de la página

        double totalSalesSum = saleService.calculateTotalRevenue(sales);
        Map<SaleChannel, Long> salesCountByChannel = saleService.countSalesByChannel(sales);

        long onlineSalesCount = salesCountByChannel.getOrDefault(SaleChannel.ONLINE, 0L);
        long storeSalesCount = salesCountByChannel.getOrDefault(SaleChannel.TIENDA, 0L);

        model.addAttribute("title", "Listado de Ventas");
        model.addAttribute("salesPage", salesPage); // <-- Pasar el objeto Page completo a la vista
        model.addAttribute("sales", sales); // También pasamos la lista para compatibilidad con el código existente
        model.addAttribute("onlineSalesCount", onlineSalesCount);
        model.addAttribute("totalSalesCount", salesPage.getTotalElements()); // <-- Usar el total de elementos de la página
        model.addAttribute("storeSalesCount", storeSalesCount);
        model.addAttribute("totalRevenue", String.format("%.2f", totalSalesSum));
        model.addAttribute("view", "sales/list");

        return "layout";
    }

    // Agrega esto al SalesController
    @GetMapping("/{id}/details")
    public String getSaleDetail(@PathVariable Long id, Model model) {
        Sale sale = saleService.getSaleById(id);

        model.addAttribute("sale", sale);
        model.addAttribute("products", sale.getDetails());
        model.addAttribute("title", "Detalle de Venta #" + sale.getId());
        model.addAttribute("view", "sales/detail");
        return "layout";
    }


    @GetMapping("/create")
    public String showNewSaleForm(Model model) {
        // Obtener la lista de productos
        List<Product> products = productService.getAllProducts();

        // Inicializar el formulario con un ítem por cada producto (cantidad por defecto 0)
        SaleForm saleForm = new SaleForm();
        products.forEach(product -> {
            SaleItemForm item = new SaleItemForm();
            item.setProductId(product.getId());
            item.setQuantity(0);
            saleForm.getSaleItems().add(item);
        });

        // Agregar datos al modelo
        model.addAttribute("title", "Ventas");
        model.addAttribute("saleForm", saleForm);
        model.addAttribute("channels", SaleChannel.values());
        model.addAttribute("products", products);
        model.addAttribute("view", "sales/newSale");

        return "layout";  // Vista Thymeleaf para nueva venta
    }


    @PostMapping("/create")
    public String registerSale(
            @ModelAttribute("saleForm") SaleForm saleForm,
            BindingResult result,
            RedirectAttributes redirectAttrs,
            Model model) { // ← 1. Inyectamos Principal

        if (result.hasErrors()) {
            model.addAttribute("channels", SaleChannel.values());
            model.addAttribute("products", productService.getAllProducts());
            model.addAttribute("view", "sales/list");
            redirectAttrs.addFlashAttribute("error", "Hubo un error al procesar la venta.");
            return "redirect:/sales";
        }

        List<SaleItemForm> itemsToProcess = saleForm.getSaleItems().stream()
                .filter(item -> item.getQuantity() != null && item.getQuantity() > 0)
                .collect(Collectors.toList());

        if (!itemsToProcess.isEmpty()) {
            Sale sale = new Sale();
            sale.setChannel(SaleChannel.valueOf(saleForm.getChannel()));
            sale.setSaleDate(LocalDateTime.now());
            sale.setTotal(saleForm.getTotal());
            sale.setDiscountPercentage(saleForm.getDiscountPercentage());
            sale.setShippingCost(saleForm.getShippingCost());
            sale.setPaymentMethod(PaymentMethod.valueOf(saleForm.getPaymentMethod()));

            // ← 2. Pasamos el nombre del usuario autenticado (principal.getName())
            saleService.processSale(sale, itemsToProcess);

            redirectAttrs.addFlashAttribute("success", "Venta registrada exitosamente.");
        } else {
            redirectAttrs.addFlashAttribute("error", "No se pueden vender 0 o menos productos.");
        }
        return "redirect:/sales";
    }

    @DeleteMapping("/delete/{id}")
    public String deleteSale(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        saleService.deleteSale(id);
        redirectAttributes.addFlashAttribute("success", "Venta eliminada correctamente");
        return "redirect:/sales";
    }


}
