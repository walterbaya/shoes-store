package com.shoesstore.shoesstore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull(message = "El nombre es obligatorio")
    private String name;

    private String description;  // Campo nuevo

    @Enumerated(EnumType.STRING)
    @NotNull(message = "La talla es obligatoria")  // Cambiado de @NotBlank a @NotNull
    private ShoeSize size;

    @Min(value = 0, message = "El precio no puede ser negativo")
    private double price;  // Campo nuevo

    @Min(value = 0, message = "El stock no puede ser negativo")
    private int stock;

    private LocalDateTime createdAt = LocalDateTime.now();

    // Enum actualizado para mostrar mejor las tallas
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

    public String getFormattedPrice() {
        return String.format("$%.2f", this.price);
    }
}