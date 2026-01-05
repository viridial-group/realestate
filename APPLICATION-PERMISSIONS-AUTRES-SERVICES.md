# 🔐 Application des Permissions aux Autres Services

## 📋 Résumé

Application du système de permissions amélioré aux services suivants :
- ✅ **document-service** : Filtrage des documents selon les organisations accessibles
- ⏳ **workflow-service** : À faire
- ⏳ **billing-service** : À faire
- ⏳ **audit-service** : À faire
- ⏳ **notification-service** : À faire
- ⏳ **resource-service** : À faire

## ✅ Document Service - Implémenté

### Fichiers Créés/Modifiés

1. **DocumentSpecification.java** (Nouveau)
   - `hasOrganization(Long)` : Filtre par organisation
   - `hasAnyOrganization(Set<Long>)` : Filtre par plusieurs organisations
   - `hasCreatedBy(Long)` : Filtre par créateur
   - `accessibleByUser(Long, Set<Long>)` : Filtre selon les permissions
   - `hasProperty(Long)` : Filtre par propriété
   - `hasResource(Long)` : Filtre par ressource
   - `isActive(Boolean)` : Filtre par statut actif

2. **DocumentRepository.java** (Modifié)
   - Ajout de `JpaSpecificationExecutor<Document>` pour supporter les specifications

3. **DocumentService.java** (Modifié)
   - Nouvelle méthode `getDocumentsWithPermissions()` qui filtre selon les permissions

4. **DocumentController.java** (Modifié)
   - Endpoint `GET /api/documents` amélioré pour utiliser le contexte de permissions
   - Filtre automatique selon les organisations accessibles

### Logique de Filtrage

- **Super Admin / Admin** : Voit tous les documents
- **Professionnel** : Voit les documents de son organisation + sous-organisations
- **Individuel** : Voit seulement ses propres documents

## 🔄 Prochaines Étapes

### Workflow Service
- Créer `WorkflowSpecification.java`
- Améliorer `WorkflowController.getWorkflows()`
- Ajouter méthode `getWorkflowsWithPermissions()` dans `WorkflowService`

### Billing Service
- Créer `SubscriptionSpecification.java` et `InvoiceSpecification.java`
- Améliorer les controllers pour filtrer selon les organisations
- Super admin voit toutes les factures/abonnements

### Audit Service
- Créer `AuditSpecification.java`
- Filtrer les logs d'audit selon les organisations accessibles
- Super admin voit tous les logs

### Notification Service
- Filtrer les notifications selon les organisations
- Utilisateurs voient seulement leurs notifications

### Resource Service
- Filtrer les ressources selon les organisations
- Appliquer les mêmes règles de permissions

## 📝 Pattern Réutilisable

Pour chaque service, suivre ce pattern :

1. **Créer Specification** : `XxxSpecification.java` avec méthodes de filtrage
2. **Modifier Repository** : Ajouter `JpaSpecificationExecutor<Xxx>`
3. **Modifier Service** : Ajouter méthode `getXxxWithPermissions()`
4. **Modifier Controller** : 
   - Récupérer le contexte de permissions via `IdentityServiceClient`
   - Appliquer les filtres selon le rôle (super admin vs autres)
   - Utiliser la nouvelle méthode du service

## 🔐 Sécurité

- ✅ Filtrage côté serveur (JPA Specifications)
- ✅ Vérification des permissions avant chaque requête
- ✅ Super admin a accès complet
- ✅ Sous-organisations incluses automatiquement

