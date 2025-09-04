package com.shoesstore.shoesstore.dto;

import com.shoesstore.shoesstore.model.Product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OcrResultDTO{
    private Long   name;
    private String brand;
    private String material;
    private String color;
    private String type;
    private String description;
    private String size;
	
}