Prochaines étapes pour le module Users
Priorité 1 — Fonctionnalités manquantes critiques
Page de détail utilisateur (Detail.vue)
viewUser est un TODO (ligne 437-439)
Créer /users/:id avec :
Informations complètes
Historique d'activité (userService.getActivity)
Gestion des rôles/permissions
Gestion des organisations (assigner/retirer, définir principale)
Statistiques de connexion
Correction du filtre de recherche
Le paramètre search est déjà passé (ligne 390), mais vérifier que le backend le prend en charge
Priorité 2 — Améliorations UX
Actions en masse
Sélection multiple (checkbox)
Activer/désactiver/supprimer plusieurs utilisateurs
Aligner avec le module organizations
Export des données
Export CSV/Excel
Filtres appliqués
Amélioration du UserDialog
Gestion des organisations multiples
Sélection de rôles multiples
Validation renforcée
Priorité 3 — Fonctionnalités avancées
Gestion des organisations utilisateur
Assigner à plusieurs organisations
Définir l'organisation principale
Utiliser les endpoints /api/identity/organization-users
Historique d'activité
Timeline des actions
Logs de connexion
Utiliser userService.getActivity
Gestion des permissions
Interface pour assigner/retirer des rôles
Utiliser userService.assignRoles / removeRoles
Souhaitez-vous que je commence par la page de détail utilisateur ? C’est la fonctionnalité la plus visible manquante.