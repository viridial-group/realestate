#!/bin/bash

# ========================
# Script de Vérification Kafka
# ========================

set -e

BOOTSTRAP_SERVER=${KAFKA_BOOTSTRAP_SERVERS:-localhost:9092}

echo "🔍 Vérification de Kafka..."
echo "📍 Bootstrap server: $BOOTSTRAP_SERVER"
echo ""

# Vérifier si Kafka est en cours d'exécution
if command -v docker &> /dev/null; then
    if docker ps | grep -q "realestate-kafka"; then
        echo "✅ Kafka est en cours d'exécution (Docker)"
        docker ps | grep "realestate-kafka"
    else
        echo "❌ Kafka n'est pas en cours d'exécution (Docker)"
        exit 1
    fi
elif pgrep -f kafka > /dev/null; then
    echo "✅ Kafka est en cours d'exécution (VPS)"
else
    echo "❌ Kafka n'est pas en cours d'exécution"
    exit 1
fi

echo ""
echo "📋 Test de connexion..."

if command -v docker &> /dev/null && docker ps | grep -q "realestate-kafka"; then
    # Docker - Confluent image
    if docker exec realestate-kafka kafka-broker-api-versions --bootstrap-server localhost:9092 > /dev/null 2>&1; then
        echo "✅ Connexion réussie"
    else
        echo "❌ Échec de la connexion"
        exit 1
    fi
elif [ -f /opt/kafka/bin/kafka-broker-api-versions ]; then
    # VPS
    if /opt/kafka/bin/kafka-broker-api-versions --bootstrap-server "$BOOTSTRAP_SERVER" > /dev/null 2>&1; then
        echo "✅ Connexion réussie"
    else
        echo "❌ Échec de la connexion"
        exit 1
    fi
fi

echo ""
echo "📋 Topics disponibles:"
./scripts/list-kafka-topics.sh

