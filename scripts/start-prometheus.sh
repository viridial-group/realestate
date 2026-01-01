#!/bin/bash

# ========================
# Script de Démarrage de Prometheus
# ========================

set -e

PROMETHEUS_VERSION="2.49.1"
PROMETHEUS_PORT=9090
CONFIG_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)/config/prometheus"
DATA_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)/data/prometheus"

echo "🚀 Démarrage de Prometheus..."
echo ""

# Créer le répertoire de données si nécessaire
mkdir -p "$DATA_DIR"

# Vérifier si Prometheus est déjà en cours d'exécution
if docker ps --format '{{.Names}}' | grep -q "^prometheus$"; then
    echo "⚠️  Prometheus est déjà en cours d'exécution"
    echo "   Pour le redémarrer: docker stop prometheus && docker rm prometheus"
    exit 0
fi

# Vérifier si le port est déjà utilisé
if lsof -Pi :$PROMETHEUS_PORT -sTCP:LISTEN -t >/dev/null 2>&1 ; then
    echo "⚠️  Le port $PROMETHEUS_PORT est déjà utilisé"
    echo "   Arrêtez le processus utilisant ce port ou modifiez PROMETHEUS_PORT"
    exit 1
fi

# Vérifier que le fichier de configuration existe
if [ ! -f "$CONFIG_DIR/prometheus.yml" ]; then
    echo "❌ Fichier de configuration introuvable: $CONFIG_DIR/prometheus.yml"
    exit 1
fi

# Démarrer Prometheus avec Docker
echo "📦 Démarrage de Prometheus (version $PROMETHEUS_VERSION)..."
docker run -d \
    --name prometheus \
    -p $PROMETHEUS_PORT:$PROMETHEUS_PORT \
    -v "$CONFIG_DIR:/etc/prometheus" \
    -v "$DATA_DIR:/prometheus" \
    prom/prometheus:v$PROMETHEUS_VERSION \
    --config.file=/etc/prometheus/prometheus.yml \
    --storage.tsdb.path=/prometheus \
    --web.console.libraries=/usr/share/prometheus/console_libraries \
    --web.console.templates=/usr/share/prometheus/consoles \
    --web.enable-lifecycle

# Attendre que Prometheus démarre
echo "⏳ Attente du démarrage de Prometheus..."
sleep 5

# Vérifier que Prometheus répond
if curl -s http://localhost:$PROMETHEUS_PORT/-/healthy > /dev/null 2>&1; then
    echo "✅ Prometheus démarré avec succès !"
    echo ""
    echo "📊 Accès:"
    echo "   - Interface Web: http://localhost:$PROMETHEUS_PORT"
    echo "   - Métriques: http://localhost:$PROMETHEUS_PORT/metrics"
    echo "   - Targets: http://localhost:$PROMETHEUS_PORT/targets"
    echo "   - Graph: http://localhost:$PROMETHEUS_PORT/graph"
    echo ""
    echo "📁 Configuration: $CONFIG_DIR/prometheus.yml"
    echo "📁 Données: $DATA_DIR"
else
    echo "❌ Prometheus n'a pas démarré correctement"
    echo "   Vérifiez les logs: docker logs prometheus"
    exit 1
fi

