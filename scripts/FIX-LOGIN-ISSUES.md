# 🔧 Solutions pour les problèmes de login

## Problème 1 : Popup d'authentification HTTP Basic sur le VPS

Le popup d'authentification peut venir de plusieurs sources :

### Solution 1 : Vérifier la configuration Nginx sur le serveur

```bash
# Sur le VPS, vérifier s'il y a une config auth_basic
grep -r "auth_basic" /etc/nginx/

# Vérifier aussi dans les includes
grep -r "auth_basic" /etc/nginx/conf.d/
grep -r "auth_basic" /etc/nginx/sites-enabled/

# Si trouvé, commenter ou supprimer les lignes dans le fichier concerné :
# auth_basic "Restricted";
# auth_basic_user_file /etc/nginx/.htpasswd;

# Puis recharger nginx
nginx -t  # Vérifier la config
systemctl reload nginx
```

### Solution 2 : Vérifier si Actuator demande une authentification

```bash
# Tester Actuator directement
curl -v http://api.viridial.com/actuator/health

# Si vous voyez "WWW-Authenticate" dans les headers, c'est le problème
# Vérifier les logs
journalctl -u gateway-service -f
journalctl -u identity-service -f
```

### Solution 3 : Vérifier les variables d'environnement Spring Boot

```bash
# Vérifier les variables d'environnement des services
systemctl show gateway-service | grep Environment
systemctl show identity-service | grep Environment

# Vérifier les fichiers .env ou application.properties
grep -r "management.security" /opt/source/realestate/
```

## Problème 2 : Erreur CSRF dans Postman

**Important :** CSRF est désactivé dans le code, mais Spring Cloud Gateway (WebFlux) peut parfois avoir des comportements différents.

### Configuration Postman correcte

**Endpoint :** `POST http://api.viridial.com/api/identity/auth/login`

**Méthode :** POST

**Headers :**
```
Content-Type: application/json
Accept: application/json
```

**Body (raw JSON) :**
```json
{
  "email": "votre-email@example.com",
  "password": "votre-mot-de-passe"
}
```

### Solutions si l'erreur CSRF persiste

#### Solution 1 : Utiliser l'endpoint directement (bypass gateway)

Si le gateway pose problème, testez directement le service identity :

```bash
# Sur le VPS, tester directement le service identity
curl -X POST http://localhost:8081/api/identity/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'
```

Si ça fonctionne directement mais pas via le gateway, le problème vient du gateway.

#### Solution 2 : Vérifier que le gateway est bien redémarré

```bash
# Le service s'appelle "realestate-gateway" et non "gateway-service"
# Vérifier le statut
systemctl status realestate-gateway

# Redémarrer le gateway
systemctl restart realestate-gateway

# Attendre quelques secondes
sleep 5

# Vérifier qu'il est bien démarré
systemctl status realestate-gateway

# Vérifier les logs au démarrage
journalctl -u realestate-gateway -n 50
```

#### Solution 3 : Vérifier la configuration Spring Cloud Gateway

Le problème peut venir du fait que Spring Cloud Gateway (WebFlux) gère CSRF différemment. Vérifier que la config est bien appliquée :

```bash
# Vérifier que le fichier GatewaySecurityConfig.java existe et est compilé
ls -la /opt/source/realestate/gateway/target/classes/com/realestate/gateway/config/

# Recompiler le gateway si nécessaire
cd /opt/source/realestate/gateway
mvn clean package -DskipTests
systemctl restart gateway-service
```

#### Solution 4 : Ajouter des headers dans Postman

Dans Postman, ajoutez ces headers supplémentaires :

```
Content-Type: application/json
Accept: application/json
X-Requested-With: XMLHttpRequest
Origin: http://viridial.com
```

### Test rapide avec curl

```bash
# Test via le gateway
curl -v -X POST http://api.viridial.com/api/identity/auth/login \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'

# Si ça ne fonctionne pas, tester directement le service
curl -v -X POST http://localhost:8081/api/identity/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'
```

## Diagnostic complet

### 1. Vérifier que tous les services sont démarrés

```bash
cd /opt/source/realestate
./scripts/check-services.sh
```

### 2. Vérifier les logs en temps réel

```bash
# Gateway (port 8080)
journalctl -u realestate-gateway -f

# Identity Service (port 8081)
journalctl -u realestate-identity-service -f

# Nginx
tail -f /var/log/nginx/api.viridial.com.error.log
```

### 3. Tester chaque étape

```bash
# 1. Test Nginx
curl -v http://api.viridial.com/actuator/health

# 2. Test Gateway directement
curl -v http://localhost:8080/actuator/health

# 3. Test Identity Service directement
curl -v http://localhost:8081/actuator/health

# 4. Test login via Gateway
curl -v -X POST http://localhost:8080/api/identity/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test","password":"test"}'

# 5. Test login directement Identity Service
curl -v -X POST http://localhost:8081/api/identity/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test","password":"test"}'
```

## Solution de contournement temporaire

Si le problème persiste, vous pouvez temporairement accéder directement au service identity :

1. Modifier Nginx pour ajouter une route directe (temporaire) :
```nginx
location /api/identity-direct/ {
    proxy_pass http://localhost:8081/api/identity/;
    # ... autres headers proxy
}
```

2. Utiliser cette route dans Postman : `http://api.viridial.com/api/identity-direct/auth/login`

## Notes importantes

- Le gateway route `/api/identity/**` vers `http://localhost:8081`
- CSRF est désactivé dans `GatewaySecurityConfig` avec `@Primary` et `@Order(-1)`
- Le service identity a aussi CSRF désactivé dans `SecurityConfig`
- Si curl fonctionne mais Postman non, c'est probablement un problème de headers ou de configuration Postman

