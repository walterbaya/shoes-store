package com.shoesstore.shoesstore.service;

import com.shoesstore.shoesstore.exception.InsufficientStockException;
import com.shoesstore.shoesstore.model.Product;
import com.shoesstore.shoesstore.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public void updateStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (product.getStock() < quantity) {
            throw new InsufficientStockException("Stock insuficiente");
        }

        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
    }

    @Transactional
    public Product updateProduct(Product product) {
        Long id = product.getId();
        if (id == null || !productRepository.existsById(id)) {
            throw new IllegalArgumentException("No existe un producto con ID: " + id);
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

    public Product saveProduct(Product product) {
        // Validar que siempre venga ID desde el formulario
        if (product.getId() == null) {
            throw new IllegalArgumentException("El ID del producto es obligatorio");
        }
        // Si es creación y ya existe, lanzar excepción opcionalmente:
        if (!productRepository.existsById(product.getId())) {
            return productRepository.save(product);
        }
        // Si ya existe, hacemos actualización
        else{
            throw new IllegalArgumentException("El código de producto ingresado ya existe, si desea modificarlo utilice la opción editar.");
        }
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

}


