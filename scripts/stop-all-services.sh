#!/bin/bash

# Script d'arrêt de tous les services
# Usage: ./scripts/stop-all-services.sh

set -e

VPS_USER="root"
VPS_HOST="148.230.112.148"

# Services à arrêter
SERVICES=(
    "realestate-gateway"
    "realestate-identity-service"
    "realestate-organization-service"
    "realestate-property-service"
    "realestate-resource-service"
    "realestate-document-service"
    "realestate-workflow-service"
    "realestate-notification-service"
    "realestate-emailing-service"
    "realestate-audit-service"
    "realestate-billing-service"
)

echo "🛑 Arrêt de tous les services..."

for service in "${SERVICES[@]}"; do
    echo "  → Arrêt de $service..."
    ssh $VPS_USER@$VPS_HOST "systemctl stop $service" || echo "    ⚠️  Erreur lors de l'arrêt de $service"
    sleep 1
done

echo ""
echo "✅ Tous les services ont été arrêtés"

