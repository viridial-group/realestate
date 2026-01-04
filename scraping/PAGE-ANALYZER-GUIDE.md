# Guide d'Analyse de Pages Web

## Outil d'Analyse de Structure de Page

Un outil a été créé pour analyser automatiquement la structure HTML d'une page web et suggérer les meilleurs sélecteurs CSS pour le scraping.

## Utilisation

### Analyser une page

```bash
npm run analyze-page -- <url>
```

### Exemples

```bash
# Analyser AnnuaireImmo
npm run analyze-page -- "https://www.annuaireimmo.fr/recherche?q=agence+immobiliere+paris"

# Analyser RankingImmo
npm run analyze-page -- "https://www.rankingimmo.fr/recherche?q=agence+immobiliere"

# Analyser Nestoria
npm run analyze-page -- "https://www.nestoria.fr/paris/agence-immobiliere"
```

## Ce que l'outil fait

1. **Navigue vers la page** avec Puppeteer
2. **Analyse la structure HTML** pour trouver :
   - Les conteneurs d'agences
   - Les sélecteurs pour les noms
   - Les sélecteurs pour les emails
   - Les sélecteurs pour les téléphones
   - Les sélecteurs pour les adresses
   - Les sélecteurs pour les sites web

3. **Extrait des exemples** de données pour validation
4. **Génère des suggestions** de sélecteurs optimaux
5. **Sauvegarde un screenshot** pour inspection visuelle

## Sortie de l'analyse

L'outil affiche :

```
=== PAGE ANALYSIS RESULTS ===

Title: [Titre de la page]
URL: [URL analysée]
Container Count: [Nombre de conteneurs trouvés]

=== SUGGESTED SELECTORS ===

Container: .agency-card
Name: h2, .agency-name
Email: a[href^="mailto:"]
Phone: a[href^="tel:"]
Address: [class*="address"]
Website: a[href^="http"]

=== SAMPLE DATA ===

Sample Names:
  1. Agence Immobilière Example 1
  2. Agence Immobilière Example 2

Sample Emails:
  1. contact@example.fr

Sample Phones:
  1. +33123456789
```

## Utiliser les résultats

Une fois l'analyse terminée :

1. **Vérifiez les sélecteurs suggérés** dans le code du scraper
2. **Comparez avec les sélecteurs actuels** dans le fichier du scraper
3. **Mettez à jour les sélecteurs** si les suggestions sont meilleures
4. **Testez le scraper** avec les nouveaux sélecteurs

## Améliorer les scrapers existants

Pour améliorer un scraper existant :

1. Analysez la page avec l'outil
2. Copiez les sélecteurs suggérés
3. Mettez à jour le fichier du scraper (ex: `AnnuaireImmoScraper.ts`)
4. Recompilez et testez

## Exemple de mise à jour

Si l'analyse suggère `.agency-card` comme conteneur :

```typescript
// Avant
const resultElements = document.querySelectorAll('.agency, .result');

// Après (avec la suggestion)
const resultElements = document.querySelectorAll('.agency-card');
```

## Dépannage

Si l'outil ne trouve rien :
- Vérifiez que l'URL est correcte
- Vérifiez que la page se charge correctement
- Regardez le screenshot sauvegardé dans `debug-screenshots/`
- Activez `DEBUG_SCREENSHOTS=true` pour plus de détails

