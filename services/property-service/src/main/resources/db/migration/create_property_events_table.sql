-- Table pour stocker les événements de propriétés (vues, contacts, favoris, partages)
-- Cette table permet de générer l'historique des statistiques

CREATE TABLE IF NOT EXISTS property_events (
    id BIGSERIAL PRIMARY KEY,
    property_id BIGINT NOT NULL,
    event_type VARCHAR(50) NOT NULL, -- 'VIEW', 'CONTACT', 'FAVORITE', 'SHARE'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user_id BIGINT,
    metadata JSONB,
    CONSTRAINT fk_property_events_property FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE
);

-- Index pour optimiser les requêtes
CREATE INDEX IF NOT EXISTS idx_property_events_property_id ON property_events(property_id);
CREATE INDEX IF NOT EXISTS idx_property_events_created_at ON property_events(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_property_events_type ON property_events(event_type);
CREATE INDEX IF NOT EXISTS idx_property_events_property_date ON property_events(property_id, created_at DESC);

-- Commentaires
COMMENT ON TABLE property_events IS 'Table pour stocker les événements liés aux propriétés (vues, contacts, favoris, partages)';
COMMENT ON COLUMN property_events.event_type IS 'Type d''événement: VIEW, CONTACT, FAVORITE, SHARE';
COMMENT ON COLUMN property_events.metadata IS 'Métadonnées supplémentaires au format JSON (ex: source, device, etc.)';

