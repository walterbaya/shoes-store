package com.shoesstore.shoesstore.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "purchase_order_attachment")
@Getter @Setter @NoArgsConstructor
public class PurchaseOrderAttachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private String contentType;
    private String storagePath;
    private long size;

    /**
     * Esta es la FK hacia PurchaseOrder; cada attachment
     * pertenece a UNA orden, y es único (1‑1).
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "order_id",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(name = "fk_attachment_order")
    )
    private PurchaseOrder order;

    public PurchaseOrderAttachment(String fileName,
                                   String contentType,
                                   String storagePath,
                                   long size,
                                   PurchaseOrder order) {
        this.fileName    = fileName;
        this.contentType = contentType;
        this.storagePath = storagePath;
        this.size        = size;
        this.order       = order;
    }
}
