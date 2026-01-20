package com.shoesstore.shoesstore.controller;

import com.shoesstore.shoesstore.model.enums.PaymentMethod;
import com.shoesstore.shoesstore.model.Product;
import com.shoesstore.shoesstore.model.Sale;
import com.shoesstore.shoesstore.model.SaleDetails;
import com.shoesstore.shoesstore.model.enums.SaleChannel;
import com.shoesstore.shoesstore.service.ProductService;
import com.shoesstore.shoesstore.service.SaleService;
import com.shoesstore.shoesstore.utils.SaleForm;
import com.shoesstore.shoesstore.utils.SaleItemForm;
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
    public String listSales(Model model) {
        List<Sale> sales = saleService.getAllSales();
        double totalSalesSum = sales.stream().mapToDouble(Sale::getTotalWithShippingCost).sum();
        Map<SaleChannel, Long> salesCountByChannel = sales.stream().collect(Collectors.groupingBy(Sale::getChannel, Collectors.counting()));
        long onlineSalesCount = salesCountByChannel.getOrDefault(SaleChannel.ONLINE, 0L);
        long storeSalesCount = salesCountByChannel.getOrDefault(SaleChannel.TIENDA, 0L);

        model.addAttribute("title", "Listado de Ventas");
        model.addAttribute("sales", sales);
        model.addAttribute("onlineSalesCount", onlineSalesCount);
        model.addAttribute("totalSalesCount", sales.size());
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
            RedirectAttributes redirectAttrs,    // ← inyectamos RedirectAttributes
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("channels", SaleChannel.values());
            model.addAttribute("products", productService.getAllProducts());
            model.addAttribute("view", "sales/list");
            redirectAttrs.addFlashAttribute("error", "Hubo un error al procesar la venta."); // ← flash
            return "redirect:/sales";  // ← redirect igual
        }

        // Filtrar ítems con qty > 0

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
            saleService.processSale(sale, itemsToProcess);

            redirectAttrs.addFlashAttribute("success", "Venta registrada exitosamente."); // ← flash
        } else {
            redirectAttrs.addFlashAttribute("error", "No se pueden vender 0 o menos productos."); // ← flash
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


