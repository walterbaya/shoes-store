package com.shoesstore.shoesstore.controller;

import com.shoesstore.shoesstore.dto.ProductDto;
import com.shoesstore.shoesstore.dto.ProductWithSuppliersDTO;
import com.shoesstore.shoesstore.model.Product;
import com.shoesstore.shoesstore.model.enums.Gender;
import com.shoesstore.shoesstore.model.enums.ShoeSize;
import com.shoesstore.shoesstore.service.ProductService;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import static com.shoesstore.shoesstore.mapper.ProductMapper.convertToDto;
import static com.shoesstore.shoesstore.mapper.ProductMapper.convertToEntity;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String listProducts(Model model) {
        List<ProductWithSuppliersDTO> products = productService.getAllProductsWithSuppliers();
        model.addAttribute("products", products);
        model.addAttribute("title", "Productos");
        model.addAttribute("view", "products/list");
        return "layout";
    }

    @GetMapping("/new")
    public String showProductForm(Model model) {
        model.addAttribute("allGenders", Gender.values());
        model.addAttribute("product", new ProductDto()); // Usamos DTO
        model.addAttribute("shoeSizes", ShoeSize.values());
        model.addAttribute("view", "products/form");
        return "layout";
    }

    @PostMapping("/save")
    public String saveProduct(
            @Valid @ModelAttribute("product") ProductDto productDto, // Recibimos DTO
            BindingResult result,
            Model model) {

        model.addAttribute("allSizes", ShoeSize.values());
        model.addAttribute("view", "products/form");

        if (result.hasErrors()) {
            model.addAttribute("allGenders", Gender.values()); // Re-enviar géneros si hay error
            return "layout";
        }

        Product product = convertToEntity(productDto);
        productService.saveProduct(product);

        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        ProductDto productDto = convertToDto(product); // Convertimos a DTO para la vista

        model.addAttribute("allGenders", Gender.values());
        model.addAttribute("product", productDto);
        model.addAttribute("allSizes", ShoeSize.values());
        model.addAttribute("view", "products/edit");
        return "layout";
    }

    @PutMapping("/update/{id}")
    public String updateProduct(
            @PathVariable Long id,
            @Valid @ModelAttribute("product") ProductDto productDto, // Recibimos DTO
            BindingResult result,
            Model model) {

        model.addAttribute("allSizes", ShoeSize.values());
        model.addAttribute("view", "products/edit");

        if (result.hasErrors()) {
            model.addAttribute("allGenders", Gender.values());
            return "layout";
        }

        Product product = convertToEntity(productDto);
        product.setId(id);
        productService.updateProduct(product);

        return "redirect:/products";
    }

    @DeleteMapping("/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        productService.deleteProduct(id);
        return "redirect:/products";
    }

}
