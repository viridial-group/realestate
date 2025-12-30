# 🚀 Guide de Déploiement VPS

Ce guide explique comment déployer tous les services sur le VPS.

## 📋 Prérequis

- Accès SSH au VPS (148.230.112.148)
- Java 21 installé sur le VPS
- Maven 3.9+ installé localement
- PostgreSQL et Redis configurés sur le VPS

## 🔧 Installation de tous les services

### Étape 1 : Compiler et installer tous les services

```bash
./scripts/install-all-services.sh
```

Ce script va :
1. Compiler tous les services avec Maven
2. Copier les JARs sur le VPS dans `/var/realestate/bin/`
3. Copier les configurations dans `/var/realestate/config/`
4. Créer l'utilisateur `realestate` si nécessaire
5. Installer les services systemd

### Étape 2 : Vérifier l'installation

```bash
ssh root@148.230.112.148 "ls -la /var/realestate/bin/"
ssh root@148.230.112.148 "ls -la /var/realestate/config/"
```

## 🚀 Démarrage des services

### Démarrer tous les services

```bash
./scripts/start-all-services.sh
```

### Démarrer un service spécifique

```bash
ssh root@148.230.112.148 "systemctl start realestate-gateway"
ssh root@148.230.112.148 "systemctl start realestate-identity-service"
# etc.
```

## 🛑 Arrêt des services

### Arrêter tous les services

```bash
./scripts/stop-all-services.sh
```

### Arrêter un service spécifique

```bash
ssh root@148.230.112.148 "systemctl stop realestate-gateway"
```

## 📊 Vérification du statut

### Statut de tous les services

```bash
./scripts/status-all-services.sh
```

### Statut d'un service spécifique

```bash
ssh root@148.230.112.148 "systemctl status realestate-gateway"
```

### Vérifier les logs

```bash
ssh root@148.230.112.148 "journalctl -u realestate-gateway -f"
ssh root@148.230.112.148 "journalctl -u realestate-identity-service -f"
# etc.
```

## 🔄 Mise à jour d'un service

### Mettre à jour un service spécifique

```bash
# 1. Compiler le service
mvn clean package -DskipTests -pl services/identity-service

# 2. Copier le JAR
scp services/identity-service/target/identity-service-*.jar root@148.230.112.148:/var/realestate/bin/identity-service.jar

# 3. Redémarrer le service
ssh root@148.230.112.148 "systemctl restart realestate-identity-service"
```

## 📝 Services disponibles

| Service | Port | Route Gateway | Service systemd |
|---------|------|----------------|-----------------|
| Gateway | 8080 | `/api/**` | `realestate-gateway` |
| Identity | 8081 | `/api/identity/**` | `realestate-identity-service` |
| Organization | 8082 | `/api/organizations/**` | `realestate-organization-service` |
| Property | 8083 | `/api/properties/**` | `realestate-property-service` |
| Resource | 8084 | `/api/resources/**` | `realestate-resource-service` |
| Document | 8085 | `/api/documents/**` | `realestate-document-service` |
| Workflow | 8086 | `/api/workflows/**` | `realestate-workflow-service` |
| Notification | 8087 | `/api/notifications/**` | `realestate-notification-service` |
| Emailing | 8088 | `/api/emails/**` | `realestate-emailing-service` |
| Audit | 8089 | `/api/audit/**` | `realestate-audit-service` |
| Billing | 8090 | `/api/billing/**` | `realestate-billing-service` |

## 🔍 Dépannage

### Service ne démarre pas

1. Vérifier les logs :
   ```bash
   ssh root@148.230.112.148 "journalctl -u realestate-gateway -n 50"
   ```

2. Vérifier que le JAR existe :
   ```bash
   ssh root@148.230.112.148 "ls -la /var/realestate/bin/gateway.jar"
   ```

3. Vérifier les permissions :
   ```bash
   ssh root@148.230.112.148 "chown -R realestate:realestate /var/realestate"
   ```

### Port déjà utilisé

```bash
ssh root@148.230.112.148 "netstat -tuln | grep :8080"
ssh root@148.230.112.148 "lsof -i :8080"
```

### Problème de connexion à la base de données

Vérifier que PostgreSQL et Redis sont accessibles :
```bash
ssh root@148.230.112.148 "psql -h 148.230.112.148 -U postgres -d realestate_db -c 'SELECT 1;'"
ssh root@148.230.112.148 "redis-cli -h 148.230.112.148 ping"
```

## 📚 Ressources

- [Configuration Nginx](./QUICK-FIX-SSL.md)
- [Variables d'environnement](../architectures/Variables%20d'Environnement.md)
- [TODO - Plan d'implémentation](../architectures/TODO%20-%20Plan%20d'Implémentation.md)

