#!/bin/bash

# Script de vérification du statut de tous les services
# Usage: ./scripts/status-all-services.sh

VPS_USER="root"
VPS_HOST="148.230.112.148"

# Services à vérifier
SERVICES=(
    "realestate-gateway:8080"
    "realestate-identity-service:8081"
    "realestate-organization-service:8082"
    "realestate-property-service:8083"
    "realestate-resource-service:8084"
    "realestate-document-service:8085"
    "realestate-workflow-service:8086"
    "realestate-notification-service:8087"
    "realestate-emailing-service:8088"
    "realestate-audit-service:8089"
    "realestate-billing-service:8090"
)

echo "📊 Statut de tous les services..."
echo ""

for service_info in "${SERVICES[@]}"; do
    IFS=':' read -r service_name port <<< "$service_info"
    
    # Vérifier le statut systemd
    status=$(ssh $VPS_USER@$VPS_HOST "systemctl is-active $service_name" 2>/dev/null || echo "inactive")
    
    # Vérifier si le port est ouvert
    port_check=$(ssh $VPS_USER@$VPS_HOST "netstat -tuln | grep :$port" 2>/dev/null && echo "open" || echo "closed")
    
    # Afficher le statut
    if [ "$status" = "active" ] && [ "$port_check" = "open" ]; then
        echo "  ✅ $service_name (port $port): ACTIF"
    elif [ "$status" = "active" ]; then
        echo "  ⚠️  $service_name (port $port): ACTIF mais port fermé"
    else
        echo "  ❌ $service_name (port $port): INACTIF"
    fi
done

echo ""
echo "📋 Détails systemd:"
ssh $VPS_USER@$VPS_HOST "systemctl status realestate-* --no-pager | grep -E '(●|Active|Main PID|Tasks|Memory)' | head -44"

