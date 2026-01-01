# 🎨 Frontend Projects - Real Estate Platform

Trois applications frontend Vue.js 3 + TypeScript avec shadcn-vue pour la plateforme immobilière.

## 📁 Projets

1. **admin** - Dashboard administrateur (Port 3001)
2. **agent** - Portail agents/agences/freelance (Port 3002)
3. **public** - Site public pour consulter et publier des annonces (Port 3003)

## 🚀 Installation Rapide

### 1. Installer les dépendances

```bash
# Pour chaque projet
cd frontend/admin && npm install
cd ../agent && npm install
cd ../public && npm install
```

### 2. Installer les composants shadcn-vue

Utilisez `npx shadcn-vue@latest add` pour installer les composants :

```bash
# Exemple : installer le composant Button
cd frontend/admin
npx shadcn-vue@latest add button

# Installer plusieurs composants
npx shadcn-vue@latest add button input card dialog form table
```

**Composants recommandés pour démarrer :**
- `button` - Boutons
- `input` - Champs de saisie
- `card` - Cartes
- `dialog` - Modales
- `form` - Formulaires
- `table` - Tableaux
- `toast` - Notifications
- `select` - Sélecteurs
- `label` - Labels
- `badge` - Badges
- `avatar` - Avatars

## 📖 Documentation Complète

Voir [FRONTEND-SETUP.md](./FRONTEND-SETUP.md) pour la documentation complète.

## 🎯 Utilisation

### Développement

```bash
# Admin
cd frontend/admin && npm run dev

# Agent
cd frontend/agent && npm run dev

# Public
cd frontend/public && npm run dev
```

### Build

```bash
npm run build
```

## 🔗 Liens

- [shadcn-vue Documentation](https://www.shadcn-vue.com)
- [Vue.js 3](https://vuejs.org)
- [TypeScript](https://www.typescriptlang.org)

