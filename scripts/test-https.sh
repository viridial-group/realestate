#!/bin/bash

# ========================
# Script de Test HTTPS
# ========================
# Usage: ./test-https.sh

set -e

echo "🔒 Test HTTPS"
echo "============="
echo ""

# Test 1: HTTPS Health Check
echo "1️⃣  Test HTTPS Health Check..."
if curl -s https://api.viridial.com/actuator/health > /dev/null; then
    echo "✅ HTTPS fonctionne!"
    echo ""
    echo "📋 Réponse:"
    curl -s https://api.viridial.com/actuator/health
    echo ""
else
    echo "❌ HTTPS ne fonctionne pas"
fi
echo ""

# Test 2: Vérification du certificat
echo "2️⃣  Vérification du certificat SSL..."
if echo | openssl s_client -connect api.viridial.com:443 -servername api.viridial.com 2>/dev/null | grep -q "Verify return code: 0"; then
    echo "✅ Certificat SSL valide"
else
    echo "⚠️  Vérification du certificat..."
    echo | openssl s_client -connect api.viridial.com:443 -servername api.viridial.com 2>/dev/null | grep -A 5 "Certificate chain"
fi
echo ""

# Test 3: Redirection HTTP -> HTTPS
echo "3️⃣  Test redirection HTTP -> HTTPS..."
response=$(curl -s -o /dev/null -w "%{http_code}" -L http://api.viridial.com/actuator/health)
if [ "$response" = "200" ]; then
    echo "✅ Redirection HTTP -> HTTPS fonctionne (code: $response)"
else
    echo "⚠️  Code de réponse: $response"
fi
echo ""

# Test 4: Port 443
echo "4️⃣  Vérification du port 443..."
if netstat -tuln 2>/dev/null | grep -q ":443 " || ss -tuln 2>/dev/null | grep -q ":443 "; then
    echo "✅ Port 443 est ouvert"
    netstat -tuln 2>/dev/null | grep ":443 " || ss -tuln 2>/dev/null | grep ":443 "
else
    echo "❌ Port 443 n'est pas ouvert"
fi
echo ""

echo "============="
echo "✅ Tests terminés!"
echo ""
echo "🌐 URLs disponibles:"
echo "   - https://api.viridial.com/actuator/health"
echo "   - https://app.viridial.com"
echo ""
echo "📝 Note: HTTP redirige automatiquement vers HTTPS (301)"

