package com.shoesstore.shoesstore.service;

import com.shoesstore.shoesstore.model.Claim;
import com.shoesstore.shoesstore.model.Product;
import com.shoesstore.shoesstore.model.Sale;
import com.shoesstore.shoesstore.model.SaleDetails;
import com.shoesstore.shoesstore.repository.ClaimRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Service
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final SaleService saleService;
    private final ProductService productService;

    @Autowired
    public ClaimService(ClaimRepository claimRepository,
                        SaleService saleService,
                        ProductService productService) {
        this.claimRepository = claimRepository;
        this.saleService = saleService;
        this.productService = productService;
    }

    // Crear nuevo reclamo
    public Claim createClaim(Long saleId, String description) {
        if (claimRepository.existsBySaleId(saleId)) {
            throw new IllegalStateException("Esta venta ya tiene un reclamo registrado");
        }

        Sale sale = saleService.getSaleById(saleId);

        Claim claim = new Claim();
        claim.setSale(sale);
        claim.setDescription(description);
        claim.setSaleDetails(new ArrayList<>(sale.getDetails()));

        return claimRepository.save(claim);
    }

    //Obtener todos los reclamos
    public List<Claim> getAllClaims() {
        return claimRepository.findAll();
    }

    // Subir comprobante de despacho
    public Claim uploadProof(Long claimId, String proofUrl) {
        Claim claim = getClaimById(claimId);
        claim.uploadProof(proofUrl);
        return claimRepository.save(claim);
    }

    // Aprobar devolución y procesar reembolso
    @Transactional
    public Claim approveRefund(Long claimId) {
        Claim claim = getClaimById(claimId);
        claim.approveRefund();

        // Procesar reembolso financiero (implementar esta lógica)
        processFinancialRefund(claim);

        return claimRepository.save(claim);
    }

    // Recibir paquete y actualizar stock
    @Transactional
    public Claim receivePackage(Long claimId) {
        Claim claim = getClaimById(claimId);
        claim.receivePackage();

        // Actualizar stock de productos
        updateProductStock(claim);

        return claimRepository.save(claim);
    }

    private void processFinancialRefund(Claim claim) {
        // Lógica para revertir el pago al cliente
        // (depende de tu sistema de pagos)
        BigDecimal refundAmount = calculateRefundAmount(claim);
        // financialService.processRefund(claim.getSale().getCustomer(), refundAmount);
    }

    private void updateProductStock(Claim claim) {
        for (SaleDetails detail : claim.getSaleDetails()) {
            Product product = productService.getProductById(detail.getProduct().getId());
            product.setStock(product.getStock() + detail.getQuantity());
            productService.saveProduct(product);
        }
    }

    private BigDecimal calculateRefundAmount(Claim claim) {
        return claim.getSaleDetails().stream()
                .map(detail -> BigDecimal.valueOf(detail.getProduct().getPrice()).multiply(BigDecimal.valueOf(detail.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Claim getClaimById(Long id) {
        return claimRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reclamo no encontrado"));
    }

    public List<Claim> getClaimsByState(Claim.State state) {
        return claimRepository.findByState(state);
    }
}