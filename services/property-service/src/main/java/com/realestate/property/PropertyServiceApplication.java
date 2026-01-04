package com.realestate.property;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Application principale du Property Service
 * 
 * Désactive l'auto-configuration Kafka pour éviter les erreurs de compatibilité de versions.
 * Kafka sera configuré manuellement via common/KafkaConfig si nécessaire.
 * 
 * Le bean overriding est activé dans application.yml pour permettre au SecurityConfig
 * du property-service de surcharger celui du common.
 */
@SpringBootApplication(exclude = {KafkaAutoConfiguration.class})
@ComponentScan(basePackages = {"com.realestate.property", "com.realestate.common"})
@EnableScheduling
@EnableAsync
public class PropertyServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PropertyServiceApplication.class, args);
    }
}

