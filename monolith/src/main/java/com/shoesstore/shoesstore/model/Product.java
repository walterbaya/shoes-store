package com.shoesstore.shoesstore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"suppliers", "supplierProducts"})
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
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

    public String getFormattedPrice() {
        return String.format("$%.2f", this.price);
    }
}
