#!/bin/bash

# ========================
# Script de Build - Gateway uniquement
# ========================
# Usage: ./build-gateway.sh

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"

echo "🔨 Build du Gateway..."

cd "$PROJECT_DIR"

# Build seulement le gateway
mvn clean package -DskipTests -pl gateway -am

if [ $? -eq 0 ]; then
    echo "✅ Build réussi!"
    echo "📦 JAR créé: gateway/target/*.jar"
    echo ""
    echo "📝 Prochaines étapes:"
    echo "   1. Copier le JAR sur le VPS:"
    echo "      scp gateway/target/*.jar root@148.230.112.148:/var/realestate/bin/gateway.jar"
    echo ""
    echo "   2. Installer le service systemd:"
    echo "      ./scripts/install-services.sh"
    echo ""
    echo "   3. Démarrer le service:"
    echo "      systemctl start realestate-gateway"
else
    echo "❌ Échec du build"
    exit 1
fi

