# 📊 Configuration du Monitoring - Real Estate Platform

**Date:** 30 Décembre 2025  
**Statut:** ✅ Configuration Prometheus & Grafana

---

## 🎯 Objectif

Configurer un système de monitoring complet pour surveiller tous les microservices de la plateforme Real Estate.

---

## 📦 Composants

### 1. Prometheus (Collecte de Métriques)

**Version:** 2.49.1  
**Port:** 9090  
**Rôle:** Collecte et stocke les métriques exposées par les services Spring Boot

**Métriques collectées:**
- Métriques HTTP (taux de requêtes, latence, erreurs)
- Métriques JVM (mémoire, threads, GC)
- Métriques de base de données (connexions, requêtes)
- Métriques personnalisées (business metrics)

### 2. Grafana (Visualisation)

**Version:** 10.3.3  
**Port:** 3000  
**Rôle:** Dashboards pour visualiser les métriques

**Dashboards:**
- Vue d'ensemble des microservices
- Métriques par service
- Alertes et seuils

---

## 🚀 Démarrage Rapide

### 1. Démarrer la Stack de Monitoring

```bash
# Démarrer Prometheus et Grafana
./scripts/start-monitoring-stack.sh
```

### 2. Accéder aux Interfaces

- **Prometheus:** http://localhost:9090
- **Grafana:** http://localhost:3000
  - Login: `admin` / `admin`
  - ⚠️ Changez le mot de passe à la première connexion

### 3. Configurer Prometheus dans Grafana

1. Allez dans **Configuration > Data Sources**
2. Cliquez sur **Add data source**
3. Sélectionnez **Prometheus**
4. URL: `http://host.docker.internal:9090` (ou `http://localhost:9090` si sur le même host)
5. Cliquez sur **Save & Test**

---

## 📋 Configuration

### Prometheus

**Fichier:** `config/prometheus/prometheus.yml`

**Services surveillés:**
- Gateway (port 8080)
- Identity Service (port 8081)
- Organization Service (port 8082)
- Property Service (port 8083)
- Resource Service (port 8084)
- Document Service (port 8085)
- Workflow Service (port 8086)
- Notification Service (port 8087)
- Emailing Service (port 8088)
- Audit Service (port 8089)
- Billing Service (port 8090)

**Intervalle de scraping:** 15 secondes

### Grafana

**Configuration automatique:**
- Data source Prometheus: `config/grafana/provisioning/datasources/prometheus.yml`
- Dashboards: `config/grafana/dashboards/`

---

## 🔍 Vérification

### Vérifier que Prometheus collecte les métriques

1. Allez sur http://localhost:9090/targets
2. Vérifiez que tous les services sont **UP** (état vert)

### Vérifier les métriques exposées

```bash
# Vérifier les métriques d'un service
curl http://localhost:8081/actuator/prometheus | grep http_server_requests

# Vérifier dans Prometheus
# Allez sur http://localhost:9090/graph
# Tapez: http_server_requests_seconds_count
```

---

## 📊 Métriques Disponibles

### Métriques HTTP (Spring Boot Actuator)

- `http_server_requests_seconds_count` - Nombre total de requêtes
- `http_server_requests_seconds_sum` - Temps total de traitement
- `http_server_requests_seconds_max` - Temps maximum de traitement

### Métriques JVM

- `jvm_memory_used_bytes` - Mémoire utilisée
- `jvm_memory_max_bytes` - Mémoire maximale
- `jvm_threads_live_threads` - Nombre de threads actifs
- `jvm_gc_pause_seconds` - Temps de pause GC

### Métriques de Base de Données

- `hikari_connections_active` - Connexions actives
- `hikari_connections_idle` - Connexions inactives
- `hikari_connections_pending` - Connexions en attente

### Métriques Kafka

- `spring_kafka_consumer_records_consumed_total` - Messages consommés
- `spring_kafka_producer_records_sent_total` - Messages envoyés

---

## 🛠️ Commandes Utiles

### Démarrer/Arrêter Prometheus

```bash
./scripts/start-prometheus.sh
./scripts/stop-prometheus.sh
```

### Démarrer/Arrêter Grafana

```bash
./scripts/start-grafana.sh
./scripts/stop-grafana.sh
```

### Vérifier les Logs

```bash
# Prometheus
docker logs prometheus

# Grafana
docker logs grafana
```

---

## 📈 Dashboards Grafana

### Dashboard: Microservices Overview

**Fichier:** `config/grafana/dashboards/microservices-overview.json`

**Panneaux:**
- HTTP Request Rate
- HTTP Error Rate
- JVM Memory Usage
- JVM Threads

**Import:**
1. Allez dans **Dashboards > Import**
2. Chargez le fichier JSON
3. Sélectionnez la source de données Prometheus

---

## 🔔 Alertes (À venir)

Les alertes Prometheus seront configurées dans:
- `config/prometheus/alerts.yml`

**Alertes prévues:**
- Taux d'erreur HTTP > 5%
- Utilisation mémoire JVM > 80%
- Temps de réponse > 1 seconde
- Service DOWN

---

## 📝 Notes

- **Prometheus** stocke les données localement dans `data/prometheus/`
- **Grafana** stocke les dashboards et configurations dans `data/grafana/`
- Les métriques sont exposées via `/actuator/prometheus` sur chaque service
- L'intervalle de scraping est de 15 secondes (configurable)

---

## 🔗 Liens Utils

- [Prometheus Documentation](https://prometheus.io/docs/)
- [Grafana Documentation](https://grafana.com/docs/)
- [Spring Boot Actuator Metrics](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html#actuator.metrics)

---

## ✅ Prochaines Étapes

1. ⏳ Configurer ELK Stack (Logstash, Kibana) pour les logs
2. ⏳ Configurer Zipkin pour le distributed tracing
3. ⏳ Créer des dashboards Grafana détaillés par service
4. ⏳ Configurer des alertes Prometheus
5. ⏳ Intégrer avec des notifications (email, Slack)

