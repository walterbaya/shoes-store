package com.shoesstore.shoesstore.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String fullName;

    @Enumerated(EnumType.STRING)
    private Role role;

    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(
            mappedBy = "user",
            cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH },
            orphanRemoval = false
    )
    private List<PurchaseOrder> ordenes = new ArrayList<>();

    public enum Role {
        ROLE_ADMIN, ROLE_SELLER, ROLE_STOCK_MANAGER
    }
}