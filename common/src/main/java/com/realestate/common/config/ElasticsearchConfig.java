package com.realestate.common.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * Configuration Elasticsearch
 * Activée uniquement si elasticsearch est configuré
 * 
 * Spring Boot 3.3.1 auto-configures ElasticsearchClient based on spring.elasticsearch.uris
 * No need to extend AbstractElasticsearchConfiguration in newer versions
 */
@Configuration
@ConditionalOnProperty(name = "spring.elasticsearch.uris")
@EnableElasticsearchRepositories(basePackages = "com.realestate.**.repository.elasticsearch")
public class ElasticsearchConfig {
    // Spring Boot auto-configuration handles the ElasticsearchClient
    // based on spring.elasticsearch.uris property
}

