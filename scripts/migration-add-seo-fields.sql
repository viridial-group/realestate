-- =====================================================
-- Migration: Ajouter les champs SEO aux propriétés
-- Date: 2024
-- Description: Ajoute meta_title, meta_description, meta_keywords, og_image
-- =====================================================

-- Ajouter les colonnes SEO si elles n'existent pas déjà
DO $$
BEGIN
    -- Meta Title
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'properties' AND column_name = 'meta_title'
    ) THEN
        ALTER TABLE properties 
        ADD COLUMN meta_title VARCHAR(255);
    END IF;

    -- Meta Description
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'properties' AND column_name = 'meta_description'
    ) THEN
        ALTER TABLE properties 
        ADD COLUMN meta_description VARCHAR(500);
    END IF;

    -- Meta Keywords
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'properties' AND column_name = 'meta_keywords'
    ) THEN
        ALTER TABLE properties 
        ADD COLUMN meta_keywords VARCHAR(500);
    END IF;

    -- OG Image
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'properties' AND column_name = 'og_image'
    ) THEN
        ALTER TABLE properties 
        ADD COLUMN og_image VARCHAR(500);
    END IF;
END $$;

-- Créer des index pour améliorer les performances de recherche
CREATE INDEX IF NOT EXISTS idx_property_meta_title ON properties(meta_title) WHERE meta_title IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_property_meta_keywords ON properties(meta_keywords) WHERE meta_keywords IS NOT NULL;

-- Commentaires pour documentation
COMMENT ON COLUMN properties.meta_title IS 'Titre SEO personnalisé pour les meta tags (optionnel, sinon utilise title)';
COMMENT ON COLUMN properties.meta_description IS 'Description SEO personnalisée pour les meta tags (optionnel, sinon utilise description)';
COMMENT ON COLUMN properties.meta_keywords IS 'Mots-clés SEO séparés par des virgules';
COMMENT ON COLUMN properties.og_image IS 'Image pour Open Graph (optionnel, sinon utilise première image)';

