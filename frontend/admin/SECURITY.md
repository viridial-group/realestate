# 🔒 Sécurité de l'Application

Documentation complète sur la sécurité de l'application frontend.

## 🛡️ Système de Sécurité

### 1. Guards de Route

#### Auth Guard (`router/guards/auth.guard.ts`)
Protège les routes nécessitant une authentification :
- Vérifie la présence d'un token JWT
- Vérifie l'état d'authentification dans le store
- Redirige vers `/login` si non authentifié
- Préserve l'URL de destination dans `redirect` query param

**Utilisation:**
```typescript
{
  path: '/dashboard',
  beforeEnter: authGuard
}
```

#### Guest Guard
Redirige les utilisateurs déjà authentifiés (pour les pages login/signup) :
- Vérifie si l'utilisateur est déjà connecté
- Redirige vers le dashboard si authentifié

**Utilisation:**
```typescript
{
  path: '/login',
  beforeEnter: guestGuard
}
```

#### Role Guard (`router/guards/role.guard.ts`)
Protège les routes nécessitant des rôles spécifiques :
- `roleGuard(...roles)` - Vérifie plusieurs rôles
- `adminGuard` - Vérifie ADMIN ou SUPER_ADMIN
- `superAdminGuard` - Vérifie uniquement SUPER_ADMIN

**Utilisation:**
```typescript
{
  path: '/users',
  beforeEnter: [authGuard, adminGuard]
}
```

### 2. Middleware d'Authentification

#### Auth Middleware (`middleware/auth.middleware.ts`)
Vérifie et rafraîchit le token automatiquement :
- Vérifie la validité du token
- Rafraîchit le token si nécessaire
- Nettoie le token invalide

**Utilisation:**
```typescript
import { authMiddleware } from '@/middleware/auth.middleware'

const isAuthenticated = await authMiddleware()
```

#### Permission Middleware
Vérifie les permissions de l'utilisateur :
- Vérifie si l'utilisateur a les rôles requis
- Super Admin a tous les droits

**Utilisation:**
```typescript
import { permissionMiddleware } from '@/middleware/auth.middleware'

const hasPermission = permissionMiddleware(['ADMIN', 'AGENT'])
```

### 3. Composable useRouteGuard

Hook réutilisable pour la gestion des permissions dans les composants :

```vue
<script setup lang="ts">
import { useRouteGuard } from '@/composables/useRouteGuard'

const { canAccess, hasRole, requireAuth, requireAdmin } = useRouteGuard()

// Vérifier l'accès
if (!canAccess.value) {
  requireAuth()
}

// Vérifier un rôle
if (!hasRole(UserRole.ADMIN)) {
  requireAdmin()
}
</script>
```

### 4. Utilitaires de Sécurité

#### Security Utils (`utils/security.utils.ts`)
Fonctions utilitaires pour la sécurité :

- `sanitizeInput()` - Nettoie les entrées utilisateur
- `isValidEmail()` - Valide le format email
- `isStrongPassword()` - Vérifie la force du mot de passe
- `getPasswordStrength()` - Score de force (0-4)
- `generateSecureToken()` - Génère un token sécurisé
- `containsDangerousContent()` - Détecte le contenu dangereux
- `escapeHtml()` - Échappe les caractères HTML
- `validateCsrfToken()` - Valide un token CSRF

## 🔐 Protection des Routes

### Routes Publiques
```typescript
{
  path: '/login',
  beforeEnter: guestGuard  // Redirige si déjà connecté
}
```

### Routes Authentifiées
```typescript
{
  path: '/dashboard',
  beforeEnter: authGuard  // Redirige si non connecté
}
```

### Routes Admin
```typescript
{
  path: '/users',
  beforeEnter: [authGuard, adminGuard]  // Auth + Admin requis
}
```

### Routes Super Admin
```typescript
{
  path: '/settings',
  beforeEnter: [authGuard, superAdminGuard]  // Auth + Super Admin requis
}
```

## 🎯 Gestion des Tokens

### Stockage
- Tokens stockés dans `localStorage`
- Géré par `tokenUtils` du module `@/shared`

### Rafraîchissement
- Automatique via `authMiddleware`
- Intercepteur HTTP dans `httpClient`

### Expiration
- Détection automatique des tokens expirés
- Redirection vers login si token invalide
- Nettoyage automatique du token

## 🚨 Gestion des Erreurs

### Erreurs d'Authentification
- Token expiré → Redirection vers login
- Token invalide → Nettoyage et redirection
- Erreur réseau → Message d'erreur utilisateur

### Erreurs de Permission
- Accès refusé → Redirection vers dashboard avec message
- Rôle insuffisant → Message d'erreur

## 📋 Checklist de Sécurité

- ✅ Guards de route pour toutes les pages protégées
- ✅ Vérification du token au démarrage de l'app
- ✅ Rafraîchissement automatique du token
- ✅ Nettoyage des tokens invalides
- ✅ Protection contre les injections XSS
- ✅ Validation des entrées utilisateur
- ✅ Gestion des permissions par rôle
- ✅ Redirection sécurisée après login
- ✅ Protection des routes sensibles

## 🔧 Configuration

### Variables d'Environnement
```env
VITE_API_BASE_URL=/api
VITE_TOKEN_KEY=auth_token
VITE_REFRESH_TOKEN_KEY=refresh_token
```

### Intercepteurs HTTP
Les intercepteurs dans `httpClient` gèrent automatiquement :
- Ajout du token dans les headers
- Rafraîchissement du token
- Gestion des erreurs 401

## 📝 Bonnes Pratiques

1. **Toujours utiliser les guards** pour les routes protégées
2. **Vérifier les permissions** avant d'afficher du contenu sensible
3. **Sanitizer les entrées** utilisateur avant affichage
4. **Valider les données** côté client ET serveur
5. **Ne jamais stocker** de données sensibles dans localStorage
6. **Utiliser HTTPS** en production
7. **Implémenter CSRF** protection si nécessaire

## 🚀 Prochaines Étapes

- [ ] Implémenter la protection CSRF
- [ ] Ajouter la validation des tokens côté serveur
- [ ] Implémenter le rate limiting
- [ ] Ajouter la détection d'anomalies
- [ ] Implémenter l'audit des actions sensibles

