#!/bin/bash

# ========================
# Script de Déploiement VPS
# ========================
# Usage: ./deploy.sh [environment]
# Environments: local, dev, staging, pre-prod, prod

set -e

ENVIRONMENT=${1:-prod}
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"

echo "🚀 Déploiement sur VPS - Environnement: $ENVIRONMENT"

# ========================
# Configuration
# ========================
VPS_USER=${VPS_USER:-root}
VPS_HOST=${VPS_HOST:-148.230.112.148}
APP_DIR=/var/realestate
SERVICE_USER=realestate

# ========================
# Fonctions
# ========================
log() {
    echo "[$(date +'%Y-%m-%d %H:%M:%S')] $1"
}

error() {
    echo "[ERROR] $1" >&2
    exit 1
}

# ========================
# Vérifications
# ========================
log "Vérification des prérequis..."

if ! command -v mvn &> /dev/null; then
    error "Maven n'est pas installé"
fi

if ! command -v java &> /dev/null; then
    error "Java n'est pas installé"
fi

# ========================
# Build
# ========================
log "Build du projet..."
cd "$PROJECT_DIR"
mvn clean package -DskipTests -P$ENVIRONMENT

if [ $? -ne 0 ]; then
    error "Échec du build"
fi

# ========================
# Préparation du déploiement
# ========================
log "Préparation du déploiement..."

# Créer les répertoires sur le VPS
ssh $VPS_USER@$VPS_HOST << EOF
    mkdir -p $APP_DIR/{bin,config,logs,storage,backup}
    mkdir -p $APP_DIR/storage/{documents,images,temp}
    chown -R $SERVICE_USER:$SERVICE_USER $APP_DIR
    chmod -R 755 $APP_DIR
EOF

# ========================
# Déploiement
# ========================
log "Déploiement des fichiers..."

# Copier les JARs
scp $PROJECT_DIR/gateway/target/*.jar $VPS_USER@$VPS_HOST:$APP_DIR/bin/gateway.jar
scp $PROJECT_DIR/services/*/target/*.jar $VPS_USER@$VPS_HOST:$APP_DIR/bin/

# Copier les configurations
scp $PROJECT_DIR/config/application-$ENVIRONMENT.yml $VPS_USER@$VPS_HOST:$APP_DIR/config/
scp $PROJECT_DIR/config/application-$ENVIRONMENT.properties $VPS_USER@$VPS_HOST:$APP_DIR/config/

# ========================
# Services Systemd
# ========================
log "Configuration des services systemd..."

ssh $VPS_USER@$VPS_HOST << EOF
    # Gateway Service
    cat > /etc/systemd/system/realestate-gateway.service << SERVICE
[Unit]
Description=Real Estate Gateway Service
After=network.target postgresql.service redis.service

[Service]
Type=simple
User=$SERVICE_USER
WorkingDirectory=$APP_DIR
ExecStart=/usr/bin/java -jar $APP_DIR/bin/gateway.jar --spring.config.location=$APP_DIR/config/application-$ENVIRONMENT.yml
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
SERVICE

    # Recharger systemd
    systemctl daemon-reload
    systemctl enable realestate-gateway
    systemctl restart realestate-gateway
EOF

log "✅ Déploiement terminé avec succès!"

