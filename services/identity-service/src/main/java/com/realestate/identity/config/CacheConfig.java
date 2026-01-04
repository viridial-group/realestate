package com.realestate.identity.config;

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
 * Configuration du cache Redis pour les paramètres SaaS
 * 
 * Caches configurés :
 * - countries : Cache pour la liste des pays actifs (TTL: 1 heure)
 * - country : Cache pour les détails d'un pays (TTL: 1 heure)
 * - cities : Cache pour la liste des villes actives (TTL: 1 heure)
 * - city : Cache pour les détails d'une ville (TTL: 1 heure)
 * - citiesByCountry : Cache pour les villes par pays (TTL: 1 heure)
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

        // Configuration pour les pays et villes (TTL plus long car changent rarement)
        RedisCacheConfiguration countryCityConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1)) // TTL : 1 heure pour les pays et villes
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        jsonSerializer))
                .disableCachingNullValues();
        
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        cacheConfigurations.put("countries", countryCityConfig);
        cacheConfigurations.put("country", countryCityConfig);
        cacheConfigurations.put("cities", countryCityConfig);
        cacheConfigurations.put("city", countryCityConfig);
        cacheConfigurations.put("citiesByCountry", countryCityConfig);
        
        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}

