package com.shoesstore.shoesstore.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "sale_details")
public class SaleDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sale_id")
    private Sale sale;

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "claim_id", referencedColumnName = "id", nullable = true)
    private Claim claim;

    @OneToMany(mappedBy = "saleDetails", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClaimDetails> details;

    private int quantity;
    private double subtotal;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Sale getSale() {
		return sale;
	}
	public void setSale(Sale sale) {
		this.sale = sale;
	}
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	public Claim getClaim() {
		return claim;
	}
	public void setClaim(Claim claim) {
		this.claim = claim;
	}
	public List<ClaimDetails> getDetails() {
		return details;
	}
	public void setDetails(List<ClaimDetails> details) {
		this.details = details;
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
    
}




