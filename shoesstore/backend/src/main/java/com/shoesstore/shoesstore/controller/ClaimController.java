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

import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
        model.addAttribute("view", "claims/list");
        return "layout";
    }

    @GetMapping("/new")
    public String newClaimForm(Model model) {
        List<Sale> salesWithoutClaims = saleService.getSalesWithoutClaims();
        model.addAttribute("sales", salesWithoutClaims);
        model.addAttribute("view", "claims/new");
        return "layout";
    }

    @PostMapping
    public String createClaim(
            @RequestParam("saleId") Long saleId,
            @RequestParam("description") String description,
            @RequestParam Map<String, String> allParams) {

        Map<Long, Integer> claimItems = new HashMap<>();

        allParams.keySet().stream()
                .filter(key -> key.matches("claimItems\\[\\d+\\]\\.include"))
                .forEach(includeKey -> {
                    String idStr = includeKey.replaceAll("claimItems\\[(\\d+)]\\.include", "$1");
                    Long saleDetailId = Long.parseLong(idStr);
                    String quantityKey = "claimItems[" + saleDetailId + "].quantity";
                    int quantity = Integer.parseInt(allParams.getOrDefault(quantityKey, "1"));
                    claimItems.put(saleDetailId, quantity);
                });

        if (claimItems.isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos un producto para reclamar");
        }

        claimService.createClaim(saleId, description, claimItems);
        return "redirect:/claims";
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
        model.addAttribute("claims", claimService.getAllClaims());
        model.addAttribute("view", "claims/view");
        return "layout";
    }
}
