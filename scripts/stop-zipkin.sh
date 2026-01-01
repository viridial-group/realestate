#!/bin/bash

# ========================
# Script d'Arrêt de Zipkin
# ========================

echo "🛑 Arrêt de Zipkin..."

if docker ps --format '{{.Names}}' | grep -q "^zipkin$"; then
    docker stop zipkin
    docker rm zipkin
    echo "✅ Zipkin arrêté"
else
    echo "ℹ️  Zipkin n'est pas en cours d'exécution"
fi

