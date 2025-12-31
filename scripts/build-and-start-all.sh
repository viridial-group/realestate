#!/bin/bash

# ========================
# Script de Build et Démarrage de Tous les Services
# ========================
# Compile tous les services et les démarre avec logs dans logs/

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
LOGS_DIR="$PROJECT_ROOT/logs"

echo "🚀 Build et Démarrage de Tous les Services"
echo "📁 Répertoire du projet: $PROJECT_ROOT"
echo "📁 Répertoire des logs: $LOGS_DIR"
echo ""

# Créer le répertoire des logs
mkdir -p "$LOGS_DIR"

# ========================
# Build des Services
# ========================
echo "🔨 Compilation des services (Maven clean package -DskipTests=true -Dmaven.test.skip=true)..."
echo ""

cd "$PROJECT_ROOT"

# Build parent et common d'abord
echo "📦 Build du parent POM et common module..."
mvn clean install -DskipTests=true -Dmaven.test.skip=true -N -q
mvn clean install -DskipTests=true -Dmaven.test.skip=true -pl common -q

# Build tous les services
echo "📦 Build de tous les services..."
mvn clean package -DskipTests=true -Dmaven.test.skip=true -q

if [ $? -ne 0 ]; then
    echo "❌ Erreur lors de la compilation"
    exit 1
fi

echo "✅ Compilation terminée"
echo ""

# ========================
# Fonction pour démarrer un service
# ========================
start_service() {
    local service_name=$1
    local service_dir=$2
    local port=$3
    local jar_pattern=$4
    
    local jar_path=$(find "$PROJECT_ROOT/$service_dir/target" -name "$jar_pattern" -type f 2>/dev/null | head -1)
    
    if [ -z "$jar_path" ]; then
        echo "❌ JAR non trouvé pour $service_name"
        return 1
    fi
    
    # Vérifier si le port est déjà utilisé
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1 ; then
        echo "⚠️  Port $port déjà utilisé pour $service_name - Service peut-être déjà démarré"
        return 1
    fi
    
    echo "🚀 Démarrage de $service_name sur le port $port..."
    
    # Démarrer le service en arrière-plan avec logs
    cd "$PROJECT_ROOT"
    nohup java -jar "$jar_path" \
        > "$LOGS_DIR/${service_name}.log" 2>&1 &
    
    local pid=$!
    echo "   PID: $pid"
    echo "   Logs: $LOGS_DIR/${service_name}.log"
    
    # Attendre un peu pour vérifier que le service démarre
    sleep 3
    
    # Vérifier si le processus est toujours actif
    if ps -p $pid > /dev/null 2>&1; then
        echo "   ✅ $service_name démarré"
        echo "$pid" > "$LOGS_DIR/${service_name}.pid"
    else
        echo "   ❌ $service_name n'a pas démarré - Vérifiez les logs"
        return 1
    fi
}

# ========================
# Démarrage des Services
# ========================
echo "🚀 Démarrage des services..."
echo ""

# Services dans l'ordre de dépendance
services=(
    "gateway:gateway:8080:gateway-*.jar"
    "identity-service:services/identity-service:8081:identity-service-*.jar"
    "organization-service:services/organization-service:8082:organization-service-*.jar"
    "resource-service:services/resource-service:8084:resource-service-*.jar"
    "property-service:services/property-service:8083:property-service-*.jar"
    "document-service:services/document-service:8085:document-service-*.jar"
    "workflow-service:services/workflow-service:8086:workflow-service-*.jar"
    "notification-service:services/notification-service:8087:notification-service-*.jar"
    "emailing-service:services/emailing-service:8088:emailing-service-*.jar"
    "audit-service:services/audit-service:8089:audit-service-*.jar"
    "billing-service:services/billing-service:8090:billing-service-*.jar"
)

for service_config in "${services[@]}"; do
    IFS=':' read -r service_name service_dir port jar_pattern <<< "$service_config"
    start_service "$service_name" "$service_dir" "$port" "$jar_pattern"
    sleep 2
done

echo ""
echo "✅ Tous les services ont été démarrés"
echo ""
echo "📋 Services démarrés:"
for service_config in "${services[@]}"; do
    IFS=':' read -r service_name service_dir port jar_pattern <<< "$service_config"
    if [ -f "$LOGS_DIR/${service_name}.pid" ]; then
        local pid=$(cat "$LOGS_DIR/${service_name}.pid")
        if ps -p $pid > /dev/null 2>&1; then
            echo "   ✅ $service_name (PID: $pid, Port: $port)"
        else
            echo "   ❌ $service_name (arrêté)"
        fi
    fi
done

echo ""
echo "📁 Logs disponibles dans: $LOGS_DIR"
echo ""
echo "💡 Commandes utiles:"
echo "   - Voir les logs: tail -f $LOGS_DIR/<service-name>.log"
echo "   - Arrêter tous les services: ./scripts/stop-all-services.sh"
echo "   - Vérifier les PIDs: cat $LOGS_DIR/*.pid"

