package com.shoesstore.shoesstore.service;

import com.shoesstore.shoesstore.model.PurchaseOrder;
import com.shoesstore.shoesstore.model.PurchaseOrderItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Override
    public void sendOrderEmail(PurchaseOrder order) {
        StringBuilder sb = new StringBuilder();
        sb.append("Sending purchase order #").append(order.getId())
                .append(" to supplier ").append(order.getSupplier().getName())
                .append(" (").append(order.getSupplier().getEmail()).append("): ");
        for (PurchaseOrderItem item : order.getItems()) {
            sb.append(" - ")
                    .append(item.getProduct().getName())
                    .append(": qty=").append(item.getQuantity())
                    .append(", price=").append(item.getPurchasePrice())
                    .append(" ");
        }
        logger.info(sb.toString());
    }
}