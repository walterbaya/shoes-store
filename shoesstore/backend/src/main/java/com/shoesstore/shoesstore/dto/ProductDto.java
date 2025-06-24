package com.shoesstore.shoesstore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private Long id;
    private String name;
    private String description;
    private String size;     // o ShoeSize si quieres el enum
    private Double price;
    private Integer stock;
    // cualquier otro campo “plano” que necesites
}