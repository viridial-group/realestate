<template>
  <div
    v-if="favoritesCount > 0"
    class="fixed bottom-4 left-4 z-50 bg-white rounded-lg shadow-2xl border border-gray-200 max-w-sm w-full mx-4 transition-all duration-300"
    :class="isExpanded ? 'max-h-[80vh]' : 'max-h-20'"
  >
    <!-- Header -->
    <div
      class="flex items-center justify-between p-4 cursor-pointer hover:bg-gray-50 transition-colors"
      @click="toggleExpanded"
    >
      <div class="flex items-center gap-3">
        <div class="relative">
          <Star class="h-5 w-5 text-yellow-500 fill-yellow-500" />
          <span
            v-if="favoritesCount > 0"
            class="absolute -top-1 -right-1 bg-yellow-500 text-white text-xs font-bold rounded-full w-5 h-5 flex items-center justify-center"
          >
            {{ favoritesCount }}
          </span>
        </div>
        <div>
          <p class="text-sm font-semibold text-gray-900">
            Favoris ({{ favoritesCount }})
          </p>
          <p class="text-xs text-gray-600">
            {{ favoritesCount === 1 ? 'propriété' : 'propriétés' }} sauvegardée{{ favoritesCount > 1 ? 's' : '' }}
          </p>
        </div>
      </div>
      <ChevronDown
        class="h-5 w-5 text-gray-400 transition-transform"
        :class="{ 'rotate-180': isExpanded }"
      />
    </div>

    <!-- Content (expanded) -->
    <div v-if="isExpanded" class="border-t border-gray-200 max-h-[calc(80vh-80px)] overflow-y-auto">
      <!-- Properties list -->
      <div class="p-4 space-y-3">
        <div
          v-for="property in favoriteProperties"
          :key="property.id"
          class="flex items-center gap-3 p-3 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors cursor-pointer"
          @click="goToProperty(property.id)"
        >
          <img
            :src="getPropertyImage(property)"
            :alt="property.title"
            class="w-16 h-16 object-cover rounded"
            @error="handleImageError"
          />
          <div class="flex-1 min-w-0">
            <p class="text-sm font-medium text-gray-900 truncate">{{ property.title }}</p>
            <p class="text-xs text-gray-600">{{ formatPrice(property.price) }}</p>
            <p class="text-xs text-gray-500">{{ property.city }}</p>
          </div>
          <button
            @click.stop="removeFavorite(property.id)"
            class="p-1 text-gray-400 hover:text-red-600 transition-colors"
            title="Retirer des favoris"
          >
            <X class="h-4 w-4" />
          </button>
        </div>
      </div>

      <!-- Actions -->
      <div class="p-4 border-t border-gray-200 bg-white space-y-2">
        <router-link
          to="/favorites"
          class="w-full px-4 py-2 bg-yellow-500 text-white rounded-lg hover:bg-yellow-600 transition-colors text-sm font-medium text-center block"
        >
          Voir tous les favoris
        </router-link>
        <button
          v-if="favoritesCount > 0"
          @click="clearAll"
          class="w-full px-4 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition-colors text-sm font-medium"
        >
          Tout supprimer
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { Star, ChevronDown, X } from 'lucide-vue-next'
import { useFavorites } from '@/composables/useFavorites'
import { publicPropertyService, type PublicProperty } from '@/api/public-property.service'
import { documentService } from '@/api/document.service'
import { getPlaceholderImage } from '@/utils/imageOptimization'
import { useToast } from '@/composables/useToast'

const router = useRouter()
const isExpanded = ref(false)
const favoriteProperties = ref<PublicProperty[]>([])
const propertyImages = ref<Record<number, any[]>>({})

const { favorites, favoritesCount, removeFavorite: removeFavoriteFromStore, clearFavorites } = useFavorites()
const { success } = useToast()

function toggleExpanded() {
  isExpanded.value = !isExpanded.value
  if (isExpanded.value && favoriteProperties.value.length === 0) {
    loadFavorites()
  }
}

async function loadFavorites() {
  favoriteProperties.value = []
  propertyImages.value = {}

  // Charger seulement les 5 premiers pour le panneau
  const favoritesToLoad = favorites.value.slice(0, 5)

  for (const propertyId of favoritesToLoad) {
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

function getPropertyImage(property: PublicProperty): string {
  const images = propertyImages.value[property.id]
  if (images && images.length > 0) {
    return documentService.getViewUrl(images[0].id)
  }
  return getPlaceholderImage(64, 64)
}

function handleImageError(event: Event) {
  const img = event.target as HTMLImageElement
  img.src = getPlaceholderImage(64, 64)
}

function formatPrice(price: number): string {
  return new Intl.NumberFormat('fr-FR', {
    style: 'currency',
    currency: 'EUR',
    maximumFractionDigits: 0,
  }).format(price)
}

function removeFavorite(propertyId: number) {
  removeFavoriteFromStore(propertyId)
  favoriteProperties.value = favoriteProperties.value.filter(p => p.id !== propertyId)
  success('Propriété retirée des favoris')
}

function clearAll() {
  if (confirm('Êtes-vous sûr de vouloir supprimer tous les favoris ?')) {
    clearFavorites()
    favoriteProperties.value = []
    isExpanded.value = false
    success('Tous les favoris ont été supprimés')
  }
}

function goToProperty(id: number) {
  router.push(`/property/${id}`)
  isExpanded.value = false
}

watch(favorites, () => {
  if (isExpanded.value) {
    loadFavorites()
  }
}, { deep: true })

onMounted(() => {
  if (favoritesCount.value > 0) {
    loadFavorites()
  }
})
</script>

<style scoped>
.transition-all {
  transition-property: all;
  transition-timing-function: cubic-bezier(0.4, 0, 0.2, 1);
}
</style>

