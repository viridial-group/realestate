# 📖 Guide d'Utilisation - Common Library

## 🎯 Objectif

Le module `common` fournit des configurations réutilisables pour tous les microservices :
- ✅ Configuration de sécurité (WebFlux pour Gateway, WebMVC pour services)
- ✅ Configuration Redis
- ✅ Auto-configuration automatique

## 📦 Ajout au Projet

### 1. Ajouter la dépendance dans `pom.xml`

```xml
<dependency>
    <groupId>com.realestate</groupId>
    <artifactId>common</artifactId>
    <version>${project.version}</version>
</dependency>
```

### 2. Configuration automatique

Les configurations sont activées automatiquement selon le type de service :

- **Gateway (WebFlux)** : `GatewaySecurityConfig` est activée
- **Services MVC** : `WebMvcSecurityConfig` est activée
- **Redis** : `RedisConfig` est activée si Redis est configuré

## 🔧 Configuration Redis

### Variables d'environnement

```properties
# Production
SPRING_DATA_REDIS_HOST=148.230.112.148
SPRING_DATA_REDIS_PORT=6379
SPRING_DATA_REDIS_PASSWORD=Abcd@1984
SPRING_DATA_REDIS_DATABASE=0
```

### Utilisation dans le code

```java
@Autowired
private RedisTemplate<String, Object> redisTemplate;

// Sauvegarder
redisTemplate.opsForValue().set("key", value);

// Récupérer
Object value = redisTemplate.opsForValue().get("key");
```

## 🔒 Personnalisation de la Sécurité

Si vous devez personnaliser la configuration de sécurité, créez votre propre `@Configuration` qui surcharge celle du module common.

### Exemple pour un service MVC

```java
@Configuration
@EnableWebSecurity
public class CustomSecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Votre configuration personnalisée
        return http.build();
    }
}
```

## ✅ Services Utilisant Common

- ✅ **Gateway** - Utilise `GatewaySecurityConfig`
- ⏳ **Identity Service** - Utilisera `WebMvcSecurityConfig` et `RedisConfig`
- ⏳ **Organization Service** - Utilisera `WebMvcSecurityConfig` et `RedisConfig`
- ⏳ **Property Service** - Utilisera `WebMvcSecurityConfig` et `RedisConfig`
- ⏳ **Autres services** - Utiliseront les configurations communes

## 📝 Notes

- Les configurations sont activées automatiquement via `@Configuration`
- Vous pouvez désactiver une configuration en créant votre propre bean
- Redis est optionnel - la configuration n'est activée que si Redis est disponible

