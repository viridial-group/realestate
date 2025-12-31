# 🧪 Guide de Test - Real Estate Platform

**Date de création :** Décembre 2024

---

## 📋 Prérequis

Avant de commencer les tests, vérifiez que tous les services sont démarrés :

```bash
# Vérifier le statut de tous les services
./scripts/status-all-services.sh

# Vérifier Elasticsearch
./scripts/check-elasticsearch.sh

# Vérifier Kafka
./scripts/check-kafka.sh
```

---

## 1️⃣ Test des Services de Base

### 1.1 Vérifier que les services répondent

```bash
# Health checks
curl http://localhost:8081/actuator/health  # Identity Service
curl http://localhost:8082/actuator/health  # Organization Service
curl http://localhost:8083/actuator/health  # Property Service
curl http://localhost:8084/actuator/health  # Resource Service
curl http://localhost:8085/actuator/health  # Document Service
curl http://localhost:8086/actuator/health  # Workflow Service
curl http://localhost:8087/actuator/health  # Notification Service
curl http://localhost:8088/actuator/health  # Emailing Service
curl http://localhost:8089/actuator/health  # Audit Service
curl http://localhost:8090/actuator/health  # Billing Service
```

**Résultat attendu :** `{"status":"UP"}`

---

## 2️⃣ Test de l'Authentification (Identity Service)

### 2.1 Inscription d'un utilisateur

```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "Test123!",
    "firstName": "Test",
    "lastName": "User"
  }'
```

**Résultat attendu :** `201 Created` avec les informations de l'utilisateur

### 2.2 Connexion (Login)

```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "Test123!"
  }'
```

**Résultat attendu :** `200 OK` avec un token JWT

**Exemple de réponse :**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer"
}
```

**💡 Sauvegardez le token pour les prochains tests :**
```bash
export JWT_TOKEN="votre_token_ici"
```

### 2.3 Obtenir l'utilisateur actuel

```bash
curl -X GET http://localhost:8081/api/identity/users/me \
  -H "Authorization: Bearer $JWT_TOKEN"
```

**Résultat attendu :** `200 OK` avec les informations de l'utilisateur

---

## 3️⃣ Test avec Swagger UI

### 3.1 Accéder à Swagger

Ouvrez dans votre navigateur :

- **Identity Service :** http://localhost:8081/swagger-ui.html
- **Property Service :** http://localhost:8083/swagger-ui.html
- **Audit Service :** http://localhost:8089/swagger-ui.html
- **Tous les autres services :** Voir `SWAGGER-URLS.md`

### 3.2 Authentification dans Swagger

1. Cliquez sur le bouton **"Authorize"** (en haut à droite)
2. Entrez : `Bearer <votre_token_jwt>`
3. Cliquez sur **"Authorize"**
4. Testez les endpoints protégés

---

## 4️⃣ Test du Property Service

### 4.1 Créer une propriété

```bash
curl -X POST http://localhost:8083/api/properties \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -d '{
    "reference": "PROP-001",
    "title": "Appartement T3 Paris",
    "description": "Bel appartement de 75m² avec balcon",
    "type": "APARTMENT",
    "status": "DRAFT",
    "price": 350000,
    "currency": "EUR",
    "surface": 75,
    "rooms": 3,
    "bedrooms": 2,
    "bathrooms": 1,
    "address": "123 Rue de la République",
    "city": "Paris",
    "postalCode": "75001",
    "country": "France",
    "organizationId": 1,
    "createdBy": 1
  }'
```

**Résultat attendu :** `201 Created` avec les détails de la propriété

### 4.2 Lister les propriétés

```bash
curl -X GET "http://localhost:8083/api/properties?organizationId=1" \
  -H "Authorization: Bearer $JWT_TOKEN"
```

### 4.3 Rechercher une propriété par ID

```bash
curl -X GET http://localhost:8083/api/properties/1 \
  -H "Authorization: Bearer $JWT_TOKEN"
```

---

## 5️⃣ Test d'Elasticsearch

### 5.1 Vérifier qu'Elasticsearch fonctionne

```bash
curl http://localhost:9200
```

### 5.2 Lister les indexes

```bash
curl "http://localhost:9200/_cat/indices?v"
```

**Résultat attendu :** Vous devriez voir `properties` et `audit-logs` après avoir créé des données

### 5.3 Rechercher dans Elasticsearch (après création d'une property)

```bash
# Recherche full-text
curl -X GET "http://localhost:9200/properties/_search?pretty" \
  -H 'Content-Type: application/json' \
  -d '{
    "query": {
      "match": {
        "title": "appartement"
      }
    }
  }'
```

### 5.4 Recherche via l'API Property Service

```bash
curl -X GET "http://localhost:8083/api/properties/search/text?q=appartement&page=0&size=10" \
  -H "Authorization: Bearer $JWT_TOKEN"
```

---

## 6️⃣ Test de Kafka

### 6.1 Vérifier que Kafka fonctionne

```bash
./scripts/check-kafka.sh
```

### 6.2 Lister les topics

```bash
./scripts/list-kafka-topics.sh
```

**Résultat attendu :** Vous devriez voir :
- `property-created`
- `property-updated`
- `document-uploaded`
- `workflow-task-created`
- `workflow-task-completed`

### 6.3 Test du flux complet

1. **Créer une propriété** (voir section 4.1)
2. **Vérifier que l'événement est publié :**
   ```bash
   # Vérifier dans les logs
   tail -f logs/property-service.log | grep "Published"
   ```
3. **Vérifier que l'indexation Elasticsearch a eu lieu :**
   ```bash
   curl "http://localhost:9200/properties/_count?pretty"
   ```
4. **Vérifier que l'audit log a été créé :**
   ```bash
   curl -X GET "http://localhost:8089/api/audit/search/organization/1?page=0&size=10" \
     -H "Authorization: Bearer $JWT_TOKEN"
   ```

---

## 7️⃣ Test des Intégrations Inter-Services

### 7.1 Test de la vérification de permission (Property Service → Identity Service)

Lors de la création d'une propriété avec un token valide, le Property Service devrait vérifier les permissions via Identity Service.

**Vérifier dans les logs :**
```bash
tail -f logs/property-service.log | grep "permission"
```

### 7.2 Test de la récupération d'email (Emailing Service → Identity Service)

Quand un workflow task est créé, l'Emailing Service devrait récupérer l'email de l'utilisateur.

**Créer un workflow task** (via Workflow Service) et vérifier les logs :
```bash
tail -f logs/emailing-service.log | grep "user email"
```

---

## 8️⃣ Test du Workflow Service

### 8.1 Créer un workflow

```bash
curl -X POST http://localhost:8086/api/workflows \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -d '{
    "targetType": "Property",
    "targetId": 1,
    "organizationId": 1,
    "createdBy": 1,
    "status": "PENDING"
  }'
```

### 8.2 Créer une tâche

```bash
curl -X POST http://localhost:8086/api/tasks \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -d '{
    "workflowId": 1,
    "type": "REVIEW",
    "status": "PENDING",
    "assignedTo": 1,
    "priority": "HIGH"
  }'
```

---

## 9️⃣ Test de l'Audit Service

### 9.1 Rechercher les logs d'audit

```bash
curl -X GET "http://localhost:8089/api/audit/search/organization/1?page=0&size=20" \
  -H "Authorization: Bearer $JWT_TOKEN"
```

### 9.2 Recherche full-text dans Elasticsearch

```bash
curl -X GET "http://localhost:8089/api/audit/search/text?q=CREATE&page=0&size=10" \
  -H "Authorization: Bearer $JWT_TOKEN"
```

---

## 🔟 Script de Test Automatisé

Créez un script de test complet :

```bash
#!/bin/bash
# test-all.sh

echo "🧪 Tests de la Plateforme Real Estate"
echo ""

# 1. Health checks
echo "1. Vérification des health checks..."
for port in 8081 8082 8083 8084 8085 8086 8087 8088 8089 8090; do
  status=$(curl -s http://localhost:$port/actuator/health | jq -r '.status' 2>/dev/null)
  if [ "$status" = "UP" ]; then
    echo "   ✅ Port $port: UP"
  else
    echo "   ❌ Port $port: DOWN"
  fi
done

# 2. Test d'inscription
echo ""
echo "2. Test d'inscription..."
response=$(curl -s -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "Test123!",
    "firstName": "Test",
    "lastName": "User"
  }')
echo "   Response: $response"

# 3. Test de login
echo ""
echo "3. Test de login..."
login_response=$(curl -s -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "Test123!"
  }')
token=$(echo $login_response | jq -r '.token' 2>/dev/null)
if [ -n "$token" ] && [ "$token" != "null" ]; then
  echo "   ✅ Login réussi, token obtenu"
  export JWT_TOKEN=$token
else
  echo "   ❌ Login échoué"
  exit 1
fi

# 4. Test de création de propriété
echo ""
echo "4. Test de création de propriété..."
property_response=$(curl -s -X POST http://localhost:8083/api/properties \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -d '{
    "reference": "PROP-TEST-001",
    "title": "Appartement Test",
    "type": "APARTMENT",
    "status": "DRAFT",
    "price": 100000,
    "organizationId": 1,
    "createdBy": 1
  }')
echo "   Response: $property_response"

# 5. Vérifier Elasticsearch
echo ""
echo "5. Vérification Elasticsearch..."
es_status=$(curl -s http://localhost:9200 | jq -r '.cluster_name' 2>/dev/null)
if [ -n "$es_status" ]; then
  echo "   ✅ Elasticsearch accessible"
  index_count=$(curl -s "http://localhost:9200/_cat/indices?v" | grep -c "properties" || echo "0")
  echo "   📊 Indexes properties: $index_count"
else
  echo "   ❌ Elasticsearch non accessible"
fi

echo ""
echo "✅ Tests terminés"
```

---

## 📊 Checklist de Test

- [ ] Tous les services répondent aux health checks
- [ ] Inscription d'utilisateur fonctionne
- [ ] Login et obtention du token JWT fonctionne
- [ ] Création de propriété fonctionne
- [ ] Recherche Elasticsearch fonctionne
- [ ] Événements Kafka sont publiés
- [ ] Indexation Elasticsearch automatique fonctionne
- [ ] Audit logs sont créés
- [ ] Swagger UI accessible pour tous les services
- [ ] Intégrations inter-services fonctionnent

---

## 🐛 Dépannage

### Service ne démarre pas
```bash
# Vérifier les logs
tail -50 logs/<service-name>.log

# Vérifier le port
lsof -i :<port>
```

### Elasticsearch ne répond pas
```bash
# Vérifier le conteneur Docker
docker ps | grep elasticsearch

# Redémarrer si nécessaire
./scripts/stop-elasticsearch.sh
./scripts/start-elasticsearch.sh
```

### Kafka ne fonctionne pas
```bash
# Vérifier les conteneurs
docker ps | grep kafka

# Redémarrer si nécessaire
./scripts/stop-kafka.sh
./scripts/start-kafka.sh
```

---

## 📚 Ressources

- **Swagger URLs :** `SWAGGER-URLS.md`
- **Elasticsearch Access :** `ELASTICSEARCH-ACCESS.md`
- **Inter-Service Integration :** `INTER-SERVICE-INTEGRATION.md`

