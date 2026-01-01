#!/bin/bash

# ========================
# Script de Statut Global
# ========================
# Affiche le statut de tous les services et composants

set -e

echo "📊 Statut Global - Real Estate Platform"
echo "========================================"
echo ""

# ========================
# Services Système
# ========================
echo "📦 Services Système:"
echo ""

services=("postgresql" "redis-server" "nginx" "elasticsearch")
for service in "${services[@]}"; do
    if systemctl is-active --quiet $service; then
        echo "  ✅ $service: ACTIF"
    else
        echo "  ❌ $service: INACTIF"
    fi
done

echo ""

# ========================
# Microservices
# ========================
echo "🚀 Microservices:"
echo ""

microservices=("realestate-gateway" "realestate-identity-service" "realestate-property-service")
for service in "${microservices[@]}"; do
    if systemctl is-active --quiet $service 2>/dev/null; then
        echo "  ✅ $service: ACTIF"
    else
        echo "  ❌ $service: INACTIF"
    fi
done

echo ""

# ========================
# Ports
# ========================
echo "🔌 Ports:"
echo ""

ports=(8080 8081 8083)
for port in "${ports[@]}"; do
    if netstat -tuln 2>/dev/null | grep -q ":$port " || ss -tuln 2>/dev/null | grep -q ":$port "; then
        echo "  ✅ Port $port: OUVERT"
    else
        echo "  ❌ Port $port: FERMÉ"
    fi
done

echo ""

# ========================
# DNS
# ========================
echo "🌐 DNS:"
echo ""

domains=("api.viridial.com" "app.viridial.com")
for domain in "${domains[@]}"; do
    ip=$(dig +short $domain @8.8.8.8 2>/dev/null | grep -E '^[0-9]+\.[0-9]+\.[0-9]+\.[0-9]+$' | head -n1)
    if [ -n "$ip" ]; then
        echo "  ✅ $domain: $ip"
    else
        echo "  ❌ $domain: NON CONFIGURÉ"
    fi
done

echo ""

# ========================
# URLs
# ========================
echo "🔗 URLs:"
echo ""

urls=("http://api.viridial.com/actuator/health" "http://app.viridial.com")
for url in "${urls[@]}"; do
    if curl -s -f -m 2 "$url" > /dev/null 2>&1; then
        echo "  ✅ $url: ACCESSIBLE"
    else
        echo "  ❌ $url: INACCESSIBLE"
    fi
done

echo ""
echo "========================================"

