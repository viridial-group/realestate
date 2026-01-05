# 🔧 Fix CSRF Error dans Gateway

## Problème
Erreur CSRF même avec curl : `An expected CSRF token cannot be found`

## Solution

### 1. Vérifier que les services sont démarrés

```bash
# Vérifier le statut des services
systemctl status realestate-gateway
systemctl status realestate-identity-service

# Vérifier les processus Java
ps aux | grep gateway
ps aux | grep identity
```

### 2. Redémarrer le gateway après modification

```bash
# Arrêter le gateway
systemctl stop realestate-gateway

# Recompiler le gateway (si vous avez modifié le code)
cd /opt/source/realestate/gateway
mvn clean package -DskipTests

# Copier le nouveau JAR
cp target/gateway-*.jar /var/realestate/bin/gateway.jar

# Redémarrer
systemctl start realestate-gateway

# Vérifier les logs
journalctl -u realestate-gateway -f
```

### 3. Tester directement le service identity (bypass gateway)

```bash
# Tester directement le service identity
curl -X POST http://localhost:8081/api/identity/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"test123"}'
```

Si ça fonctionne directement mais pas via le gateway, le problème vient du gateway.

### 4. Vérifier la configuration du gateway

Vérifier que `GatewaySecurityConfig.java` a bien CSRF désactivé :

```java
.csrf(csrf -> csrf.disable())
```

### 5. Vérifier les logs du gateway

```bash
# Voir les logs en temps réel
journalctl -u realestate-gateway -f

# Voir les dernières erreurs
journalctl -u realestate-gateway -n 100 | grep -i csrf
```

### 6. Solution alternative : Désactiver CSRF via application.yml

Si le problème persiste, ajouter dans `application-prod.yml` :

```yaml
spring:
  security:
    csrf:
      enabled: false
```

### 7. Vérifier que le gateway route correctement

```bash
# Tester le health check du gateway
curl http://localhost:8080/actuator/health

# Tester le health check de identity via gateway
curl http://api.viridial.com/actuator/health

# Tester le login via gateway (localhost)
curl -X POST http://localhost:8080/api/identity/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"test123"}'
```

## Diagnostic

Si l'erreur CSRF persiste :

1. **Vérifier la version de Spring Security** : Certaines versions ont des bugs avec CSRF dans WebFlux
2. **Vérifier les filtres actifs** : Le gateway ne doit pas avoir de filtres CSRF actifs
3. **Tester avec un client HTTP différent** : curl, Postman, httpie

## Commandes utiles

```bash
# Vérifier tous les services
./scripts/check-services.sh

# Voir les logs de tous les services
journalctl -u realestate-* -f

# Redémarrer tous les services
systemctl restart realestate-*

# Vérifier les ports utilisés
netstat -tlnp | grep -E '8080|8081'
```

