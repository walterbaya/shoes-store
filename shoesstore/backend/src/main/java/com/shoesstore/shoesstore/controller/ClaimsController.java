package com.shoesstore.shoesstore.controller;

import com.shoesstore.shoesstore.model.Product;
import com.shoesstore.shoesstore.model.Sale;
import com.shoesstore.shoesstore.service.ProductService;
import com.shoesstore.shoesstore.service.SaleService;
import com.shoesstore.shoesstore.utils.SaleForm;
import com.shoesstore.shoesstore.utils.SaleItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/sales")
public class ClaimsController {

    /*@Autowired
    private ClaimService claimService;

    @Autowired
    private SaleService saleService;

    @GetMapping
    public String showNewSaleForm(Model model) {
        // Obtener la lista de productos
        List<Product> claims = claimService.getallClaims();

        // Inicializar el formulario con un ítem por cada producto (cantidad por defecto 0)
        SaleForm saleForm = new SaleForm();
        claims.forEach(product -> {
            SaleItemForm item = new SaleItemForm();
            item.setProductId(claims.getId());
            item.setQuantity(0);
            saleForm.getSaleItems().add(item);
        });

        // Agregar datos al modelo
        model.addAttribute("saleForm", saleForm);
        model.addAttribute("channels", List.of("ONLINE", "TIENDA"));
        model.addAttribute("claims", claims);
        model.addAttribute("view", "sales/newSale");

        return "layout";
    }

    @PostMapping
    public String registerSale(@ModelAttribute("saleForm") SaleForm saleForm, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("channels", Sale.SaleChannel.values());
            model.addAttribute("products", productService.getAllProducts());
            return "sales/newSale";
        }

        // Procesar el formulario: Filtrar los ítems con cantidad > 0
        List<SaleItemForm> itemsToProcess = saleForm.getSaleItems().stream()
                .filter(item -> item.getQuantity() != null && item.getQuantity() > 0)
                .collect(Collectors.toList());

        // Crear el objeto Sale (puedes ajustar según tu entidad)
        Sale sale = new Sale();
        sale.setChannel(Sale.SaleChannel.valueOf(saleForm.getChannel()));
        sale.setSaleDate(LocalDateTime.now());

        // Asumiendo que tienes un método en saleService que construye la venta a partir del DTO
        saleService.processSale(sale, itemsToProcess);

        return "redirect:/sales"; // O redirigir a una vista de confirmación o listado de ventas
    }*/
}


