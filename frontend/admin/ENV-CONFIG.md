# Configuration des Variables d'Environnement

Ce document explique comment configurer les variables d'environnement pour l'application admin.

## 📁 Fichiers

- `.env.example` - Template avec les variables d'environnement
- `.env` - Fichier de configuration local (non versionné)
- `src/config/env.ts` - Configuration TypeScript pour accéder aux variables

## 🔧 Variables d'Environnement

### Variables Requises

| Variable | Description | Défaut |
|----------|-------------|--------|
| `VITE_API_BASE_URL` | URL de base de l'API Gateway | `http://localhost:8080` |

### Variables Optionnelles

| Variable | Description | Défaut |
|----------|-------------|--------|
| `VITE_IDENTITY_SERVICE_URL` | URL du service Identity | `http://localhost:8081` |
| `VITE_ORGANIZATION_SERVICE_URL` | URL du service Organization | `http://localhost:8082` |
| `VITE_PROPERTY_SERVICE_URL` | URL du service Property | `http://localhost:8083` |
| `VITE_RESOURCE_SERVICE_URL` | URL du service Resource | `http://localhost:8084` |
| `VITE_DOCUMENT_SERVICE_URL` | URL du service Document | `http://localhost:8085` |
| `VITE_WORKFLOW_SERVICE_URL` | URL du service Workflow | `http://localhost:8086` |
| `VITE_NOTIFICATION_SERVICE_URL` | URL du service Notification | `http://localhost:8087` |
| `VITE_EMAILING_SERVICE_URL` | URL du service Emailing | `http://localhost:8088` |
| `VITE_AUDIT_SERVICE_URL` | URL du service Audit | `http://localhost:8089` |
| `VITE_BILLING_SERVICE_URL` | URL du service Billing | `http://localhost:8090` |
| `VITE_ENV` | Environnement (development/production) | `development` |
| `VITE_APP_NAME` | Nom de l'application | `Real Estate Admin` |
| `VITE_APP_VERSION` | Version de l'application | `1.0.0` |

## 🚀 Utilisation

### 1. Créer le fichier .env

```bash
cp .env.example .env
```

### 2. Modifier les valeurs selon votre environnement

```env
# Pour le développement local
VITE_API_BASE_URL=http://localhost:8080

# Pour la production
VITE_API_BASE_URL=https://api.viridial.com
```

### 3. Utiliser dans le code

```typescript
import { env } from '@/config/env'

// Accéder à l'URL de l'API
const apiUrl = env.apiBaseUrl

// Vérifier l'environnement
if (env.isDevelopment) {
  console.log('Mode développement')
}
```

## 🌍 Environnements

### Développement Local

```env
VITE_API_BASE_URL=http://localhost:8080
VITE_ENV=development
```

### Staging

```env
VITE_API_BASE_URL=https://staging-api.viridial.com
VITE_ENV=staging
```

### Production

```env
VITE_API_BASE_URL=https://api.viridial.com
VITE_ENV=production
```

## ⚙️ Configuration Vite

Les variables d'environnement sont automatiquement chargées par Vite. Toutes les variables préfixées par `VITE_` sont exposées au client.

Le proxy dans `vite.config.ts` utilise automatiquement `VITE_API_BASE_URL` pour rediriger les requêtes `/api` vers le gateway.

## 🔒 Sécurité

⚠️ **Important** : Ne jamais commiter le fichier `.env` dans Git. Il contient des informations sensibles.

Le fichier `.env.example` est versionné et sert de template pour les autres développeurs.

## 📝 Notes

- Les variables d'environnement sont chargées au build time
- Pour les changements de variables, redémarrer le serveur de développement
- En production, les variables doivent être définies dans l'environnement du serveur

