#!/bin/bash

# ========================
# Script pour lancer un service Spring Boot depuis la racine
# ========================
# Usage: ./scripts/run-service.sh <service-name>
# Exemples:
#   ./scripts/run-service.sh gateway
#   ./scripts/run-service.sh identity-service
#   ./scripts/run-service.sh property-service

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

# Couleurs pour les messages
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Vérifier qu'un argument est fourni
if [ -z "$1" ]; then
    echo -e "${RED}❌ Erreur: Vous devez spécifier le nom du service${NC}"
    echo ""
    echo "Usage: $0 <service-name>"
    echo ""
    echo "Services disponibles:"
    echo "  - gateway"
    echo "  - identity-service"
    echo "  - property-service"
    echo "  - resource-service"
    echo "  - document-service"
    echo "  - workflow-service"
    echo "  - notification-service"
    echo "  - emailing-service"
    echo "  - audit-service"
    echo "  - billing-service"
    exit 1
fi

SERVICE_NAME=$1

# Mapper le nom du service au chemin du module
case $SERVICE_NAME in
    gateway)
        MODULE_PATH="gateway"
        ;;
    identity-service)
        MODULE_PATH="services/identity-service"
        ;;
    property-service)
        MODULE_PATH="services/property-service"
        ;;
    resource-service)
        MODULE_PATH="services/resource-service"
        ;;
    document-service)
        MODULE_PATH="services/document-service"
        ;;
    workflow-service)
        MODULE_PATH="services/workflow-service"
        ;;
    notification-service)
        MODULE_PATH="services/notification-service"
        ;;
    emailing-service)
        MODULE_PATH="services/emailing-service"
        ;;
    audit-service)
        MODULE_PATH="services/audit-service"
        ;;
    billing-service)
        MODULE_PATH="services/billing-service"
        ;;
    *)
        echo -e "${RED}❌ Service inconnu: $SERVICE_NAME${NC}"
        echo ""
        echo "Services disponibles:"
        echo "  - gateway"
        echo "  - identity-service"
        echo "  - property-service"
        echo "  - resource-service"
        echo "  - document-service"
        echo "  - workflow-service"
        echo "  - notification-service"
        echo "  - emailing-service"
        echo "  - audit-service"
        echo "  - billing-service"
        exit 1
        ;;
esac

# Vérifier que le module existe
if [ ! -d "$PROJECT_ROOT/$MODULE_PATH" ]; then
    echo -e "${RED}❌ Erreur: Le module $MODULE_PATH n'existe pas${NC}"
    exit 1
fi

# Vérifier que le pom.xml existe
if [ ! -f "$PROJECT_ROOT/$MODULE_PATH/pom.xml" ]; then
    echo -e "${RED}❌ Erreur: Le fichier pom.xml n'existe pas dans $MODULE_PATH${NC}"
    exit 1
fi

echo -e "${GREEN}🚀 Lancement du service: $SERVICE_NAME${NC}"
echo -e "${YELLOW}📁 Module: $MODULE_PATH${NC}"
echo ""

# Aller à la racine du projet
cd "$PROJECT_ROOT"

# Lancer le service avec Maven
echo -e "${GREEN}▶️  Exécution: mvn spring-boot:run -pl $MODULE_PATH${NC}"
echo ""

mvn spring-boot:run -pl "$MODULE_PATH"

