package com.shoesstore.shoesstore.service;

import com.shoesstore.shoesstore.model.*;
import com.shoesstore.shoesstore.repository.ClaimRepository;
import com.shoesstore.shoesstore.repository.SaleDetailsRepository;
import com.shoesstore.shoesstore.repository.SaleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final ProductService productService;

    @Autowired
    public ClaimService(ClaimRepository claimRepository,
                        SaleRepository saleRepository,
                        SaleDetailsRepository saleDetailsRepository,
                        ProductService productService) {
        this.claimRepository = claimRepository;
        this.saleRepository = saleRepository;
        this.saleDetailsRepository = saleDetailsRepository;
        this.productService = productService;
    }

    // Tenemos que crear el reclamo, esto significa establecer las cantidades que se van a querer eliminar de las ventas
    // Todas las claims tienen asociadas una venta y un conjunto de productos que se quieren actualizar en la venta
    // Al crear la claim podría eliminarse en algun momento supongo, por lo tanto los items no deberia borrarse instantamente
    //sino cuando ya se procesaron y se saben que se van a descontar
    @Transactional
    public Claim createClaim(Long saleId, String description, Map<Long, Integer> claimItems) {
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new EntityNotFoundException("Venta no encontrada"));

        Claim claim = new Claim();
        claim.setSale(sale);
        claim.setDescription(description);
        claim.setState(Claim.State.INITIATED);
        claim.setCreatedAt(LocalDateTime.now());

        //claimItems tiene el id del saleDetail y la cantidad

        List<ClaimDetails> claimDetails = new ArrayList<>();

        //Armo los claim details

        claimItems.keySet().forEach(id -> {
            ClaimDetails claimDetail = new ClaimDetails();
            claimDetail.setClaim(claim);
            claimDetail.setQuantity(claimItems.get(id));
            claimDetail.setSaleDetails(saleDetailsRepository.findById(id).get());
            claimDetails.add(claimDetail);
        });

        claim.setClaimDetails(claimDetails);

        // Guardar el reclamo primero
        Claim savedClaim = claimRepository.save(claim);

        // Procesar cada item reclamado
        /*List<SaleDetails> claimedDetails = new ArrayList<>();*/

       /* claimItems.forEach((saleDetailId, quantity) -> {
            SaleDetails detail = saleDetailsRepository.findById(saleDetailId)
                    .orElseThrow(() -> new EntityNotFoundException("Detalle de venta no encontrado"));

            // Validar que la cantidad reclamada no exceda la comprada
            if (quantity > detail.getQuantity()) {
                throw new IllegalArgumentException("La cantidad reclamada no puede ser mayor a la comprada");
            }

            // Si se reclama la cantidad completa
            if (quantity == detail.getQuantity()) {
                //Borramos el detalle de venta, porque es una venta que se elimina
                saleDetailsRepository.delete(detail);
            }
            // Si es un reclamo parcial
            else {
                // Crear un nuevo detalle para el reclamo parcial

                //Hay que actualizar los saleDetails unicamente, sus cantidades
                detail.setQuantity(detail.getQuantity() - quantity);
                detail.setSubtotal(detail.getSubtotal() - detail.getProduct().getPrice() * detail.getQuantity());

                saleDetailsRepository.save(detail);
            }
        });*/

        return claimRepository.save(savedClaim);
    }

    //Obtener todos los reclamos
    public List<Claim> getAllClaims() {
        return claimRepository.findAll();
    }

    // Subir comprobante de despacho
    public Claim uploadProof(Long claimId, String fileName) {
        Claim claim = getClaimById(claimId);
        claim.uploadProof(fileName); // Solo almacena el nombre del archivo
        return claimRepository.save(claim);
    }
    // Aprobar devolución y procesar reembolso
    @Transactional
    public Claim approveRefund(Long claimId) {
        Claim claim = getClaimById(claimId);
        claim.approveRefund();

        // Procesar reembolso financiero (implementar esta lógica)
        // Tenemos que actualizar el stock de los productos(va a fallar ahora porque la logica esta mal implementada de momento)

        // Actualizamos ahora la venta, cambiando el total
        Sale sale = claim.getSale();
        sale.setTotal(sale.getTotal() - calculateRefundAmount(claim).doubleValue());

        //Tendria que actualizar los stocks de los details y en caso de que se borren todos borrarlos


        //Actualizo los valores en cada detalle

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

    private void updateProductStock(Claim claim) {
        for (SaleDetails detail : claim.getSaleDetails()) {
            Product product = productService.getProductById(detail.getProduct().getId());
            product.setStock(product.getStock() + detail.getQuantity());
            productService.saveProduct(product);
        }
    }

    private BigDecimal calculateRefundAmount(Claim claim) {
        BigDecimal total = BigDecimal.ZERO;
        for (ClaimDetails detail : claim.getClaimDetails()) {
            total = total.add(detail.getQuantity().multiply(claim.getSaleDetails().get(detail.getId()).getProduct().getPrice()));
        }
        return total;
    }


    public Claim getClaimById(Long id) {
        return claimRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reclamo no encontrado"));
    }

    public List<Claim> getClaimsByState(Claim.State state) {
        return claimRepository.findByState(state);
    }
}