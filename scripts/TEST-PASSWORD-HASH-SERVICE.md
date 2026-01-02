# Test du Service de Génération de Hash

Ce document explique comment tester le service de génération de hash de mots de passe.

## 🚀 Prérequis

1. Le service Identity Service doit être démarré et accessible sur `http://localhost:8081`
2. Le service doit être compilé avec les nouveaux fichiers (PasswordHashController, PasswordHashService, etc.)

## ✅ Tests à Effectuer

### 1. Vérifier que le service est accessible

```bash
# Vérifier la santé du service
curl http://localhost:8081/actuator/health

# Vérifier que l'endpoint est accessible (sans authentification)
curl http://localhost:8081/api/identity/utils/password-hash?password=test123
```

### 2. Tester l'endpoint POST

```bash
curl -X POST http://localhost:8081/api/identity/utils/password-hash \
  -H "Content-Type: application/json" \
  -d '{"password": "admin123"}'
```

**Résultat attendu:**
```json
{
  "password": "admin123",
  "hash": "$2a$10$...",
  "algorithm": "BCrypt"
}
```

### 3. Tester l'endpoint GET

```bash
curl "http://localhost:8081/api/identity/utils/password-hash?password=admin123"
```

**Résultat attendu:** Même format JSON que ci-dessus

### 4. Utiliser le script helper

```bash
# Générer un hash pour "admin123"
./scripts/generate-password-hash.sh admin123

# Générer un hash pour un autre mot de passe
./scripts/generate-password-hash.sh "password123"
```

### 5. Vérifier dans Swagger UI

1. Ouvrir http://localhost:8081/swagger-ui.html
2. Chercher la section "Utilities"
3. Tester l'endpoint `/api/identity/utils/password-hash`

## 🔍 Vérifications

- [ ] Le service répond sans erreur 401 (pas d'authentification requise)
- [ ] Le hash généré est un hash BCrypt valide (commence par `$2a$10$`)
- [ ] Le même mot de passe génère un hash différent à chaque appel (normal pour BCrypt)
- [ ] Le hash peut être utilisé pour authentifier un utilisateur

## 🐛 Dépannage

### Erreur 404: Endpoint non trouvé
- Vérifier que le service a été redémarré après l'ajout du nouveau contrôleur
- Vérifier que le package du contrôleur est scanné par Spring

### Erreur 401: Non autorisé
- Vérifier que `SecurityConfig.java` contient `.requestMatchers("/api/identity/utils/**").permitAll()`
- Vérifier que le service a été redémarré après la modification de SecurityConfig

### Erreur 500: Erreur serveur
- Vérifier les logs du service Identity Service
- Vérifier que `PasswordEncoder` est bien injecté dans `PasswordHashService`

## 📝 Notes

- Chaque appel génère un hash différent (c'est normal pour BCrypt)
- Le hash peut être utilisé directement dans les scripts SQL
- Le service est public uniquement pour faciliter le développement

