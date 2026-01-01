-- Migration SQL pour ajouter le champ isPrimary à la table organization_users
-- et supprimer la colonne organization_id de la table users

-- 1. Ajouter la colonne is_primary à organization_users
ALTER TABLE organization_users
ADD COLUMN IF NOT EXISTS is_primary BOOLEAN DEFAULT FALSE NOT NULL;

-- 2. Créer un index pour améliorer les performances des requêtes sur l'affectation principale
CREATE INDEX IF NOT EXISTS idx_org_user_primary 
ON organization_users(user_id, is_primary) 
WHERE is_primary = TRUE;

-- 3. Mettre à jour les données existantes : définir la première affectation active comme principale
-- (si un utilisateur a plusieurs affectations, on prend la plus ancienne)
UPDATE organization_users ou1
SET is_primary = TRUE
WHERE ou1.id IN (
    SELECT ou2.id
    FROM organization_users ou2
    WHERE ou2.active = TRUE
      AND ou2.user_id = ou1.user_id
      AND ou2.id = (
          SELECT ou3.id
          FROM organization_users ou3
          WHERE ou3.user_id = ou2.user_id
            AND ou3.active = TRUE
          ORDER BY ou3.created_at ASC
          LIMIT 1
      )
)
AND NOT EXISTS (
    SELECT 1
    FROM organization_users ou4
    WHERE ou4.user_id = ou1.user_id
      AND ou4.is_primary = TRUE
      AND ou4.id != ou1.id
);

-- 4. Supprimer la colonne organization_id de la table users (si elle existe)
-- ATTENTION: Cette commande supprime la colonne. Vérifiez d'abord que toutes les données
-- ont été migrées vers organization_users avant d'exécuter cette commande.
-- ALTER TABLE users DROP COLUMN IF EXISTS organization_id;

-- Note: La suppression de organization_id dans users doit être faite manuellement
-- après vérification que toutes les données ont été migrées vers organization_users

