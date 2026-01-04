# Améliorations du système d'inscription avec abonnement

## Résumé des modifications

### 🎯 Objectif
Créer un système d'inscription unifié qui crée automatiquement l'utilisateur, l'organisation et l'abonnement en une seule transaction, avec un formulaire simplifié demandant uniquement les informations minimales.

## ✅ Modifications apportées

### 1. Backend - Nouveau service unifié

#### `SubscribeService.java` (Nouveau)
- Service qui gère toute l'inscription avec abonnement en une seule transaction
- Crée l'utilisateur
- Crée l'organisation
- Assigne l'utilisateur à l'organisation (avec `isPrimary = true`)
- **→ Le rôle ORGANIZATION_ADMIN est automatiquement assigné** (via OrganizationUserService)
- Crée l'abonnement directement via SQL (pas d'appel au billing service)
- Retourne les tokens JWT et les informations de l'organisation/abonnement

#### `AuthController.java`
- Nouvel endpoint `POST /api/identity/auth/subscribe`
- Accepte `SubscribeRequest` et retourne `SubscribeResponse`

#### DTOs créés
- `SubscribeRequest.java` : Contient les informations minimales (firstName, lastName, email, password, organizationName, planId, phone optionnel)
- `SubscribeResponse.java` : Retourne auth tokens, organizationId, subscriptionId, organizationName, planName

#### `pom.xml`
- Plus besoin de `spring-boot-starter-webflux` (l'abonnement est créé directement via SQL)

#### `application.yml`
- Plus besoin de configuration `services.billing.url` (l'abonnement est créé directement via SQL)

### 2. Frontend - Formulaire simplifié

#### `Subscribe.vue`
- **Formulaire simplifié** : Suppression du champ téléphone (optionnel, peut être ajouté plus tard)
- **Un seul appel API** : Utilise `authService.subscribe()` au lieu de 6 appels séparés
- **Processus simplifié** :
  - Avant : 6 appels API (signup → login → getProfile → createOrganization → assignUser → createSubscription)
  - Maintenant : 1 seul appel API (`subscribe`)

#### `auth.service.ts`
- Ajout de la méthode `subscribe()` qui appelle `/api/identity/auth/subscribe`
- Stocke automatiquement les tokens après l'inscription

#### Types TypeScript
- Ajout de `SubscribeRequest` et `SubscribeResponse` dans `auth.types.ts`
- Ajout de l'endpoint `SUBSCRIBE` dans `api.constants.ts`

## 📋 Informations demandées dans le formulaire

### Informations personnelles (requis)
- Prénom
- Nom
- Email
- Mot de passe
- Confirmation du mot de passe

### Informations organisation (requis)
- Nom de l'organisation

**Note** : Le téléphone a été retiré du formulaire. Il peut être ajouté plus tard dans les paramètres de l'organisation.

## 🔄 Flux d'inscription

### Avant (6 appels API)
```
1. POST /api/identity/auth/register (créer utilisateur)
2. POST /api/identity/auth/login (se connecter)
3. GET /api/identity/users/profile (récupérer userId)
4. POST /api/identity/organizations (créer organisation)
5. POST /api/identity/organizations/{id}/users (assigner utilisateur)
6. POST /api/billing/subscriptions (créer abonnement)
```

### Maintenant (1 seul appel API)
```
POST /api/identity/auth/subscribe
→ Crée tout automatiquement :
  - Utilisateur
  - Organisation
  - Association utilisateur-organisation (avec rôle ORGANIZATION_ADMIN)
  - Abonnement
  - Retourne les tokens JWT
```

## 🎁 Avantages

1. **Simplicité** : Un seul appel API au lieu de 6
2. **Transaction atomique** : Tout est créé en une seule transaction (rollback si erreur)
3. **Formulaire simplifié** : Moins de champs à remplir
4. **Rôle automatique** : Le rôle ORGANIZATION_ADMIN est assigné automatiquement
5. **Meilleure UX** : Processus plus rapide et moins d'erreurs potentielles

## 🔧 Configuration

### Variables d'environnement

```yaml
# Identity Service
# Note: Plus besoin de BILLING_SERVICE_URL car l'abonnement est créé directement via SQL

# Frontend
VITE_ADMIN_URL=http://localhost:3001  # URL de redirection après inscription
```

## 📝 Exemple d'utilisation

### Requête
```json
POST /api/identity/auth/subscribe
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "password": "password123",
  "organizationName": "Agence Immobilier Paris",
  "planId": 1
}
```

### Réponse
```json
{
  "auth": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 86400
  },
  "organizationId": 123,
  "subscriptionId": 456,
  "organizationName": "Agence Immobilier Paris",
  "planName": "ENTERPRISE"
}
```

## 🚀 Déploiement

1. **Backend** : Redémarrer le service identity-service
2. **Frontend** : Aucun changement de build nécessaire (TypeScript)
3. **Base de données** : 
   - Exécuter `scripts/add-organization-admin-role.sql` si le rôle n'existe pas encore
   - Vérifier que la permission `ROLE_DELETE` est assignée au rôle `ORGANIZATION_ADMIN`

## ✅ Tests

Pour tester le nouveau système, voir le guide complet : `TESTING-SUBSCRIPTION-SYSTEM.md`

### Test rapide

1. Aller sur `/subscribe`
2. Sélectionner un plan
3. Remplir le formulaire simplifié
4. Vérifier que :
   - L'utilisateur est créé
   - L'organisation est créée
   - L'utilisateur a le rôle ORGANIZATION_ADMIN
   - L'abonnement est créé directement dans la base de données
   - La redirection vers l'admin (port 3001) fonctionne

## 📚 Fichiers modifiés/créés

### Backend
- ✅ `services/identity-service/src/main/java/com/realestate/identity/service/SubscribeService.java` (nouveau)
- ✅ `services/identity-service/src/main/java/com/realestate/identity/dto/SubscribeRequest.java` (nouveau)
- ✅ `services/identity-service/src/main/java/com/realestate/identity/dto/SubscribeResponse.java` (nouveau)
- ✅ `services/identity-service/src/main/java/com/realestate/identity/controller/AuthController.java` (modifié)
- ✅ `services/identity-service/pom.xml` (ajout webflux)
- ✅ `services/identity-service/src/main/resources/application.yml` (ajout config billing)

### Frontend
- ✅ `frontend/public/src/views/Subscribe.vue` (simplifié)
- ✅ `frontend/shared/api/auth.service.ts` (ajout méthode subscribe)
- ✅ `frontend/shared/types/auth.types.ts` (ajout types)
- ✅ `frontend/shared/constants/api.constants.ts` (ajout endpoint)

