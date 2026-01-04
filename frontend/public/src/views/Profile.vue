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
        <!-- Informations personnelles -->
        <div class="bg-white rounded-lg shadow-sm p-6">
          <h2 class="text-lg font-semibold text-gray-900 mb-4">Informations personnelles</h2>
          
          <div v-if="loading" class="text-center py-8">
            <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600 mx-auto"></div>
          </div>

          <form v-else @submit.prevent="handleUpdateProfile" class="space-y-4">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">
                  Prénom <span class="text-red-500">*</span>
                </label>
                <input
                  v-model="profileForm.firstName"
                  type="text"
                  required
                  class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>

              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">
                  Nom <span class="text-red-500">*</span>
                </label>
                <input
                  v-model="profileForm.lastName"
                  type="text"
                  required
                  class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                Email <span class="text-red-500">*</span>
              </label>
              <input
                v-model="profileForm.email"
                type="email"
                required
                disabled
                class="w-full px-3 py-2 border border-gray-300 rounded-md bg-gray-50 text-gray-500"
              />
              <p class="mt-1 text-xs text-gray-500">L'email ne peut pas être modifié</p>
            </div>

            <div class="flex items-center justify-end gap-4 pt-4">
              <button
                type="button"
                @click="loadProfile"
                class="px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 rounded-md transition-colors"
              >
                Annuler
              </button>
              <button
                type="submit"
                :disabled="submitting"
                class="px-6 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 disabled:opacity-50 transition-colors"
              >
                {{ submitting ? 'Enregistrement...' : 'Enregistrer' }}
              </button>
            </div>
          </form>
        </div>

        <!-- Changer le mot de passe -->
        <div class="bg-white rounded-lg shadow-sm p-6">
          <h2 class="text-lg font-semibold text-gray-900 mb-4">Changer le mot de passe</h2>
          
          <form @submit.prevent="handleChangePassword" class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                Mot de passe actuel <span class="text-red-500">*</span>
              </label>
              <input
                v-model="passwordForm.currentPassword"
                type="password"
                required
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                Nouveau mot de passe <span class="text-red-500">*</span>
              </label>
              <input
                v-model="passwordForm.newPassword"
                type="password"
                required
                minlength="8"
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
              <p class="mt-1 text-xs text-gray-500">Minimum 8 caractères</p>
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                Confirmer le nouveau mot de passe <span class="text-red-500">*</span>
              </label>
              <input
                v-model="passwordForm.confirmPassword"
                type="password"
                required
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <div v-if="passwordError" class="bg-red-50 border border-red-200 rounded-lg p-4">
              <p class="text-sm text-red-800">{{ passwordError }}</p>
            </div>

            <div class="flex items-center justify-end">
              <button
                type="submit"
                :disabled="submittingPassword"
                class="px-6 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 disabled:opacity-50 transition-colors"
              >
                {{ submittingPassword ? 'Changement...' : 'Changer le mot de passe' }}
              </button>
            </div>
          </form>
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

        <!-- Préférences -->
        <div class="bg-white rounded-lg shadow-sm p-6">
          <h2 class="text-lg font-semibold text-gray-900 mb-4">Préférences</h2>
          
          <div class="space-y-4">
            <!-- Notifications -->
            <div class="flex items-center justify-between py-3 border-b border-gray-200">
              <div>
                <p class="font-medium text-gray-900">Notifications par email</p>
                <p class="text-sm text-gray-500">Recevoir des emails pour les nouveaux messages</p>
              </div>
              <label class="relative inline-flex items-center cursor-pointer">
                <input
                  v-model="preferences.emailNotifications"
                  type="checkbox"
                  @change="savePreferences"
                  class="sr-only peer"
                />
                <div class="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-blue-300 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-blue-600"></div>
              </label>
            </div>

            <!-- Mode sombre -->
            <div class="flex items-center justify-between py-3 border-b border-gray-200">
              <div>
                <p class="font-medium text-gray-900">Mode sombre</p>
                <p class="text-sm text-gray-500">Activer le thème sombre</p>
              </div>
              <label class="relative inline-flex items-center cursor-pointer">
                <input
                  v-model="preferences.darkMode"
                  type="checkbox"
                  @change="savePreferences"
                  class="sr-only peer"
                />
                <div class="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-blue-300 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-blue-600"></div>
              </label>
            </div>

            <!-- Confidentialité -->
            <div class="flex items-center justify-between py-3">
              <div>
                <p class="font-medium text-gray-900">Profil public</p>
                <p class="text-sm text-gray-500">Permettre aux autres de voir votre profil</p>
              </div>
              <label class="relative inline-flex items-center cursor-pointer">
                <input
                  v-model="preferences.publicProfile"
                  type="checkbox"
                  @change="savePreferences"
                  class="sr-only peer"
                />
                <div class="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-blue-300 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-blue-600"></div>
              </label>
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
import { useDarkMode } from '@/composables/useDarkMode'

const authStore = useAuthStore()
const { showToast } = useToast()
const { isDark, toggleDarkMode } = useDarkMode()

const loading = ref(false)
const submitting = ref(false)
const submittingPassword = ref(false)
const passwordError = ref<string | null>(null)
const loadingStats = ref(false)

const profileForm = ref({
  firstName: '',
  lastName: '',
  email: '',
  createdAt: '',
  lastLogin: '',
})

const preferences = ref({
  emailNotifications: true,
  darkMode: false,
  publicProfile: false,
})

const passwordForm = ref({
  currentPassword: '',
  newPassword: '',
  confirmPassword: '',
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
  loadPreferences()
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

function loadPreferences() {
  try {
    const stored = localStorage.getItem('user_preferences')
    if (stored) {
      preferences.value = { ...preferences.value, ...JSON.parse(stored) }
    }
    
    // Charger le mode sombre depuis le composable
    preferences.value.darkMode = isDark.value
  } catch (err) {
    console.error('Error loading preferences:', err)
  }
}

function savePreferences() {
  try {
    localStorage.setItem('user_preferences', JSON.stringify(preferences.value))
    
    // Appliquer le mode sombre si changé
    if (preferences.value.darkMode !== isDark.value) {
      toggleDarkMode()
    }
    
    showToast('Préférences sauvegardées', 'success')
  } catch (err) {
    console.error('Error saving preferences:', err)
    showToast('Erreur lors de la sauvegarde', 'error')
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

async function handleUpdateProfile() {
  submitting.value = true
  try {
    await userService.updateProfile({
      firstName: profileForm.value.firstName,
      lastName: profileForm.value.lastName,
    })
    showToast('Profil mis à jour avec succès', 'success')
    await authStore.checkAuth() // Rafraîchir les infos utilisateur
  } catch (err: any) {
    showToast(
      err.response?.data?.message || err.message || 'Erreur lors de la mise à jour',
      'error'
    )
  } finally {
    submitting.value = false
  }
}

async function handleChangePassword() {
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    passwordError.value = 'Les mots de passe ne correspondent pas'
    return
  }

  if (passwordForm.value.newPassword.length < 8) {
    passwordError.value = 'Le mot de passe doit contenir au moins 8 caractères'
    return
  }

  submittingPassword.value = true
  passwordError.value = null

  try {
    await userService.changePassword({
      currentPassword: passwordForm.value.currentPassword,
      newPassword: passwordForm.value.newPassword,
    })
    
    showToast('Mot de passe changé avec succès', 'success')
    passwordForm.value = {
      currentPassword: '',
      newPassword: '',
      confirmPassword: '',
    }
  } catch (err: any) {
    passwordError.value = err.response?.data?.message || err.message || 'Erreur lors du changement de mot de passe'
  } finally {
    submittingPassword.value = false
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

