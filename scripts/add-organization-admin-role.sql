-- =====================================================
-- Script pour ajouter le rôle ORGANIZATION_ADMIN
-- =====================================================
-- Ce rôle est pour les administrateurs d'agences qui ont
-- les permissions pour gérer leur organisation et toutes les sous-organisations
-- Ne pas confondre avec ADMIN (qui est pour le SaaS)

-- Créer le rôle ORGANIZATION_ADMIN s'il n'existe pas
INSERT INTO roles (name, description, created_at, updated_at)
VALUES (
    'ORGANIZATION_ADMIN',
    'Organization Administrator - Full access to manage organization and all sub-organizations',
    NOW(),
    NOW()
)
ON CONFLICT (name) DO UPDATE
SET description = EXCLUDED.description,
    updated_at = NOW();

-- Assigner toutes les permissions au rôle ORGANIZATION_ADMIN
-- (sauf SYSTEM_MANAGE et FULL_ACCESS qui sont réservés au SUPER_ADMIN)
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'ORGANIZATION_ADMIN'
  AND p.name IN (
    'USER_READ', 'USER_WRITE', 'USER_DELETE',
    'ORGANIZATION_READ', 'ORGANIZATION_WRITE', 'ORGANIZATION_DELETE',
    'PROPERTY_READ', 'PROPERTY_WRITE', 'PROPERTY_DELETE',
    'DOCUMENT_READ', 'DOCUMENT_WRITE', 'DOCUMENT_DELETE',
    'WORKFLOW_READ', 'WORKFLOW_WRITE', 'WORKFLOW_DELETE', 'WORKFLOW_APPROVE',
    'BILLING_READ', 'BILLING_WRITE',
    'AUDIT_READ',
    'NOTIFICATION_READ', 'NOTIFICATION_WRITE',
    'CONTACT_READ', 'CONTACT_WRITE', 'CONTACT_DELETE',
    'ROLE_READ', 'ROLE_WRITE', 'ROLE_DELETE',  -- Can manage roles within their organization (create, update, delete)
    'RESOURCE_READ', 'RESOURCE_WRITE', 'RESOURCE_DELETE'
  )
ON CONFLICT DO NOTHING;

-- Vérification
SELECT 
    r.name as role_name,
    r.description,
    COUNT(rp.permission_id) as permission_count
FROM roles r
LEFT JOIN role_permissions rp ON r.id = rp.role_id
WHERE r.name = 'ORGANIZATION_ADMIN'
GROUP BY r.id, r.name, r.description;

-- Afficher les permissions assignées
SELECT 
    r.name as role_name,
    p.name as permission_name,
    p.resource,
    p.action,
    p.description
FROM roles r
JOIN role_permissions rp ON r.id = rp.role_id
JOIN permissions p ON rp.permission_id = p.id
WHERE r.name = 'ORGANIZATION_ADMIN'
ORDER BY p.resource, p.action;

