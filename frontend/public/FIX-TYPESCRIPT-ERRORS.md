# Corrections des erreurs TypeScript

## Erreurs critiques à corriger

### 1. Modules manquants (dépendances)
Les erreurs concernant `axios`, `vue`, `pinia`, `vue-router` indiquent que les types ne sont pas trouvés. Vérifier que les dépendances sont installées.

### 2. Variables non utilisées (TS6133)
Préfixer avec `_` ou supprimer les imports/fonctions non utilisés.

### 3. Erreurs de types critiques

#### SignupRequest - firstName manquant
**Fichier**: `src/views/Login.vue:359`
**Problème**: `firstName` n'existe pas dans `SignupRequest`
**Solution**: Vérifier le type `SignupRequest` dans `@viridial/shared`

#### ResetPasswordRequest - newPassword vs password
**Fichier**: `src/views/ResetPassword.vue:169`
**Problème**: `newPassword` n'existe pas, devrait être `password`
**Solution**: Utiliser `password` au lieu de `newPassword`

#### PropertyStatus - DRAFT non valide
**Fichier**: `src/views/MyPropertyDetail.vue:607`
**Problème**: `"DRAFT"` n'est pas assignable à `PropertyStatus`
**Solution**: Vérifier le type `PropertyStatus` et utiliser le bon type

#### PropertyType - APARTMENT non valide
**Fichier**: `src/views/PropertyForm.vue:759`
**Problème**: `'APARTMENT'` n'est pas assignable à `PropertyType`
**Solution**: Vérifier le type `PropertyType` dans `@viridial/shared`

#### Profile.lastLogin vs lastLoginAt
**Fichier**: `src/views/Profile.vue:169`
**Problème**: `lastLogin` n'existe pas, devrait être `lastLoginAt`
**Solution**: Utiliser `lastLoginAt`

#### ImageOptimized - loading type
**Fichier**: `src/components/ImageOptimized.vue:16`
**Problème**: `"auto"` n'est pas valide pour `loading`
**Solution**: Utiliser `"lazy"` ou `"eager"` uniquement

#### SkeletonLoader - computed manquant
**Fichier**: `src/components/SkeletonLoader.vue:65`
**Problème**: `computed` n'est pas importé
**Solution**: Ajouter `import { computed } from 'vue'`

#### MyPropertyDetail - loadMessages manquant
**Fichier**: `src/views/MyPropertyDetail.vue:498`
**Problème**: `loadMessages` n'est pas défini
**Solution**: Créer la fonction `loadMessages` ou supprimer l'appel

#### City.vue - transactionType mismatch
**Fichier**: `src/views/City.vue:183-184`
**Problème**: Comparaison entre `"Location" | "Vente"` et `"SALE" | "RENT"`
**Solution**: Utiliser les valeurs de l'API (`SALE`, `RENT`) au lieu des valeurs affichées

#### PropertyDetail - SEOConfig type
**Fichier**: `src/views/PropertyDetail.vue:649`
**Problème**: Type incompatible avec `SEOConfig`
**Solution**: Vérifier le type attendu par `useSEO`

## Corrections rapides

### Supprimer les imports non utilisés
- `computed` dans plusieurs fichiers
- `onMounted` dans PropertyDetail.vue
- Variables non utilisées

### Corriger les types
- Utiliser les types corrects depuis `@viridial/shared`
- Vérifier les interfaces `SignupRequest`, `ResetPasswordRequest`, `PropertyStatus`, `PropertyType`

### Corriger les erreurs null/undefined
- Ajouter des vérifications null pour `error.value` avant `showToast`
- Utiliser `error.value || 'Erreur'` comme fallback

