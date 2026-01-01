# Guide de Migration i18n

Ce guide explique comment mettre à jour les vues pour utiliser i18n.

## Étapes de Migration

### 1. Importer useI18n

```typescript
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
```

### 2. Remplacer les textes en dur

**Avant:**
```vue
<h1>Gestion des Utilisateurs</h1>
<Button>Nouvel Utilisateur</Button>
```

**Après:**
```vue
<h1>{{ t('users.title') }}</h1>
<Button>{{ t('users.newUser') }}</Button>
```

### 3. Remplacements courants

| Texte en dur | Clé i18n |
|-------------|----------|
| `Gestion des Utilisateurs` | `t('users.title')` |
| `Recherche` | `t('common.search')` |
| `Statut` | `t('common.status')` |
| `Actions` | `t('common.actions')` |
| `Enregistrer` | `t('common.save')` |
| `Annuler` | `t('common.cancel')` |
| `Supprimer` | `t('common.delete')` |
| `Actif` | `t('users.active')` |
| `Inactif` | `t('users.inactive')` |
| `Suspendu` | `t('users.suspended')` |
| `En attente` | `t('users.pending')` |

### 4. Placeholders

**Avant:**
```vue
<Input placeholder="Nom, email..." />
```

**Après:**
```vue
<Input :placeholder="t('common.search')" />
```

### 5. Messages dynamiques

**Avant:**
```typescript
toast({
  title: 'Utilisateur créé',
  description: 'L\'utilisateur a été créé avec succès'
})
```

**Après:**
```typescript
toast({
  title: t('messages.success.created'),
  description: t('messages.success.created')
})
```

### 6. Paramètres dans les traductions

**Avant:**
```vue
<span>Supprimer {count} éléments</span>
```

**Après:**
```vue
<span>{{ t('messages.confirm.deleteMultiple', { count }) }}</span>
```

## Vues à migrer

- [x] Dashboard.vue
- [x] Login.vue
- [ ] Signup.vue
- [ ] ForgotPassword.vue
- [ ] Users/Index.vue (partiel)
- [ ] Users/UserDialog.vue
- [ ] Organizations/Index.vue
- [ ] Organizations/OrganizationDialog.vue
- [ ] Properties/Index.vue
- [ ] Properties/PropertyDialog.vue
- [ ] Billing/Index.vue
- [ ] Audit/Index.vue
- [ ] Notifications/Index.vue
- [ ] AdminLayout.vue

## Bonnes pratiques

1. **Toujours utiliser i18n** - Ne jamais mettre de texte en dur
2. **Clés descriptives** - Utiliser des clés claires : `users.title` plutôt que `title1`
3. **Groupement logique** - Organiser les clés par module
4. **Paramètres** - Utiliser des paramètres pour les valeurs dynamiques
5. **Tester toutes les langues** - Vérifier que toutes les traductions fonctionnent

## Commandes utiles

```bash
# Chercher les textes en dur
grep -r "Gestion\|Recherche\|Statut" src/views/

# Vérifier les imports i18n
grep -r "useI18n" src/views/
```

