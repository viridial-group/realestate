<template>
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- Onboarding Guide -->
      <OnboardingGuide
        :steps="onboardingSteps"
        storage-key="my_properties_onboarding"
        @completed="handleOnboardingCompleted"
      />

      <!-- Header -->
      <div class="mb-8">
        <div class="flex items-center justify-between">
          <div>
            <h1 class="text-3xl font-bold text-gray-900">Mes Annonces</h1>
            <p class="mt-2 text-sm text-gray-600">
              Gérez vos annonces immobilières
            </p>
          </div>
          <div class="flex items-center gap-3">
            <button
              v-if="properties.length > 0"
              @click="exportAllToPDF"
              class="inline-flex items-center gap-2 px-4 py-2 bg-green-600 text-white rounded-md hover:bg-green-700 transition-colors shadow-sm"
            >
              <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 10v6m0 0l-3-3m3 3l3-3m2 8H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
              </svg>
              Exporter tout en PDF
            </button>
            <router-link
              to="/my-properties/new"
              class="inline-flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors shadow-sm"
            >
              <Plus class="h-5 w-5" />
              Nouvelle annonce
            </router-link>
          </div>
        </div>
      </div>

      <!-- Filtres avancés -->
      <div class="mb-6 bg-white rounded-lg shadow-sm p-4">
        <div class="flex flex-wrap items-center gap-4">
          <div class="flex items-center gap-2">
            <label class="text-sm font-medium text-gray-700">Statut:</label>
            <select
              v-model="statusFilter"
              @change="() => loadProperties()"
              class="px-3 py-2 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="">Tous</option>
              <option value="DRAFT">Brouillon</option>
              <option value="AVAILABLE">Disponible</option>
              <option value="PENDING">En attente</option>
              <option value="SOLD">Vendu</option>
              <option value="RENTED">Loué</option>
            </select>
          </div>
          
          <div class="flex items-center gap-2">
            <label class="text-sm font-medium text-gray-700">Type:</label>
            <select
              v-model="typeFilter"
              @change="() => loadProperties()"
              class="px-3 py-2 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="">Tous</option>
              <option value="APARTMENT">Appartement</option>
              <option value="HOUSE">Maison</option>
              <option value="VILLA">Villa</option>
              <option value="LAND">Terrain</option>
              <option value="COMMERCIAL">Commercial</option>
            </select>
          </div>

          <div class="flex items-center gap-2">
            <label class="text-sm font-medium text-gray-700">Recherche:</label>
            <input
              v-model="searchQuery"
              @input="debouncedSearch"
              type="text"
              placeholder="Titre, ville..."
              class="px-3 py-2 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>

          <button
            v-if="statusFilter || typeFilter || searchQuery"
            @click="clearFilters"
            class="px-3 py-2 text-sm text-gray-700 hover:bg-gray-100 rounded-md transition-colors"
          >
            Réinitialiser
          </button>
        </div>
      </div>

      <!-- Liste des propriétés -->
      <div v-if="loading" class="flex justify-center py-12">
        <LoadingSpinner />
      </div>

      <div v-else-if="error" class="bg-red-50 border border-red-200 rounded-lg p-4">
        <p class="text-red-800">{{ error }}</p>
        <button
          @click="() => loadProperties()"
          class="mt-2 text-sm text-red-600 hover:text-red-800 underline"
        >
          Réessayer
        </button>
      </div>

      <div v-else-if="properties.length === 0" class="bg-white rounded-lg shadow-sm p-12 text-center">
        <Home class="h-12 w-12 text-gray-400 mx-auto mb-4" />
        <h3 class="text-lg font-medium text-gray-900 mb-2">Aucune annonce</h3>
        <p class="text-gray-600 mb-4">
          Vous n'avez pas encore créé d'annonce.
        </p>
        <router-link
          to="/my-properties/new"
          class="inline-flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors"
        >
          <Plus class="h-5 w-5" />
          Créer ma première annonce
        </router-link>
      </div>

      <div v-else class="space-y-4">
        <div
          v-for="property in properties"
          :key="property.id"
          class="bg-white rounded-lg shadow-sm hover:shadow-md transition-shadow overflow-hidden"
        >
          <div class="p-6">
            <div class="flex items-start justify-between">
              <div class="flex-1">
                <div class="flex items-center gap-3 mb-2">
                  <h3 class="text-lg font-semibold text-gray-900">
                    {{ property.title }}
                  </h3>
                  <span
                    :class="getStatusBadgeClass(property.status)"
                    class="px-2 py-1 text-xs font-medium rounded"
                  >
                    {{ getStatusLabel(property.status) }}
                  </span>
                </div>
                <p class="text-sm text-gray-600 mb-3 line-clamp-2">
                  {{ property.description }}
                </p>
                <div class="flex flex-wrap items-center gap-4 text-sm text-gray-600 mb-3">
                  <div class="flex items-center gap-1">
                    <MapPin class="h-4 w-4" />
                    <span>{{ property.city }}{{ property.postalCode ? `, ${property.postalCode}` : '' }}</span>
                  </div>
                  <div v-if="property.bedrooms" class="flex items-center gap-1">
                    <Bed class="h-4 w-4" />
                    <span>{{ property.bedrooms }} ch.</span>
                  </div>
                  <div v-if="property.surface || property.area" class="flex items-center gap-1">
                    <Square class="h-4 w-4" />
                    <span>{{ property.surface || property.area }} m²</span>
                  </div>
                  <div class="flex items-center gap-1">
                    <Euro class="h-4 w-4" />
                    <span class="font-semibold text-gray-900">{{ formatPrice(property.price) }}</span>
                  </div>
                </div>
                
                <!-- Statistiques -->
                <div v-if="propertyStats[property.id]" class="flex items-center gap-6 text-xs text-gray-500 pt-3 border-t border-gray-100">
                  <div class="flex items-center gap-1">
                    <Eye class="h-4 w-4" />
                    <span>{{ propertyStats[property.id].views || 0 }} vues</span>
                  </div>
                  <div class="flex items-center gap-1">
                    <Mail class="h-4 w-4" />
                    <span>{{ propertyStats[property.id].contacts || 0 }} contacts</span>
                  </div>
                  <div class="flex items-center gap-1">
                    <Heart class="h-4 w-4" />
                    <span>{{ propertyStats[property.id].favorites || 0 }} favoris</span>
                  </div>
                  <div class="flex items-center gap-1">
                    <Share2 class="h-4 w-4" />
                    <span>{{ propertyStats[property.id].shares || 0 }} partages</span>
                  </div>
                </div>
              </div>
              <div class="flex items-center gap-2 ml-4">
                <router-link
                  :to="`/my-properties/${property.id}`"
                  class="px-3 py-2 text-sm text-gray-600 hover:text-gray-900 hover:bg-gray-100 rounded-md transition-colors"
                  title="Voir les détails"
                >
                  <Eye class="h-4 w-4" />
                </router-link>
                <router-link
                  :to="`/my-properties/${property.id}/edit`"
                  class="px-3 py-2 text-sm text-blue-600 hover:text-blue-700 hover:bg-blue-50 rounded-md transition-colors"
                  title="Modifier"
                >
                  <Edit class="h-4 w-4" />
                </router-link>
                <button
                  @click="confirmDelete(property)"
                  class="px-3 py-2 text-sm text-red-600 hover:text-red-700 hover:bg-red-50 rounded-md transition-colors"
                  title="Supprimer"
                >
                  <Trash2 class="h-4 w-4" />
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- Pagination -->
        <div v-if="pagination.totalPages > 1" class="flex items-center justify-between bg-white rounded-lg shadow-sm p-4">
          <div class="text-sm text-gray-600">
            Page {{ pagination.currentPage + 1 }} sur {{ pagination.totalPages }}
            ({{ pagination.totalElements }} annonces)
          </div>
          <div class="flex items-center gap-2">
            <button
              @click="goToPage(pagination.currentPage - 1)"
              :disabled="pagination.first"
              class="px-3 py-2 text-sm border border-gray-300 rounded-md hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              Précédent
            </button>
            <button
              @click="goToPage(pagination.currentPage + 1)"
              :disabled="pagination.last"
              class="px-3 py-2 text-sm border border-gray-300 rounded-md hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              Suivant
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Dialog de confirmation de suppression -->
    <div
      v-if="propertyToDelete"
      class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
      @click.self="propertyToDelete = null"
    >
      <div class="bg-white rounded-lg shadow-xl max-w-md w-full mx-4 p-6">
        <h3 class="text-lg font-semibold text-gray-900 mb-4">
          Supprimer l'annonce ?
        </h3>
        <p class="text-gray-600 mb-6">
          Êtes-vous sûr de vouloir supprimer l'annonce "{{ propertyToDelete.title }}" ?
          Cette action est irréversible.
        </p>
        <div class="flex items-center justify-end gap-3">
          <button
            @click="propertyToDelete = null"
            class="px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 rounded-md transition-colors"
          >
            Annuler
          </button>
          <button
            @click="deleteProperty"
            :disabled="deleting"
            class="px-4 py-2 text-sm bg-red-600 text-white rounded-md hover:bg-red-700 disabled:opacity-50 transition-colors"
          >
            {{ deleting ? 'Suppression...' : 'Supprimer' }}
          </button>
        </div>
      </div>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Plus, Home, MapPin, Bed, Square, Euro, Edit, Trash2, Eye, Mail, Heart, Share2, FileText, Image as ImageIcon, BarChart3, MessageSquare } from 'lucide-vue-next'
import { userPropertyService, type PropertyStats } from '@/api/user-property.service'
import type { Property } from '@viridial/shared'
import LoadingSpinner from '@/components/LoadingSpinner.vue'
import { useToast } from '@/composables/useToast'
import { exportPropertiesToPDF } from '@/utils/pdfExport'
import OnboardingGuide, { type OnboardingStep } from '@/components/OnboardingGuide.vue'

const router = useRouter()
const { showToast } = useToast()

const onboardingSteps: OnboardingStep[] = [
  {
    title: 'Bienvenue sur Mes Annonces',
    subtitle: 'Gérez vos annonces facilement',
    description: 'Cette page vous permet de gérer toutes vos annonces immobilières. Vous pouvez créer, modifier, supprimer et suivre les statistiques de vos annonces.',
    icon: Home,
    skipable: true
  },
  {
    title: 'Créer votre première annonce',
    subtitle: 'Publiez votre bien en quelques clics',
    description: 'Cliquez sur "Nouvelle annonce" pour créer votre première annonce. Remplissez les informations, ajoutez des photos et publiez !',
    icon: FileText,
    skipable: true,
    action: () => {
      router.push('/my-properties/new')
    }
  },
  {
    title: 'Ajouter des photos',
    subtitle: 'Les photos augmentent les vues',
    description: 'Les annonces avec photos reçoivent 10x plus de vues. Ajoutez jusqu\'à 10 photos de qualité pour mettre en valeur votre bien.',
    icon: ImageIcon,
    skipable: true
  },
  {
    title: 'Suivre vos statistiques',
    subtitle: 'Voyez l\'impact de vos annonces',
    description: 'Consultez les statistiques de chaque annonce : vues, contacts, favoris et partages. Ces données vous aident à optimiser vos annonces.',
    icon: BarChart3,
    skipable: true
  },
  {
    title: 'Gérer vos messages',
    subtitle: 'Répondez aux contacts rapidement',
    description: 'Consultez et répondez aux messages reçus pour vos annonces depuis la page "Mes messages". Répondre rapidement augmente vos chances de conclure.',
    icon: MessageSquare,
    skipable: true,
    action: () => {
      router.push('/my-messages')
    }
  }
]

function handleOnboardingCompleted() {
  showToast('Guide terminé ! Vous êtes prêt à publier vos annonces.', 'success')
}

const properties = ref<Property[]>([])
const loading = ref(false)
const error = ref<string | null>(null)
const statusFilter = ref('')
const typeFilter = ref('')
const searchQuery = ref('')
const deleting = ref(false)
const propertyToDelete = ref<Property | null>(null)
const propertyStats = ref<Record<number, PropertyStats>>({})
const loadingStats = ref<Record<number, boolean>>({})
let searchTimeout: ReturnType<typeof setTimeout> | null = null

const pagination = ref({
  currentPage: 0,
  totalPages: 0,
  totalElements: 0,
  size: 20,
  first: true,
  last: true,
})

onMounted(() => {
  loadProperties()
})

function debouncedSearch() {
  if (searchTimeout) {
    clearTimeout(searchTimeout)
  }
  searchTimeout = setTimeout(() => {
    loadProperties(0)
  }, 500)
}

function clearFilters() {
  statusFilter.value = ''
  typeFilter.value = ''
  searchQuery.value = ''
  loadProperties(0)
}

async function loadProperties(page = 0) {
  loading.value = true
  error.value = null

  try {
    const params: any = {
      page,
      size: pagination.value.size,
    }
    if (statusFilter.value) {
      params.status = statusFilter.value
    }
    if (typeFilter.value) {
      params.type = typeFilter.value
    }
    if (searchQuery.value) {
      params.search = searchQuery.value
    }

    const response = await userPropertyService.getMyProperties(params)
    properties.value = response.content || []
    pagination.value = {
      currentPage: response.currentPage,
      totalPages: response.totalPages,
      totalElements: response.totalElements,
      size: response.size,
      first: response.first,
      last: response.last,
    }
    
    // Charger les statistiques pour chaque propriété
    await loadStatsForProperties(properties.value)
  } catch (err: any) {
    error.value = err.response?.data?.message || err.message || 'Erreur lors du chargement des annonces'
    console.error('Error loading properties:', err)
  } finally {
    loading.value = false
  }
}

async function loadStatsForProperties(propertiesList: Property[]) {
  // Charger les stats en parallèle pour toutes les propriétés
  const statsPromises = propertiesList.map(async (property) => {
    if (loadingStats.value[property.id]) return // Déjà en cours de chargement
    
    loadingStats.value[property.id] = true
    try {
      const stats = await userPropertyService.getPropertyStats(property.id)
      propertyStats.value[property.id] = stats
    } catch (err) {
      console.warn(`Could not load stats for property ${property.id}:`, err)
      // Utiliser des stats par défaut en cas d'erreur
      propertyStats.value[property.id] = {
        views: 0,
        contacts: 0,
        favorites: 0,
        shares: 0,
      }
    } finally {
      loadingStats.value[property.id] = false
    }
  })
  
  await Promise.all(statsPromises)
}

function goToPage(page: number) {
  if (page >= 0 && page < pagination.value.totalPages) {
    loadProperties(page)
  }
}

function confirmDelete(property: Property) {
  propertyToDelete.value = property
}

async function deleteProperty() {
  if (!propertyToDelete.value) return

  deleting.value = true
  try {
    await userPropertyService.deleteProperty(propertyToDelete.value.id)
    showToast('Annonce supprimée avec succès', 'success')
    propertyToDelete.value = null
    await loadProperties(pagination.value.currentPage)
  } catch (err: any) {
    showToast(
      err.response?.data?.message || err.message || 'Erreur lors de la suppression',
      'error'
    )
  } finally {
    deleting.value = false
  }
}

function getStatusLabel(status: string): string {
  const labels: Record<string, string> = {
    DRAFT: 'Brouillon',
    AVAILABLE: 'Disponible',
    PENDING: 'En attente',
    SOLD: 'Vendu',
    RENTED: 'Loué',
  }
  return labels[status] || status
}

function getStatusBadgeClass(status: string): string {
  const classes: Record<string, string> = {
    DRAFT: 'bg-gray-100 text-gray-800',
    AVAILABLE: 'bg-green-100 text-green-800',
    PENDING: 'bg-yellow-100 text-yellow-800',
    SOLD: 'bg-blue-100 text-blue-800',
    RENTED: 'bg-purple-100 text-purple-800',
  }
  return classes[status] || 'bg-gray-100 text-gray-800'
}

async function exportAllToPDF() {
  if (properties.value.length === 0) {
    showToast('Aucune annonce à exporter', 'info')
    return
  }

  // Charger toutes les propriétés si nécessaire
  let allProperties = properties.value
  if (pagination.value.totalElements > properties.value.length) {
    // Charger toutes les propriétés
    try {
      const response = await userPropertyService.getMyProperties({ page: 0, size: 1000 })
      allProperties = response.content || []
    } catch (err) {
      console.error('Error loading all properties:', err)
    }
  }

  const propertiesForExport = allProperties.map(property => ({
    id: property.id,
    title: property.title,
    description: property.description,
    price: property.price,
    surface: property.surface || property.area,
    bedrooms: property.bedrooms,
    bathrooms: property.bathrooms,
    city: property.city,
    address: property.address,
    postalCode: property.postalCode,
    type: property.type || property.propertyType,
    transactionType: property.transactionType,
  }))

  exportPropertiesToPDF(propertiesForExport)
}

function formatPrice(price: number): string {
  return new Intl.NumberFormat('fr-FR', {
    style: 'currency',
    currency: 'EUR',
    minimumFractionDigits: 0,
  }).format(price)
}
</script>

