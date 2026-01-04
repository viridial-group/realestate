# 🎯 Système de Tracking des Événements - Implémentation Complète

**Date:** 1 Janvier 2026  
**Statut:** ✅ Système de tracking implémenté

---

## 📋 Résumé

Système complet de tracking des événements de propriétés (vues, contacts, favoris, partages) pour générer des statistiques historiques réelles.

---

## 🏗️ Architecture

### Entités

1. **PropertyEvent** (`entity/PropertyEvent.java`)
   - Stocke les événements liés aux propriétés
   - Types: VIEW, CONTACT, FAVORITE, SHARE
   - Métadonnées JSON optionnelles

### Repository

2. **PropertyEventRepository** (`repository/PropertyEventRepository.java`)
   - Requêtes optimisées pour l'historique
   - Filtrage par date et propriété
   - Agrégations par type d'événement

### Services

3. **PropertyEventService** (`service/PropertyEventService.java`)
   - Méthodes de tracking: `trackView()`, `trackContact()`, `trackFavorite()`, `trackShare()`
   - Récupération de l'historique: `getPropertyStatsHistory()`, `getGlobalStatsHistory()`
   - Statistiques actuelles: `getPropertyCurrentStats()`

4. **StatsService** (modifié)
   - Utilise `PropertyEventService` pour les données réelles
   - Fallback vers données simulées si service indisponible

### Controllers

5. **PropertyEventController** (`controller/PropertyEventController.java`)
   - Endpoints pour tracker manuellement les événements
   - POST `/api/properties/{id}/events/view`
   - POST `/api/properties/{id}/events/contact`
   - POST `/api/properties/{id}/events/favorite`
   - POST `/api/properties/{id}/events/share`

6. **PropertyController** (modifié)
   - GET `/{id}/stats` - Utilise `PropertyEventService`
   - GET `/{id}/stats/history` - Historique via `StatsService`

7. **PublicPropertyController** (modifié)
   - GET `/{id}` - Track automatiquement une vue

---

## 📊 Base de Données

### Table property_events

**Script:** `src/main/resources/db/migration/create_property_events_table.sql`

**Structure:**
```sql
CREATE TABLE property_events (
    id BIGSERIAL PRIMARY KEY,
    property_id BIGINT NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user_id BIGINT,
    metadata JSONB
);
```

**Index:**
- `idx_property_events_property_id`
- `idx_property_events_created_at`
- `idx_property_events_type`
- `idx_property_events_property_date`

---

## 🔧 Utilisation

### Tracking Automatique

#### Vue d'une propriété (Public API)
```java
// Dans PublicPropertyController.getPublishedPropertyById()
propertyEventService.trackView(id, null, "{\"source\":\"public_api\"}");
```

### Tracking Manuel (Frontend)

#### Vue
```typescript
// Appeler quand une propriété est affichée
await apiClient.post(`/api/properties/${propertyId}/events/view`, {
  userId: currentUser?.id,
  metadata: { source: 'frontend', page: 'detail' }
});
```

#### Contact
```typescript
// Appeler quand un utilisateur contacte
await apiClient.post(`/api/properties/${propertyId}/events/contact`, {
  userId: currentUser?.id,
  metadata: { method: 'email' }
});
```

#### Favori
```typescript
// Appeler quand un utilisateur ajoute aux favoris
await apiClient.post(`/api/properties/${propertyId}/events/favorite`, {
  userId: currentUser?.id
});
```

#### Partage
```typescript
// Appeler quand un utilisateur partage
await apiClient.post(`/api/properties/${propertyId}/events/share`, {
  userId: currentUser?.id,
  platform: 'facebook',
  metadata: { method: 'share_button' }
});
```

---

## 📈 Récupération des Statistiques

### Statistiques Actuelles

```bash
GET /api/properties/{id}/stats
```

**Réponse:**
```json
{
  "views": 42,
  "contacts": 5,
  "favorites": 8,
  "shares": 3
}
```

### Historique

```bash
GET /api/properties/{id}/stats/history?days=7
```

**Réponse:**
```json
[
  {
    "date": "2026-01-01",
    "views": 10,
    "contacts": 2,
    "favorites": 3,
    "shares": 1
  },
  ...
]
```

---

## ⚡ Performance

### Optimisations

1. **Index sur les colonnes clés**
   - `property_id`, `created_at`, `event_type`

2. **Agrégation en mémoire**
   - Les événements sont agrégés par date en Java
   - Évite les requêtes SQL complexes

3. **Cache Redis**
   - Historique mis en cache (TTL: 5 minutes)
   - Statistiques actuelles non cachées (toujours à jour)

4. **Tracking asynchrone**
   - Le tracking dans `PublicPropertyController` ne bloque pas la réponse

---

## 🧪 Tests

### Test de Tracking

```bash
# Tracker une vue
curl -X POST "http://localhost:8083/api/properties/1/events/view" \
  -H "Content-Type: application/json" \
  -d '{"userId": 1, "metadata": {"source": "test"}}'

# Tracker un contact
curl -X POST "http://localhost:8083/api/properties/1/events/contact" \
  -H "Content-Type: application/json" \
  -d '{"userId": 1}'

# Récupérer les stats
curl -X GET "http://localhost:8083/api/properties/1/stats"
```

### Test de l'Historique

```bash
# Historique sur 7 jours
curl -X GET "http://localhost:8083/api/properties/1/stats/history?days=7"

# Historique sur 30 jours
curl -X GET "http://localhost:8083/api/properties/1/stats/history?days=30"
```

---

## 🎯 Prochaines Étapes

### Immédiat
1. ✅ Créer la table `property_events`
2. ✅ Implémenter le tracking
3. ⏳ Intégrer le tracking dans le frontend

### Court Terme
1. **Intégration Frontend**
   - Tracker les vues automatiquement
   - Tracker les favoris
   - Tracker les partages

2. **Analytics Avancées**
   - Taux de conversion (vues → contacts)
   - Sources de trafic
   - Dévices utilisés

### Moyen Terme
1. **Dashboard Analytics**
   - Graphiques temps réel
   - Comparaisons de périodes
   - Top propriétés

2. **Alertes**
   - Notifications sur pics d'activité
   - Alertes sur baisse de vues

---

## 📝 Notes Techniques

### Gestion d'Erreurs
- Le tracking ne bloque jamais les réponses
- Les erreurs sont loggées mais ignorées
- Fallback vers données simulées si service indisponible

### Sécurité
- Tracking public pour les vues (pas d'authentification requise)
- Tracking des contacts/favoris nécessite authentification
- Validation des paramètres (propertyId, userId)

### Performance
- Tracking asynchrone recommandé pour les vues
- Agrégation en mémoire pour l'historique
- Cache Redis pour réduire la charge DB

---

**Dernière mise à jour :** 1 Janvier 2026  
**Statut:** ✅ Prêt pour intégration frontend

