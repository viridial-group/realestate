# 📝 Guide Typographique - Bootstrap Vyzor

## ✅ Tailles de Police Appliquées Globalement

Les tailles typographiques ont été configurées selon les standards Bootstrap Vyzor et sont maintenant disponibles globalement dans tout le projet.

### Tailles de Base

| Classe | Taille | Pixels | Usage |
|--------|--------|--------|-------|
| `.text-xs` | 0.625rem | 10px | Très petit texte, labels |
| `.text-sm` | 0.75rem | 12px | Petit texte, métadonnées |
| `.text-base` | 0.875rem | 14px | **Taille de base (body)** |
| `.text-lg` | 1rem | 16px | Texte légèrement plus grand |
| `.text-xl` | 1.125rem | 18px | Texte important |
| `.text-2xl` | 1.5rem | 24px | Sous-titres |
| `.text-3xl` | 2rem | 32px | Titres de section |
| `.text-4xl` | 2.5rem | 40px | Titres principaux |
| `.text-5xl` | 3rem | 48px | Titres très grands |
| `.text-6xl` | 3.75rem | 60px | Titres hero |

### Titres (Headings)

| Élément | Taille | Pixels | Line Height | Font Weight |
|---------|--------|--------|-------------|-------------|
| `h1`, `.h1` | 2.5rem | 40px | 1.2 | 600 (semibold) |
| `h2`, `.h2` | 2rem | 32px | 1.3 | 600 (semibold) |
| `h3`, `.h3` | 1.75rem | 28px | 1.4 | 600 (semibold) |
| `h4`, `.h4` | 1.5rem | 24px | 1.4 | 600 (semibold) |
| `h5`, `.h5` | 1.25rem | 20px | 1.5 | 600 (semibold) |
| `h6`, `.h6` | 1rem | 16px | 1.5 | 600 (semibold) |

### Poids de Police (Font Weights)

| Classe | Valeur | Usage |
|--------|--------|-------|
| `.font-light` | 300 | Texte léger |
| `.font-normal` | 400 | Texte normal (par défaut) |
| `.font-medium` | 500 | Texte moyen |
| `.font-semibold` | 600 | Texte semi-gras (titres) |
| `.font-bold` | 700 | Texte gras |

### Hauteurs de Ligne (Line Heights)

| Classe | Valeur | Usage |
|--------|--------|-------|
| `.leading-none` | 1 | Ligne très serrée |
| `.leading-tight` | 1.25 | Ligne serrée (titres) |
| `.leading-snug` | 1.375 | Ligne légèrement serrée |
| `.leading-normal` | 1.5 | Ligne normale (par défaut) |
| `.leading-relaxed` | 1.625 | Ligne détendue |
| `.leading-loose` | 2 | Ligne très espacée |

## 🎯 Configuration Globale

### Fichier `style.css`

Les styles typographiques sont définis dans `@layer base` pour être appliqués globalement :

```css
body {
  font-size: 0.875rem; /* 14px - base */
  line-height: 1.5;
  font-weight: 400;
}
```

### Fichier `tailwind.config.ts`

Les tailles sont également configurées dans Tailwind pour utilisation avec les classes utilitaires :

```typescript
fontSize: {
  'xs': ['0.625rem', { lineHeight: '1.5' }],
  'sm': ['0.75rem', { lineHeight: '1.5' }],
  'base': ['0.875rem', { lineHeight: '1.5' }],
  // ... etc
}
```

## 📋 Utilisation

### Dans les Templates Vue

```vue
<template>
  <!-- Titres -->
  <h1>Titre Principal</h1>
  <h2>Sous-titre</h2>
  
  <!-- Avec classes Tailwind -->
  <h1 class="text-4xl font-bold">Titre Principal</h1>
  <p class="text-base">Texte normal</p>
  <span class="text-sm text-muted-foreground">Petit texte</span>
  
  <!-- Combinaisons -->
  <div class="text-2xl font-semibold leading-tight">
    Titre avec style personnalisé
  </div>
</template>
```

### Exemples d'Utilisation

#### Dashboard Cards
```vue
<CardTitle class="text-sm font-medium">Total Users</CardTitle>
<div class="text-2xl font-bold">42,643</div>
<p class="text-xs text-muted-foreground">+0.45% This Year</p>
```

#### Headers de Pages
```vue
<h1 class="text-3xl font-bold">Tableau de bord</h1>
<p class="text-muted-foreground">Vue d'ensemble de la plateforme</p>
```

#### Tableaux
```vue
<th class="text-sm font-medium">Nom</th>
<td class="text-base">John Doe</td>
<td class="text-xs text-muted-foreground">Admin</td>
```

## 🔄 Migration des Composants Existants

Les composants existants utilisent déjà les classes Tailwind standard :
- `text-sm`, `text-base`, `text-lg`, etc. continuent de fonctionner
- Les nouvelles tailles sont disponibles : `text-xs`, `text-2xl`, `text-3xl`, etc.
- Les titres HTML (`h1`-`h6`) ont maintenant les bonnes tailles par défaut

## 📊 Comparaison avec Bootstrap Vyzor

| Élément | Bootstrap Vyzor | Notre Projet | Status |
|---------|----------------|--------------|--------|
| Body/Base | 14px | 14px (0.875rem) | ✅ |
| h1 | 40px | 40px (2.5rem) | ✅ |
| h2 | 32px | 32px (2rem) | ✅ |
| h3 | 28px | 28px (1.75rem) | ✅ |
| h4 | 24px | 24px (1.5rem) | ✅ |
| h5 | 20px | 20px (1.25rem) | ✅ |
| h6 | 16px | 16px (1rem) | ✅ |
| Small | 12px | 12px (0.75rem) | ✅ |

## 🎨 Bonnes Pratiques

1. **Utiliser les classes Tailwind** pour la cohérence
2. **Respecter la hiérarchie** : h1 > h2 > h3 > h4 > h5 > h6
3. **Utiliser `text-muted-foreground`** pour les textes secondaires
4. **Combiner taille et poids** : `text-2xl font-semibold`
5. **Ajuster line-height** si nécessaire : `leading-tight` pour les titres

## 📝 Notes

- La taille de base (14px) est plus petite que la taille HTML standard (16px) pour correspondre à Bootstrap Vyzor
- Tous les titres utilisent `font-weight: 600` (semibold) par défaut
- Les line-heights sont optimisées pour chaque taille de texte
- Compatible avec le mode sombre (dark mode)

