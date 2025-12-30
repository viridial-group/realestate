#!/bin/bash

# ========================
# Script de Mise à Jour du Gateway
# ========================
# Usage: ./update-gateway.sh

set -e

echo "🔄 Mise à jour du Gateway"
echo "=========================="
echo ""

PROJECT_DIR=/opt/source/realestate
cd "$PROJECT_DIR"

# 1. Build
echo "1️⃣  Build du Gateway..."
mvn clean package -DskipTests -pl gateway -am

if [ $? -ne 0 ]; then
    echo "❌ Échec du build"
    exit 1
fi

echo "✅ Build réussi"
echo ""

# 2. Arrêter le service
echo "2️⃣  Arrêt du service..."
systemctl stop realestate-gateway
echo "✅ Service arrêté"
echo ""

# 3. Copier le nouveau JAR
echo "3️⃣  Copie du nouveau JAR..."
cp gateway/target/*.jar /var/realestate/bin/gateway.jar
chown realestate:realestate /var/realestate/bin/gateway.jar
echo "✅ JAR copié"
echo ""

# 4. Redémarrer le service
echo "4️⃣  Démarrage du service..."
systemctl start realestate-gateway
sleep 3

if systemctl is-active --quiet realestate-gateway; then
    echo "✅ Service démarré"
else
    echo "❌ Erreur lors du démarrage"
    echo "📋 Logs:"
    journalctl -u realestate-gateway -n 20 --no-pager
    exit 1
fi
echo ""

# 5. Test
echo "5️⃣  Test du Gateway..."
sleep 2
if curl -s http://localhost:8080/actuator/health > /dev/null; then
    echo "✅ Gateway répond"
    curl -s http://localhost:8080/actuator/health
else
    echo "❌ Gateway ne répond pas"
fi
echo ""

echo "=========================="
echo "✅ Mise à jour terminée!"
echo ""
echo "🌐 Testez:"
echo "   curl https://api.viridial.com/actuator/health"

