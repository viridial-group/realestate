-- =====================================================
-- Migration SQL: Ajout des champs de paramètres d'organisation
-- Date: Janvier 2025
-- Description: Ajoute les colonnes logo_url, address, city, postal_code, country, phone, email, custom_domains, quotas à la table organizations
-- =====================================================

-- Vérifier si les colonnes existent déjà avant de les ajouter
DO $$
BEGIN
    -- Ajouter logo_url si elle n'existe pas
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'organizations' AND column_name = 'logo_url'
    ) THEN
        ALTER TABLE organizations ADD COLUMN logo_url VARCHAR(500);
        RAISE NOTICE 'Colonne logo_url ajoutée';
    ELSE
        RAISE NOTICE 'Colonne logo_url existe déjà';
    END IF;

    -- Ajouter address si elle n'existe pas
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'organizations' AND column_name = 'address'
    ) THEN
        ALTER TABLE organizations ADD COLUMN address VARCHAR(255);
        RAISE NOTICE 'Colonne address ajoutée';
    ELSE
        RAISE NOTICE 'Colonne address existe déjà';
    END IF;

    -- Ajouter city si elle n'existe pas
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'organizations' AND column_name = 'city'
    ) THEN
        ALTER TABLE organizations ADD COLUMN city VARCHAR(100);
        RAISE NOTICE 'Colonne city ajoutée';
    ELSE
        RAISE NOTICE 'Colonne city existe déjà';
    END IF;

    -- Ajouter postal_code si elle n'existe pas
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'organizations' AND column_name = 'postal_code'
    ) THEN
        ALTER TABLE organizations ADD COLUMN postal_code VARCHAR(20);
        RAISE NOTICE 'Colonne postal_code ajoutée';
    ELSE
        RAISE NOTICE 'Colonne postal_code existe déjà';
    END IF;

    -- Ajouter country si elle n'existe pas
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'organizations' AND column_name = 'country'
    ) THEN
        ALTER TABLE organizations ADD COLUMN country VARCHAR(100);
        RAISE NOTICE 'Colonne country ajoutée';
    ELSE
        RAISE NOTICE 'Colonne country existe déjà';
    END IF;

    -- Ajouter phone si elle n'existe pas
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'organizations' AND column_name = 'phone'
    ) THEN
        ALTER TABLE organizations ADD COLUMN phone VARCHAR(20);
        RAISE NOTICE 'Colonne phone ajoutée';
    ELSE
        RAISE NOTICE 'Colonne phone existe déjà';
    END IF;

    -- Ajouter email si elle n'existe pas
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'organizations' AND column_name = 'email'
    ) THEN
        ALTER TABLE organizations ADD COLUMN email VARCHAR(255);
        RAISE NOTICE 'Colonne email ajoutée';
    ELSE
        RAISE NOTICE 'Colonne email existe déjà';
    END IF;

    -- Ajouter custom_domains si elle n'existe pas
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'organizations' AND column_name = 'custom_domains'
    ) THEN
        ALTER TABLE organizations ADD COLUMN custom_domains JSONB;
        RAISE NOTICE 'Colonne custom_domains ajoutée';
    ELSE
        RAISE NOTICE 'Colonne custom_domains existe déjà';
    END IF;

    -- Ajouter quotas si elle n'existe pas
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'organizations' AND column_name = 'quotas'
    ) THEN
        ALTER TABLE organizations ADD COLUMN quotas JSONB;
        RAISE NOTICE 'Colonne quotas ajoutée';
    ELSE
        RAISE NOTICE 'Colonne quotas existe déjà';
    END IF;
END $$;

RAISE NOTICE 'Migration terminée avec succès!';

