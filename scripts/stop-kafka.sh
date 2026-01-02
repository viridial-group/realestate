#!/bin/bash

# ========================
# Script d'Arrêt de Kafka
# ========================

set -e

echo "🛑 Arrêt de Kafka..."

if command -v docker &> /dev/null; then
    # Arrêter et supprimer Kafka
    if docker ps | grep -q "realestate-kafka"; then
        echo "🔄 Arrêt du conteneur Kafka..."
        docker stop realestate-kafka
        docker rm realestate-kafka
        echo "✅ Kafka arrêté (Docker)"
    elif docker ps -a | grep -q "realestate-kafka"; then
        echo "🔄 Suppression du conteneur Kafka arrêté..."
        docker rm realestate-kafka
        echo "✅ Conteneur Kafka supprimé"
    else
        echo "ℹ️  Kafka n'est pas en cours d'exécution (Docker)"
    fi
    
    # Arrêter et supprimer Zookeeper
    if docker ps | grep -q "realestate-zookeeper"; then
        echo "🔄 Arrêt du conteneur Zookeeper..."
        docker stop realestate-zookeeper
        docker rm realestate-zookeeper
        echo "✅ Zookeeper arrêté (Docker)"
    elif docker ps -a | grep -q "realestate-zookeeper"; then
        echo "🔄 Suppression du conteneur Zookeeper arrêté..."
        docker rm realestate-zookeeper
        echo "✅ Conteneur Zookeeper supprimé"
    fi
    
elif [ -f /opt/kafka/bin/kafka-server-stop.sh ]; then
    if pgrep -f kafka > /dev/null; then
        echo "🔄 Arrêt de Kafka..."
        /opt/kafka/bin/kafka-server-stop.sh 2>/dev/null || pkill -f kafka
        echo "✅ Kafka arrêté"
    else
        echo "ℹ️  Kafka n'est pas en cours d'exécution"
    fi
else
    echo "❌ Kafka n'est pas installé"
    exit 1
fi

