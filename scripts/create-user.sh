#!/bin/bash

# ========================
# Script de Création de l'Utilisateur Real Estate
# ========================
# Usage: ./create-user.sh

set -e

echo "👤 Création de l'utilisateur realestate..."

# Vérifier si l'utilisateur existe déjà
if id "realestate" &>/dev/null; then
    echo "✅ L'utilisateur realestate existe déjà"
else
    # Créer l'utilisateur et le groupe
    useradd -r -s /bin/false -d /var/realestate -m realestate
    echo "✅ Utilisateur realestate créé"
fi

# Créer les répertoires nécessaires
mkdir -p /var/realestate/{bin,config,logs,storage,backup}
mkdir -p /var/realestate/storage/{documents,images,temp}

# Définir les permissions
chown -R realestate:realestate /var/realestate
chmod -R 755 /var/realestate

echo "✅ Répertoires créés et permissions définies"
echo ""
echo "📋 Vérification:"
id realestate
ls -ld /var/realestate

