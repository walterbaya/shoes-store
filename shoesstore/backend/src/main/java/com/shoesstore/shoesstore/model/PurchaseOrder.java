package com.shoesstore.shoesstore.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "purchase_orders")
public class PurchaseOrder {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate generatedDate;
    private LocalDate dispatchDate;
    private boolean completed;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PurchaseOrderItem> items = new ArrayList<>();

    @ManyToOne(optional = true)
    @JoinColumn(
            name = "users_id",
            nullable = true,
            foreignKey = @ForeignKey(
                    name = "fk_orden_usuario",
                    foreignKeyDefinition =
                            "FOREIGN KEY (users_id) REFERENCES users(id) ON DELETE SET NULL"
            )
    )
    private User user;


    public BigDecimal getTotalOrder() {
        return items.stream()
                .map(PurchaseOrderItem::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}