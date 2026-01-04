<template>
  <div
    class="bg-white border border-gray-200 rounded-lg hover:shadow-xl hover:border-blue-300 transition-all duration-300 ease-out overflow-hidden flex flex-col h-full transform hover:-translate-y-1"
    :class="{ 'ring-2 ring-blue-200 ring-offset-1 border-blue-300 shadow-md': highlighted }"
  >
    <!-- IMAGE -->
    <div class="w-full h-48 flex-shrink-0 relative overflow-hidden group">
      <ImageOptimized
        :src="imageUrl"
        :alt="generateImageAlt(item)"
        :sizes="'(max-width: 640px) 100vw, (max-width: 1024px) 50vw, 33vw'"
        loading="lazy"
        decoding="async"
        img-class="w-full h-full object-cover transition-transform duration-300 group-hover:scale-110"
        @error="(e) => handleImageError(e)"
      />
      <!-- Badge type de transaction style Google -->
      <div
        class="absolute top-3 left-3 px-2 py-1 rounded text-xs font-medium text-white shadow-sm"
        :style="{ backgroundColor: getTransactionTypeColor() }"
      >
        {{ getTransactionType() }}
      </div>
      <!-- Badge statut style Google -->
      <div
        v-if="item.status !== 'Disponible'"
        class="absolute top-3 right-3 px-2 py-1 rounded text-xs font-medium text-white shadow-sm"
        :style="{ backgroundColor: getStatusColor(item.status) }"
      >
        {{ item.status }}
      </div>
      <!-- Badge nouveau style Google -->
      <div
        v-if="isNew"
        class="absolute top-3 right-3 px-2 py-1 rounded text-xs font-medium text-white bg-green-600 shadow-sm"
      >
        Nouveau
      </div>
    </div>

    <!-- CONTENU -->
    <div class="flex-1 p-5 sm:p-6 flex flex-col">
      <!-- Titre avec lien SEO style Google -->
      <h3 class="text-lg font-medium text-gray-900 mb-2 line-clamp-2">
        <router-link
          :to="item.slug ? `/property-slug/${item.slug}` : `/property/${item.id}`"
          :aria-label="`Voir les détails de ${item.title} à ${item.city}`"
          class="text-blue-600 hover:text-blue-800 hover:underline transition-colors"
          itemprop="url"
        >
          <span
            v-for="(part, idx) in getHighlightedTitle(item.title)"
            :key="idx"
            :class="part.highlighted ? 'bg-yellow-200 px-1 rounded font-medium' : ''"
            itemprop="name"
          >
            {{ part.text }}
          </span>
        </router-link>
      </h3>

      <!-- Localisation et Type de transaction style Google -->
      <div class="flex items-center gap-2 mb-3 flex-wrap">
        <p class="text-sm text-gray-600">
          <MapPin class="h-3 w-3 inline text-gray-500" />
          <span
            v-for="(part, idx) in getHighlightedCity(item.city)"
            :key="idx"
            :class="part.highlighted ? 'bg-yellow-200 px-1 rounded font-medium' : ''"
          >
            {{ part.text }}
          </span>
          <span class="text-gray-300 mx-1">·</span>
          <span>{{ item.type }}</span>
        </p>
      </div>

      <!-- Description style Google -->
      <p class="text-gray-600 text-sm leading-relaxed mb-4 line-clamp-2 flex-1" itemprop="description">
        <span
          v-for="(part, idx) in getHighlightedDescription(item.description)"
          :key="idx"
          :class="part.highlighted ? 'bg-yellow-200 px-1 rounded font-medium' : ''"
        >
          {{ part.text }}
        </span>
      </p>

      <!-- Métadonnées style Google -->
      <div class="flex items-center gap-3 text-xs text-gray-600 mb-4">
        <span v-if="item.bedrooms" itemprop="numberOfRooms" :content="item.bedrooms" class="flex items-center gap-1">
          <Bed class="h-3 w-3 text-gray-500" />
          {{ item.bedrooms }}
        </span>
        <span v-if="item.bathrooms" class="flex items-center gap-1">
          <Bath class="h-3 w-3 text-gray-500" />
          {{ item.bathrooms }}
        </span>
        <span itemprop="floorSize" itemscope itemtype="https://schema.org/QuantitativeValue" class="flex items-center gap-1">
          <span itemprop="value" :content="item.surface" class="hidden">{{ item.surface }}</span>
          <meta itemprop="unitCode" content="MTK" />
          <Square class="h-3 w-3 text-gray-500" />
          {{ item.surface }} m²
        </span>
      </div>

      <!-- Prix et actions style Google -->
      <div class="mt-auto pt-4 border-t border-gray-200">
        <div class="flex items-center justify-between mb-3">
          <div class="flex flex-col">
            <div class="text-lg font-semibold text-gray-900">
              {{ item.price.toLocaleString('fr-FR') }} €
            </div>
            <div class="text-xs text-gray-500">
              {{ getTransactionType() === 'Location' ? '/mois' : '' }}
            </div>
          </div>
          <div v-if="item.rating" class="flex items-center gap-1 text-xs">
            <Star class="h-3 w-3 fill-yellow-400 text-yellow-400" />
            <span class="text-gray-600">{{ item.rating.toFixed(1) }}</span>
          </div>
        </div>
        
        <div class="flex gap-2">
          <button
            @click="handleDetails"
            class="flex-1 px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 active:bg-blue-800 transition-all duration-200 text-sm font-medium shadow-sm hover:shadow-md focus:outline-none focus:ring-2 focus:ring-blue-200 focus:ring-offset-1 transform active:scale-95"
            :aria-label="`Voir les détails de ${item.title}`"
          >
            Voir détails
          </button>
          <ContactForm
            :property-id="item.id"
            :property-title="item.title"
            button-text="Contact"
            button-class="px-4 py-2 bg-white border border-gray-300 text-gray-700 rounded-md hover:bg-gray-50 transition-colors text-sm font-medium shadow-sm"
            @success="handleContactSuccess"
          />
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
import { MapPin, Bed, Bath, Square, Star } from 'lucide-vue-next'
import FavoriteButton from './FavoriteButton.vue'
import CompareButton from './CompareButton.vue'
import ImageOptimized from './ImageOptimized.vue'
import ContactForm from './ContactForm.vue'

type Listing = {
  id: number
  title: string
  city: string
  type: string
  status: 'Disponible' | 'Vendu' | 'Loué'
  transactionType?: 'Location' | 'Vente' | null // Type de transaction depuis l'API
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
  slug?: string // Slug SEO-friendly
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

function getTransactionType(): string {
  // Utiliser transactionType de l'API si disponible
  if (props.item.transactionType) {
    return props.item.transactionType
  }
  
  // Fallback : déduire du status si transactionType n'est pas défini
  if (props.item.status === 'Loué') {
    return 'Location'
  } else if (props.item.status === 'Vendu') {
    return 'Vente'
  }
  
  // Par défaut pour les propriétés disponibles
  return 'Vente'
}

function getTransactionTypeColor(): string {
  const transactionType = getTransactionType()
  if (transactionType === 'Location') {
    return '#1a73e8' // Bleu pour location
  } else if (transactionType === 'Vente') {
    return '#ea4335' // Rouge pour vente
  }
  return '#1a73e8' // Par défaut bleu (location)
}

function handleImageError(event: Event) {
  // Si l'image échoue, on peut utiliser un placeholder
  // Note: ImageOptimized gère déjà l'erreur, mais on peut ajouter une logique supplémentaire ici
  const img = event.target as HTMLImageElement
  if (img && img.src !== getPlaceholderImage(400, 300)) {
    img.src = getPlaceholderImage(400, 300)
  }
}

function handleDetails() {
  emit('details', props.item.id)
  window.location.href = `/property/${props.item.id}`
}

function handleFavoriteToggled(isFavorite: boolean) {
  // Optionnel : émettre un événement ou afficher une notification
  if (isFavorite) {
    console.log(`Propriété ${props.item.id} ajoutée aux favoris`)
  } else {
    console.log(`Propriété ${props.item.id} retirée des favoris`)
  }
}

function handleContactSuccess() {
  emit('contact', props.item.id)
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

/**
 * Génère un alt tag SEO-friendly pour l'image de la propriété
 * Format: "Appartement 3 pièces à Paris - Location - 1200€/mois"
 */
function generateImageAlt(item: Listing): string {
  const parts: string[] = []
  
  // Type de propriété
  if (item.type) {
    parts.push(item.type.toLowerCase())
  }
  
  // Nombre de pièces/chambres
  if (item.bedrooms) {
    parts.push(`${item.bedrooms} ${item.bedrooms > 1 ? 'chambres' : 'chambre'}`)
  }
  
  // Ville
  if (item.city) {
    parts.push(`à ${item.city}`)
  }
  
  // Type de transaction
  const transactionType = getTransactionType()
  if (transactionType) {
    parts.push(`- ${transactionType}`)
  }
  
  // Prix
  if (transactionType === 'Location') {
    parts.push(`- ${item.price.toLocaleString('fr-FR')}€/mois`)
  } else {
    parts.push(`- ${item.price.toLocaleString('fr-FR')}€`)
  }
  
  return parts.join(' ') || item.title
}
</script>

