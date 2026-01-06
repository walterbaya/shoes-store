package com.shoesstore.shoesstore.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Objects;


@Embeddable
@Table(name = "supplier_product_id")
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

	public Long getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Long supplierId) {
		this.supplierId = supplierId;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(productId, supplierId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SupplierProductId other = (SupplierProductId) obj;
		return Objects.equals(productId, other.productId) && Objects.equals(supplierId, other.supplierId);
	}
	
}
