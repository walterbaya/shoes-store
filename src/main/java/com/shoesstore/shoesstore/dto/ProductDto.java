package com.shoesstore.shoesstore.dto;

import com.shoesstore.shoesstore.model.enums.Gender;
import com.shoesstore.shoesstore.model.enums.ShoeSize;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public class ProductDto {
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private Long name;

    @NotNull(message = "El género es obligatorio")
    private Gender gender;

    private String description;

    @NotBlank(message = "El color es obligatorio")
    private String color;

    @NotBlank(message = "El tipo de artículo es obligatorio")
    private String type;

    @NotBlank(message = "El material es obligatorio")
    private String material;

    @NotBlank(message = "La marca es obligatoria")
    private String brand;

    @NotNull(message = "La talla es obligatoria")
    private ShoeSize size;

    @NotNull(message = "El precio es obligatorio")
    @Min(value = 0, message = "El precio no puede ser negativo")
    private Double price;

    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock = 0;


    public ProductDto() {
    }

    public ProductDto(Long id, Long name, Gender gender, String description, String color, String type, String material, String brand, ShoeSize size, Double price, Integer stock) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.description = description;
        this.color = color;
        this.type = type;
        this.material = material;
        this.brand = brand;
        this.size = size;
        this.price = price;
        this.stock = stock;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getName() {
        return name;
    }

    public void setName(Long name) {
        this.name = name;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public ShoeSize getSize() {
        return size;
    }

    public void setSize(ShoeSize size) {
        this.size = size;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}
