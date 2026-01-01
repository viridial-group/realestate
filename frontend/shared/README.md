# 📦 Shared Modules - Architecture Modulaire

Ce répertoire contient les modules partagés entre les trois applications frontend (admin, agent, public).

## 🏗️ Structure Modulaire

```
shared/
├── api/              # Services API réutilisables
├── stores/           # Stores Pinia partagés
├── composables/     # Composables Vue réutilisables
├── types/           # Types TypeScript partagés
├── utils/           # Utilitaires communs
├── constants/       # Constantes partagées
└── components/      # Composants UI réutilisables
```

## 📦 Modules Disponibles

### 1. API Module (`api/`)
Services API pour communiquer avec le backend :
- `auth.service.ts` - Authentification
- `property.service.ts` - Gestion des propriétés
- `user.service.ts` - Gestion des utilisateurs
- `organization.service.ts` - Gestion des organisations
- `http.client.ts` - Client HTTP configuré (axios)

### 2. Stores Module (`stores/`)
Stores Pinia pour la gestion d'état :
- `auth.store.ts` - État d'authentification
- `user.store.ts` - Données utilisateur
- `property.store.ts` - Propriétés
- `organization.store.ts` - Organisations

### 3. Composables Module (`composables/`)
Composables Vue réutilisables :
- `useAuth.ts` - Hook d'authentification
- `useApi.ts` - Hook pour appels API
- `useToast.ts` - Hook pour notifications
- `useForm.ts` - Hook pour formulaires

### 4. Types Module (`types/`)
Types TypeScript partagés :
- `auth.types.ts` - Types d'authentification
- `property.types.ts` - Types de propriétés
- `user.types.ts` - Types utilisateurs
- `api.types.ts` - Types API génériques

### 5. Utils Module (`utils/`)
Utilitaires communs :
- `token.utils.ts` - Gestion des tokens JWT
- `date.utils.ts` - Formatage de dates
- `validation.utils.ts` - Validation
- `format.utils.ts` - Formatage

### 6. Constants Module (`constants/`)
Constantes partagées :
- `api.constants.ts` - URLs API, endpoints
- `routes.constants.ts` - Routes
- `config.constants.ts` - Configuration

### 7. Components Module (`components/`)
Composants UI réutilisables :
- `Layout/` - Layouts communs
- `Forms/` - Formulaires réutilisables
- `Cards/` - Cartes spécialisées

## 🔧 Utilisation

### Dans un projet frontend (admin/agent/public)

```typescript
// Importer un service API
import { authService } from '@/shared/api/auth.service'

// Importer un store
import { useAuthStore } from '@/shared/stores/auth.store'

// Importer un composable
import { useAuth } from '@/shared/composables/useAuth'

// Importer un type
import type { User } from '@/shared/types/user.types'

// Importer une constante
import { API_ENDPOINTS } from '@/shared/constants/api.constants'
```

## 📝 Configuration

Chaque projet doit configurer le chemin `@/shared` dans son `tsconfig.json` :

```json
{
  "compilerOptions": {
    "paths": {
      "@/*": ["./src/*"],
      "@/shared/*": ["../shared/*"]
    }
  }
}
```

Et dans `vite.config.ts` :

```typescript
resolve: {
  alias: {
    '@': fileURLToPath(new URL('./src', import.meta.url)),
    '@/shared': fileURLToPath(new URL('../shared', import.meta.url))
  }
}
```

