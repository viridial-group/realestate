#!/bin/bash

# ========================
# Script de Démarrage des Dépendances
# ========================
# Démarre toutes les dépendances nécessaires (Redis, PostgreSQL, Kafka, Elasticsearch)

set -e

echo "🚀 Démarrage des Dépendances"
echo ""

# ========================
# 1. Redis
# ========================
echo "1. 🔴 Démarrage de Redis..."

if command -v redis-server &> /dev/null; then
    if redis-cli ping &> /dev/null; then
        echo "   ✅ Redis est déjà démarré"
    else
        echo "   🔄 Démarrage de Redis..."
        # Essayer de démarrer Redis en arrière-plan
        redis-server --daemonize yes 2>/dev/null || {
            echo "   ⚠️  Impossible de démarrer Redis automatiquement"
            echo "   💡 Démarrez Redis manuellement: redis-server"
        }
        sleep 2
        if redis-cli ping &> /dev/null; then
            echo "   ✅ Redis démarré"
        else
            echo "   ❌ Redis n'a pas démarré"
        fi
    fi
elif docker ps | grep -q "redis"; then
    echo "   ✅ Redis (Docker) est déjà démarré"
else
    echo "   ⚠️  Redis n'est pas installé"
    echo "   💡 Options:"
    echo "      1. Installer Redis: brew install redis (macOS)"
    echo "      2. Utiliser Docker: docker run -d -p 6379:6379 redis:7.2.4"
fi

echo ""

# ========================
# 2. PostgreSQL
# ========================
echo "2. 🐘 Vérification de PostgreSQL..."

if command -v psql &> /dev/null; then
    if psql -h localhost -U postgres -d realestate_db -c "SELECT 1;" &> /dev/null; then
        echo "   ✅ PostgreSQL est accessible"
    else
        echo "   ⚠️  PostgreSQL n'est pas accessible"
        echo "   💡 Vérifiez que PostgreSQL est démarré et que la base 'realestate_db' existe"
    fi
else
    echo "   ⚠️  psql n'est pas installé"
    echo "   💡 Vérifiez que PostgreSQL est démarré"
fi

echo ""

# ========================
# 3. Kafka
# ========================
echo "3. 📨 Vérification de Kafka..."

if docker ps | grep -q "kafka\|realestate-kafka"; then
    echo "   ✅ Kafka (Docker) est démarré"
else
    echo "   ⚠️  Kafka n'est pas démarré"
    echo "   💡 Démarrez Kafka: ./scripts/start-kafka.sh"
fi

echo ""

# ========================
# 4. Elasticsearch
# ========================
echo "4. 🔍 Vérification d'Elasticsearch..."

if docker ps | grep -q "elasticsearch"; then
    echo "   ✅ Elasticsearch (Docker) est démarré"
else
    echo "   ⚠️  Elasticsearch n'est pas démarré"
    echo "   💡 Démarrez Elasticsearch: ./scripts/start-elasticsearch.sh"
fi

echo ""

# ========================
# Résumé
# ========================
echo "✅ Vérification terminée"
echo ""
echo "📋 Statut des dépendances :"
echo "   - Redis: $(redis-cli ping 2>/dev/null && echo '✅ UP' || echo '❌ DOWN')"
echo "   - PostgreSQL: $(psql -h localhost -U postgres -d realestate_db -c 'SELECT 1;' &>/dev/null && echo '✅ UP' || echo '❌ DOWN')"
echo "   - Kafka: $(docker ps | grep -q 'kafka\|realestate-kafka' && echo '✅ UP' || echo '❌ DOWN')"
echo "   - Elasticsearch: $(docker ps | grep -q 'elasticsearch' && echo '✅ UP' || echo '❌ DOWN')"
echo ""
echo "💡 Pour démarrer toutes les dépendances :"
echo "   ./scripts/start-dependencies.sh"
echo "   ./scripts/start-kafka.sh"
echo "   ./scripts/start-elasticsearch.sh"

