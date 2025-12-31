#!/bin/bash

# Script pour vérifier le statut d'Elasticsearch

ELASTICSEARCH_PORT="9200"

echo "🔍 Vérification du statut d'Elasticsearch..."

if docker ps | grep -q "elasticsearch"; then
    echo "✅ Conteneur Elasticsearch en cours d'exécution"
    docker ps | grep elasticsearch
    
    echo ""
    echo "📊 Informations Elasticsearch:"
    if command -v curl &> /dev/null; then
        curl -s http://localhost:${ELASTICSEARCH_PORT} | jq '.' || curl -s http://localhost:${ELASTICSEARCH_PORT}
    else
        echo "⚠️  curl n'est pas installé"
    fi
    
    echo ""
    echo "📋 Indexes:"
    if command -v curl &> /dev/null; then
        curl -s "http://localhost:${ELASTICSEARCH_PORT}/_cat/indices?v" || echo "Impossible de récupérer les indexes"
    fi
else
    echo "❌ Elasticsearch n'est pas en cours d'exécution"
    echo "💡 Pour démarrer: ./scripts/start-elasticsearch.sh"
fi

