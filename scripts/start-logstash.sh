#!/bin/bash

# ========================
# Script de Démarrage de Logstash
# ========================

set -e

LOGSTASH_VERSION="8.15.0"
LOGSTASH_PORT=5000
ELASTICSEARCH_NETWORK="realestate-network"
CONFIG_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)/config/logstash"
DATA_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)/data/logstash"

echo "🚀 Démarrage de Logstash..."
echo ""

# Créer les répertoires nécessaires
mkdir -p "$DATA_DIR"

# Vérifier que Elasticsearch est en cours d'exécution
if ! docker ps | grep -q "elasticsearch"; then
    echo "❌ Erreur: Elasticsearch n'est pas en cours d'exécution"
    echo "💡 Démarrez d'abord Elasticsearch: ./scripts/start-elasticsearch.sh"
    exit 1
fi

# Créer le réseau Docker s'il n'existe pas
if ! docker network ls | grep -q "$ELASTICSEARCH_NETWORK"; then
    echo "📡 Création du réseau Docker: $ELASTICSEARCH_NETWORK"
    docker network create $ELASTICSEARCH_NETWORK
fi

# Vérifier si Logstash est déjà en cours d'exécution
if docker ps --format '{{.Names}}' | grep -q "^logstash$"; then
    echo "⚠️  Logstash est déjà en cours d'exécution"
    echo "   Pour le redémarrer: docker stop logstash && docker rm logstash"
    exit 0
fi

# Vérifier si le port est déjà utilisé
if lsof -Pi :$LOGSTASH_PORT -sTCP:LISTEN -t >/dev/null 2>&1 ; then
    echo "⚠️  Le port $LOGSTASH_PORT est déjà utilisé"
    echo "   Arrêtez le processus utilisant ce port ou modifiez LOGSTASH_PORT"
    exit 1
fi

# Vérifier que le fichier de configuration existe
if [ ! -f "$CONFIG_DIR/logstash.conf" ]; then
    echo "❌ Fichier de configuration introuvable: $CONFIG_DIR/logstash.conf"
    exit 1
fi

# Démarrer Logstash avec Docker
echo "📦 Démarrage de Logstash (version $LOGSTASH_VERSION)..."
docker run -d \
    --name logstash \
    --network $ELASTICSEARCH_NETWORK \
    -p $LOGSTASH_PORT:$LOGSTASH_PORT \
    -v "$CONFIG_DIR/logstash.conf:/usr/share/logstash/pipeline/logstash.conf" \
    -e "LS_JAVA_OPTS=-Xmx512m -Xms512m" \
    docker.elastic.co/logstash/logstash:$LOGSTASH_VERSION

# Attendre que Logstash démarre
echo "⏳ Attente du démarrage de Logstash (30 secondes)..."
sleep 30

# Vérifier que Logstash répond
MAX_RETRIES=10
RETRY_COUNT=0

while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    if docker logs logstash 2>&1 | grep -q "Successfully started Logstash API endpoint"; then
        echo ""
        echo "✅ Logstash démarré avec succès !"
        echo ""
        echo "📊 Configuration:"
        echo "   - Port TCP: $LOGSTASH_PORT"
        echo "   - Elasticsearch: elasticsearch:9200"
        echo "   - Config: $CONFIG_DIR/logstash.conf"
        echo ""
        echo "💡 Pour activer l'envoi de logs depuis les services Spring Boot:"
        echo "   - Définir LOGSTASH_ENABLED=true"
        echo "   - Définir LOGSTASH_HOST=localhost"
        echo "   - Définir LOGSTASH_PORT=$LOGSTASH_PORT"
        exit 0
    fi
    RETRY_COUNT=$((RETRY_COUNT + 1))
    echo "⏳ Tentative $RETRY_COUNT/$MAX_RETRIES..."
    sleep 5
done

echo "❌ Logstash n'a pas démarré correctement"
echo "📋 Logs du conteneur:"
docker logs logstash --tail 50
exit 1

