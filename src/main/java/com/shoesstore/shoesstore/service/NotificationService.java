package com.shoesstore.shoesstore.service;

import com.shoesstore.shoesstore.model.Sale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Async
    public void sendSaleConfirmationEmail(Sale sale) {
        logger.info("Iniciando envío de email para la venta ID: {}", sale.getId());
        try {
            // Simula una operación de red que toma tiempo
            Thread.sleep(3000); // 3 segundos
            logger.info("Email de confirmación enviado exitosamente para la venta ID: {} al usuario {}", sale.getId(), sale.getUser().getUsername());
        } catch (InterruptedException e) {
            logger.error("Error durante la simulación de envío de email", e);
            Thread.currentThread().interrupt();
        }
    }
}
