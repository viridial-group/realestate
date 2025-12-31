#!/bin/bash

# Script pour démarrer Elasticsearch avec Docker
# Version: 8.15.0

set -e

ELASTICSEARCH_VERSION="8.15.0"
ELASTICSEARCH_PORT="9200"
ELASTICSEARCH_NETWORK="realestate-network"

echo "🔍 Démarrage d'Elasticsearch ${ELASTICSEARCH_VERSION}..."

# Créer le réseau Docker s'il n'existe pas
if ! docker network ls | grep -q "$ELASTICSEARCH_NETWORK"; then
    echo "📡 Création du réseau Docker: $ELASTICSEARCH_NETWORK"
    docker network create $ELASTICSEARCH_NETWORK
fi

# Vérifier si Elasticsearch est déjà en cours d'exécution
if docker ps | grep -q "elasticsearch"; then
    echo "⚠️  Elasticsearch est déjà en cours d'exécution"
    docker ps | grep elasticsearch
    exit 0
fi

# Arrêter et supprimer le conteneur existant s'il existe
if docker ps -a | grep -q "elasticsearch"; then
    echo "🛑 Arrêt du conteneur Elasticsearch existant..."
    docker stop elasticsearch 2>/dev/null || true
    docker rm elasticsearch 2>/dev/null || true
fi

# Créer le répertoire pour les données Elasticsearch
ELASTICSEARCH_DATA_DIR="/tmp/elasticsearch-data"
mkdir -p "$ELASTICSEARCH_DATA_DIR"
chmod 777 "$ELASTICSEARCH_DATA_DIR"

echo "🚀 Démarrage du conteneur Elasticsearch..."

# Démarrer Elasticsearch
docker run -d \
    --name elasticsearch \
    --network $ELASTICSEARCH_NETWORK \
    -p ${ELASTICSEARCH_PORT}:9200 \
    -p 9300:9300 \
    -e "discovery.type=single-node" \
    -e "xpack.security.enabled=false" \
    -e "xpack.security.enrollment.enabled=false" \
    -e "ES_JAVA_OPTS=-Xms512m -Xmx512m" \
    -v "$ELASTICSEARCH_DATA_DIR:/usr/share/elasticsearch/data" \
    docker.elastic.co/elasticsearch/elasticsearch:${ELASTICSEARCH_VERSION}

echo "⏳ Attente du démarrage d'Elasticsearch (30 secondes)..."
sleep 30

# Vérifier que Elasticsearch est démarré
MAX_RETRIES=10
RETRY_COUNT=0

while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    if curl -s http://localhost:${ELASTICSEARCH_PORT} > /dev/null 2>&1; then
        echo "✅ Elasticsearch est démarré et accessible sur http://localhost:${ELASTICSEARCH_PORT}"
        echo ""
        echo "📊 Informations Elasticsearch:"
        if command -v jq &> /dev/null; then
            curl -s http://localhost:${ELASTICSEARCH_PORT} | jq '.'
        else
            curl -s http://localhost:${ELASTICSEARCH_PORT}
            echo ""
            echo "💡 Astuce: Installez 'jq' pour un meilleur formatage JSON: brew install jq"
        fi
        exit 0
    fi
    RETRY_COUNT=$((RETRY_COUNT + 1))
    echo "⏳ Tentative $RETRY_COUNT/$MAX_RETRIES..."
    sleep 3
done

echo "❌ Erreur: Elasticsearch n'a pas démarré correctement"
echo "📋 Logs du conteneur:"
docker logs elasticsearch
exit 1

