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
# Fonction pour arrêter un service d'infrastructure
# ========================
stop_infrastructure_service() {
    local service_name=$1
    local script_path="$SCRIPT_DIR/stop-${service_name}.sh"
    
    if [ -f "$script_path" ]; then
        echo "🛑 Arrêt de $service_name..."
        bash "$script_path" || {
            echo "   ⚠️  Erreur lors de l'arrêt de $service_name (continuons...)"
        }
    else
        # Arrêt manuel si le script n'existe pas
        if docker ps | grep -q "$service_name"; then
            docker stop "$service_name" > /dev/null 2>&1 && echo "   ✅ $service_name arrêté" || echo "   ⚠️  Erreur lors de l'arrêt de $service_name"
        else
            echo "   ℹ️  $service_name n'est pas en cours d'exécution"
        fi
    fi
}

# ========================
# 2. Arrêter les Services d'Infrastructure
# ========================
echo "🏗️  Arrêt des services d'infrastructure..."
echo ""

# Arrêter dans l'ordre inverse de démarrage (dépendances d'abord)
# 1. Kibana (avant Elasticsearch)
stop_infrastructure_service "kibana"

# 2. Elasticsearch
stop_infrastructure_service "elasticsearch"

# 3. Grafana
stop_infrastructure_service "grafana"

# 4. Prometheus
stop_infrastructure_service "prometheus"

# 5. Zipkin
stop_infrastructure_service "zipkin"

# 6. Kafka (et Zookeeper)
stop_infrastructure_service "kafka"

# 7. Redis
if docker ps | grep -q "redis"; then
    echo "🔴 Arrêt de Redis..."
    docker stop redis > /dev/null 2>&1 && echo "   ✅ Redis arrêté" || echo "   ⚠️  Erreur lors de l'arrêt de Redis"
elif pgrep -x redis-server > /dev/null; then
    echo "🔴 Arrêt de Redis..."
    pkill redis-server && echo "   ✅ Redis arrêté" || echo "   ⚠️  Erreur lors de l'arrêt de Redis"
else
    echo "🔴 Redis n'est pas en cours d'exécution"
fi

echo ""

# ========================
# Résumé
# ========================
echo "✅ Tous les services ont été arrêtés"
echo ""
echo "📋 Services arrêtés :"
echo "   - Services Spring Boot (via PIDs)"
echo "   - Services d'infrastructure :"
echo "     • Redis"
echo "     • Elasticsearch"
echo "     • Kibana"
echo "     • Kafka"
echo "     • Zookeeper"
echo "     • Prometheus"
echo "     • Grafana"
echo "     • Zipkin"
echo ""
echo "💡 Pour redémarrer tous les services :"
echo "   ./scripts/build-and-start-all.sh"
