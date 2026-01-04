# Analyse de Sites Web - Technique en Deux Étapes

## Nouvelle Approche

Le système de scraping utilise maintenant une approche en **deux étapes** pour extraire des informations complètes sur les agences immobilières :

### Étape 1 : Récupération des Domaines depuis Google

1. Le scraper Google recherche les agences immobilières
2. Extrait les URLs/domaines des résultats de recherche
3. Filtre et valide les URLs trouvées

### Étape 2 : Analyse de Chaque Site Web

Pour chaque domaine trouvé, le système :

1. **Navigue vers le site web** de l'agence
2. **Analyse le contenu** pour extraire :
   - **Nom de l'agence** : depuis les balises `<h1>`, `.company-name`, `<title>`, etc.
   - **Email** : depuis les liens `mailto:` et les patterns d'email dans le texte
   - **Téléphone** : depuis les liens `tel:` et les patterns de téléphone français
   - **Adresse** : depuis les éléments avec classes contenant "address", "adresse", "location"
   - **Description** : depuis les balises meta description et sections about
   - **Code postal et ville** : extraits automatiquement de l'adresse

## Avantages de cette Approche

✅ **Informations plus complètes** : Récupère directement depuis les sites des agences  
✅ **Emails et téléphones** : Extrait les coordonnées de contact réelles  
✅ **Adresses précises** : Récupère les adresses complètes avec codes postaux  
✅ **Meilleure qualité de données** : Informations à jour depuis les sites officiels  

## Limitations

⚠️ **Plus lent** : Chaque site doit être visité individuellement  
⚠️ **Risque de blocage** : Certains sites peuvent bloquer les robots  
⚠️ **Structure variable** : Chaque site a une structure HTML différente  

## Configuration

Le nombre de sites analysés par recherche est limité à **10** par défaut pour éviter les timeouts. Ce nombre peut être ajusté dans `GoogleScraper.ts` :

```typescript
for (const domainInfo of domainsToAnalyze.slice(0, 10)) { // Limite à 10
```

## Délais

- **3 secondes** entre chaque analyse de site web
- **2 secondes** d'attente après le chargement de chaque page

Ces délais peuvent être ajustés dans le code pour optimiser la vitesse vs. le risque de blocage.

## Extraction d'Informations

### Email
- Recherche dans les liens `mailto:`
- Recherche de patterns d'email dans le texte de la page
- Filtrage des emails de test/placeholder

### Téléphone
- Recherche dans les liens `tel:`
- Recherche de patterns de téléphone français (`+33` ou `0` suivi de 9 chiffres)
- Normalisation du format

### Adresse
- Recherche dans les éléments avec classes contenant "address", "adresse", "location"
- Détection de patterns d'adresse française (numéro, rue, code postal, ville)
- Extraction automatique du code postal et de la ville

## Logs

Le système logge :
- Chaque site analysé
- Les informations extraites (nom, email, téléphone)
- Les erreurs rencontrées lors de l'analyse

Exemple de log :
```
Analyzing website: https://example-agence.fr
Extracted from https://example-agence.fr: name=Agence Immobilière Example, email=yes, phone=yes
```

