#!/bin/bash

# ========================
# Script de Liste des Topics Kafka
# ========================

set -e

BOOTSTRAP_SERVER=${KAFKA_BOOTSTRAP_SERVERS:-localhost:9092}

echo "📋 Liste des topics Kafka..."
echo "📍 Bootstrap server: $BOOTSTRAP_SERVER"
echo ""

if command -v docker &> /dev/null && docker ps | grep -q "realestate-kafka"; then
    # Docker - Confluent image
    docker exec realestate-kafka kafka-topics \
        --list \
        --bootstrap-server localhost:9092
elif [ -f /opt/kafka/bin/kafka-topics.sh ]; then
    # VPS
    /opt/kafka/bin/kafka-topics.sh \
        --list \
        --bootstrap-server "$BOOTSTRAP_SERVER"
else
    echo "❌ Kafka n'est pas disponible"
    exit 1
fi

