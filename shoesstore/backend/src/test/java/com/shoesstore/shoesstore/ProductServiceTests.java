package com.shoesstore.shoesstore;

import com.shoesstore.shoesstore.model.Product;
import com.shoesstore.shoesstore.repository.ProductRepository;
import com.shoesstore.shoesstore.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Test
    @Transactional
    void testStockUpdate() {
        Product product = new Product("Zapato Running", 42, 100.00, 50);
        productRepository.save(product);

        productService.updateStock(product.getId(), 10);

        assertEquals(40, productRepository.findById(product.getId()).get().getStock());
    }
}