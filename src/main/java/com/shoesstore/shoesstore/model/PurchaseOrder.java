package com.shoesstore.shoesstore.model;

import com.shoesstore.shoesstore.model.enums.PriorityCondition;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "purchase_order")
public class PurchaseOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate generatedDate;
    private LocalDate dispatchDate; // Corresponde a deliveryDate del DTO

    @Enumerated(EnumType.STRING)
    @Column(name = "priority_condition", nullable = false)
    private PriorityCondition priorityCondition;

    @Column(precision = 10, scale = 2)
    @Min(0)
    private BigDecimal iva;

    @Column(precision = 10, scale = 2)
    @Min(0)
    private BigDecimal total;

    private String additionalInformation; // Este campo no se usa en el DTO, pero se mantiene

    @Column(length = 500) // Añadido campo para notas
    private String notes;

    @Column(nullable = false, length = 255) // Añadido campo para dirección de entrega
    private String deliveryAddress;

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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDate getGeneratedDate() {
		return generatedDate;
	}

	public void setGeneratedDate(LocalDate generatedDate) {
		this.generatedDate = generatedDate;
	}

	public LocalDate getDispatchDate() {
		return dispatchDate;
	}

	public void setDispatchDate(LocalDate dispatchDate) {
		this.dispatchDate = dispatchDate;
	}

	public PriorityCondition getPriorityCondition() {
		return priorityCondition;
	}

	public void setPriorityCondition(PriorityCondition priorityCondition) {
		this.priorityCondition = priorityCondition;
	}

	public BigDecimal getIva() {
		return iva;
	}

	public void setIva(BigDecimal iva) {
		this.iva = iva;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public String getAdditionalInformation() {
		return additionalInformation;
	}

	public void setAdditionalInformation(String additionalInformation) {
		this.additionalInformation = additionalInformation;
	}

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public List<PurchaseOrderItem> getItems() {
		return items;
	}

	public void setItems(List<PurchaseOrderItem> items) {
		this.items = items;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public BigDecimal getDiscount() {
		return discount;
	}

	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}

	public BigDecimal getShippingCost() {
		return shippingCost;
	}

	public void setShippingCost(BigDecimal shippingCost) {
		this.shippingCost = shippingCost;
	}

	public PurchaseOrderAttachment getAttachment() {
		return attachment;
	}

    // Constructor completo (actualizado con nuevos campos)
	public PurchaseOrder(Long id, LocalDate generatedDate, LocalDate dispatchDate, PriorityCondition priorityCondition,
			BigDecimal iva, BigDecimal total, String additionalInformation, String notes, String deliveryAddress,
			PurchaseOrderAttachment attachment, boolean completed, Supplier supplier, List<PurchaseOrderItem> items,
			User user, BigDecimal discount, BigDecimal shippingCost) {
		super();
		this.id = id;
		this.generatedDate = generatedDate;
		this.dispatchDate = dispatchDate;
		this.priorityCondition = priorityCondition;
		this.iva = iva;
		this.total = total;
		this.additionalInformation = additionalInformation;
		this.notes = notes;
		this.deliveryAddress = deliveryAddress;
		this.attachment = attachment;
		this.completed = completed;
		this.supplier = supplier;
		this.items = items;
		this.user = user;
		this.discount = discount;
		this.shippingCost = shippingCost;
	}

	public PurchaseOrder() {
	}
    
}
