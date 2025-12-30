#!/bin/bash

# ========================
# Script de Création de Configuration Production
# ========================
# Usage: ./create-prod-config.sh

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"

echo "📝 Création de la configuration de production..."

# Créer le répertoire de config
mkdir -p /var/realestate/config

# Créer application-prod.yml
cat > /var/realestate/config/application-prod.yml << 'EOF'
server:
  port: 8080

spring:
  application:
    name: gateway
  cloud:
    gateway:
      routes:
        # Routes seront ajoutées quand les services seront créés
        - id: fallback
          uri: http://localhost:8080
          predicates:
            - Path=/**
          filters:
            - SetStatus=503

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
    com.realestate: DEBUG
    org.springframework.cloud.gateway: DEBUG
EOF

echo "✅ Configuration créée: /var/realestate/config/application-prod.yml"
echo ""
echo "📋 Contenu du fichier:"
cat /var/realestate/config/application-prod.yml

