#!/bin/bash

# Script pour démarrer Redis

set -e

echo "🔴 Démarrage de Redis..."

# Vérifier si Redis est déjà démarré
if redis-cli ping &> /dev/null; then
    echo "✅ Redis est déjà démarré"
    redis-cli ping
    exit 0
fi

# Essayer de démarrer Redis
if command -v redis-server &> /dev/null; then
    echo "🔄 Démarrage de Redis..."
    
    # Vérifier si un processus Redis existe déjà
    if pgrep -x redis-server > /dev/null; then
        echo "✅ Redis est déjà en cours d'exécution (processus trouvé)"
        redis-cli ping
        exit 0
    fi
    
    # Démarrer Redis en arrière-plan
    redis-server --daemonize yes 2>/dev/null || {
        echo "⚠️  Impossible de démarrer Redis automatiquement"
        echo "💡 Démarrez Redis manuellement:"
        echo "   - macOS: brew services start redis"
        echo "   - Linux: sudo systemctl start redis"
        echo "   - Ou: redis-server"
        exit 1
    }
    
    # Attendre que Redis démarre
    sleep 2
    
    # Vérifier que Redis est démarré
    if redis-cli ping &> /dev/null; then
        echo "✅ Redis démarré avec succès"
        redis-cli ping
    else
        echo "❌ Redis n'a pas démarré correctement"
        exit 1
    fi
    
elif docker ps | grep -q "redis"; then
    echo "✅ Redis (Docker) est déjà démarré"
    
elif command -v docker &> /dev/null; then
    echo "🔄 Démarrage de Redis avec Docker..."
    docker run -d \
        --name redis \
        -p 6379:6379 \
        redis:7.2.4 \
        redis-server --appendonly yes
    
    sleep 2
    
    if redis-cli ping &> /dev/null; then
        echo "✅ Redis (Docker) démarré avec succès"
    else
        echo "❌ Redis (Docker) n'a pas démarré correctement"
        exit 1
    fi
    
else
    echo "❌ Redis n'est pas installé"
    echo ""
    echo "💡 Options d'installation :"
    echo "   1. macOS: brew install redis"
    echo "   2. Linux: sudo apt-get install redis-server"
    echo "   3. Docker: docker run -d -p 6379:6379 redis:7.2.4"
    exit 1
fi

