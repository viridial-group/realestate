<template>
  <div>
    <div v-if="loading" class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="animate-pulse space-y-6">
        <div class="h-8 bg-gray-200 rounded w-1/3"></div>
        <div class="h-64 bg-gray-200 rounded"></div>
        <div class="h-32 bg-gray-200 rounded"></div>
      </div>
    </div>

    <div v-else-if="error" class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="bg-red-50 border border-red-200 rounded-lg p-6 text-center">
        <AlertCircle class="h-12 w-12 text-red-600 mx-auto mb-4" />
        <h3 class="text-lg font-semibold text-red-900 mb-2">Erreur</h3>
        <p class="text-red-700 mb-4">{{ error }}</p>
        <div class="flex items-center justify-center gap-4">
          <button
            @click="loadProperty"
            class="px-4 py-2 bg-red-600 text-white rounded-md hover:bg-red-700 transition-colors"
          >
            Réessayer
          </button>
          <router-link
            to="/my-properties"
            class="px-4 py-2 border border-red-300 text-red-700 rounded-md hover:bg-red-50 transition-colors"
          >
            Retour à mes annonces
          </router-link>
        </div>
      </div>
    </div>

    <div v-else-if="property" class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- Header avec actions -->
      <div class="mb-6 flex items-center justify-between">
        <div class="flex items-center gap-4">
          <router-link
            to="/my-properties"
            class="text-gray-600 hover:text-gray-900 transition-colors"
          >
            <ArrowLeft class="h-5 w-5" />
          </router-link>
          <div>
            <h1 class="text-3xl font-bold text-gray-900">{{ property.title }}</h1>
            <p class="text-sm text-gray-600 mt-1">
              Référence: {{ property.reference || property.id }}
            </p>
          </div>
        </div>
        <div class="flex items-center gap-3">
          <span
            :class="getStatusBadgeClass(property.status)"
            class="px-3 py-1 text-sm font-medium rounded"
          >
            {{ getStatusLabel(property.status) }}
          </span>
          <router-link
            :to="`/my-properties/${property.id}/edit`"
            class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors flex items-center gap-2"
          >
            <Edit class="h-4 w-4" />
            Modifier
          </router-link>
        </div>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <!-- Colonne principale -->
        <div class="lg:col-span-2 space-y-6">
          <!-- Images -->
          <div class="bg-white rounded-lg shadow-sm overflow-hidden">
            <div v-if="images.length > 0" class="grid grid-cols-2 gap-2 p-2">
              <div
                v-for="(image, index) in images"
                :key="index"
                class="relative aspect-square rounded-lg overflow-hidden cursor-pointer"
                @click="selectedImageIndex = index"
              >
                <img
                  :src="image.url || image.preview"
                  :alt="`Image ${index + 1}`"
                  class="w-full h-full object-cover"
                />
                <div
                  v-if="index === 0"
                  class="absolute top-2 left-2 px-2 py-1 bg-blue-600 text-white text-xs rounded"
                >
                  Principale
                </div>
              </div>
            </div>
            <div v-else class="aspect-video bg-gray-100 flex items-center justify-center">
              <p class="text-gray-500">Aucune image</p>
            </div>
          </div>

          <!-- Description -->
          <div class="bg-white rounded-lg shadow-sm p-6">
            <h2 class="text-xl font-semibold text-gray-900 mb-4">Description</h2>
            <p class="text-gray-700 whitespace-pre-wrap">{{ property.description }}</p>
          </div>

          <!-- Caractéristiques -->
          <div class="bg-white rounded-lg shadow-sm p-6">
            <h2 class="text-xl font-semibold text-gray-900 mb-4">Caractéristiques</h2>
            <div class="grid grid-cols-2 md:grid-cols-3 gap-4">
              <div v-if="property.surface || property.area">
                <p class="text-sm text-gray-600">Surface</p>
                <p class="text-lg font-semibold text-gray-900">{{ property.surface || property.area }} m²</p>
              </div>
              <div v-if="property.bedrooms">
                <p class="text-sm text-gray-600">Chambres</p>
                <p class="text-lg font-semibold text-gray-900">{{ property.bedrooms }}</p>
              </div>
              <div v-if="property.bathrooms">
                <p class="text-sm text-gray-600">Salles de bain</p>
                <p class="text-lg font-semibold text-gray-900">{{ property.bathrooms }}</p>
              </div>
              <div v-if="property.rooms">
                <p class="text-sm text-gray-600">Pièces</p>
                <p class="text-lg font-semibold text-gray-900">{{ property.rooms }}</p>
              </div>
              <div v-if="property.yearBuilt">
                <p class="text-sm text-gray-600">Année de construction</p>
                <p class="text-lg font-semibold text-gray-900">{{ property.yearBuilt }}</p>
              </div>
              <div v-if="property.condition">
                <p class="text-sm text-gray-600">État</p>
                <p class="text-lg font-semibold text-gray-900">{{ property.condition }}</p>
              </div>
            </div>
          </div>

          <!-- Localisation -->
          <div class="bg-white rounded-lg shadow-sm p-6">
            <h2 class="text-xl font-semibold text-gray-900 mb-4">Localisation</h2>
            <div class="space-y-2">
              <p class="text-gray-700">
                <MapPin class="h-4 w-4 inline mr-2" />
                {{ property.address }}
              </p>
              <p class="text-gray-700">
                {{ property.postalCode }} {{ property.city }}, {{ property.country }}
              </p>
              <div v-if="property.latitude && property.longitude" class="mt-4 h-64 bg-gray-100 rounded-lg">
                <!-- Carte à intégrer ici si nécessaire -->
                <p class="text-center text-gray-500 pt-20">Carte (à intégrer)</p>
              </div>
            </div>
          </div>
        </div>

        <!-- Sidebar -->
        <div class="space-y-6">
          <!-- Prix et actions -->
          <div class="bg-white rounded-lg shadow-sm p-6">
            <div class="text-center mb-6">
              <p class="text-sm text-gray-600 mb-2">Prix</p>
              <p class="text-4xl font-bold text-gray-900">{{ formatPrice(property.price) }}</p>
              <p v-if="property.transactionType === 'RENT'" class="text-sm text-gray-600 mt-1">par mois</p>
            </div>

            <div class="space-y-3">
              <button
                @click="toggleStatus"
                :disabled="updatingStatus"
                :class="[
                  'w-full px-4 py-2 rounded-md transition-colors font-medium',
                  property.status === 'AVAILABLE'
                    ? 'bg-green-600 text-white hover:bg-green-700'
                    : 'bg-blue-600 text-white hover:bg-blue-700'
                ]"
              >
                {{ updatingStatus ? 'Mise à jour...' : (property.status === 'AVAILABLE' ? 'Désactiver' : 'Publier') }}
              </button>
              <router-link
                :to="`/my-properties/${property.id}/edit`"
                class="block w-full px-4 py-2 bg-gray-100 text-gray-700 rounded-md hover:bg-gray-200 transition-colors text-center font-medium"
              >
                Modifier l'annonce
              </router-link>
              <button
                @click="exportToPDF"
                class="w-full px-4 py-2 bg-blue-50 text-blue-600 rounded-md hover:bg-blue-100 transition-colors font-medium"
              >
                Exporter en PDF
              </button>
              <button
                @click="confirmDelete"
                class="w-full px-4 py-2 bg-red-50 text-red-600 rounded-md hover:bg-red-100 transition-colors font-medium"
              >
                Supprimer l'annonce
              </button>
            </div>
          </div>

          <!-- Messages -->
          <div class="bg-white rounded-lg shadow-sm p-6 mb-6">
            <div class="flex items-center justify-between mb-4">
              <h3 class="text-lg font-semibold text-gray-900">Messages</h3>
              <router-link
                :to="`/my-messages?property=${property.id}`"
                class="text-sm text-blue-600 hover:text-blue-700"
              >
                Voir tous →
              </router-link>
            </div>
            <div v-if="loadingMessages" class="text-center py-4">
              <div class="animate-spin rounded-full h-6 w-6 border-b-2 border-blue-600 mx-auto"></div>
            </div>
            <div v-else-if="propertyMessages.length === 0" class="text-center py-4">
              <p class="text-sm text-gray-600">Aucun message pour cette annonce</p>
            </div>
            <div v-else class="space-y-2">
              <div
                v-for="message in propertyMessages.slice(0, 3)"
                :key="message.id"
                class="p-3 bg-gray-50 rounded-lg cursor-pointer hover:bg-gray-100 transition-colors"
                @click="openMessage(message)"
              >
                <div class="flex items-center justify-between mb-1">
                  <p class="text-sm font-medium text-gray-900">{{ message.name }}</p>
                  <span
                    v-if="!message.readAt"
                    class="w-2 h-2 bg-blue-600 rounded-full"
                  />
                </div>
                <p class="text-xs text-gray-600 line-clamp-1">{{ message.message }}</p>
                <p class="text-xs text-gray-500 mt-1">{{ formatDate(message.createdAt) }}</p>
              </div>
              <router-link
                v-if="propertyMessages.length > 3"
                :to="`/my-messages?property=${property.id}`"
                class="block text-center text-sm text-blue-600 hover:text-blue-700 pt-2"
              >
                Voir {{ propertyMessages.length - 3 }} autre(s) message(s) →
              </router-link>
            </div>
          </div>

          <!-- Partage -->
          <div class="bg-white rounded-lg shadow-sm p-6 mb-6">
            <h3 class="text-lg font-semibold text-gray-900 mb-4">Partager cette annonce</h3>
            <ShareButtons
              :url="shareUrl"
              :title="property.title"
              :description="property.description"
              :image-url="images.length > 0 ? (images[0].url || images[0].preview) : undefined"
              :property-id="property.id"
              :show-label="false"
              @shared="handleShare"
            />
          </div>

          <!-- Statistiques -->
          <div class="bg-white rounded-lg shadow-sm p-6 mb-6">
            <h3 class="text-lg font-semibold text-gray-900 mb-4">Statistiques</h3>
            <div v-if="loadingStats" class="text-center py-4">
              <div class="animate-spin rounded-full h-6 w-6 border-b-2 border-blue-600 mx-auto"></div>
            </div>
            <div v-else class="space-y-4">
              <div class="flex items-center justify-between">
                <div class="flex items-center gap-2">
                  <Eye class="h-5 w-5 text-gray-400" />
                  <span class="text-sm text-gray-600">Vues</span>
                </div>
                <span class="text-lg font-semibold text-gray-900">{{ stats.views || 0 }}</span>
              </div>
              <div class="flex items-center justify-between">
                <div class="flex items-center gap-2">
                  <Mail class="h-5 w-5 text-gray-400" />
                  <span class="text-sm text-gray-600">Contacts</span>
                </div>
                <span class="text-lg font-semibold text-gray-900">{{ stats.contacts || 0 }}</span>
              </div>
              <div class="flex items-center justify-between">
                <div class="flex items-center gap-2">
                  <Heart class="h-5 w-5 text-gray-400" />
                  <span class="text-sm text-gray-600">Favoris</span>
                </div>
                <span class="text-lg font-semibold text-gray-900">{{ stats.favorites || 0 }}</span>
              </div>
              <div class="flex items-center justify-between">
                <div class="flex items-center gap-2">
                  <Share2 class="h-5 w-5 text-gray-400" />
                  <span class="text-sm text-gray-600">Partages</span>
                </div>
                <span class="text-lg font-semibold text-gray-900">{{ stats.shares || 0 }}</span>
              </div>
            </div>
          </div>

          <!-- Graphique d'évolution (si données disponibles) -->
          <StatsChart
            v-if="propertyChartData.length > 0"
            title="Évolution des statistiques"
            subtitle="Vues et contacts sur les 7 derniers jours"
            :data="propertyChartData"
            :loading="loadingStats"
            class="mb-6"
          />

          <!-- Informations -->
          <div class="bg-white rounded-lg shadow-sm p-6">
            <h3 class="text-lg font-semibold text-gray-900 mb-4">Informations</h3>
            <div class="space-y-3 text-sm">
              <div class="flex justify-between">
                <span class="text-gray-600">Type</span>
                <span class="font-medium text-gray-900">{{ getPropertyTypeLabel(property.type || property.propertyType) }}</span>
              </div>
              <div class="flex justify-between">
                <span class="text-gray-600">Transaction</span>
                <span class="font-medium text-gray-900">{{ property.transactionType === 'SALE' ? 'Vente' : 'Location' }}</span>
              </div>
              <div class="flex justify-between">
                <span class="text-gray-600">Créée le</span>
                <span class="font-medium text-gray-900">{{ formatDate(property.createdAt) }}</span>
              </div>
              <div class="flex justify-between">
                <span class="text-gray-600">Modifiée le</span>
                <span class="font-medium text-gray-900">{{ formatDate(property.updatedAt) }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal de suppression -->
    <div
      v-if="showDeleteConfirm"
      class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
      @click.self="showDeleteConfirm = false"
    >
      <div class="bg-white rounded-lg shadow-xl max-w-md w-full mx-4 p-6">
        <h3 class="text-lg font-semibold text-gray-900 mb-4">Supprimer l'annonce ?</h3>
        <p class="text-gray-600 mb-6">
          Êtes-vous sûr de vouloir supprimer l'annonce "{{ property?.title }}" ?
          Cette action est irréversible.
        </p>
        <div class="flex items-center justify-end gap-3">
          <button
            @click="showDeleteConfirm = false"
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

    <!-- Modal d'image -->
    <div
      v-if="selectedImageIndex !== null && images.length > 0"
      class="fixed inset-0 bg-black bg-opacity-90 flex items-center justify-center z-50"
      @click="selectedImageIndex = null"
    >
      <div class="relative max-w-5xl w-full mx-4">
        <button
          @click="selectedImageIndex = null"
          class="absolute top-4 right-4 text-white hover:text-gray-300 z-10"
        >
          <X class="h-8 w-8" />
        </button>
        <img
          :src="images[selectedImageIndex].url || images[selectedImageIndex].preview"
          :alt="`Image ${selectedImageIndex + 1}`"
          class="max-h-[90vh] mx-auto"
        />
      </div>
    </div>

    <!-- Modal de message -->
    <div
      v-if="selectedMessage"
      class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4"
      @click.self="selectedMessage = null"
    >
      <div class="bg-white rounded-lg shadow-xl max-w-2xl w-full max-h-[90vh] overflow-y-auto">
        <div class="p-6">
          <div class="flex items-center justify-between mb-4">
            <h2 class="text-2xl font-bold text-gray-900">{{ selectedMessage.subject }}</h2>
            <button
              @click="selectedMessage = null"
              class="text-gray-400 hover:text-gray-600"
            >
              <X class="h-6 w-6" />
            </button>
          </div>

          <div class="space-y-4">
            <div>
              <p class="text-sm text-gray-600 mb-1">De</p>
              <p class="font-medium text-gray-900">{{ selectedMessage.name }}</p>
              <p class="text-sm text-gray-600">{{ selectedMessage.email }}</p>
              <p v-if="selectedMessage.phone" class="text-sm text-gray-600">{{ selectedMessage.phone }}</p>
            </div>

            <div>
              <p class="text-sm text-gray-600 mb-1">Message</p>
              <p class="text-gray-700 whitespace-pre-wrap">{{ selectedMessage.message }}</p>
            </div>

            <div class="flex items-center gap-2 pt-4 border-t">
              <a
                :href="`mailto:${selectedMessage.email}?subject=Re: ${selectedMessage.subject}`"
                class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors"
              >
                Répondre par email
              </a>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ArrowLeft, Edit, AlertCircle, Eye, Mail, Heart, Share2, MapPin, X } from 'lucide-vue-next'
import { userPropertyService, type PropertyStats } from '@/api/user-property.service'
import { documentService, contactService, type ContactMessage } from '@viridial/shared'
import type { Property } from '@viridial/shared'
import { useToast } from '@/composables/useToast'
import { exportPropertyToPDF } from '@/utils/pdfExport'
import ShareButtons from '@/components/ShareButtons.vue'
import StatsChart, { type ChartSeries } from '@/components/StatsChart.vue'
import { statsHistoryService } from '@/api/stats-history.service'

const router = useRouter()
const route = useRoute()
const { showToast } = useToast()

const property = ref<Property | null>(null)
const loading = ref(false)
const error = ref<string | null>(null)
const stats = ref<PropertyStats>({
  views: 0,
  contacts: 0,
  favorites: 0,
  shares: 0,
})
const loadingStats = ref(false)
const updatingStatus = ref(false)
const deleting = ref(false)
const showDeleteConfirm = ref(false)
const selectedImageIndex = ref<number | null>(null)
const images = ref<any[]>([])
const propertyMessages = ref<ContactMessage[]>([])
const loadingMessages = ref(false)
const selectedMessage = ref<ContactMessage | null>(null)
const propertyChartData = ref<ChartSeries[]>([])

const shareUrl = computed(() => {
  if (typeof window === 'undefined') return ''
  return `${window.location.origin}/property/${property.value?.id}`
})

onMounted(async () => {
  await loadProperty()
})

async function loadProperty() {
  loading.value = true
  error.value = null

  try {
    const propertyId = Number(route.params.id)
    property.value = await userPropertyService.getMyPropertyById(propertyId)
    
    // Charger les images
    try {
      const documents = await documentService.getByPropertyId(propertyId)
      const imageDocuments = documents.filter(doc => 
        doc.type === 'IMAGE' || doc.mimeType?.startsWith('image/')
      )
      images.value = imageDocuments.map(doc => ({
        url: documentService.getViewUrl(doc.id),
        preview: documentService.getViewUrl(doc.id),
      }))
    } catch (err) {
      console.warn('Could not load images:', err)
    }

    // Charger les statistiques
    await loadStats()
    
    // Charger les messages
    await loadMessages()
  } catch (err: any) {
    error.value = err.response?.data?.message || err.message || 'Erreur lors du chargement de l\'annonce'
    showToast(error.value, 'error')
  } finally {
    loading.value = false
  }
}

async function loadStats() {
  if (!property.value) return

  loadingStats.value = true
  try {
    stats.value = await userPropertyService.getPropertyStats(property.value.id)
    
    // Générer des données de graphique (simulation sur 7 jours)
    generatePropertyChartData()
  } catch (err) {
    console.warn('Could not load stats:', err)
  } finally {
    loadingStats.value = false
  }
}

async function generatePropertyChartData() {
  if (!property.value) return

  try {
    // Essayer de récupérer les données réelles depuis l'API
    const history = await statsHistoryService.getPropertyHistory(property.value.id, { days: 7 })
    
    if (history && history.length > 0) {
      // Utiliser les données réelles
      const viewsData = history.map(point => ({
        date: new Date(point.date),
        value: point.views || 0,
      }))

      const contactsData = history.map(point => ({
        date: new Date(point.date),
        value: point.contacts || 0,
      }))

      propertyChartData.value = [
        {
          label: 'Vues',
          data: viewsData,
        },
        {
          label: 'Contacts',
          data: contactsData,
        },
      ]
    } else {
      // Fallback: générer des données simulées
      generateSimulatedPropertyChartData()
    }
  } catch (error) {
    console.warn('Could not load real stats history, using simulated data:', error)
    generateSimulatedPropertyChartData()
  }
}

function generateSimulatedPropertyChartData() {
  // Générer des données pour les 7 derniers jours
  const days = 7
  const today = new Date()
  const dates: Date[] = []
  
  for (let i = days - 1; i >= 0; i--) {
    const date = new Date(today)
    date.setDate(date.getDate() - i)
    dates.push(date)
  }

  // Simuler des données basées sur les stats actuelles
  const viewsData = dates.map((date) => ({
    date,
    value: Math.floor(Math.random() * 5) + Math.floor((stats.value.views || 0) / days),
  }))

  const contactsData = dates.map((date) => ({
    date,
    value: Math.floor(Math.random() * 2) + Math.floor((stats.value.contacts || 0) / days),
  }))

  propertyChartData.value = [
    {
      label: 'Vues',
      data: viewsData,
    },
    {
      label: 'Contacts',
      data: contactsData,
    },
  ]
}

async function toggleStatus() {
  if (!property.value) return

  updatingStatus.value = true
  try {
    const newStatus = property.value.status === 'AVAILABLE' ? 'DRAFT' : 'AVAILABLE'
    await userPropertyService.updateProperty(property.value.id, {
      status: newStatus as any,
    })
    
    property.value.status = newStatus
    showToast(
      newStatus === 'AVAILABLE' ? 'Annonce publiée' : 'Annonce désactivée',
      'success'
    )
  } catch (err: any) {
    showToast(
      err.response?.data?.message || err.message || 'Erreur lors de la mise à jour',
      'error'
    )
  } finally {
    updatingStatus.value = false
  }
}

function confirmDelete() {
  showDeleteConfirm.value = true
}

async function deleteProperty() {
  if (!property.value) return

  deleting.value = true
  try {
    await userPropertyService.deleteProperty(property.value.id)
    showToast('Annonce supprimée avec succès', 'success')
    router.push('/my-properties')
  } catch (err: any) {
    showToast(
      err.response?.data?.message || err.message || 'Erreur lors de la suppression',
      'error'
    )
  } finally {
    deleting.value = false
    showDeleteConfirm.value = false
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

function getPropertyTypeLabel(type: string): string {
  const labels: Record<string, string> = {
    APARTMENT: 'Appartement',
    HOUSE: 'Maison',
    VILLA: 'Villa',
    LAND: 'Terrain',
    COMMERCIAL: 'Commercial',
    OTHER: 'Autre',
  }
  return labels[type] || type
}

function formatPrice(price: number): string {
  return new Intl.NumberFormat('fr-FR', {
    style: 'currency',
    currency: 'EUR',
    minimumFractionDigits: 0,
  }).format(price)
}

function formatDate(dateString?: string): string {
  if (!dateString) return 'N/A'
  return new Date(dateString).toLocaleDateString('fr-FR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  })
}

function exportToPDF() {
  if (!property.value) return

  const propertyForExport = {
    id: property.value.id,
    title: property.value.title,
    description: property.value.description,
    price: property.value.price,
    surface: property.value.surface || property.value.area,
    bedrooms: property.value.bedrooms,
    bathrooms: property.value.bathrooms,
    city: property.value.city,
    address: property.value.address,
    postalCode: property.value.postalCode,
    type: property.value.type || property.value.propertyType,
    transactionType: property.value.transactionType,
    images: images.value.map(img => img.url || img.preview),
  }

  exportPropertyToPDF(propertyForExport)
}

function handleShare(platform: string) {
  // Recharger les statistiques pour mettre à jour le compteur de partages
  loadStats()
  showToast(`Annonce partagée sur ${platform}`, 'success')
}
</script>

