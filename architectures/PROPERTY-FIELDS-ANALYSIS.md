# Analyse: Property vs PropertyFeature

## Recommandation de Design

### Champs à DÉPLACER vers PropertyFeature

Ces champs sont des caractéristiques optionnelles/variables qui bénéficient du système flexible de PropertyFeature:

1. **appliancesIncluded** (JSON array)
   - Raison: Liste variable d'équipements, peut être étendue sans modifier le schéma
   - Implémentation: Stocker chaque appliance comme une PropertyFeature avec key="appliance" et value="Dishwasher"

2. **parkingFeatures** (JSON array)
   - Raison: Caractéristiques de parking variables
   - Implémentation: Stocker chaque feature comme PropertyFeature avec key="parking_feature"

3. **accessibilityFeatures** (JSON array)
   - Raison: Caractéristiques d'accessibilité optionnelles
   - Implémentation: PropertyFeature avec key="accessibility_feature"

4. **exteriorFeatures** (JSON array)
   - Raison: Caractéristiques extérieures variables
   - Implémentation: PropertyFeature avec key="exterior_feature"

5. **hoaAmenities** (JSON array)
   - Raison: Seulement si hasHOA=true, liste variable
   - Implémentation: PropertyFeature avec key="hoa_amenity"

6. **hoaServices** (JSON array)
   - Raison: Seulement si hasHOA=true, liste variable
   - Implémentation: PropertyFeature avec key="hoa_service"

7. **patioPorch** (String)
   - Raison: Optionnel, peut varier
   - Implémentation: PropertyFeature avec key="patio_porch"

8. **specialConditions** (String)
   - Raison: Condition spéciale optionnelle
   - Implémentation: PropertyFeature avec key="special_condition"

### Champs à GARDER dans Property

Ces champs sont des attributs principaux/fréquemment utilisés qui doivent rester dans Property:

- **fullBathrooms** - Champ numérique standard, fréquemment recherché
- **laundryLocation** - Enum simple (Inside/Outside/None)
- **totalStructureArea** - Surface principale, utilisée pour calculs
- **totalInteriorLivableArea** - Surface principale, utilisée pour calculs
- **virtualTourUrl** - URL simple, pas une caractéristique
- **hasGarage** - Booléen simple, fréquemment filtré
- **homeType** - Type principal de la propriété
- **propertySubtype** - Sous-type standardisé
- **condition** - État standardisé (New/Good/Fair/etc.)
- **yearBuilt** - Année, fréquemment utilisée pour filtres
- **subdivision** - Nom de subdivision, champ texte simple
- **hasHOA** - Booléen, utilisé pour filtres
- **hoaFee** - Montant, champ financier principal
- **hoaFeeFrequency** - Fréquence standardisée
- **region** - Région, utilisée pour recherches géographiques
- **pricePerSquareFoot** - Prix calculé, utilisé pour comparaisons
- **dateOnMarket** - Date, utilisée pour analyses temporelles

## Avantages de PropertyFeature

1. **Flexibilité**: Ajouter de nouvelles caractéristiques sans migration
2. **Extensibilité**: Supporte des valeurs complexes (JSON dans value)
3. **Requêtes**: Peut rechercher par caractéristique spécifique
4. **Historique**: Peut tracker les changements de caractéristiques
5. **Normalisation**: Évite les colonnes NULL massives

## Implémentation Recommandée

Pour les listes JSON, deux approches possibles:

### Approche 1: Une PropertyFeature par élément
```java
// Pour appliancesIncluded = ["Dishwasher", "Refrigerator"]
PropertyFeature pf1 = new PropertyFeature(property, "appliance", "Dishwasher");
PropertyFeature pf2 = new PropertyFeature(property, "appliance", "Refrigerator");
```

### Approche 2: Une PropertyFeature avec JSON dans value
```java
// Pour appliancesIncluded = ["Dishwasher", "Refrigerator"]
PropertyFeature pf = new PropertyFeature(property, "appliances_included", 
    "[\"Dishwasher\", \"Refrigerator\"]");
pf.setType("JSON_ARRAY");
```

**Recommandation**: Approche 1 pour meilleure recherche/filtrage, Approche 2 pour simplicité.

