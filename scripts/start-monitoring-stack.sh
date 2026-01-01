#!/bin/bash

# ========================
# Script de Démarrage de la Stack de Monitoring
# ========================
# Démarre Prometheus et Grafana

set -e

echo "🚀 Démarrage de la Stack de Monitoring..."
echo ""

# Démarrer Prometheus
if [ -f "$(dirname "$0")/start-prometheus.sh" ]; then
    "$(dirname "$0")/start-prometheus.sh"
    echo ""
else
    echo "❌ Script start-prometheus.sh introuvable"
    exit 1
fi

# Démarrer Grafana
if [ -f "$(dirname "$0")/start-grafana.sh" ]; then
    "$(dirname "$0")/start-grafana.sh"
    echo ""
else
    echo "❌ Script start-grafana.sh introuvable"
    exit 1
fi

echo "✅ Stack de Monitoring démarrée !"
echo ""
echo "📊 Accès:"
echo "   - Prometheus: http://localhost:9090"
echo "   - Grafana: http://localhost:3000 (admin/admin)"
echo ""
echo "💡 Configurez Prometheus comme source de données dans Grafana:"
echo "   1. Allez dans Configuration > Data Sources"
echo "   2. Ajoutez Prometheus"
echo "   3. URL: http://host.docker.internal:9090"

