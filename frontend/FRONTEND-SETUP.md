# 🎨 Frontend Setup - Real Estate Platform

Ce document explique comment configurer et utiliser les trois applications frontend Vue.js 3 avec TypeScript et shadcn-vue.

## 📁 Structure des Projets

```
frontend/
├── admin/          # Dashboard administrateur (Port 3001)
├── agent/          # Portail agents/agences (Port 3002)
└── public/         # Site public (Port 3003)
```

## 🚀 Installation Initiale

### 1. Installer les dépendances pour chaque projet

```bash
# Admin Dashboard
cd frontend/admin
npm install

# Agent Portal
cd ../agent
npm install

# Public Site
cd ../public
npm install
```

## 🎨 Installation des Composants shadcn-vue

shadcn-vue utilise une approche "copy-paste" : les composants sont copiés directement dans votre projet pour une personnalisation totale.

### Commandes d'installation

Pour chaque projet, utilisez `npx shadcn-vue@latest add` pour installer les composants :

```bash
# Dans frontend/admin, frontend/agent, ou frontend/public

# Installer un composant spécifique
npx shadcn-vue@latest add button
npx shadcn-vue@latest add input
npx shadcn-vue@latest add card
npx shadcn-vue@latest add dialog
npx shadcn-vue@latest add dropdown-menu
npx shadcn-vue@latest add form
npx shadcn-vue@latest add table
npx shadcn-vue@latest add toast
npx shadcn-vue@latest add select
npx shadcn-vue@latest add textarea
npx shadcn-vue@latest add label
npx shadcn-vue@latest add avatar
npx shadcn-vue@latest add badge
npx shadcn-vue@latest add separator
npx shadcn-vue@latest add tabs
npx shadcn-vue@latest add sheet
npx shadcn-vue@latest add navigation-menu
```

### Installation en lot

Vous pouvez installer plusieurs composants à la fois :

```bash
npx shadcn-vue@latest add button input card dialog form table
```

## 📦 Composants Recommandés par Projet

### Admin Dashboard (`frontend/admin`)

Composants essentiels pour la gestion administrative :

```bash
cd frontend/admin
npx shadcn-vue@latest add button input card dialog form table dropdown-menu toast select textarea label badge separator tabs sheet navigation-menu avatar
```

### Agent Portal (`frontend/agent`)

Composants pour les agents et agences :

```bash
cd frontend/agent
npx shadcn-vue@latest add button input card dialog form table dropdown-menu toast select textarea label badge separator tabs sheet avatar
```

### Public Site (`frontend/public`)

Composants pour le site public :

```bash
cd frontend/public
npx shadcn-vue@latest add button input card dialog form table dropdown-menu toast select textarea label badge separator tabs sheet navigation-menu avatar
```

## 🛠️ Scripts d'Installation Automatique

Des scripts sont disponibles pour installer automatiquement les composants de base :

```bash
# Installer les composants pour tous les projets
./scripts/install-shadcn-components.sh

# Ou pour un projet spécifique
cd frontend/admin && ./scripts/install-components.sh
```

## 🎯 Utilisation des Composants

Après installation, importez les composants dans vos fichiers Vue :

```vue
<script setup lang="ts">
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Card, CardHeader, CardTitle, CardContent } from '@/components/ui/card'
</script>

<template>
  <Card>
    <CardHeader>
      <CardTitle>Mon Titre</CardTitle>
    </CardHeader>
    <CardContent>
      <Input placeholder="Entrez votre texte" />
      <Button>Cliquer</Button>
    </CardContent>
  </Card>
</template>
```

## 🚀 Démarrage des Projets

### Mode Développement

```bash
# Admin Dashboard (Port 3001)
cd frontend/admin
npm run dev

# Agent Portal (Port 3002)
cd frontend/agent
npm run dev

# Public Site (Port 3003)
cd frontend/public
npm run dev
```

### Build de Production

```bash
# Pour chaque projet
npm run build
```

## 📚 Documentation

- [shadcn-vue Documentation](https://www.shadcn-vue.com)
- [Vue.js 3 Documentation](https://vuejs.org)
- [TypeScript Documentation](https://www.typescriptlang.org)
- [Tailwind CSS Documentation](https://tailwindcss.com)

## 🔧 Configuration

Chaque projet a sa propre configuration :

- `components.json` : Configuration shadcn-vue
- `tailwind.config.ts` : Configuration Tailwind CSS
- `vite.config.ts` : Configuration Vite
- `tsconfig.json` : Configuration TypeScript

## 🌐 Proxy API

Tous les projets sont configurés pour proxy les requêtes `/api` vers le Gateway Spring Boot sur `http://localhost:8080`.

## 📝 Notes

- Les composants shadcn-vue sont copiés dans `src/components/ui/`
- Vous pouvez modifier directement les composants selon vos besoins
- Utilisez `@/components/ui/` pour importer les composants
- Les styles sont gérés via Tailwind CSS avec des variables CSS

