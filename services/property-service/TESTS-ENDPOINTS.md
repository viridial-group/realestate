# 🧪 Tests des Nouveaux Endpoints

**Date:** 1 Janvier 2026  
**Objectif:** Tester les endpoints stats/history et suggestions améliorées

---

## 📋 Prérequis

### Base de Données
```sql
-- Exécuter le script de migration
\i src/main/resources/db/migration/create_property_events_table.sql
```

### Services Démarrés
- Property Service (port 8083)
- Redis (port 6379)
- PostgreSQL (port 5432)

---

## 🧪 Tests Stats History

### Test 1: Historique d'une Propriété

**Endpoint:** `GET /api/properties/{id}/stats/history`

```bash
# Test avec 7 jours (défaut)
curl -X GET "http://localhost:8083/api/properties/1/stats/history?days=7" \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json"

# Test avec 30 jours
curl -X GET "http://localhost:8083/api/properties/1/stats/history?days=30" \
  -H "Authorization: Bearer {token}"

# Test avec limite max (90 jours)
curl -X GET "http://localhost:8083/api/properties/1/stats/history?days=90" \
  -H "Authorization: Bearer {token}"
```

**Résultat attendu:**
- Status: 200 OK
- Format: Array de StatsHistoryPointDTO
- Données: 7, 30 ou 90 points selon le paramètre

**Vérifications:**
- [ ] Format JSON correct
- [ ] Dates en ordre chronologique
- [ ] Toutes les dates présentes (même avec 0)
- [ ] Cache fonctionne (2ème appel plus rapide)

---

### Test 2: Historique Global

**Endpoint:** `GET /api/properties/stats/history`

```bash
# Test avec 7 jours
curl -X GET "http://localhost:8083/api/properties/stats/history?days=7" \
  -H "Authorization: Bearer {token}"

# Test sans paramètre (défaut 7 jours)
curl -X GET "http://localhost:8083/api/properties/stats/history" \
  -H "Authorization: Bearer {token}"
```

**Résultat attendu:**
- Status: 200 OK
- Format: Array de StatsHistoryPointDTO
- Données: Statistiques agrégées de toutes les propriétés

---

### Test 3: Fallback (Table Inexistante)

**Scénario:** Table `property_events` n'existe pas

**Comportement attendu:**
- Status: 200 OK
- Données simulées générées
- Log d'avertissement dans les logs

**Vérifications:**
- [ ] Pas d'erreur 500
- [ ] Données simulées cohérentes
- [ ] Tendance réaliste (légère hausse)

---

## 🔍 Tests Suggestions Améliorées

### Test 1: Suggestions avec Recherche

**Endpoint:** `GET /api/public/properties/suggestions`

```bash
# Recherche simple
curl -X GET "http://localhost:8083/api/public/properties/suggestions?search=paris"

# Recherche avec limite
curl -X GET "http://localhost:8083/api/public/properties/suggestions?search=paris&limit=5"

# Recherche sans recherches populaires
curl -X GET "http://localhost:8083/api/public/properties/suggestions?search=paris&includePopular=false"

# Recherche sans tendances
curl -X GET "http://localhost:8083/api/public/properties/suggestions?search=paris&includeTrending=false"
```

**Résultat attendu:**
- Status: 200 OK
- Format: SearchSuggestionsDTO
- Données filtrées selon les paramètres

**Vérifications:**
- [ ] Villes correspondantes
- [ ] Types correspondants
- [ ] Limite respectée
- [ ] PopularSearches inclus/exclus selon paramètre

---

### Test 2: Suggestions sans Recherche

```bash
# Suggestions par défaut
curl -X GET "http://localhost:8083/api/public/properties/suggestions"

# Avec limite
curl -X GET "http://localhost:8083/api/public/properties/suggestions?limit=20"
```

**Résultat attendu:**
- Status: 200 OK
- Recherches populaires par défaut
- Villes et types les plus fréquents

---

### Test 3: Cache

```bash
# Premier appel (pas de cache)
time curl -X GET "http://localhost:8083/api/public/properties/suggestions?search=paris"

# Deuxième appel (avec cache)
time curl -X GET "http://localhost:8083/api/public/properties/suggestions?search=paris"
```

**Vérifications:**
- [ ] 2ème appel significativement plus rapide
- [ ] Résultats identiques
- [ ] Cache Redis vérifiable avec `redis-cli`

---

## 📊 Tests de Performance

### Test Cache Hit Rate

```bash
# Faire 100 appels identiques
for i in {1..100}; do
  curl -X GET "http://localhost:8083/api/public/properties/suggestions?search=paris" > /dev/null 2>&1
done

# Vérifier le cache dans Redis
redis-cli
> KEYS *searchSuggestions*
> TTL searchSuggestions:paris-10-true-true
```

**Objectif:** Cache hit rate > 90% après le 2ème appel

---

### Test Temps de Réponse

```bash
# Mesurer le temps de réponse
time curl -X GET "http://localhost:8083/api/properties/1/stats/history?days=7" \
  -H "Authorization: Bearer {token}"
```

**Objectifs:**
- Première requête: < 200ms
- Requêtes suivantes (cache): < 10ms

---

## 🔧 Tests d'Intégration Frontend

### Test 1: Dashboard

1. Se connecter au frontend
2. Aller sur `/dashboard`
3. Vérifier que le graphique s'affiche
4. Vérifier que les données sont cohérentes

**Vérifications:**
- [ ] Graphique affiché
- [ ] Données chargées
- [ ] Pas d'erreurs dans la console

---

### Test 2: MyPropertyDetail

1. Aller sur une propriété (`/my-properties/{id}`)
2. Vérifier le graphique dans la sidebar
3. Vérifier que les données sont cohérentes

**Vérifications:**
- [ ] Graphique affiché
- [ ] Données chargées
- [ ] Statistiques à jour

---

### Test 3: Recherche

1. Aller sur `/search` ou `/`
2. Taper dans le champ de recherche
3. Vérifier les suggestions

**Vérifications:**
- [ ] Suggestions affichées
- [ ] Suggestions pertinentes
- [ ] Limite respectée
- [ ] Pas de doublons

---

## 🐛 Tests d'Erreurs

### Test 1: Propriété Inexistante

```bash
curl -X GET "http://localhost:8083/api/properties/99999/stats/history" \
  -H "Authorization: Bearer {token}"
```

**Résultat attendu:**
- Status: 200 OK (avec données simulées)
- Ou Status: 404 Not Found

---

### Test 2: Paramètres Invalides

```bash
# Days > 90
curl -X GET "http://localhost:8083/api/properties/1/stats/history?days=100" \
  -H "Authorization: Bearer {token}"

# Limit > 50
curl -X GET "http://localhost:8083/api/public/properties/suggestions?limit=100"
```

**Résultat attendu:**
- Paramètres limités aux valeurs max
- Pas d'erreur 400

---

## 📝 Checklist de Validation

### Backend
- [ ] Endpoints compilent sans erreur
- [ ] Tests unitaires passent
- [ ] Cache Redis fonctionne
- [ ] Fallback fonctionne si table inexistante

### Frontend
- [ ] Graphiques s'affichent correctement
- [ ] Données chargées depuis l'API
- [ ] Suggestions améliorées fonctionnent
- [ ] Pas d'erreurs dans la console

### Performance
- [ ] Cache hit rate > 80%
- [ ] Temps de réponse < 200ms (sans cache)
- [ ] Temps de réponse < 10ms (avec cache)

---

## 🎯 Prochaines Étapes

1. **Créer la table property_events** dans la base de données
2. **Implémenter le tracking** des événements (views, contacts, etc.)
3. **Tests unitaires** pour les nouveaux services
4. **Tests d'intégration** complets
5. **Monitoring** des performances

---

**Dernière mise à jour :** 1 Janvier 2026

