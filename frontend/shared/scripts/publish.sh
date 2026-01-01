#!/bin/bash

# Script pour publier le package @viridial/shared sur npmjs
# Usage: ./scripts/publish.sh [patch|minor|major|beta]

set -e

# Couleurs pour les messages
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Fonction pour afficher les messages
info() {
    echo -e "${GREEN}ℹ️  $1${NC}"
}

warn() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

error() {
    echo -e "${RED}❌ $1${NC}"
    exit 1
}

# Vérifier que nous sommes dans le bon répertoire
if [ ! -f "package.json" ]; then
    error "package.json non trouvé. Exécutez ce script depuis le répertoire frontend/shared"
fi

# Vérifier que npm est installé
if ! command -v npm &> /dev/null; then
    error "npm n'est pas installé"
fi

# Vérifier que l'utilisateur est connecté à npm
if ! npm whoami &> /dev/null; then
    error "Vous n'êtes pas connecté à npm. Exécutez: npm login"
fi

# Récupérer le type de version (patch, minor, major, beta)
VERSION_TYPE=${1:-patch}

info "📦 Publication du package @viridial/shared sur npmjs"
info "Type de version: $VERSION_TYPE"

# Vérifier les fichiers nécessaires
if [ ! -f "index.ts" ]; then
    error "index.ts non trouvé"
fi

if [ ! -f "README.md" ]; then
    warn "README.md non trouvé (recommandé)"
fi

# Afficher les informations du package
info "Informations du package:"
npm pkg get name version description

# Demander confirmation
read -p "Voulez-vous continuer la publication? (y/N) " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    info "Publication annulée"
    exit 0
fi

# Incrémenter la version si nécessaire
if [ "$VERSION_TYPE" != "beta" ]; then
    info "Incrémentation de la version ($VERSION_TYPE)..."
    npm version $VERSION_TYPE --no-git-tag-version
fi

# Publier le package
info "Publication sur npmjs..."
if [ "$VERSION_TYPE" == "beta" ]; then
    npm publish --tag beta --access public
else
    npm publish --access public
fi

# Récupérer la nouvelle version
NEW_VERSION=$(npm pkg get version | tr -d '"')
info "✅ Package publié avec succès!"
info "Version: $NEW_VERSION"
info "Installation: npm install @viridial/shared@$NEW_VERSION"

