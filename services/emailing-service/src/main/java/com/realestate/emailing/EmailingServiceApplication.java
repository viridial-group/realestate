package com.realestate.emailing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

/**
 * Application principale du Emailing Service
 * 
 * Exclut les configurations WebFlux du common (incompatibles avec MVC)
 * Le emailing-service utilise Spring MVC, donc il doit utiliser WebMvcSecurityConfig
 */
@SpringBootApplication(exclude = {WebFluxAutoConfiguration.class})
@ComponentScan(
    basePackages = {"com.realestate.emailing", "com.realestate.common"},
    excludeFilters = {
        // Exclure les configurations WebFlux du common (incompatibles avec MVC)
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = {
                com.realestate.common.config.GatewaySecurityConfig.class,
                com.realestate.common.config.GatewayCorsConfig.class
            }
        )
    }
)
public class EmailingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmailingServiceApplication.class, args);
    }
}

