package com.realestate.common.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Auto-configuration pour les configurations communes
 * 
 * Active automatiquement :
 * - GatewaySecurityConfig si Spring Cloud Gateway est présent
 * - WebMvcSecurityConfig si Spring MVC est présent
 * - RedisConfig si Redis est configuré
 */
@Configuration
public class CommonAutoConfiguration {

    /**
     * Configuration de sécurité pour Gateway (WebFlux)
     * Activée uniquement si Spring Cloud Gateway est présent
     */
    @Configuration
    @ConditionalOnClass({EnableWebFluxSecurity.class, WebFluxConfigurer.class})
    @Import(GatewaySecurityConfig.class)
    static class GatewaySecurityAutoConfiguration {
    }

    /**
     * Configuration de sécurité pour services MVC
     * Activée uniquement si Spring MVC est présent
     */
    @Configuration
    @ConditionalOnClass({EnableWebSecurity.class, WebMvcConfigurer.class})
    @Import(WebMvcSecurityConfig.class)
    static class WebMvcSecurityAutoConfiguration {
    }

    /**
     * Configuration Redis - activée si Redis est disponible
     */
    @Configuration
    @ConditionalOnClass(RedisConnectionFactory.class)
    @Import(RedisConfig.class)
    static class RedisAutoConfiguration {
    }
}

