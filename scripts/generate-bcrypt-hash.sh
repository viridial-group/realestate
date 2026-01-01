#!/bin/bash

# Script pour générer un hash BCrypt
# Usage: ./generate-bcrypt-hash.sh <password>

PASSWORD="${1:-admin123}"

echo "🔐 Génération du hash BCrypt pour: $PASSWORD"
echo ""
echo "Option 1: Utiliser un générateur en ligne"
echo "   https://bcrypt-generator.com/"
echo "   Entrez: $PASSWORD"
echo ""
echo "Option 2: Utiliser Spring Boot (si disponible)"
echo "   Créez un test Java avec BCryptPasswordEncoder"
echo ""
echo "Option 3: Utiliser Python (bcrypt)"
if command -v python3 &> /dev/null; then
    echo "   python3 -c \"import bcrypt; print(bcrypt.hashpw(b'$PASSWORD', bcrypt.gensalt()).decode())\""
    python3 -c "import bcrypt; print('Hash:', bcrypt.hashpw(b'$PASSWORD', bcrypt.gensalt()).decode())" 2>/dev/null || echo "   ⚠️  Module bcrypt non installé. Installez avec: pip install bcrypt"
else
    echo "   ⚠️  Python3 non disponible"
fi
echo ""
echo "📝 Hash BCrypt valide pour '$PASSWORD' (exemple - générez le vôtre):"
echo "   \$2a\$10\$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi"
echo ""
echo "💡 Note: Chaque génération produit un hash différent (salt aléatoire)"
echo "   Mais tous les hashs pour le même mot de passe sont valides"

