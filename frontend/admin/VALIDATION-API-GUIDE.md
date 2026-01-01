# ✅ Intégration API & Validation - Guide

## 📦 Packages Installés

```bash
npm install vee-validate @vee-validate/zod zod
```

## 📝 Schémas de Validation

### Fichier: `src/schemas/auth.schema.ts`

Schémas Zod pour la validation :

1. **loginSchema** - Validation du formulaire de connexion
   - Email requis et valide
   - Mot de passe requis (min 6 caractères)

2. **signupSchema** - Validation de l'inscription
   - Nom requis (min 2 caractères)
   - Email requis et valide
   - Mot de passe fort (min 8 caractères, majuscule, minuscule, chiffre)
   - Confirmation de mot de passe correspondante

3. **forgotPasswordSchema** - Validation reset password
   - Email requis et valide

4. **resetPasswordSchema** - Validation avec token
   - Token requis
   - Mot de passe fort
   - Confirmation correspondante

## 🔗 Intégration API

### Login.vue

```typescript
import { Form, Field, ErrorMessage } from 'vee-validate'
import { toTypedSchema } from '@vee-validate/zod'
import { useAuth } from '@/shared'
import { loginSchema } from '@/schemas/auth.schema'
import { useToast } from '@/components/ui/toast'

const { login } = useAuth()
const { toast } = useToast()
const validationSchema = toTypedSchema(loginSchema)

// Utilisation
<Form @submit="handleLogin" :validation-schema="validationSchema">
  <Field name="email" v-slot="{ componentField, errors }">
    <Input v-bind="componentField" />
    <ErrorMessage name="email" />
  </Field>
</Form>
```

### Signup.vue

Même principe avec `signupSchema` et `authService.signup()`

### ForgotPassword.vue

Même principe avec `forgotPasswordSchema` et `authService.forgotPassword()`

## 🎨 Fonctionnalités Implémentées

### Validation
- ✅ Validation en temps réel avec VeeValidate
- ✅ Messages d'erreur personnalisés
- ✅ Validation côté client avant soumission
- ✅ Schémas Zod réutilisables

### API
- ✅ Intégration complète avec `authService`
- ✅ Gestion des erreurs avec try/catch
- ✅ Messages d'erreur depuis l'API
- ✅ Redirection après succès

### UX
- ✅ Toast notifications (succès/erreur)
- ✅ États de chargement (spinner)
- ✅ Désactivation des boutons pendant le chargement
- ✅ Messages d'erreur contextuels

## 📋 Exemple d'Utilisation

### Formulaire avec Validation

```vue
<template>
  <Form @submit="handleSubmit" :validation-schema="validationSchema">
    <Field name="email" v-slot="{ componentField, errors }">
      <div class="space-y-2">
        <Label>Email</Label>
        <Input
          v-bind="componentField"
          :class="{ 'border-destructive': errors.length > 0 }"
        />
        <ErrorMessage name="email" class="text-sm text-destructive" />
      </div>
    </Field>
    
    <Button type="submit" :disabled="loading">
      Submit
    </Button>
  </Form>
</template>

<script setup lang="ts">
import { Form, Field, ErrorMessage } from 'vee-validate'
import { toTypedSchema } from '@vee-validate/zod'
import { loginSchema } from '@/schemas/auth.schema'

const validationSchema = toTypedSchema(loginSchema)

const handleSubmit = async (values: any) => {
  // values est déjà validé par Zod
  console.log(values)
}
</script>
```

### Toast Notifications

```typescript
import { useToast } from '@/components/ui/toast'

const { toast } = useToast()

// Succès
toast({
  title: 'Succès',
  description: 'Opération réussie'
})

// Erreur
toast({
  title: 'Erreur',
  description: 'Une erreur est survenue',
  variant: 'destructive'
})
```

## 🔧 Configuration

### App.vue

Le composant `Toaster` doit être ajouté dans `App.vue` :

```vue
<template>
  <RouterView />
  <Toaster />
</template>
```

## ✅ Checklist

- [x] VeeValidate installé
- [x] Schémas Zod créés
- [x] Login.vue intégré avec API
- [x] Signup.vue intégré avec API
- [x] ForgotPassword.vue intégré avec API
- [x] Toast notifications configurées
- [x] Gestion des erreurs
- [x] États de chargement
- [x] Validation en temps réel

## 🚀 Prochaines Étapes

1. Tester les formulaires avec l'API réelle
2. Ajouter la validation dans UserDialog.vue
3. Créer des schémas pour les autres formulaires (Property, Organization, etc.)

