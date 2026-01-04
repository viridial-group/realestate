# Guide de Test - Système DVF

## ✅ Vérifications Préalables

### 1. Services Démarrés
Assurez-vous que les services suivants sont démarrés :
- **Gateway** : `http://localhost:8080`
- **Property Service** : `http://localhost:8083`
- **Frontend Admin** : `http://localhost:3001`

### 2. Base de Données
- La table `dvf_transactions` doit exister
- La table `dvf_import_history` doit exister
- Au moins une propriété en France avec un code postal valide (5 chiffres)

## 🧪 Tests à Effectuer

### Test 1 : Endpoint Market Data (Backend)
```bash
# Test direct sur le property-service
curl -X GET "http://localhost:8083/api/market-data/property/1?startDate=2022-01-01&endDate=2024-01-01"

# Test via le gateway
curl -X GET "http://localhost:8080/api/market-data/property/1?startDate=2022-01-01&endDate=2024-01-01"
```

**Résultat attendu** : 
- Si la propriété est en France : Données de marché avec statistiques
- Si la propriété n'est pas en France : Erreur 400 avec message explicite

### Test 2 : Historique des Imports (Backend)
```bash
# Récupérer l'historique
curl -X GET "http://localhost:8080/api/admin/dvf/history?page=0&size=20" \
  -H "Authorization: Bearer YOUR_TOKEN"

# Récupérer les statistiques
curl -X GET "http://localhost:8080/api/admin/dvf/stats" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Test 3 : Import DVF (Backend)
```bash
# Démarrer un import
curl -X POST "http://localhost:8080/api/admin/dvf/import/2024/75" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Test 4 : Frontend Admin

1. **Page DVF** (`/dvf`)
   - Accéder à la page de gestion DVF
   - Vérifier l'affichage des statistiques globales
   - Vérifier l'historique des imports
   - Tester l'import manuel

2. **Détail de Propriété** (`/properties/:id`)
   - Ouvrir une propriété en France
   - Vérifier l'affichage du composant `MarketDataCard`
   - Vérifier le graphique d'évolution des prix
   - Tester l'export du graphique

3. **Propriété Non Française**
   - Ouvrir une propriété hors France
   - Vérifier le message d'erreur approprié

## 🔍 Points de Vérification

### Backend
- [ ] `MarketDataController` est compilé et accessible
- [ ] `DVFAdminController` est accessible (nécessite ADMIN/SUPER_ADMIN)
- [ ] `MarketDataService` vérifie le pays (France uniquement)
- [ ] `DVFService` track les imports dans `DVFImportHistory`
- [ ] Notifications WebSocket fonctionnent (si configuré)

### Frontend
- [ ] Le composant `MarketDataCard` s'affiche correctement
- [ ] Le graphique Chart.js fonctionne
- [ ] L'export PNG fonctionne
- [ ] Les messages d'erreur sont clairs
- [ ] La page DVF affiche les statistiques
- [ ] L'historique des imports se charge

### Gateway
- [ ] Route `/api/market-data/**` pointe vers `property-service:8083`
- [ ] Route `/api/admin/dvf/**` pointe vers `property-service:8083`

## 🐛 Résolution de Problèmes

### Erreur 404 sur `/api/market-data/property/1`
**Cause** : Le service property-service n'est pas démarré ou doit être redémarré

**Solution** :
```bash
cd services/property-service
mvn spring-boot:run
```

### Erreur "Les données DVF ne sont disponibles que pour les propriétés en France"
**Cause** : La propriété n'a pas `country = "France"` ou `country = "FR"`

**Solution** : Vérifier et mettre à jour le champ `country` de la propriété

### Graphique ne s'affiche pas
**Cause** : Chart.js n'est pas correctement importé ou les données sont vides

**Solution** : 
- Vérifier que `chart.js` et `vue-chartjs` sont installés
- Vérifier que les données `priceEvolution` sont présentes

### Historique des imports vide
**Cause** : Aucun import n'a été effectué

**Solution** : Effectuer un import via la page DVF ou l'API

## 📊 Données de Test

Pour tester avec des données réelles, vous pouvez :
1. Importer des données DVF pour un département (ex: 75 pour Paris)
2. Créer une propriété en France avec un code postal valide
3. Vérifier que les données de marché s'affichent

## 🚀 Prochaines Étapes

1. Importer des données DVF réelles pour plusieurs départements
2. Configurer la synchronisation automatique (scheduler)
3. Ajouter des notifications pour les imports terminés
4. Améliorer les graphiques avec plus d'options
5. Ajouter des filtres dans la page DVF (par année, département, etc.)

