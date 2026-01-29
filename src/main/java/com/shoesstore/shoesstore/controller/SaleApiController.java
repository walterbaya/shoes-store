package com.shoesstore.shoesstore.controller;

import com.shoesstore.shoesstore.dto.ProductDto;
import com.shoesstore.shoesstore.dto.SaleDetailResponse;
import com.shoesstore.shoesstore.model.Sale;
import com.shoesstore.shoesstore.model.SaleDetails;
import com.shoesstore.shoesstore.service.SaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sales")
@Tag(name = "Sales API", description = "Endpoints para la gestión de detalles de ventas")
public class SaleApiController {

    @Autowired
    private SaleService saleService;

    public SaleApiController(SaleService saleService) {
        this.saleService = saleService;
    }

    /**
     * Implementación de Seguridad a Nivel de Método y Principio de Mínimo Privilegio.
     * @PreAuthorize: Ejecuta una expresión SpEL (Spring Expression Language) antes de entrar al método.
     * - "hasRole('ADMIN')": Permite el acceso si el usuario es administrador.
     * - "or @saleService.isOwner(#saleId, principal.username)": O, si el método isOwner del bean 'saleService'
     *   devuelve true para el ID de la venta y el nombre del usuario autenticado.
     * Esto asegura que un usuario solo pueda ver sus propias ventas, incluso si comparte el rol 'SELLER' con otros.
     */
    @GetMapping("/{saleId}/details")
    @Operation(summary = "Obtener detalles de una venta", description = "Devuelve la lista de productos, cantidades y subtotales para una venta específica.")
    @ApiResponse(responseCode = "200", description = "Detalles de la venta encontrados", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SaleDetailResponse.class)))
    @ApiResponse(responseCode = "404", description = "Venta no encontrada")
    @ApiResponse(responseCode = "403", description = "Acceso denegado")
    @PreAuthorize("hasRole('ADMIN') or @saleService.isOwner(#saleId, principal.username)")
    public ResponseEntity<List<SaleDetailResponse>> getSaleDetails(
            @Parameter(description = "ID de la venta a consultar", required = true) @PathVariable Long saleId) {
        try {
            Sale sale = saleService.getSaleById(saleId);
            List<SaleDetailResponse> details = sale.getDetails().stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(details);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    private SaleDetailResponse convertToResponse(SaleDetails detail) {
        SaleDetailResponse resp = new SaleDetailResponse();
        resp.setId(detail.getId());
        resp.setQuantity(detail.getQuantity());
        resp.setSubtotal(detail.getSubtotal());

        var p = detail.getProduct();
        ProductDto dto = new ProductDto();
        dto.setId(p.getId());
        dto.setDescription(p.getDescription());
        dto.setSize(p.getSize());
        dto.setPrice(p.getPrice());
        dto.setStock(p.getStock());

        resp.setProduct(dto);
        return resp;
    }
}
