# Recommandation: Champs à Migrer vers PropertyFeature

## ✅ Champs à DÉPLACER vers PropertyFeature (8 champs)

### Critères de Migration
- ✅ Liste JSON (array) - Variable et extensible
- ✅ Optionnel - Pas toujours présent
- ✅ Variable - Diffère beaucoup entre propriétés
- ✅ Pas fréquemment filtré/recherché directement

### Liste des Champs

| Champ | Type | Key PropertyFeature | Raison |
|-------|------|---------------------|--------|
| `appliancesIncluded` | JSON array | `appliance` | Liste variable, extensible |
| `parkingFeatures` | JSON array | `parking_feature` | Caractéristiques variables |
| `accessibilityFeatures` | JSON array | `accessibility_feature` | Optionnel, extensible |
| `exteriorFeatures` | JSON array | `exterior_feature` | Liste variable |
| `hoaAmenities` | JSON array | `hoa_amenity` | Seulement si hasHOA=true |
| `hoaServices` | JSON array | `hoa_service` | Seulement si hasHOA=true |
| `patioPorch` | String | `patio_porch` | Optionnel, variable |
| `specialConditions` | String | `special_condition` | Optionnel, variable |

## ❌ Champs à GARDER dans Property (17 champs)

### Critères de Conservation
- ✅ Numérique standard (Integer, BigDecimal)
- ✅ Booléen simple
- ✅ Enum/Type standardisé
- ✅ Fréquemment recherché/filtré
- Utilisé pour calculs
- Champ principal (non optionnel)

### Liste des Champs

| Champ | Type | Raison de Conservation |
|-------|------|------------------------|
| `fullBathrooms` | Integer | Numérique standard, filtré |
| `laundryLocation` | String (Enum) | Enum simple, filtré |
| `totalStructureArea` | BigDecimal | Surface principale, calculs |
| `totalInteriorLivableArea` | BigDecimal | Surface principale, calculs |
| `virtualTourUrl` | String | URL simple, pas caractéristique |
| `hasGarage` | Boolean | Booléen, fréquemment filtré |
| `homeType` | String | Type principal, standardisé |
| `propertySubtype` | String | Sous-type standardisé |
| `condition` | String | État standardisé, filtré |
| `yearBuilt` | Integer | Année, filtré et calculs |
| `subdivision` | String | Nom subdivision, recherche |
| `hasHOA` | Boolean | Booléen, filtré |
| `hoaFee` | BigDecimal | Montant financier, calculs |
| `hoaFeeFrequency` | String | Fréquence standardisée |
| `region` | String | Région, recherche géographique |
| `pricePerSquareFoot` | BigDecimal | Prix calculé, comparaisons |
| `dateOnMarket` | LocalDate | Date, analyses temporelles |

## 📊 Comparaison

### Avantages PropertyFeature
- ✅ Flexibilité: Ajouter sans migration
- ✅ Extensibilité: Supporte valeurs complexes
- ✅ Recherche: Filtrage par feature spécifique
- ✅ Normalisation: Évite colonnes NULL

### Avantages Property (direct)
- ✅ Performance: Requêtes plus rapides
- ✅ Simplicité: Accès direct
- ✅ Index: Indexation facile
- ✅ Filtres: WHERE clauses simples

## 🎯 Recommandation Finale

### Option 1: Migration Complète (Long terme)
**Déplacer les 8 champs vers PropertyFeature**
- Avantage: Architecture plus flexible
- Inconvénient: Migration complexe, breaking changes

### Option 2: Approche Hybride (Recommandée) ✅
**Garder dans Property mais supporter PropertyFeature**
- ✅ Champs dans Property pour performance
- ✅ PropertyFeature comme fallback/extensibilité
- ✅ Mapper charge PropertyFeatures si champs vides
- ✅ Pas de breaking changes
- ✅ Migration progressive possible

### Option 3: Status Quo
**Garder tout dans Property**
- Avantage: Simple, performant
- Inconvénient: Moins flexible pour extensions futures

## 💡 Implémentation Actuelle

Le système actuel utilise **Option 2 (Hybride)**:
- ✅ Champs dans Property (performance)
- ✅ PropertyMapper charge PropertyFeatures comme fallback
- ✅ Compatibilité ascendante
- ✅ Prêt pour migration future si nécessaire

## 🔄 Si Migration Complète Nécessaire

### Script de Migration SQL
```sql
-- Exemple pour appliancesIncluded
INSERT INTO property_features (property_id, key, value, type, active, created_at, updated_at)
SELECT 
    id,
    'appliance',
    json_array_elements_text(appliances_included::json)::text,
    'STRING',
    true,
    NOW(),
    NOW()
FROM properties
WHERE appliances_included IS NOT NULL 
  AND appliances_included != '[]'
  AND appliances_included != 'null';
```

### Étapes
1. Migrer données existantes
2. Mettre à jour code backend
3. Mettre à jour frontend
4. Supprimer colonnes
5. Tests complets

