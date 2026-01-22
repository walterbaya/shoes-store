package com.shoesstore.shoesstore.controller;

import com.shoesstore.shoesstore.dto.SaleForm;
import com.shoesstore.shoesstore.model.enums.SaleChannel;
import com.shoesstore.shoesstore.service.ProductService;
import com.shoesstore.shoesstore.service.SaleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SaleController.class)
class SaleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SaleService saleService;

    @MockBean
    private ProductService productService;

    @Test
    @WithMockUser(username = "seller", roles = {"SELLER"})
    void createSale_shouldSucceed_whenDataIsValid() throws Exception {
        // Simulamos un POST válido
        mockMvc.perform(post("/sales/create")
                        .with(csrf()) // Importante para seguridad
                        .param("channel", "ONLINE")
                        .param("paymentMethod", "CREDIT_CARD")
                        .param("shippingCost", "10.0")
                        .param("discountPercentage", "0")
                        .param("total", "100.0")
                        // Simulamos items (la estructura de lista en form-data es compleja, 
                        // aquí simplificamos asumiendo que el binding funciona si enviamos índices)
                        .param("saleItems[0].productId", "1")
                        .param("saleItems[0].quantity", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/sales"))
                .andExpect(flash().attributeExists("success"));

        // Verificamos que se llamó al servicio
        verify(saleService).processSale(any(), any());
    }

    @Test
    @WithMockUser(username = "seller", roles = {"SELLER"})
    void createSale_shouldFail_whenDataIsInvalid() throws Exception {
        // Simulamos un POST inválido (sin canal, costo negativo)
        mockMvc.perform(post("/sales/create")
                        .with(csrf())
                        .param("channel", "") // Inválido
                        .param("shippingCost", "-5.0")) // Inválido
                .andExpect(status().is3xxRedirection()) // Tu controlador redirige en error, no devuelve vista directa
                .andExpect(redirectedUrl("/sales"))
                .andExpect(flash().attributeExists("error"));
        
        // OJO: Tu controlador actual redirige a /sales en caso de error de validación, 
        // lo cual es un comportamiento que vimos antes.
    }
}
