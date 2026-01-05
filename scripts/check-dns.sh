#!/bin/bash

# ========================
# Script de Vérification DNS
# ========================
# Ce script vérifie la configuration DNS pour les domaines Viridial

set -e

echo "🔍 Vérification de la configuration DNS pour Viridial"
echo ""

DOMAINS=(
    "viridial.com"
    "www.viridial.com"
    "api.viridial.com"
    "app.viridial.com"
    "admin.viridial.com"
    "www.admin.viridial.com"
)

# Obtenir l'IP du serveur actuel
CURRENT_IP=$(curl -s ifconfig.me || curl -s ipinfo.io/ip || echo "N/A")

echo "📍 IP du serveur actuel: $CURRENT_IP"
echo ""

for domain in "${DOMAINS[@]}"; do
    echo "🔍 Vérification de $domain..."
    
    # Résolution DNS
    DNS_IP=$(dig +short $domain @8.8.8.8 2>/dev/null | head -n 1 || echo "N/A")
    
    if [ "$DNS_IP" = "N/A" ] || [ -z "$DNS_IP" ]; then
        echo "   ❌ DNS non configuré ou domaine introuvable"
    elif [ "$DNS_IP" = "$CURRENT_IP" ]; then
        echo "   ✅ DNS configuré correctement (pointe vers $DNS_IP)"
    else
        echo "   ⚠️  DNS pointe vers $DNS_IP (attendu: $CURRENT_IP)"
    fi
    
    # Vérification HTTP
    HTTP_STATUS=$(curl -s -o /dev/null -w "%{http_code}" --max-time 5 "http://$domain" 2>/dev/null || echo "000")
    
    if [ "$HTTP_STATUS" = "000" ]; then
        echo "   ⚠️  HTTP: Site inaccessible ou timeout"
    elif [ "$HTTP_STATUS" = "200" ] || [ "$HTTP_STATUS" = "301" ] || [ "$HTTP_STATUS" = "302" ]; then
        echo "   ✅ HTTP: Site accessible (status: $HTTP_STATUS)"
    else
        echo "   ⚠️  HTTP: Status $HTTP_STATUS"
    fi
    
    echo ""
done

echo "📝 Instructions pour configurer le DNS:"
echo ""
echo "1. Connectez-vous à votre fournisseur de domaine (registrar)"
echo "2. Ajoutez les enregistrements A suivants:"
echo ""
echo "   Type  | Name              | Value"
echo "   ------|-------------------|-------------------"
echo "   A     | @                 | $CURRENT_IP"
echo "   A     | www               | $CURRENT_IP"
echo "   A     | api               | $CURRENT_IP"
echo "   A     | app               | $CURRENT_IP"
echo "   A     | admin             | $CURRENT_IP"
echo "   A     | www.admin         | $CURRENT_IP"
echo ""
echo "3. Attendez la propagation DNS (peut prendre jusqu'à 48h, généralement quelques minutes)"
echo "4. Vérifiez avec: dig +short admin.viridial.com"
echo ""
echo "💡 Note: Si vous utilisez Cloudflare ou un autre CDN, configurez-le pour pointer vers $CURRENT_IP"
