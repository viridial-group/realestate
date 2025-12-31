# 🚀 Guide de Démarrage Rapide sur VPS

**Date de création :** Décembre 2024

---

## 🔧 Problème : Page de Login sur le Gateway

Si vous voyez une page de login sur `http://localhost:8080`, c'est parce que Spring Security bloque l'accès. 

### Solution : Redémarrer le Gateway avec la nouvelle configuration

La configuration a été mise à jour pour permettre l'accès public aux endpoints API. Vous devez recompiler et redémarrer le Gateway.

---

## 📋 Étapes pour Démarrer Tous les Services

### 1. Vérifier l'état actuel

```bash
./scripts/check-vps-services.sh
```

### 2. Arrêter tous les services (si nécessaire)

```bash
./scripts/stop-all-services.sh
```

### 3. Recompiler le Gateway (pour appliquer la nouvelle config de sécurité)

```bash
cd /opt/source/realestate
mvn clean package -DskipTests=true -pl gateway -am
```

### 4. Démarrer tous les services

```bash
./scripts/build-and-start-all.sh
```

**OU** si certains services sont déjà démarrés :

```bash
./scripts/start-missing-services.sh
```

---

## 🔍 Vérification

### Vérifier que le Gateway fonctionne

```bash
# Health check (devrait retourner {"status":"UP"})
curl http://localhost:8080/actuator/health

# Test d'accès à l'API (ne devrait PAS rediriger vers login)
curl http://localhost:8080/api/identity/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"Test123!"}'
```

### Vérifier tous les services

```bash
./scripts/check-vps-services.sh
```

---

## 🐛 Dépannage

### Problème : Page de login s'affiche toujours

**Cause :** Le Gateway utilise encore l'ancienne configuration de sécurité.

**Solution :**
1. Arrêter le Gateway :
   ```bash
   kill $(lsof -t -i:8080)
   ```

2. Recompiler :
   ```bash
   cd /opt/source/realestate
   mvn clean package -DskipTests=true -pl gateway -am
   ```

3. Redémarrer :
   ```bash
   cd /opt/source/realestate
   nohup java -jar gateway/target/gateway-*.jar > logs/gateway.log 2>&1 &
   echo $! > logs/gateway.pid
   ```

### Problème : Services ne démarrent pas

**Vérifier les logs :**
```bash
tail -50 /opt/source/realestate/logs/<service-name>.log
```

**Vérifier les dépendances :**
- PostgreSQL : `psql -h localhost -U postgres -d realestate_db -c "SELECT 1;"`
- Redis : `redis-cli ping`

### Problème : PostgreSQL demande un mot de passe

**Solution 1 :** Configurer `.pgpass`
```bash
echo "localhost:5432:realestate_db:postgres:VOTRE_MOT_DE_PASSE" > ~/.pgpass
chmod 600 ~/.pgpass
```

**Solution 2 :** Utiliser la variable d'environnement
```bash
export PGPASSWORD="VOTRE_MOT_DE_PASSE"
```

---

## 📝 Commandes Utiles

### Voir les processus Java

```bash
ps aux | grep java
```

### Voir les ports utilisés

```bash
netstat -tuln | grep LISTEN
```

### Arrêter un service spécifique

```bash
kill $(cat /opt/source/realestate/logs/gateway.pid)
```

### Redémarrer un service spécifique

```bash
cd /opt/source/realestate
nohup java -jar gateway/target/gateway-*.jar > logs/gateway.log 2>&1 &
echo $! > logs/gateway.pid
```

---

## ✅ Checklist

- [ ] Gateway redémarré avec nouvelle config
- [ ] Tous les services démarrés
- [ ] Health checks retournent UP
- [ ] Pas de page de login sur `/api/**`
- [ ] PostgreSQL accessible
- [ ] Redis accessible (si utilisé)

