package com.shoesstore.shoesstore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "products")
//@ToString(exclude = {"suppliers", "supplierProducts"})
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(nullable = false)
    private Long name;

    public enum Gender {
        HOMBRE("Hombre"),
        MUJER("Mujer"),
        UNISEX("Unisex"),
        NIÑO("Niño"),
        NIÑA("Niña");

        private final String displayValue;

        Gender(String displayValue) {
            this.displayValue = displayValue;
        }

        public String getDisplayValue() {
            return displayValue;
        }
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "El género es obligatorio")
    private Gender gender = Gender.UNISEX;

    private String description;

    @NotBlank(message = "El color es obligatorio")
    @Column(nullable = false)
    private String color;

    @NotBlank(message = "El tipo de artículo es obligatorio")
    @Column(nullable = false)
    private String type;

    @NotBlank(message = "El material es obligatorio")
    @Column(nullable = false)
    private String material;

    @NotBlank(message = "La marca es obligatoria")
    @Column(nullable = false)
    private String brand;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "La talla es obligatoria")
    @Column(nullable = false)
    private ShoeSize size;

    @Min(value = 0, message = "El precio no puede ser negativo")
    @Column(nullable = false)
    private Double price = 0.0;

    @Min(value = 0, message = "El stock no puede ser negativo")
    @Column(nullable = false)
    private Integer stock = 0;

    private LocalDateTime createdAt = LocalDateTime.now();

    public enum ShoeSize {
        S35("35"), S36("36"), S37("37"), S38("38"),
        S39("39"), S40("40"), S41("41"), S42("42"),
        S43("43"), S44("44"), S45("45"), S46("46");

        private final String displayValue;

        ShoeSize(String displayValue) {
            this.displayValue = displayValue;
        }

        public String getDisplayValue() {
            return displayValue;
        }
    }

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
    	
    }

    public Product(Long id, Long name, @NotNull(message = "El género es obligatorio") Gender gender, String description,
			@NotBlank(message = "El color es obligatorio") String color,
			@NotBlank(message = "El tipo de artículo es obligatorio") String type,
			@NotBlank(message = "El material es obligatorio") String material,
			@NotBlank(message = "La marca es obligatoria") String brand,
			@NotNull(message = "La talla es obligatoria") ShoeSize size,
			@Min(value = 0, message = "El precio no puede ser negativo") Double price,
			@Min(value = 0, message = "El stock no puede ser negativo") Integer stock, LocalDateTime createdAt,
			Set<Supplier> suppliers, Set<SupplierProduct> supplierProducts) {
		super();
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
		this.createdAt = createdAt;
		this.suppliers = suppliers;
		this.supplierProducts = supplierProducts;
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
