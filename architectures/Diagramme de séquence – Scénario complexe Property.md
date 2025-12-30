# Diagramme de Séquence - Scénario Complexe Property

## 📋 Contexte

Ce diagramme illustre le flux complet de création, modification, approbation et partage d'une Property avec tous les services impliqués.

**Stack :** Spring Boot 3.3.1, Kafka 3.6.1, PostgreSQL 17.2

---

## 🔄 Scénario Complet

```mermaid
sequenceDiagram
    actor User as User (Agent)
    actor Manager as Manager
    actor Admin as Admin
    participant FE as Frontend Vue.js
    participant FGW as API Gateway
    participant ID as Identity Service
    participant ORG as Organization Service
    participant RES as Resource Service
    participant PROP as Property Service
    participant DOC as Document Service
    participant WF as Workflow Engine
    participant KAFKA as Kafka Event Bus
    participant AUD as Audit Service
    participant NOTIF as Notification Service
    participant EMAIL as Emailing Service
    participant STORAGE as Object Storage

    %% ---------- Création Property ----------
    User->>FE: Crée nouvelle Property
    FE->>FGW: POST /api/properties
    FGW->>ID: Vérifie JWT + RBAC / Permissions
    ID-->>FGW: ✅ Autorisation OK
    FGW->>PROP: POST /api/properties (avec token)
    PROP->>ORG: Vérifie plan & feature flags
    ORG-->>PROP: ✅ Plan valide
    PROP->>RES: Vérifie domaine / tags / règles métiers
    RES-->>PROP: ✅ Validation OK
    PROP->>PROP: Crée Property en DB
    PROP->>KAFKA: Publie event "property.created"
    KAFKA->>AUD: Event "property.created"
    KAFKA->>NOTIF: Event "property.created"
    KAFKA->>WF: Event "property.created"
    PROP-->>FGW: ✅ Property créée (201)
    FGW-->>FE: ✅ Response avec Property ID

    %% ---------- Upload Documents ----------
    User->>FGW: Upload documents / images
    FGW->>DOC: Upload fichiers
    DOC->>Storage: Stocke fichiers
    Storage-->>DOC: URL / confirmation
    DOC->>AUD: Log upload
    DOC->>NOTIF: Notifie création document
    DOC-->>PROP: Confirme association documents

    %% ---------- Workflow d’approbation ----------
    PROP->>WF: Déclenche Workflow d’approbation multi-étapes
    WF->>Manager: Notifie approbation requise
    Manager->>WF: Approuve / Rejette / Demande modification
    WF->>PROP: Retour workflow
    WF->>AUD: Log action workflow
    WF->>NOTIF: Envoie notifications approbation
    WF->>EMAIL: Envoie email approbation
    EMAIL->>NOTIF: Confirme envoi email

    %% ---------- Partage inter-organisation ----------
    PROP->>PROP: Crée PropertyAccess pour utilisateurs externes
    PROP->>NOTIF: Notifie utilisateurs externes
    PROP->>EMAIL: Envoie email aux utilisateurs externes
    EMAIL->>NOTIF: Confirme envoi email
    PROP->>AUD: Log partage Property

    %% ---------- Publication ----------
    PROP->>RES: Marque Property comme publiée
    RES->>AUD: Log publication
    RES->>NOTIF: Notifications publication
    RES->>EMAIL: Emails publication
    EMAIL->>NOTIF: Confirme envoi

    %% ---------- Modification / mise à jour ----------
    User->>FGW: Met à jour Property
    FGW->>ID: Vérifie RBAC
    ID-->>FGW: Autorisation OK
    FGW->>PROP: Applique modifications
    PROP->>WF: Déclenche workflow si requis
    PROP->>AUD: Log modification
    PROP->>NOTIF: Notifications modification
    PROP->>EMAIL: Emails modification
    EMAIL->>NOTIF: Confirme envoi

    %% ---------- Suppression ----------
    User->>FGW: Demande suppression Property
    FGW->>ID: Vérifie RBAC
    ID-->>FGW: Autorisation OK
    FGW->>PROP: Déclenche workflow suppression
    WF->>Manager: Notifie approbation suppression
    Manager->>WF: Approuve / Rejette
    WF->>PROP: Retour workflow suppression
    PROP->>AUD: Log suppression
    PROP->>NOTIF: Notifications suppression
    PROP->>EMAIL: Emails suppression
    EMAIL->>NOTIF: Confirme envoi
