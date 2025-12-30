# 🏠 SaaS Immobilier - Real Estate Management Platform

**Version :** 1.0.0-SNAPSHOT  
**Date :** Décembre 2024

---

## 📋 Description

Plateforme SaaS complète pour la gestion immobilière basée sur une architecture microservices Spring Boot avec Vue.js, déployée sur VPS.

---

## 🏗️ Architecture

- **Architecture :** Microservices
- **Backend :** Spring Boot 3.3.1, Spring Cloud 2023.0.1
- **Frontend :** Vue.js 3.4.27, Vite 5.1.0
- **Base de données :** PostgreSQL 17.2 (148.230.112.148:5432)
- **Cache :** Redis 7.2.4 (148.230.112.148:6379)
- **Messagerie :** Kafka 3.6.1
- **Recherche :** Elasticsearch 8.15.0
- **Stockage :** VPS File System (/var/realestate/storage)
- **Déploiement :** VPS avec scripts de déploiement

> 📖 Voir [architectures/README - Guide d'Architecture.md](./architectures/README%20-%20Guide%20d'Architecture.md) pour les détails complets

---

## ⚙️ Prérequis

- **Java 21** (requis pour les tests unitaires)
  - Installer: `./scripts/setup-java21.sh`
  - Ou voir [SETUP-JAVA21.md](./SETUP-JAVA21.md) pour les instructions détaillées
- **Maven 3.9+**
- **PostgreSQL 17.2**
- **Redis 7.2.4**

## 🚀 Quick Start

### Prérequis

- Java 21 LTS
- Maven 3.9.6+
- PostgreSQL 17.2 (148.230.112.148:5432)
- Redis 7.2.4 (148.230.112.148:6379)
- Node.js 20.11.0 LTS (pour le frontend)

### Démarrage Local

```bash
# 1. Cloner le projet
git clone https://github.com/viridial-group/realestate.git
cd realestate

# 2. Build du projet
mvn clean install

# 3. Démarrer les services
mvn spring-boot:run -pl gateway
mvn spring-boot:run -pl services/identity-service
# ... autres services

# 4. Démarrer le frontend
cd frontend
npm install
npm run dev
```

### Déploiement sur VPS

```bash
# 1. Configuration initiale du VPS
./scripts/setup-vps.sh

# 2. Déploiement
./scripts/deploy.sh prod

# 3. Démarrage des services
./scripts/start-services.sh prod
```

---

## 📚 Documentation

Toute la documentation est disponible dans le dossier `architectures/` :

- [Guide d'Architecture](./architectures/README%20-%20Guide%20d'Architecture.md)
- [Stack Technique Définitif](./architectures/Stack%20Technique%20Définitif.md)
- [Plan d'Implémentation (TODO)](./architectures/TODO%20-%20Plan%20d'Implémentation.md)
- [Variables d'Environnement](./architectures/Variables%20d'Environnement.md)
- [Tests - Stratégie](./architectures/Tests%20-%20Stratégie%20et%20Documentation.md)
- [Scripts de Déploiement](./scripts/)

---

## 🧪 Tests

```bash
# Tests unitaires
mvn test

# Tests avec couverture
mvn clean test jacoco:report

# Tests d'intégration
mvn test -Dtest=*IntegrationTest
```

---

## 📊 Microservices

1. **Identity & Auth Service** - Authentification, RBAC, ACL
2. **Organization Service** - Multi-tenant, hiérarchie
3. **Resource Service** - Resource générique
4. **Property Service** - Gestion des propriétés
5. **Document Service** - Upload/download de documents (VPS File System)
6. **Workflow Engine** - Workflows d'approbation
7. **Audit Service** - Traçabilité
8. **Notification Service** - Notifications multi-canaux
9. **Emailing Service** - Emails transactionnels (Hostinger SMTP)
10. **Billing Service** - Plans et feature flags
11. **API Gateway** - Point d'entrée unique

---

## 🔐 Configuration

### Bases de Données

**PostgreSQL :**
- Host: 148.230.112.148
- Port: 5432
- User: postgres
- Password: postgres

**Redis :**
- Host: 148.230.112.148
- Port: 6379
- Password: Abcd@1984

**Email (Hostinger SMTP) :**
- Host: smtp.hostinger.com
- Port: 465
- User: support@viridial.com
- Password: S@upport!19823

**Stockage :**
- Path: /var/realestate/storage
- VPS File System

---

## 🔐 Sécurité

- Authentification JWT
- OAuth2 Resource Server
- RBAC (Role-Based Access Control)
- ACL (Access Control List)
- HTTPS/TLS
- Secrets management (Kubernetes Secrets / HashiCorp Vault)

---

## 📈 Observabilité

- **Métriques :** Prometheus + Grafana
- **Logs :** ELK Stack (Elasticsearch, Logstash, Kibana)
- **Tracing :** Zipkin + Micrometer Tracing

---

## 🛠️ Technologies

Voir [Stack Technique Définitif](./architectures/Stack%20Technique%20Définitif.md) pour la liste complète.

**Tous les services sont 100% open source et gratuits.**

---

## 🌐 URLs et Sous-domaines

- **Production Frontend :** https://app.viridial.com
- **Production API :** https://api.viridial.com
- **Staging Frontend :** https://staging-app.viridial.com
- **Staging API :** https://staging-api.viridial.com

> 📖 Voir [Sous-domaines et URLs](./architectures/Sous-domaines%20et%20URLs.md) pour la configuration complète

---

## 📝 License

[À définir]

---

## 👥 Contributeurs

**Viridial Group** - https://github.com/viridial-group

---

**Dernière mise à jour :** Décembre 2024
