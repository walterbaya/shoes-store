package com.shoesstore.shoesstore.model;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "claim_details")
public class ClaimDetails {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(optional = false)
        @JoinColumn(name = "claim_id", nullable = false)
        private Claim claim;

        @ManyToOne(optional = false)
        @JoinColumn(name = "sale_details_id", nullable = false)
        private SaleDetails saleDetails;

        private Integer quantity;
}

