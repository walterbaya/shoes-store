package com.shoesstore.shoesstore.service;

import com.shoesstore.shoesstore.model.*;
import com.shoesstore.shoesstore.repository.ClaimRepository;
import com.shoesstore.shoesstore.repository.SaleDetailsRepository;
import com.shoesstore.shoesstore.repository.SaleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final SaleRepository saleRepository;
    private final SaleDetailsRepository saleDetailsRepository;

    public ClaimService(ClaimRepository claimRepository, SaleRepository saleRepository,
			SaleDetailsRepository saleDetailsRepository) {
		super();
		this.claimRepository = claimRepository;
		this.saleRepository = saleRepository;
		this.saleDetailsRepository = saleDetailsRepository;
	}

    @Transactional
    public Claim createClaim(Long saleId, String description, Map<Long, Integer> claimItems) {
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new EntityNotFoundException("Venta no encontrada"));

        Claim claim = new Claim();
        claim.setSale(sale);
        claim.setDescription(description);
        claim.setState(Claim.State.INITIATED);
        claim.setCreatedAt(LocalDateTime.now());

        List<ClaimDetails> claimDetails = new ArrayList<>();

        claimItems.keySet().forEach(id -> {
            ClaimDetails claimDetail = new ClaimDetails();
            claimDetail.setClaim(claim);
            claimDetail.setQuantity(claimItems.get(id));
            SaleDetails saleDetails = saleDetailsRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Detalle de venta no encontrado"));
            saleDetails.setClaim(claim);
            claimDetail.setSaleDetails(saleDetails);
            claimDetails.add(claimDetail);
        });

        claim.setClaimDetails(claimDetails);

        return claimRepository.save(claim);
    }

    @Transactional
    public Claim uploadProof(Long claimId, String fileName) {
        Claim claim = getClaimById(claimId);
        
        if (claim.getState() != Claim.State.INITIATED) {
            throw new IllegalStateException("Solo se puede subir comprobante en estado INICIADO");
        }
        
        claim.setShippingProofUrl(fileName);
        claim.setState(Claim.State.PROOF_UPLOADED);
        claim.setProofUploadedDate(LocalDateTime.now());
        
        return claimRepository.save(claim);
    }

    @Transactional
    public Claim approveRefund(Long claimId) {
        Claim claim = getClaimById(claimId);
        
        if (claim.getState() != Claim.State.PROOF_UPLOADED) {
            throw new IllegalStateException("El comprobante debe estar subido y en espera de aprobación");
        }
        
        claim.setState(Claim.State.REFUND_PROCESSED);
        claim.setRefundProcessedDate(LocalDateTime.now());

        //Actualizamos los detalles
        claim.getClaimDetails().forEach(claimDetail -> {
            //Actualizamos ahora el subtotal
            claimDetail.getSaleDetails().setSubtotal(claimDetail.getSaleDetails().getSubtotal() - claimDetail.getSaleDetails().getProduct().getPrice() * claimDetail.getQuantity());
        });

        //En otro caso Actualizamos ahora la venta, cambiando el total
        Sale sale = claim.getSale();
        sale.setTotal(sale.getTotal() - calculateRefundAmount(claim).doubleValue());

        return claim; // Se guarda automáticamente al final de la transacción
    }

    @Transactional
    public Claim receivePackage(Long claimId) {
        Claim claim = getClaimById(claimId);
        
        if (claim.getState() != Claim.State.REFUND_PROCESSED) {
            throw new IllegalStateException("Debe haberse procesado el reembolso antes de recibir el paquete");
        }
        
        claim.setState(Claim.State.PACKAGE_RECEIVED);
        claim.setPackageReceivedDate(LocalDateTime.now());

        // Actualizar el stock de los productos
        updateProductStock(claim);

        //Actualizamos los detalles
        claim.getClaimDetails().forEach(claimDetail -> {
            //Actualizamos la cantidad
            claimDetail.getSaleDetails().setQuantity(claimDetail.getSaleDetails().getQuantity() - claimDetail.getQuantity());

            if(claimDetail.getQuantity() == 0){
                saleDetailsRepository.delete(claimDetail.getSaleDetails());
            }

            if(claimDetail.getSaleDetails().getQuantity() < 0){
                throw new IllegalArgumentException("La cantidad reclamada no puede ser mayor a la comprada");
            }
        });

        //Revisamos si la venta quedo sin ninguno de los detalles
        if(claim.getSale().getDetails() == null || claim.getSale().getDetails().isEmpty() ) {
            //En ese caso borramos la venta
            saleRepository.delete(claim.getSale());
        }

        return claim;
    }

    public List<Claim> getAllClaims() {
        return claimRepository.findAll();
    }

    public Claim getClaimById(Long id) {
        return claimRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reclamo no encontrado"));
    }

    public List<Claim> getClaimsByState(Claim.State state) {
        return claimRepository.findByState(state);
    }

    private void updateProductStock(Claim claim) {
        claim.getClaimDetails().forEach(detail -> {
            detail.getSaleDetails().getProduct()
                    .setStock(detail.getSaleDetails().getProduct().getStock() + detail.getQuantity());
        });
    }

    private BigDecimal calculateRefundAmount(Claim claim) {
        BigDecimal total = BigDecimal.ZERO;
        for (ClaimDetails detail : claim.getClaimDetails()) {
            total = total.add(BigDecimal.valueOf(detail.getQuantity()).multiply(BigDecimal.valueOf(detail.getSaleDetails().getProduct().getPrice())));
        }
        return total;
    }

    @Transactional
    public Claim invalidateProof(Long claimId) {
        Claim claim = getClaimById(claimId);
        if (claim.getState() != Claim.State.PROOF_UPLOADED) {
            throw new IllegalStateException("Solo se puede invalidar comprobantes en estado PROOF_UPLOADED");
        }
        claim.setState(Claim.State.INITIATED);
        claim.setProofUploadedDate(null);
        claim.setShippingProofUrl(null);
        return claimRepository.save(claim);
    }

    @Transactional
    public void deleteClaim(Long claimId) {
        Claim claim = getClaimById(claimId);
        if(claim.getState() != Claim.State.PACKAGE_RECEIVED){
            throw new IllegalStateException("Solo se puede eliminar reclamos en estado finalizado.");
        }

        Sale sale = claim.getSale();
        claim.getClaimDetails().forEach(detail -> {
            detail.getSaleDetails().setClaim(null);
        });
        if (sale != null) {
            sale.setClaim(null);
        }

        claimRepository.delete(claim);
    }
}
