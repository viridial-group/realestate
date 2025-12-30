# 🚀 GitHub - Préparation et Push

**Date :** Décembre 2024

---

## 📋 Checklist avant Push

### 1. Fichiers à Vérifier

- [x] ✅ `.gitignore` créé et configuré
- [x] ✅ `README.md` principal créé
- [x] ✅ Documentation complète dans `architectures/`
- [x] ✅ Fichiers inutiles supprimés
- [x] ✅ Variables d'environnement documentées (sans secrets)

### 2. Structure du Projet

```
realestate/
├── .gitignore
├── README.md
├── architectures/
│   ├── README - Guide d'Architecture.md
│   ├── Stack Technique Définitif.md
│   ├── TODO - Plan d'Implémentation.md
│   ├── Docker - Configuration et Déploiement.md
│   ├── Variables d'Environnement.md
│   ├── Tests - Stratégie et Documentation.md
│   └── ... (autres documents)
└── [autres dossiers à créer lors de l'implémentation]
```

---

## 🔧 Commandes Git

### Initialisation

```bash
# 1. Initialiser Git (si pas déjà fait)
git init

# 2. Ajouter tous les fichiers
git add .

# 3. Commit initial
git commit -m "feat: Initial commit - Architecture et documentation complète

- Architecture microservices complète
- Documentation technique détaillée
- Stack technique défini (Spring Boot 3.3.1, Vue.js 3.4.27)
- Configuration Docker complète
- Variables d'environnement documentées
- Stratégie de tests définie
- TODO plan d'implémentation créé"
```

### Création du Repository GitHub

```bash
# 1. Créer le repository sur GitHub (via interface web)
# Nom: realestate
# Description: SaaS Immobilier - Real Estate Management Platform
# Visibilité: Private ou Public (selon besoin)

# 2. Ajouter le remote
git remote add origin https://github.com/viridial-group/realestate.git

# 3. Renommer la branche principale (si nécessaire)
git branch -M main

# 4. Push initial
git push -u origin main
```
git remote set-url origin https://<TOKEN>@github.com/viridial-group/realestate.git
---

## 📝 Structure des Commits

### Convention de Nommage

- `feat:` Nouvelle fonctionnalité
- `fix:` Correction de bug
- `docs:` Documentation
- `style:` Formatage, point-virgule manquant, etc.
- `refactor:` Refactoring
- `test:` Ajout de tests
- `chore:` Maintenance, configuration

### Exemples

```bash
git commit -m "docs: Ajout documentation Docker complète"
git commit -m "feat: Configuration variables d'environnement"
git commit -m "docs: Mise à jour diagrammes UML"
```

---

## 🔐 Sécurité

### Fichiers à NE JAMAIS Commiter

- `.env.local`
- `.env.prod`
- `*.secret`
- `*.key`
- `*.pem`
- `secrets/`
- Fichiers avec mots de passe

### Vérification avant Push

```bash
# Vérifier les fichiers qui seront commités
git status

# Vérifier le contenu des fichiers sensibles
git diff

# Vérifier .gitignore
cat .gitignore
```

---

## 📦 Branches

### Structure Recommandée

- `main` : Branche principale (production)
- `develop` : Branche de développement
- `feature/*` : Nouvelles fonctionnalités
- `fix/*` : Corrections de bugs
- `docs/*` : Documentation

### Création des Branches

```bash
# Créer et basculer sur develop
git checkout -b develop

# Créer une branche feature
git checkout -b feature/identity-service

# Créer une branche docs
git checkout -b docs/update-architecture
```

---

## 🏷️ Tags

### Création de Tags

```bash
# Tag pour version d'architecture
git tag -a v0.1.0-architecture -m "Version architecture complète"
git push origin v0.1.0-architecture

# Tag pour version de documentation
git tag -a v0.1.0-docs -m "Documentation complète"
git push origin v0.1.0-docs
```

---

## 📊 GitHub Actions (CI/CD)

### Fichier `.github/workflows/ci.yml`

```yaml
name: CI

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main, develop ]

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 21
      uses: actions/setup-java@v3
      with:
        java-version: '21'
        distribution: 'temurin'
    
    - name: Build with Maven
      run: mvn clean install -DskipTests
    
    - name: Run tests
      run: mvn test
    
    - name: Generate coverage report
      run: mvn jacoco:report
```

---

## ✅ Checklist Finale

Avant de push sur GitHub :

- [x] ✅ `.gitignore` configuré
- [x] ✅ Aucun secret dans les fichiers
- [x] ✅ README.md à jour
- [x] ✅ Documentation complète
- [x] ✅ Structure de projet claire
- [ ] ⏳ Tests locaux passent (quand code implémenté)
- [ ] ⏳ Code review effectué (quand code implémenté)

---

## 🚀 Commandes Finales

```bash
# 1. Vérifier l'état
git status

# 2. Ajouter tous les fichiers
git add .

# 3. Commit
git commit -m "docs: Architecture et documentation complète"

# 4. Push
git push -u origin main
```

---

**Dernière mise à jour :** Décembre 2024

