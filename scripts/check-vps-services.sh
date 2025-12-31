#!/bin/bash

# ========================
# Script de Vérification des Services sur VPS
# ========================
# Vérifie le statut de tous les services sur le VPS

set -e

LOGS_DIR="/opt/source/realestate/logs"

echo "🔍 Vérification des Services sur VPS"
echo "📁 Répertoire des logs: $LOGS_DIR"
echo ""

# ========================
# 1. Services Spring Boot (via PIDs)
# ========================
echo "1. 📦 Services Spring Boot:"
echo ""

if [ ! -d "$LOGS_DIR" ]; then
    echo "   ⚠️  Répertoire logs introuvable: $LOGS_DIR"
else
    services=(
        "gateway:8080"
        "identity-service:8081"
        "organization-service:8082"
        "property-service:8083"
        "resource-service:8084"
        "document-service:8085"
        "workflow-service:8086"
        "notification-service:8087"
        "emailing-service:8088"
        "audit-service:8089"
        "billing-service:8090"
    )
    
    for service_info in "${services[@]}"; do
        service_name=$(echo $service_info | cut -d: -f1)
        port=$(echo $service_info | cut -d: -f2)
        
        # Vérifier via PID
        pid_file="$LOGS_DIR/${service_name}.pid"
        if [ -f "$pid_file" ]; then
            pid=$(cat "$pid_file" 2>/dev/null || echo "")
            if [ -n "$pid" ] && ps -p $pid > /dev/null 2>&1; then
                echo -n "   ✅ $service_name (PID: $pid, Port: $port)"
                # Vérifier le health check
                health=$(curl -s http://localhost:$port/actuator/health 2>/dev/null | grep -o '"status":"[^"]*"' | cut -d'"' -f4 || echo "UNKNOWN")
                if [ "$health" = "UP" ]; then
                    echo " - Health: ✅ UP"
                else
                    echo " - Health: ⚠️  $health"
                fi
            else
                echo "   ❌ $service_name (PID invalide ou processus arrêté)"
            fi
        else
            # Vérifier si le port est utilisé
            if lsof -i :$port > /dev/null 2>&1 || netstat -tuln 2>/dev/null | grep -q ":$port "; then
                echo "   ⚠️  $service_name - Port $port utilisé mais pas de PID file"
            else
                echo "   ❌ $service_name - Non démarré"
            fi
        fi
    done
fi

echo ""

# ========================
# 2. Dépendances (PostgreSQL, Redis)
# ========================
echo "2. 🗄️  Dépendances:"
echo ""

# PostgreSQL
if command -v psql &> /dev/null; then
    # Essayer sans mot de passe (si .pgpass configuré)
    if PGPASSWORD="${SPRING_DATASOURCE_PASSWORD:-123456}" psql -h localhost -U postgres -d realestate_db -c "SELECT 1;" &> /dev/null 2>&1; then
        echo "   ✅ PostgreSQL - Accessible"
    else
        echo "   ⚠️  PostgreSQL - Vérification nécessite un mot de passe"
        echo "      💡 Configurez .pgpass ou SPRING_DATASOURCE_PASSWORD"
    fi
else
    echo "   ⚠️  PostgreSQL - psql non installé"
fi

# Redis
if command -v redis-cli &> /dev/null; then
    if redis-cli ping &> /dev/null; then
        echo "   ✅ Redis - Accessible"
    else
        echo "   ❌ Redis - Non accessible"
    fi
else
    echo "   ⚠️  Redis - redis-cli non installé"
fi

echo ""

# ========================
# 3. Docker Services (Kafka, Elasticsearch)
# ========================
echo "3. 🐳 Services Docker:"
echo ""

if command -v docker &> /dev/null; then
    # Kafka
    if docker ps | grep -q "kafka\|realestate-kafka"; then
        echo "   ✅ Kafka - En cours d'exécution"
    else
        echo "   ❌ Kafka - Non démarré"
    fi
    
    # Zookeeper
    if docker ps | grep -q "zookeeper\|realestate-zookeeper"; then
        echo "   ✅ Zookeeper - En cours d'exécution"
    else
        echo "   ❌ Zookeeper - Non démarré"
    fi
    
    # Elasticsearch
    if docker ps | grep -q "elasticsearch"; then
        echo "   ✅ Elasticsearch - En cours d'exécution"
    else
        echo "   ❌ Elasticsearch - Non démarré"
    fi
    
    # Kibana
    if docker ps | grep -q "kibana"; then
        echo "   ✅ Kibana - En cours d'exécution"
    else
        echo "   ❌ Kibana - Non démarré"
    fi
else
    echo "   ⚠️  Docker - Non installé"
fi

echo ""

# ========================
# 4. Health Checks HTTP
# ========================
echo "4. 🏥 Health Checks HTTP:"
echo ""

ports=(8080 8081 8082 8083 8084 8085 8086 8087 8088 8089 8090)
service_names=("gateway" "identity" "organization" "property" "resource" "document" "workflow" "notification" "emailing" "audit" "billing")

for i in "${!ports[@]}"; do
    port=${ports[$i]}
    name=${service_names[$i]}
    
    health_response=$(curl -s http://localhost:$port/actuator/health 2>/dev/null || echo "")
    health=$(echo "$health_response" | grep -o '"status":"[^"]*"' | cut -d'"' -f4 || echo "UNKNOWN")
    
    if [ "$health" = "UP" ]; then
        echo "   ✅ Port $port ($name) - UP"
    elif [ "$health" = "DOWN" ]; then
        echo "   ⚠️  Port $port ($name) - DOWN"
        # Afficher les détails si disponibles
        components=$(echo "$health_response" | grep -o '"components":{[^}]*}' | head -1 || echo "")
        if [ -n "$components" ]; then
            echo "      Détails: $components"
        fi
    elif [ "$health" = "UNKNOWN" ]; then
        if lsof -i :$port > /dev/null 2>&1 || netstat -tuln 2>/dev/null | grep -q ":$port "; then
            echo "   ⚠️  Port $port ($name) - Répond mais health check inaccessible"
        else
            echo "   ❌ Port $port ($name) - Non accessible"
        fi
    else
        echo "   ⚠️  Port $port ($name) - Status: $health"
    fi
done

echo ""
echo "✅ Vérification terminée"

