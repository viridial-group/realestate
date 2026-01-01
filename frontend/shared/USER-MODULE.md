# 👥 Module de Gestion des Utilisateurs SaaS

Module complet pour la gestion des utilisateurs de la plateforme SaaS.

## 📦 Composants du Module

### 1. Service API (`api/user.service.ts`)

Service complet pour toutes les opérations sur les utilisateurs :

- **Gestion de base** : `getAll`, `getById`, `create`, `update`, `delete`
- **Profil utilisateur** : `getProfile`, `updateProfile`, `changePassword`
- **Statut** : `activate`, `deactivate`, `suspend`
- **Recherche** : `search`, `getStats`, `getActivity`
- **Rôles** : `assignRoles`, `removeRoles`
- **Email** : `verifyEmail`, `resendVerificationEmail`

### 2. Store Pinia (`stores/user.store.ts`)

Gestion d'état centralisée :

- **State** : `users`, `currentUser`, `selectedUser`, `stats`, `loading`
- **Getters** : `activeUsers`, `inactiveUsers`, `suspendedUsers`, `usersByRole`
- **Actions** : Toutes les opérations CRUD + gestion de statut

### 3. Composable (`composables/useUser.ts`)

Hook réutilisable avec permissions :

- **State & Getters** : Accès à tous les états et données
- **Actions** : Toutes les opérations sur les utilisateurs
- **Permissions** : `canEditUser`, `canDeleteUser`, `canManageRoles`

### 4. Types TypeScript (`types/user.types.ts`)

Types complets pour les utilisateurs :

- `User`, `UserCreate`, `UserUpdate`
- `UserProfile`, `UserPreferences`, `NotificationSettings`
- `UserStatus`, `UserRole` (enums)
- `UserStats`, `UserActivity`

## 🚀 Utilisation

### Dans un composant Vue

```vue
<script setup lang="ts">
import { useUser } from '@/shared'
import { UserStatus, UserRole } from '@/shared'

const {
  users,
  loading,
  loadUsers,
  createUser,
  updateUser,
  deleteUser,
  canEditUser,
  canDeleteUser
} = useUser()

// Charger les utilisateurs
await loadUsers({ status: UserStatus.ACTIVE })

// Créer un utilisateur
const newUser = await createUser({
  email: 'user@example.com',
  name: 'John Doe',
  password: 'password123',
  roles: [UserRole.AGENT]
})

// Vérifier les permissions
if (canEditUser(userId)) {
  await updateUser(userId, { name: 'New Name' })
}
</script>
```

### Utiliser le service directement

```typescript
import { userService } from '@/shared'
import type { UserCreate } from '@/shared'

// Créer un utilisateur
const user = await userService.create({
  email: 'user@example.com',
  name: 'John Doe',
  password: 'password123',
  roles: ['AGENT']
})

// Récupérer les statistiques
const stats = await userService.getStats()
```

### Utiliser le store directement

```typescript
import { useUserStore } from '@/shared'

const userStore = useUserStore()

// Charger les utilisateurs
await userStore.fetchUsers()

// Accéder aux données
const activeUsers = userStore.activeUsers
const stats = userStore.stats
```

## 🔐 Gestion des Permissions

Le composable `useUser` inclut des helpers de permissions :

```typescript
const { canEditUser, canDeleteUser, canManageRoles } = useUser()

// Vérifier si on peut éditer un utilisateur
if (canEditUser(userId)) {
  // Éditer l'utilisateur
}

// Vérifier si on peut supprimer
if (canDeleteUser(userId)) {
  // Supprimer l'utilisateur
}

// Vérifier si on peut gérer les rôles
if (canManageRoles(userId)) {
  // Assigner/retirer des rôles
}
```

## 📊 Types de Rôles

```typescript
enum UserRole {
  SUPER_ADMIN = 'SUPER_ADMIN',        // Super administrateur
  ADMIN = 'ADMIN',                    // Administrateur
  AGENT = 'AGENT',                    // Agent immobilier
  FREELANCE = 'FREELANCE',            // Freelance
  AUTO_ENTREPRENEUR = 'AUTO_ENTREPRENEUR', // Auto-entrepreneur
  PARTICULAR = 'PARTICULAR',          // Particulier
  USER = 'USER'                       // Utilisateur standard
}
```

## 📈 Statuts Utilisateur

```typescript
enum UserStatus {
  ACTIVE = 'ACTIVE',      // Actif
  INACTIVE = 'INACTIVE',  // Inactif
  SUSPENDED = 'SUSPENDED', // Suspendu
  PENDING = 'PENDING',    // En attente
  DELETED = 'DELETED'     // Supprimé
}
```

## 🔍 Recherche et Filtres

```typescript
const { loadUsers } = useUser()

// Recherche avec filtres
await loadUsers({
  search: 'john',
  status: UserStatus.ACTIVE,
  role: UserRole.AGENT,
  organizationId: 1,
  page: 0,
  size: 20
})
```

## 📝 Exemple Complet

```vue
<template>
  <div>
    <Button @click="loadUsers">Charger les utilisateurs</Button>
    
    <Table v-if="!loading">
      <TableHeader>
        <TableRow>
          <TableHead>Nom</TableHead>
          <TableHead>Email</TableHead>
          <TableHead>Rôles</TableHead>
          <TableHead>Statut</TableHead>
          <TableHead>Actions</TableHead>
        </TableRow>
      </TableHeader>
      <TableBody>
        <TableRow v-for="user in users" :key="user.id">
          <TableCell>{{ user.name }}</TableCell>
          <TableCell>{{ user.email }}</TableCell>
          <TableCell>{{ user.roles.join(', ') }}</TableCell>
          <TableCell>{{ user.status }}</TableCell>
          <TableCell>
            <Button 
              v-if="canEditUser(user.id)" 
              @click="editUser(user.id)"
            >
              Éditer
            </Button>
            <Button 
              v-if="canDeleteUser(user.id)" 
              @click="deleteUser(user.id)"
              variant="destructive"
            >
              Supprimer
            </Button>
          </TableCell>
        </TableRow>
      </TableBody>
    </Table>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useUser } from '@/shared'
import { Button } from '@/components/ui/button'
import { Table, TableHeader, TableRow, TableHead, TableBody, TableCell } from '@/components/ui/table'

const {
  users,
  loading,
  loadUsers,
  deleteUser: removeUser,
  canEditUser,
  canDeleteUser
} = useUser()

onMounted(() => {
  loadUsers()
})

const editUser = (id: number) => {
  // Navigation vers la page d'édition
}

const deleteUser = async (id: number) => {
  if (confirm('Êtes-vous sûr de vouloir supprimer cet utilisateur ?')) {
    await removeUser(id)
    await loadUsers() // Recharger la liste
  }
}
</script>
```

## 🎯 Fonctionnalités

- ✅ CRUD complet des utilisateurs
- ✅ Gestion des rôles et permissions
- ✅ Gestion des statuts (actif, inactif, suspendu)
- ✅ Recherche et filtres avancés
- ✅ Statistiques des utilisateurs
- ✅ Historique d'activité
- ✅ Gestion du profil utilisateur
- ✅ Changement de mot de passe
- ✅ Vérification d'email
- ✅ Pagination
- ✅ Permissions intégrées

