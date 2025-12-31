#!/bin/bash

# ========================
# Script de Création des Topics Kafka
# ========================
# Crée tous les topics nécessaires pour l'application

set -e

BOOTSTRAP_SERVER=${KAFKA_BOOTSTRAP_SERVERS:-localhost:9092}

echo "📋 Création des topics Kafka..."
echo "📍 Bootstrap server: $BOOTSTRAP_SERVER"
echo ""

# Fonction pour créer un topic
create_topic() {
    local topic_name=$1
    local partitions=${2:-3}
    local replication_factor=${3:-1}
    
    echo "🔄 Création du topic: $topic_name (partitions: $partitions, replication: $replication_factor)"
    
    if command -v docker &> /dev/null && docker ps | grep -q "realestate-kafka"; then
        # Docker - Confluent image
        docker exec realestate-kafka kafka-topics \
            --create \
            --bootstrap-server localhost:9092 \
            --topic "$topic_name" \
            --partitions "$partitions" \
            --replication-factor "$replication_factor" \
            --if-not-exists 2>/dev/null && echo "   ✅ Topic créé" || echo "   ⚠️  Topic existe déjà"
    elif [ -f /opt/kafka/bin/kafka-topics.sh ]; then
        # VPS
        /opt/kafka/bin/kafka-topics.sh \
            --create \
            --bootstrap-server "$BOOTSTRAP_SERVER" \
            --topic "$topic_name" \
            --partitions "$partitions" \
            --replication-factor "$replication_factor" \
            --if-not-exists 2>/dev/null && echo "   ✅ Topic créé" || echo "   ⚠️  Topic existe déjà"
    else
        echo "   ❌ Kafka n'est pas disponible"
        exit 1
    fi
}

# Topics pour les événements
create_topic "property-created" 3 1
create_topic "property-updated" 3 1
create_topic "document-uploaded" 3 1
create_topic "workflow-task-created" 3 1
create_topic "workflow-task-completed" 3 1

echo ""
echo "✅ Tous les topics ont été créés"
echo ""
echo "📋 Topics créés:"
echo "   - property-created"
echo "   - property-updated"
echo "   - document-uploaded"
echo "   - workflow-task-created"
echo "   - workflow-task-completed"
echo ""
echo "💡 Pour lister les topics: ./scripts/list-kafka-topics.sh"

