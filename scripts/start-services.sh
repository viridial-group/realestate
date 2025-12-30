#!/bin/bash

# ========================
# Script de Démarrage des Services
# ========================

set -e

ENVIRONMENT=${1:-prod}

echo "🚀 Démarrage des services - Environnement: $ENVIRONMENT"

# ========================
# Services Système
# ========================
echo "📦 Démarrage des services système..."

# PostgreSQL
if systemctl start postgresql; then
    echo "✅ PostgreSQL démarré"
else
    echo "⚠️  PostgreSQL déjà démarré ou erreur"
fi

# Redis
if systemctl start redis-server; then
    echo "✅ Redis démarré"
else
    echo "⚠️  Redis déjà démarré ou erreur"
fi

# Elasticsearch (optionnel)
if systemctl is-enabled elasticsearch > /dev/null 2>&1; then
    if systemctl start elasticsearch; then
        echo "✅ Elasticsearch démarré"
    else
        echo "⚠️  Elasticsearch déjà démarré ou erreur"
    fi
else
    echo "ℹ️  Elasticsearch non configuré (optionnel)"
fi

# Kafka (optionnel)
if [ -f /opt/kafka/bin/kafka-server-start.sh ]; then
    if ! pgrep -f kafka > /dev/null; then
        /opt/kafka/bin/kafka-server-start.sh -daemon /opt/kafka/config/server.properties
        echo "✅ Kafka démarré"
    else
        echo "⚠️  Kafka déjà démarré"
    fi
else
    echo "ℹ️  Kafka non installé (optionnel)"
fi

# ========================
# Microservices
# ========================
echo ""
echo "🚀 Démarrage des microservices..."

services=(
    "realestate-gateway"
    "realestate-identity-service"
    "realestate-organization-service"
    "realestate-property-service"
)

for service in "${services[@]}"; do
    if systemctl is-enabled "$service" > /dev/null 2>&1; then
        if systemctl start "$service"; then
            echo "✅ $service démarré"
            # Attendre un peu pour que le service démarre
            sleep 2
        else
            echo "❌ Erreur lors du démarrage de $service"
            echo "   💡 Vérifier les logs: journalctl -u $service -n 50"
        fi
    else
        echo "⚠️  $service non installé (exécutez: ./scripts/install-services.sh)"
    fi
done

echo ""
echo "✅ Démarrage terminé!"
echo ""
echo "📝 Vérifier le statut:"
echo "   ./scripts/status.sh"
echo ""
echo "📋 Voir les logs:"
echo "   journalctl -u realestate-gateway -f"

