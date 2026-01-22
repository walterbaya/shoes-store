package com.shoesstore.shoesstore.service;

import com.shoesstore.shoesstore.dto.PurchaseOrderDto;
import com.shoesstore.shoesstore.dto.PurchaseOrderItemDto;
import com.shoesstore.shoesstore.mapper.PurchaseOrderMapper;
import com.shoesstore.shoesstore.model.*;
import com.shoesstore.shoesstore.repository.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PurchaseOrderService {

    private final PurchaseOrderRepository orderRepo;
    private final ProductRepository productRepo;
    private final SupplierRepository supplierRepo;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    public PurchaseOrderService(PurchaseOrderRepository orderRepo,
                                ProductRepository productRepo,
                                SupplierRepository supplierRepo,
                                UserRepository userRepository,
                                FileStorageService fileStorageService) {
        this.orderRepo = orderRepo;
        this.productRepo = productRepo;
        this.supplierRepo = supplierRepo;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
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

    @Transactional
    public void createOrder(PurchaseOrderDto dto, MultipartFile file) throws IOException {
        PurchaseOrder order = PurchaseOrderMapper.convertToEntity(dto);

        // Busco proveedor
        Supplier supplier = supplierRepo.findById(dto.getSupplierId())
                .orElseThrow(() -> new IllegalArgumentException("Proveedor no existe"));
        order.setSupplier(supplier);

        // Usuario actual
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        order.setUser(user);

        order.setGeneratedDate(LocalDate.now());
        order.setCompleted(false);

        // Creo ítems
        List<PurchaseOrderItem> items = dto.getItems().stream()
                .filter(itemDto -> itemDto.getQuantity() > 0)
                .map(itemDto -> {
                    Product product = productRepo.findById(itemDto.getProductId())
                            .orElseThrow(() -> new IllegalArgumentException("Producto no existe"));
                    return PurchaseOrderMapper.convertToEntityItem(itemDto, product);
                })
                .collect(Collectors.toList());

        if (items.isEmpty()) {
            throw new IllegalArgumentException("La orden debe contener al menos un producto");
        }
        order.setItems(items);

        // Cálculos de resumen
        calculateTotals(order);

        // Guardar la orden
        order = orderRepo.save(order);

        // Procesar archivo
        if (file != null && !file.isEmpty()) {
            String relativePath = fileStorageService.storeFileForOrder(order.getId(), file);
            PurchaseOrderAttachment att = new PurchaseOrderAttachment(
                    file.getOriginalFilename(),
                    file.getContentType(),
                    relativePath,
                    file.getSize(),
                    order
            );
            order.setAttachment(att);
            orderRepo.save(order);
        }
    }

    @Transactional
    public void updateOrder(Long orderId, PurchaseOrderDto dto, MultipartFile file) throws IOException {
        PurchaseOrder existingOrder = orderRepo.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("La orden no existe"));

        // Actualizar campos
        Supplier supplier = supplierRepo.findById(dto.getSupplierId())
                .orElseThrow(() -> new IllegalArgumentException("Proveedor no existe"));
        existingOrder.setSupplier(supplier);
        existingOrder.setDispatchDate(dto.getDeliveryDate()); // CORREGIDO: Usar setDispatchDate
        existingOrder.setPriorityCondition(dto.getPriorityCondition());
        existingOrder.setDeliveryAddress(dto.getDeliveryAddress());
        existingOrder.setDiscount(dto.getDiscount());
        existingOrder.setShippingCost(dto.getShippingCost());
        existingOrder.setNotes(dto.getNotes());

        // Actualizar ítems
        existingOrder.getItems().clear();
        List<PurchaseOrderItem> items = dto.getItems().stream()
                .filter(itemDto -> itemDto.getQuantity() > 0)
                .map(itemDto -> {
                    Product product = productRepo.findById(itemDto.getProductId())
                            .orElseThrow(() -> new IllegalArgumentException("Producto no existe"));
                    return PurchaseOrderMapper.convertToEntityItem(itemDto, product);
                })
                .collect(Collectors.toList());
        existingOrder.setItems(items);

        // Recalcular totales
        calculateTotals(existingOrder);

        // Procesar archivo
        if (file != null && !file.isEmpty()) {
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

        orderRepo.save(existingOrder);
    }

    @Transactional
    public void deleteOrder(Long id) throws Exception {
        PurchaseOrder order = orderRepo.findById(id).orElseThrow(() -> new Exception("Orden no encontrada"));

        if (order.getAttachment() != null) {
            fileStorageService.deleteFile(order.getAttachment().getStoragePath());
        }

        orderRepo.delete(order);
    }

    private void calculateTotals(PurchaseOrder order) {
        BigDecimal subtotal = order.getItems().stream()
                .map(i -> i.getPurchasePrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discountAmt = subtotal.multiply(order.getDiscount())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal ivaAmt = subtotal.subtract(discountAmt)
                .multiply(BigDecimal.valueOf(0.21))
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal total = subtotal
                .subtract(discountAmt)
                .add(ivaAmt)
                .add(order.getShippingCost())
                .setScale(2, RoundingMode.HALF_UP);

        order.setIva(ivaAmt);
        order.setTotal(total);
    }
}
