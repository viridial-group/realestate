-- Migration: Ajout des tables price_alerts et visit_appointments

-- =====================================================
-- Table price_alerts
-- =====================================================
CREATE TABLE IF NOT EXISTS price_alerts (
    id BIGSERIAL PRIMARY KEY,
    property_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    alert_type VARCHAR(50) NOT NULL,
    target_price NUMERIC(15, 2),
    percentage_threshold DOUBLE PRECISION,
    is_percentage BOOLEAN NOT NULL DEFAULT false,
    notified BOOLEAN NOT NULL DEFAULT false,
    notified_at TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT true,
    email_notification BOOLEAN NOT NULL DEFAULT true,
    in_app_notification BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_price_alert_property FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_price_alert_property ON price_alerts(property_id);
CREATE INDEX IF NOT EXISTS idx_price_alert_user ON price_alerts(user_id);
CREATE INDEX IF NOT EXISTS idx_price_alert_active ON price_alerts(active);
CREATE INDEX IF NOT EXISTS idx_price_alert_property_user ON price_alerts(property_id, user_id);

COMMENT ON TABLE price_alerts IS 'Alertes de prix pour les propriétés';
COMMENT ON COLUMN price_alerts.property_id IS 'ID de la propriété';
COMMENT ON COLUMN price_alerts.user_id IS 'ID de l''utilisateur qui a créé l''alerte';
COMMENT ON COLUMN price_alerts.alert_type IS 'Type d''alerte: PRICE_DROP, PRICE_INCREASE, PERCENTAGE_DROP, PERCENTAGE_INCREASE';
COMMENT ON COLUMN price_alerts.target_price IS 'Prix cible pour l''alerte';
COMMENT ON COLUMN price_alerts.percentage_threshold IS 'Seuil de pourcentage (ex: 5.0 pour 5%)';
COMMENT ON COLUMN price_alerts.is_percentage IS 'Indique si l''alerte est basée sur un pourcentage';
COMMENT ON COLUMN price_alerts.notified IS 'Indique si l''alerte a déjà été notifiée';
COMMENT ON COLUMN price_alerts.notified_at IS 'Date de la dernière notification';

-- =====================================================
-- Table visit_appointments
-- =====================================================
CREATE TABLE IF NOT EXISTS visit_appointments (
    id BIGSERIAL PRIMARY KEY,
    property_id BIGINT NOT NULL,
    user_id BIGINT,
    agent_id BIGINT NOT NULL,
    visitor_name VARCHAR(255),
    visitor_email VARCHAR(255),
    visitor_phone VARCHAR(20),
    appointment_date TIMESTAMP NOT NULL,
    duration_minutes INTEGER DEFAULT 60,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    notes TEXT,
    agent_notes TEXT,
    reminder_sent BOOLEAN DEFAULT false,
    reminder_sent_at TIMESTAMP,
    confirmed_at TIMESTAMP,
    cancelled_at TIMESTAMP,
    cancellation_reason VARCHAR(500),
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_visit_property FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_visit_property ON visit_appointments(property_id);
CREATE INDEX IF NOT EXISTS idx_visit_user ON visit_appointments(user_id);
CREATE INDEX IF NOT EXISTS idx_visit_agent ON visit_appointments(agent_id);
CREATE INDEX IF NOT EXISTS idx_visit_date ON visit_appointments(appointment_date);
CREATE INDEX IF NOT EXISTS idx_visit_status ON visit_appointments(status);

COMMENT ON TABLE visit_appointments IS 'Rendez-vous de visite pour les propriétés';
COMMENT ON COLUMN visit_appointments.property_id IS 'ID de la propriété';
COMMENT ON COLUMN visit_appointments.user_id IS 'ID de l''utilisateur qui demande la visite (peut être null pour visites anonymes)';
COMMENT ON COLUMN visit_appointments.agent_id IS 'ID de l''agent qui effectuera la visite';
COMMENT ON COLUMN visit_appointments.visitor_name IS 'Nom du visiteur (pour visites anonymes)';
COMMENT ON COLUMN visit_appointments.visitor_email IS 'Email du visiteur';
COMMENT ON COLUMN visit_appointments.visitor_phone IS 'Téléphone du visiteur';
COMMENT ON COLUMN visit_appointments.appointment_date IS 'Date et heure du rendez-vous';
COMMENT ON COLUMN visit_appointments.duration_minutes IS 'Durée estimée en minutes';
COMMENT ON COLUMN visit_appointments.status IS 'Statut: PENDING, CONFIRMED, CANCELLED, COMPLETED, NO_SHOW';
COMMENT ON COLUMN visit_appointments.notes IS 'Notes additionnelles';
COMMENT ON COLUMN visit_appointments.agent_notes IS 'Notes de l''agent après la visite';
COMMENT ON COLUMN visit_appointments.reminder_sent IS 'Indique si un rappel a été envoyé';
COMMENT ON COLUMN visit_appointments.reminder_sent_at IS 'Date d''envoi du rappel';
COMMENT ON COLUMN visit_appointments.confirmed_at IS 'Date de confirmation';
COMMENT ON COLUMN visit_appointments.cancelled_at IS 'Date d''annulation';
COMMENT ON COLUMN visit_appointments.cancellation_reason IS 'Raison de l''annulation';

