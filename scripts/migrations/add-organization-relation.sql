-- =====================================================
-- Migration: Ajouter la relation Organization-User dans identity-service
-- =====================================================

-- 1. Créer la table organizations si elle n'existe pas
CREATE TABLE IF NOT EXISTS organizations (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(500),
    domain VARCHAR(100),
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2. Créer l'index sur le nom
CREATE INDEX IF NOT EXISTS idx_org_name ON organizations(name);

-- 3. Ajouter la colonne organization_id à la table users
ALTER TABLE users 
ADD COLUMN IF NOT EXISTS organization_id BIGINT;

-- 4. Créer la contrainte de clé étrangère
ALTER TABLE users 
ADD CONSTRAINT fk_user_organization 
FOREIGN KEY (organization_id) REFERENCES organizations(id) 
ON DELETE SET NULL;

-- 5. Créer l'index pour améliorer les performances
CREATE INDEX IF NOT EXISTS idx_user_organization ON users(organization_id);

-- 6. Commentaires
COMMENT ON COLUMN users.organization_id IS 'Référence à l''organisation principale de l''utilisateur';
COMMENT ON TABLE organizations IS 'Organisations dans identity-service';

-- =====================================================
-- Vérification
-- =====================================================

DO $$
BEGIN
    RAISE NOTICE '✅ Migration terminée avec succès!';
    RAISE NOTICE '📊 Vérification:';
    RAISE NOTICE '   - Table organizations: %', (SELECT COUNT(*) FROM information_schema.tables WHERE table_name = 'organizations');
    RAISE NOTICE '   - Colonne organization_id: %', (SELECT COUNT(*) FROM information_schema.columns WHERE table_name = 'users' AND column_name = 'organization_id');
    RAISE NOTICE '   - Contrainte FK: %', (SELECT COUNT(*) FROM information_schema.table_constraints WHERE constraint_name = 'fk_user_organization');
END $$;

