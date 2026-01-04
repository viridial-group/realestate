# Intégration DVF (Demandes de Valeurs Foncières)

## 📋 Vue d'ensemble

Ce service intègre les données DVF (Demandes de Valeurs Foncières) de [data.gouv.fr](https://www.data.gouv.fr/datasets/demandes-de-valeurs-foncieres/) pour enrichir les propriétés avec des statistiques de marché immobilier.

## 🔗 Source des données

- **URL**: https://www.data.gouv.fr/datasets/demandes-de-valeurs-foncieres/
- **Producteur**: Direction Générale des Finances Publiques (DGFiP)
- **Licence**: Licence Ouverte / Open Licence version 2.0
- **Mise à jour**: Semestrielle (avril et octobre)
- **Couverture**: France métropolitaine et DOM-TOM (sauf Alsace, Moselle, Mayotte)

## 🏗️ Architecture

### Entités

1. **`DVFTransaction`** : Stocke les transactions immobilières issues des fichiers DVF
   - Date de mutation, nature, valeur foncière
   - Type de local (Maison, Appartement, etc.)
   - Surface, nombre de pièces
   - Localisation (code postal, commune, coordonnées GPS)
   - Prix au m² calculé automatiquement

### Services

1. **`DVFService`** : Télécharge et parse les fichiers DVF depuis data.gouv.fr
   - Import asynchrone par département et année
   - Parsing des fichiers CSV avec séparateur `|`
   - Insertion par batch pour optimiser les performances

2. **`MarketDataService`** : Analyse les données de marché
   - Calcul de statistiques (moyenne, médiane, min, max)
   - Évolution trimestrielle des prix
   - Comparaison avec une propriété spécifique
   - Recherche de transactions similaires

### Contrôleurs

1. **`MarketDataController`** : API publique pour les données de marché
   - `/api/market-data/postal-code/{codePostal}` : Statistiques par code postal
   - `/api/market-data/property/{propertyId}` : Statistiques avec comparaison
   - `/api/market-data/property/{propertyId}/similar` : Transactions similaires
   - ⚠️ **Restriction** : Uniquement pour les propriétés en France

2. **`DVFAdminController`** : API d'administration (nécessite ADMIN ou SUPER_ADMIN)
   - `/api/admin/dvf/import/{year}/{department}` : Démarrer un import
   - `/api/admin/dvf/clean/{year}` : Nettoyer les données d'une année
   - `/api/admin/dvf/history` : Historique des imports (paginé)
   - `/api/admin/dvf/stats` : Statistiques globales DVF
   - `POST /api/admin/dvf/import/{year}/{department}` : Import manuel
   - `DELETE /api/admin/dvf/clean/{year}` : Nettoyage des données

## ⚙️ Configuration

### Variables d'environnement

```yaml
# Activer le service DVF
DVF_ENABLED=true

# Taille des batches d'insertion
DVF_MAX_BATCH=1000

# Activer le scheduler automatique
DVF_SCHEDULER_ENABLED=true

# Départements à synchroniser (codes INSEE)
DVF_DEPARTMENTS=75,13,69,33,31,59,44,67,92,93,94
```

### Configuration dans `application.yml`

```yaml
dvf:
  enabled: ${DVF_ENABLED:false}
  max-transactions-per-batch: ${DVF_MAX_BATCH:1000}
  scheduler:
    enabled: ${DVF_SCHEDULER_ENABLED:false}
    departments: ${DVF_DEPARTMENTS:75,13,69,33,31,59,44,67,92,93,94}
```

## 📊 Utilisation

### 1. Import manuel des données

```bash
# Importer les données pour Paris (75) pour 2024
POST /api/admin/dvf/import/2024/75

# Importer pour plusieurs départements
POST /api/admin/dvf/import/2024/13  # Bouches-du-Rhône
POST /api/admin/dvf/import/2024/69  # Rhône
```

### 2. Obtenir les statistiques de marché

```bash
# Pour un code postal
GET /api/market-data/postal-code/75001?propertyType=APARTMENT&startDate=2023-01-01&endDate=2024-12-31

# Pour une propriété avec comparaison
GET /api/market-data/property/123?startDate=2023-01-01&endDate=2024-12-31

# Transactions similaires
GET /api/market-data/property/123/similar?limit=10
```

### 3. Synchronisation automatique

Le scheduler s'exécute automatiquement :
- **15 avril** à 2h du matin
- **15 octobre** à 2h du matin

Il synchronise les départements configurés pour l'année en cours.

## 📈 Données disponibles

### Statistiques de marché

- **Prix moyen au m²** : Moyenne arithmétique
- **Prix médian au m²** : Médiane (plus robuste aux valeurs aberrantes)
- **Prix min/max au m²** : Fourchettes de prix
- **Nombre de transactions** : Volume de transactions
- **Évolution trimestrielle** : Tendance des prix par trimestre

### Comparaison de propriété

- **Différence en pourcentage** : Écart par rapport au marché
- **Évaluation** : SURESTIMÉ, SOUS-ESTIMÉ, ou CORRECT
- **Recommandation** : Conseil basé sur l'analyse

## 🔒 Sécurité et conformité

⚠️ **Important** : Les données DVF contiennent des informations à caractère personnel.

Conformément à l'article R112 A-3 du Livre des procédures fiscales :
- L'utilisation ne doit pas permettre la ré-identification des personnes
- La réutilisation ne doit pas permettre l'indexation depuis les moteurs de recherche externes

**Recommandations** :
- Ne pas exposer les données brutes DVF publiquement
- Utiliser uniquement des statistiques agrégées
- Respecter les conditions générales d'utilisation de data.gouv.fr

## 🚀 Prochaines étapes

1. **Enrichissement automatique** : Enrichir automatiquement les propriétés avec les données de marché lors de la création/modification
2. **Cache** : Mettre en cache les statistiques de marché pour améliorer les performances
3. **Graphiques** : Ajouter des graphiques d'évolution des prix dans le frontend
4. **Alertes** : Notifier les agents lorsque le prix d'une propriété est significativement différent du marché

## 📚 Ressources

- [Documentation DVF sur data.gouv.fr](https://www.data.gouv.fr/datasets/demandes-de-valeurs-foncieres/)
- [API DVF+ par Sogefi](https://www.data.gouv.fr/datasets/demandes-de-valeurs-foncieres/reuses_and_dataservices)
- [Décret n° 2018‑1350](https://www.legifrance.gouv.fr/jorf/id/JORFTEXT000037865847)

