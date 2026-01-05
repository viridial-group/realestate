#!/bin/bash

# Script pour arrêter toutes les APIs et les frontends
# Usage: ./scripts/stop-all.sh

# Ne pas arrêter le script en cas d'erreur pour permettre d'arrêter tous les services
set +e

# Couleurs pour les messages
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Répertoire racine du projet
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$PROJECT_ROOT"

# Répertoire des logs
LOGS_DIR="$PROJECT_ROOT/logs"

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}Arrêt de tous les services${NC}"
echo -e "${GREEN}========================================${NC}"

# Fonction pour arrêter un service
stop_service() {
    local service_name=$1
    local pid_file="$LOGS_DIR/${service_name}.pid"

    if [ ! -f "$pid_file" ]; then
        echo -e "${YELLOW}⚠️  $service_name n'est pas en cours d'exécution (fichier PID introuvable)${NC}"
        return 0
    fi

    local pid=$(cat "$pid_file")
    
    if ! ps -p $pid > /dev/null 2>&1; then
        echo -e "${YELLOW}⚠️  $service_name n'est pas en cours d'exécution (PID: $pid)${NC}"
        rm -f "$pid_file"
        return 0
    fi

    echo -e "${GREEN}🛑 Arrêt de $service_name (PID: $pid)...${NC}"
    
    # Essayer d'arrêter gracieusement
    kill $pid 2>/dev/null || true
    
    # Attendre jusqu'à 10 secondes pour l'arrêt gracieux
    local count=0
    while ps -p $pid > /dev/null 2>&1 && [ $count -lt 10 ]; do
        sleep 1
        count=$((count + 1))
    done
    
    # Si le processus est toujours actif, forcer l'arrêt
    if ps -p $pid > /dev/null 2>&1; then
        echo -e "${YELLOW}⚠️  Arrêt forcé de $service_name...${NC}"
        kill -9 $pid 2>/dev/null || true
        sleep 1
    fi
    
    if ! ps -p $pid > /dev/null 2>&1; then
        echo -e "${GREEN}✅ $service_name arrêté${NC}"
        rm -f "$pid_file"
    else
        echo -e "${RED}❌ Impossible d'arrêter $service_name${NC}"
        return 1
    fi
}

# Fonction pour arrêter un processus par port
stop_by_port() {
    local port=$1
    local service_name=$2
    
    local pid=$(lsof -ti:$port 2>/dev/null || true)
    
    if [ -z "$pid" ]; then
        echo -e "${YELLOW}⚠️  Aucun processus sur le port $port ($service_name)${NC}"
        return 0
    fi
    
    echo -e "${GREEN}🛑 Arrêt du processus sur le port $port ($service_name, PID: $pid)...${NC}"
    
    # Essayer d'arrêter gracieusement
    kill $pid 2>/dev/null || true
    
    # Attendre jusqu'à 10 secondes pour l'arrêt gracieux
    local count=0
    while lsof -ti:$port >/dev/null 2>&1 && [ $count -lt 10 ]; do
        sleep 1
        count=$((count + 1))
    done
    
    # Si le processus est toujours actif, forcer l'arrêt
    if lsof -ti:$port >/dev/null 2>&1; then
        echo -e "${YELLOW}⚠️  Arrêt forcé du processus sur le port $port...${NC}"
        kill -9 $pid 2>/dev/null || true
        sleep 1
    fi
    
    if ! lsof -ti:$port >/dev/null 2>&1; then
        echo -e "${GREEN}✅ Processus sur le port $port arrêté${NC}"
    else
        echo -e "${RED}❌ Impossible d'arrêter le processus sur le port $port${NC}"
        return 1
    fi
}

echo -e "\n${GREEN}--- Arrêt des Frontends ---${NC}"

# Arrêter les frontends
stop_service "frontend-public"
stop_service "frontend-admin"
stop_service "frontend-agent"

# Arrêter aussi par port au cas où
stop_by_port 3003 "frontend-public"
stop_by_port 3001 "frontend-admin"
stop_by_port 3002 "frontend-agent"

echo -e "\n${GREEN}--- Arrêt des Services Backend ---${NC}"

# Arrêter les services dans l'ordre inverse
stop_service "billing-service"
stop_service "audit-service"
stop_service "emailing-service"
stop_service "notification-service"
stop_service "workflow-service"
stop_service "document-service"
stop_service "resource-service"
stop_service "property-service"
stop_service "gateway"
stop_service "identity-service"

# Arrêter aussi par port au cas où
stop_by_port 8090 "billing-service"
stop_by_port 8089 "audit-service"
stop_by_port 8088 "emailing-service"
stop_by_port 8087 "notification-service"
stop_by_port 8086 "workflow-service"
stop_by_port 8085 "document-service"
stop_by_port 8084 "resource-service"
stop_by_port 8083 "property-service"
stop_by_port 8080 "gateway"
stop_by_port 8081 "identity-service"

echo -e "\n${GREEN}========================================${NC}"
echo -e "${GREEN}✅ Tous les services sont arrêtés${NC}"
echo -e "${GREEN}========================================${NC}"

