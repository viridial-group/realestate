#!/bin/bash

# ========================
# Script de Diagnostic des Health Checks DOWN
# ========================
# Diagnostique pourquoi les health checks retournent DOWN

set -e

echo "🔍 Diagnostic des Health Checks DOWN"
echo ""

# ========================
# 1. Vérifier Redis
# ========================
echo "1. 🔴 Vérification de Redis..."
if command -v redis-cli &> /dev/null; then
    if redis-cli ping &> /dev/null; then
        echo "   ✅ Redis - Accessible"
    else
        echo "   ❌ Redis - NON ACCESSIBLE"
        echo "      💡 Solution: Démarrer Redis"
        echo "         - Local: redis-server"
        echo "         - Docker: docker run -d -p 6379:6379 redis:7.2.4"
    fi
else
    echo "   ⚠️  redis-cli non installé"
fi

echo ""

# ========================
# 2. Vérifier PostgreSQL
# ========================
echo "2. 🐘 Vérification de PostgreSQL..."

# Essayer avec le mot de passe par défaut
PGPASSWORD="${SPRING_DATASOURCE_PASSWORD:-123456}"
if command -v psql &> /dev/null; then
    if PGPASSWORD="$PGPASSWORD" psql -h localhost -U postgres -d realestate_db -c "SELECT 1;" &> /dev/null 2>&1; then
        echo "   ✅ PostgreSQL - Accessible"
    else
        echo "   ❌ PostgreSQL - NON ACCESSIBLE"
        echo "      💡 Vérifiez:"
        echo "         - PostgreSQL est démarré: systemctl status postgresql"
        echo "         - La base 'realestate_db' existe"
        echo "         - Le mot de passe est correct (SPRING_DATASOURCE_PASSWORD)"
        echo "         - Les permissions sont correctes"
    fi
else
    echo "   ⚠️  psql non installé"
fi

echo ""

# ========================
# 3. Vérifier les Health Checks détaillés
# ========================
echo "3. 🏥 Health Checks Détaillés..."
echo ""

ports=(8080 8081 8082 8083 8084 8085 8086 8087 8088 8089 8090)
service_names=("gateway" "identity" "organization" "property" "resource" "document" "workflow" "notification" "emailing" "audit" "billing")

for i in "${!ports[@]}"; do
    port=${ports[$i]}
    name=${service_names[$i]}
    
    health_response=$(curl -s http://localhost:$port/actuator/health 2>/dev/null || echo "")
    
    if [ -z "$health_response" ]; then
        echo "   ❌ Port $port ($name) - Non accessible"
        continue
    fi
    
    status=$(echo "$health_response" | grep -o '"status":"[^"]*"' | cut -d'"' -f4 || echo "UNKNOWN")
    
    if [ "$status" = "UP" ]; then
        echo "   ✅ Port $port ($name) - UP"
    else
        echo "   ⚠️  Port $port ($name) - $status"
        
        # Afficher les composants en échec
        if echo "$health_response" | grep -q "components"; then
            echo "      Composants:"
            echo "$health_response" | grep -o '"components":{[^}]*}' | sed 's/.*components":{//' | sed 's/}.*//' | while IFS= read -r component; do
                comp_name=$(echo "$component" | grep -o '"[^"]*":' | head -1 | tr -d '":')
                comp_status=$(echo "$component" | grep -o '"status":"[^"]*"' | cut -d'"' -f4)
                if [ "$comp_status" != "UP" ]; then
                    echo "         - $comp_name: $comp_status"
                fi
            done
        fi
    fi
done

echo ""

# ========================
# 4. Vérifier les logs pour erreurs
# ========================
echo "4. 📋 Dernières erreurs dans les logs..."
echo ""

LOGS_DIR="/opt/source/realestate/logs"

if [ -d "$LOGS_DIR" ]; then
    for log_file in "$LOGS_DIR"/*.log; do
        if [ -f "$log_file" ]; then
            service_name=$(basename "$log_file" .log)
            errors=$(tail -100 "$log_file" | grep -i "error\|exception\|failed" | tail -3)
            if [ -n "$errors" ]; then
                echo "   ⚠️  $service_name:"
                echo "$errors" | sed 's/^/      /'
            fi
        fi
    done
else
    echo "   ⚠️  Répertoire logs introuvable: $LOGS_DIR"
fi

echo ""
echo "✅ Diagnostic terminé"

