#!/bin/bash

# ========================
# Script de Déploiement et Démarrage de Tous les Services
# ========================
# Ce script :
# 1. Compile tous les services
# 2. Copie les JARs dans /var/realestate/bin/
# 3. Redémarre tous les services systemd
# 4. Génère un rapport final

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
BIN_DIR="/var/realestate/bin"
CONFIG_DIR="/var/realestate/config"

# Couleurs
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Services à déployer
declare -A SERVICES=(
    ["gateway"]="gateway:gateway-*.jar:8080"
    ["identity-service"]="services/identity-service:identity-service-*.jar:8081"
    ["property-service"]="services/property-service:property-service-*.jar:8083"
    ["resource-service"]="services/resource-service:resource-service-*.jar:8084"
    ["document-service"]="services/document-service:document-service-*.jar:8085"
    ["workflow-service"]="services/workflow-service:workflow-service-*.jar:8086"
    ["notification-service"]="services/notification-service:notification-service-*.jar:8087"
    ["emailing-service"]="services/emailing-service:emailing-service-*.jar:8088"
    ["audit-service"]="services/audit-service:audit-service-*.jar:8089"
    ["billing-service"]="services/billing-service:billing-service-*.jar:8090"
)

# Fonction pour afficher un message
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

# Fonction pour vérifier si on est root
check_root() {
    if [ "$EUID" -ne 0 ]; then
        log_error "Ce script doit être exécuté en tant que root"
        echo "Utilisez: sudo $0"
        exit 1
    fi
}

# Fonction pour créer les répertoires nécessaires
create_directories() {
    log_info "Création des répertoires nécessaires..."
    
    mkdir -p "$BIN_DIR"
    mkdir -p "$CONFIG_DIR"
    mkdir -p /var/realestate/logs
    mkdir -p /var/realestate/storage
    
    log_success "Répertoires créés"
}

# Fonction pour compiler un service
compile_service() {
    local service_name=$1
    local service_path=$2
    
    log_info "Compilation de $service_name..."
    
    cd "$PROJECT_ROOT"
    
    if mvn clean package -DskipTests -pl "$service_path" -q; then
        log_success "$service_name compilé"
        return 0
    else
        log_error "Erreur lors de la compilation de $service_name"
        return 1
    fi
}

# Fonction pour copier un JAR
copy_jar() {
    local service_name=$1
    local service_path=$2
    local jar_pattern=$3
    
    log_info "Copie du JAR pour $service_name..."
    
    # Trouver le JAR
    local jar_file=$(find "$PROJECT_ROOT/$service_path/target" -name "$jar_pattern" -type f | head -1)
    
    if [ -z "$jar_file" ]; then
        log_error "JAR non trouvé pour $service_name (pattern: $jar_pattern)"
        return 1
    fi
    
    # Copier le JAR
    local target_jar="$BIN_DIR/${service_name}.jar"
    cp "$jar_file" "$target_jar"
    chmod 755 "$target_jar"
    
    log_success "JAR copié: $target_jar"
    return 0
}

# Fonction pour vérifier le statut d'un service
check_service_status() {
    local service_name=$1
    local port=$2
    
    local status="UNKNOWN"
    local health="UNKNOWN"
    
    # Vérifier systemd
    if systemctl is-active --quiet "realestate-${service_name}" 2>/dev/null; then
        status="RUNNING"
    elif systemctl is-enabled --quiet "realestate-${service_name}" 2>/dev/null; then
        status="STOPPED"
    else
        status="NOT_INSTALLED"
    fi
    
    # Vérifier le port (si le service est en cours d'exécution)
    if [ "$status" = "RUNNING" ]; then
        if curl -s -f -m 2 "http://localhost:${port}/actuator/health" > /dev/null 2>&1; then
            health="HEALTHY"
        else
            health="UNHEALTHY"
        fi
    fi
    
    echo "$status|$health"
}

# Fonction pour redémarrer un service
restart_service() {
    local service_name=$1
    
    log_info "Redémarrage de realestate-${service_name}..."
    
    if systemctl restart "realestate-${service_name}" 2>/dev/null; then
        sleep 2
        if systemctl is-active --quiet "realestate-${service_name}"; then
            log_success "realestate-${service_name} redémarré"
            return 0
        else
            log_warning "realestate-${service_name} redémarré mais inactif"
            return 1
        fi
    else
        log_warning "Impossible de redémarrer realestate-${service_name} (peut-être non installé)"
        return 1
    fi
}

# Fonction pour démarrer un service
start_service() {
    local service_name=$1
    
    log_info "Démarrage de realestate-${service_name}..."
    
    if systemctl start "realestate-${service_name}" 2>/dev/null; then
        sleep 3
        if systemctl is-active --quiet "realestate-${service_name}"; then
            log_success "realestate-${service_name} démarré"
            return 0
        else
            log_warning "realestate-${service_name} démarrage échoué"
            return 1
        fi
    else
        log_warning "Impossible de démarrer realestate-${service_name} (peut-être non installé)"
        return 1
    fi
}

# ========================
# MAIN
# ========================

echo ""
echo "=========================================="
echo "🚀 Déploiement et Démarrage des Services"
echo "=========================================="
echo ""

# Vérifier qu'on est root
check_root

# Créer les répertoires
create_directories

# Compiler le module common d'abord
log_info "Compilation du module common..."
cd "$PROJECT_ROOT"
if mvn clean install -DskipTests -pl common -q; then
    log_success "Module common compilé"
else
    log_error "Erreur lors de la compilation du module common"
    exit 1
fi

# Compiler et copier les JARs
log_info ""
log_info "📦 Compilation et déploiement des services..."
echo ""

declare -A compile_results
declare -A copy_results

for service_name in "${!SERVICES[@]}"; do
    IFS=':' read -r service_path jar_pattern port <<< "${SERVICES[$service_name]}"
    
    # Compiler
    if compile_service "$service_name" "$service_path"; then
        compile_results[$service_name]="SUCCESS"
        
        # Copier le JAR
        if copy_jar "$service_name" "$service_path" "$jar_pattern"; then
            copy_results[$service_name]="SUCCESS"
        else
            copy_results[$service_name]="FAILED"
        fi
    else
        compile_results[$service_name]="FAILED"
        copy_results[$service_name]="SKIPPED"
    fi
    
    echo ""
done

# Redémarrer tous les services
log_info ""
log_info "🔄 Redémarrage de tous les services systemd..."
echo ""

declare -A restart_results

for service_name in "${!SERVICES[@]}"; do
    if restart_service "$service_name"; then
        restart_results[$service_name]="SUCCESS"
    else
        restart_results[$service_name]="FAILED"
    fi
    echo ""
done

# Attendre un peu pour que les services démarrent
log_info "⏳ Attente de 5 secondes pour le démarrage des services..."
sleep 5

# Vérifier le statut final
log_info ""
log_info "📊 Vérification du statut final des services..."
echo ""

declare -A final_status
declare -A final_health

for service_name in "${!SERVICES[@]}"; do
    IFS=':' read -r service_path jar_pattern port <<< "${SERVICES[$service_name]}"
    IFS='|' read -r status health <<< "$(check_service_status "$service_name" "$port")"
    final_status[$service_name]=$status
    final_health[$service_name]=$health
done

# ========================
# RAPPORT FINAL
# ========================

echo ""
echo "=========================================="
echo "📋 RAPPORT FINAL"
echo "=========================================="
echo ""

printf "%-25s %-12s %-12s %-12s %-12s\n" "SERVICE" "COMPILATION" "DEPLOIEMENT" "STATUT" "SANTE"
echo "------------------------------------------------------------------------------------------------"

for service_name in "${!SERVICES[@]}"; do
    IFS=':' read -r service_path jar_pattern port <<< "${SERVICES[$service_name]}"
    
    compile_status="${compile_results[$service_name]:-UNKNOWN}"
    copy_status="${copy_results[$service_name]:-UNKNOWN}"
    status="${final_status[$service_name]:-UNKNOWN}"
    health="${final_health[$service_name]:-UNKNOWN}"
    
    # Formater avec couleurs
    if [ "$compile_status" = "SUCCESS" ]; then
        compile_display="${GREEN}✅${NC}"
    else
        compile_display="${RED}❌${NC}"
    fi
    
    if [ "$copy_status" = "SUCCESS" ]; then
        copy_display="${GREEN}✅${NC}"
    else
        copy_display="${RED}❌${NC}"
    fi
    
    if [ "$status" = "RUNNING" ]; then
        status_display="${GREEN}RUNNING${NC}"
    elif [ "$status" = "STOPPED" ]; then
        status_display="${YELLOW}STOPPED${NC}"
    elif [ "$status" = "NOT_INSTALLED" ]; then
        status_display="${RED}NOT_INSTALLED${NC}"
    else
        status_display="${RED}UNKNOWN${NC}"
    fi
    
    if [ "$health" = "HEALTHY" ]; then
        health_display="${GREEN}HEALTHY${NC}"
    elif [ "$health" = "UNHEALTHY" ]; then
        health_display="${YELLOW}UNHEALTHY${NC}"
    else
        health_display="${RED}N/A${NC}"
    fi
    
    printf "%-25s %-12s %-12s %-12s %-12s\n" "$service_name" "$compile_display" "$copy_display" "$status_display" "$health_display"
done

echo ""
echo "=========================================="
echo "📁 Emplacements"
echo "=========================================="
echo "JARs: $BIN_DIR"
echo "Configs: $CONFIG_DIR"
echo "Logs: /var/realestate/logs"
echo ""

# Statistiques
total_services=${#SERVICES[@]}
running_count=0
healthy_count=0

for service_name in "${!SERVICES[@]}"; do
    if [ "${final_status[$service_name]}" = "RUNNING" ]; then
        ((running_count++))
    fi
    if [ "${final_health[$service_name]}" = "HEALTHY" ]; then
        ((healthy_count++))
    fi
done

echo "=========================================="
echo "📊 Statistiques"
echo "=========================================="
echo "Total de services: $total_services"
echo "Services en cours d'exécution: $running_count/$total_services"
echo "Services sains: $healthy_count/$total_services"
echo ""

if [ $running_count -eq $total_services ] && [ $healthy_count -eq $total_services ]; then
    log_success "Tous les services sont opérationnels !"
    exit 0
elif [ $running_count -gt 0 ]; then
    log_warning "Certains services ne sont pas opérationnels"
    exit 1
else
    log_error "Aucun service n'est en cours d'exécution"
    exit 1
fi

