Prochaines étapes recommandées
Option 1 : Compléter le module Billing (priorité haute)
Temps estimé : 2-3 heures
Pourquoi :
Interface prête, mais les appels API sont en TODO
Fonctionnalité importante pour la monétisation
Impact business direct
À faire :
Créer le service API billingService dans frontend/shared/api/
Connecter les endpoints backend (subscriptions, invoices, stats)
Implémenter les fonctions TODO dans billing/Index.vue
Ajouter la gestion des factures (liste, détails, téléchargement)
Option 2 : Compléter le module Notifications (priorité moyenne)
Temps estimé : 1-2 heures
Pourquoi :
Interface prête, mais les actions sont en TODO
Améliore l'expérience utilisateur
Relativement rapide à implémenter
À faire :
Créer le service API notificationService
Implémenter markAsRead, markAllAsRead, deleteNotification
Connecter l'API backend
Ajouter les notifications en temps réel (optionnel)
Option 3 : Créer le module Documents (priorité moyenne)
Temps estimé : 3-4 heures
Pourquoi :
Dossier existe mais vide
Fonctionnalité importante pour gérer les documents des propriétés
Complète le système de gestion
À faire :
Créer documents/Index.vue (liste des documents)
Créer documents/Upload.vue (upload de documents)
Intégrer avec documentService existant
Ajouter la prévisualisation et le téléchargement
Option 4 : Améliorer la validation des formulaires (priorité basse)
Temps estimé : 2-3 heures
Pourquoi :
VeeValidate est installé mais peu utilisé
Améliore l'UX avec validation en temps réel
Réduit les erreurs de saisie
À faire :
Créer les schémas Zod pour les formulaires
Intégrer VeeValidate dans properties/Form.vue
Ajouter la validation dans users/UserDialog.vue
Améliorer les messages d'erreur
Option 5 : Développer le module Workflows (priorité basse)
Temps estimé : 4-6 heures
Pourquoi :
Module en placeholder
Fonctionnalité avancée pour les workflows d'approbation
Nécessite un backend complet
À faire :
Créer l'interface de gestion des workflows
Créer les formulaires d'approbation
Intégrer avec le backend Workflow Service
Ajouter la timeline des approbations
Recommandation
Commencer par Option 1 (Billing) car :
Impact business direct
Interface déjà prête, il reste à connecter l'API
Temps d'implémentation court (2-3 heures)
Complète une fonctionnalité importante
Ensuite Option 2 (Notifications) pour améliorer l'UX rapidement.
Souhaitez-vous que je commence par l'Option 1 (Billing) ?


clean package -DskipTests
java -jar target/*.jar

Exemple :

kill -9 1234

✅ Vérifier que le port est libéré
lsof -i :5432