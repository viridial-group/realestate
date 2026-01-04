-- Migration: Ajout de la table price_history pour l'historique des prix des propriétés

CREATE TABLE IF NOT EXISTS price_history (
    id BIGSERIAL PRIMARY KEY,
    property_id BIGINT NOT NULL,
    price NUMERIC(15, 2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'EUR',
    change_date TIMESTAMP NOT NULL,
    change_reason VARCHAR(500),
    created_by BIGINT,
    source VARCHAR(50) DEFAULT 'MANUAL',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_price_history_property FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE
);

-- Index pour améliorer les performances
CREATE INDEX IF NOT EXISTS idx_price_history_property ON price_history(property_id);
CREATE INDEX IF NOT EXISTS idx_price_history_date ON price_history(change_date);
CREATE INDEX IF NOT EXISTS idx_price_history_property_date ON price_history(property_id, change_date);

-- Commentaires
COMMENT ON TABLE price_history IS 'Historique des changements de prix pour les propriétés';
COMMENT ON COLUMN price_history.property_id IS 'ID de la propriété';
COMMENT ON COLUMN price_history.price IS 'Prix au moment du changement';
COMMENT ON COLUMN price_history.currency IS 'Devise (EUR, USD, etc.)';
COMMENT ON COLUMN price_history.change_date IS 'Date du changement de prix';
COMMENT ON COLUMN price_history.change_reason IS 'Raison du changement (ex: Market adjustment, Renovation, etc.)';
COMMENT ON COLUMN price_history.created_by IS 'ID de l''utilisateur qui a effectué le changement';
COMMENT ON COLUMN price_history.source IS 'Source du changement: MANUAL, AUTO, IMPORT';

