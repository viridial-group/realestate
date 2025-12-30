# Architecture Technique – SaaS Immobilier avec Spring Boot

## 📋 Versions Recommandées

**Stack Principal (Choix Définitifs) :**
- **Java** : 21 LTS ✅
- **Spring Boot** : 3.3.1 ✅
- **Spring Cloud** : 2023.0.1 ✅
- **Spring Cloud Gateway** : 4.1.1 ✅
- **PostgreSQL** : 17.2 ✅
- **Redis** : 7.2.4 ✅
- **Elasticsearch** : 8.15.0 ✅
- **Kafka** : 3.6.1 ✅
- **Vue.js** : 3.4.27 ✅

> 📖 Pour plus de détails, voir le document : **"Versions et Compatibilité - Technologies Recommandées.md"**

---

## 🏗️ Diagramme d'Architecture

```mermaid
graph TD
    %% FRONTEND
    FE[Frontend Vue.js 3.4.27<br/>+ Vite 5.1.0]

    %% API GATEWAY
    API[API Gateway<br/>Spring Cloud Gateway 4.1.1]

    %% MICROSERVICES
    ID[Identity & Auth Service<br/>Spring Boot 3.3.1]
    ORG[Organization & Team Service<br/>Spring Boot 3.3.1]
    PROP[Property Service<br/>Spring Boot 3.3.1]
    RES[Resource Service<br/>Spring Boot 3.3.1]
    DOC[Document Service<br/>Spring Boot 3.3.1]
    WF[Workflow Engine Service<br/>Spring Boot 3.3.1]
    AUD[Audit Service<br/>Spring Boot 3.3.1]
    NOTIF[Notification Service<br/>Spring Boot 3.3.1]
    EMAIL[Emailing Service<br/>Spring Boot 3.3.1]
    BILL[Billing / Plan Service<br/>Spring Boot 3.3.1]

    %% DATABASES
    POSTGRES[(PostgreSQL 17.2)]
    REDIS[(Redis 7.2.4 Cache)]
    ELASTIC[(Elasticsearch 8.15.0)]
    STORAGE[(File Storage:<br/>VPS File System)]

    %% MESSAGE BUS / EVENT
    BUS[Event Bus:<br/>Kafka 3.6.1]

    %% FRONTEND -> API
    FE --> API

    %% API -> Services
    API --> ID
    API --> ORG
    API --> PROP
    API --> RES
    API --> DOC
    API --> WF
    API --> AUD
    API --> NOTIF
    API --> EMAIL
    API --> BILL

    %% Services -> DB / Storage
    ID --> POSTGRES
    ORG --> POSTGRES
    PROP --> POSTGRES
    RES --> POSTGRES
    DOC --> POSTGRES
    DOC --> STORAGE
    WF --> POSTGRES
    AUD --> POSTGRES
    NOTIF --> POSTGRES
    EMAIL --> POSTGRES
    BILL --> POSTGRES

    %% Event-driven communication
    PROP --> BUS
    RES --> BUS
    DOC --> BUS
    WF --> BUS
    AUD --> BUS
    NOTIF --> BUS
    EMAIL --> BUS
    BILL --> BUS
```

---

## 🔧 Technologies & Versions Détaillées

### Backend Core ✅
- **Spring Boot** : 3.3.1 (Framework principal)
- **Spring Framework** : 6.1.24+
- **Spring Security** : 6.3.1 (JWT + OAuth2)
- **Spring Data JPA** : 3.2.1
- **Spring Cloud Gateway** : 4.1.1 (API Gateway)

### Bases de Données ✅
- **PostgreSQL** : 17.2 (Base principale multi-tenant)
- **Redis** : 7.2.4 (Cache, sessions, JWT, rate limiting)
- **Elasticsearch** : 8.15.0 (Recherche et audit logs)

### Messagerie ✅
- **Apache Kafka** : 3.6.1 (Event bus pour haute performance)
- **Spring Kafka** : 3.1.1

### Stockage ✅
- **VPS File System** : Système de fichiers du VPS (/var/realestate/storage)

### Observabilité ✅
- **Prometheus** : 2.49.1 (Métriques)
- **Grafana** : 10.3.3 (Visualisation)
- **ELK Stack** : 8.15.0 (Elasticsearch, Logstash, Kibana)
- **Zipkin** : 2.24.4 (Tracing distribué)
- **Micrometer** : 1.12.5

### Frontend ✅
- **Vue.js** : 3.4.27
- **Vite** : 5.1.0 (Build tool)
- **Node.js** : 20.11.0 LTS

---

## ⚠️ Notes Importantes

1. **Jakarta EE** : Spring Boot 3.x utilise `jakarta.*` au lieu de `javax.*`
2. **Java 21 LTS** : ✅ Choix définitif - Dernière LTS avec meilleures performances
3. **Kafka** : ✅ Choix définitif - Meilleur pour microservices à grande échelle
4. **Vue.js** : ✅ Choix définitif - Framework frontend simple et performant
5. **ELK Stack** : ✅ Choix définitif - Solution complète pour logs
6. **Compatibilité** : Toutes les versions sont testées et compatibles entre elles

## 🎯 Justification des Choix

- **Java 21** : Dernière LTS avec virtual threads, meilleures performances
- **PostgreSQL 17** : Dernière version stable avec améliorations de performance
- **Kafka** : Meilleure scalabilité et durabilité pour architecture événementielle
- **Vue.js** : Courbe d'apprentissage douce, écosystème riche, performance optimale
- **ELK Stack** : Solution complète et mature pour la gestion des logs
- **Zipkin** : Plus simple à déployer et maintenir que Jaeger
