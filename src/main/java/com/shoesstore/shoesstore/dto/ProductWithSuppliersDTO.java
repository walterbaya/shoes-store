package com.shoesstore.shoesstore.dto;

import com.shoesstore.shoesstore.model.Product;
import com.shoesstore.shoesstore.model.Supplier;
import com.shoesstore.shoesstore.model.enums.Gender;
import com.shoesstore.shoesstore.model.enums.ShoeSize;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class ProductWithSuppliersDTO {
    private Long id;

    @NotNull(message = "El código (name) es obligatorio")
    private Long name; // Revertido a Long

    private String description;

    @NotNull(message = "El color es obligatorio")
    private String color;

    @NotNull(message = "El tipo de artículo es obligatorio")
    private String type;

    @NotNull(message = "El material es obligatorio")
    private String material;

    @NotNull(message = "La marca es obligatoria")
    private String brand;

    @NotNull(message = "La talla es obligatoria")
    private ShoeSize size;

    @NotNull(message = "El precio es obligatorio")
    @Min(value = 0, message = "El precio no puede ser negativo")
    private Double price;

    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    private Set<String> supplierNames = new HashSet<>();
    private LocalDateTime createdAt;

    @NotNull(message = "El género es obligatorio")
    private Gender gender;

    
    public ProductWithSuppliersDTO() {
    	
    }
    
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
        if (product.getSuppliers() != null) {
            for (Supplier supplier : product.getSuppliers()) {
                this.supplierNames.add(supplier.getName());
            }
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
        return gender != null ? gender.getDisplayValue() : "";
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

	public Set<String> getSupplierNames() {
		return supplierNames;
	}

	public void setSupplierNames(Set<String> supplierNames) {
		this.supplierNames = supplierNames;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }
}
