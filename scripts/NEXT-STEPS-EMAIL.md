# Prochaines étapes - Intégration Email

## ✅ Terminé

1. **EmailServiceClient créé** dans le module `common`
2. **Templates d'email créés** (12 templates modernes)
3. **Intégration dans Identity Service** :
   - Email de bienvenue (`welcome_email`) après inscription
   - Email de confirmation (`registration_confirmation`) après inscription avec organisation
4. **Intégration dans Property Service** :
   - Email de publication (`property_published`) quand une propriété est publiée
   - Email de nouveau message (`contact_message`) quand un message de contact est reçu
5. **Configurations ajoutées** dans les `application.yml`

## 🔧 Corrections effectuées

1. **Erreur de compilation corrigée** : `user.getOrganizationId()` → `null` (les particuliers n'ont pas d'organisation)
2. **Configurations ajoutées** :
   - `services.emailing.url` dans identity-service et property-service
   - `app.frontend.url` dans identity-service et property-service

## 📋 Prochaines intégrations à faire

### 1. Password Reset (Identity Service) - PRIORITÉ HAUTE

**Template** : `password_reset`  
**Endpoint à créer** : `/api/identity/auth/forgot-password` et `/api/identity/auth/reset-password`

**Fonctionnalités** :
- Générer un token de réinitialisation (JWT avec expiration 24h)
- Stocker le token dans Redis ou base de données
- Envoyer l'email avec le lien de réinitialisation
- Valider le token lors de la réinitialisation

**Code à ajouter dans AuthService** :
```java
public void requestPasswordReset(String email) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    
    // Générer token de réinitialisation
    String resetToken = jwtService.generatePasswordResetToken(email);
    
    // Stocker le token (Redis ou DB)
    // ...
    
    // Envoyer l'email
    Map<String, Object> variables = new HashMap<>();
    variables.put("firstName", user.getFirstName());
    variables.put("resetUrl", frontendUrl + "/reset-password?token=" + resetToken);
    
    emailServiceClient.sendEmailFromTemplateAsync(
        "password_reset",
        user.getEmail(),
        user.getId(),
        null, // organizationId
        variables,
        null
    );
}
```

### 2. Organization Invitation (Identity Service) - PRIORITÉ MOYENNE

**Template** : `organization_invitation`  
**Endpoint existant** : Vérifier s'il existe déjà dans `OrganizationUserService`

**Fonctionnalités** :
- Créer une invitation avec token
- Envoyer l'email avec lien d'acceptation/refus
- Gérer l'expiration de l'invitation

### 3. Property Approval (Property Service) - PRIORITÉ MOYENNE

**Template** : `property_approval`  
**Déclencheur** : Quand une propriété change de statut (APPROVED/REJECTED)

**Code à ajouter dans PropertyService** :
```java
private void sendPropertyApprovalEmail(Property property, String status, String reason, String authToken) {
    // Récupérer utilisateur
    // Préparer variables selon status (APPROVED/REJECTED)
    // Envoyer email avec template property_approval
}
```

### 4. Price Alert (Property Service) - PRIORITÉ BASSE

**Template** : `price_alert`  
**Déclencheur** : Quand le prix d'une propriété suivie change

**Code à ajouter dans PriceHistoryService** :
```java
public void notifyPriceChange(Property property, BigDecimal oldPrice, BigDecimal newPrice) {
    // Récupérer tous les utilisateurs qui suivent cette propriété
    // Pour chaque utilisateur, envoyer l'email d'alerte
}
```

### 5. Visit Confirmation (Property Service) - PRIORITÉ BASSE

**Template** : `visit_confirmation`  
**Déclencheur** : Quand une visite est confirmée

**Code à ajouter dans VisitAppointmentService** :
```java
public void confirmVisit(VisitAppointment visit) {
    // Envoyer email de confirmation au visiteur
    // Envoyer email de notification au propriétaire
}
```

### 6. Weekly Summary (Notification Service) - PRIORITÉ BASSE

**Template** : `weekly_summary`  
**Déclencheur** : Tâche planifiée hebdomadaire (cron job)

**Code à créer** :
```java
@Scheduled(cron = "0 0 9 * * MON") // Chaque lundi à 9h
public void sendWeeklySummaries() {
    // Pour chaque utilisateur actif
    // Calculer les statistiques de la semaine
    // Envoyer l'email de résumé
}
```

## 🧪 Tests à effectuer

1. **Tester l'envoi d'email de bienvenue** :
   - Inscrire un nouvel utilisateur
   - Vérifier que l'email est reçu
   - Vérifier le contenu de l'email

2. **Tester l'envoi d'email de confirmation** :
   - S'inscrire avec création d'organisation
   - Vérifier que l'email est reçu

3. **Tester l'envoi d'email de publication** :
   - Créer une propriété avec statut PUBLISHED
   - Vérifier que l'email est reçu par le propriétaire

4. **Tester l'envoi d'email de contact** :
   - Envoyer un message de contact pour une propriété
   - Vérifier que l'email est reçu par le propriétaire

## 🔍 Vérifications

1. **Vérifier que le service emailing est démarré** :
   ```bash
   curl http://localhost:8088/actuator/health
   ```

2. **Vérifier que les templates sont chargés** :
   ```sql
   SELECT name, type, active FROM email_templates;
   ```

3. **Vérifier les emails envoyés** :
   ```sql
   SELECT recipient_email, subject, status, sent_at 
   FROM emails 
   ORDER BY created_at DESC 
   LIMIT 10;
   ```

## 📝 Notes importantes

- Les emails sont envoyés de manière **asynchrone** (fire and forget)
- Les erreurs d'envoi d'email **ne bloquent pas** les opérations principales
- Le circuit breaker protège contre les pannes du service emailing
- Les templates peuvent être personnalisés par organisation

## 🚀 Déploiement

Avant de déployer en production :

1. **Configurer les variables d'environnement** :
   - `EMAILING_SERVICE_URL`
   - `FRONTEND_URL`
   - Configurer le serveur SMTP dans le service emailing

2. **Charger les templates** :
   ```bash
   psql -U postgres -d realestate_db -f scripts/seed-email-templates.sql
   ```

3. **Tester tous les scénarios** d'envoi d'email

4. **Monitorer les emails** :
   - Vérifier le taux de succès
   - Vérifier les emails en échec
   - Configurer les alertes pour les problèmes

