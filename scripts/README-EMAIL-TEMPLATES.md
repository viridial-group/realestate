# Templates d'Email - Viridial

Ce document décrit tous les templates d'email disponibles dans le système Viridial.

## Installation

Pour installer tous les templates dans la base de données :

```bash
psql -U postgres -d realestate_db -f scripts/seed-email-templates.sql
```

## Structure des Templates

Chaque template contient :
- **name** : Identifiant unique du template
- **type** : Catégorie du template (WELCOME, CONFIRMATION, INVITATION, NOTIFICATION, etc.)
- **subject** : Sujet de l'email (peut contenir des variables `{{variable}}`)
- **body** : Corps HTML de l'email (peut contenir des variables `{{variable}}`)
- **available_variables** : Liste JSON des variables disponibles

## Variables

Les variables dans les templates utilisent la syntaxe `{{variableName}}`. Elles sont remplacées lors de l'envoi de l'email.

## Templates Disponibles

### 1. WELCOME - Email de bienvenue
**Nom** : `welcome_email`  
**Type** : `WELCOME`  
**Variables** :
- `firstName` : Prénom de l'utilisateur
- `appUrl` : URL de l'application

**Exemple d'utilisation** :
```java
Map<String, Object> variables = new HashMap<>();
variables.put("firstName", "Jean");
variables.put("appUrl", "https://viridial.com");
emailService.sendEmailFromTemplate("welcome_email", "jean@example.com", userId, orgId, variables);
```

---

### 2. CONFIRMATION - Confirmation d'inscription
**Nom** : `registration_confirmation`  
**Type** : `CONFIRMATION`  
**Variables** :
- `firstName` : Prénom de l'utilisateur
- `email` : Email de l'utilisateur
- `registrationDate` : Date d'inscription
- `loginUrl` : URL de connexion

---

### 3. INVITATION - Invitation à rejoindre une organisation
**Nom** : `organization_invitation`  
**Type** : `INVITATION`  
**Variables** :
- `recipientName` : Nom du destinataire
- `inviterName` : Nom de la personne qui invite
- `organizationName` : Nom de l'organisation
- `organizationDescription` : Description de l'organisation
- `acceptUrl` : URL pour accepter l'invitation
- `declineUrl` : URL pour décliner l'invitation
- `expirationDate` : Date d'expiration de l'invitation

---

### 4. PASSWORD_RESET - Réinitialisation de mot de passe
**Nom** : `password_reset`  
**Type** : `PASSWORD_RESET`  
**Variables** :
- `firstName` : Prénom de l'utilisateur
- `resetUrl` : URL de réinitialisation (avec token)

---

### 5. PROPERTY_PUBLISHED - Propriété publiée
**Nom** : `property_published`  
**Type** : `NOTIFICATION`  
**Variables** :
- `firstName` : Prénom du propriétaire
- `propertyTitle` : Titre de la propriété
- `propertyAddress` : Adresse de la propriété
- `propertyCity` : Ville de la propriété
- `propertyPrice` : Prix de la propriété
- `propertyArea` : Surface de la propriété
- `propertyUrl` : URL de la propriété

---

### 6. CONTACT_MESSAGE - Nouveau message de contact
**Nom** : `contact_message`  
**Type** : `NOTIFICATION`  
**Variables** :
- `recipientName` : Nom du destinataire
- `senderName` : Nom de l'expéditeur
- `senderEmail` : Email de l'expéditeur
- `senderPhone` : Téléphone de l'expéditeur (optionnel, peut être vide)
- `message` : Contenu du message
- `propertyTitle` : Titre de la propriété concernée
- `propertyAddress` : Adresse de la propriété
- `messageUrl` : URL pour répondre au message

**Note** : Pour `senderPhone`, si la variable est vide, elle ne sera pas affichée dans le template.

---

### 7. APPROVAL_REQUEST - Demande d'approbation
**Nom** : `approval_request`  
**Type** : `NOTIFICATION`  
**Variables** :
- `approverName` : Nom de la personne qui doit approuver
- `requesterName` : Nom de la personne qui demande
- `taskTitle` : Titre de la tâche
- `taskDescription` : Description de la tâche
- `taskType` : Type de tâche
- `requestDate` : Date de la demande
- `approveUrl` : URL pour approuver
- `rejectUrl` : URL pour refuser
- `expirationDate` : Date d'expiration

---

### 8. PRICE_ALERT - Alerte de changement de prix
**Nom** : `price_alert`  
**Type** : `NOTIFICATION`  
**Variables** :
- `firstName` : Prénom de l'utilisateur
- `propertyTitle` : Titre de la propriété
- `propertyAddress` : Adresse de la propriété
- `propertyCity` : Ville de la propriété
- `oldPrice` : Ancien prix
- `newPrice` : Nouveau prix
- `priceChange` : Description du changement (ex: "-5%", "+10 000 €")
- `propertyUrl` : URL de la propriété
- `unsubscribeUrl` : URL pour se désabonner

---

### 9. VISIT_CONFIRMATION - Confirmation de visite
**Nom** : `visit_confirmation`  
**Type** : `CONFIRMATION`  
**Variables** :
- `visitorName` : Nom du visiteur
- `propertyTitle` : Titre de la propriété
- `propertyAddress` : Adresse de la propriété
- `visitDate` : Date de la visite
- `visitTime` : Heure de la visite
- `contactName` : Nom du contact
- `contactPhone` : Téléphone du contact
- `visitUrl` : URL pour voir les détails de la visite

---

### 10. PROPERTY_FAVORITE - Propriété ajoutée aux favoris
**Nom** : `property_favorite`  
**Type** : `NOTIFICATION`  
**Variables** :
- `firstName` : Prénom de l'utilisateur
- `propertyTitle` : Titre de la propriété
- `propertyAddress` : Adresse de la propriété
- `propertyCity` : Ville de la propriété
- `propertyPrice` : Prix de la propriété
- `propertyArea` : Surface de la propriété
- `propertyRooms` : Nombre de pièces
- `propertyUrl` : URL de la propriété
- `favoritesUrl` : URL vers la liste des favoris

---

### 11. WEEKLY_SUMMARY - Résumé hebdomadaire
**Nom** : `weekly_summary`  
**Type** : `NOTIFICATION`  
**Variables** :
- `firstName` : Prénom de l'utilisateur
- `weekPeriod` : Période de la semaine (ex: "Du 1er au 7 janvier 2024")
- `totalViews` : Nombre total de vues
- `totalContacts` : Nombre total de contacts
- `totalFavorites` : Nombre total d'ajouts aux favoris
- `newProperties` : Nombre de nouvelles propriétés
- `dashboardUrl` : URL du tableau de bord

---

### 12. PROPERTY_APPROVAL - Approbation de propriété
**Nom** : `property_approval`  
**Type** : `NOTIFICATION`  
**Variables** :
- `firstName` : Prénom du propriétaire
- `propertyTitle` : Titre de la propriété
- `propertyAddress` : Adresse de la propriété
- `approvalStatus` : Statut d'approbation (ex: "approuvée", "refusée")
- `approvalStatusText` : Texte du statut (ex: "Annonce approuvée", "Annonce refusée")
- `approvalIcon` : Icône (ex: "✓" ou "✗")
- `approvalBackgroundColor` : Couleur de fond (ex: "#f0fdf4" pour approuvé, "#fef2f2" pour refusé)
- `approvalBorderColor` : Couleur de bordure (ex: "#10b981" pour approuvé, "#ef4444" pour refusé)
- `approvalTextColor` : Couleur du texte (ex: "#166534" pour approuvé, "#991b1b" pour refusé)
- `approvalMessage` : Message d'approbation ou de refus
- `rejectionReason` : Raison du refus (peut être vide, format HTML)
- `actionButtonText` : Texte du bouton d'action (ex: "Voir mon annonce" ou "Modifier mon annonce")
- `footerMessage` : Message du footer
- `propertyUrl` : URL de la propriété

**Exemple d'utilisation pour une approbation** :
```java
Map<String, Object> variables = new HashMap<>();
variables.put("firstName", "Jean");
variables.put("propertyTitle", "Appartement à Paris");
variables.put("propertyAddress", "123 Rue de la Paix");
variables.put("approvalStatus", "approuvée");
variables.put("approvalStatusText", "Annonce approuvée");
variables.put("approvalIcon", "✓");
variables.put("approvalBackgroundColor", "#f0fdf4");
variables.put("approvalBorderColor", "#10b981");
variables.put("approvalTextColor", "#166534");
variables.put("approvalMessage", "✅ Votre annonce est maintenant publiée et visible par tous les utilisateurs de Viridial.");
variables.put("rejectionReason", ""); // Vide pour une approbation
variables.put("actionButtonText", "Voir mon annonce");
variables.put("footerMessage", "Félicitations ! Votre annonce est en ligne.");
variables.put("propertyUrl", "https://viridial.com/properties/123");
```

**Exemple d'utilisation pour un refus** :
```java
Map<String, Object> variables = new HashMap<>();
variables.put("firstName", "Jean");
variables.put("propertyTitle", "Appartement à Paris");
variables.put("propertyAddress", "123 Rue de la Paix");
variables.put("approvalStatus", "refusée");
variables.put("approvalStatusText", "Annonce refusée");
variables.put("approvalIcon", "✗");
variables.put("approvalBackgroundColor", "#fef2f2");
variables.put("approvalBorderColor", "#ef4444");
variables.put("approvalTextColor", "#991b1b");
variables.put("approvalMessage", "❌ Votre annonce n'a pas été approuvée.");
variables.put("rejectionReason", "<p style=\"margin: 10px 0 0; color: #991b1b; font-size: 14px;\"><strong>Raison :</strong> Photos de mauvaise qualité</p>");
variables.put("actionButtonText", "Modifier mon annonce");
variables.put("footerMessage", "Vous pouvez modifier votre annonce et la soumettre à nouveau pour approbation.");
variables.put("propertyUrl", "https://viridial.com/properties/123/edit");
```

## Design

Tous les templates utilisent un design moderne et responsive avec :
- **Gradients colorés** pour les en-têtes
- **Cartes avec ombres** pour le contenu
- **Boutons CTA** avec gradients
- **Couleurs cohérentes** selon le type de notification
- **Responsive** pour mobile et desktop
- **Accessibilité** avec des contrastes appropriés

## Personnalisation

Les templates peuvent être personnalisés par organisation. Pour créer un template personnalisé :

1. Copiez un template existant
2. Modifiez le `name` et l'`organization_id`
3. Personnalisez le `body` HTML
4. Activez-le avec `active = true` et `is_default = true`

## Bonnes Pratiques

1. **Toujours tester** les templates avec des données réelles avant la production
2. **Vérifier les variables** : s'assurer que toutes les variables sont fournies lors de l'envoi
3. **Tester sur différents clients email** : Gmail, Outlook, Apple Mail, etc.
4. **Vérifier le responsive** sur mobile et desktop
5. **Utiliser des URLs absolues** pour les images et liens
6. **Éviter les images externes** qui peuvent être bloquées

## Support

Pour toute question ou problème avec les templates, contactez l'équipe de développement.

