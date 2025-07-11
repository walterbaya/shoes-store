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
import java.util.function.Function;
import java.util.stream.Collectors;

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
        // Persistimos primero el supplier para asegurar que tenga ID
        Supplier persisted = supplierRepository.save(supplier);

        // Limpiamos relaciones previas
        persisted.getSupplierProducts().clear();
        // Si usás price map, también clear ahí

        // Creamos y asociamos cada SupplierProduct con su precio
        for (int i = 0; i < productIds.size(); i++) {
            Long pid = productIds.get(i);
            BigDecimal pr = prices.get(i);

            productRepository.findById(pid).ifPresent(prod -> {
                SupplierProduct sp = new SupplierProduct();
                sp.setId(new SupplierProductId(persisted.getId(), pid));
                sp.setSupplier(persisted);
                sp.setProduct(prod);
                sp.setPrice(pr != null ? pr : BigDecimal.ZERO); // asegura no null
                persisted.getSupplierProducts().add(sp);
            });
        }

        // Guardamos las relaciones
        return supplierRepository.save(persisted);
    }

    @Transactional
    public void update(Long id, Supplier datos, List<Long> productIds, List<BigDecimal> prices) {
        Supplier existing = supplierRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Supplier not found"));
        existing.setName(datos.getName());
        existing.setEmail(datos.getEmail());

        // Mapa temporal para buscar asociaciones existentes por ID de producto
        Map<Long, SupplierProduct> existingProductsMap = existing.getSupplierProducts().stream()
                .collect(Collectors.toMap(sp -> sp.getProduct().getId(), Function.identity()));

        // Eliminar asociaciones que ya no están en la nueva lista
        existing.getSupplierProducts().removeIf(sp -> !productIds.contains(sp.getProduct().getId()));

        for (int i = 0; i < productIds.size(); i++) {
            Long pid = productIds.get(i);
            BigDecimal price = prices.get(i);
            productRepository.findById(pid).ifPresent(product -> {
                if (existingProductsMap.containsKey(pid)) {
                    // Actualizar precio si la asociación existe
                    existingProductsMap.get(pid).setPrice(price);
                } else {
                    // Crear nueva asociación solo si no existe
                    SupplierProduct newSp = new SupplierProduct(existing, product, price);
                    existing.getSupplierProducts().add(newSp);
                }
            });
        }
    }

    public List<Supplier> findAll() { return supplierRepository.findAll(); }
    public Supplier findById(Long id) { return supplierRepository.findById(id).orElse(null); }
}


