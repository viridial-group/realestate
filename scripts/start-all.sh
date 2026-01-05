#!/bin/bash

# Script pour démarrer toutes les APIs et les frontends
# Usage: ./scripts/start-all.sh

# Arrêter le script en cas d'erreur de compilation, mais continuer pour les démarrages
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
mkdir -p "$LOGS_DIR"

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}Démarrage de tous les services${NC}"
echo -e "${GREEN}========================================${NC}"

# Étape 0: Démarrage des services d'infrastructure
echo -e "\n${GREEN}--- Services d'Infrastructure ---${NC}"

# Fonction pour démarrer un service d'infrastructure
start_infrastructure() {
    local service_name=$1
    local script_path="$PROJECT_ROOT/scripts/start-${service_name}.sh"
    
    if [ -f "$script_path" ]; then
        echo -e "${GREEN}🚀 Démarrage de $service_name...${NC}"
        bash "$script_path" > /dev/null 2>&1
        if [ $? -eq 0 ]; then
            echo -e "${GREEN}✅ $service_name démarré${NC}"
        else
            echo -e "${YELLOW}⚠️  $service_name pourrait déjà être démarré ou nécessite une configuration${NC}"
        fi
    else
        echo -e "${YELLOW}⚠️  Script de démarrage pour $service_name introuvable${NC}"
    fi
}

# Démarrer les services d'infrastructure dans l'ordre
start_infrastructure "redis"
start_infrastructure "kafka"
start_infrastructure "elasticsearch"

# Vérifier PostgreSQL (généralement géré par systemd)
echo -e "${GREEN}🔍 Vérification de PostgreSQL...${NC}"
if command -v psql &> /dev/null; then
    if psql -h localhost -U postgres -d realestate_db -c "SELECT 1;" &> /dev/null 2>&1; then
        echo -e "${GREEN}✅ PostgreSQL est accessible${NC}"
    else
        echo -e "${YELLOW}⚠️  PostgreSQL n'est pas accessible. Vérifiez qu'il est démarré:${NC}"
        echo -e "${YELLOW}   macOS: brew services start postgresql${NC}"
        echo -e "${YELLOW}   Linux: sudo systemctl start postgresql${NC}"
    fi
else
    echo -e "${YELLOW}⚠️  psql n'est pas installé. PostgreSQL pourrait être sur un serveur distant.${NC}"
fi

# Étape 1: Compilation de tous les JARs
echo -e "\n${GREEN}--- Compilation des JARs ---${NC}"
echo -e "${YELLOW}⏳ Compilation en cours... (cela peut prendre plusieurs minutes)${NC}"

# Compiler le common d'abord (dépendance des autres services)
if [ -d "$PROJECT_ROOT/common" ]; then
    echo -e "${GREEN}📦 Compilation du module common...${NC}"
    cd "$PROJECT_ROOT/common"
    mvn clean install -DskipTests > /dev/null 2>&1
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ Common compilé avec succès${NC}"
    else
        echo -e "${RED}❌ Erreur lors de la compilation du common${NC}"
        exit 1
    fi
    cd "$PROJECT_ROOT"
fi

# Compiler le gateway
if [ -d "$PROJECT_ROOT/gateway" ]; then
    echo -e "${GREEN}📦 Compilation du gateway...${NC}"
    cd "$PROJECT_ROOT/gateway"
    mvn clean package -DskipTests > /dev/null 2>&1
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ Gateway compilé avec succès${NC}"
    else
        echo -e "${RED}❌ Erreur lors de la compilation du gateway${NC}"
        exit 1
    fi
    cd "$PROJECT_ROOT"
fi

# Compiler tous les services en parallèle
echo -e "${GREEN}📦 Compilation des services backend...${NC}"

SERVICES=(
    "identity-service"
    "property-service"
    "resource-service"
    "document-service"
    "workflow-service"
    "notification-service"
    "emailing-service"
    "audit-service"
    "billing-service"
)

# Compiler les services en parallèle
COMPILATION_ERRORS=0
PIDS=()

for service in "${SERVICES[@]}"; do
    (
        service_dir="$PROJECT_ROOT/services/$service"
        if [ -d "$service_dir" ]; then
            cd "$service_dir"
            if mvn clean package -DskipTests > /dev/null 2>&1; then
                echo -e "${GREEN}✅ $service compilé avec succès${NC}"
            else
                echo -e "${RED}❌ Erreur lors de la compilation de $service${NC}"
                echo -e "${YELLOW}💡 Exécutez 'cd $service_dir && mvn clean package' pour voir les détails${NC}"
                exit 1
            fi
        else
            echo -e "${YELLOW}⚠️  Répertoire $service_dir introuvable${NC}"
        fi
    ) &
    PIDS+=($!)
done

# Attendre que toutes les compilations se terminent et vérifier les erreurs
for pid in "${PIDS[@]}"; do
    if ! wait $pid; then
        COMPILATION_ERRORS=1
    fi
done

if [ $COMPILATION_ERRORS -ne 0 ]; then
    echo -e "\n${RED}❌ Des erreurs de compilation ont été détectées. Veuillez les corriger avant de continuer.${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Tous les JARs sont compilés${NC}"

# Fonction pour vérifier si un port est utilisé
check_port() {
    local port=$1
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1 ; then
        return 0  # Port utilisé
    else
        return 1  # Port libre
    fi
}

# Fonction pour démarrer un service Spring Boot
start_service() {
    local service_name=$1
    local service_dir=$2
    local port=$3
    local jar_file="$service_dir/target/${service_name}-1.0.1-SNAPSHOT.jar"
    local log_file="$LOGS_DIR/${service_name}.log"
    local pid_file="$LOGS_DIR/${service_name}.pid"

    if [ -f "$pid_file" ]; then
        local pid=$(cat "$pid_file")
        if ps -p $pid > /dev/null 2>&1; then
            echo -e "${YELLOW}⚠️  $service_name est déjà en cours d'exécution (PID: $pid)${NC}"
            return 0
        else
            rm -f "$pid_file"
        fi
    fi

    if check_port $port; then
        echo -e "${YELLOW}⚠️  Le port $port est déjà utilisé. $service_name pourrait déjà être démarré.${NC}"
        return 0
    fi

    if [ ! -f "$jar_file" ]; then
        echo -e "${RED}❌ JAR non trouvé pour $service_name après compilation. Vérifiez les erreurs de compilation.${NC}"
        return 1
    fi

    echo -e "${GREEN}🚀 Démarrage de $service_name sur le port $port...${NC}"
    cd "$service_dir"
    nohup java -jar "$jar_file" > "$log_file" 2>&1 &
    local pid=$!
    echo $pid > "$pid_file"
    cd "$PROJECT_ROOT"
    
    # Attendre un peu pour vérifier que le service démarre
    sleep 3
    if ps -p $pid > /dev/null 2>&1; then
        echo -e "${GREEN}✅ $service_name démarré (PID: $pid)${NC}"
    else
        echo -e "${RED}❌ Échec du démarrage de $service_name. Vérifiez les logs: $log_file${NC}"
        rm -f "$pid_file"
        return 1
    fi
}

# Fonction pour démarrer un frontend
start_frontend() {
    local frontend_name=$1
    local frontend_dir=$2
    local port=$3
    local log_file="$LOGS_DIR/${frontend_name}.log"
    local pid_file="$LOGS_DIR/${frontend_name}.pid"

    if [ -f "$pid_file" ]; then
        local pid=$(cat "$pid_file")
        if ps -p $pid > /dev/null 2>&1; then
            echo -e "${YELLOW}⚠️  $frontend_name est déjà en cours d'exécution (PID: $pid)${NC}"
            return 0
        else
            rm -f "$pid_file"
        fi
    fi

    if check_port $port; then
        echo -e "${YELLOW}⚠️  Le port $port est déjà utilisé. $frontend_name pourrait déjà être démarré.${NC}"
        return 0
    fi

    if [ ! -d "$frontend_dir/node_modules" ]; then
        echo -e "${YELLOW}⚠️  Dépendances non installées pour $frontend_name. Installation en cours...${NC}"
        cd "$frontend_dir"
        npm install > /dev/null 2>&1
        cd "$PROJECT_ROOT"
    fi

    echo -e "${GREEN}🚀 Démarrage de $frontend_name sur le port $port...${NC}"
    cd "$frontend_dir"
    # Le port est déjà configuré dans vite.config.ts, on utilise juste npm run dev
    nohup npm run dev > "$log_file" 2>&1 &
    local pid=$!
    echo $pid > "$pid_file"
    cd "$PROJECT_ROOT"
    
    # Attendre un peu pour vérifier que le frontend démarre
    sleep 3
    if ps -p $pid > /dev/null 2>&1; then
        echo -e "${GREEN}✅ $frontend_name démarré (PID: $pid)${NC}"
    else
        echo -e "${RED}❌ Échec du démarrage de $frontend_name. Vérifiez les logs: $log_file${NC}"
        rm -f "$pid_file"
        return 1
    fi
}

# Démarrer les services dans l'ordre de dépendance

echo -e "\n${GREEN}--- Services Backend ---${NC}"

# 1. Identity Service (port 8081) - Service de base
start_service "identity-service" "$PROJECT_ROOT/services/identity-service" 8081

# 2. Gateway (port 8080) - Dépend de Identity Service
start_service "gateway" "$PROJECT_ROOT/gateway" 8080

# 3. Autres services (peuvent démarrer en parallèle)
start_service "property-service" "$PROJECT_ROOT/services/property-service" 8083 &
start_service "resource-service" "$PROJECT_ROOT/services/resource-service" 8084 &
start_service "document-service" "$PROJECT_ROOT/services/document-service" 8085 &
start_service "workflow-service" "$PROJECT_ROOT/services/workflow-service" 8086 &
start_service "notification-service" "$PROJECT_ROOT/services/notification-service" 8087 &
start_service "emailing-service" "$PROJECT_ROOT/services/emailing-service" 8088 &
start_service "audit-service" "$PROJECT_ROOT/services/audit-service" 8089 &
start_service "billing-service" "$PROJECT_ROOT/services/billing-service" 8090 &

# Attendre que tous les services démarrent
wait

echo -e "\n${GREEN}--- Frontends ---${NC}"

# Démarrer les frontends
start_frontend "frontend-public" "$PROJECT_ROOT/frontend/public" 3003 &
start_frontend "frontend-admin" "$PROJECT_ROOT/frontend/admin" 3001 &
start_frontend "frontend-agent" "$PROJECT_ROOT/frontend/agent" 3002 &

# Attendre que tous les frontends démarrent
wait

echo -e "\n${GREEN}========================================${NC}"
echo -e "${GREEN}✅ Tous les services sont démarrés${NC}"
echo -e "${GREEN}========================================${NC}"
echo -e "\n${YELLOW}URLs disponibles:${NC}"
echo -e "  Gateway:        http://localhost:8080"
echo -e "  Identity:       http://localhost:8081"
echo -e "  Property:       http://localhost:8083"
echo -e "  Resource:       http://localhost:8084"
echo -e "  Document:       http://localhost:8085"
echo -e "  Workflow:       http://localhost:8086"
echo -e "  Notification:   http://localhost:8087"
echo -e "  Emailing:       http://localhost:8088"
echo -e "  Audit:          http://localhost:8089"
echo -e "  Billing:        http://localhost:8090"
echo -e "\n  Frontend Public: http://localhost:3003"
echo -e "  Frontend Admin:  http://localhost:3001"
echo -e "  Frontend Agent:  http://localhost:3002"
echo -e "\n${YELLOW}Services d'infrastructure:${NC}"
echo -e "  Redis:        localhost:6379"
echo -e "  PostgreSQL:   localhost:5432"
echo -e "  Kafka:        localhost:9092"
echo -e "  Elasticsearch: localhost:9200"
echo -e "\n${YELLOW}Logs disponibles dans: $LOGS_DIR${NC}"
echo -e "${YELLOW}Pour arrêter tous les services: ./scripts/stop-all.sh${NC}"

