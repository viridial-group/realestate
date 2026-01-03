<script setup lang="ts">
    import { ref, computed, onMounted, watch } from 'vue'
    import { useRoute } from 'vue-router'
    import { usePublicProperties } from '@/composables/usePublicProperties'
    import { ref as mapViewRef } from 'vue'
    import FiltersBar from '@/components/FiltersBar.vue'
    import ListingsPanel from '@/components/ListingsPanel.vue'
    import InterestsPanel from '@/components/InterestsPanel.vue'
    import MapView from '@/components/MapView.vue'
    import QuickFilters from '@/components/QuickFilters.vue'
    import ViewToggle from '@/components/ViewToggle.vue'
    import Breadcrumbs from '@/components/Breadcrumbs.vue'
    import PropertyCard from '@/components/PropertyCard.vue'
    import AdvancedFilters from '@/components/AdvancedFilters.vue'
    import SearchSuggestions from '@/components/SearchSuggestions.vue'
    import ShareSearchButton from '@/components/ShareSearchButton.vue'
    import SearchStats from '@/components/SearchStats.vue'
    import { interests } from '@/data/interests'
    import { useShareSearch } from '@/composables/useShareSearch'
    
    // Composable pour les propriétés
    const { formattedProperties, loading, error, pagination, loadProperties } = usePublicProperties()
    
    // Filtres
    const query = ref('')
    const type = ref('Tous')
    const status = ref('Tous')
    const sortBy = ref('default')
    const maxPrice = ref<number | null>(null)
    const minPrice = ref<number | null>(null)
    const minSurface = ref<number | null>(null)
    const maxSurface = ref<number | null>(null)
    const bedrooms = ref<number | null>(null)
    const bathrooms = ref<number | null>(null)
    const dateFilter = ref<string>('')
    const currentPage = ref(0)
    const highlightedListingId = ref<number | null>(null)
    const mapView = mapViewRef<InstanceType<typeof MapView> | null>(null)
    const userLocation = ref<{ lat: number; lng: number } | null>(null)
    const viewMode = ref<'list' | 'grid'>('list')
    
    // Suggestions de recherche similaires
    const searchSuggestions = computed(() => {
      const suggestions: string[] = []
      if (query.value) {
        // Suggestions basées sur la recherche actuelle
        if (query.value.toLowerCase().includes('paris')) {
          suggestions.push('Appartement Paris', 'Studio Paris', 'Villa Paris')
        } else if (query.value.toLowerCase().includes('lyon')) {
          suggestions.push('Appartement Lyon', 'Maison Lyon', 'Studio Lyon')
        } else if (query.value.toLowerCase().includes('marseille')) {
          suggestions.push('Appartement Marseille', 'Villa Marseille', 'Studio Marseille')
        }
      }
      
      // Suggestions génériques si pas de recherche
      if (!query.value && filteredListings.value.length > 0) {
        suggestions.push('Appartement à louer', 'Villa avec jardin', 'Studio étudiant', 'Maison familiale')
      }
      
      return suggestions.slice(0, 6) // Limiter à 6 suggestions
    })
    
    // Charger les propriétés au montage
    onMounted(() => {
      // Vérifier si des paramètres de recherche sont passés depuis la landing page
      const route = useRoute()
      if (route.query.search) {
        query.value = route.query.search as string
      }
      if (route.query.type) {
        type.value = route.query.type as string
      }
      fetchProperties()
    })
    
    // Watcher pour recharger quand les filtres changent (avec debounce amélioré)
    let debounceTimer: ReturnType<typeof setTimeout> | null = null
    watch([query, type, status, maxPrice, minSurface, bedrooms, bathrooms], () => {
      // Réinitialiser à la page 0 quand les filtres changent
      currentPage.value = 0
      if (debounceTimer) {
        clearTimeout(debounceTimer)
      }
      debounceTimer = setTimeout(() => {
        fetchProperties()
      }, 500) // Debounce de 500ms (augmenté pour réduire les appels API)
    })
    
    // Watcher pour changer de page
    watch(currentPage, () => {
      fetchProperties()
    })
    
    // Fonction pour charger les propriétés depuis l'API
    async function fetchProperties() {
      const params: any = {
        page: currentPage.value,
        size: 20, // 20 items par page
      }
      
      // Ajouter les filtres
      if (query.value && query.value.trim()) {
        params.search = query.value.trim()
      }
      
      if (type.value && type.value !== 'Tous') {
        params.type = type.value
      }
      
      if (minPrice.value) {
        params.minPrice = minPrice.value
      }
      
      if (maxPrice.value) {
        params.maxPrice = maxPrice.value
      }
      
      if (minSurface.value) {
        params.minSurface = minSurface.value
      }
      
      if (maxSurface.value) {
        params.maxSurface = maxSurface.value
      }
      
      if (bedrooms.value) {
        params.bedrooms = bedrooms.value
      }
      
      if (bathrooms.value) {
        params.bathrooms = bathrooms.value
      }
      
      // Ajouter le tri côté serveur
      if (sortBy.value && sortBy.value !== 'default') {
        params.sortBy = sortBy.value
      }
      
      // Filtre par date
      if (dateFilter.value) {
        const now = new Date()
        let startDate: Date
        
        switch (dateFilter.value) {
          case 'today':
            startDate = new Date(now.getFullYear(), now.getMonth(), now.getDate())
            break
          case 'week':
            startDate = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000)
            break
          case 'month':
            startDate = new Date(now.getFullYear(), now.getMonth(), 1)
            break
          case '3months':
            startDate = new Date(now.getFullYear(), now.getMonth() - 3, now.getDate())
            break
          default:
            startDate = new Date(0) // Toutes les dates
        }
        
        // Convertir en ISO string pour l'API
        params.createdAfter = startDate.toISOString()
      }
      
      await loadProperties(params)
    }
    
    // Fonctions de pagination
    function goToPage(page: number) {
      if (page >= 0 && page < pagination.value.totalPages) {
        currentPage.value = page
      }
    }
    
    function nextPage() {
      if (!pagination.value.last) {
        currentPage.value++
      }
    }
    
    function prevPage() {
      if (!pagination.value.first) {
        currentPage.value--
      }
    }
    
    // Propriétés filtrées (filtre status côté client car l'API filtre déjà PUBLISHED/AVAILABLE)
    // Le tri est maintenant fait côté serveur
    const filteredListings = computed(() => {
      let filtered = [...formattedProperties.value]
      
      // Filtrer les propriétés sans coordonnées valides (ne pas les afficher sur la map)
      filtered = filtered.filter(l => {
        // Si la propriété a des coordonnées valides ou si on ne filtre pas par coordonnées
        return (l as any).hasCoordinates !== false || true // Pour l'instant, on affiche tout
      })
      
      // Filtre par status (côté client car l'API filtre déjà PUBLISHED/AVAILABLE)
      if (status.value && status.value !== 'Tous') {
        filtered = filtered.filter(l => l.status === status.value)
      }
      
      // Le tri est maintenant fait côté serveur, pas besoin de trier ici
      
      return filtered
    })
    
    function clearFilters() {
      maxPrice.value = null
      minPrice.value = null
      minSurface.value = null
      maxSurface.value = null
      bedrooms.value = null
      bathrooms.value = null
      query.value = ''
      type.value = 'Tous'
      status.value = 'Tous'
      sortBy.value = 'default'
      dateFilter.value = ''
      currentPage.value = 0
      fetchProperties()
    }
    
    function handleMarkerClick(listingId: number) {
      highlightedListingId.value = listingId
      // Scroll vers le listing correspondant
      setTimeout(() => {
        const element = document.getElementById(`listing-${listingId}`)
        if (element) {
          element.scrollIntoView({ behavior: 'smooth', block: 'center' })
        }
      }, 100)
    }
    
    function handleListingClick(listingId: number) {
      highlightedListingId.value = listingId
      // Centrer la map sur le listing
      if (mapView.value) {
        mapView.value.centerOnListing(listingId)
      }
    }
    
    function handleInterestClick(interest: { lat: number; lng: number; icon: string }) {
      // Zoom sur le POI dans la map
      if (mapView.value) {
        mapView.value.centerOnPOI(interest.lat, interest.lng)
      }
    }
    
    function handleContact(listingId: number) {
      alert(`Contact pour la propriété ${listingId}`)
      // TODO: Implémenter modal de contact
    }
    
    function handleSuggestionClick(suggestion: string) {
      query.value = suggestion
      currentPage.value = 0
      fetchProperties()
    }
    
    function handleUserLocationUpdated(location: { lat: number; lng: number }) {
      userLocation.value = location
    }
    
    // Gestion des filtres rapides
    function handleQuickFilterChange(filter: { type: string; key: string; value: any }) {
      if (filter.type === 'price') {
        if (filter.value.maxPrice) {
          maxPrice.value = filter.value.maxPrice
        }
        if (filter.value.minPrice) {
          // Pour les filtres rapides, on peut aussi définir un minPrice si nécessaire
        }
      } else if (filter.type === 'type') {
        type.value = filter.value.key
      } else if (filter.type === 'surface') {
        if (filter.value.maxSurface) {
          minSurface.value = filter.value.maxSurface
        }
        if (filter.value.minSurface) {
          minSurface.value = filter.value.minSurface
        }
      }
    }
    
    function handleClearQuickFilters() {
      maxPrice.value = null
      minSurface.value = null
      type.value = 'Tous'
    }
    
    // Breadcrumbs
    const breadcrumbs = computed(() => {
      const items = [{ label: 'Recherche' }]
      if (query.value || type.value !== 'Tous' || status.value !== 'Tous') {
        items.push({ label: 'Résultats de recherche' })
      }
      return items
    })

    // Compter les filtres actifs
    function getActiveFiltersCount(): number {
      let count = 0
      if (query.value && query.value.trim()) count++
      if (type.value && type.value !== 'Tous') count++
      if (status.value && status.value !== 'Tous') count++
      if (maxPrice.value) count++
      if (minPrice.value) count++
      if (minSurface.value) count++
      if (maxSurface.value) count++
      if (bedrooms.value) count++
      if (bathrooms.value) count++
      if (dateFilter.value) count++
      if (sortBy.value && sortBy.value !== 'default') count++
      return count
    }

    const hasActiveFilters = computed(() => getActiveFiltersCount() > 0)
    </script>
    
    <template>
      <div>
        <!-- Breadcrumbs avec bouton de partage -->
        <div class="flex items-center justify-between mb-4">
          <Breadcrumbs :items="breadcrumbs" />
          <ShareSearchButton
            :search-params="{
              query,
              type,
              status,
              maxPrice,
              minSurface,
              bedrooms,
              bathrooms,
              sortBy,
              dateFilter
            }"
          />
        </div>
        
        <!-- Filtres -->
        <FiltersBar
          v-model:query="query"
          v-model:type="type"
          v-model:status="status"
          v-model:sortBy="sortBy"
          v-model:maxPrice="maxPrice"
          v-model:minSurface="minSurface"
          v-model:bedrooms="bedrooms"
          v-model:bathrooms="bathrooms"
          v-model:dateFilter="dateFilter"
          :show-date-filter="true"
          @clear-filters="clearFilters"
        />
        
        <!-- Filtres rapides -->
        <div class="mt-4">
          <QuickFilters
            :active-filters="{ maxPrice, type, minSurface }"
            @filter-change="handleQuickFilterChange"
            @clear-all="handleClearQuickFilters"
          />
        </div>
        
        <!-- Filtres avancés -->
        <div class="mt-4">
          <AdvancedFilters
            :minPrice="minPrice"
            :maxPrice="maxPrice"
            :minSurface="minSurface"
            :maxSurface="maxSurface"
            :bedrooms="bedrooms"
            :bathrooms="bathrooms"
            @update:minPrice="minPrice = $event"
            @update:maxPrice="maxPrice = $event"
            @update:minSurface="minSurface = $event"
            @update:maxSurface="maxSurface = $event"
            @update:bedrooms="bedrooms = $event"
            @update:bathrooms="bathrooms = $event"
            @apply="fetchProperties"
            @reset="clearFilters"
          />
        </div>
        
        <!-- État de chargement -->
        <div v-if="loading" class="mt-6 text-center py-12">
          <div class="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
          <p class="mt-4 text-gray-600">Chargement des propriétés...</p>
        </div>
        
        <!-- Message d'erreur -->
        <div v-else-if="error" class="mt-6 bg-red-50 border border-red-200 rounded-lg p-4">
          <div class="flex items-center gap-2">
            <span class="text-red-600">⚠️</span>
            <p class="text-red-800">{{ error }}</p>
          </div>
          <button
            @click="fetchProperties"
            class="mt-3 px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors text-sm"
          >
            Réessayer
          </button>
        </div>
        
        <!-- Contenu principal -->
        <template v-else>
          <!-- Statistiques de recherche -->
          <SearchStats
            :stats="{
              totalResults: pagination?.totalElements || 0,
              filtersActive: getActiveFiltersCount()
            }"
            :show-stats="true"
            :show-clear="hasActiveFilters"
            @clear="clearFilters"
          />
          
          <!-- 🗺️ MAP APRÈS FILTRES -->
          <div class="mt-6">
            <div class="mb-3 flex items-center justify-between">
              <h3 class="text-lg font-semibold text-gray-800">
                {{ filteredListings.length }} annonce{{ filteredListings.length > 1 ? 's' : '' }} sur la carte
              </h3>
            </div>
            <MapView 
              ref="mapView"
              :listings="filteredListings" 
              :interests="interests"
              :highlighted-listing-id="highlightedListingId"
              @marker-click="handleMarkerClick"
              @user-location-updated="handleUserLocationUpdated"
            />
          </div>
        
          <!-- Résultats -->
          <div class="grid grid-cols-1 lg:grid-cols-12 gap-6 mt-6">
            <section class="lg:col-span-8">
              <!-- En-tête avec toggle vue et compteur -->
              <div class="flex items-center justify-between mb-4">
                <div class="flex items-center gap-4">
                  <h2 class="text-xl font-semibold text-gray-800">
                    {{ pagination?.totalElements || filteredListings.length }} annonce{{ (pagination?.totalElements || filteredListings.length) > 1 ? 's' : '' }}
                  </h2>
                  <ViewToggle v-model="viewMode" />
                </div>
              </div>
              
              <!-- Vue liste -->
              <div v-if="viewMode === 'list'">
                <ListingsPanel 
                  :items="filteredListings" 
                  :highlighted-id="highlightedListingId"
                  :search-query="query"
                  :pagination="pagination"
                  @listing-click="handleListingClick"
                  @page-change="goToPage"
                  @next-page="nextPage"
                  @prev-page="prevPage"
                />
              </div>
              
              <!-- Vue grille -->
              <div v-else class="grid grid-cols-1 md:grid-cols-2 gap-6">
                <PropertyCard
                  v-for="item in filteredListings"
                  :key="item.id"
                  :item="item"
                  :highlighted="highlightedListingId === item.id"
                  @details="handleListingClick"
                  @contact="handleContact"
                />
              </div>
              
              <!-- Pagination pour vue grille -->
              <div
                v-if="viewMode === 'grid' && pagination && pagination.totalPages > 1"
                class="flex items-center justify-center gap-2 mt-8 pt-6 border-t border-gray-200"
              >
                <button
                  @click="prevPage"
                  :disabled="pagination.first"
                  class="px-4 py-2 rounded-lg border border-gray-300 bg-white text-gray-700 hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed transition-colors text-sm font-medium"
                >
                  ‹ Précédent
                </button>
                <span class="text-sm text-gray-600">
                  Page {{ pagination.currentPage + 1 }} sur {{ pagination.totalPages }}
                </span>
                <button
                  @click="nextPage"
                  :disabled="pagination.last"
                  class="px-4 py-2 rounded-lg border border-gray-300 bg-white text-gray-700 hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed transition-colors text-sm font-medium"
                >
                  Suivant ›
                </button>
              </div>
            </section>
        
            <aside class="lg:col-span-4">
              <InterestsPanel 
                :user-location="userLocation"
                :max-distance="10"
                @interest-click="handleInterestClick" 
              />
            </aside>
          </div>
          
          <!-- Message si aucune propriété -->
          <div v-if="!loading && filteredListings.length === 0" class="mt-6 text-center py-12 bg-gray-50 rounded-lg">
            <p class="text-gray-600 text-lg">Aucune propriété trouvée</p>
            <p class="text-gray-500 text-sm mt-2">Essayez de modifier vos critères de recherche</p>
          </div>
          
          <!-- Suggestions de recherche -->
          <SearchSuggestions
            v-if="!loading && filteredListings.length > 0"
            :suggestions="searchSuggestions"
            @suggestion-click="handleSuggestionClick"
          />
        </template>
      </div>
    </template>
    