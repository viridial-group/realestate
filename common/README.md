# 📚 Common Library

Bibliothèque commune pour partager les configurations et utilitaires entre tous les microservices.

## 📦 Contenu

### Configurations

1. **GatewaySecurityConfig** - Configuration de sécurité pour Spring Cloud Gateway (WebFlux)
   - Accès public aux endpoints `/actuator/**`
   - Accès public aux routes `/api/**` (sera sécurisé avec JWT plus tard)
   - CSRF désactivé
   - Activée automatiquement si Spring Cloud Gateway est présent

2. **WebMvcSecurityConfig** - Configuration de sécurité pour les services Spring MVC
   - Accès public aux endpoints `/actuator/**`
   - Accès public aux routes `/api/**` (sera sécurisé avec JWT plus tard)
   - Sessions stateless
   - CSRF désactivé
   - Activée automatiquement si Spring MVC est présent

3. **RedisConfig** - Configuration Redis commune
   - Configuration automatique via variables d'environnement
   - Sérialisation JSON pour les valeurs
   - Support du mot de passe Redis
   - Activée automatiquement si Redis est configuré

## 🔧 Utilisation

### Dans le Gateway (WebFlux)

Le `GatewaySecurityConfig` est automatiquement activé si le module `common` est inclus.

### Dans les autres services (WebMVC)

Le `WebMvcSecurityConfig` est automatiquement activé si le module `common` est inclus.

### Configuration Redis

La configuration Redis est automatiquement activée. Utilisez les variables d'environnement :

```properties
spring.data.redis.host=148.230.112.148
spring.data.redis.port=6379
spring.data.redis.password=Abcd@1984
spring.data.redis.database=0
```

## 📝 Exemple d'utilisation

### Dans pom.xml d'un service

```xml
<dependency>
    <groupId>com.realestate</groupId>
    <artifactId>common</artifactId>
    <version>${project.version}</version>
</dependency>
```

### Injection de RedisTemplate

```java
@Autowired
private RedisTemplate<String, Object> redisTemplate;

public void saveToCache(String key, Object value) {
    redisTemplate.opsForValue().set(key, value);
}
```

## 🎯 Avantages

- ✅ Évite la duplication de code
- ✅ Configuration centralisée et cohérente
- ✅ Facilite la maintenance
- ✅ Réutilisable par tous les microservices

