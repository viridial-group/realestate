-- =====================================================
-- Script SQL pour créer tous les templates d'email
-- Templates modernes et beaux pour le SaaS immobilier
-- =====================================================

-- Fonction helper pour échapper les guillemets dans le HTML
-- Note: PostgreSQL utilise $$ pour les chaînes littérales

-- =====================================================
-- 1. WELCOME - Email de bienvenue
-- =====================================================
INSERT INTO email_templates (name, description, type, subject, body, organization_id, active, is_default, available_variables, created_at, updated_at)
VALUES (
    'welcome_email',
    'Email de bienvenue pour les nouveaux utilisateurs',
    'WELCOME',
    'Bienvenue sur Viridial, {{firstName}} ! 🏠',
    '<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Bienvenue sur Viridial</title>
</head>
<body style="margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, ''Segoe UI'', Roboto, ''Helvetica Neue'', Arial, sans-serif; background-color: #f5f7fa;">
    <table role="presentation" style="width: 100%; border-collapse: collapse;">
        <tr>
            <td style="padding: 40px 20px; text-align: center;">
                <table role="presentation" style="max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 12px; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);">
                    <!-- Header -->
                    <tr>
                        <td style="padding: 40px 40px 20px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); border-radius: 12px 12px 0 0; text-align: center;">
                            <h1 style="margin: 0; color: #ffffff; font-size: 32px; font-weight: 700;">🏠 Viridial</h1>
                        </td>
                    </tr>
                    <!-- Content -->
                    <tr>
                        <td style="padding: 40px;">
                            <h2 style="margin: 0 0 20px; color: #1a202c; font-size: 24px; font-weight: 600;">Bienvenue, {{firstName}} !</h2>
                            <p style="margin: 0 0 20px; color: #4a5568; font-size: 16px; line-height: 1.6;">
                                Nous sommes ravis de vous accueillir sur Viridial, votre plateforme immobilière de confiance.
                            </p>
                            <p style="margin: 0 0 30px; color: #4a5568; font-size: 16px; line-height: 1.6;">
                                Votre compte a été créé avec succès. Vous pouvez maintenant commencer à explorer les propriétés disponibles ou publier vos propres annonces.
                            </p>
                            <!-- CTA Button -->
                            <table role="presentation" style="width: 100%; margin: 30px 0;">
                                <tr>
                                    <td style="text-align: center;">
                                        <a href="{{appUrl}}" style="display: inline-block; padding: 14px 32px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: #ffffff; text-decoration: none; border-radius: 8px; font-weight: 600; font-size: 16px;">Accéder à mon espace</a>
                                    </td>
                                </tr>
                            </table>
                            <!-- Features -->
                            <div style="margin: 40px 0; padding: 30px; background-color: #f7fafc; border-radius: 8px;">
                                <h3 style="margin: 0 0 20px; color: #1a202c; font-size: 18px; font-weight: 600;">Découvrez nos fonctionnalités :</h3>
                                <ul style="margin: 0; padding-left: 20px; color: #4a5568; font-size: 15px; line-height: 1.8;">
                                    <li>Recherche avancée de propriétés</li>
                                    <li>Gestion de vos annonces</li>
                                    <li>Suivi des statistiques</li>
                                    <li>Notifications en temps réel</li>
                                </ul>
                            </div>
                        </td>
                    </tr>
                    <!-- Footer -->
                    <tr>
                        <td style="padding: 30px 40px; background-color: #edf2f7; border-radius: 0 0 12px 12px; text-align: center;">
                            <p style="margin: 0 0 10px; color: #718096; font-size: 14px;">
                                Si vous avez des questions, n''hésitez pas à nous contacter à <a href="mailto:support@viridial.com" style="color: #667eea; text-decoration: none;">support@viridial.com</a>
                            </p>
                            <p style="margin: 0; color: #a0aec0; font-size: 12px;">
                                © 2024 Viridial. Tous droits réservés.
                            </p>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
</body>
</html>',
    NULL,
    true,
    true,
    '["firstName", "appUrl"]',
    NOW(),
    NOW()
) ON CONFLICT DO NOTHING;

-- =====================================================
-- 2. CONFIRMATION - Confirmation d'inscription
-- =====================================================
INSERT INTO email_templates (name, description, type, subject, body, organization_id, active, is_default, available_variables, created_at, updated_at)
VALUES (
    'registration_confirmation',
    'Confirmation d''inscription',
    'CONFIRMATION',
    'Confirmation de votre inscription - Viridial',
    '<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Confirmation d''inscription</title>
</head>
<body style="margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, ''Segoe UI'', Roboto, ''Helvetica Neue'', Arial, sans-serif; background-color: #f5f7fa;">
    <table role="presentation" style="width: 100%; border-collapse: collapse;">
        <tr>
            <td style="padding: 40px 20px; text-align: center;">
                <table role="presentation" style="max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 12px; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);">
                    <!-- Header -->
                    <tr>
                        <td style="padding: 40px 40px 20px; background: linear-gradient(135deg, #10b981 0%, #059669 100%); border-radius: 12px 12px 0 0; text-align: center;">
                            <div style="width: 80px; height: 80px; margin: 0 auto 20px; background-color: rgba(255, 255, 255, 0.2); border-radius: 50%; display: flex; align-items: center; justify-content: center;">
                                <span style="font-size: 40px;">✓</span>
                            </div>
                            <h1 style="margin: 0; color: #ffffff; font-size: 28px; font-weight: 700;">Inscription confirmée</h1>
                        </td>
                    </tr>
                    <!-- Content -->
                    <tr>
                        <td style="padding: 40px;">
                            <h2 style="margin: 0 0 20px; color: #1a202c; font-size: 24px; font-weight: 600;">Bonjour {{firstName}},</h2>
                            <p style="margin: 0 0 20px; color: #4a5568; font-size: 16px; line-height: 1.6;">
                                Votre inscription sur Viridial a été confirmée avec succès !
                            </p>
                            <div style="margin: 30px 0; padding: 20px; background-color: #f0fdf4; border-left: 4px solid #10b981; border-radius: 4px;">
                                <p style="margin: 0; color: #166534; font-size: 15px; font-weight: 500;">
                                    <strong>Email :</strong> {{email}}<br>
                                    <strong>Date d''inscription :</strong> {{registrationDate}}
                                </p>
                            </div>
                            <p style="margin: 20px 0; color: #4a5568; font-size: 16px; line-height: 1.6;">
                                Vous pouvez maintenant vous connecter et commencer à utiliser tous les services de Viridial.
                            </p>
                            <!-- CTA Button -->
                            <table role="presentation" style="width: 100%; margin: 30px 0;">
                                <tr>
                                    <td style="text-align: center;">
                                        <a href="{{loginUrl}}" style="display: inline-block; padding: 14px 32px; background: linear-gradient(135deg, #10b981 0%, #059669 100%); color: #ffffff; text-decoration: none; border-radius: 8px; font-weight: 600; font-size: 16px;">Se connecter</a>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                    <!-- Footer -->
                    <tr>
                        <td style="padding: 30px 40px; background-color: #edf2f7; border-radius: 0 0 12px 12px; text-align: center;">
                            <p style="margin: 0 0 10px; color: #718096; font-size: 14px;">
                                Besoin d''aide ? Contactez-nous à <a href="mailto:support@viridial.com" style="color: #10b981; text-decoration: none;">support@viridial.com</a>
                            </p>
                            <p style="margin: 0; color: #a0aec0; font-size: 12px;">© 2024 Viridial. Tous droits réservés.</p>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
</body>
</html>',
    NULL,
    true,
    true,
    '["firstName", "email", "registrationDate", "loginUrl"]',
    NOW(),
    NOW()
) ON CONFLICT DO NOTHING;

-- =====================================================
-- 3. INVITATION - Invitation à rejoindre une organisation
-- =====================================================
INSERT INTO email_templates (name, description, type, subject, body, organization_id, active, is_default, available_variables, created_at, updated_at)
VALUES (
    'organization_invitation',
    'Invitation à rejoindre une organisation',
    'INVITATION',
    'Invitation à rejoindre {{organizationName}} sur Viridial',
    '<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Invitation</title>
</head>
<body style="margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, ''Segoe UI'', Roboto, ''Helvetica Neue'', Arial, sans-serif; background-color: #f5f7fa;">
    <table role="presentation" style="width: 100%; border-collapse: collapse;">
        <tr>
            <td style="padding: 40px 20px; text-align: center;">
                <table role="presentation" style="max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 12px; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);">
                    <!-- Header -->
                    <tr>
                        <td style="padding: 40px 40px 20px; background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%); border-radius: 12px 12px 0 0; text-align: center;">
                            <div style="width: 80px; height: 80px; margin: 0 auto 20px; background-color: rgba(255, 255, 255, 0.2); border-radius: 50%; display: flex; align-items: center; justify-content: center;">
                                <span style="font-size: 40px;">✉️</span>
                            </div>
                            <h1 style="margin: 0; color: #ffffff; font-size: 28px; font-weight: 700;">Vous êtes invité !</h1>
                        </td>
                    </tr>
                    <!-- Content -->
                    <tr>
                        <td style="padding: 40px;">
                            <h2 style="margin: 0 0 20px; color: #1a202c; font-size: 24px; font-weight: 600;">Bonjour {{recipientName}},</h2>
                            <p style="margin: 0 0 20px; color: #4a5568; font-size: 16px; line-height: 1.6;">
                                <strong>{{inviterName}}</strong> vous invite à rejoindre <strong>{{organizationName}}</strong> sur Viridial.
                            </p>
                            <div style="margin: 30px 0; padding: 25px; background: linear-gradient(135deg, #eff6ff 0%, #dbeafe 100%); border-radius: 8px; border-left: 4px solid #3b82f6;">
                                <p style="margin: 0 0 10px; color: #1e40af; font-size: 16px; font-weight: 600;">{{organizationName}}</p>
                                <p style="margin: 0; color: #1e3a8a; font-size: 14px;">{{organizationDescription}}</p>
                            </div>
                            <p style="margin: 20px 0; color: #4a5568; font-size: 16px; line-height: 1.6;">
                                En acceptant cette invitation, vous pourrez collaborer avec l''équipe et accéder aux propriétés de l''organisation.
                            </p>
                            <!-- CTA Buttons -->
                            <table role="presentation" style="width: 100%; margin: 30px 0;">
                                <tr>
                                    <td style="text-align: center;">
                                        <a href="{{acceptUrl}}" style="display: inline-block; padding: 14px 32px; margin: 0 10px 10px 0; background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%); color: #ffffff; text-decoration: none; border-radius: 8px; font-weight: 600; font-size: 16px;">Accepter l''invitation</a>
                                        <a href="{{declineUrl}}" style="display: inline-block; padding: 14px 32px; margin: 0 0 10px 10px; background-color: #e5e7eb; color: #374151; text-decoration: none; border-radius: 8px; font-weight: 600; font-size: 16px;">Décliner</a>
                                    </td>
                                </tr>
                            </table>
                            <p style="margin: 20px 0 0; color: #9ca3af; font-size: 14px; line-height: 1.6;">
                                <em>Cette invitation expire le {{expirationDate}}.</em>
                            </p>
                        </td>
                    </tr>
                    <!-- Footer -->
                    <tr>
                        <td style="padding: 30px 40px; background-color: #edf2f7; border-radius: 0 0 12px 12px; text-align: center;">
                            <p style="margin: 0 0 10px; color: #718096; font-size: 14px;">
                                Si vous ne souhaitez pas rejoindre cette organisation, vous pouvez ignorer cet email.
                            </p>
                            <p style="margin: 0; color: #a0aec0; font-size: 12px;">© 2024 Viridial. Tous droits réservés.</p>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
</body>
</html>',
    NULL,
    true,
    true,
    '["recipientName", "inviterName", "organizationName", "organizationDescription", "acceptUrl", "declineUrl", "expirationDate"]',
    NOW(),
    NOW()
) ON CONFLICT DO NOTHING;

-- =====================================================
-- 4. PASSWORD_RESET - Réinitialisation de mot de passe
-- =====================================================
INSERT INTO email_templates (name, description, type, subject, body, organization_id, active, is_default, available_variables, created_at, updated_at)
VALUES (
    'password_reset',
    'Réinitialisation de mot de passe',
    'PASSWORD_RESET',
    'Réinitialisation de votre mot de passe - Viridial',
    '<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Réinitialisation de mot de passe</title>
</head>
<body style="margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, ''Segoe UI'', Roboto, ''Helvetica Neue'', Arial, sans-serif; background-color: #f5f7fa;">
    <table role="presentation" style="width: 100%; border-collapse: collapse;">
        <tr>
            <td style="padding: 40px 20px; text-align: center;">
                <table role="presentation" style="max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 12px; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);">
                    <!-- Header -->
                    <tr>
                        <td style="padding: 40px 40px 20px; background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%); border-radius: 12px 12px 0 0; text-align: center;">
                            <div style="width: 80px; height: 80px; margin: 0 auto 20px; background-color: rgba(255, 255, 255, 0.2); border-radius: 50%; display: flex; align-items: center; justify-content: center;">
                                <span style="font-size: 40px;">🔐</span>
                            </div>
                            <h1 style="margin: 0; color: #ffffff; font-size: 28px; font-weight: 700;">Réinitialisation de mot de passe</h1>
                        </td>
                    </tr>
                    <!-- Content -->
                    <tr>
                        <td style="padding: 40px;">
                            <h2 style="margin: 0 0 20px; color: #1a202c; font-size: 24px; font-weight: 600;">Bonjour {{firstName}},</h2>
                            <p style="margin: 0 0 20px; color: #4a5568; font-size: 16px; line-height: 1.6;">
                                Nous avons reçu une demande de réinitialisation de mot de passe pour votre compte Viridial.
                            </p>
                            <div style="margin: 30px 0; padding: 20px; background-color: #fffbeb; border-left: 4px solid #f59e0b; border-radius: 4px;">
                                <p style="margin: 0; color: #92400e; font-size: 15px;">
                                    <strong>⚠️ Important :</strong> Si vous n''avez pas demandé cette réinitialisation, ignorez cet email. Votre mot de passe ne sera pas modifié.
                                </p>
                            </div>
                            <p style="margin: 20px 0; color: #4a5568; font-size: 16px; line-height: 1.6;">
                                Pour réinitialiser votre mot de passe, cliquez sur le bouton ci-dessous. Ce lien est valide pendant <strong>24 heures</strong>.
                            </p>
                            <!-- CTA Button -->
                            <table role="presentation" style="width: 100%; margin: 30px 0;">
                                <tr>
                                    <td style="text-align: center;">
                                        <a href="{{resetUrl}}" style="display: inline-block; padding: 14px 32px; background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%); color: #ffffff; text-decoration: none; border-radius: 8px; font-weight: 600; font-size: 16px;">Réinitialiser mon mot de passe</a>
                                    </td>
                                </tr>
                            </table>
                            <p style="margin: 20px 0 0; color: #9ca3af; font-size: 14px; line-height: 1.6;">
                                Ou copiez ce lien dans votre navigateur :<br>
                                <span style="color: #3b82f6; word-break: break-all;">{{resetUrl}}</span>
                            </p>
                        </td>
                    </tr>
                    <!-- Footer -->
                    <tr>
                        <td style="padding: 30px 40px; background-color: #edf2f7; border-radius: 0 0 12px 12px; text-align: center;">
                            <p style="margin: 0 0 10px; color: #718096; font-size: 14px;">
                                Pour des raisons de sécurité, ce lien expire dans 24 heures.
                            </p>
                            <p style="margin: 0; color: #a0aec0; font-size: 12px;">© 2024 Viridial. Tous droits réservés.</p>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
</body>
</html>',
    NULL,
    true,
    true,
    '["firstName", "resetUrl"]',
    NOW(),
    NOW()
) ON CONFLICT DO NOTHING;

-- =====================================================
-- 5. PROPERTY_PUBLISHED - Propriété publiée
-- =====================================================
INSERT INTO email_templates (name, description, type, subject, body, organization_id, active, is_default, available_variables, created_at, updated_at)
VALUES (
    'property_published',
    'Notification de publication de propriété',
    'NOTIFICATION',
    'Votre annonce "{{propertyTitle}}" a été publiée !',
    '<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Propriété publiée</title>
</head>
<body style="margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, ''Segoe UI'', Roboto, ''Helvetica Neue'', Arial, sans-serif; background-color: #f5f7fa;">
    <table role="presentation" style="width: 100%; border-collapse: collapse;">
        <tr>
            <td style="padding: 40px 20px; text-align: center;">
                <table role="presentation" style="max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 12px; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);">
                    <!-- Header -->
                    <tr>
                        <td style="padding: 40px 40px 20px; background: linear-gradient(135deg, #10b981 0%, #059669 100%); border-radius: 12px 12px 0 0; text-align: center;">
                            <div style="width: 80px; height: 80px; margin: 0 auto 20px; background-color: rgba(255, 255, 255, 0.2); border-radius: 50%; display: flex; align-items: center; justify-content: center;">
                                <span style="font-size: 40px;">🏡</span>
                            </div>
                            <h1 style="margin: 0; color: #ffffff; font-size: 28px; font-weight: 700;">Annonce publiée !</h1>
                        </td>
                    </tr>
                    <!-- Content -->
                    <tr>
                        <td style="padding: 40px;">
                            <h2 style="margin: 0 0 20px; color: #1a202c; font-size: 24px; font-weight: 600;">Félicitations, {{firstName}} !</h2>
                            <p style="margin: 0 0 20px; color: #4a5568; font-size: 16px; line-height: 1.6;">
                                Votre annonce <strong>"{{propertyTitle}}"</strong> a été publiée avec succès et est maintenant visible sur Viridial.
                            </p>
                            <!-- Property Card -->
                            <div style="margin: 30px 0; padding: 0; background-color: #f7fafc; border-radius: 8px; overflow: hidden; border: 1px solid #e2e8f0;">
                                <div style="padding: 20px;">
                                    <h3 style="margin: 0 0 10px; color: #1a202c; font-size: 20px; font-weight: 600;">{{propertyTitle}}</h3>
                                    <p style="margin: 0 0 15px; color: #4a5568; font-size: 15px;">{{propertyAddress}}, {{propertyCity}}</p>
                                    <div style="display: flex; gap: 20px; margin-top: 15px;">
                                        <div>
                                            <span style="color: #718096; font-size: 13px;">Prix</span>
                                            <p style="margin: 5px 0 0; color: #1a202c; font-size: 18px; font-weight: 700;">{{propertyPrice}} €</p>
                                        </div>
                                        <div>
                                            <span style="color: #718096; font-size: 13px;">Surface</span>
                                            <p style="margin: 5px 0 0; color: #1a202c; font-size: 18px; font-weight: 700;">{{propertyArea}} m²</p>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <!-- CTA Button -->
                            <table role="presentation" style="width: 100%; margin: 30px 0;">
                                <tr>
                                    <td style="text-align: center;">
                                        <a href="{{propertyUrl}}" style="display: inline-block; padding: 14px 32px; background: linear-gradient(135deg, #10b981 0%, #059669 100%); color: #ffffff; text-decoration: none; border-radius: 8px; font-weight: 600; font-size: 16px;">Voir mon annonce</a>
                                    </td>
                                </tr>
                            </table>
                            <p style="margin: 20px 0 0; color: #4a5568; font-size: 15px; line-height: 1.6;">
                                💡 <strong>Astuce :</strong> Partagez votre annonce sur les réseaux sociaux pour augmenter sa visibilité !
                            </p>
                        </td>
                    </tr>
                    <!-- Footer -->
                    <tr>
                        <td style="padding: 30px 40px; background-color: #edf2f7; border-radius: 0 0 12px 12px; text-align: center;">
                            <p style="margin: 0 0 10px; color: #718096; font-size: 14px;">
                                Besoin d''aide ? Contactez-nous à <a href="mailto:support@viridial.com" style="color: #10b981; text-decoration: none;">support@viridial.com</a>
                            </p>
                            <p style="margin: 0; color: #a0aec0; font-size: 12px;">© 2024 Viridial. Tous droits réservés.</p>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
</body>
</html>',
    NULL,
    true,
    true,
    '["firstName", "propertyTitle", "propertyAddress", "propertyCity", "propertyPrice", "propertyArea", "propertyUrl"]',
    NOW(),
    NOW()
) ON CONFLICT DO NOTHING;

-- =====================================================
-- 6. CONTACT_MESSAGE - Nouveau message de contact
-- =====================================================
INSERT INTO email_templates (name, description, type, subject, body, organization_id, active, is_default, available_variables, created_at, updated_at)
VALUES (
    'contact_message',
    'Notification de nouveau message de contact',
    'NOTIFICATION',
    'Nouveau message concernant "{{propertyTitle}}"',
    '<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Nouveau message</title>
</head>
<body style="margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, ''Segoe UI'', Roboto, ''Helvetica Neue'', Arial, sans-serif; background-color: #f5f7fa;">
    <table role="presentation" style="width: 100%; border-collapse: collapse;">
        <tr>
            <td style="padding: 40px 20px; text-align: center;">
                <table role="presentation" style="max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 12px; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);">
                    <!-- Header -->
                    <tr>
                        <td style="padding: 40px 40px 20px; background: linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%); border-radius: 12px 12px 0 0; text-align: center;">
                            <div style="width: 80px; height: 80px; margin: 0 auto 20px; background-color: rgba(255, 255, 255, 0.2); border-radius: 50%; display: flex; align-items: center; justify-content: center;">
                                <span style="font-size: 40px;">💬</span>
                            </div>
                            <h1 style="margin: 0; color: #ffffff; font-size: 28px; font-weight: 700;">Nouveau message</h1>
                        </td>
                    </tr>
                    <!-- Content -->
                    <tr>
                        <td style="padding: 40px;">
                            <h2 style="margin: 0 0 20px; color: #1a202c; font-size: 24px; font-weight: 600;">Bonjour {{recipientName}},</h2>
                            <p style="margin: 0 0 20px; color: #4a5568; font-size: 16px; line-height: 1.6;">
                                Vous avez reçu un nouveau message concernant votre annonce <strong>"{{propertyTitle}}"</strong>.
                            </p>
                            <!-- Message Card -->
                            <div style="margin: 30px 0; padding: 25px; background-color: #faf5ff; border-left: 4px solid #8b5cf6; border-radius: 8px;">
                                <div style="margin-bottom: 15px;">
                                    <span style="color: #6b21a8; font-size: 13px; font-weight: 600;">De :</span>
                                    <p style="margin: 5px 0 0; color: #1a202c; font-size: 16px; font-weight: 600;">{{senderName}}</p>
                                    <p style="margin: 5px 0 0; color: #718096; font-size: 14px;">{{senderEmail}}</p>
                                    <p style="margin: 5px 0 0; color: #718096; font-size: 14px;">{{senderPhone}}</p>
                                </div>
                                <div style="margin-top: 20px; padding-top: 20px; border-top: 1px solid #e9d5ff;">
                                    <span style="color: #6b21a8; font-size: 13px; font-weight: 600;">Message :</span>
                                    <p style="margin: 10px 0 0; color: #1a202c; font-size: 15px; line-height: 1.6; white-space: pre-wrap;">{{message}}</p>
                                </div>
                            </div>
                            <!-- Property Info -->
                            <div style="margin: 20px 0; padding: 15px; background-color: #f7fafc; border-radius: 8px;">
                                <p style="margin: 0 0 5px; color: #718096; font-size: 13px;">Annonce concernée :</p>
                                <p style="margin: 0; color: #1a202c; font-size: 16px; font-weight: 600;">{{propertyTitle}}</p>
                                <p style="margin: 5px 0 0; color: #4a5568; font-size: 14px;">{{propertyAddress}}</p>
                            </div>
                            <!-- CTA Button -->
                            <table role="presentation" style="width: 100%; margin: 30px 0;">
                                <tr>
                                    <td style="text-align: center;">
                                        <a href="{{messageUrl}}" style="display: inline-block; padding: 14px 32px; background: linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%); color: #ffffff; text-decoration: none; border-radius: 8px; font-weight: 600; font-size: 16px;">Répondre au message</a>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                    <!-- Footer -->
                    <tr>
                        <td style="padding: 30px 40px; background-color: #edf2f7; border-radius: 0 0 12px 12px; text-align: center;">
                            <p style="margin: 0 0 10px; color: #718096; font-size: 14px;">
                                Répondez rapidement pour augmenter vos chances de conclure une affaire !
                            </p>
                            <p style="margin: 0; color: #a0aec0; font-size: 12px;">© 2024 Viridial. Tous droits réservés.</p>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
</body>
</html>',
    NULL,
    true,
    true,
    '["recipientName", "senderName", "senderEmail", "senderPhone", "message", "propertyTitle", "propertyAddress", "messageUrl"]',
    NOW(),
    NOW()
) ON CONFLICT DO NOTHING;

-- =====================================================
-- 7. APPROVAL_REQUEST - Demande d'approbation
-- =====================================================
INSERT INTO email_templates (name, description, type, subject, body, organization_id, active, is_default, available_variables, created_at, updated_at)
VALUES (
    'approval_request',
    'Demande d''approbation',
    'NOTIFICATION',
    'Demande d''approbation : {{taskTitle}}',
    '<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Demande d''approbation</title>
</head>
<body style="margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, ''Segoe UI'', Roboto, ''Helvetica Neue'', Arial, sans-serif; background-color: #f5f7fa;">
    <table role="presentation" style="width: 100%; border-collapse: collapse;">
        <tr>
            <td style="padding: 40px 20px; text-align: center;">
                <table role="presentation" style="max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 12px; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);">
                    <!-- Header -->
                    <tr>
                        <td style="padding: 40px 40px 20px; background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%); border-radius: 12px 12px 0 0; text-align: center;">
                            <div style="width: 80px; height: 80px; margin: 0 auto 20px; background-color: rgba(255, 255, 255, 0.2); border-radius: 50%; display: flex; align-items: center; justify-content: center;">
                                <span style="font-size: 40px;">✅</span>
                            </div>
                            <h1 style="margin: 0; color: #ffffff; font-size: 28px; font-weight: 700;">Demande d''approbation</h1>
                        </td>
                    </tr>
                    <!-- Content -->
                    <tr>
                        <td style="padding: 40px;">
                            <h2 style="margin: 0 0 20px; color: #1a202c; font-size: 24px; font-weight: 600;">Bonjour {{approverName}},</h2>
                            <p style="margin: 0 0 20px; color: #4a5568; font-size: 16px; line-height: 1.6;">
                                <strong>{{requesterName}}</strong> a besoin de votre approbation pour :
                            </p>
                            <!-- Request Card -->
                            <div style="margin: 30px 0; padding: 25px; background-color: #fffbeb; border-left: 4px solid #f59e0b; border-radius: 8px;">
                                <h3 style="margin: 0 0 10px; color: #1a202c; font-size: 20px; font-weight: 600;">{{taskTitle}}</h3>
                                <p style="margin: 0 0 15px; color: #4a5568; font-size: 15px; line-height: 1.6;">{{taskDescription}}</p>
                                <div style="margin-top: 15px; padding-top: 15px; border-top: 1px solid #fde68a;">
                                    <p style="margin: 0; color: #92400e; font-size: 14px;">
                                        <strong>Type :</strong> {{taskType}}<br>
                                        <strong>Demandé par :</strong> {{requesterName}}<br>
                                        <strong>Date :</strong> {{requestDate}}
                                    </p>
                                </div>
                            </div>
                            <!-- CTA Buttons -->
                            <table role="presentation" style="width: 100%; margin: 30px 0;">
                                <tr>
                                    <td style="text-align: center;">
                                        <a href="{{approveUrl}}" style="display: inline-block; padding: 14px 32px; margin: 0 10px 10px 0; background: linear-gradient(135deg, #10b981 0%, #059669 100%); color: #ffffff; text-decoration: none; border-radius: 8px; font-weight: 600; font-size: 16px;">Approuver</a>
                                        <a href="{{rejectUrl}}" style="display: inline-block; padding: 14px 32px; margin: 0 0 10px 10px; background-color: #fee2e2; color: #991b1b; text-decoration: none; border-radius: 8px; font-weight: 600; font-size: 16px;">Refuser</a>
                                    </td>
                                </tr>
                            </table>
                            <p style="margin: 20px 0 0; color: #9ca3af; font-size: 14px; line-height: 1.6;">
                                <em>Cette demande expire le {{expirationDate}}.</em>
                            </p>
                        </td>
                    </tr>
                    <!-- Footer -->
                    <tr>
                        <td style="padding: 30px 40px; background-color: #edf2f7; border-radius: 0 0 12px 12px; text-align: center;">
                            <p style="margin: 0 0 10px; color: #718096; font-size: 14px;">
                                Vous pouvez également répondre directement depuis votre espace Viridial.
                            </p>
                            <p style="margin: 0; color: #a0aec0; font-size: 12px;">© 2024 Viridial. Tous droits réservés.</p>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
</body>
</html>',
    NULL,
    true,
    true,
    '["approverName", "requesterName", "taskTitle", "taskDescription", "taskType", "requestDate", "approveUrl", "rejectUrl", "expirationDate"]',
    NOW(),
    NOW()
) ON CONFLICT DO NOTHING;

-- =====================================================
-- 8. PRICE_ALERT - Alerte de prix
-- =====================================================
INSERT INTO email_templates (name, description, type, subject, body, organization_id, active, is_default, available_variables, created_at, updated_at)
VALUES (
    'price_alert',
    'Alerte de changement de prix',
    'NOTIFICATION',
    'Alerte prix : {{propertyTitle}} - {{priceChange}}',
    '<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Alerte de prix</title>
</head>
<body style="margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, ''Segoe UI'', Roboto, ''Helvetica Neue'', Arial, sans-serif; background-color: #f5f7fa;">
    <table role="presentation" style="width: 100%; border-collapse: collapse;">
        <tr>
            <td style="padding: 40px 20px; text-align: center;">
                <table role="presentation" style="max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 12px; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);">
                    <!-- Header -->
                    <tr>
                        <td style="padding: 40px 40px 20px; background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%); border-radius: 12px 12px 0 0; text-align: center;">
                            <div style="width: 80px; height: 80px; margin: 0 auto 20px; background-color: rgba(255, 255, 255, 0.2); border-radius: 50%; display: flex; align-items: center; justify-content: center;">
                                <span style="font-size: 40px;">💰</span>
                            </div>
                            <h1 style="margin: 0; color: #ffffff; font-size: 28px; font-weight: 700;">Alerte de prix</h1>
                        </td>
                    </tr>
                    <!-- Content -->
                    <tr>
                        <td style="padding: 40px;">
                            <h2 style="margin: 0 0 20px; color: #1a202c; font-size: 24px; font-weight: 600;">Bonjour {{firstName}},</h2>
                            <p style="margin: 0 0 20px; color: #4a5568; font-size: 16px; line-height: 1.6;">
                                Le prix de la propriété que vous suivez a changé !
                            </p>
                            <!-- Price Change Card -->
                            <div style="margin: 30px 0; padding: 25px; background-color: #fef2f2; border-left: 4px solid #ef4444; border-radius: 8px;">
                                <h3 style="margin: 0 0 15px; color: #1a202c; font-size: 20px; font-weight: 600;">{{propertyTitle}}</h3>
                                <p style="margin: 0 0 15px; color: #4a5568; font-size: 15px;">{{propertyAddress}}, {{propertyCity}}</p>
                                <div style="display: flex; align-items: center; gap: 15px; margin-top: 20px;">
                                    <div style="flex: 1; padding: 15px; background-color: #ffffff; border-radius: 6px; text-align: center;">
                                        <span style="color: #718096; font-size: 12px; display: block; margin-bottom: 5px;">Ancien prix</span>
                                        <p style="margin: 0; color: #9ca3af; font-size: 18px; font-weight: 600; text-decoration: line-through;">{{oldPrice}} €</p>
                                    </div>
                                    <div style="font-size: 24px; color: #ef4444;">→</div>
                                    <div style="flex: 1; padding: 15px; background-color: #ffffff; border-radius: 6px; text-align: center;">
                                        <span style="color: #718096; font-size: 12px; display: block; margin-bottom: 5px;">Nouveau prix</span>
                                        <p style="margin: 0; color: #ef4444; font-size: 24px; font-weight: 700;">{{newPrice}} €</p>
                                    </div>
                                </div>
                                <div style="margin-top: 15px; padding: 12px; background-color: #fee2e2; border-radius: 6px; text-align: center;">
                                    <p style="margin: 0; color: #991b1b; font-size: 16px; font-weight: 600;">{{priceChange}}</p>
                                </div>
                            </div>
                            <!-- CTA Button -->
                            <table role="presentation" style="width: 100%; margin: 30px 0;">
                                <tr>
                                    <td style="text-align: center;">
                                        <a href="{{propertyUrl}}" style="display: inline-block; padding: 14px 32px; background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%); color: #ffffff; text-decoration: none; border-radius: 8px; font-weight: 600; font-size: 16px;">Voir la propriété</a>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                    <!-- Footer -->
                    <tr>
                        <td style="padding: 30px 40px; background-color: #edf2f7; border-radius: 0 0 12px 12px; text-align: center;">
                            <p style="margin: 0 0 10px; color: #718096; font-size: 14px;">
                                Vous recevez cette alerte car vous suivez cette propriété. <a href="{{unsubscribeUrl}}" style="color: #ef4444; text-decoration: none;">Se désabonner</a>
                            </p>
                            <p style="margin: 0; color: #a0aec0; font-size: 12px;">© 2024 Viridial. Tous droits réservés.</p>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
</body>
</html>',
    NULL,
    true,
    true,
    '["firstName", "propertyTitle", "propertyAddress", "propertyCity", "oldPrice", "newPrice", "priceChange", "propertyUrl", "unsubscribeUrl"]',
    NOW(),
    NOW()
) ON CONFLICT DO NOTHING;

-- =====================================================
-- 9. VISIT_CONFIRMATION - Confirmation de visite
-- =====================================================
INSERT INTO email_templates (name, description, type, subject, body, organization_id, active, is_default, available_variables, created_at, updated_at)
VALUES (
    'visit_confirmation',
    'Confirmation de rendez-vous de visite',
    'CONFIRMATION',
    'Confirmation de votre visite : {{propertyTitle}}',
    '<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Confirmation de visite</title>
</head>
<body style="margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, ''Segoe UI'', Roboto, ''Helvetica Neue'', Arial, sans-serif; background-color: #f5f7fa;">
    <table role="presentation" style="width: 100%; border-collapse: collapse;">
        <tr>
            <td style="padding: 40px 20px; text-align: center;">
                <table role="presentation" style="max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 12px; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);">
                    <!-- Header -->
                    <tr>
                        <td style="padding: 40px 40px 20px; background: linear-gradient(135deg, #06b6d4 0%, #0891b2 100%); border-radius: 12px 12px 0 0; text-align: center;">
                            <div style="width: 80px; height: 80px; margin: 0 auto 20px; background-color: rgba(255, 255, 255, 0.2); border-radius: 50%; display: flex; align-items: center; justify-content: center;">
                                <span style="font-size: 40px;">📅</span>
                            </div>
                            <h1 style="margin: 0; color: #ffffff; font-size: 28px; font-weight: 700;">Visite confirmée</h1>
                        </td>
                    </tr>
                    <!-- Content -->
                    <tr>
                        <td style="padding: 40px;">
                            <h2 style="margin: 0 0 20px; color: #1a202c; font-size: 24px; font-weight: 600;">Bonjour {{visitorName}},</h2>
                            <p style="margin: 0 0 20px; color: #4a5568; font-size: 16px; line-height: 1.6;">
                                Votre demande de visite pour <strong>"{{propertyTitle}}"</strong> a été confirmée.
                            </p>
                            <!-- Visit Details Card -->
                            <div style="margin: 30px 0; padding: 25px; background-color: #ecfeff; border-left: 4px solid #06b6d4; border-radius: 8px;">
                                <h3 style="margin: 0 0 20px; color: #1a202c; font-size: 20px; font-weight: 600;">Détails de la visite</h3>
                                <table role="presentation" style="width: 100%; border-collapse: collapse;">
                                    <tr>
                                        <td style="padding: 8px 0; color: #0e7490; font-size: 14px; font-weight: 600; width: 120px;">Date :</td>
                                        <td style="padding: 8px 0; color: #1a202c; font-size: 15px;">{{visitDate}}</td>
                                    </tr>
                                    <tr>
                                        <td style="padding: 8px 0; color: #0e7490; font-size: 14px; font-weight: 600;">Heure :</td>
                                        <td style="padding: 8px 0; color: #1a202c; font-size: 15px;">{{visitTime}}</td>
                                    </tr>
                                    <tr>
                                        <td style="padding: 8px 0; color: #0e7490; font-size: 14px; font-weight: 600;">Propriété :</td>
                                        <td style="padding: 8px 0; color: #1a202c; font-size: 15px;">{{propertyTitle}}</td>
                                    </tr>
                                    <tr>
                                        <td style="padding: 8px 0; color: #0e7490; font-size: 14px; font-weight: 600;">Adresse :</td>
                                        <td style="padding: 8px 0; color: #1a202c; font-size: 15px;">{{propertyAddress}}</td>
                                    </tr>
                                    <tr>
                                        <td style="padding: 8px 0; color: #0e7490; font-size: 14px; font-weight: 600;">Contact :</td>
                                        <td style="padding: 8px 0; color: #1a202c; font-size: 15px;">{{contactName}}<br>{{contactPhone}}</td>
                                    </tr>
                                </table>
                            </div>
                            <!-- CTA Button -->
                            <table role="presentation" style="width: 100%; margin: 30px 0;">
                                <tr>
                                    <td style="text-align: center;">
                                        <a href="{{visitUrl}}" style="display: inline-block; padding: 14px 32px; background: linear-gradient(135deg, #06b6d4 0%, #0891b2 100%); color: #ffffff; text-decoration: none; border-radius: 8px; font-weight: 600; font-size: 16px;">Voir les détails</a>
                                    </td>
                                </tr>
                            </table>
                            <div style="margin: 20px 0; padding: 15px; background-color: #f0f9ff; border-radius: 6px;">
                                <p style="margin: 0; color: #0c4a6e; font-size: 14px;">
                                    💡 <strong>Rappel :</strong> Vous recevrez un rappel 24h avant votre visite.
                                </p>
                            </div>
                        </td>
                    </tr>
                    <!-- Footer -->
                    <tr>
                        <td style="padding: 30px 40px; background-color: #edf2f7; border-radius: 0 0 12px 12px; text-align: center;">
                            <p style="margin: 0 0 10px; color: #718096; font-size: 14px;">
                                Pour modifier ou annuler votre visite, connectez-vous à votre espace Viridial.
                            </p>
                            <p style="margin: 0; color: #a0aec0; font-size: 12px;">© 2024 Viridial. Tous droits réservés.</p>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
</body>
</html>',
    NULL,
    true,
    true,
    '["visitorName", "propertyTitle", "propertyAddress", "visitDate", "visitTime", "contactName", "contactPhone", "visitUrl"]',
    NOW(),
    NOW()
) ON CONFLICT DO NOTHING;

-- =====================================================
-- 10. PROPERTY_FAVORITE - Propriété ajoutée aux favoris
-- =====================================================
INSERT INTO email_templates (name, description, type, subject, body, organization_id, active, is_default, available_variables, created_at, updated_at)
VALUES (
    'property_favorite',
    'Notification de propriété ajoutée aux favoris',
    'NOTIFICATION',
    'Nouvelle propriété dans vos favoris : {{propertyTitle}}',
    '<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Propriété ajoutée aux favoris</title>
</head>
<body style="margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, ''Segoe UI'', Roboto, ''Helvetica Neue'', Arial, sans-serif; background-color: #f5f7fa;">
    <table role="presentation" style="width: 100%; border-collapse: collapse;">
        <tr>
            <td style="padding: 40px 20px; text-align: center;">
                <table role="presentation" style="max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 12px; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);">
                    <!-- Header -->
                    <tr>
                        <td style="padding: 40px 40px 20px; background: linear-gradient(135deg, #ec4899 0%, #db2777 100%); border-radius: 12px 12px 0 0; text-align: center;">
                            <div style="width: 80px; height: 80px; margin: 0 auto 20px; background-color: rgba(255, 255, 255, 0.2); border-radius: 50%; display: flex; align-items: center; justify-content: center;">
                                <span style="font-size: 40px;">❤️</span>
                            </div>
                            <h1 style="margin: 0; color: #ffffff; font-size: 28px; font-weight: 700;">Ajoutée aux favoris</h1>
                        </td>
                    </tr>
                    <!-- Content -->
                    <tr>
                        <td style="padding: 40px;">
                            <h2 style="margin: 0 0 20px; color: #1a202c; font-size: 24px; font-weight: 600;">Bonjour {{firstName}},</h2>
                            <p style="margin: 0 0 20px; color: #4a5568; font-size: 16px; line-height: 1.6;">
                                Vous avez ajouté <strong>"{{propertyTitle}}"</strong> à vos favoris.
                            </p>
                            <!-- Property Card -->
                            <div style="margin: 30px 0; padding: 0; background-color: #fdf2f8; border-radius: 8px; overflow: hidden; border: 1px solid #fbcfe8;">
                                <div style="padding: 20px;">
                                    <h3 style="margin: 0 0 10px; color: #1a202c; font-size: 20px; font-weight: 600;">{{propertyTitle}}</h3>
                                    <p style="margin: 0 0 15px; color: #4a5568; font-size: 15px;">{{propertyAddress}}, {{propertyCity}}</p>
                                    <div style="display: flex; gap: 20px; margin-top: 15px; flex-wrap: wrap;">
                                        <div>
                                            <span style="color: #9f1239; font-size: 13px;">Prix</span>
                                            <p style="margin: 5px 0 0; color: #1a202c; font-size: 18px; font-weight: 700;">{{propertyPrice}} €</p>
                                        </div>
                                        <div>
                                            <span style="color: #9f1239; font-size: 13px;">Surface</span>
                                            <p style="margin: 5px 0 0; color: #1a202c; font-size: 18px; font-weight: 700;">{{propertyArea}} m²</p>
                                        </div>
                                        <div>
                                            <span style="color: #9f1239; font-size: 13px;">Pièces</span>
                                            <p style="margin: 5px 0 0; color: #1a202c; font-size: 18px; font-weight: 700;">{{propertyRooms}}</p>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <!-- CTA Button -->
                            <table role="presentation" style="width: 100%; margin: 30px 0;">
                                <tr>
                                    <td style="text-align: center;">
                                        <a href="{{propertyUrl}}" style="display: inline-block; padding: 14px 32px; background: linear-gradient(135deg, #ec4899 0%, #db2777 100%); color: #ffffff; text-decoration: none; border-radius: 8px; font-weight: 600; font-size: 16px;">Voir la propriété</a>
                                    </td>
                                </tr>
                            </table>
                            <p style="margin: 20px 0 0; color: #4a5568; font-size: 15px; line-height: 1.6;">
                                💡 <strong>Astuce :</strong> Vous pouvez créer une alerte de prix pour être notifié en cas de changement.
                            </p>
                        </td>
                    </tr>
                    <!-- Footer -->
                    <tr>
                        <td style="padding: 30px 40px; background-color: #edf2f7; border-radius: 0 0 12px 12px; text-align: center;">
                            <p style="margin: 0 0 10px; color: #718096; font-size: 14px;">
                                Gérez vos favoris depuis <a href="{{favoritesUrl}}" style="color: #ec4899; text-decoration: none;">votre espace</a>
                            </p>
                            <p style="margin: 0; color: #a0aec0; font-size: 12px;">© 2024 Viridial. Tous droits réservés.</p>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
</body>
</html>',
    NULL,
    true,
    true,
    '["firstName", "propertyTitle", "propertyAddress", "propertyCity", "propertyPrice", "propertyArea", "propertyRooms", "propertyUrl", "favoritesUrl"]',
    NOW(),
    NOW()
) ON CONFLICT DO NOTHING;

-- =====================================================
-- 11. WEEKLY_SUMMARY - Résumé hebdomadaire
-- =====================================================
INSERT INTO email_templates (name, description, type, subject, body, organization_id, active, is_default, available_variables, created_at, updated_at)
VALUES (
    'weekly_summary',
    'Résumé hebdomadaire des activités',
    'NOTIFICATION',
    'Votre résumé hebdomadaire Viridial',
    '<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Résumé hebdomadaire</title>
</head>
<body style="margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, ''Segoe UI'', Roboto, ''Helvetica Neue'', Arial, sans-serif; background-color: #f5f7fa;">
    <table role="presentation" style="width: 100%; border-collapse: collapse;">
        <tr>
            <td style="padding: 40px 20px; text-align: center;">
                <table role="presentation" style="max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 12px; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);">
                    <!-- Header -->
                    <tr>
                        <td style="padding: 40px 40px 20px; background: linear-gradient(135deg, #6366f1 0%, #4f46e5 100%); border-radius: 12px 12px 0 0; text-align: center;">
                            <h1 style="margin: 0; color: #ffffff; font-size: 28px; font-weight: 700;">📊 Résumé hebdomadaire</h1>
                            <p style="margin: 10px 0 0; color: rgba(255, 255, 255, 0.9); font-size: 16px;">{{weekPeriod}}</p>
                        </td>
                    </tr>
                    <!-- Content -->
                    <tr>
                        <td style="padding: 40px;">
                            <h2 style="margin: 0 0 20px; color: #1a202c; font-size: 24px; font-weight: 600;">Bonjour {{firstName}},</h2>
                            <p style="margin: 0 0 30px; color: #4a5568; font-size: 16px; line-height: 1.6;">
                                Voici un résumé de votre activité cette semaine sur Viridial :
                            </p>
                            <!-- Stats Grid -->
                            <table role="presentation" style="width: 100%; border-collapse: collapse; margin: 30px 0;">
                                <tr>
                                    <td style="padding: 20px; background-color: #eef2ff; border-radius: 8px; text-align: center; width: 50%;">
                                        <p style="margin: 0; color: #6366f1; font-size: 32px; font-weight: 700;">{{totalViews}}</p>
                                        <p style="margin: 5px 0 0; color: #4a5568; font-size: 14px;">Vues totales</p>
                                    </td>
                                    <td style="width: 20px;"></td>
                                    <td style="padding: 20px; background-color: #f0fdf4; border-radius: 8px; text-align: center; width: 50%;">
                                        <p style="margin: 0; color: #10b981; font-size: 32px; font-weight: 700;">{{totalContacts}}</p>
                                        <p style="margin: 5px 0 0; color: #4a5568; font-size: 14px;">Nouveaux contacts</p>
                                    </td>
                                </tr>
                                <tr><td colspan="3" style="height: 20px;"></td></tr>
                                <tr>
                                    <td style="padding: 20px; background-color: #fef3c7; border-radius: 8px; text-align: center; width: 50%;">
                                        <p style="margin: 0; color: #f59e0b; font-size: 32px; font-weight: 700;">{{totalFavorites}}</p>
                                        <p style="margin: 5px 0 0; color: #4a5568; font-size: 14px;">Ajouts favoris</p>
                                    </td>
                                    <td style="width: 20px;"></td>
                                    <td style="padding: 20px; background-color: #fce7f3; border-radius: 8px; text-align: center; width: 50%;">
                                        <p style="margin: 0; color: #ec4899; font-size: 32px; font-weight: 700;">{{newProperties}}</p>
                                        <p style="margin: 5px 0 0; color: #4a5568; font-size: 14px;">Nouvelles annonces</p>
                                    </td>
                                </tr>
                            </table>
                            <!-- CTA Button -->
                            <table role="presentation" style="width: 100%; margin: 30px 0;">
                                <tr>
                                    <td style="text-align: center;">
                                        <a href="{{dashboardUrl}}" style="display: inline-block; padding: 14px 32px; background: linear-gradient(135deg, #6366f1 0%, #4f46e5 100%); color: #ffffff; text-decoration: none; border-radius: 8px; font-weight: 600; font-size: 16px;">Voir le tableau de bord</a>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                    <!-- Footer -->
                    <tr>
                        <td style="padding: 30px 40px; background-color: #edf2f7; border-radius: 0 0 12px 12px; text-align: center;">
                            <p style="margin: 0 0 10px; color: #718096; font-size: 14px;">
                                Continuez à publier des annonces de qualité pour augmenter votre visibilité !
                            </p>
                            <p style="margin: 0; color: #a0aec0; font-size: 12px;">© 2024 Viridial. Tous droits réservés.</p>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
</body>
</html>',
    NULL,
    true,
    true,
    '["firstName", "weekPeriod", "totalViews", "totalContacts", "totalFavorites", "newProperties", "dashboardUrl"]',
    NOW(),
    NOW()
) ON CONFLICT DO NOTHING;

-- =====================================================
-- 12. PROPERTY_APPROVAL - Approbation de propriété
-- =====================================================
INSERT INTO email_templates (name, description, type, subject, body, organization_id, active, is_default, available_variables, created_at, updated_at)
VALUES (
    'property_approval',
    'Notification d''approbation de propriété',
    'NOTIFICATION',
    'Votre annonce "{{propertyTitle}}" a été {{approvalStatus}}',
    '<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Approbation de propriété</title>
</head>
<body style="margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, ''Segoe UI'', Roboto, ''Helvetica Neue'', Arial, sans-serif; background-color: #f5f7fa;">
    <table role="presentation" style="width: 100%; border-collapse: collapse;">
        <tr>
            <td style="padding: 40px 20px; text-align: center;">
                <table role="presentation" style="max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 12px; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);">
                    <!-- Header -->
                    <tr>
                        <td style="padding: 40px 40px 20px; background: linear-gradient(135deg, #10b981 0%, #059669 100%); border-radius: 12px 12px 0 0; text-align: center;">
                            <div style="width: 80px; height: 80px; margin: 0 auto 20px; background-color: rgba(255, 255, 255, 0.2); border-radius: 50%; display: flex; align-items: center; justify-content: center;">
                                <span style="font-size: 40px;">{{approvalIcon}}</span>
                            </div>
                            <h1 style="margin: 0; color: #ffffff; font-size: 28px; font-weight: 700;">{{approvalStatusText}}</h1>
                        </td>
                    </tr>
                    <!-- Content -->
                    <tr>
                        <td style="padding: 40px;">
                            <h2 style="margin: 0 0 20px; color: #1a202c; font-size: 24px; font-weight: 600;">Bonjour {{firstName}},</h2>
                            <p style="margin: 0 0 20px; color: #4a5568; font-size: 16px; line-height: 1.6;">
                                Votre annonce <strong>"{{propertyTitle}}"</strong> a été {{approvalStatus}}.
                            </p>
                            <div style="margin: 30px 0; padding: 20px; background-color: {{approvalBackgroundColor}}; border-left: 4px solid {{approvalBorderColor}}; border-radius: 4px;">
                                <p style="margin: 0; color: {{approvalTextColor}}; font-size: 15px;">
                                    {{approvalMessage}}
                                </p>
                                {{rejectionReason}}
                            </div>
                            <!-- Property Info -->
                            <div style="margin: 20px 0; padding: 15px; background-color: #f7fafc; border-radius: 8px;">
                                <p style="margin: 0 0 5px; color: #718096; font-size: 13px;">Annonce :</p>
                                <p style="margin: 0; color: #1a202c; font-size: 16px; font-weight: 600;">{{propertyTitle}}</p>
                                <p style="margin: 5px 0 0; color: #4a5568; font-size: 14px;">{{propertyAddress}}</p>
                            </div>
                            <!-- CTA Button -->
                            <table role="presentation" style="width: 100%; margin: 30px 0;">
                                <tr>
                                    <td style="text-align: center;">
                                        <a href="{{propertyUrl}}" style="display: inline-block; padding: 14px 32px; background: linear-gradient(135deg, #10b981 0%, #059669 100%); color: #ffffff; text-decoration: none; border-radius: 8px; font-weight: 600; font-size: 16px;">{{actionButtonText}}</a>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                    <!-- Footer -->
                    <tr>
                        <td style="padding: 30px 40px; background-color: #edf2f7; border-radius: 0 0 12px 12px; text-align: center;">
                            <p style="margin: 0 0 10px; color: #718096; font-size: 14px;">
                                {{footerMessage}}
                            </p>
                            <p style="margin: 0; color: #a0aec0; font-size: 12px;">© 2024 Viridial. Tous droits réservés.</p>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
</body>
</html>',
    NULL,
    true,
    true,
    '["firstName", "propertyTitle", "propertyAddress", "approvalStatus", "approvalStatusText", "approvalIcon", "approvalBackgroundColor", "approvalBorderColor", "approvalTextColor", "approvalMessage", "rejectionReason", "actionButtonText", "footerMessage", "propertyUrl"]',
    NOW(),
    NOW()
) ON CONFLICT DO NOTHING;

