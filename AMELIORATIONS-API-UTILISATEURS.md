# 🚀 Améliorations des APIs avec Logique Utilisateur Individuel vs Professionnel

## 📋 Résumé des Améliorations

### ✅ Services Créés

#### 1. **UserTypeService** (`services/identity-service`)
Service centralisé pour déterminer et gérer le type d'utilisateur.

**Fonctionnalités :**
- Détermine le type d'utilisateur (INDIVIDUAL, PROFESSIONAL, UNKNOWN)
- Vérifie si un utilisateur est individuel ou professionnel
- Récupère l'organisation principale d'un utilisateur
- Vérifie les permissions de gestion de propriétés selon le type

**Méthodes principales :**
```java
- determineUserType(User user) : UserType
- isIndividual(User user) : boolean
- isProfessional(User user) : boolean
- getPrimaryOrganizationId(Long userId) : Long
- canManageProperty(Long userId, Long propertyCreatedBy, Long propertyOrganizationId) : boolean
```

### ✅ Endpoints Améliorés

#### 1. **GET /api/identity/users/me**
Amélioré pour inclure le type d'utilisateur et l'organisation principale.

**Réponse :**
```json
{
  "id": 1,
  "email": "user@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "userType": "INDIVIDUAL" | "PROFESSIONAL",
  "organizationId": 123,
  "primaryOrganizationId": 123,
  "roleNames": ["INDIVIDUAL"],
  ...
}
```

#### 2. **GET /api/identity/users/me/type** (Nouveau)
Endpoint dédié pour obtenir uniquement le type d'utilisateur.

**Réponse :**
```json
{
  "type": "INDIVIDUAL" | "PROFESSIONAL",
  "isIndividual": true,
  "isProfessional": false,
  "organizationId": 123
}
```

#### 3. **GET /api/properties/my** (Amélioré)
Filtre automatiquement selon le type d'utilisateur :
- **Individuel** : Retourne seulement les propriétés créées par l'utilisateur (`createdBy = userId`)
- **Professionnel** : Retourne toutes les propriétés de son organisation (`organizationId = userOrganizationId`)

### ✅ Logique de Différenciation

#### Utilisateurs Individuels (Particuliers)
- **Rôle** : `INDIVIDUAL`
- **Organisation** : `null`
- **Propriétés** : Seulement celles créées par l'utilisateur
- **Permissions** :
  - Gérer ses propres propriétés
  - Lire et répondre aux messages reçus
  - Gérer son profil

#### Utilisateurs Professionnels
- **Rôles** : `ORGANIZATION_ADMIN`, `MANAGER`, `USER`
- **Organisation** : Au moins une organisation
- **Propriétés** : Toutes les propriétés de son organisation
- **Permissions** :
  - Gérer toutes les propriétés de l'organisation
  - Gérer les utilisateurs de l'organisation
  - Gérer les workflows et documents

### ✅ DTOs Améliorés

#### UserDTO
Ajout de deux nouveaux champs :
- `userType` : Type d'utilisateur (INDIVIDUAL, PROFESSIONAL, UNKNOWN)
- `primaryOrganizationId` : ID de l'organisation principale

### ✅ Services Property Améliorés

#### PropertyService
Nouvelle méthode `getPropertiesByOrganizationId()` :
- Filtre les propriétés par organisation avec pagination
- Utilise JPA Specifications pour un filtrage efficace
- Supporte le filtrage par statut

#### PropertyController
Amélioration de `getMyProperties()` :
- Détecte automatiquement le type d'utilisateur
- Applique le bon filtre selon le type
- Retourne les bonnes propriétés pour chaque type

## 🔧 Utilisation

### Pour les Utilisateurs Individuels

```bash
# Obtenir mes propriétés (seulement celles que j'ai créées)
GET /api/properties/my

# Obtenir mon type
GET /api/identity/users/me/type
# Réponse: {"type": "INDIVIDUAL", "isIndividual": true, ...}
```

### Pour les Utilisateurs Professionnels

```bash
# Obtenir mes propriétés (toutes celles de mon organisation)
GET /api/properties/my

# Obtenir mon type
GET /api/identity/users/me/type
# Réponse: {"type": "PROFESSIONAL", "isProfessional": true, "organizationId": 123, ...}
```

## 📝 Fichiers Modifiés

1. **services/identity-service/src/main/java/com/realestate/identity/service/UserTypeService.java** (Nouveau)
2. **services/identity-service/src/main/java/com/realestate/identity/controller/UserController.java** (Amélioré)
3. **services/identity-service/src/main/java/com/realestate/identity/dto/UserDTO.java** (Amélioré)
4. **services/property-service/src/main/java/com/realestate/property/controller/PropertyController.java** (Amélioré)
5. **services/property-service/src/main/java/com/realestate/property/service/PropertyService.java** (Amélioré)

## 🎯 Prochaines Étapes Recommandées

1. **Améliorer les permissions** : Créer des annotations personnalisées pour vérifier le type d'utilisateur
2. **Endpoints spécifiques** : Créer des endpoints dédiés pour chaque type (ex: `/api/properties/individual/my`, `/api/properties/organization/my`)
3. **Quotas différents** : Implémenter des quotas différents selon le type d'utilisateur
4. **Fonctionnalités spécifiques** : Ajouter des fonctionnalités spécifiques aux professionnels (statistiques d'équipe, workflows, etc.)

## 🔐 Sécurité

- Les utilisateurs individuels ne peuvent accéder qu'à leurs propres propriétés
- Les utilisateurs professionnels peuvent accéder aux propriétés de leur organisation
- Les vérifications sont faites côté serveur pour garantir la sécurité

