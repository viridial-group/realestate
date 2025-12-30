#!/bin/bash

# ========================
# Script de Configuration VPS
# ========================
# Ce script configure le VPS pour le déploiement

set -e

echo "🔧 Configuration du VPS pour Real Estate Platform"

# ========================
# Variables
# ========================
APP_DIR=/var/realestate
SERVICE_USER=realestate
JAVA_VERSION=21

# ========================
# Mise à jour du système
# ========================
echo "📦 Mise à jour du système..."
apt-get update
apt-get upgrade -y

# ========================
# Installation Java 21
# ========================
echo "☕ Installation de Java $JAVA_VERSION..."
apt-get install -y openjdk-$JAVA_VERSION-jdk
java -version

# ========================
# Installation Maven
# ========================
echo "🔨 Installation de Maven..."
apt-get install -y maven
mvn -version

# ========================
# Installation PostgreSQL
# ========================
echo "🐘 Vérification de PostgreSQL..."
if ! command -v psql &> /dev/null; then
    apt-get install -y postgresql postgresql-contrib
fi

# ========================
# Installation Redis
# ========================
echo "📦 Vérification de Redis..."
if ! command -v redis-cli &> /dev/null; then
    apt-get install -y redis-server
    systemctl enable redis-server
    systemctl start redis-server
fi

# ========================
# Installation Kafka
# ========================
echo "📨 Installation de Kafka..."
KAFKA_VERSION=3.6.1
KAFKA_DIR=/opt/kafka

if [ ! -d "$KAFKA_DIR" ]; then
    cd /tmp
    wget https://downloads.apache.org/kafka/3.6.1/kafka_2.13-$KAFKA_VERSION.tgz
    tar -xzf kafka_2.13-$KAFKA_VERSION.tgz
    mv kafka_2.13-$KAFKA_VERSION $KAFKA_DIR
    rm kafka_2.13-$KAFKA_VERSION.tgz
fi

# ========================
# Installation Elasticsearch
# ========================
echo "🔍 Installation d'Elasticsearch..."
if ! command -v elasticsearch &> /dev/null; then
    wget -qO - https://artifacts.elastic.co/GPG-KEY-elasticsearch | apt-key add -
    echo "deb https://artifacts.elastic.co/packages/8.x/apt stable main" | tee /etc/apt/sources.list.d/elastic-8.x.list
    apt-get update
    apt-get install -y elasticsearch
    systemctl enable elasticsearch
    systemctl start elasticsearch
fi

# ========================
# Création de l'utilisateur
# ========================
echo "👤 Création de l'utilisateur $SERVICE_USER..."
if ! id "$SERVICE_USER" &>/dev/null; then
    useradd -r -s /bin/bash -d $APP_DIR $SERVICE_USER
fi

# ========================
# Création des répertoires
# ========================
echo "📁 Création des répertoires..."
mkdir -p $APP_DIR/{bin,config,logs,storage,backup}
mkdir -p $APP_DIR/storage/{documents,images,temp}
chown -R $SERVICE_USER:$SERVICE_USER $APP_DIR
chmod -R 755 $APP_DIR

# ========================
# Configuration Nginx (si nécessaire)
# ========================
echo "🌐 Configuration Nginx..."
if command -v nginx &> /dev/null; then
    cat > /etc/nginx/sites-available/realestate << NGINX
server {
    listen 80;
    server_name _;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
    }
}
NGINX
    ln -sf /etc/nginx/sites-available/realestate /etc/nginx/sites-enabled/
    nginx -t && systemctl reload nginx
fi

# ========================
# Configuration Firewall
# ========================
echo "🔥 Configuration du firewall..."
if command -v ufw &> /dev/null; then
    ufw allow 22/tcp
    ufw allow 80/tcp
    ufw allow 443/tcp
    ufw allow 8080/tcp
    ufw --force enable
fi

# ========================
# Finalisation
# ========================
echo "✅ Configuration du VPS terminée!"
echo ""
echo "📋 Prochaines étapes:"
echo "1. Configurer PostgreSQL (déjà configuré sur 148.230.112.148:5432)"
echo "2. Configurer Redis (déjà configuré sur 148.230.112.148:6379)"
echo "3. Exécuter ./deploy.sh pour déployer l'application"

