-- =====================================================
-- Migration SQL: Ajout des champs de profil utilisateur
-- Date: Janvier 2025
-- Description: Ajoute les colonnes avatar_url, language, timezone, notification_preferences à la table users
-- =====================================================

-- Vérifier si les colonnes existent déjà avant de les ajouter
DO $$
BEGIN
    -- Ajouter avatar_url si elle n'existe pas
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'users' AND column_name = 'avatar_url'
    ) THEN
        ALTER TABLE users ADD COLUMN avatar_url VARCHAR(500);
        RAISE NOTICE 'Colonne avatar_url ajoutée';
    ELSE
        RAISE NOTICE 'Colonne avatar_url existe déjà';
    END IF;

    -- Ajouter language si elle n'existe pas
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'users' AND column_name = 'language'
    ) THEN
        ALTER TABLE users ADD COLUMN language VARCHAR(10) DEFAULT 'fr';
        RAISE NOTICE 'Colonne language ajoutée';
    ELSE
        RAISE NOTICE 'Colonne language existe déjà';
    END IF;

    -- Ajouter timezone si elle n'existe pas
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'users' AND column_name = 'timezone'
    ) THEN
        ALTER TABLE users ADD COLUMN timezone VARCHAR(50) DEFAULT 'Europe/Paris';
        RAISE NOTICE 'Colonne timezone ajoutée';
    ELSE
        RAISE NOTICE 'Colonne timezone existe déjà';
    END IF;

    -- Ajouter notification_preferences si elle n'existe pas
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'users' AND column_name = 'notification_preferences'
    ) THEN
        ALTER TABLE users ADD COLUMN notification_preferences VARCHAR(2000);
        RAISE NOTICE 'Colonne notification_preferences ajoutée';
    ELSE
        RAISE NOTICE 'Colonne notification_preferences existe déjà';
    END IF;
END $$;

-- Mettre à jour les utilisateurs existants avec des valeurs par défaut si NULL
UPDATE users 
SET 
    language = COALESCE(language, 'fr'),
    timezone = COALESCE(timezone, 'Europe/Paris'),
    notification_preferences = COALESCE(notification_preferences, '{"email": true, "push": true, "sms": false, "marketing": false}')
WHERE language IS NULL OR timezone IS NULL OR notification_preferences IS NULL;

RAISE NOTICE 'Migration terminée avec succès!';

