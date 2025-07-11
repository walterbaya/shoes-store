package com.shoesstore.shoesstore.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
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
}




