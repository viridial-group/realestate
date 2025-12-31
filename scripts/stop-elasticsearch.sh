#!/bin/bash

# Script pour arrêter Elasticsearch

set -e

echo "🛑 Arrêt d'Elasticsearch..."

if docker ps | grep -q "elasticsearch"; then
    docker stop elasticsearch
    echo "✅ Elasticsearch arrêté"
else
    echo "ℹ️  Elasticsearch n'est pas en cours d'exécution"
fi

