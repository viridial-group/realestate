#!/bin/bash

# ========================
# Script de Diagnostic Kafka
# ========================
# Aide à diagnostiquer les problèmes avec Kafka

set -e

echo "🔍 Diagnostic de Kafka..."
echo ""

# Couleurs
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m'

log_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

log_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

log_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

log_error() {
    echo -e "${RED}❌ $1${NC}"
}

# Vérifier Docker
if ! command -v docker &> /dev/null; then
    log_error "Docker n'est pas installé"
    exit 1
fi

log_success "Docker est installé"

# Vérifier les conteneurs
echo ""
echo "=========================================="
echo "📦 Statut des Conteneurs"
echo "=========================================="

# Zookeeper
if docker ps | grep -q "realestate-zookeeper"; then
    log_success "Zookeeper est en cours d'exécution"
    docker ps | grep "realestate-zookeeper"
elif docker ps -a | grep -q "realestate-zookeeper"; then
    log_warning "Zookeeper est arrêté"
    echo "   Statut:"
    docker ps -a | grep "realestate-zookeeper"
else
    log_error "Zookeeper n'existe pas"
fi

echo ""

# Kafka
if docker ps | grep -q "realestate-kafka"; then
    log_success "Kafka est en cours d'exécution"
    docker ps | grep "realestate-kafka"
elif docker ps -a | grep -q "realestate-kafka"; then
    log_warning "Kafka est arrêté"
    echo "   Statut:"
    docker ps -a | grep "realestate-kafka"
    echo ""
    echo "   Code de sortie:"
    docker inspect realestate-kafka --format='{{.State.ExitCode}}' 2>/dev/null || echo "   (non disponible)"
else
    log_error "Kafka n'existe pas"
fi

# Logs
echo ""
echo "=========================================="
echo "📋 Logs Zookeeper (dernières 20 lignes)"
echo "=========================================="
if docker ps -a | grep -q "realestate-zookeeper"; then
    docker logs realestate-zookeeper 2>/dev/null | tail -20 || echo "   (logs non disponibles)"
else
    echo "   Zookeeper n'existe pas"
fi

echo ""
echo "=========================================="
echo "📋 Logs Kafka (dernières 30 lignes)"
echo "=========================================="
if docker ps -a | grep -q "realestate-kafka"; then
    docker logs realestate-kafka 2>/dev/null | tail -30 || echo "   (logs non disponibles)"
else
    echo "   Kafka n'existe pas"
fi

# Vérifier le réseau
echo ""
echo "=========================================="
echo "🌐 Réseau Docker"
echo "=========================================="
if docker network ls | grep -q "realestate-network"; then
    log_success "Réseau realestate-network existe"
    docker network inspect realestate-network --format='{{range .Containers}}{{.Name}} {{end}}' 2>/dev/null || echo "   (aucun conteneur)"
else
    log_warning "Réseau realestate-network n'existe pas"
fi

# Vérifier les ports
echo ""
echo "=========================================="
echo "🔌 Ports"
echo "=========================================="
if netstat -tlnp 2>/dev/null | grep -q ":2181"; then
    log_success "Port 2181 (Zookeeper) est ouvert"
else
    log_warning "Port 2181 (Zookeeper) n'est pas ouvert"
fi

if netstat -tlnp 2>/dev/null | grep -q ":9092"; then
    log_success "Port 9092 (Kafka) est ouvert"
else
    log_warning "Port 9092 (Kafka) n'est pas ouvert"
fi

# Test de connexion
echo ""
echo "=========================================="
echo "🧪 Tests de Connexion"
echo "=========================================="

# Test Zookeeper
if docker ps | grep -q "realestate-zookeeper"; then
    if docker exec realestate-zookeeper nc -z localhost 2181 2>/dev/null; then
        log_success "Zookeeper répond sur le port 2181"
    else
        log_error "Zookeeper ne répond pas sur le port 2181"
    fi
else
    log_warning "Zookeeper n'est pas en cours d'exécution"
fi

# Test Kafka
if docker ps | grep -q "realestate-kafka"; then
    if docker exec realestate-kafka kafka-broker-api-versions --bootstrap-server localhost:9092 >/dev/null 2>&1; then
        log_success "Kafka répond sur le port 9092"
    else
        log_warning "Kafka ne répond pas encore (peut être en cours de démarrage)"
    fi
else
    log_warning "Kafka n'est pas en cours d'exécution"
fi

# Recommandations
echo ""
echo "=========================================="
echo "💡 Recommandations"
echo "=========================================="

if ! docker ps | grep -q "realestate-zookeeper"; then
    echo "   1. Démarrer Zookeeper: ./scripts/start-kafka.sh"
fi

if ! docker ps | grep -q "realestate-kafka"; then
    if docker ps -a | grep -q "realestate-kafka"; then
        echo "   1. Nettoyer et redémarrer:"
        echo "      ./scripts/stop-kafka.sh"
        echo "      ./scripts/start-kafka.sh"
    else
        echo "   1. Démarrer Kafka: ./scripts/start-kafka.sh"
    fi
fi

if docker ps | grep -q "realestate-kafka" && docker ps | grep -q "realestate-zookeeper"; then
    log_success "Kafka et Zookeeper sont opérationnels"
    echo ""
    echo "   Pour créer les topics:"
    echo "      ./scripts/create-kafka-topics.sh"
    echo ""
    echo "   Pour lister les topics:"
    echo "      ./scripts/list-kafka-topics.sh"
fi

echo ""

