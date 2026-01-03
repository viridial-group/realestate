# 📋 Analyse des Fonctionnalités Manquantes - SaaS Immobilier

**Date d'analyse :** Janvier 2025  
**Dernière mise à jour :** Janvier 2025  
**Version :** 1.1

---

## 📊 Vue d'ensemble

Ce document identifie les fonctionnalités manquantes dans le backend et le frontend admin en comparant l'état actuel avec les besoins d'un SaaS immobilier complet.

---

## ✅ Fonctionnalités Implémentées

### Backend
- ✅ Authentification JWT (login, register, refresh)
- ✅ Gestion des utilisateurs (CRUD)
- ✅ Gestion des rôles et permissions (RBAC)
- ✅ Gestion des organisations (CRUD, hiérarchie)
- ✅ Gestion des propriétés (CRUD complet)
- ✅ Recherche de propriétés (filtres multiples, Elasticsearch)
- ✅ Statistiques dashboard (propriétés, utilisateurs, organisations)
- ✅ Gestion des documents (upload, download)
- ✅ **Optimisation automatique d'images** (compression, redimensionnement)
- ✅ **Profil utilisateur complet** (mise à jour profil, préférences)
- ✅ **Paramètres d'organisation** (logo, adresse, contact, quotas, domaines)
- ✅ Workflows d'approbation (création, tâches)
- ✅ Notifications (création, lecture, archivage)
- ✅ Emailing (envoi, templates)
- ✅ Facturation (plans, abonnements, factures)
- ✅ Audit logs (création, recherche)
- ✅ Resource Service (ressources génériques)

### Frontend Admin
- ✅ Dashboard avec statistiques
- ✅ Gestion des utilisateurs (liste, création, édition, suppression)
- ✅ Gestion des propriétés (liste, création, édition, détail, carte)
- ✅ Gestion des organisations (liste, création, édition, détail)
- ✅ Gestion des rôles et permissions (liste, création, affectation)
- ✅ Gestion des documents (liste, upload)
- ✅ Gestion des workflows (liste, création, détail)
- ✅ Gestion de la facturation (abonnements, factures, plans)
- ✅ Audit et logs (liste, recherche)
- ✅ Notifications (liste, marquer comme lu)
- ✅ Documentation

---

## ❌ Fonctionnalités Manquantes - Backend

### 1. 🔐 Authentification & Sécurité

#### Manquantes
- ⚠️ **Réinitialisation de mot de passe** (Backend partiel, frontend non connecté)
  - ✅ Endpoints définis dans `API_ENDPOINTS` (`FORGOT_PASSWORD`, `RESET_PASSWORD`)
  - ❌ Endpoints backend `/api/identity/auth/forgot-password` et `/api/identity/auth/reset-password` non implémentés dans `AuthController`
  - ✅ Frontend : page `ForgotPassword.vue` existe mais non connectée aux endpoints
  - ❌ Pas de service backend pour générer/valider les tokens de réinitialisation

- ❌ **Vérification d'email** (inscription)
  - Pas d'endpoint pour vérifier l'email
  - Pas de système de tokens de vérification
  - Pas d'envoi automatique d'email de vérification

- ❌ **Authentification à deux facteurs (2FA)**
  - Pas d'endpoints pour activer/désactiver 2FA
  - Pas de génération de codes QR
  - Pas de validation de codes 2FA

- ❌ **Gestion des sessions**
  - Pas d'endpoint pour lister les sessions actives
  - Pas de déconnexion à distance
  - Pas de gestion des tokens révoqués

- ❌ **Rate limiting par utilisateur**
  - Pas de limitation de requêtes par utilisateur
  - Pas de protection contre les attaques brute force

- ❌ **OAuth2 / SSO**
  - Pas d'intégration OAuth2 (Google, Microsoft, etc.)
  - Pas de Single Sign-On (SSO)

---

### 2. 👥 Gestion des Utilisateurs

#### Manquantes
- ✅ **Profil utilisateur complet** (Implémenté)
  - ✅ Endpoint pour mettre à jour le profil (avatar, préférences) : `PUT /api/identity/users/me`
  - ✅ Gestion des préférences utilisateur (langue, timezone, notifications) : `PUT /api/identity/users/me/preferences`
  - ✅ Vue frontend complète : `/profile` avec formulaires pour profil et préférences

- ❌ **Gestion des équipes (Teams)**
  - Backend : `TeamController` existe mais non testé
  - Frontend : pas de vue pour gérer les équipes
  - Pas d'assignation d'utilisateurs aux équipes depuis le frontend

- ❌ **Import/Export d'utilisateurs**
  - Pas d'endpoint pour importer des utilisateurs (CSV/Excel)
  - Pas d'endpoint pour exporter la liste des utilisateurs

- ❌ **Statistiques utilisateurs avancées**
  - Pas de statistiques d'activité par utilisateur
  - Pas de graphiques d'évolution dans le temps
  - Pas de rapport d'utilisation par utilisateur

- ❌ **Gestion des invitations**
  - Pas d'endpoint pour inviter des utilisateurs par email
  - Pas de système de tokens d'invitation
  - Pas de gestion des invitations en attente

---

### 3. 🏢 Gestion des Organisations

#### Manquantes
- ✅ **Paramètres d'organisation** (Implémenté)
  - ✅ Endpoint pour gérer les paramètres (logo, adresse, contact) : `GET/PUT /api/identity/organizations/{id}/settings`
  - ✅ Configuration des quotas par organisation (max_properties, max_users, max_storage_gb)
  - ✅ Gestion des domaines personnalisés (JSON array)
  - ✅ Upload de logo intégré avec document-service
  - ✅ Vue frontend complète : `/organizations/:id/settings`

- ❌ **Statistiques d'organisation**
  - Pas de statistiques détaillées par organisation
  - Pas de rapport d'utilisation des ressources
  - Pas de graphiques d'évolution

- ❌ **Gestion des membres**
  - Backend : `OrganizationUserController` existe mais non testé
  - Frontend : pas de vue dédiée pour gérer les membres d'une organisation
  - Pas d'assignation de rôles par organisation depuis le frontend

- ❌ **Import/Export d'organisations**
  - Pas d'endpoint pour importer/exporter des organisations

---

### 4. 🏠 Gestion des Propriétés

#### Manquantes
- ⚠️ **Import/Export de propriétés** (Partiellement implémenté)
  - ✅ Export CSV/Excel côté frontend (basique, dans `Detail.vue` et `Index.vue`)
  - ❌ Pas d'endpoint backend pour exporter avec filtres avancés
  - ❌ Pas d'endpoint pour importer des propriétés (CSV/Excel)
  - ❌ Pas de validation d'import
  - ❌ Pas de gestion des erreurs d'import
  - ❌ Export limité à une seule propriété (Detail.vue) ou liste basique (Index.vue)

- ❌ **Duplication de propriétés**
  - Pas d'endpoint pour dupliquer une propriété
  - Pas de copie avec ou sans documents

- ❌ **Historique des modifications**
  - Pas de système de versioning des propriétés
  - Pas d'historique des changements (qui, quand, quoi)

- ❌ **Favoris/Watchlist**
  - Pas d'endpoint pour ajouter/retirer des favoris
  - Pas de liste de favoris par utilisateur

- ❌ **Comparaison de propriétés**
  - Pas d'endpoint pour comparer plusieurs propriétés
  - Pas de vue de comparaison dans le frontend

- ❌ **Statistiques avancées**
  - Pas de statistiques par agent/assigné
  - Pas de statistiques de performance (temps de vente, etc.)
  - Pas de graphiques d'évolution des prix

- ❌ **Géolocalisation avancée**
  - Pas d'endpoint pour rechercher par rayon (proximité)
  - Pas de calcul de distances
  - Pas de recherche par polygone

- ❌ **Visites et rendez-vous**
  - Pas d'endpoint pour gérer les visites
  - Pas de calendrier de disponibilité
  - Pas de système de réservation de visites

---

### 5. 📄 Gestion des Documents

#### Manquantes
- ❌ **Gestion des dossiers**
  - Pas d'endpoint pour créer/gérer des dossiers
  - Pas de hiérarchie de dossiers

- ❌ **Prévisualisation de documents**
  - Pas d'endpoint pour générer des thumbnails
  - Pas de prévisualisation PDF/images dans le navigateur

- ❌ **Versioning de documents**
  - Pas de système de versions pour les documents
  - Pas d'historique des modifications

- ❌ **Partage de documents**
  - Pas d'endpoint pour partager des documents avec des liens temporaires
  - Pas de gestion des permissions de partage

- ❌ **OCR et extraction de texte**
  - Pas d'extraction de texte depuis les images/PDF
  - Pas de recherche dans le contenu des documents

- ✅ **Compression et optimisation** (Implémenté)
  - ✅ Optimisation automatique des images (redimensionnement, compression)
  - ✅ Service `ImageOptimizationService` avec Thumbnailator
  - ✅ Configuration via `application.yml` (qualité, dimensions max, taille max)
  - ✅ Optimisation automatique pour images > 500KB
  - ✅ Préservation du ratio d'aspect et gestion des formats (JPEG, PNG, GIF)

---

### 6. 🔄 Workflows

#### Manquantes
- ❌ **Templates de workflows**
  - Pas de système de templates réutilisables
  - Pas de bibliothèque de workflows prédéfinis

- ❌ **Statistiques de workflows**
  - Pas de statistiques de performance (temps moyen, taux d'approbation)
  - Pas de graphiques d'évolution

- ❌ **Notifications automatiques**
  - Pas d'intégration complète avec le service de notifications
  - Pas de rappels automatiques pour les tâches en attente

- ❌ **Délégation de tâches**
  - Pas d'endpoint pour déléguer une tâche à un autre utilisateur
  - Pas de gestion des absences

- ❌ **Conditions et règles métier**
  - Pas de système de règles conditionnelles dans les workflows
  - Pas de workflows dynamiques basés sur les données

---

### 7. 💰 Facturation

#### Manquantes
- ❌ **Paiements en ligne**
  - Pas d'intégration avec des processeurs de paiement (Stripe, PayPal)
  - Pas de gestion des cartes bancaires
  - Pas de système de facturation automatique

- ❌ **Gestion des remises et promotions**
  - Pas d'endpoint pour créer des codes promo
  - Pas de système de remises personnalisées

- ❌ **Rapports financiers**
  - Pas de rapports de revenus détaillés
  - Pas d'export comptable
  - Pas de graphiques d'évolution des revenus

- ❌ **Gestion des taxes**
  - Pas de calcul automatique des taxes
  - Pas de gestion des taux de TVA par pays

- ❌ **Abonnements d'essai**
  - Pas de système d'essai gratuit
  - Pas de conversion automatique après essai

- ❌ **Upgrade/Downgrade de plan**
  - Pas d'endpoint pour changer de plan
  - Pas de calcul prorata
  - Pas de gestion des rétrofacturations

---

### 8. 🔔 Notifications

#### Manquantes
- ❌ **Préférences de notifications**
  - Pas d'endpoint pour gérer les préférences par canal (email, SMS, push)
  - Pas de gestion des heures de réception

- ❌ **Templates de notifications**
  - Pas de système de templates personnalisables
  - Pas de gestion des notifications par type d'événement

- ❌ **Notifications en temps réel**
  - Pas d'intégration WebSocket pour les notifications push
  - Pas de système de notifications en temps réel dans le frontend

- ❌ **Notifications groupées**
  - Pas de système de digests quotidiens/hebdomadaires
  - Pas de regroupement de notifications similaires

---

### 9. 📧 Emailing

#### Manquantes
- ❌ **Campagnes email**
  - Pas d'endpoint pour créer des campagnes
  - Pas de gestion des listes de diffusion
  - Pas de statistiques d'ouverture/clics

- ❌ **Templates avancés**
  - Pas d'éditeur de templates WYSIWYG
  - Pas de variables dynamiques dans les templates

- ❌ **Planification d'emails**
  - Pas d'envoi différé
  - Pas de planification d'emails récurrents

---

### 10. 📊 Audit & Reporting

#### Manquantes
- ❌ **Rapports personnalisés**
  - Pas de système de création de rapports personnalisés
  - Pas d'export de rapports (PDF, Excel)

- ❌ **Tableaux de bord personnalisables**
  - Pas de système de widgets configurables
  - Pas de sauvegarde de vues personnalisées

- ❌ **Alertes et seuils**
  - Pas de système d'alertes configurables
  - Pas de notifications basées sur des seuils

---

### 11. 🔍 Recherche Avancée

#### Manquantes
- ❌ **Recherche globale**
  - Pas d'endpoint de recherche globale (propriétés, utilisateurs, documents)
  - Pas de recherche unifiée dans Elasticsearch

- ❌ **Recherche sauvegardée**
  - Pas de système de recherches sauvegardées
  - Pas d'alertes sur nouvelles correspondances

- ❌ **Recherche par similarité**
  - Pas de recherche de propriétés similaires
  - Pas de recommandations basées sur l'historique

---

### 12. 🌐 API & Intégrations

#### Manquantes
- ❌ **API Keys**
  - Pas de gestion des clés API
  - Pas de limitation par clé API
  - Pas de rotation de clés

- ❌ **Webhooks**
  - Pas de système de webhooks
  - Pas de gestion des événements externes

- ❌ **Intégrations tierces**
  - Pas d'intégration CRM (Salesforce, HubSpot)
  - Pas d'intégration avec des portails immobiliers (Leboncoin, SeLoger)
  - Pas d'intégration avec des outils de signature électronique

---

### 13. 🗄️ Resource Service

#### Manquantes
- ❌ **Interface frontend**
  - Pas de vue pour gérer les ressources génériques
  - Pas d'utilisation du Resource Service dans le frontend

- ❌ **Partage inter-organisation**
  - Backend existe mais non testé
  - Frontend : pas d'interface pour partager des ressources

---

## ❌ Fonctionnalités Manquantes - Frontend Admin

### 1. 📊 Dashboard

#### Manquantes
- ❌ **Widgets personnalisables**
  - Pas de système de drag & drop pour réorganiser les widgets
  - Pas de sauvegarde de vues personnalisées

- ❌ **Graphiques avancés**
  - Graphiques basiques présents mais limités
  - Pas de graphiques interactifs (zoom, filtres temporels)
  - Pas de graphiques de comparaison (période vs période)

- ❌ **Filtres temporels**
  - Pas de sélection de période personnalisée
  - Pas de comparaison entre périodes

- ❌ **Export de rapports**
  - Pas d'export PDF/Excel du dashboard
  - Pas de génération de rapports automatiques

---

### 2. 👥 Gestion des Utilisateurs

#### Manquantes
- ❌ **Profil utilisateur**
  - Pas de page de profil utilisateur
  - Pas de modification d'avatar
  - Pas de gestion des préférences

- ❌ **Gestion des équipes**
  - Pas de vue pour créer/gérer les équipes
  - Pas d'assignation d'utilisateurs aux équipes
  - Pas de vue des membres d'une équipe

- ❌ **Import/Export**
  - Pas de fonctionnalité d'import CSV/Excel
  - Pas d'export de la liste des utilisateurs

- ❌ **Statistiques par utilisateur**
  - Pas de page de statistiques détaillées par utilisateur
  - Pas de graphiques d'activité

- ❌ **Invitations**
  - Pas de système d'invitation d'utilisateurs
  - Pas de gestion des invitations en attente

---

### 3. 🏢 Gestion des Organisations

#### Manquantes
- ✅ **Paramètres d'organisation** (Implémenté)
  - ✅ Page de paramètres complète : `/organizations/:id/settings`
  - ✅ Configuration du logo (upload via document-service)
  - ✅ Gestion de l'adresse et du contact
  - ✅ Configuration des quotas (propriétés, utilisateurs, stockage)
  - ✅ Gestion des domaines personnalisés (JSON array)

- ❌ **Gestion des membres**
  - Pas de vue dédiée pour gérer les membres
  - Pas d'assignation de rôles depuis l'interface
  - Pas de vue des permissions par membre

- ❌ **Statistiques d'organisation**
  - Pas de page de statistiques détaillées
  - Pas de rapports d'utilisation

- ❌ **Hiérarchie visuelle**
  - Pas de visualisation de la hiérarchie d'organisations
  - Pas d'arbre organisationnel interactif

---

### 4. 🏠 Gestion des Propriétés

#### Manquantes
- ⚠️ **Import/Export** (Partiellement implémenté)
  - ✅ Export CSV/Excel basique (une propriété ou liste simple)
  - ❌ Pas de fonctionnalité d'import CSV/Excel
  - ❌ Pas d'export avec filtres avancés appliqués
  - ❌ Pas de template d'import avec validation

- ❌ **Duplication**
  - Pas de bouton pour dupliquer une propriété
  - Pas de modal de configuration de duplication

- ❌ **Historique**
  - Pas d'affichage de l'historique des modifications
  - Pas de comparaison de versions

- ❌ **Favoris**
  - Pas de système de favoris
  - Pas de liste de favoris

- ❌ **Comparaison**
  - Pas de vue de comparaison côte à côte
  - Pas de sélection multiple pour comparer

- ❌ **Statistiques avancées**
  - Pas de page de statistiques détaillées par propriété
  - Pas de graphiques de performance

- ❌ **Visites et rendez-vous**
  - Pas de calendrier de visites
  - Pas de gestion des rendez-vous
  - Pas de système de réservation

---

### 5. 📄 Gestion des Documents

#### Manquantes
- ❌ **Gestion des dossiers**
  - Pas d'interface pour créer/gérer des dossiers
  - Pas de navigation par dossiers

- ❌ **Prévisualisation**
  - Pas de prévisualisation intégrée (PDF, images)
  - Pas de visionneuse de documents

- ❌ **Versioning**
  - Pas d'affichage des versions
  - Pas de restauration de version

- ❌ **Partage**
  - Pas d'interface pour partager des documents
  - Pas de génération de liens de partage

- ❌ **Recherche dans les documents**
  - Pas de recherche dans le contenu des documents
  - Pas de filtres par type de contenu

---

### 6. 🔄 Workflows

#### Manquantes
- ❌ **Templates**
  - Pas de bibliothèque de templates
  - Pas de création de templates depuis l'interface

- ❌ **Statistiques**
  - Pas de page de statistiques de workflows
  - Pas de graphiques de performance

- ❌ **Notifications en temps réel**
  - Pas de notifications push dans le navigateur
  - Pas de mise à jour en temps réel des tâches

- ❌ **Délégation**
  - Pas d'interface pour déléguer des tâches
  - Pas de gestion des absences

---

### 7. 💰 Facturation

#### Manquantes
- ❌ **Paiements**
  - Pas d'interface de paiement
  - Pas d'intégration avec Stripe/PayPal

- ❌ **Codes promo**
  - Pas de gestion des codes promo
  - Pas d'application de codes promo

- ❌ **Rapports financiers**
  - Pas de page de rapports financiers détaillés
  - Pas d'export comptable

- ❌ **Changement de plan**
  - Pas d'interface pour changer de plan
  - Pas de calcul prorata affiché

---

### 8. 🔔 Notifications

#### Manquantes
- ❌ **Préférences**
  - Pas de page de préférences de notifications
  - Pas de gestion des canaux par type d'événement

- ❌ **Templates**
  - Pas d'éditeur de templates de notifications
  - Pas de prévisualisation de notifications

- ❌ **Notifications en temps réel**
  - Pas de système de notifications push
  - Pas de badge de compteur en temps réel

---

### 9. 📊 Audit & Reporting

#### Manquantes
- ❌ **Rapports personnalisés**
  - Pas de créateur de rapports
  - Pas d'export de rapports personnalisés

- ❌ **Filtres avancés**
  - Filtres basiques présents mais limités
  - Pas de filtres complexes (ET/OU)
  - Pas de sauvegarde de filtres

- ❌ **Export avancé**
  - Export basique présent mais limité
  - Pas d'export avec formatage personnalisé
  - Pas d'export programmé

---

### 10. ⚙️ Paramètres & Configuration

#### Manquantes
- ❌ **Paramètres système**
  - Pas de page de paramètres système
  - Pas de configuration des fonctionnalités
  - Pas de gestion des feature flags

- ❌ **Paramètres utilisateur**
  - Pas de page de préférences utilisateur
  - Pas de gestion du profil (avatar, langue, timezone)

- ❌ **Thème et personnalisation**
  - Pas de sélection de thème (clair/sombre)
  - Pas de personnalisation de l'interface

- ❌ **Intégrations**
  - Pas de page de gestion des intégrations
  - Pas de configuration d'API keys
  - Pas de gestion des webhooks

---

### 11. 🔍 Recherche

#### Manquantes
- ❌ **Recherche globale**
  - Pas de barre de recherche globale
  - Pas de recherche unifiée (propriétés, utilisateurs, documents)

- ❌ **Recherche sauvegardée**
  - Pas de système de recherches sauvegardées
  - Pas d'alertes sur nouvelles correspondances

- ❌ **Filtres avancés**
  - Filtres basiques présents
  - Pas de filtres complexes avec opérateurs logiques
  - Pas de filtres par date personnalisée

---

### 12. 📱 Responsive & Mobile

#### Manquantes
- ❌ **Optimisation mobile**
  - Interface responsive basique mais à améliorer
  - Pas d'application mobile native
  - Pas de PWA (Progressive Web App)

---

### 13. 🌐 Internationalisation

#### Manquantes
- ❌ **Gestion complète i18n**
  - i18n présent mais incomplet
  - Pas de traduction complète de toutes les pages
  - Pas de gestion de la langue par utilisateur

---

### 14. 🔐 Sécurité Frontend

#### Manquantes
- ❌ **Gestion des sessions**
  - Pas d'affichage des sessions actives
  - Pas de déconnexion à distance

- ❌ **2FA**
  - Pas d'interface pour activer/désactiver 2FA
  - Pas de génération de codes QR

- ❌ **Historique de connexion**
  - Pas d'affichage de l'historique de connexion
  - Pas d'alertes sur connexions suspectes

---

## 🎯 Priorités Recommandées

### 🔥 Priorité Haute (Fonctionnalités Essentielles)

1. **Réinitialisation de mot de passe** (Backend + Frontend)
   - Connecter les endpoints existants au frontend
   - Tester le flux complet

2. **Import/Export de propriétés** (Backend + Frontend)
   - Endpoints d'import/export CSV/Excel
   - Interface d'import dans le frontend

3. **Gestion des équipes** (Frontend)
   - Créer les vues pour gérer les équipes
   - Connecter avec le backend existant

4. **Notifications en temps réel** (Backend + Frontend)
   - Intégration WebSocket
   - Notifications push dans le navigateur

5. ✅ **Paramètres utilisateur** (Backend + Frontend) - **IMPLÉMENTÉ**
   - ✅ Endpoints pour gérer le profil : `PUT /api/identity/users/me`
   - ✅ Page de préférences utilisateur : `/profile`

6. ✅ **Paramètres d'organisation** (Backend + Frontend) - **IMPLÉMENTÉ**
   - ✅ Endpoints : `GET/PUT /api/identity/organizations/{id}/settings`
   - ✅ Page complète : `/organizations/:id/settings`
   - ✅ Upload de logo intégré avec document-service

7. ✅ **Optimisation d'images** (Backend) - **IMPLÉMENTÉ**
   - ✅ Service `ImageOptimizationService` avec Thumbnailator
   - ✅ Compression et redimensionnement automatiques

---

### ⚡ Priorité Moyenne (Amélioration UX)

6. **Duplication de propriétés** (Backend + Frontend)
7. **Historique des modifications** (Backend + Frontend)
8. **Favoris/Watchlist** (Backend + Frontend)
9. **Statistiques avancées** (Backend + Frontend)
10. **Gestion des dossiers de documents** (Backend + Frontend)

---

### 💡 Priorité Basse (Nice to Have)

11. **2FA** (Backend + Frontend)
12. **OAuth2/SSO** (Backend + Frontend)
13. **Paiements en ligne** (Backend + Frontend)
14. **Intégrations tierces** (Backend + Frontend)
15. **Application mobile** (Nouveau projet)

---

## 📈 Statistiques

- **Total fonctionnalités manquantes identifiées :** ~77
- **Fonctionnalités implémentées récemment :** 3
  - ✅ Profil utilisateur complet
  - ✅ Paramètres d'organisation
  - ✅ Compression et optimisation d'images
- **Fonctionnalités critiques :** ~12
- **Fonctionnalités importantes :** ~25
- **Fonctionnalités optionnelles :** ~40

---

## 🔄 Prochaines Étapes Recommandées

1. **Phase 1 (1-2 semaines)**
   - Réinitialisation de mot de passe
   - Import/Export de propriétés
   - Gestion des équipes (frontend)

2. **Phase 2 (2-3 semaines)** - ✅ **EN COURS**
   - Notifications en temps réel
   - ✅ Paramètres utilisateur - **TERMINÉ**
   - ✅ Paramètres d'organisation - **TERMINÉ**
   - ✅ Optimisation d'images - **TERMINÉ**
   - Duplication de propriétés

3. **Phase 3 (3-4 semaines)**
   - Historique des modifications
   - Favoris/Watchlist
   - Statistiques avancées

---

**Note :** Ce document doit être mis à jour régulièrement au fur et à mesure de l'implémentation des fonctionnalités.

