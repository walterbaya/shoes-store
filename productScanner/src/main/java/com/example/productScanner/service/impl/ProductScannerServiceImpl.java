package com.example.productScanner.service.impl;
import com.example.productScanner.service.IProductScannerService;
import com.example.productScanner.service.client.ProductsFeignClient;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class ProductScannerServiceImpl implements IProductScannerService {

    private ProductsFeignClient productsFeignClient;

    @Override
    public void increaseStockInOneUnit(Long id) {
        productsFeignClient.addOneToStock(id);
    }
}



















































