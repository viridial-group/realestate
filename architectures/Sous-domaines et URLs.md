# 🌐 Sous-domaines et URLs - Configuration

**Date :** Décembre 2024  
**Domaine principal :** https://viridial.com  
**GitHub :** https://github.com/viridial-group

---

## 📋 Structure des Sous-domaines

### 🚀 Production

| Service | Sous-domaine | URL | Description |
|---------|-------------|-----|-------------|
| **Application Frontend** | `app` | https://app.viridial.com | Application principale Vue.js |
| **API Gateway** | `api` | https://api.viridial.com | Point d'entrée API (Spring Cloud Gateway) |
| **Documentation API** | `api-docs` | https://api-docs.viridial.com | Swagger/OpenAPI documentation |
| **Grafana** | `grafana` | https://grafana.viridial.com | Monitoring et métriques |
| **Kibana** | `kibana` | https://kibana.viridial.com | Visualisation des logs |
| **Prometheus** | `prometheus` | https://prometheus.viridial.com | Collecte de métriques |
| **Zipkin** | `zipkin` | https://zipkin.viridial.com | Distributed tracing |

### 🧪 Staging

| Service | Sous-domaine | URL | Description |
|---------|-------------|-----|-------------|
| **Application Frontend** | `staging-app` | https://staging-app.viridial.com | Application staging |
| **API Gateway** | `staging-api` | https://staging-api.viridial.com | API staging |
| **Documentation API** | `staging-api-docs` | https://staging-api-docs.viridial.com | Documentation staging |

### 🔧 Pre-Production

| Service | Sous-domaine | URL | Description |
|---------|-------------|-----|-------------|
| **Application Frontend** | `preprod-app` | https://preprod-app.viridial.com | Application pre-prod |
| **API Gateway** | `preprod-api` | https://preprod-api.viridial.com | API pre-prod |

### 💻 Développement

| Service | Sous-domaine | URL | Description |
|---------|-------------|-----|-------------|
| **Application Frontend** | `dev-app` | https://dev-app.viridial.com | Application développement |
| **API Gateway** | `dev-api` | https://dev-api.viridial.com | API développement |

---

## 🔧 Configuration Nginx

### Production - API Gateway

```nginx
server {
    listen 80;
    server_name api.viridial.com;
    
    # Redirection HTTPS
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name api.viridial.com;

    ssl_certificate /etc/ssl/certs/viridial.com.crt;
    ssl_certificate_key /etc/ssl/private/viridial.com.key;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # CORS
        add_header Access-Control-Allow-Origin https://app.viridial.com;
        add_header Access-Control-Allow-Methods "GET, POST, PUT, DELETE, OPTIONS";
        add_header Access-Control-Allow-Headers "Authorization, Content-Type";
    }
}
```

### Production - Frontend

```nginx
server {
    listen 80;
    server_name app.viridial.com;
    
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name app.viridial.com;

    ssl_certificate /etc/ssl/certs/viridial.com.crt;
    ssl_certificate_key /etc/ssl/private/viridial.com.key;

    root /var/www/viridial-app/dist;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /api {
        proxy_pass https://api.viridial.com;
    }
}
```

### Staging - API Gateway

```nginx
server {
    listen 80;
    server_name staging-api.viridial.com;
    
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name staging-api.viridial.com;

    ssl_certificate /etc/ssl/certs/viridial.com.crt;
    ssl_certificate_key /etc/ssl/private/viridial.com.key;

    location / {
        proxy_pass http://localhost:8081;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

---

## 📝 Configuration Spring Boot

### application-prod.yml

```yaml
server:
  port: 8080

spring:
  application:
    name: gateway
  cloud:
    gateway:
      routes:
        - id: identity-service
          uri: http://localhost:8081
          predicates:
            - Path=/api/identity/**
        
        - id: property-service
          uri: http://localhost:8083
          predicates:
            - Path=/api/properties/**

# CORS Configuration
cors:
  allowed-origins:
    - https://app.viridial.com
    - https://staging-app.viridial.com
  allowed-methods: GET,POST,PUT,DELETE,OPTIONS
  allowed-headers: Authorization,Content-Type
```

---

## 🔐 Variables d'Environnement

### Production

```properties
# URLs
APP_URL=https://app.viridial.com
API_URL=https://api.viridial.com
API_DOCS_URL=https://api-docs.viridial.com

# CORS
CORS_ALLOWED_ORIGINS=https://app.viridial.com
```

### Staging

```properties
# URLs
APP_URL=https://staging-app.viridial.com
API_URL=https://staging-api.viridial.com
API_DOCS_URL=https://staging-api-docs.viridial.com

# CORS
CORS_ALLOWED_ORIGINS=https://staging-app.viridial.com
```

---

## ✅ Checklist de Configuration DNS

### Enregistrements DNS à créer (Production - PRIORITÉ)

- [ ] `api.viridial.com` → `148.230.112.148` (A record) **REQUIS**
- [ ] `app.viridial.com` → `148.230.112.148` (A record) **REQUIS**
- [ ] `api-docs.viridial.com` → `148.230.112.148` (A record) - Optionnel

### Staging (Plus tard)

- [ ] `staging-app.viridial.com` → `148.230.112.148` (A record)
- [ ] `staging-api.viridial.com` → `148.230.112.148` (A record)

### Monitoring (Optionnel)

- [ ] `grafana.viridial.com` → `148.230.112.148` (A record)
- [ ] `kibana.viridial.com` → `148.230.112.148` (A record)
- [ ] `prometheus.viridial.com` → `148.230.112.148` (A record)
- [ ] `zipkin.viridial.com` → `148.230.112.148` (A record)

### Vérification DNS

```bash
# Vérifier que les DNS sont configurés
./scripts/check-dns.sh
```

> 📖 Voir [Configuration DNS.md](./Configuration%20DNS.md) pour les instructions détaillées

### Certificats SSL

- [ ] Certificat SSL pour `*.viridial.com` (wildcard) ou certificats individuels
- [ ] Configuration Let's Encrypt (recommandé) ou certificat commercial

---

## 🚀 Déploiement

### Configuration Nginx sur VPS

#### Option 1 : Scripts Automatiques (Recommandé)

```bash
# 1. Installer Nginx
apt-get install -y nginx

# 2. Configuration Nginx avec les scripts
./scripts/setup-nginx.sh

# 3. Configuration SSL avec Let's Encrypt
./scripts/setup-ssl.sh
```

#### Option 2 : Configuration Manuelle

```bash
# 1. Installer Nginx et Certbot
apt-get install -y nginx certbot python3-certbot-nginx

# 2. Copier les configurations
cp config/nginx/*.conf /etc/nginx/sites-available/

# 3. Activer les sites (IMPORTANT: avec .conf)
ln -s /etc/nginx/sites-available/api.viridial.com.conf /etc/nginx/sites-enabled/api.viridial.com.conf
ln -s /etc/nginx/sites-available/app.viridial.com.conf /etc/nginx/sites-enabled/app.viridial.com.conf

# 4. Tester la configuration
nginx -t

# 5. Recharger Nginx
systemctl reload nginx

# 6. Obtenir les certificats SSL
certbot --nginx -d api.viridial.com -d app.viridial.com
```

#### Correction des Liens (si erreur)

Si vous avez créé les liens sans `.conf`, exécutez :

```bash
# Supprimer les liens incorrects
rm /etc/nginx/sites-enabled/api.viridial.com
rm /etc/nginx/sites-enabled/app.viridial.com

# Créer les liens corrects (avec .conf)
ln -s /etc/nginx/sites-available/api.viridial.com.conf /etc/nginx/sites-enabled/api.viridial.com.conf
ln -s /etc/nginx/sites-available/app.viridial.com.conf /etc/nginx/sites-enabled/app.viridial.com.conf

# Ou utiliser le script de correction
./scripts/fix-nginx-links.sh
```

---

## 📊 Résumé

| Environnement | Frontend | API | Docs |
|---------------|----------|-----|------|
| **Production** | app.viridial.com | api.viridial.com | api-docs.viridial.com |
| **Staging** | staging-app.viridial.com | staging-api.viridial.com | staging-api-docs.viridial.com |
| **Pre-Prod** | preprod-app.viridial.com | preprod-api.viridial.com | - |
| **Dev** | dev-app.viridial.com | dev-api.viridial.com | - |

---

**Dernière mise à jour :** Décembre 2024

