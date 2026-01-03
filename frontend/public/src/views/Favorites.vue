<template>
  <div class="max-w-7xl mx-auto px-4 py-8">
    <div class="mb-8">
      <h1 class="text-3xl font-bold text-gray-900 mb-2">Mes favoris</h1>
      <p class="text-gray-600">
        {{ favoritesCount }} propriété{{ favoritesCount > 1 ? 's' : '' }} en favoris
      </p>
    </div>

    <!-- Message si aucun favori -->
    <div v-if="favoritesCount === 0" class="text-center py-16 bg-white rounded-lg shadow-sm">
      <div class="text-6xl mb-4">⭐</div>
      <h2 class="text-2xl font-semibold text-gray-900 mb-2">Aucun favori</h2>
      <p class="text-gray-600 mb-6">
        Commencez à ajouter des propriétés à vos favoris pour les retrouver facilement.
      </p>
      <router-link
        to="/search"
        class="inline-block px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors font-medium"
      >
        Parcourir les propriétés
      </router-link>
    </div>

    <!-- Liste des favoris -->
    <div v-else class="space-y-6">
      <!-- Toggle vue -->
      <div class="flex items-center justify-between">
        <div class="flex items-center gap-4">
          <ViewToggle v-model="viewMode" />
        </div>
        <div class="flex items-center gap-3">
          <button
            v-if="favoriteProperties.length > 0"
            @click="exportToPDF"
            class="px-4 py-2 bg-blue-50 text-blue-600 rounded-lg hover:bg-blue-100 transition-colors text-sm font-medium flex items-center gap-2"
          >
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 10v6m0 0l-3-3m3 3l3-3m2 8H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
            </svg>
            Exporter PDF
          </button>
          <button
            @click="clearAllFavorites"
            class="px-4 py-2 bg-red-50 text-red-600 rounded-lg hover:bg-red-100 transition-colors text-sm font-medium"
          >
            Tout supprimer
          </button>
        </div>
      </div>

      <!-- Vue liste -->
      <div v-if="viewMode === 'list'" class="space-y-6">
        <div
          v-for="property in favoriteProperties"
          :key="property.id"
          class="bg-white rounded-lg shadow-md hover:shadow-lg transition overflow-hidden flex flex-col sm:flex-row"
        >
          <!-- Image -->
          <div class="w-full sm:w-40 h-48 sm:h-40 flex-shrink-0">
            <img
              :src="getImageUrl(property)"
              :alt="property.title"
              class="w-full h-full object-cover"
              loading="lazy"
              @error="handleImageError"
            />
          </div>

          <!-- Contenu -->
          <div class="flex-1 p-4 flex flex-col justify-between">
            <div>
              <h3 class="text-lg font-bold text-blue-800 mb-2">{{ property.title }}</h3>
              <p class="text-sm text-gray-600 mb-2">📍 {{ property.city }} • {{ property.type }}</p>
              <p class="text-gray-700 text-sm line-clamp-2">{{ property.description }}</p>
            </div>

            <div class="mt-4 flex items-center justify-between">
              <div class="text-lg font-bold text-blue-600">
                €{{ property.price.toLocaleString('fr-FR') }}
              </div>
              <div class="flex gap-2">
                <button
                  @click="goToDetails(property.id)"
                  class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors text-sm font-medium"
                >
                  Voir détails
                </button>
                <FavoriteButton :property-id="property.id" />
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Vue grille -->
      <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <PropertyCard
          v-for="property in favoriteProperties"
          :key="property.id"
          :item="property"
          @details="goToDetails"
          @contact="handleContact"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useFavorites } from '@/composables/useFavorites'
import { publicPropertyService, type PublicProperty } from '@/api/public-property.service'
import { documentService } from '@/api/document.service'
import PropertyCard from '@/components/PropertyCard.vue'
import FavoriteButton from '@/components/FavoriteButton.vue'
import ViewToggle from '@/components/ViewToggle.vue'
import { getPlaceholderImage } from '@/utils/imageOptimization'
import { exportFavoritesToPDF } from '@/utils/pdfExport'

const router = useRouter()
const { favorites, favoritesCount, clearFavorites } = useFavorites()
const favoriteProperties = ref<PublicProperty[]>([])
const viewMode = ref<'list' | 'grid'>('grid')
const propertyImages = ref<Record<number, any[]>>({})

onMounted(async () => {
  await loadFavoriteProperties()
})

async function loadFavoriteProperties() {
  favoriteProperties.value = []
  
  for (const propertyId of favorites.value) {
    try {
      const property = await publicPropertyService.getPublishedPropertyById(propertyId)
      favoriteProperties.value.push(property)
      
      // Charger les images
      const documents = await documentService.getByPropertyId(propertyId)
      propertyImages.value[propertyId] = documentService.filterImages(documents)
    } catch (error) {
      console.error(`Error loading favorite property ${propertyId}:`, error)
    }
  }
}

function getImageUrl(property: PublicProperty): string {
  const images = propertyImages.value[property.id]
  if (images && images.length > 0) {
    return documentService.getViewUrl(images[0].id)
  }
  return getPlaceholderImage(400, 300)
}

function handleImageError(event: Event) {
  const img = event.target as HTMLImageElement
  img.src = getPlaceholderImage(400, 300)
}

function goToDetails(id: number) {
  router.push(`/property/${id}`)
}

function handleContact(id: number) {
  alert(`Contact pour la propriété ${id}`)
}

function clearAllFavorites() {
  if (confirm('Êtes-vous sûr de vouloir supprimer tous vos favoris ?')) {
    clearFavorites()
    favoriteProperties.value = []
  }
}

function exportToPDF() {
  if (favoriteProperties.value.length === 0) {
    alert('Aucune propriété à exporter')
    return
  }
  exportFavoritesToPDF(favoriteProperties.value)
}
</script>

