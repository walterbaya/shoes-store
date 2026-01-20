package com.shoesstore.shoesstore.service;

import com.shoesstore.shoesstore.dto.ProductWithSuppliersDTO;
import com.shoesstore.shoesstore.exception.InsufficientStockException;
import com.shoesstore.shoesstore.exception.ProductServiceException;
import com.shoesstore.shoesstore.model.*;
import com.shoesstore.shoesstore.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final PurchaseOrderItemsRepository purchaseOrderItemsRepository;
    private final ClaimRepository claimRepository;
    private final SaleDetailsRepository saleDetailsRepository;

    
    
    public ProductService(ProductRepository productRepository,
			PurchaseOrderItemsRepository purchaseOrderItemsRepository, ClaimRepository claimRepository,
			SaleDetailsRepository saleDetailsRepository) {
		
		this.productRepository = productRepository;
		this.purchaseOrderItemsRepository = purchaseOrderItemsRepository;
		this.claimRepository = claimRepository;
		this.saleDetailsRepository = saleDetailsRepository;
	}

	public List<Product> getAllProducts() {
        return productRepository.findAll();
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

    @Transactional
    public List<ProductWithSuppliersDTO> getAllProductsWithSuppliers() {
        List<Product> products = productRepository.findAllWithSuppliers();
        return products.stream()
                .map(ProductWithSuppliersDTO::new)
                .collect(Collectors.toList());
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

    @Transactional
    public void addOneToStock(Long id) {
        if (id == null || !productRepository.existsById(id)) {
            throw new IllegalArgumentException("No existe un producto con ID: " + id);
        }

        productRepository.addOneToStock(id);
    }

    public Product saveProduct(Product product) {
        // Validar que siempre venga ID desde el formulario
        if (product.getName() == null) {
            throw new ProductServiceException("El ID del producto es obligatorio");
        }
        if (product.getPrice() <= 0) {
            throw new ProductServiceException(("El precio debe ser mayor a 0");
        }
        if(product.getStock() <= 0){
            throw new ProductServiceException(("El stock debe ser mayor a 0");
        }

        return productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Long id) {

        Optional<Product> optionalProduct = productRepository.findById(id);

        if(optionalProduct.isPresent()){

           List<SaleDetails> saleDetailsList = saleDetailsRepository.findAllByProductId(id);
           List<Claim> claims = saleDetailsList.stream()
                   .map(SaleDetails::getClaim)
                   .filter(Objects::nonNull)
                   .toList();
           List<PurchaseOrderItem> purchaseOrderItems = purchaseOrderItemsRepository.findAllByProductId(id)
                   .stream()
                   .filter(Objects::nonNull)
                   .toList();

           if(!claims.isEmpty()){
               List<Long> claimIds = claims.stream().map(Claim::getId).toList();
               throw new IllegalArgumentException("No se puede eliminar un producto que posee reclamos registrados, elimine primeramente las reclamos con los siguientes IDs: " + claimIds.stream().map(String::valueOf).collect(Collectors.joining(", ")));
           }

            if(!purchaseOrderItems.isEmpty()){
                List<Long> purchaseOrderItemIds = purchaseOrderItems.stream().map(PurchaseOrderItem::getId).toList();
                throw new IllegalArgumentException("No se puede eliminar un producto que posee ordenes de compra registradas" + ", elimine primeramente las ordenes de compra con los siguientes IDs: " + purchaseOrderItemIds.stream().map(String::valueOf).collect(Collectors.joining(", ")));
            }

            if(!saleDetailsList.isEmpty()){
               List<Long> saleIds = saleDetailsList.stream().map(sd -> sd.getSale().getId()).toList();
               throw new IllegalArgumentException("No se puede eliminar un producto que posee ventas registradas, elimine primeramente las ventas con los siguientes IDs: " + saleIds.stream().map(String::valueOf).collect(Collectors.joining(", ")));
           }


        }
        else{
            throw new IllegalArgumentException("No existe un producto con ID: " + id);
        }

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe un producto con ID: " + id));

        //Eliminar de los suppliers y supplierProducts
        product.getSuppliers().forEach(supplier -> supplier.getProducts().remove(product));
        product.getSupplierProducts().forEach(supplierProduct -> supplierProduct.setProduct(null));

        productRepository.deleteById(id);
    }

}


