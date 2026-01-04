-- Migration: Ajouter le champ slug pour les URLs SEO-friendly
-- Date: 2024-01-03

-- Ajouter la colonne slug si elle n'existe pas
DO $$ 
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'properties' 
        AND column_name = 'slug'
    ) THEN
        ALTER TABLE properties 
        ADD COLUMN slug VARCHAR(255);
        
        -- Commentaire pour documenter la colonne
        COMMENT ON COLUMN properties.slug IS 'Slug SEO-friendly pour les URLs (ex: appartement-paris-3-pieces-123)';
        
        -- Créer un index unique pour le slug
        CREATE UNIQUE INDEX IF NOT EXISTS idx_property_slug ON properties(slug) 
        WHERE slug IS NOT NULL;
        
        -- Créer un index pour améliorer les performances de recherche
        CREATE INDEX IF NOT EXISTS idx_property_slug_lookup ON properties(slug) 
        WHERE slug IS NOT NULL;
    END IF;
END $$;

-- Générer les slugs pour les propriétés existantes (optionnel)
-- Cette requête peut être exécutée séparément si nécessaire
-- UPDATE properties 
-- SET slug = LOWER(
--     REGEXP_REPLACE(
--         REGEXP_REPLACE(
--             REGEXP_REPLACE(
--                 CONCAT(COALESCE(type, ''), '-', COALESCE(city, ''), '-', COALESCE(bedrooms::text, ''), '-pieces-', id::text),
--                 '[^a-z0-9-]', '', 'g'
--             ),
--             '-+', '-', 'g'
--         ),
--         '^-|-$', '', 'g'
--     )
-- )
-- WHERE slug IS NULL;

