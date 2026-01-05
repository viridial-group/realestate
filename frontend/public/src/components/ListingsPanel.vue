<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { documentService } from '@/api/document.service'
import type { Document } from '@/api/document.service'
import { getPlaceholderImage } from '@/utils/imageOptimization'
import { highlightText, parseSearchTerms } from '@/utils/searchHighlight'
import CompareButton from './CompareButton.vue'
import ContactForm from './ContactForm.vue'
import { Card, CardContent } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { MapPin, Home, Euro, Square, Star, Eye, ExternalLink } from 'lucide-vue-next'

const router = useRouter()
    
    type Listing = {
      id: number
      title: string
      city: string
      type: string
      status: 'Disponible' | 'Vendu' | 'Loué'
      transactionType?: 'Location' | 'Vente' | null // Type de transaction depuis l'API
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
      'center-on-listing': [id: number]
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
      emit('listing-click', item.id)
      // Naviguer vers la page de détail pour voir les avis
      router.push(`/property/${item.id}`)
    }
    
    
    function handleVisualiser(item: Listing) {
      emit('listing-click', item.id)
        // Centrer la map sur la propriété
      if (item.lat && item.lng) {
        emit('center-on-listing', item.id)
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

    function getTransactionType(item: Listing): string {
      // Utiliser transactionType de l'API si disponible
      if (item.transactionType) {
        return item.transactionType
      }
      
      // Fallback : déduire du status si transactionType n'est pas défini
      if (item.status === 'Loué') {
        return 'Location'
      } else if (item.status === 'Vendu') {
        return 'Vente'
      }
      
      // Par défaut pour les propriétés disponibles
      return 'Vente'
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
    
    // Générer un alt text SEO-friendly pour les images
    function getImageAlt(item: Listing): string {
      const transactionType = getTransactionType(item)
      const transactionText = transactionType === 'Location' ? 'à louer' : 'à vendre'
      return `${item.type} ${transactionText} - ${item.title} - ${item.city} - ${item.surface}m² - ${item.price.toLocaleString('fr-FR')}€`
    }
    
    // Générer l'URL canonique pour chaque propriété
    function getPropertyUrl(item: Listing): string {
      const siteUrl = typeof window !== 'undefined' ? window.location.origin : 'http://viridial.com'
      return `${siteUrl}/property/${item.id}`
    }
    
    // Générer les structured data pour la liste de résultats
    const structuredData = computed(() => {
      if (!props.items || props.items.length === 0) {
        return null
      }
      
      const items = props.items.map((item, index) => {
        const propertyUrl = getPropertyUrl(item)
        const transactionType = getTransactionType(item)
        const isRent = transactionType === 'Location'
        
        const structuredItem: any = {
          '@type': 'ListItem',
          position: (props.pagination?.currentPage || 0) * (props.pagination?.size || 20) + index + 1,
          item: {
            '@type': 'RealEstateListing',
            '@id': propertyUrl,
            name: item.title,
            description: item.description || item.title,
            url: propertyUrl,
            address: {
              '@type': 'PostalAddress',
              addressLocality: item.city,
              addressCountry: 'FR'
            },
            ...(item.lat && item.lng && {
              geo: {
                '@type': 'GeoCoordinates',
                latitude: item.lat,
                longitude: item.lng
              }
            }),
            floorSize: {
              '@type': 'QuantitativeValue',
              value: item.surface,
              unitCode: 'MTK'
            },
            numberOfRooms: item.surface > 0 ? Math.round(item.surface / 20) : undefined,
            offers: {
              '@type': 'Offer',
              price: item.price,
              priceCurrency: 'EUR',
              availability: 'https://schema.org/InStock',
              ...(isRent && {
                priceSpecification: {
                  '@type': 'UnitPriceSpecification',
                  price: item.price,
                  priceCurrency: 'EUR',
                  unitCode: 'MON'
                }
              })
            },
            ...(getImageUrl(item) && !getImageUrl(item).includes('placeholder') && {
              image: getImageUrl(item)
            })
          }
        }
        
        return structuredItem
      })
      
      return {
        '@context': 'https://schema.org',
        '@type': 'ItemList',
        name: `Résultats de recherche immobilière`,
        description: `${props.pagination?.totalElements || props.items.length} annonce${(props.pagination?.totalElements || props.items.length) > 1 ? 's' : ''} trouvée${(props.pagination?.totalElements || props.items.length) > 1 ? 's' : ''}`,
        numberOfItems: props.pagination?.totalElements || props.items.length,
        itemListElement: items
      }
    })
    
    // Injecter les structured data dans le head
    watch(structuredData, (newData) => {
      if (typeof document === 'undefined') return
      
      // Supprimer l'ancien script
      const existingScript = document.querySelector('script[data-listings-structured-data]')
      if (existingScript) {
        existingScript.remove()
      }
      
      // Ajouter le nouveau script si des données existent
      if (newData) {
        const script = document.createElement('script')
        script.type = 'application/ld+json'
        script.setAttribute('data-listings-structured-data', 'true')
        script.textContent = JSON.stringify(newData, null, 2)
        document.head.appendChild(script)
      }
    }, { immediate: true })
    </script>
    
    <template>
      <section class="space-y-6" aria-label="Résultats de recherche immobilière">
        <header>
          <h2 class="text-sm text-gray-600 mb-4">
            Environ {{ props.pagination?.totalElements || props.items.length }} résultat{{ (props.pagination?.totalElements || props.items.length) > 1 ? 's' : '' }}
          </h2>
        </header>
    
        <Card
          v-for="item in paginatedItems"
          :key="item.id"
          :id="`listing-${item.id}`"
          class="overflow-hidden transition-all duration-200 bg-white border border-gray-200 rounded-lg hover:shadow-md hover:border-gray-300"
          :class="{ 'ring-2 ring-blue-200 ring-offset-1 border-blue-300': props.highlightedId === item.id }"
          itemscope
          itemtype="https://schema.org/RealEstateListing"
        >
          <div class="flex flex-col sm:flex-row">
            <!-- IMAGE -->
            <div class="w-full sm:w-64 h-48 sm:h-auto flex-shrink-0 relative overflow-hidden">
              <a
                :href="getPropertyUrl(item)"
                :aria-label="`Voir les détails de ${item.title}`"
                class="block w-full h-full"
              >
                <img
                  :src="getImageUrl(item)"
                  :alt="getImageAlt(item)"
                  class="w-full h-full object-cover transition-transform duration-300 hover:scale-105"
                  loading="lazy"
                  decoding="async"
                  itemprop="image"
                  @error="handleImageError"
                />
              </a>
              <!-- Badge transaction type en overlay style Google -->
              <div class="absolute top-3 left-3">
                <Badge
                  :class="getTransactionType(item) === 'Location' ? 'bg-blue-600' : 'bg-red-600'"
                  class="text-white text-xs font-medium px-2 py-1 rounded shadow-sm"
                >
                  {{ getTransactionType(item) }}
                </Badge>
              </div>
            </div>
    
            <!-- CONTENU -->
            <CardContent class="flex-1 p-6 flex flex-col justify-between">
            <div class="space-y-3">
              <!-- TITRE + ÉTOILES -->
              <div class="flex justify-between items-start gap-4">
                <h3 class="text-xl font-medium text-gray-900 leading-tight" itemprop="name">
                  <a
                    :href="getPropertyUrl(item)"
                    :aria-label="`Voir les détails de ${item.title}`"
                    class="text-blue-600 hover:text-blue-800 hover:underline transition-colors"
                  >
                    <span
                      v-for="(part, idx) in getHighlightedTitle(item.title)"
                      :key="idx"
                      :class="part.highlighted ? 'bg-yellow-200 px-1 rounded font-medium' : ''"
                    >
                      {{ part.text }}
                    </span>
                  </a>
                </h3>
    
                <!-- Étoiles & avis style Google -->
                <div v-if="item.reviews > 0" class="flex items-center gap-1 flex-shrink-0">
                  <div class="flex items-center">
                    <Star
                      v-for="i in 5"
                      :key="i"
                      :class="[
                        'h-4 w-4',
                        i <= Math.round(item.rating) ? 'fill-yellow-400 text-yellow-400' : 'text-gray-300'
                      ]"
                    />
                  </div>
                  <span class="text-sm text-gray-600 ml-1">
                    {{ item.rating.toFixed(1) }} ({{ item.reviews }})
                  </span>
                </div>
              </div>
    
              <!-- LOCALISATION & TYPE style Google -->
              <div class="flex items-center gap-2 flex-wrap" itemprop="address" itemscope itemtype="https://schema.org/PostalAddress">
                <div class="flex items-center gap-1 text-sm text-gray-600">
                  <MapPin class="h-4 w-4 text-gray-500" />
                  <span itemprop="addressLocality">
                    <span
                      v-for="(part, idx) in getHighlightedCity(item.city)"
                      :key="idx"
                      :class="part.highlighted ? 'bg-yellow-200 px-1 rounded font-medium' : ''"
                    >
                      {{ part.text }}
                    </span>
                  </span>
                  <span itemprop="addressCountry" class="hidden">FR</span>
                </div>
                <span class="text-gray-300">·</span>
                <div class="flex items-center gap-1 text-sm text-gray-600">
                  <Home class="h-4 w-4 text-gray-500" />
                  <span itemprop="category">{{ item.type }}</span>
                </div>
              </div>
    
              <!-- DESCRIPTION style Google -->
              <p class="text-gray-600 text-sm leading-relaxed line-clamp-2" itemprop="description">
                <span
                  v-for="(part, idx) in getHighlightedDescription(item.description)"
                  :key="idx"
                  :class="part.highlighted ? 'bg-yellow-200 px-1 rounded font-medium' : ''"
                >
                  {{ part.text }}
                </span>
              </p>
            </div>
    
            <div class="border-t border-gray-200 my-4"></div>
    
            <!-- MÉTADONNÉES & BOUTONS style Google -->
            <div class="flex flex-wrap justify-between items-center gap-4">
              <!-- Détails prix & surface -->
              <div class="flex items-center gap-4" itemprop="offers" itemscope itemtype="https://schema.org/Offer">
                <div class="flex items-center gap-1">
                  <Euro class="h-4 w-4 text-gray-500" />
                  <div class="flex flex-col">
                    <span class="text-lg font-semibold text-gray-900" itemprop="price" :content="item.price">
                      {{ item.price.toLocaleString('fr-FR') }} €
                    </span>
                    <span v-if="getTransactionType(item) === 'Location'" class="text-xs text-gray-500">/mois</span>
                  </div>
                  <span itemprop="priceCurrency" content="EUR" class="hidden">EUR</span>
                </div>
                <span class="text-gray-300">·</span>
                <div class="flex items-center gap-1">
                  <Square class="h-4 w-4 text-gray-500" />
                  <div class="flex flex-col">
                    <span class="text-sm font-medium text-gray-700" itemprop="floorSize" itemscope itemtype="https://schema.org/QuantitativeValue">
                      <span itemprop="value" :content="item.surface">{{ item.surface }}</span>
                      <span itemprop="unitCode" content="MTK" class="hidden">MTK</span>
                      <span> m²</span>
                    </span>
                  </div>
                </div>
                <meta itemprop="availability" content="https://schema.org/InStock" />
              </div>
    
              <!-- Boutons style Google -->
              <div class="flex flex-wrap gap-2">
                <ContactForm
                  :property-id="item.id"
                  :property-title="item.title"
                  button-text="Contact"
                  button-class="inline-flex items-center justify-center gap-2 rounded-md text-sm font-medium transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-blue-200 focus-visible:ring-offset-1 disabled:pointer-events-none disabled:opacity-50 border border-gray-300 bg-white hover:bg-gray-50 text-gray-700 h-9 px-4 shadow-sm"
                />

                <Button
                  variant="outline"
                  size="sm"
                  as-child
                  class="border-gray-300 bg-white hover:bg-gray-50 text-gray-700 shadow-sm"
                >
                  <a
                    :href="getPropertyUrl(item)"
                    :aria-label="`Voir les détails de ${item.title}`"
                    itemprop="url"
                  >
                    <ExternalLink class="h-4 w-4 mr-2" />
                    Détails
                  </a>
                </Button>

                <Button
                  variant="outline"
                  size="sm"
                  @click="handleVisualiser(item)"
                  class="border-gray-300 bg-white hover:bg-gray-50 text-gray-700 shadow-sm"
                >
                  <Eye class="h-4 w-4 mr-2" />
                  Carte
                </Button>

                <CompareButton :property-id="item.id" />

                <Button
                  v-if="item.reviews > 0"
                  variant="outline"
                  size="sm"
                  @click="handleAvis(item)"
                  class="border-gray-300 bg-white hover:bg-gray-50 text-gray-700 shadow-sm"
                >
                  <Star class="h-4 w-4 mr-2" />
                  Avis
                </Button>
              </div>
            </div>
          </CardContent>
          </div>
        </Card>
    
        <div
          v-if="props.items.length === 0"
          class="text-center text-gray-500 py-10"
          role="status"
          aria-live="polite"
        >
          <p>Aucune annonce trouvée</p>
          <p class="text-sm mt-2">Essayez de modifier vos critères de recherche</p>
        </div>
        
        <!-- Pagination serveur style Google -->
        <nav
          v-if="props.pagination && props.pagination.totalPages > 1"
          class="flex items-center justify-center gap-1 mt-8 pt-6 border-t border-gray-200"
          aria-label="Pagination des résultats"
        >
          <Button
            variant="outline"
            size="sm"
            @click="prevPage"
            :disabled="props.pagination.first"
            class="border-gray-300 bg-white hover:bg-gray-50 text-gray-700 shadow-sm disabled:opacity-50"
          >
            ‹ Précédent
          </Button>
          
          <div class="flex gap-1">
            <Button
              v-for="page in visiblePages"
              :key="page"
              :variant="typeof page === 'number' && page === props.pagination.currentPage ? 'default' : 'outline'"
              :size="'sm'"
              :disabled="typeof page === 'string'"
              @click="typeof page === 'number' && goToPage(page)"
              :class="[
                'min-w-[40px]',
                typeof page === 'number' && page === props.pagination.currentPage
                  ? 'bg-blue-600 text-white border-blue-600 hover:bg-blue-700'
                  : 'border-gray-300 bg-white hover:bg-gray-50 text-gray-700 shadow-sm'
              ]"
            >
              {{ typeof page === 'number' ? page + 1 : page }}
            </Button>
          </div>
          
          <Button
            variant="outline"
            size="sm"
            @click="nextPage"
            :disabled="props.pagination.last"
            class="border-gray-300 bg-white hover:bg-gray-50 text-gray-700 shadow-sm disabled:opacity-50"
          >
            Suivant ›
          </Button>
        </nav>
        
        <!-- Info pagination style Google -->
        <div
          v-if="props.pagination && props.items.length > 0"
          class="text-center text-xs text-gray-500 mt-4"
          role="status"
          aria-live="polite"
        >
          <p>
            Page {{ props.pagination.currentPage + 1 }} sur {{ props.pagination.totalPages }}
          </p>
        </div>
      </section>
    </template>
    