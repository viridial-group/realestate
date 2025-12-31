#!/bin/bash

# Script pour arrêter Kibana

set -e

echo "🛑 Arrêt de Kibana..."

if docker ps | grep -q "kibana"; then
    docker stop kibana
    echo "✅ Kibana arrêté"
else
    echo "ℹ️  Kibana n'est pas en cours d'exécution"
fi

