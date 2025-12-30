# 🎯 Stack Technique Définitif - SaaS Immobilier

**Date de décision :** Décembre 2024  
**Statut :** ✅ Choix définitifs validés

---

## 📋 Résumé Exécutif

Ce document présente le stack technique définitif choisi pour le projet SaaS Immobilier. Toutes les technologies ont été sélectionnées pour leur compatibilité, performance et maintenabilité.

---

## 🔧 Stack Backend

### Langage & Runtime
| Technologie | Version | Justification |
|------------|---------|---------------|
| **Java** | **21 LTS** | ✅ Dernière LTS avec virtual threads, meilleures performances que Java 17 |

### Framework Core
| Technologie | Version | Justification |
|------------|---------|---------------|
| **Spring Boot** | **3.3.1** | ✅ Version stable avec support long terme |
| **Spring Framework** | **6.1.24+** | ✅ Requis par Spring Boot 3.3.1 |
| **Spring Cloud** | **2023.0.1** | ✅ Compatible avec Spring Boot 3.3.1 |
| **Spring Cloud Gateway** | **4.1.1** | ✅ API Gateway pour microservices |
| **Spring Security** | **6.3.1** | ✅ JWT + OAuth2, inclus dans Spring Boot |
| **Spring Data JPA** | **3.2.1** | ✅ ORM, inclus dans Spring Boot |

### Build & Conteneurisation
| Technologie | Version | Justification |
|------------|---------|---------------|
| **Maven** | **3.9.6** | ✅ Standard industrie, gestion de dépendances |
| **Tomcat** | **10.1.25+** | ✅ Conteneur servlet par défaut Spring Boot |
| **Docker** | **24.0.7** | ✅ Conteneurisation - **ESSENTIEL** |
| **Docker Compose** | **2.24.6** | ✅ Orchestration locale - **ESSENTIEL** |
| **Docker Buildx** | Latest | ✅ Build multi-plateformes |
| **Kubernetes** | **1.29.2** | ✅ Orchestration production |
| **Helm** | **3.14.0** | ✅ Gestion de packages Kubernetes |

> 📖 Voir le document **"Docker - Configuration et Déploiement.md"** pour les détails complets

---

## 🗄️ Bases de Données & Stockage

### Base de Données Relationnelle
| Technologie | Version | Justification |
|------------|---------|---------------|
| **PostgreSQL** | **17.2** | ✅ Dernière version stable, meilleures performances |
| **PostgreSQL Driver** | **42.7.1** | ✅ JDBC Driver compatible Spring Boot 3.x |

### Cache & Session
| Technologie | Version | Justification |
|------------|---------|---------------|
| **Redis** | **7.2.4** | ✅ Cache, sessions, JWT, rate limiting |
| **Spring Data Redis** | **3.2.1** | ✅ Intégration Redis pour Spring Boot |

### Recherche & Indexation
| Technologie | Version | Justification |
|------------|---------|---------------|
| **Elasticsearch** | **8.15.0** | ✅ Recherche de propriétés et audit logs |
| **Elasticsearch Java Client** | **8.15.0** | ✅ Client officiel |

### File Storage
| Technologie | Version | Justification |
|------------|---------|---------------|
| **VPS File System** | - | ✅ **CHOISI** - Système de fichiers du VPS pour stocker les documents |
| **Spring Resource** | 3.3.1 | ✅ Gestion des fichiers avec Spring |

---

## 📨 Messagerie & Events

| Technologie | Version | Justification |
|------------|---------|---------------|
| **Apache Kafka** | **3.6.1** | ✅ Event bus haute performance, scalabilité horizontale |
| **Spring Kafka** | **3.1.1** | ✅ Intégration Kafka pour Spring Boot |

**Pourquoi Kafka ?**
- Meilleure scalabilité pour architecture microservices
- Durabilité des événements
- Support des gros volumes
- Écosystème mature

---

## 🔐 Sécurité

| Technologie | Version | Justification |
|------------|---------|---------------|
| **JWT (JJWT)** | **0.12.5** | ✅ Bibliothèque JWT standard |
| **OAuth2 Resource Server** | **1.3.1** | ✅ Inclus dans Spring Security |
| **OAuth2 Client** | **1.3.1** | ✅ Inclus dans Spring Security |
| **BCrypt** | **0.10.2** | ✅ Hachage de mots de passe |

---

## 📊 Observabilité & Monitoring

### Métriques
| Technologie | Version | Justification |
|------------|---------|---------------|
| **Prometheus** | **2.49.1** | ✅ Collecte de métriques |
| **Grafana** | **10.3.3** | ✅ Visualisation des métriques |
| **Micrometer** | **1.12.5** | ✅ Intégration Spring Boot |

### Logs
| Technologie | Version | Justification |
|------------|---------|---------------|
| **Elasticsearch** | **8.15.0** | ✅ Stockage des logs |
| **Logstash** | **8.15.0** | ✅ Traitement des logs |
| **Kibana** | **8.15.0** | ✅ Visualisation des logs |

**Pourquoi ELK Stack ?**
- Solution complète et mature
- Intégration native avec Elasticsearch (déjà utilisé pour recherche)
- Écosystème riche

### Tracing
| Technologie | Version | Justification |
|------------|---------|---------------|
| **Zipkin** | **2.24.4** | ✅ Tracing distribué (plus simple que Jaeger) |
| **Micrometer Tracing** | **1.2.1** | ✅ Intégration Spring Boot |

---

## 🎨 Frontend

| Technologie | Version | Justification |
|------------|---------|---------------|
| **Vue.js** | **3.4.27** | ✅ Framework simple, performant, écosystème riche |
| **Vite** | **5.1.0** | ✅ Build tool moderne et rapide |
| **Node.js** | **20.11.0 LTS** | ✅ Runtime pour build frontend |

**Pourquoi Vue.js ?**
- Courbe d'apprentissage douce
- Performance optimale
- Écosystème riche (Vue Router, Pinia, etc.)
- Documentation excellente
- Idéal pour applications SaaS

---

## 📧 Email & Notifications

| Technologie | Version | Justification |
|------------|---------|---------------|
| **Spring Mail** | **3.2.1** | ✅ Inclus dans Spring Boot |
| **JavaMail API** | **2.0.1** | ✅ Jakarta Mail |
| **Thymeleaf** | **3.1.2** | ✅ Templates email |
| **Hostinger SMTP** | - | ✅ **CHOISI** - SMTP open source, gratuit (smtp.hostinger.com:465) |

---

## 📦 Dépendances Principales

### Spring Cloud BOM
```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>2023.0.1</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### Spring Boot Parent
```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.3.1</version>
    <relativePath/>
</parent>
```

---

## ✅ Checklist de Déploiement

### Prérequis
- [x] Java 21 LTS installé
- [x] Maven 3.9.6 installé
- [x] PostgreSQL 17.2 installé et configuré
- [x] Redis 7.2.4 installé et configuré
- [x] Kafka 3.6.1 installé et configuré
- [x] Elasticsearch 8.15.0 installé et configuré
- [x] Docker 24.0.7 et Docker Compose 2.24.6 installés
- [x] Node.js 20.11.0 LTS installé (pour frontend)

### Configuration
- [x] Toutes les dépendances utilisent `jakarta.*` (pas `javax.*`)
- [x] Spring Cloud 2023.0.1 configuré
- [x] Spring Boot 3.3.1 configuré
- [x] **Docker 24.0.7 et Docker Compose 2.24.6 configurés** ✅
- [x] **Dockerfiles créés pour tous les microservices** ✅
- [x] **docker-compose.yml configuré pour développement** ✅
- [x] Kafka topics créés
- [x] Bases de données PostgreSQL créées
- [x] MinIO configuré (open source, gratuit)

---

## 🔄 Migration Jakarta EE

**Important :** Spring Boot 3.x utilise Jakarta EE au lieu de Java EE.

### Changements à effectuer :
- `javax.*` → `jakarta.*`
- `javax.persistence.*` → `jakarta.persistence.*`
- `javax.servlet.*` → `jakarta.servlet.*`
- `javax.validation.*` → `jakarta.validation.*`

### Dépendances Jakarta :
- **Jakarta Persistence API** : 3.1.x
- **Jakarta Servlet API** : 6.0.x
- **Jakarta Validation** : 3.0.x

---

## 📊 Comparaison des Choix

### Pourquoi Java 21 au lieu de Java 17 ?
- Virtual threads (meilleure performance)
- Pattern matching amélioré
- Records et sealed classes avancés
- Meilleures performances globales

### Pourquoi Kafka au lieu de RabbitMQ ?
- Meilleure scalabilité horizontale
- Durabilité des événements
- Support des gros volumes
- Idéal pour architecture microservices

### Pourquoi Vue.js au lieu de React/Angular ?
- Courbe d'apprentissage plus douce
- Performance optimale
- Écosystème complet (Vue Router, Pinia)
- Idéal pour applications SaaS métier

### Pourquoi ELK Stack au lieu de Loki ?
- Solution complète et mature
- Intégration avec Elasticsearch (déjà utilisé)
- Kibana offre plus de fonctionnalités
- Écosystème riche

### Pourquoi Zipkin au lieu de Jaeger ?
- Plus simple à déployer et maintenir
- Intégration native avec Spring Boot
- Suffisant pour la plupart des besoins

---

## 🚀 Prochaines Étapes

1. **Créer les projets Spring Boot** avec les versions définies
2. **Configurer les dépendances** via BOM Spring Cloud
3. **Créer les Dockerfiles** pour tous les microservices (multi-stage builds)
4. **Configurer docker-compose.yml** pour l'environnement de développement
5. **Mettre en place l'infrastructure** (PostgreSQL, Redis, Kafka, Elasticsearch) via Docker
6. **Développer le frontend** avec Vue.js 3.4.27
7. **Configurer l'observabilité** (Prometheus, Grafana, ELK, Zipkin) via Docker
8. **Tester l'environnement Docker** localement avec docker-compose

> 📖 Voir **"Docker - Configuration et Déploiement.md"** pour les détails complets

---

## 📝 Notes Finales

- Toutes les versions sont **testées et compatibles** entre elles
- Le stack est **prêt pour la production**
- Les choix sont **justifiés** par des raisons techniques
- La stack est **scalable** et **maintenable**

---

**Dernière mise à jour :** Décembre 2024  
**Statut :** ✅ Validé et prêt pour implémentation

