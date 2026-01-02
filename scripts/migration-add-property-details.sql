-- Migration: Add detailed property fields
-- Description: Adds comprehensive property details fields to the properties table
-- Date: 2025-01-01

-- Interior & Bathrooms
ALTER TABLE properties 
ADD COLUMN IF NOT EXISTS full_bathrooms INTEGER;

-- Appliances
ALTER TABLE properties 
ADD COLUMN IF NOT EXISTS appliances_included TEXT,
ADD COLUMN IF NOT EXISTS laundry_location VARCHAR(50);

-- Interior area
ALTER TABLE properties 
ADD COLUMN IF NOT EXISTS total_structure_area NUMERIC(10, 2),
ADD COLUMN IF NOT EXISTS total_interior_livable_area NUMERIC(10, 2);

-- Video & virtual tour
ALTER TABLE properties 
ADD COLUMN IF NOT EXISTS virtual_tour_url VARCHAR(500);

-- Parking
ALTER TABLE properties 
ADD COLUMN IF NOT EXISTS parking_features TEXT,
ADD COLUMN IF NOT EXISTS has_garage BOOLEAN;

-- Accessibility
ALTER TABLE properties 
ADD COLUMN IF NOT EXISTS accessibility_features TEXT;

-- Features
ALTER TABLE properties 
ADD COLUMN IF NOT EXISTS patio_porch VARCHAR(200),
ADD COLUMN IF NOT EXISTS exterior_features TEXT;

-- Details
ALTER TABLE properties 
ADD COLUMN IF NOT EXISTS special_conditions VARCHAR(100);

-- Construction
ALTER TABLE properties 
ADD COLUMN IF NOT EXISTS home_type VARCHAR(50),
ADD COLUMN IF NOT EXISTS property_subtype VARCHAR(50),
ADD COLUMN IF NOT EXISTS condition VARCHAR(50),
ADD COLUMN IF NOT EXISTS year_built INTEGER;

-- Community & HOA
ALTER TABLE properties 
ADD COLUMN IF NOT EXISTS subdivision VARCHAR(200),
ADD COLUMN IF NOT EXISTS has_hoa BOOLEAN,
ADD COLUMN IF NOT EXISTS hoa_amenities TEXT,
ADD COLUMN IF NOT EXISTS hoa_services TEXT,
ADD COLUMN IF NOT EXISTS hoa_fee NUMERIC(10, 2),
ADD COLUMN IF NOT EXISTS hoa_fee_frequency VARCHAR(20);

-- Location
ALTER TABLE properties 
ADD COLUMN IF NOT EXISTS region VARCHAR(100);

-- Financial & listing details
ALTER TABLE properties 
ADD COLUMN IF NOT EXISTS price_per_square_foot NUMERIC(10, 2),
ADD COLUMN IF NOT EXISTS date_on_market DATE;

-- Add indexes for commonly queried fields
CREATE INDEX IF NOT EXISTS idx_property_year_built ON properties(year_built);
CREATE INDEX IF NOT EXISTS idx_property_home_type ON properties(home_type);
CREATE INDEX IF NOT EXISTS idx_property_region ON properties(region);
CREATE INDEX IF NOT EXISTS idx_property_date_on_market ON properties(date_on_market);
CREATE INDEX IF NOT EXISTS idx_property_has_hoa ON properties(has_hoa);

COMMENT ON COLUMN properties.full_bathrooms IS 'Number of full bathrooms';
COMMENT ON COLUMN properties.appliances_included IS 'JSON array of included appliances';
COMMENT ON COLUMN properties.laundry_location IS 'Laundry location: Inside, Outside, None';
COMMENT ON COLUMN properties.total_structure_area IS 'Total structure area in sqft or m²';
COMMENT ON COLUMN properties.total_interior_livable_area IS 'Total interior livable area in sqft or m²';
COMMENT ON COLUMN properties.virtual_tour_url IS 'URL to virtual tour';
COMMENT ON COLUMN properties.parking_features IS 'JSON array of parking features';
COMMENT ON COLUMN properties.has_garage IS 'Whether property has a garage';
COMMENT ON COLUMN properties.accessibility_features IS 'JSON array of accessibility features';
COMMENT ON COLUMN properties.patio_porch IS 'Patio and porch information';
COMMENT ON COLUMN properties.exterior_features IS 'JSON array of exterior features';
COMMENT ON COLUMN properties.special_conditions IS 'Special conditions: Resale, New Construction, etc.';
COMMENT ON COLUMN properties.home_type IS 'Home type: Condo, House, Townhouse, etc.';
COMMENT ON COLUMN properties.property_subtype IS 'Property subtype: Condominium, Single Family, etc.';
COMMENT ON COLUMN properties.condition IS 'Property condition: New, Good, Fair, etc.';
COMMENT ON COLUMN properties.year_built IS 'Year the property was built';
COMMENT ON COLUMN properties.subdivision IS 'Subdivision name';
COMMENT ON COLUMN properties.has_hoa IS 'Whether property has HOA';
COMMENT ON COLUMN properties.hoa_amenities IS 'JSON array of HOA amenities';
COMMENT ON COLUMN properties.hoa_services IS 'JSON array of HOA services';
COMMENT ON COLUMN properties.hoa_fee IS 'HOA fee amount';
COMMENT ON COLUMN properties.hoa_fee_frequency IS 'HOA fee frequency: monthly, quarterly, annually';
COMMENT ON COLUMN properties.region IS 'Region or district name';
COMMENT ON COLUMN properties.price_per_square_foot IS 'Price per square foot';
COMMENT ON COLUMN properties.date_on_market IS 'Date property was listed on market';

