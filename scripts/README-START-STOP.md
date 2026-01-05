# Scripts de Démarrage et d'Arrêt

Ce dossier contient des scripts pour démarrer et arrêter facilement tous les services et frontends du projet.

## Scripts Disponibles

### `start-all.sh`
Démarre tous les services backend et frontends dans le bon ordre.

**Usage:**
```bash
./scripts/start-all.sh
```

**Services démarrés:**
- **Backend Services:**
  - Identity Service (port 8081)
  - Gateway (port 8080)
  - Property Service (port 8083)
  - Resource Service (port 8084)
  - Document Service (port 8085)
  - Workflow Service (port 8086)
  - Notification Service (port 8087)
  - Emailing Service (port 8088)
  - Audit Service (port 8089)
  - Billing Service (port 8090)

- **Frontends:**
  - Frontend Public (port 3003)
  - Frontend Admin (port 3001)
  - Frontend Agent (port 3002)

**Fonctionnalités:**
- Vérifie si les services sont déjà en cours d'exécution
- Compile automatiquement les JARs si nécessaire
- Installe les dépendances npm si nécessaire
- Crée les fichiers PID pour le suivi des processus
- Affiche les URLs de tous les services

### `stop-all.sh`
Arrête tous les services backend et frontends.

**Usage:**
```bash
./scripts/stop-all.sh
```

**Fonctionnalités:**
- Arrêt gracieux des processus (SIGTERM)
- Arrêt forcé si nécessaire (SIGKILL)
- Nettoyage des fichiers PID
- Arrêt par port si les fichiers PID ne sont pas disponibles

## Structure des Logs

Tous les logs sont stockés dans le répertoire `logs/` à la racine du projet:
- `logs/{service-name}.log` - Logs de chaque service
- `logs/{service-name}.pid` - PID de chaque processus

## Exemples d'Utilisation

### Démarrage complet
```bash
cd /Users/mac/poledata/realestate
./scripts/start-all.sh
```

### Arrêt complet
```bash
cd /Users/mac/poledata/realestate
./scripts/stop-all.sh
```

### Vérification des services
```bash
# Vérifier les processus en cours
ps aux | grep java
ps aux | grep node

# Vérifier les ports utilisés
lsof -i :8080  # Gateway
lsof -i :8081  # Identity
lsof -i :3003  # Frontend Public
```

## Dépannage

### Un service ne démarre pas
1. Vérifiez les logs: `tail -f logs/{service-name}.log`
2. Vérifiez si le port est déjà utilisé: `lsof -i :{port}`
3. Vérifiez que les dépendances sont installées (Maven pour backend, npm pour frontend)

### Un service ne s'arrête pas
1. Utilisez `kill -9 {pid}` pour forcer l'arrêt
2. Vérifiez les processus zombies: `ps aux | grep {service-name}`
3. Supprimez manuellement le fichier PID: `rm logs/{service-name}.pid`

### Port déjà utilisé
Si un port est déjà utilisé, le script affichera un avertissement. Vous pouvez:
1. Arrêter le processus qui utilise le port: `lsof -ti:{port} | xargs kill`
2. Ou modifier le port dans le fichier de configuration du service

## Notes

- Les services backend nécessitent Java 21
- Les frontends nécessitent Node.js et npm
- Les JARs sont compilés automatiquement si absents
- Les dépendances npm sont installées automatiquement si absentes
- Les services démarrent en arrière-plan (nohup)

