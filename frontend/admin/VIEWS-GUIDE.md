# 📄 Guide des Vues - Admin Dashboard

## 📋 Vues Disponibles

### 1. Dashboard (`/`)
**Fichier:** `src/views/Dashboard.vue`

Vue d'ensemble de la plateforme avec :
- Statistiques principales (utilisateurs, propriétés, organisations)
- Graphiques de répartition par rôle et statut
- Actions rapides vers les différentes sections

**Fonctionnalités:**
- Affichage des stats en temps réel
- Navigation rapide vers les modules

### 2. Gestion des Utilisateurs (`/users`)
**Fichier:** `src/views/Users.vue`

Interface complète de gestion des utilisateurs avec :
- Liste des utilisateurs avec pagination
- Filtres (recherche, statut, rôle)
- Statistiques (total, actifs, inactifs, suspendus)
- Actions (voir, modifier, activer, désactiver, suspendre, supprimer)
- Création/édition via dialog

**Fonctionnalités:**
- Recherche en temps réel
- Filtres multiples
- Pagination
- Gestion des permissions (canEditUser, canDeleteUser)
- Badges de statut et rôles
- Avatar utilisateur

### 3. Gestion des Propriétés (`/properties`)
**Fichier:** `src/views/Properties.vue`

Placeholder pour la gestion des propriétés (à compléter)

### 4. Gestion des Organisations (`/organizations`)
**Fichier:** `src/views/Organizations.vue`

Placeholder pour la gestion des organisations (à compléter)

### 5. Authentification
- `Login.vue` - Page de connexion
- `Signup.vue` - Page d'inscription
- `ForgotPassword.vue` - Réinitialisation de mot de passe

## 🧩 Composants

### UserDialog
**Fichier:** `src/components/users/UserDialog.vue`

Dialog réutilisable pour créer/modifier un utilisateur :
- Formulaire complet (nom, email, téléphone, etc.)
- Sélection de statut
- Sélection multiple de rôles (checkboxes)
- Validation
- Gestion des erreurs

## 🎨 Composants shadcn-vue Utilisés

- `Card`, `CardHeader`, `CardTitle`, `CardContent`, `CardDescription`
- `Button`
- `Input`
- `Label`
- `Table`, `TableHeader`, `TableRow`, `TableHead`, `TableBody`, `TableCell`
- `Badge`
- `Avatar`, `AvatarImage`, `AvatarFallback`
- `Select`, `SelectContent`, `SelectItem`, `SelectTrigger`, `SelectValue`
- `Dialog`, `DialogContent`, `DialogHeader`, `DialogTitle`, `DialogDescription`, `DialogFooter`
- `DropdownMenu`, `DropdownMenuContent`, `DropdownMenuItem`, `DropdownMenuSeparator`, `DropdownMenuTrigger`

## 🔧 Utilisation du Module User

Toutes les vues utilisent le module `@/shared` :

```typescript
import { useUser, UserStatus, UserRole } from '@/shared'

const {
  users,
  loading,
  total,
  loadUsers,
  createUser,
  updateUser,
  deleteUser,
  canEditUser,
  canDeleteUser
} = useUser()
```

## 📊 Fonctionnalités Implémentées

### Users.vue
- ✅ Liste paginée des utilisateurs
- ✅ Recherche et filtres
- ✅ Statistiques en temps réel
- ✅ Actions CRUD complètes
- ✅ Gestion des permissions
- ✅ Badges de statut et rôles
- ✅ Formatage des dates
- ✅ Dialog de création/édition

### Dashboard.vue
- ✅ Statistiques principales
- ✅ Graphiques de répartition
- ✅ Actions rapides
- ✅ Navigation vers les modules

## 🚀 Prochaines Étapes

1. **Compléter Properties.vue**
   - Liste des propriétés avec filtres
   - Création/édition de propriétés
   - Upload d'images
   - Recherche avancée

2. **Compléter Organizations.vue**
   - Liste des organisations
   - Création/édition
   - Gestion des membres

3. **Ajouter un Layout**
   - Sidebar avec navigation
   - Header avec profil utilisateur
   - Breadcrumbs

4. **Améliorer le Dashboard**
   - Graphiques interactifs (Chart.js ou Recharts)
   - Activités récentes
   - Notifications

## 📝 Notes

- Toutes les vues utilisent TypeScript strict
- Les composants shadcn-vue sont utilisés pour l'UI
- Le module `@/shared` fournit toute la logique métier
- Les permissions sont gérées automatiquement via `canEditUser`, `canDeleteUser`

