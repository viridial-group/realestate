sequenceDiagram
    actor User
    actor Manager
    actor Admin
    participant FGW as API Gateway / Frontend
    participant ID as Identity & Auth Service
    participant PROP as Property Service
    participant RES as Resource Service
    participant DOC as Document Service
    participant WF as Workflow Engine
    participant AUD as Audit Service
    participant NOTIF as Notification Service
    participant EMAIL as Emailing Service
    participant Storage as Object Storage (S3/GCS)

    %% ---------- Création Property ----------
    User->>FGW: Crée nouvelle Property avec détails
    FGW->>ID: Vérifie RBAC / Permissions
    ID-->>FGW: Autorisation OK
    FGW->>PROP: Crée Property
    PROP->>RES: Vérifie domaine / tags / règles métiers
    RES-->>PROP: Validation OK
    PROP->>AUD: Log création Property
    AUD-->>PROP: Log OK

    %% ---------- Upload Documents ----------
    User->>FGW: Upload documents / images
    FGW->>DOC: Upload fichiers
    DOC->>Storage: Stocke fichiers
    Storage-->>DOC: URL / confirmation
    DOC->>AUD: Log upload
    DOC->>NOTIF: Notifie création document
    DOC-->>PROP: Confirme association documents

    %% ---------- Workflow d’approbation ----------
    PROP->>WF: Déclenche Workflow d’approbation
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
