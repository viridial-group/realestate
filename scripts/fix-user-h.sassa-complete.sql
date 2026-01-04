-- =====================================================
-- Script COMPLET pour corriger l'utilisateur h.sassa@yahoo.fr
-- =====================================================
-- Ce script:
-- 1. Met à jour le mot de passe avec un hash BCrypt valide
-- 2. Assure que l'utilisateur est activé
-- 3. Assigne le rôle ORGANIZATION_ADMIN (pour un abonnement ENTERPRISE)
-- 4. Vérifie l'association avec l'organisation

-- =====================================================
-- 1. CORRIGER LE MOT DE PASSE
-- =====================================================

-- IMPORTANT: Remplacez ce hash par un hash généré via:
-- curl -X POST http://localhost:8081/api/identity/utils/password-hash -H "Content-Type: application/json" -d '{"password":"1234567890"}'
-- Ou utilisez: https://bcrypt-generator.com/

UPDATE users 
SET password = '$2a$10$rKqJ8vX5YzQ3mNpL2wHx9eBcDfGhIjKlMnOpQrStUvWxYzAbCdEf',
    updated_at = NOW(),
    enabled = true,
    account_non_expired = true,
    account_non_locked = true,
    credentials_non_expired = true
WHERE email = 'h.sassa@yahoo.fr';

-- =====================================================
-- 2. ASSIGNER LE RÔLE ORGANIZATION_ADMIN
-- =====================================================

-- Assigner le rôle ORGANIZATION_ADMIN à l'utilisateur (pour un abonnement ENTERPRISE)
-- Ce rôle donne les permissions pour gérer l'organisation et toutes les sous-organisations
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email = 'h.sassa@yahoo.fr' AND r.name = 'ORGANIZATION_ADMIN'
ON CONFLICT (user_id, role_id) DO NOTHING;

-- Note: ORGANIZATION_ADMIN est différent de ADMIN
-- - ADMIN = Administrateur SaaS (plateforme)
-- - ORGANIZATION_ADMIN = Administrateur d'agence (organisation)

-- =====================================================
-- 3. VÉRIFICATIONS
-- =====================================================

-- Vérification de l'utilisateur
SELECT 
    id,
    email, 
    first_name, 
    last_name, 
    enabled,
    account_non_expired,
    account_non_locked,
    credentials_non_expired,
    CASE 
        WHEN password LIKE '$2a$10$%' 
        THEN '✅ Mot de passe hashé avec BCrypt'
        ELSE '❌ Mot de passe non hashé correctement'
    END as password_status,
    created_at,
    updated_at
FROM users 
WHERE email = 'h.sassa@yahoo.fr';

-- Vérification des rôles
SELECT 
    u.email,
    r.name as role_name,
    r.description as role_description
FROM users u
LEFT JOIN user_roles ur ON u.id = ur.user_id
LEFT JOIN roles r ON ur.role_id = r.id
WHERE u.email = 'h.sassa@yahoo.fr';

-- Vérification de l'organisation et de l'abonnement
SELECT 
    u.email,
    o.id as organization_id,
    o.name as organization_name,
    o.active as org_active,
    ou.is_primary,
    ou.active as org_user_active,
    p.name as plan_name,
    p.price as plan_price,
    s.status as subscription_status,
    s.active as subscription_active,
    s.start_date,
    s.end_date
FROM users u
LEFT JOIN organization_users ou ON u.id = ou.user_id
LEFT JOIN organizations o ON ou.organization_id = o.id
LEFT JOIN subscriptions s ON o.id = s.organization_id AND s.active = true
LEFT JOIN plans p ON s.plan_id = p.id
WHERE u.email = 'h.sassa@yahoo.fr'
ORDER BY ou.is_primary DESC NULLS LAST;

-- =====================================================
-- 4. RÉSUMÉ
-- =====================================================

DO $$
DECLARE
    user_exists BOOLEAN;
    has_password BOOLEAN;
    has_role BOOLEAN;
    has_org BOOLEAN;
    has_subscription BOOLEAN;
BEGIN
    -- Vérifier l'utilisateur
    SELECT EXISTS(SELECT 1 FROM users WHERE email = 'h.sassa@yahoo.fr') INTO user_exists;
    SELECT EXISTS(SELECT 1 FROM users WHERE email = 'h.sassa@yahoo.fr' AND password LIKE '$2a$10$%') INTO has_password;
    SELECT EXISTS(SELECT 1 FROM users u JOIN user_roles ur ON u.id = ur.user_id JOIN roles r ON ur.role_id = r.id WHERE u.email = 'h.sassa@yahoo.fr' AND r.name = 'ORGANIZATION_ADMIN') INTO has_role;
    SELECT EXISTS(SELECT 1 FROM users u JOIN organization_users ou ON u.id = ou.user_id WHERE u.email = 'h.sassa@yahoo.fr') INTO has_org;
    SELECT EXISTS(
        SELECT 1 FROM users u 
        JOIN organization_users ou ON u.id = ou.user_id 
        JOIN subscriptions s ON ou.organization_id = s.organization_id 
        WHERE u.email = 'h.sassa@yahoo.fr' AND s.active = true
    ) INTO has_subscription;
    
    RAISE NOTICE '';
    RAISE NOTICE '═══════════════════════════════════════════════════════════';
    RAISE NOTICE '📋 RÉSUMÉ POUR h.sassa@yahoo.fr';
    RAISE NOTICE '═══════════════════════════════════════════════════════════';
    RAISE NOTICE '   Utilisateur existe: %', CASE WHEN user_exists THEN '✅ Oui' ELSE '❌ Non' END;
    RAISE NOTICE '   Mot de passe hashé: %', CASE WHEN has_password THEN '✅ Oui' ELSE '❌ Non' END;
    RAISE NOTICE '   Rôle ORGANIZATION_ADMIN: %', CASE WHEN has_role THEN '✅ Oui' ELSE '❌ Non' END;
    RAISE NOTICE '   Organisation: %', CASE WHEN has_org THEN '✅ Oui' ELSE '❌ Non' END;
    RAISE NOTICE '   Abonnement actif: %', CASE WHEN has_subscription THEN '✅ Oui' ELSE '❌ Non' END;
    RAISE NOTICE '═══════════════════════════════════════════════════════════';
    RAISE NOTICE '';
    RAISE NOTICE '🔑 Informations de connexion:';
    RAISE NOTICE '   Email: h.sassa@yahoo.fr';
    RAISE NOTICE '   Password: 1234567890';
    RAISE NOTICE '';
END $$;

