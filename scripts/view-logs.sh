#!/bin/bash

# ========================
# Script de Visualisation des Logs
# ========================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
LOGS_DIR="$PROJECT_ROOT/logs"

if [ $# -eq 0 ]; then
    echo "📋 Visualisation des Logs"
    echo ""
    echo "Usage: $0 <service-name> [options]"
    echo ""
    echo "Services disponibles:"
    echo "  - gateway"
    echo "  - identity-service"
    echo "  - organization-service"
    echo "  - resource-service"
    echo "  - property-service"
    echo "  - document-service"
    echo "  - workflow-service"
    echo "  - notification-service"
    echo "  - emailing-service"
    echo "  - audit-service"
    echo "  - billing-service"
    echo ""
    echo "Options:"
    echo "  -f, --follow    Suivre les logs en temps réel (tail -f)"
    echo "  -n, --lines N   Afficher les N dernières lignes (défaut: 50)"
    echo "  -a, --all       Afficher tous les logs"
    echo ""
    echo "Exemples:"
    echo "  $0 property-service"
    echo "  $0 gateway -f"
    echo "  $0 identity-service -n 100"
    exit 1
fi

SERVICE_NAME=$1
LOG_FILE="$LOGS_DIR/${SERVICE_NAME}.log"
shift

if [ ! -f "$LOG_FILE" ]; then
    echo "❌ Fichier de log introuvable: $LOG_FILE"
    exit 1
fi

LINES=50
FOLLOW=false

while [[ $# -gt 0 ]]; do
    case $1 in
        -f|--follow)
            FOLLOW=true
            shift
            ;;
        -n|--lines)
            LINES=$2
            shift 2
            ;;
        -a|--all)
            LINES=999999
            shift
            ;;
        *)
            echo "❌ Option inconnue: $1"
            exit 1
            ;;
    esac
done

if [ "$FOLLOW" = true ]; then
    echo "📋 Logs de $SERVICE_NAME (suivi en temps réel)..."
    echo "📍 Fichier: $LOG_FILE"
    echo "🛑 Appuyez sur Ctrl+C pour arrêter"
    echo ""
    tail -f "$LOG_FILE"
else
    echo "📋 Dernières $LINES lignes de $SERVICE_NAME..."
    echo "📍 Fichier: $LOG_FILE"
    echo ""
    tail -n "$LINES" "$LOG_FILE"
fi
