-- Migration: Ajouter le champ transaction_type pour distinguer Location et Vente
-- Date: 2024-01-03

-- Ajouter la colonne transaction_type si elle n'existe pas
DO $$ 
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'properties' 
        AND column_name = 'transaction_type'
    ) THEN
        ALTER TABLE properties 
        ADD COLUMN transaction_type VARCHAR(50);
        
        -- Commentaire pour documenter la colonne
        COMMENT ON COLUMN properties.transaction_type IS 'Type de transaction: RENT (Location) ou SALE (Vente)';
    END IF;
END $$;

-- Optionnel: Mettre à jour les valeurs existantes basées sur le statut
-- Si status = RENTED → transaction_type = RENT
-- Si status = SOLD → transaction_type = SALE
-- Sinon, laisser NULL (sera déterminé par défaut dans l'application)
UPDATE properties 
SET transaction_type = 'RENT' 
WHERE status = 'RENTED' AND transaction_type IS NULL;

UPDATE properties 
SET transaction_type = 'SALE' 
WHERE status = 'SOLD' AND transaction_type IS NULL;

-- Créer un index pour améliorer les performances de recherche
CREATE INDEX IF NOT EXISTS idx_property_transaction_type ON properties(transaction_type) 
WHERE transaction_type IS NOT NULL;

