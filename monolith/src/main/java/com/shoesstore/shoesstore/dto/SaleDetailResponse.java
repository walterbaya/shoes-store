package com.shoesstore.shoesstore.dto;

import java.util.Objects;

public class SaleDetailResponse {
    
	private Long id;
    private ProductDto product;
    private int quantity;
    private double subtotal;
    
    public SaleDetailResponse() {
    	
    }

	public SaleDetailResponse(Long id, ProductDto product, int quantity, double subtotal) {
		super();
		this.id = id;
		this.product = product;
		this.quantity = quantity;
		this.subtotal = subtotal;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ProductDto getProduct() {
		return product;
	}

	public void setProduct(ProductDto product) {
		this.product = product;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public double getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(double subtotal) {
		this.subtotal = subtotal;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, product, quantity, subtotal);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SaleDetailResponse other = (SaleDetailResponse) obj;
		return Objects.equals(id, other.id) && Objects.equals(product, other.product) && quantity == other.quantity
				&& Double.doubleToLongBits(subtotal) == Double.doubleToLongBits(other.subtotal);
	}

	@Override
	public String toString() {
		return "SaleDetailResponse [id=" + id + ", product=" + product + ", quantity=" + quantity + ", subtotal="
				+ subtotal + "]";
	}
    
}