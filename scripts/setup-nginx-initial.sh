#!/bin/bash

# ========================
# Script de Configuration Nginx Initiale (HTTP seulement)
# ========================
# Ce script configure Nginx en HTTP d'abord, puis certbot ajoutera HTTPS

set -e

echo "🌐 Configuration initiale de Nginx (HTTP)"

# ========================
# Variables
# ========================
NGINX_SITES_AVAILABLE=/etc/nginx/sites-available
NGINX_SITES_ENABLED=/etc/nginx/sites-enabled
PROJECT_DIR=/opt/source/realestate

# ========================
# Vérification
# ========================
if [ ! -d "$PROJECT_DIR" ]; then
    echo "❌ Erreur: Le répertoire $PROJECT_DIR n'existe pas"
    exit 1
fi

# ========================
# Copie des configurations
# ========================
echo "📋 Copie des configurations Nginx..."

if [ -f "$PROJECT_DIR/config/nginx/api.viridial.com.conf" ]; then
    cp "$PROJECT_DIR/config/nginx/api.viridial.com.conf" "$NGINX_SITES_AVAILABLE/"
    echo "✅ api.viridial.com.conf copié"
else
    echo "❌ Erreur: $PROJECT_DIR/config/nginx/api.viridial.com.conf n'existe pas"
    exit 1
fi

if [ -f "$PROJECT_DIR/config/nginx/app.viridial.com.conf" ]; then
    cp "$PROJECT_DIR/config/nginx/app.viridial.com.conf" "$NGINX_SITES_AVAILABLE/"
    echo "✅ app.viridial.com.conf copié"
else
    echo "❌ Erreur: $PROJECT_DIR/config/nginx/app.viridial.com.conf n'existe pas"
    exit 1
fi

# ========================
# Suppression des anciens liens
# ========================
echo "🧹 Nettoyage des anciens liens..."

rm -f "$NGINX_SITES_ENABLED/api.viridial.com"
rm -f "$NGINX_SITES_ENABLED/app.viridial.com"
rm -f "$NGINX_SITES_ENABLED/api.viridial.com.conf"
rm -f "$NGINX_SITES_ENABLED/app.viridial.com.conf"

# ========================
# Création des liens symboliques
# ========================
echo "🔗 Création des liens symboliques..."

ln -s "$NGINX_SITES_AVAILABLE/api.viridial.com.conf" "$NGINX_SITES_ENABLED/api.viridial.com.conf"
echo "✅ Lien api.viridial.com.conf créé"

ln -s "$NGINX_SITES_AVAILABLE/app.viridial.com.conf" "$NGINX_SITES_ENABLED/app.viridial.com.conf"
echo "✅ Lien app.viridial.com.conf créé"

# ========================
# Test de la configuration
# ========================
echo "🧪 Test de la configuration Nginx..."

if nginx -t; then
    echo "✅ Configuration Nginx valide"
else
    echo "❌ Erreur dans la configuration Nginx"
    exit 1
fi

# ========================
# Rechargement de Nginx
# ========================
echo "🔄 Rechargement de Nginx..."

if systemctl reload nginx; then
    echo "✅ Nginx rechargé avec succès"
else
    echo "❌ Erreur lors du rechargement de Nginx"
    exit 1
fi

# ========================
# Vérification
# ========================
echo ""
echo "📋 Vérification des liens créés:"
ls -la "$NGINX_SITES_ENABLED" | grep viridial

echo ""
echo "✅ Configuration Nginx initiale terminée!"
echo ""
echo "📝 Prochaines étapes:"
echo "1. Vérifier que les DNS pointent vers ce serveur"
echo "2. Vérifier que les services Spring Boot sont démarrés"
echo "3. Exécuter: ./scripts/setup-ssl.sh pour configurer HTTPS"

