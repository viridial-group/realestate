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
        
        # Gérer Zookeeper (supprimer si arrêté, redémarrer si nécessaire)
        if docker ps | grep -q "realestate-zookeeper"; then
            echo "✅ Zookeeper est déjà démarré"
        else
            # Supprimer le conteneur arrêté s'il existe
            if docker ps -a | grep -q "realestate-zookeeper"; then
                echo "🛑 Suppression du conteneur Zookeeper arrêté..."
                docker rm realestate-zookeeper 2>/dev/null || true
            fi
            
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
        
        # Gérer Kafka (supprimer si arrêté, redémarrer si nécessaire)
        if docker ps -a | grep -q "realestate-kafka"; then
            if ! docker ps | grep -q "realestate-kafka"; then
                echo "🛑 Suppression du conteneur Kafka arrêté..."
                docker rm realestate-kafka 2>/dev/null || true
            fi
        fi
        
        # Vérifier que Zookeeper est prêt
        echo "⏳ Attente que Zookeeper soit prêt..."
        for i in {1..30}; do
            if docker exec realestate-zookeeper nc -z localhost 2181 2>/dev/null; then
                echo "✅ Zookeeper est prêt"
                break
            fi
            if [ $i -eq 30 ]; then
                echo "❌ Zookeeper n'est pas prêt après 30 secondes"
                docker logs realestate-zookeeper 2>/dev/null | tail -20
                exit 1
            fi
            sleep 1
        done
        
        # Démarrer Kafka
        echo "🔄 Démarrage de Kafka..."
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
            -e KAFKA_AUTO_CREATE_TOPICS_ENABLE=true \
            confluentinc/cp-kafka:7.5.0
        
        # Attendre que Kafka démarre
        echo "⏳ Attente du démarrage de Kafka (20 secondes)..."
        sleep 20
        
        # Vérifier que Kafka est démarré
        if docker ps | grep -q "realestate-kafka"; then
            # Vérifier que Kafka répond
            echo "🔍 Vérification de la santé de Kafka..."
            for i in {1..30}; do
                if docker exec realestate-kafka kafka-broker-api-versions --bootstrap-server localhost:9092 >/dev/null 2>&1; then
                    echo "✅ Kafka démarré avec succès (Docker)"
                    echo "📍 Bootstrap servers: localhost:9092"
                    break
                fi
                if [ $i -eq 30 ]; then
                    echo "⚠️  Kafka est démarré mais ne répond pas encore"
                    echo "   Les logs peuvent indiquer le problème:"
                    docker logs realestate-kafka 2>/dev/null | tail -30
                fi
                sleep 1
            done
        else
            echo "❌ Erreur lors du démarrage de Kafka"
            echo ""
            echo "📋 Logs du conteneur Kafka:"
            docker logs realestate-kafka 2>/dev/null | tail -50 || echo "   (logs non disponibles)"
            echo ""
            echo "📋 Statut des conteneurs:"
            docker ps -a | grep -E "realestate-kafka|realestate-zookeeper" || true
            echo ""
            echo "💡 Solutions possibles:"
            echo "   1. Vérifier que Zookeeper est bien démarré: docker ps | grep zookeeper"
            echo "   2. Vérifier les logs de Zookeeper: docker logs realestate-zookeeper"
            echo "   3. Nettoyer et redémarrer: ./scripts/stop-kafka.sh && ./scripts/start-kafka.sh"
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

