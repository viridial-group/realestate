#!/bin/bash

# ========================
# Script d'Arrêt de Tous les Services
# ========================
# Arrête tous les services Spring Boot, Kafka, Elasticsearch et Kibana

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
LOGS_DIR="$PROJECT_ROOT/logs"

echo "🛑 Arrêt de tous les services..."
echo ""

# ========================
# 1. Arrêter les services Spring Boot
# ========================
echo "📦 Arrêt des services Spring Boot..."
echo ""

if [ ! -d "$LOGS_DIR" ]; then
    echo "ℹ️  Aucun service Spring Boot en cours d'exécution (répertoire logs introuvable)"
else
    # Arrêter tous les services via leurs PIDs
    for pid_file in "$LOGS_DIR"/*.pid; do
        if [ -f "$pid_file" ]; then
            service_name=$(basename "$pid_file" .pid)
            pid=$(cat "$pid_file")
            
            if ps -p $pid > /dev/null 2>&1; then
                echo "🛑 Arrêt de $service_name (PID: $pid)..."
                kill $pid 2>/dev/null || true
                
                # Attendre que le processus se termine
                for i in {1..10}; do
                    if ! ps -p $pid > /dev/null 2>&1; then
                        break
                    fi
                    sleep 1
                done
                
                # Force kill si nécessaire
                if ps -p $pid > /dev/null 2>&1; then
                    echo "   ⚠️  Force kill de $service_name..."
                    kill -9 $pid 2>/dev/null || true
                fi
                
                echo "   ✅ $service_name arrêté"
            else
                echo "ℹ️  $service_name n'est pas en cours d'exécution"
            fi
            
            rm -f "$pid_file"
        fi
    done
fi

echo ""

# ========================
# 2. Arrêter Kibana
# ========================
echo "🎨 Arrêt de Kibana..."
if docker ps | grep -q "kibana"; then
    docker stop kibana > /dev/null 2>&1 && echo "   ✅ Kibana arrêté" || echo "   ⚠️  Erreur lors de l'arrêt de Kibana"
else
    echo "   ℹ️  Kibana n'est pas en cours d'exécution"
fi

echo ""

# ========================
# 3. Arrêter Elasticsearch
# ========================
echo "🔍 Arrêt d'Elasticsearch..."
if docker ps | grep -q "elasticsearch"; then
    docker stop elasticsearch > /dev/null 2>&1 && echo "   ✅ Elasticsearch arrêté" || echo "   ⚠️  Erreur lors de l'arrêt d'Elasticsearch"
else
    echo "   ℹ️  Elasticsearch n'est pas en cours d'exécution"
fi

echo ""

# ========================
# 4. Arrêter Kafka
# ========================
echo "📨 Arrêt de Kafka..."

# Vérifier différents noms de conteneurs Kafka possibles
KAFKA_CONTAINER=""
if docker ps | grep -q "realestate-kafka"; then
    KAFKA_CONTAINER="realestate-kafka"
elif docker ps | grep -q "kafka"; then
    KAFKA_CONTAINER=$(docker ps | grep kafka | grep -v zookeeper | awk '{print $NF}' | head -1)
fi

if [ -n "$KAFKA_CONTAINER" ]; then
    docker stop "$KAFKA_CONTAINER" > /dev/null 2>&1 && echo "   ✅ Kafka arrêté" || echo "   ⚠️  Erreur lors de l'arrêt de Kafka"
else
    echo "   ℹ️  Kafka n'est pas en cours d'exécution"
fi

# Arrêter Zookeeper si présent
ZOOKEEPER_CONTAINER=""
if docker ps | grep -q "realestate-zookeeper"; then
    ZOOKEEPER_CONTAINER="realestate-zookeeper"
elif docker ps | grep -q "zookeeper"; then
    ZOOKEEPER_CONTAINER=$(docker ps | grep zookeeper | awk '{print $NF}' | head -1)
fi

if [ -n "$ZOOKEEPER_CONTAINER" ]; then
    echo "🦘 Arrêt de Zookeeper..."
    docker stop "$ZOOKEEPER_CONTAINER" > /dev/null 2>&1 && echo "   ✅ Zookeeper arrêté" || echo "   ⚠️  Erreur lors de l'arrêt de Zookeeper"
fi

echo ""

# ========================
# Résumé
# ========================
echo "✅ Tous les services ont été arrêtés"
echo ""
echo "📋 Services arrêtés :"
echo "   - Services Spring Boot (via PIDs)"
echo "   - Kibana (Docker)"
echo "   - Elasticsearch (Docker)"
echo "   - Kafka (Docker)"
echo "   - Zookeeper (Docker)"
echo ""
echo "💡 Pour redémarrer tous les services :"
echo "   ./scripts/build-and-start-all.sh"
