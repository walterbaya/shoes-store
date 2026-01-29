package com.shoesstore.shoesstore.events;

import com.shoesstore.shoesstore.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Componente que actúa como "Subscriber" en el Patrón Observador.
 * Escucha eventos de la aplicación y reacciona a ellos.
 */
@Component
public class SaleEventListener {

    private static final Logger logger = LoggerFactory.getLogger(SaleEventListener.class);
    private final NotificationService notificationService;

    public SaleEventListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Patrón Observador (Subscriber):
     * El método anotado con @EventListener se suscribe automáticamente a eventos del tipo SaleCreatedEvent.
     * Cuando SaleService publica el evento, Spring invoca este método.
     * La lógica de notificación se delega a otro servicio, manteniendo el principio de Responsabilidad Única.
     */
    @EventListener
    public void handleSaleCreatedEvent(SaleCreatedEvent event) {
        logger.info("Evento SaleCreatedEvent recibido para la venta ID: {}", event.getSale().getId());
        notificationService.sendSaleConfirmationEmail(event.getSale());
    }
}
