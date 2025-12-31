# 📚 Liens Swagger - Documentation API

**Date de création :** Décembre 2024

---

## 🌐 Accès Local (localhost)

### API Gateway
- **Swagger UI :** http://localhost:8080/swagger-ui.html
- **API Docs (JSON) :** http://localhost:8080/v3/api-docs
- **Port :** 8080

### Services Microservices

#### 1. Identity & Auth Service
- **Swagger UI :** http://localhost:8081/swagger-ui.html
- **API Docs (JSON) :** http://localhost:8081/v3/api-docs
- **Port :** 8081
- **Endpoints principaux :**
  - `/api/auth/register` - Inscription
  - `/api/auth/login` - Connexion
  - `/api/auth/refresh` - Rafraîchir le token
  - `/api/users/**` - Gestion des utilisateurs

#### 2. Organization Service
- **Swagger UI :** http://localhost:8082/swagger-ui.html
- **API Docs (JSON) :** http://localhost:8082/v3/api-docs
- **Port :** 8082
- **Endpoints principaux :**
  - `/api/organizations/**` - Gestion des organisations
  - `/api/teams/**` - Gestion des équipes

#### 3. Property Service
- **Swagger UI :** http://localhost:8083/swagger-ui.html
- **API Docs (JSON) :** http://localhost:8083/v3/api-docs
- **Port :** 8083
- **Endpoints principaux :**
  - `/api/properties/**` - Gestion des propriétés immobilières

#### 4. Resource Service
- **Swagger UI :** http://localhost:8084/swagger-ui.html
- **API Docs (JSON) :** http://localhost:8084/v3/api-docs
- **Port :** 8084
- **Endpoints principaux :**
  - `/api/resources/**` - Gestion des ressources
  - `/api/domains/**` - Gestion des domaines
  - `/api/tags/**` - Gestion des tags

#### 5. Document Service
- **Swagger UI :** http://localhost:8085/swagger-ui.html
- **API Docs (JSON) :** http://localhost:8085/v3/api-docs
- **Port :** 8085
- **Endpoints principaux :**
  - `/api/documents/**` - Gestion des documents

#### 6. Workflow Service
- **Swagger UI :** http://localhost:8086/swagger-ui.html
- **API Docs (JSON) :** http://localhost:8086/v3/api-docs
- **Port :** 8086
- **Endpoints principaux :**
  - `/api/workflows/**` - Gestion des workflows
  - `/api/tasks/**` - Gestion des tâches

#### 7. Notification Service
- **Swagger UI :** http://localhost:8087/swagger-ui.html
- **API Docs (JSON) :** http://localhost:8087/v3/api-docs
- **Port :** 8087
- **Endpoints principaux :**
  - `/api/notifications/**` - Gestion des notifications

#### 8. Emailing Service
- **Swagger UI :** http://localhost:8088/swagger-ui.html
- **API Docs (JSON) :** http://localhost:8088/v3/api-docs
- **Port :** 8088
- **Endpoints principaux :**
  - `/api/emails/**` - Gestion des emails
  - `/api/email-templates/**` - Gestion des templates

#### 9. Audit Service
- **Swagger UI :** http://localhost:8089/swagger-ui.html
- **API Docs (JSON) :** http://localhost:8089/v3/api-docs
- **Port :** 8089
- **Endpoints principaux :**
  - `/api/audit-logs/**` - Consultation des logs d'audit

#### 10. Billing Service
- **Swagger UI :** http://localhost:8090/swagger-ui.html
- **API Docs (JSON) :** http://localhost:8090/v3/api-docs
- **Port :** 8090
- **Endpoints principaux :**
  - `/api/plans/**` - Gestion des plans
  - `/api/subscriptions/**` - Gestion des abonnements
  - `/api/invoices/**` - Gestion des factures

---

## 🌍 Accès via API Gateway (Production)

Si les services sont accessibles via le Gateway :

- **Gateway Swagger :** https://api.viridial.com/swagger-ui.html
- **API Docs :** https://api.viridial.com/v3/api-docs

**Note :** Le Gateway peut agréger la documentation de tous les services si configuré.

---

## 🔐 Authentification pour Swagger

### Méthode 1 : JWT Token (Recommandé)

1. **Obtenir un token :**
   ```bash
   curl -X POST http://localhost:8081/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{
       "email": "admin@viridial.com",
       "password": "admin123"
     }'
   ```

2. **Dans Swagger UI :**
   - Cliquez sur le bouton **"Authorize"** (en haut à droite)
   - Entrez : `Bearer <votre-token-jwt>`
   - Cliquez sur **"Authorize"**

### Méthode 2 : Utilisateur par défaut

- **Email :** `admin@viridial.com`
- **Password :** `admin123`

---

## 📋 Liste Rapide des URLs

```bash
# Identity Service
http://localhost:8081/swagger-ui.html

# Organization Service
http://localhost:8082/swagger-ui.html

# Property Service
http://localhost:8083/swagger-ui.html

# Resource Service
http://localhost:8084/swagger-ui.html

# Document Service
http://localhost:8085/swagger-ui.html

# Workflow Service
http://localhost:8086/swagger-ui.html

# Notification Service
http://localhost:8087/swagger-ui.html

# Emailing Service
http://localhost:8088/swagger-ui.html

# Audit Service
http://localhost:8089/swagger-ui.html

# Billing Service
http://localhost:8090/swagger-ui.html
```

---

## 💡 Notes

- **Swagger est désactivé en production** (`application-prod.yml`)
- **Swagger est activé en local** par défaut
- Tous les services utilisent **Swagger/OpenAPI 3** avec descriptions en anglais
- Les endpoints nécessitent généralement une **authentification JWT**

---

## 🚀 Commandes Utiles

```bash
# Vérifier qu'un service est démarré
curl http://localhost:8081/actuator/health

# Obtenir un token JWT
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@viridial.com","password":"admin123"}'

# Tester un endpoint avec token
curl -X GET http://localhost:8081/api/users \
  -H "Authorization: Bearer <token>"
```

