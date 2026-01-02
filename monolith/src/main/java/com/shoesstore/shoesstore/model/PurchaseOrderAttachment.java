package com.shoesstore.shoesstore.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "purchase_order_attachment")
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getStoragePath() {
		return storagePath;
	}

	public void setStoragePath(String storagePath) {
		this.storagePath = storagePath;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public PurchaseOrder getOrder() {
		return order;
	}

	public void setOrder(PurchaseOrder order) {
		this.order = order;
	}

	public PurchaseOrderAttachment() {
		
	}
    
    
    
}
