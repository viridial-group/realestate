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
DROP TABLE IF EXISTS tasks CASCADE;
DROP TABLE IF EXISTS approval_workflows CASCADE;
DROP TABLE IF EXISTS notification_subscriptions CASCADE;
DROP TABLE IF EXISTS notifications CASCADE;
DROP TABLE IF EXISTS payments CASCADE;
DROP TABLE IF EXISTS invoices CASCADE;
DROP TABLE IF EXISTS subscriptions CASCADE;
DROP TABLE IF EXISTS plans CASCADE;
DROP TABLE IF EXISTS visit_appointments CASCADE;
DROP TABLE IF EXISTS price_alerts CASCADE;
DROP TABLE IF EXISTS price_history CASCADE;
DROP TABLE IF EXISTS advertisement_clicks CASCADE;
DROP TABLE IF EXISTS advertisement_impressions CASCADE;
DROP TABLE IF EXISTS advertisements CASCADE;
DROP TABLE IF EXISTS organization_contact_messages CASCADE;
DROP TABLE IF EXISTS organization_reviews CASCADE;
DROP TABLE IF EXISTS reviews CASCADE;
DROP TABLE IF EXISTS dvf_import_history CASCADE;
DROP TABLE IF EXISTS dvf_transactions CASCADE;
DROP TABLE IF EXISTS contact_messages CASCADE;
DROP TABLE IF EXISTS blog_posts CASCADE;
DROP TABLE IF EXISTS property_events CASCADE;
DROP TABLE IF EXISTS property_features CASCADE;
DROP TABLE IF EXISTS property_accesses CASCADE;
DROP TABLE IF EXISTS properties CASCADE;
DROP TABLE IF EXISTS organization_users CASCADE;
DROP TABLE IF EXISTS teams CASCADE;
DROP TABLE IF EXISTS user_roles CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS organizations CASCADE;
DROP TABLE IF EXISTS cities CASCADE;
DROP TABLE IF EXISTS countries CASCADE;
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
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'tasks') THEN
        TRUNCATE TABLE tasks CASCADE;
    END IF;
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'approval_workflows') THEN
        TRUNCATE TABLE approval_workflows CASCADE;
    END IF;
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
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'contact_messages') THEN
        TRUNCATE TABLE contact_messages CASCADE;
    END IF;
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'visit_appointments') THEN
        TRUNCATE TABLE visit_appointments CASCADE;
    END IF;
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'price_alerts') THEN
        TRUNCATE TABLE price_alerts CASCADE;
    END IF;
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'price_history') THEN
        TRUNCATE TABLE price_history CASCADE;
    END IF;
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'blog_posts') THEN
        TRUNCATE TABLE blog_posts CASCADE;
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

-- =====================================================
-- TABLE: countries (Configuration SaaS)
-- =====================================================
CREATE TABLE IF NOT EXISTS countries (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(2) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    code3 VARCHAR(3),
    phone_code VARCHAR(10),
    currency VARCHAR(10),
    currency_symbol VARCHAR(10),
    timezone VARCHAR(50),
    flag_emoji TEXT,
    description TEXT,
    latitude DECIMAL(10, 7),
    longitude DECIMAL(10, 7),
    important_data JSONB,
    active BOOLEAN NOT NULL DEFAULT true,
    display_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_country_code ON countries(code);
CREATE INDEX IF NOT EXISTS idx_country_name ON countries(name);
CREATE INDEX IF NOT EXISTS idx_country_active ON countries(active);

COMMENT ON TABLE countries IS 'Table des pays pour la configuration SaaS';
COMMENT ON COLUMN countries.code IS 'Code ISO 3166-1 alpha-2 (ex: FR, US)';
COMMENT ON COLUMN countries.code3 IS 'Code ISO 3166-1 alpha-3 (ex: FRA, USA)';
COMMENT ON COLUMN countries.important_data IS 'Données importantes au format JSON (population, superficie, etc.)';

-- =====================================================
-- TABLE: cities (Configuration SaaS)
-- =====================================================
CREATE TABLE IF NOT EXISTS cities (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    postal_code VARCHAR(20),
    country_id BIGINT NOT NULL,
    latitude DECIMAL(10, 7),
    longitude DECIMAL(10, 7),
    region VARCHAR(100),
    department VARCHAR(100),
    timezone VARCHAR(50),
    description TEXT,
    important_data JSONB,
    active BOOLEAN NOT NULL DEFAULT true,
    display_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_city_country FOREIGN KEY (country_id) REFERENCES countries(id) ON DELETE CASCADE,
    CONSTRAINT uk_city_name_country UNIQUE (name, country_id)
);

CREATE INDEX IF NOT EXISTS idx_city_name ON cities(name);
CREATE INDEX IF NOT EXISTS idx_city_country ON cities(country_id);
CREATE INDEX IF NOT EXISTS idx_city_postal_code ON cities(postal_code);
CREATE INDEX IF NOT EXISTS idx_city_active ON cities(active);

COMMENT ON TABLE cities IS 'Table des villes pour la configuration SaaS';
COMMENT ON COLUMN cities.important_data IS 'Données importantes au format JSON (population, densité, attractions, etc.)';

-- =====================================================
-- TABLE: permissions
-- =====================================================
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
    -- Paramètres d'organisation
    logo_url VARCHAR(500),
    address VARCHAR(255),
    city VARCHAR(100),
    postal_code VARCHAR(20),
    country VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(255),
    custom_domains JSONB,
    quotas JSONB,
    default_office_hours TEXT,
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
    avatar_url VARCHAR(500),
    language VARCHAR(10) DEFAULT 'fr',
    timezone VARCHAR(50) DEFAULT 'Europe/Paris',
    notification_preferences VARCHAR(2000),
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
    transaction_type VARCHAR(50),
    slug VARCHAR(255) UNIQUE,
    -- SEO Meta Tags
    meta_title VARCHAR(255),
    meta_description VARCHAR(500),
    meta_keywords VARCHAR(500),
    og_image VARCHAR(500),
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
    -- Zillow-inspired fields
    pet_friendly BOOLEAN DEFAULT FALSE,
    special_offer VARCHAR(500),
    office_hours TEXT,
    neighborhood VARCHAR(100),
    walk_score INTEGER,
    transit_score INTEGER,
    bike_score INTEGER,
    building_name VARCHAR(200),
    flooring TEXT,
    unit_features TEXT,
    building_amenities TEXT,
    available_units TEXT,
    pet_policy TEXT,
    parking_policy VARCHAR(100),
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
CREATE INDEX IF NOT EXISTS idx_property_transaction_type ON properties(transaction_type) WHERE transaction_type IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_property_slug ON properties(slug) WHERE slug IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_property_meta_title ON properties(meta_title) WHERE meta_title IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_property_meta_keywords ON properties(meta_keywords) WHERE meta_keywords IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_property_type ON properties(type);
CREATE INDEX IF NOT EXISTS idx_property_city ON properties(city);
CREATE INDEX IF NOT EXISTS idx_property_pet_friendly ON properties(pet_friendly);
CREATE INDEX IF NOT EXISTS idx_property_neighborhood ON properties(neighborhood);
CREATE INDEX IF NOT EXISTS idx_property_building_name ON properties(building_name);
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

-- Table property_events
CREATE TABLE IF NOT EXISTS property_events (
    id BIGSERIAL PRIMARY KEY,
    property_id BIGINT NOT NULL,
    event_type VARCHAR(50) NOT NULL CHECK (event_type IN ('VIEW', 'CONTACT', 'FAVORITE', 'SHARE')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id BIGINT,
    metadata JSONB,
    CONSTRAINT fk_property_events_property 
        FOREIGN KEY (property_id) 
        REFERENCES properties(id) 
        ON DELETE CASCADE 
        ON UPDATE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_property_events_property_id ON property_events(property_id);
CREATE INDEX IF NOT EXISTS idx_property_events_created_at ON property_events(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_property_events_type ON property_events(event_type);
CREATE INDEX IF NOT EXISTS idx_property_events_property_date ON property_events(property_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_property_events_user_id ON property_events(user_id) WHERE user_id IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_property_events_date_type ON property_events(DATE(created_at), event_type);
CREATE INDEX IF NOT EXISTS idx_property_events_metadata ON property_events USING GIN (metadata) WHERE metadata IS NOT NULL;

COMMENT ON TABLE property_events IS 'Table pour stocker les événements liés aux propriétés (vues, contacts, favoris, partages). Permet de générer des statistiques historiques et des analytics.';
COMMENT ON COLUMN property_events.id IS 'Identifiant unique de l''événement (auto-incrémenté)';
COMMENT ON COLUMN property_events.property_id IS 'Identifiant de la propriété concernée (FK vers properties.id)';
COMMENT ON COLUMN property_events.event_type IS 'Type d''événement: VIEW (vue), CONTACT (contact), FAVORITE (favori), SHARE (partage)';
COMMENT ON COLUMN property_events.created_at IS 'Date et heure de création de l''événement (timestamp)';
COMMENT ON COLUMN property_events.user_id IS 'Identifiant de l''utilisateur qui a déclenché l''événement (optionnel, NULL pour les vues anonymes)';
COMMENT ON COLUMN property_events.metadata IS 'Métadonnées supplémentaires au format JSONB (ex: {"source": "frontend", "device": "mobile", "platform": "facebook"})';

-- Table blog_posts
CREATE TABLE IF NOT EXISTS blog_posts (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    excerpt VARCHAR(500),
    content TEXT,
    slug VARCHAR(255) NOT NULL UNIQUE,
    category VARCHAR(100),
    tags VARCHAR(500),
    featured_image VARCHAR(500),
    meta_description VARCHAR(500),
    meta_keywords VARCHAR(500),
    og_image VARCHAR(500),
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    published_at TIMESTAMP,
    author_id BIGINT NOT NULL,
    author_name VARCHAR(255),
    organization_id BIGINT,
    view_count BIGINT DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_blog_post_author FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT fk_blog_post_organization FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_blog_slug ON blog_posts(slug);
CREATE INDEX IF NOT EXISTS idx_blog_status ON blog_posts(status);
CREATE INDEX IF NOT EXISTS idx_blog_published_at ON blog_posts(published_at);
CREATE INDEX IF NOT EXISTS idx_blog_category ON blog_posts(category);

-- Table contact_messages
CREATE TABLE IF NOT EXISTS contact_messages (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    subject VARCHAR(500) NOT NULL,
    message TEXT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'NEW',
    property_id BIGINT,
    organization_id BIGINT,
    read_at TIMESTAMP,
    read_by BIGINT,
    replied_at TIMESTAMP,
    replied_by BIGINT,
    notes TEXT,
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_contact_property FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE SET NULL,
    CONSTRAINT fk_contact_organization FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_contact_email ON contact_messages(email);
CREATE INDEX IF NOT EXISTS idx_contact_status ON contact_messages(status);
CREATE INDEX IF NOT EXISTS idx_contact_property ON contact_messages(property_id);
CREATE INDEX IF NOT EXISTS idx_contact_created ON contact_messages(created_at);
CREATE INDEX IF NOT EXISTS idx_contact_organization ON contact_messages(organization_id);

-- Table organization_contact_messages
CREATE TABLE IF NOT EXISTS organization_contact_messages (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    subject VARCHAR(500) NOT NULL,
    message TEXT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'NEW',
    organization_id BIGINT NOT NULL,
    read_at TIMESTAMP,
    read_by BIGINT,
    replied_at TIMESTAMP,
    replied_by BIGINT,
    notes TEXT,
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_org_contact_organization FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_org_contact_email ON organization_contact_messages(email);
CREATE INDEX IF NOT EXISTS idx_org_contact_status ON organization_contact_messages(status);
CREATE INDEX IF NOT EXISTS idx_org_contact_organization ON organization_contact_messages(organization_id);
CREATE INDEX IF NOT EXISTS idx_org_contact_created ON organization_contact_messages(created_at);

-- Table advertisements
CREATE TABLE IF NOT EXISTS advertisements (
    id BIGSERIAL PRIMARY KEY,
    organization_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    image_url VARCHAR(500),
    link_url VARCHAR(500),
    ad_type VARCHAR(50) NOT NULL,
    position VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP,
    budget NUMERIC(15, 2),
    daily_budget NUMERIC(15, 2),
    cost_per_click NUMERIC(10, 2),
    cost_per_impression NUMERIC(10, 2),
    target_locations TEXT,
    target_property_types TEXT,
    target_transaction_types TEXT,
    impressions BIGINT NOT NULL DEFAULT 0,
    clicks BIGINT NOT NULL DEFAULT 0,
    conversions BIGINT NOT NULL DEFAULT 0,
    total_spent NUMERIC(15, 2) NOT NULL DEFAULT 0,
    max_impressions_per_day INTEGER,
    priority INTEGER NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    CONSTRAINT fk_advertisement_organization FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_ad_organization ON advertisements(organization_id);
CREATE INDEX IF NOT EXISTS idx_ad_status ON advertisements(status);
CREATE INDEX IF NOT EXISTS idx_ad_active ON advertisements(active);
CREATE INDEX IF NOT EXISTS idx_ad_type ON advertisements(ad_type);
CREATE INDEX IF NOT EXISTS idx_ad_start_end ON advertisements(start_date, end_date);

-- Table advertisement_impressions
CREATE TABLE IF NOT EXISTS advertisement_impressions (
    id BIGSERIAL PRIMARY KEY,
    advertisement_id BIGINT NOT NULL,
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    property_id BIGINT,
    page_type VARCHAR(50),
    city VARCHAR(100),
    postal_code VARCHAR(20),
    impressed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ad_impression_ad FOREIGN KEY (advertisement_id) REFERENCES advertisements(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_ad_impression_ad ON advertisement_impressions(advertisement_id);
CREATE INDEX IF NOT EXISTS idx_ad_impression_date ON advertisement_impressions(impressed_at);
CREATE INDEX IF NOT EXISTS idx_ad_impression_ip ON advertisement_impressions(ip_address);

-- Table advertisement_clicks
CREATE TABLE IF NOT EXISTS advertisement_clicks (
    id BIGSERIAL PRIMARY KEY,
    advertisement_id BIGINT NOT NULL,
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    referrer VARCHAR(500),
    property_id BIGINT,
    city VARCHAR(100),
    postal_code VARCHAR(20),
    clicked_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ad_click_ad FOREIGN KEY (advertisement_id) REFERENCES advertisements(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_ad_click_ad ON advertisement_clicks(advertisement_id);
CREATE INDEX IF NOT EXISTS idx_ad_click_date ON advertisement_clicks(clicked_at);
CREATE INDEX IF NOT EXISTS idx_ad_click_ip ON advertisement_clicks(ip_address);

-- Table reviews
CREATE TABLE IF NOT EXISTS reviews (
    id BIGSERIAL PRIMARY KEY,
    property_id BIGINT NOT NULL,
    user_id BIGINT,
    author_name VARCHAR(255),
    author_email VARCHAR(255),
    rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    verified_purchase BOOLEAN DEFAULT false,
    helpful_count INTEGER DEFAULT 0,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_review_property FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_review_property ON reviews(property_id);
CREATE INDEX IF NOT EXISTS idx_review_user ON reviews(user_id);
CREATE INDEX IF NOT EXISTS idx_review_status ON reviews(status);
CREATE INDEX IF NOT EXISTS idx_review_created_at ON reviews(created_at);

-- Table organization_reviews (avis d'agences)
CREATE TABLE IF NOT EXISTS organization_reviews (
    id BIGSERIAL PRIMARY KEY,
    organization_id BIGINT NOT NULL,
    user_id BIGINT,
    author_name VARCHAR(255),
    author_email VARCHAR(255),
    rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    verified_client BOOLEAN DEFAULT false,
    helpful_count INTEGER DEFAULT 0,
    transaction_type VARCHAR(50),
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_org_review_organization ON organization_reviews(organization_id);
CREATE INDEX IF NOT EXISTS idx_org_review_user ON organization_reviews(user_id);
CREATE INDEX IF NOT EXISTS idx_org_review_status ON organization_reviews(status);
CREATE INDEX IF NOT EXISTS idx_org_review_created_at ON organization_reviews(created_at);

-- =====================================================
-- AVIS D'AGENCES IMMOBILIÈRES (Organization Reviews)
-- =====================================================

-- Avis pour Immobilier Paris (ID 1)
INSERT INTO organization_reviews (organization_id, user_id, author_name, author_email, rating, comment, status, verified_client, helpful_count, transaction_type, active, created_at, updated_at)
SELECT 
    1,
    NULL::BIGINT,
    'Sophie Martin',
    'sophie.martin@example.com',
    5,
    'Excellente agence ! L''équipe est très professionnelle et réactive. Ils nous ont aidés à trouver notre appartement idéal à Paris en un temps record. Je recommande vivement !',
    'APPROVED',
    true,
    12,
    'SALE',
    true,
    NOW() - INTERVAL '30 days',
    NOW() - INTERVAL '30 days'
UNION ALL SELECT 
    1,
    NULL::BIGINT,
    'Pierre Durand',
    'pierre.durand@example.com',
    5,
    'Service impeccable de A à Z. L''agent était très à l''écoute et nous a guidés tout au long du processus d''achat. Très satisfait !',
    'APPROVED',
    true,
    8,
    'SALE',
    true,
    NOW() - INTERVAL '25 days',
    NOW() - INTERVAL '25 days'
UNION ALL SELECT 
    1,
    NULL::BIGINT,
    'Julie Bernard',
    'julie.bernard@example.com',
    4,
    'Bonne expérience globale. L''agence a un bon portefeuille de biens et les visites sont bien organisées. Quelques petits délais mais rien de rédhibitoire.',
    'APPROVED',
    true,
    5,
    'RENT',
    true,
    NOW() - INTERVAL '20 days',
    NOW() - INTERVAL '20 days'
UNION ALL SELECT 
    1,
    NULL::BIGINT,
    'Thomas Petit',
    'thomas.petit@example.com',
    5,
    'Agence sérieuse et compétente. Nous avons acheté notre appartement grâce à eux. Le suivi est excellent et les conseils sont précieux.',
    'APPROVED',
    true,
    15,
    'SALE',
    true,
    NOW() - INTERVAL '15 days',
    NOW() - INTERVAL '15 days'
UNION ALL SELECT 
    1,
    NULL::BIGINT,
    'Camille Rousseau',
    'camille.rousseau@example.com',
    4,
    'Très satisfaite de mes interactions avec cette agence. L''équipe est professionnelle et les biens proposés correspondent à mes attentes.',
    'APPROVED',
    false,
    3,
    'RENT',
    true,
    NOW() - INTERVAL '10 days',
    NOW() - INTERVAL '10 days'
ON CONFLICT DO NOTHING;

-- Avis pour Real Estate Lyon (ID 2)
INSERT INTO organization_reviews (organization_id, user_id, author_name, author_email, rating, comment, status, verified_client, helpful_count, transaction_type, active, created_at, updated_at)
SELECT 
    2,
    NULL::BIGINT,
    'Marc Lefebvre',
    'marc.lefebvre@example.com',
    5,
    'Agence de confiance à Lyon. Nous avons trouvé notre maison rapidement grâce à leur expertise du marché local. Service irréprochable !',
    'APPROVED',
    true,
    10,
    'SALE',
    true,
    NOW() - INTERVAL '28 days',
    NOW() - INTERVAL '28 days'
UNION ALL SELECT 
    2,
    NULL::BIGINT,
    'Isabelle Moreau',
    'isabelle.moreau@example.com',
    4,
    'Bonne agence avec une bonne connaissance du marché lyonnais. Les visites sont bien organisées et les conseils sont pertinents.',
    'APPROVED',
    true,
    6,
    'SALE',
    true,
    NOW() - INTERVAL '22 days',
    NOW() - INTERVAL '22 days'
UNION ALL SELECT 
    2,
    NULL::BIGINT,
    'Nicolas Girard',
    'nicolas.girard@example.com',
    5,
    'Excellente expérience ! L''équipe est très professionnelle et nous a aidés à trouver exactement ce que nous cherchions. Je recommande !',
    'APPROVED',
    true,
    9,
    'RENT',
    true,
    NOW() - INTERVAL '18 days',
    NOW() - INTERVAL '18 days'
ON CONFLICT DO NOTHING;

-- Avis pour Marseille Property (ID 3)
INSERT INTO organization_reviews (organization_id, user_id, author_name, author_email, rating, comment, status, verified_client, helpful_count, transaction_type, active, created_at, updated_at)
SELECT 
    3,
    NULL::BIGINT,
    'Laurence Blanc',
    'laurence.blanc@example.com',
    4,
    'Agence sérieuse à Marseille. Bon suivi et bonne communication. Nous avons trouvé notre appartement sans trop de difficultés.',
    'APPROVED',
    true,
    7,
    'RENT',
    true,
    NOW() - INTERVAL '24 days',
    NOW() - INTERVAL '24 days'
UNION ALL SELECT 
    3,
    NULL::BIGINT,
    'Antoine Garcia',
    'antoine.garcia@example.com',
    5,
    'Très satisfait de cette agence ! L''équipe est dynamique et connaît bien le marché marseillais. Service au top !',
    'APPROVED',
    true,
    11,
    'SALE',
    true,
    NOW() - INTERVAL '19 days',
    NOW() - INTERVAL '19 days'
ON CONFLICT DO NOTHING;
CREATE INDEX IF NOT EXISTS idx_review_property_status ON reviews(property_id, status);
CREATE INDEX IF NOT EXISTS idx_review_property_active ON reviews(property_id, active);

-- Table price_history
CREATE TABLE IF NOT EXISTS price_history (
    id BIGSERIAL PRIMARY KEY,
    property_id BIGINT NOT NULL,
    price NUMERIC(15, 2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'EUR',
    change_date TIMESTAMP NOT NULL,
    change_reason VARCHAR(500),
    created_by BIGINT,
    source VARCHAR(50) DEFAULT 'MANUAL',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_price_history_property FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_price_history_property ON price_history(property_id);
CREATE INDEX IF NOT EXISTS idx_price_history_date ON price_history(change_date);
CREATE INDEX IF NOT EXISTS idx_price_history_property_date ON price_history(property_id, change_date);

-- Table price_alerts
CREATE TABLE IF NOT EXISTS price_alerts (
    id BIGSERIAL PRIMARY KEY,
    property_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    alert_type VARCHAR(50) NOT NULL,
    target_price NUMERIC(15, 2),
    percentage_threshold DOUBLE PRECISION,
    is_percentage BOOLEAN NOT NULL DEFAULT false,
    notified BOOLEAN NOT NULL DEFAULT false,
    notified_at TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT true,
    email_notification BOOLEAN NOT NULL DEFAULT true,
    in_app_notification BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_price_alert_property FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_price_alert_property ON price_alerts(property_id);
CREATE INDEX IF NOT EXISTS idx_price_alert_user ON price_alerts(user_id);
CREATE INDEX IF NOT EXISTS idx_price_alert_active ON price_alerts(active);
CREATE INDEX IF NOT EXISTS idx_price_alert_property_user ON price_alerts(property_id, user_id);

-- Table visit_appointments
CREATE TABLE IF NOT EXISTS visit_appointments (
    id BIGSERIAL PRIMARY KEY,
    property_id BIGINT NOT NULL,
    user_id BIGINT,
    agent_id BIGINT NOT NULL,
    visitor_name VARCHAR(255),
    visitor_email VARCHAR(255),
    visitor_phone VARCHAR(20),
    appointment_date TIMESTAMP NOT NULL,
    duration_minutes INTEGER DEFAULT 60,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    notes TEXT,
    agent_notes TEXT,
    reminder_sent BOOLEAN DEFAULT false,
    reminder_sent_at TIMESTAMP,
    confirmed_at TIMESTAMP,
    cancelled_at TIMESTAMP,
    cancellation_reason VARCHAR(500),
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_visit_property FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_visit_property ON visit_appointments(property_id);
CREATE INDEX IF NOT EXISTS idx_visit_user ON visit_appointments(user_id);
CREATE INDEX IF NOT EXISTS idx_visit_agent ON visit_appointments(agent_id);
CREATE INDEX IF NOT EXISTS idx_visit_date ON visit_appointments(appointment_date);
CREATE INDEX IF NOT EXISTS idx_visit_status ON visit_appointments(status);

-- Table dvf_transactions (Demandes de Valeurs Foncières)
CREATE TABLE IF NOT EXISTS dvf_transactions (
    id BIGSERIAL PRIMARY KEY,
    mutation_date DATE NOT NULL,
    nature_mutation VARCHAR(50),
    valeur_fonciere NUMERIC(15, 2),
    numero_disposition INTEGER,
    lot1_numero VARCHAR(50),
    lot1_surface_carrez NUMERIC(10, 2),
    lot2_numero VARCHAR(50),
    lot2_surface_carrez NUMERIC(10, 2),
    lot3_numero VARCHAR(50),
    lot3_surface_carrez NUMERIC(10, 2),
    lot4_numero VARCHAR(50),
    lot4_surface_carrez NUMERIC(10, 2),
    lot5_numero VARCHAR(50),
    lot5_surface_carrez NUMERIC(10, 2),
    nombre_lots INTEGER,
    code_type_local INTEGER,
    type_local VARCHAR(50),
    surface_reelle_bati NUMERIC(10, 2),
    nombre_pieces_principales INTEGER,
    code_nature_culture VARCHAR(10),
    nature_culture VARCHAR(100),
    code_nature_culture_speciale VARCHAR(10),
    nature_culture_speciale VARCHAR(100),
    surface_terrain NUMERIC(10, 2),
    longitude NUMERIC(10, 7),
    latitude NUMERIC(10, 7),
    code_voie VARCHAR(10),
    type_voie VARCHAR(50),
    voie VARCHAR(255),
    code_postal VARCHAR(10),
    commune VARCHAR(255),
    commune_code VARCHAR(10),
    code_departement VARCHAR(5),
    ancien_code_commune VARCHAR(10),
    ancien_nom_commune VARCHAR(255),
    id_parcelle VARCHAR(50),
    ancien_id_parcelle VARCHAR(50),
    numero_volume VARCHAR(10),
    lot VARCHAR(10),
    prix_metre_carre NUMERIC(10, 2),
    millesime VARCHAR(10),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_dvf_mutation_date ON dvf_transactions(mutation_date);
CREATE INDEX IF NOT EXISTS idx_dvf_commune ON dvf_transactions(commune_code);
CREATE INDEX IF NOT EXISTS idx_dvf_type_local ON dvf_transactions(type_local);
CREATE INDEX IF NOT EXISTS idx_dvf_surface ON dvf_transactions(surface_reelle_bati);
CREATE INDEX IF NOT EXISTS idx_dvf_prix ON dvf_transactions(valeur_fonciere);
CREATE INDEX IF NOT EXISTS idx_dvf_location ON dvf_transactions(latitude, longitude);
CREATE INDEX IF NOT EXISTS idx_dvf_code_postal ON dvf_transactions(code_postal);
CREATE INDEX IF NOT EXISTS idx_dvf_millesime ON dvf_transactions(millesime);

-- Table dvf_import_history
CREATE TABLE IF NOT EXISTS dvf_import_history (
    id BIGSERIAL PRIMARY KEY,
    year VARCHAR(10) NOT NULL,
    department VARCHAR(10) NOT NULL,
    status VARCHAR(50) NOT NULL,
    transaction_count BIGINT,
    error_message TEXT,
    started_by BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_dvf_import_year_dept ON dvf_import_history(year, department);
CREATE INDEX IF NOT EXISTS idx_dvf_import_status ON dvf_import_history(status);
CREATE INDEX IF NOT EXISTS idx_dvf_import_created ON dvf_import_history(created_at);

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
    -- User permissions
    ('USER_READ', 'user', 'READ', 'Read user information', NOW(), NOW()),
    ('USER_WRITE', 'user', 'WRITE', 'Create and update users', NOW(), NOW()),
    ('USER_DELETE', 'user', 'DELETE', 'Delete users', NOW(), NOW()),
    -- Organization permissions
    ('ORGANIZATION_READ', 'organization', 'READ', 'Read organization information', NOW(), NOW()),
    ('ORGANIZATION_WRITE', 'organization', 'WRITE', 'Create and update organizations', NOW(), NOW()),
    ('ORGANIZATION_DELETE', 'organization', 'DELETE', 'Delete organizations', NOW(), NOW()),
    -- Property permissions
    ('PROPERTY_READ', 'property', 'READ', 'Read property information', NOW(), NOW()),
    ('PROPERTY_WRITE', 'property', 'WRITE', 'Create and update properties', NOW(), NOW()),
    ('PROPERTY_DELETE', 'property', 'DELETE', 'Delete properties', NOW(), NOW()),
    -- Document permissions
    ('DOCUMENT_READ', 'document', 'READ', 'Read document information', NOW(), NOW()),
    ('DOCUMENT_WRITE', 'document', 'WRITE', 'Create and update documents', NOW(), NOW()),
    ('DOCUMENT_DELETE', 'document', 'DELETE', 'Delete documents', NOW(), NOW()),
    -- Workflow permissions
    ('WORKFLOW_READ', 'workflow', 'READ', 'Read workflow information', NOW(), NOW()),
    ('WORKFLOW_WRITE', 'workflow', 'WRITE', 'Create and update workflows', NOW(), NOW()),
    ('WORKFLOW_DELETE', 'workflow', 'DELETE', 'Delete workflows', NOW(), NOW()),
    ('WORKFLOW_APPROVE', 'workflow', 'APPROVE', 'Approve workflows', NOW(), NOW()),
    -- Billing permissions
    ('BILLING_READ', 'billing', 'READ', 'Read billing information', NOW(), NOW()),
    ('BILLING_WRITE', 'billing', 'WRITE', 'Create and update billing', NOW(), NOW()),
    ('BILLING_MANAGE', 'billing', 'MANAGE', 'Manage billing and plans', NOW(), NOW()),
    -- Audit permissions
    ('AUDIT_READ', 'audit', 'READ', 'Read audit logs', NOW(), NOW()),
    ('AUDIT_MANAGE', 'audit', 'MANAGE', 'Manage audit logs', NOW(), NOW()),
    -- Notification permissions
    ('NOTIFICATION_READ', 'notification', 'READ', 'Read notifications', NOW(), NOW()),
    ('NOTIFICATION_WRITE', 'notification', 'WRITE', 'Create and send notifications', NOW(), NOW()),
    -- Contact permissions
    ('CONTACT_READ', 'contact', 'READ', 'Read contact messages', NOW(), NOW()),
    ('CONTACT_WRITE', 'contact', 'WRITE', 'Create and update contact messages', NOW(), NOW()),
    ('CONTACT_DELETE', 'contact', 'DELETE', 'Delete contact messages', NOW(), NOW()),
    -- Role & Permission permissions
    ('ROLE_READ', 'role', 'READ', 'Read roles and permissions', NOW(), NOW()),
    ('ROLE_WRITE', 'role', 'WRITE', 'Create and update roles', NOW(), NOW()),
    ('ROLE_DELETE', 'role', 'DELETE', 'Delete roles', NOW(), NOW()),
    -- Resource permissions
    ('RESOURCE_READ', 'resource', 'READ', 'Read resources', NOW(), NOW()),
    ('RESOURCE_WRITE', 'resource', 'WRITE', 'Create and update resources', NOW(), NOW()),
    ('RESOURCE_DELETE', 'resource', 'DELETE', 'Delete resources', NOW(), NOW()),
    -- System permissions
    ('SYSTEM_MANAGE', 'system', 'MANAGE', 'Manage system-wide settings', NOW(), NOW()),
    ('FULL_ACCESS', '*', '*', 'Full access to all resources and actions', NOW(), NOW())
ON CONFLICT (name) DO NOTHING;

-- =====================================================
-- 2. CRÉER LES RÔLES
-- =====================================================

INSERT INTO roles (name, description, created_at, updated_at)
VALUES 
    ('SUPER_ADMIN', 'Super Administrator with full access to all resources across all organizations (SaaS level)', NOW(), NOW()),
    ('ADMIN', 'Administrator with full access (SaaS level - for platform administrators)', NOW(), NOW()),
    ('ORGANIZATION_ADMIN', 'Organization Administrator - Full access to manage organization and all sub-organizations', NOW(), NOW()),
    ('MANAGER', 'Manager with elevated access', NOW(), NOW()),
    ('USER', 'Standard user with basic access', NOW(), NOW()),
    ('INDIVIDUAL', 'Individual user (particulier) - Can manage their own properties and receive messages', NOW(), NOW())
ON CONFLICT (name) DO NOTHING;

-- =====================================================
-- 3. ASSOCIER LES PERMISSIONS AUX RÔLES
-- =====================================================

-- SUPER_ADMIN: Toutes les permissions - Full access to everything
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'SUPER_ADMIN'
ON CONFLICT DO NOTHING;

-- ADMIN (SaaS level): Toutes les permissions (sauf system management)
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'ADMIN'
  AND p.name NOT IN ('SYSTEM_MANAGE', 'FULL_ACCESS')
ON CONFLICT DO NOTHING;

-- ORGANIZATION_ADMIN: Full access to manage organization and sub-organizations
-- This role is for agency administrators who manage their organization and all sub-organizations
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

-- MANAGER: Read et Write (pas Delete)
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'MANAGER' 
  AND p.name IN (
    'USER_READ', 'USER_WRITE',
    'ORGANIZATION_READ', 'ORGANIZATION_WRITE',
    'PROPERTY_READ', 'PROPERTY_WRITE',
    'DOCUMENT_READ', 'DOCUMENT_WRITE',
    'WORKFLOW_READ', 'WORKFLOW_WRITE', 'WORKFLOW_APPROVE',
    'NOTIFICATION_READ', 'NOTIFICATION_WRITE',
    'CONTACT_READ', 'CONTACT_WRITE',
    'RESOURCE_READ', 'RESOURCE_WRITE'
  )
ON CONFLICT DO NOTHING;

-- USER: Read seulement
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'USER' 
  AND p.name IN (
    'USER_READ',
    'ORGANIZATION_READ',
    'PROPERTY_READ',
    'DOCUMENT_READ',
    'WORKFLOW_READ',
    'NOTIFICATION_READ',
    'CONTACT_READ',
    'RESOURCE_READ'
  )
ON CONFLICT DO NOTHING;

-- INDIVIDUAL (Particulier): Permissions pour gérer ses propres propriétés et messages
-- Peut créer, modifier et supprimer ses propres propriétés
-- Peut lire et répondre aux messages reçus pour ses propriétés
-- Peut gérer son propre profil
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'INDIVIDUAL' 
  AND p.name IN (
    'USER_READ', 'USER_WRITE',  -- Lire et modifier son propre profil
    'PROPERTY_READ', 'PROPERTY_WRITE', 'PROPERTY_DELETE',  -- Gérer ses propres propriétés
    'CONTACT_READ', 'CONTACT_WRITE',  -- Lire et répondre aux messages
    'NOTIFICATION_READ',  -- Lire ses notifications
    'DOCUMENT_READ', 'DOCUMENT_WRITE'  -- Gérer les documents de ses propriétés
  )
ON CONFLICT DO NOTHING;

-- =====================================================
-- 4. CRÉER LES UTILISATEURS SAAS PRINCIPAUX
-- =====================================================

-- Note: Les mots de passe sont encodés avec BCrypt
-- Hash BCrypt pour "admin123": $2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi
-- Hash BCrypt pour "superadmin123": $2a$10$rKqJ8vX5YzQ3mNpL2wHx9eBcDfGhIjKlMnOpQrStUvWxYzAbCdEf
-- Pour générer un nouveau hash: https://bcrypt-generator.com/ ou utiliser BCryptPasswordEncoder

-- Créer le SUPER ADMIN (Super Administrator avec accès total)
INSERT INTO users (email, password, first_name, last_name, enabled, account_non_expired, account_non_locked, credentials_non_expired, avatar_url, language, timezone, notification_preferences, created_at, updated_at)
VALUES ('superadmin@viridial.com', '$2a$10$rKqJ8vX5YzQ3mNpL2wHx9eBcDfGhIjKlMnOpQrStUvWxYzAbCdEf', 'Super', 'Admin', true, true, true, true, NULL, 'fr', 'Europe/Paris', '{"email": true, "push": true, "sms": false, "marketing": false}', NOW(), NOW())
ON CONFLICT (email) DO UPDATE 
SET password = EXCLUDED.password,
    first_name = EXCLUDED.first_name,
    last_name = EXCLUDED.last_name,
    enabled = EXCLUDED.enabled,
    avatar_url = EXCLUDED.avatar_url,
    language = EXCLUDED.language,
    timezone = EXCLUDED.timezone,
    notification_preferences = EXCLUDED.notification_preferences,
    updated_at = NOW();

-- Assigner le rôle SUPER_ADMIN au super admin
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email = 'superadmin@viridial.com' AND r.name = 'SUPER_ADMIN'
ON CONFLICT DO NOTHING;

-- Créer l'ADMIN SAAS (Administrator avec accès complet sauf système)
INSERT INTO users (email, password, first_name, last_name, enabled, account_non_expired, account_non_locked, credentials_non_expired, avatar_url, language, timezone, notification_preferences, created_at, updated_at)
VALUES ('admin@viridial.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Admin', 'SaaS', true, true, true, true, NULL, 'fr', 'Europe/Paris', '{"email": true, "push": true, "sms": false, "marketing": false}', NOW(), NOW())
ON CONFLICT (email) DO UPDATE 
SET password = EXCLUDED.password,
    first_name = EXCLUDED.first_name,
    last_name = EXCLUDED.last_name,
    enabled = EXCLUDED.enabled,
    avatar_url = EXCLUDED.avatar_url,
    language = EXCLUDED.language,
    timezone = EXCLUDED.timezone,
    notification_preferences = EXCLUDED.notification_preferences,
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

INSERT INTO organizations (name, description, domain, active, default_office_hours, created_at, updated_at)
VALUES 
    ('Immobilier Paris', 'Agence immobilière spécialisée dans le marché parisien', 'paris-immobilier.fr', true, 
     '{"monday": "9:00-18:00", "tuesday": "9:00-18:00", "wednesday": "9:00-18:00", "thursday": "9:00-18:00", "friday": "9:00-18:00", "saturday": "10:00-16:00", "sunday": "closed"}', 
     NOW(), NOW()),
    ('Real Estate Lyon', 'Agence immobilière à Lyon et région', 'lyon-realestate.fr', true,
     '{"monday": "9:00-18:00", "tuesday": "9:00-18:00", "wednesday": "9:00-18:00", "thursday": "9:00-18:00", "friday": "9:00-18:00", "saturday": "10:00-16:00", "sunday": "closed"}',
     NOW(), NOW()),
    ('Property Marseille', 'Agence immobilière sur la Côte d''Azur', 'marseille-property.fr', true,
     '{"monday": "9:00-18:00", "tuesday": "9:00-18:00", "wednesday": "9:00-18:00", "thursday": "9:00-18:00", "friday": "9:00-18:00", "saturday": "10:00-16:00", "sunday": "closed"}',
     NOW(), NOW()),
    ('Bordeaux Immobilier', 'Agence immobilière à Bordeaux', 'bordeaux-immobilier.fr', true,
     '{"monday": "9:00-18:00", "tuesday": "9:00-18:00", "wednesday": "9:00-18:00", "thursday": "9:00-18:00", "friday": "9:00-18:00", "saturday": "10:00-16:00", "sunday": "closed"}',
     NOW(), NOW()),
    ('Nice Properties', 'Agence immobilière à Nice', 'nice-properties.fr', true,
     '{"monday": "9:00-18:00", "tuesday": "9:00-18:00", "wednesday": "9:00-18:00", "thursday": "9:00-18:00", "friday": "9:00-18:00", "saturday": "10:00-16:00", "sunday": "closed"}',
     NOW(), NOW())
ON CONFLICT (name) DO UPDATE
SET default_office_hours = EXCLUDED.default_office_hours,
    updated_at = NOW();

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
INSERT INTO users (email, password, first_name, last_name, enabled, account_non_expired, account_non_locked, credentials_non_expired, avatar_url, language, timezone, notification_preferences, created_at, updated_at)
VALUES 
    ('directeur@paris-immobilier.fr', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Jean', 'Dupont', true, true, true, true, NULL, 'fr', 'Europe/Paris', '{"email": true, "push": true, "sms": false, "marketing": true}', NOW(), NOW()),
    ('manager@paris-immobilier.fr', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Marie', 'Martin', true, true, true, true, NULL, 'fr', 'Europe/Paris', '{"email": true, "push": true, "sms": false, "marketing": false}', NOW(), NOW()),
    ('agent1@paris-immobilier.fr', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Pierre', 'Bernard', true, true, true, true, NULL, 'fr', 'Europe/Paris', '{"email": true, "push": true, "sms": false, "marketing": false}', NOW(), NOW()),
    ('agent2@paris-immobilier.fr', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Sophie', 'Dubois', true, true, true, true, NULL, 'fr', 'Europe/Paris', '{"email": true, "push": false, "sms": false, "marketing": false}', NOW(), NOW()),
    ('agent3@paris-immobilier.fr', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Lucas', 'Moreau', true, true, true, true, NULL, 'fr', 'Europe/Paris', '{"email": true, "push": true, "sms": false, "marketing": false}', NOW(), NOW())
ON CONFLICT (email) DO NOTHING;

-- Agence 2: Real Estate Lyon
INSERT INTO users (email, password, first_name, last_name, enabled, account_non_expired, account_non_locked, credentials_non_expired, avatar_url, language, timezone, notification_preferences, created_at, updated_at)
VALUES 
    ('directeur@lyon-realestate.fr', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Claire', 'Laurent', true, true, true, true, NULL, 'fr', 'Europe/Paris', '{"email": true, "push": true, "sms": false, "marketing": true}', NOW(), NOW()),
    ('manager@lyon-realestate.fr', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Thomas', 'Simon', true, true, true, true, NULL, 'fr', 'Europe/Paris', '{"email": true, "push": true, "sms": false, "marketing": false}', NOW(), NOW()),
    ('agent1@lyon-realestate.fr', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Emma', 'Michel', true, true, true, true, NULL, 'fr', 'Europe/Paris', '{"email": true, "push": true, "sms": false, "marketing": false}', NOW(), NOW()),
    ('agent2@lyon-realestate.fr', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Hugo', 'Garcia', true, true, true, true, NULL, 'fr', 'Europe/Paris', '{"email": true, "push": false, "sms": false, "marketing": false}', NOW(), NOW())
ON CONFLICT (email) DO NOTHING;

-- Agence 3: Property Marseille
INSERT INTO users (email, password, first_name, last_name, enabled, account_non_expired, account_non_locked, credentials_non_expired, avatar_url, language, timezone, notification_preferences, created_at, updated_at)
VALUES 
    ('directeur@marseille-property.fr', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Nathalie', 'David', true, true, true, true, NULL, 'fr', 'Europe/Paris', '{"email": true, "push": true, "sms": false, "marketing": true}', NOW(), NOW()),
    ('manager@marseille-property.fr', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Antoine', 'Bertrand', true, true, true, true, NULL, 'fr', 'Europe/Paris', '{"email": true, "push": true, "sms": false, "marketing": false}', NOW(), NOW()),
    ('agent1@marseille-property.fr', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Julie', 'Roux', true, true, true, true, NULL, 'fr', 'Europe/Paris', '{"email": true, "push": true, "sms": false, "marketing": false}', NOW(), NOW()),
    ('agent2@marseille-property.fr', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Maxime', 'Vincent', true, true, true, true, NULL, 'fr', 'Europe/Paris', '{"email": true, "push": false, "sms": false, "marketing": false}', NOW(), NOW()),
    ('agent3@marseille-property.fr', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Camille', 'Fournier', true, true, true, true, NULL, 'fr', 'Europe/Paris', '{"email": true, "push": true, "sms": false, "marketing": false}', NOW(), NOW())
ON CONFLICT (email) DO NOTHING;

-- Agence 4: Bordeaux Immobilier
INSERT INTO users (email, password, first_name, last_name, enabled, account_non_expired, account_non_locked, credentials_non_expired, avatar_url, language, timezone, notification_preferences, created_at, updated_at)
VALUES 
    ('directeur@bordeaux-immobilier.fr', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Isabelle', 'Girard', true, true, true, true, NULL, 'fr', 'Europe/Paris', '{"email": true, "push": true, "sms": false, "marketing": true}', NOW(), NOW()),
    ('manager@bordeaux-immobilier.fr', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Julien', 'Bonnet', true, true, true, true, NULL, 'fr', 'Europe/Paris', '{"email": true, "push": true, "sms": false, "marketing": false}', NOW(), NOW()),
    ('agent1@bordeaux-immobilier.fr', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Laura', 'Dupuis', true, true, true, true, NULL, 'fr', 'Europe/Paris', '{"email": true, "push": true, "sms": false, "marketing": false}', NOW(), NOW())
ON CONFLICT (email) DO NOTHING;

-- Agence 5: Nice Properties
INSERT INTO users (email, password, first_name, last_name, enabled, account_non_expired, account_non_locked, credentials_non_expired, avatar_url, language, timezone, notification_preferences, created_at, updated_at)
VALUES 
    ('directeur@nice-properties.fr', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Philippe', 'Lambert', true, true, true, true, NULL, 'fr', 'Europe/Paris', '{"email": true, "push": true, "sms": false, "marketing": true}', NOW(), NOW()),
    ('agent1@nice-properties.fr', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Amélie', 'Fontaine', true, true, true, true, NULL, 'fr', 'Europe/Paris', '{"email": true, "push": true, "sms": false, "marketing": false}', NOW(), NOW()),
    ('agent2@nice-properties.fr', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Nicolas', 'Rousseau', true, true, true, true, NULL, 'fr', 'Europe/Paris', '{"email": true, "push": false, "sms": false, "marketing": false}', NOW(), NOW())
ON CONFLICT (email) DO NOTHING;

-- Freelances (sans organisation)
INSERT INTO users (email, password, first_name, last_name, enabled, account_non_expired, account_non_locked, credentials_non_expired, avatar_url, language, timezone, notification_preferences, created_at, updated_at)
VALUES 
    ('freelance1@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Sarah', 'Lefebvre', true, true, true, true, NULL, 'fr', 'Europe/Paris', '{"email": true, "push": true, "sms": false, "marketing": false}', NOW(), NOW()),
    ('freelance2@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Olivier', 'Blanc', true, true, true, true, NULL, 'fr', 'Europe/Paris', '{"email": true, "push": false, "sms": false, "marketing": false}', NOW(), NOW()),
    ('freelance3@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Céline', 'Garnier', true, true, true, true, NULL, 'fr', 'Europe/Paris', '{"email": true, "push": true, "sms": false, "marketing": false}', NOW(), NOW())
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
INSERT INTO properties (reference, title, description, type, status, transaction_type, slug, meta_title, meta_description, meta_keywords, og_image, price, currency, surface, rooms, bedrooms, bathrooms, full_bathrooms, appliances_included, laundry_location, total_structure_area, total_interior_livable_area, virtual_tour_url, parking_features, has_garage, accessibility_features, patio_porch, exterior_features, special_conditions, home_type, property_subtype, condition, year_built, subdivision, has_hoa, hoa_amenities, hoa_services, hoa_fee, hoa_fee_frequency, region, price_per_square_foot, date_on_market, address, city, postal_code, country, latitude, longitude, organization_id, team_id, assigned_user_id, created_by, active, features, pet_friendly, special_offer, office_hours, neighborhood, walk_score, transit_score, bike_score, building_name, flooring, unit_features, building_amenities, available_units, pet_policy, parking_policy, created_at, updated_at)
VALUES 
    ('PROP-PARIS-001', 'Appartement luxueux à Paris', 'Magnifique appartement situé dans le cœur de Paris. Idéal pour une famille.', 'APARTMENT', 'PUBLISHED', 'SALE', 'appartement-paris-3-pieces-luxueux-1',
    'Appartement luxueux 3 pièces à Paris - 450 000€ | Immobilier Paris',
    'Appartement luxueux de 85.5 m² avec 3 chambres dans le Marais, Paris. Prix: 450 000€. Idéal pour une famille. Parking, ascenseur, balcon.',
    'appartement, paris, marais, 3 chambres, luxe, vente, immobilier, 75001',
    NULL, 450000.00, 'EUR', 85.5, 4, 3, 2, 2, '["Dishwasher", "Refrigerator", "Oven", "Microwave"]', 'Inside', 85.5, 85.5, 'https://virtualtour.example.com/paris-001', '["Garage", "Street"]', true, '["Elevator Access"]', 'Balcony', '["Balcony", "Courtyard"]', 'Resale', 'Condo', 'Condominium', 'Good', 2010, 'Le Marais', true, '["Elevator(s)", "Laundry", "Pool"]', '["Maintenance", "Security"]', 250.00, 'monthly', 'Le Marais', 5263.16, '2024-01-15',
     '15 Rue de la République', 'Paris', '75001', 'France', 48.8606, 2.3376,
     (SELECT id FROM organizations WHERE name = 'Immobilier Paris'), NULL,
     (SELECT id FROM users WHERE email = 'agent1@paris-immobilier.fr'),
     (SELECT id FROM users WHERE email = 'directeur@paris-immobilier.fr'), true, '{"parking": true, "elevator": true, "balcony": true, "terrace": false, "garden": false, "swimming_pool": false, "air_conditioning": true, "heating": "central", "energy_class": "C", "floor": 5, "total_floors": 7}',
     false, NULL, '{"monday": "9:00-18:00", "tuesday": "9:00-18:00", "wednesday": "9:00-18:00", "thursday": "9:00-18:00", "friday": "9:00-18:00", "saturday": "10:00-16:00", "sunday": "closed"}', 'Le Marais', 95, 88, 72, 'Résidence Le Marais', '["Hardwood", "Tile"]', '["Air Conditioning", "Heating", "High Ceilings", "Large Windows"]', '["Elevator", "Concierge", "Gym", "Pool", "Parking"]', NULL, '{"dogsAllowed": false, "catsAllowed": true, "maxWeight": 10, "deposit": 500}', 'Parking disponible dans le garage souterrain. Tarif: 150€/mois.', NOW(), NOW()),
    ('PROP-PARIS-002', 'Maison moderne à Paris', 'Belle maison moderne avec jardin. Parfait pour une famille.', 'HOUSE', 'PUBLISHED', 'SALE', 'maison-paris-4-pieces-moderne-2',
    'Maison moderne 4 chambres à Paris - 650 000€ | Immobilier Paris',
    'Maison moderne de 120 m² avec 4 chambres et jardin dans le 8ème arrondissement, Paris. Prix: 650 000€. Parfait pour une famille.',
    'maison, paris, champs-elysees, 4 chambres, jardin, vente, immobilier, 75008',
    NULL, 650000.00, 'EUR', 120.0, 5, 4, 3, 3, '["Dishwasher", "Refrigerator", "Oven", "Microwave", "Washing Machine", "Dryer"]', 'Inside', 120.0, 120.0, 'https://virtualtour.example.com/paris-002', '["Garage", "Driveway"]', true, '[]', 'Patio', '["Terrace", "Garden", "Patio"]', 'Resale', 'House', 'Single Family', 'Good', 2015, 'Champs-Élysées', false, NULL, NULL, NULL, NULL, 'Champs-Élysées', 5416.67, '2024-02-01',
     '42 Avenue des Champs', 'Paris', '75008', 'France', 48.8738, 2.2950,
     (SELECT id FROM organizations WHERE name = 'Immobilier Paris'), NULL,
     (SELECT id FROM users WHERE email = 'agent2@paris-immobilier.fr'),
     (SELECT id FROM users WHERE email = 'directeur@paris-immobilier.fr'), true, '{"parking": true, "elevator": false, "balcony": false, "terrace": true, "garden": true, "swimming_pool": false, "air_conditioning": true, "heating": "gas", "energy_class": "B", "floor": 0, "total_floors": 2}',
     true, 'Réduction de 5% pour achat avant fin du mois', '{"monday": "10:00-19:00", "tuesday": "10:00-19:00", "wednesday": "10:00-19:00", "thursday": "10:00-19:00", "friday": "10:00-19:00", "saturday": "10:00-17:00", "sunday": "closed"}', 'Champs-Élysées', 98, 92, 85, NULL, '["Hardwood", "Carpet"]', '["Fireplace", "Built-in Shelves", "Walk-in Closet"]', '["Garden", "Terrace", "Garage"]', NULL, '{"dogsAllowed": true, "catsAllowed": true, "maxWeight": 25, "deposit": 800}', 'Garage privé inclus. 2 places de parking.', NOW(), NOW()),
    ('PROP-PARIS-003', 'Studio cosy Paris', 'Studio cosy et bien situé, idéal pour étudiant ou jeune actif.', 'APARTMENT', 'PUBLISHED', 'SALE', 'appartement-paris-studio-cosy-3',
    'Studio cosy à Paris - 180 000€ | Immobilier Paris',
    'Studio cosy de 35 m² dans le Quartier Latin, Paris. Prix: 180 000€. Idéal pour étudiant ou jeune actif.',
    'studio, paris, quartier latin, étudiant, jeune actif, vente, immobilier, 75005',
    NULL, 180000.00, 'EUR', 35.0, 1, 0, 1, 1, '["Refrigerator", "Oven"]', 'None', 35.0, 35.0, NULL, '["Street"]', false, '["Elevator Access"]', NULL, '[]', 'Resale', 'Condo', 'Condominium', 'Fair', 1985, 'Quartier Latin', true, '["Elevator(s)", "Laundry"]', '["Maintenance"]', 120.00, 'monthly', 'Quartier Latin', 5142.86, '2024-03-10',
     '8 Boulevard Saint-Michel', 'Paris', '75005', 'France', 48.8506, 2.3442,
     (SELECT id FROM organizations WHERE name = 'Immobilier Paris'), NULL,
     (SELECT id FROM users WHERE email = 'agent3@paris-immobilier.fr'),
     (SELECT id FROM users WHERE email = 'manager@paris-immobilier.fr'), true, '{"parking": false, "elevator": true, "balcony": false, "terrace": false, "garden": false, "swimming_pool": false, "air_conditioning": false, "heating": "electric", "energy_class": "D", "floor": 3, "total_floors": 5}',
     false, NULL, NULL, 'Quartier Latin', 87, 91, 78, NULL, '["Linoleum"]', '["High Ceilings"]', '["Elevator", "Laundry"]', NULL, '{"dogsAllowed": false, "catsAllowed": false}', 'Parking non disponible. Stationnement dans la rue.', NOW(), NOW()),
    ('PROP-PARIS-004', 'Loft design Paris', 'Loft design dans quartier branché. Caractère et modernité.', 'APARTMENT', 'PUBLISHED', 'SALE', 'appartement-paris-2-pieces-loft-design-4',
    'Loft design 2 pièces à Paris - 520 000€ | Immobilier Paris',
    'Loft design de 95 m² avec 2 chambres dans le Marais, Paris. Prix: 520 000€. Caractère et modernité.',
    'loft, paris, marais, 2 chambres, design, vente, immobilier, 75004',
    NULL, 520000.00, 'EUR', 95.0, 3, 2, 2, 2, '["Dishwasher", "Refrigerator", "Oven", "Microwave", "Washing Machine"]', 'Inside', 95.0, 95.0, 'https://virtualtour.example.com/paris-004', '["Street"]', false, '["Elevator Access"]', 'Balcony', '["Balcony", "Exposed Beams"]', 'Resale', 'Condo', 'Loft', 'Good', 2005, 'Le Marais', true, '["Elevator(s)", "Laundry"]', '["Maintenance"]', 200.00, 'monthly', 'Le Marais', 5473.68, '2024-01-20',
     '25 Rue de Rivoli', 'Paris', '75004', 'France', 48.8566, 2.3522,
     (SELECT id FROM organizations WHERE name = 'Immobilier Paris'), NULL,
     (SELECT id FROM users WHERE email = 'agent1@paris-immobilier.fr'),
     (SELECT id FROM users WHERE email = 'directeur@paris-immobilier.fr'), true, '{"parking": false, "elevator": true, "balcony": true, "terrace": false, "garden": false, "swimming_pool": false, "air_conditioning": true, "heating": "central", "energy_class": "B", "floor": 2, "total_floors": 4, "exposed_beams": true, "high_ceiling": true}',
     true, 'Visite virtuelle disponible', '{"monday": "9:00-18:00", "tuesday": "9:00-18:00", "wednesday": "9:00-18:00", "thursday": "9:00-18:00", "friday": "9:00-18:00", "saturday": "10:00-16:00", "sunday": "closed"}', 'Le Marais', 96, 89, 75, 'Loft Le Marais', '["Concrete", "Hardwood"]', '["Exposed Beams", "High Ceilings", "Large Windows", "Open Layout"]', '["Elevator", "Laundry"]', NULL, '{"dogsAllowed": true, "catsAllowed": true, "maxWeight": 15, "deposit": 600}', 'Parking dans la rue uniquement.', NOW(), NOW()),
    ('PROP-PARIS-005', 'Terrain constructible Paris', 'Terrain constructible de 300m², idéal pour projet immobilier.', 'LAND', 'DRAFT', 'SALE', 'terrain-paris-constructible-5',
    'Terrain constructible 300 m² à Paris - 250 000€ | Immobilier Paris',
    'Terrain constructible de 300 m² dans le 16ème arrondissement, Paris. Prix: 250 000€. Idéal pour projet immobilier.',
    'terrain, paris, constructible, 16ème, vente, immobilier, 75016',
    NULL, 250000.00, 'EUR', 300.0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'New Construction', NULL, NULL, NULL, NULL, NULL, false, NULL, NULL, NULL, NULL, 'Montaigne', 833.33, NULL,
     '100 Avenue Montaigne', 'Paris', '75016', 'France', 48.8698, 2.3312,
     (SELECT id FROM organizations WHERE name = 'Immobilier Paris'), NULL,
     (SELECT id FROM users WHERE email = 'agent2@paris-immobilier.fr'),
     (SELECT id FROM users WHERE email = 'directeur@paris-immobilier.fr'), true, '{"buildable": true, "zoning": "residential", "utilities": true, "access_road": true, "slope": "flat", "orientation": "south", "view": "city"}',
     NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW())
ON CONFLICT (reference) DO NOTHING;

-- Propriétés pour Real Estate Lyon (org_id = 2)
INSERT INTO properties (reference, title, description, type, status, transaction_type, slug, meta_title, meta_description, meta_keywords, og_image, price, currency, surface, rooms, bedrooms, bathrooms, full_bathrooms, appliances_included, laundry_location, total_structure_area, total_interior_livable_area, virtual_tour_url, parking_features, has_garage, accessibility_features, patio_porch, exterior_features, special_conditions, home_type, property_subtype, condition, year_built, subdivision, has_hoa, hoa_amenities, hoa_services, hoa_fee, hoa_fee_frequency, region, price_per_square_foot, date_on_market, address, city, postal_code, country, latitude, longitude, organization_id, team_id, assigned_user_id, created_by, active, features, pet_friendly, special_offer, office_hours, neighborhood, walk_score, transit_score, bike_score, building_name, flooring, unit_features, building_amenities, available_units, pet_policy, parking_policy, created_at, updated_at)
VALUES 
    ('PROP-LYON-001', 'Appartement T3 Lyon', 'Appartement lumineux T3 dans le centre de Lyon.', 'APARTMENT', 'PUBLISHED', 'SALE', 'appartement-lyon-2-pieces-t3-6',
    'Appartement T3 à Lyon - 280 000€ | Real Estate Lyon',
    'Appartement lumineux T3 de 65 m² avec 2 chambres dans le centre de Lyon. Prix: 280 000€.',
    'appartement, lyon, t3, 2 chambres, centre-ville, vente, immobilier, 69001',
    NULL, 280000.00, 'EUR', 65.0, 3, 2, 1, 1, '["Dishwasher", "Refrigerator", "Oven"]', 'Inside', 65.0, 65.0, NULL, '["Street"]', false, '["Elevator Access"]', 'Balcony', '["Balcony"]', 'Resale', 'Condo', 'Condominium', 'Good', 2000, 'Centre-ville', true, '["Elevator(s)", "Laundry"]', '["Maintenance"]', 150.00, 'monthly', 'Centre-ville', 4307.69, '2024-02-15',
     '12 Rue de la République', 'Lyon', '69001', 'France', 45.7640, 4.8357,
     (SELECT id FROM organizations WHERE name = 'Real Estate Lyon'), NULL,
     (SELECT id FROM users WHERE email = 'agent1@lyon-realestate.fr'),
     (SELECT id FROM users WHERE email = 'directeur@lyon-realestate.fr'), true, '{"parking": false, "elevator": true, "balcony": true, "terrace": false, "garden": false, "swimming_pool": false, "air_conditioning": false, "heating": "central", "energy_class": "C", "floor": 4, "total_floors": 6}',
     NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
    ('PROP-LYON-002', 'Maison avec jardin Lyon', 'Belle maison avec jardin, quartier calme.', 'HOUSE', 'PUBLISHED', 'SALE', 'maison-lyon-3-pieces-jardin-7',
    'Maison avec jardin à Lyon - 420 000€ | Real Estate Lyon',
    'Belle maison de 110 m² avec 3 chambres et jardin dans le quartier Part-Dieu, Lyon. Prix: 420 000€.',
    'maison, lyon, jardin, 3 chambres, part-dieu, vente, immobilier, 69003',
    NULL, 420000.00, 'EUR', 110.0, 4, 3, 2, 2, '["Dishwasher", "Refrigerator", "Oven", "Microwave", "Washing Machine", "Dryer"]', 'Inside', 110.0, 110.0, NULL, '["Garage", "Driveway"]', true, '[]', 'Patio', '["Terrace", "Garden", "Patio"]', 'Resale', 'House', 'Single Family', 'Good', 2012, 'Part-Dieu', false, NULL, NULL, NULL, NULL, 'Part-Dieu', 3818.18, '2024-03-01',
     '45 Rue du Faubourg', 'Lyon', '69003', 'France', 45.7597, 4.8422,
     (SELECT id FROM organizations WHERE name = 'Real Estate Lyon'), NULL,
     (SELECT id FROM users WHERE email = 'agent2@lyon-realestate.fr'),
     (SELECT id FROM users WHERE email = 'directeur@lyon-realestate.fr'), true, '{"parking": true, "elevator": false, "balcony": false, "terrace": true, "garden": true, "swimming_pool": false, "air_conditioning": false, "heating": "gas", "energy_class": "B", "floor": 0, "total_floors": 2, "garden_size": 80}',
     NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
    ('PROP-LYON-003', 'Local commercial Lyon', 'Local commercial idéal pour commerce ou bureau.', 'COMMERCIAL', 'PUBLISHED', 'SALE', 'commercial-lyon-local-8',
    'Local commercial à Lyon - 350 000€ | Real Estate Lyon',
    'Local commercial de 150 m² dans le centre-ville de Lyon. Prix: 350 000€. Idéal pour commerce ou bureau.',
    'local commercial, lyon, commerce, bureau, centre-ville, vente, immobilier, 69001',
    NULL, 350000.00, 'EUR', 150.0, 2, 0, 1, 1, NULL, NULL, 150.0, 150.0, NULL, '["Garage", "Street"]', true, '["Wheelchair Access", "Elevator Access"]', NULL, '["Storefront"]', 'Resale', 'Commercial', 'Retail', 'Good', 1995, 'Centre-ville', false, NULL, NULL, NULL, NULL, 'Centre-ville', 2333.33, '2024-01-10',
     '78 Avenue de la République', 'Lyon', '69002', 'France', 45.7500, 4.8500,
     (SELECT id FROM organizations WHERE name = 'Real Estate Lyon'), NULL,
     (SELECT id FROM users WHERE email = 'agent1@lyon-realestate.fr'),
     (SELECT id FROM users WHERE email = 'manager@lyon-realestate.fr'), true, '{"parking": true, "elevator": true, "storefront": true, "storage": true, "office_space": true, "heating": "central", "air_conditioning": true, "commercial_license": true, "accessibility": true}',
     NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
    ('PROP-LYON-004', 'Appartement T2 Lyon', 'Appartement T2 rénové, proche transports.', 'APARTMENT', 'SOLD', 'SALE', 'appartement-lyon-1-piece-t2-renove-9',
    'Appartement T2 rénové à Lyon - 195 000€ | Real Estate Lyon',
    'Appartement T2 rénové de 45 m² avec 1 chambre dans le quartier Part-Dieu, Lyon. Prix: 195 000€.',
    'appartement, lyon, t2, 1 chambre, part-dieu, vente, immobilier, 69006',
    NULL, 195000.00, 'EUR', 45.0, 2, 1, 1, 1, '["Refrigerator", "Oven"]', 'None', 45.0, 45.0, NULL, '["Street"]', false, '["Elevator Access"]', NULL, '[]', 'Resale', 'Condo', 'Condominium', 'Good', 1990, 'Part-Dieu', true, '["Elevator(s)", "Laundry"]', '["Maintenance"]', 100.00, 'monthly', 'Part-Dieu', 4333.33, '2023-11-20',
     '30 Rue Garibaldi', 'Lyon', '69006', 'France', 45.7715, 4.8526,
     (SELECT id FROM organizations WHERE name = 'Real Estate Lyon'), NULL,
     (SELECT id FROM users WHERE email = 'agent2@lyon-realestate.fr'),
     (SELECT id FROM users WHERE email = 'directeur@lyon-realestate.fr'), true, '{"parking": false, "elevator": true, "balcony": false, "terrace": false, "garden": false, "swimming_pool": false, "air_conditioning": false, "heating": "electric", "energy_class": "D", "floor": 2, "total_floors": 4, "renovated": 2020}',
     NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW())
ON CONFLICT (reference) DO NOTHING;

-- Propriétés pour Property Marseille (org_id = 3)
INSERT INTO properties (reference, title, description, type, status, transaction_type, slug, meta_title, meta_description, meta_keywords, og_image, price, currency, surface, rooms, bedrooms, bathrooms, full_bathrooms, appliances_included, laundry_location, total_structure_area, total_interior_livable_area, virtual_tour_url, parking_features, has_garage, accessibility_features, patio_porch, exterior_features, special_conditions, home_type, property_subtype, condition, year_built, subdivision, has_hoa, hoa_amenities, hoa_services, hoa_fee, hoa_fee_frequency, region, price_per_square_foot, date_on_market, address, city, postal_code, country, latitude, longitude, organization_id, team_id, assigned_user_id, created_by, active, features, pet_friendly, special_offer, office_hours, neighborhood, walk_score, transit_score, bike_score, building_name, flooring, unit_features, building_amenities, available_units, pet_policy, parking_policy, created_at, updated_at)
VALUES 
    ('PROP-MARSEILLE-001', 'Villa avec piscine Marseille', 'Superbe villa avec piscine, vue mer.', 'HOUSE', 'PUBLISHED', 'SALE', 'maison-marseille-4-pieces-villa-piscine-10',
    'Villa avec piscine à Marseille - 850 000€ | Property Marseille',
    'Superbe villa de 180 m² avec 4 chambres, piscine et vue mer dans le quartier Corniche, Marseille. Prix: 850 000€.',
    'villa, marseille, piscine, 4 chambres, vue mer, corniche, vente, immobilier, 13007',
    NULL, 850000.00, 'EUR', 180.0, 6, 4, 3, 3, '["Dishwasher", "Refrigerator", "Oven", "Microwave", "Washing Machine", "Dryer"]', 'Inside', 180.0, 180.0, 'https://virtualtour.example.com/marseille-001', '["Garage", "Driveway"]', true, '[]', 'Patio', '["Terrace", "Garden", "Patio", "Pool"]', 'Resale', 'House', 'Single Family', 'New', 2018, 'Corniche', false, NULL, NULL, NULL, NULL, 'Corniche', 4722.22, '2024-01-05',
     '15 Corniche Kennedy', 'Marseille', '13007', 'France', 43.2965, 5.3698,
     (SELECT id FROM organizations WHERE name = 'Property Marseille'), NULL,
     (SELECT id FROM users WHERE email = 'agent1@marseille-property.fr'),
     (SELECT id FROM users WHERE email = 'directeur@marseille-property.fr'), true, '{"parking": true, "elevator": false, "balcony": false, "terrace": true, "garden": true, "swimming_pool": true, "air_conditioning": true, "heating": "central", "energy_class": "B", "floor": 0, "total_floors": 2, "garden_size": 200, "pool_size": 40, "sea_view": true}',
     NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
    ('PROP-MARSEILLE-002', 'Appartement vue mer Marseille', 'Appartement avec vue mer, proche plage.', 'APARTMENT', 'PUBLISHED', 'SALE', 'appartement-marseille-2-pieces-vue-mer-11',
    'Appartement vue mer à Marseille - 380 000€ | Property Marseille',
    'Appartement de 70 m² avec 2 chambres et vue mer dans le Vieux-Port, Marseille. Prix: 380 000€. Proche plage.',
    'appartement, marseille, vue mer, 2 chambres, vieux-port, plage, vente, immobilier, 13001',
    NULL, 380000.00, 'EUR', 70.0, 3, 2, 2, 2, '["Dishwasher", "Refrigerator", "Oven", "Microwave"]', 'Inside', 70.0, 70.0, 'https://virtualtour.example.com/marseille-002', '["Street"]', false, '["Elevator Access"]', 'Balcony', '["Balcony"]', 'Resale', 'Condo', 'Condominium', 'Good', 2008, 'Vieux-Port', true, '["Elevator(s)", "Laundry", "Pool"]', '["Maintenance", "Security"]', 180.00, 'monthly', 'Vieux-Port', 5428.57, '2024-02-20',
     '22 Rue de la République', 'Marseille', '13001', 'France', 43.2965, 5.3764,
     (SELECT id FROM organizations WHERE name = 'Property Marseille'), NULL,
     (SELECT id FROM users WHERE email = 'agent2@marseille-property.fr'),
     (SELECT id FROM users WHERE email = 'directeur@marseille-property.fr'), true, '{"parking": false, "elevator": true, "balcony": true, "terrace": false, "garden": false, "swimming_pool": false, "air_conditioning": true, "heating": "central", "energy_class": "C", "floor": 6, "total_floors": 8, "sea_view": true}',
     NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
    ('PROP-MARSEILLE-003', 'Studio Marseille centre', 'Studio bien situé, proche métro.', 'APARTMENT', 'RENTED', 'RENT', 'appartement-marseille-studio-centre-12',
    'Studio à louer à Marseille - 95 000€ | Property Marseille',
    'Studio de 28 m² bien situé dans le centre-ville de Marseille. Prix: 95 000€. Proche métro.',
    'studio, marseille, centre-ville, location, métro, immobilier, 13001',
    NULL, 95000.00, 'EUR', 28.0, 1, 0, 1, 1, '["Refrigerator", "Oven"]', 'None', 28.0, 28.0, NULL, '["Street"]', false, '["Elevator Access"]', NULL, '[]', 'Resale', 'Condo', 'Condominium', 'Fair', 1975, 'Centre-ville', true, '["Elevator(s)", "Laundry"]', '["Maintenance"]', 90.00, 'monthly', 'Centre-ville', 3392.86, '2023-10-15',
     '8 Cours Belsunce', 'Marseille', '13001', 'France', 43.2965, 5.3764,
     (SELECT id FROM organizations WHERE name = 'Property Marseille'), NULL,
     (SELECT id FROM users WHERE email = 'agent3@marseille-property.fr'),
     (SELECT id FROM users WHERE email = 'manager@marseille-property.fr'), true, '{"parking": false, "elevator": true, "balcony": false, "terrace": false, "garden": false, "swimming_pool": false, "air_conditioning": false, "heating": "electric", "energy_class": "E", "floor": 1, "total_floors": 5}',
     NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
    ('PROP-MARSEILLE-004', 'Terrain constructible Marseille', 'Terrain de 500m², vue panoramique.', 'LAND', 'PUBLISHED', 'SALE', 'terrain-marseille-constructible-13',
    'Terrain constructible 500 m² à Marseille - 180 000€ | Property Marseille',
    'Terrain constructible de 500 m² avec vue panoramique dans le quartier Goudes, Marseille. Prix: 180 000€.',
    'terrain, marseille, constructible, goudes, vue panoramique, vente, immobilier, 13008',
    NULL, 180000.00, 'EUR', 500.0, 0, 0, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'New Construction', NULL, NULL, NULL, NULL, 'Goudes', false, NULL, NULL, NULL, NULL, 'Goudes', 360.00, '2024-03-15',
     '50 Chemin des Goudes', 'Marseille', '13008', 'France', 43.2200, 5.3500,
     (SELECT id FROM organizations WHERE name = 'Property Marseille'), NULL,
     (SELECT id FROM users WHERE email = 'agent1@marseille-property.fr'),
     (SELECT id FROM users WHERE email = 'directeur@marseille-property.fr'), true, '{"buildable": true, "zoning": "residential", "utilities": true, "access_road": true, "slope": "moderate", "orientation": "south", "view": "sea", "panoramic_view": true}',
     NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
    ('PROP-MARSEILLE-005', 'Maison de ville Marseille', 'Maison de ville rénovée, charme provençal.', 'HOUSE', 'PUBLISHED', 'SALE', 'maison-marseille-4-pieces-ville-renovee-14',
    'Maison de ville rénovée à Marseille - 520 000€ | Property Marseille',
    'Maison de ville rénovée de 130 m² avec 4 chambres dans le quartier Paradis, Marseille. Prix: 520 000€. Charme provençal.',
    'maison, marseille, ville, 4 chambres, rénovée, provençal, paradis, vente, immobilier, 13006',
    NULL, 520000.00, 'EUR', 130.0, 5, 4, 2, 2, '["Dishwasher", "Refrigerator", "Oven", "Microwave", "Washing Machine", "Dryer"]', 'Inside', 130.0, 130.0, NULL, '["Garage", "Street"]', true, '[]', 'Patio', '["Terrace", "Patio"]', 'Resale', 'House', 'Townhouse', 'Good', 1920, 'Paradis', false, NULL, NULL, NULL, NULL, 'Paradis', 4000.00, '2024-01-25',
     '35 Rue Paradis', 'Marseille', '13006', 'France', 43.2900, 5.3800,
     (SELECT id FROM organizations WHERE name = 'Property Marseille'), NULL,
     (SELECT id FROM users WHERE email = 'agent2@marseille-property.fr'),
     (SELECT id FROM users WHERE email = 'directeur@marseille-property.fr'), true, '{"parking": true, "elevator": false, "balcony": false, "terrace": true, "garden": false, "swimming_pool": false, "air_conditioning": true, "heating": "gas", "energy_class": "C", "floor": 0, "total_floors": 3, "renovated": 2015, "period_features": true}',
     NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
    ('PROP-MARSEILLE-006', 'Appartement T4 Marseille', 'Appartement T4 spacieux, balcon.', 'APARTMENT', 'PUBLISHED', 'SALE', 'appartement-marseille-3-pieces-t4-spacieux-15',
    'Appartement T4 spacieux à Marseille - 320 000€ | Property Marseille',
    'Appartement T4 spacieux de 85 m² avec 3 chambres et balcon dans le quartier Longchamp, Marseille. Prix: 320 000€.',
    'appartement, marseille, t4, 3 chambres, balcon, longchamp, vente, immobilier, 13001',
    NULL, 320000.00, 'EUR', 85.0, 4, 3, 2, 2, '["Dishwasher", "Refrigerator", "Oven"]', 'Inside', 85.0, 85.0, NULL, '["Street"]', false, '["Elevator Access"]', 'Balcony', '["Balcony"]', 'Resale', 'Condo', 'Condominium', 'Good', 1995, 'Longchamp', true, '["Elevator(s)", "Laundry"]', '["Maintenance"]', 140.00, 'monthly', 'Longchamp', 3764.71, '2024-02-10',
     '18 Boulevard Longchamp', 'Marseille', '13001', 'France', 43.2965, 5.3764,
     (SELECT id FROM organizations WHERE name = 'Property Marseille'), NULL,
     (SELECT id FROM users WHERE email = 'agent3@marseille-property.fr'),
     (SELECT id FROM users WHERE email = 'directeur@marseille-property.fr'), true, '{"parking": false, "elevator": true, "balcony": true, "terrace": false, "garden": false, "swimming_pool": false, "air_conditioning": false, "heating": "central", "energy_class": "D", "floor": 3, "total_floors": 6}',
     NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW())
ON CONFLICT (reference) DO NOTHING;

-- Propriétés pour Bordeaux Immobilier (org_id = 4)
INSERT INTO properties (reference, title, description, type, status, transaction_type, slug, meta_title, meta_description, meta_keywords, og_image, price, currency, surface, rooms, bedrooms, bathrooms, full_bathrooms, appliances_included, laundry_location, total_structure_area, total_interior_livable_area, virtual_tour_url, parking_features, has_garage, accessibility_features, patio_porch, exterior_features, special_conditions, home_type, property_subtype, condition, year_built, subdivision, has_hoa, hoa_amenities, hoa_services, hoa_fee, hoa_fee_frequency, region, price_per_square_foot, date_on_market, address, city, postal_code, country, latitude, longitude, organization_id, team_id, assigned_user_id, created_by, active, features, pet_friendly, special_offer, office_hours, neighborhood, walk_score, transit_score, bike_score, building_name, flooring, unit_features, building_amenities, available_units, pet_policy, parking_policy, created_at, updated_at)
VALUES 
    ('PROP-BORDEAUX-001', 'Appartement centre Bordeaux', 'Appartement dans le centre historique.', 'APARTMENT', 'PUBLISHED', 'SALE', 'appartement-bordeaux-2-pieces-centre-16',
    'Appartement centre historique à Bordeaux - 295 000€ | Bordeaux Immobilier',
    'Appartement de 60 m² avec 2 chambres dans le centre historique de Bordeaux. Prix: 295 000€.',
    'appartement, bordeaux, centre historique, 2 chambres, vente, immobilier, 33000',
    NULL, 295000.00, 'EUR', 60.0, 3, 2, 1, 1, '["Refrigerator", "Oven"]', 'None', 60.0, 60.0, NULL, '["Street"]', false, '[]', NULL, '[]', 'Resale', 'Condo', 'Historic', 'Fair', 1850, 'Centre historique', false, NULL, NULL, NULL, NULL, 'Centre historique', 4916.67, '2024-02-05',
     '10 Rue Sainte-Catherine', 'Bordeaux', '33000', 'France', 44.8378, -0.5792,
     (SELECT id FROM organizations WHERE name = 'Bordeaux Immobilier'), NULL,
     (SELECT id FROM users WHERE email = 'agent1@bordeaux-immobilier.fr'),
     (SELECT id FROM users WHERE email = 'directeur@bordeaux-immobilier.fr'), true, '{"parking": false, "elevator": false, "balcony": false, "terrace": false, "garden": false, "swimming_pool": false, "air_conditioning": false, "heating": "central", "energy_class": "D", "floor": 2, "total_floors": 4, "period_features": true, "historic_building": true}',
     NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
    ('PROP-BORDEAUX-002', 'Maison avec terrasse Bordeaux', 'Maison avec grande terrasse, jardin.', 'HOUSE', 'PUBLISHED', 'SALE', 'maison-bordeaux-4-pieces-terrasse-17',
    'Maison avec terrasse à Bordeaux - 480 000€ | Bordeaux Immobilier',
    'Maison de 125 m² avec 4 chambres, grande terrasse et jardin dans le quartier Intendance, Bordeaux. Prix: 480 000€.',
    'maison, bordeaux, terrasse, jardin, 4 chambres, intendance, vente, immobilier, 33000',
    NULL, 480000.00, 'EUR', 125.0, 5, 4, 3, 3, '["Dishwasher", "Refrigerator", "Oven", "Microwave", "Washing Machine", "Dryer"]', 'Inside', 125.0, 125.0, NULL, '["Garage", "Driveway"]', true, '[]', 'Patio', '["Terrace", "Garden", "Patio"]', 'Resale', 'House', 'Single Family', 'Good', 2010, 'Intendance', false, NULL, NULL, NULL, NULL, 'Intendance', 3840.00, '2024-01-30',
     '25 Cours de l''Intendance', 'Bordeaux', '33000', 'France', 44.8400, -0.5800,
     (SELECT id FROM organizations WHERE name = 'Bordeaux Immobilier'), NULL,
     (SELECT id FROM users WHERE email = 'agent1@bordeaux-immobilier.fr'),
     (SELECT id FROM users WHERE email = 'directeur@bordeaux-immobilier.fr'), true, '{"parking": true, "elevator": false, "balcony": false, "terrace": true, "garden": true, "swimming_pool": false, "air_conditioning": true, "heating": "gas", "energy_class": "C", "floor": 0, "total_floors": 2, "garden_size": 150, "terrace_size": 30}',
     NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
    ('PROP-BORDEAUX-003', 'Local commercial Bordeaux', 'Local commercial en centre-ville.', 'COMMERCIAL', 'PUBLISHED', 'SALE', 'commercial-bordeaux-local-18',
    'Local commercial à Bordeaux - 280 000€ | Bordeaux Immobilier',
    'Local commercial de 120 m² en centre-ville de Bordeaux. Prix: 280 000€.',
    'local commercial, bordeaux, centre-ville, commerce, vente, immobilier, 33000',
    NULL, 280000.00, 'EUR', 120.0, 1, 0, 1, 1, NULL, NULL, 120.0, 120.0, NULL, '["Street"]', false, '[]', NULL, '["Storefront"]', 'Resale', 'Commercial', 'Retail', 'Fair', 1900, 'Centre-ville', false, NULL, NULL, NULL, NULL, 'Centre-ville', 2333.33, '2024-03-05',
     '40 Rue des Trois-Conils', 'Bordeaux', '33000', 'France', 44.8378, -0.5792,
     (SELECT id FROM organizations WHERE name = 'Bordeaux Immobilier'), NULL,
     (SELECT id FROM users WHERE email = 'agent1@bordeaux-immobilier.fr'),
     (SELECT id FROM users WHERE email = 'manager@bordeaux-immobilier.fr'), true, '{"parking": false, "elevator": false, "storefront": true, "storage": true, "office_space": false, "heating": "electric", "air_conditioning": false, "commercial_license": true, "accessibility": false, "historic_building": true}',
     NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW())
ON CONFLICT (reference) DO NOTHING;

-- Propriétés pour Nice Properties (org_id = 5)
INSERT INTO properties (reference, title, description, type, status, transaction_type, slug, meta_title, meta_description, meta_keywords, og_image, price, currency, surface, rooms, bedrooms, bathrooms, full_bathrooms, appliances_included, laundry_location, total_structure_area, total_interior_livable_area, virtual_tour_url, parking_features, has_garage, accessibility_features, patio_porch, exterior_features, special_conditions, home_type, property_subtype, condition, year_built, subdivision, has_hoa, hoa_amenities, hoa_services, hoa_fee, hoa_fee_frequency, region, price_per_square_foot, date_on_market, address, city, postal_code, country, latitude, longitude, organization_id, team_id, assigned_user_id, created_by, active, features, pet_friendly, special_offer, office_hours, neighborhood, walk_score, transit_score, bike_score, building_name, flooring, unit_features, building_amenities, available_units, pet_policy, parking_policy, created_at, updated_at)
VALUES 
    ('PROP-NICE-001', 'Appartement vue mer Nice', 'Appartement avec vue mer exceptionnelle.', 'APARTMENT', 'PUBLISHED', 'SALE', 'appartement-nice-2-pieces-vue-mer-19',
    'Appartement vue mer à Nice - 420 000€ | Nice Properties',
    'Appartement de 75 m² avec 2 chambres et vue mer exceptionnelle sur la Promenade des Anglais, Nice. Prix: 420 000€.',
    'appartement, nice, vue mer, 2 chambres, promenade des anglais, vente, immobilier, 06000',
    NULL, 420000.00, 'EUR', 75.0, 3, 2, 2, 2, '["Dishwasher", "Refrigerator", "Oven", "Microwave"]', 'Inside', 75.0, 75.0, 'https://virtualtour.example.com/nice-001', '["Street"]', false, '["Elevator Access"]', 'Balcony', '["Balcony"]', 'Resale', 'Condo', 'Condominium', 'Good', 2015, 'Promenade des Anglais', true, '["Elevator(s)", "Laundry", "Pool"]', '["Maintenance", "Security"]', 220.00, 'monthly', 'Promenade des Anglais', 5600.00, '2024-01-12',
     '12 Promenade des Anglais', 'Nice', '06000', 'France', 43.6959, 7.2554,
     (SELECT id FROM organizations WHERE name = 'Nice Properties'), NULL,
     (SELECT id FROM users WHERE email = 'agent1@nice-properties.fr'),
     (SELECT id FROM users WHERE email = 'directeur@nice-properties.fr'), true, '{"parking": false, "elevator": true, "balcony": true, "terrace": false, "garden": false, "swimming_pool": false, "air_conditioning": true, "heating": "central", "energy_class": "B", "floor": 8, "total_floors": 10, "sea_view": true, "panoramic_view": true}',
     NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
    ('PROP-NICE-002', 'Villa de prestige Nice', 'Villa de prestige avec piscine, vue panoramique.', 'HOUSE', 'PUBLISHED', 'SALE', 'maison-nice-5-pieces-villa-prestige-20',
    'Villa de prestige à Nice - 1 200 000€ | Nice Properties',
    'Villa de prestige de 250 m² avec 5 chambres, piscine et vue panoramique dans le quartier Cimiez, Nice. Prix: 1 200 000€.',
    'villa, nice, prestige, 5 chambres, piscine, cimiez, vente, immobilier, 06000',
    NULL, 1200000.00, 'EUR', 250.0, 7, 5, 4, 4, '["Dishwasher", "Refrigerator", "Oven", "Microwave", "Washing Machine", "Dryer", "Wine Cooler"]', 'Inside', 250.0, 250.0, 'https://virtualtour.example.com/nice-002', '["Garage", "Driveway"]', true, '["Elevator Access"]', 'Patio', '["Terrace", "Garden", "Patio", "Pool"]', 'Resale', 'House', 'Luxury', 'New', 2020, 'Cimiez', false, NULL, NULL, NULL, NULL, 'Cimiez', 4800.00, '2024-01-01',
     '50 Boulevard de Cimiez', 'Nice', '06000', 'France', 43.7100, 7.2700,
     (SELECT id FROM organizations WHERE name = 'Nice Properties'), NULL,
     (SELECT id FROM users WHERE email = 'agent2@nice-properties.fr'),
     (SELECT id FROM users WHERE email = 'directeur@nice-properties.fr'), true, '{"parking": true, "elevator": true, "balcony": false, "terrace": true, "garden": true, "swimming_pool": true, "air_conditioning": true, "heating": "central", "energy_class": "A", "floor": 0, "total_floors": 3, "garden_size": 500, "pool_size": 60, "sea_view": true, "panoramic_view": true, "luxury": true}',
     NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
    ('PROP-NICE-003', 'Studio Nice centre', 'Studio proche plage et commerces.', 'APARTMENT', 'RENTED', 'RENT', 'appartement-nice-studio-centre-21',
    'Studio à louer à Nice - 110 000€ | Nice Properties',
    'Studio de 30 m² proche plage et commerces dans le centre-ville de Nice. Prix: 110 000€.',
    'studio, nice, centre-ville, plage, location, immobilier, 06000',
    NULL, 110000.00, 'EUR', 30.0, 1, 0, 1, 1, '["Refrigerator", "Oven"]', 'None', 30.0, 30.0, NULL, '["Street"]', false, '["Elevator Access"]', NULL, '[]', 'Resale', 'Condo', 'Condominium', 'Fair', 1980, 'Centre-ville', true, '["Elevator(s)", "Laundry"]', '["Maintenance"]', 95.00, 'monthly', 'Centre-ville', 3666.67, '2023-09-10',
     '8 Rue de France', 'Nice', '06000', 'France', 43.6959, 7.2554,
     (SELECT id FROM organizations WHERE name = 'Nice Properties'), NULL,
     (SELECT id FROM users WHERE email = 'agent1@nice-properties.fr'),
     (SELECT id FROM users WHERE email = 'directeur@nice-properties.fr'), true, '{"parking": false, "elevator": true, "balcony": false, "terrace": false, "garden": false, "swimming_pool": false, "air_conditioning": false, "heating": "electric", "energy_class": "E", "floor": 1, "total_floors": 4}',
     NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
    ('PROP-NICE-004', 'Appartement T3 Nice', 'Appartement T3 rénové, proche vieille ville.', 'APARTMENT', 'PUBLISHED', 'SALE', 'appartement-nice-2-pieces-t3-renove-22',
    'Appartement T3 rénové à Nice - 350 000€ | Nice Properties',
    'Appartement T3 rénové de 68 m² avec 2 chambres, proche vieille ville dans le quartier Jean Médecin, Nice. Prix: 350 000€.',
    'appartement, nice, t3, 2 chambres, rénové, vieille ville, jean médecin, vente, immobilier, 06000',
    NULL, 350000.00, 'EUR', 68.0, 3, 2, 1, 1, '["Dishwasher", "Refrigerator", "Oven", "Microwave"]', 'Inside', 68.0, 68.0, NULL, '["Street"]', false, '["Elevator Access"]', 'Balcony', '["Balcony"]', 'Resale', 'Condo', 'Condominium', 'Good', 1995, 'Jean Médecin', true, '["Elevator(s)", "Laundry"]', '["Maintenance"]', 160.00, 'monthly', 'Jean Médecin', 5147.06, '2024-02-28',
     '22 Avenue Jean Médecin', 'Nice', '06000', 'France', 43.7000, 7.2600,
     (SELECT id FROM organizations WHERE name = 'Nice Properties'), NULL,
     (SELECT id FROM users WHERE email = 'agent2@nice-properties.fr'),
     (SELECT id FROM users WHERE email = 'directeur@nice-properties.fr'), true, '{"parking": false, "elevator": true, "balcony": true, "terrace": false, "garden": false, "swimming_pool": false, "air_conditioning": true, "heating": "central", "energy_class": "C", "floor": 3, "total_floors": 5, "renovated": 2021}',
     NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW())
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
-- 11.5. CRÉER DES MESSAGES DE CONTACT
-- =====================================================

-- Messages de contact pour tester la fonctionnalité de comptage des messages non lus
-- Certains messages sont NEW (non lus), d'autres sont READ (lus) ou REPLIED (répondus)

-- Messages pour PROP-PARIS-001 (Appartement luxueux à Paris)
INSERT INTO contact_messages (name, email, phone, subject, message, status, property_id, organization_id, read_at, read_by, replied_at, replied_by, notes, active, created_at, updated_at)
SELECT 
    'Marie Dubois', 'marie.dubois@example.com', '+33612345678', 
    'Demande de visite - Appartement luxueux à Paris', 
    'Bonjour, je suis intéressée par votre appartement situé au 15 Rue de la République. Serait-il possible de planifier une visite ? Merci.',
    'NEW', 
    (SELECT id FROM properties WHERE reference = 'PROP-PARIS-001'),
    (SELECT organization_id FROM properties WHERE reference = 'PROP-PARIS-001'),
    NULL::TIMESTAMP, NULL::BIGINT, NULL::TIMESTAMP, NULL::BIGINT, NULL::TEXT, true, NOW() - INTERVAL '2 days', NOW() - INTERVAL '2 days'
UNION ALL SELECT 
    'Pierre Martin', 'pierre.martin@example.com', '+33623456789',
    'Question sur le prix - Appartement Paris',
    'Bonjour, le prix est-il négociable ? Y a-t-il des charges ?',
    'READ',
    (SELECT id FROM properties WHERE reference = 'PROP-PARIS-001'),
    (SELECT organization_id FROM properties WHERE reference = 'PROP-PARIS-001'),
    NOW() - INTERVAL '1 day', (SELECT id FROM users WHERE email = 'agent1@paris-immobilier.fr'), NULL::TIMESTAMP, NULL::BIGINT, 'Client intéressé, à rappeler', true, NOW() - INTERVAL '3 days', NOW() - INTERVAL '1 day'
UNION ALL SELECT 
    'Sophie Bernard', 'sophie.bernard@example.com', NULL,
    'Demande d''informations complémentaires',
    'Pourriez-vous me fournir plus d''informations sur les charges et les travaux récents ?',
    'NEW',
    (SELECT id FROM properties WHERE reference = 'PROP-PARIS-001'),
    (SELECT organization_id FROM properties WHERE reference = 'PROP-PARIS-001'),
    NULL::TIMESTAMP, NULL::BIGINT, NULL::TIMESTAMP, NULL::BIGINT, NULL::TEXT, true, NOW() - INTERVAL '5 hours', NOW() - INTERVAL '5 hours'
ON CONFLICT DO NOTHING;

-- Messages pour PROP-PARIS-002 (Maison moderne à Paris)
INSERT INTO contact_messages (name, email, phone, subject, message, status, property_id, organization_id, read_at, read_by, replied_at, replied_by, notes, active, created_at, updated_at)
SELECT 
    'Jean Lefebvre', 'jean.lefebvre@example.com', '+33634567890',
    'Visite maison moderne Paris',
    'Bonjour, je souhaiterais visiter cette maison. Quels sont vos horaires disponibles ?',
    'NEW',
    (SELECT id FROM properties WHERE reference = 'PROP-PARIS-002'),
    (SELECT organization_id FROM properties WHERE reference = 'PROP-PARIS-002'),
    NULL::TIMESTAMP, NULL::BIGINT, NULL::TIMESTAMP, NULL::BIGINT, NULL::TEXT, true, NOW() - INTERVAL '1 day', NOW() - INTERVAL '1 day'
UNION ALL SELECT 
    'Claire Moreau', 'claire.moreau@example.com', '+33645678901',
    'Question sur le jardin',
    'Le jardin est-il entretenu ? Quelle est sa superficie exacte ?',
    'REPLIED',
    (SELECT id FROM properties WHERE reference = 'PROP-PARIS-002'),
    (SELECT organization_id FROM properties WHERE reference = 'PROP-PARIS-002'),
    NOW() - INTERVAL '2 days', (SELECT id FROM users WHERE email = 'agent2@paris-immobilier.fr'),
    NOW() - INTERVAL '2 days', (SELECT id FROM users WHERE email = 'agent2@paris-immobilier.fr'),
    'Répondu par email', true, NOW() - INTERVAL '4 days', NOW() - INTERVAL '2 days'
ON CONFLICT DO NOTHING;

-- Messages pour PROP-LYON-001 (Appartement T3 Lyon)
INSERT INTO contact_messages (name, email, phone, subject, message, status, property_id, organization_id, read_at, read_by, replied_at, replied_by, notes, active, created_at, updated_at)
SELECT 
    'Thomas Petit', 'thomas.petit@example.com', '+33412345678',
    'Intéressé par l''appartement Lyon',
    'Bonjour, je suis très intéressé par cet appartement. Pourriez-vous me donner plus de détails ?',
    'NEW',
    (SELECT id FROM properties WHERE reference = 'PROP-LYON-001'),
    (SELECT organization_id FROM properties WHERE reference = 'PROP-LYON-001'),
    NULL::TIMESTAMP, NULL::BIGINT, NULL::TIMESTAMP, NULL::BIGINT, NULL::TEXT, true, NOW() - INTERVAL '6 hours', NOW() - INTERVAL '6 hours'
UNION ALL SELECT 
    'Emma Rousseau', 'emma.rousseau@example.com', NULL,
    'Question parking',
    'Y a-t-il un parking disponible avec cet appartement ?',
    'READ',
    (SELECT id FROM properties WHERE reference = 'PROP-LYON-001'),
    (SELECT organization_id FROM properties WHERE reference = 'PROP-LYON-001'),
    NOW() - INTERVAL '1 day', (SELECT id FROM users WHERE email = 'agent1@lyon-realestate.fr'), NULL::TIMESTAMP, NULL::BIGINT, NULL::TEXT, true, NOW() - INTERVAL '2 days', NOW() - INTERVAL '1 day'
ON CONFLICT DO NOTHING;

-- Messages pour PROP-MARSEILLE-001 (Villa avec piscine Marseille)
INSERT INTO contact_messages (name, email, phone, subject, message, status, property_id, organization_id, read_at, read_by, replied_at, replied_by, notes, active, created_at, updated_at)
SELECT 
    'Marc Durand', 'marc.durand@example.com', '+33423456789',
    'Villa avec piscine - Demande de visite',
    'Cette villa m''intéresse beaucoup. Pourriez-vous organiser une visite cette semaine ?',
    'NEW',
    (SELECT id FROM properties WHERE reference = 'PROP-MARSEILLE-001'),
    (SELECT organization_id FROM properties WHERE reference = 'PROP-MARSEILLE-001'),
    NULL::TIMESTAMP, NULL::BIGINT, NULL::TIMESTAMP, NULL::BIGINT, NULL::TEXT, true, NOW() - INTERVAL '3 hours', NOW() - INTERVAL '3 hours'
UNION ALL SELECT 
    'Lucie Blanc', 'lucie.blanc@example.com', '+33434567890',
    'Question piscine',
    'La piscine est-elle chauffée ? Y a-t-il un système de sécurité ?',
    'NEW',
    (SELECT id FROM properties WHERE reference = 'PROP-MARSEILLE-001'),
    (SELECT organization_id FROM properties WHERE reference = 'PROP-MARSEILLE-001'),
    NULL::TIMESTAMP, NULL::BIGINT, NULL::TIMESTAMP, NULL::BIGINT, NULL::TEXT, true, NOW() - INTERVAL '1 day', NOW() - INTERVAL '1 day'
UNION ALL SELECT 
    'Antoine Girard', 'antoine.girard@example.com', '+33445678901',
    'Informations sur la villa',
    'Pourriez-vous me fournir le DPE et les diagnostics ?',
    'REPLIED',
    (SELECT id FROM properties WHERE reference = 'PROP-MARSEILLE-001'),
    (SELECT organization_id FROM properties WHERE reference = 'PROP-MARSEILLE-001'),
    NOW() - INTERVAL '3 days', (SELECT id FROM users WHERE email = 'agent1@marseille-property.fr'),
    NOW() - INTERVAL '2 days', (SELECT id FROM users WHERE email = 'agent1@marseille-property.fr'),
    'Documents envoyés par email', true, NOW() - INTERVAL '5 days', NOW() - INTERVAL '2 days'
ON CONFLICT DO NOTHING;

-- Messages pour PROP-BORDEAUX-001 (Appartement centre Bordeaux)
INSERT INTO contact_messages (name, email, phone, subject, message, status, property_id, organization_id, read_at, read_by, replied_at, replied_by, notes, active, created_at, updated_at)
SELECT 
    'Camille Lemoine', 'camille.lemoine@example.com', '+33512345678',
    'Appartement centre Bordeaux',
    'Bonjour, cet appartement m''intéresse. Est-il toujours disponible ?',
    'NEW',
    (SELECT id FROM properties WHERE reference = 'PROP-BORDEAUX-001'),
    (SELECT organization_id FROM properties WHERE reference = 'PROP-BORDEAUX-001'),
    NULL::TIMESTAMP, NULL::BIGINT, NULL::TIMESTAMP, NULL::BIGINT, NULL::TEXT, true, NOW() - INTERVAL '4 hours', NOW() - INTERVAL '4 hours'
ON CONFLICT DO NOTHING;

-- Messages pour PROP-NICE-001 (Appartement vue mer Nice)
INSERT INTO contact_messages (name, email, phone, subject, message, status, property_id, organization_id, read_at, read_by, replied_at, replied_by, notes, active, created_at, updated_at)
SELECT 
    'Nicolas Fabre', 'nicolas.fabre@example.com', '+33456789012',
    'Appartement vue mer - Très intéressé',
    'Cet appartement avec vue mer correspond parfaitement à mes critères. Quand puis-je le visiter ?',
    'NEW',
    (SELECT id FROM properties WHERE reference = 'PROP-NICE-001'),
    (SELECT organization_id FROM properties WHERE reference = 'PROP-NICE-001'),
    NULL::TIMESTAMP, NULL::BIGINT, NULL::TIMESTAMP, NULL::BIGINT, NULL::TEXT, true, NOW() - INTERVAL '2 hours', NOW() - INTERVAL '2 hours'
UNION ALL SELECT 
    'Isabelle Lambert', 'isabelle.lambert@example.com', NULL,
    'Question sur les charges',
    'Quel est le montant des charges mensuelles ?',
    'READ',
    (SELECT id FROM properties WHERE reference = 'PROP-NICE-001'),
    (SELECT organization_id FROM properties WHERE reference = 'PROP-NICE-001'),
    NOW() - INTERVAL '1 day', (SELECT id FROM users WHERE email = 'agent1@nice-properties.fr'), NULL::TIMESTAMP, NULL::BIGINT, NULL::TEXT, true, NOW() - INTERVAL '3 days', NOW() - INTERVAL '1 day'
ON CONFLICT DO NOTHING;

-- Message sans propriété associée (message général)
INSERT INTO contact_messages (name, email, phone, subject, message, status, property_id, organization_id, read_at, read_by, replied_at, replied_by, notes, active, created_at, updated_at)
SELECT 
    'Pauline Garnier', 'pauline.garnier@example.com', '+33656789012',
    'Demande générale d''information',
    'Bonjour, je recherche un appartement T3 dans le centre de Paris. Avez-vous des biens disponibles ?',
    'NEW',
    NULL,
    (SELECT id FROM organizations WHERE name = 'Immobilier Paris'),
    NULL::TIMESTAMP, NULL::BIGINT, NULL::TIMESTAMP, NULL::BIGINT, NULL::TEXT, true, NOW() - INTERVAL '1 day', NOW() - INTERVAL '1 day'
ON CONFLICT DO NOTHING;

-- =====================================================
-- 11.7. MESSAGES DE CONTACT D'AGENCES (organization_contact_messages)
-- =====================================================

-- Messages de contact pour les agences (sans propriété associée)
-- Ces messages sont envoyés directement aux agences depuis la page de détail d'agence

-- Messages pour l'agence Immobilier Paris (ID 1)
INSERT INTO organization_contact_messages (name, email, phone, subject, message, status, organization_id, read_at, read_by, replied_at, replied_by, notes, active, created_at, updated_at)
SELECT 
    'Sophie Martin', 'sophie.martin@example.com', '+33612345678',
    'Demande d''information sur vos services',
    'Bonjour, je souhaiterais obtenir des informations sur vos services immobiliers. Avez-vous des biens disponibles dans le 15ème arrondissement ?',
    'NEW',
    (SELECT id FROM organizations WHERE name = 'Immobilier Paris'),
    NULL::TIMESTAMP, NULL::BIGINT, NULL::TIMESTAMP, NULL::BIGINT, NULL::TEXT, true, NOW() - INTERVAL '2 hours', NOW() - INTERVAL '2 hours'
UNION ALL SELECT 
    'Pierre Bernard', 'pierre.bernard@example.com', '+33623456789',
    'Recherche d''appartement T4',
    'Bonjour, je recherche un appartement T4 dans Paris. Pourriez-vous me contacter pour discuter de mes besoins ?',
    'READ',
    (SELECT id FROM organizations WHERE name = 'Immobilier Paris'),
    NOW() - INTERVAL '1 day', (SELECT id FROM users WHERE email = 'manager@paris-immobilier.fr'), NULL::TIMESTAMP, NULL::BIGINT, 'Client intéressé par un T4, budget 500k€', true, NOW() - INTERVAL '3 days', NOW() - INTERVAL '1 day'
UNION ALL SELECT 
    'Marie Dubois', 'marie.dubois@example.com', '+33634567890',
    'Question sur vos honoraires',
    'Quels sont vos honoraires pour la vente d''un bien immobilier ?',
    'REPLIED',
    (SELECT id FROM organizations WHERE name = 'Immobilier Paris'),
    NOW() - INTERVAL '2 days', (SELECT id FROM users WHERE email = 'manager@paris-immobilier.fr'), NOW() - INTERVAL '1 day', (SELECT id FROM users WHERE email = 'manager@paris-immobilier.fr'), 'Répondu par email', true, NOW() - INTERVAL '5 days', NOW() - INTERVAL '1 day'
ON CONFLICT DO NOTHING;

-- Messages pour l'agence Real Estate Lyon (ID 2)
INSERT INTO organization_contact_messages (name, email, phone, subject, message, status, organization_id, read_at, read_by, replied_at, replied_by, notes, active, created_at, updated_at)
SELECT 
    'Thomas Petit', 'thomas.petit@example.com', '+33412345678',
    'Recherche maison avec jardin',
    'Bonjour, je recherche une maison avec jardin dans la région lyonnaise. Avez-vous des biens correspondants ?',
    'NEW',
    (SELECT id FROM organizations WHERE name = 'Real Estate Lyon'),
    NULL::TIMESTAMP, NULL::BIGINT, NULL::TIMESTAMP, NULL::BIGINT, NULL::TEXT, true, NOW() - INTERVAL '5 hours', NOW() - INTERVAL '5 hours'
UNION ALL SELECT 
    'Julie Moreau', 'julie.moreau@example.com', '+33423456789',
    'Estimation de bien immobilier',
    'Je souhaiterais faire estimer mon appartement. Proposez-vous ce service ?',
    'READ',
    (SELECT id FROM organizations WHERE name = 'Real Estate Lyon'),
    NOW() - INTERVAL '4 hours', (SELECT id FROM users WHERE email = 'agent1@lyon-realestate.fr'), NULL::TIMESTAMP, NULL::BIGINT, NULL::TEXT, true, NOW() - INTERVAL '1 day', NOW() - INTERVAL '4 hours'
ON CONFLICT DO NOTHING;

-- Messages pour l'agence Property Marseille (ID 3)
INSERT INTO organization_contact_messages (name, email, phone, subject, message, status, organization_id, read_at, read_by, replied_at, replied_by, notes, active, created_at, updated_at)
SELECT 
    'Marc Durand', 'marc.durand@example.com', '+33434567890',
    'Recherche local commercial',
    'Bonjour, je recherche un local commercial dans le centre de Marseille. Avez-vous des disponibilités ?',
    'NEW',
    (SELECT id FROM organizations WHERE name = 'Property Marseille'),
    NULL::TIMESTAMP, NULL::BIGINT, NULL::TIMESTAMP, NULL::BIGINT, NULL::TEXT, true, NOW() - INTERVAL '1 hour', NOW() - INTERVAL '1 hour'
ON CONFLICT DO NOTHING;

-- Messages pour l'agence Bordeaux Immobilier (ID 4)
INSERT INTO organization_contact_messages (name, email, phone, subject, message, status, organization_id, read_at, read_by, replied_at, replied_by, notes, active, created_at, updated_at)
SELECT 
    'Camille Lemoine', 'camille.lemoine@example.com', '+33512345678',
    'Demande de visite d''agence',
    'Bonjour, je souhaiterais visiter votre agence pour discuter de mes projets immobiliers. Quels sont vos horaires ?',
    'READ',
    (SELECT id FROM organizations WHERE name = 'Bordeaux Immobilier'),
    NOW() - INTERVAL '6 hours', (SELECT id FROM users WHERE email = 'manager@bordeaux-immobilier.fr'), NULL::TIMESTAMP, NULL::BIGINT, 'Appeler pour fixer un rendez-vous', true, NOW() - INTERVAL '2 days', NOW() - INTERVAL '6 hours'
UNION ALL SELECT 
    'Nicolas Fabre', 'nicolas.fabre@example.com', '+33523456789',
    'Question sur vos services de gestion locative',
    'Proposez-vous des services de gestion locative ? Si oui, quels sont vos tarifs ?',
    'REPLIED',
    (SELECT id FROM organizations WHERE name = 'Bordeaux Immobilier'),
    NOW() - INTERVAL '3 days', (SELECT id FROM users WHERE email = 'manager@bordeaux-immobilier.fr'), NOW() - INTERVAL '2 days', (SELECT id FROM users WHERE email = 'manager@bordeaux-immobilier.fr'), 'Tarifs envoyés par email', true, NOW() - INTERVAL '5 days', NOW() - INTERVAL '2 days'
ON CONFLICT DO NOTHING;

-- Messages pour l'agence Nice Properties (ID 5)
INSERT INTO organization_contact_messages (name, email, phone, subject, message, status, organization_id, read_at, read_by, replied_at, replied_by, notes, active, created_at, updated_at)
SELECT 
    'Isabelle Lambert', 'isabelle.lambert@example.com', '+33456789012',
    'Recherche villa avec piscine',
    'Bonjour, je recherche une villa avec piscine sur la Côte d''Azur. Avez-vous des biens dans ce segment ?',
    'NEW',
    (SELECT id FROM organizations WHERE name = 'Nice Properties'),
    NULL::TIMESTAMP, NULL::BIGINT, NULL::TIMESTAMP, NULL::BIGINT, NULL::TEXT, true, NOW() - INTERVAL '3 hours', NOW() - INTERVAL '3 hours'
UNION ALL SELECT 
    'Pauline Garnier', 'pauline.garnier@example.com', '+33467890123',
    'Demande d''information générale',
    'Je souhaiterais en savoir plus sur votre agence et vos services. Pourriez-vous me contacter ?',
    'READ',
    (SELECT id FROM organizations WHERE name = 'Nice Properties'),
    NOW() - INTERVAL '1 day', (SELECT id FROM users WHERE email = 'agent1@nice-properties.fr'), NULL::TIMESTAMP, NULL::BIGINT, NULL::TEXT, true, NOW() - INTERVAL '4 days', NOW() - INTERVAL '1 day'
ON CONFLICT DO NOTHING;

-- =====================================================
-- 11.6. CRÉER DES AVIS (REVIEWS)
-- =====================================================

-- Avis pour PROP-PARIS-001 (Appartement luxueux à Paris)
INSERT INTO reviews (property_id, user_id, author_name, author_email, rating, comment, status, verified_purchase, helpful_count, active, created_at, updated_at)
SELECT 
    (SELECT id FROM properties WHERE reference = 'PROP-PARIS-001'),
    NULL::BIGINT,
    'Marie Dubois',
    'marie.dubois@example.com',
    5,
    'Excellent appartement ! Très bien situé, proche des transports et des commerces. L''agence a été très professionnelle et réactive. Je recommande vivement !',
    'APPROVED',
    true,
    12,
    true,
    NOW() - INTERVAL '30 days',
    NOW() - INTERVAL '30 days'
UNION ALL SELECT 
    (SELECT id FROM properties WHERE reference = 'PROP-PARIS-001'),
    NULL::BIGINT,
    'Pierre Martin',
    'pierre.martin@example.com',
    4,
    'Très bel appartement avec une belle luminosité. Le quartier est agréable et calme. Seul petit bémol : les charges sont un peu élevées.',
    'APPROVED',
    true,
    8,
    true,
    NOW() - INTERVAL '25 days',
    NOW() - INTERVAL '25 days'
UNION ALL SELECT 
    (SELECT id FROM properties WHERE reference = 'PROP-PARIS-001'),
    NULL::BIGINT,
    'Sophie Bernard',
    'sophie.bernard@example.com',
    5,
    'Parfait ! L''appartement correspond exactement à la description. L''équipe de l''agence a été à l''écoute et très professionnelle. Transaction sans problème.',
    'APPROVED',
    true,
    15,
    true,
    NOW() - INTERVAL '20 days',
    NOW() - INTERVAL '20 days'
UNION ALL SELECT 
    (SELECT id FROM properties WHERE reference = 'PROP-PARIS-001'),
    NULL::BIGINT,
    'Jean Dupont',
    'jean.dupont@example.com',
    3,
    'Appartement correct mais un peu bruyant. Le quartier est bien mais il y a du passage. Prix un peu élevé pour la surface.',
    'APPROVED',
    false,
    3,
    true,
    NOW() - INTERVAL '15 days',
    NOW() - INTERVAL '15 days'
ON CONFLICT DO NOTHING;

-- Avis pour PROP-PARIS-002 (Maison moderne à Paris)
INSERT INTO reviews (property_id, user_id, author_name, author_email, rating, comment, status, verified_purchase, helpful_count, active, created_at, updated_at)
SELECT 
    (SELECT id FROM properties WHERE reference = 'PROP-PARIS-002'),
    NULL::BIGINT,
    'Claire Moreau',
    'claire.moreau@example.com',
    5,
    'Superbe maison ! Le jardin est magnifique et très bien entretenu. L''agence a été parfaite, très réactive et professionnelle. Je suis ravie de mon achat !',
    'APPROVED',
    true,
    20,
    true,
    NOW() - INTERVAL '28 days',
    NOW() - INTERVAL '28 days'
UNION ALL SELECT 
    (SELECT id FROM properties WHERE reference = 'PROP-PARIS-002'),
    NULL::BIGINT,
    'Antoine Girard',
    'antoine.girard@example.com',
    4,
    'Belle maison avec un bon potentiel. Le quartier est agréable. L''agence a bien géré la transaction. Je recommande.',
    'APPROVED',
    true,
    10,
    true,
    NOW() - INTERVAL '22 days',
    NOW() - INTERVAL '22 days'
UNION ALL SELECT 
    (SELECT id FROM properties WHERE reference = 'PROP-PARIS-002'),
    NULL::BIGINT,
    'Lucie Blanc',
    'lucie.blanc@example.com',
    5,
    'Maison exceptionnelle ! Tout est parfait : l''emplacement, la qualité de la construction, le jardin. L''équipe a été au top du début à la fin.',
    'APPROVED',
    true,
    18,
    true,
    NOW() - INTERVAL '18 days',
    NOW() - INTERVAL '18 days'
ON CONFLICT DO NOTHING;

-- Avis pour PROP-LYON-001 (Appartement T3 Lyon)
INSERT INTO reviews (property_id, user_id, author_name, author_email, rating, comment, status, verified_purchase, helpful_count, active, created_at, updated_at)
SELECT 
    (SELECT id FROM properties WHERE reference = 'PROP-LYON-001'),
    NULL::BIGINT,
    'Thomas Petit',
    'thomas.petit@example.com',
    5,
    'Excellent appartement dans un quartier très sympa de Lyon. L''agence a été très professionnelle et a su répondre à toutes mes questions. Je recommande !',
    'APPROVED',
    true,
    14,
    true,
    NOW() - INTERVAL '35 days',
    NOW() - INTERVAL '35 days'
UNION ALL SELECT 
    (SELECT id FROM properties WHERE reference = 'PROP-LYON-001'),
    NULL::BIGINT,
    'Emma Rousseau',
    'emma.rousseau@example.com',
    4,
    'Très bon appartement, bien situé. Le parking est un plus. L''agence a été réactive. Seul point négatif : quelques travaux à prévoir.',
    'APPROVED',
    true,
    7,
    true,
    NOW() - INTERVAL '28 days',
    NOW() - INTERVAL '28 days'
ON CONFLICT DO NOTHING;

-- Avis pour PROP-MARSEILLE-001 (Villa avec piscine Marseille)
INSERT INTO reviews (property_id, user_id, author_name, author_email, rating, comment, status, verified_purchase, helpful_count, active, created_at, updated_at)
SELECT 
    (SELECT id FROM properties WHERE reference = 'PROP-MARSEILLE-001'),
    NULL::BIGINT,
    'Marc Durand',
    'marc.durand@example.com',
    5,
    'Villa de rêve ! La piscine est magnifique et la vue est exceptionnelle. L''agence a été parfaite, très professionnelle. Je suis très satisfait !',
    'APPROVED',
    true,
    25,
    true,
    NOW() - INTERVAL '40 days',
    NOW() - INTERVAL '40 days'
UNION ALL SELECT 
    (SELECT id FROM properties WHERE reference = 'PROP-MARSEILLE-001'),
    NULL::BIGINT,
    'Isabelle Lambert',
    'isabelle.lambert@example.com',
    5,
    'Superbe villa ! Tout est parfait : la piscine, le jardin, la vue sur la mer. L''équipe de l''agence a été formidable. Transaction sans souci.',
    'APPROVED',
    true,
    22,
    true,
    NOW() - INTERVAL '32 days',
    NOW() - INTERVAL '32 days'
UNION ALL SELECT 
    (SELECT id FROM properties WHERE reference = 'PROP-MARSEILLE-001'),
    NULL::BIGINT,
    'Nicolas Fabre',
    'nicolas.fabre@example.com',
    4,
    'Belle villa avec une piscine très agréable. Le quartier est calme et bien situé. L''agence a bien géré la transaction. Je recommande.',
    'APPROVED',
    true,
    11,
    true,
    NOW() - INTERVAL '26 days',
    NOW() - INTERVAL '26 days'
ON CONFLICT DO NOTHING;

-- Avis pour PROP-BORDEAUX-001 (Appartement centre Bordeaux)
INSERT INTO reviews (property_id, user_id, author_name, author_email, rating, comment, status, verified_purchase, helpful_count, active, created_at, updated_at)
SELECT 
    (SELECT id FROM properties WHERE reference = 'PROP-BORDEAUX-001'),
    NULL::BIGINT,
    'Camille Lemoine',
    'camille.lemoine@example.com',
    5,
    'Appartement parfait au cœur de Bordeaux ! Très bien situé, proche de tout. L''agence a été excellente, très réactive et professionnelle.',
    'APPROVED',
    true,
    16,
    true,
    NOW() - INTERVAL '38 days',
    NOW() - INTERVAL '38 days'
UNION ALL SELECT 
    (SELECT id FROM properties WHERE reference = 'PROP-BORDEAUX-001'),
    NULL::BIGINT,
    'Pauline Garnier',
    'pauline.garnier@example.com',
    4,
    'Très bon appartement dans le centre de Bordeaux. L''emplacement est idéal. L''agence a bien géré la transaction. Je suis satisfaite.',
    'APPROVED',
    true,
    9,
    true,
    NOW() - INTERVAL '30 days',
    NOW() - INTERVAL '30 days'
ON CONFLICT DO NOTHING;

-- =====================================================
-- AVIS D'AGENCES IMMOBILIÈRES (Organization Reviews)
-- =====================================================

-- Avis pour Immobilier Paris (ID 1)
INSERT INTO organization_reviews (organization_id, user_id, author_name, author_email, rating, comment, status, verified_client, helpful_count, transaction_type, active, created_at, updated_at)
SELECT 
    1,
    NULL::BIGINT,
    'Sophie Martin',
    'sophie.martin@example.com',
    5,
    'Excellente agence ! L''équipe est très professionnelle et réactive. Ils nous ont aidés à trouver notre appartement idéal à Paris en un temps record. Je recommande vivement !',
    'APPROVED',
    true,
    12,
    'SALE',
    true,
    NOW() - INTERVAL '30 days',
    NOW() - INTERVAL '30 days'
UNION ALL SELECT 
    1,
    NULL::BIGINT,
    'Pierre Durand',
    'pierre.durand@example.com',
    5,
    'Service impeccable de A à Z. L''agent était très à l''écoute et nous a guidés tout au long du processus d''achat. Très satisfait !',
    'APPROVED',
    true,
    8,
    'SALE',
    true,
    NOW() - INTERVAL '25 days',
    NOW() - INTERVAL '25 days'
UNION ALL SELECT 
    1,
    NULL::BIGINT,
    'Julie Bernard',
    'julie.bernard@example.com',
    4,
    'Bonne expérience globale. L''agence a un bon portefeuille de biens et les visites sont bien organisées. Quelques petits délais mais rien de rédhibitoire.',
    'APPROVED',
    true,
    5,
    'RENT',
    true,
    NOW() - INTERVAL '20 days',
    NOW() - INTERVAL '20 days'
UNION ALL SELECT 
    1,
    NULL::BIGINT,
    'Thomas Petit',
    'thomas.petit@example.com',
    5,
    'Agence sérieuse et compétente. Nous avons acheté notre appartement grâce à eux. Le suivi est excellent et les conseils sont précieux.',
    'APPROVED',
    true,
    15,
    'SALE',
    true,
    NOW() - INTERVAL '15 days',
    NOW() - INTERVAL '15 days'
UNION ALL SELECT 
    1,
    NULL::BIGINT,
    'Camille Rousseau',
    'camille.rousseau@example.com',
    4,
    'Très satisfaite de mes interactions avec cette agence. L''équipe est professionnelle et les biens proposés correspondent à mes attentes.',
    'APPROVED',
    false,
    3,
    'RENT',
    true,
    NOW() - INTERVAL '10 days',
    NOW() - INTERVAL '10 days'
ON CONFLICT DO NOTHING;

-- Avis pour Real Estate Lyon (ID 2)
INSERT INTO organization_reviews (organization_id, user_id, author_name, author_email, rating, comment, status, verified_client, helpful_count, transaction_type, active, created_at, updated_at)
SELECT 
    2,
    NULL::BIGINT,
    'Marc Lefebvre',
    'marc.lefebvre@example.com',
    5,
    'Agence de confiance à Lyon. Nous avons trouvé notre maison rapidement grâce à leur expertise du marché local. Service irréprochable !',
    'APPROVED',
    true,
    10,
    'SALE',
    true,
    NOW() - INTERVAL '28 days',
    NOW() - INTERVAL '28 days'
UNION ALL SELECT 
    2,
    NULL::BIGINT,
    'Isabelle Moreau',
    'isabelle.moreau@example.com',
    4,
    'Bonne agence avec une bonne connaissance du marché lyonnais. Les visites sont bien organisées et les conseils sont pertinents.',
    'APPROVED',
    true,
    6,
    'SALE',
    true,
    NOW() - INTERVAL '22 days',
    NOW() - INTERVAL '22 days'
UNION ALL SELECT 
    2,
    NULL::BIGINT,
    'Nicolas Girard',
    'nicolas.girard@example.com',
    5,
    'Excellente expérience ! L''équipe est très professionnelle et nous a aidés à trouver exactement ce que nous cherchions. Je recommande !',
    'APPROVED',
    true,
    9,
    'RENT',
    true,
    NOW() - INTERVAL '18 days',
    NOW() - INTERVAL '18 days'
ON CONFLICT DO NOTHING;

-- Avis pour Marseille Property (ID 3)
INSERT INTO organization_reviews (organization_id, user_id, author_name, author_email, rating, comment, status, verified_client, helpful_count, transaction_type, active, created_at, updated_at)
SELECT 
    3,
    NULL::BIGINT,
    'Laurence Blanc',
    'laurence.blanc@example.com',
    4,
    'Agence sérieuse à Marseille. Bon suivi et bonne communication. Nous avons trouvé notre appartement sans trop de difficultés.',
    'APPROVED',
    true,
    7,
    'RENT',
    true,
    NOW() - INTERVAL '24 days',
    NOW() - INTERVAL '24 days'
UNION ALL SELECT 
    3,
    NULL::BIGINT,
    'Antoine Garcia',
    'antoine.garcia@example.com',
    5,
    'Très satisfait de cette agence ! L''équipe est dynamique et connaît bien le marché marseillais. Service au top !',
    'APPROVED',
    true,
    11,
    'SALE',
    true,
    NOW() - INTERVAL '19 days',
    NOW() - INTERVAL '19 days'
ON CONFLICT DO NOTHING;

-- Avis pour Bordeaux Immobilier (ID 4)
INSERT INTO organization_reviews (organization_id, user_id, author_name, author_email, rating, comment, status, verified_client, helpful_count, transaction_type, active, created_at, updated_at)
SELECT 
    4,
    NULL::BIGINT,
    'Marie Dubois',
    'marie.dubois@example.com',
    5,
    'Agence très professionnelle à Bordeaux. L''équipe nous a accompagnés avec beaucoup de sérieux dans notre projet d''achat. Excellent service !',
    'APPROVED',
    true,
    14,
    'SALE',
    true,
    NOW() - INTERVAL '27 days',
    NOW() - INTERVAL '27 days'
UNION ALL SELECT 
    4,
    NULL::BIGINT,
    'François Leroy',
    'francois.leroy@example.com',
    4,
    'Bonne agence avec une bonne connaissance du marché bordelais. Les visites sont bien organisées et les conseils sont utiles.',
    'APPROVED',
    true,
    6,
    'RENT',
    true,
    NOW() - INTERVAL '21 days',
    NOW() - INTERVAL '21 days'
UNION ALL SELECT 
    4,
    NULL::BIGINT,
    'Céline Moreau',
    'celine.moreau@example.com',
    5,
    'Très satisfaite de cette agence ! Nous avons trouvé notre maison rapidement grâce à leur expertise. Je recommande !',
    'APPROVED',
    true,
    9,
    'SALE',
    true,
    NOW() - INTERVAL '16 days',
    NOW() - INTERVAL '16 days'
ON CONFLICT DO NOTHING;

-- Avis pour Nice Properties (ID 5)
INSERT INTO organization_reviews (organization_id, user_id, author_name, author_email, rating, comment, status, verified_client, helpful_count, transaction_type, active, created_at, updated_at)
SELECT 
    5,
    NULL::BIGINT,
    'Jean-Paul Martin',
    'jeanpaul.martin@example.com',
    5,
    'Excellente agence à Nice ! L''équipe est très réactive et connaît parfaitement le marché de la Côte d''Azur. Service irréprochable !',
    'APPROVED',
    true,
    13,
    'SALE',
    true,
    NOW() - INTERVAL '29 days',
    NOW() - INTERVAL '29 days'
UNION ALL SELECT 
    5,
    NULL::BIGINT,
    'Sylvie Bernard',
    'sylvie.bernard@example.com',
    4,
    'Bonne expérience avec cette agence. Les biens proposés sont de qualité et l''accompagnement est professionnel.',
    'APPROVED',
    true,
    5,
    'RENT',
    true,
    NOW() - INTERVAL '23 days',
    NOW() - INTERVAL '23 days'
UNION ALL SELECT 
    5,
    NULL::BIGINT,
    'Philippe Rousseau',
    'philippe.rousseau@example.com',
    5,
    'Agence sérieuse et compétente. Nous avons acheté notre appartement avec vue sur mer grâce à eux. Très satisfait !',
    'APPROVED',
    true,
    11,
    'SALE',
    true,
    NOW() - INTERVAL '17 days',
    NOW() - INTERVAL '17 days'
ON CONFLICT DO NOTHING;

-- Avis pour PROP-NICE-001 (Appartement vue mer Nice)
INSERT INTO reviews (property_id, user_id, author_name, author_email, rating, comment, status, verified_purchase, helpful_count, active, created_at, updated_at)
SELECT 
    (SELECT id FROM properties WHERE reference = 'PROP-NICE-001'),
    NULL::BIGINT,
    'Laurent Simon',
    'laurent.simon@example.com',
    5,
    'Appartement exceptionnel avec une vue magnifique sur la mer ! L''agence a été parfaite, très professionnelle. Je recommande vivement !',
    'APPROVED',
    true,
    28,
    true,
    NOW() - INTERVAL '45 days',
    NOW() - INTERVAL '45 days'
UNION ALL SELECT 
    (SELECT id FROM properties WHERE reference = 'PROP-NICE-001'),
    NULL::BIGINT,
    'Céline Roux',
    'celine.roux@example.com',
    5,
    'Superbe appartement avec vue mer ! Tout est parfait : l''emplacement, la qualité, la vue. L''équipe a été au top. Je suis ravie !',
    'APPROVED',
    true,
    24,
    true,
    NOW() - INTERVAL '36 days',
    NOW() - INTERVAL '36 days'
UNION ALL SELECT 
    (SELECT id FROM properties WHERE reference = 'PROP-NICE-001'),
    NULL::BIGINT,
    'David Mercier',
    'david.mercier@example.com',
    4,
    'Très bel appartement avec une belle vue. Le quartier est agréable. L''agence a bien géré la transaction. Je recommande.',
    'APPROVED',
    true,
    13,
    true,
    NOW() - INTERVAL '29 days',
    NOW() - INTERVAL '29 days'
ON CONFLICT DO NOTHING;

-- Quelques avis en attente de modération (PENDING)
INSERT INTO reviews (property_id, user_id, author_name, author_email, rating, comment, status, verified_purchase, helpful_count, active, created_at, updated_at)
SELECT 
    (SELECT id FROM properties WHERE reference = 'PROP-PARIS-001'),
    NULL::BIGINT,
    'Nouvel Acheteur',
    'nouvel.acheteur@example.com',
    5,
    'Très bon appartement, je recommande !',
    'PENDING',
    false,
    0,
    true,
    NOW() - INTERVAL '2 days',
    NOW() - INTERVAL '2 days'
UNION ALL SELECT 
    (SELECT id FROM properties WHERE reference = 'PROP-LYON-001'),
    NULL::BIGINT,
    'Client Satisfait',
    'client.satisfait@example.com',
    4,
    'Appartement correct, bon rapport qualité-prix.',
    'PENDING',
    false,
    0,
    true,
    NOW() - INTERVAL '1 day',
    NOW() - INTERVAL '1 day'
ON CONFLICT DO NOTHING;

-- =====================================================
-- 11.7. CRÉER DES HISTORIQUES DE PRIX
-- =====================================================

-- Historique des prix pour PROP-PARIS-001 (Appartement luxueux à Paris)
INSERT INTO price_history (property_id, price, currency, change_date, change_reason, created_by, source, created_at, updated_at)
SELECT 
    (SELECT id FROM properties WHERE reference = 'PROP-PARIS-001'),
    480000.00,
    'EUR',
    NOW() - INTERVAL '120 days',
    'Prix initial lors de la création',
    (SELECT id FROM users WHERE email = 'directeur@paris-immobilier.fr'),
    'AUTO',
    NOW() - INTERVAL '120 days',
    NOW() - INTERVAL '120 days'
UNION ALL SELECT 
    (SELECT id FROM properties WHERE reference = 'PROP-PARIS-001'),
    470000.00,
    'EUR',
    NOW() - INTERVAL '90 days',
    'Réduction pour accélérer la vente',
    (SELECT id FROM users WHERE email = 'directeur@paris-immobilier.fr'),
    'MANUAL',
    NOW() - INTERVAL '90 days',
    NOW() - INTERVAL '90 days'
UNION ALL SELECT 
    (SELECT id FROM properties WHERE reference = 'PROP-PARIS-001'),
    450000.00,
    'EUR',
    NOW() - INTERVAL '30 days',
    'Ajustement du marché',
    (SELECT id FROM users WHERE email = 'directeur@paris-immobilier.fr'),
    'MANUAL',
    NOW() - INTERVAL '30 days',
    NOW() - INTERVAL '30 days'
ON CONFLICT DO NOTHING;

-- Historique des prix pour PROP-PARIS-002 (Maison moderne à Paris)
INSERT INTO price_history (property_id, price, currency, change_date, change_reason, created_by, source, created_at, updated_at)
SELECT 
    (SELECT id FROM properties WHERE reference = 'PROP-PARIS-002'),
    680000.00,
    'EUR',
    NOW() - INTERVAL '100 days',
    'Prix initial lors de la création',
    (SELECT id FROM users WHERE email = 'directeur@paris-immobilier.fr'),
    'AUTO',
    NOW() - INTERVAL '100 days',
    NOW() - INTERVAL '100 days'
UNION ALL SELECT 
    (SELECT id FROM properties WHERE reference = 'PROP-PARIS-002'),
    665000.00,
    'EUR',
    NOW() - INTERVAL '60 days',
    'Réduction stratégique',
    (SELECT id FROM users WHERE email = 'directeur@paris-immobilier.fr'),
    'MANUAL',
    NOW() - INTERVAL '60 days',
    NOW() - INTERVAL '60 days'
UNION ALL SELECT 
    (SELECT id FROM properties WHERE reference = 'PROP-PARIS-002'),
    650000.00,
    'EUR',
    NOW() - INTERVAL '20 days',
    'Ajustement final du prix',
    (SELECT id FROM users WHERE email = 'directeur@paris-immobilier.fr'),
    'MANUAL',
    NOW() - INTERVAL '20 days',
    NOW() - INTERVAL '20 days'
ON CONFLICT DO NOTHING;

-- Historique des prix pour PROP-LYON-001 (Appartement T3 Lyon)
INSERT INTO price_history (property_id, price, currency, change_date, change_reason, created_by, source, created_at, updated_at)
SELECT 
    (SELECT id FROM properties WHERE reference = 'PROP-LYON-001'),
    320000.00,
    'EUR',
    NOW() - INTERVAL '110 days',
    'Prix initial lors de la création',
    (SELECT id FROM users WHERE email = 'manager@lyon-realestate.fr'),
    'AUTO',
    NOW() - INTERVAL '110 days',
    NOW() - INTERVAL '110 days'
UNION ALL SELECT 
    (SELECT id FROM properties WHERE reference = 'PROP-LYON-001'),
    310000.00,
    'EUR',
    NOW() - INTERVAL '50 days',
    'Réduction pour attirer les acheteurs',
    (SELECT id FROM users WHERE email = 'manager@lyon-realestate.fr'),
    'MANUAL',
    NOW() - INTERVAL '50 days',
    NOW() - INTERVAL '50 days'
ON CONFLICT DO NOTHING;

-- Historique des prix pour PROP-MARSEILLE-001 (Villa avec piscine Marseille)
INSERT INTO price_history (property_id, price, currency, change_date, change_reason, created_by, source, created_at, updated_at)
SELECT 
    (SELECT id FROM properties WHERE reference = 'PROP-MARSEILLE-001'),
    850000.00,
    'EUR',
    NOW() - INTERVAL '130 days',
    'Prix initial lors de la création',
    (SELECT id FROM users WHERE email = 'agent1@marseille-property.fr'),
    'AUTO',
    NOW() - INTERVAL '130 days',
    NOW() - INTERVAL '130 days'
UNION ALL SELECT 
    (SELECT id FROM properties WHERE reference = 'PROP-MARSEILLE-001'),
    820000.00,
    'EUR',
    NOW() - INTERVAL '80 days',
    'Réduction importante pour vente rapide',
    (SELECT id FROM users WHERE email = 'agent1@marseille-property.fr'),
    'MANUAL',
    NOW() - INTERVAL '80 days',
    NOW() - INTERVAL '80 days'
UNION ALL SELECT 
    (SELECT id FROM properties WHERE reference = 'PROP-MARSEILLE-001'),
    800000.00,
    'EUR',
    NOW() - INTERVAL '40 days',
    'Ajustement final',
    (SELECT id FROM users WHERE email = 'agent1@marseille-property.fr'),
    'MANUAL',
    NOW() - INTERVAL '40 days',
    NOW() - INTERVAL '40 days'
ON CONFLICT DO NOTHING;

-- Historique des prix pour PROP-BORDEAUX-001 (Appartement centre Bordeaux)
INSERT INTO price_history (property_id, price, currency, change_date, change_reason, created_by, source, created_at, updated_at)
SELECT 
    (SELECT id FROM properties WHERE reference = 'PROP-BORDEAUX-001'),
    280000.00,
    'EUR',
    NOW() - INTERVAL '95 days',
    'Prix initial lors de la création',
    (SELECT id FROM users WHERE email = 'agent1@bordeaux-realestate.fr'),
    'AUTO',
    NOW() - INTERVAL '95 days',
    NOW() - INTERVAL '95 days'
UNION ALL SELECT 
    (SELECT id FROM properties WHERE reference = 'PROP-BORDEAUX-001'),
    275000.00,
    'EUR',
    NOW() - INTERVAL '45 days',
    'Petite réduction pour accélérer',
    (SELECT id FROM users WHERE email = 'agent1@bordeaux-realestate.fr'),
    'MANUAL',
    NOW() - INTERVAL '45 days',
    NOW() - INTERVAL '45 days'
ON CONFLICT DO NOTHING;

-- Historique des prix pour PROP-NICE-001 (Appartement vue mer Nice)
INSERT INTO price_history (property_id, price, currency, change_date, change_reason, created_by, source, created_at, updated_at)
SELECT 
    (SELECT id FROM properties WHERE reference = 'PROP-NICE-001'),
    420000.00,
    'EUR',
    NOW() - INTERVAL '140 days',
    'Prix initial lors de la création',
    (SELECT id FROM users WHERE email = 'agent1@nice-property.fr'),
    'AUTO',
    NOW() - INTERVAL '140 days',
    NOW() - INTERVAL '140 days'
UNION ALL SELECT 
    (SELECT id FROM properties WHERE reference = 'PROP-NICE-001'),
    410000.00,
    'EUR',
    NOW() - INTERVAL '70 days',
    'Ajustement saisonnier',
    (SELECT id FROM users WHERE email = 'agent1@nice-property.fr'),
    'MANUAL',
    NOW() - INTERVAL '70 days',
    NOW() - INTERVAL '70 days'
UNION ALL SELECT 
    (SELECT id FROM properties WHERE reference = 'PROP-NICE-001'),
    400000.00,
    'EUR',
    NOW() - INTERVAL '25 days',
    'Réduction pour vente avant été',
    (SELECT id FROM users WHERE email = 'agent1@nice-property.fr'),
    'MANUAL',
    NOW() - INTERVAL '25 days',
    NOW() - INTERVAL '25 days'
ON CONFLICT DO NOTHING;

-- =====================================================
-- 11.8. CRÉER DES ALERTES DE PRIX
-- =====================================================

-- Alertes de prix pour PROP-PARIS-001
INSERT INTO price_alerts (property_id, user_id, alert_type, target_price, percentage_threshold, is_percentage, notified, active, email_notification, in_app_notification, created_at, updated_at)
SELECT 
    (SELECT id FROM properties WHERE reference = 'PROP-PARIS-001'),
    (SELECT id FROM users WHERE email = 'directeur@paris-immobilier.fr'),
    'PERCENTAGE_DROP',
    NULL::NUMERIC,
    5.0,
    true,
    false,
    true,
    true,
    true,
    NOW() - INTERVAL '20 days',
    NOW() - INTERVAL '20 days'
ON CONFLICT DO NOTHING;

-- Alertes de prix pour PROP-PARIS-002
INSERT INTO price_alerts (property_id, user_id, alert_type, target_price, percentage_threshold, is_percentage, notified, active, email_notification, in_app_notification, created_at, updated_at)
SELECT 
    (SELECT id FROM properties WHERE reference = 'PROP-PARIS-002'),
    (SELECT id FROM users WHERE email = 'manager@paris-immobilier.fr'),
    'PRICE_DROP',
    640000.00,
    NULL::DOUBLE PRECISION,
    false,
    false,
    true,
    true,
    true,
    NOW() - INTERVAL '15 days',
    NOW() - INTERVAL '15 days'
ON CONFLICT DO NOTHING;

-- Alerte de prix pour PROP-LYON-001
INSERT INTO price_alerts (property_id, user_id, alert_type, target_price, percentage_threshold, is_percentage, notified, active, email_notification, in_app_notification, created_at, updated_at)
SELECT 
    (SELECT id FROM properties WHERE reference = 'PROP-LYON-001'),
    (SELECT id FROM users WHERE email = 'directeur@lyon-realestate.fr'),
    'PRICE_DROP',
    260000.00,
    NULL::DOUBLE PRECISION,
    false,
    false,
    true,
    true,
    true,
    NOW() - INTERVAL '10 days',
    NOW() - INTERVAL '10 days'
ON CONFLICT DO NOTHING;

-- Alerte de prix pour PROP-MARSEILLE-001
INSERT INTO price_alerts (property_id, user_id, alert_type, target_price, percentage_threshold, is_percentage, notified, notified_at, active, email_notification, in_app_notification, created_at, updated_at)
SELECT 
    (SELECT id FROM properties WHERE reference = 'PROP-MARSEILLE-001'),
    (SELECT id FROM users WHERE email = 'directeur@marseille-property.fr'),
    'PERCENTAGE_INCREASE',
    NULL::NUMERIC,
    3.0,
    true,
    true,
    NOW() - INTERVAL '2 days',
    true,
    true,
    true,
    NOW() - INTERVAL '25 days',
    NOW() - INTERVAL '2 days'
ON CONFLICT DO NOTHING;

-- =====================================================
-- 11.9. CRÉER DES RENDEZ-VOUS DE VISITE
-- =====================================================

-- Visites pour PROP-PARIS-001
INSERT INTO visit_appointments (property_id, user_id, agent_id, visitor_name, visitor_email, visitor_phone, appointment_date, duration_minutes, status, notes, reminder_sent, active, created_at, updated_at)
SELECT 
    (SELECT id FROM properties WHERE reference = 'PROP-PARIS-001'),
    NULL::BIGINT,
    (SELECT id FROM users WHERE email = 'agent1@paris-immobilier.fr'),
    'Marie Dupont',
    'marie.dupont@example.com',
    '+33612345678',
    NOW() + INTERVAL '3 days',
    60,
    'CONFIRMED',
    'Intéressée par l''appartement, première visite',
    true,
    true,
    NOW() - INTERVAL '5 days',
    NOW() - INTERVAL '2 days'
UNION ALL SELECT 
    (SELECT id FROM properties WHERE reference = 'PROP-PARIS-001'),
    NULL::BIGINT,
    (SELECT id FROM users WHERE email = 'agent1@paris-immobilier.fr'),
    'Jean Martin',
    'jean.martin@example.com',
    '+33623456789',
    NOW() + INTERVAL '7 days',
    90,
    'PENDING',
    'Deuxième visite avec conjoint',
    false,
    true,
    NOW() - INTERVAL '2 days',
    NOW() - INTERVAL '2 days'
ON CONFLICT DO NOTHING;

-- Visites pour PROP-PARIS-002
INSERT INTO visit_appointments (property_id, user_id, agent_id, visitor_name, visitor_email, visitor_phone, appointment_date, duration_minutes, status, notes, reminder_sent, active, created_at, updated_at)
SELECT 
    (SELECT id FROM properties WHERE reference = 'PROP-PARIS-002'),
    NULL::BIGINT,
    (SELECT id FROM users WHERE email = 'agent2@paris-immobilier.fr'),
    'Sophie Bernard',
    'sophie.bernard@example.com',
    '+33634567890',
    NOW() + INTERVAL '5 days',
    60,
    'CONFIRMED',
    'Visite de la maison avec jardin',
    true,
    true,
    NOW() - INTERVAL '4 days',
    NOW() - INTERVAL '1 day'
ON CONFLICT DO NOTHING;

-- Visites pour PROP-LYON-001
INSERT INTO visit_appointments (property_id, user_id, agent_id, visitor_name, visitor_email, visitor_phone, appointment_date, duration_minutes, status, notes, reminder_sent, active, created_at, updated_at)
SELECT 
    (SELECT id FROM properties WHERE reference = 'PROP-LYON-001'),
    NULL::BIGINT,
    (SELECT id FROM users WHERE email = 'agent1@lyon-realestate.fr'),
    'Thomas Petit',
    'thomas.petit@example.com',
    '+33412345678',
    NOW() + INTERVAL '2 days',
    60,
    'PENDING',
    'Première visite',
    false,
    true,
    NOW() - INTERVAL '1 day',
    NOW() - INTERVAL '1 day'
ON CONFLICT DO NOTHING;

-- Visites pour PROP-LYON-002
INSERT INTO visit_appointments (property_id, user_id, agent_id, visitor_name, visitor_email, visitor_phone, appointment_date, duration_minutes, status, notes, agent_notes, reminder_sent, confirmed_at, active, created_at, updated_at)
SELECT 
    (SELECT id FROM properties WHERE reference = 'PROP-LYON-002'),
    NULL::BIGINT,
    (SELECT id FROM users WHERE email = 'agent2@lyon-realestate.fr'),
    'Claire Dubois',
    'claire.dubois@example.com',
    '+33423456789',
    NOW() + INTERVAL '4 days',
    90,
    'CONFIRMED',
    'Intéressée par la maison avec jardin',
    'Client très intéressé, demande de financement en cours',
    true,
    NOW() - INTERVAL '1 day',
    true,
    NOW() - INTERVAL '3 days',
    NOW() - INTERVAL '1 day'
ON CONFLICT DO NOTHING;

-- Visites pour PROP-MARSEILLE-001 - Visite 1
INSERT INTO visit_appointments (property_id, user_id, agent_id, visitor_name, visitor_email, visitor_phone, appointment_date, duration_minutes, status, notes, agent_notes, reminder_sent, confirmed_at, active, created_at, updated_at)
SELECT 
    (SELECT id FROM properties WHERE reference = 'PROP-MARSEILLE-001'),
    NULL::BIGINT,
    (SELECT id FROM users WHERE email = 'agent1@marseille-property.fr'),
    'Pierre Moreau',
    'pierre.moreau@example.com',
    '+33434567890',
    NOW() + INTERVAL '6 days',
    120,
    'CONFIRMED',
    'Visite de la villa avec piscine, vue mer',
    NULL,
    true,
    NOW() - INTERVAL '2 days',
    true,
    NOW() - INTERVAL '5 days',
    NOW() - INTERVAL '2 days'
ON CONFLICT DO NOTHING;

-- Visites pour PROP-MARSEILLE-001 - Visite 2
INSERT INTO visit_appointments (property_id, user_id, agent_id, visitor_name, visitor_email, visitor_phone, appointment_date, duration_minutes, status, notes, agent_notes, reminder_sent, confirmed_at, active, created_at, updated_at)
SELECT 
    (SELECT id FROM properties WHERE reference = 'PROP-MARSEILLE-001'),
    NULL::BIGINT,
    (SELECT id FROM users WHERE email = 'agent1@marseille-property.fr'),
    'Sophie Laurent',
    'sophie.laurent@example.com',
    '+33445678901',
    NOW() - INTERVAL '5 days',
    90,
    'COMPLETED',
    'Visite effectuée',
    'Visite très positive, client très intéressé. Demande de devis pour travaux.',
    true,
    NOW() - INTERVAL '6 days',
    true,
    NOW() - INTERVAL '8 days',
    NOW() - INTERVAL '5 days'
ON CONFLICT DO NOTHING;

-- Visites pour PROP-MARSEILLE-002
INSERT INTO visit_appointments (property_id, user_id, agent_id, visitor_name, visitor_email, visitor_phone, appointment_date, duration_minutes, status, notes, reminder_sent, active, created_at, updated_at)
SELECT 
    (SELECT id FROM properties WHERE reference = 'PROP-MARSEILLE-002'),
    NULL::BIGINT,
    (SELECT id FROM users WHERE email = 'agent2@marseille-property.fr'),
    'Marc Lefebvre',
    'marc.lefebvre@example.com',
    '+33456789012',
    NOW() + INTERVAL '1 day',
    60,
    'PENDING',
    'Première visite de l''appartement vue mer',
    false,
    true,
    NOW() - INTERVAL '2 days',
    NOW() - INTERVAL '2 days'
ON CONFLICT DO NOTHING;

-- Visite annulée pour PROP-PARIS-003
INSERT INTO visit_appointments (property_id, user_id, agent_id, visitor_name, visitor_email, visitor_phone, appointment_date, duration_minutes, status, notes, cancellation_reason, cancelled_at, reminder_sent, active, created_at, updated_at)
SELECT 
    (SELECT id FROM properties WHERE reference = 'PROP-PARIS-003'),
    NULL::BIGINT,
    (SELECT id FROM users WHERE email = 'agent2@paris-immobilier.fr'),
    'Julie Rousseau',
    'julie.rousseau@example.com',
    '+33678901234',
    NOW() - INTERVAL '3 days',
    60,
    'CANCELLED',
    'Visite annulée par le client',
    'Client a trouvé un autre bien',
    NOW() - INTERVAL '4 days',
    false,
    true,
    NOW() - INTERVAL '7 days',
    NOW() - INTERVAL '4 days'
ON CONFLICT DO NOTHING;

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
-- 13. CRÉER DES WORKFLOWS D'APPROBATION
-- =====================================================

-- Créer la table approval_workflows si elle n'existe pas
CREATE TABLE IF NOT EXISTS approval_workflows (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    action VARCHAR(100) NOT NULL,
    organization_id BIGINT NOT NULL,
    created_by BIGINT NOT NULL,
    target_type VARCHAR(100) NOT NULL,
    target_id BIGINT,
    steps TEXT,
    required_roles TEXT,
    active BOOLEAN NOT NULL DEFAULT true,
    is_default BOOLEAN NOT NULL DEFAULT false,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Créer la table tasks si elle n'existe pas
CREATE TABLE IF NOT EXISTS tasks (
    id BIGSERIAL PRIMARY KEY,
    workflow_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(2000),
    type VARCHAR(100) NOT NULL DEFAULT 'REVIEW',
    priority VARCHAR(50) NOT NULL DEFAULT 'MEDIUM',
    step_number INTEGER NOT NULL,
    assigned_to BIGINT,
    assigned_role VARCHAR(100),
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    due_date TIMESTAMP,
    completed_at TIMESTAMP,
    completed_by BIGINT,
    comments VARCHAR(1000),
    organization_id BIGINT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_task_workflow FOREIGN KEY (workflow_id) REFERENCES approval_workflows(id) ON DELETE CASCADE
);

-- Créer les index pour les workflows
CREATE INDEX IF NOT EXISTS idx_workflow_org ON approval_workflows(organization_id);
CREATE INDEX IF NOT EXISTS idx_workflow_action ON approval_workflows(action);
CREATE INDEX IF NOT EXISTS idx_workflow_active ON approval_workflows(active);
CREATE INDEX IF NOT EXISTS idx_workflow_status ON approval_workflows(status);

-- Créer les index pour les tâches
CREATE INDEX IF NOT EXISTS idx_task_workflow ON tasks(workflow_id);
CREATE INDEX IF NOT EXISTS idx_task_assigned_to ON tasks(assigned_to);
CREATE INDEX IF NOT EXISTS idx_task_status ON tasks(status);
CREATE INDEX IF NOT EXISTS idx_task_due_date ON tasks(due_date);

-- Workflows pour Immobilier Paris
INSERT INTO approval_workflows (name, description, action, organization_id, created_by, target_type, steps, required_roles, active, is_default, status, created_at, updated_at)
SELECT 
    'Validation création propriété', 
    'Workflow d''approbation pour la création de nouvelles propriétés. Nécessite validation manager puis directeur.',
    'PROPERTY_CREATE',
    o.id,
    u.id,
    'PROPERTY',
    '[{"step": 1, "name": "Vérification des informations", "role": "AGENT"}, {"step": 2, "name": "Validation Manager", "role": "MANAGER"}, {"step": 3, "name": "Approbation finale", "role": "DIRECTOR"}]',
    '["AGENT", "MANAGER", "DIRECTOR"]',
    true,
    true,
    'COMPLETED',
    NOW() - INTERVAL '10 days',
    NOW() - INTERVAL '10 days'
FROM organizations o, users u
WHERE o.name = 'Immobilier Paris' AND u.email = 'directeur@paris-immobilier.fr'
UNION ALL SELECT 
    'Modification propriété',
    'Workflow pour modifier une propriété existante. Validation manager requise.',
    'PROPERTY_UPDATE',
    o.id,
    u.id,
    'PROPERTY',
    '[{"step": 1, "name": "Révision des modifications", "role": "AGENT"}, {"step": 2, "name": "Validation Manager", "role": "MANAGER"}]',
    '["AGENT", "MANAGER"]',
    true,
    true,
    'IN_PROGRESS',
    NOW() - INTERVAL '8 days',
    NOW() - INTERVAL '8 days'
FROM organizations o, users u
WHERE o.name = 'Immobilier Paris' AND u.email = 'directeur@paris-immobilier.fr'
UNION ALL SELECT 
    'Upload document',
    'Workflow pour l''upload de documents. Validation automatique pour les agents.',
    'DOCUMENT_UPLOAD',
    o.id,
    u.id,
    'DOCUMENT',
    '[{"step": 1, "name": "Vérification du document", "role": "AGENT"}]',
    '["AGENT"]',
    true,
    true,
    'PENDING',
    NOW() - INTERVAL '5 days',
    NOW() - INTERVAL '5 days'
FROM organizations o, users u
WHERE o.name = 'Immobilier Paris' AND u.email = 'directeur@paris-immobilier.fr'
ON CONFLICT DO NOTHING;

-- Workflows pour Real Estate Lyon
INSERT INTO approval_workflows (name, description, action, organization_id, created_by, target_type, steps, required_roles, active, is_default, status, created_at, updated_at)
SELECT 
    'Validation création propriété',
    'Processus d''approbation pour nouvelles propriétés avec validation en 2 étapes.',
    'PROPERTY_CREATE',
    o.id,
    u.id,
    'PROPERTY',
    '[{"step": 1, "name": "Saisie et vérification", "role": "AGENT"}, {"step": 2, "name": "Approbation Manager", "role": "MANAGER"}]',
    '["AGENT", "MANAGER"]',
    true,
    true,
    'PENDING',
    NOW() - INTERVAL '7 days',
    NOW() - INTERVAL '7 days'
FROM organizations o, users u
WHERE o.name = 'Real Estate Lyon' AND u.email = 'directeur@lyon-realestate.fr'
UNION ALL SELECT 
    'Suppression propriété',
    'Workflow strict pour la suppression de propriétés. Nécessite double validation.',
    'PROPERTY_DELETE',
    o.id,
    u.id,
    'PROPERTY',
    '[{"step": 1, "name": "Demande de suppression", "role": "AGENT"}, {"step": 2, "name": "Validation Manager", "role": "MANAGER"}, {"step": 3, "name": "Approbation finale Directeur", "role": "DIRECTOR"}]',
    '["AGENT", "MANAGER", "DIRECTOR"]',
    true,
    true,
    'PENDING',
    NOW() - INTERVAL '6 days',
    NOW() - INTERVAL '6 days'
FROM organizations o, users u
WHERE o.name = 'Real Estate Lyon' AND u.email = 'directeur@lyon-realestate.fr'
ON CONFLICT DO NOTHING;

-- Workflows pour Property Marseille
INSERT INTO approval_workflows (name, description, action, organization_id, created_by, target_type, steps, required_roles, active, is_default, status, created_at, updated_at)
SELECT 
    'Validation création propriété',
    'Workflow simplifié pour création de propriétés.',
    'PROPERTY_CREATE',
    o.id,
    u.id,
    'PROPERTY',
    '[{"step": 1, "name": "Création et validation", "role": "AGENT"}]',
    '["AGENT"]',
    true,
    true,
    'CANCELLED',
    NOW() - INTERVAL '4 days',
    NOW() - INTERVAL '4 days'
FROM organizations o, users u
WHERE o.name = 'Property Marseille' AND u.email = 'directeur@marseille-property.fr'
ON CONFLICT DO NOTHING;

-- =====================================================
-- 13.1. CRÉER DES TÂCHES POUR LES WORKFLOWS
-- =====================================================

-- Tâches pour le workflow de création de propriété (Immobilier Paris) - PROP-PARIS-001
INSERT INTO tasks (workflow_id, title, description, type, priority, step_number, assigned_to, assigned_role, status, due_date, completed_at, completed_by, comments, organization_id, active, created_at, updated_at)
SELECT 
    w.id,
    'Vérification des informations de la propriété',
    'Vérifier que toutes les informations de la propriété PROP-PARIS-001 sont complètes et correctes.',
    'REVIEW',
    'MEDIUM',
    1,
    (SELECT id FROM users WHERE email = 'agent1@paris-immobilier.fr'),
    'AGENT',
    'APPROVED',
    NOW() - INTERVAL '5 days',
    NOW() - INTERVAL '4 days',
    (SELECT id FROM users WHERE email = 'agent1@paris-immobilier.fr'),
    'Toutes les informations vérifiées et validées.',
    o.id,
    true,
    NOW() - INTERVAL '5 days',
    NOW() - INTERVAL '4 days'
FROM approval_workflows w, organizations o
WHERE w.action = 'PROPERTY_CREATE' AND w.organization_id = o.id AND o.name = 'Immobilier Paris' AND w.is_default = true
UNION ALL SELECT 
    w.id,
    'Validation Manager',
    'Valider la création de la propriété PROP-PARIS-001.',
    'APPROVAL',
    'HIGH',
    2,
    (SELECT id FROM users WHERE email = 'manager@paris-immobilier.fr'),
    'MANAGER',
    'APPROVED',
    NOW() - INTERVAL '4 days',
    NOW() - INTERVAL '3 days',
    (SELECT id FROM users WHERE email = 'manager@paris-immobilier.fr'),
    'Propriété validée, conforme aux standards.',
    o.id,
    true,
    NOW() - INTERVAL '4 days',
    NOW() - INTERVAL '3 days'
FROM approval_workflows w, organizations o
WHERE w.action = 'PROPERTY_CREATE' AND w.organization_id = o.id AND o.name = 'Immobilier Paris' AND w.is_default = true
UNION ALL SELECT 
    w.id,
    'Approbation finale Directeur',
    'Approbation finale de la propriété PROP-PARIS-001.',
    'APPROVAL',
    'HIGH',
    3,
    (SELECT id FROM users WHERE email = 'directeur@paris-immobilier.fr'),
    'DIRECTOR',
    'APPROVED',
    NOW() - INTERVAL '3 days',
    NOW() - INTERVAL '2 days',
    (SELECT id FROM users WHERE email = 'directeur@paris-immobilier.fr'),
    'Approuvé pour publication.',
    o.id,
    true,
    NOW() - INTERVAL '3 days',
    NOW() - INTERVAL '2 days'
FROM approval_workflows w, organizations o
WHERE w.action = 'PROPERTY_CREATE' AND w.organization_id = o.id AND o.name = 'Immobilier Paris' AND w.is_default = true
ON CONFLICT DO NOTHING;

-- Tâches en cours pour le workflow de modification (Immobilier Paris) - PROP-PARIS-002
INSERT INTO tasks (workflow_id, title, description, type, priority, step_number, assigned_to, assigned_role, status, due_date, completed_at, completed_by, comments, organization_id, active, created_at, updated_at)
SELECT 
    w.id,
    'Révision des modifications',
    'Réviser les modifications apportées à la propriété PROP-PARIS-002.',
    'REVIEW',
    'MEDIUM',
    1,
    (SELECT id FROM users WHERE email = 'agent2@paris-immobilier.fr'),
    'AGENT',
    'IN_PROGRESS',
    NOW() + INTERVAL '2 days',
    NULL::TIMESTAMP,
    NULL::BIGINT,
    NULL,
    o.id,
    true,
    NOW() - INTERVAL '1 day',
    NOW() - INTERVAL '1 day'
FROM approval_workflows w, organizations o
WHERE w.action = 'PROPERTY_UPDATE' AND w.organization_id = o.id AND o.name = 'Immobilier Paris' AND w.is_default = true
ON CONFLICT DO NOTHING;

-- Tâches en attente pour Real Estate Lyon
INSERT INTO tasks (workflow_id, title, description, type, priority, step_number, assigned_to, assigned_role, status, due_date, completed_at, completed_by, comments, organization_id, active, created_at, updated_at)
SELECT 
    w.id,
    'Saisie et vérification',
    'Saisir et vérifier les informations de la nouvelle propriété.',
    'REVIEW',
    'MEDIUM',
    1,
    (SELECT id FROM users WHERE email = 'agent1@lyon-realestate.fr'),
    'AGENT',
    'PENDING',
    NOW() + INTERVAL '3 days',
    NULL::TIMESTAMP,
    NULL::BIGINT,
    NULL,
    o.id,
    true,
    NOW() - INTERVAL '2 days',
    NOW() - INTERVAL '2 days'
FROM approval_workflows w, organizations o
WHERE w.action = 'PROPERTY_CREATE' AND w.organization_id = o.id AND o.name = 'Real Estate Lyon' AND w.is_default = true
UNION ALL SELECT 
    w.id,
    'Demande de suppression',
    'Demande de suppression de la propriété PROP-LYON-001.',
    'REVIEW',
    'HIGH',
    1,
    (SELECT id FROM users WHERE email = 'agent2@lyon-realestate.fr'),
    'AGENT',
    'PENDING',
    NOW() + INTERVAL '1 day',
    NULL::TIMESTAMP,
    NULL::BIGINT,
    NULL,
    o.id,
    true,
    NOW() - INTERVAL '1 day',
    NOW() - INTERVAL '1 day'
FROM approval_workflows w, organizations o
WHERE w.action = 'PROPERTY_DELETE' AND w.organization_id = o.id AND o.name = 'Real Estate Lyon' AND w.is_default = true
ON CONFLICT DO NOTHING;

-- Tâche rejetée pour Property Marseille
INSERT INTO tasks (workflow_id, title, description, type, priority, step_number, assigned_to, assigned_role, status, due_date, completed_at, completed_by, comments, organization_id, active, created_at, updated_at)
SELECT 
    w.id,
    'Création et validation',
    'Créer et valider la nouvelle propriété.',
    'REVIEW',
    'MEDIUM',
    1,
    (SELECT id FROM users WHERE email = 'agent1@marseille-property.fr'),
    'AGENT',
    'REJECTED',
    NOW() - INTERVAL '3 days',
    NOW() - INTERVAL '2 days',
    (SELECT id FROM users WHERE email = 'manager@marseille-property.fr'),
    'Informations incomplètes. Veuillez compléter les champs manquants.',
    o.id,
    true,
    NOW() - INTERVAL '3 days',
    NOW() - INTERVAL '2 days'
FROM approval_workflows w, organizations o
WHERE w.action = 'PROPERTY_CREATE' AND w.organization_id = o.id AND o.name = 'Property Marseille' AND w.is_default = true
ON CONFLICT DO NOTHING;

-- =====================================================
-- 14. CRÉER LES ARTICLES DE BLOG
-- =====================================================

-- Récupérer les IDs des utilisateurs et organisations pour les articles
DO $$
DECLARE
    admin_user_id BIGINT;
    org1_id BIGINT;
    org2_id BIGINT;
    org3_id BIGINT;
BEGIN
    -- Récupérer l'ID de l'admin
    SELECT id INTO admin_user_id FROM users WHERE email = 'admin@viridial.com' LIMIT 1;
    
    -- Récupérer les IDs des organisations
    SELECT id INTO org1_id FROM organizations WHERE name = 'Paris Immobilier' LIMIT 1;
    SELECT id INTO org2_id FROM organizations WHERE name = 'Lyon Real Estate' LIMIT 1;
    SELECT id INTO org3_id FROM organizations WHERE name = 'Marseille Property' LIMIT 1;
    
    -- Article 1: Guide d'achat
    INSERT INTO blog_posts (
        title, excerpt, content, slug, category, tags, 
        meta_description, meta_keywords, status, published_at,
        author_id, author_name, organization_id, view_count,
        created_at, updated_at
    ) VALUES (
        'Guide complet pour acheter votre premier appartement à Paris',
        'Découvrez tous nos conseils pratiques pour réussir votre premier achat immobilier à Paris. Budget, financement, démarches administratives : tout ce que vous devez savoir.',
        '<h2>Introduction</h2><p>Acheter votre premier appartement à Paris est une étape importante. Cette ville offre de nombreuses opportunités mais aussi des défis spécifiques. Dans ce guide complet, nous vous accompagnons pas à pas.</p><h2>1. Définir votre budget</h2><p>Avant de commencer vos recherches, il est essentiel de définir votre budget. Prenez en compte :</p><ul><li>Vos économies pour l''apport</li><li>Votre capacité d''emprunt</li><li>Les frais de notaire (environ 8% du prix d''achat)</li><li>Les frais d''agence</li><li>Les travaux éventuels</li></ul><h2>2. Choisir le bon quartier</h2><p>Paris compte 20 arrondissements, chacun avec ses spécificités. Le 11e arrondissement est très prisé pour son dynamisme, tandis que le 15e offre plus d''espace pour les familles.</p><h2>3. Les financements disponibles</h2><p>Plusieurs options s''offrent à vous : prêt immobilier classique, prêt à taux zéro (PTZ), prêt action logement, etc. N''hésitez pas à comparer les offres de plusieurs banques.</p><h2>Conclusion</h2><p>Acheter à Paris demande de la préparation et de la patience. Avec les bons conseils et un accompagnement professionnel, vous trouverez le bien qui correspond à vos attentes.</p>',
        'guide-complet-acheter-premier-appartement-paris',
        'Guide',
        'achat, paris, premier achat, appartement, conseils',
        'Guide complet pour acheter votre premier appartement à Paris. Conseils pratiques, budget, financement et démarches administratives.',
        'achat appartement paris, premier achat immobilier, guide achat paris, conseils achat immobilier',
        'PUBLISHED',
        NOW() - INTERVAL '15 days',
        admin_user_id,
        'Équipe Viridial',
        org1_id,
        245,
        NOW() - INTERVAL '20 days',
        NOW() - INTERVAL '15 days'
    ) ON CONFLICT (slug) DO NOTHING;
    
    -- Article 2: Investissement locatif
    INSERT INTO blog_posts (
        title, excerpt, content, slug, category, tags,
        meta_description, meta_keywords, status, published_at,
        author_id, author_name, organization_id, view_count,
        created_at, updated_at
    ) VALUES (
        'Investissement locatif : les meilleurs quartiers de Lyon en 2024',
        'Où investir dans l''immobilier locatif à Lyon ? Découvrez notre analyse des quartiers les plus rentables et les tendances du marché.',
        '<h2>Pourquoi investir à Lyon ?</h2><p>Lyon est la deuxième ville de France et offre un marché locatif dynamique. Avec une population étudiante importante et un bassin d''emploi attractif, la demande locative est forte.</p><h2>Les quartiers à privilégier</h2><h3>1. La Part-Dieu</h3><p>Quartier d''affaires en plein essor, la Part-Dieu attire les cadres et professionnels. Les loyers sont élevés mais la demande est constante.</p><h3>2. Confluence</h3><p>Nouveau quartier moderne, la Confluence séduit les jeunes actifs. Les prix sont encore accessibles avec un fort potentiel de croissance.</p><h3>3. Croix-Rousse</h3><p>Quartier historique et authentique, la Croix-Rousse offre un bon équilibre entre prix et rendement locatif.</p><h2>Calcul de rentabilité</h2><p>Pour un investissement locatif réussi, visez un rendement brut d''au moins 5%. Prenez en compte :</p><ul><li>Le prix d''achat</li><li>Les charges (taxe foncière, charges de copropriété)</li><li>Les frais de gestion</li><li>Les vacances locatives</li></ul>',
        'investissement-locatif-meilleurs-quartiers-lyon-2024',
        'Investissement',
        'investissement, location, lyon, rentabilité, quartiers',
        'Guide complet pour investir dans l''immobilier locatif à Lyon. Analyse des quartiers les plus rentables en 2024.',
        'investissement locatif lyon, rentabilité immobilière, quartiers lyon, achat locatif',
        'PUBLISHED',
        NOW() - INTERVAL '10 days',
        admin_user_id,
        'Équipe Viridial',
        org2_id,
        189,
        NOW() - INTERVAL '15 days',
        NOW() - INTERVAL '10 days'
    ) ON CONFLICT (slug) DO NOTHING;
    
    -- Article 3: Actualités marché
    INSERT INTO blog_posts (
        title, excerpt, content, slug, category, tags,
        meta_description, meta_keywords, status, published_at,
        author_id, author_name, organization_id, view_count,
        created_at, updated_at
    ) VALUES (
        'Marché immobilier 2024 : tendances et prévisions',
        'Analyse complète du marché immobilier français en 2024. Évolution des prix, nouvelles réglementations et conseils pour acheteurs et vendeurs.',
        '<h2>État du marché en 2024</h2><p>Le marché immobilier français connaît une période de transition en 2024. Après plusieurs années de hausse continue, les prix se stabilisent dans la plupart des régions.</p><h2>Évolution des prix par région</h2><h3>Île-de-France</h3><p>Paris et sa région restent le marché le plus cher de France, mais la croissance ralentit. Les prix se stabilisent autour de 10 000€/m² en moyenne à Paris.</p><h3>Province</h3><p>Les grandes métropoles (Lyon, Marseille, Bordeaux) continuent d''attirer les investisseurs. Les prix progressent modérément, entre 2% et 4% par an.</p><h2>Nouvelles réglementations</h2><p>Plusieurs mesures impactent le marché en 2024 :</p><ul><li>Nouveau DPE (Diagnostic de Performance Énergétique) plus strict</li><li>Encadrement des loyers renforcé dans certaines zones</li><li>Nouvelles aides à l''accession</li></ul><h2>Conseils pour 2024</h2><p>Pour les acheteurs : prenez votre temps, négociez, et vérifiez bien le DPE. Pour les vendeurs : mettez en valeur votre bien et soyez réaliste sur le prix.</p>',
        'marche-immobilier-2024-tendances-previsions',
        'Actualités',
        'marché immobilier, tendances, prix, 2024, prévisions',
        'Analyse complète du marché immobilier français en 2024. Tendances, évolution des prix et nouvelles réglementations.',
        'marché immobilier 2024, prix immobilier, tendances immobilières, prévisions marché',
        'PUBLISHED',
        NOW() - INTERVAL '5 days',
        admin_user_id,
        'Équipe Viridial',
        org1_id,
        312,
        NOW() - INTERVAL '8 days',
        NOW() - INTERVAL '5 days'
    ) ON CONFLICT (slug) DO NOTHING;
    
    -- Article 4: Conseils vendeurs
    INSERT INTO blog_posts (
        title, excerpt, content, slug, category, tags,
        meta_description, meta_keywords, status, published_at,
        author_id, author_name, organization_id, view_count,
        created_at, updated_at
    ) VALUES (
        '10 conseils pour vendre votre bien rapidement et au meilleur prix',
        'Vous souhaitez vendre votre appartement ou votre maison ? Découvrez nos 10 conseils d''experts pour une vente rapide et au meilleur prix.',
        '<h2>1. Fixez le bon prix</h2><p>Un prix trop élevé fait fuir les acheteurs. Faites réaliser une estimation professionnelle et restez réaliste.</p><h2>2. Mettez en valeur votre bien</h2><p>Un bien bien présenté se vend mieux. Nettoyez, rangez, et mettez en valeur les points forts de votre propriété.</p><h2>3. Photos professionnelles</h2><p>Les photos sont la première impression. Investissez dans des photos professionnelles qui mettent en valeur votre bien.</p><h2>4. Dépensez intelligemment</h2><p>Ne faites pas de gros travaux inutiles. Concentrez-vous sur les améliorations qui rapportent : peinture, éclairage, rangement.</p><h2>5. Choisissez le bon moment</h2><p>Le printemps et l''automne sont généralement les meilleures périodes pour vendre.</p><h2>6. Soignez la description</h2><p>Une bonne description attire les acheteurs. Mettez en avant les atouts : quartier, transports, équipements.</p><h2>7. Soyez disponible</h2><p>Facilitez les visites. Plus vous êtes disponible, plus vite vous trouverez un acheteur.</p><h2>8. Préparez les documents</h2><p>Ayez tous les documents prêts : DPE, diagnostics, plans, etc. Cela accélère la vente.</p><h2>9. Négociez intelligemment</h2><p>Soyez ouvert à la négociation mais connaissez votre prix plancher.</p><h2>10. Faites confiance à un professionnel</h2><p>Un agent immobilier expérimenté vous fait gagner du temps et maximise vos chances de vendre au meilleur prix.</p>',
        '10-conseils-vendre-bien-rapidement-meilleur-prix',
        'Conseils',
        'vente, conseils, prix, vendeur, rapidité',
        '10 conseils d''experts pour vendre votre bien immobilier rapidement et au meilleur prix. Guide pratique complet.',
        'vendre appartement, vendre maison, conseils vente immobilière, prix vente',
        'PUBLISHED',
        NOW() - INTERVAL '3 days',
        admin_user_id,
        'Équipe Viridial',
        org3_id,
        156,
        NOW() - INTERVAL '6 days',
        NOW() - INTERVAL '3 days'
    ) ON CONFLICT (slug) DO NOTHING;
    
    -- Article 5: DPE et rénovation
    INSERT INTO blog_posts (
        title, excerpt, content, slug, category, tags,
        meta_description, meta_keywords, status, published_at,
        author_id, author_name, organization_id, view_count,
        created_at, updated_at
    ) VALUES (
        'DPE 2024 : comprendre le nouveau diagnostic et ses impacts',
        'Le Diagnostic de Performance Énergétique a évolué en 2024. Découvrez les changements et leurs impacts sur l''achat, la vente et la location.',
        '<h2>Qu''est-ce que le DPE ?</h2><p>Le Diagnostic de Performance Énergétique (DPE) évalue la consommation énergétique d''un bien immobilier. Il est obligatoire depuis 2006 pour la vente et la location.</p><h2>Les nouveautés 2024</h2><p>Le nouveau DPE est plus strict et plus précis :</p><ul><li>Méthode de calcul améliorée</li><li>Prise en compte de tous les usages énergétiques</li><li>Sanctions renforcées pour les biens énergivores</li></ul><h2>Les classes énergétiques</h2><p>De A (très performant) à G (très énergivore), le DPE classe votre bien. Depuis 2024, les biens classés F et G sont soumis à des restrictions.</p><h2>Impacts sur la vente</h2><p>Un bien mal classé est plus difficile à vendre. Les banques sont plus réticentes à financer, et les acheteurs négocient davantage.</p><h2>Impacts sur la location</h2><p>Les biens classés F et G ne peuvent plus être loués sans travaux. C''est une opportunité pour les investisseurs de rénover.</p><h2>Les aides à la rénovation</h2><p>De nombreuses aides existent : MaPrimeRénov, éco-PTZ, TVA réduite, etc. Renseignez-vous avant de rénover.</p>',
        'dpe-2024-comprendre-nouveau-diagnostic-impacts',
        'Guide',
        'DPE, diagnostic, énergie, rénovation, 2024',
        'Guide complet sur le nouveau DPE 2024. Comprendre les changements et leurs impacts sur l''achat, la vente et la location.',
        'DPE 2024, diagnostic performance énergétique, classe énergétique, rénovation énergétique',
        'PUBLISHED',
        NOW() - INTERVAL '1 day',
        admin_user_id,
        'Équipe Viridial',
        org1_id,
        98,
        NOW() - INTERVAL '4 days',
        NOW() - INTERVAL '1 day'
    ) ON CONFLICT (slug) DO NOTHING;
    
    -- Article 6: Quartier Marseille
    INSERT INTO blog_posts (
        title, excerpt, content, slug, category, tags,
        meta_description, meta_keywords, status, published_at,
        author_id, author_name, organization_id, view_count,
        created_at, updated_at
    ) VALUES (
        'Marseille : les quartiers qui montent en 2024',
        'Découvrez les quartiers marseillais en pleine mutation. Où investir et vivre à Marseille en 2024 ?',
        '<h2>Marseille, une ville en transformation</h2><p>Marseille connaît une véritable renaissance. De nombreux quartiers se transforment, attirant de nouveaux résidents et investisseurs.</p><h2>Les quartiers à suivre</h2><h3>1. Le Panier</h3><p>Quartier historique en pleine rénovation, le Panier séduit par son authenticité et son charme méditerranéen.</p><h3>2. La Joliette</h3><p>Quartier d''affaires moderne, la Joliette offre un cadre de vie agréable avec vue sur le port.</p><h3>3. Endoume</h3><p>Quartier résidentiel calme et familial, Endoume offre un bon compromis entre prix et qualité de vie.</p><h2>Prix du m² par quartier</h2><p>Les prix varient considérablement : de 3 000€/m² dans certains quartiers populaires à plus de 6 000€/m² dans les quartiers huppés.</p><h2>Investissement locatif</h2><p>Marseille offre de bonnes opportunités pour l''investissement locatif, avec des rendements attractifs et une demande locative forte.</p>',
        'marseille-quartiers-montent-2024',
        'Actualités',
        'marseille, quartiers, investissement, tendances, 2024',
        'Découvrez les quartiers marseillais en pleine mutation en 2024. Où investir et vivre à Marseille ?',
        'marseille quartiers, investissement marseille, immobilier marseille, quartiers marseille',
        'PUBLISHED',
        NOW() - INTERVAL '7 days',
        admin_user_id,
        'Équipe Viridial',
        org3_id,
        201,
        NOW() - INTERVAL '12 days',
        NOW() - INTERVAL '7 days'
    ) ON CONFLICT (slug) DO NOTHING;
    
    -- Article 7: Financement
    INSERT INTO blog_posts (
        title, excerpt, content, slug, category, tags,
        meta_description, meta_keywords, status, published_at,
        author_id, author_name, organization_id, view_count,
        created_at, updated_at
    ) VALUES (
        'Prêt immobilier : comment obtenir le meilleur taux en 2024',
        'Tous nos conseils pour négocier votre prêt immobilier et obtenir le meilleur taux. Comparaison des offres, dossier, négociation.',
        '<h2>Comprendre les taux immobiliers</h2><p>Les taux immobiliers varient selon plusieurs facteurs : votre profil, la durée du prêt, le montant emprunté, et la banque.</p><h2>Préparer son dossier</h2><p>Un dossier solide augmente vos chances d''obtenir un bon taux :</p><ul><li>Stabilité professionnelle</li><li>Revenus réguliers</li><li>Apport suffisant (au moins 10%)</li><li>Pas de crédits en cours</li></ul><h2>Comparer les offres</h2><p>Ne vous contentez pas d''une seule banque. Faites jouer la concurrence et comparez au moins 3 à 5 offres.</p><h2>Négocier</h2><p>Les taux sont négociables ! N''hésitez pas à négocier, surtout si vous avez un bon profil. Vous pouvez économiser plusieurs milliers d''euros.</p><h2>Les aides disponibles</h2><p>Plusieurs dispositifs peuvent vous aider : PTZ, prêt action logement, garantie Visale, etc. Renseignez-vous sur vos éligibilités.</p>',
        'pret-immobilier-obtenir-meilleur-taux-2024',
        'Guide',
        'prêt immobilier, taux, financement, négociation, banque',
        'Guide complet pour obtenir le meilleur taux de prêt immobilier en 2024. Conseils pour négocier et comparer les offres.',
        'prêt immobilier, taux prêt, financement immobilier, négociation prêt',
        'PUBLISHED',
        NOW() - INTERVAL '12 days',
        admin_user_id,
        'Équipe Viridial',
        org2_id,
        278,
        NOW() - INTERVAL '18 days',
        NOW() - INTERVAL '12 days'
    ) ON CONFLICT (slug) DO NOTHING;
    
    -- Article 8: DRAFT (non publié)
    INSERT INTO blog_posts (
        title, excerpt, content, slug, category, tags,
        meta_description, meta_keywords, status, published_at,
        author_id, author_name, organization_id, view_count,
        created_at, updated_at
    ) VALUES (
        'Les tendances de la décoration immobilière en 2024',
        'Découvrez les dernières tendances en matière de décoration pour valoriser votre bien immobilier.',
        '<h2>Les couleurs tendance</h2><p>Les couleurs neutres et apaisantes sont à l''honneur en 2024.</p><h2>Les matériaux naturels</h2><p>Le bois, la pierre et les matériaux naturels sont très appréciés.</p>',
        'tendances-decoration-immobiliere-2024',
        'Conseils',
        'décoration, tendances, rénovation, design',
        'Découvrez les tendances de la décoration immobilière en 2024 pour valoriser votre bien.',
        'décoration immobilière, tendances décoration, design intérieur',
        'DRAFT',
        NULL,
        admin_user_id,
        'Équipe Viridial',
        org1_id,
        0,
        NOW() - INTERVAL '2 days',
        NOW() - INTERVAL '2 days'
    ) ON CONFLICT (slug) DO NOTHING;
    
END $$;

-- =====================================================
-- 14.5. CRÉER DES PAYS ET VILLES (Configuration SaaS)
DO $$
BEGIN
    -- Pays
    INSERT INTO countries (code, name, code3, phone_code, currency, currency_symbol, timezone, flag_emoji, description, latitude, longitude, important_data, active, display_order) VALUES
    ('FR', 'France', 'FRA', '+33', 'EUR', '€', 'Europe/Paris', '🇫🇷', 'République française, pays d''Europe occidentale', 46.603354, 1.888334, '{"population": 67897000, "area": 643801, "languages": ["fr"], "capital": "Paris", "gdp": 2937.5}'::jsonb, true, 1),
    ('BE', 'Belgique', 'BEL', '+32', 'EUR', '€', 'Europe/Brussels', '🇧🇪', 'Royaume de Belgique, pays d''Europe occidentale', 50.503887, 4.469936, '{"population": 11623000, "area": 30528, "languages": ["nl", "fr", "de"], "capital": "Bruxelles", "gdp": 529.6}'::jsonb, true, 2),
    ('CH', 'Suisse', 'CHE', '+41', 'CHF', 'CHF', 'Europe/Zurich', '🇨🇭', 'Confédération suisse, pays d''Europe centrale', 46.818188, 8.227512, '{"population": 8703000, "area": 41285, "languages": ["de", "fr", "it", "rm"], "capital": "Berne", "gdp": 812.9}'::jsonb, true, 3),
    ('LU', 'Luxembourg', 'LUX', '+352', 'EUR', '€', 'Europe/Luxembourg', '🇱🇺', 'Grand-Duché de Luxembourg', 49.815273, 6.129583, '{"population": 640000, "area": 2586, "languages": ["lb", "fr", "de"], "capital": "Luxembourg", "gdp": 85.1}'::jsonb, true, 4),
    ('MC', 'Monaco', 'MCO', '+377', 'EUR', '€', 'Europe/Monaco', '🇲🇨', 'Principauté de Monaco', 43.738418, 7.424616, '{"population": 39000, "area": 2.02, "languages": ["fr"], "capital": "Monaco", "gdp": 7.2}'::jsonb, true, 5),
    ('ES', 'Espagne', 'ESP', '+34', 'EUR', '€', 'Europe/Madrid', '🇪🇸', 'Royaume d''Espagne', 40.463667, -3.74922, '{"population": 47431000, "area": 505990, "languages": ["es"], "capital": "Madrid", "gdp": 1427.0}'::jsonb, true, 6),
    ('IT', 'Italie', 'ITA', '+39', 'EUR', '€', 'Europe/Rome', '🇮🇹', 'République italienne', 41.87194, 12.56738, '{"population": 58853000, "area": 301340, "languages": ["it"], "capital": "Rome", "gdp": 2107.7}'::jsonb, true, 7),
    ('PT', 'Portugal', 'PRT', '+351', 'EUR', '€', 'Europe/Lisbon', '🇵🇹', 'République portugaise', 39.399872, -8.224454, '{"population": 10277000, "area": 92090, "languages": ["pt"], "capital": "Lisbonne", "gdp": 251.2}'::jsonb, true, 8),
    ('DE', 'Allemagne', 'DEU', '+49', 'EUR', '€', 'Europe/Berlin', '🇩🇪', 'République fédérale d''Allemagne', 51.165691, 10.451526, '{"population": 83240000, "area": 357022, "languages": ["de"], "capital": "Berlin", "gdp": 4223.1}'::jsonb, true, 9),
    ('GB', 'Royaume-Uni', 'GBR', '+44', 'GBP', '£', 'Europe/London', '🇬🇧', 'Royaume-Uni de Grande-Bretagne et d''Irlande du Nord', 55.378051, -3.435973, '{"population": 67330000, "area": 243610, "languages": ["en"], "capital": "Londres", "gdp": 3108.4}'::jsonb, true, 10),
    ('US', 'États-Unis', 'USA', '+1', 'USD', '$', 'America/New_York', '🇺🇸', 'États-Unis d''Amérique', 37.09024, -95.712891, '{"population": 331900000, "area": 9833517, "languages": ["en"], "capital": "Washington", "gdp": 25463.7}'::jsonb, true, 11),
    ('CA', 'Canada', 'CAN', '+1', 'CAD', 'C$', 'America/Toronto', '🇨🇦', 'Canada', 56.130366, -106.346771, '{"population": 38250000, "area": 9984670, "languages": ["en", "fr"], "capital": "Ottawa", "gdp": 2139.8}'::jsonb, true, 12),
    ('MA', 'Maroc', 'MAR', '+212', 'MAD', 'د.م.', 'Africa/Casablanca', '🇲🇦', 'Royaume du Maroc', 31.791702, -7.09262, '{"population": 37478000, "area": 446550, "languages": ["ar", "fr"], "capital": "Rabat", "gdp": 133.1}'::jsonb, true, 13),
    ('TN', 'Tunisie', 'TUN', '+216', 'TND', 'د.ت', 'Africa/Tunis', '🇹🇳', 'République tunisienne', 33.886917, 9.537499, '{"population": 12046000, "area": 163610, "languages": ["ar", "fr"], "capital": "Tunis", "gdp": 46.7}'::jsonb, true, 14),
    ('DZ', 'Algérie', 'DZA', '+213', 'DZD', 'د.ج', 'Africa/Algiers', '🇩🇿', 'République algérienne démocratique et populaire', 28.033886, 1.659626, '{"population": 44616000, "area": 2381741, "languages": ["ar", "fr"], "capital": "Alger", "gdp": 191.0}'::jsonb, true, 15)
    ON CONFLICT (code) DO NOTHING;

    -- Villes françaises
    INSERT INTO cities (name, postal_code, country_id, latitude, longitude, region, department, timezone, description, important_data, active, display_order)
    SELECT 
      c.name,
      c.postal_code,
      (SELECT id FROM countries WHERE code = 'FR') as country_id,
      c.latitude,
      c.longitude,
      c.region,
      c.department,
      'Europe/Paris' as timezone,
      c.description,
      c.important_data::jsonb,
      true,
      c.display_order
    FROM (VALUES
      ('Paris', '75001', 48.856614, 2.352222, 'Île-de-France', 'Paris', 'Capitale de la France, ville la plus peuplée', '{"population": 2161000, "area": 105.4, "density": 20499, "tourist_attractions": ["Tour Eiffel", "Louvre", "Notre-Dame", "Arc de Triomphe"]}', 1),
      ('Lyon', '69001', 45.764043, 4.835659, 'Auvergne-Rhône-Alpes', 'Rhône', 'Deuxième plus grande ville de France', '{"population": 522000, "area": 47.87, "density": 10900, "tourist_attractions": ["Basilique Notre-Dame de Fourvière", "Vieux Lyon", "Parc de la Tête d''Or"]}', 2),
      ('Marseille', '13001', 43.296482, 5.36978, 'Provence-Alpes-Côte d''Azur', 'Bouches-du-Rhône', 'Plus grand port de France', '{"population": 870000, "area": 240.62, "density": 3615, "tourist_attractions": ["Vieux-Port", "Notre-Dame de la Garde", "Calanques"]}', 3),
      ('Toulouse', '31000', 43.604652, 1.444209, 'Occitanie', 'Haute-Garonne', 'Ville rose, capitale de l''aéronautique', '{"population": 498000, "area": 118.3, "density": 4208, "tourist_attractions": ["Capitole", "Basilique Saint-Sernin", "Cité de l''espace"]}', 4),
      ('Nice', '06000', 43.710173, 7.261953, 'Provence-Alpes-Côte d''Azur', 'Alpes-Maritimes', 'Capitale de la Côte d''Azur', '{"population": 342000, "area": 71.92, "density": 4757, "tourist_attractions": ["Promenade des Anglais", "Vieille Ville", "Colline du Château"]}', 5),
      ('Nantes', '44000', 47.218371, -1.553621, 'Pays de la Loire', 'Loire-Atlantique', 'Capitale historique de la Bretagne', '{"population": 318000, "area": 65.19, "density": 4878, "tourist_attractions": ["Château des ducs de Bretagne", "Île de Nantes", "Machines de l''île"]}', 6),
      ('Strasbourg', '67000', 48.573405, 7.752111, 'Grand Est', 'Bas-Rhin', 'Capitale européenne', '{"population": 291000, "area": 78.26, "density": 3716, "tourist_attractions": ["Cathédrale Notre-Dame", "Petite France", "Parlement européen"]}', 7),
      ('Montpellier', '34000', 43.610769, 3.876716, 'Occitanie', 'Hérault', 'Ville étudiante dynamique', '{"population": 299000, "area": 56.88, "density": 5258, "tourist_attractions": ["Place de la Comédie", "Antigone", "Jardin des plantes"]}', 8),
      ('Bordeaux', '33000', 44.837789, -0.57918, 'Nouvelle-Aquitaine', 'Gironde', 'Capitale du vin', '{"population": 260000, "area": 49.36, "density": 5268, "tourist_attractions": ["Place de la Bourse", "Cité du Vin", "Quartier Saint-Pierre"]}', 9),
      ('Lille', '59000', 50.62925, 3.057256, 'Hauts-de-France', 'Nord', 'Capitale des Flandres', '{"population": 234000, "area": 34.83, "density": 6720, "tourist_attractions": ["Grand Place", "Vieille Bourse", "Citadelle"]}', 10),
      ('Rennes', '35000', 48.117266, -1.677793, 'Bretagne', 'Ille-et-Vilaine', 'Capitale de la Bretagne', '{"population": 222000, "area": 50.39, "density": 4405, "tourist_attractions": ["Parlement de Bretagne", "Cathédrale Saint-Pierre", "Thabor"]}', 11),
      ('Reims', '51100', 49.258329, 4.031696, 'Grand Est', 'Marne', 'Cité des sacres', '{"population": 183000, "area": 46.9, "density": 3902, "tourist_attractions": ["Cathédrale Notre-Dame", "Palais du Tau", "Caves de champagne"]}', 12),
      ('Saint-Étienne', '42000', 45.439695, 4.387178, 'Auvergne-Rhône-Alpes', 'Loire', 'Ville industrielle', '{"population": 172000, "area": 79.97, "density": 2150, "tourist_attractions": ["Musée d''art moderne", "Stade Geoffroy-Guichard"]}', 13),
      ('Toulon', '83000', 43.124228, 5.928, 'Provence-Alpes-Côte d''Azur', 'Var', 'Grand port militaire', '{"population": 176000, "area": 42.84, "density": 4106, "tourist_attractions": ["Mont Faron", "Vieille Ville", "Plages"]}', 14),
      ('Le Havre', '76600', 49.49437, 0.107929, 'Normandie', 'Seine-Maritime', 'Port maritime important', '{"population": 170000, "area": 46.95, "density": 3621, "tourist_attractions": ["Musée d''art moderne", "Port", "Plage"]}', 15),
      ('Grenoble', '38000', 45.188529, 5.724524, 'Auvergne-Rhône-Alpes', 'Isère', 'Capitale des Alpes', '{"population": 160000, "area": 18.13, "density": 8828, "tourist_attractions": ["Bastille", "Musée de Grenoble", "Téléphérique"]}', 16),
      ('Dijon', '21000', 47.322047, 5.04148, 'Bourgogne-Franche-Comté', 'Côte-d''Or', 'Capitale de la Bourgogne', '{"population": 159000, "area": 40.41, "density": 3937, "tourist_attractions": ["Palais des ducs", "Musée des Beaux-Arts", "Rue de la Liberté"]}', 17),
      ('Angers', '49000', 47.473988, -0.551558, 'Pays de la Loire', 'Maine-et-Loire', 'Cité historique', '{"population": 155000, "area": 42.71, "density": 3629, "tourist_attractions": ["Château d''Angers", "Cathédrale", "Terra Botanica"]}', 18),
      ('Nîmes', '30000', 43.836699, 4.360054, 'Occitanie', 'Gard', 'Ville romaine', '{"population": 151000, "area": 161.85, "density": 933, "tourist_attractions": ["Arènes", "Maison Carrée", "Pont du Gard"]}', 19),
      ('Villeurbanne', '69100', 45.771944, 4.890171, 'Auvergne-Rhône-Alpes', 'Rhône', 'Ville de la banlieue lyonnaise', '{"population": 150000, "area": 14.52, "density": 10331, "tourist_attractions": ["Gratte-Ciel", "TNP"]}', 20)
    ) AS c(name, postal_code, latitude, longitude, region, department, description, important_data, display_order)
    ON CONFLICT (name, country_id) DO NOTHING;

    -- Villes belges
    INSERT INTO cities (name, postal_code, country_id, latitude, longitude, region, timezone, description, important_data, active, display_order)
    SELECT 
      c.name,
      c.postal_code,
      (SELECT id FROM countries WHERE code = 'BE') as country_id,
      c.latitude,
      c.longitude,
      c.region,
      'Europe/Brussels' as timezone,
      c.description,
      c.important_data::jsonb,
      true,
      c.display_order
    FROM (VALUES
      ('Bruxelles', '1000', 50.850346, 4.351721, 'Région de Bruxelles-Capitale', 'Capitale de la Belgique et de l''UE', '{"population": 185000, "area": 32.61, "density": 5673, "tourist_attractions": ["Grand-Place", "Atomium", "Manneken Pis"]}', 1),
      ('Anvers', '2000', 51.219448, 4.402464, 'Région flamande', 'Grand port et ville culturelle', '{"population": 529000, "area": 204.51, "density": 2587, "tourist_attractions": ["Cathédrale Notre-Dame", "Musée Rubens", "Zoo"]}', 2)
    ) AS c(name, postal_code, latitude, longitude, region, description, important_data, display_order)
    ON CONFLICT (name, country_id) DO NOTHING;

    -- Villes suisses
    INSERT INTO cities (name, postal_code, country_id, latitude, longitude, region, timezone, description, important_data, active, display_order)
    SELECT 
      c.name,
      c.postal_code,
      (SELECT id FROM countries WHERE code = 'CH') as country_id,
      c.latitude,
      c.longitude,
      c.region,
      'Europe/Zurich' as timezone,
      c.description,
      c.important_data::jsonb,
      true,
      c.display_order
    FROM (VALUES
      ('Genève', '1200', 46.204391, 6.143158, 'Genève', 'Ville internationale et siège d''organisations', '{"population": 203000, "area": 15.93, "density": 12747, "tourist_attractions": ["Jet d''eau", "Palais des Nations", "Vieille Ville"]}', 1),
      ('Zurich', '8001', 47.376887, 8.541694, 'Zurich', 'Plus grande ville de Suisse', '{"population": 421000, "area": 87.88, "density": 4791, "tourist_attractions": ["Vieille Ville", "Lac de Zurich", "Bahnhofstrasse"]}', 2),
      ('Lausanne', '1000', 46.519962, 6.633597, 'Vaud', 'Capitale olympique', '{"population": 140000, "area": 41.37, "density": 3384, "tourist_attractions": ["Cathédrale", "Musée Olympique", "Lac Léman"]}', 3)
    ) AS c(name, postal_code, latitude, longitude, region, description, important_data, display_order)
    ON CONFLICT (name, country_id) DO NOTHING;

    -- Villes marocaines
    INSERT INTO cities (name, postal_code, country_id, latitude, longitude, region, timezone, description, important_data, active, display_order)
    SELECT 
      c.name,
      c.postal_code,
      (SELECT id FROM countries WHERE code = 'MA') as country_id,
      c.latitude,
      c.longitude,
      c.region,
      'Africa/Casablanca' as timezone,
      c.description,
      c.important_data::jsonb,
      true,
      c.display_order
    FROM (VALUES
      ('Casablanca', '20000', 33.573110, -7.589843, 'Casablanca-Settat', 'Plus grande ville du Maroc', '{"population": 3350000, "area": 220, "density": 15227, "tourist_attractions": ["Hassan II", "Corniche", "Vieille Médina"]}', 1),
      ('Rabat', '10000', 34.020882, -6.841650, 'Rabat-Salé-Kénitra', 'Capitale du Maroc', '{"population": 580000, "area": 117, "density": 4957, "tourist_attractions": ["Kasbah des Oudaias", "Tour Hassan", "Mausolée Mohammed V"]}', 2)
    ) AS c(name, postal_code, latitude, longitude, region, description, important_data, display_order)
    ON CONFLICT (name, country_id) DO NOTHING;
END $$;

-- 14.6. CRÉER DES ANNONCES PUBLICITAIRES
-- =====================================================

DO $$
DECLARE
    org1_id BIGINT;
    org2_id BIGINT;
    org3_id BIGINT;
    org4_id BIGINT;
    org5_id BIGINT;
    admin_user_id BIGINT;
BEGIN
    -- Récupérer les IDs des organisations
    SELECT id INTO org1_id FROM organizations WHERE name = 'Immobilier Paris' LIMIT 1;
    SELECT id INTO org2_id FROM organizations WHERE name = 'Real Estate Lyon' LIMIT 1;
    SELECT id INTO org3_id FROM organizations WHERE name = 'Property Marseille' LIMIT 1;
    SELECT id INTO org4_id FROM organizations WHERE name = 'Bordeaux Immobilier' LIMIT 1;
    SELECT id INTO org5_id FROM organizations WHERE name = 'Nice Properties' LIMIT 1;
    SELECT id INTO admin_user_id FROM users WHERE email = 'admin@viridial.com' LIMIT 1;
    
    -- Annonce 1: Bannière principale pour Immobilier Paris (BANNER - TOP)
    INSERT INTO advertisements (
        organization_id, title, description, image_url, link_url,
        ad_type, position, status, start_date, end_date,
        budget, daily_budget, cost_per_click, cost_per_impression,
        target_locations, target_property_types, target_transaction_types,
        impressions, clicks, conversions, total_spent,
        max_impressions_per_day, priority, active, created_by,
        created_at, updated_at
    ) VALUES (
        org1_id,
        'Découvrez nos biens d''exception à Paris',
        'Plus de 200 biens disponibles dans les meilleurs quartiers de Paris. Visites virtuelles disponibles.',
        'https://images.unsplash.com/photo-1512917774080-9991f1c4c750?w=1200&h=400&fit=crop',
        'https://paris-immobilier.fr/properties',
        'BANNER',
        'TOP',
        'ACTIVE',
        NOW() - INTERVAL '30 days',
        NOW() + INTERVAL '60 days',
        5000.00,
        150.00,
        0.50,
        2.00,
        '{"cities": ["Paris"], "postalCodes": ["75001", "75002", "75003", "75004", "75005", "75006", "75007", "75008", "75009", "75010", "75011", "75012", "75013", "75014", "75015", "75016", "75017", "75018", "75019", "75020"], "regions": ["Île-de-France"]}',
        '["APARTMENT", "HOUSE", "PENTHOUSE"]',
        '["SALE", "RENT"]',
        12500,
        342,
        28,
        1250.50,
        5000,
        10,
        true,
        admin_user_id,
        NOW() - INTERVAL '35 days',
        NOW() - INTERVAL '1 day'
    );
    
    -- Annonce 2: Sidebar pour Real Estate Lyon (SIDEBAR - SIDEBAR_RIGHT)
    INSERT INTO advertisements (
        organization_id, title, description, image_url, link_url,
        ad_type, position, status, start_date, end_date,
        budget, daily_budget, cost_per_click, cost_per_impression,
        target_locations, target_property_types, target_transaction_types,
        impressions, clicks, conversions, total_spent,
        max_impressions_per_day, priority, active, created_by,
        created_at, updated_at
    ) VALUES (
        org2_id,
        'Investissement locatif à Lyon',
        'Rendements attractifs jusqu''à 6% dans les meilleurs quartiers lyonnais. Accompagnement personnalisé.',
        'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=400&h=600&fit=crop',
        'https://lyon-realestate.fr/investment',
        'SIDEBAR',
        'SIDEBAR_RIGHT',
        'ACTIVE',
        NOW() - INTERVAL '15 days',
        NOW() + INTERVAL '45 days',
        2500.00,
        80.00,
        0.75,
        1.50,
        '{"cities": ["Lyon"], "postalCodes": ["69001", "69002", "69003", "69004", "69005", "69006", "69007", "69008", "69009"], "regions": ["Auvergne-Rhône-Alpes"]}',
        '["APARTMENT", "HOUSE"]',
        '["RENT"]',
        6800,
        145,
        12,
        890.25,
        3000,
        8,
        true,
        admin_user_id,
        NOW() - INTERVAL '20 days',
        NOW() - INTERVAL '2 days'
    ) ON CONFLICT DO NOTHING;
    
    -- Annonce 3: Inline pour Property Marseille (INLINE)
    INSERT INTO advertisements (
        organization_id, title, description, image_url, link_url,
        ad_type, position, status, start_date, end_date,
        budget, daily_budget, cost_per_click, cost_per_impression,
        target_locations, target_property_types, target_transaction_types,
        impressions, clicks, conversions, total_spent,
        max_impressions_per_day, priority, active, created_by,
        created_at, updated_at
    ) VALUES (
        org3_id,
        'Villas de prestige sur la Côte d''Azur',
        'Sélection exclusive de villas avec vue mer. Piscine, jardin, parking. Visites sur rendez-vous.',
        'https://images.unsplash.com/photo-1600596542815-ffad4c1539a9?w=1200&h=300&fit=crop',
        'https://marseille-property.fr/villas',
        'INLINE',
        'INLINE',
        'ACTIVE',
        NOW() - INTERVAL '10 days',
        NOW() + INTERVAL '30 days',
        3500.00,
        120.00,
        1.00,
        2.50,
        '{"cities": ["Marseille", "Nice", "Cannes", "Antibes"], "postalCodes": ["13001", "13002", "13003", "13004", "13005", "13006", "13007", "13008", "13009", "13010", "13011", "13012", "13013", "13014", "13015", "13016"], "regions": ["Provence-Alpes-Côte d''Azur"]}',
        '["HOUSE", "VILLA", "PENTHOUSE"]',
        '["SALE"]',
        4200,
        98,
        8,
        650.75,
        2500,
        7,
        true,
        admin_user_id,
        NOW() - INTERVAL '15 days',
        NOW() - INTERVAL '3 days'
    ) ON CONFLICT DO NOTHING;
    
    -- Annonce 4: Sponsored Property pour Bordeaux Immobilier (SPONSORED_PROPERTY)
    INSERT INTO advertisements (
        organization_id, title, description, image_url, link_url,
        ad_type, position, status, start_date, end_date,
        budget, daily_budget, cost_per_click, cost_per_impression,
        target_locations, target_property_types, target_transaction_types,
        impressions, clicks, conversions, total_spent,
        max_impressions_per_day, priority, active, created_by,
        created_at, updated_at
    ) VALUES (
        org4_id,
        'Appartement T3 rénové - Centre-ville Bordeaux',
        'Magnifique appartement 75m², 3 pièces, rénové avec goût. Proche transports et commerces. Prix attractif.',
        'https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=800&h=600&fit=crop',
        'https://bordeaux-immobilier.fr/properties/123',
        'SPONSORED_PROPERTY',
        'INLINE',
        'ACTIVE',
        NOW() - INTERVAL '5 days',
        NOW() + INTERVAL '25 days',
        1500.00,
        60.00,
        0.60,
        1.80,
        '{"cities": ["Bordeaux"], "postalCodes": ["33000", "33100", "33200", "33300"], "regions": ["Nouvelle-Aquitaine"]}',
        '["APARTMENT"]',
        '["SALE"]',
        2800,
        67,
        5,
        420.30,
        2000,
        6,
        true,
        admin_user_id,
        NOW() - INTERVAL '8 days',
        NOW() - INTERVAL '1 day'
    ) ON CONFLICT DO NOTHING;
    
    -- Annonce 5: Bannière pour Nice Properties (BANNER - BOTTOM)
    INSERT INTO advertisements (
        organization_id, title, description, image_url, link_url,
        ad_type, position, status, start_date, end_date,
        budget, daily_budget, cost_per_click, cost_per_impression,
        target_locations, target_property_types, target_transaction_types,
        impressions, clicks, conversions, total_spent,
        max_impressions_per_day, priority, active, created_by,
        created_at, updated_at
    ) VALUES (
        org5_id,
        'Location saisonnière à Nice',
        'Appartements et studios meublés pour vos vacances. Vue mer, proche plage. Réservation en ligne.',
        'https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=1200&h=400&fit=crop',
        'https://nice-properties.fr/seasonal-rental',
        'BANNER',
        'BOTTOM',
        'ACTIVE',
        NOW() - INTERVAL '20 days',
        NOW() + INTERVAL '40 days',
        4000.00,
        130.00,
        0.80,
        2.20,
        '{"cities": ["Nice", "Cannes", "Antibes", "Monaco"], "postalCodes": ["06000", "06100", "06200", "06300", "06400"], "regions": ["Provence-Alpes-Côte d''Azur"]}',
        '["APARTMENT", "STUDIO"]',
        '["RENT"]',
        9500,
        210,
        18,
        1120.80,
        4000,
        9,
        true,
        admin_user_id,
        NOW() - INTERVAL '25 days',
        NOW() - INTERVAL '2 days'
    ) ON CONFLICT DO NOTHING;
    
    -- Annonce 6: Sidebar gauche pour Immobilier Paris (SIDEBAR - SIDEBAR_LEFT)
    INSERT INTO advertisements (
        organization_id, title, description, image_url, link_url,
        ad_type, position, status, start_date, end_date,
        budget, daily_budget, cost_per_click, cost_per_impression,
        target_locations, target_property_types, target_transaction_types,
        impressions, clicks, conversions, total_spent,
        max_impressions_per_day, priority, active, created_by,
        created_at, updated_at
    ) VALUES (
        org1_id,
        'Estimation gratuite de votre bien',
        'Obtenez une estimation précise et gratuite de votre appartement ou maison à Paris. Expert certifié.',
        'https://images.unsplash.com/photo-1560518883-ce09059eeffa?w=400&h=600&fit=crop',
        'https://paris-immobilier.fr/estimation',
        'SIDEBAR',
        'SIDEBAR_LEFT',
        'ACTIVE',
        NOW() - INTERVAL '7 days',
        NOW() + INTERVAL '53 days',
        2000.00,
        70.00,
        0.55,
        1.40,
        '{"cities": ["Paris"], "postalCodes": ["75001", "75002", "75003", "75004", "75005", "75006", "75007", "75008", "75009", "75010", "75011", "75012", "75013", "75014", "75015", "75016", "75017", "75018", "75019", "75020"], "regions": ["Île-de-France"]}',
        '["APARTMENT", "HOUSE"]',
        '["SALE"]',
        5200,
        125,
        10,
        680.50,
        2500,
        5,
        true,
        admin_user_id,
        NOW() - INTERVAL '10 days',
        NOW() - INTERVAL '1 day'
    ) ON CONFLICT DO NOTHING;
    
    -- Annonce 7: Inline pour Real Estate Lyon (INLINE) - Promotion spéciale
    INSERT INTO advertisements (
        organization_id, title, description, image_url, link_url,
        ad_type, position, status, start_date, end_date,
        budget, daily_budget, cost_per_click, cost_per_impression,
        target_locations, target_property_types, target_transaction_types,
        impressions, clicks, conversions, total_spent,
        max_impressions_per_day, priority, active, created_by,
        created_at, updated_at
    ) VALUES (
        org2_id,
        'Promotion: Frais d''agence réduits jusqu''à fin du mois',
        'Profitez de nos frais d''agence réduits de 30% sur tous nos biens. Offre limitée, ne tardez pas !',
        'https://images.unsplash.com/photo-1560518883-ce09059eeffa?w=1200&h=300&fit=crop',
        'https://lyon-realestate.fr/promotion',
        'INLINE',
        'INLINE',
        'ACTIVE',
        NOW() - INTERVAL '3 days',
        NOW() + INTERVAL '27 days',
        1800.00,
        90.00,
        0.70,
        1.90,
        '{"cities": ["Lyon", "Villeurbanne", "Vénissieux"], "postalCodes": ["69001", "69002", "69003", "69004", "69005", "69006", "69007", "69008", "69009"], "regions": ["Auvergne-Rhône-Alpes"]}',
        '["APARTMENT", "HOUSE", "COMMERCIAL"]',
        '["SALE", "RENT"]',
        3200,
        78,
        6,
        510.20,
        2000,
        4,
        true,
        admin_user_id,
        NOW() - INTERVAL '5 days',
        NOW() - INTERVAL '1 day'
    ) ON CONFLICT DO NOTHING;
    
    -- Annonce 8: Popup pour Property Marseille (POPUP) - Annonce en pause
    INSERT INTO advertisements (
        organization_id, title, description, image_url, link_url,
        ad_type, position, status, start_date, end_date,
        budget, daily_budget, cost_per_click, cost_per_impression,
        target_locations, target_property_types, target_transaction_types,
        impressions, clicks, conversions, total_spent,
        max_impressions_per_day, priority, active, created_by,
        created_at, updated_at
    ) VALUES (
        org3_id,
        'Nouveau programme immobilier - Résidence de standing',
        'Découvrez notre nouveau programme de 50 appartements avec vue mer. Livraison prévue en 2025.',
        'https://images.unsplash.com/photo-1600585154340-be6161a56a0c?w=600&h=600&fit=crop',
        'https://marseille-property.fr/new-program',
        'POPUP',
        'INLINE',
        'PAUSED',
        NOW() - INTERVAL '2 days',
        NOW() + INTERVAL '58 days',
        6000.00,
        200.00,
        1.20,
        3.00,
        '{"cities": ["Marseille", "Aix-en-Provence"], "postalCodes": ["13001", "13002", "13003", "13004", "13005", "13006", "13007", "13008"], "regions": ["Provence-Alpes-Côte d''Azur"]}',
        '["APARTMENT"]',
        '["SALE"]',
        1500,
        35,
        3,
        280.50,
        1000,
        3,
        true,
        admin_user_id,
        NOW() - INTERVAL '5 days',
        NOW() - INTERVAL '1 day'
    ) ON CONFLICT DO NOTHING;
    
    -- Annonce 9: Bannière pour Bordeaux Immobilier (BANNER - HEADER)
    INSERT INTO advertisements (
        organization_id, title, description, image_url, link_url,
        ad_type, position, status, start_date, end_date,
        budget, daily_budget, cost_per_click, cost_per_impression,
        target_locations, target_property_types, target_transaction_types,
        impressions, clicks, conversions, total_spent,
        max_impressions_per_day, priority, active, created_by,
        created_at, updated_at
    ) VALUES (
        org4_id,
        'Bordeaux: Le marché immobilier explose !',
        'Découvrez pourquoi Bordeaux est la ville la plus attractive de France pour investir. Analyse gratuite.',
        'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=1200&h=200&fit=crop',
        'https://bordeaux-immobilier.fr/market-analysis',
        'BANNER',
        'HEADER',
        'ACTIVE',
        NOW() - INTERVAL '12 days',
        NOW() + INTERVAL '18 days',
        2200.00,
        100.00,
        0.65,
        1.70,
        '{"cities": ["Bordeaux", "Pessac", "Mérignac"], "postalCodes": ["33000", "33100", "33200", "33300"], "regions": ["Nouvelle-Aquitaine"]}',
        '["APARTMENT", "HOUSE", "COMMERCIAL"]',
        '["SALE", "RENT"]',
        6100,
        145,
        11,
        890.75,
        3000,
        7,
        true,
        admin_user_id,
        NOW() - INTERVAL '15 days',
        NOW() - INTERVAL '2 days'
    ) ON CONFLICT DO NOTHING;
    
    -- Annonce 10: Sponsored Property pour Nice Properties (SPONSORED_PROPERTY)
    INSERT INTO advertisements (
        organization_id, title, description, image_url, link_url,
        ad_type, position, status, start_date, end_date,
        budget, daily_budget, cost_per_click, cost_per_impression,
        target_locations, target_property_types, target_transaction_types,
        impressions, clicks, conversions, total_spent,
        max_impressions_per_day, priority, active, created_by,
        created_at, updated_at
    ) VALUES (
        org5_id,
        'Studio meublé - Promenade des Anglais',
        'Studio 25m², entièrement meublé, vue mer. Idéal investissement locatif. Rendement 5.5%.',
        'https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=800&h=600&fit=crop',
        'https://nice-properties.fr/properties/456',
        'SPONSORED_PROPERTY',
        'INLINE',
        'ACTIVE',
        NOW() - INTERVAL '1 day',
        NOW() + INTERVAL '29 days',
        1200.00,
        50.00,
        0.50,
        1.50,
        '{"cities": ["Nice"], "postalCodes": ["06000", "06100", "06200"], "regions": ["Provence-Alpes-Côte d''Azur"]}',
        '["STUDIO", "APARTMENT"]',
        '["SALE", "RENT"]',
        1800,
        42,
        4,
        315.60,
        1500,
        5,
        true,
        admin_user_id,
        NOW() - INTERVAL '3 days',
        NOW()
    ) ON CONFLICT DO NOTHING;
    
    -- Annonce 11: DRAFT - Non publiée (Immobilier Paris)
    INSERT INTO advertisements (
        organization_id, title, description, image_url, link_url,
        ad_type, position, status, start_date, end_date,
        budget, daily_budget, cost_per_click, cost_per_impression,
        target_locations, target_property_types, target_transaction_types,
        impressions, clicks, conversions, total_spent,
        max_impressions_per_day, priority, active, created_by,
        created_at, updated_at
    ) VALUES (
        org1_id,
        'Nouvelle agence dans le 16ème arrondissement',
        'Venez découvrir notre nouvelle agence. Inauguration le 15 mars. Cocktail et animations prévus.',
        'https://images.unsplash.com/photo-1560518883-ce09059eeffa?w=1200&h=400&fit=crop',
        'https://paris-immobilier.fr/opening',
        'BANNER',
        'TOP',
        'DRAFT',
        NOW() + INTERVAL '10 days',
        NOW() + INTERVAL '40 days',
        3000.00,
        100.00,
        0.60,
        1.80,
        '{"cities": ["Paris"], "postalCodes": ["75016"], "regions": ["Île-de-France"]}',
        '[]',
        '[]',
        0,
        0,
        0,
        0.00,
        2000,
        2,
        true,
        admin_user_id,
        NOW() - INTERVAL '5 days',
        NOW() - INTERVAL '2 days'
    ) ON CONFLICT DO NOTHING;
    
    -- Annonce 12: EXPIRED - Expirée (Real Estate Lyon)
    INSERT INTO advertisements (
        organization_id, title, description, image_url, link_url,
        ad_type, position, status, start_date, end_date,
        budget, daily_budget, cost_per_click, cost_per_impression,
        target_locations, target_property_types, target_transaction_types,
        impressions, clicks, conversions, total_spent,
        max_impressions_per_day, priority, active, created_by,
        created_at, updated_at
    ) VALUES (
        org2_id,
        'Promotion Black Friday - Frais réduits',
        'Profitez de nos frais d''agence réduits de 50% pour le Black Friday. Offre limitée !',
        'https://images.unsplash.com/photo-1600585154340-be6161a56a0c?w=1200&h=400&fit=crop',
        'https://lyon-realestate.fr/black-friday',
        'BANNER',
        'TOP',
        'EXPIRED',
        NOW() - INTERVAL '60 days',
        NOW() - INTERVAL '30 days',
        4000.00,
        200.00,
        0.80,
        2.20,
        '{"cities": ["Lyon"], "postalCodes": ["69001", "69002", "69003", "69004", "69005", "69006", "69007", "69008", "69009"], "regions": ["Auvergne-Rhône-Alpes"]}',
        '["APARTMENT", "HOUSE"]',
        '["SALE", "RENT"]',
        25000,
        580,
        45,
        3850.00,
        5000,
        8,
        false,
        admin_user_id,
        NOW() - INTERVAL '65 days',
        NOW() - INTERVAL '30 days'
    ) ON CONFLICT DO NOTHING;
    
END $$;

-- =====================================================
-- 14.7. CRÉER DES IMPRESSIONS ET CLICS D'ANNONCES (JEUX DE DONNÉES)
-- =====================================================

DO $$
DECLARE
    ad1_id BIGINT;
    ad2_id BIGINT;
    ad3_id BIGINT;
    ad4_id BIGINT;
    ad5_id BIGINT;
    prop1_id BIGINT;
    prop2_id BIGINT;
    prop3_id BIGINT;
BEGIN
    -- Récupérer les IDs des annonces actives
    SELECT id INTO ad1_id FROM advertisements WHERE title = 'Découvrez nos biens d''exception à Paris' LIMIT 1;
    SELECT id INTO ad2_id FROM advertisements WHERE title = 'Investissement locatif à Lyon' LIMIT 1;
    SELECT id INTO ad3_id FROM advertisements WHERE title = 'Villas de prestige sur la Côte d''Azur' LIMIT 1;
    SELECT id INTO ad4_id FROM advertisements WHERE title = 'Appartement T3 rénové - Centre-ville Bordeaux' LIMIT 1;
    SELECT id INTO ad5_id FROM advertisements WHERE title = 'Location saisonnière à Nice' LIMIT 1;
    
    -- Récupérer quelques IDs de propriétés
    SELECT id INTO prop1_id FROM properties LIMIT 1 OFFSET 0;
    SELECT id INTO prop2_id FROM properties LIMIT 1 OFFSET 5;
    SELECT id INTO prop3_id FROM properties LIMIT 1 OFFSET 10;
    
    -- Insérer des impressions pour l'annonce 1 (Paris)
    IF ad1_id IS NOT NULL THEN
        -- Impressions des 7 derniers jours
        FOR i IN 1..150 LOOP
            INSERT INTO advertisement_impressions (
                advertisement_id, ip_address, user_agent, property_id, page_type,
                city, postal_code, impressed_at
            ) VALUES (
                ad1_id,
                '192.168.1.' || (100 + (i % 50)),
                'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36',
                CASE WHEN i % 3 = 0 THEN prop1_id ELSE NULL END,
                CASE (i % 4) WHEN 0 THEN 'PROPERTY_DETAIL' WHEN 1 THEN 'PROPERTY_LIST' WHEN 2 THEN 'HOME' ELSE 'SEARCH' END,
                'Paris',
                '7500' || LPAD((1 + (i % 20))::TEXT, 1, '0'),
                NOW() - (RANDOM() * INTERVAL '7 days')
            );
        END LOOP;
        
        -- Clics pour l'annonce 1
        FOR i IN 1..35 LOOP
            INSERT INTO advertisement_clicks (
                advertisement_id, ip_address, user_agent, referrer, property_id,
                city, postal_code, clicked_at
            ) VALUES (
                ad1_id,
                '192.168.1.' || (100 + (i % 50)),
                'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36',
                'https://example.com/properties',
                CASE WHEN i % 3 = 0 THEN prop1_id ELSE NULL END,
                'Paris',
                '7500' || LPAD((1 + (i % 20))::TEXT, 1, '0'),
                NOW() - (RANDOM() * INTERVAL '7 days')
            );
        END LOOP;
    END IF;
    
    -- Insérer des impressions pour l'annonce 2 (Lyon)
    IF ad2_id IS NOT NULL THEN
        FOR i IN 1..120 LOOP
            INSERT INTO advertisement_impressions (
                advertisement_id, ip_address, user_agent, property_id, page_type,
                city, postal_code, impressed_at
            ) VALUES (
                ad2_id,
                '192.168.2.' || (100 + (i % 50)),
                'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36',
                CASE WHEN i % 4 = 0 THEN prop2_id ELSE NULL END,
                CASE (i % 4) WHEN 0 THEN 'PROPERTY_DETAIL' WHEN 1 THEN 'PROPERTY_LIST' WHEN 2 THEN 'HOME' ELSE 'SEARCH' END,
                'Lyon',
                '6900' || LPAD((1 + (i % 9))::TEXT, 1, '0'),
                NOW() - (RANDOM() * INTERVAL '5 days')
            ) ON CONFLICT DO NOTHING;
        END LOOP;
        
        -- Clics pour l'annonce 2
        FOR i IN 1..28 LOOP
            INSERT INTO advertisement_clicks (
                advertisement_id, ip_address, user_agent, referrer, property_id,
                city, postal_code, clicked_at
            ) VALUES (
                ad2_id,
                '192.168.2.' || (100 + (i % 50)),
                'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36',
                'https://example.com/investment',
                CASE WHEN i % 4 = 0 THEN prop2_id ELSE NULL END,
                'Lyon',
                '6900' || LPAD((1 + (i % 9))::TEXT, 1, '0'),
                NOW() - (RANDOM() * INTERVAL '5 days')
            ) ON CONFLICT DO NOTHING;
        END LOOP;
    END IF;
    
    -- Insérer des impressions pour l'annonce 3 (Marseille)
    IF ad3_id IS NOT NULL THEN
        FOR i IN 1..90 LOOP
            INSERT INTO advertisement_impressions (
                advertisement_id, ip_address, user_agent, property_id, page_type,
                city, postal_code, impressed_at
            ) VALUES (
                ad3_id,
                '192.168.3.' || (100 + (i % 50)),
                'Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36',
                CASE WHEN i % 5 = 0 THEN prop3_id ELSE NULL END,
                CASE (i % 4) WHEN 0 THEN 'PROPERTY_DETAIL' WHEN 1 THEN 'PROPERTY_LIST' WHEN 2 THEN 'HOME' ELSE 'SEARCH' END,
                'Marseille',
                '1300' || LPAD((1 + (i % 16))::TEXT, 2, '0'),
                NOW() - (RANDOM() * INTERVAL '4 days')
            ) ON CONFLICT DO NOTHING;
        END LOOP;
        
        -- Clics pour l'annonce 3
        FOR i IN 1..20 LOOP
            INSERT INTO advertisement_clicks (
                advertisement_id, ip_address, user_agent, referrer, property_id,
                city, postal_code, clicked_at
            ) VALUES (
                ad3_id,
                '192.168.3.' || (100 + (i % 50)),
                'Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36',
                'https://example.com/villas',
                CASE WHEN i % 5 = 0 THEN prop3_id ELSE NULL END,
                'Marseille',
                '1300' || LPAD((1 + (i % 16))::TEXT, 2, '0'),
                NOW() - (RANDOM() * INTERVAL '4 days')
            ) ON CONFLICT DO NOTHING;
        END LOOP;
    END IF;
    
    -- Insérer des impressions pour l'annonce 4 (Bordeaux)
    IF ad4_id IS NOT NULL THEN
        FOR i IN 1..70 LOOP
            INSERT INTO advertisement_impressions (
                advertisement_id, ip_address, user_agent, property_id, page_type,
                city, postal_code, impressed_at
            ) VALUES (
                ad4_id,
                '192.168.4.' || (100 + (i % 50)),
                'Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X) AppleWebKit/605.1.15',
                CASE WHEN i % 6 = 0 THEN prop1_id ELSE NULL END,
                CASE (i % 4) WHEN 0 THEN 'PROPERTY_DETAIL' WHEN 1 THEN 'PROPERTY_LIST' WHEN 2 THEN 'HOME' ELSE 'SEARCH' END,
                'Bordeaux',
                '33000',
                NOW() - (RANDOM() * INTERVAL '3 days')
            ) ON CONFLICT DO NOTHING;
        END LOOP;
        
        -- Clics pour l'annonce 4
        FOR i IN 1..12 LOOP
            INSERT INTO advertisement_clicks (
                advertisement_id, ip_address, user_agent, referrer, property_id,
                city, postal_code, clicked_at
            ) VALUES (
                ad4_id,
                '192.168.4.' || (100 + (i % 50)),
                'Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X) AppleWebKit/605.1.15',
                'https://example.com/properties/123',
                CASE WHEN i % 6 = 0 THEN prop1_id ELSE NULL END,
                'Bordeaux',
                '33000',
                NOW() - (RANDOM() * INTERVAL '3 days')
            ) ON CONFLICT DO NOTHING;
        END LOOP;
    END IF;
    
    -- Insérer des impressions pour l'annonce 5 (Nice)
    IF ad5_id IS NOT NULL THEN
        FOR i IN 1..200 LOOP
            INSERT INTO advertisement_impressions (
                advertisement_id, ip_address, user_agent, property_id, page_type,
                city, postal_code, impressed_at
            ) VALUES (
                ad5_id,
                '192.168.5.' || (100 + (i % 50)),
                'Mozilla/5.0 (Android 11; Mobile; rv:89.0) Gecko/89.0 Firefox/89.0',
                CASE WHEN i % 7 = 0 THEN prop2_id ELSE NULL END,
                CASE (i % 4) WHEN 0 THEN 'PROPERTY_DETAIL' WHEN 1 THEN 'PROPERTY_LIST' WHEN 2 THEN 'HOME' ELSE 'SEARCH' END,
                'Nice',
                '06000',
                NOW() - (RANDOM() * INTERVAL '10 days')
            ) ON CONFLICT DO NOTHING;
        END LOOP;
        
        -- Clics pour l'annonce 5
        FOR i IN 1..45 LOOP
            INSERT INTO advertisement_clicks (
                advertisement_id, ip_address, user_agent, referrer, property_id,
                city, postal_code, clicked_at
            ) VALUES (
                ad5_id,
                '192.168.5.' || (100 + (i % 50)),
                'Mozilla/5.0 (Android 11; Mobile; rv:89.0) Gecko/89.0 Firefox/89.0',
                'https://example.com/seasonal-rental',
                CASE WHEN i % 7 = 0 THEN prop2_id ELSE NULL END,
                'Nice',
                '06000',
                NOW() - (RANDOM() * INTERVAL '10 days')
            ) ON CONFLICT DO NOTHING;
        END LOOP;
    END IF;
    
END $$;

-- =====================================================
-- 15. CRÉER DES ABONNEMENTS AUX NOTIFICATIONS
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
-- 14.5. CRÉER DES ÉVÉNEMENTS DE PROPRIÉTÉS (TEST)
-- =====================================================

-- Générer des événements de test pour les propriétés publiées
-- Simule des vues, contacts, favoris et partages sur les 7 derniers jours
DO $$
DECLARE
    prop_rec RECORD;
    event_date DATE;
    i INTEGER;
    j INTEGER;
    user_id_val BIGINT;
    metadata_json JSONB;
    event_types TEXT[] := ARRAY['VIEW', 'CONTACT', 'FAVORITE', 'SHARE'];
BEGIN
    -- Parcourir les propriétés publiées
    FOR prop_rec IN 
        SELECT id FROM properties WHERE status IN ('PUBLISHED', 'AVAILABLE') AND active = true LIMIT 20
    LOOP
        -- Générer des événements pour les 7 derniers jours
        FOR i IN 0..6 LOOP
            event_date := CURRENT_DATE - i;
            
            -- Générer des vues (plus fréquentes - 5 à 15 par jour)
            FOR j IN 1..(5 + (RANDOM() * 10)::INTEGER) LOOP
                user_id_val := CASE WHEN RANDOM() > 0.3 THEN (RANDOM() * 20 + 1)::BIGINT ELSE NULL END;
                metadata_json := jsonb_build_object(
                    'source', CASE WHEN RANDOM() > 0.5 THEN 'frontend' ELSE 'public_api' END,
                    'device', CASE (RANDOM() * 3)::INTEGER 
                        WHEN 0 THEN 'desktop' 
                        WHEN 1 THEN 'mobile' 
                        ELSE 'tablet' 
                    END
                );
                
                INSERT INTO property_events (property_id, event_type, created_at, user_id, metadata)
                VALUES (
                    prop_rec.id,
                    'VIEW',
                    event_date + (RANDOM() * INTERVAL '1 day'),
                    user_id_val,
                    metadata_json
                ) ON CONFLICT DO NOTHING;
            END LOOP;
            
            -- Générer quelques contacts (moins fréquents - 10% de chance par jour)
            IF RANDOM() > 0.9 THEN
                user_id_val := (RANDOM() * 20 + 1)::BIGINT;
                metadata_json := jsonb_build_object(
                    'method', CASE WHEN RANDOM() > 0.5 THEN 'email' ELSE 'phone' END
                );
                
                INSERT INTO property_events (property_id, event_type, created_at, user_id, metadata)
                VALUES (
                    prop_rec.id,
                    'CONTACT',
                    event_date + (RANDOM() * INTERVAL '1 day'),
                    user_id_val,
                    metadata_json
                ) ON CONFLICT DO NOTHING;
            END IF;
            
            -- Générer quelques favoris (20% de chance par jour)
            IF RANDOM() > 0.8 THEN
                user_id_val := (RANDOM() * 20 + 1)::BIGINT;
                metadata_json := jsonb_build_object('source', 'favorites_panel');
                
                INSERT INTO property_events (property_id, event_type, created_at, user_id, metadata)
                VALUES (
                    prop_rec.id,
                    'FAVORITE',
                    event_date + (RANDOM() * INTERVAL '1 day'),
                    user_id_val,
                    metadata_json
                ) ON CONFLICT DO NOTHING;
            END IF;
            
            -- Générer quelques partages (5% de chance par jour)
            IF RANDOM() > 0.95 THEN
                user_id_val := (RANDOM() * 20 + 1)::BIGINT;
                metadata_json := jsonb_build_object(
                    'platform', CASE (RANDOM() * 4)::INTEGER 
                        WHEN 0 THEN 'facebook' 
                        WHEN 1 THEN 'twitter' 
                        WHEN 2 THEN 'linkedin' 
                        ELSE 'whatsapp' 
                    END
                );
                
                INSERT INTO property_events (property_id, event_type, created_at, user_id, metadata)
                VALUES (
                    prop_rec.id,
                    'SHARE',
                    event_date + (RANDOM() * INTERVAL '1 day'),
                    user_id_val,
                    metadata_json
                ) ON CONFLICT DO NOTHING;
            END IF;
        END LOOP;
    END LOOP;
    
    RAISE NOTICE 'Événements de propriétés générés avec succès';
END $$;

-- =====================================================
-- 15. RÉSUMÉ ET STATISTIQUES
-- =====================================================

DO $$
DECLARE
    user_count INTEGER;
    org_count INTEGER;
    team_count INTEGER;
    org_user_count INTEGER;
    prop_count INTEGER;
    prop_feature_count INTEGER;
    property_event_count INTEGER;
    role_count INTEGER;
    perm_count INTEGER;
    plan_count INTEGER;
    subscription_count INTEGER;
    invoice_count INTEGER;
    payment_count INTEGER;
    notification_count INTEGER;
    notification_subscription_count INTEGER;
    workflow_count INTEGER;
    task_count INTEGER;
    blog_post_count INTEGER;
    contact_message_count INTEGER;
    contact_message_new_count INTEGER;
    organization_contact_message_count INTEGER;
    organization_contact_message_new_count INTEGER;
    review_count INTEGER;
    review_approved_count INTEGER;
    review_pending_count INTEGER;
    organization_review_count INTEGER;
    organization_review_approved_count INTEGER;
    organization_review_pending_count INTEGER;
    price_history_count INTEGER;
    price_alert_count INTEGER;
    visit_appointment_count INTEGER;
    advertisement_count INTEGER;
    advertisement_active_count INTEGER;
    advertisement_impression_count INTEGER;
    advertisement_click_count INTEGER;
    country_count INTEGER;
    city_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO user_count FROM users;
    SELECT COUNT(*) INTO org_count FROM organizations;
    SELECT COUNT(*) INTO team_count FROM teams;
    SELECT COUNT(*) INTO org_user_count FROM organization_users;
    SELECT COUNT(*) INTO prop_count FROM properties;
    SELECT COUNT(*) INTO prop_feature_count FROM property_features;
    SELECT COUNT(*) INTO property_event_count FROM property_events;
    SELECT COUNT(*) INTO role_count FROM roles;
    SELECT COUNT(*) INTO perm_count FROM permissions;
    SELECT COUNT(*) INTO plan_count FROM plans;
    SELECT COUNT(*) INTO subscription_count FROM subscriptions;
    SELECT COUNT(*) INTO invoice_count FROM invoices;
    SELECT COUNT(*) INTO payment_count FROM payments;
    SELECT COUNT(*) INTO notification_count FROM notifications;
    SELECT COUNT(*) INTO notification_subscription_count FROM notification_subscriptions;
    SELECT COUNT(*) INTO workflow_count FROM approval_workflows;
    SELECT COUNT(*) INTO task_count FROM tasks;
    SELECT COUNT(*) INTO blog_post_count FROM blog_posts;
    SELECT COUNT(*) INTO contact_message_count FROM contact_messages WHERE active = true;
    SELECT COUNT(*) INTO contact_message_new_count FROM contact_messages WHERE active = true AND status = 'NEW';
    SELECT COUNT(*) INTO organization_contact_message_count FROM organization_contact_messages WHERE active = true;
    SELECT COUNT(*) INTO organization_contact_message_new_count FROM organization_contact_messages WHERE active = true AND status = 'NEW';
    SELECT COUNT(*) INTO review_count FROM reviews WHERE active = true;
    SELECT COUNT(*) INTO review_approved_count FROM reviews WHERE active = true AND status = 'APPROVED';
    SELECT COUNT(*) INTO review_pending_count FROM reviews WHERE active = true AND status = 'PENDING';
    SELECT COUNT(*) INTO organization_review_count FROM organization_reviews WHERE active = true;
    SELECT COUNT(*) INTO organization_review_approved_count FROM organization_reviews WHERE active = true AND status = 'APPROVED';
    SELECT COUNT(*) INTO organization_review_pending_count FROM organization_reviews WHERE active = true AND status = 'PENDING';
    SELECT COUNT(*) INTO price_history_count FROM price_history;
    SELECT COUNT(*) INTO price_alert_count FROM price_alerts WHERE active = true;
    SELECT COUNT(*) INTO visit_appointment_count FROM visit_appointments WHERE active = true;
    
    -- Statistiques des annonces
    SELECT COUNT(*) INTO advertisement_count FROM advertisements WHERE active = true;
    SELECT COUNT(*) INTO advertisement_active_count FROM advertisements WHERE active = true AND status = 'ACTIVE';
    SELECT COUNT(*) INTO advertisement_impression_count FROM advertisement_impressions;
    SELECT COUNT(*) INTO advertisement_click_count FROM advertisement_clicks;
    
    -- Statistiques des pays et villes
    SELECT COUNT(*) INTO country_count FROM countries WHERE active = true;
    SELECT COUNT(*) INTO city_count FROM cities WHERE active = true;
    
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
    RAISE NOTICE '   • Événements de propriétés: %', property_event_count;
    RAISE NOTICE '   • Plans d''abonnement: %', plan_count;
    RAISE NOTICE '   • Abonnements actifs: %', subscription_count;
    RAISE NOTICE '   • Factures: %', invoice_count;
    RAISE NOTICE '   • Paiements: %', payment_count;
    RAISE NOTICE '   • Notifications: %', notification_count;
    RAISE NOTICE '   • Abonnements aux notifications: %', notification_subscription_count;
    RAISE NOTICE '   • Workflows d''approbation: %', workflow_count;
    RAISE NOTICE '   • Tâches: %', task_count;
    RAISE NOTICE '   • Articles de blog: %', blog_post_count;
    RAISE NOTICE '   • Messages de contact: % (dont % non lus)', contact_message_count, contact_message_new_count;
           RAISE NOTICE '   • Messages de contact agences: % (dont % non lus)', organization_contact_message_count, organization_contact_message_new_count;
           
    RAISE NOTICE '   • Avis clients: % (dont % approuvés, % en attente)', review_count, review_approved_count, review_pending_count;
    RAISE NOTICE '   • Avis d''agences: % (dont % approuvés, % en attente)', organization_review_count, organization_review_approved_count, organization_review_pending_count;
    RAISE NOTICE '   • Historiques de prix: %', price_history_count;
    RAISE NOTICE '   • Alertes de prix: %', price_alert_count;
    RAISE NOTICE '   • Rendez-vous de visite: %', visit_appointment_count;
    RAISE NOTICE '   • Annonces publicitaires: % (dont % actives)', advertisement_count, advertisement_active_count;
    RAISE NOTICE '   • Impressions d''annonces: %', advertisement_impression_count;
    RAISE NOTICE '   • Clics sur annonces: %', advertisement_click_count;
    RAISE NOTICE '   • Pays actifs: %', country_count;
    RAISE NOTICE '   • Villes actives: %', city_count;
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

