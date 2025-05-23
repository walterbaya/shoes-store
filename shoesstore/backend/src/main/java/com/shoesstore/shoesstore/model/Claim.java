package com.shoesstore.shoesstore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "claim")
public class Claim {

    //Al barrarse una claim no se borran los detalles de venta.
    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinColumn(name = "sale_detail_id", referencedColumnName = "id")
    private List<SaleDetails> saleDetails;

    //En caso de que se borre los claims no vamos a borrar las ventas
    @OneToOne(optional = false, cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinColumn(name = "sale_id", referencedColumnName = "id")
    private Sale sale;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private String description;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "El estado es obligatorio")
    private State state;

    @NotNull(message = "Es obligatorio tener una fecha de inicio para el reclamo")
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum State {
        // Parte 1: devolución iniciada, esperando comprobante
        INITIATED("Devolución iniciada, a la espera de comprobante de despacho"),
        // Parte 2: comprobante subido, esperando aprobación manual
        PROOF_UPLOADED("Comprobante cargado, a la espera de aprobación del vendedor"),
        // Parte 3: comprobante aprobado y reembolso procesado
        REFUND_PROCESSED("Comprobante aprobado, reembolso realizado"),
        // Parte 4: paquete recibido y stock actualizado
        PACKAGE_RECEIVED("Paquete recibido, stock actualizado");

        private final String displayValue;

        State(String displayValue) {
            this.displayValue = displayValue;
        }

        public String getDisplayValue() {
            return displayValue;
        }
    }
}
