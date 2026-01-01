#!/bin/bash

# ========================
# Script de Démarrage de Grafana
# ========================

set -e

GRAFANA_VERSION="10.3.3"
GRAFANA_PORT=3000
DATA_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)/data/grafana"
PROVISIONING_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)/config/grafana/provisioning"
DASHBOARDS_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)/config/grafana/dashboards"

echo "🚀 Démarrage de Grafana..."
echo ""

# Créer les répertoires nécessaires
mkdir -p "$DATA_DIR"
mkdir -p "$PROVISIONING_DIR/datasources"
mkdir -p "$PROVISIONING_DIR/dashboards"
mkdir -p "$DASHBOARDS_DIR"

# Vérifier si Grafana est déjà en cours d'exécution
if docker ps --format '{{.Names}}' | grep -q "^grafana$"; then
    echo "⚠️  Grafana est déjà en cours d'exécution"
    echo "   Pour le redémarrer: docker stop grafana && docker rm grafana"
    exit 0
fi

# Vérifier si le port est déjà utilisé
if lsof -Pi :$GRAFANA_PORT -sTCP:LISTEN -t >/dev/null 2>&1 ; then
    echo "⚠️  Le port $GRAFANA_PORT est déjà utilisé"
    echo "   Arrêtez le processus utilisant ce port ou modifiez GRAFANA_PORT"
    exit 1
fi

# Démarrer Grafana avec Docker
echo "📦 Démarrage de Grafana (version $GRAFANA_VERSION)..."
docker run -d \
    --name grafana \
    -p $GRAFANA_PORT:$GRAFANA_PORT \
    -v "$DATA_DIR:/var/lib/grafana" \
    -v "$PROVISIONING_DIR:/etc/grafana/provisioning" \
    -v "$DASHBOARDS_DIR:/var/lib/grafana/dashboards" \
    -e "GF_SECURITY_ADMIN_USER=admin" \
    -e "GF_SECURITY_ADMIN_PASSWORD=admin" \
    -e "GF_USERS_ALLOW_SIGN_UP=false" \
    -e "GF_SERVER_ROOT_URL=http://localhost:$GRAFANA_PORT" \
    -e "GF_INSTALL_PLUGINS=" \
    grafana/grafana:$GRAFANA_VERSION

# Attendre que Grafana démarre
echo "⏳ Attente du démarrage de Grafana..."
sleep 10

# Vérifier que Grafana répond
if curl -s http://localhost:$GRAFANA_PORT/api/health > /dev/null 2>&1; then
    echo "✅ Grafana démarré avec succès !"
    echo ""
    echo "📊 Accès:"
    echo "   - Interface Web: http://localhost:$GRAFANA_PORT"
    echo "   - Login: admin / admin"
    echo "   - (Changez le mot de passe à la première connexion)"
    echo ""
    echo "📁 Données: $DATA_DIR"
    echo "📁 Provisioning: $PROVISIONING_DIR"
    echo ""
    echo "💡 Pour configurer Prometheus comme source de données:"
    echo "   1. Allez dans Configuration > Data Sources"
    echo "   2. Ajoutez Prometheus"
    echo "   3. URL: http://host.docker.internal:9090 (ou http://localhost:9090 si sur le même host)"
else
    echo "❌ Grafana n'a pas démarré correctement"
    echo "   Vérifiez les logs: docker logs grafana"
    exit 1
fi

