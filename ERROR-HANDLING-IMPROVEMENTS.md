# Améliorations de la Gestion d'Erreurs et Validation

## ✅ Modifications Apportées

### 1. Backend - Gestion d'Erreurs Améliorée

#### `SubscribeService.java`

**Validations ajoutées :**
- ✅ Validation de l'email (format, longueur)
- ✅ Validation du mot de passe (longueur, force optionnelle)
- ✅ Validation du prénom et nom (longueur, trim)
- ✅ Validation du nom d'organisation (longueur, caractères autorisés)
- ✅ Validation du téléphone (optionnel, longueur)
- ✅ Validation du plan (existence, actif)

**Gestion d'erreurs :**
- ✅ Utilisation de `BadRequestException` pour les erreurs de validation
- ✅ Utilisation de `ResourceNotFoundException` pour les ressources introuvables
- ✅ Messages d'erreur en français et détaillés
- ✅ Try-catch autour de chaque opération critique
- ✅ Logs détaillés pour le debugging

**Validations spécifiques :**

1. **Email** :
   - Format valide (regex)
   - Longueur max 255 caractères
   - Conversion en minuscules et trim

2. **Mot de passe** :
   - Minimum 8 caractères
   - Maximum 255 caractères
   - Validation optionnelle de la force (majuscule, minuscule, chiffre)
   - Note : La validation stricte avec caractères spéciaux est commentée mais disponible

3. **Nom d'organisation** :
   - Minimum 2 caractères
   - Maximum 255 caractères
   - Caractères autorisés : lettres, chiffres, espaces, tirets, points, underscores
   - Support des caractères accentués (À-ÿ)

4. **Plan** :
   - Vérification de l'existence
   - Vérification que le plan est actif
   - Vérification qu'il n'y a pas déjà un abonnement actif

#### `AuthController.java`

**Améliorations :**
- ✅ Utilisation des exceptions personnalisées (BadRequestException, ResourceNotFoundException)
- ✅ Les exceptions sont propagées au `GlobalExceptionHandler` pour un traitement uniforme
- ✅ Logs détaillés pour chaque type d'erreur
- ✅ Messages d'erreur plus informatifs

### 2. Frontend - Validation et Gestion d'Erreurs

#### `Subscribe.vue`

**Validations ajoutées :**
- ✅ Validation du format email (regex améliorée)
- ✅ Validation de la longueur des champs
- ✅ Validation de la force du mot de passe (majuscule, minuscule, chiffre)
- ✅ Validation du nom d'organisation (caractères autorisés)
- ✅ Messages d'erreur détaillés et en français

**Gestion d'erreurs améliorée :**
- ✅ Extraction des messages d'erreur depuis `err.response.data.message`
- ✅ Support des erreurs de validation avec détails
- ✅ Messages d'erreur par défaut plus clairs
- ✅ Affichage des erreurs de validation par champ

#### `SubscribeRequest.java`

**Validations Bean Validation :**
- ✅ `@NotBlank` pour tous les champs requis
- ✅ `@Email` pour la validation de l'email
- ✅ `@Size` pour la longueur des champs
- ✅ `@Pattern` pour la validation du mot de passe (majuscule, minuscule, chiffre)
- ✅ Messages d'erreur en français

## 📋 Règles de Validation

### Email
- ✅ Format valide (regex)
- ✅ Longueur max : 255 caractères
- ✅ Conversion automatique en minuscules

### Mot de passe
- ✅ Longueur : 8-255 caractères
- ✅ Doit contenir au moins :
  - Une majuscule (A-Z)
  - Une minuscule (a-z)
  - Un chiffre (0-9)
- ⚠️ Caractères spéciaux : optionnel (validation commentée)

### Prénom / Nom
- ✅ Longueur : 1-100 caractères
- ✅ Trim automatique

### Nom d'organisation
- ✅ Longueur : 2-255 caractères
- ✅ Caractères autorisés : lettres, chiffres, espaces, tirets (-), points (.), underscores (_)
- ✅ Support des caractères accentués (À-ÿ)
- ✅ Trim automatique

### Téléphone (optionnel)
- ✅ Longueur max : 20 caractères
- ✅ Trim automatique

### Plan
- ✅ Doit exister dans la base de données
- ✅ Doit être actif
- ✅ L'organisation ne doit pas avoir déjà un abonnement actif

## 🔍 Messages d'Erreur

### Erreurs de Validation

| Erreur | Message |
|--------|---------|
| Email invalide | "Le format de l'email est invalide" |
| Email existant | "Un compte avec cet email existe déjà. Veuillez utiliser un autre email ou vous connecter." |
| Mot de passe trop court | "Le mot de passe doit contenir au moins 8 caractères" |
| Mot de passe faible | "Le mot de passe doit contenir au moins une majuscule, une minuscule et un chiffre" |
| Nom organisation invalide | "Le nom de l'organisation contient des caractères invalides. Utilisez uniquement des lettres, chiffres, espaces, tirets, points et underscores" |
| Nom organisation existant | "Une organisation avec ce nom existe déjà. Veuillez choisir un autre nom." |
| Plan introuvable | "Plan with id {id} not found" |
| Plan inactif | "Le plan sélectionné n'est pas actif. Veuillez choisir un autre plan." |
| Abonnement existant | "Cette organisation a déjà un abonnement actif" |

### Erreurs Techniques

| Erreur | Message |
|--------|---------|
| Erreur création utilisateur | "Erreur lors de la création de l'utilisateur: {détails}" |
| Erreur création organisation | "Erreur lors de la création de l'organisation: {détails}" |
| Erreur assignation | "Erreur lors de l'assignation de l'utilisateur à l'organisation: {détails}" |
| Erreur création abonnement | "Erreur lors de la création de l'abonnement: {détails}" |
| Erreur génération tokens | "Erreur lors de la génération des tokens d'authentification" |
| Erreur inattendue | "Une erreur inattendue s'est produite lors de l'inscription. Veuillez réessayer." |

## 🧪 Tests de Validation

### Test 1 : Email invalide
```json
{
  "firstName": "Test",
  "lastName": "User",
  "email": "invalid-email",
  "password": "Password123",
  "organizationName": "Test Org",
  "planId": 1
}
```
**Attendu** : Erreur "Le format de l'email est invalide"

### Test 2 : Mot de passe faible
```json
{
  "firstName": "Test",
  "lastName": "User",
  "email": "test@example.com",
  "password": "password",
  "organizationName": "Test Org",
  "planId": 1
}
```
**Attendu** : Erreur "Le mot de passe doit contenir au moins une majuscule, une minuscule et un chiffre"

### Test 3 : Nom d'organisation invalide
```json
{
  "firstName": "Test",
  "lastName": "User",
  "email": "test@example.com",
  "password": "Password123",
  "organizationName": "Test@Org#123",
  "planId": 1
}
```
**Attendu** : Erreur "Le nom de l'organisation contient des caractères invalides..."

### Test 4 : Plan introuvable
```json
{
  "firstName": "Test",
  "lastName": "User",
  "email": "test@example.com",
  "password": "Password123",
  "organizationName": "Test Org",
  "planId": 99999
}
```
**Attendu** : Erreur "Plan with id 99999 not found"

## 📝 Notes

- Les validations frontend et backend sont synchronisées
- Les messages d'erreur sont en français pour une meilleure UX
- Les logs détaillés facilitent le debugging
- Les exceptions personnalisées permettent un traitement uniforme via `GlobalExceptionHandler`
- La validation du mot de passe peut être renforcée en décommentant le pattern strict

