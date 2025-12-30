#!/bin/bash

# Script pour installer et configurer Java 21

set -e

echo "🔧 Configuration Java 21 pour Real Estate Platform"
echo "=================================================="

# Vérifier si Java 21 est déjà installé
if /usr/libexec/java_home -v 21 &>/dev/null; then
    JAVA_21_HOME=$(/usr/libexec/java_home -v 21)
    echo "✅ Java 21 déjà installé: $JAVA_21_HOME"
    export JAVA_HOME=$JAVA_21_HOME
    export PATH=$JAVA_HOME/bin:$PATH
    echo ""
    echo "Java version:"
    java -version
    echo ""
    echo "Maven version:"
    mvn -version | head -3
    exit 0
fi

echo "📦 Java 21 n'est pas installé"
echo ""
echo "Options d'installation:"
echo "1. Homebrew (recommandé)"
echo "2. SDKMAN"
echo "3. Téléchargement manuel"
echo ""
read -p "Choisissez une option (1-3): " choice

case $choice in
    1)
        echo "🍺 Installation via Homebrew..."
        if ! command -v brew &> /dev/null; then
            echo "❌ Homebrew n'est pas installé"
            echo "Installez-le depuis: https://brew.sh"
            exit 1
        fi
        brew install openjdk@21
        JAVA_21_HOME=$(/usr/libexec/java_home -v 21)
        ;;
    2)
        echo "📦 Installation via SDKMAN..."
        if ! command -v sdk &> /dev/null; then
            echo "Installation de SDKMAN..."
            curl -s "https://get.sdkman.io" | bash
            source "$HOME/.sdkman/bin/sdkman-init.sh"
        fi
        sdk install java 21.0.1-tem
        sdk use java 21.0.1-tem
        JAVA_21_HOME=$JAVA_HOME
        ;;
    3)
        echo "📥 Téléchargement manuel..."
        echo "Téléchargez Java 21 depuis: https://adoptium.net/temurin/releases/?version=21"
        echo "Ou utilisez: brew install openjdk@21"
        exit 0
        ;;
    *)
        echo "❌ Option invalide"
        exit 1
        ;;
esac

if [ -z "$JAVA_21_HOME" ]; then
    echo "❌ Impossible de trouver Java 21"
    exit 1
fi

# Configurer JAVA_HOME pour cette session
export JAVA_HOME=$JAVA_21_HOME
export PATH=$JAVA_HOME/bin:$PATH

# Ajouter à ~/.zshrc ou ~/.bash_profile
SHELL_CONFIG=""
if [ -f "$HOME/.zshrc" ]; then
    SHELL_CONFIG="$HOME/.zshrc"
elif [ -f "$HOME/.bash_profile" ]; then
    SHELL_CONFIG="$HOME/.bash_profile"
fi

if [ -n "$SHELL_CONFIG" ]; then
    if ! grep -q "JAVA_HOME.*21" "$SHELL_CONFIG"; then
        echo "" >> "$SHELL_CONFIG"
        echo "# Java 21 for Real Estate Platform" >> "$SHELL_CONFIG"
        echo "export JAVA_HOME=\$(/usr/libexec/java_home -v 21 2>/dev/null || echo \"$JAVA_21_HOME\")" >> "$SHELL_CONFIG"
        echo "export PATH=\$JAVA_HOME/bin:\$PATH" >> "$SHELL_CONFIG"
        echo "✅ Configuration ajoutée à $SHELL_CONFIG"
    fi
fi

echo ""
echo "✅ Java 21 configuré!"
echo ""
echo "Java version:"
java -version
echo ""
echo "Maven version:"
mvn -version | head -3
echo ""
echo "💡 Pour appliquer les changements dans cette session:"
echo "   source $SHELL_CONFIG"
echo ""
echo "💡 Pour tester le projet:"
echo "   mvn clean test -pl services/identity-service"

