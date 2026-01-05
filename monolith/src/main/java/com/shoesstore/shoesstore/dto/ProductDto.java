package com.shoesstore.shoesstore.dto;

import java.util.Objects;

public class ProductDto {
    private Long id;
    private String code;
    private String description;
    private String size;     // o ShoeSize si quieres el enum
    private Double price;
    private Integer stock;
    
    public ProductDto() {
    	
    }

	public ProductDto(Long id, String code, String description, String size, Double price, Integer stock) {
		super();
		this.id = id;
		this.code = code;
		this.description = description;
		this.size = size;
		this.price = price;
		this.stock = stock;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
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

	@Override
	public int hashCode() {
		return Objects.hash(code, description, id, price, size, stock);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProductDto other = (ProductDto) obj;
		return Objects.equals(code, other.code) && Objects.equals(description, other.description)
				&& Objects.equals(id, other.id) && Objects.equals(price, other.price)
				&& Objects.equals(size, other.size) && Objects.equals(stock, other.stock);
	}

}