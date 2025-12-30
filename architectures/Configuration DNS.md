# 🌐 Configuration DNS - Viridial

**Date :** Décembre 2024  
**Domaine :** viridial.com

---

## 📋 Enregistrements DNS Requis

### Production

| Type | Nom | Valeur | TTL | Description |
|------|-----|--------|-----|-------------|
| **A** | `api.viridial.com` | `148.230.112.148` | 3600 | API Gateway |
| **A** | `app.viridial.com` | `148.230.112.148` | 3600 | Application Frontend |
| **A** | `api-docs.viridial.com` | `148.230.112.148` | 3600 | Documentation API |

### Staging

| Type | Nom | Valeur | TTL | Description |
|------|-----|--------|-----|-------------|
| **A** | `staging-api.viridial.com` | `148.230.112.148` | 3600 | API Staging |
| **A** | `staging-app.viridial.com` | `148.230.112.148` | 3600 | Frontend Staging |

### Monitoring (Optionnel)

| Type | Nom | Valeur | TTL | Description |
|------|-----|--------|-----|-------------|
| **A** | `grafana.viridial.com` | `148.230.112.148` | 3600 | Grafana |
| **A** | `kibana.viridial.com` | `148.230.112.148` | 3600 | Kibana |
| **A** | `prometheus.viridial.com` | `148.230.112.148` | 3600 | Prometheus |

---

## 🔧 Configuration dans le Panneau DNS

### Étapes

1. **Connectez-vous à votre panneau de gestion DNS**
   - Hébergeur de domaine (ex: Hostinger, Cloudflare, etc.)
   - Ou gestionnaire DNS de votre registrar

2. **Ajoutez les enregistrements A**

   Pour chaque sous-domaine, créez un enregistrement :
   
   ```
   Type: A
   Nom: api.viridial.com (ou juste "api" selon votre panneau)
   Valeur: 148.230.112.148
   TTL: 3600 (ou par défaut)
   ```

3. **Répétez pour tous les sous-domaines**

---

## ✅ Vérification DNS

### Sur le VPS

```bash
# Vérifier la configuration DNS
./scripts/check-dns.sh
```

### Manuellement

```bash
# Vérifier api.viridial.com
dig api.viridial.com @8.8.8.8

# Vérifier app.viridial.com
dig app.viridial.com @8.8.8.8

# Vérifier depuis le serveur
nslookup api.viridial.com
nslookup app.viridial.com
```

### Résultat Attendu

```
api.viridial.com.    IN    A    148.230.112.148
app.viridial.com.    IN    A    148.230.112.148
```

---

## ⏱️ Propagation DNS

- **Temps de propagation :** 5 minutes à 48 heures
- **Généralement :** 15-30 minutes
- **Vérification :** Utilisez `./scripts/check-dns.sh` régulièrement

---

## 🔍 Dépannage

### Problème : DNS non propagé

```bash
# Vérifier depuis différents serveurs DNS
dig api.viridial.com @8.8.8.8      # Google DNS
dig api.viridial.com @1.1.1.1      # Cloudflare DNS
dig api.viridial.com @208.67.222.222 # OpenDNS
```

### Problème : IP incorrecte

1. Vérifiez l'IP du serveur :
   ```bash
   curl ifconfig.me
   ```

2. Vérifiez que les DNS pointent vers cette IP

3. Attendez la propagation

### Problème : Certbot échoue

Si certbot échoue avec une erreur DNS :

1. Vérifiez les DNS : `./scripts/check-dns.sh`
2. Attendez la propagation complète
3. Réessayez : `./scripts/setup-ssl.sh`

---

## 📝 Checklist

Avant d'exécuter `setup-ssl.sh` :

- [ ] Enregistrement A pour `api.viridial.com` créé
- [ ] Enregistrement A pour `app.viridial.com` créé
- [ ] Les deux pointent vers `148.230.112.148`
- [ ] DNS propagés (vérifié avec `check-dns.sh`)
- [ ] Nginx configuré et fonctionnel
- [ ] Services Spring Boot démarrés (port 8080, etc.)

---

## 🚀 Commandes Rapides

```bash
# 1. Vérifier les DNS
./scripts/check-dns.sh

# 2. Si OK, configurer SSL
./scripts/setup-ssl.sh

# 3. Vérifier que tout fonctionne
curl -I http://api.viridial.com/actuator/health
curl -I https://api.viridial.com/actuator/health
```

---

**Dernière mise à jour :** Décembre 2024

