-- ====================================
-- User Service - Database Schema
-- ====================================

-- Drop tables if exist
DROP TABLE IF EXISTS user_roles CASCADE;
DROP TABLE IF EXISTS role_permissions CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS roles CASCADE;
DROP TABLE IF EXISTS permissions CASCADE;
DROP TABLE IF EXISTS organizations CASCADE;

-- ====================================
-- 1️⃣ Organizations
-- ====================================
CREATE TABLE organizations (
    id UUID PRIMARY KEY,
    name TEXT NOT NULL,
    parent_id UUID REFERENCES organizations(id),
    version BIGINT NOT NULL DEFAULT 0
);

INSERT INTO organizations (id, name, parent_id, version) VALUES
('11111111-1111-1111-1111-111111111111', 'Groupe ABC', NULL, 0),
('11111111-aaaa-aaaa-aaaa-111111111111', 'ABC France', '11111111-1111-1111-1111-111111111111', 0),
('11111111-bbbb-bbbb-bbbb-111111111111', 'ABC Paris', '11111111-aaaa-aaaa-aaaa-111111111111', 0),
('11111111-cccc-cccc-cccc-111111111111', 'ABC Lyon', '11111111-aaaa-aaaa-aaaa-111111111111', 0),
('11111111-dddd-dddd-dddd-111111111111', 'ABC IT', '11111111-bbbb-bbbb-bbbb-111111111111', 0),
('22222222-2222-2222-2222-222222222222', 'Groupe XYZ', NULL, 0),
('22222222-aaaa-aaaa-aaaa-222222222222', 'XYZ Europe', '22222222-2222-2222-2222-222222222222', 0),
('22222222-bbbb-bbbb-bbbb-222222222222', 'XYZ Paris', '22222222-aaaa-aaaa-aaaa-222222222222', 0);

-- ====================================
-- 2️⃣ Permissions
-- ====================================
CREATE TABLE permissions (
    id UUID PRIMARY KEY,
    code TEXT NOT NULL,
    organization_id UUID REFERENCES organizations(id) NOT NULL
);

INSERT INTO permissions (id, code, organization_id) VALUES
-- Permissions User Management
('aaaaaaaa-1111-1111-1111-aaaaaaaa1111', 'USER_CREATE', '11111111-1111-1111-1111-111111111111'),
('aaaaaaaa-2222-2222-2222-aaaaaaaa2222', 'USER_READ', '11111111-1111-1111-1111-111111111111'),
('aaaaaaaa-3333-3333-3333-aaaaaaaa3333', 'ROLE_ASSIGN', '11111111-1111-1111-1111-111111111111'),
('aaaaaaaa-4444-4444-4444-aaaaaaaa4444', 'USER_UPDATE', '11111111-1111-1111-1111-111111111111'),
('aaaaaaaa-5555-5555-5555-aaaaaaaa5555', 'USER_DELETE', '11111111-1111-1111-1111-111111111111'),
('aaaaaaaa-6666-6666-6666-aaaaaaaa6666', 'ORGANIZATION_READ', '11111111-1111-1111-1111-111111111111'),
('aaaaaaaa-7777-7777-7777-aaaaaaaa7777', 'ORGANIZATION_CREATE', '11111111-1111-1111-1111-111111111111'),
('aaaaaaaa-8888-8888-8888-aaaaaaaa8888', 'ORGANIZATION_UPDATE', '11111111-1111-1111-1111-111111111111'),
('aaaaaaaa-9999-9999-9999-aaaaaaaa9999', 'ORGANIZATION_DELETE', '11111111-1111-1111-1111-111111111111');

-- ====================================
-- 3️⃣ Roles
-- ====================================
CREATE TABLE roles (
    id UUID PRIMARY KEY,
    name TEXT NOT NULL,
    organization_id UUID REFERENCES organizations(id),
    parent_role_id UUID REFERENCES roles(id)
);

INSERT INTO roles (id, name, organization_id, parent_role_id) VALUES
('cccccccc-1111-1111-1111-cccccccc1111', 'SUPER_ADMIN', '11111111-1111-1111-1111-111111111111', NULL),
('cccccccc-2222-2222-2222-cccccccc2222', 'ADMIN', '11111111-1111-1111-1111-111111111111', 'cccccccc-1111-1111-1111-cccccccc1111'),
('cccccccc-3333-3333-3333-cccccccc3333', 'MANAGER', '11111111-1111-1111-1111-111111111111', 'cccccccc-2222-2222-2222-cccccccc2222'),
('cccccccc-4444-4444-4444-cccccccc4444', 'USER', '11111111-1111-1111-1111-111111111111', 'cccccccc-3333-3333-3333-cccccccc3333'),
('cccccccc-5555-5555-5555-cccccccc5555', 'FRANCE_MANAGER', '11111111-aaaa-aaaa-aaaa-111111111111', 'cccccccc-3333-3333-3333-cccccccc3333'),
('cccccccc-6666-6666-6666-cccccccc6666', 'FRANCE_USER', '11111111-aaaa-aaaa-aaaa-111111111111', 'cccccccc-4444-4444-4444-cccccccc4444');

-- ====================================
-- 4️⃣ Role-Permissions
-- ====================================
CREATE TABLE role_permissions (
    role_id UUID REFERENCES roles(id),
    permission_id UUID REFERENCES permissions(id),
    PRIMARY KEY (role_id, permission_id)
);

INSERT INTO role_permissions (role_id, permission_id) VALUES
-- SUPER_ADMIN - Toutes les permissions
('cccccccc-1111-1111-1111-cccccccc1111', 'aaaaaaaa-1111-1111-1111-aaaaaaaa1111'),
('cccccccc-1111-1111-1111-cccccccc1111', 'aaaaaaaa-2222-2222-2222-aaaaaaaa2222'),
('cccccccc-1111-1111-1111-cccccccc1111', 'aaaaaaaa-3333-3333-3333-aaaaaaaa3333'),
('cccccccc-1111-1111-1111-cccccccc1111', 'aaaaaaaa-4444-4444-4444-aaaaaaaa4444'),
('cccccccc-1111-1111-1111-cccccccc1111', 'aaaaaaaa-5555-5555-5555-aaaaaaaa5555'),
('cccccccc-1111-1111-1111-cccccccc1111', 'aaaaaaaa-6666-6666-6666-aaaaaaaa6666'),
('cccccccc-1111-1111-1111-cccccccc1111', 'aaaaaaaa-7777-7777-7777-aaaaaaaa7777'),
('cccccccc-1111-1111-1111-cccccccc1111', 'aaaaaaaa-8888-8888-8888-aaaaaaaa8888'),
('cccccccc-1111-1111-1111-cccccccc1111', 'aaaaaaaa-9999-9999-9999-aaaaaaaa9999'),
-- ADMIN - Lecture User et toutes les permissions Organization
('cccccccc-2222-2222-2222-cccccccc2222', 'aaaaaaaa-2222-2222-2222-aaaaaaaa2222'),
('cccccccc-2222-2222-2222-cccccccc2222', 'aaaaaaaa-6666-6666-6666-aaaaaaaa6666'),
('cccccccc-2222-2222-2222-cccccccc2222', 'aaaaaaaa-7777-7777-7777-aaaaaaaa7777'),
('cccccccc-2222-2222-2222-cccccccc2222', 'aaaaaaaa-8888-8888-8888-aaaaaaaa8888'),
('cccccccc-2222-2222-2222-cccccccc2222', 'aaaaaaaa-9999-9999-9999-aaaaaaaa9999'),
-- MANAGER - Lecture User et Organization
('cccccccc-3333-3333-3333-cccccccc3333', 'aaaaaaaa-2222-2222-2222-aaaaaaaa2222'),
('cccccccc-3333-3333-3333-cccccccc3333', 'aaaaaaaa-6666-6666-6666-aaaaaaaa6666'),
-- USER - Lecture User et Organization
('cccccccc-4444-4444-4444-cccccccc4444', 'aaaaaaaa-2222-2222-2222-aaaaaaaa2222'),
('cccccccc-4444-4444-4444-cccccccc4444', 'aaaaaaaa-6666-6666-6666-aaaaaaaa6666');

-- ====================================
-- 5️⃣ Users
-- ====================================
CREATE TABLE users (
    id UUID PRIMARY KEY,
    email TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    organization_id UUID REFERENCES organizations(id) NOT NULL,
    preferred_language VARCHAR(10) DEFAULT 'fr',
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_user_organization_id ON users(organization_id);
CREATE INDEX idx_user_email ON users(email);

-- Password: password123 (hash: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy)
INSERT INTO users (id, email, password, organization_id, preferred_language, version) VALUES
('eeeeeeee-1111-1111-1111-eeeeeeee1111', 'superadmin@abc.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '11111111-1111-1111-1111-111111111111', 'fr', 0),
('eeeeeeee-2222-2222-2222-eeeeeeee2222', 'admin@abc.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '11111111-aaaa-aaaa-aaaa-111111111111', 'fr', 0),
('eeeeeeee-3333-3333-3333-eeeeeeee3333', 'manager@abc.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '11111111-bbbb-bbbb-bbbb-111111111111', 'en', 0),
('eeeeeeee-4444-4444-4444-eeeeeeee4444', 'user@abc.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '11111111-cccc-cccc-cccc-111111111111', 'es', 0);

-- ====================================
-- 6️⃣ User-Roles
-- ====================================
CREATE TABLE user_roles (
    user_id UUID REFERENCES users(id),
    role_id UUID REFERENCES roles(id),
    PRIMARY KEY (user_id, role_id)
);

INSERT INTO user_roles (user_id, role_id) VALUES
('eeeeeeee-1111-1111-1111-eeeeeeee1111', 'cccccccc-1111-1111-1111-cccccccc1111'), -- superadmin@abc.com -> SUPER_ADMIN
('eeeeeeee-2222-2222-2222-eeeeeeee2222', 'cccccccc-2222-2222-2222-cccccccc2222'), -- admin@abc.com -> ADMIN
('eeeeeeee-3333-3333-3333-eeeeeeee3333', 'cccccccc-3333-3333-3333-cccccccc3333'), -- manager@abc.com -> MANAGER
('eeeeeeee-4444-4444-4444-eeeeeeee4444', 'cccccccc-4444-4444-4444-cccccccc4444'); -- user@abc.com -> USER

