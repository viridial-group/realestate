#!/bin/bash

# ========================
# Script de Vérification des Logs Gateway
# ========================
# Usage: ./check-gateway-logs.sh

echo "📋 Logs du Gateway"
echo "=================="
echo ""

# Statut du service
echo "1️⃣  Statut du service:"
systemctl status realestate-gateway --no-pager -l | head -n 15
echo ""

# Derniers logs
echo "2️⃣  Derniers logs (50 lignes):"
journalctl -u realestate-gateway -n 50 --no-pager
echo ""

# Erreurs récentes
echo "3️⃣  Erreurs récentes:"
journalctl -u realestate-gateway --since "5 minutes ago" --no-pager | grep -i error || echo "Aucune erreur récente"
echo ""

# Test de connexion
echo "4️⃣  Test de connexion:"
if curl -s http://localhost:8080/actuator/health > /dev/null; then
    echo "✅ Gateway répond"
    curl -s http://localhost:8080/actuator/health
else
    echo "❌ Gateway ne répond pas"
fi
echo ""

