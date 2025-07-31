package com.shoesstore.shoesstore.service;

import com.shoesstore.shoesstore.model.*;
import com.shoesstore.shoesstore.repository.ClaimRepository;
import com.shoesstore.shoesstore.repository.SaleDetailsRepository;
import com.shoesstore.shoesstore.repository.SaleRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClaimServiceTest {

    @Mock
    private ClaimRepository claimRepository;

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private ClaimService claimService;

    @Test
    void testCreateClaim() {
        Long saleId = 1L;
        String description = "Producto defectuoso";

        Sale sale = new Sale();
        sale.setId(saleId);

        when(saleRepository.findById(saleId)).thenReturn(Optional.of(sale));

        ArgumentCaptor<Claim> captor = ArgumentCaptor.forClass(Claim.class);
        when(claimRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Claim created = claimService.createClaim(saleId, description, new HashMap<>());

        verify(claimRepository).save(captor.capture());
        Claim saved = captor.getValue();

        assertEquals(sale, saved.getSale());
        assertEquals(description, saved.getDescription());
        assertEquals(Claim.State.INITIATED, saved.getState());
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    void testUploadProof() {
        Long claimId = 1L;
        MultipartFile file = mock(MultipartFile.class);

        String filePath = "proof123.pdf";
        Claim claim = new Claim();
        claim.setId(claimId);
        claim.setState(Claim.State.INITIATED);

        when(claimRepository.findById(claimId)).thenReturn(Optional.of(claim));
        when(claimRepository.save(any())).thenAnswer(i -> i.getArgument(0));


        Claim updated = claimService.uploadProof(claimId, filePath);


        assertEquals(Claim.State.PROOF_UPLOADED, updated.getState());
        assertEquals("/uploads/proof123.pdf", updated.getShippingProofUrl());
        assertNotNull(updated.getProofUploadedDate());
    }

    @Test
    void testApproveRefund() {
        Long claimId = 2L;

        Claim claim = new Claim();
        claim.setId(claimId);
        claim.setState(Claim.State.PROOF_UPLOADED);

        ClaimDetails claimDetail = new ClaimDetails();
        claimDetail.setClaim(claim);
        claimDetail.setQuantity(1);
        claimDetail.setSaleDetails(new SaleDetails());

        claimDetail.getSaleDetails().setProduct(new Product());
        claim.setClaimDetails( List.of(claimDetail));

        Sale sale = new Sale();
        sale.setTotal(100.0);
        claim.setSale(sale);

        when(claimRepository.findById(claimId)).thenReturn(Optional.of(claim));

        Claim result = claimService.approveRefund(claimId);

        assertEquals(Claim.State.REFUND_PROCESSED, result.getState());
        assertNotNull(result.getRefundProcessedDate());
    }

    @Test
    void testReceivePackage() {
        Long claimId = 3L;

        Claim claim = new Claim();
        claim.setId(claimId);
        claim.setState(Claim.State.REFUND_PROCESSED);

        ClaimDetails claimDetail = new ClaimDetails();
        claimDetail.setClaim(claim);
        claimDetail.setQuantity(1);
        SaleDetails saleDetails = new SaleDetails();
        saleDetails.setQuantity(100);
        claimDetail.setSaleDetails(saleDetails);

        claimDetail.getSaleDetails().setProduct(new Product());
        claim.setClaimDetails( List.of(claimDetail));

        Sale sale = new Sale();
        sale.setTotal(100.0);
        claim.setSale(sale);

        when(claimRepository.findById(claimId)).thenReturn(Optional.of(claim));

        Claim result = claimService.receivePackage(claimId);

        assertEquals(Claim.State.PACKAGE_RECEIVED, result.getState());
        assertNotNull(result.getPackageReceivedDate());
    }

    @Test
    void testInvalidateProof() {
        Long claimId = 4L;

        Claim claim = new Claim();
        claim.setId(claimId);
        claim.setState(Claim.State.PROOF_UPLOADED);
        claim.setShippingProofUrl("proof.pdf");

        when(claimRepository.findById(claimId)).thenReturn(Optional.of(claim));
        when(claimRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Claim result = claimService.invalidateProof(claimId);

        assertEquals(Claim.State.INITIATED, result.getState());
        assertEquals("/uploads/null", result.getShippingProofUrl());
        assertNull(result.getProofUploadedDate());
    }

    @Test
    void testDeleteClaim() {
        Long claimId = 5L;

        // Crear un reclamo de prueba
        Claim claim = new Claim();
        claim.setId(claimId);
        claim.setState(Claim.State.PACKAGE_RECEIVED);
        claim.setShippingProofUrl("proof.pdf");

        ClaimDetails claimDetail = new ClaimDetails();
        claimDetail.setClaim(claim);
        claimDetail.setQuantity(1);
        SaleDetails saleDetails = new SaleDetails();
        saleDetails.setQuantity(100);
        claimDetail.setSaleDetails(saleDetails);

        claimDetail.getSaleDetails().setProduct(new Product());
        claim.setClaimDetails(List.of(claimDetail));

        Sale sale = new Sale();
        sale.setTotal(100.0);
        claim.setSale(sale);

        // Simular la llamada al repositorio
        when(claimRepository.findById(claimId)).thenReturn(Optional.of(claim));

        // Ejecutar el método
        claimService.deleteClaim(claimId);

        // Verificaciones
        verify(claimRepository).delete(claim);
        verify(claimRepository).findById(claimId); // Opcional: que se buscó el reclamo
    }


    @Test
    void testGetAllClaims() {
        List<Claim> claims = List.of(new Claim(), new Claim());
        when(claimRepository.findAll()).thenReturn(claims);

        List<Claim> result = claimService.getAllClaims();

        assertEquals(2, result.size());
    }

    @Test
    void testGetClaimsByState() {
        Claim.State state = Claim.State.INITIATED;
        List<Claim> claims = List.of(new Claim());
        when(claimRepository.findByState(state)).thenReturn(claims);

        List<Claim> result = claimService.getClaimsByState(state);

        assertEquals(1, result.size());
    }

    @Test
    void testGetClaimById() {
        Claim claim = new Claim();
        claim.setId(1L);
        when(claimRepository.findById(1L)).thenReturn(Optional.of(claim));

        Optional<Claim> result = Optional.ofNullable(claimService.getClaimById(1L));

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }
}
