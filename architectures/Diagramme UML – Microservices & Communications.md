# Diagramme Microservices & Communications

## 📋 Versions

**Stack :**
- Spring Boot 3.3.1
- Spring Cloud Gateway 4.1.1
- Kafka 3.6.1 (Event Bus)
- PostgreSQL 17.2
- Redis 7.2.4

---

## 🏗️ Diagramme d'Architecture

```mermaid
graph TD
    %% ========================
    %% FRONTEND & GATEWAY
    %% ========================
    FE[Frontend Vue.js 3.4.27]
    FGW[API Gateway<br/>Spring Cloud Gateway 4.1.1]

    %% ========================
    %% MICROSERVICES CORE
    %% ========================
    ID[Identity & Auth Service<br/>Spring Boot 3.3.1<br/>JWT + OAuth2]
    ORG[Organization Service<br/>Spring Boot 3.3.1<br/>Multi-tenant]
    RES[Resource Service<br/>Spring Boot 3.3.1<br/>Resource générique]
    PROP[Property Service<br/>Spring Boot 3.3.1<br/>Gestion Property]

    %% ========================
    %% MICROSERVICES SUPPORT
    %% ========================
    DOC[Document Service<br/>Spring Boot 3.3.1<br/>Upload/Download]
    WF[Workflow Engine<br/>Spring Boot 3.3.1<br/>Approbations]
    AUD[Audit Service<br/>Spring Boot 3.3.1<br/>Logging]
    BILL[Billing Service<br/>Spring Boot 3.3.1<br/>Plans & Features]
    NOTIF[Notification Service<br/>Spring Boot 3.3.1<br/>Push/In-app/SMS]
    EMAIL[Emailing Service<br/>Spring Boot 3.3.1<br/>Templates]

    %% ========================
    %% INFRASTRUCTURE
    %% ========================
    POSTGRES[(PostgreSQL 17.2)]
    REDIS[(Redis 7.2.4)]
    KAFKA[Kafka 3.6.1<br/>Event Bus]
    ELASTIC[(Elasticsearch 8.15.0)]
    STORAGE[(File Storage<br/>VPS File System)]

    %% ========================
    %% CONNECTIONS
    %% ========================
    FE -->|HTTPS| FGW
    FGW -->|REST| ID
    FGW -->|REST| ORG
    FGW -->|REST| RES
    FGW -->|REST| PROP
    FGW -->|REST| DOC
    FGW -->|REST| WF
    FGW -->|REST| AUD
    FGW -->|REST| BILL
    FGW -->|REST| NOTIF
    FGW -->|REST| EMAIL

    %% Property Service interactions
    PROP -->|Check RBAC/ACL| ID
    PROP -->|Check Domain/Tags| RES
    PROP -->|Events| KAFKA
    PROP -->|Events| AUD
    PROP -->|Events| NOTIF
    PROP -->|Upload/Attach| DOC

    %% Document Service
    DOC -->|Store/Retrieve| STORAGE
    DOC -->|Events| KAFKA
    DOC -->|Events| AUD
    DOC -->|Events| NOTIF

    %% Workflow interactions
    WF -->|Events| KAFKA
    WF -->|Events| AUD
    WF -->|Events| NOTIF
    WF -->|Trigger| EMAIL

    %% Billing interactions
    BILL -->|Events| KAFKA
    BILL -->|Events| AUD
    BILL -->|Events| NOTIF
    BILL -->|Trigger| EMAIL

    %% Email interactions
    EMAIL -->|Events| KAFKA
    EMAIL -->|Log| NOTIF

    %% Database connections
    ID --> POSTGRES
    ID --> REDIS
    ORG --> POSTGRES
    RES --> POSTGRES
    PROP --> POSTGRES
    DOC --> POSTGRES
    WF --> POSTGRES
    AUD --> POSTGRES
    AUD --> ELASTIC
    BILL --> POSTGRES
    NOTIF --> POSTGRES
    EMAIL --> POSTGRES

    %% Event Bus connections
    KAFKA -.->|Subscribe| ID
    KAFKA -.->|Subscribe| ORG
    KAFKA -.->|Subscribe| RES
    KAFKA -.->|Subscribe| PROP
    KAFKA -.->|Subscribe| DOC
    KAFKA -.->|Subscribe| WF
    KAFKA -.->|Subscribe| AUD
    KAFKA -.->|Subscribe| BILL
    KAFKA -.->|Subscribe| NOTIF
    KAFKA -.->|Subscribe| EMAIL
```

---

## 📨 Communication entre Services

### Communication Synchrone (REST)
- **API Gateway → Microservices** : REST via Spring Cloud Gateway
- **Microservices → Microservices** : REST direct (interne)

### Communication Asynchrone (Events)
- **Tous les services → Kafka** : Publication d'événements
- **Tous les services ← Kafka** : Abonnement aux événements

### Exemples d'Événements

| Événement | Publisher | Subscribers |
|-----------|-----------|------------|
| `property.created` | Property Service | Workflow, Audit, Notification |
| `property.updated` | Property Service | Workflow, Audit, Notification |
| `property.deleted` | Property Service | Workflow, Audit, Notification |
| `document.uploaded` | Document Service | Audit, Notification |
| `workflow.approved` | Workflow Service | Property, Audit, Notification, Email |
| `user.created` | Identity Service | Organization, Audit, Notification |

---

## 🔐 Sécurité

- **Authentification** : JWT via Identity Service
- **Autorisation** : RBAC + ACL via Identity Service
- **Communication** : HTTPS/TLS entre services
- **Secrets** : Kubernetes Secrets / HashiCorp Vault (open source)

---

**Dernière mise à jour :** Décembre 2024
