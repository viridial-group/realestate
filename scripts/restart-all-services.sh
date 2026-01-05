#!/bin/bash

# ========================
# Script de Redémarrage Rapide de Tous les Services
# ========================
# Usage: sudo ./scripts/restart-all-services.sh

set -e

# Couleurs
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Services à redémarrer
SERVICES=(
    "realestate-gateway"
    "realestate-identity-service"
    "realestate-property-service"
    "realestate-resource-service"
    "realestate-document-service"
    "realestate-workflow-service"
    "realestate-notification-service"
    "realestate-emailing-service"
    "realestate-audit-service"
    "realestate-billing-service"
)

log_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

log_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

log_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

log_error() {
    echo -e "${RED}❌ $1${NC}"
}

# Vérifier qu'on est root
if [ "$EUID" -ne 0 ]; then
    log_error "Ce script doit être exécuté en tant que root"
    echo "Utilisez: sudo $0"
    exit 1
fi

echo ""
echo "=========================================="
echo "🔄 Redémarrage de Tous les Services"
echo "=========================================="
echo ""

# Redémarrer tous les services
declare -A results

for service in "${SERVICES[@]}"; do
    log_info "Redémarrage de $service..."
    
    if systemctl restart "$service" 2>/dev/null; then
        sleep 2
        if systemctl is-active --quiet "$service"; then
            log_success "$service redémarré"
            results[$service]="SUCCESS"
        else
            log_warning "$service redémarré mais inactif"
            results[$service]="WARNING"
        fi
    else
        log_warning "$service non trouvé ou erreur"
        results[$service]="NOT_FOUND"
    fi
    echo ""
done

# Attendre un peu
log_info "⏳ Attente de 5 secondes..."
sleep 5

# Vérifier le statut final
echo ""
echo "=========================================="
echo "📊 Statut Final"
echo "=========================================="
echo ""

printf "%-35s %-15s\n" "SERVICE" "STATUT"
echo "--------------------------------------------------------"

success_count=0
warning_count=0
failed_count=0

for service in "${SERVICES[@]}"; do
    if systemctl is-active --quiet "$service" 2>/dev/null; then
        status="${GREEN}RUNNING${NC}"
        ((success_count++))
    elif systemctl is-enabled --quiet "$service" 2>/dev/null; then
        status="${YELLOW}STOPPED${NC}"
        ((warning_count++))
    else
        status="${RED}NOT_INSTALLED${NC}"
        ((failed_count++))
    fi
    
    printf "%-35s %-15s\n" "$service" "$status"
done

echo ""
echo "=========================================="
echo "📊 Résumé"
echo "=========================================="
echo "Services en cours d'exécution: $success_count/${#SERVICES[@]}"
echo "Services arrêtés: $warning_count/${#SERVICES[@]}"
echo "Services non installés: $failed_count/${#SERVICES[@]}"
echo ""

if [ $success_count -eq ${#SERVICES[@]} ]; then
    log_success "Tous les services sont en cours d'exécution !"
    exit 0
elif [ $success_count -gt 0 ]; then
    log_warning "Certains services ne sont pas en cours d'exécution"
    exit 1
else
    log_error "Aucun service n'est en cours d'exécution"
    exit 1
fi

