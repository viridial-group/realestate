-- =====================================================
-- Script pour corriger le mot de passe de l'admin
-- =====================================================
-- Ce script met à jour le mot de passe de admin@viridial.com
-- pour utiliser le hash BCrypt de "admin123" au lieu de "password123"

-- Hash BCrypt pour "admin123": $2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi

UPDATE users 
SET password = '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi',
    updated_at = NOW()
WHERE email = 'admin@viridial.com';

-- Vérification
SELECT 
    email, 
    first_name, 
    last_name, 
    enabled,
    CASE 
        WHEN password = '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi' 
        THEN '✅ Mot de passe correct (admin123)'
        ELSE '❌ Mot de passe incorrect'
    END as password_status
FROM users 
WHERE email = 'admin@viridial.com';

