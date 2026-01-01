#!/bin/bash

# ========================
# Script d'Arrêt de Prometheus
# ========================

echo "🛑 Arrêt de Prometheus..."

if docker ps --format '{{.Names}}' | grep -q "^prometheus$"; then
    docker stop prometheus
    docker rm prometheus
    echo "✅ Prometheus arrêté"
else
    echo "ℹ️  Prometheus n'est pas en cours d'exécution"
fi

