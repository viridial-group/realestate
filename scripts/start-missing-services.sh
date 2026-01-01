#!/bin/bash

# ========================
# Script de Démarrage des Services Manquants sur VPS
# ========================
# Démarre uniquement les services qui ne sont pas déjà démarrés

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
LOGS_DIR="/opt/source/realestate/logs"

echo "🚀 Démarrage des Services Manquants"
echo "📁 Répertoire du projet: $PROJECT_ROOT"
echo "📁 Répertoire des logs: $LOGS_DIR"
echo ""

# Créer le répertoire des logs
mkdir -p "$LOGS_DIR"

# Fonction pour démarrer un service s'il n'est pas déjà démarré
start_service_if_needed() {
    local service_name=$1
    local service_dir=$2
    local port=$3
    local jar_pattern=$4
    
    # Vérifier si le service est déjà démarré
    pid_file="$LOGS_DIR/${service_name}.pid"
    if [ -f "$pid_file" ]; then
        pid=$(cat "$pid_file" 2>/dev/null || echo "")
        if [ -n "$pid" ] && ps -p $pid > /dev/null 2>&1; then
            echo "✅ $service_name est déjà démarré (PID: $pid, Port: $port)"
            return 0
        fi
    fi
    
    # Vérifier si le port est déjà utilisé
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1 ; then
        echo "⚠️  Port $port déjà utilisé pour $service_name - Service peut-être déjà démarré"
        return 1
    fi
    
    # Chercher le JAR
    local jar_path=$(find "$PROJECT_ROOT/$service_dir/target" -name "$jar_pattern" -type f 2>/dev/null | head -1)
    
    if [ -z "$jar_path" ]; then
        echo "❌ JAR non trouvé pour $service_name - Compilez d'abord: mvn clean package -DskipTests=true"
        return 1
    fi
    
    echo "🚀 Démarrage de $service_name sur le port $port..."
    
    # Démarrer le service en arrière-plan avec logs
    cd "$PROJECT_ROOT"
    nohup java -jar "$jar_path" \
        > "$LOGS_DIR/${service_name}.log" 2>&1 &
    
    local pid=$!
    echo "   PID: $pid"
    echo "   Logs: $LOGS_DIR/${service_name}.log"
    
    # Attendre un peu pour vérifier que le service démarre
    sleep 3
    
    # Vérifier si le processus est toujours actif
    if ps -p $pid > /dev/null 2>&1; then
        echo "   ✅ $service_name démarré"
        echo "$pid" > "$LOGS_DIR/${service_name}.pid"
        return 0
    else
        echo "   ❌ $service_name n'a pas démarré - Vérifiez les logs"
        return 1
    fi
}

# Services dans l'ordre de dépendance
services=(
    "identity-service:services/identity-service:8081:identity-service-*.jar"
    "resource-service:services/resource-service:8084:resource-service-*.jar"
    "property-service:services/property-service:8083:property-service-*.jar"
    "document-service:services/document-service:8085:document-service-*.jar"
    "workflow-service:services/workflow-service:8086:workflow-service-*.jar"
    "notification-service:services/notification-service:8087:notification-service-*.jar"
    "emailing-service:services/emailing-service:8088:emailing-service-*.jar"
    "audit-service:services/audit-service:8089:audit-service-*.jar"
    "billing-service:services/billing-service:8090:billing-service-*.jar"
)

echo "📦 Vérification et démarrage des services..."
echo ""

started_count=0
skipped_count=0
failed_count=0

for service_config in "${services[@]}"; do
    IFS=':' read -r service_name service_dir port jar_pattern <<< "$service_config"
    if start_service_if_needed "$service_name" "$service_dir" "$port" "$jar_pattern"; then
        if [ -f "$LOGS_DIR/${service_name}.pid" ]; then
            started_count=$((started_count + 1))
        else
            skipped_count=$((skipped_count + 1))
        fi
    else
        failed_count=$((failed_count + 1))
    fi
    sleep 1
done

echo ""
echo "✅ Vérification terminée"
echo ""
echo "📊 Résumé :"
echo "   ✅ Services démarrés: $started_count"
echo "   ⏭️  Services déjà actifs: $skipped_count"
echo "   ❌ Services en échec: $failed_count"
echo ""
echo "📁 Logs disponibles dans: $LOGS_DIR"
echo ""
echo "💡 Pour vérifier l'état de tous les services :"
echo "   ./scripts/check-vps-services.sh"

