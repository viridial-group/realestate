#!/bin/bash

# Script pour créer le fichier .env à partir de .env.example

ENV_FILE=".env"
ENV_EXAMPLE=".env.example"

if [ -f "$ENV_FILE" ]; then
  echo "⚠️  Le fichier .env existe déjà."
  read -p "Voulez-vous le remplacer? (y/N) " -n 1 -r
  echo
  if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "❌ Opération annulée."
    exit 1
  fi
fi

if [ ! -f "$ENV_EXAMPLE" ]; then
  echo "❌ Le fichier .env.example n'existe pas."
  exit 1
fi

cp "$ENV_EXAMPLE" "$ENV_FILE"
echo "✅ Fichier .env créé à partir de .env.example"
echo ""
echo "📝 N'oubliez pas de modifier les valeurs selon votre environnement!"

