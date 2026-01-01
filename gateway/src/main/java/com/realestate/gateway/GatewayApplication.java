package com.realestate.gateway;

import com.realestate.common.config.CommonAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication(exclude = {CommonAutoConfiguration.class})
@ComponentScan(
    basePackages = {"com.realestate.gateway", "com.realestate.common"},
    excludeFilters = {
        // Exclure la configuration de sécurité du common pour utiliser celle du gateway
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = {com.realestate.common.config.GatewaySecurityConfig.class}
        )
    }
)
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}

