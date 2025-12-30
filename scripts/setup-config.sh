#!/bin/bash

# ========================
# Script de Configuration Production
# ========================
# Usage: ./setup-config.sh

set -e

echo "📝 Configuration de production pour Gateway..."

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
echo "📋 Vérification:"
ls -lh /var/realestate/config/application-prod.yml
echo ""
echo "📄 Contenu:"
cat /var/realestate/config/application-prod.yml

