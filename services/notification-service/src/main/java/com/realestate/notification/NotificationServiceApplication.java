package com.realestate.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Application principale du Notification Service
 * 
 * Gère les notifications en temps réel, les abonnements aux notifications,
 * et la distribution multi-canal (in-app, push, SMS, email).
 * Le CorsConfig du module common est automatiquement chargé via @SpringBootApplication.
 * Le SecurityConfig local surcharge celui du common (bean overriding activé).
 */
@SpringBootApplication
public class NotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }
}

