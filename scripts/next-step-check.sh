#!/bin/bash

# ========================
# Script de Vérification - Prochaines Étapes
# ========================
# Vérifie l'état actuel et propose les actions immédiates

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

echo "🔍 Vérification de l'état du projet"
echo "===================================="
echo ""

# ========================
# 1. Vérifier les services système
# ========================
echo "📦 1. Services Système:"
echo ""

# PostgreSQL
if command -v psql &> /dev/null; then
    if psql -h 148.230.112.148 -U postgres -d realestate_db -c "SELECT 1;" &> /dev/null; then
        echo "   ✅ PostgreSQL: Accessible"
    else
        echo "   ❌ PostgreSQL: Non accessible (148.230.112.148:5432)"
    fi
else
    echo "   ⚠️  PostgreSQL: psql non installé (vérification impossible)"
fi

# Redis
if command -v redis-cli &> /dev/null; then
    if redis-cli -h 148.230.112.148 ping &> /dev/null; then
        echo "   ✅ Redis: Accessible"
    else
        echo "   ❌ Redis: Non accessible (148.230.112.148:6379)"
    fi
else
    echo "   ⚠️  Redis: redis-cli non installé (vérification impossible)"
fi

echo ""

# ========================
# 2. Vérifier les services backend
# ========================
echo "🚀 2. Services Backend:"
echo ""

services=(
    "8080:Gateway"
    "8081:Identity Service"
    "8083:Property Service"
    "8084:Resource Service"
    "8085:Document Service"
    "8086:Workflow Service"
    "8087:Notification Service"
    "8088:Emailing Service"
    "8089:Audit Service"
    "8090:Billing Service"
)

all_services_running=true

for service_info in "${services[@]}"; do
    IFS=':' read -r port name <<< "$service_info"
    
    if curl -s -f -m 2 "http://localhost:$port/actuator/health" &> /dev/null; then
        echo "   ✅ $name (Port $port): Démarré"
    else
        echo "   ❌ $name (Port $port): Non démarré"
        all_services_running=false
    fi
done

echo ""

# ========================
# 3. Vérifier les JARs compilés
# ========================
echo "📦 3. JARs Compilés:"
echo ""

jar_files=(
    "gateway/target/gateway-*.jar:Gateway"
    "services/identity-service/target/identity-service-*.jar:Identity Service"
    "services/property-service/target/property-service-*.jar:Property Service"
    "services/resource-service/target/resource-service-*.jar:Resource Service"
    "services/document-service/target/document-service-*.jar:Document Service"
    "services/workflow-service/target/workflow-service-*.jar:Workflow Service"
    "services/notification-service/target/notification-service-*.jar:Notification Service"
    "services/emailing-service/target/emailing-service-*.jar:Emailing Service"
    "services/audit-service/target/audit-service-*.jar:Audit Service"
    "services/billing-service/target/billing-service-*.jar:Billing Service"
)

all_jars_exist=true

cd "$PROJECT_ROOT"
for jar_info in "${jar_files[@]}"; do
    IFS=':' read -r pattern name <<< "$jar_info"
    
    if ls $pattern &> /dev/null; then
        jar_file=$(ls $pattern | head -1)
        jar_size=$(du -h "$jar_file" | cut -f1)
        echo "   ✅ $name: $jar_size"
    else
        echo "   ❌ $name: Non compilé"
        all_jars_exist=false
    fi
done

echo ""

# ========================
# 4. Vérifier le frontend
# ========================
echo "🎨 4. Frontend:"
echo ""

frontend_projects=("admin" "agent" "public")

for project in "${frontend_projects[@]}"; do
    if [ -d "$PROJECT_ROOT/frontend/$project" ]; then
        if [ -f "$PROJECT_ROOT/frontend/$project/package.json" ]; then
            if [ -d "$PROJECT_ROOT/frontend/$project/node_modules" ]; then
                echo "   ✅ $project: Installé"
            else
                echo "   ⚠️  $project: node_modules manquant (npm install requis)"
            fi
        else
            echo "   ⚠️  $project: package.json manquant"
        fi
    else
        echo "   ❌ $project: Répertoire manquant"
    fi
done

echo ""

# ========================
# 5. Recommandations
# ========================
echo "💡 5. Actions Recommandées:"
echo ""

if [ "$all_jars_exist" = false ]; then
    echo "   🔨 Compiler tous les services:"
    echo "      ./scripts/build-and-start-all.sh"
    echo ""
fi

if [ "$all_services_running" = false ]; then
    echo "   🚀 Démarrer les services manquants:"
    echo "      ./scripts/start-missing-services.sh"
    echo "      OU"
    echo "      ./scripts/build-and-start-all.sh"
    echo ""
fi

echo "   📋 Vérifier l'état détaillé:"
echo "      ./scripts/check-services.sh"
echo ""
echo "   📊 Voir le statut global:"
echo "      ./scripts/status.sh"
echo ""

# ========================
# 6. Prochaines étapes
# ========================
echo "🎯 6. Prochaines Étapes:"
echo ""
echo "   1. Si services non démarrés → ./scripts/build-and-start-all.sh"
echo "   2. Tester l'authentification → curl http://localhost:8080/api/identity/auth/login"
echo "   3. Vérifier Swagger → http://localhost:8080/swagger-ui.html"
echo "   4. Finaliser frontend → Voir frontend/FRONTEND-NEXT-STEPS.md"
echo "   5. Déployer en production → ./scripts/deploy.sh prod"
echo ""

echo "📖 Documentation complète: LANCEMENT-PROJET-NEXT-STEPS.md"
echo ""

