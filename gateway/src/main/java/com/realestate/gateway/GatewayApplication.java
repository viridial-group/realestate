package com.realestate.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(
    basePackages = {"com.realestate.gateway", "com.realestate.common"},
    excludeFilters = {
        // Exclure les configurations WebMVC du common (incompatibles avec WebFlux)
        // WebMvcSecurityConfig et CorsConfig utilisent jakarta.servlet.* (WebMVC)
        // GlobalExceptionHandler utilise WebRequest (WebMVC) au lieu de ServerWebExchange (WebFlux)
        // Le gateway utilise WebFlux, donc il doit utiliser GatewayCorsConfig et GatewaySecurityConfig
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = {
                com.realestate.common.config.GatewaySecurityConfig.class,
                com.realestate.common.config.WebMvcSecurityConfig.class,
                com.realestate.common.config.CorsConfig.class,
                com.realestate.common.exception.GlobalExceptionHandler.class
            }
        )
    }
)
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}

