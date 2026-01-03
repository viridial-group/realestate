<template>
  <div v-if="loading" class="text-center py-12">
    <div class="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
    <p class="mt-4 text-gray-600">Chargement de la propriété...</p>
  </div>

  <div v-else-if="error" class="bg-red-50 border border-red-200 rounded-lg p-4">
    <div class="flex items-center gap-2">
      <span class="text-red-600">⚠️</span>
      <p class="text-red-800">{{ error }}</p>
    </div>
    <button
      @click="loadProperty"
      class="mt-3 px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors text-sm"
    >
      Réessayer
    </button>
  </div>

  <div v-else-if="property" class="max-w-7xl mx-auto px-4 py-8">
    <!-- Header avec bouton retour -->
    <div class="mb-6">
      <button
        @click="$router.push('/')"
        class="flex items-center gap-2 text-gray-600 hover:text-gray-900 transition-colors mb-4"
      >
        <span>←</span>
        <span>Retour à la recherche</span>
      </button>
    </div>

    <!-- En-tête avec titre et partage -->
    <div class="flex items-start justify-between mb-6">
      <div class="flex-1">
        <h1 class="text-3xl font-bold text-gray-900 mb-2">{{ property.title }}</h1>
        <div class="flex items-center gap-4 mb-2">
          <p class="text-2xl font-semibold text-blue-600">
            €{{ property.price?.toLocaleString('fr-FR') }}
          </p>
          <p class="text-gray-600">
            {{ property.surface }} m²
            <span v-if="property.bedrooms"> • {{ property.bedrooms }} chambre{{ property.bedrooms > 1 ? 's' : '' }}</span>
            <span v-if="property.bathrooms"> • {{ property.bathrooms }} salle{{ property.bathrooms > 1 ? 's' : '' }} de bain</span>
          </p>
        </div>
        <p class="text-gray-600">
          📍 {{ property.address || property.city || 'Non spécifié' }}
        </p>
      </div>
      <div v-if="property" class="ml-4">
        <ShareButtons
          :url="shareUrl"
          :title="property.title"
          :description="property.description"
        />
      </div>
    </div>

    <!-- Galerie d'images améliorée -->
    <div class="mb-8">
      <ImageGallery v-if="images.length > 0" :images="images" />
      <div v-else class="w-full h-96 bg-gray-200 rounded-lg flex items-center justify-center">
        <span class="text-gray-400">Aucune image disponible</span>
      </div>
    </div>

    <!-- Contenu principal -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
      <!-- Colonne principale -->
      <div class="lg:col-span-2 space-y-6">
        <!-- Description -->
        <div class="bg-white rounded-lg shadow-sm p-6">
          <h2 class="text-xl font-semibold text-gray-900 mb-4">Description</h2>
          <p class="text-gray-700 whitespace-pre-line">{{ property.description || 'Aucune description disponible.' }}</p>
        </div>

        <!-- Caractéristiques -->
        <div class="bg-white rounded-lg shadow-sm p-6">
          <h2 class="text-xl font-semibold text-gray-900 mb-4">Caractéristiques</h2>
          <div class="grid grid-cols-2 gap-4">
            <div v-if="property.surface">
              <span class="text-gray-600">Surface</span>
              <p class="font-semibold">{{ property.surface }} m²</p>
            </div>
            <div v-if="property.rooms">
              <span class="text-gray-600">Pièces</span>
              <p class="font-semibold">{{ property.rooms }}</p>
            </div>
            <div v-if="property.bedrooms">
              <span class="text-gray-600">Chambres</span>
              <p class="font-semibold">{{ property.bedrooms }}</p>
            </div>
            <div v-if="property.bathrooms">
              <span class="text-gray-600">Salles de bain</span>
              <p class="font-semibold">{{ property.bathrooms }}</p>
            </div>
            <div v-if="property.yearBuilt">
              <span class="text-gray-600">Année de construction</span>
              <p class="font-semibold">{{ property.yearBuilt }}</p>
            </div>
            <div v-if="property.condition">
              <span class="text-gray-600">État</span>
              <p class="font-semibold">{{ property.condition }}</p>
            </div>
          </div>
        </div>

        <!-- Localisation -->
        <div v-if="property.latitude && property.longitude" class="bg-white rounded-lg shadow-sm p-0 overflow-hidden">
          <h2 class="text-xl font-semibold text-gray-900 mb-4 p-6 pb-0">Localisation</h2>
          <div class="w-full h-[500px]">
            <PropertyMap
              :property="{
                id: property.id,
                title: property.title,
                latitude: property.latitude,
                longitude: property.longitude,
                address: property.address,
                city: property.city
              }"
              :hide-action-button="true"
              :hide-sidebar="true"
            />
          </div>
        </div>
      </div>

      <!-- Sidebar -->
      <div class="lg:col-span-1 space-y-6">
        <!-- Actions -->
        <div class="bg-white rounded-lg shadow-sm p-6">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">Actions</h3>
          <div class="space-y-3">
            <button
              class="w-full px-4 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors font-medium"
            >
              Contacter
            </button>
            <div class="space-y-3">
              <div class="flex items-center justify-center gap-3">
                <FavoriteButton
                  v-if="property"
                  :property-id="property.id"
                  @favorite-toggled="handleFavoriteToggled"
                />
                <span class="text-sm text-gray-600">Ajouter aux favoris</span>
              </div>
              <div class="flex items-center justify-center gap-3">
                <CompareButton
                  v-if="property"
                  :property-id="property.id"
                />
                <span class="text-sm text-gray-600">Comparer</span>
              </div>
            </div>
          </div>
        </div>

        <!-- Informations -->
        <div class="bg-white rounded-lg shadow-sm p-6">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">Informations</h3>
          <div class="space-y-3 text-sm">
            <div>
              <span class="text-gray-600">Référence</span>
              <p class="font-semibold">{{ property.reference }}</p>
            </div>
            <div>
              <span class="text-gray-600">Type</span>
              <p class="font-semibold">{{ property.type }}</p>
            </div>
            <div>
              <span class="text-gray-600">Statut</span>
              <p class="font-semibold">{{ property.status }}</p>
            </div>
            <div v-if="property.dateOnMarket">
              <span class="text-gray-600">Sur le marché depuis</span>
              <p class="font-semibold">{{ formatDate(property.dateOnMarket) }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- Propriétés similaires -->
    <RelatedProperties
      v-if="property"
      :current-property-id="property.id"
      :city="property.city"
      :type="property.type"
      :max-price="property.price"
      @property-click="handleRelatedPropertyClick"
    />
  </div>
</template>

<script setup lang="ts">
    import { ref, computed, onMounted, watch } from 'vue'
    import { useRoute, useRouter } from 'vue-router'
import { publicPropertyService, type PublicProperty } from '@/api/public-property.service'
import { documentService, type Document } from '@/api/document.service'
    import PropertyMap from '@/components/PropertyMap.vue'
    import ImageGallery from '@/components/ImageGallery.vue'
    import ShareButtons from '@/components/ShareButtons.vue'
    import RelatedProperties from '@/components/RelatedProperties.vue'
    import FavoriteButton from '@/components/FavoriteButton.vue'
    import CompareButton from '@/components/CompareButton.vue'
    import { getPlaceholderImage } from '@/utils/imageOptimization'

const route = useRoute()
const router = useRouter()

const property = ref<PublicProperty | null>(null)
const propertyId = ref<number | null>(null)
    const images = ref<Document[]>([])
    const loading = ref(false)
    const error = ref<string | null>(null)
    const selectedImage = ref<Document | null>(null)
    const shareUrl = computed(() => {
      if (typeof window !== 'undefined') {
        return window.location.href
      }
      return ''
    })

watch(
  () => route.params.id,
  async (newId) => {
    if (newId) {
      propertyId.value = Number(newId)
      await loadProperty()
    }
  },
  { immediate: true }
)

async function loadProperty() {
  if (!propertyId.value) {
    propertyId.value = Number(route.params.id)
  }
  
  if (!propertyId.value) {
    error.value = 'ID de propriété invalide'
    return
  }

  loading.value = true
  error.value = null

  try {
    // Charger la propriété
    property.value = await publicPropertyService.getPublishedPropertyById(propertyId.value)
    
    // Charger les images
    const allDocuments = await documentService.getByPropertyId(propertyId.value)
    images.value = documentService.filterImages(allDocuments)
  } catch (err: any) {
    error.value = err.response?.data?.message || err.message || 'Erreur lors du chargement de la propriété'
    console.error('Error loading property:', err)
  } finally {
    loading.value = false
  }
}

function formatDate(dateString: string): string {
  const date = new Date(dateString)
  return date.toLocaleDateString('fr-FR', { year: 'numeric', month: 'long', day: 'numeric' })
}

function handleRelatedPropertyClick(id: number) {
      propertyId.value = id
      router.push(`/property/${id}`)
      loadProperty()
      window.scrollTo({ top: 0, behavior: 'smooth' })
    }
    
    function handleFavoriteToggled(isFavorite: boolean) {
      // Optionnel : afficher une notification
      if (isFavorite) {
        console.log(`Propriété ${property.value?.id} ajoutée aux favoris`)
      } else {
        console.log(`Propriété ${property.value?.id} retirée des favoris`)
      }
    }
</script>

