#!/bin/bash

# Script d'installation de tous les services sur le VPS
# Usage: ./scripts/install-all-services.sh

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"

# Configuration
VPS_USER="root"
VPS_HOST="148.230.112.148"
APP_DIR="/var/realestate"
BIN_DIR="$APP_DIR/bin"
CONFIG_DIR="$APP_DIR/config"
LOG_DIR="$APP_DIR/logs"

# Services à installer
SERVICES=(
    "gateway:8080"
    "identity-service:8081"
    "organization-service:8082"
    "property-service:8083"
    "resource-service:8084"
    "document-service:8085"
    "workflow-service:8086"
    "notification-service:8087"
    "emailing-service:8088"
    "audit-service:8089"
    "billing-service:8090"
)

echo "🔧 Installation de tous les services sur le VPS..."

# Créer les répertoires nécessaires
echo "📁 Création des répertoires..."
ssh $VPS_USER@$VPS_HOST "mkdir -p $BIN_DIR $CONFIG_DIR $LOG_DIR"

# Créer l'utilisateur realestate s'il n'existe pas
echo "👤 Vérification de l'utilisateur realestate..."
ssh $VPS_USER@$VPS_HOST "id -u realestate &>/dev/null || useradd -r -s /bin/bash -d $APP_DIR realestate"

# Compiler tous les services LOCALEMENT
echo "🔨 Compilation de tous les services (localement)..."
cd "$PROJECT_DIR"

# Vérifier que nous sommes dans le bon répertoire
if [ ! -f "pom.xml" ]; then
    echo "❌ Erreur: pom.xml non trouvé dans $PROJECT_DIR"
    exit 1
fi

# Installer d'abord le parent POM et common
echo "  → Installation du parent POM et common..."
mvn clean install -DskipTests -pl common -am -N

# Compiler tous les services
echo "  → Compilation de tous les services..."
mvn clean package -DskipTests

# Copier les JARs et configurations
echo "📦 Copie des JARs et configurations..."
for service_info in "${SERVICES[@]}"; do
    IFS=':' read -r service_name port <<< "$service_info"
    
    echo "  → Installation de $service_name (port $port)..."
    
    # Déterminer le chemin du JAR
    if [ "$service_name" = "gateway" ]; then
        JAR_PATH="$PROJECT_DIR/gateway/target/gateway-*.jar"
        CONFIG_PATH="$PROJECT_DIR/gateway/src/main/resources/application-prod.yml"
    else
        JAR_PATH="$PROJECT_DIR/services/$service_name/target/$service_name-*.jar"
        CONFIG_PATH="$PROJECT_DIR/services/$service_name/src/main/resources/application-prod.yml"
    fi
    
    # Trouver le JAR exact (résoudre le wildcard)
    JAR_FILE=$(ls $JAR_PATH 2>/dev/null | head -1)
    
    # Copier le JAR
    if [ -n "$JAR_FILE" ] && [ -f "$JAR_FILE" ]; then
        scp "$JAR_FILE" $VPS_USER@$VPS_HOST:$BIN_DIR/$service_name.jar
        echo "    ✅ JAR copié: $(basename $JAR_FILE)"
    else
        echo "    ⚠️  JAR non trouvé pour $service_name (cherché: $JAR_PATH)"
        continue
    fi
    
    # Copier la configuration si elle existe
    if [ -f "$CONFIG_PATH" ]; then
        ssh $VPS_USER@$VPS_HOST "mkdir -p $CONFIG_DIR"
        scp "$CONFIG_PATH" $VPS_USER@$VPS_HOST:$CONFIG_DIR/$service_name-application-prod.yml
        echo "    ✅ Configuration copiée"
    fi
    
    # Définir les permissions
    ssh $VPS_USER@$VPS_HOST "chown -R realestate:realestate $APP_DIR"
    ssh $VPS_USER@$VPS_HOST "chmod +x $BIN_DIR/$service_name.jar"
done

# Installer les services systemd
echo "⚙️  Installation des services systemd..."
ssh $VPS_USER@$VPS_HOST "cd /opt/source/realestate && ./scripts/install-services.sh"

echo ""
echo "✅ Installation terminée !"
echo ""
echo "📝 Prochaines étapes:"
echo "   1. Vérifier les configurations: ssh $VPS_USER@$VPS_HOST 'ls -la $CONFIG_DIR'"
echo "   2. Démarrer tous les services: ./scripts/start-all-services.sh"
echo "   3. Vérifier le statut: ./scripts/status-all-services.sh"

