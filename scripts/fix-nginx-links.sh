#!/bin/bash

# ========================
# Script de Correction des Liens Nginx
# ========================
# Ce script corrige les liens symboliques Nginx qui pointent vers les mauvais fichiers

set -e

echo "🔧 Correction des liens Nginx"

NGINX_SITES_AVAILABLE=/etc/nginx/sites-available
NGINX_SITES_ENABLED=/etc/nginx/sites-enabled

# ========================
# Suppression des liens incorrects
# ========================
echo "🧹 Suppression des liens incorrects..."

# Supprimer les liens sans .conf
if [ -L "$NGINX_SITES_ENABLED/api.viridial.com" ]; then
    rm "$NGINX_SITES_ENABLED/api.viridial.com"
    echo "✅ Lien incorrect api.viridial.com supprimé"
fi

if [ -L "$NGINX_SITES_ENABLED/app.viridial.com" ]; then
    rm "$NGINX_SITES_ENABLED/app.viridial.com"
    echo "✅ Lien incorrect app.viridial.com supprimé"
fi

# ========================
# Création des liens corrects (avec .conf)
# ========================
echo "🔗 Création des liens corrects..."

if [ -f "$NGINX_SITES_AVAILABLE/api.viridial.com.conf" ]; then
    if [ ! -L "$NGINX_SITES_ENABLED/api.viridial.com.conf" ]; then
        ln -s "$NGINX_SITES_AVAILABLE/api.viridial.com.conf" "$NGINX_SITES_ENABLED/api.viridial.com.conf"
        echo "✅ Lien api.viridial.com.conf créé"
    else
        echo "✅ Lien api.viridial.com.conf existe déjà"
    fi
else
    echo "❌ Erreur: $NGINX_SITES_AVAILABLE/api.viridial.com.conf n'existe pas"
    exit 1
fi

if [ -f "$NGINX_SITES_AVAILABLE/app.viridial.com.conf" ]; then
    if [ ! -L "$NGINX_SITES_ENABLED/app.viridial.com.conf" ]; then
        ln -s "$NGINX_SITES_AVAILABLE/app.viridial.com.conf" "$NGINX_SITES_ENABLED/app.viridial.com.conf"
        echo "✅ Lien app.viridial.com.conf créé"
    else
        echo "✅ Lien app.viridial.com.conf existe déjà"
    fi
else
    echo "❌ Erreur: $NGINX_SITES_AVAILABLE/app.viridial.com.conf n'existe pas"
    exit 1
fi

# ========================
# Test de la configuration
# ========================
echo "🧪 Test de la configuration Nginx..."

if nginx -t; then
    echo "✅ Configuration Nginx valide"
    echo ""
    echo "📋 Liens créés:"
    ls -la "$NGINX_SITES_ENABLED" | grep viridial
    echo ""
    echo "✅ Correction terminée! Vous pouvez maintenant exécuter certbot."
else
    echo "❌ Erreur dans la configuration Nginx"
    exit 1
fi

