# 🔍 Accès à Elasticsearch - Guide Complet

**Date de création :** Décembre 2024

---

## 📋 Options d'Accès

### 1. 🌐 Kibana (Recommandé) - Interface Graphique Officielle

**Kibana** est l'interface graphique officielle d'Elasticsearch, fournie par Elastic.

#### Démarrage
```bash
./scripts/start-kibana.sh
```

#### Accès
- **URL :** http://localhost:5601
- **Temps de démarrage :** ~60 secondes

#### Fonctionnalités
- **Dev Tools** : Console pour exécuter des requêtes Elasticsearch
- **Discover** : Exploration visuelle des données indexées
- **Dashboard** : Création de tableaux de bord personnalisés
- **Index Management** : Gestion des indexes (création, suppression, mapping)
- **Stack Monitoring** : Monitoring de l'état d'Elasticsearch

#### Exemple de requête dans Dev Tools
```json
GET /properties/_search
{
  "query": {
    "match_all": {}
  }
}
```

---

### 2. 🔧 API REST Directe (curl)

#### Vérifier le statut
```bash
curl http://localhost:9200
```

#### Lister tous les indexes
```bash
curl "http://localhost:9200/_cat/indices?v"
```

#### Rechercher dans les properties
```bash
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

#### Obtenir les informations d'un index
```bash
curl "http://localhost:9200/properties?pretty"
```

#### Compter les documents
```bash
curl "http://localhost:9200/properties/_count?pretty"
```

---

### 3. 🛠️ Postman / Insomnia

#### Configuration
- **Base URL :** `http://localhost:9200`
- **Headers :** `Content-Type: application/json`

#### Requêtes courantes

**GET - Statut du cluster**
```
GET http://localhost:9200/_cluster/health?pretty
```

**GET - Liste des indexes**
```
GET http://localhost:9200/_cat/indices?v
```

**POST - Recherche**
```
POST http://localhost:9200/properties/_search
Body (JSON):
{
  "query": {
    "match": {
      "city": "Paris"
    }
  }
}
```

---

### 4. 🌐 Elasticvue (Extension Navigateur)

**Elasticvue** est une extension Chrome/Firefox pour visualiser Elasticsearch.

#### Installation
- **Chrome :** [Chrome Web Store](https://chrome.google.com/webstore/detail/elasticvue/hkedbapjpblbodpgbajblpnlpenaebaa)
- **Firefox :** [Firefox Add-ons](https://addons.mozilla.org/fr/firefox/addon/elasticvue/)

#### Configuration
1. Ouvrir l'extension
2. Ajouter une connexion :
   - **Name :** Local Elasticsearch
   - **URL :** http://localhost:9200
   - **Auth :** None (pas d'authentification en local)

#### Fonctionnalités
- Visualisation des indexes
- Recherche et filtrage
- Gestion des documents
- Visualisation des mappings

---

### 5. 🐳 Docker Exec (Accès Direct au Conteneur)

#### Accéder au conteneur
```bash
docker exec -it elasticsearch bash
```

#### Utiliser les outils Elasticsearch
```bash
# Dans le conteneur
curl http://localhost:9200
```

---

## 📊 Endpoints Utiles

### Cluster & Health
```bash
# Santé du cluster
curl http://localhost:9200/_cluster/health?pretty

# Informations du cluster
curl http://localhost:9200/_cluster/stats?pretty

# Informations des nodes
curl http://localhost:9200/_nodes?pretty
```

### Indexes
```bash
# Lister tous les indexes
curl "http://localhost:9200/_cat/indices?v"

# Informations d'un index spécifique
curl "http://localhost:9200/properties?pretty"

# Mapping d'un index
curl "http://localhost:9200/properties/_mapping?pretty"

# Statistiques d'un index
curl "http://localhost:9200/properties/_stats?pretty"
```

### Recherche
```bash
# Recherche simple
curl "http://localhost:9200/properties/_search?q=title:appartement&pretty"

# Recherche avec body JSON
curl -X POST "http://localhost:9200/properties/_search?pretty" \
  -H 'Content-Type: application/json' \
  -d '{
    "query": {
      "bool": {
        "must": [
          { "match": { "city": "Paris" }},
          { "range": { "price": { "gte": 100000, "lte": 500000 }}}
        ]
      }
    }
  }'
```

---

## 🔐 Sécurité

**Note :** En production, Elasticsearch devrait être sécurisé avec :
- Authentification (username/password)
- HTTPS/TLS
- Firewall rules
- X-Pack Security (version payante) ou Search Guard (open source)

Pour le développement local, la sécurité est désactivée pour faciliter l'accès.

---

## 🚀 Démarrage Rapide

### Option 1 : Kibana (Interface Graphique)
```bash
# 1. Démarrer Elasticsearch
./scripts/start-elasticsearch.sh

# 2. Démarrer Kibana
./scripts/start-kibana.sh

# 3. Ouvrir dans le navigateur
open http://localhost:5601
```

### Option 2 : API REST
```bash
# Vérifier que Elasticsearch fonctionne
curl http://localhost:9200

# Lister les indexes
curl "http://localhost:9200/_cat/indices?v"
```

### Option 3 : Extension Navigateur
1. Installer Elasticvue (Chrome/Firefox)
2. Ajouter connexion : http://localhost:9200
3. Explorer les données

---

## 📚 Ressources

- [Documentation Elasticsearch](https://www.elastic.co/guide/en/elasticsearch/reference/8.15/index.html)
- [Kibana User Guide](https://www.elastic.co/guide/en/kibana/8.15/index.html)
- [Elasticsearch REST API](https://www.elastic.co/guide/en/elasticsearch/reference/8.15/rest-apis.html)

---

## 💡 Astuces

### Vérifier que les indexes sont créés
```bash
curl "http://localhost:9200/_cat/indices?v"
```

Vous devriez voir :
- `properties` : Index des propriétés immobilières
- `audit-logs` : Index des logs d'audit

### Rechercher une property spécifique
```bash
curl "http://localhost:9200/properties/_search?q=reference:PROP-001&pretty"
```

### Compter les documents par index
```bash
curl "http://localhost:9200/properties/_count?pretty"
curl "http://localhost:9200/audit-logs/_count?pretty"
```

