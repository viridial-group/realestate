-- =====================================================
-- Migration: Ajout de la table contact_messages
-- Description: Table pour stocker les messages de contact
-- Date: 2024
-- =====================================================

-- Table contact_messages
CREATE TABLE IF NOT EXISTS contact_messages (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    subject VARCHAR(500) NOT NULL,
    message TEXT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'NEW',
    property_id BIGINT,
    organization_id BIGINT,
    read_at TIMESTAMP,
    read_by BIGINT,
    replied_at TIMESTAMP,
    replied_by BIGINT,
    notes TEXT,
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_contact_property FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE SET NULL,
    CONSTRAINT fk_contact_organization FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE SET NULL
);

-- Index pour améliorer les performances
CREATE INDEX IF NOT EXISTS idx_contact_email ON contact_messages(email);
CREATE INDEX IF NOT EXISTS idx_contact_status ON contact_messages(status);
CREATE INDEX IF NOT EXISTS idx_contact_property ON contact_messages(property_id);
CREATE INDEX IF NOT EXISTS idx_contact_created ON contact_messages(created_at);
CREATE INDEX IF NOT EXISTS idx_contact_organization ON contact_messages(organization_id);

-- Commentaires sur la table et les colonnes
COMMENT ON TABLE contact_messages IS 'Table pour stocker les messages de contact reçus depuis le formulaire public';
COMMENT ON COLUMN contact_messages.id IS 'Identifiant unique du message';
COMMENT ON COLUMN contact_messages.name IS 'Nom complet de l''expéditeur';
COMMENT ON COLUMN contact_messages.email IS 'Adresse email de l''expéditeur';
COMMENT ON COLUMN contact_messages.phone IS 'Numéro de téléphone (optionnel)';
COMMENT ON COLUMN contact_messages.subject IS 'Sujet du message';
COMMENT ON COLUMN contact_messages.message IS 'Contenu du message';
COMMENT ON COLUMN contact_messages.status IS 'Statut du message: NEW, READ, REPLIED, ARCHIVED';
COMMENT ON COLUMN contact_messages.property_id IS 'ID de la propriété concernée (optionnel)';
COMMENT ON COLUMN contact_messages.organization_id IS 'ID de l''organisation concernée';
COMMENT ON COLUMN contact_messages.read_at IS 'Date de lecture du message';
COMMENT ON COLUMN contact_messages.read_by IS 'ID de l''utilisateur qui a lu le message';
COMMENT ON COLUMN contact_messages.replied_at IS 'Date de réponse au message';
COMMENT ON COLUMN contact_messages.replied_by IS 'ID de l''utilisateur qui a répondu';
COMMENT ON COLUMN contact_messages.notes IS 'Notes internes sur le message';
COMMENT ON COLUMN contact_messages.active IS 'Indique si le message est actif (soft delete)';
COMMENT ON COLUMN contact_messages.created_at IS 'Date de création du message';
COMMENT ON COLUMN contact_messages.updated_at IS 'Date de dernière mise à jour';

