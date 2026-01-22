package com.shoesstore.shoesstore.mapper;

import com.shoesstore.shoesstore.dto.ProductDto;
import com.shoesstore.shoesstore.model.Product;

public class ProductMapper {

    public static Product convertToEntity(ProductDto dto) {
        Product product = new Product();
        product.setId(dto.getId());
        product.setName(dto.getName());
        product.setGender(dto.getGender());
        product.setDescription(dto.getDescription());
        product.setColor(dto.getColor());
        product.setType(dto.getType());
        product.setMaterial(dto.getMaterial());
        product.setBrand(dto.getBrand());
        product.setSize(dto.getSize());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        return product;
    }

    public static ProductDto convertToDto(Product entity) {
        ProductDto dto = new ProductDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setGender(entity.getGender());
        dto.setDescription(entity.getDescription());
        dto.setColor(entity.getColor());
        dto.setType(entity.getType());
        dto.setMaterial(entity.getMaterial());
        dto.setBrand(entity.getBrand());
        dto.setSize(entity.getSize());
        dto.setPrice(entity.getPrice());
        dto.setStock(entity.getStock());
        return dto;
    }
}
