package com.example.productScanner.service;

import com.example.productScanner.dto.ScanRequest;

public interface IProductScannerService {
    void increaseStockInOneUnit(ScanRequest scanRequest);
}
