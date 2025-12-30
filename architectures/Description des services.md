# Description des Services

## 📋 Versions Recommandées (Choix Définitifs) ✅

**Stack Technique :**
- **Java** : 21 LTS ✅
- **Spring Boot** : 3.3.1 ✅
- **Spring Security** : 6.3.1 ✅
- **Spring Cloud** : 2023.0.1 ✅
- **PostgreSQL** : 17.2 ✅
- **Redis** : 7.2.4 ✅
- **Elasticsearch** : 8.15.0 ✅
- **Kafka** : 3.6.1 ✅
- **Vue.js** : 3.4.27 ✅

---

## a) Identity & Auth Service

**Technologies :**
- Spring Security 6.3.1 + JWT (JJWT 0.12.5) + OAuth2 1.3.1 ✅
- Spring Boot 3.3.1 ✅
- PostgreSQL 17.2 (Users, Roles, Permissions) ✅
- Redis 7.2.4 (Cache sessions, tokens JWT) ✅

**Fonctionnalités :**
- RBAC + ACL (ResourceAccess, PropertyAccess)
- Gestion des utilisateurs et délégation de rôles
- Authentification JWT et OAuth2

b) Organization & Team Service

Organisation multi-tenant, hiérarchie (parent / filiales)

Teams et rôles personnalisables par organisation

Assignation de rôles aux utilisateurs

c) Property Service

Gestion des propriétés (Property = Resource)

Vérification RBAC + ACL + Plan (feature flags)

Intégration avec Workflow, Document et Notification

d) Resource Service

Resource générique pour étendre à d’autres domaines

Gestion des tags, domaines, partage entre organisations

e) Document Service

Upload sécurisé, stockage externe (S3/GCS/MinIO)

Association à Property ou Resource

Notifications et audit à chaque action

f) Workflow Engine Service

Multi-étapes pour création, modification, suppression

Assignation aux rôles / utilisateurs

Déclenche events pour notification et email

g) Audit Service

Tracé complet pour toutes actions critiques

Recherche via Elasticsearch pour reporting et conformité

h) Notification Service

Notifications push, in-app, SMS

Subscriptions par utilisateur / équipe

i) Emailing Service

Emails transactionnels et workflow-triggered

Templates multi-tenant

j) Billing / Plan Service

Gestion des plans et abonnements par organisation

Feature flags pour activer/désactiver certaines fonctionnalités selon plan

Notifications et emails sur renouvellement ou dépassement de quota

## 3️⃣ Base de données et cache

**PostgreSQL 17.2** : base relationnelle multi-tenant ✅
- Driver JDBC : 42.7.1 ✅
- Spring Data JPA : 3.2.1 ✅

**Redis 7.2.4** : cache des sessions, tokens JWT, quotas, rate limiting ✅
- Spring Data Redis : 3.2.1 ✅

**Elasticsearch 8.15.0** : recherche rapide des propriétés et audit logs ✅
- Client Java : 8.15.0 ✅

**Object Storage** : documents et médias (PDF, images, vidéos) ✅
- **MinIO** : RELEASE.2024-12-13T00-00-00Z (développement)
- **VPS File System** : Système de fichiers du VPS (/var/realestate/storage) ✅

## 4️⃣ Event-driven architecture

**Messagerie :**
- **Kafka 3.6.1** ✅ avec Spring Kafka 3.1.1 ✅
- Choix définitif pour haute performance et scalabilité

**Exemple de flux :** Property créé → Workflow déclenché → Notifications → Emails → Audit

5️⃣ Contrôle d’accès combiné Plan + Role
Si utilisateur.has_permission("create_property") ET
   organisation.plan.includes_feature("property") ET
   ACL.allows_access(utilisateur, property):
       autoriser action
Sinon:
       refuser action


Permet de combiner RBAC + ACL + feature flags du plan

## 6️⃣ Observabilité et monitoring

**Métriques :**
- **Prometheus** : 2.49.1 ✅ (collecte métriques)
- **Grafana** : 10.3.3 ✅ (visualisation)
- **Micrometer** : 1.12.5 ✅ (intégration Spring Boot)

**Logs centralisés :**
- **ELK Stack** : Elasticsearch 8.15.0, Logstash 8.15.0, Kibana 8.15.0 ✅
- Choix définitif - Solution complète pour logs

**Tracing distribué :**
- **Zipkin** : 2.24.4 ✅
- **Micrometer Tracing** : 1.2.1 ✅ (intégration Spring Boot)

💡 Avantages de cette architecture

Scalable et microservices-ready

Multi-tenant avec hiérarchie et ACL

Feature flags via Plan pour contrôle granulaire

Audit et notifications centralisés

Extensible à d’autres types de Resource (ex : véhicules, biens commerciaux)

Prêt pour CI/CD et déploiement cloud (Docker + Kubernetes)