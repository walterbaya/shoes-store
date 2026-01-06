package com.shoesstore.shoesstore.model;

import jakarta.persistence.*;
import java.math.BigDecimal;


@Entity
@Table(name = "supplier_product")
public class SupplierProduct {
    @EmbeddedId
    private SupplierProductId id;

    @MapsId("supplierId")
    @ManyToOne
    @JoinColumn(name="supplier_id")
    private Supplier supplier;

    @MapsId("productId")
    @ManyToOne
    @JoinColumn(name="product_id")
    private Product product;

    @Column(nullable = false)
    private BigDecimal price;

    // Constructor sin args necesario
    public SupplierProduct() {}

    public SupplierProduct(Supplier supplier, Product product, BigDecimal price) {
        this.id = new SupplierProductId(supplier.getId(), product.getId());
        this.supplier = supplier;
        this.product = product;
        this.price = price;
    }

	public SupplierProductId getId() {
		return id;
	}

	public void setId(SupplierProductId id) {
		this.id = id;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}
    
}
