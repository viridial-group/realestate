<template>
  <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- Header -->
      <div class="mb-8">
        <h1 class="text-3xl font-bold text-gray-900">Mon Profil</h1>
        <p class="mt-2 text-sm text-gray-600">
          Gérez vos informations personnelles
        </p>
      </div>

      <div class="space-y-6">
        <!-- Informations personnelles (vue d'ensemble) -->
        <div class="bg-white rounded-lg shadow-sm p-6">
          <div class="flex items-center justify-between mb-4">
            <h2 class="text-lg font-semibold text-gray-900">Informations personnelles</h2>
            <router-link
              to="/profile/settings"
              class="text-sm text-blue-600 hover:text-blue-700 font-medium"
            >
              Modifier →
            </router-link>
          </div>
          
          <div v-if="loading" class="text-center py-8">
            <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600 mx-auto"></div>
          </div>

          <div v-else class="space-y-4">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <p class="text-sm text-gray-500 mb-1">Prénom</p>
                <p class="text-base font-medium text-gray-900">{{ profileForm.firstName || 'Non renseigné' }}</p>
              </div>
              <div>
                <p class="text-sm text-gray-500 mb-1">Nom</p>
                <p class="text-base font-medium text-gray-900">{{ profileForm.lastName || 'Non renseigné' }}</p>
              </div>
            </div>
            <div>
              <p class="text-sm text-gray-500 mb-1">Email</p>
              <p class="text-base font-medium text-gray-900">{{ profileForm.email }}</p>
            </div>
          </div>
        </div>

        <!-- Statistiques -->
        <div class="bg-white rounded-lg shadow-sm p-6">
          <div class="flex items-center justify-between mb-4">
            <h2 class="text-lg font-semibold text-gray-900">Mes statistiques</h2>
            <router-link
              to="/dashboard"
              class="text-sm text-blue-600 hover:text-blue-700"
            >
              Voir le dashboard →
            </router-link>
          </div>
          
          <div v-if="loadingStats" class="text-center py-8">
            <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600 mx-auto"></div>
          </div>

          <div v-else class="space-y-4">
            <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
              <div class="text-center p-4 bg-blue-50 rounded-lg hover:bg-blue-100 transition-colors cursor-pointer">
                <p class="text-2xl font-bold text-blue-600">{{ stats.totalProperties || 0 }}</p>
                <p class="text-sm text-gray-600 mt-1">Annonces</p>
                <p class="text-xs text-gray-500 mt-1">{{ stats.activeProperties || 0 }} actives</p>
              </div>
              <div class="text-center p-4 bg-green-50 rounded-lg hover:bg-green-100 transition-colors cursor-pointer">
                <p class="text-2xl font-bold text-green-600">{{ stats.totalViews || 0 }}</p>
                <p class="text-sm text-gray-600 mt-1">Vues</p>
                <p class="text-xs text-gray-500 mt-1">{{ stats.viewsThisMonth || 0 }} ce mois</p>
              </div>
              <div class="text-center p-4 bg-purple-50 rounded-lg hover:bg-purple-100 transition-colors cursor-pointer">
                <p class="text-2xl font-bold text-purple-600">{{ stats.totalContacts || 0 }}</p>
                <p class="text-sm text-gray-600 mt-1">Contacts</p>
                <p class="text-xs text-gray-500 mt-1">{{ stats.contactsThisMonth || 0 }} ce mois</p>
              </div>
              <div class="text-center p-4 bg-orange-50 rounded-lg hover:bg-orange-100 transition-colors cursor-pointer">
                <p class="text-2xl font-bold text-orange-600">{{ stats.totalFavorites || 0 }}</p>
                <p class="text-sm text-gray-600 mt-1">Favoris</p>
                <p class="text-xs text-gray-500 mt-1">Sur vos annonces</p>
              </div>
            </div>

            <!-- Taux de conversion -->
            <div class="mt-4 p-4 bg-gray-50 rounded-lg">
              <div class="flex items-center justify-between">
                <span class="text-sm font-medium text-gray-700">Taux de conversion</span>
                <span class="text-lg font-bold text-gray-900">
                  {{ stats.conversionRate > 0 ? stats.conversionRate.toFixed(1) : 0 }}%
                </span>
              </div>
              <p class="text-xs text-gray-500 mt-1">Contacts / Vues</p>
            </div>
          </div>
        </div>


        <!-- Sécurité -->
        <div class="bg-white rounded-lg shadow-sm p-6">
          <h2 class="text-lg font-semibold text-gray-900 mb-4">Sécurité</h2>
          
          <div class="space-y-4">
            <div class="flex items-center justify-between py-3 border-b border-gray-200">
              <div>
                <p class="font-medium text-gray-900">Compte créé le</p>
                <p class="text-sm text-gray-500">{{ formatDate(profileForm.createdAt) }}</p>
              </div>
            </div>

            <div class="flex items-center justify-between py-3">
              <div>
                <p class="font-medium text-gray-900">Dernière connexion</p>
                <p class="text-sm text-gray-500">{{ formatDate(profileForm.lastLogin) || 'Jamais' }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useAuthStore, userService } from '@viridial/shared'
import { useToast } from '@/composables/useToast'
import { userPropertyService } from '@/api/user-property.service'

const authStore = useAuthStore()
const { showToast } = useToast()

const loading = ref(false)
const loadingStats = ref(false)

const profileForm = ref({
  firstName: '',
  lastName: '',
  email: '',
  createdAt: '',
  lastLogin: '',
})

const stats = ref({
  totalProperties: 0,
  activeProperties: 0,
  totalViews: 0,
  viewsThisMonth: 0,
  totalContacts: 0,
  contactsThisMonth: 0,
  totalFavorites: 0,
  conversionRate: 0,
})

onMounted(async () => {
  await loadProfile()
  await loadStats()
})

async function loadProfile() {
  loading.value = true
  try {
    const profile = await userService.getProfile()
    profileForm.value = {
      firstName: profile.firstName || '',
      lastName: profile.lastName || '',
      email: profile.email || '',
      createdAt: profile.createdAt || '',
      lastLogin: profile.lastLogin || '',
    }
  } catch (err: any) {
    showToast('Erreur lors du chargement du profil', 'error')
  } finally {
    loading.value = false
  }
}


async function loadStats() {
  loadingStats.value = true
  try {
    const response = await userPropertyService.getMyProperties({ page: 0, size: 100 })
    const properties = response.content || []
    
    stats.value.totalProperties = properties.length
    stats.value.activeProperties = properties.filter(p => p.status === 'AVAILABLE').length
    
    // Charger les stats pour chaque propriété
    let totalViews = 0
    let viewsThisMonth = 0
    let totalContacts = 0
    let contactsThisMonth = 0
    let totalFavorites = 0
    
    const now = new Date()
    const currentMonth = now.getMonth()
    const currentYear = now.getFullYear()
    
    for (const property of properties) {
      try {
        const propertyStats = await userPropertyService.getPropertyStats(property.id)
        totalViews += propertyStats.views || 0
        totalContacts += propertyStats.contacts || 0
        totalFavorites += propertyStats.favorites || 0
        
        // Calculer les stats du mois (approximation)
        if (property.createdAt) {
          const createdDate = new Date(property.createdAt)
          if (createdDate.getMonth() === currentMonth && createdDate.getFullYear() === currentYear) {
            viewsThisMonth += Math.floor((propertyStats.views || 0) * 0.3) // Approximation
            contactsThisMonth += Math.floor((propertyStats.contacts || 0) * 0.3)
          }
        }
      } catch (err) {
        // Ignorer les erreurs pour les stats individuelles
      }
    }
    
    stats.value.totalViews = totalViews
    stats.value.viewsThisMonth = viewsThisMonth
    stats.value.totalContacts = totalContacts
    stats.value.contactsThisMonth = contactsThisMonth
    stats.value.totalFavorites = totalFavorites
    stats.value.conversionRate = totalViews > 0 ? (totalContacts / totalViews) * 100 : 0
  } catch (err) {
    console.error('Error loading stats:', err)
  } finally {
    loadingStats.value = false
  }
}


function formatDate(dateString?: string): string {
  if (!dateString) return 'N/A'
  return new Date(dateString).toLocaleDateString('fr-FR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  })
}
</script>

