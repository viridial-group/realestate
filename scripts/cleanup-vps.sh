#!/bin/bash

# ========================
# Script de Nettoyage VPS Ubuntu
# ========================
# Nettoie les fichiers temporaires, logs anciens, caches, etc.
# Usage: ./scripts/cleanup-vps.sh [--dry-run] [--aggressive]

set -e

# Couleurs pour les messages
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Options
DRY_RUN=false
AGGRESSIVE=false

# Parse arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        --dry-run)
            DRY_RUN=true
            shift
            ;;
        --aggressive)
            AGGRESSIVE=true
            shift
            ;;
        *)
            echo -e "${RED}Option inconnue: $1${NC}"
            echo "Usage: $0 [--dry-run] [--aggressive]"
            exit 1
            ;;
    esac
done

if [ "$DRY_RUN" = true ]; then
    echo -e "${YELLOW}⚠️  MODE DRY-RUN: Aucun fichier ne sera supprimé${NC}"
    echo ""
fi

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}Nettoyage du VPS Ubuntu${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""

# Fonction pour calculer l'espace libéré
calculate_size() {
    local path=$1
    if [ -d "$path" ] || [ -f "$path" ]; then
        du -sh "$path" 2>/dev/null | cut -f1
    else
        echo "0"
    fi
}

# Fonction pour supprimer avec vérification
safe_remove() {
    local path=$1
    local description=$2
    
    if [ ! -e "$path" ]; then
        echo -e "${YELLOW}⚠️  $description: introuvable${NC}"
        return 0
    fi
    
    local size=$(calculate_size "$path")
    echo -e "${BLUE}🗑️  $description: ${size}${NC}"
    
    if [ "$DRY_RUN" = false ]; then
        if [ -d "$path" ]; then
            rm -rf "$path" 2>/dev/null && echo -e "${GREEN}   ✅ Supprimé${NC}" || echo -e "${RED}   ❌ Erreur lors de la suppression${NC}"
        else
            rm -f "$path" 2>/dev/null && echo -e "${GREEN}   ✅ Supprimé${NC}" || echo -e "${RED}   ❌ Erreur lors de la suppression${NC}"
        fi
    else
        echo -e "${YELLOW}   [DRY-RUN] Serait supprimé${NC}"
    fi
}

# ========================
# 1. Nettoyage des packages
# ========================
echo -e "${GREEN}--- Nettoyage des packages ---${NC}"

if [ "$DRY_RUN" = false ]; then
    echo -e "${BLUE}🗑️  Nettoyage du cache apt...${NC}"
    apt-get clean -y > /dev/null 2>&1 && echo -e "${GREEN}   ✅ Cache apt nettoyé${NC}" || echo -e "${YELLOW}   ⚠️  Erreur (peut nécessiter sudo)${NC}"
    
    echo -e "${BLUE}🗑️  Suppression des packages inutilisés...${NC}"
    apt-get autoremove -y > /dev/null 2>&1 && echo -e "${GREEN}   ✅ Packages inutilisés supprimés${NC}" || echo -e "${YELLOW}   ⚠️  Erreur (peut nécessiter sudo)${NC}"
    
    echo -e "${BLUE}🗑️  Suppression des packages obsolètes...${NC}"
    apt-get autoclean -y > /dev/null 2>&1 && echo -e "${GREEN}   ✅ Packages obsolètes supprimés${NC}" || echo -e "${YELLOW}   ⚠️  Erreur (peut nécessiter sudo)${NC}"
else
    echo -e "${YELLOW}   [DRY-RUN] Cache apt, packages inutilisés et obsolètes seraient nettoyés${NC}"
fi

echo ""

# ========================
# 2. Nettoyage des logs système
# ========================
echo -e "${GREEN}--- Nettoyage des logs système ---${NC}"

# Logs journald (plus de 7 jours)
if [ "$DRY_RUN" = false ]; then
    if command -v journalctl &> /dev/null; then
        echo -e "${BLUE}🗑️  Nettoyage des logs journald (plus de 7 jours)...${NC}"
        journalctl --vacuum-time=7d > /dev/null 2>&1 && echo -e "${GREEN}   ✅ Logs journald nettoyés${NC}" || echo -e "${YELLOW}   ⚠️  Erreur (peut nécessiter sudo)${NC}"
    fi
else
    echo -e "${YELLOW}   [DRY-RUN] Logs journald (plus de 7 jours) seraient nettoyés${NC}"
fi

# Logs dans /var/log
LOG_DIRS=(
    "/var/log/nginx"
    "/var/log/apache2"
    "/var/log/mysql"
    "/var/log/postgresql"
)

for log_dir in "${LOG_DIRS[@]}"; do
    if [ -d "$log_dir" ]; then
        # Supprimer les logs compressés de plus de 30 jours
        find "$log_dir" -name "*.gz" -type f -mtime +30 -exec rm -f {} \; 2>/dev/null || true
        # Supprimer les logs de plus de 7 jours (si agressif)
        if [ "$AGGRESSIVE" = true ]; then
            find "$log_dir" -name "*.log" -type f -mtime +7 -exec rm -f {} \; 2>/dev/null || true
        fi
    fi
done

echo ""

# ========================
# 3. Nettoyage des fichiers temporaires
# ========================
echo -e "${GREEN}--- Nettoyage des fichiers temporaires ---${NC}"

TEMP_DIRS=(
    "/tmp"
    "/var/tmp"
    "$HOME/.cache"
    "/var/cache"
)

for temp_dir in "${TEMP_DIRS[@]}"; do
    if [ -d "$temp_dir" ]; then
        # Supprimer les fichiers de plus de 7 jours
        find "$temp_dir" -type f -atime +7 -delete 2>/dev/null || true
        # Supprimer les répertoires vides
        find "$temp_dir" -type d -empty -delete 2>/dev/null || true
    fi
done

echo -e "${GREEN}✅ Fichiers temporaires nettoyés${NC}"
echo ""

# ========================
# 4. Nettoyage des logs de l'application
# ========================
echo -e "${GREEN}--- Nettoyage des logs de l'application ---${NC}"

APP_LOG_DIRS=(
    "/opt/source/realestate/logs"
    "$HOME/logs"
    "/var/log/realestate"
)

for log_dir in "${APP_LOG_DIRS[@]}"; do
    if [ -d "$log_dir" ]; then
        # Supprimer les logs de plus de 30 jours
        find "$log_dir" -name "*.log" -type f -mtime +30 -delete 2>/dev/null || true
        # Supprimer les fichiers .pid orphelins
        find "$log_dir" -name "*.pid" -type f ! -exec pgrep -F {} \; -delete 2>/dev/null || true
        echo -e "${GREEN}✅ Logs nettoyés dans $log_dir${NC}"
    fi
done

echo ""

# ========================
# 5. Nettoyage Docker (si installé)
# ========================
echo -e "${GREEN}--- Nettoyage Docker ---${NC}"

if command -v docker &> /dev/null; then
    if [ "$DRY_RUN" = false ]; then
        echo -e "${BLUE}🗑️  Nettoyage des conteneurs arrêtés...${NC}"
        docker container prune -f > /dev/null 2>&1 && echo -e "${GREEN}   ✅ Conteneurs arrêtés supprimés${NC}" || echo -e "${YELLOW}   ⚠️  Erreur${NC}"
        
        echo -e "${BLUE}🗑️  Nettoyage des images non utilisées...${NC}"
        docker image prune -f > /dev/null 2>&1 && echo -e "${GREEN}   ✅ Images non utilisées supprimées${NC}" || echo -e "${YELLOW}   ⚠️  Erreur${NC}"
        
        if [ "$AGGRESSIVE" = true ]; then
            echo -e "${BLUE}🗑️  Nettoyage agressif Docker (volumes, réseaux, etc.)...${NC}"
            docker system prune -af --volumes > /dev/null 2>&1 && echo -e "${GREEN}   ✅ Système Docker nettoyé${NC}" || echo -e "${YELLOW}   ⚠️  Erreur${NC}"
        else
            echo -e "${BLUE}🗑️  Nettoyage du système Docker...${NC}"
            docker system prune -f > /dev/null 2>&1 && echo -e "${GREEN}   ✅ Système Docker nettoyé${NC}" || echo -e "${YELLOW}   ⚠️  Erreur${NC}"
        fi
    else
        echo -e "${YELLOW}   [DRY-RUN] Conteneurs, images et système Docker seraient nettoyés${NC}"
    fi
else
    echo -e "${YELLOW}⚠️  Docker n'est pas installé${NC}"
fi

echo ""

# ========================
# 6. Nettoyage des snapshots (si ZFS/BTRFS)
# ========================
echo -e "${GREEN}--- Nettoyage des snapshots ---${NC}"

if command -v zfs &> /dev/null; then
    if [ "$DRY_RUN" = false ]; then
        echo -e "${BLUE}🗑️  Nettoyage des snapshots ZFS anciens...${NC}"
        zfs list -t snapshot -H -o name | while read snapshot; do
            zfs destroy "$snapshot" 2>/dev/null || true
        done
        echo -e "${GREEN}   ✅ Snapshots ZFS nettoyés${NC}"
    else
        echo -e "${YELLOW}   [DRY-RUN] Snapshots ZFS seraient nettoyés${NC}"
    fi
else
    echo -e "${YELLOW}⚠️  ZFS n'est pas disponible${NC}"
fi

echo ""

# ========================
# 7. Nettoyage des caches utilisateur
# ========================
echo -e "${GREEN}--- Nettoyage des caches utilisateur ---${NC}"

USER_CACHE_DIRS=(
    "$HOME/.cache"
    "$HOME/.npm"
    "$HOME/.m2/repository"
    "$HOME/.gradle/caches"
)

for cache_dir in "${USER_CACHE_DIRS[@]}"; do
    if [ -d "$cache_dir" ]; then
        size_before=$(calculate_size "$cache_dir")
        if [ "$DRY_RUN" = false ]; then
            # Nettoyer les caches npm
            if [[ "$cache_dir" == *".npm"* ]]; then
                npm cache clean --force > /dev/null 2>&1 || true
            fi
            # Nettoyer les caches Maven (garder seulement les dernières versions)
            if [[ "$cache_dir" == *".m2"* ]]; then
                find "$cache_dir" -type d -name "*-SNAPSHOT" -mtime +30 -exec rm -rf {} \; 2>/dev/null || true
            fi
        fi
        echo -e "${GREEN}✅ Cache nettoyé: $cache_dir${NC}"
    fi
done

echo ""

# ========================
# 8. Nettoyage des fichiers core dumps
# ========================
echo -e "${GREEN}--- Nettoyage des core dumps ---${NC}"

if [ "$DRY_RUN" = false ]; then
    find / -name "core.*" -type f -mtime +7 -delete 2>/dev/null || true
    find /var/crash -name "*.crash" -type f -mtime +7 -delete 2>/dev/null || true
    echo -e "${GREEN}✅ Core dumps nettoyés${NC}"
else
    echo -e "${YELLOW}   [DRY-RUN] Core dumps seraient nettoyés${NC}"
fi

echo ""

# ========================
# 9. Nettoyage des fichiers de swap
# ========================
if [ "$AGGRESSIVE" = true ]; then
    echo -e "${GREEN}--- Nettoyage des fichiers de swap ---${NC}"
    echo -e "${YELLOW}⚠️  Mode agressif: Les fichiers de swap ne seront pas nettoyés (nécessite un redémarrage)${NC}"
    echo ""
fi

# ========================
# 10. Statistiques d'espace disque
# ========================
echo -e "${GREEN}--- Statistiques d'espace disque ---${NC}"

df -h / | tail -1 | awk '{print "Espace utilisé: " $3 " / " $2 " (" $5 " utilisé)"}'
df -h / | tail -1 | awk '{print "Espace disponible: " $4}'

echo ""

# ========================
# Résumé
# ========================
echo -e "${GREEN}========================================${NC}"
if [ "$DRY_RUN" = true ]; then
    echo -e "${YELLOW}✅ Nettoyage simulé terminé (DRY-RUN)${NC}"
    echo -e "${YELLOW}💡 Exécutez sans --dry-run pour effectuer le nettoyage réel${NC}"
else
    echo -e "${GREEN}✅ Nettoyage terminé${NC}"
fi
echo -e "${GREEN}========================================${NC}"
echo ""
echo -e "${BLUE}💡 Astuces:${NC}"
echo -e "   - Utilisez --dry-run pour voir ce qui serait supprimé"
echo -e "   - Utilisez --aggressive pour un nettoyage plus approfondi"
echo -e "   - Exécutez ce script régulièrement (cron hebdomadaire recommandé)"
echo ""

