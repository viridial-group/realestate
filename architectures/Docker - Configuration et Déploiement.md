# 🐳 Docker - Configuration et Déploiement

**Date de mise à jour :** Décembre 2024  
**Versions :** Docker 24.0.7, Docker Compose 2.24.6

---

## 📋 Vue d'ensemble

Ce document décrit la stratégie Docker pour le projet SaaS Immobilier, incluant les Dockerfiles, docker-compose, et les bonnes pratiques de conteneurisation.

---

## 🔧 Versions Docker

| Technologie | Version | Notes |
|------------|---------|-------|
| **Docker** | **24.0.7** | ✅ Moteur de conteneurisation |
| **Docker Compose** | **2.24.6** | ✅ Orchestration locale |
| **Docker Buildx** | Latest | ✅ Build multi-plateformes |
| **Kubernetes** | **1.29.2** | ✅ Orchestration production |
| **Helm** | **3.14.0** | ✅ Gestion de packages Kubernetes |

---

## 🏗️ Structure Docker

### Organisation des Dockerfiles

Chaque microservice aura son propre Dockerfile optimisé :

```
realestate/
├── docker/
│   ├── docker-compose.yml          # Environnement de développement
│   ├── docker-compose.prod.yml     # Environnement de production
│   └── .env.example                # Variables d'environnement
├── services/
│   ├── identity-service/
│   │   └── Dockerfile
│   ├── organization-service/
│   │   └── Dockerfile
│   ├── property-service/
│   │   └── Dockerfile
│   └── ... (autres services)
└── gateway/
    └── Dockerfile
```

---

## 📝 Dockerfile Template pour Microservices Spring Boot

### Dockerfile Multi-Stage (Optimisé)

```dockerfile
# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copier les fichiers de configuration Maven
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

# Télécharger les dépendances (cache layer)
RUN mvn dependency:go-offline -B

# Copier le code source
COPY src ./src

# Build de l'application
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Créer un utilisateur non-root
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copier le JAR depuis le stage de build
COPY --from=build /app/target/*.jar app.jar

# Variables d'environnement
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Exposer le port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Point d'entrée
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

### Dockerfile Optimisé avec Jib (Alternative)

```dockerfile
# Utiliser Jib pour build optimisé (pas besoin de Dockerfile)
# Configuration dans pom.xml avec jib-maven-plugin
```

---

## 🐙 Docker Compose pour Développement

### docker-compose.yml

```yaml
version: '3.8'

services:
  # ========================
  # INFRASTRUCTURE
  # ========================
  postgres:
    image: postgres:17.2-alpine
    container_name: realestate-postgres
    environment:
      POSTGRES_DB: realestate_db
      POSTGRES_USER: realestate_user
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD:-changeme}
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    networks:
      - realestate-network
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U realestate_user"]
      interval: 10s
      timeout: 5s
      retries: 5

  redis:
    image: redis:7.2.4-alpine
    container_name: realestate-redis
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
    networks:
      - realestate-network
    command: redis-server --appendonly yes
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 3s
      retries: 5

  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch:8.15.0
    container_name: realestate-elasticsearch
    environment:
      - discovery.type=single-node
      - xpack.security.enabled=false
      - "ES_JAVA_OPTS=-Xms512m -Xmx512m"
    ports:
      - "9200:9200"
      - "9300:9300"
    volumes:
      - elasticsearch_data:/usr/share/elasticsearch/data
    networks:
      - realestate-network
    healthcheck:
      test: ["CMD-SHELL", "curl -f http://localhost:9200/_cluster/health || exit 1"]
      interval: 30s
      timeout: 10s
      retries: 5

  kafka:
    image: apache/kafka:3.6.1
    container_name: realestate-kafka
    ports:
      - "9092:9092"
      - "9093:9093"
    environment:
      KAFKA_NODE_ID: 1
      KAFKA_PROCESS_ROLES: broker,controller
      KAFKA_LISTENERS: PLAINTEXT://:9092,CONTROLLER://:9093
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_CONTROLLER_LISTENER_NAMES: CONTROLLER
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT
      KAFKA_CONTROLLER_QUORUM_VOTERS: 1@localhost:9093
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 1
      KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: 1
    volumes:
      - kafka_data:/var/lib/kafka/data
    networks:
      - realestate-network
    healthcheck:
      test: ["CMD-SHELL", "kafka-broker-api-versions --bootstrap-server localhost:9092 || exit 1"]
      interval: 30s
      timeout: 10s
      retries: 5

  minio:
    image: minio/minio:RELEASE.2024-12-13T00-00-00Z
    container_name: realestate-minio
    ports:
      - "9000:9000"
      - "9001:9001"
    environment:
      MINIO_ROOT_USER: ${MINIO_ROOT_USER:-minioadmin}
      MINIO_ROOT_PASSWORD: ${MINIO_ROOT_PASSWORD:-minioadmin}
    volumes:
      - minio_data:/data
    networks:
      - realestate-network
    command: server /data --console-address ":9001"
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:9000/minio/health/live"]
      interval: 30s
      timeout: 20s
      retries: 3

  # ========================
  # OBSERVABILITÉ
  # ========================
  prometheus:
    image: prom/prometheus:2.49.1
    container_name: realestate-prometheus
    ports:
      - "9090:9090"
    volumes:
      - ./prometheus/prometheus.yml:/etc/prometheus/prometheus.yml
      - prometheus_data:/prometheus
    networks:
      - realestate-network
    command:
      - '--config.file=/etc/prometheus/prometheus.yml'
      - '--storage.tsdb.path=/prometheus'

  grafana:
    image: grafana/grafana:10.3.3
    container_name: realestate-grafana
    ports:
      - "3000:3000"
    environment:
      - GF_SECURITY_ADMIN_USER=admin
      - GF_SECURITY_ADMIN_PASSWORD=${GRAFANA_PASSWORD:-admin}
    volumes:
      - grafana_data:/var/lib/grafana
    networks:
      - realestate-network
    depends_on:
      - prometheus

  zipkin:
    image: openzipkin/zipkin:2.24.4
    container_name: realestate-zipkin
    ports:
      - "9411:9411"
    networks:
      - realestate-network

  # ========================
  # MICROSERVICES
  # ========================
  gateway:
    build:
      context: ./gateway
      dockerfile: Dockerfile
    container_name: realestate-gateway
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - SPRING_CLOUD_GATEWAY_ROUTES[0].id=identity-service
      - SPRING_CLOUD_GATEWAY_ROUTES[0].uri=http://identity-service:8080
    networks:
      - realestate-network
    depends_on:
      postgres:
        condition: service_healthy
      redis:
        condition: service_healthy
    healthcheck:
      test: ["CMD", "wget", "--no-verbose", "--tries=1", "--spider", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3

  identity-service:
    build:
      context: ./services/identity-service
      dockerfile: Dockerfile
    container_name: realestate-identity-service
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/realestate_db
      - SPRING_DATASOURCE_USERNAME=realestate_user
      - SPRING_DATASOURCE_PASSWORD=${POSTGRES_PASSWORD:-changeme}
      - SPRING_REDIS_HOST=redis
      - SPRING_REDIS_PORT=6379
    networks:
      - realestate-network
    depends_on:
      postgres:
        condition: service_healthy
      redis:
        condition: service_healthy
    healthcheck:
      test: ["CMD", "wget", "--no-verbose", "--tries=1", "--spider", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3

  # ... autres microservices avec la même structure

volumes:
  postgres_data:
  redis_data:
  elasticsearch_data:
  kafka_data:
  minio_data:
  prometheus_data:
  grafana_data:

networks:
  realestate-network:
    driver: bridge
```

---

## 🚀 Docker Compose pour Production

### docker-compose.prod.yml

```yaml
version: '3.8'

services:
  # Infrastructure (identique au dev)
  postgres:
    image: postgres:17.2-alpine
    restart: always
    environment:
      POSTGRES_DB: ${POSTGRES_DB}
      POSTGRES_USER: ${POSTGRES_USER}
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
    volumes:
      - postgres_data:/var/lib/postgresql/data
    networks:
      - realestate-network
    deploy:
      resources:
        limits:
          cpus: '2'
          memory: 2G
        reservations:
          cpus: '1'
          memory: 1G

  # ... autres services avec restart: always et resource limits
```

---

## 📦 Images Docker Recommandées

### Base Images

| Service | Image | Version | Justification |
|---------|-------|---------|---------------|
| **Java Runtime** | `eclipse-temurin:21-jre-alpine` | Latest | ✅ Image officielle, légère (Alpine) |
| **Maven Build** | `maven:3.9.6-eclipse-temurin-21` | Latest | ✅ Build avec Maven |
| **PostgreSQL** | `postgres:17.2-alpine` | 17.2 | ✅ Image Alpine légère |
| **Redis** | `redis:7.2.4-alpine` | 7.2.4 | ✅ Image Alpine légère |
| **Elasticsearch** | `docker.elastic.co/elasticsearch/elasticsearch:8.15.0` | 8.15.0 | ✅ Image officielle |
| **Kafka** | `apache/kafka:3.6.1` | 3.6.1 | ✅ Image officielle |
| **MinIO** | `minio/minio:RELEASE.2024-12-13T00-00-00Z` | Latest | ✅ Image officielle |

### Observabilité

| Service | Image | Version |
|---------|-------|---------|
| **Prometheus** | `prom/prometheus:2.49.1` | 2.49.1 |
| **Grafana** | `grafana/grafana:10.3.3` | 10.3.3 |
| **Zipkin** | `openzipkin/zipkin:2.24.4` | 2.24.4 |

---

## ✅ Bonnes Pratiques Docker

### 1. Multi-Stage Builds
- Réduire la taille des images finales
- Séparer les étapes de build et runtime

### 2. Utilisateur Non-Root
- Créer un utilisateur dédié dans les conteneurs
- Réduire les risques de sécurité

### 3. Health Checks
- Implémenter des health checks pour tous les services
- Utiliser Spring Boot Actuator `/actuator/health`

### 4. Variables d'Environnement
- Utiliser des fichiers `.env` pour la configuration
- Ne jamais commiter les secrets dans les images

### 5. Volumes pour Persistance
- Utiliser des volumes nommés pour les données
- Séparer les données des conteneurs

### 6. Réseaux Docker
- Créer un réseau dédié pour l'application
- Isoler les services

### 7. Resource Limits
- Définir des limites CPU et mémoire
- Éviter la surconsommation de ressources

### 8. .dockerignore
- Exclure les fichiers inutiles du contexte de build
- Réduire le temps de build

**Exemple .dockerignore :**
```
.git
.gitignore
README.md
target/
.idea/
*.iml
.env
```

---

## 🔧 Configuration Spring Boot pour Docker

### application-docker.yml

```yaml
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST:postgres}:${DB_PORT:5432}/${DB_NAME:realestate_db}
    username: ${DB_USER:realestate_user}
    password: ${DB_PASSWORD}
  
  redis:
    host: ${REDIS_HOST:redis}
    port: ${REDIS_PORT:6379}
  
  kafka:
    bootstrap-servers: ${KAFKA_BOOTSTRAP_SERVERS:kafka:9092}

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  health:
    liveness-state:
      enabled: true
    readiness-state:
      enabled: true
```

---

## 🚀 Commandes Docker Utiles

### Développement

```bash
# Démarrer tous les services
docker-compose up -d

# Démarrer un service spécifique
docker-compose up -d postgres redis

# Voir les logs
docker-compose logs -f identity-service

# Arrêter tous les services
docker-compose down

# Arrêter et supprimer les volumes
docker-compose down -v

# Rebuild une image
docker-compose build identity-service

# Rebuild et redémarrer
docker-compose up -d --build identity-service
```

### Production

```bash
# Démarrer avec le fichier de production
docker-compose -f docker-compose.prod.yml up -d

# Vérifier les health checks
docker-compose ps

# Voir les ressources utilisées
docker stats
```

---

## 📊 Monitoring Docker

### Métriques à Surveiller

- **CPU Usage** : Par conteneur
- **Memory Usage** : Par conteneur
- **Network I/O** : Trafic réseau
- **Disk I/O** : Accès disque
- **Health Status** : État des health checks

### Outils

- **Docker Stats** : `docker stats`
- **cAdvisor** : Monitoring des conteneurs
- **Prometheus** : Collecte de métriques Docker

---

## 🔒 Sécurité Docker

### Bonnes Pratiques

1. **Scans de Vulnérabilités**
   - Utiliser `docker scan` ou Trivy
   - Scanner les images avant déploiement

2. **Secrets Management**
   - Utiliser Docker Secrets ou variables d'environnement sécurisées
   - Ne jamais hardcoder les secrets

3. **Images Minimales**
   - Utiliser des images Alpine
   - Supprimer les packages inutiles

4. **Mises à Jour**
   - Maintenir les images à jour
   - Surveiller les CVE

---

## 📝 Checklist Docker

### Avant Déploiement

- [ ] Tous les Dockerfiles utilisent multi-stage builds
- [ ] Health checks configurés pour tous les services
- [ ] Utilisateurs non-root dans les conteneurs
- [ ] Variables d'environnement externalisées
- [ ] Volumes configurés pour la persistance
- [ ] Resource limits définis
- [ ] .dockerignore configuré
- [ ] Images scannées pour vulnérabilités
- [ ] docker-compose.yml testé localement
- [ ] Documentation à jour

---

## 🔗 Ressources

- [Docker Documentation](https://docs.docker.com/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Spring Boot Docker Guide](https://spring.io/guides/gs/spring-boot-docker/)
- [Best Practices for Writing Dockerfiles](https://docs.docker.com/develop/dev-best-practices/)

---

**Dernière mise à jour :** Décembre 2024

