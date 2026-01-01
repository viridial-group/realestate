# 🔐 Pages d'Authentification

Pages d'authentification créées pour les trois projets frontend, inspirées du design moderne des templates [Sliced](https://srbthemes.kcubeinfotech.com/sliced/html/login.html).

## 📄 Pages Créées

### Pour chaque projet (admin, agent, public) :

1. **Login.vue** - Page de connexion
   - Connexion avec Google et Apple
   - Formulaire email/password
   - Lien "Forgot Password?"
   - Lien vers Signup

2. **Signup.vue** - Page d'inscription
   - Inscription avec Google et Apple
   - Formulaire complet (nom, email, password, confirm password)
   - Checkbox "I Accept the Terms and Conditions"
   - Lien vers Login

3. **ForgotPassword.vue** - Réinitialisation de mot de passe
   - Formulaire email
   - Instructions pour recevoir le lien de réinitialisation
   - Lien retour vers Login

## 🎨 Design & Style

### Caractéristiques du design :

- ✅ **Gradient Background** : Fond dégradé élégant (slate-50 to slate-100)
- ✅ **Card Centered** : Carte centrée avec ombre portée
- ✅ **Logo Section** : Logo avec badge coloré en haut
- ✅ **Social Login** : Boutons Google et Apple avec icônes SVG
- ✅ **Divider** : Séparateur "Or with Email"
- ✅ **Form Fields** : Champs de formulaire avec labels
- ✅ **Responsive** : Design responsive avec padding adaptatif
- ✅ **Dark Mode** : Support du mode sombre
- ✅ **Typography** : Titres et textes bien hiérarchisés

### Composants shadcn-vue utilisés :

- `Card` & `CardContent` - Conteneur principal
- `Button` - Boutons d'action et sociaux
- `Input` - Champs de saisie
- `Label` - Labels pour les formulaires
- `Separator` - Séparateur visuel

## 🛣️ Routes Configurées

### Admin (`frontend/admin`)
- `/login` - Page de connexion
- `/signup` - Page d'inscription
- `/forgot-password` - Réinitialisation

### Agent (`frontend/agent`)
- `/login` - Page de connexion
- `/signup` - Page d'inscription
- `/forgot-password` - Réinitialisation

### Public (`frontend/public`)
- `/login` - Page de connexion
- `/signup` - Page d'inscription
- `/forgot-password` - Réinitialisation

## 🚀 Utilisation

### Accéder aux pages :

```bash
# Admin
http://localhost:3001/login
http://localhost:3001/signup
http://localhost:3001/forgot-password

# Agent
http://localhost:3002/login
http://localhost:3002/signup
http://localhost:3002/forgot-password

# Public
http://localhost:3003/login
http://localhost:3003/signup
http://localhost:3003/forgot-password
```

## 🔧 Intégration API

Les pages contiennent des fonctions `handleLogin`, `handleSignup`, et `handleReset` qui doivent être connectées à l'API Gateway :

```typescript
// Exemple d'intégration API
const handleLogin = async () => {
  try {
    const response = await axios.post('/api/identity/auth/login', {
      email: form.value.email,
      password: form.value.password
    })
    // Stocker le token
    // Rediriger vers le dashboard
  } catch (error) {
    // Gérer l'erreur
  }
}
```

## 📝 Notes

- Les formulaires incluent la validation HTML5 de base
- Les états de chargement sont gérés avec `loading`
- Les liens de navigation utilisent `RouterLink` de Vue Router
- Le design est cohérent avec les templates Sliced
- Support complet du dark mode via Tailwind CSS

## 🎯 Prochaines Étapes

1. Connecter les formulaires à l'API Gateway
2. Ajouter la validation avec VeeValidate ou Zod
3. Implémenter la gestion des tokens JWT
4. Ajouter les toasts pour les notifications
5. Implémenter l'authentification Google/Apple (OAuth)

