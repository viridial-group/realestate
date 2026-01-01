package com.realestate.identity.config;

import com.realestate.identity.filter.JwtAuthenticationFilter;
import com.realestate.identity.filter.RemoveWwwAuthenticateFilter;
import com.realestate.identity.filter.RolePermissionFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<RemoveWwwAuthenticateFilter> removeWwwAuthenticateFilterRegistration(
            RemoveWwwAuthenticateFilter filter) {
        FilterRegistrationBean<RemoveWwwAuthenticateFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        registration.addUrlPatterns("/*");
        return registration;
    }

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtAuthenticationFilterRegistration(
            JwtAuthenticationFilter filter) {
        FilterRegistrationBean<JwtAuthenticationFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
        registration.addUrlPatterns("/*");
        return registration;
    }

    @Bean
    public FilterRegistrationBean<RolePermissionFilter> rolePermissionFilterRegistration(
            RolePermissionFilter filter) {
        FilterRegistrationBean<RolePermissionFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 2);
        registration.addUrlPatterns("/*");
        return registration;
    }
}

