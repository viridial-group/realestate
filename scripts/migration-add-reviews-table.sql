-- Migration: Ajout de la table reviews pour le système d'avis
-- Date: 2024

-- Créer la table reviews
CREATE TABLE IF NOT EXISTS reviews (
    id BIGSERIAL PRIMARY KEY,
    property_id BIGINT NOT NULL,
    user_id BIGINT,
    author_name VARCHAR(255),
    author_email VARCHAR(255),
    rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    verified_purchase BOOLEAN DEFAULT false,
    helpful_count INTEGER DEFAULT 0,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_review_property FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE
);

-- Créer les index pour améliorer les performances
CREATE INDEX IF NOT EXISTS idx_review_property ON reviews(property_id);
CREATE INDEX IF NOT EXISTS idx_review_user ON reviews(user_id);
CREATE INDEX IF NOT EXISTS idx_review_status ON reviews(status);
CREATE INDEX IF NOT EXISTS idx_review_created_at ON reviews(created_at);
CREATE INDEX IF NOT EXISTS idx_review_property_status ON reviews(property_id, status);
CREATE INDEX IF NOT EXISTS idx_review_property_active ON reviews(property_id, active);

-- Commentaires sur la table et les colonnes
COMMENT ON TABLE reviews IS 'Table pour stocker les avis des clients sur les propriétés';
COMMENT ON COLUMN reviews.property_id IS 'ID de la propriété concernée';
COMMENT ON COLUMN reviews.user_id IS 'ID de l''utilisateur qui a laissé l''avis (peut être null pour avis anonymes)';
COMMENT ON COLUMN reviews.author_name IS 'Nom de l''auteur (pour avis anonymes ou si l''utilisateur n''existe plus)';
COMMENT ON COLUMN reviews.author_email IS 'Email de l''auteur (optionnel)';
COMMENT ON COLUMN reviews.rating IS 'Note de 1 à 5';
COMMENT ON COLUMN reviews.comment IS 'Commentaire de l''avis';
COMMENT ON COLUMN reviews.status IS 'Statut: PENDING, APPROVED, REJECTED';
COMMENT ON COLUMN reviews.verified_purchase IS 'Indique si l''avis provient d''un achat vérifié';
COMMENT ON COLUMN reviews.helpful_count IS 'Nombre de personnes qui ont trouvé l''avis utile';
COMMENT ON COLUMN reviews.active IS 'Pour soft delete';

