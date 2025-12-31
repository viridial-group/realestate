#!/bin/bash

# Script pour démarrer Kibana (interface graphique pour Elasticsearch)
# Version: 8.15.0 (compatible avec Elasticsearch 8.15.0)

set -e

KIBANA_VERSION="8.15.0"
KIBANA_PORT="5601"
ELASTICSEARCH_NETWORK="realestate-network"

echo "🎨 Démarrage de Kibana ${KIBANA_VERSION}..."

# Vérifier que Elasticsearch est en cours d'exécution
if ! docker ps | grep -q "elasticsearch"; then
    echo "❌ Erreur: Elasticsearch n'est pas en cours d'exécution"
    echo "💡 Démarrez d'abord Elasticsearch: ./scripts/start-elasticsearch.sh"
    exit 1
fi

# Vérifier si Kibana est déjà en cours d'exécution
if docker ps | grep -q "kibana"; then
    echo "⚠️  Kibana est déjà en cours d'exécution"
    docker ps | grep kibana
    echo ""
    echo "🌐 Accès: http://localhost:${KIBANA_PORT}"
    exit 0
fi

# Arrêter et supprimer le conteneur existant s'il existe
if docker ps -a | grep -q "kibana"; then
    echo "🛑 Arrêt du conteneur Kibana existant..."
    docker stop kibana 2>/dev/null || true
    docker rm kibana 2>/dev/null || true
fi

echo "🚀 Démarrage du conteneur Kibana..."

# Démarrer Kibana
docker run -d \
    --name kibana \
    --network $ELASTICSEARCH_NETWORK \
    -p ${KIBANA_PORT}:5601 \
    -e "ELASTICSEARCH_HOSTS=http://elasticsearch:9200" \
    -e "SERVER_NAME=kibana" \
    -e "SERVER_HOST=0.0.0.0" \
    docker.elastic.co/kibana/kibana:${KIBANA_VERSION}

echo "⏳ Attente du démarrage de Kibana (60 secondes)..."
echo "   (Kibana prend plus de temps à démarrer qu'Elasticsearch)"
sleep 60

# Vérifier que Kibana est démarré
MAX_RETRIES=15
RETRY_COUNT=0

while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    if curl -s http://localhost:${KIBANA_PORT}/api/status > /dev/null 2>&1; then
        echo ""
        echo "✅ Kibana est démarré et accessible !"
        echo ""
        echo "🌐 Interface Web: http://localhost:${KIBANA_PORT}"
        echo ""
        echo "📋 Fonctionnalités disponibles :"
        echo "   - Dev Tools : Requêtes Elasticsearch interactives"
        echo "   - Discover : Exploration des données indexées"
        echo "   - Dashboard : Création de tableaux de bord"
        echo "   - Index Management : Gestion des indexes"
        exit 0
    fi
    RETRY_COUNT=$((RETRY_COUNT + 1))
    echo "⏳ Tentative $RETRY_COUNT/$MAX_RETRIES..."
    sleep 5
done

echo "❌ Erreur: Kibana n'a pas démarré correctement"
echo "📋 Logs du conteneur:"
docker logs kibana --tail 50
exit 1

