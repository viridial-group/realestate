# 📦 Guide de Publication sur npmjs

Ce guide explique comment publier le package `@viridial/shared` sur npmjs.

## 📋 Prérequis

1. **Compte npm** : Créez un compte sur [npmjs.com](https://www.npmjs.com/signup)
2. **Organisation npm** : Créez une organisation `@viridial` sur npmjs (optionnel mais recommandé)
   - Profil npm : [viridialdev](https://www.npmjs.com/~viridialdev)
3. **Authentification** : Connectez-vous à npm

```bash
npm login
```

## 🚀 Publication

> **Note** : Le package compile automatiquement TypeScript en JavaScript avant la publication grâce aux scripts `prepublishOnly` et `prepack`.

### Méthode 1 : Script automatique (Recommandé)

```bash
cd frontend/shared

# Publication avec version patch (1.0.1 -> 1.0.2)
./scripts/publish.sh patch

# Publication avec version minor (1.0.1 -> 1.1.0)
./scripts/publish.sh minor

# Publication avec version major (1.0.1 -> 2.0.0)
./scripts/publish.sh major

# Publication en version beta
./scripts/publish.sh beta
```

### Méthode 2 : Commandes npm manuelles

```bash
cd frontend/shared

# 1. Vérifier que vous êtes connecté
npm whoami

# 2. Compiler TypeScript (automatique avant publication)
npm run build

# 3. Vérifier les informations du package
npm pkg get name version

# 4. Incrémenter la version (optionnel)
npm version patch   # 1.0.1 -> 1.0.2
npm version minor   # 1.0.1 -> 1.1.0
npm version major   # 1.0.1 -> 2.0.0

# 5. Publier le package (compile automatiquement avant)
npm publish --access public

# Ou pour une version beta
npm publish --tag beta --access public
```

## 📝 Configuration du package.json

Le `package.json` est déjà configuré avec :

- ✅ **name** : `@viridial/shared` (scope npm)
- ✅ **version** : Version sémantique
- ✅ **main** : `./dist/index.js` (fichier JavaScript compilé)
- ✅ **types** : `./dist/index.d.ts` (définitions TypeScript)
- ✅ **files** : Fichiers à inclure dans le package (seulement `dist/`)
- ✅ **exports** : Points d'entrée du module (ESM et CommonJS)
- ✅ **peerDependencies** : Dépendances requises par les consommateurs
- ✅ **publishConfig** : Configuration de publication
- ✅ **scripts** : 
  - `build` : Compile TypeScript en JavaScript
  - `prepublishOnly` : Compile automatiquement avant publication
  - `prepack` : Compile automatiquement avant empaquetage

## 🔧 Personnalisation

### 1. Mettre à jour les métadonnées

Éditez `package.json` pour mettre à jour :

```json
{
  "author": {
    "name": "Votre Nom",
    "email": "votre@email.com"
  },
  "repository": {
    "url": "https://github.com/votre-org/realestate.git"
  },
  "bugs": {
    "url": "https://github.com/votre-org/realestate/issues"
  }
}
```

### 2. Créer une organisation npm (Recommandé)

1. Allez sur [npmjs.com](https://www.npmjs.com)
2. Créez une organisation `@viridial` (ou utilisez votre compte `viridialdev`)
3. Ajoutez les membres de l'équipe
4. Le package sera publié sous `@viridial/shared`

### 3. Configurer les fichiers à publier

Le fichier `.npmignore` contrôle ce qui est exclu. Par défaut, il exclut :
- `node_modules/`
- Fichiers de test
- Fichiers de build
- Fichiers de développement

## 📦 Installation après publication

Une fois publié, le package peut être installé avec :

```bash
npm install @viridial/shared
```

Ou avec une version spécifique :

```bash
npm install @viridial/shared@1.0.2
```

## 🔄 Mise à jour du package

Pour publier une nouvelle version :

1. **Modifier le code** dans `frontend/shared`
2. **Incrémenter la version** :
   ```bash
   npm version patch  # ou minor, major
   ```
3. **Publier** :
   ```bash
   npm publish --access public
   ```

## 🏷️ Versions et Tags

- **Version normale** : `npm publish` publie sur le tag `latest`
- **Version beta** : `npm publish --tag beta` publie sur le tag `beta`
- **Installation beta** : `npm install @viridial/shared@beta`

## ✅ Checklist avant publication

- [ ] Code testé et fonctionnel
- [ ] `package.json` à jour avec les bonnes métadonnées
- [ ] `README.md` complet et à jour
- [ ] Version incrémentée si nécessaire
- [ ] Connecté à npm (`npm whoami`)
- [ ] Aucune erreur de linting
- [ ] Fichiers `.npmignore` configuré correctement

## 🐛 Dépannage

### Erreur : "You do not have permission to publish"

**Solution** : Vérifiez que vous êtes connecté et avez les droits sur l'organisation :
```bash
npm login
npm whoami
```

### Erreur : "Package name already exists"

**Solution** : Le nom du package est déjà pris. Changez le nom dans `package.json` ou contactez le propriétaire.

### Erreur : "Invalid package name"

**Solution** : Le nom doit suivre les conventions npm :
- Pas de majuscules
- Pas d'espaces
- Peut contenir des tirets et underscores
- Pour les scopes : `@scope/package-name`

## 📚 Ressources

- [Documentation npm](https://docs.npmjs.com/)
- [Semantic Versioning](https://semver.org/)
- [npm Package Best Practices](https://docs.npmjs.com/packages-and-modules/contributing-packages-to-the-registry)

## 🔐 Sécurité

⚠️ **Important** : Ne publiez jamais :
- Des secrets ou tokens
- Des fichiers `.env`
- Des clés API
- Des informations sensibles

Le fichier `.npmignore` exclut automatiquement ces fichiers.

