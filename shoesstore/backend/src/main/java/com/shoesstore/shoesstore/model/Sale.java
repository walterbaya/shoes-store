package com.shoesstore.shoesstore.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private SaleChannel channel;

    private double total;

    private LocalDateTime saleDate = LocalDateTime.now();

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL)
    private List<SaleDetails> details;

    public enum SaleChannel {
        ONLINE,
        TIENDA;
    }
}
