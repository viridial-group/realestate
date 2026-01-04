# Mise à jour : Rôle ORGANIZATION_ADMIN

## Résumé

Un nouveau rôle `ORGANIZATION_ADMIN` a été créé pour les administrateurs d'agences. Ce rôle :
- ✅ Donne les permissions pour gérer l'organisation et toutes les sous-organisations
- ✅ Est automatiquement assigné lors de l'inscription avec abonnement
- ✅ Ne doit pas être confondu avec `ADMIN` (qui est pour le SaaS)

## Différences entre les rôles

| Rôle | Niveau | Description |
|------|--------|-------------|
| **SUPER_ADMIN** | SaaS | Accès total à toutes les organisations et au système |
| **ADMIN** | SaaS | Administrateur de la plateforme (pas d'accès système) |
| **ORGANIZATION_ADMIN** | Organisation | Administrateur d'agence - Gère son organisation et sous-organisations |
| **MANAGER** | Organisation | Manager avec permissions read/write |
| **USER** | Organisation | Utilisateur standard avec permissions read seulement |

## Modifications apportées

### 1. Backend (Java)

#### `DataInitializer.java`
- ✅ Ajout du rôle `ORGANIZATION_ADMIN` avec toutes les permissions nécessaires
- ✅ Permissions incluent : gestion des utilisateurs, organisations, propriétés, documents, workflows, billing, etc.

#### `RoleService.java`
- ✅ Ajout de la méthode `assignRoleToUserByName()` pour assigner un rôle par nom

#### `OrganizationUserService.java`
- ✅ Modification de `addUserToOrganization()` pour assigner automatiquement le rôle `ORGANIZATION_ADMIN` lorsque `isPrimary = true`
- ✅ Cela signifie que le créateur d'une organisation reçoit automatiquement ce rôle

### 2. Scripts SQL

#### `add-organization-admin-role.sql`
- ✅ Script pour ajouter le rôle dans une base existante
- ✅ Assignation des permissions

#### `seed-database-complete.sql`
- ✅ Mise à jour pour inclure le rôle `ORGANIZATION_ADMIN`
- ✅ Assignation des permissions

## Utilisation

### Pour les nouvelles inscriptions

Le rôle est automatiquement assigné lors de l'inscription avec abonnement. Le processus dans `Subscribe.vue` :

1. Création de l'utilisateur
2. Création de l'organisation
3. Assignation de l'utilisateur à l'organisation avec `isPrimary = true`
4. **→ Le rôle ORGANIZATION_ADMIN est automatiquement assigné**

### Pour les utilisateurs existants

Exécutez le script SQL pour ajouter le rôle :

```bash
psql -h localhost -U postgres -d realestate -f scripts/add-organization-admin-role.sql
```

Puis assignez le rôle aux administrateurs d'agences existants :

```sql
-- Assigner le rôle ORGANIZATION_ADMIN à un utilisateur
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email = 'user@example.com' AND r.name = 'ORGANIZATION_ADMIN'
ON CONFLICT DO NOTHING;
```

### Pour h.sassa@yahoo.fr

Exécutez le script complet :

```bash
psql -h localhost -U postgres -d realestate -f scripts/fix-user-h.sassa-complete.sql
```

Puis assignez le rôle ORGANIZATION_ADMIN :

```sql
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email = 'h.sassa@yahoo.fr' AND r.name = 'ORGANIZATION_ADMIN'
ON CONFLICT DO NOTHING;
```

## Vérification

Pour vérifier qu'un utilisateur a le rôle ORGANIZATION_ADMIN :

```sql
SELECT 
    u.email,
    r.name as role_name,
    r.description
FROM users u
JOIN user_roles ur ON u.id = ur.user_id
JOIN roles r ON ur.role_id = r.id
WHERE u.email = 'user@example.com' AND r.name = 'ORGANIZATION_ADMIN';
```

## Permissions du rôle ORGANIZATION_ADMIN

Le rôle a accès à :
- ✅ Gestion des utilisateurs (READ, WRITE, DELETE)
- ✅ Gestion des organisations (READ, WRITE, DELETE) - inclut les sous-organisations
- ✅ Gestion des propriétés (READ, WRITE, DELETE)
- ✅ Gestion des documents (READ, WRITE, DELETE)
- ✅ Gestion des workflows (READ, WRITE, DELETE, APPROVE)
- ✅ Gestion de la facturation (READ, WRITE)
- ✅ Consultation des audits (READ)
- ✅ Gestion des notifications (READ, WRITE)
- ✅ Gestion des contacts (READ, WRITE, DELETE)
- ✅ Gestion des rôles (READ, WRITE) - pour son organisation
- ✅ Gestion des ressources (READ, WRITE, DELETE)

**Exclusions :**
- ❌ SYSTEM_MANAGE (réservé au SUPER_ADMIN)
- ❌ FULL_ACCESS (réservé au SUPER_ADMIN)

