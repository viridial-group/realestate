# Password Hash Generation Service

Ce service permet de générer des hash BCrypt pour les mots de passe, utile pour les scripts de seed de base de données.

## 🔓 Accès Public

Ce service est **accessible sans authentification** pour faciliter la génération de hash lors du développement et du seeding.

## 📡 Endpoints

### POST `/api/identity/utils/password-hash`

Génère un hash BCrypt pour un mot de passe fourni dans le body de la requête.

**Request Body:**
```json
{
  "password": "admin123"
}
```

**Response:**
```json
{
  "password": "admin123",
  "hash": "$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi",
  "algorithm": "BCrypt"
}
```

### GET `/api/identity/utils/password-hash?password=admin123`

Génère un hash BCrypt pour un mot de passe fourni en paramètre de requête.

**Response:**
```json
{
  "password": "admin123",
  "hash": "$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi",
  "algorithm": "BCrypt"
}
```

## 💡 Exemples d'utilisation

### Avec curl

```bash
# POST method
curl -X POST http://localhost:8081/api/identity/utils/password-hash \
  -H "Content-Type: application/json" \
  -d '{"password": "admin123"}'

# GET method
curl "http://localhost:8081/api/identity/utils/password-hash?password=admin123"
```

### Avec JavaScript/Fetch

```javascript
// POST method
const response = await fetch('http://localhost:8081/api/identity/utils/password-hash', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({ password: 'admin123' })
});

const data = await response.json();
console.log('Hash:', data.hash);
```

### Utilisation dans les scripts SQL

1. Générer le hash via l'API
2. Copier le hash dans votre script SQL

```sql
-- Exemple avec le hash généré
INSERT INTO users (email, password, ...)
VALUES ('admin@viridial.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', ...);
```

## ⚠️ Note de Sécurité

**Important:** Ce service est public uniquement pour faciliter le développement et le seeding. En production, vous devriez :

1. Désactiver cet endpoint ou le protéger avec une authentification
2. Utiliser des variables d'environnement pour contrôler l'accès
3. Limiter l'accès par IP si nécessaire

Pour désactiver en production, modifiez `SecurityConfig.java` :

```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/api/identity/auth/**").permitAll()
    // .requestMatchers("/api/identity/utils/**").permitAll() // Commenter cette ligne
    .requestMatchers("/actuator/**").permitAll()
    .anyRequest().authenticated()
)
```

