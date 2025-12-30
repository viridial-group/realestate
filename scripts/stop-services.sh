#!/bin/bash

# ========================
# Script d'Arrêt des Services
# ========================

set -e

echo "🛑 Arrêt des services..."

# Arrêter les microservices
systemctl stop realestate-gateway
systemctl stop realestate-identity-service
systemctl stop realestate-organization-service
systemctl stop realestate-property-service
# ... autres services

# Arrêter Kafka
/opt/kafka/bin/kafka-server-stop.sh

# Arrêter Elasticsearch
systemctl stop elasticsearch

# Arrêter Redis
systemctl stop redis-server

# PostgreSQL reste actif (partagé)

echo "✅ Tous les services sont arrêtés!"

