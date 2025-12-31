#!/bin/bash

# ========================
# Script de Démarrage Kafka
# ========================
# Démarre Kafka en local (Docker) ou sur le VPS

set -e

echo "🚀 Démarrage de Kafka..."

# Vérifier si Docker est disponible
if command -v docker &> /dev/null; then
    echo "📦 Utilisation de Docker pour démarrer Kafka..."
    
    # Vérifier si Kafka est déjà en cours d'exécution
    if docker ps | grep -q "realestate-kafka"; then
        echo "✅ Kafka est déjà démarré (Docker)"
        docker ps | grep "realestate-kafka"
    else
        echo "🔄 Démarrage de Kafka avec Docker..."
        
        # Créer le réseau si nécessaire
        docker network create realestate-network 2>/dev/null || true
        
        # Démarrer Zookeeper (requis pour Kafka < 3.0 ou avec Confluent)
        if ! docker ps | grep -q "realestate-zookeeper"; then
            echo "🔄 Démarrage de Zookeeper..."
            docker run -d \
                --name realestate-zookeeper \
                --network realestate-network \
                -p 2181:2181 \
                -e ZOOKEEPER_CLIENT_PORT=2181 \
                -e ZOOKEEPER_TICK_TIME=2000 \
                confluentinc/cp-zookeeper:7.5.0
            sleep 5
        fi
        
        # Démarrer Kafka
        docker run -d \
            --name realestate-kafka \
            --network realestate-network \
            -p 9092:9092 \
            -e KAFKA_BROKER_ID=1 \
            -e KAFKA_ZOOKEEPER_CONNECT=realestate-zookeeper:2181 \
            -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
            -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 \
            -e KAFKA_TRANSACTION_STATE_LOG_MIN_ISR=1 \
            -e KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR=1 \
            confluentinc/cp-kafka:7.5.0
        
        echo "⏳ Attente du démarrage de Kafka (10 secondes)..."
        sleep 10
        
        # Vérifier que Kafka est démarré
        if docker ps | grep -q "realestate-kafka"; then
            echo "✅ Kafka démarré avec succès (Docker)"
            echo "📍 Bootstrap servers: localhost:9092"
        else
            echo "❌ Erreur lors du démarrage de Kafka"
            docker logs realestate-kafka
            exit 1
        fi
    fi
    
elif [ -f /opt/kafka/bin/kafka-server-start.sh ]; then
    # VPS: Kafka installé localement
    echo "📦 Utilisation de Kafka installé sur le VPS..."
    
    if pgrep -f kafka > /dev/null; then
        echo "✅ Kafka est déjà démarré"
    else
        echo "🔄 Démarrage de Kafka..."
        /opt/kafka/bin/kafka-server-start.sh -daemon /opt/kafka/config/server.properties
        sleep 5
        echo "✅ Kafka démarré"
        echo "📍 Bootstrap servers: localhost:9092"
    fi
    
else
    echo "❌ Kafka n'est pas disponible"
    echo ""
    echo "Options pour installer Kafka:"
    echo "1. Installer Docker et utiliser: ./scripts/start-kafka.sh"
    echo "2. Installer Kafka manuellement sur le VPS: ./scripts/setup-vps.sh"
    echo ""
    echo "Pour installer Docker sur macOS:"
    echo "  brew install --cask docker"
    echo ""
    echo "Pour installer Docker sur Linux:"
    echo "  curl -fsSL https://get.docker.com -o get-docker.sh"
    echo "  sh get-docker.sh"
    exit 1
fi

echo ""
echo "📋 Prochaines étapes:"
echo "  1. Créer les topics: ./scripts/create-kafka-topics.sh"
echo "  2. Vérifier les topics: ./scripts/list-kafka-topics.sh"
echo "  3. Démarrer les services Spring Boot"

