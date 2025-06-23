package com.shoesstore.shoesstore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.math.BigDecimal;
import java.util.*;

import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.GenerationType.IDENTITY;


@Entity
@Table(name = "proveedores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = "products")
public class Supplier {
    @Id @GeneratedValue(strategy = IDENTITY) @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Email @Column(nullable = false)
    private String email;
    @ManyToMany(cascade = { PERSIST, MERGE })
    @JoinTable(name = "supplier_product",
            joinColumns = @JoinColumn(name = "supplier_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id"))
    private List<Product> products = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "supplier_product_price", joinColumns = @JoinColumn(name = "supplier_id"))
    @MapKeyColumn(name = "product_id")
    @Column(name = "price", nullable = false)
    private Map<Long, BigDecimal> productPrices = new HashMap<>();

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SupplierProduct> supplierProducts = new HashSet<>();
}
