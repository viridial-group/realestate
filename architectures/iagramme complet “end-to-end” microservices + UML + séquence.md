graph TD
    %% ========================
    %% FRONTEND / API GATEWAY
    %% ========================
    FGW[Frontend / API Gateway]

    %% ========================
    %% IDENTITY & AUTH
    %% ========================
    ID[Identity & Auth Service]
    ID_DB[(Users, Roles, Permissions, RoleDelegation)]

    %% ========================
    %% ORGANIZATION & TEAMS
    %% ========================
    ORG[Organization & Teams Service]
    ORG_DB[(Organizations, Teams, OrganizationRoles, OrganizationUser)]

    %% ========================
    %% DOMAIN & RESOURCE
    %% ========================
    RES[Domain & Resource Service]
    RES_DB[(Domains, Resource, ResourceAccess, Tags)]

    %% ========================
    %% PROPERTY SERVICE
    %% ========================
    PROP[Property Service]
    PROP_DB[(Properties, PropertyAccess)]

    %% ========================
    %% DOCUMENTS
    %% ========================
    DOC[Document Service]
    DOC_DB[(Documents, Media)]
    Storage[(Object Storage: S3 / GCS / MinIO)]

    %% ========================
    %% WORKFLOW & APPROVALS
    %% ========================
    WF[Workflow Engine Service]
    WF_DB[(ApprovalWorkflows, Tasks)]

    %% ========================
    %% AUDIT & LOGGING
    %% ========================
    AUD[Audit & Logging Service]
    AUD_DB[(AuditLog, Events)]

    %% ========================
    %% BILLING / SUBSCRIPTION
    %% ========================
    BILL[Billing Service]
    BILL_DB[(Plans, Subscriptions)]

    %% ========================
    %% NOTIFICATIONS & EMAILING
    %% ========================
    NOTIF[Notification Service]
    EMAIL[Emailing Service]
    NOTIF_DB[(Notifications)]
    EMAIL_DB[(EmailTemplates, Logs)]

    %% ========================
    %% FLOW CONNECTIONS
    %% ========================

    %% FRONTEND -> Services
    FGW -->|Auth Token / API Calls| ID
    FGW --> ORG
    FGW --> RES
    FGW --> PROP
    FGW --> DOC
    FGW --> WF
    FGW --> AUD
    FGW --> BILL
    FGW --> NOTIF
    FGW --> EMAIL

    %% PROPERTY SERVICE interactions
    PROP -->|Check RBAC / ACL| ID
    PROP -->|Check Domains & Tags| RES
    PROP -->|Emit Events| WF
    PROP -->|Emit Events| AUD
    PROP -->|Emit Events| NOTIF
    PROP -->|Attach Documents| DOC

    %% DOCUMENT SERVICE interactions
    DOC -->|Store / Retrieve Files| Storage
    DOC -->|Emit Events| AUD
    DOC -->|Emit Events| NOTIF

    %% RESOURCE SERVICE events
    RES -->|Emit Events| WF
    RES -->|Emit Events| AUD
    RES -->|Emit Events| NOTIF

    %% ORGANIZATION / Teams
    ORG -->|Emit Events| WF
    ORG -->|Emit Events| AUD
    ORG -->|Emit Events| NOTIF

    %% WORKFLOW interactions
    WF -->|Emit Events| AUD
    WF -->|Emit Events| NOTIF
    WF -->|Trigger Email| EMAIL

    %% BILLING interactions
    BILL -->|Emit Events| AUD
    BILL -->|Emit Events| NOTIF
    BILL -->|Trigger Email| EMAIL

    %% EMAIL interactions
    EMAIL -->|Send / Log| NOTIF
