#!/bin/bash

# ========================
# Script de Test du Gateway
# ========================
# Usage: ./test-gateway.sh

set -e

echo "🧪 Test du Gateway"
echo "=================="
echo ""

# Test 1: Health check local
echo "1️⃣  Test Health Check (localhost:8080)..."
if curl -s http://localhost:8080/actuator/health > /dev/null; then
    echo "✅ Health check réussi"
    curl -s http://localhost:8080/actuator/health | jq . 2>/dev/null || curl -s http://localhost:8080/actuator/health
else
    echo "❌ Health check échoué"
fi
echo ""

# Test 2: Test via Nginx (si DNS configuré)
echo "2️⃣  Test via Nginx (api.viridial.com)..."
if curl -s http://api.viridial.com/actuator/health > /dev/null; then
    echo "✅ Accès via Nginx réussi"
    curl -s http://api.viridial.com/actuator/health | jq . 2>/dev/null || curl -s http://api.viridial.com/actuator/health
else
    echo "⚠️  Accès via Nginx échoué (DNS peut-être non configuré)"
fi
echo ""

# Test 3: Test de la route fallback
echo "3️⃣  Test Route Fallback..."
response=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/test)
if [ "$response" = "503" ]; then
    echo "✅ Route fallback fonctionne (503 Service Unavailable)"
else
    echo "⚠️  Réponse inattendue: $response"
fi
echo ""

# Test 4: Vérification du port
echo "4️⃣  Vérification du port 8080..."
if netstat -tuln | grep -q ":8080"; then
    echo "✅ Port 8080 est en écoute"
    netstat -tuln | grep ":8080"
else
    echo "❌ Port 8080 n'est pas en écoute"
fi
echo ""

# Test 5: Statut du service
echo "5️⃣  Statut du service systemd..."
systemctl status realestate-gateway --no-pager -l | head -n 10
echo ""

echo "✅ Tests terminés!"

