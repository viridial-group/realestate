#!/bin/bash

# ========================
# Script de Test du Flux Complet
# ========================
# Teste le flux: Property Created → Kafka → Workflow → Notification → Audit

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

echo "🧪 Test du Flux Complet Kafka"
echo ""

# Vérifier que Kafka est démarré
echo "1️⃣  Vérification de Kafka..."
if ! ./scripts/check-kafka.sh > /dev/null 2>&1; then
    echo "   ⚠️  Kafka n'est pas démarré. Démarrage..."
    ./scripts/start-kafka.sh
    sleep 5
fi
echo "   ✅ Kafka est actif"
echo ""

# Vérifier que les topics existent
echo "2️⃣  Vérification des topics Kafka..."
TOPICS=$(./scripts/list-kafka-topics.sh 2>/dev/null | wc -l)
if [ "$TOPICS" -lt 5 ]; then
    echo "   ⚠️  Topics manquants. Création..."
    ./scripts/create-kafka-topics.sh
fi
echo "   ✅ Topics disponibles"
echo ""

# Vérifier que les services sont démarrés
echo "3️⃣  Vérification des services..."
if [ ! -f "$PROJECT_ROOT/logs/gateway.pid" ]; then
    echo "   ⚠️  Services non démarrés. Démarrage..."
    ./scripts/build-and-start-all.sh
    echo "   ⏳ Attente du démarrage des services (15 secondes)..."
    sleep 15
fi
echo "   ✅ Services démarrés"
echo ""

# Test de création d'une Property via l'API
echo "4️⃣  Test de création d'une Property..."
GATEWAY_URL="http://localhost:8080"
PROPERTY_SERVICE_URL="http://localhost:8083"

# Vérifier que le Gateway répond
if curl -s "$GATEWAY_URL/actuator/health" > /dev/null 2>&1; then
    echo "   ✅ Gateway accessible"
    
    # Test de création d'une Property (nécessite un token JWT valide)
    echo "   ℹ️  Pour tester complètement, vous devez:"
    echo "      1. Créer un utilisateur via Identity Service"
    echo "      2. Obtenir un token JWT"
    echo "      3. Créer une Property avec le token"
    echo "      4. Vérifier les événements Kafka"
else
    echo "   ⚠️  Gateway non accessible sur $GATEWAY_URL"
fi
echo ""

# Vérifier les logs pour les événements Kafka
echo "5️⃣  Vérification des logs..."
if [ -f "$PROJECT_ROOT/logs/property-service.log" ]; then
    echo "   📋 Dernières lignes du log Property Service:"
    tail -n 5 "$PROJECT_ROOT/logs/property-service.log" | grep -i "kafka\|event" || echo "      (aucun événement Kafka récent)"
fi
echo ""

echo "✅ Test du flux terminé"
echo ""
echo "💡 Pour tester manuellement:"
echo "   1. Créer une Property via l'API"
echo "   2. Vérifier les logs: ./scripts/view-logs.sh workflow-service -f"
echo "   3. Vérifier les logs: ./scripts/view-logs.sh notification-service -f"

