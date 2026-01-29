package com.shoesstore.shoesstore.service;

import com.shoesstore.shoesstore.dto.ProductWithSuppliersDTO;
import com.shoesstore.shoesstore.exception.ProductServiceException;
import com.shoesstore.shoesstore.model.*;
import com.shoesstore.shoesstore.repository.*;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Servicio que gestiona la lógica de negocio para los productos.
 * Implementa varios patrones de diseño clave:
 * 1. Inyección de Dependencias (DI): Las dependencias se inyectan a través del constructor.
 * 2. Patrón de Caché: Utiliza Spring Cache para optimizar lecturas frecuentes.
 */
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final PurchaseOrderItemsRepository purchaseOrderItemsRepository;
    private final SaleDetailsRepository saleDetailsRepository;

    /**
     * Patrón de Inyección de Dependencias (Constructor Injection).
     * Spring inyecta automáticamente los repositorios necesarios.
     * Esto facilita el testing unitario al permitir inyectar mocks.
     */
    public ProductService(ProductRepository productRepository,
                          PurchaseOrderItemsRepository purchaseOrderItemsRepository, ClaimRepository claimRepository,
                          SaleDetailsRepository saleDetailsRepository) {

        this.productRepository = productRepository;
        this.purchaseOrderItemsRepository = purchaseOrderItemsRepository;
        this.saleDetailsRepository = saleDetailsRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Transactional
    public void updateStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductServiceException("Producto no encontrado"));

        if (product.getStock() < quantity) {
            throw new ProductServiceException("Stock insuficiente");
        }

        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
    }

    /**
     * Implementación del Patrón de Caché (Cache-Aside).
     * @Cacheable: Antes de ejecutar el método, Spring verifica si el resultado ya está en la caché "products".
     * Si existe, lo devuelve inmediatamente (ahorrando una consulta a BD).
     * Si no, ejecuta el método y guarda el resultado en la caché.
     */
    @Transactional(readOnly = true)
    @Cacheable("products")
    public List<ProductWithSuppliersDTO> getAllProductsWithSuppliers() {
        List<Product> products = productRepository.findAllWithSuppliers();
        return products.stream()
                .map(ProductWithSuppliersDTO::new)
                .collect(Collectors.toList());
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductServiceException("Producto no encontrado con ID: " + id));
    }

    /**
     * @CacheEvict: Invalida la caché "products" cuando se modifica un producto.
     * Esto asegura la consistencia de datos (Cache Consistency), forzando a que la próxima lectura
     * vaya a la base de datos para obtener los datos frescos.
     */
    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public Product updateProduct(Product product) {
        Long id = product.getId();
        if (id == null || !productRepository.existsById(id)) {
            throw new ProductServiceException("No existe un producto con ID: " + id);
        }
        // Recupero el original y actualizo solo los campos editables
        Product existing = productRepository.findById(id).get();
        existing.setColor(product.getColor());
        existing.setType(product.getType());
        existing.setMaterial(product.getMaterial());
        existing.setBrand(product.getBrand());
        existing.setSize(product.getSize());
        existing.setDescription(product.getDescription());
        existing.setPrice(product.getPrice());
        existing.setStock(product.getStock());
        // (no tocamos createdAt ni suppliers)
        return productRepository.save(existing);
    }

    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public Product saveProduct(Product product) {
        if (product.getId() != null && productRepository.existsById(product.getId())) {
            throw new ProductServiceException("El producto con ID " + product.getId() + " ya existe");
        }
        if (product.getPrice() <= 0) {
            throw new ProductServiceException("El precio debe ser mayor a 0");
        }
        if (product.getStock() <= 0) {
            throw new ProductServiceException("El stock debe ser mayor a 0");
        }

        return productRepository.save(product);
    }

    private void validateDeleteProduct(List<SaleDetails> saleDetailsList, List<Claim> claims, List<PurchaseOrderItem> purchaseOrderItems) {
        if (!claims.isEmpty()) {
            String claimIds = claims.stream().map(c -> String.valueOf(c.getId())).collect(Collectors.joining(", "));
            throw new ProductServiceException("No se puede eliminar un producto que posee reclamos registrados, elimine primeramente las reclamos con los siguientes IDs: " + claimIds);
        }

        if (!purchaseOrderItems.isEmpty()) {
            String purchaseOrderItemIds = purchaseOrderItems.stream().map(p -> String.valueOf(p.getId())).collect(Collectors.joining(", "));
            throw new ProductServiceException("No se puede eliminar un producto que posee ordenes de compra registradas" + ", elimine primeramente las ordenes de compra con los siguientes IDs: " + purchaseOrderItemIds);
        }

        if (!saleDetailsList.isEmpty()) {
            String saleIds = saleDetailsList.stream().map(sd -> String.valueOf(sd.getSale().getId())).collect(Collectors.joining(", "));
            throw new ProductServiceException("No se puede eliminar un producto que posee ventas registradas, elimine primeramente las ventas con los siguientes IDs: " + saleIds);
        }
    }

    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public void deleteProduct(Long id) {

        Product product = productRepository.findById(id).orElseThrow(() -> new ProductServiceException("No existe un producto con ID: " + id));

        List<SaleDetails> saleDetailsList = saleDetailsRepository.findAllByProductId(id);
        List<Claim> claims = saleDetailsList.stream()
                .map(SaleDetails::getClaim)
                .filter(Objects::nonNull)
                .toList();

        List<PurchaseOrderItem> purchaseOrderItems = purchaseOrderItemsRepository.findAllByProductId(id)
                .stream()
                .filter(Objects::nonNull)
                .toList();

        validateDeleteProduct(saleDetailsList, claims, purchaseOrderItems);

        //Eliminar de los suppliers y supplierProducts
        product.getSuppliers().forEach(supplier -> supplier.getProducts().remove(product));
        product.getSupplierProducts().forEach(supplierProduct -> supplierProduct.setProduct(null));

        productRepository.deleteById(id);

    }

}
