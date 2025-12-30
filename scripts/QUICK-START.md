# 🚀 Guide de Démarrage Rapide

## 📋 Prérequis

- ✅ PostgreSQL actif (148.230.112.148:5432)
- ✅ Redis actif (148.230.112.148:6379)
- ✅ Nginx configuré
- ✅ DNS configurés (api.viridial.com, app.viridial.com)

## 🔧 Installation des Services Systemd

```bash
# 1. Installer les fichiers systemd
./scripts/install-services.sh
```

## 🏗️ Build et Déploiement

```bash
# 1. Build du projet (depuis votre machine locale ou VPS)
mvn clean package -DskipTests -Pprod

# 2. Copier les JARs sur le VPS
scp gateway/target/*.jar root@148.230.112.148:/var/realestate/bin/gateway.jar
scp services/identity-service/target/*.jar root@148.230.112.148:/var/realestate/bin/identity-service.jar
scp services/organization-service/target/*.jar root@148.230.112.148:/var/realestate/bin/organization-service.jar
scp services/property-service/target/*.jar root@148.230.112.148:/var/realestate/bin/property-service.jar

# 3. Copier les configurations
scp config/application-prod.yml root@148.230.112.148:/var/realestate/config/
```

## 🚀 Démarrage des Services

```bash
# Sur le VPS
cd /opt/source/realestate

# 1. Installer les services systemd
./scripts/install-services.sh

# 2. Démarrer tous les services
./scripts/start-services.sh

# 3. Vérifier le statut
./scripts/status.sh
```

## 📊 Vérification

```bash
# Vérifier tous les services
./scripts/check-services.sh

# Voir les logs d'un service
./scripts/view-logs.sh realestate-gateway

# Vérifier les ports
netstat -tuln | grep -E '808[0-3]'
```

## 🔍 Dépannage

### Service ne démarre pas

```bash
# Voir les logs
journalctl -u realestate-gateway -n 100

# Vérifier que le JAR existe
ls -lh /var/realestate/bin/*.jar

# Vérifier que la config existe
ls -lh /var/realestate/config/application-prod.yml

# Tester manuellement
java -jar /var/realestate/bin/gateway.jar --spring.config.location=/var/realestate/config/application-prod.yml
```

### Port déjà utilisé

```bash
# Trouver quel processus utilise le port
lsof -i :8080
# ou
netstat -tulpn | grep 8080

# Arrêter le processus
kill -9 <PID>
```

## 📝 Commandes Utiles

```bash
# Démarrer un service spécifique
systemctl start realestate-gateway

# Arrêter un service
systemctl stop realestate-gateway

# Redémarrer un service
systemctl restart realestate-gateway

# Voir le statut
systemctl status realestate-gateway

# Suivre les logs en temps réel
journalctl -u realestate-gateway -f

# Vérifier que le service démarre au boot
systemctl is-enabled realestate-gateway
```

