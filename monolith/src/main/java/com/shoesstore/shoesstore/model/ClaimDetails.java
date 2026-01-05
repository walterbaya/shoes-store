package com.shoesstore.shoesstore.model;

import jakarta.persistence.*;


@Entity
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

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public Claim getClaim() {
			return claim;
		}

		public void setClaim(Claim claim) {
			this.claim = claim;
		}

		public SaleDetails getSaleDetails() {
			return saleDetails;
		}

		public void setSaleDetails(SaleDetails saleDetails) {
			this.saleDetails = saleDetails;
		}

		public Integer getQuantity() {
			return quantity;
		}

		public void setQuantity(Integer quantity) {
			this.quantity = quantity;
		}
        
        
}

