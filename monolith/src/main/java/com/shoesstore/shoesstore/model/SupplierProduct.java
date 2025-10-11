package com.shoesstore.shoesstore.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


@Entity
@Getter
@Setter
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
}
