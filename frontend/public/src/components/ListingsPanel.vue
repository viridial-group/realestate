<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { documentService } from '@/api/document.service'
import type { Document } from '@/api/document.service'
import { getPlaceholderImage } from '@/utils/imageOptimization'
import { highlightText, parseSearchTerms } from '@/utils/searchHighlight'
import CompareButton from './CompareButton.vue'
    
    type Listing = {
      id: number
      title: string
      city: string
      type: string
      status: 'Disponible' | 'Vendu' | 'Loué'
      price: number
      surface: number
      lat: number
      lng: number
      description: string
      rating: number
      reviews: number
    }
    
    const props = defineProps<{ 
      items: Listing[]
      highlightedId?: number | null
      searchQuery?: string
      pagination?: {
        currentPage: number
        totalPages: number
        totalElements: number
        size: number
        first: boolean
        last: boolean
      }
    }>()
    
    const emit = defineEmits<{
      'listing-click': [id: number]
      'page-change': [page: number]
      'next-page': []
      'prev-page': []
    }>()
    
    // Utiliser les items directement (pagination côté serveur)
    const paginatedItems = computed(() => props.items)
    
    function goToPage(page: number) {
      emit('page-change', page)
      window.scrollTo({ top: 0, behavior: 'smooth' })
    }
    
    function nextPage() {
      emit('next-page')
      window.scrollTo({ top: 0, behavior: 'smooth' })
    }
    
    function prevPage() {
      emit('prev-page')
      window.scrollTo({ top: 0, behavior: 'smooth' })
    }
    
    // Helper pour générer les pages visibles (basé sur pagination serveur)
    const visiblePages = computed(() => {
      if (!props.pagination) return []
      
      const pages: (number | string)[] = []
      const total = props.pagination.totalPages
      const current = props.pagination.currentPage
      
      if (total <= 7) {
        // Afficher toutes les pages si <= 7
        for (let i = 0; i < total; i++) {
          pages.push(i)
        }
      } else {
        // Logique avec ellipsis (pages 0-indexed)
        if (current <= 2) {
          for (let i = 0; i <= 3; i++) pages.push(i)
          pages.push('...')
          pages.push(total - 1)
        } else if (current >= total - 3) {
          pages.push(0)
          pages.push('...')
          for (let i = total - 4; i < total; i++) pages.push(i)
        } else {
          pages.push(0)
          pages.push('...')
          for (let i = current - 1; i <= current + 1; i++) pages.push(i)
          pages.push('...')
          pages.push(total - 1)
        }
      }
      return pages
    })
    
    // Actions
    function handleAvis(item: Listing) {
      alert(`Voir avis pour : ${item.title}`)
    }
    
    function handleContact(item: Listing) {
      alert(`Contact pour : ${item.title}`)
    }
    
    function handleDetails(item: Listing) {
      // Navigate to details page
      window.location.href = `/property/${item.id}`
    }
    
    function handleVisualiser(item: Listing) {
      emit('listing-click', item.id)
      // TODO: Center map on listing
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
    
    // Images
    const propertyImages = ref<Record<number, Document[]>>({})
    
    // Charger les images pour chaque propriété
    async function loadImagesForProperty(propertyId: number) {
      if (propertyImages.value[propertyId]) {
        return // Déjà chargé
      }
      
      try {
        const documents = await documentService.getByPropertyId(propertyId)
        propertyImages.value[propertyId] = documentService.filterImages(documents)
      } catch (error) {
        console.error(`Error loading images for property ${propertyId}:`, error)
        propertyImages.value[propertyId] = []
      }
    }
    
    // Charger les images pour toutes les propriétés visibles (lazy loading)
    watch(() => props.items, async (newItems) => {
      // Charger les images de manière asynchrone et progressive
      // Ne pas bloquer le rendu initial
      newItems.forEach((item, index) => {
        // Délai progressif pour éviter de surcharger l'API
        setTimeout(() => {
          loadImagesForProperty(item.id)
        }, index * 50) // 50ms entre chaque chargement
      })
    }, { immediate: true })
    
    function getImageUrl(item: Listing): string {
      const images = propertyImages.value[item.id]
      if (images && images.length > 0) {
        return documentService.getViewUrl(images[0].id)
      }
      // Placeholder SVG optimisé
      return getPlaceholderImage(400, 300)
    }
    
    function handleImageError(event: Event) {
      const img = event.target as HTMLImageElement
      img.src = getPlaceholderImage(400, 300)
    }
    </script>
    
    <template>
      <div class="space-y-6">
        <h2 class="text-xl font-semibold text-gray-800">
          {{ props.pagination?.totalElements || props.items.length }} annonce{{ (props.pagination?.totalElements || props.items.length) > 1 ? 's' : '' }} trouvée{{ (props.pagination?.totalElements || props.items.length) > 1 ? 's' : '' }}
        </h2>
    
        <div
          v-for="item in paginatedItems"
          :key="item.id"
          :id="`listing-${item.id}`"
          class="bg-white shadow-md rounded-lg hover:shadow-lg transition overflow-hidden flex flex-col sm:flex-row"
          :class="{ 'ring-2 ring-primary ring-offset-2': props.highlightedId === item.id }"
        >
          <!-- IMAGE -->
          <div class="w-full sm:w-40 h-48 sm:h-40 flex-shrink-0">
            <img
              :src="getImageUrl(item)"
              alt="Image annonce"
              class="w-full h-full object-cover"
              loading="lazy"
              decoding="async"
              @error="handleImageError"
            />
          </div>
    
          <!-- CONTENU -->
          <div class="flex-1 p-4 flex flex-col justify-between">
            <div>
              <!-- TITRE + ÉTOILES -->
              <div class="flex justify-between items-start flex-wrap">
                <h3 class="text-lg font-bold text-blue-800 dark:text-blue-400">
                  <span
                    v-for="(part, idx) in getHighlightedTitle(item.title)"
                    :key="idx"
                    :class="part.highlighted ? 'bg-yellow-200 dark:bg-yellow-900 px-1 rounded' : ''"
                  >
                    {{ part.text }}
                  </span>
                </h3>
    
                <!-- Étoiles & avis -->
                <div class="flex items-center text-sm mt-1 sm:mt-0">
                  <span
                    v-for="i in 5"
                    :key="i"
                    class="text-xs mr-0.5"
                    :class="i <= Math.round(item.rating) ? 'text-yellow-500' : 'text-gray-300'"
                  >
                    ★
                  </span>
                  <span class="text-gray-600 ml-2 text-sm">
                    ({{ item.reviews }} avis)
                  </span>
                </div>
              </div>
    
              <!-- LOCALISATION & TYPE -->
              <p class="text-sm text-gray-600 dark:text-gray-400 mt-1">
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
    
              <!-- DESCRIPTION -->
              <p class="text-gray-700 dark:text-gray-300 text-sm mt-2 line-clamp-3">
                <span
                  v-for="(part, idx) in getHighlightedDescription(item.description)"
                  :key="idx"
                  :class="part.highlighted ? 'bg-yellow-200 dark:bg-yellow-900 px-1 rounded font-medium' : ''"
                >
                  {{ part.text }}
                </span>
              </p>
            </div>
    
            <!-- MÉTADONNÉES & BOUTONS -->
            <div class="mt-4 flex flex-wrap justify-between items-center gap-2">
              <!-- Détails prix & surface -->
              <div class="text-sm text-gray-800 font-medium">
                €{{ item.price }} • {{ item.surface }} m²
              </div>
    
              <!-- Boutons style Google -->
              <div class="flex flex-wrap gap-2">
                <button
                  @click="handleContact(item)"
                  class="bg-blue-50 text-blue-700 px-3 py-1 rounded-full hover:bg-blue-100 transition flex items-center justify-center"
                >
                  <span class="font-medium text-sm">Contact</span>
                </button>

                <button
                  @click="handleDetails(item)"
                  class="bg-gray-50 text-gray-800 px-3 py-1 rounded-full hover:bg-gray-100 transition flex items-center justify-center"
                >
                  <span class="font-medium text-sm">Détails</span>
                </button>

                <button
                  @click="handleVisualiser(item)"
                  class="bg-purple-50 text-purple-700 px-3 py-1 rounded-full hover:bg-purple-100 transition flex items-center justify-center"
                >
                  <span class="font-medium text-sm">Voir Map</span>
                </button>

                <CompareButton :property-id="item.id" />

                <button
                  @click="handleAvis(item)"
                  class="bg-yellow-50 text-yellow-800 px-3 py-1 rounded-full hover:bg-yellow-100 transition flex items-center justify-center"
                >
                  <span class="font-medium text-sm">Avis</span>
                </button>
              </div>
            </div>
          </div>
        </div>
    
        <div
          v-if="props.items.length === 0"
          class="text-center text-gray-500 py-10"
        >
          Aucune annonce trouvée
        </div>
        
        <!-- Pagination serveur -->
        <div
          v-if="props.pagination && props.pagination.totalPages > 1"
          class="flex items-center justify-center gap-2 mt-8 pt-6 border-t border-gray-200"
        >
          <button
            @click="prevPage"
            :disabled="props.pagination.first"
            class="px-4 py-2 rounded-lg border border-gray-300 bg-white text-gray-700 hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed transition-colors text-sm font-medium"
          >
            ‹ Précédent
          </button>
          
          <div class="flex gap-1">
            <button
              v-for="page in visiblePages"
              :key="page"
              @click="typeof page === 'number' && goToPage(page)"
              :disabled="typeof page === 'string'"
              class="px-3 py-2 rounded-lg border text-sm font-medium transition-colors min-w-[40px]"
              :class="
                typeof page === 'number' && page === props.pagination.currentPage
                  ? 'bg-blue-600 text-white border-blue-600'
                  : typeof page === 'string'
                  ? 'border-transparent text-gray-400 cursor-default'
                  : 'bg-white text-gray-700 border-gray-300 hover:bg-gray-50'
              "
            >
              {{ typeof page === 'number' ? page + 1 : page }}
            </button>
          </div>
          
          <button
            @click="nextPage"
            :disabled="props.pagination.last"
            class="px-4 py-2 rounded-lg border border-gray-300 bg-white text-gray-700 hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed transition-colors text-sm font-medium"
          >
            Suivant ›
          </button>
        </div>
        
        <!-- Info pagination -->
        <div
          v-if="props.pagination && props.items.length > 0"
          class="text-center text-sm text-gray-500 mt-4"
        >
          Affichage de {{ props.pagination.currentPage * props.pagination.size + 1 }} à 
          {{ Math.min((props.pagination.currentPage + 1) * props.pagination.size, props.pagination.totalElements) }} 
          sur {{ props.pagination.totalElements }} annonce{{ props.pagination.totalElements > 1 ? 's' : '' }}
        </div>
      </div>
    </template>
    