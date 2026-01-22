package com.shoesstore.shoesstore.dto;

import com.shoesstore.shoesstore.model.enums.PriorityCondition;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PurchaseOrderDto {

    private Long id;

    @NotNull(message = "El ID del proveedor es obligatorio")
    private Long supplierId;

    @NotNull(message = "La fecha de entrega es obligatoria")
    private LocalDate deliveryDate;

    @NotNull(message = "La condición de prioridad es obligatoria")
    private PriorityCondition priorityCondition;

    @NotBlank(message = "La dirección de entrega es obligatoria")
    private String deliveryAddress;

    @DecimalMin(value = "0.0", message = "El descuento no puede ser negativo")
    private BigDecimal discount = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "El costo de envío no puede ser negativo")
    private BigDecimal shippingCost = BigDecimal.ZERO;

    private String notes;

    @Valid // Para validar los ítems de la orden
    private List<PurchaseOrderItemDto> items = new ArrayList<>();

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public PriorityCondition getPriorityCondition() {
        return priorityCondition;
    }

    public void setPriorityCondition(PriorityCondition priorityCondition) {
        this.priorityCondition = priorityCondition;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<PurchaseOrderItemDto> getItems() {
        return items;
    }

    public void setItems(List<PurchaseOrderItemDto> items) {
        this.items = items;
    }
}
