-- =====================================================
-- Script SQL COMPLET pour restructurer et peupler la base de données
-- Simule des agences immobilières avec toutes les entités
-- =====================================================

-- =====================================================
-- 0. SUPPRIMER TOUTES LES TABLES (DROP ALL)
-- =====================================================

-- Désactiver temporairement les contraintes de clés étrangères
SET session_replication_role = 'replica';

-- Supprimer toutes les tables dans l'ordre inverse des dépendances
DROP TABLE IF EXISTS notification_subscriptions CASCADE;
DROP TABLE IF EXISTS notifications CASCADE;
DROP TABLE IF EXISTS payments CASCADE;
DROP TABLE IF EXISTS invoices CASCADE;
DROP TABLE IF EXISTS subscriptions CASCADE;
DROP TABLE IF EXISTS plans CASCADE;
DROP TABLE IF EXISTS property_features CASCADE;
DROP TABLE IF EXISTS property_accesses CASCADE;
DROP TABLE IF EXISTS properties CASCADE;
DROP TABLE IF EXISTS organization_users CASCADE;
DROP TABLE IF EXISTS teams CASCADE;
DROP TABLE IF EXISTS user_roles CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS organizations CASCADE;
DROP TABLE IF EXISTS role_permissions CASCADE;
DROP TABLE IF EXISTS roles CASCADE;
DROP TABLE IF EXISTS permissions CASCADE;

-- Supprimer les contraintes et index qui pourraient rester
DROP INDEX IF EXISTS idx_property_feature_key CASCADE;
DROP INDEX IF EXISTS idx_property_feature CASCADE;
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM pg_constraint 
        WHERE conname = 'uk_property_feature_key'
    ) THEN
        ALTER TABLE property_features DROP CONSTRAINT IF EXISTS uk_property_feature_key CASCADE;
    END IF;
    IF EXISTS (
        SELECT 1 FROM pg_constraint 
        WHERE conname = 'uk_property_feature_key_value'
    ) THEN
        ALTER TABLE property_features DROP CONSTRAINT IF EXISTS uk_property_feature_key_value CASCADE;
    END IF;
END $$;

-- Réactiver les contraintes
SET session_replication_role = 'origin';

-- =====================================================
-- 0.1. NETTOYER LES DONNÉES (si les tables existent encore)
-- =====================================================

-- Désactiver temporairement les contraintes de clés étrangères
SET session_replication_role = 'replica';

-- Supprimer les données dans l'ordre inverse des dépendances (si les tables existent)
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'notification_subscriptions') THEN
        TRUNCATE TABLE notification_subscriptions CASCADE;
    END IF;
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'notifications') THEN
        TRUNCATE TABLE notifications CASCADE;
    END IF;
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'payments') THEN
        TRUNCATE TABLE payments CASCADE;
    END IF;
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'invoices') THEN
        TRUNCATE TABLE invoices CASCADE;
    END IF;
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'subscriptions') THEN
        TRUNCATE TABLE subscriptions CASCADE;
    END IF;
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'plans') THEN
        TRUNCATE TABLE plans CASCADE;
    END IF;
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'property_features') THEN
        TRUNCATE TABLE property_features CASCADE;
    END IF;
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'property_accesses') THEN
        TRUNCATE TABLE property_accesses CASCADE;
    END IF;
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'properties') THEN
        TRUNCATE TABLE properties CASCADE;
    END IF;
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'organization_users') THEN
        TRUNCATE TABLE organization_users CASCADE;
    END IF;
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'teams') THEN
        TRUNCATE TABLE teams CASCADE;
    END IF;
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'user_roles') THEN
        TRUNCATE TABLE user_roles CASCADE;
    END IF;
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'users') THEN
        TRUNCATE TABLE users CASCADE;
    END IF;
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'organizations') THEN
        TRUNCATE TABLE organizations CASCADE;
    END IF;
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'role_permissions') THEN
        TRUNCATE TABLE role_permissions CASCADE;
    END IF;
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'roles') THEN
        TRUNCATE TABLE roles CASCADE;
    END IF;
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'permissions') THEN
        TRUNCATE TABLE permissions CASCADE;
    END IF;
END $$;

-- Réactiver les contraintes
SET session_replication_role = 'origin';

-- =====================================================
-- 0.2. CRÉER TOUTES LES TABLES
-- =====================================================

-- Table permissions
CREATE TABLE IF NOT EXISTS permissions (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    resource VARCHAR(100) NOT NULL,
    action VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_permission_name ON permissions(name);
CREATE INDEX IF NOT EXISTS idx_permission_resource_action ON permissions(resource, action);

-- Table roles
CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_role_name ON roles(name);

-- Table role_permissions
CREATE TABLE IF NOT EXISTS role_permissions (
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_role_permission_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    CONSTRAINT fk_role_permission_permission FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
);

-- Table organizations
CREATE TABLE IF NOT EXISTS organizations (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(500),
    domain VARCHAR(100),
    parent_id BIGINT,
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_organization_parent FOREIGN KEY (parent_id) REFERENCES organizations(id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_org_name ON organizations(name);
CREATE INDEX IF NOT EXISTS idx_org_parent ON organizations(parent_id);

-- Table users
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    enabled BOOLEAN NOT NULL DEFAULT true,
    account_non_expired BOOLEAN NOT NULL DEFAULT true,
    account_non_locked BOOLEAN NOT NULL DEFAULT true,
    credentials_non_expired BOOLEAN NOT NULL DEFAULT true,
    last_login_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_user_email ON users(email);

-- Table user_roles
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_role_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_role_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Table teams
CREATE TABLE IF NOT EXISTS teams (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(500),
    organization_id BIGINT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_team_organization FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_team_organization ON teams(organization_id);
CREATE INDEX IF NOT EXISTS idx_team_name ON teams(name);

-- Table organization_users
CREATE TABLE IF NOT EXISTS organization_users (
    id BIGSERIAL PRIMARY KEY,
    organization_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    team_id BIGINT,
    active BOOLEAN NOT NULL DEFAULT true,
    is_primary BOOLEAN NOT NULL DEFAULT false,
    custom_roles VARCHAR(1000),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_org_user_organization FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE,
    CONSTRAINT fk_org_user_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_org_user_team FOREIGN KEY (team_id) REFERENCES teams(id) ON DELETE SET NULL,
    CONSTRAINT uk_org_user UNIQUE (organization_id, user_id)
);

CREATE INDEX IF NOT EXISTS idx_org_user_organization ON organization_users(organization_id);
CREATE INDEX IF NOT EXISTS idx_org_user_user ON organization_users(user_id);
CREATE INDEX IF NOT EXISTS idx_org_user ON organization_users(organization_id, user_id);

-- Table properties
CREATE TABLE IF NOT EXISTS properties (
    id BIGSERIAL PRIMARY KEY,
    reference VARCHAR(50) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    price NUMERIC(15, 2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'EUR',
    surface NUMERIC(10, 2),
    rooms INTEGER,
    bedrooms INTEGER,
    bathrooms INTEGER,
    full_bathrooms INTEGER,
    appliances_included TEXT,
    laundry_location VARCHAR(50),
    total_structure_area NUMERIC(10, 2),
    total_interior_livable_area NUMERIC(10, 2),
    virtual_tour_url VARCHAR(500),
    parking_features TEXT,
    has_garage BOOLEAN,
    accessibility_features TEXT,
    patio_porch VARCHAR(200),
    exterior_features TEXT,
    special_conditions VARCHAR(100),
    home_type VARCHAR(50),
    property_subtype VARCHAR(50),
    condition VARCHAR(50),
    year_built INTEGER,
    subdivision VARCHAR(200),
    has_hoa BOOLEAN,
    hoa_amenities TEXT,
    hoa_services TEXT,
    hoa_fee NUMERIC(10, 2),
    hoa_fee_frequency VARCHAR(20),
    region VARCHAR(100),
    price_per_square_foot NUMERIC(10, 2),
    date_on_market DATE,
    address VARCHAR(100),
    city VARCHAR(100),
    postal_code VARCHAR(20),
    country VARCHAR(100),
    latitude NUMERIC(10, 7),
    longitude NUMERIC(10, 7),
    organization_id BIGINT NOT NULL,
    team_id BIGINT,
    assigned_user_id BIGINT,
    created_by BIGINT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT true,
    features TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_property_organization FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE,
    CONSTRAINT fk_property_team FOREIGN KEY (team_id) REFERENCES teams(id) ON DELETE SET NULL,
    CONSTRAINT fk_property_assigned_user FOREIGN KEY (assigned_user_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_property_created_by FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT
);

CREATE INDEX IF NOT EXISTS idx_property_org ON properties(organization_id);
CREATE INDEX IF NOT EXISTS idx_property_assigned_user ON properties(assigned_user_id);
CREATE INDEX IF NOT EXISTS idx_property_reference ON properties(reference);
CREATE INDEX IF NOT EXISTS idx_property_status ON properties(status);
CREATE INDEX IF NOT EXISTS idx_property_type ON properties(type);
CREATE INDEX IF NOT EXISTS idx_property_city ON properties(city);
CREATE INDEX IF NOT EXISTS idx_property_year_built ON properties(year_built);
CREATE INDEX IF NOT EXISTS idx_property_home_type ON properties(home_type);
CREATE INDEX IF NOT EXISTS idx_property_region ON properties(region);
CREATE INDEX IF NOT EXISTS idx_property_date_on_market ON properties(date_on_market);
CREATE INDEX IF NOT EXISTS idx_property_has_hoa ON properties(has_hoa);

-- Table property_accesses
CREATE TABLE IF NOT EXISTS property_accesses (
    id BIGSERIAL PRIMARY KEY,
    property_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    organization_id BIGINT NOT NULL,
    permission VARCHAR(50) NOT NULL,
    expires_at TIMESTAMP,
    granted_by BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_property_access_property FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE,
    CONSTRAINT fk_property_access_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_property_access_organization FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE,
    CONSTRAINT fk_property_access_granted_by FOREIGN KEY (granted_by) REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_property_access_property ON property_accesses(property_id);
CREATE INDEX IF NOT EXISTS idx_property_access_user ON property_accesses(user_id);

-- Table property_features
CREATE TABLE IF NOT EXISTS property_features (
    id BIGSERIAL PRIMARY KEY,
    property_id BIGINT NOT NULL,
    key VARCHAR(100) NOT NULL,
    value VARCHAR(500),
    type VARCHAR(50),
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_property_feature_property FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE,
    CONSTRAINT uk_property_feature_key_value UNIQUE (property_id, key, value)
);

CREATE INDEX IF NOT EXISTS idx_property_feature ON property_features(property_id);

-- Table plans
CREATE TABLE IF NOT EXISTS plans (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    price NUMERIC(10, 2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'EUR',
    billing_period VARCHAR(50) DEFAULT 'MONTHLY',
    max_properties INTEGER,
    max_users INTEGER,
    max_storage_gb INTEGER,
    features TEXT,
    active BOOLEAN NOT NULL DEFAULT true,
    is_default BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_plan_name ON plans(name);
CREATE INDEX IF NOT EXISTS idx_plan_active ON plans(active);

-- Table subscriptions
CREATE TABLE IF NOT EXISTS subscriptions (
    id BIGSERIAL PRIMARY KEY,
    plan_id BIGINT NOT NULL,
    organization_id BIGINT NOT NULL UNIQUE,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP,
    trial_end_date TIMESTAMP,
    auto_renew BOOLEAN NOT NULL DEFAULT true,
    cancelled_at TIMESTAMP,
    cancelled_by BIGINT,
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_subscription_plan FOREIGN KEY (plan_id) REFERENCES plans(id) ON DELETE RESTRICT,
    CONSTRAINT fk_subscription_organization FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_subscription_org ON subscriptions(organization_id);
CREATE INDEX IF NOT EXISTS idx_subscription_plan ON subscriptions(plan_id);
CREATE INDEX IF NOT EXISTS idx_subscription_status ON subscriptions(status);
CREATE INDEX IF NOT EXISTS idx_subscription_active ON subscriptions(active);

-- Table invoices
CREATE TABLE IF NOT EXISTS invoices (
    id BIGSERIAL PRIMARY KEY,
    subscription_id BIGINT NOT NULL,
    invoice_number VARCHAR(50) NOT NULL UNIQUE,
    organization_id BIGINT NOT NULL,
    amount NUMERIC(10, 2) NOT NULL,
    tax_amount NUMERIC(10, 2) NOT NULL DEFAULT 0,
    total_amount NUMERIC(10, 2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'EUR',
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    due_date DATE NOT NULL,
    paid_at TIMESTAMP,
    billing_period_start DATE NOT NULL,
    billing_period_end DATE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_invoice_subscription FOREIGN KEY (subscription_id) REFERENCES subscriptions(id) ON DELETE RESTRICT,
    CONSTRAINT fk_invoice_organization FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_invoice_subscription ON invoices(subscription_id);
CREATE INDEX IF NOT EXISTS idx_invoice_organization ON invoices(organization_id);
CREATE INDEX IF NOT EXISTS idx_invoice_number ON invoices(invoice_number);
CREATE INDEX IF NOT EXISTS idx_invoice_status ON invoices(status);

-- Table payments
CREATE TABLE IF NOT EXISTS payments (
    id BIGSERIAL PRIMARY KEY,
    invoice_id BIGINT NOT NULL,
    transaction_id VARCHAR(100) NOT NULL UNIQUE,
    amount NUMERIC(10, 2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'EUR',
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    payment_method VARCHAR(50),
    payment_gateway_response TEXT,
    paid_at TIMESTAMP,
    error_message TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payment_invoice FOREIGN KEY (invoice_id) REFERENCES invoices(id) ON DELETE RESTRICT
);

CREATE INDEX IF NOT EXISTS idx_payment_invoice ON payments(invoice_id);
CREATE INDEX IF NOT EXISTS idx_payment_status ON payments(status);
CREATE INDEX IF NOT EXISTS idx_payment_transaction_id ON payments(transaction_id);

-- Table notifications
CREATE TABLE IF NOT EXISTS notifications (
    id BIGSERIAL PRIMARY KEY,
    type VARCHAR(100) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT,
    recipient_id BIGINT NOT NULL,
    organization_id BIGINT NOT NULL,
    sender_id BIGINT,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    channel VARCHAR(100),
    target_type VARCHAR(100),
    target_id BIGINT,
    action_url VARCHAR(500),
    read_at TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT true,
    metadata TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notification_recipient FOREIGN KEY (recipient_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_notification_organization FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE,
    CONSTRAINT fk_notification_sender FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_notification_recipient ON notifications(recipient_id);
CREATE INDEX IF NOT EXISTS idx_notification_status ON notifications(status);
CREATE INDEX IF NOT EXISTS idx_notification_type ON notifications(type);
CREATE INDEX IF NOT EXISTS idx_notification_org ON notifications(organization_id);
CREATE INDEX IF NOT EXISTS idx_notification_created ON notifications(created_at);

-- Table notification_subscriptions
CREATE TABLE IF NOT EXISTS notification_subscriptions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    organization_id BIGINT NOT NULL,
    notification_type VARCHAR(100) NOT NULL,
    channel VARCHAR(100),
    enabled BOOLEAN NOT NULL DEFAULT true,
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notification_subscription_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_notification_subscription_organization FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_subscription_user ON notification_subscriptions(user_id);
CREATE INDEX IF NOT EXISTS idx_subscription_org ON notification_subscriptions(organization_id);
CREATE INDEX IF NOT EXISTS idx_subscription_type ON notification_subscriptions(notification_type);
CREATE INDEX IF NOT EXISTS idx_subscription_active ON notification_subscriptions(active);

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
-- 5.1. CRÉER LES PLANS D'ABONNEMENT (COMPÉTITIFS AVEC SELOGER)
-- =====================================================

-- Plan FREE : Gratuit pour tester (limité)
INSERT INTO plans (name, description, price, currency, billing_period, max_properties, max_users, max_storage_gb, features, active, is_default, created_at, updated_at)
VALUES (
    'FREE',
    'Plan gratuit pour tester la plateforme. Idéal pour les agents indépendants qui débutent.',
    0.00,
    'EUR',
    'MONTHLY',
    5,  -- Maximum 5 propriétés
    1,  -- 1 utilisateur
    1,  -- 1 GB de stockage
    '["property_listing", "basic_search", "property_details", "photo_upload"]',
    true,
    true,  -- Plan par défaut
    NOW(),
    NOW()
)
ON CONFLICT (name) DO UPDATE
SET description = EXCLUDED.description,
    price = EXCLUDED.price,
    max_properties = EXCLUDED.max_properties,
    max_users = EXCLUDED.max_users,
    max_storage_gb = EXCLUDED.max_storage_gb,
    features = EXCLUDED.features,
    is_default = EXCLUDED.is_default,
    updated_at = NOW();

-- Plan STARTER : 29€/mois (vs SeLoger Essentiel ~49€/mois) - 40% moins cher
INSERT INTO plans (name, description, price, currency, billing_period, max_properties, max_users, max_storage_gb, features, active, is_default, created_at, updated_at)
VALUES (
    'STARTER',
    'Plan Starter pour petites agences. Toutes les fonctionnalités essentielles à un prix compétitif.',
    29.00,
    'EUR',
    'MONTHLY',
    50,  -- Maximum 50 propriétés
    3,   -- 3 utilisateurs
    10,  -- 10 GB de stockage
    '["property_listing", "advanced_search", "property_details", "photo_upload", "virtual_tours", "email_notifications", "basic_analytics", "mobile_app"]',
    true,
    false,
    NOW(),
    NOW()
)
ON CONFLICT (name) DO UPDATE
SET description = EXCLUDED.description,
    price = EXCLUDED.price,
    max_properties = EXCLUDED.max_properties,
    max_users = EXCLUDED.max_users,
    max_storage_gb = EXCLUDED.max_storage_gb,
    features = EXCLUDED.features,
    updated_at = NOW();

-- Plan PROFESSIONAL : 69€/mois (vs SeLoger Confort ~99€/mois) - 30% moins cher
INSERT INTO plans (name, description, price, currency, billing_period, max_properties, max_users, max_storage_gb, features, active, is_default, created_at, updated_at)
VALUES (
    'PROFESSIONAL',
    'Plan Professional pour agences en croissance. Fonctionnalités avancées et support prioritaire.',
    69.00,
    'EUR',
    'MONTHLY',
    200,  -- Maximum 200 propriétés
    10,   -- 10 utilisateurs
    50,   -- 50 GB de stockage
    '["property_listing", "advanced_search", "property_details", "photo_upload", "virtual_tours", "email_notifications", "advanced_analytics", "mobile_app", "crm_integration", "lead_management", "document_management", "team_collaboration", "custom_branding", "priority_support"]',
    true,
    false,
    NOW(),
    NOW()
)
ON CONFLICT (name) DO UPDATE
SET description = EXCLUDED.description,
    price = EXCLUDED.price,
    max_properties = EXCLUDED.max_properties,
    max_users = EXCLUDED.max_users,
    max_storage_gb = EXCLUDED.max_storage_gb,
    features = EXCLUDED.features,
    updated_at = NOW();

-- Plan ENTERPRISE : 149€/mois (vs SeLoger Premium ~199€/mois) - 25% moins cher
INSERT INTO plans (name, description, price, currency, billing_period, max_properties, max_users, max_storage_gb, features, active, is_default, created_at, updated_at)
VALUES (
    'ENTERPRISE',
    'Plan Enterprise pour grandes agences et réseaux. Fonctionnalités illimitées et support dédié.',
    149.00,
    'EUR',
    'MONTHLY',
    NULL,  -- Illimité
    NULL,  -- Illimité
    500,   -- 500 GB de stockage
    '["property_listing", "advanced_search", "property_details", "photo_upload", "virtual_tours", "email_notifications", "advanced_analytics", "mobile_app", "crm_integration", "lead_management", "document_management", "team_collaboration", "custom_branding", "priority_support", "api_access", "white_label", "multi_location", "workflow_automation", "custom_reports", "dedicated_account_manager"]',
    true,
    false,
    NOW(),
    NOW()
)
ON CONFLICT (name) DO UPDATE
SET description = EXCLUDED.description,
    price = EXCLUDED.price,
    max_properties = EXCLUDED.max_properties,
    max_users = EXCLUDED.max_users,
    max_storage_gb = EXCLUDED.max_storage_gb,
    features = EXCLUDED.features,
    updated_at = NOW();

-- =====================================================
-- 5.2. CRÉER LES ABONNEMENTS POUR LES ORGANISATIONS
-- =====================================================

-- Immobilier Paris : Plan PROFESSIONAL (grande agence parisienne)
INSERT INTO subscriptions (plan_id, organization_id, status, start_date, end_date, auto_renew, active, created_at, updated_at)
SELECT 
    p.id,
    o.id,
    'ACTIVE',
    NOW() - INTERVAL '2 months',  -- Abonné depuis 2 mois
    NOW() + INTERVAL '10 months', -- Se termine dans 10 mois (abonnement annuel)
    true,
    true,
    NOW(),
    NOW()
FROM plans p, organizations o
WHERE p.name = 'PROFESSIONAL' AND o.name = 'Immobilier Paris'
ON CONFLICT (organization_id) DO UPDATE
SET plan_id = EXCLUDED.plan_id,
    status = EXCLUDED.status,
    updated_at = NOW();

-- Real Estate Lyon : Plan STARTER (agence moyenne)
INSERT INTO subscriptions (plan_id, organization_id, status, start_date, end_date, auto_renew, active, created_at, updated_at)
SELECT 
    p.id,
    o.id,
    'ACTIVE',
    NOW() - INTERVAL '1 month',   -- Abonné depuis 1 mois
    NOW() + INTERVAL '11 months', -- Se termine dans 11 mois
    true,
    true,
    NOW(),
    NOW()
FROM plans p, organizations o
WHERE p.name = 'STARTER' AND o.name = 'Real Estate Lyon'
ON CONFLICT (organization_id) DO UPDATE
SET plan_id = EXCLUDED.plan_id,
    status = EXCLUDED.status,
    updated_at = NOW();

-- Property Marseille : Plan PROFESSIONAL (agence sur la Côte d'Azur)
INSERT INTO subscriptions (plan_id, organization_id, status, start_date, end_date, auto_renew, active, created_at, updated_at)
SELECT 
    p.id,
    o.id,
    'ACTIVE',
    NOW() - INTERVAL '3 months',  -- Abonné depuis 3 mois
    NOW() + INTERVAL '9 months',  -- Se termine dans 9 mois
    true,
    true,
    NOW(),
    NOW()
FROM plans p, organizations o
WHERE p.name = 'PROFESSIONAL' AND o.name = 'Property Marseille'
ON CONFLICT (organization_id) DO UPDATE
SET plan_id = EXCLUDED.plan_id,
    status = EXCLUDED.status,
    updated_at = NOW();

-- Bordeaux Immobilier : Plan STARTER (petite agence)
INSERT INTO subscriptions (plan_id, organization_id, status, start_date, end_date, trial_end_date, auto_renew, active, created_at, updated_at)
SELECT 
    p.id,
    o.id,
    'ACTIVE',
    NOW() - INTERVAL '15 days',    -- Abonné depuis 15 jours
    NOW() + INTERVAL '11 months 15 days', -- Se termine dans 11 mois et 15 jours
    NOW() + INTERVAL '15 days',   -- Fin de période d'essai dans 15 jours
    true,
    true,
    NOW(),
    NOW()
FROM plans p, organizations o
WHERE p.name = 'STARTER' AND o.name = 'Bordeaux Immobilier'
ON CONFLICT (organization_id) DO UPDATE
SET plan_id = EXCLUDED.plan_id,
    status = EXCLUDED.status,
    updated_at = NOW();

-- Nice Properties : Plan ENTERPRISE (grande agence)
INSERT INTO subscriptions (plan_id, organization_id, status, start_date, end_date, auto_renew, active, created_at, updated_at)
SELECT 
    p.id,
    o.id,
    'ACTIVE',
    NOW() - INTERVAL '6 months',  -- Abonné depuis 6 mois
    NOW() + INTERVAL '6 months',  -- Se termine dans 6 mois
    true,
    true,
    NOW(),
    NOW()
FROM plans p, organizations o
WHERE p.name = 'ENTERPRISE' AND o.name = 'Nice Properties'
ON CONFLICT (organization_id) DO UPDATE
SET plan_id = EXCLUDED.plan_id,
    status = EXCLUDED.status,
    updated_at = NOW();

-- =====================================================
-- 5.3. CRÉER LES FACTURES (INVOICES) POUR LES ABONNEMENTS
-- =====================================================

-- Immobilier Paris (PROFESSIONAL - 69€/mois) - Factures mensuelles
-- Facture 1: Il y a 2 mois (payée)
INSERT INTO invoices (subscription_id, invoice_number, organization_id, amount, tax_amount, total_amount, currency, status, due_date, paid_at, billing_period_start, billing_period_end, created_at, updated_at)
SELECT 
    s.id,
    'INV-PARIS-2024-001',
    s.organization_id,
    69.00,
    13.80,
    82.80,
    'EUR',
    'PAID',
    (NOW() - INTERVAL '2 months')::DATE + INTERVAL '15 days',
    NOW() - INTERVAL '1 month 20 days',
    (NOW() - INTERVAL '2 months')::DATE,
    (NOW() - INTERVAL '1 month')::DATE - INTERVAL '1 day',
    NOW() - INTERVAL '2 months',
    NOW() - INTERVAL '2 months'
FROM subscriptions s
JOIN organizations o ON s.organization_id = o.id
WHERE o.name = 'Immobilier Paris'
ON CONFLICT (invoice_number) DO NOTHING;

-- Facture 2: Il y a 1 mois (payée)
INSERT INTO invoices (subscription_id, invoice_number, organization_id, amount, tax_amount, total_amount, currency, status, due_date, paid_at, billing_period_start, billing_period_end, created_at, updated_at)
SELECT 
    s.id,
    'INV-PARIS-2024-002',
    s.organization_id,
    69.00,
    13.80,
    82.80,
    'EUR',
    'PAID',
    (NOW() - INTERVAL '1 month')::DATE + INTERVAL '15 days',
    NOW() - INTERVAL '20 days',
    (NOW() - INTERVAL '1 month')::DATE,
    NOW()::DATE - INTERVAL '1 day',
    NOW() - INTERVAL '1 month',
    NOW() - INTERVAL '1 month'
FROM subscriptions s
JOIN organizations o ON s.organization_id = o.id
WHERE o.name = 'Immobilier Paris'
ON CONFLICT (invoice_number) DO NOTHING;

-- Facture 3: Ce mois (en attente)
INSERT INTO invoices (subscription_id, invoice_number, organization_id, amount, tax_amount, total_amount, currency, status, due_date, billing_period_start, billing_period_end, created_at, updated_at)
SELECT 
    s.id,
    'INV-PARIS-2024-003',
    s.organization_id,
    69.00,
    13.80,
    82.80,
    'EUR',
    'PENDING',
    NOW()::DATE + INTERVAL '15 days',
    NOW()::DATE,
    (NOW() + INTERVAL '1 month')::DATE - INTERVAL '1 day',
    NOW(),
    NOW()
FROM subscriptions s
JOIN organizations o ON s.organization_id = o.id
WHERE o.name = 'Immobilier Paris'
ON CONFLICT (invoice_number) DO NOTHING;

-- Real Estate Lyon (STARTER - 29€/mois) - Factures mensuelles
-- Facture 1: Il y a 1 mois (payée)
INSERT INTO invoices (subscription_id, invoice_number, organization_id, amount, tax_amount, total_amount, currency, status, due_date, paid_at, billing_period_start, billing_period_end, created_at, updated_at)
SELECT 
    s.id,
    'INV-LYON-2024-001',
    s.organization_id,
    29.00,
    5.80,
    34.80,
    'EUR',
    'PAID',
    (NOW() - INTERVAL '1 month')::DATE + INTERVAL '15 days',
    NOW() - INTERVAL '18 days',
    (NOW() - INTERVAL '1 month')::DATE,
    NOW()::DATE - INTERVAL '1 day',
    NOW() - INTERVAL '1 month',
    NOW() - INTERVAL '1 month'
FROM subscriptions s
JOIN organizations o ON s.organization_id = o.id
WHERE o.name = 'Real Estate Lyon'
ON CONFLICT (invoice_number) DO NOTHING;

-- Facture 2: Ce mois (en attente)
INSERT INTO invoices (subscription_id, invoice_number, organization_id, amount, tax_amount, total_amount, currency, status, due_date, billing_period_start, billing_period_end, created_at, updated_at)
SELECT 
    s.id,
    'INV-LYON-2024-002',
    s.organization_id,
    29.00,
    5.80,
    34.80,
    'EUR',
    'PENDING',
    NOW()::DATE + INTERVAL '15 days',
    NOW()::DATE,
    (NOW() + INTERVAL '1 month')::DATE - INTERVAL '1 day',
    NOW(),
    NOW()
FROM subscriptions s
JOIN organizations o ON s.organization_id = o.id
WHERE o.name = 'Real Estate Lyon'
ON CONFLICT (invoice_number) DO NOTHING;

-- Property Marseille (PROFESSIONAL - 69€/mois) - Factures mensuelles
-- Facture 1: Il y a 3 mois (payée)
INSERT INTO invoices (subscription_id, invoice_number, organization_id, amount, tax_amount, total_amount, currency, status, due_date, paid_at, billing_period_start, billing_period_end, created_at, updated_at)
SELECT 
    s.id,
    'INV-MARSEILLE-2024-001',
    s.organization_id,
    69.00,
    13.80,
    82.80,
    'EUR',
    'PAID',
    (NOW() - INTERVAL '3 months')::DATE + INTERVAL '15 days',
    NOW() - INTERVAL '2 months 20 days',
    (NOW() - INTERVAL '3 months')::DATE,
    (NOW() - INTERVAL '2 months')::DATE - INTERVAL '1 day',
    NOW() - INTERVAL '3 months',
    NOW() - INTERVAL '3 months'
FROM subscriptions s
JOIN organizations o ON s.organization_id = o.id
WHERE o.name = 'Property Marseille'
ON CONFLICT (invoice_number) DO NOTHING;

-- Facture 2: Il y a 2 mois (payée)
INSERT INTO invoices (subscription_id, invoice_number, organization_id, amount, tax_amount, total_amount, currency, status, due_date, paid_at, billing_period_start, billing_period_end, created_at, updated_at)
SELECT 
    s.id,
    'INV-MARSEILLE-2024-002',
    s.organization_id,
    69.00,
    13.80,
    82.80,
    'EUR',
    'PAID',
    (NOW() - INTERVAL '2 months')::DATE + INTERVAL '15 days',
    NOW() - INTERVAL '1 month 20 days',
    (NOW() - INTERVAL '2 months')::DATE,
    (NOW() - INTERVAL '1 month')::DATE - INTERVAL '1 day',
    NOW() - INTERVAL '2 months',
    NOW() - INTERVAL '2 months'
FROM subscriptions s
JOIN organizations o ON s.organization_id = o.id
WHERE o.name = 'Property Marseille'
ON CONFLICT (invoice_number) DO NOTHING;

-- Facture 3: Il y a 1 mois (payée)
INSERT INTO invoices (subscription_id, invoice_number, organization_id, amount, tax_amount, total_amount, currency, status, due_date, paid_at, billing_period_start, billing_period_end, created_at, updated_at)
SELECT 
    s.id,
    'INV-MARSEILLE-2024-003',
    s.organization_id,
    69.00,
    13.80,
    82.80,
    'EUR',
    'PAID',
    (NOW() - INTERVAL '1 month')::DATE + INTERVAL '15 days',
    NOW() - INTERVAL '20 days',
    (NOW() - INTERVAL '1 month')::DATE,
    NOW()::DATE - INTERVAL '1 day',
    NOW() - INTERVAL '1 month',
    NOW() - INTERVAL '1 month'
FROM subscriptions s
JOIN organizations o ON s.organization_id = o.id
WHERE o.name = 'Property Marseille'
ON CONFLICT (invoice_number) DO NOTHING;

-- Facture 4: Ce mois (en attente)
INSERT INTO invoices (subscription_id, invoice_number, organization_id, amount, tax_amount, total_amount, currency, status, due_date, billing_period_start, billing_period_end, created_at, updated_at)
SELECT 
    s.id,
    'INV-MARSEILLE-2024-004',
    s.organization_id,
    69.00,
    13.80,
    82.80,
    'EUR',
    'PENDING',
    NOW()::DATE + INTERVAL '15 days',
    NOW()::DATE,
    (NOW() + INTERVAL '1 month')::DATE - INTERVAL '1 day',
    NOW(),
    NOW()
FROM subscriptions s
JOIN organizations o ON s.organization_id = o.id
WHERE o.name = 'Property Marseille'
ON CONFLICT (invoice_number) DO NOTHING;

-- Bordeaux Immobilier (STARTER - 29€/mois) - Facture mensuelle
-- Facture 1: Il y a 15 jours (en attente - période d'essai)
INSERT INTO invoices (subscription_id, invoice_number, organization_id, amount, tax_amount, total_amount, currency, status, due_date, billing_period_start, billing_period_end, created_at, updated_at)
SELECT 
    s.id,
    'INV-BORDEAUX-2024-001',
    s.organization_id,
    29.00,
    5.80,
    34.80,
    'EUR',
    'PENDING',
    NOW()::DATE + INTERVAL '15 days',
    (NOW() - INTERVAL '15 days')::DATE,
    NOW()::DATE - INTERVAL '1 day',
    NOW() - INTERVAL '15 days',
    NOW() - INTERVAL '15 days'
FROM subscriptions s
JOIN organizations o ON s.organization_id = o.id
WHERE o.name = 'Bordeaux Immobilier'
ON CONFLICT (invoice_number) DO NOTHING;

-- Nice Properties (ENTERPRISE - 149€/mois) - Factures mensuelles
-- Facture 1: Il y a 6 mois (payée)
INSERT INTO invoices (subscription_id, invoice_number, organization_id, amount, tax_amount, total_amount, currency, status, due_date, paid_at, billing_period_start, billing_period_end, created_at, updated_at)
SELECT 
    s.id,
    'INV-NICE-2024-001',
    s.organization_id,
    149.00,
    29.80,
    178.80,
    'EUR',
    'PAID',
    (NOW() - INTERVAL '6 months')::DATE + INTERVAL '15 days',
    NOW() - INTERVAL '5 months 20 days',
    (NOW() - INTERVAL '6 months')::DATE,
    (NOW() - INTERVAL '5 months')::DATE - INTERVAL '1 day',
    NOW() - INTERVAL '6 months',
    NOW() - INTERVAL '6 months'
FROM subscriptions s
JOIN organizations o ON s.organization_id = o.id
WHERE o.name = 'Nice Properties'
ON CONFLICT (invoice_number) DO NOTHING;

-- Facture 2: Il y a 5 mois (payée)
INSERT INTO invoices (subscription_id, invoice_number, organization_id, amount, tax_amount, total_amount, currency, status, due_date, paid_at, billing_period_start, billing_period_end, created_at, updated_at)
SELECT 
    s.id,
    'INV-NICE-2024-002',
    s.organization_id,
    149.00,
    29.80,
    178.80,
    'EUR',
    'PAID',
    (NOW() - INTERVAL '5 months')::DATE + INTERVAL '15 days',
    NOW() - INTERVAL '4 months 20 days',
    (NOW() - INTERVAL '5 months')::DATE,
    (NOW() - INTERVAL '4 months')::DATE - INTERVAL '1 day',
    NOW() - INTERVAL '5 months',
    NOW() - INTERVAL '5 months'
FROM subscriptions s
JOIN organizations o ON s.organization_id = o.id
WHERE o.name = 'Nice Properties'
ON CONFLICT (invoice_number) DO NOTHING;

-- Facture 3: Il y a 4 mois (payée)
INSERT INTO invoices (subscription_id, invoice_number, organization_id, amount, tax_amount, total_amount, currency, status, due_date, paid_at, billing_period_start, billing_period_end, created_at, updated_at)
SELECT 
    s.id,
    'INV-NICE-2024-003',
    s.organization_id,
    149.00,
    29.80,
    178.80,
    'EUR',
    'PAID',
    (NOW() - INTERVAL '4 months')::DATE + INTERVAL '15 days',
    NOW() - INTERVAL '3 months 20 days',
    (NOW() - INTERVAL '4 months')::DATE,
    (NOW() - INTERVAL '3 months')::DATE - INTERVAL '1 day',
    NOW() - INTERVAL '4 months',
    NOW() - INTERVAL '4 months'
FROM subscriptions s
JOIN organizations o ON s.organization_id = o.id
WHERE o.name = 'Nice Properties'
ON CONFLICT (invoice_number) DO NOTHING;

-- Facture 4: Il y a 3 mois (payée)
INSERT INTO invoices (subscription_id, invoice_number, organization_id, amount, tax_amount, total_amount, currency, status, due_date, paid_at, billing_period_start, billing_period_end, created_at, updated_at)
SELECT 
    s.id,
    'INV-NICE-2024-004',
    s.organization_id,
    149.00,
    29.80,
    178.80,
    'EUR',
    'PAID',
    (NOW() - INTERVAL '3 months')::DATE + INTERVAL '15 days',
    NOW() - INTERVAL '2 months 20 days',
    (NOW() - INTERVAL '3 months')::DATE,
    (NOW() - INTERVAL '2 months')::DATE - INTERVAL '1 day',
    NOW() - INTERVAL '3 months',
    NOW() - INTERVAL '3 months'
FROM subscriptions s
JOIN organizations o ON s.organization_id = o.id
WHERE o.name = 'Nice Properties'
ON CONFLICT (invoice_number) DO NOTHING;

-- Facture 5: Il y a 2 mois (payée)
INSERT INTO invoices (subscription_id, invoice_number, organization_id, amount, tax_amount, total_amount, currency, status, due_date, paid_at, billing_period_start, billing_period_end, created_at, updated_at)
SELECT 
    s.id,
    'INV-NICE-2024-005',
    s.organization_id,
    149.00,
    29.80,
    178.80,
    'EUR',
    'PAID',
    (NOW() - INTERVAL '2 months')::DATE + INTERVAL '15 days',
    NOW() - INTERVAL '1 month 20 days',
    (NOW() - INTERVAL '2 months')::DATE,
    (NOW() - INTERVAL '1 month')::DATE - INTERVAL '1 day',
    NOW() - INTERVAL '2 months',
    NOW() - INTERVAL '2 months'
FROM subscriptions s
JOIN organizations o ON s.organization_id = o.id
WHERE o.name = 'Nice Properties'
ON CONFLICT (invoice_number) DO NOTHING;

-- Facture 6: Il y a 1 mois (payée)
INSERT INTO invoices (subscription_id, invoice_number, organization_id, amount, tax_amount, total_amount, currency, status, due_date, paid_at, billing_period_start, billing_period_end, created_at, updated_at)
SELECT 
    s.id,
    'INV-NICE-2024-006',
    s.organization_id,
    149.00,
    29.80,
    178.80,
    'EUR',
    'PAID',
    (NOW() - INTERVAL '1 month')::DATE + INTERVAL '15 days',
    NOW() - INTERVAL '20 days',
    (NOW() - INTERVAL '1 month')::DATE,
    NOW()::DATE - INTERVAL '1 day',
    NOW() - INTERVAL '1 month',
    NOW() - INTERVAL '1 month'
FROM subscriptions s
JOIN organizations o ON s.organization_id = o.id
WHERE o.name = 'Nice Properties'
ON CONFLICT (invoice_number) DO NOTHING;

-- Facture 7: Ce mois (en attente)
INSERT INTO invoices (subscription_id, invoice_number, organization_id, amount, tax_amount, total_amount, currency, status, due_date, billing_period_start, billing_period_end, created_at, updated_at)
SELECT 
    s.id,
    'INV-NICE-2024-007',
    s.organization_id,
    149.00,
    29.80,
    178.80,
    'EUR',
    'PENDING',
    NOW()::DATE + INTERVAL '15 days',
    NOW()::DATE,
    (NOW() + INTERVAL '1 month')::DATE - INTERVAL '1 day',
    NOW(),
    NOW()
FROM subscriptions s
JOIN organizations o ON s.organization_id = o.id
WHERE o.name = 'Nice Properties'
ON CONFLICT (invoice_number) DO NOTHING;

-- =====================================================
-- 5.4. CRÉER LES PAIEMENTS (PAYMENTS) POUR LES FACTURES PAYÉES
-- =====================================================

-- Paiements pour Immobilier Paris
-- Paiement pour INV-PARIS-2024-001
INSERT INTO payments (invoice_id, transaction_id, amount, currency, status, payment_method, paid_at, created_at)
SELECT 
    i.id,
    'TXN-PARIS-001-' || TO_CHAR(i.paid_at, 'YYYYMMDDHH24MISS'),
    i.total_amount,
    i.currency,
    'COMPLETED',
    'CREDIT_CARD',
    i.paid_at,
    i.paid_at
FROM invoices i
WHERE i.invoice_number = 'INV-PARIS-2024-001'
ON CONFLICT (transaction_id) DO NOTHING;

-- Paiement pour INV-PARIS-2024-002
INSERT INTO payments (invoice_id, transaction_id, amount, currency, status, payment_method, paid_at, created_at)
SELECT 
    i.id,
    'TXN-PARIS-002-' || TO_CHAR(i.paid_at, 'YYYYMMDDHH24MISS'),
    i.total_amount,
    i.currency,
    'COMPLETED',
    'CREDIT_CARD',
    i.paid_at,
    i.paid_at
FROM invoices i
WHERE i.invoice_number = 'INV-PARIS-2024-002'
ON CONFLICT (transaction_id) DO NOTHING;

-- Paiements pour Real Estate Lyon
-- Paiement pour INV-LYON-2024-001
INSERT INTO payments (invoice_id, transaction_id, amount, currency, status, payment_method, paid_at, created_at)
SELECT 
    i.id,
    'TXN-LYON-001-' || TO_CHAR(i.paid_at, 'YYYYMMDDHH24MISS'),
    i.total_amount,
    i.currency,
    'COMPLETED',
    'BANK_TRANSFER',
    i.paid_at,
    i.paid_at
FROM invoices i
WHERE i.invoice_number = 'INV-LYON-2024-001'
ON CONFLICT (transaction_id) DO NOTHING;

-- Paiements pour Property Marseille
-- Paiement pour INV-MARSEILLE-2024-001
INSERT INTO payments (invoice_id, transaction_id, amount, currency, status, payment_method, paid_at, created_at)
SELECT 
    i.id,
    'TXN-MARSEILLE-001-' || TO_CHAR(i.paid_at, 'YYYYMMDDHH24MISS'),
    i.total_amount,
    i.currency,
    'COMPLETED',
    'CREDIT_CARD',
    i.paid_at,
    i.paid_at
FROM invoices i
WHERE i.invoice_number = 'INV-MARSEILLE-2024-001'
ON CONFLICT (transaction_id) DO NOTHING;

-- Paiement pour INV-MARSEILLE-2024-002
INSERT INTO payments (invoice_id, transaction_id, amount, currency, status, payment_method, paid_at, created_at)
SELECT 
    i.id,
    'TXN-MARSEILLE-002-' || TO_CHAR(i.paid_at, 'YYYYMMDDHH24MISS'),
    i.total_amount,
    i.currency,
    'COMPLETED',
    'CREDIT_CARD',
    i.paid_at,
    i.paid_at
FROM invoices i
WHERE i.invoice_number = 'INV-MARSEILLE-2024-002'
ON CONFLICT (transaction_id) DO NOTHING;

-- Paiement pour INV-MARSEILLE-2024-003
INSERT INTO payments (invoice_id, transaction_id, amount, currency, status, payment_method, paid_at, created_at)
SELECT 
    i.id,
    'TXN-MARSEILLE-003-' || TO_CHAR(i.paid_at, 'YYYYMMDDHH24MISS'),
    i.total_amount,
    i.currency,
    'COMPLETED',
    'CREDIT_CARD',
    i.paid_at,
    i.paid_at
FROM invoices i
WHERE i.invoice_number = 'INV-MARSEILLE-2024-003'
ON CONFLICT (transaction_id) DO NOTHING;

-- Paiements pour Nice Properties
-- Paiement pour INV-NICE-2024-001
INSERT INTO payments (invoice_id, transaction_id, amount, currency, status, payment_method, paid_at, created_at)
SELECT 
    i.id,
    'TXN-NICE-001-' || TO_CHAR(i.paid_at, 'YYYYMMDDHH24MISS'),
    i.total_amount,
    i.currency,
    'COMPLETED',
    'CREDIT_CARD',
    i.paid_at,
    i.paid_at
FROM invoices i
WHERE i.invoice_number = 'INV-NICE-2024-001'
ON CONFLICT (transaction_id) DO NOTHING;

-- Paiement pour INV-NICE-2024-002
INSERT INTO payments (invoice_id, transaction_id, amount, currency, status, payment_method, paid_at, created_at)
SELECT 
    i.id,
    'TXN-NICE-002-' || TO_CHAR(i.paid_at, 'YYYYMMDDHH24MISS'),
    i.total_amount,
    i.currency,
    'COMPLETED',
    'CREDIT_CARD',
    i.paid_at,
    i.paid_at
FROM invoices i
WHERE i.invoice_number = 'INV-NICE-2024-002'
ON CONFLICT (transaction_id) DO NOTHING;

-- Paiement pour INV-NICE-2024-003
INSERT INTO payments (invoice_id, transaction_id, amount, currency, status, payment_method, paid_at, created_at)
SELECT 
    i.id,
    'TXN-NICE-003-' || TO_CHAR(i.paid_at, 'YYYYMMDDHH24MISS'),
    i.total_amount,
    i.currency,
    'COMPLETED',
    'CREDIT_CARD',
    i.paid_at,
    i.paid_at
FROM invoices i
WHERE i.invoice_number = 'INV-NICE-2024-003'
ON CONFLICT (transaction_id) DO NOTHING;

-- Paiement pour INV-NICE-2024-004
INSERT INTO payments (invoice_id, transaction_id, amount, currency, status, payment_method, paid_at, created_at)
SELECT 
    i.id,
    'TXN-NICE-004-' || TO_CHAR(i.paid_at, 'YYYYMMDDHH24MISS'),
    i.total_amount,
    i.currency,
    'COMPLETED',
    'CREDIT_CARD',
    i.paid_at,
    i.paid_at
FROM invoices i
WHERE i.invoice_number = 'INV-NICE-2024-004'
ON CONFLICT (transaction_id) DO NOTHING;

-- Paiement pour INV-NICE-2024-005
INSERT INTO payments (invoice_id, transaction_id, amount, currency, status, payment_method, paid_at, created_at)
SELECT 
    i.id,
    'TXN-NICE-005-' || TO_CHAR(i.paid_at, 'YYYYMMDDHH24MISS'),
    i.total_amount,
    i.currency,
    'COMPLETED',
    'CREDIT_CARD',
    i.paid_at,
    i.paid_at
FROM invoices i
WHERE i.invoice_number = 'INV-NICE-2024-005'
ON CONFLICT (transaction_id) DO NOTHING;

-- Paiement pour INV-NICE-2024-006
INSERT INTO payments (invoice_id, transaction_id, amount, currency, status, payment_method, paid_at, created_at)
SELECT 
    i.id,
    'TXN-NICE-006-' || TO_CHAR(i.paid_at, 'YYYYMMDDHH24MISS'),
    i.total_amount,
    i.currency,
    'COMPLETED',
    'CREDIT_CARD',
    i.paid_at,
    i.paid_at
FROM invoices i
WHERE i.invoice_number = 'INV-NICE-2024-006'
ON CONFLICT (transaction_id) DO NOTHING;

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
INSERT INTO properties (reference, title, description, type, status, price, currency, surface, rooms, bedrooms, bathrooms, full_bathrooms, appliances_included, laundry_location, total_structure_area, total_interior_livable_area, virtual_tour_url, parking_features, has_garage, accessibility_features, patio_porch, exterior_features, special_conditions, home_type, property_subtype, condition, year_built, subdivision, has_hoa, hoa_amenities, hoa_services, hoa_fee, hoa_fee_frequency, region, price_per_square_foot, date_on_market, address, city, postal_code, country, latitude, longitude, organization_id, team_id, assigned_user_id, created_by, active, features, created_at, updated_at)
VALUES 
    ('PROP-PARIS-001', 'Appartement luxueux à Paris', 'Magnifique appartement situé dans le cœur de Paris. Idéal pour une famille.', 'APARTMENT', 'PUBLISHED', 450000.00, 'EUR', 85.5, 4, 3, 2, 2, '["Dishwasher", "Refrigerator", "Oven", "Microwave"]', 'Inside', 85.5, 85.5, 'https://virtualtour.example.com/paris-001', '["Garage", "Street"]', true, '["Elevator Access"]', 'Balcony', '["Balcony", "Courtyard"]', 'Resale', 'Condo', 'Condominium', 'Good', 2010, 'Le Marais', true, '["Elevator(s)", "Laundry", "Pool"]', '["Maintenance", "Security"]', 250.00, 'monthly', 'Le Marais', 5263.16, '2024-01-15',
     '15 Rue de la République', 'Paris', '75001', 'France', 48.8606, 2.3376,
     (SELECT id FROM organizations WHERE name = 'Immobilier Paris'), NULL,
     (SELECT id FROM users WHERE email = 'agent1@paris-immobilier.fr'),
     (SELECT id FROM users WHERE email = 'directeur@paris-immobilier.fr'), true, '{"parking": true, "elevator": true, "balcony": true, "terrace": false, "garden": false, "swimming_pool": false, "air_conditioning": true, "heating": "central", "energy_class": "C", "floor": 5, "total_floors": 7}', NOW(), NOW()),
    ('PROP-PARIS-002', 'Maison moderne à Paris', 'Belle maison moderne avec jardin. Parfait pour une famille.', 'HOUSE', 'PUBLISHED', 650000.00, 'EUR', 120.0, 5, 4, 3, 3, '["Dishwasher", "Refrigerator", "Oven", "Microwave", "Washing Machine", "Dryer"]', 'Inside', 120.0, 120.0, 'https://virtualtour.example.com/paris-002', '["Garage", "Driveway"]', true, '[]', 'Patio', '["Terrace", "Garden", "Patio"]', 'Resale', 'House', 'Single Family', 'Good', 2015, 'Champs-Élysées', false, NULL, NULL, NULL, NULL, 'Champs-Élysées', 5416.67, '2024-02-01',
     '42 Avenue des Champs', 'Paris', '75008', 'France', 48.8738, 2.2950,
     (SELECT id FROM organizations WHERE name = 'Immobilier Paris'), NULL,
     (SELECT id FROM users WHERE email = 'agent2@paris-immobilier.fr'),
     (SELECT id FROM users WHERE email = 'directeur@paris-immobilier.fr'), true, '{"parking": true, "elevator": false, "balcony": false, "terrace": true, "garden": true, "swimming_pool": false, "air_conditioning": true, "heating": "gas", "energy_class": "B", "floor": 0, "total_floors": 2}', NOW(), NOW()),
    ('PROP-PARIS-003', 'Studio cosy Paris', 'Studio cosy et bien situé, idéal pour étudiant ou jeune actif.', 'APARTMENT', 'PUBLISHED', 180000.00, 'EUR', 35.0, 1, 0, 1, 1, '["Refrigerator", "Oven"]', 'None', 35.0, 35.0, NULL, '["Street"]', false, '["Elevator Access"]', NULL, '[]', 'Resale', 'Condo', 'Condominium', 'Fair', 1985, 'Quartier Latin', true, '["Elevator(s)", "Laundry"]', '["Maintenance"]', 120.00, 'monthly', 'Quartier Latin', 5142.86, '2024-03-10',
     '8 Boulevard Saint-Michel', 'Paris', '75005', 'France', 48.8506, 2.3442,
     (SELECT id FROM organizations WHERE name = 'Immobilier Paris'), NULL,
     (SELECT id FROM users WHERE email = 'agent3@paris-immobilier.fr'),
     (SELECT id FROM users WHERE email = 'manager@paris-immobilier.fr'), true, '{"parking": false, "elevator": true, "balcony": false, "terrace": false, "garden": false, "swimming_pool": false, "air_conditioning": false, "heating": "electric", "energy_class": "D", "floor": 3, "total_floors": 5}', NOW(), NOW()),
    ('PROP-PARIS-004', 'Loft design Paris', 'Loft design dans quartier branché. Caractère et modernité.', 'APARTMENT', 'PUBLISHED', 520000.00, 'EUR', 95.0, 3, 2, 2, 2, '["Dishwasher", "Refrigerator", "Oven", "Microwave", "Washing Machine"]', 'Inside', 95.0, 95.0, 'https://virtualtour.example.com/paris-004', '["Street"]', false, '["Elevator Access"]', 'Balcony', '["Balcony", "Exposed Beams"]', 'Resale', 'Condo', 'Loft', 'Good', 2005, 'Le Marais', true, '["Elevator(s)", "Laundry"]', '["Maintenance"]', 200.00, 'monthly', 'Le Marais', 5473.68, '2024-01-20',
     '25 Rue de Rivoli', 'Paris', '75004', 'France', 48.8566, 2.3522,
     (SELECT id FROM organizations WHERE name = 'Immobilier Paris'), NULL,
     (SELECT id FROM users WHERE email = 'agent1@paris-immobilier.fr'),
     (SELECT id FROM users WHERE email = 'directeur@paris-immobilier.fr'), true, '{"parking": false, "elevator": true, "balcony": true, "terrace": false, "garden": false, "swimming_pool": false, "air_conditioning": true, "heating": "central", "energy_class": "B", "floor": 2, "total_floors": 4, "exposed_beams": true, "high_ceiling": true}', NOW(), NOW()),
    ('PROP-PARIS-005', 'Terrain constructible Paris', 'Terrain constructible de 300m², idéal pour projet immobilier.', 'LAND', 'DRAFT', 250000.00, 'EUR', 300.0, 0, 0, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'New Construction', NULL, NULL, NULL, NULL, 'Montaigne', false, NULL, NULL, NULL, NULL, 'Montaigne', 833.33, NULL,
     '100 Avenue Montaigne', 'Paris', '75016', 'France', 48.8698, 2.3312,
     (SELECT id FROM organizations WHERE name = 'Immobilier Paris'), NULL,
     (SELECT id FROM users WHERE email = 'agent2@paris-immobilier.fr'),
     (SELECT id FROM users WHERE email = 'directeur@paris-immobilier.fr'), true, '{"buildable": true, "zoning": "residential", "utilities": true, "access_road": true, "slope": "flat", "orientation": "south", "view": "city"}', NOW(), NOW())
ON CONFLICT (reference) DO NOTHING;

-- Propriétés pour Real Estate Lyon (org_id = 2)
INSERT INTO properties (reference, title, description, type, status, price, currency, surface, rooms, bedrooms, bathrooms, full_bathrooms, appliances_included, laundry_location, total_structure_area, total_interior_livable_area, virtual_tour_url, parking_features, has_garage, accessibility_features, patio_porch, exterior_features, special_conditions, home_type, property_subtype, condition, year_built, subdivision, has_hoa, hoa_amenities, hoa_services, hoa_fee, hoa_fee_frequency, region, price_per_square_foot, date_on_market, address, city, postal_code, country, latitude, longitude, organization_id, team_id, assigned_user_id, created_by, active, features, created_at, updated_at)
VALUES 
    ('PROP-LYON-001', 'Appartement T3 Lyon', 'Appartement lumineux T3 dans le centre de Lyon.', 'APARTMENT', 'PUBLISHED', 280000.00, 'EUR', 65.0, 3, 2, 1, 1, '["Dishwasher", "Refrigerator", "Oven"]', 'Inside', 65.0, 65.0, NULL, '["Street"]', false, '["Elevator Access"]', 'Balcony', '["Balcony"]', 'Resale', 'Condo', 'Condominium', 'Good', 2000, 'Centre-ville', true, '["Elevator(s)", "Laundry"]', '["Maintenance"]', 150.00, 'monthly', 'Centre-ville', 4307.69, '2024-02-15',
     '12 Rue de la République', 'Lyon', '69001', 'France', 45.7640, 4.8357,
     (SELECT id FROM organizations WHERE name = 'Real Estate Lyon'), NULL,
     (SELECT id FROM users WHERE email = 'agent1@lyon-realestate.fr'),
     (SELECT id FROM users WHERE email = 'directeur@lyon-realestate.fr'), true, '{"parking": false, "elevator": true, "balcony": true, "terrace": false, "garden": false, "swimming_pool": false, "air_conditioning": false, "heating": "central", "energy_class": "C", "floor": 4, "total_floors": 6}', NOW(), NOW()),
    ('PROP-LYON-002', 'Maison avec jardin Lyon', 'Belle maison avec jardin, quartier calme.', 'HOUSE', 'PUBLISHED', 420000.00, 'EUR', 110.0, 4, 3, 2, 2, '["Dishwasher", "Refrigerator", "Oven", "Microwave", "Washing Machine", "Dryer"]', 'Inside', 110.0, 110.0, NULL, '["Garage", "Driveway"]', true, '[]', 'Patio', '["Terrace", "Garden", "Patio"]', 'Resale', 'House', 'Single Family', 'Good', 2012, 'Part-Dieu', false, NULL, NULL, NULL, NULL, 'Part-Dieu', 3818.18, '2024-03-01',
     '45 Rue du Faubourg', 'Lyon', '69003', 'France', 45.7597, 4.8422,
     (SELECT id FROM organizations WHERE name = 'Real Estate Lyon'), NULL,
     (SELECT id FROM users WHERE email = 'agent2@lyon-realestate.fr'),
     (SELECT id FROM users WHERE email = 'directeur@lyon-realestate.fr'), true, '{"parking": true, "elevator": false, "balcony": false, "terrace": true, "garden": true, "swimming_pool": false, "air_conditioning": false, "heating": "gas", "energy_class": "B", "floor": 0, "total_floors": 2, "garden_size": 80}', NOW(), NOW()),
    ('PROP-LYON-003', 'Local commercial Lyon', 'Local commercial idéal pour commerce ou bureau.', 'COMMERCIAL', 'PUBLISHED', 350000.00, 'EUR', 150.0, 2, 0, 1, 1, NULL, NULL, 150.0, 150.0, NULL, '["Garage", "Street"]', true, '["Wheelchair Access", "Elevator Access"]', NULL, '["Storefront"]', 'Resale', 'Commercial', 'Retail', 'Good', 1995, 'Centre-ville', false, NULL, NULL, NULL, NULL, 'Centre-ville', 2333.33, '2024-01-10',
     '78 Avenue de la République', 'Lyon', '69002', 'France', 45.7500, 4.8500,
     (SELECT id FROM organizations WHERE name = 'Real Estate Lyon'), NULL,
     (SELECT id FROM users WHERE email = 'agent1@lyon-realestate.fr'),
     (SELECT id FROM users WHERE email = 'manager@lyon-realestate.fr'), true, '{"parking": true, "elevator": true, "storefront": true, "storage": true, "office_space": true, "heating": "central", "air_conditioning": true, "commercial_license": true, "accessibility": true}', NOW(), NOW()),
    ('PROP-LYON-004', 'Appartement T2 Lyon', 'Appartement T2 rénové, proche transports.', 'APARTMENT', 'SOLD', 195000.00, 'EUR', 45.0, 2, 1, 1, 1, '["Refrigerator", "Oven"]', 'None', 45.0, 45.0, NULL, '["Street"]', false, '["Elevator Access"]', NULL, '[]', 'Resale', 'Condo', 'Condominium', 'Good', 1990, 'Part-Dieu', true, '["Elevator(s)", "Laundry"]', '["Maintenance"]', 100.00, 'monthly', 'Part-Dieu', 4333.33, '2023-11-20',
     '30 Rue Garibaldi', 'Lyon', '69006', 'France', 45.7715, 4.8526,
     (SELECT id FROM organizations WHERE name = 'Real Estate Lyon'), NULL,
     (SELECT id FROM users WHERE email = 'agent2@lyon-realestate.fr'),
     (SELECT id FROM users WHERE email = 'directeur@lyon-realestate.fr'), true, '{"parking": false, "elevator": true, "balcony": false, "terrace": false, "garden": false, "swimming_pool": false, "air_conditioning": false, "heating": "electric", "energy_class": "D", "floor": 2, "total_floors": 4, "renovated": 2020}', NOW(), NOW())
ON CONFLICT (reference) DO NOTHING;

-- Propriétés pour Property Marseille (org_id = 3)
INSERT INTO properties (reference, title, description, type, status, price, currency, surface, rooms, bedrooms, bathrooms, full_bathrooms, appliances_included, laundry_location, total_structure_area, total_interior_livable_area, virtual_tour_url, parking_features, has_garage, accessibility_features, patio_porch, exterior_features, special_conditions, home_type, property_subtype, condition, year_built, subdivision, has_hoa, hoa_amenities, hoa_services, hoa_fee, hoa_fee_frequency, region, price_per_square_foot, date_on_market, address, city, postal_code, country, latitude, longitude, organization_id, team_id, assigned_user_id, created_by, active, features, created_at, updated_at)
VALUES 
    ('PROP-MARSEILLE-001', 'Villa avec piscine Marseille', 'Superbe villa avec piscine, vue mer.', 'HOUSE', 'PUBLISHED', 850000.00, 'EUR', 180.0, 6, 4, 3, 3, '["Dishwasher", "Refrigerator", "Oven", "Microwave", "Washing Machine", "Dryer"]', 'Inside', 180.0, 180.0, 'https://virtualtour.example.com/marseille-001', '["Garage", "Driveway"]', true, '[]', 'Patio', '["Terrace", "Garden", "Patio", "Pool"]', 'Resale', 'House', 'Single Family', 'New', 2018, 'Corniche', false, NULL, NULL, NULL, NULL, 'Corniche', 4722.22, '2024-01-05',
     '15 Corniche Kennedy', 'Marseille', '13007', 'France', 43.2965, 5.3698,
     (SELECT id FROM organizations WHERE name = 'Property Marseille'), NULL,
     (SELECT id FROM users WHERE email = 'agent1@marseille-property.fr'),
     (SELECT id FROM users WHERE email = 'directeur@marseille-property.fr'), true, '{"parking": true, "elevator": false, "balcony": false, "terrace": true, "garden": true, "swimming_pool": true, "air_conditioning": true, "heating": "central", "energy_class": "B", "floor": 0, "total_floors": 2, "garden_size": 200, "pool_size": 40, "sea_view": true}', NOW(), NOW()),
    ('PROP-MARSEILLE-002', 'Appartement vue mer Marseille', 'Appartement avec vue mer, proche plage.', 'APARTMENT', 'PUBLISHED', 380000.00, 'EUR', 70.0, 3, 2, 2, 2, '["Dishwasher", "Refrigerator", "Oven", "Microwave"]', 'Inside', 70.0, 70.0, 'https://virtualtour.example.com/marseille-002', '["Street"]', false, '["Elevator Access"]', 'Balcony', '["Balcony"]', 'Resale', 'Condo', 'Condominium', 'Good', 2008, 'Vieux-Port', true, '["Elevator(s)", "Laundry", "Pool"]', '["Maintenance", "Security"]', 180.00, 'monthly', 'Vieux-Port', 5428.57, '2024-02-20',
     '22 Rue de la République', 'Marseille', '13001', 'France', 43.2965, 5.3764,
     (SELECT id FROM organizations WHERE name = 'Property Marseille'), NULL,
     (SELECT id FROM users WHERE email = 'agent2@marseille-property.fr'),
     (SELECT id FROM users WHERE email = 'directeur@marseille-property.fr'), true, '{"parking": false, "elevator": true, "balcony": true, "terrace": false, "garden": false, "swimming_pool": false, "air_conditioning": true, "heating": "central", "energy_class": "C", "floor": 6, "total_floors": 8, "sea_view": true}', NOW(), NOW()),
    ('PROP-MARSEILLE-003', 'Studio Marseille centre', 'Studio bien situé, proche métro.', 'APARTMENT', 'RENTED', 95000.00, 'EUR', 28.0, 1, 0, 1, 1, '["Refrigerator", "Oven"]', 'None', 28.0, 28.0, NULL, '["Street"]', false, '["Elevator Access"]', NULL, '[]', 'Resale', 'Condo', 'Condominium', 'Fair', 1975, 'Centre-ville', true, '["Elevator(s)", "Laundry"]', '["Maintenance"]', 90.00, 'monthly', 'Centre-ville', 3392.86, '2023-10-15',
     '8 Cours Belsunce', 'Marseille', '13001', 'France', 43.2965, 5.3764,
     (SELECT id FROM organizations WHERE name = 'Property Marseille'), NULL,
     (SELECT id FROM users WHERE email = 'agent3@marseille-property.fr'),
     (SELECT id FROM users WHERE email = 'manager@marseille-property.fr'), true, '{"parking": false, "elevator": true, "balcony": false, "terrace": false, "garden": false, "swimming_pool": false, "air_conditioning": false, "heating": "electric", "energy_class": "E", "floor": 1, "total_floors": 5}', NOW(), NOW()),
    ('PROP-MARSEILLE-004', 'Terrain constructible Marseille', 'Terrain de 500m², vue panoramique.', 'LAND', 'PUBLISHED', 180000.00, 'EUR', 500.0, 0, 0, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'New Construction', NULL, NULL, NULL, NULL, 'Goudes', false, NULL, NULL, NULL, NULL, 'Goudes', 360.00, '2024-03-15',
     '50 Chemin des Goudes', 'Marseille', '13008', 'France', 43.2200, 5.3500,
     (SELECT id FROM organizations WHERE name = 'Property Marseille'), NULL,
     (SELECT id FROM users WHERE email = 'agent1@marseille-property.fr'),
     (SELECT id FROM users WHERE email = 'directeur@marseille-property.fr'), true, '{"buildable": true, "zoning": "residential", "utilities": true, "access_road": true, "slope": "moderate", "orientation": "south", "view": "sea", "panoramic_view": true}', NOW(), NOW()),
    ('PROP-MARSEILLE-005', 'Maison de ville Marseille', 'Maison de ville rénovée, charme provençal.', 'HOUSE', 'PUBLISHED', 520000.00, 'EUR', 130.0, 5, 4, 2, 2, '["Dishwasher", "Refrigerator", "Oven", "Microwave", "Washing Machine", "Dryer"]', 'Inside', 130.0, 130.0, NULL, '["Garage", "Street"]', true, '[]', 'Patio', '["Terrace", "Patio"]', 'Resale', 'House', 'Townhouse', 'Good', 1920, 'Paradis', false, NULL, NULL, NULL, NULL, 'Paradis', 4000.00, '2024-01-25',
     '35 Rue Paradis', 'Marseille', '13006', 'France', 43.2900, 5.3800,
     (SELECT id FROM organizations WHERE name = 'Property Marseille'), NULL,
     (SELECT id FROM users WHERE email = 'agent2@marseille-property.fr'),
     (SELECT id FROM users WHERE email = 'directeur@marseille-property.fr'), true, '{"parking": true, "elevator": false, "balcony": false, "terrace": true, "garden": false, "swimming_pool": false, "air_conditioning": true, "heating": "gas", "energy_class": "C", "floor": 0, "total_floors": 3, "renovated": 2015, "period_features": true}', NOW(), NOW()),
    ('PROP-MARSEILLE-006', 'Appartement T4 Marseille', 'Appartement T4 spacieux, balcon.', 'APARTMENT', 'PUBLISHED', 320000.00, 'EUR', 85.0, 4, 3, 2, 2, '["Dishwasher", "Refrigerator", "Oven"]', 'Inside', 85.0, 85.0, NULL, '["Street"]', false, '["Elevator Access"]', 'Balcony', '["Balcony"]', 'Resale', 'Condo', 'Condominium', 'Good', 1995, 'Longchamp', true, '["Elevator(s)", "Laundry"]', '["Maintenance"]', 140.00, 'monthly', 'Longchamp', 3764.71, '2024-02-10',
     '18 Boulevard Longchamp', 'Marseille', '13001', 'France', 43.2965, 5.3764,
     (SELECT id FROM organizations WHERE name = 'Property Marseille'), NULL,
     (SELECT id FROM users WHERE email = 'agent3@marseille-property.fr'),
     (SELECT id FROM users WHERE email = 'directeur@marseille-property.fr'), true, '{"parking": false, "elevator": true, "balcony": true, "terrace": false, "garden": false, "swimming_pool": false, "air_conditioning": false, "heating": "central", "energy_class": "D", "floor": 3, "total_floors": 6}', NOW(), NOW())
ON CONFLICT (reference) DO NOTHING;

-- Propriétés pour Bordeaux Immobilier (org_id = 4)
INSERT INTO properties (reference, title, description, type, status, price, currency, surface, rooms, bedrooms, bathrooms, full_bathrooms, appliances_included, laundry_location, total_structure_area, total_interior_livable_area, virtual_tour_url, parking_features, has_garage, accessibility_features, patio_porch, exterior_features, special_conditions, home_type, property_subtype, condition, year_built, subdivision, has_hoa, hoa_amenities, hoa_services, hoa_fee, hoa_fee_frequency, region, price_per_square_foot, date_on_market, address, city, postal_code, country, latitude, longitude, organization_id, team_id, assigned_user_id, created_by, active, features, created_at, updated_at)
VALUES 
    ('PROP-BORDEAUX-001', 'Appartement centre Bordeaux', 'Appartement dans le centre historique.', 'APARTMENT', 'PUBLISHED', 295000.00, 'EUR', 60.0, 3, 2, 1, 1, '["Refrigerator", "Oven"]', 'None', 60.0, 60.0, NULL, '["Street"]', false, '[]', NULL, '[]', 'Resale', 'Condo', 'Historic', 'Fair', 1850, 'Centre historique', false, NULL, NULL, NULL, NULL, 'Centre historique', 4916.67, '2024-02-05',
     '10 Rue Sainte-Catherine', 'Bordeaux', '33000', 'France', 44.8378, -0.5792,
     (SELECT id FROM organizations WHERE name = 'Bordeaux Immobilier'), NULL,
     (SELECT id FROM users WHERE email = 'agent1@bordeaux-immobilier.fr'),
     (SELECT id FROM users WHERE email = 'directeur@bordeaux-immobilier.fr'), true, '{"parking": false, "elevator": false, "balcony": false, "terrace": false, "garden": false, "swimming_pool": false, "air_conditioning": false, "heating": "central", "energy_class": "D", "floor": 2, "total_floors": 4, "period_features": true, "historic_building": true}', NOW(), NOW()),
    ('PROP-BORDEAUX-002', 'Maison avec terrasse Bordeaux', 'Maison avec grande terrasse, jardin.', 'HOUSE', 'PUBLISHED', 480000.00, 'EUR', 125.0, 5, 4, 3, 3, '["Dishwasher", "Refrigerator", "Oven", "Microwave", "Washing Machine", "Dryer"]', 'Inside', 125.0, 125.0, NULL, '["Garage", "Driveway"]', true, '[]', 'Patio', '["Terrace", "Garden", "Patio"]', 'Resale', 'House', 'Single Family', 'Good', 2010, 'Intendance', false, NULL, NULL, NULL, NULL, 'Intendance', 3840.00, '2024-01-30',
     '25 Cours de l''Intendance', 'Bordeaux', '33000', 'France', 44.8400, -0.5800,
     (SELECT id FROM organizations WHERE name = 'Bordeaux Immobilier'), NULL,
     (SELECT id FROM users WHERE email = 'agent1@bordeaux-immobilier.fr'),
     (SELECT id FROM users WHERE email = 'directeur@bordeaux-immobilier.fr'), true, '{"parking": true, "elevator": false, "balcony": false, "terrace": true, "garden": true, "swimming_pool": false, "air_conditioning": true, "heating": "gas", "energy_class": "C", "floor": 0, "total_floors": 2, "garden_size": 150, "terrace_size": 30}', NOW(), NOW()),
    ('PROP-BORDEAUX-003', 'Local commercial Bordeaux', 'Local commercial en centre-ville.', 'COMMERCIAL', 'PUBLISHED', 280000.00, 'EUR', 120.0, 1, 0, 1, 1, NULL, NULL, 120.0, 120.0, NULL, '["Street"]', false, '[]', NULL, '["Storefront"]', 'Resale', 'Commercial', 'Retail', 'Fair', 1900, 'Centre-ville', false, NULL, NULL, NULL, NULL, 'Centre-ville', 2333.33, '2024-03-05',
     '40 Rue des Trois-Conils', 'Bordeaux', '33000', 'France', 44.8378, -0.5792,
     (SELECT id FROM organizations WHERE name = 'Bordeaux Immobilier'), NULL,
     (SELECT id FROM users WHERE email = 'agent1@bordeaux-immobilier.fr'),
     (SELECT id FROM users WHERE email = 'manager@bordeaux-immobilier.fr'), true, '{"parking": false, "elevator": false, "storefront": true, "storage": true, "office_space": false, "heating": "electric", "air_conditioning": false, "commercial_license": true, "accessibility": false, "historic_building": true}', NOW(), NOW())
ON CONFLICT (reference) DO NOTHING;

-- Propriétés pour Nice Properties (org_id = 5)
INSERT INTO properties (reference, title, description, type, status, price, currency, surface, rooms, bedrooms, bathrooms, full_bathrooms, appliances_included, laundry_location, total_structure_area, total_interior_livable_area, virtual_tour_url, parking_features, has_garage, accessibility_features, patio_porch, exterior_features, special_conditions, home_type, property_subtype, condition, year_built, subdivision, has_hoa, hoa_amenities, hoa_services, hoa_fee, hoa_fee_frequency, region, price_per_square_foot, date_on_market, address, city, postal_code, country, latitude, longitude, organization_id, team_id, assigned_user_id, created_by, active, features, created_at, updated_at)
VALUES 
    ('PROP-NICE-001', 'Appartement vue mer Nice', 'Appartement avec vue mer exceptionnelle.', 'APARTMENT', 'PUBLISHED', 420000.00, 'EUR', 75.0, 3, 2, 2, 2, '["Dishwasher", "Refrigerator", "Oven", "Microwave"]', 'Inside', 75.0, 75.0, 'https://virtualtour.example.com/nice-001', '["Street"]', false, '["Elevator Access"]', 'Balcony', '["Balcony"]', 'Resale', 'Condo', 'Condominium', 'Good', 2015, 'Promenade des Anglais', true, '["Elevator(s)", "Laundry", "Pool"]', '["Maintenance", "Security"]', 220.00, 'monthly', 'Promenade des Anglais', 5600.00, '2024-01-12',
     '12 Promenade des Anglais', 'Nice', '06000', 'France', 43.6959, 7.2554,
     (SELECT id FROM organizations WHERE name = 'Nice Properties'), NULL,
     (SELECT id FROM users WHERE email = 'agent1@nice-properties.fr'),
     (SELECT id FROM users WHERE email = 'directeur@nice-properties.fr'), true, '{"parking": false, "elevator": true, "balcony": true, "terrace": false, "garden": false, "swimming_pool": false, "air_conditioning": true, "heating": "central", "energy_class": "B", "floor": 8, "total_floors": 10, "sea_view": true, "panoramic_view": true}', NOW(), NOW()),
    ('PROP-NICE-002', 'Villa de prestige Nice', 'Villa de prestige avec piscine, vue panoramique.', 'HOUSE', 'PUBLISHED', 1200000.00, 'EUR', 250.0, 7, 5, 4, 4, '["Dishwasher", "Refrigerator", "Oven", "Microwave", "Washing Machine", "Dryer", "Wine Cooler"]', 'Inside', 250.0, 250.0, 'https://virtualtour.example.com/nice-002', '["Garage", "Driveway"]', true, '["Elevator Access"]', 'Patio', '["Terrace", "Garden", "Patio", "Pool"]', 'Resale', 'House', 'Luxury', 'New', 2020, 'Cimiez', false, NULL, NULL, NULL, NULL, 'Cimiez', 4800.00, '2024-01-01',
     '50 Boulevard de Cimiez', 'Nice', '06000', 'France', 43.7100, 7.2700,
     (SELECT id FROM organizations WHERE name = 'Nice Properties'), NULL,
     (SELECT id FROM users WHERE email = 'agent2@nice-properties.fr'),
     (SELECT id FROM users WHERE email = 'directeur@nice-properties.fr'), true, '{"parking": true, "elevator": true, "balcony": false, "terrace": true, "garden": true, "swimming_pool": true, "air_conditioning": true, "heating": "central", "energy_class": "A", "floor": 0, "total_floors": 3, "garden_size": 500, "pool_size": 60, "sea_view": true, "panoramic_view": true, "luxury": true}', NOW(), NOW()),
    ('PROP-NICE-003', 'Studio Nice centre', 'Studio proche plage et commerces.', 'APARTMENT', 'RENTED', 110000.00, 'EUR', 30.0, 1, 0, 1, 1, '["Refrigerator", "Oven"]', 'None', 30.0, 30.0, NULL, '["Street"]', false, '["Elevator Access"]', NULL, '[]', 'Resale', 'Condo', 'Condominium', 'Fair', 1980, 'Centre-ville', true, '["Elevator(s)", "Laundry"]', '["Maintenance"]', 95.00, 'monthly', 'Centre-ville', 3666.67, '2023-09-10',
     '8 Rue de France', 'Nice', '06000', 'France', 43.6959, 7.2554,
     (SELECT id FROM organizations WHERE name = 'Nice Properties'), NULL,
     (SELECT id FROM users WHERE email = 'agent1@nice-properties.fr'),
     (SELECT id FROM users WHERE email = 'directeur@nice-properties.fr'), true, '{"parking": false, "elevator": true, "balcony": false, "terrace": false, "garden": false, "swimming_pool": false, "air_conditioning": false, "heating": "electric", "energy_class": "E", "floor": 1, "total_floors": 4}', NOW(), NOW()),
    ('PROP-NICE-004', 'Appartement T3 Nice', 'Appartement T3 rénové, proche vieille ville.', 'APARTMENT', 'PUBLISHED', 350000.00, 'EUR', 68.0, 3, 2, 1, 1, '["Dishwasher", "Refrigerator", "Oven", "Microwave"]', 'Inside', 68.0, 68.0, NULL, '["Street"]', false, '["Elevator Access"]', 'Balcony', '["Balcony"]', 'Resale', 'Condo', 'Condominium', 'Good', 1995, 'Jean Médecin', true, '["Elevator(s)", "Laundry"]', '["Maintenance"]', 160.00, 'monthly', 'Jean Médecin', 5147.06, '2024-02-28',
     '22 Avenue Jean Médecin', 'Nice', '06000', 'France', 43.7000, 7.2600,
     (SELECT id FROM organizations WHERE name = 'Nice Properties'), NULL,
     (SELECT id FROM users WHERE email = 'agent2@nice-properties.fr'),
     (SELECT id FROM users WHERE email = 'directeur@nice-properties.fr'), true, '{"parking": false, "elevator": true, "balcony": true, "terrace": false, "garden": false, "swimming_pool": false, "air_conditioning": true, "heating": "central", "energy_class": "C", "floor": 3, "total_floors": 5, "renovated": 2021}', NOW(), NOW())
ON CONFLICT (reference) DO NOTHING;

-- =====================================================
-- 11. CRÉER DES PROPRIÉTÉS FEATURES
-- =====================================================

-- Features pour PROP-PARIS-001 (Appartement luxueux à Paris)
INSERT INTO property_features (property_id, key, value, type, active, created_at, updated_at)
SELECT 
    (SELECT id FROM properties WHERE reference = 'PROP-PARIS-001'), 'appliance', 'Dishwasher', 'STRING', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM property_features WHERE property_id = (SELECT id FROM properties WHERE reference = 'PROP-PARIS-001') AND key = 'appliance' AND value = 'Dishwasher')
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-PARIS-001'), 'appliance', 'Refrigerator', 'STRING', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM property_features WHERE property_id = (SELECT id FROM properties WHERE reference = 'PROP-PARIS-001') AND key = 'appliance' AND value = 'Refrigerator')
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-PARIS-001'), 'appliance', 'Oven', 'STRING', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM property_features WHERE property_id = (SELECT id FROM properties WHERE reference = 'PROP-PARIS-001') AND key = 'appliance' AND value = 'Oven')
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-PARIS-001'), 'appliance', 'Microwave', 'STRING', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM property_features WHERE property_id = (SELECT id FROM properties WHERE reference = 'PROP-PARIS-001') AND key = 'appliance' AND value = 'Microwave')
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-PARIS-001'), 'parking_feature', 'Garage', 'STRING', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM property_features WHERE property_id = (SELECT id FROM properties WHERE reference = 'PROP-PARIS-001') AND key = 'parking_feature' AND value = 'Garage')
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-PARIS-001'), 'parking_feature', 'Street', 'STRING', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM property_features WHERE property_id = (SELECT id FROM properties WHERE reference = 'PROP-PARIS-001') AND key = 'parking_feature' AND value = 'Street')
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-PARIS-001'), 'accessibility_feature', 'Elevator Access', 'STRING', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM property_features WHERE property_id = (SELECT id FROM properties WHERE reference = 'PROP-PARIS-001') AND key = 'accessibility_feature' AND value = 'Elevator Access')
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-PARIS-001'), 'exterior_feature', 'Balcony', 'STRING', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM property_features WHERE property_id = (SELECT id FROM properties WHERE reference = 'PROP-PARIS-001') AND key = 'exterior_feature' AND value = 'Balcony')
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-PARIS-001'), 'exterior_feature', 'Courtyard', 'STRING', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM property_features WHERE property_id = (SELECT id FROM properties WHERE reference = 'PROP-PARIS-001') AND key = 'exterior_feature' AND value = 'Courtyard')
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-PARIS-001'), 'patio_porch', 'Balcony', 'STRING', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM property_features WHERE property_id = (SELECT id FROM properties WHERE reference = 'PROP-PARIS-001') AND key = 'patio_porch' AND value = 'Balcony')
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-PARIS-001'), 'special_condition', 'Resale', 'STRING', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM property_features WHERE property_id = (SELECT id FROM properties WHERE reference = 'PROP-PARIS-001') AND key = 'special_condition' AND value = 'Resale')
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-PARIS-001'), 'hoa_amenity', 'Elevator(s)', 'STRING', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM property_features WHERE property_id = (SELECT id FROM properties WHERE reference = 'PROP-PARIS-001') AND key = 'hoa_amenity' AND value = 'Elevator(s)')
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-PARIS-001'), 'hoa_amenity', 'Laundry', 'STRING', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM property_features WHERE property_id = (SELECT id FROM properties WHERE reference = 'PROP-PARIS-001') AND key = 'hoa_amenity' AND value = 'Laundry')
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-PARIS-001'), 'hoa_amenity', 'Pool', 'STRING', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM property_features WHERE property_id = (SELECT id FROM properties WHERE reference = 'PROP-PARIS-001') AND key = 'hoa_amenity' AND value = 'Pool')
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-PARIS-001'), 'hoa_service', 'Maintenance', 'STRING', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM property_features WHERE property_id = (SELECT id FROM properties WHERE reference = 'PROP-PARIS-001') AND key = 'hoa_service' AND value = 'Maintenance')
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-PARIS-001'), 'hoa_service', 'Security', 'STRING', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM property_features WHERE property_id = (SELECT id FROM properties WHERE reference = 'PROP-PARIS-001') AND key = 'hoa_service' AND value = 'Security');

-- Features pour PROP-PARIS-002 (Maison moderne à Paris)
INSERT INTO property_features (property_id, key, value, type, active, created_at, updated_at)
SELECT 
    (SELECT id FROM properties WHERE reference = 'PROP-PARIS-002'), 'appliance', 'Dishwasher', 'STRING', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM property_features WHERE property_id = (SELECT id FROM properties WHERE reference = 'PROP-PARIS-002') AND key = 'appliance' AND value = 'Dishwasher')
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-PARIS-002'), 'appliance', 'Refrigerator', 'STRING', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM property_features WHERE property_id = (SELECT id FROM properties WHERE reference = 'PROP-PARIS-002') AND key = 'appliance' AND value = 'Refrigerator')
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-PARIS-002'), 'appliance', 'Oven', 'STRING', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM property_features WHERE property_id = (SELECT id FROM properties WHERE reference = 'PROP-PARIS-002') AND key = 'appliance' AND value = 'Oven')
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-PARIS-002'), 'appliance', 'Microwave', 'STRING', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM property_features WHERE property_id = (SELECT id FROM properties WHERE reference = 'PROP-PARIS-002') AND key = 'appliance' AND value = 'Microwave')
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-PARIS-002'), 'appliance', 'Washing Machine', 'STRING', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM property_features WHERE property_id = (SELECT id FROM properties WHERE reference = 'PROP-PARIS-002') AND key = 'appliance' AND value = 'Washing Machine')
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-PARIS-002'), 'appliance', 'Dryer', 'STRING', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM property_features WHERE property_id = (SELECT id FROM properties WHERE reference = 'PROP-PARIS-002') AND key = 'appliance' AND value = 'Dryer')
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-PARIS-002'), 'parking_feature', 'Garage', 'STRING', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM property_features WHERE property_id = (SELECT id FROM properties WHERE reference = 'PROP-PARIS-002') AND key = 'parking_feature' AND value = 'Garage')
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-PARIS-002'), 'parking_feature', 'Driveway', 'STRING', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM property_features WHERE property_id = (SELECT id FROM properties WHERE reference = 'PROP-PARIS-002') AND key = 'parking_feature' AND value = 'Driveway')
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-PARIS-002'), 'exterior_feature', 'Terrace', 'STRING', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM property_features WHERE property_id = (SELECT id FROM properties WHERE reference = 'PROP-PARIS-002') AND key = 'exterior_feature' AND value = 'Terrace')
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-PARIS-002'), 'exterior_feature', 'Garden', 'STRING', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM property_features WHERE property_id = (SELECT id FROM properties WHERE reference = 'PROP-PARIS-002') AND key = 'exterior_feature' AND value = 'Garden')
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-PARIS-002'), 'exterior_feature', 'Patio', 'STRING', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM property_features WHERE property_id = (SELECT id FROM properties WHERE reference = 'PROP-PARIS-002') AND key = 'exterior_feature' AND value = 'Patio')
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-PARIS-002'), 'patio_porch', 'Patio', 'STRING', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM property_features WHERE property_id = (SELECT id FROM properties WHERE reference = 'PROP-PARIS-002') AND key = 'patio_porch' AND value = 'Patio')
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-PARIS-002'), 'special_condition', 'Resale', 'STRING', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM property_features WHERE property_id = (SELECT id FROM properties WHERE reference = 'PROP-PARIS-002') AND key = 'special_condition' AND value = 'Resale');

-- Features pour PROP-PARIS-004 (Loft design Paris)
INSERT INTO property_features (property_id, key, value, type, active, created_at, updated_at)
SELECT 
    (SELECT id FROM properties WHERE reference = 'PROP-PARIS-004'), 'appliance', 'Dishwasher', 'STRING', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM property_features WHERE property_id = (SELECT id FROM properties WHERE reference = 'PROP-PARIS-004') AND key = 'appliance' AND value = 'Dishwasher')
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-PARIS-004'), 'appliance', 'Refrigerator', 'STRING', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM property_features WHERE property_id = (SELECT id FROM properties WHERE reference = 'PROP-PARIS-004') AND key = 'appliance' AND value = 'Refrigerator')
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-PARIS-004'), 'appliance', 'Oven', 'STRING', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM property_features WHERE property_id = (SELECT id FROM properties WHERE reference = 'PROP-PARIS-004') AND key = 'appliance' AND value = 'Oven')
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-PARIS-004'), 'appliance', 'Microwave', 'STRING', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM property_features WHERE property_id = (SELECT id FROM properties WHERE reference = 'PROP-PARIS-004') AND key = 'appliance' AND value = 'Microwave')
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-PARIS-004'), 'appliance', 'Washing Machine', 'STRING', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM property_features WHERE property_id = (SELECT id FROM properties WHERE reference = 'PROP-PARIS-004') AND key = 'appliance' AND value = 'Washing Machine')
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-PARIS-004'), 'parking_feature', 'Street', 'STRING', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM property_features WHERE property_id = (SELECT id FROM properties WHERE reference = 'PROP-PARIS-004') AND key = 'parking_feature' AND value = 'Street')
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-PARIS-004'), 'accessibility_feature', 'Elevator Access', 'STRING', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM property_features WHERE property_id = (SELECT id FROM properties WHERE reference = 'PROP-PARIS-004') AND key = 'accessibility_feature' AND value = 'Elevator Access')
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-PARIS-004'), 'exterior_feature', 'Balcony', 'STRING', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM property_features WHERE property_id = (SELECT id FROM properties WHERE reference = 'PROP-PARIS-004') AND key = 'exterior_feature' AND value = 'Balcony')
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-PARIS-004'), 'exterior_feature', 'Exposed Beams', 'STRING', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM property_features WHERE property_id = (SELECT id FROM properties WHERE reference = 'PROP-PARIS-004') AND key = 'exterior_feature' AND value = 'Exposed Beams')
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-PARIS-004'), 'patio_porch', 'Balcony', 'STRING', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM property_features WHERE property_id = (SELECT id FROM properties WHERE reference = 'PROP-PARIS-004') AND key = 'patio_porch' AND value = 'Balcony')
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-PARIS-004'), 'special_condition', 'Resale', 'STRING', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM property_features WHERE property_id = (SELECT id FROM properties WHERE reference = 'PROP-PARIS-004') AND key = 'special_condition' AND value = 'Resale')
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-PARIS-004'), 'hoa_amenity', 'Elevator(s)', 'STRING', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM property_features WHERE property_id = (SELECT id FROM properties WHERE reference = 'PROP-PARIS-004') AND key = 'hoa_amenity' AND value = 'Elevator(s)')
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-PARIS-004'), 'hoa_amenity', 'Laundry', 'STRING', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM property_features WHERE property_id = (SELECT id FROM properties WHERE reference = 'PROP-PARIS-004') AND key = 'hoa_amenity' AND value = 'Laundry')
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-PARIS-004'), 'hoa_service', 'Maintenance', 'STRING', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM property_features WHERE property_id = (SELECT id FROM properties WHERE reference = 'PROP-PARIS-004') AND key = 'hoa_service' AND value = 'Maintenance');

-- Features pour PROP-MARSEILLE-001 (Villa avec piscine Marseille)
INSERT INTO property_features (property_id, key, value, type, active, created_at, updated_at)
SELECT 
    (SELECT id FROM properties WHERE reference = 'PROP-MARSEILLE-001'), 'appliance', 'Dishwasher', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-MARSEILLE-001'), 'appliance', 'Refrigerator', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-MARSEILLE-001'), 'appliance', 'Oven', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-MARSEILLE-001'), 'appliance', 'Microwave', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-MARSEILLE-001'), 'appliance', 'Washing Machine', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-MARSEILLE-001'), 'appliance', 'Dryer', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-MARSEILLE-001'), 'parking_feature', 'Garage', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-MARSEILLE-001'), 'parking_feature', 'Driveway', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-MARSEILLE-001'), 'exterior_feature', 'Terrace', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-MARSEILLE-001'), 'exterior_feature', 'Garden', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-MARSEILLE-001'), 'exterior_feature', 'Patio', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-MARSEILLE-001'), 'exterior_feature', 'Pool', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-MARSEILLE-001'), 'patio_porch', 'Patio', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-MARSEILLE-001'), 'special_condition', 'Resale', 'STRING', true, NOW(), NOW()
ON CONFLICT (property_id, key, value) DO NOTHING;

-- Features pour PROP-MARSEILLE-002 (Appartement vue mer Marseille)
INSERT INTO property_features (property_id, key, value, type, active, created_at, updated_at)
SELECT 
    (SELECT id FROM properties WHERE reference = 'PROP-MARSEILLE-002'), 'appliance', 'Dishwasher', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-MARSEILLE-002'), 'appliance', 'Refrigerator', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-MARSEILLE-002'), 'appliance', 'Oven', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-MARSEILLE-002'), 'appliance', 'Microwave', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-MARSEILLE-002'), 'parking_feature', 'Street', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-MARSEILLE-002'), 'accessibility_feature', 'Elevator Access', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-MARSEILLE-002'), 'exterior_feature', 'Balcony', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-MARSEILLE-002'), 'patio_porch', 'Balcony', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-MARSEILLE-002'), 'special_condition', 'Resale', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-MARSEILLE-002'), 'hoa_amenity', 'Elevator(s)', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-MARSEILLE-002'), 'hoa_amenity', 'Laundry', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-MARSEILLE-002'), 'hoa_amenity', 'Pool', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-MARSEILLE-002'), 'hoa_service', 'Maintenance', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-MARSEILLE-002'), 'hoa_service', 'Security', 'STRING', true, NOW(), NOW()
ON CONFLICT (property_id, key, value) DO NOTHING;

-- Features pour PROP-MARSEILLE-005 (Maison de ville Marseille)
INSERT INTO property_features (property_id, key, value, type, active, created_at, updated_at)
SELECT 
    (SELECT id FROM properties WHERE reference = 'PROP-MARSEILLE-005'), 'appliance', 'Dishwasher', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-MARSEILLE-005'), 'appliance', 'Refrigerator', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-MARSEILLE-005'), 'appliance', 'Oven', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-MARSEILLE-005'), 'appliance', 'Microwave', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-MARSEILLE-005'), 'appliance', 'Washing Machine', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-MARSEILLE-005'), 'appliance', 'Dryer', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-MARSEILLE-005'), 'parking_feature', 'Garage', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-MARSEILLE-005'), 'parking_feature', 'Street', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-MARSEILLE-005'), 'exterior_feature', 'Terrace', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-MARSEILLE-005'), 'exterior_feature', 'Patio', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-MARSEILLE-005'), 'patio_porch', 'Patio', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-MARSEILLE-005'), 'special_condition', 'Resale', 'STRING', true, NOW(), NOW()
ON CONFLICT (property_id, key, value) DO NOTHING;

-- Features pour PROP-NICE-001 (Appartement vue mer Nice)
INSERT INTO property_features (property_id, key, value, type, active, created_at, updated_at)
SELECT 
    (SELECT id FROM properties WHERE reference = 'PROP-NICE-001'), 'appliance', 'Dishwasher', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-NICE-001'), 'appliance', 'Refrigerator', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-NICE-001'), 'appliance', 'Oven', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-NICE-001'), 'appliance', 'Microwave', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-NICE-001'), 'parking_feature', 'Street', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-NICE-001'), 'accessibility_feature', 'Elevator Access', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-NICE-001'), 'exterior_feature', 'Balcony', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-NICE-001'), 'patio_porch', 'Balcony', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-NICE-001'), 'special_condition', 'Resale', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-NICE-001'), 'hoa_amenity', 'Elevator(s)', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-NICE-001'), 'hoa_amenity', 'Laundry', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-NICE-001'), 'hoa_amenity', 'Pool', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-NICE-001'), 'hoa_service', 'Maintenance', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-NICE-001'), 'hoa_service', 'Security', 'STRING', true, NOW(), NOW()
ON CONFLICT (property_id, key, value) DO NOTHING;

-- Features pour PROP-NICE-002 (Villa de prestige Nice)
INSERT INTO property_features (property_id, key, value, type, active, created_at, updated_at)
SELECT 
    (SELECT id FROM properties WHERE reference = 'PROP-NICE-002'), 'appliance', 'Dishwasher', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-NICE-002'), 'appliance', 'Refrigerator', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-NICE-002'), 'appliance', 'Oven', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-NICE-002'), 'appliance', 'Microwave', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-NICE-002'), 'appliance', 'Washing Machine', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-NICE-002'), 'appliance', 'Dryer', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-NICE-002'), 'appliance', 'Wine Cooler', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-NICE-002'), 'parking_feature', 'Garage', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-NICE-002'), 'parking_feature', 'Driveway', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-NICE-002'), 'accessibility_feature', 'Elevator Access', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-NICE-002'), 'exterior_feature', 'Terrace', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-NICE-002'), 'exterior_feature', 'Garden', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-NICE-002'), 'exterior_feature', 'Patio', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-NICE-002'), 'exterior_feature', 'Pool', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-NICE-002'), 'patio_porch', 'Patio', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-NICE-002'), 'special_condition', 'Resale', 'STRING', true, NOW(), NOW()
ON CONFLICT (property_id, key, value) DO NOTHING;

-- Features pour PROP-NICE-004 (Appartement T3 Nice)
INSERT INTO property_features (property_id, key, value, type, active, created_at, updated_at)
SELECT 
    (SELECT id FROM properties WHERE reference = 'PROP-NICE-004'), 'appliance', 'Dishwasher', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-NICE-004'), 'appliance', 'Refrigerator', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-NICE-004'), 'appliance', 'Oven', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-NICE-004'), 'appliance', 'Microwave', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-NICE-004'), 'parking_feature', 'Street', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-NICE-004'), 'accessibility_feature', 'Elevator Access', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-NICE-004'), 'exterior_feature', 'Balcony', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-NICE-004'), 'patio_porch', 'Balcony', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-NICE-004'), 'special_condition', 'Resale', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-NICE-004'), 'hoa_amenity', 'Elevator(s)', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-NICE-004'), 'hoa_amenity', 'Laundry', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-NICE-004'), 'hoa_service', 'Maintenance', 'STRING', true, NOW(), NOW()
ON CONFLICT (property_id, key, value) DO NOTHING;

-- Features pour PROP-LYON-002 (Maison avec jardin Lyon)
INSERT INTO property_features (property_id, key, value, type, active, created_at, updated_at)
SELECT 
    (SELECT id FROM properties WHERE reference = 'PROP-LYON-002'), 'appliance', 'Dishwasher', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-LYON-002'), 'appliance', 'Refrigerator', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-LYON-002'), 'appliance', 'Oven', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-LYON-002'), 'appliance', 'Microwave', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-LYON-002'), 'appliance', 'Washing Machine', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-LYON-002'), 'appliance', 'Dryer', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-LYON-002'), 'parking_feature', 'Garage', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-LYON-002'), 'parking_feature', 'Driveway', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-LYON-002'), 'exterior_feature', 'Terrace', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-LYON-002'), 'exterior_feature', 'Garden', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-LYON-002'), 'exterior_feature', 'Patio', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-LYON-002'), 'patio_porch', 'Patio', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-LYON-002'), 'special_condition', 'Resale', 'STRING', true, NOW(), NOW()
ON CONFLICT (property_id, key, value) DO NOTHING;

-- Features pour PROP-LYON-003 (Local commercial Lyon)
INSERT INTO property_features (property_id, key, value, type, active, created_at, updated_at)
SELECT 
    (SELECT id FROM properties WHERE reference = 'PROP-LYON-003'), 'parking_feature', 'Garage', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-LYON-003'), 'parking_feature', 'Street', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-LYON-003'), 'accessibility_feature', 'Wheelchair Access', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-LYON-003'), 'accessibility_feature', 'Elevator Access', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-LYON-003'), 'exterior_feature', 'Storefront', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-LYON-003'), 'special_condition', 'Resale', 'STRING', true, NOW(), NOW()
ON CONFLICT (property_id, key, value) DO NOTHING;

-- Features pour PROP-BORDEAUX-002 (Maison avec terrasse Bordeaux)
INSERT INTO property_features (property_id, key, value, type, active, created_at, updated_at)
SELECT 
    (SELECT id FROM properties WHERE reference = 'PROP-BORDEAUX-002'), 'appliance', 'Dishwasher', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-BORDEAUX-002'), 'appliance', 'Refrigerator', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-BORDEAUX-002'), 'appliance', 'Oven', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-BORDEAUX-002'), 'appliance', 'Microwave', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-BORDEAUX-002'), 'appliance', 'Washing Machine', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-BORDEAUX-002'), 'appliance', 'Dryer', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-BORDEAUX-002'), 'parking_feature', 'Garage', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-BORDEAUX-002'), 'parking_feature', 'Driveway', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-BORDEAUX-002'), 'exterior_feature', 'Terrace', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-BORDEAUX-002'), 'exterior_feature', 'Garden', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-BORDEAUX-002'), 'exterior_feature', 'Patio', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-BORDEAUX-002'), 'patio_porch', 'Patio', 'STRING', true, NOW(), NOW()
UNION ALL SELECT (SELECT id FROM properties WHERE reference = 'PROP-BORDEAUX-002'), 'special_condition', 'Resale', 'STRING', true, NOW(), NOW()
ON CONFLICT (property_id, key, value) DO NOTHING;

-- =====================================================
-- 12. CRÉER DES NOTIFICATIONS
-- =====================================================

-- Notifications pour Immobilier Paris
INSERT INTO notifications (type, title, message, recipient_id, organization_id, sender_id, status, channel, target_type, target_id, action_url, read_at, active, metadata, created_at, updated_at)
SELECT 
    'INFO', 'Bienvenue sur la plateforme', 'Votre compte a été créé avec succès. Explorez toutes les fonctionnalités disponibles.', u.id, o.id, NULL::BIGINT, 'SENT', 'IN_APP', NULL, NULL::BIGINT, NULL, NULL::TIMESTAMP, true, '{"welcome": true}', NOW() - INTERVAL '5 days', NOW() - INTERVAL '5 days'
FROM users u, organizations o
WHERE u.email = 'directeur@paris-immobilier.fr' AND o.name = 'Immobilier Paris'
UNION ALL SELECT 
    'SUCCESS', 'Nouvelle propriété ajoutée', 'La propriété "Appartement T2 Paris 15ème" a été ajoutée avec succès.', u.id, o.id, NULL::BIGINT, 'READ', 'IN_APP', 'PROPERTY', (SELECT id FROM properties WHERE reference = 'PROP-PARIS-001'), '/properties/' || (SELECT id::text FROM properties WHERE reference = 'PROP-PARIS-001'), NOW() - INTERVAL '2 days', true, '{"propertyId": 1}', NOW() - INTERVAL '2 days', NOW() - INTERVAL '2 days'
FROM users u, organizations o
WHERE u.email = 'manager@paris-immobilier.fr' AND o.name = 'Immobilier Paris'
UNION ALL SELECT 
    'ALERT', 'Paiement reçu', 'Un paiement de 29.00€ a été reçu pour votre abonnement STARTER.', u.id, o.id, NULL::BIGINT, 'SENT', 'IN_APP', 'SUBSCRIPTION', (SELECT id FROM subscriptions WHERE organization_id = o.id LIMIT 1), '/billing/subscriptions', NULL::TIMESTAMP, true, '{"amount": 29.00, "currency": "EUR"}', NOW() - INTERVAL '1 day', NOW() - INTERVAL '1 day'
FROM users u, organizations o
WHERE u.email = 'directeur@paris-immobilier.fr' AND o.name = 'Immobilier Paris'
UNION ALL SELECT 
    'INFO', 'Nouvelle tâche assignée', 'Une nouvelle tâche vous a été assignée.', u.id, o.id, (SELECT id FROM users WHERE email = 'directeur@paris-immobilier.fr')::BIGINT, 'PENDING', 'IN_APP', 'TASK', NULL::BIGINT, '/tasks', NULL::TIMESTAMP, true, '{"taskType": "FOLLOW_UP"}', NOW() - INTERVAL '3 hours', NOW() - INTERVAL '3 hours'
FROM users u, organizations o
WHERE u.email = 'agent1@paris-immobilier.fr' AND o.name = 'Immobilier Paris'
UNION ALL SELECT 
    'WARNING', 'Facture en attente', 'Votre facture est en attente de paiement.', u.id, o.id, NULL::BIGINT, 'SENT', 'IN_APP', 'INVOICE', (SELECT id FROM invoices WHERE organization_id = o.id ORDER BY created_at DESC LIMIT 1), '/billing/invoices/' || (SELECT id::text FROM invoices WHERE organization_id = o.id ORDER BY created_at DESC LIMIT 1), NULL::TIMESTAMP, true, '{"invoiceNumber": "INV-001"}', NOW() - INTERVAL '12 hours', NOW() - INTERVAL '12 hours'
FROM users u, organizations o
WHERE u.email = 'directeur@paris-immobilier.fr' AND o.name = 'Immobilier Paris'
ON CONFLICT DO NOTHING;

-- Notifications pour Real Estate Lyon
INSERT INTO notifications (type, title, message, recipient_id, organization_id, sender_id, status, channel, target_type, target_id, action_url, read_at, active, metadata, created_at, updated_at)
SELECT 
    'INFO', 'Bienvenue sur la plateforme', 'Votre compte a été créé avec succès. Explorez toutes les fonctionnalités disponibles.', u.id, o.id, NULL::BIGINT, 'SENT', 'IN_APP', NULL, NULL::BIGINT, NULL, NOW() - INTERVAL '1 day', true, '{"welcome": true}', NOW() - INTERVAL '4 days', NOW() - INTERVAL '4 days'
FROM users u, organizations o
WHERE u.email = 'directeur@lyon-realestate.fr' AND o.name = 'Real Estate Lyon'
UNION ALL SELECT 
    'SUCCESS', 'Nouvelle propriété ajoutée', 'La propriété "Maison avec jardin Lyon 6ème" a été ajoutée avec succès.', u.id, o.id, NULL::BIGINT, 'SENT', 'IN_APP', 'PROPERTY', (SELECT id FROM properties WHERE reference = 'PROP-LYON-002'), '/properties/' || (SELECT id::text FROM properties WHERE reference = 'PROP-LYON-002'), NULL::TIMESTAMP, true, '{"propertyId": 2}', NOW() - INTERVAL '1 day', NOW() - INTERVAL '1 day'
FROM users u, organizations o
WHERE u.email = 'manager@lyon-realestate.fr' AND o.name = 'Real Estate Lyon'
UNION ALL SELECT 
    'ALERT', 'Paiement reçu', 'Un paiement de 69.00€ a été reçu pour votre abonnement PROFESSIONAL.', u.id, o.id, NULL::BIGINT, 'READ', 'IN_APP', 'SUBSCRIPTION', (SELECT id FROM subscriptions WHERE organization_id = o.id LIMIT 1), '/billing/subscriptions', NOW() - INTERVAL '6 hours', true, '{"amount": 69.00, "currency": "EUR"}', NOW() - INTERVAL '2 days', NOW() - INTERVAL '2 days'
FROM users u, organizations o
WHERE u.email = 'directeur@lyon-realestate.fr' AND o.name = 'Real Estate Lyon'
ON CONFLICT DO NOTHING;

-- Notifications pour Property Marseille
INSERT INTO notifications (type, title, message, recipient_id, organization_id, sender_id, status, channel, target_type, target_id, action_url, read_at, active, metadata, created_at, updated_at)
SELECT 
    'INFO', 'Bienvenue sur la plateforme', 'Votre compte a été créé avec succès. Explorez toutes les fonctionnalités disponibles.', u.id, o.id, NULL::BIGINT, 'SENT', 'IN_APP', NULL, NULL::BIGINT, NULL, NULL::TIMESTAMP, true, '{"welcome": true}', NOW() - INTERVAL '3 days', NOW() - INTERVAL '3 days'
FROM users u, organizations o
WHERE u.email = 'directeur@marseille-property.fr' AND o.name = 'Property Marseille'
UNION ALL SELECT 
    'SUCCESS', 'Nouvelle propriété ajoutée', 'La propriété "Villa avec piscine Nice" a été ajoutée avec succès.', u.id, o.id, NULL::BIGINT, 'SENT', 'IN_APP', 'PROPERTY', (SELECT id FROM properties WHERE reference = 'PROP-NICE-002'), '/properties/' || (SELECT id::text FROM properties WHERE reference = 'PROP-NICE-002'), NULL::TIMESTAMP, true, '{"propertyId": 3}', NOW() - INTERVAL '1 day', NOW() - INTERVAL '1 day'
FROM users u, organizations o
WHERE u.email = 'manager@marseille-property.fr' AND o.name = 'Property Marseille'
UNION ALL SELECT 
    'INFO', 'Rappel de rendez-vous', 'Vous avez un rendez-vous prévu demain à 14h00 pour la visite de la propriété.', u.id, o.id, NULL::BIGINT, 'PENDING', 'IN_APP', 'PROPERTY', (SELECT id FROM properties WHERE reference = 'PROP-NICE-001'), '/properties/' || (SELECT id::text FROM properties WHERE reference = 'PROP-NICE-001'), NULL::TIMESTAMP, true, '{"appointmentDate": "2024-01-15T14:00:00"}', NOW() - INTERVAL '18 hours', NOW() - INTERVAL '18 hours'
FROM users u, organizations o
WHERE u.email = 'agent1@marseille-property.fr' AND o.name = 'Property Marseille'
ON CONFLICT DO NOTHING;

-- Notifications système pour tous les utilisateurs
INSERT INTO notifications (type, title, message, recipient_id, organization_id, sender_id, status, channel, target_type, target_id, action_url, read_at, active, metadata, created_at, updated_at)
SELECT 
    'SYSTEM', 'Mise à jour de la plateforme', 'Une nouvelle version de la plateforme est disponible avec de nouvelles fonctionnalités.', u.id, ou.organization_id, NULL::BIGINT, 'PENDING', 'IN_APP', NULL, NULL::BIGINT, '/settings', NULL::TIMESTAMP, true, '{"version": "1.2.0", "features": ["notifications", "documents"]}', NOW() - INTERVAL '6 hours', NOW() - INTERVAL '6 hours'
FROM users u
JOIN organization_users ou ON u.id = ou.user_id
WHERE ou.active = true
ON CONFLICT DO NOTHING;

-- =====================================================
-- 13. CRÉER DES ABONNEMENTS AUX NOTIFICATIONS
-- =====================================================

-- Abonnements par défaut pour tous les utilisateurs (tous les types de notifications activés)
INSERT INTO notification_subscriptions (user_id, organization_id, notification_type, channel, enabled, active, created_at, updated_at)
SELECT 
    u.id, ou.organization_id, 'PROPERTY_CREATED', 'IN_APP', true, true, NOW(), NOW()
FROM users u
JOIN organization_users ou ON u.id = ou.user_id
WHERE ou.active = true
UNION ALL SELECT 
    u.id, ou.organization_id, 'PROPERTY_UPDATED', 'IN_APP', true, true, NOW(), NOW()
FROM users u
JOIN organization_users ou ON u.id = ou.user_id
WHERE ou.active = true
UNION ALL SELECT 
    u.id, ou.organization_id, 'INVOICE_CREATED', 'IN_APP', true, true, NOW(), NOW()
FROM users u
JOIN organization_users ou ON u.id = ou.user_id
WHERE ou.active = true
UNION ALL SELECT 
    u.id, ou.organization_id, 'PAYMENT_RECEIVED', 'IN_APP', true, true, NOW(), NOW()
FROM users u
JOIN organization_users ou ON u.id = ou.user_id
WHERE ou.active = true
UNION ALL SELECT 
    u.id, ou.organization_id, 'TASK_ASSIGNED', 'IN_APP', true, true, NOW(), NOW()
FROM users u
JOIN organization_users ou ON u.id = ou.user_id
WHERE ou.active = true
UNION ALL SELECT 
    u.id, ou.organization_id, 'APPROVAL_REQUEST', 'IN_APP', true, true, NOW(), NOW()
FROM users u
JOIN organization_users ou ON u.id = ou.user_id
WHERE ou.active = true
ON CONFLICT DO NOTHING;

-- Abonnements email pour les directeurs (notifications importantes)
INSERT INTO notification_subscriptions (user_id, organization_id, notification_type, channel, enabled, active, created_at, updated_at)
SELECT 
    u.id, ou.organization_id, 'INVOICE_CREATED', 'EMAIL', true, true, NOW(), NOW()
FROM users u
JOIN organization_users ou ON u.id = ou.user_id
JOIN user_roles ur ON u.id = ur.user_id
JOIN roles r ON ur.role_id = r.id
WHERE ou.active = true AND r.name IN ('ADMIN', 'DIRECTOR')
UNION ALL SELECT 
    u.id, ou.organization_id, 'PAYMENT_RECEIVED', 'EMAIL', true, true, NOW(), NOW()
FROM users u
JOIN organization_users ou ON u.id = ou.user_id
JOIN user_roles ur ON u.id = ur.user_id
JOIN roles r ON ur.role_id = r.id
WHERE ou.active = true AND r.name IN ('ADMIN', 'DIRECTOR')
ON CONFLICT DO NOTHING;

-- =====================================================
-- 14. RÉSUMÉ ET STATISTIQUES
-- =====================================================

DO $$
DECLARE
    user_count INTEGER;
    org_count INTEGER;
    team_count INTEGER;
    org_user_count INTEGER;
    prop_count INTEGER;
    prop_feature_count INTEGER;
    role_count INTEGER;
    perm_count INTEGER;
    plan_count INTEGER;
    subscription_count INTEGER;
    invoice_count INTEGER;
    payment_count INTEGER;
    notification_count INTEGER;
    notification_subscription_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO user_count FROM users;
    SELECT COUNT(*) INTO org_count FROM organizations;
    SELECT COUNT(*) INTO team_count FROM teams;
    SELECT COUNT(*) INTO org_user_count FROM organization_users;
    SELECT COUNT(*) INTO prop_count FROM properties;
    SELECT COUNT(*) INTO prop_feature_count FROM property_features;
    SELECT COUNT(*) INTO role_count FROM roles;
    SELECT COUNT(*) INTO perm_count FROM permissions;
    SELECT COUNT(*) INTO plan_count FROM plans;
    SELECT COUNT(*) INTO subscription_count FROM subscriptions;
    SELECT COUNT(*) INTO invoice_count FROM invoices;
    SELECT COUNT(*) INTO payment_count FROM payments;
    SELECT COUNT(*) INTO notification_count FROM notifications;
    SELECT COUNT(*) INTO notification_subscription_count FROM notification_subscriptions;
    
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
    RAISE NOTICE '   • Caractéristiques de propriétés: %', prop_feature_count;
    RAISE NOTICE '   • Plans d''abonnement: %', plan_count;
    RAISE NOTICE '   • Abonnements actifs: %', subscription_count;
    RAISE NOTICE '   • Factures: %', invoice_count;
    RAISE NOTICE '   • Paiements: %', payment_count;
    RAISE NOTICE '   • Notifications: %', notification_count;
    RAISE NOTICE '   • Abonnements aux notifications: %', notification_subscription_count;
    RAISE NOTICE '';
    RAISE NOTICE '💰 PLANS DISPONIBLES:';
    RAISE NOTICE '   • FREE: 0€/mois (5 propriétés, 1 utilisateur)';
    RAISE NOTICE '   • STARTER: 29€/mois (50 propriétés, 3 utilisateurs) - 40%% moins cher que SeLoger';
    RAISE NOTICE '   • PROFESSIONAL: 69€/mois (200 propriétés, 10 utilisateurs) - 30%% moins cher';
    RAISE NOTICE '   • ENTERPRISE: 149€/mois (illimité) - 25%% moins cher';
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

