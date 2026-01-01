# 📋 Configuration ELK Stack - Real Estate Platform

**Date:** 30 Décembre 2025  
**Statut:** ✅ Configuration Logstash & Kibana pour logs centralisés

---

## 🎯 Objectif

Configurer une stack ELK complète pour centraliser, traiter et visualiser les logs de tous les microservices.

---

## 📦 Composants

### 1. Elasticsearch (Stockage)

**Version:** 8.15.0  
**Port:** 9200  
**Rôle:** Stockage et indexation des logs

**Déjà configuré:** ✅ Voir `ELASTICSEARCH-ACCESS.md`

### 2. Logstash (Traitement)

**Version:** 8.15.0  
**Port:** 5000 (TCP)  
**Rôle:** Collecte, transformation et envoi des logs vers Elasticsearch

**Configuration:**
- Input: TCP port 5000 (JSON)
- Filter: Parsing JSON, extraction de champs
- Output: Elasticsearch (index `realestate-logs-YYYY.MM.dd`)

### 3. Kibana (Visualisation)

**Version:** 8.15.0  
**Port:** 5601  
**Rôle:** Interface web pour explorer et visualiser les logs

**Déjà configuré:** ✅ Voir `ELASTICSEARCH-ACCESS.md`

---

## 🚀 Démarrage Rapide

### 1. Démarrer la Stack ELK

```bash
# Démarrer Elasticsearch, Logstash et Kibana
./scripts/start-elk-stack.sh
```

### 2. Activer l'Envoi de Logs depuis les Services

Pour chaque service Spring Boot, définir les variables d'environnement :

```bash
export LOGSTASH_ENABLED=true
export LOGSTASH_HOST=localhost
export LOGSTASH_PORT=5000
```

Ou dans `application.yml` :

```yaml
logging:
  config: classpath:logback-spring.xml

# Variables d'environnement
LOGSTASH_ENABLED: true
LOGSTASH_HOST: localhost
LOGSTASH_PORT: 5000
```

### 3. Accéder à Kibana

- **URL:** http://localhost:5601
- **Index Pattern:** `realestate-logs-*`
- **Time Field:** `@timestamp`

---

## 📋 Configuration

### Logstash

**Fichier:** `config/logstash/logstash.conf`

**Input:**
- TCP port 5000 (JSON lines)
- Reçoit les logs JSON des services Spring Boot

**Filter:**
- Parse les logs JSON
- Extrait les champs (timestamp, level, service, message, exception)
- Ajoute des tags (error, warning)

**Output:**
- Envoie vers Elasticsearch
- Index: `realestate-logs-YYYY.MM.dd`
- Template: `realestate-logs-template.json`

### Logback (Services Spring Boot)

**Fichier:** `common/src/main/resources/logback-spring.xml`

**Appenders:**
1. **CONSOLE** - Logs dans la console
2. **FILE** - Logs dans des fichiers locaux
3. **LOGSTASH** - Logs vers Logstash (JSON via TCP)

**Activation:**
- Conditionnelle via `LOGSTASH_ENABLED=true`
- Configuration via variables d'environnement

---

## 🔍 Vérification

### Vérifier que Logstash reçoit les logs

```bash
# Voir les logs de Logstash
docker logs logstash --tail 50

# Vérifier que Logstash écoute sur le port 5000
netstat -tuln | grep 5000
```

### Vérifier les logs dans Elasticsearch

```bash
# Lister les index
curl http://localhost:9200/_cat/indices?v

# Vérifier les logs récents
curl -X GET "http://localhost:9200/realestate-logs-*/_search?pretty" \
  -H 'Content-Type: application/json' \
  -d '{
    "size": 10,
    "sort": [{"@timestamp": "desc"}]
  }'
```

### Vérifier dans Kibana

1. Allez sur http://localhost:5601
2. Allez dans **Management > Stack Management > Index Patterns**
3. Créez un index pattern: `realestate-logs-*`
4. Sélectionnez le time field: `@timestamp`
5. Allez dans **Discover** pour voir les logs

---

## 📊 Champs Disponibles dans Kibana

- `@timestamp` - Date et heure du log
- `log_level` - Niveau (DEBUG, INFO, WARN, ERROR)
- `service_name` - Nom du service (ex: `identity-service`)
- `thread_name` - Nom du thread
- `log_message` - Message du log
- `exception` - Exception (si présente)
- `stack_trace` - Stack trace (si présente)
- `tags` - Tags (error, warning)

---

## 🛠️ Commandes Utiles

### Démarrer/Arrêter Logstash

```bash
./scripts/start-logstash.sh
./scripts/stop-logstash.sh
```

### Démarrer/Arrêter la Stack ELK

```bash
./scripts/start-elk-stack.sh
./scripts/stop-elasticsearch.sh
./scripts/stop-logstash.sh
./scripts/stop-kibana.sh
```

### Vérifier les Logs

```bash
# Logstash
docker logs logstash --tail 100 -f

# Elasticsearch
docker logs elasticsearch --tail 100 -f

# Kibana
docker logs kibana --tail 100 -f
```

---

## 📈 Dashboards Kibana (À créer)

### Dashboard: Application Logs Overview

**Panneaux suggérés:**
- Logs par niveau (pie chart)
- Logs par service (bar chart)
- Logs par heure (line chart)
- Top 10 erreurs (table)
- Logs récents (table)

**Création:**
1. Allez dans **Analytics > Dashboard**
2. Créez un nouveau dashboard
3. Ajoutez des visualisations basées sur l'index `realestate-logs-*`

---

## 🔔 Alertes (À venir)

Les alertes Kibana seront configurées pour :
- Taux d'erreur > 5%
- Service DOWN (pas de logs depuis 5 minutes)
- Exception spécifique détectée

---

## 📝 Notes

- **Logstash** nécessite Elasticsearch pour fonctionner
- Les logs sont indexés par jour (`realestate-logs-YYYY.MM.dd`)
- Les logs sont conservés 30 jours par défaut (configurable)
- L'envoi vers Logstash est optionnel (via `LOGSTASH_ENABLED`)
- Les logs sont toujours écrits dans la console et les fichiers locaux

---

## 🔗 Liens Utils

- [Elasticsearch Documentation](https://www.elastic.co/guide/en/elasticsearch/reference/8.15/index.html)
- [Logstash Documentation](https://www.elastic.co/guide/en/logstash/8.15/index.html)
- [Kibana Documentation](https://www.elastic.co/guide/en/kibana/8.15/index.html)
- [Logback Documentation](http://logback.qos.ch/documentation.html)
- [Logstash Logback Encoder](https://github.com/logfellow/logstash-logback-encoder)

---

## ✅ Prochaines Étapes

1. ⏳ Créer des dashboards Kibana pour visualiser les logs
2. ⏳ Configurer des alertes Kibana
3. ⏳ Optimiser les index Elasticsearch (retention, shards)
4. ⏳ Configurer la rotation des index (Index Lifecycle Management)

