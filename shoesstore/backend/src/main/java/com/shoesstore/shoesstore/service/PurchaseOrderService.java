package com.shoesstore.shoesstore.service;

import com.shoesstore.shoesstore.model.Product;
import com.shoesstore.shoesstore.model.PurchaseOrder;
import com.shoesstore.shoesstore.model.PurchaseOrderItem;
import com.shoesstore.shoesstore.model.Supplier;
import com.shoesstore.shoesstore.repository.ProductRepository;
import com.shoesstore.shoesstore.repository.PurchaseOrderRepository;
import com.shoesstore.shoesstore.repository.SupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PurchaseOrderService {

    private final PurchaseOrderRepository orderRepo;
    private final ProductRepository productRepo;
    private final SupplierRepository supplierRepo;

    public PurchaseOrderService(PurchaseOrderRepository orderRepo,
                                ProductRepository productRepo,
                                SupplierRepository supplierRepo) {
        this.orderRepo = orderRepo;
        this.productRepo = productRepo;
        this.supplierRepo = supplierRepo;
    }

    public List<PurchaseOrder> findAll() {
        return orderRepo.findAll();
    }

    public PurchaseOrder findById(Long id) {
        return orderRepo.findById(id).orElse(null);
    }

    @Transactional
    public void completeOrder(Long id) {
        PurchaseOrder po = findById(id);
        if (po == null || po.isCompleted()) return;

        po.setCompleted(true);
        for (PurchaseOrderItem item : po.getItems()) {
            Product p = item.getProduct();
            p.setStock(p.getStock() + item.getQuantity());
            productRepo.save(p);
        }
        orderRepo.save(po);
    }

    @Transactional
    public void createOrder(Long supplierId, Map<Long, Integer> productsAndQuantities) {
        Supplier supplier = supplierRepo.findById(supplierId).orElseThrow();

        PurchaseOrder po = new PurchaseOrder();
        po.setSupplier(supplier);
        po.setGeneratedDate(LocalDate.now());
        po.setCompleted(false);

        List<PurchaseOrderItem> items = productsAndQuantities.entrySet().stream()
                .map(e -> {
                    Product p = productRepo.findById(e.getKey()).orElseThrow();
                    PurchaseOrderItem item = new PurchaseOrderItem();
                    item.setOrder(po);
                    item.setProduct(p);
                    item.setQuantity(e.getValue());
                    item.setPurchasePrice(BigDecimal.valueOf(p.getPrice()));
                    return item;
                }).collect(Collectors.toList());

        po.setItems(items);
        orderRepo.save(po);
    }
}
