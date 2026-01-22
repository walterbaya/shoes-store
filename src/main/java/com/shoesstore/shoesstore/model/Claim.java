package com.shoesstore.shoesstore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "claim")
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "sale_id", nullable = false, unique = true)
    private Sale sale;

    @OneToMany(mappedBy = "claim", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClaimDetails> claimDetails;

    private String description;

    // Almacena solo el nombre del archivo
    private String shippingProofUrl;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "El estado es obligatorio")
    @Column(nullable = false)
    private State state = State.INITIATED;

    @NotNull(message = "Es obligatorio tener una fecha de inicio para el reclamo")
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime proofUploadedDate;
    private LocalDateTime refundProcessedDate;
    private LocalDateTime packageReceivedDate;

    public enum State {
        INITIATED("Devolución iniciada, a la espera de comprobante de despacho"),
        PROOF_UPLOADED("Comprobante cargado, a la espera de aprobación del vendedor"),
        REFUND_PROCESSED("Comprobante aprobado, reembolso realizado"),
        PACKAGE_RECEIVED("Paquete recibido, stock actualizado");

        private final String displayValue;
        State(String displayValue) { this.displayValue = displayValue; }
        public String getDisplayValue() { return displayValue; }
    }

    // Getters y Setters (POJO puro)

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Sale getSale() {
        return sale;
    }

    public void setSale(Sale sale) {
        this.sale = sale;
    }

    public List<ClaimDetails> getClaimDetails() {
        return claimDetails;
    }

    public void setClaimDetails(List<ClaimDetails> claimDetails) {
        this.claimDetails = claimDetails;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getShippingProofUrl() {
        return shippingProofUrl;
    }

    public void setShippingProofUrl(String shippingProofUrl) {
        this.shippingProofUrl = shippingProofUrl;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getProofUploadedDate() {
        return proofUploadedDate;
    }

    public void setProofUploadedDate(LocalDateTime proofUploadedDate) {
        this.proofUploadedDate = proofUploadedDate;
    }

    public LocalDateTime getRefundProcessedDate() {
        return refundProcessedDate;
    }

    public void setRefundProcessedDate(LocalDateTime refundProcessedDate) {
        this.refundProcessedDate = refundProcessedDate;
    }

    public LocalDateTime getPackageReceivedDate() {
        return packageReceivedDate;
    }

    public void setPackageReceivedDate(LocalDateTime packageReceivedDate) {
        this.packageReceivedDate = packageReceivedDate;
    }
}
