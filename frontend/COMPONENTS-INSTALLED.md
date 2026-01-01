# 🎨 Composants shadcn-vue Installés

Tous les composants shadcn-vue ont été installés avec succès dans les trois projets frontend.

## 📦 Composants Installés

Les composants suivants sont disponibles dans **chaque projet** (admin, agent, public) :

### Composants de Base
- ✅ **button** - Boutons avec variantes
- ✅ **input** - Champs de saisie
- ✅ **label** - Labels pour formulaires
- ✅ **textarea** - Zones de texte multilignes
- ✅ **select** - Sélecteurs déroulants

### Composants de Layout
- ✅ **card** - Cartes avec header, content, footer
- ✅ **separator** - Séparateurs visuels
- ✅ **tabs** - Onglets
- ✅ **sheet** - Panneaux latéraux
- ✅ **badge** - Badges/étiquettes

### Composants Interactifs
- ✅ **dialog** - Modales/Dialogues
- ✅ **form** - Formulaires avec validation
- ✅ **table** - Tableaux de données
- ✅ **toast** - Notifications toast
- ✅ **avatar** - Avatars utilisateurs

### Composants Spéciaux
- ✅ **navigation-menu** - Menu de navigation (uniquement dans `public`)

## 📁 Structure des Composants

Tous les composants sont installés dans :
```
src/components/ui/[component-name]/
```

Exemple pour le composant Button :
```
src/components/ui/button/
  ├── Button.vue
  └── index.ts
```

## 🚀 Utilisation

### Import d'un composant

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

## 📚 Documentation

Pour plus d'informations sur chaque composant :
- [shadcn-vue Documentation](https://www.shadcn-vue.com)
- [Composants disponibles](https://www.shadcn-vue.com/docs/components)

## ➕ Ajouter d'autres composants

Pour installer des composants supplémentaires :

```bash
cd frontend/[admin|agent|public]
npx shadcn-vue@latest add [nom-du-composant]
```

Exemples :
```bash
npx shadcn-vue@latest add dropdown-menu
npx shadcn-vue@latest add popover
npx shadcn-vue@latest add calendar
npx shadcn-vue@latest add date-picker
```

## ✅ Vérification

Pour vérifier les composants installés :

```bash
ls -la frontend/[admin|agent|public]/src/components/ui/
```

