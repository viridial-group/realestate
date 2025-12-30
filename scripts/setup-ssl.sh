#!/bin/bash

# ========================
# Script de Configuration SSL avec Let's Encrypt
# ========================
# Ce script configure les certificats SSL pour les sous-domaines

set -e

echo "🔒 Configuration SSL avec Let's Encrypt"

# ========================
# Vérification de Nginx
# ========================
if ! command -v nginx &> /dev/null; then
    echo "❌ Nginx n'est pas installé"
    exit 1
fi

# ========================
# Installation de Certbot
# ========================
if ! command -v certbot &> /dev/null; then
    echo "📦 Installation de Certbot..."
    apt-get update
    apt-get install -y certbot python3-certbot-nginx
    echo "✅ Certbot installé"
else
    echo "✅ Certbot déjà installé"
fi

# ========================
# Vérification de la configuration Nginx
# ========================
echo "🧪 Vérification de la configuration Nginx..."

if ! nginx -t; then
    echo "❌ Erreur dans la configuration Nginx. Corrigez les erreurs avant de continuer."
    exit 1
fi

echo "✅ Configuration Nginx valide"

# ========================
# Vérification des liens symboliques
# ========================
echo "🔍 Vérification des liens symboliques..."

if [ ! -L "/etc/nginx/sites-enabled/api.viridial.com.conf" ]; then
    echo "❌ Erreur: Le lien /etc/nginx/sites-enabled/api.viridial.com.conf n'existe pas"
    echo "💡 Exécutez d'abord: ./scripts/setup-nginx.sh"
    exit 1
fi

if [ ! -L "/etc/nginx/sites-enabled/app.viridial.com.conf" ]; then
    echo "❌ Erreur: Le lien /etc/nginx/sites-enabled/app.viridial.com.conf n'existe pas"
    echo "💡 Exécutez d'abord: ./scripts/setup-nginx.sh"
    exit 1
fi

echo "✅ Liens symboliques corrects"

# ========================
# Vérification DNS
# ========================
echo "🌐 Vérification DNS..."

API_IP=$(dig +short api.viridial.com @8.8.8.8 | grep -E '^[0-9]+\.[0-9]+\.[0-9]+\.[0-9]+$' | head -n1)
APP_IP=$(dig +short app.viridial.com @8.8.8.8 | grep -E '^[0-9]+\.[0-9]+\.[0-9]+\.[0-9]+$' | head -n1)
SERVER_IP=$(curl -s ifconfig.me 2>/dev/null || curl -s ifconfig.co 2>/dev/null || echo "UNKNOWN")

if [ -z "$API_IP" ] || [ -z "$APP_IP" ]; then
    echo "❌ Erreur: Les DNS ne sont pas configurés correctement"
    echo "   API IP: ${API_IP:-NON CONFIGURÉ}"
    echo "   APP IP: ${APP_IP:-NON CONFIGURÉ}"
    echo "   Server IP: $SERVER_IP"
    echo ""
    echo "📝 Instructions:"
    echo "1. Configurez les DNS pour api.viridial.com et app.viridial.com"
    echo "2. Les deux doivent pointer vers l'IP: $SERVER_IP"
    echo "3. Vérifiez avec: ./scripts/check-dns.sh"
    echo "4. Puis réessayez: ./scripts/setup-ssl.sh"
    echo ""
    echo "💡 Pour vérifier les DNS, exécutez:"
    echo "   ./scripts/check-dns.sh"
    exit 1
else
    echo "✅ DNS configurés"
    echo "   API IP: $API_IP"
    echo "   APP IP: $APP_IP"
    echo "   Server IP: $SERVER_IP"
    
    # Vérifier que les IP correspondent
    if [ "$API_IP" != "$SERVER_IP" ] || [ "$APP_IP" != "$SERVER_IP" ]; then
        echo "⚠️  Attention: Les IP DNS ne correspondent pas à l'IP du serveur"
        echo "   Vérifiez que les DNS pointent vers: $SERVER_IP"
        echo ""
        read -p "Continuer quand même? (y/n) " -n 1 -r
        echo
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            exit 1
        fi
    fi
fi

# ========================
# Obtention des certificats
# ========================
echo ""
echo "🔒 Obtention des certificats SSL..."

certbot --nginx \
    -d api.viridial.com \
    -d app.viridial.com \
    --non-interactive \
    --agree-tos \
    --email support@viridial.com \
    --redirect

if [ $? -eq 0 ]; then
    echo "✅ Certificats SSL obtenus avec succès"
else
    echo "❌ Erreur lors de l'obtention des certificats"
    exit 1
fi

# ========================
# Vérification du renouvellement automatique
# ========================
echo "🔄 Vérification du renouvellement automatique..."

if systemctl is-active --quiet certbot.timer; then
    echo "✅ Service de renouvellement automatique actif"
else
    echo "⚠️  Service de renouvellement automatique non actif"
    systemctl enable certbot.timer
    systemctl start certbot.timer
    echo "✅ Service de renouvellement activé"
fi

# ========================
# Test de la configuration finale
# ========================
echo "🧪 Test de la configuration finale..."

if nginx -t; then
    echo "✅ Configuration Nginx valide"
    systemctl reload nginx
    echo "✅ Nginx rechargé"
else
    echo "❌ Erreur dans la configuration Nginx"
    exit 1
fi

echo ""
echo "✅ Configuration SSL terminée avec succès!"
echo ""
echo "🌐 URLs disponibles:"
echo "   - https://api.viridial.com"
echo "   - https://app.viridial.com"
echo ""
echo "📝 Les certificats seront renouvelés automatiquement"

