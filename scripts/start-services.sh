#!/bin/bash

# ========================
# Script de Démarrage des Services
# ========================

set -e

ENVIRONMENT=${1:-prod}
APP_DIR=/var/realestate

echo "🚀 Démarrage des services - Environnement: $ENVIRONMENT"

# Démarrer PostgreSQL
systemctl start postgresql
echo "✅ PostgreSQL démarré"

# Démarrer Redis
systemctl start redis-server
echo "✅ Redis démarré"

# Démarrer Elasticsearch
systemctl start elasticsearch
echo "✅ Elasticsearch démarré"

# Démarrer Kafka
/opt/kafka/bin/kafka-server-start.sh -daemon /opt/kafka/config/server.properties
echo "✅ Kafka démarré"

# Démarrer les microservices
systemctl start realestate-gateway
echo "✅ Gateway démarré"

systemctl start realestate-identity-service
echo "✅ Identity Service démarré"

systemctl start realestate-organization-service
echo "✅ Organization Service démarré"

systemctl start realestate-property-service
echo "✅ Property Service démarré"

# ... autres services

echo "✅ Tous les services sont démarrés!"

