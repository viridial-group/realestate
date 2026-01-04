# Test Rapide - Système DVF

## ✅ Vérifications Préalables

### 1. Services Démarrés
Assurez-vous que les services suivants sont démarrés :
- ✅ **Gateway** : `http://localhost:8080` (redémarré après ajout de la route)
- ✅ **Property Service** : `http://localhost:8083`
- ✅ **Frontend Admin** : `http://localhost:3001`

### 2. Route Gateway
Vérifier que la route `/api/admin/dvf/**` est configurée dans `gateway/src/main/resources/application.yml` :
```yaml
- id: dvf-admin-service
  uri: http://localhost:8083
  predicates:
    - Path=/api/admin/dvf/**
```

## 🧪 Tests Rapides

### Test 1 : Vérifier la Route Gateway

```bash
# Test direct sur le gateway (sans authentification, devrait retourner 401/403)
curl -X GET "http://localhost:8080/api/admin/dvf/stats"

# Si vous avez un token (remplacer YOUR_TOKEN)
curl -X GET "http://localhost:8080/api/admin/dvf/stats" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Résultat attendu :**
- ✅ **200 OK** : Route fonctionne, statistiques retournées
- ✅ **401/403** : Route fonctionne mais authentification requise
- ❌ **404** : Route non trouvée (gateway non redémarré ou route mal configurée)

### Test 2 : Test via le Frontend

1. **Accéder à la page DVF** : `http://localhost:3001/dvf`
2. **Vérifier l'affichage** :
   - ✅ Les statistiques globales s'affichent (même si vides)
   - ✅ L'historique des imports s'affiche (même si vide)
   - ✅ Pas d'erreur 404 dans la console

### Test 3 : Test d'Import (Optionnel)

1. **Cliquer sur "Importer des données"**
2. **Remplir le formulaire** :
   - Année : `2024`
   - Département : `75` (Paris)
3. **Cliquer sur "Importer"**
4. **Vérifier** :
   - ✅ Message de succès affiché
   - ✅ L'import apparaît dans l'historique avec le statut "EN_COURS"
   - ✅ Les statistiques se mettent à jour après l'import

## 🔍 Vérification des Endpoints

### Endpoints DVF Admin (nécessitent ADMIN ou SUPER_ADMIN)

| Endpoint | Méthode | Description |
|----------|---------|-------------|
| `/api/admin/dvf/stats` | GET | Statistiques globales DVF |
| `/api/admin/dvf/history` | GET | Historique des imports (avec pagination) |
| `/api/admin/dvf/import/{year}/{department}` | POST | Démarrer un import |
| `/api/admin/dvf/clean/{year}` | DELETE | Nettoyer les données d'une année |

### Endpoints Market Data (publiques)

| Endpoint | Méthode | Description |
|----------|---------|-------------|
| `/api/market-data/property/{propertyId}` | GET | Données de marché pour une propriété |
| `/api/market-data/postal-code/{codePostal}` | GET | Données de marché pour un code postal |
| `/api/market-data/property/{propertyId}/similar` | GET | Transactions similaires |

## 🐛 Résolution de Problèmes

### Erreur 404 sur `/api/admin/dvf/stats`

**Cause :** Le gateway n'a pas été redémarré après l'ajout de la route.

**Solution :**
```bash
# Arrêter le gateway (Ctrl+C)
# Redémarrer le gateway
cd gateway
mvn spring-boot:run
```

### Erreur 401/403 sur les endpoints admin

**Cause :** Authentification requise (ADMIN ou SUPER_ADMIN).

**Solution :**
- Se connecter au frontend avec un compte admin
- Ou inclure le token JWT dans les requêtes curl :
```bash
curl -X GET "http://localhost:8080/api/admin/dvf/stats" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Les statistiques sont vides

**Cause :** Aucune donnée DVF n'a été importée.

**Solution :**
1. Importer des données via la page DVF ou l'API
2. Attendre la fin de l'import (peut prendre plusieurs minutes)
3. Recharger la page

### Erreur CORS

**Cause :** Configuration CORS incorrecte dans le gateway.

**Solution :** Vérifier que `application.yml` contient :
```yaml
cors:
  allowed-origins: http://localhost:3000,http://localhost:3001,http://localhost:3003,http://localhost:5173,http://localhost:5174
```

## ✅ Checklist de Validation

- [ ] Gateway redémarré après ajout de la route
- [ ] Route `/api/admin/dvf/**` configurée dans `application.yml`
- [ ] Page DVF accessible : `http://localhost:3001/dvf`
- [ ] Statistiques globales s'affichent (même si vides)
- [ ] Historique des imports s'affiche (même si vide)
- [ ] Pas d'erreur 404 dans la console du navigateur
- [ ] Formulaire d'import fonctionne
- [ ] Endpoints répondent correctement (200, 401, ou 403, mais pas 404)

## 🚀 Prochaines Étapes

Une fois que tout fonctionne :
1. Importer des données DVF de test (Paris 2024)
2. Vérifier l'affichage des statistiques après import
3. Tester l'affichage des données de marché sur une propriété en France
4. Vérifier le composant `MarketDataCard` sur le détail d'une propriété

