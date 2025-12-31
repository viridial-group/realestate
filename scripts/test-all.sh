#!/bin/bash

# ========================
# Script de Test Automatisé
# ========================
# Teste tous les services de la plateforme

set -e

echo "🧪 Tests de la Plateforme Real Estate"
echo ""

# Couleurs
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Fonction pour tester un endpoint
test_endpoint() {
    local method=$1
    local url=$2
    local headers=$3
    local data=$4
    local description=$5
    
    echo -n "   Testing: $description... "
    
    if [ -n "$data" ]; then
        response=$(curl -s -w "\n%{http_code}" -X $method "$url" \
            -H "Content-Type: application/json" \
            $headers \
            -d "$data" 2>/dev/null)
    else
        response=$(curl -s -w "\n%{http_code}" -X $method "$url" \
            $headers 2>/dev/null)
    fi
    
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | sed '$d')
    
    if [ "$http_code" -ge 200 ] && [ "$http_code" -lt 300 ]; then
        echo -e "${GREEN}✅ OK (HTTP $http_code)${NC}"
        return 0
    else
        echo -e "${RED}❌ FAILED (HTTP $http_code)${NC}"
        echo "   Response: $body"
        return 1
    fi
}

# ========================
# 1. Health Checks
# ========================
echo "1. 📊 Vérification des health checks..."
echo ""

services=(
    "8081:Identity Service"
    "8082:Organization Service"
    "8083:Property Service"
    "8084:Resource Service"
    "8085:Document Service"
    "8086:Workflow Service"
    "8087:Notification Service"
    "8088:Emailing Service"
    "8089:Audit Service"
    "8090:Billing Service"
)

for service in "${services[@]}"; do
    port=$(echo $service | cut -d: -f1)
    name=$(echo $service | cut -d: -f2)
    
    echo -n "   $name (port $port)... "
    status=$(curl -s http://localhost:$port/actuator/health 2>/dev/null | grep -o '"status":"[^"]*"' | cut -d'"' -f4 || echo "DOWN")
    
    if [ "$status" = "UP" ]; then
        echo -e "${GREEN}✅ UP${NC}"
    else
        echo -e "${RED}❌ DOWN${NC}"
    fi
done

echo ""

# ========================
# 2. Test d'inscription
# ========================
echo "2. 👤 Test d'inscription..."

# Générer un email unique
TEST_EMAIL="test$(date +%s)@example.com"

echo -n "   Testing: Register new user ($TEST_EMAIL)... "

response=$(curl -s -w "\n%{http_code}" -X POST "http://localhost:8081/api/identity/auth/register" \
    -H "Content-Type: application/json" \
    -d "{\"email\":\"$TEST_EMAIL\",\"password\":\"Test123!\",\"firstName\":\"Test\",\"lastName\":\"User\"}" 2>/dev/null)

http_code=$(echo "$response" | tail -n1)
body=$(echo "$response" | sed '$d')

if [ "$http_code" -ge 200 ] && [ "$http_code" -lt 300 ]; then
    echo -e "${GREEN}✅ OK (HTTP $http_code)${NC}"
elif [ "$http_code" -eq 400 ]; then
    echo -e "${YELLOW}⚠️  BAD REQUEST (HTTP $http_code)${NC}"
    echo "   Response: $body"
    echo "   💡 Vérifiez les logs pour plus de détails"
else
    echo -e "${RED}❌ FAILED (HTTP $http_code)${NC}"
    echo "   Response: $body"
fi

echo ""

# ========================
# 3. Test de login
# ========================
echo "3. 🔐 Test de login..."
echo -n "   Testing: Login... "

login_response=$(curl -s -X POST http://localhost:8081/api/identity/auth/login \
    -H "Content-Type: application/json" \
    -d '{"email":"admin@viridial.com","password":"admin123"}' 2>/dev/null)

token=$(echo $login_response | grep -o '"token":"[^"]*"' | cut -d'"' -f4 || echo "")

if [ -n "$token" ] && [ "$token" != "null" ] && [ -n "$token" ]; then
    echo -e "${GREEN}✅ OK${NC}"
    export JWT_TOKEN=$token
    echo "   Token obtenu: ${token:0:20}..."
else
    echo -e "${YELLOW}⚠️  Login avec admin@viridial.com échoué, tentative avec test@example.com...${NC}"
    # Essayer avec un utilisateur de test
    login_response=$(curl -s -X POST http://localhost:8081/api/identity/auth/login \
        -H "Content-Type: application/json" \
        -d '{"email":"test@example.com","password":"Test123!"}' 2>/dev/null)
    token=$(echo $login_response | grep -o '"token":"[^"]*"' | cut -d'"' -f4 || echo "")
    if [ -n "$token" ]; then
        export JWT_TOKEN=$token
        echo -e "${GREEN}✅ OK${NC}"
    else
        echo -e "${RED}❌ FAILED${NC}"
        echo "   ⚠️  Continuer sans token (certains tests échoueront)"
    fi
fi

echo ""

# ========================
# 4. Test de création de propriété
# ========================
if [ -n "$JWT_TOKEN" ]; then
    echo "4. 🏠 Test de création de propriété..."
    test_endpoint "POST" "http://localhost:8083/api/properties" \
        "-H \"Authorization: Bearer $JWT_TOKEN\"" \
        '{"reference":"PROP-TEST-'$(date +%s)'","title":"Appartement Test","type":"APARTMENT","status":"DRAFT","price":100000,"organizationId":1,"createdBy":1}' \
        "Create property"
    echo ""
fi

# ========================
# 5. Vérification Elasticsearch
# ========================
echo "5. 🔍 Vérification Elasticsearch..."
echo -n "   Elasticsearch accessible... "

es_response=$(curl -s http://localhost:9200 2>/dev/null || echo "")
if [ -n "$es_response" ]; then
    echo -e "${GREEN}✅ OK${NC}"
    
    # Compter les indexes
    index_count=$(curl -s "http://localhost:9200/_cat/indices?v" 2>/dev/null | grep -c "properties" || echo "0")
    echo "   📊 Indexes 'properties': $index_count"
else
    echo -e "${RED}❌ DOWN${NC}"
fi

echo ""

# ========================
# 6. Vérification Kafka
# ========================
echo "6. 📨 Vérification Kafka..."
if docker ps | grep -q "kafka\|realestate-kafka"; then
    echo -e "   ${GREEN}✅ Kafka est en cours d'exécution${NC}"
else
    echo -e "   ${YELLOW}⚠️  Kafka n'est pas en cours d'exécution${NC}"
fi

echo ""

# ========================
# Résumé
# ========================
echo "✅ Tests terminés"
echo ""
echo "📋 Prochaines étapes :"
echo "   1. Accéder à Swagger: http://localhost:8081/swagger-ui.html"
echo "   2. Tester les endpoints manuellement"
echo "   3. Vérifier les logs: tail -f logs/*.log"
echo "   4. Consulter le guide complet: TESTING-GUIDE.md"

