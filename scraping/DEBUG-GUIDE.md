# Guide de Débogage du Système de Scraping

## Problème : Aucune agence trouvée (0 résultats)

Si le scraper ne trouve aucune agence, voici les étapes pour diagnostiquer le problème :

### 1. Activer le mode debug avec screenshots

Ajoutez dans votre fichier `.env` :
```env
DEBUG_SCREENSHOTS=true
LOG_LEVEL=debug
```

Cela permettra de :
- Capturer des screenshots de chaque page visitée
- Afficher plus de logs de débogage
- Voir les messages de la console du navigateur

### 2. Vérifier les screenshots

Les screenshots seront sauvegardés dans le dossier `debug-screenshots/`. Examinez-les pour voir :
- Si la page s'est chargée correctement
- Si un CAPTCHA est présent
- Si la page est bloquée
- Si la structure HTML est différente de ce qui est attendu

### 3. Vérifier les logs

Les logs détaillés vous indiqueront :
- Le nombre d'éléments trouvés avec chaque sélecteur CSS
- Les messages d'erreur de la console du navigateur
- Les indicateurs de blocage détectés

### 4. Causes possibles

#### a) Blocage anti-bot
- **Symptômes** : Page avec CAPTCHA, message "Access Denied", redirection vers une page d'erreur
- **Solutions** :
  - Augmenter les délais entre requêtes (`SCRAPING_DELAY`)
  - Utiliser des proxies rotatifs
  - Modifier le User-Agent
  - Utiliser un mode non-headless (temporairement pour tester)

#### b) Structure HTML changée
- **Symptômes** : Page charge correctement mais aucun élément trouvé
- **Solutions** :
  - Inspecter manuellement la page dans un navigateur
  - Mettre à jour les sélecteurs CSS dans les scrapers
  - Utiliser des sélecteurs plus génériques

#### c) JavaScript non exécuté
- **Symptômes** : Page blanche ou contenu manquant
- **Solutions** :
  - Augmenter le délai d'attente (`await this.delay()`)
  - Utiliser `waitUntil: 'networkidle2'` ou `'domcontentloaded'`
  - Attendre des sélecteurs spécifiques avec `page.waitForSelector()`

### 5. Tester manuellement

Pour tester un scraper spécifique :

1. Ouvrez la page dans un navigateur normal
2. Inspectez le HTML avec les outils de développement
3. Identifiez les sélecteurs CSS corrects
4. Mettez à jour le scraper avec les bons sélecteurs

### 6. Exemple de test manuel

```javascript
// Dans la console du navigateur
document.querySelectorAll('.g').length  // Pour Google
document.querySelectorAll('.bi-container').length  // Pour PagesJaunes
```

### 7. Améliorer les scrapers

Les scrapers ont maintenant plusieurs stratégies de fallback :
- Essai de multiples sélecteurs CSS
- Recherche de texte contenant des mots-clés
- Logs détaillés pour chaque étape

Si aucun résultat n'est trouvé, les logs indiqueront :
- Combien d'éléments ont été trouvés avec chaque sélecteur
- Si une stratégie de fallback a été utilisée
- Les indicateurs de blocage détectés

### 8. Solutions alternatives

Si le scraping direct ne fonctionne pas :
- Utiliser des APIs officielles si disponibles
- Utiliser des services de scraping tiers
- Scraper des sources alternatives (annuaires, réseaux sociaux)
- Utiliser des proxies résidentiels

