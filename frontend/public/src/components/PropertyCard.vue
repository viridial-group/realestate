<template>
  <div
    class="bg-white rounded-lg shadow-md hover:shadow-lg transition-all overflow-hidden flex flex-col h-full"
    :class="{ 'ring-2 ring-blue-500 ring-offset-2': highlighted }"
  >
    <!-- IMAGE -->
    <div class="w-full h-48 flex-shrink-0 relative">
      <img
        :src="imageUrl"
        :alt="item.title"
        class="w-full h-full object-cover"
        loading="lazy"
        decoding="async"
        @error="handleImageError"
      />
      <!-- Badge statut -->
      <div
        class="absolute top-2 right-2 px-2 py-1 rounded text-xs font-semibold text-white shadow-lg"
        :style="{ backgroundColor: getStatusColor(item.status) }"
      >
        {{ item.status }}
      </div>
      <!-- Badge nouveau (si créé il y a moins de 7 jours) -->
      <div
        v-if="isNew"
        class="absolute top-2 left-2 px-2 py-1 rounded text-xs font-semibold text-white bg-green-500 shadow-lg"
      >
        Nouveau
      </div>
    </div>

    <!-- CONTENU -->
    <div class="flex-1 p-4 flex flex-col">
      <!-- Titre -->
      <h3 class="text-lg font-bold text-blue-800 dark:text-blue-400 mb-2 line-clamp-2">
        <span
          v-for="(part, idx) in getHighlightedTitle(item.title)"
          :key="idx"
          :class="part.highlighted ? 'bg-yellow-200 dark:bg-yellow-900 px-1 rounded' : ''"
        >
          {{ part.text }}
        </span>
      </h3>

      <!-- Localisation -->
      <p class="text-sm text-gray-600 dark:text-gray-400 mb-3">
        📍 
        <span
          v-for="(part, idx) in getHighlightedCity(item.city)"
          :key="idx"
          :class="part.highlighted ? 'bg-yellow-200 dark:bg-yellow-900 px-1 rounded font-medium' : ''"
        >
          {{ part.text }}
        </span>
        • {{ item.type }}
      </p>

      <!-- Description -->
      <p class="text-gray-700 dark:text-gray-300 text-sm mb-4 line-clamp-2 flex-1">
        <span
          v-for="(part, idx) in getHighlightedDescription(item.description)"
          :key="idx"
          :class="part.highlighted ? 'bg-yellow-200 dark:bg-yellow-900 px-1 rounded font-medium' : ''"
        >
          {{ part.text }}
        </span>
      </p>

      <!-- Métadonnées -->
      <div class="flex items-center gap-4 text-xs text-gray-600 mb-4">
        <span v-if="item.bedrooms">🛏️ {{ item.bedrooms }}</span>
        <span v-if="item.bathrooms">🚿 {{ item.bathrooms }}</span>
        <span>📐 {{ item.surface }} m²</span>
      </div>

      <!-- Prix et actions -->
      <div class="mt-auto pt-4 border-t border-gray-100">
        <div class="flex items-center justify-between mb-3">
          <div class="text-lg font-bold text-blue-600">
            €{{ item.price.toLocaleString('fr-FR') }}
          </div>
          <div v-if="item.rating" class="flex items-center gap-1 text-xs">
            <span class="text-yellow-500">⭐</span>
            <span class="text-gray-600">{{ item.rating.toFixed(1) }}</span>
          </div>
        </div>
        
        <div class="flex gap-2">
          <button
            @click="handleDetails"
            class="flex-1 px-3 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors text-sm font-medium"
          >
            Voir détails
          </button>
          <FavoriteButton
            :property-id="item.id"
            @favorite-toggled="handleFavoriteToggled"
          />
          <CompareButton :property-id="item.id" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, onMounted } from 'vue'
import { documentService } from '@/api/document.service'
import type { Document } from '@/api/document.service'
import { getPlaceholderImage } from '@/utils/imageOptimization'
import { highlightText, parseSearchTerms } from '@/utils/searchHighlight'
import FavoriteButton from './FavoriteButton.vue'
import CompareButton from './CompareButton.vue'

type Listing = {
  id: number
  title: string
  city: string
  type: string
  status: 'Disponible' | 'Vendu' | 'Loué'
  price: number
  surface: number
  bedrooms?: number
  bathrooms?: number
  lat: number
  lng: number
  description: string
  rating?: number
  reviews?: number
  createdAt?: string
}

const props = defineProps<{
  item: Listing
  highlighted?: boolean
  images?: Document[]
  searchQuery?: string
}>()

// Charger les images si non fournies
const propertyImages = ref<Document[]>(props.images || [])

onMounted(async () => {
  if (!props.images || props.images.length === 0) {
    try {
      const documents = await documentService.getByPropertyId(props.item.id)
      propertyImages.value = documentService.filterImages(documents)
    } catch (error) {
      console.error(`Error loading images for property ${props.item.id}:`, error)
      propertyImages.value = []
    }
  }
})

const emit = defineEmits<{
  'details': [id: number]
  'contact': [id: number]
  'click': [id: number]
}>()

const isNew = computed(() => {
  if (!props.item.createdAt) return false
  const created = new Date(props.item.createdAt)
  const now = new Date()
  const diffDays = Math.floor((now.getTime() - created.getTime()) / (1000 * 60 * 60 * 24))
  return diffDays <= 7
})

const imageUrl = computed(() => {
  if (propertyImages.value.length > 0) {
    return documentService.getViewUrl(propertyImages.value[0].id)
  }
  return getPlaceholderImage(400, 300)
})

function getStatusColor(status: string): string {
  switch (status) {
    case 'Disponible':
      return '#34a853'
    case 'Loué':
      return '#fbbc04'
    case 'Vendu':
      return '#ea4335'
    default:
      return '#1a73e8'
  }
}

function handleImageError(event: Event) {
  const img = event.target as HTMLImageElement
  img.src = getPlaceholderImage(400, 300)
}

function handleDetails() {
  emit('details', props.item.id)
  window.location.href = `/property/${props.item.id}`
}

function handleContact() {
  emit('contact', props.item.id)
}

function handleFavoriteToggled(isFavorite: boolean) {
  // Optionnel : émettre un événement ou afficher une notification
  if (isFavorite) {
    console.log(`Propriété ${props.item.id} ajoutée aux favoris`)
  } else {
    console.log(`Propriété ${props.item.id} retirée des favoris`)
  }
}

// Highlighting des termes de recherche
const searchTerms = computed(() => {
  if (!props.searchQuery) {
    return { exactPhrases: [], includeTerms: [], excludeTerms: [] }
  }
  return parseSearchTerms(props.searchQuery)
})

function getHighlightedTitle(title: string): Array<{ text: string; highlighted: boolean }> {
  return highlightText(title, searchTerms.value)
}

function getHighlightedDescription(description: string): Array<{ text: string; highlighted: boolean }> {
  return highlightText(description, searchTerms.value)
}

function getHighlightedCity(city: string): Array<{ text: string; highlighted: boolean }> {
  return highlightText(city, searchTerms.value)
}
</script>

