# Guide de Test - Système d'Inscription avec Abonnement

## ✅ Ce qui a été implémenté

### Backend
- ✅ Service unifié `SubscribeService` qui crée tout en une transaction
- ✅ Endpoint `POST /api/identity/auth/subscribe`
- ✅ Création automatique de l'organisation
- ✅ Attribution automatique du rôle `ORGANIZATION_ADMIN`
- ✅ Création de l'abonnement directement via SQL (pas d'appel au billing service)
- ✅ Gestion correcte des champs JSONB avec `@JdbcTypeCode(SqlTypes.JSON)`

### Frontend
- ✅ Formulaire simplifié (prénom, nom, email, mot de passe, nom organisation)
- ✅ Un seul appel API au lieu de 6
- ✅ Redirection vers le front admin (port 3001) après inscription

### Permissions
- ✅ Rôle `ORGANIZATION_ADMIN` avec toutes les permissions nécessaires
- ✅ Peut gérer les utilisateurs, organisations, rôles dans son organisation

## 🧪 Étapes de Test

### 1. Prérequis

```bash
# Vérifier que les services sont démarrés
# - Identity Service (port 8081)
# - Billing Service (port 8090) - optionnel maintenant
# - PostgreSQL (port 5432)
# - Frontend Public (port 3000 ou 5173)
# - Frontend Admin (port 3001)
```

### 2. Vérifier la base de données

```sql
-- Vérifier que le rôle ORGANIZATION_ADMIN existe
SELECT * FROM roles WHERE name = 'ORGANIZATION_ADMIN';

-- Vérifier les permissions du rôle
SELECT r.name, p.name, p.resource, p.action
FROM roles r
JOIN role_permissions rp ON r.id = rp.role_id
JOIN permissions p ON rp.permission_id = p.id
WHERE r.name = 'ORGANIZATION_ADMIN'
ORDER BY p.resource, p.action;

-- Vérifier que ROLE_DELETE est présent
SELECT * FROM permissions WHERE name = 'ROLE_DELETE';
```

### 3. Test d'inscription via l'interface

1. **Accéder au formulaire d'inscription**
   - URL : `http://localhost:3000/subscribe` (ou le port du frontend public)

2. **Remplir le formulaire**
   - Prénom : `Test`
   - Nom : `User`
   - Email : `test.user@example.com` (utiliser un email unique)
   - Mot de passe : `password123`
   - Confirmation : `password123`
   - Nom de l'organisation : `Test Organization`
   - Sélectionner un plan (ex: ENTERPRISE)

3. **Soumettre le formulaire**
   - Vérifier qu'il n'y a pas d'erreur
   - Vérifier le message de succès
   - Attendre la redirection (2 secondes)

4. **Vérifier la redirection**
   - Doit rediriger vers `http://localhost:3001/login?email=test.user@example.com`

### 4. Vérifications dans la base de données

```sql
-- Vérifier que l'utilisateur a été créé
SELECT id, email, first_name, last_name, enabled 
FROM users 
WHERE email = 'test.user@example.com';

-- Vérifier que l'organisation a été créée
SELECT id, name, active 
FROM organizations 
WHERE name = 'Test Organization';

-- Vérifier l'association utilisateur-organisation
SELECT ou.user_id, ou.organization_id, ou.is_primary, u.email, o.name
FROM organization_users ou
JOIN users u ON ou.user_id = u.id
JOIN organizations o ON ou.organization_id = o.id
WHERE u.email = 'test.user@example.com';

-- Vérifier que le rôle ORGANIZATION_ADMIN est assigné
SELECT u.email, r.name as role_name
FROM users u
JOIN user_roles ur ON u.id = ur.user_id
JOIN roles r ON ur.role_id = r.id
WHERE u.email = 'test.user@example.com';

-- Vérifier que l'abonnement a été créé
SELECT s.id, s.organization_id, s.plan_id, s.status, s.start_date, s.end_date, p.name as plan_name
FROM subscriptions s
JOIN plans p ON s.plan_id = p.id
JOIN organizations o ON s.organization_id = o.id
WHERE o.name = 'Test Organization';
```

### 5. Test de connexion

1. **Se connecter avec le nouvel utilisateur**
   - Email : `test.user@example.com`
   - Mot de passe : `password123`

2. **Vérifier les permissions**
   - L'utilisateur doit pouvoir accéder au dashboard admin
   - L'utilisateur doit pouvoir gérer les utilisateurs de son organisation
   - L'utilisateur doit pouvoir gérer les rôles de son organisation
   - L'utilisateur doit pouvoir gérer les sous-organisations

### 6. Test via API (curl)

```bash
# Test de l'endpoint subscribe
curl -X POST http://localhost:8081/api/identity/auth/subscribe \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "API",
    "lastName": "Test",
    "email": "api.test@example.com",
    "password": "password123",
    "organizationName": "API Test Organization",
    "planId": 1
  }'

# Vérifier la réponse
# Doit retourner :
# {
#   "auth": {
#     "accessToken": "...",
#     "refreshToken": "...",
#     "expiresIn": 86400
#   },
#   "organizationId": ...,
#   "subscriptionId": ...,
#   "organizationName": "API Test Organization",
#   "planName": "..."
# }
```

## 🔍 Points de Vérification

### ✅ Checklist de Validation

- [ ] L'utilisateur est créé avec les bonnes informations
- [ ] L'organisation est créée avec le bon nom
- [ ] L'utilisateur est associé à l'organisation avec `isPrimary = true`
- [ ] Le rôle `ORGANIZATION_ADMIN` est automatiquement assigné
- [ ] L'abonnement est créé avec le bon plan
- [ ] La date de fin de l'abonnement est calculée correctement (MONTHLY = +1 mois, YEARLY = +1 an)
- [ ] Les tokens JWT sont retournés et valides
- [ ] La redirection vers le front admin fonctionne
- [ ] L'utilisateur peut se connecter avec ses identifiants
- [ ] L'utilisateur a les permissions pour gérer son organisation

### ⚠️ Problèmes Potentiels

1. **Erreur JSONB** : Si vous voyez une erreur sur `custom_domains` ou `quotas`
   - Vérifier que `@JdbcTypeCode(SqlTypes.JSON)` est présent sur ces champs
   - Vérifier que les champs sont initialisés à `null`

2. **Erreur de contrainte de clé étrangère** : Si l'abonnement ne peut pas être créé
   - Vérifier que l'organisation existe bien dans la base
   - Vérifier que le plan existe (planId valide)

3. **Rôle non assigné** : Si l'utilisateur n'a pas le rôle ORGANIZATION_ADMIN
   - Vérifier que `OrganizationUserService.addUserToOrganization()` est appelé avec `isPrimary = true`
   - Vérifier que le rôle ORGANIZATION_ADMIN existe dans la base

4. **Redirection ne fonctionne pas** : Si la redirection échoue
   - Vérifier que le front admin est démarré sur le port 3001
   - Vérifier la variable d'environnement `VITE_ADMIN_URL` si configurée

## 📊 Tests de Performance

### Test de charge (optionnel)

```bash
# Tester avec plusieurs inscriptions simultanées
for i in {1..10}; do
  curl -X POST http://localhost:8081/api/identity/auth/subscribe \
    -H "Content-Type: application/json" \
    -d "{
      \"firstName\": \"Test$i\",
      \"lastName\": \"User\",
      \"email\": \"test$i@example.com\",
      \"password\": \"password123\",
      \"organizationName\": \"Test Org $i\",
      \"planId\": 1
    }" &
done
wait
```

## 🐛 Debug

### Logs à vérifier

```bash
# Logs du service identity
tail -f logs/identity-service.log | grep -i "subscribe\|organization\|subscription"

# Vérifier les erreurs
grep -i "error\|exception" logs/identity-service.log
```

### Requêtes SQL de debug

```sql
-- Vérifier les dernières inscriptions
SELECT 
    u.email,
    u.created_at as user_created,
    o.name as org_name,
    o.created_at as org_created,
    s.id as subscription_id,
    s.created_at as subscription_created,
    r.name as role_name
FROM users u
LEFT JOIN organization_users ou ON u.id = ou.user_id
LEFT JOIN organizations o ON ou.organization_id = o.id
LEFT JOIN subscriptions s ON o.id = s.organization_id
LEFT JOIN user_roles ur ON u.id = ur.user_id
LEFT JOIN roles r ON ur.role_id = r.id
WHERE u.created_at > NOW() - INTERVAL '1 hour'
ORDER BY u.created_at DESC;
```

## ✅ Prochaines Étapes Recommandées

1. **Tests manuels** : Tester le flux complet d'inscription
2. **Tests automatisés** : Créer des tests unitaires et d'intégration pour `SubscribeService`
3. **Documentation API** : Vérifier que Swagger documente correctement l'endpoint `/subscribe`
4. **Validation des permissions** : Tester que l'utilisateur peut bien gérer son organisation
5. **Tests de régression** : Vérifier que les autres fonctionnalités ne sont pas cassées

## 📝 Notes

- Le système crée maintenant l'abonnement directement via SQL, donc le billing service n'est plus nécessaire pour l'inscription
- Tous les champs JSONB sont gérés avec `@JdbcTypeCode(SqlTypes.JSON)`
- Le rôle `ORGANIZATION_ADMIN` a maintenant `ROLE_DELETE` pour permettre la suppression de rôles personnalisés
- La redirection se fait maintenant vers le port 3001 (front admin)

