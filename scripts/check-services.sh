#!/bin/bash

# ========================
# Script de Vérification des Services
# ========================
# Ce script vérifie que tous les services sont démarrés et accessibles

set -e

echo "🔍 Vérification des services"

# ========================
# Variables
# ========================
GATEWAY_PORT=8080
IDENTITY_PORT=8081
ORGANIZATION_PORT=8082
PROPERTY_PORT=8083

ALL_OK=true

# ========================
# Fonction de vérification
# ========================
check_service() {
    local name=$1
    local port=$2
    local url=$3
    
    echo -n "🔍 Vérification $name (port $port)... "
    
    if curl -s -f -m 2 "$url" > /dev/null 2>&1; then
        echo "✅ OK"
        return 0
    else
        echo "❌ NON DÉMARRÉ"
        echo "   💡 Démarrer avec: systemctl start realestate-$name"
        ALL_OK=false
        return 1
    fi
}

# ========================
# Vérification des services système
# ========================
echo "📦 Services système:"
echo ""

# PostgreSQL
if systemctl is-active --quiet postgresql; then
    echo "✅ PostgreSQL: ACTIF"
else
    echo "❌ PostgreSQL: INACTIF"
    echo "   💡 Démarrer avec: systemctl start postgresql"
    ALL_OK=false
fi

# Redis
if systemctl is-active --quiet redis-server; then
    echo "✅ Redis: ACTIF"
else
    echo "❌ Redis: INACTIF"
    echo "   💡 Démarrer avec: systemctl start redis-server"
    ALL_OK=false
fi

echo ""

# ========================
# Vérification des microservices
# ========================
echo "🚀 Microservices:"
echo ""

# Gateway
check_service "gateway" "$GATEWAY_PORT" "http://localhost:$GATEWAY_PORT/actuator/health" || true

# Identity Service
check_service "identity-service" "$IDENTITY_PORT" "http://localhost:$IDENTITY_PORT/actuator/health" || true

# Organization Service
check_service "organization-service" "$ORGANIZATION_PORT" "http://localhost:$ORGANIZATION_PORT/actuator/health" || true

# Property Service
check_service "property-service" "$PROPERTY_PORT" "http://localhost:$PROPERTY_PORT/actuator/health" || true

echo ""

# ========================
# Vérification Nginx
# ========================
echo "🌐 Nginx:"
if systemctl is-active --quiet nginx; then
    echo "✅ Nginx: ACTIF"
    if nginx -t > /dev/null 2>&1; then
        echo "✅ Configuration Nginx: VALIDE"
    else
        echo "❌ Configuration Nginx: ERREUR"
        ALL_OK=false
    fi
else
    echo "❌ Nginx: INACTIF"
    echo "   💡 Démarrer avec: systemctl start nginx"
    ALL_OK=false
fi

echo ""

# ========================
# Résumé
# ========================
if [ "$ALL_OK" = true ]; then
    echo "✅ Tous les services sont opérationnels!"
    echo ""
    echo "🌐 URLs disponibles:"
    echo "   - http://api.viridial.com"
    echo "   - http://app.viridial.com"
else
    echo "❌ Certains services ne sont pas démarrés"
    echo ""
    echo "📝 Actions requises:"
    echo "1. Démarrer les services manquants"
    echo "2. Vérifier les logs: journalctl -u realestate-*"
    echo "3. Réexécuter: ./scripts/check-services.sh"
    exit 1
fi

