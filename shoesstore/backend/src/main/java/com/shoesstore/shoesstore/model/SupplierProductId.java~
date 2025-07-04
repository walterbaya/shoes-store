package com.shoesstore.shoesstore.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
@Embeddable
@Table(name = "proveedor_producto")
public class SupplierProductId implements Serializable {
    @Column(name="supplier_id")
    private Long supplierId;

    @Column(name="product_id")
    private Long productId;

    // Constructor sin argumentos (requerido)
    public SupplierProductId() {}

    // Constructor útil para manejar la clave
    public SupplierProductId(Long supplierId, Long productId) {
        this.supplierId = supplierId;
        this.productId = productId;
    }
}
