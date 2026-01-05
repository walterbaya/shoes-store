package com.shoesstore.shoesstore.service;

import com.shoesstore.shoesstore.model.Product;
import com.shoesstore.shoesstore.model.Sale;
import com.shoesstore.shoesstore.model.SaleDetails;
import com.shoesstore.shoesstore.model.User;
import com.shoesstore.shoesstore.repository.ClaimRepository;
import com.shoesstore.shoesstore.repository.SaleDetailsRepository;
import com.shoesstore.shoesstore.repository.SaleRepository;
import com.shoesstore.shoesstore.repository.UserRepository;
import com.shoesstore.shoesstore.utils.SaleItemForm;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final ProductService productService;
    private final SaleDetailsRepository saleDetailsRepository;
    private final UserService userService;
    	
    public SaleService(SaleRepository saleRepository, ProductService productService,
			SaleDetailsRepository saleDetailsRepository, UserService userService) {
		this.saleRepository = saleRepository;
		this.productService = productService;
		this.saleDetailsRepository = saleDetailsRepository;
		this.userService = userService;
	}


	@Transactional
    public Sale processSale(Sale sale, List<SaleItemForm> itemsToProcess) {
        double totalSale = 0;
        List<SaleDetails> saleDetailsList = new ArrayList<>();
        Optional<User> user = userService.getUserByUsername("username");

        for (SaleItemForm item : itemsToProcess) {
            productService.updateStock(item.getProductId(), item.getQuantity());
            Product product = productService.getProductById(item.getProductId());

            SaleDetails saleDetails = new SaleDetails();
            saleDetails.setProduct(product);
            saleDetails.setQuantity(item.getQuantity());
            saleDetails.setSubtotal(item.getQuantity() * product.getPrice());

            saleDetailsList.add(saleDetails);

            totalSale += saleDetails.getSubtotal();

        }

        sale.setUser(user.get());
        sale.setTotal(totalSale - totalSale * (sale.getDiscountPercentage()/100.0));

        Sale res = saleRepository.save(sale);

        saleDetailsList.forEach(saleDetails ->
                {
                    saleDetails.setSale(res);
                    saleDetailsRepository.save(saleDetails);
            }
        );

        return res;
    }


    public List<Sale> getSalesByDate(LocalDateTime startDate, LocalDateTime endDate) {
        // Si startDate o endDate son nulos, asignar valores por defecto
        LocalDateTime start = (startDate != null) ? startDate : LocalDateTime.now().minusMonths(1);
        LocalDateTime end = (endDate != null) ? endDate : LocalDateTime.now();

        // Llamar al método del repositorio con las fechas y la paginación
        return saleRepository.findBySaleDateBetween(start, end);
    }

    public Sale getSaleById(Long id) {
        return saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con ID: " + id));
    }

    @Transactional
    public void deleteSale(Long id) {
        Sale sale = getSaleById(id);

        if (sale.getClaim() != null) {
            throw new RuntimeException("No se puede eliminar una venta con un reclamo asociado, elimine primero el reclamo y posteriormente la venta.");
        }
        else {

            sale.getDetails().forEach(saleDetails -> {
                productService.updateStock(saleDetails.getProduct().getId(), saleDetails.getQuantity());
                saleDetailsRepository.delete(saleDetails);
            });
            saleRepository.delete(sale);
        }
    }
    
    public List<Object[]> getDailySalesData(LocalDateTime start, LocalDateTime end) {
        return saleRepository.findDailySalesByUser(start, end);
    }

    public List<Object[]> getTopSellers(LocalDateTime start, LocalDateTime end) {
        return saleRepository.findTopSellers(start, end);
    }

    public List<Sale> getAllSales() {
        return saleRepository.findAll();
    }

    public List<Sale> getSalesWithoutClaims() {
        return saleRepository.findAll().stream()
                .filter(sale -> sale.getTotal() > 0 && sale.getClaim() == null)
                .collect(Collectors.toList());
    }
}