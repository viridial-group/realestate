# 🔍 Configuration Zipkin - Real Estate Platform

**Date:** 30 Décembre 2025  
**Statut:** ✅ Configuration Zipkin pour Distributed Tracing

---

## 🎯 Objectif

Configurer Zipkin pour tracer les requêtes à travers tous les microservices et visualiser les appels inter-services.

---

## 📦 Composants

### Zipkin (Distributed Tracing)

**Version:** 2.24.4  
**Port:** 9411  
**Rôle:** Collecte, stocke et visualise les traces distribuées

**Fonctionnalités:**
- Visualisation des traces (spans)
- Analyse de la latence
- Détection des goulots d'étranglement
- Visualisation des dépendances entre services

---

## 🚀 Démarrage Rapide

### 1. Démarrer Zipkin

```bash
./scripts/start-zipkin.sh
```

### 2. Activer le Tracing dans les Services

Pour chaque service Spring Boot, ajouter dans `application.yml` :

```yaml
spring:
  profiles:
    include: zipkin

# Ou définir les variables d'environnement
ZIPKIN_ENABLED: true
ZIPKIN_URL: http://localhost:9411/api/v2/spans
```

### 3. Accéder à Zipkin

- **URL:** http://localhost:9411
- **Recherche:** Par service, trace ID, ou plage de dates
- **Visualisation:** Arbre de traces avec latences

---

## 📋 Configuration

### Dépendances

**Fichier:** `common/pom.xml`

Les dépendances suivantes ont été ajoutées :
- `micrometer-tracing-bridge-brave` - Bridge pour Brave (tracing)
- `zipkin-reporter-brave` - Reporter pour envoyer les traces à Zipkin

### Configuration des Services

**Fichier:** `config/application-zipkin.yml`

**Paramètres:**
- `management.tracing.enabled=true` - Active le tracing
- `management.tracing.sampling.probability=1.0` - 100% des requêtes tracées
- `zipkin.enabled=true` - Active l'export vers Zipkin
- `management.tracing.export.zipkin.endpoint` - URL de l'API Zipkin

### Activation

**Option 1: Via Profile Spring**

```yaml
spring:
  profiles:
    include: zipkin
```

**Option 2: Via Variables d'Environnement**

```bash
export ZIPKIN_ENABLED=true
export ZIPKIN_URL=http://localhost:9411/api/v2/spans
```

---

## 🔍 Vérification

### Vérifier que Zipkin reçoit les traces

1. Allez sur http://localhost:9411
2. Cliquez sur "Run Query" (sans filtres)
3. Vous devriez voir des traces si les services envoient des requêtes

### Tester une Requête Traced

```bash
# Faire une requête via le Gateway
curl -X GET "http://localhost:8080/api/identity/users/1" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Vérifier dans Zipkin
# 1. Allez sur http://localhost:9411
# 2. Recherchez par service: "gateway" ou "identity-service"
# 3. Cliquez sur une trace pour voir le détail
```

---

## 📊 Visualisation des Traces

### Structure d'une Trace

Une trace contient plusieurs **spans** :
- **Root Span:** Requête HTTP entrante (ex: GET /api/identity/users/1)
- **Child Spans:** Appels inter-services (ex: Identity Service → Database)
- **Tags:** Métadonnées (HTTP method, status code, service name)

### Exemple de Trace

```
GET /api/identity/users/1
├── Gateway (10ms)
    └── Identity Service (50ms)
        ├── Database Query (30ms)
        └── Redis Cache (5ms)
```

---

## 🛠️ Commandes Utiles

### Démarrer/Arrêter Zipkin

```bash
./scripts/start-zipkin.sh
./scripts/stop-zipkin.sh
```

### Vérifier les Logs

```bash
docker logs zipkin --tail 100 -f
```

### Vérifier l'API Zipkin

```bash
# Health check
curl http://localhost:9411/health

# Services listés
curl http://localhost:9411/api/v2/services
```

---

## 📈 Utilisation

### Rechercher des Traces

1. **Par Service:** Sélectionnez un service dans la liste
2. **Par Trace ID:** Entrez un trace ID (ex: depuis les logs)
3. **Par Plage de Dates:** Sélectionnez une période
4. **Par Durée:** Filtrez les traces lentes

### Analyser une Trace

1. Cliquez sur une trace dans la liste
2. Visualisez l'arbre de spans
3. Identifiez les spans les plus lents
4. Vérifiez les tags et annotations

### Détecter les Problèmes

- **Traces lentes:** Spans avec une durée élevée
- **Erreurs:** Spans avec tag `error=true`
- **Goulots d'étranglement:** Services avec beaucoup de traces lentes

---

## 🔔 Intégration avec les Logs

Les traces peuvent être corrélées avec les logs via le **Trace ID**.

**Dans les logs:**
```
2025-12-30 10:15:23.456 [http-nio-8081-exec-1] INFO  c.r.i.controller.UserController - [traceId=abc123,spanId=def456] Getting user 1
```

**Dans Zipkin:**
- Recherchez par trace ID: `abc123`
- Visualisez tous les spans associés

---

## 📝 Notes

- **Sampling:** En production, réduisez `sampling.probability` à 0.1 (10%) pour réduire la charge
- **Performance:** Le tracing ajoute une légère latence (~1-5ms par span)
- **Stockage:** Zipkin stocke les traces en mémoire par défaut (perdues au redémarrage)
- **Persistance:** Pour la production, configurez un backend de stockage (Elasticsearch, MySQL, etc.)

---

## 🔗 Liens Utils

- [Zipkin Documentation](https://zipkin.io/)
- [Micrometer Tracing Documentation](https://micrometer.io/docs/tracing)
- [Spring Boot Observability](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html#actuator.micrometer-tracing)

---

## ✅ Prochaines Étapes

1. ⏳ Configurer la persistance des traces (Elasticsearch backend)
2. ⏳ Créer des alertes sur les traces lentes
3. ⏳ Intégrer avec Grafana pour visualisation des métriques de tracing
4. ⏳ Optimiser le sampling en production

