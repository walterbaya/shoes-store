package com.shoesstore.shoesstore.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "ventas")
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private SaleChannel channel;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;


    private double shippingCost;
    private double discountPercentage;

    private double total;

    private LocalDateTime saleDate = LocalDateTime.now();

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SaleDetails> details;

    @OneToOne(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    private Claim claim;

    public enum SaleChannel {
        ONLINE, TIENDA;
    }
}
