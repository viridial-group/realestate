# Script SQL de Seed - Données de Test

Ce script SQL permet de peupler la base de données avec des données de test complètes pour simuler des agences immobilières.

## 📋 Contenu du Seed

Le script crée :

1. **Utilisateurs** (22 utilisateurs)
   - 1 admin principal
   - 5 agences avec directeurs, managers et agents
   - 3 freelances

2. **Organisations** (5 agences)
   - Immobilier Paris
   - Real Estate Lyon
   - Property Marseille
   - Bordeaux Immobilier
   - Nice Properties

3. **Équipes** (5 équipes)
   - Équipes Ventes et Locations pour certaines agences

4. **Associations** (utilisateurs ↔ organisations)
   - Tous les utilisateurs sont associés à leur agence respective

5. **Propriétés** (22 propriétés)
   - Réparties entre les 5 agences
   - Différents types : APARTMENT, HOUSE, COMMERCIAL, LAND
   - Différents statuts : DRAFT, PUBLISHED, SOLD, RENTED

## 🚀 Utilisation

### Option 1: Via psql

```bash
psql -h localhost -U postgres -d realestate_db -f scripts/seed-database.sql
```

### Option 2: Via Docker

```bash
docker exec -i postgres_container psql -U postgres -d realestate_db < scripts/seed-database.sql
```

### Option 3: Via pgAdmin ou autre client SQL

Ouvrez le fichier `scripts/seed-database.sql` et exécutez-le dans votre client SQL.

## 🔑 Comptes de Test

Tous les utilisateurs ont le mot de passe : **`password123`**

### Comptes principaux :

- **Admin** : `admin@viridial.com`
- **Directeur Paris** : `directeur@paris-immobilier.fr`
- **Manager Lyon** : `manager@lyon-realestate.fr`
- **Agent Marseille** : `agent1@marseille-property.fr`

### Liste complète des emails :

**Immobilier Paris:**
- directeur@paris-immobilier.fr
- manager@paris-immobilier.fr
- agent1@paris-immobilier.fr
- agent2@paris-immobilier.fr
- agent3@paris-immobilier.fr

**Real Estate Lyon:**
- directeur@lyon-realestate.fr
- manager@lyon-realestate.fr
- agent1@lyon-realestate.fr
- agent2@lyon-realestate.fr

**Property Marseille:**
- directeur@marseille-property.fr
- manager@marseille-property.fr
- agent1@marseille-property.fr
- agent2@marseille-property.fr
- agent3@marseille-property.fr

**Bordeaux Immobilier:**
- directeur@bordeaux-immobilier.fr
- manager@bordeaux-immobilier.fr
- agent1@bordeaux-immobilier.fr

**Nice Properties:**
- directeur@nice-properties.fr
- agent1@nice-properties.fr
- agent2@nice-properties.fr

**Freelances:**
- freelance1@example.com
- freelance2@example.com
- freelance3@example.com

## ⚠️ Notes Importantes

1. **Mots de passe** : Les mots de passe dans le script sont encodés avec BCrypt pour "password123". Si vous utilisez un autre encodage, vous devrez les mettre à jour.

2. **Rôles** : Le script suppose que les rôles (ADMIN, MANAGER, USER) existent déjà. Ils sont créés par `DataInitializer` au démarrage de l'application.

3. **IDs** : Le script utilise des requêtes SELECT pour trouver les IDs dynamiquement, donc l'ordre d'insertion n'est pas critique.

4. **Conflits** : Le script utilise `ON CONFLICT DO NOTHING` pour éviter les erreurs si les données existent déjà.

5. **Nettoyage** : Les commandes TRUNCATE sont commentées pour sécurité. Décommentez-les si vous voulez nettoyer avant de seed.

## 🔄 Réinitialiser les Données

Pour réinitialiser complètement :

```sql
-- ATTENTION: Cela supprime TOUTES les données!
TRUNCATE TABLE organization_users CASCADE;
TRUNCATE TABLE teams CASCADE;
TRUNCATE TABLE organizations CASCADE;
TRUNCATE TABLE properties CASCADE;
TRUNCATE TABLE user_roles CASCADE;
TRUNCATE TABLE users CASCADE;
```

Puis réexécutez le script de seed.

## 📊 Statistiques Après Seed

Après l'exécution, vous devriez avoir :
- ~22 utilisateurs
- 5 organisations
- 5 équipes
- ~20 associations utilisateurs-organisations
- ~22 propriétés

## 🧪 Tests

Vous pouvez tester l'authentification avec n'importe quel compte :

```bash
curl -X POST http://localhost:8081/api/identity/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "directeur@paris-immobilier.fr",
    "password": "password123"
  }'
```

