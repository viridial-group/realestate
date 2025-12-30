#!/bin/bash

# Script d'installation de tous les services sur le VPS
# Usage: ./scripts/install-all-services.sh

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"

# Configuration
VPS_USER="root"
VPS_HOST="148.230.112.148"
APP_DIR="/var/realestate"
BIN_DIR="$APP_DIR/bin"
CONFIG_DIR="$APP_DIR/config"
LOG_DIR="$APP_DIR/logs"

# Services à installer
SERVICES=(
    "gateway:8080"
    "identity-service:8081"
    "organization-service:8082"
    "property-service:8083"
    "resource-service:8084"
    "document-service:8085"
    "workflow-service:8086"
    "notification-service:8087"
    "emailing-service:8088"
    "audit-service:8089"
    "billing-service:8090"
)

# Fonction pour créer application-prod.yml
create_prod_config() {
    local service_name=$1
    local port=$2
    
    # Déterminer le chemin de configuration sur le VPS
    local config_file="$CONFIG_DIR/$service_name-application-prod.yml"
    
    # Template de configuration de base
    local config_content="server:
  port: $port

spring:
  application:
    name: $service_name
  
  datasource:
    url: \${SPRING_DATASOURCE_URL:jdbc:postgresql://148.230.112.148:5432/realestate_db}
    username: \${SPRING_DATASOURCE_USERNAME:postgres}
    password: \${SPRING_DATASOURCE_PASSWORD:123456}
    driver-class-name: org.postgresql.Driver
  
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
  
  data:
    redis:
      host: \${SPRING_DATA_REDIS_HOST:148.230.112.148}
      port: \${SPRING_DATA_REDIS_PORT:6379}
      password: \${SPRING_DATA_REDIS_PASSWORD:Abcd@1984}
      database: \${SPRING_DATA_REDIS_DATABASE:0}"

    # Configuration spécifique pour gateway
    if [ "$service_name" = "gateway" ]; then
        config_content="server:
  port: $port

spring:
  application:
    name: gateway
  cloud:
    gateway:
      routes:
        - id: identity-service
          uri: http://localhost:8081
          predicates:
            - Path=/api/identity/**
        - id: organization-service
          uri: http://localhost:8082
          predicates:
            - Path=/api/organizations/**
        - id: resource-service
          uri: http://localhost:8084
          predicates:
            - Path=/api/resources/**
        - id: property-service
          uri: http://localhost:8083
          predicates:
            - Path=/api/properties/**
        - id: document-service
          uri: http://localhost:8085
          predicates:
            - Path=/api/documents/**
        - id: workflow-service
          uri: http://localhost:8086
          predicates:
            - Path=/api/workflows/**
        - id: notification-service
          uri: http://localhost:8087
          predicates:
            - Path=/api/notifications/**
        - id: emailing-service
          uri: http://localhost:8088
          predicates:
            - Path=/api/emails/**
        - id: audit-service
          uri: http://localhost:8089
          predicates:
            - Path=/api/audit/**
        - id: billing-service
          uri: http://localhost:8090
          predicates:
            - Path=/api/billing/**"
    fi
    
    # Ajouter la section management et logging
    config_content="$config_content

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

logging:
  level:
    root: INFO
    com.realestate: DEBUG"

    # Ajouter Swagger config pour les services (pas gateway)
    if [ "$service_name" != "gateway" ]; then
        config_content="$config_content

# Swagger/OpenAPI Configuration (désactivé en production par défaut)
springdoc:
  api-docs:
    enabled: false
  swagger-ui:
    enabled: false"
    fi
    
    # Écrire la configuration sur le VPS
    ssh $VPS_USER@$VPS_HOST "cat > $config_file << 'EOF'
$config_content
EOF"
}

echo "🔧 Installation de tous les services sur le VPS..."

# Créer les répertoires nécessaires
echo "📁 Création des répertoires..."
ssh $VPS_USER@$VPS_HOST "mkdir -p $BIN_DIR $CONFIG_DIR $LOG_DIR"

# Créer l'utilisateur realestate s'il n'existe pas
echo "👤 Vérification de l'utilisateur realestate..."
ssh $VPS_USER@$VPS_HOST "id -u realestate &>/dev/null || useradd -r -s /bin/bash -d $APP_DIR realestate"

# Compiler tous les services LOCALEMENT
echo "🔨 Compilation de tous les services (localement)..."
cd "$PROJECT_DIR"

# Vérifier que nous sommes dans le bon répertoire
if [ ! -f "pom.xml" ]; then
    echo "❌ Erreur: pom.xml non trouvé dans $PROJECT_DIR"
    exit 1
fi

# Installer d'abord le parent POM (sans modules)
echo "  → Installation du parent POM..."
mvn clean install -DskipTests -N

# Installer le module common
echo "  → Installation du module common..."
mvn clean install -DskipTests -pl common

# Compiler tous les services
echo "  → Compilation de tous les services..."
mvn clean package -DskipTests

# Copier les JARs et configurations
echo "📦 Copie des JARs et configurations..."
for service_info in "${SERVICES[@]}"; do
    IFS=':' read -r service_name port <<< "$service_info"
    
    echo "  → Installation de $service_name (port $port)..."
    
    # Déterminer le chemin du JAR
    if [ "$service_name" = "gateway" ]; then
        JAR_PATH="$PROJECT_DIR/gateway/target/gateway-*.jar"
        CONFIG_PATH="$PROJECT_DIR/gateway/src/main/resources/application-prod.yml"
    else
        JAR_PATH="$PROJECT_DIR/services/$service_name/target/$service_name-*.jar"
        CONFIG_PATH="$PROJECT_DIR/services/$service_name/src/main/resources/application-prod.yml"
    fi
    
    # Trouver le JAR exact (résoudre le wildcard)
    JAR_FILE=$(ls $JAR_PATH 2>/dev/null | head -1)
    
    # Copier le JAR
    if [ -n "$JAR_FILE" ] && [ -f "$JAR_FILE" ]; then
        scp "$JAR_FILE" $VPS_USER@$VPS_HOST:$BIN_DIR/$service_name.jar
        echo "    ✅ JAR copié: $(basename $JAR_FILE)"
    else
        echo "    ⚠️  JAR non trouvé pour $service_name (cherché: $JAR_PATH)"
        continue
    fi
    
    # Créer ou copier la configuration
    ssh $VPS_USER@$VPS_HOST "mkdir -p $CONFIG_DIR"
    
    if [ -f "$CONFIG_PATH" ]; then
        # Copier la configuration existante
        scp "$CONFIG_PATH" $VPS_USER@$VPS_HOST:$CONFIG_DIR/$service_name-application-prod.yml
        echo "    ✅ Configuration copiée"
    else
        # Créer la configuration application-prod.yml
        echo "    → Création de application-prod.yml..."
        create_prod_config "$service_name" "$port"
        echo "    ✅ Configuration créée"
    fi
    
    # Définir les permissions
    ssh $VPS_USER@$VPS_HOST "chown -R realestate:realestate $APP_DIR"
    ssh $VPS_USER@$VPS_HOST "chmod +x $BIN_DIR/$service_name.jar"
done

# Installer les services systemd
echo "⚙️  Installation des services systemd..."
ssh $VPS_USER@$VPS_HOST "cd /opt/source/realestate && ./scripts/install-services.sh"

echo ""
echo "✅ Installation terminée !"
echo ""
echo "📝 Prochaines étapes:"
echo "   1. Vérifier les configurations: ssh $VPS_USER@$VPS_HOST 'ls -la $CONFIG_DIR'"
echo "   2. Démarrer tous les services: ./scripts/start-all-services.sh"
echo "   3. Vérifier le statut: ./scripts/status-all-services.sh"

