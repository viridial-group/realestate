# Versions et Compatibilité - Technologies Recommandées

## 📋 Vue d'ensemble
Ce document liste les versions recommandées et compatibles pour le SaaS Immobilier basé sur Spring Boot microservices.

**Date de mise à jour :** Décembre 2024

---

## 🔧 Technologies Backend

### Java & Framework Core
| Technologie | Version Recommandée | Version Minimale | Notes |
|------------|---------------------|------------------|-------|
| **Java** | **21 LTS** | 17 LTS | ✅ **CHOISI** - Dernière LTS avec meilleures performances |
| **Spring Boot** | **3.3.1** | 3.2.x | ✅ **CHOISI** - Version stable avec support long terme |
| **Spring Framework** | **6.1.24+** | 6.1.0 | Requis par Spring Boot 3.3.x |
| **Spring Cloud** | **2023.0.1** | 2023.0.0 | ✅ **CHOISI** - Compatible avec Spring Boot 3.3.x |
| **Spring Cloud Gateway** | **4.1.1** | 4.0.x | ✅ **CHOISI** - API Gateway pour microservices |
| **Spring Security** | **6.3.1** | 6.2.x | ✅ **CHOISI** - Inclus dans Spring Boot 3.3.x |
| **Spring Data JPA** | **3.2.1** | 3.1.x | ✅ **CHOISI** - Inclus dans Spring Boot 3.3.x |

### Build Tools
| Technologie | Version Recommandée | Version Minimale | Notes |
|------------|---------------------|------------------|-------|
| **Maven** | **3.9.6** | 3.6.3+ | ✅ **CHOISI** - Build tool principal (standard industrie) |

### Conteneurs Servlet
| Technologie | Version Recommandée | Notes |
|------------|---------------------|-------|
| **Tomcat** | **10.1.25+** | ✅ **CHOISI** - Servlet 6.0 - Conteneur par défaut Spring Boot |

---

## 🗄️ Bases de Données & Stockage

### Bases de Données Relationnelles
| Technologie | Version Recommandée | Version Minimale | Notes |
|------------|---------------------|------------------|-------|
| **PostgreSQL** | **17.2** | 14.x | ✅ **CHOISI** - Dernière version stable, meilleures performances |
| **PostgreSQL Driver** | **42.7.1** | 42.6.x | ✅ **CHOISI** - JDBC Driver pour Spring Boot 3.x |

### Cache & Session Store
| Technologie | Version Recommandée | Version Minimale | Notes |
|------------|---------------------|------------------|-------|
| **Redis** | **7.2.4** | 7.0.x | ✅ **CHOISI** - Cache, sessions, JWT, rate limiting |
| **Spring Data Redis** | **3.2.1** | 3.1.x | ✅ **CHOISI** - Inclus dans Spring Boot 3.3.x |

### Recherche & Indexation
| Technologie | Version Recommandée | Version Minimale | Notes |
|------------|---------------------|------------------|-------|
| **Elasticsearch** | **8.15.0** | 8.11.x | ✅ **CHOISI** - Recherche de propriétés et audit logs |
| **Elasticsearch Java Client** | **8.15.0** | 8.11.x | ✅ **CHOISI** - Client officiel compatible |

### Object Storage
| Technologie | Version Recommandée | Notes |
|------------|---------------------|-------|
| **VPS File System** | - | ✅ **CHOISI** - Système de fichiers du VPS pour stocker les documents |
| **Spring Resource** | 3.3.1 | ✅ **CHOISI** - Gestion des fichiers avec Spring |

---

## 📨 Messagerie & Event Bus

| Technologie | Version Recommandée | Version Minimale | Notes |
|------------|---------------------|------------------|-------|
| **Apache Kafka** | **3.6.1** | 3.5.x | ✅ **CHOISI** - Event bus pour haute performance et scalabilité |
| **Spring Kafka** | **3.1.1** | 3.0.x | ✅ **CHOISI** - Intégration Kafka pour Spring Boot |

**Justification :** Kafka est choisi pour sa capacité à gérer de gros volumes d'événements, sa durabilité et sa scalabilité horizontale, essentielles pour une architecture microservices en production.

---

## 🔐 Sécurité & Authentification

| Technologie | Version Recommandée | Notes |
|------------|---------------------|-------|
| **JWT (JJWT)** | **0.12.5** | ✅ **CHOISI** - Bibliothèque JWT pour Java |
| **OAuth2 Resource Server** | **1.3.1** | ✅ **CHOISI** - Inclus dans Spring Security 6.3.x |
| **OAuth2 Client** | **1.3.1** | ✅ **CHOISI** - Inclus dans Spring Security 6.3.x |
| **BCrypt** | **0.10.2** | ✅ **CHOISI** - Hachage de mots de passe (inclus dans Spring Security) |

---

## 📊 Observabilité & Monitoring

| Technologie | Version Recommandée | Notes |
|------------|---------------------|-------|
| **Prometheus** | **2.49.1** | ✅ **CHOISI** - Métriques et monitoring |
| **Grafana** | **10.3.3** | ✅ **CHOISI** - Visualisation des métriques |
| **Micrometer** | **1.12.5** | ✅ **CHOISI** - Inclus dans Spring Boot 3.3.x |
| **ELK Stack** | | ✅ **CHOISI** - Stack complet pour logs |
| - **Elasticsearch** | **8.15.0** | Voir section Recherche |
| - **Logstash** | **8.15.0** | ✅ **CHOISI** - Traitement des logs |
| - **Kibana** | **8.15.0** | ✅ **CHOISI** - Visualisation des logs |
| **Zipkin** | **2.24.4** | ✅ **CHOISI** - Distributed tracing (plus simple que Jaeger) |
| **Micrometer Tracing** | **1.2.1** | ✅ **CHOISI** - Tracing distribué moderne |

---

## 🎨 Frontend

| Technologie | Version Recommandée | Notes |
|------------|---------------------|-------|
| **Vue.js** | **3.4.27** | ✅ **CHOISI** - Framework frontend (simple, performant, écosystème riche) |
| **Node.js** | **20.11.0 LTS** | ✅ **CHOISI** - Runtime pour build frontend |
| **Vite** | **5.1.0** | ✅ **CHOISI** - Build tool moderne pour Vue.js |

---

## 🐳 Conteneurisation & Orchestration

| Technologie | Version Recommandée | Notes |
|------------|---------------------|-------|
| **Docker** | **24.0.7** | ✅ **CHOISI** - Conteneurisation des services - **ESSENTIEL** |
| **Docker Compose** | **2.24.6** | ✅ **CHOISI** - Orchestration locale - **ESSENTIEL** |
| **Docker Buildx** | Latest | ✅ **CHOISI** - Build multi-plateformes |
| **Kubernetes** | **1.29.2** | ✅ **CHOISI** - Orchestration production |
| **Helm** | **3.14.0** | ✅ **CHOISI** - Gestion de packages Kubernetes |

> 📖 Voir le document **"Docker - Configuration et Déploiement.md"** pour :
> - Dockerfiles multi-stage optimisés
> - Configuration docker-compose.yml complète
> - Bonnes pratiques Docker
> - Images Docker recommandées
> - Health checks et monitoring

---

## 📧 Email & Notifications

| Technologie | Version Recommandée | Notes |
|------------|---------------------|-------|
| **Spring Mail** | **3.2.1** | ✅ **CHOISI** - Inclus dans Spring Boot 3.3.x |
| **JavaMail API** | **2.0.1** | ✅ **CHOISI** - Jakarta Mail (migré de javax) |
| **Thymeleaf** | **3.1.2** | ✅ **CHOISI** - Templates email |
| **Hostinger SMTP** | - | ✅ **CHOISI** - SMTP open source, gratuit (smtp.hostinger.com:465) |

---

## 🔄 Migration Jakarta EE

**Important :** Spring Boot 3.x utilise **Jakarta EE** au lieu de **Java EE**.

### Changements principaux :
- `javax.*` → `jakarta.*`
- `javax.persistence.*` → `jakarta.persistence.*`
- `javax.servlet.*` → `jakarta.servlet.*`
- `javax.validation.*` → `jakarta.validation.*`

### Dépendances à mettre à jour :
- **Jakarta Persistence API** : 3.1.x
- **Jakarta Servlet API** : 6.0.x
- **Jakarta Validation** : 3.0.x

---

## 📦 Gestion des Dépendances

### Spring Cloud BOM (Bill of Materials)
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

### Spring Boot BOM
```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.3.1</version>
    <relativePath/>
</parent>
```

---

## ✅ Checklist de Compatibilité

Avant de démarrer le projet, vérifier :

- [x] **Java 21 LTS** installé ✅
- [x] **Spring Boot 3.3.1** configuré ✅
- [x] **Spring Cloud 2023.0.1** configuré ✅
- [x] **PostgreSQL 17.2** installé et configuré ✅
- [x] **Redis 7.2.4** installé et configuré ✅
- [x] **Kafka 3.6.1** installé et configuré ✅
- [x] **Elasticsearch 8.15.0** installé et configuré ✅
- [x] Toutes les dépendances utilisent `jakarta.*` au lieu de `javax.*` ✅
- [x] **Maven 3.9.6** installé ✅
- [x] **Docker 24.0.7** et **Docker Compose 2.24.6** installés ✅
- [x] **Vue.js 3.4.27** avec **Vite 5.1.0** pour le frontend ✅

---

## 🔗 Ressources Utiles

- [Spring Boot Release Notes](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.0-Release-Notes)
- [Spring Cloud Release Train](https://spring.io/projects/spring-cloud)
- [Spring Boot Compatibility Matrix](https://docs.spring.io/spring-boot/docs/current/reference/html/getting-started.html#getting-started.system-requirements)
- [PostgreSQL Downloads](https://www.postgresql.org/download/)
- [Redis Downloads](https://redis.io/download)

---

## 📝 Notes Importantes

1. **Java 21 LTS :** ✅ **CHOISI** - Dernière LTS avec meilleures performances que Java 17
2. **Spring Boot 3.3.1 :** ✅ **CHOISI** - Nécessite Java 17 minimum et utilise Jakarta EE
3. **PostgreSQL 17.2 :** ✅ **CHOISI** - Dernière version stable avec meilleures performances
4. **Redis 7.2.4 :** ✅ **CHOISI** - Meilleure compatibilité avec Spring Boot 3.x
5. **Kafka 3.6.1 :** ✅ **CHOISI** - Meilleur pour microservices à grande échelle
6. **Vue.js 3.4.27 :** ✅ **CHOISI** - Framework frontend simple et performant
7. **ELK Stack :** ✅ **CHOISI** - Solution complète pour logs (plus complet que Loki)
8. **Zipkin :** ✅ **CHOISI** - Tracing distribué (plus simple que Jaeger)
9. **Monitoring :** Implémenter Prometheus + Grafana dès le début pour la production

---

**Dernière mise à jour :** Décembre 2024

