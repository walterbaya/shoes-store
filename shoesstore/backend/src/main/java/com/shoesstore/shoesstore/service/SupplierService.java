package com.shoesstore.shoesstore.service;

import com.shoesstore.shoesstore.model.Product;
import com.shoesstore.shoesstore.model.Supplier;
import com.shoesstore.shoesstore.model.SupplierProduct;
import com.shoesstore.shoesstore.model.SupplierProductId;
import com.shoesstore.shoesstore.repository.ProductRepository;
import com.shoesstore.shoesstore.repository.SupplierRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SupplierService {
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;

    public SupplierService(SupplierRepository supplierRepository, ProductRepository productRepository) {
        this.supplierRepository = supplierRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public Supplier save(Supplier supplier, List<Long> productIds, List<BigDecimal> prices) {
        supplier.getProducts().clear();
        supplier.getProductPrices().clear();
        for (int i = 0; i < productIds.size(); i++) {
            Long pid = productIds.get(i);
            BigDecimal pr = prices.get(i);
            productRepository.findById(pid).ifPresent(prod -> {
                supplier.getProducts().add(prod);
                supplier.getProductPrices().put(pid, pr);
            });
        }
        return supplierRepository.save(supplier);
    }

    @Transactional
    public Supplier saveWithPrices(Supplier supplier, List<Long> prodIds, Map<String,String> params){
        supplierRepository.save(supplier);
        for(Long pid : prodIds){
            String key = "price_" + pid;
            BigDecimal price = new BigDecimal(params.get(key));
            SupplierProduct sp = new SupplierProduct(new SupplierProductId(supplier.getId(), pid), supplier, productRepository.findById(pid).get(), price);
            supplier.getSupplierProducts().add(sp);
        }
        return supplierRepository.save(supplier);
    }


    @Transactional
    public void update(Long id, Supplier datos, List<Long> productIds, List<BigDecimal> prices) {
        Supplier existing = supplierRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Supplier not found"));
        existing.setName(datos.getName());
        existing.setEmail(datos.getEmail());
        existing.getProducts().clear();
        existing.getProductPrices().clear();
        for (int i = 0; i < productIds.size(); i++) {
            Long pid = productIds.get(i);
            BigDecimal pr = prices.get(i);
            productRepository.findById(pid).ifPresent(prod -> {
                existing.getProducts().add(prod);
                existing.getProductPrices().put(pid, pr);
            });
        }
        supplierRepository.save(existing);
    }

    public List<Supplier> findAll() { return supplierRepository.findAll(); }
    public Supplier findById(Long id) { return supplierRepository.findById(id).orElse(null); }
}


