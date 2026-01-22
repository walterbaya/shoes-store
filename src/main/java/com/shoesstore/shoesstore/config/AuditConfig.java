package com.shoesstore.shoesstore.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.cache.annotation.EnableCaching; // <-- Añadir esta importación

@Configuration
@EnableJpaAuditing
@EnableCaching // <-- Añadir esta anotación
public class AuditConfig {
}
