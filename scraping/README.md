# Système de Scraping - Agences Immobilières

Ce système permet de collecter automatiquement les informations des agences immobilières et des sociétés du domaine immobilier depuis Internet et de les sauvegarder dans la base de données PostgreSQL.

## 🚀 Installation

### Prérequis

- Node.js 18+ et npm
- PostgreSQL avec la base de données `realestate_db`
- Accès Internet

### Installation des dépendances

```bash
npm install
```

### Configuration

1. Copiez le fichier `.env.example` vers `.env`:
```bash
cp .env.example .env
```

2. Modifiez le fichier `.env` avec vos paramètres de base de données:
```env
DB_HOST=localhost
DB_PORT=5432
DB_NAME=realestate_db
DB_USER=postgres
DB_PASSWORD=your_password
```

## 📋 Utilisation

### Compilation TypeScript

```bash
npm run build
```

### Exécution

```bash
# Mode production (compilé)
npm start

# Mode développement (avec ts-node)
npm run dev
```

### Script combiné

```bash
npm run scrape
```

## 🏗️ Architecture

```
scraping/
├── src/
│   ├── config/          # Configuration
│   ├── database/        # Connexion et opérations DB
│   ├── models/          # Modèles de données
│   ├── scrapers/        # Scrapers pour différents sites
│   ├── services/        # Services métier
│   └── utils/           # Utilitaires (logger, validators)
├── dist/                # Code compilé
├── logs/                # Fichiers de logs
└── package.json
```

## 🔍 Scrapers Disponibles

1. **GoogleScraper** - Recherche Google pour trouver des agences
2. **PagesJaunesScraper** - PagesJaunes.fr pour les informations détaillées
3. **LeboncoinScraper** - Leboncoin.fr pour les annonces d'agences

## 📊 Données Collectées

Pour chaque agence, le système collecte :
- Nom de l'agence
- Description
- Email
- Téléphone
- Adresse complète
- Ville
- Code postal
- Pays
- Site web
- Domaine
- Logo (URL)
- Source de la donnée

## 🗄️ Base de Données

Les données sont sauvegardées dans la table `organizations` avec les champs suivants :
- `name` - Nom de l'organisation
- `description` - Description
- `email` - Email
- `phone` - Téléphone
- `address` - Adresse
- `city` - Ville
- `postal_code` - Code postal
- `country` - Pays
- `domain` - Domaine du site web
- `logo_url` - URL du logo
- `active` - Statut actif/inactif

## ⚙️ Configuration Avancée

### Paramètres de scraping

Dans le fichier `.env` :
- `SCRAPING_DELAY` - Délai entre les requêtes (ms)
- `SCRAPING_TIMEOUT` - Timeout des requêtes (ms)
- `MAX_RETRIES` - Nombre de tentatives en cas d'échec
- `CONCURRENT_REQUESTS` - Nombre de requêtes simultanées

### Termes de recherche

Modifiez `SEARCH_TERMS` et `CITIES` dans `.env` pour personnaliser les recherches.

## 📝 Logs

Les logs sont sauvegardés dans le dossier `logs/` :
- `scraping.log` - Logs généraux
- `error.log` - Erreurs uniquement

## 🔒 Sécurité et Éthique

- Respect des `robots.txt`
- Délais entre les requêtes pour ne pas surcharger les serveurs
- Rotation des User-Agents
- Gestion des erreurs et retry automatique

## 🚧 Améliorations Futures

- [ ] Support de Puppeteer pour les sites JavaScript lourds
- [ ] Scraping de sites supplémentaires (SeLoger, PAP, etc.)
- [ ] Extraction d'emails depuis les sites web
- [ ] Géocodage automatique des adresses
- [ ] Détection de doublons améliorée
- [ ] Interface web pour monitorer le scraping
- [ ] Export des données en CSV/JSON

## 📄 Licence

ISC

