package com.shoesstore.shoesstore.controller;


import com.shoesstore.shoesstore.model.Product;
import com.shoesstore.shoesstore.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    // Inyección de dependencias
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String listarProductos(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("title", "Catálogo");
        model.addAttribute("view", "products/list");
        return "layout";
    }

    @GetMapping("/new")
    public String showProductForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("shoeSizes", Product.ShoeSize.values()); // Pasa los valores del enum
        return "products/form";
    }

    @PostMapping("/save")
    public String saveProduct(@Valid @ModelAttribute Product product,
                              BindingResult result,
                              Model model) {
        if(result.hasErrors()) {
            model.addAttribute("allSizes", Product.ShoeSize.values());
            return "products/form";
        }
        productService.saveProduct(product);
        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        model.addAttribute("allSizes", Product.ShoeSize.values()); // Si usas el enum de tallas
        return "products/form"; // Reutiliza el mismo formulario para crear/editar
    }

    @PostMapping("/update/{id}")
    public String updateProduct(@PathVariable Long id,
                                @Valid @ModelAttribute Product product,
                                BindingResult result) {
        if(result.hasErrors()) {
            return "products/form";
        }
        product.setId(id);
        productService.saveProduct(product);
        return "redirect:/products";
    }

    @DeleteMapping("/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/products";
    }

}