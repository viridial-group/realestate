#!/bin/bash

# ========================
# Script de Démarrage Rapide
# ========================
# Démarre tout l'environnement (Kafka + Services)

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

echo "🚀 Démarrage Rapide de l'Environnement Complet"
echo ""

# 1. Démarrer Kafka
echo "📨 1/3 Démarrage de Kafka..."
if ! ./scripts/check-kafka.sh > /dev/null 2>&1; then
    ./scripts/start-kafka.sh
    sleep 5
    ./scripts/create-kafka-topics.sh
else
    echo "   ✅ Kafka déjà démarré"
fi
echo ""

# 2. Vérifier les topics
echo "📋 2/3 Vérification des topics..."
TOPICS_COUNT=$(./scripts/list-kafka-topics.sh 2>/dev/null | wc -l)
if [ "$TOPICS_COUNT" -lt 5 ]; then
    ./scripts/create-kafka-topics.sh
else
    echo "   ✅ Tous les topics sont créés"
fi
echo ""

# 3. Démarrer les services
echo "🔨 3/3 Build et démarrage des services..."
if [ ! -f "$PROJECT_ROOT/logs/gateway.pid" ]; then
    ./scripts/build-and-start-all.sh
else
    echo "   ✅ Services déjà démarrés"
    echo "   💡 Pour redémarrer: ./scripts/stop-all-services.sh puis ./scripts/build-and-start-all.sh"
fi
echo ""

echo "✅ Environnement prêt !"
echo ""
echo "📊 Statut:"
./scripts/status-all-services.sh 2>/dev/null || echo "   (Services en cours de démarrage...)"

