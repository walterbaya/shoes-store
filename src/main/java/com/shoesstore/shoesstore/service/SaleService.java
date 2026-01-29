package com.shoesstore.shoesstore.service;

import com.shoesstore.shoesstore.events.SaleCreatedEvent;
import com.shoesstore.shoesstore.model.Product;
import com.shoesstore.shoesstore.model.Sale;
import com.shoesstore.shoesstore.model.SaleDetails;
import com.shoesstore.shoesstore.model.User;
import com.shoesstore.shoesstore.model.enums.SaleChannel;
import com.shoesstore.shoesstore.repository.SaleDetailsRepository;
import com.shoesstore.shoesstore.repository.SaleRepository;
import com.shoesstore.shoesstore.dto.SaleItemForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servicio para la lógica de negocio de las ventas.
 * Implementa el rol de "Publisher" en el Patrón Observador.
 */
@Service
public class SaleService {

    private static final Logger logger = LoggerFactory.getLogger(SaleService.class);

    private final SaleRepository saleRepository;
    private final ProductService productService;
    private final SaleDetailsRepository saleDetailsRepository;
    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;

    public SaleService(SaleRepository saleRepository, ProductService productService,
                       SaleDetailsRepository saleDetailsRepository, UserService userService,
                       ApplicationEventPublisher eventPublisher) {
        this.saleRepository = saleRepository;
        this.productService = productService;
        this.saleDetailsRepository = saleDetailsRepository;
        this.userService = userService;
        this.eventPublisher = eventPublisher;
    }


    @Transactional
    public Sale processSale(Sale sale, List<SaleItemForm> itemsToProcess) {
        logger.info("Iniciando procesamiento de venta.");

        double totalSale = 0;
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.debug("Usuario autenticado para la venta: {}", username);

        List<SaleDetails> saleDetailsList = new ArrayList<>();

        Optional<User> user = userService.getUserByUsername(username);
        if (user.isEmpty()) {
            logger.error("Usuario {} no encontrado al procesar venta.", username);
            throw new RuntimeException("Usuario no encontrado");
        }
        sale.setUser(user.get());

        for (SaleItemForm item : itemsToProcess) {
            productService.updateStock(item.getProductId(), item.getQuantity());
            Product product = productService.getProductById(item.getProductId());

            SaleDetails saleDetails = new SaleDetails();
            saleDetails.setProduct(product);
            saleDetails.setQuantity(item.getQuantity());
            saleDetails.setSubtotal(item.getQuantity() * product.getPrice());

            saleDetailsList.add(saleDetails);

            totalSale += saleDetails.getSubtotal();
            logger.debug("Producto {} añadido a la venta con cantidad {}", product.getName(), item.getQuantity());
        }

        sale.setTotal(totalSale - totalSale * (sale.getDiscountPercentage() / 100.0));

        Sale res = saleRepository.save(sale);
        logger.info("Venta con ID {} guardada exitosamente.", res.getId());

        saleDetailsList.forEach(saleDetails -> {
            saleDetails.setSale(res);
            saleDetailsRepository.save(saleDetails);
            logger.debug("Detalle de venta para producto {} guardado.", saleDetails.getProduct().getName());
        });

        /**
         * Patrón Observador (Publisher):
         * Después de completar la lógica principal (guardar la venta), se publica un evento.
         * Esto desacopla la lógica de venta de las acciones secundarias (como enviar notificaciones).
         * Otros componentes pueden "suscribirse" a este evento sin que este servicio los conozca.
         */
        eventPublisher.publishEvent(new SaleCreatedEvent(this, res));
        logger.info("Evento SaleCreatedEvent publicado para la venta con ID {}.", res.getId());

        return res;
    }

    public List<Sale> getSalesByDate(LocalDateTime startDate, LocalDateTime endDate) {
        logger.info("Buscando ventas entre {} y {}.", startDate, endDate);
        LocalDateTime start = (startDate != null) ? startDate : LocalDateTime.now().minusMonths(1);
        LocalDateTime end = (endDate != null) ? endDate : LocalDateTime.now();
        return saleRepository.findBySaleDateBetween(start, end);
    }

    public Sale getSaleById(Long id) {
        logger.info("Buscando venta con ID {}.", id);
        return saleRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Venta con ID {} no encontrada.", id);
                    return new RuntimeException("Venta no encontrada con ID: " + id);
                });
    }

    @Transactional
    @PreAuthorize("hasAuthority('sale:delete')")
    public void deleteSale(Long id) {
        logger.info("Intentando eliminar venta con ID {}.", id);
        Sale sale = getSaleById(id);

        if (sale.getClaim() != null) {
            logger.warn("Intento de eliminar venta con ID {} que tiene un reclamo asociado.", id);
            throw new RuntimeException("No se puede eliminar una venta con un reclamo asociado, elimine primero el reclamo y posteriormente la venta.");
        } else {
            sale.getDetails().forEach(saleDetails -> {
                productService.updateStock(saleDetails.getProduct().getId(), saleDetails.getQuantity());
                saleDetailsRepository.delete(saleDetails);
                logger.debug("Stock de producto {} actualizado y detalle de venta eliminado.", saleDetails.getProduct().getName());
            });
            saleRepository.delete(sale);
            logger.info("Venta con ID {} eliminada correctamente.", id);
        }
    }

    public List<Object[]> getDailySalesData(LocalDateTime start, LocalDateTime end) {
        logger.debug("Obteniendo datos de ventas diarias entre {} y {}.", start, end);
        return saleRepository.findDailySalesByUser(start, end);
    }

    public List<Object[]> getTopSellers(LocalDateTime start, LocalDateTime end) {
        logger.debug("Obteniendo top vendedores entre {} y {}.", start, end);
        return saleRepository.findTopSellers(start, end);
    }

    public List<Sale> getAllSales() {
        logger.debug("Obteniendo todas las ventas.");
        return saleRepository.findAll();
    }

    public Page<Sale> getAllSales(Pageable pageable) {
        logger.debug("Obteniendo ventas paginadas. Página: {}, Tamaño: {}, Orden: {}", pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        return saleRepository.findAll(pageable);
    }

    public List<Sale> getSalesWithoutClaims() {
        logger.debug("Obteniendo ventas sin reclamos.");
        return saleRepository.findSalesWithTotalAndNoClaim();
    }

    public double calculateTotalRevenue(List<Sale> sales) {
        logger.debug("Calculando ingresos totales para {} ventas.", sales.size());
        return sales.stream()
                .mapToDouble(Sale::getTotalWithShippingCost)
                .sum();
    }

    public Map<SaleChannel, Long> countSalesByChannel(List<Sale> sales) {
        logger.debug("Contando ventas por canal para {} ventas.", sales.size());
        return sales.stream()
                .collect(Collectors.groupingBy(Sale::getChannel, Collectors.counting()));
    }

    /**
     * Método de soporte para la seguridad a nivel de método.
     * Permite verificar la propiedad de un objeto antes de autorizar una operación.
     */
    public boolean isOwner(Long saleId, String username) {
        return saleRepository.findById(saleId)
                .map(sale -> sale.getUser().getUsername().equals(username))
                .orElse(false);
    }
}
