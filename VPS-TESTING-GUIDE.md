# 🧪 Guide de Test sur VPS

**Date de création :** Décembre 2024  
**Environnement :** VPS (Production/Staging)

---

## 📋 Prérequis

Avant de tester, vérifiez que tous les services sont démarrés :

```bash
# Vérifier l'état de tous les services
./scripts/check-vps-services.sh

# Ou utiliser le script de statut
./scripts/status-all-services.sh
```

---

## 1️⃣ Vérification Rapide

### 1.1 Vérifier que les services répondent

```bash
# Health checks
curl http://localhost:8080/actuator/health  # Gateway
curl http://localhost:8081/actuator/health  # Identity Service
curl http://localhost:8083/actuator/health  # Property Service
```

### 1.2 Vérifier via le Gateway (si configuré)

```bash
# Via le domaine (si DNS configuré)
curl https://api.viridial.com/actuator/health

# Ou via IP
curl http://<VPS_IP>:8080/actuator/health
```

---

## 2️⃣ Test de l'Authentification

### 2.1 Inscription

```bash
curl -X POST http://localhost:8081/api/identity/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "Test123!",
    "firstName": "Test",
    "lastName": "User"
  }'
```

### 2.2 Login

```bash
curl -X POST http://localhost:8081/api/identity/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "Test123!"
  }'
```

**Sauvegardez le token :**
```bash
export JWT_TOKEN="votre_token_ici"
```

---

## 3️⃣ Test via le Gateway (Production)

Si le Gateway est configuré avec Nginx et DNS :

### 3.1 Test d'authentification

```bash
# Via le domaine
curl -X POST https://api.viridial.com/api/identity/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "Test123!"
  }'
```

### 3.2 Test de création de propriété

```bash
curl -X POST https://api.viridial.com/api/properties \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -d '{
    "reference": "PROP-001",
    "title": "Appartement T3",
    "type": "APARTMENT",
    "status": "DRAFT",
    "price": 350000,
    "organizationId": 1,
    "createdBy": 1
  }'
```

---

## 4️⃣ Vérification des Logs

### 4.1 Voir les logs d'un service

```bash
# Logs en temps réel
tail -f /opt/source/realestate/logs/identity-service.log

# Dernières 50 lignes
tail -50 /opt/source/realestate/logs/property-service.log

# Rechercher des erreurs
grep -i error /opt/source/realestate/logs/*.log
```

### 4.2 Vérifier les PIDs

```bash
# Lister tous les PIDs
cat /opt/source/realestate/logs/*.pid

# Vérifier qu'un processus est actif
ps -p $(cat /opt/source/realestate/logs/gateway.pid)
```

---

## 5️⃣ Test d'Elasticsearch

### 5.1 Vérifier Elasticsearch

```bash
# Si Elasticsearch est sur le VPS
curl http://localhost:9200

# Lister les indexes
curl "http://localhost:9200/_cat/indices?v"
```

### 5.2 Recherche via l'API

```bash
curl -X GET "http://localhost:8083/api/properties/search/text?q=appartement" \
  -H "Authorization: Bearer $JWT_TOKEN"
```

---

## 6️⃣ Test de Kafka

### 6.1 Vérifier Kafka

```bash
# Si Kafka est en Docker
docker ps | grep kafka

# Lister les topics
docker exec -it realestate-kafka kafka-topics --bootstrap-server localhost:9092 --list
```

### 6.2 Vérifier les événements

Après avoir créé une propriété, vérifiez que l'événement est publié :

```bash
# Voir les logs du Property Service
tail -f /opt/source/realestate/logs/property-service.log | grep "Published"
```

---

## 7️⃣ Commandes Utiles sur VPS

### 7.1 Redémarrer un service spécifique

```bash
# Arrêter un service
kill $(cat /opt/source/realestate/logs/gateway.pid)

# Redémarrer
cd /opt/source/realestate
nohup java -jar gateway/target/gateway-*.jar > logs/gateway.log 2>&1 &
echo $! > logs/gateway.pid
```

### 7.2 Vérifier l'utilisation des ports

```bash
# Voir tous les ports utilisés
netstat -tuln | grep LISTEN

# Voir quel processus utilise un port
lsof -i :8080
```

### 7.3 Vérifier l'utilisation mémoire/CPU

```bash
# Voir les processus Java
ps aux | grep java

# Utilisation mémoire
free -h

# Utilisation disque
df -h
```

---

## 8️⃣ Dépannage

### 8.1 Service ne démarre pas

```bash
# 1. Vérifier les logs
tail -100 /opt/source/realestate/logs/<service-name>.log

# 2. Vérifier le port
lsof -i :<port>

# 3. Vérifier les dépendances (PostgreSQL, Redis)
psql -h localhost -U postgres -d realestate_db -c "SELECT 1;"
redis-cli ping
```

### 8.2 Health check DOWN

```bash
# Vérifier les dépendances
./scripts/check-vps-services.sh

# Vérifier les logs
tail -50 /opt/source/realestate/logs/<service-name>.log | grep -i error
```

### 8.3 Port déjà utilisé

```bash
# Voir quel processus utilise le port
lsof -i :8080

# Arrêter le processus
kill <PID>

# Ou arrêter tous les services
./scripts/stop-all-services.sh
```

---

## 9️⃣ Test Automatisé sur VPS

Créez un script de test adapté au VPS :

```bash
#!/bin/bash
# test-vps.sh

echo "🧪 Tests sur VPS"
echo ""

# 1. Health checks
echo "1. Health checks..."
for port in 8080 8081 8082 8083; do
  status=$(curl -s http://localhost:$port/actuator/health | grep -o '"status":"[^"]*"' | cut -d'"' -f4 || echo "DOWN")
  echo "   Port $port: $status"
done

# 2. Test de login
echo ""
echo "2. Test de login..."
response=$(curl -s -X POST http://localhost:8081/api/identity/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@viridial.com","password":"admin123"}')

token=$(echo $response | grep -o '"token":"[^"]*"' | cut -d'"' -f4 || echo "")
if [ -n "$token" ]; then
  echo "   ✅ Login réussi"
  export JWT_TOKEN=$token
else
  echo "   ❌ Login échoué"
fi

# 3. Test de création de propriété
if [ -n "$JWT_TOKEN" ]; then
  echo ""
  echo "3. Test de création de propriété..."
  curl -s -X POST http://localhost:8083/api/properties \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $JWT_TOKEN" \
    -d '{
      "reference": "PROP-TEST-001",
      "title": "Test Property",
      "type": "APARTMENT",
      "status": "DRAFT",
      "price": 100000,
      "organizationId": 1,
      "createdBy": 1
    }' | head -20
fi

echo ""
echo "✅ Tests terminés"
```

---

## 🔟 Checklist de Test VPS

- [ ] Tous les services sont démarrés (via `check-vps-services.sh`)
- [ ] PostgreSQL est accessible
- [ ] Redis est accessible
- [ ] Kafka est démarré (si utilisé)
- [ ] Elasticsearch est démarré (si utilisé)
- [ ] Health checks retournent UP
- [ ] Login fonctionne
- [ ] Création de propriété fonctionne
- [ ] Gateway route correctement (si configuré)
- [ ] Logs ne contiennent pas d'erreurs critiques

---

## 📚 Ressources

- **Scripts disponibles :**
  - `./scripts/check-vps-services.sh` - Vérification complète
  - `./scripts/status-all-services.sh` - Statut des services
  - `./scripts/stop-all-services.sh` - Arrêter tous les services
  - `./scripts/build-and-start-all.sh` - Build et démarrage

- **Logs :** `/opt/source/realestate/logs/`

