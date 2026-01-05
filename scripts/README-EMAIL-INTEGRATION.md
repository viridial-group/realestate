# Intégration de l'envoi d'emails dans les APIs

Ce document décrit l'intégration de l'envoi d'emails dans les différents services de l'application.

## Architecture

L'envoi d'emails utilise :
- **EmailServiceClient** : Client REST dans le module `common` pour communiquer avec le service emailing
- **Templates d'email** : Templates HTML pré-définis dans la base de données
- **Envoi asynchrone** : Les emails sont envoyés de manière asynchrone (fire and forget) pour ne pas bloquer les opérations principales

## Services intégrés

### 1. Identity Service

#### AuthService.register()
**Template** : `welcome_email`  
**Déclencheur** : Après l'inscription d'un nouvel utilisateur (particulier)  
**Variables** :
- `firstName` : Prénom de l'utilisateur
- `appUrl` : URL de l'application frontend

**Code** :
```java
sendWelcomeEmail(user);
```

#### SubscribeService.subscribe()
**Template** : `registration_confirmation`  
**Déclencheur** : Après l'inscription avec création d'organisation  
**Variables** :
- `firstName` : Prénom de l'utilisateur
- `email` : Email de l'utilisateur
- `registrationDate` : Date d'inscription formatée
- `loginUrl` : URL de connexion

**Code** :
```java
sendRegistrationConfirmationEmail(user, organization);
```

### 2. Property Service

#### PropertyService.createProperty()
**Template** : `property_published`  
**Déclencheur** : Quand une propriété est créée avec le statut `PUBLISHED`  
**Variables** :
- `firstName` : Prénom du propriétaire
- `propertyTitle` : Titre de la propriété
- `propertyAddress` : Adresse de la propriété
- `propertyCity` : Ville de la propriété
- `propertyPrice` : Prix de la propriété
- `propertyArea` : Surface de la propriété
- `propertyUrl` : URL de la propriété

**Code** :
```java
if (saved.getStatus() != null && saved.getStatus().equals("PUBLISHED")) {
    sendPropertyPublishedEmail(saved, authToken);
}
```

### 3. ContactMessageService

#### ContactMessageService.createContactMessage()
**Template** : `contact_message`  
**Déclencheur** : Quand un nouveau message de contact est créé pour une propriété  
**Variables** :
- `recipientName` : Nom du destinataire (propriétaire de l'annonce)
- `senderName` : Nom de l'expéditeur
- `senderEmail` : Email de l'expéditeur
- `senderPhone` : Téléphone de l'expéditeur (optionnel)
- `message` : Contenu du message
- `propertyTitle` : Titre de la propriété concernée
- `propertyAddress` : Adresse de la propriété
- `messageUrl` : URL pour répondre au message

**Code** :
```java
if (assignedUserId != null && property != null) {
    sendContactMessageEmail(saved, property, assignedUserId);
}
```

## Configuration

### Variables d'environnement

Ajoutez dans les fichiers `application.yml` de chaque service :

```yaml
# URL du service emailing
services:
  emailing:
    url: ${EMAILING_SERVICE_URL:http://localhost:8088}

# URL du frontend (pour les liens dans les emails)
app:
  frontend:
    url: ${FRONTEND_URL:http://localhost:3003}
```

### Dépendances

Le module `common` doit être inclus dans les services qui envoient des emails :

```xml
<dependency>
    <groupId>com.realestate</groupId>
    <artifactId>common</artifactId>
    <version>${project.version}</version>
</dependency>
```

## Gestion des erreurs

Les emails sont envoyés de manière **asynchrone** et **non-bloquante** :
- Si l'envoi d'email échoue, l'opération principale (inscription, création de propriété, etc.) continue
- Les erreurs sont loggées mais n'interrompent pas le flux
- Le circuit breaker Resilience4j protège contre les pannes du service emailing

## Exemples d'utilisation

### Envoi d'email depuis un service

```java
@Autowired(required = false)
private EmailServiceClient emailServiceClient;

private void sendEmail() {
    if (emailServiceClient == null) {
        return; // Service non disponible
    }
    
    Map<String, Object> variables = new HashMap<>();
    variables.put("firstName", "Jean");
    variables.put("propertyTitle", "Appartement à Paris");
    // ... autres variables
    
    // Envoi asynchrone (fire and forget)
    emailServiceClient.sendEmailFromTemplateAsync(
            "property_published",
            "jean@example.com",
            userId,
            organizationId,
            variables,
            authToken
    );
}
```

## Prochaines intégrations

### À implémenter

1. **Password Reset** (Identity Service)
   - Template : `password_reset`
   - Déclencheur : Quand un utilisateur demande une réinitialisation de mot de passe

2. **Organization Invitation** (Identity Service)
   - Template : `organization_invitation`
   - Déclencheur : Quand un utilisateur est invité à rejoindre une organisation

3. **Property Approval** (Property Service)
   - Template : `property_approval`
   - Déclencheur : Quand une propriété est approuvée ou refusée

4. **Price Alert** (Property Service)
   - Template : `price_alert`
   - Déclencheur : Quand le prix d'une propriété suivie change

5. **Visit Confirmation** (Property Service)
   - Template : `visit_confirmation`
   - Déclencheur : Quand une visite est confirmée

6. **Approval Request** (Workflow Service)
   - Template : `approval_request`
   - Déclencheur : Quand une demande d'approbation est créée

## Tests

Pour tester l'envoi d'emails :

1. Vérifier que le service emailing est démarré
2. Vérifier que les templates sont chargés dans la base de données :
   ```bash
   psql -U postgres -d realestate_db -f scripts/seed-email-templates.sql
   ```
3. Effectuer les actions qui déclenchent les emails (inscription, création de propriété, etc.)
4. Vérifier les logs du service emailing pour confirmer l'envoi
5. Vérifier la boîte email du destinataire

## Monitoring

Les emails sont trackés dans la table `emails` :
- Statut : `PENDING`, `SENT`, `FAILED`, `BOUNCED`
- Retry count : Nombre de tentatives
- Error message : Message d'erreur en cas d'échec

Pour voir les emails envoyés :
```sql
SELECT * FROM emails ORDER BY created_at DESC LIMIT 10;
```

