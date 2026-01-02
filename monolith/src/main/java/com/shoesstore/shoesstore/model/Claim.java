package com.shoesstore.shoesstore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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

    // Nuevo campo para almacenar la URL del comprobante de despacho
    private String shippingProofUrl;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "El estado es obligatorio")
    @Column(nullable = false)
    private State state = State.INITIATED;  // Valor por defecto

    @NotNull(message = "Es obligatorio tener una fecha de inicio para el reclamo")
    private LocalDateTime createdAt = LocalDateTime.now();


    // Nuevos campos para registrar fechas de cada etapa
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

    // Métodos para manejar las transiciones de estado
    public void uploadProof(String proofUrl) {
        if (this.state != State.INITIATED) {
            throw new IllegalStateException("Solo se puede subir comprobante en estado INICIADO");
        }
        this.shippingProofUrl = proofUrl;
        this.state = State.PROOF_UPLOADED;
        this.proofUploadedDate = LocalDateTime.now();
    }

    public void approveRefund() {
        if (this.state != State.PROOF_UPLOADED) {
            throw new IllegalStateException("El comprobante debe estar subido y en espera de aprobación");
        }
        this.state = State.REFUND_PROCESSED;
        this.refundProcessedDate = LocalDateTime.now();
    }

    public void receivePackage() {
        if (this.state != State.REFUND_PROCESSED) {
            throw new IllegalStateException("Debe haberse procesado el reembolso antes de recibir el paquete");
        }
        this.state = State.PACKAGE_RECEIVED;
        this.packageReceivedDate = LocalDateTime.now();
    }

    // Getter para la URL completa
    public String getShippingProofUrl() {
        return "/uploads/" + shippingProofUrl;
    }


}