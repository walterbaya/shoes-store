package com.shoesstore.shoesstore.dto;

import com.shoesstore.shoesstore.model.Product;

public record OcrResultDTO(
        String rawBlock,
        String name,
        String brand,
        String material,
        String color,
        String type,
        String description,
        String size,
        Integer stock,
        Product savedEntity
) {}