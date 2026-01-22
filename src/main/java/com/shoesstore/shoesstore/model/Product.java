package com.shoesstore.shoesstore.model;

import com.shoesstore.shoesstore.model.enums.Gender;
import com.shoesstore.shoesstore.model.enums.ShoeSize;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "products")
//@ToString(exclude = {"suppliers", "supplierProducts"})
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(nullable = false)
    private Long name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    private String description;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String material;

    @Column(nullable = false)
    private String brand;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShoeSize size;

    @Min(value = 0, message = "El precio no puede ser negativo")
    @Column(nullable = false)
    private Double price = 0.0;

    @Min(value = 0, message = "El stock no puede ser negativo")
    @Column(nullable = false)
    private Integer stock = 0;

    private LocalDateTime createdAt;

    @ManyToMany
    @JoinTable(
            name = "supplier_product",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "supplier_id")
    )
    private Set<Supplier> suppliers = new HashSet<>();

    @OneToMany(mappedBy = "product")
    private Set<SupplierProduct> supplierProducts = new HashSet<>();

    public Product() {
        this.createdAt = LocalDateTime.now();
    }

    public Product(Long id, Long name, Gender gender, String description,
                   String color, String type,
                   String material,
                   String brand,
                   ShoeSize size,
                   Double price,
                   Integer stock,
                   Set<Supplier> suppliers, Set<SupplierProduct> supplierProducts) {
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
        this.suppliers = suppliers;
        this.supplierProducts = supplierProducts;
        this.createdAt = LocalDateTime.now();
    }


    public String getFormattedPrice() {
        return String.format("$%.2f", this.price);
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Set<Supplier> getSuppliers() {
        return suppliers;
    }

    public void setSuppliers(Set<Supplier> suppliers) {
        this.suppliers = suppliers;
    }

    public Set<SupplierProduct> getSupplierProducts() {
        return supplierProducts;
    }

    public void setSupplierProducts(Set<SupplierProduct> supplierProducts) {
        this.supplierProducts = supplierProducts;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Product other = (Product) obj;
        return Objects.equals(id, other.id);
    }
}
