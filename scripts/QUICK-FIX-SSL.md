# 🔒 Guide Rapide - Configuration SSL

## ❌ Problème Actuel

Vous essayez d'accéder à `https://api.viridial.com` mais obtenez une erreur de connexion refusée.

**Raison :** SSL n'est pas encore configuré. Nginx écoute seulement sur le port 80 (HTTP).

## ✅ Solution Immédiate : Tester en HTTP

Pour tester maintenant, utilisez **HTTP** au lieu de HTTPS :

```bash
# Dans Postman ou navigateur
http://api.viridial.com/actuator/health

# Ou avec curl
curl http://api.viridial.com/actuator/health
```

## 🔒 Configuration SSL (HTTPS)

### Étape 1 : Vérifier les DNS

```bash
cd /opt/source/realestate
./scripts/check-dns.sh
```

Les DNS doivent pointer vers `148.230.112.148` :
- `api.viridial.com` → `148.230.112.148`
- `app.viridial.com` → `148.230.112.148`

### Étape 2 : Configurer SSL avec Certbot

```bash
./scripts/setup-ssl.sh
```

Ce script va :
1. Vérifier que Nginx est configuré
2. Vérifier que les DNS sont corrects
3. Obtenir les certificats SSL de Let's Encrypt
4. Configurer automatiquement HTTPS dans Nginx

### Étape 3 : Vérifier

```bash
# Test HTTPS
curl https://api.viridial.com/actuator/health

# Diagnostic complet
./scripts/diagnose-connection.sh
```

## 🔍 Diagnostic

Si vous avez des problèmes, exécutez :

```bash
./scripts/diagnose-connection.sh
```

Ce script vérifie :
- ✅ Gateway local
- ✅ Nginx HTTP
- ✅ Nginx HTTPS
- ✅ Ports ouverts
- ✅ DNS
- ✅ Firewall

## 📝 Notes

- **HTTP fonctionne maintenant** : Utilisez `http://` pour tester
- **HTTPS nécessite SSL** : Exécutez `setup-ssl.sh` après avoir configuré les DNS
- **Let's Encrypt est gratuit** : Les certificats SSL sont gratuits et renouvelés automatiquement

