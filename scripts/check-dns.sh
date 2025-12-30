#!/bin/bash

# ========================
# Script de Vérification DNS
# ========================
# Ce script vérifie que les DNS sont correctement configurés

set -e

echo "🌐 Vérification de la configuration DNS"

# ========================
# Variables
# ========================
DOMAINS=("api.viridial.com" "app.viridial.com")
SERVER_IP=$(curl -s ifconfig.me 2>/dev/null || curl -s ifconfig.co 2>/dev/null || echo "UNKNOWN")

echo "📍 IP du serveur: $SERVER_IP"
echo ""

# ========================
# Vérification de chaque domaine
# ========================
ALL_OK=true

for domain in "${DOMAINS[@]}"; do
    echo "🔍 Vérification de $domain..."
    
    # Résolution DNS
    DNS_IP=$(dig +short $domain @8.8.8.8 | grep -E '^[0-9]+\.[0-9]+\.[0-9]+\.[0-9]+$' | head -n1)
    DNS_IPV6=$(dig +short $domain AAAA @8.8.8.8 | head -n1)
    
    if [ -z "$DNS_IP" ]; then
        echo "   ❌ Aucun enregistrement A trouvé pour $domain"
        echo "   💡 Action requise: Créer un enregistrement DNS A pointant vers $SERVER_IP"
        ALL_OK=false
    else
        echo "   ✅ Enregistrement A trouvé: $DNS_IP"
        
        if [ "$DNS_IP" != "$SERVER_IP" ]; then
            echo "   ⚠️  Attention: L'IP DNS ($DNS_IP) ne correspond pas à l'IP du serveur ($SERVER_IP)"
            echo "   💡 Vérifiez que l'enregistrement DNS pointe vers la bonne IP"
        else
            echo "   ✅ L'IP DNS correspond à l'IP du serveur"
        fi
    fi
    
    if [ -n "$DNS_IPV6" ]; then
        echo "   ℹ️  Enregistrement AAAA trouvé: $DNS_IPV6"
    fi
    
    echo ""
done

# ========================
# Résumé
# ========================
if [ "$ALL_OK" = true ]; then
    echo "✅ Tous les DNS sont correctement configurés!"
    echo ""
    echo "📝 Vous pouvez maintenant exécuter:"
    echo "   ./scripts/setup-ssl.sh"
else
    echo "❌ Certains DNS ne sont pas configurés correctement"
    echo ""
    echo "📝 Instructions pour configurer les DNS:"
    echo ""
    echo "1. Connectez-vous à votre panneau de gestion DNS (hébergeur de domaine)"
    echo "2. Créez les enregistrements suivants:"
    echo ""
    for domain in "${DOMAINS[@]}"; do
        echo "   Type: A"
        echo "   Nom: $domain"
        echo "   Valeur: $SERVER_IP"
        echo "   TTL: 3600 (ou par défaut)"
        echo ""
    done
    echo "3. Attendez la propagation DNS (5-30 minutes)"
    echo "4. Vérifiez avec: ./scripts/check-dns.sh"
    echo "5. Puis exécutez: ./scripts/setup-ssl.sh"
    exit 1
fi

