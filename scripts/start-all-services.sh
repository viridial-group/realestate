#!/bin/bash

# Script de démarrage de tous les services
# Usage: ./scripts/start-all-services.sh

set -e

VPS_USER="root"
VPS_HOST="148.230.112.148"

# Services à démarrer
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

echo "🚀 Démarrage de tous les services..."

for service in "${SERVICES[@]}"; do
    echo "  → Démarrage de $service..."
    ssh $VPS_USER@$VPS_HOST "systemctl start $service" || echo "    ⚠️  Erreur lors du démarrage de $service"
    sleep 2
done

echo ""
echo "✅ Tous les services ont été démarrés"
echo ""
echo "📊 Vérification du statut..."
ssh $VPS_USER@$VPS_HOST "systemctl status realestate-* --no-pager | grep -E '(Active|Main PID)' | head -22"

