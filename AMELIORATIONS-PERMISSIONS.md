# 🔐 Améliorations du Système de Permissions

## 📋 Résumé des Améliorations

### ✅ Services Créés

#### 1. **PermissionContextService** (`services/identity-service`)
Service centralisé pour gérer le contexte de permissions d'un utilisateur.

**Fonctionnalités :**
- Détermine les organisations accessibles (incluant les sous-organisations récursivement)
- Identifie si l'utilisateur est SUPER_ADMIN ou ADMIN
- Fournit le contexte complet de permissions pour filtrer les données

**Méthodes principales :**
```java
- getPermissionContext(Long userId) : PermissionContext
- getPermissionContextByEmail(String email) : PermissionContext
- canAccessOrganization(Long userId, Long organizationId) : boolean
- canAccessProperty(Long userId, Long propertyCreatedBy, Long propertyOrganizationId) : boolean
- filterAccessibleOrganizationIds(Long userId, Set<Long> organizationIds) : Set<Long>
```

**PermissionContext contient :**
- `userId` : ID de l'utilisateur
- `roleNames` : Rôles de l'utilisateur
- `isSuperAdmin` : Si l'utilisateur est SUPER_ADMIN
- `isAdmin` : Si l'utilisateur est ADMIN
- `accessibleOrganizationIds` : Toutes les organisations accessibles (incluant sous-organisations)
- `directOrganizationIds` : Organisations directes seulement
- `userType` : Type d'utilisateur (INDIVIDUAL, PROFESSIONAL, UNKNOWN)

### ✅ Endpoints Créés/Améliorés

#### 1. **GET /api/identity/users/me/permissions** (Nouveau)
Retourne le contexte complet de permissions de l'utilisateur connecté.

**Réponse :**
```json
{
  "userId": 1,
  "roleNames": ["ORGANIZATION_ADMIN"],
  "superAdmin": false,
  "admin": false,
  "accessibleOrganizationIds": [1, 2, 3, 4],
  "directOrganizationIds": [1],
  "userType": "PROFESSIONAL"
}
```

#### 2. **GET /api/properties** (Amélioré)
Filtre automatiquement les propriétés selon les permissions :
- **Super Admin / Admin** : Voit toutes les propriétés
- **Professionnel** : Voit les propriétés de son organisation + sous-organisations
- **Individuel** : Voit seulement ses propres propriétés

### ✅ Logique de Filtrage

#### Super Admin / Admin
- **Accès** : Toutes les données sans restriction
- **Organisations** : Toutes les organisations
- **Propriétés** : Toutes les propriétés

#### Utilisateurs Professionnels
- **Accès** : Seulement leurs organisations et sous-organisations
- **Organisations** : Organisations directes + toutes les sous-organisations (récursif)
- **Propriétés** : 
  - Propriétés créées par l'utilisateur
  - Propriétés de son organisation
  - Propriétés des sous-organisations

#### Utilisateurs Individuels
- **Accès** : Seulement leurs propres données
- **Organisations** : Aucune
- **Propriétés** : Seulement celles créées par l'utilisateur

### ✅ Specifications JPA Améliorées

#### PropertySpecification
Nouvelles méthodes :
- `hasAnyOrganization(Set<Long> organizationIds)` : Filtre par plusieurs organisations
- `accessibleByUser(Long userId, Set<Long> accessibleOrganizationIds)` : Filtre selon les permissions
- `hasCreatedBy(Long createdBy)` : Filtre par créateur

### ✅ Services Property Améliorés

#### PropertyService
Nouvelles méthodes :
- `getPropertiesWithFiltersAndPermissions()` : Filtre avec pagination selon les permissions
- `getAllPropertiesWithFiltersAndPermissions()` : Filtre sans pagination selon les permissions

## 🔧 Utilisation

### Pour les Super Admins

```bash
# Obtenir toutes les propriétés (sans filtre)
GET /api/properties
Authorization: Bearer <super_admin_token>
```

### Pour les Utilisateurs Professionnels

```bash
# Obtenir le contexte de permissions
GET /api/identity/users/me/permissions
Authorization: Bearer <token>

# Obtenir les propriétés (filtrées automatiquement)
GET /api/properties
Authorization: Bearer <token>
# Retourne seulement les propriétés de l'organisation + sous-organisations
```

### Pour les Utilisateurs Individuels

```bash
# Obtenir les propriétés (filtrées automatiquement)
GET /api/properties
Authorization: Bearer <token>
# Retourne seulement les propriétés créées par l'utilisateur
```

## 📝 Fichiers Modifiés

1. **services/identity-service/.../PermissionContextService.java** (Nouveau)
2. **services/identity-service/.../PermissionContextDTO.java** (Nouveau)
3. **services/identity-service/.../UserController.java** (Amélioré)
4. **common/.../PermissionContextDTO.java** (Nouveau)
5. **common/.../IdentityServiceClient.java** (Amélioré)
6. **services/property-service/.../PropertyController.java** (Amélioré)
7. **services/property-service/.../PropertyService.java** (Amélioré)
8. **services/property-service/.../PropertySpecification.java** (Amélioré)

## 🎯 Fonctionnalités Clés

### Récupération Récursive des Sous-Organisations

Le service `PermissionContextService` récupère récursivement toutes les sous-organisations :

```java
private Set<Long> getAllSubOrganizationIds(Long organizationId) {
    Set<Long> allSubOrgIds = new HashSet<>();
    collectSubOrganizationIds(organizationId, allSubOrgIds);
    return allSubOrgIds;
}
```

### Filtrage Automatique

Les endpoints appliquent automatiquement les filtres selon le contexte de permissions :

```java
if (!isSuperAdmin && !isAdmin && userId != null) {
    // Filtrer selon les organisations accessibles
    spec = spec.and(PropertySpecification.accessibleByUser(userId, accessibleOrgIds));
}
```

## 🔐 Sécurité

- ✅ Les utilisateurs ne peuvent accéder qu'aux données de leurs organisations
- ✅ Les sous-organisations sont incluses automatiquement
- ✅ Le super admin a accès à tout
- ✅ Les vérifications sont faites côté serveur
- ✅ Les filtres sont appliqués avant la requête à la base de données

## 🚀 Prochaines Étapes

1. **Appliquer aux autres services** : Étendre le filtrage aux autres services (documents, workflows, etc.)
2. **Cache** : Mettre en cache le contexte de permissions pour améliorer les performances
3. **Audit** : Logger les accès refusés pour le suivi de sécurité
4. **Tests** : Ajouter des tests unitaires et d'intégration pour valider le système de permissions

