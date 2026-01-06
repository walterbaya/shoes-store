package com.shoesstore.shoesstore.repository;

import com.shoesstore.shoesstore.model.PurchaseOrderAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseOrderAttachmentRepository
        extends JpaRepository<PurchaseOrderAttachment, Long> {}

