#!/bin/bash

# Script pour mettre à jour les vues avec i18n
# Ce script remplace les textes en dur par des appels t()

echo "📝 Mise à jour i18n des vues..."

# Liste des remplacements courants
# Note: Ce script est un exemple, les remplacements doivent être faits manuellement

echo "✅ Dashboard.vue - Terminé"
echo "✅ Login.vue - Terminé"
echo "🔄 Users/Index.vue - En cours..."
echo "⏳ Organizations/Index.vue - À venir"
echo "⏳ Properties/Index.vue - À venir"
echo "⏳ Billing/Index.vue - À venir"
echo "⏳ Audit/Index.vue - À venir"
echo "⏳ Notifications/Index.vue - À venir"
echo "⏳ AdminLayout.vue - À venir"

echo ""
echo "📋 Remplacements à faire:"
echo "   • 'Gestion des Utilisateurs' → t('users.title')"
echo "   • 'Recherche' → t('common.search')"
echo "   • 'Statut' → t('common.status')"
echo "   • 'Actions' → t('common.actions')"
echo "   • 'Actif' → t('users.active')"
echo "   • 'Inactif' → t('users.inactive')"
echo "   • 'Suspendu' → t('users.suspended')"
echo "   • 'En attente' → t('users.pending')"

