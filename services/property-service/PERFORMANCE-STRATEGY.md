# 🚀 Stratégie de Performance - Public Property API

## 📋 Vue d'ensemble

L'API publique `/api/public/properties` utilise une **approche hybride optimisée** pour maximiser les performances tout en restant flexible.

## 🎯 Stratégie à 3 niveaux

### 1. **Elasticsearch** (si disponible)
- ✅ **Utilisation** : Recherches textuelles complexes (full-text search)
- ✅ **Avantages** : 
  - Recherche très rapide sur de grandes quantités de données
  - Support de la recherche floue (fuzzy search)
  - Scoring et ranking des résultats
- ⚠️ **Condition** : Nécessite Elasticsearch configuré et disponible
- 📍 **Quand** : Recherche textuelle (`search` parameter) + Elasticsearch disponible

### 2. **PostgreSQL avec JPA Specifications** (fallback principal)
- ✅ **Utilisation** : Filtres simples et combinés (type, city, price, surface, etc.)
- ✅ **Avantages** :
  - Très rapide pour les filtres structurés
  - Utilise les index de la base de données
  - Pas de dépendance externe
- 📍 **Quand** : 
  - Filtres simples (sans recherche textuelle)
  - Elasticsearch non disponible
  - Fallback automatique

### 3. **Cache Redis** (toujours actif)
- ✅ **Utilisation** : Cache des résultats fréquents
- ✅ **Configuration** :
  - `publicProperties` : TTL 5 minutes (listes)
  - `publicProperty` : TTL 10 minutes (détails)
- ✅ **Avantages** :
  - Réduction drastique de la charge sur la base
  - Réponses instantanées pour les requêtes fréquentes
  - Invalidation automatique après TTL

## 📊 Flux de décision

```
Requête → Cache Redis ?
  ├─ OUI → Retourne résultat (0-5ms)
  └─ NON → Recherche textuelle ?
      ├─ OUI + Elasticsearch disponible → Elasticsearch
      │   └─ Cache le résultat
      └─ NON → PostgreSQL + JPA Specifications
          └─ Cache le résultat
```

## 🔧 Configuration

### Elasticsearch (optionnel)
```yaml
spring:
  elasticsearch:
    uris: ${ELASTICSEARCH_URIS:http://localhost:9200}
```

### Redis (requis pour le cache)
```yaml
spring:
  data:
    redis:
      host: ${SPRING_DATA_REDIS_HOST:localhost}
      port: ${SPRING_DATA_REDIS_PORT:6379}
```

### Cache Configuration
- **Fichier** : `CacheConfig.java`
- **TTL Listes** : 5 minutes
- **TTL Détails** : 10 minutes

## 📈 Performances attendues

### Avec Cache Redis
- **Première requête** : 50-200ms (selon complexité)
- **Requêtes suivantes** : 1-5ms (cache hit)

### Sans Cache (PostgreSQL)
- **Filtres simples** : 20-100ms
- **Filtres complexes** : 100-500ms
- **Recherche textuelle** : 200-1000ms

### Avec Elasticsearch
- **Recherche textuelle** : 50-200ms
- **Filtres combinés** : 100-300ms

## 🎯 Recommandations

### Pour production
1. ✅ **Activer Redis** : Essentiel pour les performances
2. ⚠️ **Elasticsearch** : Recommandé si > 10,000 propriétés ou recherche textuelle fréquente
3. ✅ **Index PostgreSQL** : S'assurer que les colonnes suivantes sont indexées :
   - `status`
   - `type`
   - `city`
   - `price`
   - `surface`
   - `created_at` (pour le tri)

### Index SQL recommandés
```sql
CREATE INDEX idx_property_status ON property(status);
CREATE INDEX idx_property_type ON property(type);
CREATE INDEX idx_property_city ON property(city);
CREATE INDEX idx_property_price ON property(price);
CREATE INDEX idx_property_surface ON property(surface);
CREATE INDEX idx_property_created_at ON property(created_at DESC);
CREATE INDEX idx_property_status_city ON property(status, city);
```

## 🔍 Monitoring

### Métriques à surveiller
- Temps de réponse moyen (target: < 200ms)
- Taux de cache hit (target: > 70%)
- Charge Elasticsearch (si utilisé)
- Charge PostgreSQL

### Logs
Le service log automatiquement :
- Stratégie utilisée (Elasticsearch vs PostgreSQL)
- Temps d'exécution
- Utilisation du cache

## 🚀 Évolution future

### Court terme
- [ ] Implémenter la recherche Elasticsearch complète avec tous les filtres
- [ ] Ajouter des métriques Prometheus
- [ ] Optimiser les index PostgreSQL

### Long terme
- [ ] CDN pour les images de propriétés
- [ ] Pagination cursor-based (plus performant)
- [ ] Pré-chargement intelligent (prefetch)

