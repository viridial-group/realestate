# Guide de Publication - @viridial/shared

## 📦 Publication sur npm

### Prérequis
- Compte npm avec authentification 2FA activée
- Code OTP depuis votre authentificateur (Google Authenticator, Authy, etc.)

### Méthode 1 : Script interactif

```bash
cd frontend/shared
./publish.sh
```

Le script vous demandera le code OTP et publiera automatiquement.

### Méthode 2 : Commande directe

```bash
cd frontend/shared
npm publish --access public --otp=<VOTRE_CODE_OTP>
```

**Exemple :**
```bash
npm publish --access public --otp=123456
```

### Méthode 3 : Sans OTP (si 2FA désactivé)

```bash
cd frontend/shared
npm publish --access public
```

## 🔄 Réinstallation dans les projets frontend

Après publication, mettre à jour dans chaque projet :

```bash
# Admin
cd frontend/admin
npm install @viridial/shared@latest

# Agent (quand créé)
cd frontend/agent
npm install @viridial/shared@latest

# Public (quand créé)
cd frontend/public
npm install @viridial/shared@latest
```

## 📋 Vérification

Vérifier la version publiée :
```bash
npm view @viridial/shared version
```

Voir les détails du package :
```bash
npm view @viridial/shared
```

## ⚠️ Notes importantes

1. **Version** : La version est automatiquement incrémentée avec `npm version patch`
2. **Build** : Le build est exécuté automatiquement avant la publication (via `prepublishOnly`)
3. **OTP** : Le code OTP expire après 30 secondes, soyez prêt à le saisir rapidement
4. **Test local** : Le package local fonctionne déjà avec `file:../shared` dans `package.json`

## 🐛 Dépannage

### Erreur "EOTP"
- Vérifiez que vous avez entré le bon code OTP
- Le code expire rapidement, réessayez avec un nouveau code

### Erreur "403 Forbidden"
- Vérifiez que vous êtes connecté : `npm whoami`
- Vérifiez les permissions sur le package

### Erreur "Version already exists"
- Incrémentez la version : `npm version patch` (ou `minor`, `major`)

