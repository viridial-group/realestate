# Guide d'Installation - Système de Scraping

## 📦 Installation

1. **Installer les dépendances**:
```bash
cd /Users/mac/poledata/realestate/scraping
npm install
```

2. **Créer le fichier `.env`**:
```bash
cp .env.example .env
```

3. **Configurer la base de données dans `.env`**:
```env
DB_HOST=localhost
DB_PORT=5432
DB_NAME=realestate_db
DB_USER=postgres
DB_PASSWORD=votre_mot_de_passe
```

## 🚀 Utilisation

### Compiler le projet
```bash
npm run build
```

### Exécuter le scraping
```bash
npm start
```

### Mode développement
```bash
npm run dev
```

## 📋 Structure du Projet

```
scraping/
├── src/
│   ├── config/
│   │   └── config.ts          # Configuration
│   ├── database/
│   │   └── db.ts              # Connexion PostgreSQL
│   ├── models/
│   │   └── Agency.ts          # Modèles de données
│   ├── scrapers/
│   │   ├── BaseScraper.ts     # Classe de base
│   │   ├── GoogleScraper.ts   # Scraper Google
│   │   ├── PagesJaunesScraper.ts
│   │   ├── LeboncoinScraper.ts
│   │   └── index.ts
│   ├── services/
│   │   └── ScrapingService.ts # Service principal
│   ├── utils/
│   │   ├── logger.ts          # Système de logs
│   │   └── validators.ts      # Validation des données
│   └── index.ts               # Point d'entrée
├── package.json
├── tsconfig.json
└── README.md
```

## 🔧 Configuration

Modifiez les paramètres dans `.env` :

- **SCRAPING_DELAY**: Délai entre les requêtes (ms)
- **SCRAPING_TIMEOUT**: Timeout des requêtes (ms)
- **MAX_RETRIES**: Nombre de tentatives
- **CONCURRENT_REQUESTS**: Requêtes simultanées
- **SEARCH_TERMS**: Termes de recherche (séparés par virgules)
- **CITIES**: Villes à rechercher (séparées par virgules)

## 📊 Résultats

Les agences sont sauvegardées dans la table `organizations` de votre base de données PostgreSQL.

