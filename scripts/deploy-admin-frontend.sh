#!/bin/bash

# ========================
# Script de Déploiement Frontend Admin
# ========================
# Ce script construit et déploie le frontend admin sur le serveur

set -e

echo "🚀 Déploiement du Frontend Admin"

# ========================
# Variables
# ========================
PROJECT_DIR=/opt/source/realestate
ADMIN_DIR="$PROJECT_DIR/frontend/admin"
DEPLOY_DIR=/var/www/viridial-admin
DEPLOY_DIST_DIR="$DEPLOY_DIR/dist"

# ========================
# Vérification
# ========================
if [ ! -d "$PROJECT_DIR" ]; then
    echo "❌ Erreur: Le répertoire $PROJECT_DIR n'existe pas"
    exit 1
fi

if [ ! -d "$ADMIN_DIR" ]; then
    echo "❌ Erreur: Le répertoire $ADMIN_DIR n'existe pas"
    exit 1
fi

# ========================
# Création du répertoire de déploiement
# ========================
echo "📁 Création du répertoire de déploiement..."
sudo mkdir -p "$DEPLOY_DIST_DIR"
echo "✅ Répertoire $DEPLOY_DIST_DIR créé"

# ========================
# Installation des dépendances
# ========================
echo "📦 Installation des dépendances..."
cd "$ADMIN_DIR"

if [ ! -d "node_modules" ]; then
    echo "   Installation de npm install..."
    npm install
else
    echo "   Dépendances déjà installées, mise à jour..."
    #npm install
fi

# ========================
# Build du frontend
# ========================
echo "🔨 Construction du frontend admin..."
npm run build

if [ ! -d "dist" ]; then
    echo "❌ Erreur: Le répertoire dist n'a pas été créé après le build"
    exit 1
fi

echo "✅ Build terminé avec succès"

# ========================
# Sauvegarde de l'ancienne version (si elle existe)
# ========================
if [ -d "$DEPLOY_DIST_DIR" ] && [ "$(ls -A $DEPLOY_DIST_DIR 2>/dev/null)" ]; then
    echo "💾 Sauvegarde de l'ancienne version..."
    BACKUP_DIR="$DEPLOY_DIR/backup-$(date +%Y%m%d-%H%M%S)"
    sudo mkdir -p "$BACKUP_DIR"
    sudo cp -r "$DEPLOY_DIST_DIR"/* "$BACKUP_DIR/" 2>/dev/null || true
    echo "✅ Sauvegarde créée dans $BACKUP_DIR"
fi

# ========================
# Copie des fichiers build
# ========================
echo "📋 Copie des fichiers build vers $DEPLOY_DIST_DIR..."
sudo rm -rf "$DEPLOY_DIST_DIR"/*
sudo cp -r "$ADMIN_DIR/dist"/* "$DEPLOY_DIST_DIR/"
sudo chown -R www-data:www-data "$DEPLOY_DIST_DIR"
sudo chmod -R 755 "$DEPLOY_DIST_DIR"

echo "✅ Fichiers copiés avec succès"

# ========================
# Vérification de la configuration Nginx
# ========================
echo "🔍 Vérification de la configuration Nginx..."
if [ -f "/etc/nginx/sites-available/admin.viridial.com.conf" ]; then
    echo "✅ Configuration Nginx trouvée"
    
    # Test de la configuration
    if sudo nginx -t; then
        echo "✅ Configuration Nginx valide"
        
        # Rechargement de Nginx
        echo "🔄 Rechargement de Nginx..."
        if sudo systemctl reload nginx; then
            echo "✅ Nginx rechargé avec succès"
        else
            echo "⚠️  Erreur lors du rechargement de Nginx (mais les fichiers sont déployés)"
        fi
    else
        echo "⚠️  Erreur dans la configuration Nginx (mais les fichiers sont déployés)"
    fi
else
    echo "⚠️  Configuration Nginx non trouvée. Exécutez: ./scripts/setup-nginx.sh"
fi

# ========================
# Résumé
# ========================
echo ""
echo "✅ Déploiement du Frontend Admin terminé avec succès!"
echo ""
echo "📝 Informations:"
echo "   - Répertoire de déploiement: $DEPLOY_DIST_DIR"
echo "   - URL: http://admin.viridial.com (ou https://admin.viridial.com si SSL configuré)"
echo ""
echo "📝 Prochaines étapes (si nécessaire):"
echo "   1. Configurer le DNS pour admin.viridial.com"
echo "   2. Configurer SSL avec Certbot: certbot --nginx -d admin.viridial.com"
echo "   3. Vérifier que l'API Gateway est accessible sur http://localhost:8080"

