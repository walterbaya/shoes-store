package com.shoesstore.shoesstore.service;

import com.shoesstore.shoesstore.model.*;
import com.shoesstore.shoesstore.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@Transactional
public class PurchaseOrderService {

    private final PurchaseOrderRepository        orderRepo;
    private final ProductRepository              productRepo;
    private final SupplierRepository             supplierRepo;
    private final UserRepository                 userRepository;
    private final FileStorageService             fileStorageService;

    public PurchaseOrderService(PurchaseOrderRepository orderRepo,
                                ProductRepository productRepo,
                                SupplierRepository supplierRepo,
                                UserRepository userRepository,
                                FileStorageService fileStorageService) {
        this.orderRepo           = orderRepo;
        this.productRepo         = productRepo;
        this.supplierRepo        = supplierRepo;
        this.userRepository      = userRepository;
        this.fileStorageService  = fileStorageService;
    }

    public List<PurchaseOrder> findAll() {
        return orderRepo.findAll();
    }

    public PurchaseOrder findById(Long id) {
        return orderRepo.findById(id).orElse(null);
    }

    public void completeOrder(Long id) {
        PurchaseOrder po = findById(id);
        if (po == null || po.isCompleted()) return;

        po.setCompleted(true);
        po.getItems().forEach(item -> {
            Product p = item.getProduct();
            p.setStock(p.getStock() + item.getQuantity());
            productRepo.save(p);
        });
        orderRepo.save(po);
    }

    //products and quantities tiene el id y la cantidad de cada producto
    @Transactional
    public void createOrder(
            PurchaseOrder purchaseOrder,
            Long supplierId,
            Map<Long, Integer> productsAndQuantities,
            BigDecimal discountPct,
            BigDecimal shippingCost,
            MultipartFile file
    ) throws IOException {
        // Validaciones básicas
        if (discountPct.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El descuento no puede ser negativo");
        }
        if (shippingCost.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El costo de envío no puede ser negativo");
        }

        // Busco proveedor
        Supplier supplier = supplierRepo.findById(supplierId)
                .orElseThrow(() -> new IllegalArgumentException("Proveedor no existe"));

        // Completo campos bindeados desde el formulario
        purchaseOrder.setSupplier(supplier);
        purchaseOrder.setGeneratedDate(LocalDate.now());
        purchaseOrder.setCompleted(false);
        purchaseOrder.setDispatchDate(purchaseOrder.getDispatchDate());
        purchaseOrder.setPriorityCondition(purchaseOrder.getPriorityCondition());
        purchaseOrder.setDiscount(discountPct);
        purchaseOrder.setShippingCost(shippingCost);

        // Usuario actual
        String username = "username";
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        purchaseOrder.setUser(user);

        // Creo ítems filtrando cantidades > 0
        PurchaseOrder finalPurchaseOrder = purchaseOrder;
        List<PurchaseOrderItem> items = productsAndQuantities.entrySet().stream()
                .filter(e -> e.getValue() > 0)
                .map(e -> {

                    //de los supplier products obtengo

                    SupplierProduct sp = supplier.getSupplierProducts().stream()
                            .filter(sup -> sup.getProduct().getId().equals(e.getKey()))
                            .findFirst()
                            .orElseThrow(() -> new IllegalArgumentException("Producto no existe"));
                    Product p = sp.getProduct();
                    PurchaseOrderItem item = new PurchaseOrderItem();
                    item.setOrder(finalPurchaseOrder);
                    item.setProduct(p);
                    item.setQuantity(e.getValue());
                    //Este precio no se obtiene del producto directamente sino que es el precio al que lo vende el proveedor
                    item.setPurchasePrice(sp.getPrice());
                    item.setSubtotal(item.getPurchasePrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                    return item;
                })
                .collect(Collectors.toList());

        if (items.isEmpty()) {
            throw new IllegalArgumentException("La orden debe contener al menos un producto");
        }
        purchaseOrder.setItems(items);

        // Cálculos de resumen
        BigDecimal subtotal = items.stream()
                .map(i -> i.getPurchasePrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discountAmt = subtotal.multiply(discountPct)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal ivaAmt = subtotal.subtract(discountAmt)
                .multiply(BigDecimal.valueOf(0.21))
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal total = subtotal
                .subtract(discountAmt)
                .add(ivaAmt)
                .add(shippingCost)
                .setScale(2, RoundingMode.HALF_UP);

        purchaseOrder.setIva(ivaAmt);
        purchaseOrder.setTotal(total);

        // 1. Guardar la orden PRIMERO (para generar ID)
        purchaseOrder.setAttachment(null); // Asegurar que no hay adjunto inicial
        purchaseOrder = orderRepo.save(purchaseOrder); // ¡Persiste y genera ID!

        // 2. Procesar archivo DESPUÉS de tener ID
        if (file != null && !file.isEmpty()) {
            String relativePath = fileStorageService.storeFileForOrder(purchaseOrder.getId(), file);
            PurchaseOrderAttachment att = new PurchaseOrderAttachment(
                    file.getOriginalFilename(),
                    file.getContentType(),
                    relativePath,
                    file.getSize(),
                    purchaseOrder
            );
            purchaseOrder.setAttachment(att);
            orderRepo.save(purchaseOrder); // Actualiza con adjunto
        }
    }

    @Transactional
    public void updateOrder(
            Long orderId, // Nuevo parámetro para identificar la orden existente
            PurchaseOrder purchaseOrderData, // Datos actualizados de la orden
            Long supplierId,
            Map<Long, Integer> productsAndQuantities,
            BigDecimal discountPct,
            BigDecimal shippingCost,
            MultipartFile file
    ) throws IOException {
        // Validaciones básicas
        if (discountPct.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El descuento no puede ser negativo");
        }
        if (shippingCost.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El costo de envío no puede ser negativo");
        }

        // Buscar la orden existente
        PurchaseOrder existingOrder = orderRepo.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("La orden no existe"));

        // Buscar proveedor
        Supplier supplier = supplierRepo.findById(supplierId)
                .orElseThrow(() -> new IllegalArgumentException("Proveedor no existe"));

        // Actualizar campos de la orden existente
        existingOrder.setSupplier(supplier);
        existingOrder.setDispatchDate(purchaseOrderData.getDispatchDate());
        existingOrder.setPriorityCondition(purchaseOrderData.getPriorityCondition());
        existingOrder.setDiscount(discountPct);
        existingOrder.setShippingCost(shippingCost);

        // Eliminar ítems existentes
        existingOrder.getItems().clear();

        // Crear nuevos ítems
        List<PurchaseOrderItem> items = productsAndQuantities.entrySet().stream()
                .filter(e -> e.getValue() > 0)
                .map(e -> {
                    Product p = productRepo.findById(e.getKey())
                            .orElseThrow(() -> new IllegalArgumentException("Producto no existe"));
                    PurchaseOrderItem item = new PurchaseOrderItem();
                    item.setOrder(existingOrder);
                    item.setProduct(p);
                    item.setQuantity(e.getValue());
                    item.setPurchasePrice(BigDecimal.valueOf(p.getPrice()));
                    return item;
                })
                .collect(Collectors.toList());

        if (items.isEmpty()) {
            throw new IllegalArgumentException("La orden debe contener al menos un producto");
        }
        existingOrder.setItems(items);

        // Cálculos de resumen
        BigDecimal subtotal = items.stream()
                .map(i -> i.getPurchasePrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discountAmt = subtotal.multiply(discountPct)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal ivaAmt = subtotal.subtract(discountAmt)
                .multiply(BigDecimal.valueOf(0.21))
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal total = subtotal
                .subtract(discountAmt)
                .add(ivaAmt)
                .add(shippingCost)
                .setScale(2, RoundingMode.HALF_UP);

        existingOrder.setIva(ivaAmt);
        existingOrder.setTotal(total);

        // Procesar archivo adjunto
        if (file != null && !file.isEmpty()) {
            // Eliminar archivo anterior si existe
            if (existingOrder.getAttachment() != null) {
                fileStorageService.deleteFile(existingOrder.getAttachment().getStoragePath());
            }

            String relativePath = fileStorageService.storeFileForOrder(existingOrder.getId(), file);
            PurchaseOrderAttachment att = new PurchaseOrderAttachment(
                    file.getOriginalFilename(),
                    file.getContentType(),
                    relativePath,
                    file.getSize(),
                    existingOrder
            );
            existingOrder.setAttachment(att);
        }

        // Guardar la orden actualizada
        orderRepo.save(existingOrder);
    }


    @Transactional
    public void deleteOrder(Long id) throws Exception {
        // 1. Buscar la orden
        PurchaseOrder order = orderRepo.findById(id).orElseThrow(() -> new Exception("Orden no encontrada"));

        // 2. Eliminar archivo adjunto si existe
        if (order.getAttachment() != null) {
            fileStorageService.deleteFile(order.getAttachment().getStoragePath());
        }

        // 3. Eliminar la orden
        orderRepo.delete(order);
    }

}
