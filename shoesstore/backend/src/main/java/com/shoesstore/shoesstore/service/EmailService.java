package com.shoesstore.shoesstore.service;

import com.shoesstore.shoesstore.model.PurchaseOrder;

public interface EmailService {
    /**
     * Envía un resumen de la orden de compra. Implementaciones pueden imprimir en log, enviar email real, etc.
     */
    void sendOrderEmail(PurchaseOrder order);
}