# 🔗 Intégrations Inter-Services

**Date de création :** Décembre 2024  
**Statut :** ✅ Implémenté

---

## 📋 Vue d'ensemble

Ce document décrit les intégrations REST synchrones entre les microservices, complémentaires à la communication asynchrone via Kafka.

---

## 🏗️ Architecture

### Communication Synchrone (REST)
- **WebClient** (Spring WebFlux) pour les appels HTTP réactifs
- **Resilience4j** pour les circuit breakers et retry
- **Configuration centralisée** dans le module `common`

### Communication Asynchrone (Kafka)
- Déjà implémentée (voir `PHASE-2-ROADMAP.md`)

---

## 🔧 Composants Créés

### 1. Module Common - Clients REST

#### DTOs
- `UserInfoDTO` : Informations utilisateur depuis Identity Service
- `PermissionCheckDTO` : Résultat de vérification de permission
- `DomainInfoDTO` : Informations de domaine depuis Resource Service

#### Clients
- `IdentityServiceClient` : Client pour Identity Service
  - `getUserById(userId, authToken)` : Obtenir les infos d'un utilisateur
  - `getUserByEmail(email, authToken)` : Obtenir les infos par email
  - `checkPermission(userId, permission, resourceType, resourceId, authToken)` : Vérifier une permission

- `ResourceServiceClient` : Client pour Resource Service
  - `getDomainById(domainId, authToken)` : Obtenir les infos d'un domaine
  - `validateDomain(domainId, organizationId, authToken)` : Valider un domaine

#### Configuration
- `Resilience4jConfig` : Configuration des circuit breakers et retry
  - Circuit Breaker : 50% failure rate, 30s wait, 10 calls window
  - Retry : 3 tentatives, 500ms entre chaque

---

## 🔌 Intégrations Implémentées

### 1. Property Service → Identity Service

**Objectif :** Vérifier les permissions avant de créer une propriété

**Méthode :** `PropertyService.createProperty(property, authToken)`

**Validation :**
- Vérifie que l'utilisateur a la permission `property:create`
- Utilise `IdentityServiceClient.checkPermission()`
- Circuit breaker en cas d'erreur (fail closed - refuse l'accès)

**Configuration :**
```yaml
services:
  identity:
    url: ${IDENTITY_SERVICE_URL:http://localhost:8081}
```

---

### 2. Emailing Service → Identity Service

**Objectif :** Obtenir l'email d'un utilisateur pour envoyer des notifications

**Méthode :** `EmailEventConsumer.getUserEmail(userId)`

**Utilisation :**
- Appelé depuis les consumers Kafka (`handleWorkflowTaskCreated`, `handleWorkflowTaskCompleted`)
- Récupère l'email de l'utilisateur assigné/complété
- Utilise `IdentityServiceClient.getUserById()`

**Configuration :**
```yaml
services:
  identity:
    url: ${IDENTITY_SERVICE_URL:http://localhost:8081}
  internal:
    auth-token: ${SERVICE_AUTH_TOKEN:} # Token interne pour appels inter-services
```

---

## ⚙️ Configuration Resilience4j

### Circuit Breaker

**Configuration par défaut :**
- **Failure Rate Threshold :** 50%
- **Wait Duration in Open State :** 30 secondes
- **Sliding Window Size :** 10 appels
- **Minimum Number of Calls :** 5 appels
- **Permitted Calls in Half-Open :** 3 appels

**Comportement :**
- Circuit ouvert après 50% d'échecs
- Attente de 30s avant de réessayer
- Passe en half-open après 30s
- Se ferme si les 3 appels suivants réussissent

### Retry

**Configuration par défaut :**
- **Max Attempts :** 3 tentatives
- **Wait Duration :** 500ms entre chaque tentative
- **Retry On :** ConnectException, TimeoutException, 5xx errors

---

## 📝 Exemple d'Utilisation

### Dans Property Service

```java
@Service
public class PropertyService {
    private final IdentityServiceClient identityServiceClient;
    
    @Transactional
    public Property createProperty(Property property, String authToken) {
        // Vérifier les permissions
        if (authToken != null && property.getCreatedBy() != null) {
            Boolean hasPermission = identityServiceClient
                .checkPermission(
                    property.getCreatedBy(),
                    "property:create",
                    "Property",
                    null,
                    authToken
                )
                .block();
            
            if (hasPermission == null || !hasPermission) {
                throw new RuntimeException("Permission denied");
            }
        }
        
        // Créer la propriété...
    }
}
```

### Dans Emailing Service

```java
@Component
public class EmailEventConsumer {
    private final IdentityServiceClient identityServiceClient;
    
    private String getUserEmail(Long userId) {
        Optional<UserInfoDTO> userOpt = identityServiceClient
            .getUserById(userId, serviceAuthToken)
            .block();
        
        return userOpt.map(UserInfoDTO::getEmail).orElse(null);
    }
}
```

---

## 🔐 Authentification Inter-Services

### Option 1 : Service Token (Recommandé)
- Token interne partagé entre services
- Configuré via `services.internal.auth-token`
- Utilisé pour les appels inter-services uniquement

### Option 2 : JWT Token Utilisateur
- Token JWT de l'utilisateur authentifié
- Passé depuis le controller via `@RequestHeader("Authorization")`
- Utilisé pour les validations de permissions

---

## 🚀 Prochaines Étapes

### À Implémenter

1. **Workflow Service → Identity Service**
   - Obtenir les infos utilisateur pour les assignations de tâches
   - Vérifier les permissions pour les workflows

2. **Notification Service → Identity Service**
   - Obtenir les préférences de notification des utilisateurs
   - Obtenir les emails pour les notifications email

3. **Document Service → Identity Service**
   - Vérifier les permissions avant upload/download
   - Vérifier les ACL pour les documents partagés

4. **Service Discovery**
   - Implémenter Eureka ou Consul pour la découverte automatique
   - Remplacer les URLs hardcodées par des noms de services

5. **Load Balancing**
   - Ajouter un load balancer pour les appels inter-services
   - Utiliser Spring Cloud LoadBalancer

---

## 📊 Monitoring

### Métriques Resilience4j

Les métriques sont exposées via Actuator :
- `/actuator/metrics/resilience4j.circuitbreaker.calls`
- `/actuator/metrics/resilience4j.retry.calls`

### Logs

Tous les appels sont loggés avec :
- Succès/échec
- Temps de réponse
- Circuit breaker state changes
- Retry attempts

---

## 🔍 Dépannage

### Circuit Breaker Ouvert

**Symptôme :** Tous les appels échouent immédiatement

**Solution :**
1. Vérifier que le service cible est démarré
2. Vérifier la connectivité réseau
3. Attendre 30s pour que le circuit passe en half-open
4. Vérifier les logs pour identifier la cause initiale

### Timeout Errors

**Symptôme :** `TimeoutException` dans les logs

**Solution :**
1. Augmenter le timeout dans `WebClient` (actuellement 5s)
2. Vérifier la performance du service cible
3. Vérifier la charge réseau

---

## 📚 Références

- [Spring WebClient Documentation](https://docs.spring.io/spring-framework/reference/web/webflux-webclient.html)
- [Resilience4j Documentation](https://resilience4j.readme.io/)
- [Circuit Breaker Pattern](https://martinfowler.com/bliki/CircuitBreaker.html)

