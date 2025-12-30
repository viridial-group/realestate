#!/bin/bash

# ========================
# Script d'Installation des Services Systemd
# ========================
# Ce script installe les fichiers systemd pour tous les microservices

set -e

echo "🔧 Installation des services systemd"

# ========================
# Variables
# ========================
PROJECT_DIR=/opt/source/realestate
SYSTEMD_DIR=/etc/systemd/system

# ========================
# Vérification
# ========================
if [ ! -d "$PROJECT_DIR/config/systemd" ]; then
    echo "❌ Erreur: Le répertoire $PROJECT_DIR/config/systemd n'existe pas"
    exit 1
fi

# ========================
# Copie des fichiers systemd
# ========================
echo "📋 Copie des fichiers systemd..."

for service_file in "$PROJECT_DIR/config/systemd"/*.service; do
    if [ -f "$service_file" ]; then
        filename=$(basename "$service_file")
        cp "$service_file" "$SYSTEMD_DIR/"
        echo "✅ $filename copié"
    fi
done

# ========================
# Rechargement de systemd
# ========================
echo "🔄 Rechargement de systemd..."
systemctl daemon-reload
echo "✅ Systemd rechargé"

# ========================
# Activation des services
# ========================
echo "🔗 Activation des services..."

services=(
    "realestate-gateway"
    "realestate-identity-service"
    "realestate-organization-service"
    "realestate-property-service"
)

for service in "${services[@]}"; do
    if systemctl enable "$service" > /dev/null 2>&1; then
        echo "✅ $service activé (démarrage automatique)"
    else
        echo "⚠️  $service: fichier systemd non trouvé"
    fi
done

echo ""
echo "✅ Installation terminée!"
echo ""
echo "📝 Prochaines étapes:"
echo "1. Vérifier que les JARs sont dans /var/realestate/bin/"
echo "2. Vérifier que les configurations sont dans /var/realestate/config/"
echo "3. Démarrer les services: ./scripts/start-services.sh"
echo "4. Vérifier le statut: ./scripts/status.sh"

