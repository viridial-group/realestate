-- Migration: Ajouter les champs inspirés de Zillow pour améliorer les détails des propriétés
-- Date: 2024
-- Description: Ajoute des champs pour les équipements, politiques, scores de quartier, etc.

-- Ajouter les nouveaux champs
ALTER TABLE properties
ADD COLUMN IF NOT EXISTS pet_friendly BOOLEAN DEFAULT FALSE,
ADD COLUMN IF NOT EXISTS special_offer VARCHAR(500),
ADD COLUMN IF NOT EXISTS office_hours TEXT,
ADD COLUMN IF NOT EXISTS neighborhood VARCHAR(100),
ADD COLUMN IF NOT EXISTS walk_score INTEGER,
ADD COLUMN IF NOT EXISTS transit_score INTEGER,
ADD COLUMN IF NOT EXISTS bike_score INTEGER,
ADD COLUMN IF NOT EXISTS building_name VARCHAR(200),
ADD COLUMN IF NOT EXISTS flooring TEXT,
ADD COLUMN IF NOT EXISTS unit_features TEXT,
ADD COLUMN IF NOT EXISTS building_amenities TEXT,
ADD COLUMN IF NOT EXISTS available_units TEXT,
ADD COLUMN IF NOT EXISTS pet_policy TEXT,
ADD COLUMN IF NOT EXISTS parking_policy VARCHAR(100);

-- Ajouter des index pour améliorer les performances de recherche
CREATE INDEX IF NOT EXISTS idx_property_pet_friendly ON properties(pet_friendly);
CREATE INDEX IF NOT EXISTS idx_property_neighborhood ON properties(neighborhood);
CREATE INDEX IF NOT EXISTS idx_property_building_name ON properties(building_name);

-- Commentaires pour documentation
COMMENT ON COLUMN properties.pet_friendly IS 'Indique si les animaux de compagnie sont acceptés';
COMMENT ON COLUMN properties.special_offer IS 'Offre spéciale (ex: "3 Months Free Rent")';
COMMENT ON COLUMN properties.office_hours IS 'Horaires du bureau au format JSON';
COMMENT ON COLUMN properties.neighborhood IS 'Nom du quartier (ex: "Downtown")';
COMMENT ON COLUMN properties.walk_score IS 'Score de marche (0-100)';
COMMENT ON COLUMN properties.transit_score IS 'Score de transport (0-100)';
COMMENT ON COLUMN properties.bike_score IS 'Score de vélo (0-100)';
COMMENT ON COLUMN properties.building_name IS 'Nom du bâtiment (ex: "7 Dekalb")';
COMMENT ON COLUMN properties.flooring IS 'Types de revêtements au format JSON array';
COMMENT ON COLUMN properties.unit_features IS 'Caractéristiques des unités au format JSON array';
COMMENT ON COLUMN properties.building_amenities IS 'Équipements du bâtiment au format JSON array';
COMMENT ON COLUMN properties.available_units IS 'Unités disponibles au format JSON array';
COMMENT ON COLUMN properties.pet_policy IS 'Politique des animaux au format JSON';
COMMENT ON COLUMN properties.parking_policy IS 'Politique de parking (ex: "None", "Garage", "Street")';

