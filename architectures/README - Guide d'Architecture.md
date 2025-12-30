# 📚 Guide d'Architecture - SaaS Immobilier

## 🎯 Vue d'ensemble

Ce dossier contient toute la documentation d'architecture pour le projet SaaS Immobilier basé sur Spring Boot microservices.

**Dernière mise à jour :** Décembre 2024

---

## 📖 Documents Disponibles

### 🏗️ Architecture Générale

1. **[Architecture technique – SaaS Immobilier avec Spring Boot.md](./Architecture%20technique%20–%20SaaS%20Immobilier%20avec%20Spring%20Boot.md)**
   - Diagramme d'architecture global
   - Technologies et versions recommandées
   - Vue d'ensemble des composants

2. **[MicroservicesArchitecture.md](./MicroservicesArchitecture.md)**
   - Architecture détaillée des microservices
   - Interactions entre services
   - Bases de données par service

3. **[Description des services.md](./Description%20des%20services.md)**
   - Description détaillée de chaque microservice
   - Technologies utilisées par service
   - Fonctionnalités principales

### 🔧 Versions et Compatibilité

4. **[Versions et Compatibilité - Technologies Recommandées.md](./Versions%20et%20Compatibilité%20-%20Technologies%20Recommandées.md)** ⭐
   - Tableau complet des versions recommandées
   - Compatibilité entre technologies
   - Checklist de déploiement
   - Notes de migration Jakarta EE

5. **[Stack Technique Définitif.md](./Stack%20Technique%20Définitif.md)** ⭐
   - Stack technique complet avec choix définitifs
   - Justifications des choix
   - Checklist de déploiement

6. **[Docker - Configuration et Déploiement.md](./Docker%20-%20Configuration%20et%20Déploiement.md)** 🐳 ⭐ **ESSENTIEL**
   - Dockerfiles multi-stage optimisés
   - Configuration docker-compose.yml complète
   - Images Docker recommandées
   - Bonnes pratiques Docker
   - Health checks et monitoring

### 📊 Diagrammes UML

7. **[UML Combiné – SaaS Immobilier Avancé.md](./UML%20Combiné%20–%20SaaS%20Immobilier%20Avancé.md)**
   - Diagramme UML complet de tous les modèles
   - Relations entre entités
   - Modèle de données global

8. **[UML Combiné – Plan + Roles + Permissions + Property + Workflow.md](./UML%20Combiné%20–%20Plan%20+%20Roles%20+%20Permissions%20+%20Property%20+%20Workflow.md)**
   - Modèle de sécurité et permissions
   - Workflow et approbations
   - Plans et feature flags

9. **[Diagramme UML – Organisation & Users (RBAC).md](./Diagramme%20UML%20–%20Organisation%20&%20Users%20(RBAC).md)**
   - Modèle RBAC complet
   - Organisation et utilisateurs
   - Délégation de rôles

8. **[Diagramme UML – Property comme Resource spécialisée.md](./Diagramme%20UML%20–%20Property%20comme%20Resource%20spécialisée.md)**
   - Modèle Property
   - PropertyAccess (ACL)

9. **[Diagramme UML – Resource générique.md](./Diagramme%20UML%20–%20Resource%20générique.md)**
   - Modèle Resource générique
   - Domaines et tags
   - ResourceAccess

10. **[Diagramme UML – Document & Media.md](./Diagramme%20UML%20–%20Document%20&%20Media.md)**
    - Modèle Document
    - Stockage externe

11. **[Diagramme UML – Workflow & Approvals.md](./Diagramme%20UML%20–%20Workflow%20&%20Approvals.md)**
    - Modèle Workflow
    - Tasks et approbations

12. **[Diagramme UML – Audit & Logging.md](./Diagramme%20UML%20–%20Audit%20&%20Logging.md)**
    - Modèle AuditLog
    - Traçabilité

13. **[Diagramme UML – Notification & Emailing.md](./Diagramme%20UML%20–%20Notification%20&%20Emailing.md)**
    - Modèle Notification
    - Modèle Email

14. **[Diagramme UML – Microservices & Communications.md](./Diagramme%20UML%20–%20Microservices%20&%20Communications.md)**
    - Communication entre microservices
    - Flux de données

### 🔄 Diagrammes de Séquence

15. **[Diagramme de séquence – Scénario complexe Property.md](./Diagramme%20de%20séquence%20–%20Scénario%20complexe%20Property.md)**
    - Flux complet de création/modification/suppression
    - Interactions entre services
    - Workflow d'approbation

### 📋 Tableaux et Règles Métier

16. **[tableau complet de rôles et permissions.md](./tableau%20complet%20de%20rôles%20et%20permissions.md)**
    - Rôles système (SaaS_Admin, Super_Admin)
    - Rôles par organisation (Owner, Manager, Agent, Viewer)
    - Permissions détaillées par module
    - Mapping rôles → permissions

---

## 🚀 Quick Start

### 1. Commencer par lire :
- **Architecture technique – SaaS Immobilier avec Spring Boot.md** (vue d'ensemble)
- **Versions et Compatibilité - Technologies Recommandées.md** (versions à utiliser)

### 2. Comprendre les services :
- **Description des services.md** (détails de chaque service)
- **MicroservicesArchitecture.md** (interactions)

### 3. Modèles de données :
- **UML Combiné – SaaS Immobilier Avancé.md** (vue complète)
- **tableau complet de rôles et permissions.md** (sécurité)

### 4. Flux métier :
- **Diagramme de séquence – Scénario complexe Property.md** (exemples)

---

## 🔧 Stack Technique Recommandée

### Core
- **Java** : 21 LTS ✅
- **Spring Boot** : 3.3.1 ✅
- **Spring Cloud** : 2023.0.1 ✅
- **Spring Cloud Gateway** : 4.1.1 ✅

### Bases de Données
- **PostgreSQL** : 17.2 ✅
- **Redis** : 7.2.4 ✅
- **Elasticsearch** : 8.15.0 ✅

### Messagerie
- **Kafka** : 3.6.1 ✅

### Conteneurisation 🐳
- **Docker** : 24.0.7 ✅ **ESSENTIEL**
- **Docker Compose** : 2.24.6 ✅ **ESSENTIEL**
- **Kubernetes** : 1.29.2 ✅

### Observabilité
- **Prometheus** : 2.49.1 + **Grafana** : 10.3.3 ✅
- **ELK Stack** : 8.15.0 ✅
- **Zipkin** : 2.24.4 ✅

> 📖 Voir **[Stack Technique Définitif.md](./Stack%20Technique%20Définitif.md)** et **[Docker - Configuration et Déploiement.md](./Docker%20-%20Configuration%20et%20Déploiement.md)** pour les détails complets

---

## ⚠️ Points Importants

### Migration Jakarta EE
Spring Boot 3.x utilise **Jakarta EE** au lieu de **Java EE** :
- `javax.*` → `jakarta.*`
- `javax.persistence.*` → `jakarta.persistence.*`
- `javax.servlet.*` → `jakarta.servlet.*`

### Java LTS
- Utiliser **Java 17** ou **21** (LTS) pour la production
- Spring Boot 3.x nécessite **Java 17 minimum**

### Compatibilité
- Toutes les versions listées dans le document de compatibilité sont testées ensemble
- Utiliser les BOM (Bill of Materials) Spring pour gérer les dépendances

---

## 📝 Structure des Microservices

1. **Identity & Auth Service** - Authentification, RBAC, ACL
2. **Organization & Team Service** - Multi-tenant, hiérarchie
3. **Property Service** - Gestion des propriétés
4. **Resource Service** - Resource générique extensible
5. **Document Service** - Upload et stockage de documents
6. **Workflow Engine Service** - Workflows d'approbation
7. **Audit Service** - Traçabilité complète
8. **Notification Service** - Notifications push, in-app, SMS
9. **Emailing Service** - Emails transactionnels
10. **Billing / Plan Service** - Plans et feature flags

---

## 🔗 Ressources Externes

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Cloud Documentation](https://spring.io/projects/spring-cloud)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Redis Documentation](https://redis.io/documentation)
- [Kafka Documentation](https://kafka.apache.org/documentation/)

---

## 📅 Historique des Mises à Jour

- **Décembre 2024** : Ajout du document de compatibilité des versions, amélioration des documents existants avec versions recommandées

---

**Pour toute question ou amélioration, référez-vous aux documents détaillés dans ce dossier.**

