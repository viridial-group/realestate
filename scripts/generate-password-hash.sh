#!/bin/bash

# =====================================================
# Script pour générer un hash BCrypt via Identity Service
# =====================================================
# Usage: ./scripts/generate-password-hash.sh [password]
#   Si aucun mot de passe n'est fourni, utilise "admin123" par défaut

set -e

# Configuration
IDENTITY_SERVICE_URL="${IDENTITY_SERVICE_URL:-http://localhost:8081}"
PASSWORD="${1:-admin123}"

echo "🔐 Génération du hash BCrypt via Identity Service"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "📡 Service: $IDENTITY_SERVICE_URL"
echo "🔑 Mot de passe: $PASSWORD"
echo ""

# Vérifier que le service est accessible
if ! curl -s -f "$IDENTITY_SERVICE_URL/actuator/health" > /dev/null 2>&1; then
    echo "❌ Erreur: Le service Identity Service n'est pas accessible à $IDENTITY_SERVICE_URL"
    echo "   💡 Assurez-vous que le service est démarré"
    exit 1
fi

# Générer le hash via l'API
echo "🔄 Génération du hash..."
RESPONSE=$(curl -s -X POST "$IDENTITY_SERVICE_URL/api/identity/utils/password-hash" \
    -H "Content-Type: application/json" \
    -d "{\"password\": \"$PASSWORD\"}")

# Extraire le hash de la réponse JSON
HASH=$(echo "$RESPONSE" | grep -o '"hash":"[^"]*' | cut -d'"' -f4)

if [ -z "$HASH" ]; then
    echo "❌ Erreur: Impossible de générer le hash"
    echo "   Réponse du serveur: $RESPONSE"
    exit 1
fi

echo ""
echo "✅ Hash généré avec succès!"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "📋 Résultat:"
echo "   Password: $PASSWORD"
echo "   Hash:     $HASH"
echo "   Algo:     BCrypt"
echo ""
echo "📝 Pour utiliser dans SQL:"
echo "   INSERT INTO users (email, password, ...)"
echo "   VALUES ('user@example.com', '$HASH', ...);"
echo ""
echo "💡 Copier le hash ci-dessus dans votre script SQL"
echo ""

