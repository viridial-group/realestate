-- Script SQL pour créer les tables countries et cities
-- Ce script doit être exécuté dans la base de données de identity-service

-- Table des pays
CREATE TABLE IF NOT EXISTS countries (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(2) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    code3 VARCHAR(3),
    phone_code VARCHAR(10),
    currency VARCHAR(10),
    currency_symbol VARCHAR(10),
    timezone VARCHAR(50),
    flag_emoji TEXT,
    description TEXT,
    latitude DECIMAL(10, 7),
    longitude DECIMAL(10, 7),
    important_data JSONB,
    active BOOLEAN NOT NULL DEFAULT true,
    display_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Index pour les pays
CREATE INDEX IF NOT EXISTS idx_country_code ON countries(code);
CREATE INDEX IF NOT EXISTS idx_country_name ON countries(name);
CREATE INDEX IF NOT EXISTS idx_country_active ON countries(active);

-- Table des villes
CREATE TABLE IF NOT EXISTS cities (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    postal_code VARCHAR(20),
    country_id BIGINT NOT NULL,
    latitude DECIMAL(10, 7),
    longitude DECIMAL(10, 7),
    region VARCHAR(100),
    department VARCHAR(100),
    timezone VARCHAR(50),
    description TEXT,
    important_data JSONB,
    active BOOLEAN NOT NULL DEFAULT true,
    display_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_city_country FOREIGN KEY (country_id) REFERENCES countries(id) ON DELETE CASCADE
);

-- Index pour les villes
CREATE INDEX IF NOT EXISTS idx_city_name ON cities(name);
CREATE INDEX IF NOT EXISTS idx_city_country ON cities(country_id);
CREATE INDEX IF NOT EXISTS idx_city_postal_code ON cities(postal_code);
CREATE INDEX IF NOT EXISTS idx_city_active ON cities(active);

-- Commentaires
COMMENT ON TABLE countries IS 'Table des pays pour la configuration SaaS';
COMMENT ON TABLE cities IS 'Table des villes pour la configuration SaaS';
COMMENT ON COLUMN countries.code IS 'Code ISO 3166-1 alpha-2 (ex: FR, US)';
COMMENT ON COLUMN countries.code3 IS 'Code ISO 3166-1 alpha-3 (ex: FRA, USA)';
COMMENT ON COLUMN countries.important_data IS 'Données importantes au format JSON (population, superficie, etc.)';
COMMENT ON COLUMN cities.important_data IS 'Données importantes au format JSON (population, densité, attractions, etc.)';

-- =====================================================
-- INSERTION DES DONNÉES DE TEST
-- =====================================================

-- Pays
INSERT INTO countries (code, name, code3, phone_code, currency, currency_symbol, timezone, flag_emoji, description, latitude, longitude, important_data, active, display_order) VALUES
('FR', 'France', 'FRA', '+33', 'EUR', '€', 'Europe/Paris', '🇫🇷', 'République française, pays d''Europe occidentale', 46.603354, 1.888334, '{"population": 67897000, "area": 643801, "languages": ["fr"], "capital": "Paris", "gdp": 2937.5}', true, 1),
('BE', 'Belgique', 'BEL', '+32', 'EUR', '€', 'Europe/Brussels', '🇧🇪', 'Royaume de Belgique, pays d''Europe occidentale', 50.503887, 4.469936, '{"population": 11623000, "area": 30528, "languages": ["nl", "fr", "de"], "capital": "Bruxelles", "gdp": 529.6}', true, 2),
('CH', 'Suisse', 'CHE', '+41', 'CHF', 'CHF', 'Europe/Zurich', '🇨🇭', 'Confédération suisse, pays d''Europe centrale', 46.818188, 8.227512, '{"population": 8703000, "area": 41285, "languages": ["de", "fr", "it", "rm"], "capital": "Berne", "gdp": 812.9}', true, 3),
('LU', 'Luxembourg', 'LUX', '+352', 'EUR', '€', 'Europe/Luxembourg', '🇱🇺', 'Grand-Duché de Luxembourg', 49.815273, 6.129583, '{"population": 640000, "area": 2586, "languages": ["lb", "fr", "de"], "capital": "Luxembourg", "gdp": 85.1}', true, 4),
('MC', 'Monaco', 'MCO', '+377', 'EUR', '€', 'Europe/Monaco', '🇲🇨', 'Principauté de Monaco', 43.738418, 7.424616, '{"population": 39000, "area": 2.02, "languages": ["fr"], "capital": "Monaco", "gdp": 7.2}', true, 5),
('ES', 'Espagne', 'ESP', '+34', 'EUR', '€', 'Europe/Madrid', '🇪🇸', 'Royaume d''Espagne', 40.463667, -3.74922, '{"population": 47431000, "area": 505990, "languages": ["es"], "capital": "Madrid", "gdp": 1427.0}', true, 6),
('IT', 'Italie', 'ITA', '+39', 'EUR', '€', 'Europe/Rome', '🇮🇹', 'République italienne', 41.87194, 12.56738, '{"population": 58853000, "area": 301340, "languages": ["it"], "capital": "Rome", "gdp": 2107.7}', true, 7),
('PT', 'Portugal', 'PRT', '+351', 'EUR', '€', 'Europe/Lisbon', '🇵🇹', 'République portugaise', 39.399872, -8.224454, '{"population": 10277000, "area": 92090, "languages": ["pt"], "capital": "Lisbonne", "gdp": 251.2}', true, 8),
('DE', 'Allemagne', 'DEU', '+49', 'EUR', '€', 'Europe/Berlin', '🇩🇪', 'République fédérale d''Allemagne', 51.165691, 10.451526, '{"population": 83240000, "area": 357022, "languages": ["de"], "capital": "Berlin", "gdp": 4223.1}', true, 9),
('GB', 'Royaume-Uni', 'GBR', '+44', 'GBP', '£', 'Europe/London', '🇬🇧', 'Royaume-Uni de Grande-Bretagne et d''Irlande du Nord', 55.378051, -3.435973, '{"population": 67330000, "area": 243610, "languages": ["en"], "capital": "Londres", "gdp": 3108.4}', true, 10),
('US', 'États-Unis', 'USA', '+1', 'USD', '$', 'America/New_York', '🇺🇸', 'États-Unis d''Amérique', 37.09024, -95.712891, '{"population": 331900000, "area": 9833517, "languages": ["en"], "capital": "Washington", "gdp": 25463.7}', true, 11),
('CA', 'Canada', 'CAN', '+1', 'CAD', 'C$', 'America/Toronto', '🇨🇦', 'Canada', 56.130366, -106.346771, '{"population": 38250000, "area": 9984670, "languages": ["en", "fr"], "capital": "Ottawa", "gdp": 2139.8}', true, 12),
('MA', 'Maroc', 'MAR', '+212', 'MAD', 'د.م.', 'Africa/Casablanca', '🇲🇦', 'Royaume du Maroc', 31.791702, -7.09262, '{"population": 37478000, "area": 446550, "languages": ["ar", "fr"], "capital": "Rabat", "gdp": 133.1}', true, 13),
('TN', 'Tunisie', 'TUN', '+216', 'TND', 'د.ت', 'Africa/Tunis', '🇹🇳', 'République tunisienne', 33.886917, 9.537499, '{"population": 12046000, "area": 163610, "languages": ["ar", "fr"], "capital": "Tunis", "gdp": 46.7}', true, 14),
('DZ', 'Algérie', 'DZA', '+213', 'DZD', 'د.ج', 'Africa/Algiers', '🇩🇿', 'République algérienne démocratique et populaire', 28.033886, 1.659626, '{"population": 44616000, "area": 2381741, "languages": ["ar", "fr"], "capital": "Alger", "gdp": 191.0}', true, 15)
ON CONFLICT (code) DO NOTHING;

-- Villes françaises
INSERT INTO cities (name, postal_code, country_id, latitude, longitude, region, department, timezone, description, important_data, active, display_order)
SELECT 
  c.name,
  c.postal_code,
  (SELECT id FROM countries WHERE code = 'FR') as country_id,
  c.latitude,
  c.longitude,
  c.region,
  c.department,
  'Europe/Paris' as timezone,
  c.description,
  c.important_data,
  true,
  c.display_order
FROM (VALUES
  ('Paris', '75001', 48.856614, 2.352222, 'Île-de-France', 'Paris', 'Capitale de la France, ville la plus peuplée', '{"population": 2161000, "area": 105.4, "density": 20499, "tourist_attractions": ["Tour Eiffel", "Louvre", "Notre-Dame", "Arc de Triomphe"]}', 1),
  ('Lyon', '69001', 45.764043, 4.835659, 'Auvergne-Rhône-Alpes', 'Rhône', 'Deuxième plus grande ville de France', '{"population": 522000, "area": 47.87, "density": 10900, "tourist_attractions": ["Basilique Notre-Dame de Fourvière", "Vieux Lyon", "Parc de la Tête d''Or"]}', 2),
  ('Marseille', '13001', 43.296482, 5.36978, 'Provence-Alpes-Côte d''Azur', 'Bouches-du-Rhône', 'Plus grand port de France', '{"population": 870000, "area": 240.62, "density": 3615, "tourist_attractions": ["Vieux-Port", "Notre-Dame de la Garde", "Calanques"]}', 3),
  ('Toulouse', '31000', 43.604652, 1.444209, 'Occitanie', 'Haute-Garonne', 'Ville rose, capitale de l''aéronautique', '{"population": 498000, "area": 118.3, "density": 4208, "tourist_attractions": ["Capitole", "Basilique Saint-Sernin", "Cité de l''espace"]}', 4),
  ('Nice', '06000', 43.710173, 7.261953, 'Provence-Alpes-Côte d''Azur', 'Alpes-Maritimes', 'Capitale de la Côte d''Azur', '{"population": 342000, "area": 71.92, "density": 4757, "tourist_attractions": ["Promenade des Anglais", "Vieille Ville", "Colline du Château"]}', 5),
  ('Nantes', '44000', 47.218371, -1.553621, 'Pays de la Loire', 'Loire-Atlantique', 'Capitale historique de la Bretagne', '{"population": 318000, "area": 65.19, "density": 4878, "tourist_attractions": ["Château des ducs de Bretagne", "Île de Nantes", "Machines de l''île"]}', 6),
  ('Strasbourg', '67000', 48.573405, 7.752111, 'Grand Est', 'Bas-Rhin', 'Capitale européenne', '{"population": 291000, "area": 78.26, "density": 3716, "tourist_attractions": ["Cathédrale Notre-Dame", "Petite France", "Parlement européen"]}', 7),
  ('Montpellier', '34000', 43.610769, 3.876716, 'Occitanie', 'Hérault', 'Ville étudiante dynamique', '{"population": 299000, "area": 56.88, "density": 5258, "tourist_attractions": ["Place de la Comédie", "Antigone", "Jardin des plantes"]}', 8),
  ('Bordeaux', '33000', 44.837789, -0.57918, 'Nouvelle-Aquitaine', 'Gironde', 'Capitale du vin', '{"population": 260000, "area": 49.36, "density": 5268, "tourist_attractions": ["Place de la Bourse", "Cité du Vin", "Quartier Saint-Pierre"]}', 9),
  ('Lille', '59000', 50.62925, 3.057256, 'Hauts-de-France', 'Nord', 'Capitale des Flandres', '{"population": 234000, "area": 34.83, "density": 6720, "tourist_attractions": ["Grand Place", "Vieille Bourse", "Citadelle"]}', 10),
  ('Rennes', '35000', 48.117266, -1.677793, 'Bretagne', 'Ille-et-Vilaine', 'Capitale de la Bretagne', '{"population": 222000, "area": 50.39, "density": 4405, "tourist_attractions": ["Parlement de Bretagne", "Cathédrale Saint-Pierre", "Thabor"]}', 11),
  ('Reims', '51100', 49.258329, 4.031696, 'Grand Est', 'Marne', 'Cité des sacres', '{"population": 183000, "area": 46.9, "density": 3902, "tourist_attractions": ["Cathédrale Notre-Dame", "Palais du Tau", "Caves de champagne"]}', 12),
  ('Saint-Étienne', '42000', 45.439695, 4.387178, 'Auvergne-Rhône-Alpes', 'Loire', 'Ville industrielle', '{"population": 172000, "area": 79.97, "density": 2150, "tourist_attractions": ["Musée d''art moderne", "Stade Geoffroy-Guichard"]}', 13),
  ('Toulon', '83000', 43.124228, 5.928, 'Provence-Alpes-Côte d''Azur', 'Var', 'Grand port militaire', '{"population": 176000, "area": 42.84, "density": 4106, "tourist_attractions": ["Mont Faron", "Vieille Ville", "Plages"]}', 14),
  ('Le Havre', '76600', 49.49437, 0.107929, 'Normandie', 'Seine-Maritime', 'Port maritime important', '{"population": 170000, "area": 46.95, "density": 3621, "tourist_attractions": ["Musée d''art moderne", "Port", "Plage"]}', 15),
  ('Grenoble', '38000', 45.188529, 5.724524, 'Auvergne-Rhône-Alpes', 'Isère', 'Capitale des Alpes', '{"population": 160000, "area": 18.13, "density": 8828, "tourist_attractions": ["Bastille", "Musée de Grenoble", "Téléphérique"]}', 16),
  ('Dijon', '21000', 47.322047, 5.04148, 'Bourgogne-Franche-Comté', 'Côte-d''Or', 'Capitale de la Bourgogne', '{"population": 159000, "area": 40.41, "density": 3937, "tourist_attractions": ["Palais des ducs", "Musée des Beaux-Arts", "Rue de la Liberté"]}', 17),
  ('Angers', '49000', 47.473988, -0.551558, 'Pays de la Loire', 'Maine-et-Loire', 'Cité historique', '{"population": 155000, "area": 42.71, "density": 3629, "tourist_attractions": ["Château d''Angers", "Cathédrale", "Terra Botanica"]}', 18),
  ('Nîmes', '30000', 43.836699, 4.360054, 'Occitanie', 'Gard', 'Ville romaine', '{"population": 151000, "area": 161.85, "density": 933, "tourist_attractions": ["Arènes", "Maison Carrée", "Pont du Gard"]}', 19),
  ('Villeurbanne', '69100', 45.771944, 4.890171, 'Auvergne-Rhône-Alpes', 'Rhône', 'Ville de la banlieue lyonnaise', '{"population": 150000, "area": 14.52, "density": 10331, "tourist_attractions": ["Gratte-Ciel", "TNP"]}', 20)
) AS c(name, postal_code, latitude, longitude, region, department, description, important_data, display_order)
ON CONFLICT DO NOTHING;

-- Villes belges
INSERT INTO cities (name, postal_code, country_id, latitude, longitude, region, timezone, description, important_data, active, display_order)
SELECT 
  c.name,
  c.postal_code,
  (SELECT id FROM countries WHERE code = 'BE') as country_id,
  c.latitude,
  c.longitude,
  c.region,
  'Europe/Brussels' as timezone,
  c.description,
  c.important_data,
  true,
  c.display_order
FROM (VALUES
  ('Bruxelles', '1000', 50.850346, 4.351721, 'Région de Bruxelles-Capitale', 'Capitale de la Belgique et de l''UE', '{"population": 185000, "area": 32.61, "density": 5673, "tourist_attractions": ["Grand-Place", "Atomium", "Manneken Pis"]}', 1),
  ('Anvers', '2000', 51.219448, 4.402464, 'Région flamande', 'Grand port et ville culturelle', '{"population": 529000, "area": 204.51, "density": 2587, "tourist_attractions": ["Cathédrale Notre-Dame", "Musée Rubens", "Zoo"]}', 2)
) AS c(name, postal_code, latitude, longitude, region, description, important_data, display_order)
ON CONFLICT DO NOTHING;

-- Villes suisses
INSERT INTO cities (name, postal_code, country_id, latitude, longitude, region, timezone, description, important_data, active, display_order)
SELECT 
  c.name,
  c.postal_code,
  (SELECT id FROM countries WHERE code = 'CH') as country_id,
  c.latitude,
  c.longitude,
  c.region,
  'Europe/Zurich' as timezone,
  c.description,
  c.important_data,
  true,
  c.display_order
FROM (VALUES
  ('Genève', '1200', 46.204391, 6.143158, 'Genève', 'Ville internationale et siège d''organisations', '{"population": 203000, "area": 15.93, "density": 12747, "tourist_attractions": ["Jet d''eau", "Palais des Nations", "Vieille Ville"]}', 1),
  ('Zurich', '8001', 47.376887, 8.541694, 'Zurich', 'Plus grande ville de Suisse', '{"population": 421000, "area": 87.88, "density": 4791, "tourist_attractions": ["Vieille Ville", "Lac de Zurich", "Bahnhofstrasse"]}', 2),
  ('Lausanne', '1000', 46.519962, 6.633597, 'Vaud', 'Capitale olympique', '{"population": 140000, "area": 41.37, "density": 3384, "tourist_attractions": ["Cathédrale", "Musée Olympique", "Lac Léman"]}', 3)
) AS c(name, postal_code, latitude, longitude, region, description, important_data, display_order)
ON CONFLICT DO NOTHING;

-- Villes marocaines
INSERT INTO cities (name, postal_code, country_id, latitude, longitude, region, timezone, description, important_data, active, display_order)
SELECT 
  c.name,
  c.postal_code,
  (SELECT id FROM countries WHERE code = 'MA') as country_id,
  c.latitude,
  c.longitude,
  c.region,
  'Africa/Casablanca' as timezone,
  c.description,
  c.important_data,
  true,
  c.display_order
FROM (VALUES
  ('Casablanca', '20000', 33.573110, -7.589843, 'Casablanca-Settat', 'Plus grande ville du Maroc', '{"population": 3350000, "area": 220, "density": 15227, "tourist_attractions": ["Hassan II", "Corniche", "Vieille Médina"]}', 1),
  ('Rabat', '10000', 34.020882, -6.841650, 'Rabat-Salé-Kénitra', 'Capitale du Maroc', '{"population": 580000, "area": 117, "density": 4957, "tourist_attractions": ["Kasbah des Oudaias", "Tour Hassan", "Mausolée Mohammed V"]}', 2)
) AS c(name, postal_code, latitude, longitude, region, description, important_data, display_order)
ON CONFLICT DO NOTHING;

