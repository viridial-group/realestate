#!/bin/bash

# ========================
# Script de Visualisation des Logs
# ========================
# Usage: ./view-logs.sh [service] [lines]

set -e

SERVICE=${1:-realestate-gateway}
LINES=${2:-50}

echo "📋 Logs de $SERVICE (dernières $LINES lignes)"
echo "=========================================="
echo ""

if systemctl list-units --type=service | grep -q "$SERVICE"; then
    journalctl -u "$SERVICE" -n "$LINES" --no-pager
else
    echo "❌ Service $SERVICE non trouvé"
    echo ""
    echo "Services disponibles:"
    systemctl list-units --type=service | grep realestate || echo "Aucun service realestate trouvé"
fi

