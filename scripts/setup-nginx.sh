#!/bin/bash

# ========================
# Script de Configuration Nginx
# ========================
# Ce script configure Nginx avec les sous-domaines viridial.com

set -e

echo "🌐 Configuration de Nginx pour Viridial"

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

if [ ! -d "$NGINX_SITES_AVAILABLE" ]; then
    echo "❌ Erreur: Nginx n'est pas installé ou $NGINX_SITES_AVAILABLE n'existe pas"
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

if [ -f "$PROJECT_DIR/config/nginx/viridial.com.conf" ]; then
    cp "$PROJECT_DIR/config/nginx/viridial.com.conf" "$NGINX_SITES_AVAILABLE/"
    echo "✅ viridial.com.conf copié"
else
    echo "❌ Erreur: $PROJECT_DIR/config/nginx/viridial.com.conf n'existe pas"
    exit 1
fi

if [ -f "$PROJECT_DIR/config/nginx/admin.viridial.com.conf" ]; then
    cp "$PROJECT_DIR/config/nginx/admin.viridial.com.conf" "$NGINX_SITES_AVAILABLE/"
    echo "✅ admin.viridial.com.conf copié"
else
    echo "❌ Erreur: $PROJECT_DIR/config/nginx/admin.viridial.com.conf n'existe pas"
    exit 1
fi

# ========================
# Suppression des anciens liens (si existants)
# ========================
echo "🧹 Nettoyage des anciens liens..."

if [ -L "$NGINX_SITES_ENABLED/api.viridial.com" ]; then
    rm "$NGINX_SITES_ENABLED/api.viridial.com"
    echo "✅ Ancien lien api.viridial.com supprimé"
fi

if [ -L "$NGINX_SITES_ENABLED/app.viridial.com" ]; then
    rm "$NGINX_SITES_ENABLED/app.viridial.com"
    echo "✅ Ancien lien app.viridial.com supprimé"
fi

if [ -L "$NGINX_SITES_ENABLED/api.viridial.com.conf" ]; then
    rm "$NGINX_SITES_ENABLED/api.viridial.com.conf"
    echo "✅ Ancien lien api.viridial.com.conf supprimé"
fi

if [ -L "$NGINX_SITES_ENABLED/app.viridial.com.conf" ]; then
    rm "$NGINX_SITES_ENABLED/app.viridial.com.conf"
    echo "✅ Ancien lien app.viridial.com.conf supprimé"
fi

if [ -L "$NGINX_SITES_ENABLED/viridial.com" ]; then
    rm "$NGINX_SITES_ENABLED/viridial.com"
    echo "✅ Ancien lien viridial.com supprimé"
fi

if [ -L "$NGINX_SITES_ENABLED/viridial.com.conf" ]; then
    rm "$NGINX_SITES_ENABLED/viridial.com.conf"
    echo "✅ Ancien lien viridial.com.conf supprimé"
fi

if [ -L "$NGINX_SITES_ENABLED/admin.viridial.com" ]; then
    rm "$NGINX_SITES_ENABLED/admin.viridial.com"
    echo "✅ Ancien lien admin.viridial.com supprimé"
fi

if [ -L "$NGINX_SITES_ENABLED/admin.viridial.com.conf" ]; then
    rm "$NGINX_SITES_ENABLED/admin.viridial.com.conf"
    echo "✅ Ancien lien admin.viridial.com.conf supprimé"
fi

# ========================
# Création des liens symboliques (avec .conf)
# ========================
echo "🔗 Création des liens symboliques..."

if [ -f "$NGINX_SITES_AVAILABLE/api.viridial.com.conf" ]; then
    ln -s "$NGINX_SITES_AVAILABLE/api.viridial.com.conf" "$NGINX_SITES_ENABLED/api.viridial.com.conf"
    echo "✅ Lien api.viridial.com.conf créé"
else
    echo "❌ Erreur: $NGINX_SITES_AVAILABLE/api.viridial.com.conf n'existe pas"
    exit 1
fi

if [ -f "$NGINX_SITES_AVAILABLE/app.viridial.com.conf" ]; then
    ln -s "$NGINX_SITES_AVAILABLE/app.viridial.com.conf" "$NGINX_SITES_ENABLED/app.viridial.com.conf"
    echo "✅ Lien app.viridial.com.conf créé"
else
    echo "❌ Erreur: $NGINX_SITES_AVAILABLE/app.viridial.com.conf n'existe pas"
    exit 1
fi

if [ -f "$NGINX_SITES_AVAILABLE/viridial.com.conf" ]; then
    ln -s "$NGINX_SITES_AVAILABLE/viridial.com.conf" "$NGINX_SITES_ENABLED/viridial.com.conf"
    echo "✅ Lien viridial.com.conf créé"
else
    echo "❌ Erreur: $NGINX_SITES_AVAILABLE/viridial.com.conf n'existe pas"
    exit 1
fi

if [ -f "$NGINX_SITES_AVAILABLE/admin.viridial.com.conf" ]; then
    ln -s "$NGINX_SITES_AVAILABLE/admin.viridial.com.conf" "$NGINX_SITES_ENABLED/admin.viridial.com.conf"
    echo "✅ Lien admin.viridial.com.conf créé"
else
    echo "❌ Erreur: $NGINX_SITES_AVAILABLE/admin.viridial.com.conf n'existe pas"
    exit 1
fi

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
# Vérification des liens
# ========================
echo ""
echo "📋 Vérification des liens créés:"
ls -la "$NGINX_SITES_ENABLED" | grep viridial

echo ""
echo "✅ Configuration Nginx terminée avec succès!"
echo ""
echo "📝 Prochaines étapes:"
echo "1. Vérifier que les DNS pointent vers ce serveur (utiliser: ./scripts/check-dns.sh)"
echo "2. Configurer les enregistrements DNS A pour:"
echo "   - viridial.com et www.viridial.com"
echo "   - api.viridial.com"
echo "   - app.viridial.com"
echo "   - admin.viridial.com et www.admin.viridial.com"
echo "3. Attendre la propagation DNS (quelques minutes à quelques heures)"
echo "4. Exécuter: certbot --nginx -d viridial.com -d www.viridial.com -d api.viridial.com -d app.viridial.com -d admin.viridial.com -d www.admin.viridial.com"
echo "5. Vérifier que les services Spring Boot sont démarrés sur les ports 8080, 8081, etc."
echo "6. Construire le frontend public: cd frontend/public && npm run build"
echo "7. Copier les fichiers build vers /var/www/viridial-public/dist"
echo "8. Construire le frontend admin: cd frontend/admin && npm run build"
echo "9. Copier les fichiers build vers /var/www/viridial-admin/dist (ou utiliser: ./scripts/deploy-admin-frontend.sh)"

