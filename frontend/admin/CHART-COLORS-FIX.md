# 🎨 Correction des Couleurs des Graphiques

## ✅ Problème Identifié

Chart.js ne peut pas résoudre directement les variables CSS comme `hsl(var(--chart-1))`. Il a besoin de valeurs HSL complètes ou hexadécimales.

## 🔧 Solution Appliquée

### 1. Conversion des Couleurs en Valeurs HSL Complètes

**Avant (ne fonctionnait pas):**
```typescript
backgroundColor: 'hsl(var(--chart-1))'
```

**Après (fonctionne):**
```typescript
backgroundColor: 'hsl(270, 91%, 65%)' // Purple
```

### 2. Palette de Couleurs Appliquée

#### PropertiesByTypeChart.vue
- **Appartement**: `hsl(270, 91%, 65%)` - Purple
- **Maison**: `hsl(330, 81%, 60%)` - Pink
- **Villa**: `hsl(25, 95%, 53%)` - Orange
- **Terrain**: `hsl(195, 100%, 50%)` - Cyan/Blue
- **Commercial**: `hsl(142, 76%, 36%)` - Green
- **Autre**: `hsl(0, 84%, 60%)` - Red/Coral

#### PropertiesByStatusChart.vue
Utilise la même palette dans l'ordre :
- Purple, Pink, Orange, Cyan/Blue, Green, Red/Coral

#### PropertiesTimelineChart.vue
- Ligne principale : `hsl(270, 91%, 65%)` - Purple
- Remplissage : `hsla(270, 91%, 65%, 0.1)` - Purple avec transparence

### 3. Helper Function pour les Variables CSS

Pour les couleurs des axes et de la grille qui peuvent utiliser les variables CSS (car Chart.js les résout dans le contexte DOM), une fonction helper a été créée :

```typescript
const getCSSVariable = (varName: string): string => {
  if (typeof window !== 'undefined') {
    const value = getComputedStyle(document.documentElement).getPropertyValue(varName).trim()
    return value ? `hsl(${value})` : 'hsl(270, 91%, 65%)'
  }
  return 'hsl(270, 91%, 65%)'
}
```

### 4. Tooltips Améliorés

Les tooltips utilisent maintenant des valeurs RGB directes pour une meilleure compatibilité :
- Background: `rgba(255, 255, 255, 0.95)`
- Text: `rgb(15, 23, 42)`
- Border: `rgba(226, 232, 240, 1)`

## 📊 Résultat

Les graphiques affichent maintenant correctement :
- ✅ Couleurs vibrantes (Purple, Pink, Orange, Cyan, Green, Red)
- ✅ Chaque type de propriété a sa propre couleur
- ✅ Chaque statut a sa propre couleur dans le camembert
- ✅ La ligne temporelle en Purple avec remplissage
- ✅ Tooltips stylisés et lisibles

## 🎨 Correspondance des Couleurs

| Type/Statut | Couleur | HSL |
|------------|---------|-----|
| Appartement | Purple | `hsl(270, 91%, 65%)` |
| Maison | Pink | `hsl(330, 81%, 60%)` |
| Villa | Orange | `hsl(25, 95%, 53%)` |
| Terrain | Cyan/Blue | `hsl(195, 100%, 50%)` |
| Commercial | Green | `hsl(142, 76%, 36%)` |
| Autre | Red/Coral | `hsl(0, 84%, 60%)` |

## 📝 Notes Techniques

- Les couleurs des datasets (backgroundColor, borderColor) utilisent des valeurs HSL complètes
- Les couleurs des axes et grilles utilisent la fonction helper pour résoudre les variables CSS
- Les tooltips utilisent des valeurs RGB pour une meilleure compatibilité
- Toutes les couleurs correspondent à la palette définie dans `style.css`

