package com.realestate.property.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration du cache Redis pour optimiser les performances
 * 
 * Caches configurés :
 * - publicProperties : Cache pour les listes de propriétés publiques (TTL: 5 minutes)
 * - publicProperty : Cache pour les détails d'une propriété (TTL: 10 minutes)
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Crée un ObjectMapper configuré pour Redis avec support des types Java 8 date/time
     */
    private ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.activateDefaultTyping(
                mapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL,
                com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY
        );
        return mapper;
    }

    /**
     * Crée un serializer JSON pour Redis avec support des types Java 8 date/time
     */
    private GenericJackson2JsonRedisSerializer createJsonRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer(createObjectMapper());
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        GenericJackson2JsonRedisSerializer jsonSerializer = createJsonRedisSerializer();
        
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(5)) // TTL par défaut : 5 minutes
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        jsonSerializer))
                .disableCachingNullValues();

        // Configuration spécifique pour le cache des propriétés individuelles (plus long)
        RedisCacheConfiguration propertyConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10)) // TTL : 10 minutes pour les détails
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        jsonSerializer))
                .disableCachingNullValues();

        // Configuration pour le cache des villes (TTL plus long car les villes changent rarement)
        GenericJackson2JsonRedisSerializer citiesJsonSerializer = createJsonRedisSerializer();
        RedisCacheConfiguration citiesConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30)) // TTL : 30 minutes pour les villes
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        citiesJsonSerializer))
                .disableCachingNullValues();
        
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        cacheConfigurations.put("publicProperty", propertyConfig);
        cacheConfigurations.put("publicProperties", defaultConfig); // Réactivé avec PagedPropertyResponse
        cacheConfigurations.put("availableCities", citiesConfig); // Cache pour les villes
        
        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}

