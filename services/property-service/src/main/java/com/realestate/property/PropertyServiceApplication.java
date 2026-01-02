package com.realestate.property;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;

/**
 * Application principale du Property Service
 * 
 * Désactive l'auto-configuration Kafka pour éviter les erreurs de compatibilité de versions.
 * Kafka sera configuré manuellement via common/KafkaConfig si nécessaire.
 */
@SpringBootApplication(exclude = {KafkaAutoConfiguration.class})
public class PropertyServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PropertyServiceApplication.class, args);
    }
}

