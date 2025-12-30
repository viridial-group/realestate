1️⃣ Rôles et permissions globales (SaaS Admin)
Role	Description	Permissions
SaaS_Admin	Administrateur de la plateforme	manage_organizations, view_audit, manage_billing, manage_templates
Super_Admin	Super utilisateur global	full_access (accès à tout)

Ces rôles ne peuvent pas être modifiés par les organisations.

2️⃣ Rôles par défaut pour chaque organisation (template)
Role	Description	Permissions
Owner	Propriétaire de l’organisation	full_access_all_properties, manage_users, manage_teams, manage_workflows, manage_documents, manage_feature_flags, view_audit
Manager	Responsable / superviseur	create_edit_properties, approve_workflows, assign_users_to_properties, view_reports, upload_documents, receive_notifications
Agent	Agent immobilier	read_properties, add_documents, update_documents, receive_notifications, participate_workflows
Viewer	Lecture seule	read_properties, view_documents, receive_notifications

Ces rôles peuvent être personnalisés par l’Owner de l’organisation.

3️⃣ Permissions détaillées (exemples pour chaque module)
Module	Permission Key	Description
Property	create_property	Créer une nouvelle Property
Property	edit_property	Modifier une Property existante
Property	delete_property	Supprimer une Property
Property	read_property	Lire une Property
Document	upload_document	Upload / joindre un document à une Property
Document	edit_document	Modifier un document
Document	delete_document	Supprimer un document
Workflow	approve_workflow	Approuver / rejeter une action
Workflow	trigger_workflow	Déclencher un workflow
ACL	share_property	Partager Property avec d’autres utilisateurs / organisations
Audit	view_audit	Consulter les logs d’audit
FeatureFlags	manage_feature_flags	Activer / désactiver des fonctionnalités
Billing	view_billing	Voir l’état du plan et facturation
Billing	manage_billing	Changer le plan ou gérer facturation
Notification	receive_notifications	Recevoir notifications push / email / SMS

4️⃣ Initialisation de la base

Crée les rôles système SaaS dans la table roles globale.

Crée les permissions globales dans la table permissions.

Associe les rôles aux permissions via role_permissions.

Lorsqu’une organisation est créée, copie les rôles template (Owner, Manager, Agent, Viewer) dans organization_roles de cette organisation.

Chaque Owner de l’organisation peut assigner ces rôles aux utilisateurs (organization_users) et modifier les rôles locaux si nécessaire.

Audit Service trace toute modification de rôle ou permission pour conformité.

5️⃣ Exemple de mapping Owner → Permissions
Role	Permissions
Owner	create_property, edit_property, delete_property, read_property, upload_document, edit_document, delete_document, approve_workflow, trigger_workflow, share_property, view_audit, manage_feature_flags