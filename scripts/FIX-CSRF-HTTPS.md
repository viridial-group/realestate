# 🔧 Fix CSRF Error avec HTTPS

## Problème
Erreur CSRF `An expected CSRF token cannot be found` même avec HTTPS activé.

## Cause
Avec HTTPS, Spring Security peut avoir des comportements différents concernant CSRF, surtout quand l'application est derrière un reverse proxy (Nginx).

## Solutions appliquées

### 1. Configuration Java (GatewaySecurityConfig.java)
✅ CSRF désactivé explicitement dans le code Java
✅ Headers de sécurité désactivés pour éviter les conflits

### 2. Configuration YAML (application-prod.yml)
✅ Ajout de `spring.security.csrf.enabled: false` pour forcer la désactivation

### 3. Configuration Nginx
✅ Headers `X-Forwarded-Proto`, `X-Forwarded-Port`, `X-Forwarded-Host` configurés

## Actions à effectuer sur le VPS

### 1. Recompiler et redéployer le gateway

```bash
cd /opt/source/realestate

# Recompiler le gateway
cd gateway
mvn clean package -DskipTests

# Copier le nouveau JAR
cp target/gateway-*.jar /var/realestate/bin/gateway.jar

# Redémarrer le gateway
systemctl restart realestate-gateway

# Vérifier les logs
journalctl -u realestate-gateway -f
```

### 2. Mettre à jour la configuration Nginx

```bash
# Copier la nouvelle config
cp /opt/source/realestate/config/nginx/api.viridial.com.conf /etc/nginx/sites-available/api.viridial.com.conf

# Vérifier la config
nginx -t

# Recharger Nginx
systemctl reload nginx
```

### 3. Tester avec HTTPS

```bash
# Tester le login via HTTPS
curl -X POST https://api.viridial.com/api/identity/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"test123"}'

# Si vous avez un certificat auto-signé, ajouter -k
curl -k -X POST https://api.viridial.com/api/identity/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"test123"}'
```

### 4. Vérifier que les headers sont bien passés

```bash
# Vérifier les logs du gateway pour voir les headers reçus
journalctl -u realestate-gateway -n 50 | grep -i forwarded
```

## Vérifications

### 1. Vérifier que CSRF est bien désactivé

Dans les logs du gateway au démarrage, vous devriez voir que CSRF est désactivé.

### 2. Vérifier la configuration Spring Security

```bash
# Vérifier que le fichier GatewaySecurityConfig.java est bien compilé
ls -la /opt/source/realestate/gateway/target/classes/com/realestate/gateway/config/GatewaySecurityConfig.class
```

### 3. Tester directement le service identity (bypass gateway)

```bash
# Si le problème persiste, tester directement
curl -X POST http://localhost:8081/api/identity/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"test123"}'
```

Si ça fonctionne directement mais pas via le gateway, le problème vient du gateway.

## Configuration Postman avec HTTPS

Dans Postman, pour tester avec HTTPS :

1. **URL :** `POST https://api.viridial.com/api/identity/auth/login`
2. **Settings → SSL certificate verification :** Désactiver si certificat auto-signé
3. **Headers :**
   ```
   Content-Type: application/json
   Accept: application/json
   ```

## Notes importantes

- Avec HTTPS, Spring Security peut être plus strict sur CSRF
- Les headers `X-Forwarded-*` sont essentiels pour que Spring Boot sache qu'il est derrière HTTPS
- La configuration `spring.security.csrf.enabled: false` dans YAML force la désactivation même si le code Java ne suffit pas

## Si le problème persiste

1. Vérifier les logs du gateway : `journalctl -u realestate-gateway -f`
2. Vérifier les logs Nginx : `tail -f /var/log/nginx/api.viridial.com.error.log`
3. Tester avec curl en ajoutant `-v` pour voir les headers complets
4. Vérifier que le certificat SSL est valide et que Nginx le charge correctement

