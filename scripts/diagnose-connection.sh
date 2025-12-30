#!/bin/bash

# ========================
# Script de Diagnostic de Connexion
# ========================
# Usage: ./diagnose-connection.sh

set -e

echo "🔍 Diagnostic de Connexion"
echo "=========================="
echo ""

# Test 1: Gateway local
echo "1️⃣  Test Gateway local (localhost:8080)..."
if curl -s http://localhost:8080/actuator/health > /dev/null; then
    echo "✅ Gateway répond sur localhost:8080"
    curl -s http://localhost:8080/actuator/health | head -n 5
else
    echo "❌ Gateway ne répond pas sur localhost:8080"
    echo "   Vérifiez: systemctl status realestate-gateway"
fi
echo ""

# Test 2: Nginx HTTP (port 80)
echo "2️⃣  Test Nginx HTTP (api.viridial.com:80)..."
if curl -s http://api.viridial.com/actuator/health > /dev/null; then
    echo "✅ Nginx HTTP fonctionne"
    curl -s http://api.viridial.com/actuator/health | head -n 5
else
    echo "❌ Nginx HTTP ne répond pas"
    echo "   Vérifiez: systemctl status nginx"
    echo "   Vérifiez: nginx -t"
fi
echo ""

# Test 3: Nginx HTTPS (port 443)
echo "3️⃣  Test Nginx HTTPS (api.viridial.com:443)..."
if curl -s -k https://api.viridial.com/actuator/health > /dev/null 2>&1; then
    echo "✅ Nginx HTTPS fonctionne"
    curl -s -k https://api.viridial.com/actuator/health | head -n 5
else
    echo "⚠️  Nginx HTTPS ne répond pas (SSL non configuré)"
    echo "   Solution: Exécutez ./scripts/setup-ssl.sh"
fi
echo ""

# Test 4: Ports ouverts
echo "4️⃣  Vérification des ports..."
if netstat -tuln 2>/dev/null | grep -q ":80 "; then
    echo "✅ Port 80 (HTTP) est ouvert"
else
    echo "❌ Port 80 (HTTP) n'est pas ouvert"
fi

if netstat -tuln 2>/dev/null | grep -q ":443 "; then
    echo "✅ Port 443 (HTTPS) est ouvert"
else
    echo "⚠️  Port 443 (HTTPS) n'est pas ouvert (SSL non configuré)"
fi
echo ""

# Test 5: DNS
echo "5️⃣  Vérification DNS..."
API_IP=$(dig +short api.viridial.com @8.8.8.8 2>/dev/null | grep -E '^[0-9]+\.[0-9]+\.[0-9]+\.[0-9]+$' | head -n1)
SERVER_IP=$(curl -s ifconfig.me 2>/dev/null || curl -s ifconfig.co 2>/dev/null || echo "UNKNOWN")

if [ -n "$API_IP" ]; then
    echo "✅ DNS api.viridial.com résolu: $API_IP"
    if [ "$API_IP" = "$SERVER_IP" ]; then
        echo "✅ DNS pointe vers le bon serveur"
    else
        echo "⚠️  DNS pointe vers $API_IP mais le serveur est $SERVER_IP"
    fi
else
    echo "❌ DNS api.viridial.com non résolu"
fi
echo ""

# Test 6: Firewall
echo "6️⃣  Vérification Firewall..."
if command -v ufw &> /dev/null; then
    if ufw status | grep -q "Status: active"; then
        echo "⚠️  UFW est actif"
        echo "   Vérifiez que les ports 80 et 443 sont ouverts:"
        echo "   ufw status | grep -E '(80|443)'"
    else
        echo "✅ UFW n'est pas actif (ou firewall désactivé)"
    fi
else
    echo "ℹ️  UFW non installé"
fi
echo ""

# Résumé
echo "=========================="
echo "📋 Résumé:"
echo ""
echo "✅ Pour tester maintenant (HTTP):"
echo "   curl http://api.viridial.com/actuator/health"
echo ""
echo "🔒 Pour activer HTTPS:"
echo "   1. Vérifiez les DNS: ./scripts/check-dns.sh"
echo "   2. Configurez SSL: ./scripts/setup-ssl.sh"
echo ""
echo "🌐 URLs disponibles:"
echo "   - http://api.viridial.com (fonctionne maintenant)"
echo "   - https://api.viridial.com (nécessite SSL)"
echo ""

