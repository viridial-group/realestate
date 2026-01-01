#!/bin/bash

# ========================
# Script d'Arrêt des Services
# ========================

set -e

echo "🛑 Arrêt des services..."

# ========================
# Microservices
# ========================
echo "🚀 Arrêt des microservices..."

services=(
    "realestate-gateway"
    "realestate-identity-service"
    "realestate-property-service"
)

for service in "${services[@]}"; do
    if systemctl is-active --quiet "$service" 2>/dev/null; then
        systemctl stop "$service"
        echo "✅ $service arrêté"
    else
        echo "ℹ️  $service déjà arrêté"
    fi
done

# ========================
# Services Optionnels
# ========================
echo ""
echo "📦 Arrêt des services optionnels..."

# Kafka
if pgrep -f kafka > /dev/null; then
    /opt/kafka/bin/kafka-server-stop.sh 2>/dev/null || true
    echo "✅ Kafka arrêté"
fi

# Elasticsearch
if systemctl is-active --quiet elasticsearch 2>/dev/null; then
    systemctl stop elasticsearch
    echo "✅ Elasticsearch arrêté"
fi

# Redis (optionnel - peut être partagé)
# systemctl stop redis-server

# PostgreSQL reste actif (partagé avec d'autres services)

echo ""
echo "✅ Arrêt terminé!"
echo ""
echo "ℹ️  PostgreSQL et Redis restent actifs (services partagés)"

