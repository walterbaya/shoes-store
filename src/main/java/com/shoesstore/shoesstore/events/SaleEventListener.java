package com.shoesstore.shoesstore.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class SaleEventListener {

    private static final Logger logger = LoggerFactory.getLogger(SaleEventListener.class);

    @EventListener
    public void handleSaleCreatedEvent(SaleCreatedEvent event) {
        logger.info("SaleCreatedEvent recibido para la venta con ID: {}", event.getSale().getId());
        // Aquí podrías añadir lógica para:
        // - Enviar un email de confirmación
        // - Actualizar un sistema de inventario externo
        // - Notificar a otros microservicios
        // - Actualizar estadísticas en tiempo real
        logger.info("Simulando envío de email de confirmación para la venta {}.", event.getSale().getId());
    }
}
