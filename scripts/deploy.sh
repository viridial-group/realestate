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

# Copier les JARs (seulement le gateway pour l'instant)
if [ -f "$PROJECT_DIR/gateway/target"/*.jar ]; then
    scp $PROJECT_DIR/gateway/target/*.jar $VPS_USER@$VPS_HOST:$APP_DIR/bin/gateway.jar
    log "✅ Gateway JAR copié"
else
    error "Gateway JAR introuvable. Avez-vous compilé le projet?"
fi

# Copier les configurations (si elles existent)
if [ -f "$PROJECT_DIR/gateway/src/main/resources/application-$ENVIRONMENT.yml" ]; then
    ssh $VPS_USER@$VPS_HOST "mkdir -p $APP_DIR/config"
    scp $PROJECT_DIR/gateway/src/main/resources/application-$ENVIRONMENT.yml $VPS_USER@$VPS_HOST:$APP_DIR/config/application-prod.yml
    log "✅ Configuration copiée"
fi

# ========================
# Services Systemd
# ========================
log "Configuration des services systemd..."

ssh $VPS_USER@$VPS_HOST << EOF
    # Gateway Service
    if [ -f "$APP_DIR/bin/gateway.jar" ]; then
        cat > /etc/systemd/system/realestate-gateway.service << SERVICE
[Unit]
Description=Real Estate Gateway Service
After=network.target postgresql.service redis.service

[Service]
Type=simple
User=$SERVICE_USER
WorkingDirectory=$APP_DIR
ExecStart=/usr/bin/java -jar $APP_DIR/bin/gateway.jar --spring.config.location=$APP_DIR/config/application-prod.yml
Restart=always
RestartSec=10
StandardOutput=journal
StandardError=journal

[Install]
WantedBy=multi-user.target
SERVICE

        # Recharger systemd
        systemctl daemon-reload
        systemctl enable realestate-gateway
        systemctl restart realestate-gateway
        log "✅ Service Gateway démarré"
    else
        log "⚠️  Gateway JAR introuvable, service non créé"
    fi
EOF

log "✅ Déploiement terminé avec succès!"

