#!/bin/bash

# ========================
# Script de Démarrage de Zipkin
# ========================

set -e

ZIPKIN_VERSION="2.24.4"
ZIPKIN_PORT=9411

echo "🚀 Démarrage de Zipkin..."
echo ""

# Vérifier si Zipkin est déjà en cours d'exécution
if docker ps --format '{{.Names}}' | grep -q "^zipkin$"; then
    echo "⚠️  Zipkin est déjà en cours d'exécution"
    echo "   Pour le redémarrer: docker stop zipkin && docker rm zipkin"
    exit 0
fi

# Vérifier si le port est déjà utilisé
if lsof -Pi :$ZIPKIN_PORT -sTCP:LISTEN -t >/dev/null 2>&1 ; then
    echo "⚠️  Le port $ZIPKIN_PORT est déjà utilisé"
    echo "   Arrêtez le processus utilisant ce port ou modifiez ZIPKIN_PORT"
    exit 1
fi

# Démarrer Zipkin avec Docker
echo "📦 Démarrage de Zipkin (version $ZIPKIN_VERSION)..."
docker run -d \
    --name zipkin \
    -p $ZIPKIN_PORT:$ZIPKIN_PORT \
    openzipkin/zipkin:$ZIPKIN_VERSION

# Attendre que Zipkin démarre
echo "⏳ Attente du démarrage de Zipkin (10 secondes)..."
sleep 10

# Vérifier que Zipkin répond
MAX_RETRIES=10
RETRY_COUNT=0

while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    if curl -s http://localhost:$ZIPKIN_PORT > /dev/null 2>&1; then
        echo ""
        echo "✅ Zipkin démarré avec succès !"
        echo ""
        echo "📊 Accès:"
        echo "   - Interface Web: http://localhost:$ZIPKIN_PORT"
        echo "   - API: http://localhost:$ZIPKIN_PORT/api/v2/spans"
        echo ""
        echo "💡 Pour activer le tracing dans les services Spring Boot:"
        echo "   - Définir ZIPKIN_ENABLED=true"
        echo "   - Définir ZIPKIN_URL=http://localhost:$ZIPKIN_PORT/api/v2/spans"
        exit 0
    fi
    RETRY_COUNT=$((RETRY_COUNT + 1))
    echo "⏳ Tentative $RETRY_COUNT/$MAX_RETRIES..."
    sleep 3
done

echo "❌ Zipkin n'a pas démarré correctement"
echo "📋 Logs du conteneur:"
docker logs zipkin --tail 50
exit 1

