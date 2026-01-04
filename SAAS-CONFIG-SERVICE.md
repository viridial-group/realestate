# Service de Configuration SaaS - Pays et Villes

## 📋 Vue d'ensemble

Ce service permet de gérer les paramètres SaaS, notamment les pays et leurs villes avec leurs coordonnées géographiques et données importantes. Le système utilise un cache Redis avec invalidation automatique lors des opérations de création, mise à jour ou suppression.

## 🏗️ Architecture

### Backend (Identity Service)

#### Entités
- **Country** (`com.realestate.identity.entity.Country`)
  - Code ISO 3166-1 alpha-2 (ex: FR, US)
  - Nom, code3, phoneCode, currency, timezone
  - Coordonnées géographiques (latitude, longitude)
  - Données importantes (JSON)
  - Relation OneToMany avec City

- **City** (`com.realestate.identity.entity.City`)
  - Nom, code postal
  - Relation ManyToOne avec Country
  - Coordonnées géographiques
  - Région, département, timezone
  - Données importantes (JSON)

#### Services
- **CountryService** : Gestion des pays avec cache
  - `getAllActiveCountries()` - Cache: `countries::all-active`
  - `getCountryById(id)` - Cache: `country::{id}`
  - `getCountryByCode(code)` - Cache: `country::code-{code}`
  - `createCountry()` - Invalide tous les caches
  - `updateCountry()` - Invalide tous les caches
  - `deleteCountry()` - Invalide tous les caches

- **CityService** : Gestion des villes avec cache
  - `getAllActiveCities()` - Cache: `cities::all-active`
  - `getCitiesByCountryCode(code)` - Cache: `citiesByCountry::{code}`
  - `getCitiesByCountryId(id)` - Cache: `citiesByCountry::country-id-{id}`
  - `getCityById(id)` - Cache: `city::{id}`
  - `createCity()` - Invalide tous les caches
  - `updateCity()` - Invalide tous les caches
  - `deleteCity()` - Invalide tous les caches

#### Cache Configuration
- **TTL** : 1 heure pour tous les caches de pays/villes
- **Invalidation automatique** : Lors de toute opération d'écriture (create, update, delete)
- **Caches** :
  - `countries` : Liste des pays actifs
  - `country` : Détails d'un pays
  - `cities` : Liste des villes actives
  - `city` : Détails d'une ville
  - `citiesByCountry` : Villes par pays

### Frontend

#### Services API
- **countryService** (`frontend/shared/api/country.service.ts`)
  - `getAllActive()` : Récupère tous les pays actifs
  - `getById(id)` : Récupère un pays par ID
  - `getByCode(code)` : Récupère un pays par code ISO
  - `create(country)` : Crée un nouveau pays
  - `update(id, country)` : Met à jour un pays
  - `delete(id)` : Supprime un pays

- **cityService** (`frontend/shared/api/city.service.ts`)
  - `getAllActive()` : Récupère toutes les villes actives
  - `getByCountryCode(code)` : Récupère les villes d'un pays par code
  - `getByCountryId(id)` : Récupère les villes d'un pays par ID
  - `getById(id)` : Récupère une ville par ID
  - `create(city)` : Crée une nouvelle ville
  - `update(id, city)` : Met à jour une ville
  - `delete(id)` : Supprime une ville

#### Types TypeScript
- **Country** : Interface pour un pays
- **City** : Interface pour une ville
- **CountryCreate/CountryUpdate** : Types pour création/mise à jour
- **CityCreate/CityUpdate** : Types pour création/mise à jour

## 🔌 Endpoints API

### Countries

```
GET    /api/identity/countries              # Liste des pays actifs (public)
GET    /api/identity/countries/{id}         # Détails d'un pays (public)
GET    /api/identity/countries/code/{code}  # Pays par code ISO (public)
POST   /api/identity/countries              # Créer un pays (ADMIN)
PUT    /api/identity/countries/{id}         # Mettre à jour un pays (ADMIN)
DELETE /api/identity/countries/{id}         # Supprimer un pays (ADMIN)
```

### Cities

```
GET    /api/identity/cities                      # Liste des villes actives (public)
GET    /api/identity/cities/{id}                 # Détails d'une ville (public)
GET    /api/identity/cities/country/{code}       # Villes par code pays (public)
GET    /api/identity/cities/country-id/{id}      # Villes par ID pays (public)
POST   /api/identity/cities                      # Créer une ville (ADMIN)
PUT    /api/identity/cities/{id}                 # Mettre à jour une ville (ADMIN)
DELETE /api/identity/cities/{id}                 # Supprimer une ville (ADMIN)
```

## 🔐 Sécurité

- **Lecture (GET)** : Accès public (pas d'authentification requise)
- **Écriture (POST, PUT, DELETE)** : Nécessite l'authentification et les permissions ADMIN

## 📊 Base de données

### Tables

#### countries
- `id` : BIGSERIAL PRIMARY KEY
- `code` : VARCHAR(2) UNIQUE (ISO 3166-1 alpha-2)
- `name` : VARCHAR(100)
- `code3` : VARCHAR(3) (ISO 3166-1 alpha-3)
- `phone_code` : VARCHAR(10)
- `currency` : VARCHAR(10)
- `currency_symbol` : VARCHAR(10)
- `timezone` : VARCHAR(50)
- `flag_emoji` : TEXT
- `description` : TEXT
- `latitude` : DECIMAL(10, 7)
- `longitude` : DECIMAL(10, 7)
- `important_data` : JSONB
- `active` : BOOLEAN DEFAULT true
- `display_order` : INTEGER DEFAULT 0
- `created_at` : TIMESTAMP
- `updated_at` : TIMESTAMP

#### cities
- `id` : BIGSERIAL PRIMARY KEY
- `name` : VARCHAR(255)
- `postal_code` : VARCHAR(20)
- `country_id` : BIGINT (FK vers countries)
- `latitude` : DECIMAL(10, 7)
- `longitude` : DECIMAL(10, 7)
- `region` : VARCHAR(100)
- `department` : VARCHAR(100)
- `timezone` : VARCHAR(50)
- `description` : TEXT
- `important_data` : JSONB
- `active` : BOOLEAN DEFAULT true
- `display_order` : INTEGER DEFAULT 0
- `created_at` : TIMESTAMP
- `updated_at` : TIMESTAMP

### Script SQL

Exécuter le script `scripts/create-countries-cities-tables.sql` pour créer les tables.

## 🚀 Utilisation

### Backend

```java
@Autowired
private CountryService countryService;

@Autowired
private CityService cityService;

// Récupérer tous les pays actifs (avec cache)
List<CountryDTO> countries = countryService.getAllActiveCountries();

// Récupérer les villes d'un pays (avec cache)
List<CityDTO> cities = cityService.getCitiesByCountryCode("FR");

// Créer un pays (invalide automatiquement le cache)
CountryDTO country = countryService.createCountry(countryDTO);
```

### Frontend

```typescript
import { countryService, cityService } from '@/shared'

// Récupérer tous les pays actifs
const countries = await countryService.getAllActive()

// Récupérer les villes d'un pays
const cities = await cityService.getByCountryCode('FR')

// Créer un pays (nécessite authentification ADMIN)
const newCountry = await countryService.create({
  code: 'FR',
  name: 'France',
  flagEmoji: '🇫🇷',
  currency: 'EUR',
  currencySymbol: '€',
  phoneCode: '+33',
  timezone: 'Europe/Paris',
  active: true,
  displayOrder: 1
})
```

## 📝 Notes importantes

1. **Cache** : Le cache est automatiquement invalidé lors de toute opération d'écriture
2. **Validation** : Les codes pays doivent être uniques
3. **Cascade** : La suppression d'un pays supprime automatiquement ses villes (CASCADE)
4. **Ordre d'affichage** : Utilisez `displayOrder` pour contrôler l'ordre d'affichage
5. **Données importantes** : Stockées en JSONB pour flexibilité

## 🔄 Prochaines étapes

1. Créer l'interface admin pour gérer les pays et villes
2. Intégrer les services dans les composants de filtres (SidebarFilters)
3. Ajouter des données de test (seed script)
4. Implémenter la recherche et le filtrage avancés

