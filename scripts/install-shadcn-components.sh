#!/bin/bash

# Script pour installer les composants shadcn-vue dans tous les projets frontend

set -e

echo "🎨 Installation des composants shadcn-vue"
echo ""

# Liste des composants à installer
COMPONENTS=(
  "button"
  "input"
  "card"
  "dialog"
  "form"
  "table"
  "dropdown-menu"
  "toast"
  "select"
  "textarea"
  "label"
  "badge"
  "separator"
  "tabs"
  "sheet"
  "avatar"
  "navigation-menu"
)

# Projets frontend
PROJECTS=("admin" "agent" "public")

for project in "${PROJECTS[@]}"; do
  echo "📦 Installation pour frontend/$project..."
  cd "frontend/$project"
  
  # Vérifier que node_modules existe
  if [ ! -d "node_modules" ]; then
    echo "  ⚠️  node_modules non trouvé. Installation des dépendances..."
    npm install
  fi
  
  # Installer les composants
  for component in "${COMPONENTS[@]}"; do
    echo "  ➕ Installation de $component..."
    npx shadcn-vue@latest add "$component" --yes || echo "  ⚠️  $component déjà installé ou erreur"
  done
  
  cd ../..
  echo "✅ $project terminé"
  echo ""
done

echo "🎉 Installation terminée pour tous les projets !"
echo ""
echo "📝 Pour installer d'autres composants :"
echo "   cd frontend/[admin|agent|public]"
echo "   npx shadcn-vue@latest add [nom-du-composant]"

