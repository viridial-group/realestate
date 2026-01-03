# 🔌 Intégration API - Frontend Public

## ✅ Changements effectués

### 1. **Service API créé**
- **Fichier** : `src/api/public-property.service.ts`
- **Endpoint** : `/api/public/properties`
- **Fonctionnalités** :
  - `getPublishedProperties()` - Liste paginée avec filtres
  - `getPublishedPropertyById()` - Détails par ID
  - `getPublishedPropertyByReference()` - Détails par référence

### 2. **Composable créé**
- **Fichier** : `src/composables/usePublicProperties.ts`
- **Fonctionnalités** :
  - Gestion de l'état (loading, error, properties)
  - Mapping automatique des données API → format composants
  - Conversion des coordonnées (latitude/longitude → lat/lng)
  - Mapping des status (PUBLISHED/AVAILABLE → Disponible)

### 3. **Home.vue adapté**
- ✅ Remplacement des données statiques par l'API
- ✅ Chargement automatique au montage
- ✅ Debounce sur les filtres (300ms)
- ✅ États de chargement et erreurs
- ✅ Gestion des propriétés sans coordonnées

### 4. **FiltresBar amélioré**
- ✅ TypeScript avec types stricts
- ✅ Gestion des valeurs null pour maxPrice/minSurface
- ✅ Événements typés

### 5. **MapView amélioré**
- ✅ Filtrage des propriétés sans coordonnées valides
- ✅ Fallback sur vue par défaut si aucune propriété

## 🔄 Mapping des données

### API → Composants

| API | Composant |
|---|---|
| `latitude` | `lat` |
| `longitude` | `lng` |
| `PUBLISHED` / `AVAILABLE` | `Disponible` |
| `RENTED` | `Loué` |
| `SOLD` | `Vendu` |
| `price` (BigDecimal) | `price` (number) |
| `surface` (BigDecimal) | `surface` (number) |

## 📡 Endpoints utilisés

```
GET /api/public/properties
  Query params:
    - type: string
    - city: string
    - country: string
    - minPrice: number
    - maxPrice: number
    - minSurface: number
    - maxSurface: number
    - bedrooms: number
    - bathrooms: number
    - search: string
    - page: number (default: 0)
    - size: number (default: 20)

GET /api/public/properties/{id}
GET /api/public/properties/reference/{reference}
```

## 🎯 Fonctionnalités

### ✅ Implémenté
- Chargement des propriétés depuis l'API
- Filtres (type, prix, surface, recherche)
- Tri côté client
- États de chargement
- Gestion des erreurs
- Debounce sur les filtres
- Mapping automatique des données

### ⚠️ À améliorer
- Pagination côté serveur (actuellement limitée à 100)
- Tri côté serveur (actuellement côté client)
- Gestion des images depuis l'API
- Rating/Reviews depuis l'API

## 🚀 Configuration

### Proxy Vite
Le proxy est déjà configuré dans `vite.config.ts` :
```typescript
proxy: {
  '/api': {
    target: 'http://localhost:8080',
    changeOrigin: true
  }
}
```

### Variables d'environnement
Pour la production, créer un fichier `.env` :
```env
VITE_API_BASE_URL=https://api.viridial.com
```

## 📝 Notes

- Les propriétés sans coordonnées valides sont filtrées de la map mais affichées dans la liste
- Le debounce de 300ms évite les appels API trop fréquents
- Les erreurs sont affichées avec un bouton "Réessayer"
- Le cache Redis côté backend améliore les performances

