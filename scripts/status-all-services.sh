#!/bin/bash

# ========================
# Script de Statut de Tous les Services
# ========================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
LOGS_DIR="$PROJECT_ROOT/logs"

echo "📊 Statut des Services"
echo ""

if [ ! -d "$LOGS_DIR" ]; then
    echo "ℹ️  Aucun service démarré (répertoire logs introuvable)"
    exit 0
fi

services=(
    "gateway:8080"
    "identity-service:8081"
    "resource-service:8084"
    "property-service:8083"
    "document-service:8085"
    "workflow-service:8086"
    "notification-service:8087"
    "emailing-service:8088"
    "audit-service:8089"
    "billing-service:8090"
)

echo "Service Name              | Port  | Status    | PID"
echo "-------------------------|-------|-----------|------"

for service_config in "${services[@]}"; do
    IFS=':' read -r service_name port <<< "$service_config"
    pid_file="$LOGS_DIR/${service_name}.pid"
    
    if [ -f "$pid_file" ]; then
        pid=$(cat "$pid_file")
        if ps -p $pid > /dev/null 2>&1; then
            # Vérifier si le port est en écoute
            if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1; then
                status="✅ Running"
            else
                status="⚠️  No Port"
            fi
            printf "%-24s | %-5s | %-9s | %s\n" "$service_name" "$port" "$status" "$pid"
        else
            printf "%-24s | %-5s | ❌ Stopped | -\n" "$service_name" "$port"
            rm -f "$pid_file"
        fi
    else
        printf "%-24s | %-5s | ⚪ Not Run | -\n" "$service_name" "$port"
    fi
done

echo ""
echo "📁 Logs disponibles dans: $LOGS_DIR"
