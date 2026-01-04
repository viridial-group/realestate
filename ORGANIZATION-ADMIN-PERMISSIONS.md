# Permissions du rôle ORGANIZATION_ADMIN

## Vue d'ensemble

Le rôle `ORGANIZATION_ADMIN` est conçu pour les administrateurs d'agences qui ont besoin de gérer complètement leur organisation et toutes les sous-organisations.

## Permissions complètes

### ✅ Gestion des utilisateurs
- `USER_READ` : Lire les informations des utilisateurs
- `USER_WRITE` : Créer et modifier les utilisateurs
- `USER_DELETE` : Supprimer les utilisateurs

### ✅ Gestion des organisations
- `ORGANIZATION_READ` : Lire les informations des organisations
- `ORGANIZATION_WRITE` : Créer et modifier les organisations (y compris les sous-organisations)
- `ORGANIZATION_DELETE` : Supprimer les organisations (y compris les sous-organisations)

### ✅ Gestion des rôles
- `ROLE_READ` : Lire les rôles et permissions
- `ROLE_WRITE` : Créer et modifier les rôles personnalisés dans leur organisation
- `ROLE_DELETE` : Supprimer les rôles personnalisés dans leur organisation

### ✅ Gestion des propriétés
- `PROPERTY_READ` : Lire les propriétés
- `PROPERTY_WRITE` : Créer et modifier les propriétés
- `PROPERTY_DELETE` : Supprimer les propriétés

### ✅ Gestion des documents
- `DOCUMENT_READ` : Lire les documents
- `DOCUMENT_WRITE` : Créer et modifier les documents
- `DOCUMENT_DELETE` : Supprimer les documents

### ✅ Gestion des workflows
- `WORKFLOW_READ` : Lire les workflows
- `WORKFLOW_WRITE` : Créer et modifier les workflows
- `WORKFLOW_DELETE` : Supprimer les workflows
- `WORKFLOW_APPROVE` : Approuver les workflows

### ✅ Gestion de la facturation
- `BILLING_READ` : Lire les informations de facturation
- `BILLING_WRITE` : Modifier la facturation de leur organisation

### ✅ Audit
- `AUDIT_READ` : Lire les logs d'audit

### ✅ Notifications
- `NOTIFICATION_READ` : Lire les notifications
- `NOTIFICATION_WRITE` : Créer et envoyer des notifications

### ✅ Messages de contact
- `CONTACT_READ` : Lire les messages de contact
- `CONTACT_WRITE` : Créer et modifier les messages de contact
- `CONTACT_DELETE` : Supprimer les messages de contact

### ✅ Ressources
- `RESOURCE_READ` : Lire les ressources
- `RESOURCE_WRITE` : Créer et modifier les ressources
- `RESOURCE_DELETE` : Supprimer les ressources

## Ce que le rôle ORGANIZATION_ADMIN peut faire

1. **Gérer les utilisateurs** : Créer, modifier, supprimer des utilisateurs dans son organisation
2. **Gérer les organisations** : Créer, modifier, supprimer des sous-organisations
3. **Gérer les rôles** : Créer, modifier, supprimer des rôles personnalisés pour son organisation
4. **Gérer les propriétés** : Accès complet aux propriétés de son organisation
5. **Gérer les documents** : Accès complet aux documents
6. **Gérer les workflows** : Créer, modifier, approuver les workflows
7. **Gérer la facturation** : Voir et modifier la facturation de son organisation
8. **Voir l'audit** : Consulter les logs d'audit de son organisation

## Différences avec ADMIN (SaaS)

| Fonctionnalité | ORGANIZATION_ADMIN | ADMIN (SaaS) |
|----------------|-------------------|--------------|
| Niveau | Organisation | Plateforme (SaaS) |
| Portée | Son organisation + sous-organisations | Toutes les organisations |
| Gestion des rôles | Rôles personnalisés de l'organisation | Tous les rôles |
| Gestion système | ❌ Non | ✅ Oui (partiel) |
| Gestion billing | Son organisation uniquement | Toutes les organisations |

## Attribution automatique

Le rôle `ORGANIZATION_ADMIN` est automatiquement attribué :
- Lors de l'inscription avec abonnement (utilisateur principal de l'organisation)
- Lors de l'ajout d'un utilisateur à une organisation avec `isPrimary = true`

## Mise à jour

Pour mettre à jour les permissions dans une base existante, exécutez :
```sql
-- Ajouter ROLE_DELETE si manquant
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'ORGANIZATION_ADMIN'
  AND p.name = 'ROLE_DELETE'
ON CONFLICT DO NOTHING;
```

