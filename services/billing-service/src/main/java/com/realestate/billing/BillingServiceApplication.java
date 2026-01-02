package com.realestate.billing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Application principale du Billing Service
 * 
 * Gère la facturation, les abonnements, les plans et les factures.
 * Le CorsConfig du module common est automatiquement chargé via @SpringBootApplication.
 * Le SecurityConfig local surcharge celui du common (bean overriding activé).
 */
@SpringBootApplication
public class BillingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BillingServiceApplication.class, args);
    }
}

