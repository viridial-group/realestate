# Internationalisation (i18n)

Ce dossier contient toutes les traductions pour l'application admin.

## Langues supportées

- 🇬🇧 **English** (en) - Fallback
- 🇫🇷 **Français** (fr) - Par défaut
- 🇪🇸 **Español** (es)
- 🇮🇹 **Italiano** (it)
- 🇩🇪 **Deutsch** (de)
- 🇻🇳 **Tiếng Việt** (vi)
- 🇨🇳 **中文** (zh)
- 🇸🇦 **العربية** (ar) - RTL support

## Structure

Chaque fichier de langue (`en.ts`, `fr.ts`, etc.) contient les mêmes clés de traduction organisées par module :

- `common` - Traductions communes (boutons, actions, etc.)
- `auth` - Authentification
- `dashboard` - Tableau de bord
- `users` - Gestion des utilisateurs
- `organizations` - Gestion des organisations
- `properties` - Gestion des propriétés
- `billing` - Facturation
- `audit` - Audit et logs
- `notifications` - Notifications
- `validation` - Messages de validation
- `messages` - Messages de succès/erreur/confirmation

## Utilisation

### Dans un composant Vue

```vue
<script setup lang="ts">
import { useI18n } from 'vue-i18n'

const { t } = useI18n()

// Utilisation simple
const title = t('dashboard.title')

// Avec paramètres
const message = t('messages.confirm.deleteMultiple', { count: 5 })
</script>

<template>
  <h1>{{ t('dashboard.title') }}</h1>
</template>
```

### Avec le composable personnalisé

```vue
<script setup lang="ts">
import { useI18n as useCustomI18n } from '@/composables/useI18n'

const { common, messages } = useCustomI18n()

// Helpers prédéfinis
const saveLabel = common.save()
const successMessage = messages.success.created()
</script>
```

### Changer la langue

Le sélecteur de langue est disponible dans le header de l'application. La préférence est sauvegardée dans `localStorage` et persistée entre les sessions.

## Support RTL

L'arabe (ar) est automatiquement configuré en mode RTL (Right-to-Left). L'attribut `dir="rtl"` est ajouté à l'élément `<html>` lorsque l'arabe est sélectionné.

## Ajouter une nouvelle langue

1. Créer un nouveau fichier `xx.ts` dans ce dossier
2. Copier la structure d'un fichier existant (par exemple `en.ts`)
3. Traduire toutes les clés
4. Ajouter la langue dans `index.ts` :
   ```typescript
   import xx from './xx'
   
   export const supportedLocales = [
     // ... autres langues
     { code: 'xx', name: 'Nom de la langue', flag: '🏳️' }
   ]
   
   // Dans messages
   messages: {
     // ...
     xx
   }
   ```

## Bonnes pratiques

- Toujours utiliser les clés de traduction au lieu de texte en dur
- Utiliser des paramètres pour les valeurs dynamiques : `t('key', { param: value })`
- Vérifier que toutes les langues ont les mêmes clés
- Tester avec différentes langues, notamment l'arabe pour le RTL

