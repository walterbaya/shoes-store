package com.shoesstore.shoesstore.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
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
    @JoinColumn(name = "claim_id", referencedColumnName = "id")
    private Claim claim;

    private int quantity;
    private double subtotal;
}
