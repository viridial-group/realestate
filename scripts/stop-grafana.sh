#!/bin/bash

# ========================
# Script d'Arrêt de Grafana
# ========================

echo "🛑 Arrêt de Grafana..."

if docker ps --format '{{.Names}}' | grep -q "^grafana$"; then
    docker stop grafana
    docker rm grafana
    echo "✅ Grafana arrêté"
else
    echo "ℹ️  Grafana n'est pas en cours d'exécution"
fi

