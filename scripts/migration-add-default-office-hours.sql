-- =====================================================
-- Migration: Ajouter les horaires du bureau par défaut aux organisations
-- Date: 2024
-- Description: Ajoute default_office_hours pour automatiser les horaires des propriétés
-- =====================================================

-- Ajouter la colonne default_office_hours si elle n'existe pas déjà
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'organizations' AND column_name = 'default_office_hours'
    ) THEN
        ALTER TABLE organizations 
        ADD COLUMN default_office_hours TEXT;
    END IF;
END $$;

-- Initialiser les horaires par défaut pour les organisations existantes qui n'en ont pas
UPDATE organizations
SET default_office_hours = '{"monday": "9:00-18:00", "tuesday": "9:00-18:00", "wednesday": "9:00-18:00", "thursday": "9:00-18:00", "friday": "9:00-18:00", "saturday": "10:00-16:00", "sunday": "closed"}'
WHERE default_office_hours IS NULL OR default_office_hours = '';

-- Commentaire pour documentation
COMMENT ON COLUMN organizations.default_office_hours IS 'Horaires du bureau par défaut au format JSON. Utilisés automatiquement pour les nouvelles propriétés si non spécifiés. Format: {"monday": "9:00-18:00", "tuesday": "9:00-18:00", ...}';

