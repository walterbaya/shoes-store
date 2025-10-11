package com.shoesstore.shoesstore.dto;

import com.shoesstore.shoesstore.model.Product;
import com.shoesstore.shoesstore.model.Supplier;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductWithSuppliersDTO {
    private Long id;
    private Long name;
    private String description;
    private String color;
    private String type;
    private String material;
    private String brand;
    private Product.ShoeSize size;
    private Double price;
    private Integer stock;
    private Set<String> supplierNames = new HashSet<>();
    private LocalDateTime createdAt;
    private Product.Gender gender;

    // Constructor que acepta un Product
    public ProductWithSuppliersDTO(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.color = product.getColor();
        this.type = product.getType();
        this.material = product.getMaterial();
        this.brand = product.getBrand();
        this.size = product.getSize();
        this.price = product.getPrice();
        this.stock = product.getStock();
        this.createdAt = product.getCreatedAt();
        this.gender = product.getGender();


        // Extraer nombres de proveedores
        for (Supplier supplier : product.getSuppliers()) {
            this.supplierNames.add(supplier.getName());
        }
    }

    // Getters y setters
    public String getFormattedPrice() {
        return String.format("$%.2f", this.price);
    }

    public String getSupplierNamesAsString() {
        return String.join(", ", supplierNames);
    }

    public String getGenderDisplay() {
        return gender.getDisplayValue();
    }
}