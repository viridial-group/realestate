#!/bin/bash

# ========================
# Script d'Arrêt de Logstash
# ========================

echo "🛑 Arrêt de Logstash..."

if docker ps --format '{{.Names}}' | grep -q "^logstash$"; then
    docker stop logstash
    docker rm logstash
    echo "✅ Logstash arrêté"
else
    echo "ℹ️  Logstash n'est pas en cours d'exécution"
fi

