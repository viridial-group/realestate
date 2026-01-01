#!/bin/bash

# ========================
# Script de Démarrage de la Stack ELK
# ========================
# Démarre Elasticsearch, Logstash et Kibana

set -e

echo "🚀 Démarrage de la Stack ELK..."
echo ""

# Démarrer Elasticsearch
if [ -f "$(dirname "$0")/start-elasticsearch.sh" ]; then
    "$(dirname "$0")/start-elasticsearch.sh"
    echo ""
else
    echo "❌ Script start-elasticsearch.sh introuvable"
    exit 1
fi

# Attendre qu'Elasticsearch soit prêt
echo "⏳ Attente qu'Elasticsearch soit prêt..."
sleep 10

# Démarrer Logstash
if [ -f "$(dirname "$0")/start-logstash.sh" ]; then
    "$(dirname "$0")/start-logstash.sh"
    echo ""
else
    echo "❌ Script start-logstash.sh introuvable"
    exit 1
fi

# Démarrer Kibana
if [ -f "$(dirname "$0")/start-kibana.sh" ]; then
    "$(dirname "$0")/start-kibana.sh"
    echo ""
else
    echo "❌ Script start-kibana.sh introuvable"
    exit 1
fi

echo "✅ Stack ELK démarrée !"
echo ""
echo "📊 Accès:"
echo "   - Elasticsearch: http://localhost:9200"
echo "   - Logstash: TCP port 5000"
echo "   - Kibana: http://localhost:5601"
echo ""
echo "💡 Pour activer l'envoi de logs depuis les services:"
echo "   - Définir LOGSTASH_ENABLED=true"
echo "   - Définir LOGSTASH_HOST=localhost"
echo "   - Définir LOGSTASH_PORT=5000"

