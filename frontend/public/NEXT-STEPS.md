# 🚀 Prochaines Étapes - Frontend Public

## ✅ État Actuel

### Fonctionnalités Implémentées
- ✅ Intégration API complète
- ✅ Filtres de base (type, prix, surface, recherche)
- ✅ Tri côté client
- ✅ États de chargement et erreurs
- ✅ Debounce sur les filtres
- ✅ Map avec markers et POI
- ✅ Pagination côté client (5 items/page)
- ✅ Routes de base (/publish, /about)

### Problèmes Identifiés
- ⚠️ Pagination limitée à 100 items (hardcodé)
- ⚠️ Tri côté client (devrait être côté serveur)
- ⚠️ Images placeholder (picsum.photos) au lieu des vraies images
- ⚠️ Pas de page de détails pour une propriété
- ⚠️ Filtres limités (manque bedrooms, bathrooms, etc.)

---

## 🎯 Plan d'Action - Priorités

### 🔴 Priorité 1 - Critique (1-2 jours)

#### 1. **Pagination Côté Serveur**
**Problème** : Actuellement limité à 100 items hardcodés
**Solution** :
- Implémenter une vraie pagination avec `currentPage` et `totalPages` depuis l'API
- Modifier `usePublicProperties` pour gérer la pagination
- Mettre à jour `ListingsPanel` pour utiliser la pagination serveur
- Ajouter un indicateur "X-Y sur Z résultats"

**Fichiers à modifier** :
- `src/composables/usePublicProperties.ts`
- `src/views/Home.vue`
- `src/components/ListingsPanel.vue`

#### 2. **Page de Détails d'une Propriété**
**Problème** : Pas de page pour voir les détails complets
**Solution** :
- Créer `src/views/PropertyDetail.vue`
- Ajouter route `/property/:id` ou `/property/:reference`
- Afficher toutes les informations (images, description, features, etc.)
- Intégrer la map avec la propriété centrée
- Bouton "Retour à la recherche"

**Fichiers à créer** :
- `src/views/PropertyDetail.vue`
- Route dans `src/router/index.js`

#### 3. **Gestion des Images depuis l'API**
**Problème** : Utilise picsum.photos au lieu des vraies images
**Solution** :
- Modifier le mapping pour inclure les images depuis l'API
- Créer un composant `PropertyImage.vue` avec lazy loading
- Gérer les images manquantes avec placeholder
- Optimiser le chargement (srcset, sizes)

**Fichiers à modifier** :
- `src/composables/usePublicProperties.ts`
- `src/components/ListingsPanel.vue`
- `src/components/MapView.vue` (popups)

---

### 🟡 Priorité 2 - Important (2-3 jours)

#### 4. **Filtres Avancés**
**Problème** : Filtres limités (manque bedrooms, bathrooms, etc.)
**Solution** :
- Ajouter filtres `bedrooms` et `bathrooms` dans `FiltersBar`
- Ajouter filtre par `city` avec autocomplete
- Ajouter filtre par `country`
- Implémenter un panneau de filtres avancés (collapsible)

**Fichiers à modifier** :
- `src/components/FiltersBar.vue`
- `src/views/Home.vue`
- `src/api/public-property.service.ts` (types)

#### 5. **Tri Côté Serveur**
**Problème** : Tri actuellement côté client
**Solution** :
- Ajouter paramètre `sortBy` à l'API
- Modifier le backend pour supporter le tri
- Envoyer le paramètre de tri à l'API
- Supprimer le tri côté client

**Fichiers à modifier** :
- `src/views/Home.vue`
- `src/api/public-property.service.ts`
- Backend : `PublicPropertyController.java`

#### 6. **Améliorer l'UX de la Recherche**
**Problème** : Pas de suggestions, pas d'autocomplete
**Solution** :
- Ajouter autocomplete pour la recherche de ville
- Suggestions de recherche populaires
- Historique de recherche (localStorage)
- Filtres rapides (boutons)

**Fichiers à créer/modifier** :
- `src/components/SearchAutocomplete.vue`
- `src/composables/useSearchHistory.ts`
- `src/components/FiltersBar.vue`

---

### 🟢 Priorité 3 - Amélioration (3-4 jours)

#### 7. **Optimisations Performance**
**Problème** : Pas d'optimisations pour grandes listes
**Solution** :
- Lazy loading des images
- Virtual scrolling pour `ListingsPanel`
- Debounce amélioré (augmenter à 500ms)
- Memoization des computed properties
- Code splitting des routes

**Fichiers à modifier** :
- `src/components/ListingsPanel.vue`
- `src/composables/usePublicProperties.ts`
- `src/router/index.js`

#### 8. **Fonctionnalités Utilisateur**
**Problème** : Pas de favoris, comparaison, etc.
**Solution** :
- Système de favoris (localStorage ou API si auth)
- Comparaison de propriétés (max 3)
- Partage de propriété (lien, email)
- Impression de fiche

**Fichiers à créer** :
- `src/composables/useFavorites.ts`
- `src/composables/useComparison.ts`
- `src/components/FavoriteButton.vue`
- `src/components/ComparisonPanel.vue`

#### 9. **Améliorer la Map**
**Problème** : Map basique, pas de clustering
**Solution** :
- Ajouter clustering pour les markers proches
- Filtres de POI (afficher/masquer par type)
- Recherche par zone (draw polygon)
- Heatmap des prix

**Fichiers à modifier** :
- `src/components/MapView.vue`
- Ajouter `leaflet.markercluster`

---

## 📋 Checklist de Mise en Œuvre

### Phase 1 - Pagination & Détails (Priorité 1)
- [ ] Modifier `usePublicProperties` pour gérer pagination serveur
- [ ] Mettre à jour `ListingsPanel` avec pagination serveur
- [ ] Créer `PropertyDetail.vue`
- [ ] Ajouter route `/property/:id`
- [ ] Intégrer images depuis l'API
- [ ] Créer composant `PropertyImage.vue`

### Phase 2 - Filtres & Tri (Priorité 2)
- [ ] Ajouter filtres bedrooms/bathrooms
- [ ] Implémenter autocomplete pour city
- [ ] Ajouter tri côté serveur
- [ ] Créer panneau filtres avancés

### Phase 3 - UX & Performance (Priorité 3)
- [ ] Lazy loading images
- [ ] Virtual scrolling
- [ ] Système de favoris
- [ ] Clustering sur la map

---

## 🔧 Détails Techniques

### Pagination Serveur

```typescript
// usePublicProperties.ts
interface PaginationInfo {
  currentPage: number
  totalPages: number
  totalElements: number
  size: number
}

const pagination = ref<PaginationInfo>({
  currentPage: 0,
  totalPages: 0,
  totalElements: 0,
  size: 20
})
```

### Images API

```typescript
// PropertyDTO devrait inclure :
interface PropertyImage {
  id: number
  url: string
  thumbnailUrl?: string
  isPrimary: boolean
}

// Mapping dans usePublicProperties
function mapToListingFormat(property: PublicProperty) {
  return {
    // ...
    images: property.images || [],
    primaryImage: property.images?.find(img => img.isPrimary)?.url || placeholder
  }
}
```

### Tri Serveur

```typescript
// Backend : ajouter paramètre sort
@RequestParam(required = false) String sortBy, // "price-asc", "price-desc", etc.
@RequestParam(required = false) String sortField, // "price", "surface", "createdAt"
```

---

## 📊 Métriques de Succès

### Performance
- ⚡ Temps de chargement initial < 2s
- ⚡ Temps de réponse filtres < 500ms
- ⚡ Images lazy loaded

### UX
- ✅ Pagination intuitive
- ✅ Page de détails complète
- ✅ Filtres avancés accessibles
- ✅ Recherche avec suggestions

### Fonctionnalités
- ✅ Toutes les propriétés accessibles (pas de limite 100)
- ✅ Images réelles affichées
- ✅ Tri performant côté serveur

---

## 🎯 Recommandation Immédiate

**Commencer par la Priorité 1** :
1. Pagination côté serveur (impact immédiat sur UX)
2. Page de détails (fonctionnalité essentielle)
3. Images depuis l'API (améliore la qualité)

Ces 3 tâches sont critiques et amélioreront significativement l'expérience utilisateur.

