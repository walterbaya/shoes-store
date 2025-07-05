package com.shoesstore.shoesstore.controller;

import com.shoesstore.shoesstore.model.Supplier;
import com.shoesstore.shoesstore.model.SupplierProduct;
import com.shoesstore.shoesstore.repository.ProductRepository;
import com.shoesstore.shoesstore.service.CustomUserDetailsService;
import com.shoesstore.shoesstore.service.SupplierService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/suppliers")
public class SupplierController {
    private final SupplierService supplierService;
    private final ProductRepository productRepository;

    public SupplierController(SupplierService supplierService, ProductRepository productRepository, CustomUserDetailsService customUserDetailsService) {
        this.supplierService = supplierService;
        this.productRepository = productRepository;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("title", "Proveedores");
        model.addAttribute("suppliers", supplierService.findAll());
        model.addAttribute("view", "suppliers/view");
        return "layout";
    }

    @GetMapping("/new")
    public String newSupplier(Model model) {

        Map<Long, BigDecimal> productPrices = new HashMap<>();
        model.addAttribute("supplier", new Supplier());
        model.addAttribute("products", productRepository.findAll());
        model.addAttribute("productPrices", productPrices);
        model.addAttribute("view", "suppliers/new");
        return "layout";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Supplier supplier, @RequestParam("productIds") List<Long> productIds, @RequestParam("prices") List<BigDecimal> prices) {
        supplierService.save(supplier, productIds, prices);
        return "redirect:/suppliers";
    }

    @GetMapping("/update/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Supplier supplier = supplierService.findById(id);
        model.addAttribute("supplier", supplier);
        model.addAttribute("products", productRepository.findAll());

        // Extraemos precios por producto
        Map<Long, BigDecimal> priceMap = supplier.getSupplierProducts().stream()
                .collect(Collectors.toMap(
                        sp -> sp.getProduct().getId(),
                        SupplierProduct::getPrice
                ));
        model.addAttribute("productPrices", priceMap);

        model.addAttribute("view", "suppliers/new");
        return "layout";
    }


    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id,
                         @ModelAttribute Supplier supplier,
                         @RequestParam("productIds") List<Long> productIds,
                         @RequestParam("prices") List<BigDecimal> prices) {
        supplierService.update(id, supplier, productIds, prices);
        return "redirect:/suppliers";
    }

    @DeleteMapping("/delete/{id}")
    public String deleteSupplier(@PathVariable Long id) {
        supplierService.delete(id);
        return "redirect:/suppliers";
    }
}

