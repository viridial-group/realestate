-- Script SQL pour mettre à jour transaction_type pour les propriétés PUBLISHED et DRAFT
-- Date: 2024-01-03

-- =====================================================
-- 1. Mettre à jour les propriétés PUBLISHED
-- =====================================================
-- Par défaut, les propriétés PUBLISHED sont en vente (SALE)
-- Sauf si elles ont déjà un transaction_type défini
UPDATE properties 
SET transaction_type = 'SALE'
WHERE status = 'PUBLISHED' 
  AND (transaction_type IS NULL OR transaction_type = '');

-- Si vous voulez aussi mettre à jour les propriétés PUBLISHED qui sont en location
-- (par exemple, basé sur un critère spécifique comme le prix ou autre)
-- Vous pouvez ajouter une condition supplémentaire :
-- UPDATE properties 
-- SET transaction_type = 'RENT'
-- WHERE status = 'PUBLISHED' 
--   AND transaction_type IS NULL
--   AND price < 200000; -- Exemple : propriétés à bas prix = location

-- =====================================================
-- 2. Mettre à jour les propriétés DRAFT
-- =====================================================
-- Par défaut, les propriétés DRAFT sont en vente (SALE)
-- Sauf si elles ont déjà un transaction_type défini
UPDATE properties 
SET transaction_type = 'SALE'
WHERE status = 'DRAFT' 
  AND (transaction_type IS NULL OR transaction_type = '');

-- =====================================================
-- 3. Mettre à jour les propriétés AVAILABLE (si elles existent)
-- =====================================================
UPDATE properties 
SET transaction_type = 'SALE'
WHERE status = 'AVAILABLE' 
  AND (transaction_type IS NULL OR transaction_type = '');

-- =====================================================
-- 4. Vérification des résultats
-- =====================================================
-- Afficher le nombre de propriétés mises à jour par statut
SELECT 
    status,
    transaction_type,
    COUNT(*) as count
FROM properties
WHERE status IN ('PUBLISHED', 'DRAFT', 'AVAILABLE')
GROUP BY status, transaction_type
ORDER BY status, transaction_type;

-- Afficher les propriétés qui n'ont toujours pas de transaction_type
SELECT 
    id,
    reference,
    title,
    status,
    transaction_type
FROM properties
WHERE status IN ('PUBLISHED', 'DRAFT', 'AVAILABLE')
  AND (transaction_type IS NULL OR transaction_type = '')
ORDER BY status, id;

