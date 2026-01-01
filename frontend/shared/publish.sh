#!/bin/bash

# Script pour publier @viridial/shared sur npm

echo "📦 Publication de @viridial/shared sur npm"
echo ""
echo "⚠️  Cette opération nécessite un code OTP (One-Time Password)"
echo "   Vérifiez votre authentificateur (Google Authenticator, Authy, etc.)"
echo ""
read -p "Entrez le code OTP: " otp_code

if [ -z "$otp_code" ]; then
  echo "❌ Code OTP requis. Publication annulée."
  exit 1
fi

echo ""
echo "🔄 Publication en cours..."
npm publish --access public --otp="$otp_code"

if [ $? -eq 0 ]; then
  echo ""
  echo "✅ Package publié avec succès!"
  echo "📦 Version: $(node -p "require('./package.json').version")"
else
  echo ""
  echo "❌ Erreur lors de la publication"
  exit 1
fi

