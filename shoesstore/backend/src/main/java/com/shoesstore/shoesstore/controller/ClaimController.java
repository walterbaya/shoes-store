package com.shoesstore.shoesstore.controller;

import com.shoesstore.shoesstore.model.Claim;
import com.shoesstore.shoesstore.model.Sale;
import com.shoesstore.shoesstore.service.ClaimService;
import com.shoesstore.shoesstore.service.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequestMapping("/claims")
public class ClaimController {

    private final ClaimService claimService;
    private final SaleService saleService;

    @Autowired
    public ClaimController(ClaimService claimService, SaleService saleService) {
        this.claimService = claimService;
        this.saleService = saleService;
    }

    @GetMapping
    public String listClaims(Model model) {
        model.addAttribute("claims", claimService.getAllClaims());
        return "claims/list";
    }

    @GetMapping("/new")
    public String newClaimForm(Model model) {
        // Obtener ventas sin reclamos
        List<Sale> salesWithoutClaims = saleService.getSalesWithoutClaims();

        model.addAttribute("sales", salesWithoutClaims);
        return "claims/new";
    }

    @PostMapping
    public String createClaim(@RequestParam("saleId") Long saleId,
                              @RequestParam("description") String description) {
        Claim claim = claimService.createClaim(saleId, description);
        return "redirect:/claims/" + claim.getId();
    }

    @PostMapping("/{id}/proof")
    public String uploadProof(@PathVariable Long id, @RequestParam("proofUrl") String proofUrl) {
        claimService.uploadProof(id, proofUrl);
        return "redirect:/claims/" + id;
    }

    @PostMapping("/{id}/approve")
    public String approveRefund(@PathVariable Long id) {
        claimService.approveRefund(id);
        return "redirect:/claims/" + id;
    }

    @PostMapping("/{id}/receive")
    public String receivePackage(@PathVariable Long id) {
        claimService.receivePackage(id);
        return "redirect:/claims/" + id;
    }

    @GetMapping("/{id}")
    public String viewClaim(@PathVariable Long id, Model model) {
        Claim claim = claimService.getClaimById(id);
        model.addAttribute("claim", claim);
        return "claims/view";
    }
}