# 🔐 Variables d'Environnement - Configuration par Environnement

**Date de création :** Décembre 2024

---

## 📋 Vue d'ensemble

Ce document liste toutes les variables d'environnement nécessaires pour chaque environnement (local, dev, staging, pre-prod, production).

---

## 🏗️ Structure des Fichiers

```
realestate/
├── .env.local              # Variables locales (non commitées)
├── .env.local.example      # Template pour local
├── .env.dev                # Variables développement
├── .env.staging            # Variables staging
├── .env.pre-prod           # Variables pre-production
├── .env.prod               # Variables production (non commitées)
└── .env.prod.example       # Template pour production
```

---

## 🔧 Variables par Environnement

### Local (.env.local)

```properties
# ========================
# ENVIRONMENT
# ========================
ENVIRONMENT=local
SPRING_PROFILES_ACTIVE=local

# ========================
# DATABASE - PostgreSQL
# ========================
DB_HOST=148.230.112.148
DB_PORT=5432
DB_NAME=realestate_db
DB_USER=postgres
DB_PASSWORD=postgres
DB_SCHEMA=public

# Spring Data Source Configuration
spring.datasource.url=jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}

# ========================
# REDIS
# ========================
REDIS_HOST=148.230.112.148
REDIS_PORT=6379
REDIS_USERNAME=
REDIS_PASSWORD=Abcd@1984
REDIS_DATABASE=0

# Spring Data Redis Configuration
spring.data.redis.host=${REDIS_HOST}
spring.data.redis.port=${REDIS_PORT}
spring.data.redis.username=${REDIS_USERNAME:}
spring.data.redis.password=${REDIS_PASSWORD}

# ========================
# KAFKA
# ========================
KAFKA_BOOTSTRAP_SERVERS=localhost:9092
KAFKA_GROUP_ID=realestate-local

# ========================
# ELASTICSEARCH
# ========================
ELASTICSEARCH_HOST=localhost
ELASTICSEARCH_PORT=9200
ELASTICSEARCH_USERNAME=
ELASTICSEARCH_PASSWORD=

# ========================
# FILE STORAGE - VPS File System
# ========================
FILE_STORAGE_PATH=/var/realestate/storage
FILE_STORAGE_URL_PREFIX=/api/files
# Note: Utilisation du système de fichiers du VPS pour stocker les documents

# ========================
# JWT
# ========================
JWT_SECRET=local-secret-key-change-in-production-min-256-bits
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000

# ========================
# OAUTH2
# ========================
OAUTH2_CLIENT_ID=local-client-id
OAUTH2_CLIENT_SECRET=local-client-secret

# ========================
# EMAIL - Hostinger SMTP
# ========================
FROM_NAME=support@viridial.com
SMTP_HOST=smtp.hostinger.com
SMTP_PORT=465
SMTP_SECURE=true
SMTP_USER=support@viridial.com
SMTP_PASS=S@upport!19823
EMAIL_FROM=support@viridial.com

# Spring Mail Configuration
spring.mail.host=${SMTP_HOST}
spring.mail.port=${SMTP_PORT}
spring.mail.username=${SMTP_USER}
spring.mail.password=${SMTP_PASS}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.ssl.enable=${SMTP_SECURE}
spring.mail.properties.mail.smtp.starttls.enable=${SMTP_SECURE}

# ========================
# API GATEWAY
# ========================
GATEWAY_PORT=8080
GATEWAY_CORS_ORIGINS=http://localhost:3000,http://localhost:5173

# URLs
APP_URL=http://localhost:5173
API_URL=http://localhost:8080
API_DOCS_URL=http://localhost:8080/swagger-ui.html

# ========================
# OBSERVABILITY
# ========================
PROMETHEUS_ENABLED=true
ZIPKIN_ENABLED=true
ZIPKIN_URL=http://localhost:9411

# ========================
# LOGGING
# ========================
LOG_LEVEL=DEBUG
LOGSTASH_HOST=localhost
LOGSTASH_PORT=5000
```

---

### Développement (.env.dev)

```properties
# ========================
# ENVIRONMENT
# ========================
ENVIRONMENT=dev
SPRING_PROFILES_ACTIVE=dev

# ========================
# DATABASE - PostgreSQL
# ========================
DB_HOST=148.230.112.148
DB_PORT=5432
DB_NAME=realestate_db_dev
DB_USER=postgres
DB_PASSWORD=postgres
DB_SCHEMA=public

# Spring Data Source Configuration
spring.datasource.url=jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}

# ========================
# REDIS
# ========================
REDIS_HOST=148.230.112.148
REDIS_PORT=6379
REDIS_USERNAME=
REDIS_PASSWORD=Abcd@1984
REDIS_DATABASE=0

# Spring Data Redis Configuration
spring.data.redis.host=${REDIS_HOST}
spring.data.redis.port=${REDIS_PORT}
spring.data.redis.username=${REDIS_USERNAME:}
spring.data.redis.password=${REDIS_PASSWORD}

# ========================
# KAFKA
# ========================
KAFKA_BOOTSTRAP_SERVERS=kafka-dev:9092
KAFKA_GROUP_ID=realestate-dev

# ========================
# ELASTICSEARCH
# ========================
ELASTICSEARCH_HOST=elasticsearch-dev
ELASTICSEARCH_PORT=9200
ELASTICSEARCH_USERNAME=${ELASTICSEARCH_USERNAME_DEV}
ELASTICSEARCH_PASSWORD=${ELASTICSEARCH_PASSWORD_DEV}

# ========================
# FILE STORAGE - VPS File System
# ========================
FILE_STORAGE_PATH=/var/realestate/storage/dev
FILE_STORAGE_URL_PREFIX=/api/files

# ========================
# JWT
# ========================
JWT_SECRET=${JWT_SECRET_DEV}
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000

# ========================
# EMAIL - Hostinger SMTP
# ========================
FROM_NAME=support@viridial.com
SMTP_HOST=smtp.hostinger.com
SMTP_PORT=465
SMTP_SECURE=true
SMTP_USER=support@viridial.com
SMTP_PASS=S@upport!19823
EMAIL_FROM=support@viridial.com

# Spring Mail Configuration
spring.mail.host=${SMTP_HOST}
spring.mail.port=${SMTP_PORT}
spring.mail.username=${SMTP_USER}
spring.mail.password=${SMTP_PASS}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.ssl.enable=${SMTP_SECURE}
spring.mail.properties.mail.smtp.starttls.enable=${SMTP_SECURE}

# ========================
# API GATEWAY
# ========================
GATEWAY_PORT=8080
GATEWAY_CORS_ORIGINS=https://dev-app.viridial.com

# URLs
APP_URL=https://dev-app.viridial.com
API_URL=https://dev-api.viridial.com
API_DOCS_URL=https://dev-api.viridial.com/swagger-ui.html

# ========================
# OBSERVABILITY
# ========================
PROMETHEUS_ENABLED=true
ZIPKIN_ENABLED=true
ZIPKIN_URL=http://zipkin-dev:9411

# ========================
# LOGGING
# ========================
LOG_LEVEL=INFO
LOGSTASH_HOST=logstash-dev
LOGSTASH_PORT=5000
```

---

### Staging (.env.staging)

```properties
# ========================
# ENVIRONMENT
# ========================
ENVIRONMENT=staging
SPRING_PROFILES_ACTIVE=staging

# ========================
# DATABASE - PostgreSQL
# ========================
DB_HOST=148.230.112.148
DB_PORT=5432
DB_NAME=realestate_db_staging
DB_USER=realestate_user
DB_PASSWORD=${DB_PASSWORD_STAGING}
DB_SCHEMA=public

# ========================
# REDIS
# ========================
REDIS_HOST=148.230.112.148
REDIS_PORT=6379
REDIS_PASSWORD=${REDIS_PASSWORD_STAGING}
REDIS_DATABASE=0

# ========================
# KAFKA
# ========================
KAFKA_BOOTSTRAP_SERVERS=kafka-staging-1:9092,kafka-staging-2:9092,kafka-staging-3:9092
KAFKA_GROUP_ID=realestate-staging

# ========================
# ELASTICSEARCH
# ========================
ELASTICSEARCH_HOST=elasticsearch-staging.aws.com
ELASTICSEARCH_PORT=9200
ELASTICSEARCH_USERNAME=${ELASTICSEARCH_USERNAME_STAGING}
ELASTICSEARCH_PASSWORD=${ELASTICSEARCH_PASSWORD_STAGING}

# ========================
# FILE STORAGE - VPS File System
# ========================
FILE_STORAGE_PATH=/var/realestate/storage/staging
FILE_STORAGE_URL_PREFIX=/api/files

# ========================
# JWT
# ========================
JWT_SECRET=${JWT_SECRET_STAGING}
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000

# ========================
# EMAIL - Hostinger SMTP
# ========================
FROM_NAME=support@viridial.com
SMTP_HOST=smtp.hostinger.com
SMTP_PORT=465
SMTP_SECURE=true
SMTP_USER=support@viridial.com
SMTP_PASS=S@upport!19823
EMAIL_FROM=support@viridial.com

# Spring Mail Configuration
spring.mail.host=${SMTP_HOST}
spring.mail.port=${SMTP_PORT}
spring.mail.username=${SMTP_USER}
spring.mail.password=${SMTP_PASS}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.ssl.enable=${SMTP_SECURE}
spring.mail.properties.mail.smtp.starttls.enable=${SMTP_SECURE}

# ========================
# API GATEWAY
# ========================
GATEWAY_PORT=8080
GATEWAY_CORS_ORIGINS=https://staging-app.viridial.com

# URLs
APP_URL=https://staging-app.viridial.com
API_URL=https://staging-api.viridial.com
API_DOCS_URL=https://staging-api-docs.viridial.com

# ========================
# OBSERVABILITY
# ========================
PROMETHEUS_ENABLED=true
ZIPKIN_ENABLED=true
ZIPKIN_URL=https://zipkin-staging.realestate.com

# ========================
# LOGGING
# ========================
LOG_LEVEL=WARN
LOGSTASH_HOST=logstash-staging.aws.com
LOGSTASH_PORT=5000
```

---

### Pre-Production (.env.pre-prod)

```properties
# ========================
# ENVIRONMENT
# ========================
ENVIRONMENT=pre-prod
SPRING_PROFILES_ACTIVE=pre-prod

# ========================
# DATABASE - PostgreSQL
# ========================
DB_HOST=148.230.112.148
DB_PORT=5432
DB_NAME=realestate_db_preprod
DB_USER=realestate_user
DB_PASSWORD=${DB_PASSWORD_PREPROD}
DB_SCHEMA=public

# ========================
# REDIS
# ========================
REDIS_HOST=148.230.112.148
REDIS_PORT=6379
REDIS_PASSWORD=${REDIS_PASSWORD_PREPROD}
REDIS_DATABASE=0

# ========================
# KAFKA
# ========================
KAFKA_BOOTSTRAP_SERVERS=kafka-preprod-1:9092,kafka-preprod-2:9092,kafka-preprod-3:9092
KAFKA_GROUP_ID=realestate-preprod

# ========================
# ELASTICSEARCH
# ========================
ELASTICSEARCH_HOST=elasticsearch-preprod.aws.com
ELASTICSEARCH_PORT=9200
ELASTICSEARCH_USERNAME=${ELASTICSEARCH_USERNAME_PREPROD}
ELASTICSEARCH_PASSWORD=${ELASTICSEARCH_PASSWORD_PREPROD}

# ========================
# FILE STORAGE - VPS File System
# ========================
FILE_STORAGE_PATH=/var/realestate/storage/preprod
FILE_STORAGE_URL_PREFIX=/api/files

# ========================
# JWT
# ========================
JWT_SECRET=${JWT_SECRET_PREPROD}
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000

# ========================
# EMAIL - Hostinger SMTP
# ========================
FROM_NAME=support@viridial.com
SMTP_HOST=smtp.hostinger.com
SMTP_PORT=465
SMTP_SECURE=true
SMTP_USER=support@viridial.com
SMTP_PASS=S@upport!19823
EMAIL_FROM=support@viridial.com

# Spring Mail Configuration
spring.mail.host=${SMTP_HOST}
spring.mail.port=${SMTP_PORT}
spring.mail.username=${SMTP_USER}
spring.mail.password=${SMTP_PASS}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.ssl.enable=${SMTP_SECURE}
spring.mail.properties.mail.smtp.starttls.enable=${SMTP_SECURE}

# ========================
# API GATEWAY
# ========================
GATEWAY_PORT=8080
GATEWAY_CORS_ORIGINS=https://preprod-app.viridial.com

# URLs
APP_URL=https://preprod-app.viridial.com
API_URL=https://preprod-api.viridial.com
API_DOCS_URL=https://preprod-api.viridial.com/swagger-ui.html

# ========================
# OBSERVABILITY
# ========================
PROMETHEUS_ENABLED=true
ZIPKIN_ENABLED=true
ZIPKIN_URL=https://zipkin-preprod.realestate.com

# ========================
# LOGGING
# ========================
LOG_LEVEL=WARN
LOGSTASH_HOST=logstash-preprod.aws.com
LOGSTASH_PORT=5000
```

---

### Production (.env.prod)

```properties
# ========================
# ENVIRONMENT
# ========================
ENVIRONMENT=production
SPRING_PROFILES_ACTIVE=prod

# ========================
# DATABASE - PostgreSQL
# ========================
DB_HOST=148.230.112.148
DB_PORT=5432
DB_NAME=realestate_db_prod
DB_USER=postgres
DB_PASSWORD=postgres
DB_SCHEMA=public

# Spring Data Source Configuration
spring.datasource.url=jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}

# ========================
# REDIS
# ========================
REDIS_HOST=148.230.112.148
REDIS_PORT=6379
REDIS_USERNAME=
REDIS_PASSWORD=Abcd@1984
REDIS_DATABASE=0

# Spring Data Redis Configuration
spring.data.redis.host=${REDIS_HOST}
spring.data.redis.port=${REDIS_PORT}
spring.data.redis.username=${REDIS_USERNAME:}
spring.data.redis.password=${REDIS_PASSWORD}

# ========================
# KAFKA (MSK)
# ========================
KAFKA_BOOTSTRAP_SERVERS=${KAFKA_BOOTSTRAP_SERVERS_PROD}
KAFKA_GROUP_ID=realestate-prod

# ========================
# ELASTICSEARCH (OpenSearch)
# ========================
ELASTICSEARCH_HOST=${ELASTICSEARCH_HOST_PROD}
ELASTICSEARCH_PORT=9200
ELASTICSEARCH_USERNAME=${ELASTICSEARCH_USERNAME_PROD}
ELASTICSEARCH_PASSWORD=${ELASTICSEARCH_PASSWORD_PROD}

# ========================
# FILE STORAGE - VPS File System
# ========================
FILE_STORAGE_PATH=/var/realestate/storage/prod
FILE_STORAGE_URL_PREFIX=/api/files

# ========================
# JWT
# ========================
JWT_SECRET=${JWT_SECRET_PROD}
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000

# ========================
# OAUTH2
# ========================
OAUTH2_CLIENT_ID=${OAUTH2_CLIENT_ID_PROD}
OAUTH2_CLIENT_SECRET=${OAUTH2_CLIENT_SECRET_PROD}

# ========================
# EMAIL - Hostinger SMTP
# ========================
FROM_NAME=support@viridial.com
SMTP_HOST=smtp.hostinger.com
SMTP_PORT=465
SMTP_SECURE=true
SMTP_USER=support@viridial.com
SMTP_PASS=S@upport!19823
EMAIL_FROM=support@viridial.com

# Spring Mail Configuration
spring.mail.host=${SMTP_HOST}
spring.mail.port=${SMTP_PORT}
spring.mail.username=${SMTP_USER}
spring.mail.password=${SMTP_PASS}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.ssl.enable=${SMTP_SECURE}
spring.mail.properties.mail.smtp.starttls.enable=${SMTP_SECURE}

# ========================
# API GATEWAY
# ========================
GATEWAY_PORT=8080
GATEWAY_CORS_ORIGINS=https://app.viridial.com,https://viridial.com

# URLs
APP_URL=https://app.viridial.com
API_URL=https://api.viridial.com
API_DOCS_URL=https://api-docs.viridial.com

# ========================
# OBSERVABILITY
# ========================
PROMETHEUS_ENABLED=true
ZIPKIN_ENABLED=true
ZIPKIN_URL=https://zipkin.realestate.com

# ========================
# LOGGING
# ========================
LOG_LEVEL=ERROR
LOGSTASH_HOST=${LOGSTASH_HOST_PROD}
LOGSTASH_PORT=5000

# ========================
# SECURITY
# ========================
ENABLE_HTTPS=true
SSL_CERT_PATH=/etc/ssl/certs/realestate.crt
SSL_KEY_PATH=/etc/ssl/private/realestate.key
```

---

## 🔐 Gestion des Secrets

### Local & Dev
- Utiliser des fichiers `.env` (non commités)
- Utiliser `.env.example` comme template

### Staging, Pre-Prod, Production
- Utiliser **Kubernetes Secrets** (open source)
- Utiliser **HashiCorp Vault** (open source) - Alternative recommandée
- Ne jamais commiter les secrets dans Git

### Exemple Kubernetes Secret

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: realestate-secrets
type: Opaque
stringData:
  DB_PASSWORD: ${DB_PASSWORD}
  JWT_SECRET: ${JWT_SECRET}
  FILE_STORAGE_PATH: ${FILE_STORAGE_PATH}
```

---

## ✅ Checklist de Configuration

### Avant Déploiement

- [ ] Tous les fichiers `.env.example` créés
- [ ] Variables d'environnement documentées
- [ ] Secrets configurés dans Kubernetes Secrets ou HashiCorp Vault (open source)
- [ ] `.env.local` et `.env.prod` dans `.gitignore`
- [ ] Validation des variables dans le code
- [ ] Tests avec différentes configurations

---

## 📝 Notes Importantes

1. **Ne jamais commiter** les fichiers `.env.local` et `.env.prod`
2. **Toujours utiliser** des secrets managers en production
3. **Valider** toutes les variables au démarrage de l'application
4. **Documenter** toutes les nouvelles variables
5. **Utiliser** des valeurs par défaut sécurisées

---

**Dernière mise à jour :** Décembre 2024

