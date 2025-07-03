package com.shoesstore.shoesstore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate generatedDate;
    private LocalDate dispatchDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority_condition", nullable = false)
    private PriorityCondition priorityCondition;

    @Column(precision = 10, scale = 2)
    @Min(0)
    private BigDecimal iva;

    @Column(precision = 10, scale = 2)
    @Min(0)
    private BigDecimal total;

    private String additionalInformation;

    @OneToOne(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private PurchaseOrderAttachment attachment;

    @Column(nullable = false)
    private boolean completed;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PurchaseOrderItem> items = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Validaciones adicionales
    @Column(precision = 10, scale = 2)
    @Min(0) @Max(100)
    private BigDecimal discount;

    @Column(precision = 10, scale = 2)
    @Min(0)
    private BigDecimal shippingCost;

    public BigDecimal getTotalOrder() {
        return items.stream()
                .map(PurchaseOrderItem::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void setAttachment(PurchaseOrderAttachment att) {
        this.attachment = att;
        if (att != null) {
            att.setOrder(this);
        }
    }
}