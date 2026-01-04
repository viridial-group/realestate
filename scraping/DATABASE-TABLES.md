# Configuration des Tables de Base de Données

## Préfixe de Table

Le système de scraping utilise un préfixe configurable pour toutes les tables créées. Cela permet d'éviter les conflits avec d'autres tables de la base de données.

### Configuration

Le préfixe peut être configuré via la variable d'environnement `TABLE_PREFIX` dans le fichier `.env` :

```env
TABLE_PREFIX=scraping_
```

Par défaut, si `TABLE_PREFIX` n'est pas défini, le système utilise `scraping_` comme préfixe.

### Table Créée

Avec le préfixe par défaut, la table suivante sera créée :

- **`scraping_agencies`** : Table principale pour stocker les agences immobilières scrapées

### Structure de la Table `scraping_agencies`

```sql
CREATE TABLE scraping_agencies (
  id SERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL UNIQUE,
  description TEXT,
  email VARCHAR(255),
  phone VARCHAR(50),
  address TEXT,
  city VARCHAR(100),
  postal_code VARCHAR(20),
  country VARCHAR(100) DEFAULT 'France',
  website TEXT,
  domain VARCHAR(255),
  logo_url TEXT,
  source VARCHAR(100),
  source_url TEXT,
  metadata JSONB,
  active BOOLEAN DEFAULT true,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
```

### Index Créés

Les index suivants sont automatiquement créés pour optimiser les requêtes :

- `idx_scraping_agencies_email` : Index sur le champ email (si non null)
- `idx_scraping_agencies_city` : Index sur le champ city (si non null)
- `idx_scraping_agencies_country` : Index sur le champ country (si non null)
- `idx_scraping_agencies_source` : Index sur le champ source (si non null)
- `idx_scraping_agencies_active` : Index sur le champ active
- `idx_scraping_agencies_created_at` : Index sur le champ created_at

### Création Automatique

Les tables et index sont créés automatiquement lors de la première connexion à la base de données. Le système vérifie si les tables existent avant de les créer, donc il est sûr d'exécuter le script plusieurs fois.

### Exemple d'Utilisation

Si vous définissez `TABLE_PREFIX=my_prefix_` dans votre `.env`, la table sera nommée `my_prefix_agencies`.

### Migration

Si vous changez le préfixe après avoir déjà créé des tables, vous devrez :

1. Sauvegarder les données existantes
2. Modifier `TABLE_PREFIX` dans `.env`
3. Relancer le scraper (les nouvelles tables seront créées avec le nouveau préfixe)
4. Migrer les données si nécessaire

