-- =====================================================
-- Script SQL COMPLET pour restructurer et peupler la base de données
-- Simule des agences immobilières avec toutes les entités
-- =====================================================

-- =====================================================
-- 0. NETTOYER TOUTES LES TABLES (RESTRUCTURATION)
-- =====================================================

-- Désactiver temporairement les contraintes de clés étrangères
SET session_replication_role = 'replica';

-- Supprimer les données dans l'ordre inverse des dépendances
TRUNCATE TABLE organization_users CASCADE;
TRUNCATE TABLE teams CASCADE;
TRUNCATE TABLE properties CASCADE;
TRUNCATE TABLE user_roles CASCADE;
TRUNCATE TABLE users CASCADE;
TRUNCATE TABLE organizations CASCADE;
TRUNCATE TABLE roles CASCADE;
TRUNCATE TABLE permissions CASCADE;
TRUNCATE TABLE role_permissions CASCADE;

-- Réactiver les contraintes
SET session_replication_role = 'origin';

-- =====================================================
-- 1. CRÉER LES PERMISSIONS
-- =====================================================

INSERT INTO permissions (name, resource, action, description, created_at, updated_at)
VALUES 
    ('USER_READ', 'user', 'READ', 'Read user information', NOW(), NOW()),
    ('USER_WRITE', 'user', 'WRITE', 'Create and update users', NOW(), NOW()),
    ('USER_DELETE', 'user', 'DELETE', 'Delete users', NOW(), NOW()),
    ('ORGANIZATION_READ', 'organization', 'READ', 'Read organization information', NOW(), NOW()),
    ('ORGANIZATION_WRITE', 'organization', 'WRITE', 'Create and update organizations', NOW(), NOW()),
    ('PROPERTY_READ', 'property', 'READ', 'Read property information', NOW(), NOW()),
    ('PROPERTY_WRITE', 'property', 'WRITE', 'Create and update properties', NOW(), NOW())
ON CONFLICT (name) DO NOTHING;

-- =====================================================
-- 2. CRÉER LES RÔLES
-- =====================================================

INSERT INTO roles (name, description, created_at, updated_at)
VALUES 
    ('ADMIN', 'Administrator with full access', NOW(), NOW()),
    ('MANAGER', 'Manager with elevated access', NOW(), NOW()),
    ('USER', 'Standard user with basic access', NOW(), NOW())
ON CONFLICT (name) DO NOTHING;

-- =====================================================
-- 3. ASSOCIER LES PERMISSIONS AUX RÔLES
-- =====================================================

-- ADMIN: Toutes les permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'ADMIN'
ON CONFLICT DO NOTHING;

-- MANAGER: Read et Write (pas Delete)
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'MANAGER' 
  AND p.name IN ('USER_READ', 'USER_WRITE', 'ORGANIZATION_READ', 'ORGANIZATION_WRITE', 'PROPERTY_READ', 'PROPERTY_WRITE')
ON CONFLICT DO NOTHING;

-- USER: Read seulement
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'USER' 
  AND p.name IN ('USER_READ', 'ORGANIZATION_READ', 'PROPERTY_READ')
ON CONFLICT DO NOTHING;

-- =====================================================
-- 4. CRÉER L'ADMIN SAAS PRINCIPAL
-- =====================================================

-- Note: Le mot de passe est encodé avec BCrypt pour "admin123"
-- Hash BCrypt pour "admin123": $2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi
-- Pour générer un nouveau hash: https://bcrypt-generator.com/ ou utiliser BCryptPasswordEncoder

INSERT INTO users (email, password, first_name, last_name, enabled, account_non_expired, account_non_locked, credentials_non_expired, created_at, updated_at)
VALUES ('admin@viridial.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Admin', 'SaaS', true, true, true, true, NOW(), NOW())
ON CONFLICT (email) DO UPDATE 
SET password = EXCLUDED.password,
    first_name = EXCLUDED.first_name,
    last_name = EXCLUDED.last_name,
    enabled = EXCLUDED.enabled,
    updated_at = NOW();

-- Assigner le rôle ADMIN à l'admin principal
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email = 'admin@viridial.com' AND r.name = 'ADMIN'
ON CONFLICT DO NOTHING;

-- =====================================================
-- 5. CRÉER DES ORGANISATIONS
-- =====================================================

INSERT INTO organizations (name, description, domain, active, created_at, updated_at)
VALUES 
    ('Immobilier Paris', 'Agence immobilière spécialisée dans le marché parisien', 'paris-immobilier.fr', true, NOW(), NOW()),
    ('Real Estate Lyon', 'Agence immobilière à Lyon et région', 'lyon-realestate.fr', true, NOW(), NOW()),
    ('Property Marseille', 'Agence immobilière sur la Côte d''Azur', 'marseille-property.fr', true, NOW(), NOW()),
    ('Bordeaux Immobilier', 'Agence immobilière à Bordeaux', 'bordeaux-immobilier.fr', true, NOW(), NOW()),
    ('Nice Properties', 'Agence immobilière à Nice', 'nice-properties.fr', true, NOW(), NOW())
ON CONFLICT (name) DO NOTHING;

-- =====================================================
-- 6. CRÉER DES UTILISATEURS POUR CHAQUE AGENCE
-- =====================================================

-- Agence 1: Immobilier Paris
INSERT INTO users (email, password, first_name, last_name, enabled, account_non_expired, account_non_locked, credentials_non_expired, created_at, updated_at)
VALUES 
    ('directeur@paris-immobilier.fr', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Jean', 'Dupont', true, true, true, true, NOW(), NOW()),
    ('manager@paris-immobilier.fr', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Marie', 'Martin', true, true, true, true, NOW(), NOW()),
    ('agent1@paris-immobilier.fr', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Pierre', 'Bernard', true, true, true, true, NOW(), NOW()),
    ('agent2@paris-immobilier.fr', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Sophie', 'Dubois', true, true, true, true, NOW(), NOW()),
    ('agent3@paris-immobilier.fr', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Lucas', 'Moreau', true, true, true, true, NOW(), NOW())
ON CONFLICT (email) DO NOTHING;

-- Agence 2: Real Estate Lyon
INSERT INTO users (email, password, first_name, last_name, enabled, account_non_expired, account_non_locked, credentials_non_expired, created_at, updated_at)
VALUES 
    ('directeur@lyon-realestate.fr', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Claire', 'Laurent', true, true, true, true, NOW(), NOW()),
    ('manager@lyon-realestate.fr', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Thomas', 'Simon', true, true, true, true, NOW(), NOW()),
    ('agent1@lyon-realestate.fr', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Emma', 'Michel', true, true, true, true, NOW(), NOW()),
    ('agent2@lyon-realestate.fr', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Hugo', 'Garcia', true, true, true, true, NOW(), NOW())
ON CONFLICT (email) DO NOTHING;

-- Agence 3: Property Marseille
INSERT INTO users (email, password, first_name, last_name, enabled, account_non_expired, account_non_locked, credentials_non_expired, created_at, updated_at)
VALUES 
    ('directeur@marseille-property.fr', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Nathalie', 'David', true, true, true, true, NOW(), NOW()),
    ('manager@marseille-property.fr', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Antoine', 'Bertrand', true, true, true, true, NOW(), NOW()),
    ('agent1@marseille-property.fr', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Julie', 'Roux', true, true, true, true, NOW(), NOW()),
    ('agent2@marseille-property.fr', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Maxime', 'Vincent', true, true, true, true, NOW(), NOW()),
    ('agent3@marseille-property.fr', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Camille', 'Fournier', true, true, true, true, NOW(), NOW())
ON CONFLICT (email) DO NOTHING;

-- Agence 4: Bordeaux Immobilier
INSERT INTO users (email, password, first_name, last_name, enabled, account_non_expired, account_non_locked, credentials_non_expired, created_at, updated_at)
VALUES 
    ('directeur@bordeaux-immobilier.fr', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Isabelle', 'Girard', true, true, true, true, NOW(), NOW()),
    ('manager@bordeaux-immobilier.fr', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Julien', 'Bonnet', true, true, true, true, NOW(), NOW()),
    ('agent1@bordeaux-immobilier.fr', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Laura', 'Dupuis', true, true, true, true, NOW(), NOW())
ON CONFLICT (email) DO NOTHING;

-- Agence 5: Nice Properties
INSERT INTO users (email, password, first_name, last_name, enabled, account_non_expired, account_non_locked, credentials_non_expired, created_at, updated_at)
VALUES 
    ('directeur@nice-properties.fr', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Philippe', 'Lambert', true, true, true, true, NOW(), NOW()),
    ('agent1@nice-properties.fr', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Amélie', 'Fontaine', true, true, true, true, NOW(), NOW()),
    ('agent2@nice-properties.fr', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Nicolas', 'Rousseau', true, true, true, true, NOW(), NOW())
ON CONFLICT (email) DO NOTHING;

-- Freelances (sans organisation)
INSERT INTO users (email, password, first_name, last_name, enabled, account_non_expired, account_non_locked, credentials_non_expired, created_at, updated_at)
VALUES 
    ('freelance1@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Sarah', 'Lefebvre', true, true, true, true, NOW(), NOW()),
    ('freelance2@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Olivier', 'Blanc', true, true, true, true, NOW(), NOW()),
    ('freelance3@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Céline', 'Garnier', true, true, true, true, NOW(), NOW())
ON CONFLICT (email) DO NOTHING;

-- =====================================================
-- 7. ASSIGNER DES RÔLES AUX UTILISATEURS
-- =====================================================

-- Directeurs (ADMIN)
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email IN (
    'directeur@paris-immobilier.fr',
    'directeur@lyon-realestate.fr',
    'directeur@marseille-property.fr',
    'directeur@bordeaux-immobilier.fr',
    'directeur@nice-properties.fr'
) AND r.name = 'ADMIN'
ON CONFLICT DO NOTHING;

-- Managers (MANAGER)
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, COALESCE(rm.id, ru.id)
FROM users u
CROSS JOIN LATERAL (
    SELECT id FROM roles WHERE name = 'MANAGER' LIMIT 1
) rm
CROSS JOIN LATERAL (
    SELECT id FROM roles WHERE name = 'USER' LIMIT 1
) ru
WHERE u.email LIKE '%manager@%'
ON CONFLICT DO NOTHING;

-- Agents et freelances (USER)
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE (u.email LIKE '%agent%' OR u.email LIKE '%freelance%')
  AND r.name = 'USER'
ON CONFLICT DO NOTHING;

-- =====================================================
-- 8. CRÉER DES ÉQUIPES
-- =====================================================

-- Équipes pour Immobilier Paris
INSERT INTO teams (name, description, organization_id, active, created_at, updated_at)
SELECT 'Équipe Ventes', 'Équipe dédiée aux ventes', o.id, true, NOW(), NOW()
FROM organizations o WHERE o.name = 'Immobilier Paris'
ON CONFLICT DO NOTHING;

INSERT INTO teams (name, description, organization_id, active, created_at, updated_at)
SELECT 'Équipe Locations', 'Équipe dédiée aux locations', o.id, true, NOW(), NOW()
FROM organizations o WHERE o.name = 'Immobilier Paris'
ON CONFLICT DO NOTHING;

-- Équipe pour Real Estate Lyon
INSERT INTO teams (name, description, organization_id, active, created_at, updated_at)
SELECT 'Équipe Ventes', 'Équipe dédiée aux ventes', o.id, true, NOW(), NOW()
FROM organizations o WHERE o.name = 'Real Estate Lyon'
ON CONFLICT DO NOTHING;

-- Équipes pour Property Marseille
INSERT INTO teams (name, description, organization_id, active, created_at, updated_at)
SELECT 'Équipe Ventes', 'Équipe dédiée aux ventes', o.id, true, NOW(), NOW()
FROM organizations o WHERE o.name = 'Property Marseille'
ON CONFLICT DO NOTHING;

INSERT INTO teams (name, description, organization_id, active, created_at, updated_at)
SELECT 'Équipe Locations', 'Équipe dédiée aux locations', o.id, true, NOW(), NOW()
FROM organizations o WHERE o.name = 'Property Marseille'
ON CONFLICT DO NOTHING;

-- =====================================================
-- 9. ASSOCIER LES UTILISATEURS AUX ORGANISATIONS (organization_users)
-- La première affectation de chaque utilisateur est marquée comme principale (is_primary = TRUE)
-- =====================================================

-- Immobilier Paris
-- Directeur, Manager, Agent1 -> Équipe Ventes (affectation principale)
INSERT INTO organization_users (user_id, organization_id, team_id, active, is_primary, created_at, updated_at)
SELECT u.id, o.id, t.id, true, true, NOW(), NOW()
FROM users u, organizations o
LEFT JOIN teams t ON t.organization_id = o.id AND t.name = 'Équipe Ventes'
WHERE u.email = 'directeur@paris-immobilier.fr' AND o.name = 'Immobilier Paris'
ON CONFLICT DO NOTHING;

INSERT INTO organization_users (user_id, organization_id, team_id, active, is_primary, created_at, updated_at)
SELECT u.id, o.id, t.id, true, true, NOW(), NOW()
FROM users u, organizations o
LEFT JOIN teams t ON t.organization_id = o.id AND t.name = 'Équipe Ventes'
WHERE u.email = 'manager@paris-immobilier.fr' AND o.name = 'Immobilier Paris'
ON CONFLICT DO NOTHING;

INSERT INTO organization_users (user_id, organization_id, team_id, active, is_primary, created_at, updated_at)
SELECT u.id, o.id, t.id, true, true, NOW(), NOW()
FROM users u, organizations o
LEFT JOIN teams t ON t.organization_id = o.id AND t.name = 'Équipe Ventes'
WHERE u.email = 'agent1@paris-immobilier.fr' AND o.name = 'Immobilier Paris'
ON CONFLICT DO NOTHING;

-- Agent2, Agent3 -> Équipe Locations (affectation principale)
INSERT INTO organization_users (user_id, organization_id, team_id, active, is_primary, created_at, updated_at)
SELECT u.id, o.id, t.id, true, true, NOW(), NOW()
FROM users u, organizations o
LEFT JOIN teams t ON t.organization_id = o.id AND t.name = 'Équipe Locations'
WHERE u.email = 'agent2@paris-immobilier.fr' AND o.name = 'Immobilier Paris'
ON CONFLICT DO NOTHING;

INSERT INTO organization_users (user_id, organization_id, team_id, active, is_primary, created_at, updated_at)
SELECT u.id, o.id, t.id, true, true, NOW(), NOW()
FROM users u, organizations o
LEFT JOIN teams t ON t.organization_id = o.id AND t.name = 'Équipe Locations'
WHERE u.email = 'agent3@paris-immobilier.fr' AND o.name = 'Immobilier Paris'
ON CONFLICT DO NOTHING;

-- Real Estate Lyon (tous -> Équipe Ventes, affectation principale)
INSERT INTO organization_users (user_id, organization_id, team_id, active, is_primary, created_at, updated_at)
SELECT u.id, o.id, t.id, true, true, NOW(), NOW()
FROM users u, organizations o
LEFT JOIN teams t ON t.organization_id = o.id AND t.name = 'Équipe Ventes'
WHERE u.email = 'directeur@lyon-realestate.fr' AND o.name = 'Real Estate Lyon'
ON CONFLICT DO NOTHING;

INSERT INTO organization_users (user_id, organization_id, team_id, active, is_primary, created_at, updated_at)
SELECT u.id, o.id, t.id, true, true, NOW(), NOW()
FROM users u, organizations o
LEFT JOIN teams t ON t.organization_id = o.id AND t.name = 'Équipe Ventes'
WHERE u.email = 'manager@lyon-realestate.fr' AND o.name = 'Real Estate Lyon'
ON CONFLICT DO NOTHING;

INSERT INTO organization_users (user_id, organization_id, team_id, active, is_primary, created_at, updated_at)
SELECT u.id, o.id, t.id, true, true, NOW(), NOW()
FROM users u, organizations o
LEFT JOIN teams t ON t.organization_id = o.id AND t.name = 'Équipe Ventes'
WHERE u.email = 'agent1@lyon-realestate.fr' AND o.name = 'Real Estate Lyon'
ON CONFLICT DO NOTHING;

INSERT INTO organization_users (user_id, organization_id, team_id, active, is_primary, created_at, updated_at)
SELECT u.id, o.id, t.id, true, true, NOW(), NOW()
FROM users u, organizations o
LEFT JOIN teams t ON t.organization_id = o.id AND t.name = 'Équipe Ventes'
WHERE u.email = 'agent2@lyon-realestate.fr' AND o.name = 'Real Estate Lyon'
ON CONFLICT DO NOTHING;

-- Property Marseille
-- Directeur, Manager, Agent1 -> Équipe Ventes (affectation principale)
INSERT INTO organization_users (user_id, organization_id, team_id, active, is_primary, created_at, updated_at)
SELECT u.id, o.id, t.id, true, true, NOW(), NOW()
FROM users u, organizations o
LEFT JOIN teams t ON t.organization_id = o.id AND t.name = 'Équipe Ventes'
WHERE u.email = 'directeur@marseille-property.fr' AND o.name = 'Property Marseille'
ON CONFLICT DO NOTHING;

INSERT INTO organization_users (user_id, organization_id, team_id, active, is_primary, created_at, updated_at)
SELECT u.id, o.id, t.id, true, true, NOW(), NOW()
FROM users u, organizations o
LEFT JOIN teams t ON t.organization_id = o.id AND t.name = 'Équipe Ventes'
WHERE u.email = 'manager@marseille-property.fr' AND o.name = 'Property Marseille'
ON CONFLICT DO NOTHING;

INSERT INTO organization_users (user_id, organization_id, team_id, active, is_primary, created_at, updated_at)
SELECT u.id, o.id, t.id, true, true, NOW(), NOW()
FROM users u, organizations o
LEFT JOIN teams t ON t.organization_id = o.id AND t.name = 'Équipe Ventes'
WHERE u.email = 'agent1@marseille-property.fr' AND o.name = 'Property Marseille'
ON CONFLICT DO NOTHING;

-- Agent2, Agent3 -> Équipe Locations (affectation principale)
INSERT INTO organization_users (user_id, organization_id, team_id, active, is_primary, created_at, updated_at)
SELECT u.id, o.id, t.id, true, true, NOW(), NOW()
FROM users u, organizations o
LEFT JOIN teams t ON t.organization_id = o.id AND t.name = 'Équipe Locations'
WHERE u.email = 'agent2@marseille-property.fr' AND o.name = 'Property Marseille'
ON CONFLICT DO NOTHING;

INSERT INTO organization_users (user_id, organization_id, team_id, active, is_primary, created_at, updated_at)
SELECT u.id, o.id, t.id, true, true, NOW(), NOW()
FROM users u, organizations o
LEFT JOIN teams t ON t.organization_id = o.id AND t.name = 'Équipe Locations'
WHERE u.email = 'agent3@marseille-property.fr' AND o.name = 'Property Marseille'
ON CONFLICT DO NOTHING;

-- Bordeaux Immobilier (sans équipe, affectation principale)
INSERT INTO organization_users (user_id, organization_id, active, is_primary, created_at, updated_at)
SELECT u.id, o.id, true, true, NOW(), NOW()
FROM users u, organizations o
WHERE u.email = 'directeur@bordeaux-immobilier.fr' AND o.name = 'Bordeaux Immobilier'
ON CONFLICT DO NOTHING;

INSERT INTO organization_users (user_id, organization_id, active, is_primary, created_at, updated_at)
SELECT u.id, o.id, true, true, NOW(), NOW()
FROM users u, organizations o
WHERE u.email = 'manager@bordeaux-immobilier.fr' AND o.name = 'Bordeaux Immobilier'
ON CONFLICT DO NOTHING;

INSERT INTO organization_users (user_id, organization_id, active, is_primary, created_at, updated_at)
SELECT u.id, o.id, true, true, NOW(), NOW()
FROM users u, organizations o
WHERE u.email = 'agent1@bordeaux-immobilier.fr' AND o.name = 'Bordeaux Immobilier'
ON CONFLICT DO NOTHING;

-- Nice Properties (sans équipe, affectation principale)
INSERT INTO organization_users (user_id, organization_id, active, is_primary, created_at, updated_at)
SELECT u.id, o.id, true, true, NOW(), NOW()
FROM users u, organizations o
WHERE u.email = 'directeur@nice-properties.fr' AND o.name = 'Nice Properties'
ON CONFLICT DO NOTHING;

INSERT INTO organization_users (user_id, organization_id, active, is_primary, created_at, updated_at)
SELECT u.id, o.id, true, true, NOW(), NOW()
FROM users u, organizations o
WHERE u.email = 'agent1@nice-properties.fr' AND o.name = 'Nice Properties'
ON CONFLICT DO NOTHING;

INSERT INTO organization_users (user_id, organization_id, active, is_primary, created_at, updated_at)
SELECT u.id, o.id, true, true, NOW(), NOW()
FROM users u, organizations o
WHERE u.email = 'agent2@nice-properties.fr' AND o.name = 'Nice Properties'
ON CONFLICT DO NOTHING;

-- =====================================================
-- 10. CRÉER DES PROPRIÉTÉS
-- =====================================================

-- Propriétés pour Immobilier Paris (org_id = 1)
INSERT INTO properties (reference, title, description, type, status, price, currency, surface, rooms, bedrooms, bathrooms, address, city, postal_code, country, organization_id, assigned_user_id, created_by, active, created_at, updated_at)
VALUES 
    ('PROP-PARIS-001', 'Appartement luxueux à Paris', 'Magnifique appartement situé dans le cœur de Paris. Idéal pour une famille.', 'APARTMENT', 'PUBLISHED', 450000.00, 'EUR', 85.5, 4, 3, 2, '15 Rue de la République', 'Paris', '75001', 'France', 
     (SELECT id FROM organizations WHERE name = 'Immobilier Paris'),
     (SELECT id FROM users WHERE email = 'agent1@paris-immobilier.fr'),
     (SELECT id FROM users WHERE email = 'directeur@paris-immobilier.fr'), true, NOW(), NOW()),
    ('PROP-PARIS-002', 'Maison moderne à Paris', 'Belle maison moderne avec jardin. Parfait pour une famille.', 'HOUSE', 'PUBLISHED', 650000.00, 'EUR', 120.0, 5, 4, 3, '42 Avenue des Champs', 'Paris', '75008', 'France',
     (SELECT id FROM organizations WHERE name = 'Immobilier Paris'),
     (SELECT id FROM users WHERE email = 'agent2@paris-immobilier.fr'),
     (SELECT id FROM users WHERE email = 'directeur@paris-immobilier.fr'), true, NOW(), NOW()),
    ('PROP-PARIS-003', 'Studio cosy Paris', 'Studio cosy et bien situé, idéal pour étudiant ou jeune actif.', 'APARTMENT', 'PUBLISHED', 180000.00, 'EUR', 35.0, 1, 0, 1, '8 Boulevard Saint-Michel', 'Paris', '75005', 'France',
     (SELECT id FROM organizations WHERE name = 'Immobilier Paris'),
     (SELECT id FROM users WHERE email = 'agent3@paris-immobilier.fr'),
     (SELECT id FROM users WHERE email = 'manager@paris-immobilier.fr'), true, NOW(), NOW()),
    ('PROP-PARIS-004', 'Loft design Paris', 'Loft design dans quartier branché. Caractère et modernité.', 'APARTMENT', 'PUBLISHED', 520000.00, 'EUR', 95.0, 3, 2, 2, '25 Rue de Rivoli', 'Paris', '75004', 'France',
     (SELECT id FROM organizations WHERE name = 'Immobilier Paris'),
     (SELECT id FROM users WHERE email = 'agent1@paris-immobilier.fr'),
     (SELECT id FROM users WHERE email = 'directeur@paris-immobilier.fr'), true, NOW(), NOW()),
    ('PROP-PARIS-005', 'Terrain constructible Paris', 'Terrain constructible de 300m², idéal pour projet immobilier.', 'LAND', 'DRAFT', 250000.00, 'EUR', 300.0, 0, 0, 0, '100 Avenue Montaigne', 'Paris', '75016', 'France',
     (SELECT id FROM organizations WHERE name = 'Immobilier Paris'),
     (SELECT id FROM users WHERE email = 'agent2@paris-immobilier.fr'),
     (SELECT id FROM users WHERE email = 'directeur@paris-immobilier.fr'), true, NOW(), NOW())
ON CONFLICT (reference) DO NOTHING;

-- Propriétés pour Real Estate Lyon (org_id = 2)
INSERT INTO properties (reference, title, description, type, status, price, currency, surface, rooms, bedrooms, bathrooms, address, city, postal_code, country, organization_id, assigned_user_id, created_by, active, created_at, updated_at)
VALUES 
    ('PROP-LYON-001', 'Appartement T3 Lyon', 'Appartement lumineux T3 dans le centre de Lyon.', 'APARTMENT', 'PUBLISHED', 280000.00, 'EUR', 65.0, 3, 2, 1, '12 Rue de la République', 'Lyon', '69001', 'France',
     (SELECT id FROM organizations WHERE name = 'Real Estate Lyon'),
     (SELECT id FROM users WHERE email = 'agent1@lyon-realestate.fr'),
     (SELECT id FROM users WHERE email = 'directeur@lyon-realestate.fr'), true, NOW(), NOW()),
    ('PROP-LYON-002', 'Maison avec jardin Lyon', 'Belle maison avec jardin, quartier calme.', 'HOUSE', 'PUBLISHED', 420000.00, 'EUR', 110.0, 4, 3, 2, '45 Rue du Faubourg', 'Lyon', '69003', 'France',
     (SELECT id FROM organizations WHERE name = 'Real Estate Lyon'),
     (SELECT id FROM users WHERE email = 'agent2@lyon-realestate.fr'),
     (SELECT id FROM users WHERE email = 'directeur@lyon-realestate.fr'), true, NOW(), NOW()),
    ('PROP-LYON-003', 'Local commercial Lyon', 'Local commercial idéal pour commerce ou bureau.', 'COMMERCIAL', 'PUBLISHED', 350000.00, 'EUR', 150.0, 2, 0, 1, '78 Avenue de la République', 'Lyon', '69002', 'France',
     (SELECT id FROM organizations WHERE name = 'Real Estate Lyon'),
     (SELECT id FROM users WHERE email = 'agent1@lyon-realestate.fr'),
     (SELECT id FROM users WHERE email = 'manager@lyon-realestate.fr'), true, NOW(), NOW()),
    ('PROP-LYON-004', 'Appartement T2 Lyon', 'Appartement T2 rénové, proche transports.', 'APARTMENT', 'SOLD', 195000.00, 'EUR', 45.0, 2, 1, 1, '30 Rue Garibaldi', 'Lyon', '69006', 'France',
     (SELECT id FROM organizations WHERE name = 'Real Estate Lyon'),
     (SELECT id FROM users WHERE email = 'agent2@lyon-realestate.fr'),
     (SELECT id FROM users WHERE email = 'directeur@lyon-realestate.fr'), true, NOW(), NOW())
ON CONFLICT (reference) DO NOTHING;

-- Propriétés pour Property Marseille (org_id = 3)
INSERT INTO properties (reference, title, description, type, status, price, currency, surface, rooms, bedrooms, bathrooms, address, city, postal_code, country, organization_id, assigned_user_id, created_by, active, created_at, updated_at)
VALUES 
    ('PROP-MARSEILLE-001', 'Villa avec piscine Marseille', 'Superbe villa avec piscine, vue mer.', 'HOUSE', 'PUBLISHED', 850000.00, 'EUR', 180.0, 6, 4, 3, '15 Corniche Kennedy', 'Marseille', '13007', 'France',
     (SELECT id FROM organizations WHERE name = 'Property Marseille'),
     (SELECT id FROM users WHERE email = 'agent1@marseille-property.fr'),
     (SELECT id FROM users WHERE email = 'directeur@marseille-property.fr'), true, NOW(), NOW()),
    ('PROP-MARSEILLE-002', 'Appartement vue mer Marseille', 'Appartement avec vue mer, proche plage.', 'APARTMENT', 'PUBLISHED', 380000.00, 'EUR', 70.0, 3, 2, 2, '22 Rue de la République', 'Marseille', '13001', 'France',
     (SELECT id FROM organizations WHERE name = 'Property Marseille'),
     (SELECT id FROM users WHERE email = 'agent2@marseille-property.fr'),
     (SELECT id FROM users WHERE email = 'directeur@marseille-property.fr'), true, NOW(), NOW()),
    ('PROP-MARSEILLE-003', 'Studio Marseille centre', 'Studio bien situé, proche métro.', 'APARTMENT', 'RENTED', 95000.00, 'EUR', 28.0, 1, 0, 1, '8 Cours Belsunce', 'Marseille', '13001', 'France',
     (SELECT id FROM organizations WHERE name = 'Property Marseille'),
     (SELECT id FROM users WHERE email = 'agent3@marseille-property.fr'),
     (SELECT id FROM users WHERE email = 'manager@marseille-property.fr'), true, NOW(), NOW()),
    ('PROP-MARSEILLE-004', 'Terrain constructible Marseille', 'Terrain de 500m², vue panoramique.', 'LAND', 'PUBLISHED', 180000.00, 'EUR', 500.0, 0, 0, 0, '50 Chemin des Goudes', 'Marseille', '13008', 'France',
     (SELECT id FROM organizations WHERE name = 'Property Marseille'),
     (SELECT id FROM users WHERE email = 'agent1@marseille-property.fr'),
     (SELECT id FROM users WHERE email = 'directeur@marseille-property.fr'), true, NOW(), NOW()),
    ('PROP-MARSEILLE-005', 'Maison de ville Marseille', 'Maison de ville rénovée, charme provençal.', 'HOUSE', 'PUBLISHED', 520000.00, 'EUR', 130.0, 5, 4, 2, '35 Rue Paradis', 'Marseille', '13006', 'France',
     (SELECT id FROM organizations WHERE name = 'Property Marseille'),
     (SELECT id FROM users WHERE email = 'agent2@marseille-property.fr'),
     (SELECT id FROM users WHERE email = 'directeur@marseille-property.fr'), true, NOW(), NOW()),
    ('PROP-MARSEILLE-006', 'Appartement T4 Marseille', 'Appartement T4 spacieux, balcon.', 'APARTMENT', 'PUBLISHED', 320000.00, 'EUR', 85.0, 4, 3, 2, '18 Boulevard Longchamp', 'Marseille', '13001', 'France',
     (SELECT id FROM organizations WHERE name = 'Property Marseille'),
     (SELECT id FROM users WHERE email = 'agent3@marseille-property.fr'),
     (SELECT id FROM users WHERE email = 'directeur@marseille-property.fr'), true, NOW(), NOW())
ON CONFLICT (reference) DO NOTHING;

-- Propriétés pour Bordeaux Immobilier (org_id = 4)
INSERT INTO properties (reference, title, description, type, status, price, currency, surface, rooms, bedrooms, bathrooms, address, city, postal_code, country, organization_id, assigned_user_id, created_by, active, created_at, updated_at)
VALUES 
    ('PROP-BORDEAUX-001', 'Appartement centre Bordeaux', 'Appartement dans le centre historique.', 'APARTMENT', 'PUBLISHED', 295000.00, 'EUR', 60.0, 3, 2, 1, '10 Rue Sainte-Catherine', 'Bordeaux', '33000', 'France',
     (SELECT id FROM organizations WHERE name = 'Bordeaux Immobilier'),
     (SELECT id FROM users WHERE email = 'agent1@bordeaux-immobilier.fr'),
     (SELECT id FROM users WHERE email = 'directeur@bordeaux-immobilier.fr'), true, NOW(), NOW()),
    ('PROP-BORDEAUX-002', 'Maison avec terrasse Bordeaux', 'Maison avec grande terrasse, jardin.', 'HOUSE', 'PUBLISHED', 480000.00, 'EUR', 125.0, 5, 4, 3, '25 Cours de l''Intendance', 'Bordeaux', '33000', 'France',
     (SELECT id FROM organizations WHERE name = 'Bordeaux Immobilier'),
     (SELECT id FROM users WHERE email = 'agent1@bordeaux-immobilier.fr'),
     (SELECT id FROM users WHERE email = 'directeur@bordeaux-immobilier.fr'), true, NOW(), NOW()),
    ('PROP-BORDEAUX-003', 'Local commercial Bordeaux', 'Local commercial en centre-ville.', 'COMMERCIAL', 'PUBLISHED', 280000.00, 'EUR', 120.0, 1, 0, 1, '40 Rue des Trois-Conils', 'Bordeaux', '33000', 'France',
     (SELECT id FROM organizations WHERE name = 'Bordeaux Immobilier'),
     (SELECT id FROM users WHERE email = 'agent1@bordeaux-immobilier.fr'),
     (SELECT id FROM users WHERE email = 'manager@bordeaux-immobilier.fr'), true, NOW(), NOW())
ON CONFLICT (reference) DO NOTHING;

-- Propriétés pour Nice Properties (org_id = 5)
INSERT INTO properties (reference, title, description, type, status, price, currency, surface, rooms, bedrooms, bathrooms, address, city, postal_code, country, organization_id, assigned_user_id, created_by, active, created_at, updated_at)
VALUES 
    ('PROP-NICE-001', 'Appartement vue mer Nice', 'Appartement avec vue mer exceptionnelle.', 'APARTMENT', 'PUBLISHED', 420000.00, 'EUR', 75.0, 3, 2, 2, '12 Promenade des Anglais', 'Nice', '06000', 'France',
     (SELECT id FROM organizations WHERE name = 'Nice Properties'),
     (SELECT id FROM users WHERE email = 'agent1@nice-properties.fr'),
     (SELECT id FROM users WHERE email = 'directeur@nice-properties.fr'), true, NOW(), NOW()),
    ('PROP-NICE-002', 'Villa de prestige Nice', 'Villa de prestige avec piscine, vue panoramique.', 'HOUSE', 'PUBLISHED', 1200000.00, 'EUR', 250.0, 7, 5, 4, '50 Boulevard de Cimiez', 'Nice', '06000', 'France',
     (SELECT id FROM organizations WHERE name = 'Nice Properties'),
     (SELECT id FROM users WHERE email = 'agent2@nice-properties.fr'),
     (SELECT id FROM users WHERE email = 'directeur@nice-properties.fr'), true, NOW(), NOW()),
    ('PROP-NICE-003', 'Studio Nice centre', 'Studio proche plage et commerces.', 'APARTMENT', 'RENTED', 110000.00, 'EUR', 30.0, 1, 0, 1, '8 Rue de France', 'Nice', '06000', 'France',
     (SELECT id FROM organizations WHERE name = 'Nice Properties'),
     (SELECT id FROM users WHERE email = 'agent1@nice-properties.fr'),
     (SELECT id FROM users WHERE email = 'directeur@nice-properties.fr'), true, NOW(), NOW()),
    ('PROP-NICE-004', 'Appartement T3 Nice', 'Appartement T3 rénové, proche vieille ville.', 'APARTMENT', 'PUBLISHED', 350000.00, 'EUR', 68.0, 3, 2, 1, '22 Avenue Jean Médecin', 'Nice', '06000', 'France',
     (SELECT id FROM organizations WHERE name = 'Nice Properties'),
     (SELECT id FROM users WHERE email = 'agent2@nice-properties.fr'),
     (SELECT id FROM users WHERE email = 'directeur@nice-properties.fr'), true, NOW(), NOW())
ON CONFLICT (reference) DO NOTHING;

-- =====================================================
-- RÉSUMÉ FINAL
-- =====================================================

DO $$
DECLARE
    user_count INTEGER;
    org_count INTEGER;
    team_count INTEGER;
    org_user_count INTEGER;
    prop_count INTEGER;
    role_count INTEGER;
    perm_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO user_count FROM users;
    SELECT COUNT(*) INTO org_count FROM organizations;
    SELECT COUNT(*) INTO team_count FROM teams;
    SELECT COUNT(*) INTO org_user_count FROM organization_users;
    SELECT COUNT(*) INTO prop_count FROM properties;
    SELECT COUNT(*) INTO role_count FROM roles;
    SELECT COUNT(*) INTO perm_count FROM permissions;
    
    RAISE NOTICE '';
    RAISE NOTICE '═══════════════════════════════════════════════════════════';
    RAISE NOTICE '✅ SEED TERMINÉ AVEC SUCCÈS!';
    RAISE NOTICE '═══════════════════════════════════════════════════════════';
    RAISE NOTICE '';
    RAISE NOTICE '📊 STATISTIQUES:';
    RAISE NOTICE '   • Permissions: %', perm_count;
    RAISE NOTICE '   • Rôles: %', role_count;
    RAISE NOTICE '   • Utilisateurs: %', user_count;
    RAISE NOTICE '   • Organisations: %', org_count;
    RAISE NOTICE '   • Équipes: %', team_count;
    RAISE NOTICE '   • Associations utilisateurs-organisations: %', org_user_count;
    RAISE NOTICE '   • Propriétés: %', prop_count;
    RAISE NOTICE '';
    RAISE NOTICE '🔑 COMPTE ADMIN SAAS:';
    RAISE NOTICE '   Email: admin@viridial.com';
    RAISE NOTICE '   Password: admin123';
    RAISE NOTICE '';
    RAISE NOTICE '👥 COMPTES DE TEST (password: password123):';
    RAISE NOTICE '   • Directeur Paris: directeur@paris-immobilier.fr';
    RAISE NOTICE '   • Manager Lyon: manager@lyon-realestate.fr';
    RAISE NOTICE '   • Agent Marseille: agent1@marseille-property.fr';
    RAISE NOTICE '';
    RAISE NOTICE '═══════════════════════════════════════════════════════════';
END $$;

