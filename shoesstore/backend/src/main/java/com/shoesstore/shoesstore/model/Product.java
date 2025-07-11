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
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = "suppliers")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "El código del producto es obligatorio")
    @Column(nullable = false, unique = true) // Código único
    private String code;

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
        S43("43"), S44("44");

        private final String displayValue;

        ShoeSize(String displayValue) {
            this.displayValue = displayValue;
        }

        public String getDisplayValue() {
            return displayValue;
        }
    }

    @ManyToMany(mappedBy = "products", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private Set<Supplier> suppliers = new HashSet<>();

    @OneToMany(mappedBy = "product")
    private Set<SupplierProduct> supplierProducts = new HashSet<>();

    public String getFormattedPrice() {
        return String.format("$%.2f", this.price);
    }
}
