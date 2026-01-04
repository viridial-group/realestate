<script setup lang="ts">
    import { ref, computed, onMounted, watch } from 'vue'
    import { useRoute } from 'vue-router'
    import { usePublicProperties } from '@/composables/usePublicProperties'
    import { ref as mapViewRef } from 'vue'
    import ListingsPanel from '@/components/ListingsPanel.vue'
    import InterestsPanel from '@/components/InterestsPanel.vue'
    import MapView from '@/components/MapView.vue'
    import ViewToggle from '@/components/ViewToggle.vue'
    import Breadcrumbs from '@/components/Breadcrumbs.vue'
    import PropertyCard from '@/components/PropertyCard.vue'
    import SearchSuggestions from '@/components/SearchSuggestions.vue'
    import ShareSearchButton from '@/components/ShareSearchButton.vue'
    import SearchStats from '@/components/SearchStats.vue'
    import SkeletonLoader from '@/components/SkeletonLoader.vue'
    import ErrorMessage from '@/components/ErrorMessage.vue'
    import EmptyState from '@/components/EmptyState.vue'
    import AdvertisementSlot from '@/components/AdvertisementSlot.vue'
import SearchAutocomplete from '@/components/SearchAutocomplete.vue'
import SidebarFilters from '@/components/SidebarFilters.vue'
import QuickFilters from '@/components/QuickFilters.vue'
import HomeSections from '@/components/HomeSections.vue'
import { interests } from '@/data/interests'
import { useSEO, generateSiteStructuredData } from '@/composables/useSEO'
    
    // Composable pour les propriétés
    const { formattedProperties, loading, error, pagination, loadProperties } = usePublicProperties()
    
    // État de chargement séparé pour les filtres (ne remplace pas le contenu)
    const filtersLoading = ref(false)
    const initialLoad = ref(true)
    
    // Filtres
    const query = ref('')
    const transactionType = ref('Tous')
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
    const country = ref<string>('')
    const city = ref<string>('')
    const currentPage = ref(0)
    const highlightedListingId = ref<number | null>(null)
    const mapView = mapViewRef<InstanceType<typeof MapView> | null>(null)
    const userLocation = ref<{ lat: number; lng: number } | null>(null)
    const viewMode = ref<'list' | 'grid'>('list')
    const resultsSectionRef = ref<HTMLElement | null>(null)
    const showMobileFilters = ref(false)
    
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
    
    // SEO dynamique basé sur les filtres
    const seoTitle = computed(() => {
      const parts: string[] = []
      
      if (query.value && query.value.trim()) {
        parts.push(query.value.trim())
      }
      
      if (type.value && type.value !== 'Tous') {
        const typeLabels: Record<string, string> = {
          'APARTMENT': 'Appartements',
          'HOUSE': 'Maisons',
          'VILLA': 'Villas',
          'LAND': 'Terrains',
          'COMMERCIAL': 'Locaux commerciaux'
        }
        parts.push(typeLabels[type.value] || type.value)
      }
      
      if (transactionType.value && transactionType.value !== 'Tous') {
        const transactionLabels: Record<string, string> = {
          'RENT': 'à louer',
          'SALE': 'à vendre'
        }
        parts.push(transactionLabels[transactionType.value] || transactionType.value)
      }
      
      if (parts.length > 0) {
        return parts.join(' ') + ' | Recherche Immobilier'
      }
      
      return 'Recherche de biens immobiliers | Viridial'
    })
    
    const seoDescription = computed(() => {
      const parts: string[] = []
      
      if (query.value && query.value.trim()) {
        parts.push(`Recherche de biens immobiliers pour "${query.value.trim()}"`)
      } else {
        parts.push('Trouvez votre bien immobilier idéal')
      }
      
      if (type.value && type.value !== 'Tous') {
        const typeLabels: Record<string, string> = {
          'APARTMENT': 'appartements',
          'HOUSE': 'maisons',
          'VILLA': 'villas',
          'LAND': 'terrains',
          'COMMERCIAL': 'locaux commerciaux'
        }
        parts.push(typeLabels[type.value] || type.value.toLowerCase())
      } else {
        parts.push('appartements, maisons et villas')
      }
      
      if (transactionType.value && transactionType.value !== 'Tous') {
        parts.push(transactionType.value === 'RENT' ? 'à louer' : 'à vendre')
      } else {
        parts.push('à vendre ou à louer')
      }
      
      if (pagination.value && pagination.value.totalElements > 0) {
        parts.push(`- ${pagination.value.totalElements} annonce${pagination.value.totalElements > 1 ? 's' : ''} disponible${pagination.value.totalElements > 1 ? 's' : ''}`)
      }
      
      parts.push('en France')
      
      return parts.join(' ') + '. Recherchez parmi des milliers d\'annonces immobilières vérifiées.'
    })
    
    const seoKeywords = computed(() => {
      const keywords: string[] = ['immobilier', 'recherche', 'France']
      
      if (query.value && query.value.trim()) {
        keywords.push(query.value.trim().toLowerCase())
      }
      
      if (type.value && type.value !== 'Tous') {
        keywords.push(type.value.toLowerCase())
      }
      
      if (transactionType.value && transactionType.value !== 'Tous') {
        keywords.push(transactionType.value === 'RENT' ? 'location' : 'vente')
      }
      
      keywords.push('achat', 'appartement', 'maison', 'villa', 'annonces immobilières')
      
      return keywords
    })
    
    const canonicalUrl = computed(() => {
      const siteUrl = typeof window !== 'undefined' ? window.location.origin : 'http://viridial.com'
      const params = new URLSearchParams()
      
      if (query.value && query.value.trim()) {
        params.set('q', query.value.trim())
      }
      if (type.value && type.value !== 'Tous') {
        params.set('type', type.value)
      }
      if (transactionType.value && transactionType.value !== 'Tous') {
        params.set('transaction', transactionType.value)
      }
      if (maxPrice.value) {
        params.set('maxPrice', maxPrice.value.toString())
      }
      if (minPrice.value) {
        params.set('minPrice', minPrice.value.toString())
      }
      if (minSurface.value) {
        params.set('minSurface', minSurface.value.toString())
      }
      if (bedrooms.value) {
        params.set('bedrooms', bedrooms.value.toString())
      }
      
      const queryString = params.toString()
      return queryString ? `${siteUrl}/search?${queryString}` : `${siteUrl}/search`
    })
    
    // Fonction pour mettre à jour le SEO
    function updateSEO() {
      const siteUrl = typeof window !== 'undefined' ? window.location.origin : 'http://viridial.com'
      
      useSEO({
        title: seoTitle.value,
        description: seoDescription.value,
        keywords: seoKeywords.value,
        type: 'website',
        canonical: canonicalUrl.value,
        url: canonicalUrl.value
      })
      
      // Ajouter les structured data Organization et WebSite (une seule fois)
      if (typeof document !== 'undefined') {
        const existingSiteScript = document.querySelector('script[data-site-structured-data]')
        if (!existingSiteScript) {
          const siteStructuredData = generateSiteStructuredData(siteUrl)
          const script = document.createElement('script')
          script.type = 'application/ld+json'
          script.setAttribute('data-site-structured-data', 'true')
          script.textContent = JSON.stringify(siteStructuredData, null, 2)
          document.head.appendChild(script)
        }
        
        // Ajouter les structured data pour la recherche (SearchAction et ItemList)
        addSearchStructuredData(siteUrl)
      }
    }
    
    // Fonction pour ajouter les structured data de recherche
    function addSearchStructuredData(siteUrl: string) {
      if (typeof document === 'undefined') return
      
      // Supprimer l'ancien script de recherche
      const existingScript = document.querySelector('script[data-search-structured-data]')
      if (existingScript) {
        existingScript.remove()
      }
      
      const searchUrl = canonicalUrl.value
      
      // Structured Data: WebSite avec SearchAction
      const websiteStructuredData = {
        '@context': 'https://schema.org',
        '@type': 'WebSite',
        '@id': `${siteUrl}#website`,
        url: siteUrl,
        name: 'Viridial - Annonces Immobilières',
        description: 'Plateforme immobilière pour trouver votre bien idéal',
        potentialAction: {
          '@type': 'SearchAction',
          target: {
            '@type': 'EntryPoint',
            urlTemplate: `${siteUrl}/search?q={search_term_string}`
          },
          'query-input': 'required name=search_term_string'
        }
      }
      
      // Structured Data: ItemList pour les résultats de recherche
      let itemListStructuredData: any = null
      if (formattedProperties.value && formattedProperties.value.length > 0) {
        const items = formattedProperties.value.slice(0, 10).map((property, index) => ({
          '@type': 'ListItem',
          position: index + 1,
          item: {
            '@type': 'RealEstateListing',
            '@id': `${siteUrl}/property/${property.id}`,
            name: property.title,
            description: property.description || property.title,
            url: `${siteUrl}/property/${property.id}`,
            ...((property as any).imageUrl && {
              image: (property as any).imageUrl
            }),
            ...(property.price && {
              offers: {
                '@type': 'Offer',
                price: property.price,
                priceCurrency: 'EUR',
                availability: 'https://schema.org/InStock'
              }
            })
          }
        }))
        
        itemListStructuredData = {
          '@context': 'https://schema.org',
          '@type': 'ItemList',
          '@id': `${searchUrl}#search-results`,
          name: seoTitle.value,
          description: seoDescription.value,
          numberOfItems: pagination.value?.totalElements || items.length,
          itemListElement: items
        }
      }
      
      // Injecter les structured data
      const script = document.createElement('script')
      script.type = 'application/ld+json'
      script.setAttribute('data-search-structured-data', 'true')
      
      const allStructuredData = {
        '@context': 'https://schema.org',
        '@graph': [
          websiteStructuredData,
          ...(itemListStructuredData ? [itemListStructuredData] : [])
        ]
      }
      
      script.textContent = JSON.stringify(allStructuredData, null, 2)
      document.head.appendChild(script)
    }
    
    // Charger les propriétés au montage
    onMounted(() => {
      // Vérifier si des paramètres de recherche sont passés depuis la landing page
      const route = useRoute()
      if (route.query.search || route.query.q) {
        query.value = (route.query.search || route.query.q) as string
      }
      if (route.query.type) {
        type.value = route.query.type as string
      }
      if (route.query.transaction) {
        transactionType.value = route.query.transaction as string
      }
      if (route.query.maxPrice) {
        maxPrice.value = Number(route.query.maxPrice)
      }
      if (route.query.minPrice) {
        minPrice.value = Number(route.query.minPrice)
      }
      if (route.query.minSurface) {
        minSurface.value = Number(route.query.minSurface)
      }
      if (route.query.bedrooms) {
        bedrooms.value = Number(route.query.bedrooms)
      }
      
      fetchProperties()
      updateSEO()
      initialLoad.value = false
    })
    
    // Mettre à jour le SEO quand les filtres ou résultats changent
    watch([query, transactionType, type, status, formattedProperties, pagination], () => {
      updateSEO()
    }, { deep: true })
    
    // Watcher pour recharger quand les filtres changent (avec debounce amélioré)
    let debounceTimer: ReturnType<typeof setTimeout> | null = null
    watch([query, transactionType, type, status, maxPrice, minSurface, bedrooms, bathrooms, country, city], () => {
      // Réinitialiser à la page 0 quand les filtres changent
      currentPage.value = 0
      if (debounceTimer) {
        clearTimeout(debounceTimer)
      }
      // Utiliser filtersLoading au lieu de loading pour éviter le resize
      filtersLoading.value = true
      debounceTimer = setTimeout(() => {
        fetchProperties(true)
      }, 500) // Debounce de 500ms (augmenté pour réduire les appels API)
    })
    
    // Watcher pour changer de page
    watch(currentPage, () => {
      fetchProperties(false)
      // Scroller vers le haut lors du changement de page
      scrollToResultsTop()
    })
    
    // Fonction pour charger les propriétés depuis l'API
    async function fetchProperties(isFilterChange = false) {
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
      
      if (country.value && country.value.trim()) {
        params.country = country.value.trim()
      }
      
      if (city.value && city.value.trim()) {
        params.city = city.value.trim()
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
      
      // Filtre par type de transaction
      if (transactionType.value && transactionType.value !== 'Tous') {
        // Convertir Location/Vente en RENT/SALE pour l'API
        if (transactionType.value === 'Location') {
          params.transactionType = 'RENT'
        } else if (transactionType.value === 'Vente') {
          params.transactionType = 'SALE'
        } else {
          params.transactionType = transactionType.value
        }
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
      
      // Si c'est un changement de filtre, ne pas utiliser loading global
      if (isFilterChange) {
        try {
          await loadProperties(params)
          // Invalider la taille de la carte après le chargement pour mettre à jour les marqueurs
          if (mapView.value) {
            setTimeout(() => {
              mapView.value?.invalidateSize()
            }, 100)
          }
          // Scroller vers le haut de la liste des résultats
          scrollToResultsTop()
        } finally {
          filtersLoading.value = false
        }
      } else {
        await loadProperties(params)
        if (initialLoad.value) {
          initialLoad.value = false
        } else {
          // Scroller vers le haut lors des recherches non initiales
          scrollToResultsTop()
        }
      }
    }
    
    // Fonction pour scroller vers le haut de la liste des résultats
    function scrollToResultsTop() {
      // Attendre que le DOM soit mis à jour
      setTimeout(() => {
        if (resultsSectionRef.value) {
          resultsSectionRef.value.scrollIntoView({ 
            behavior: 'smooth', 
            block: 'start' 
          })
        } else {
          // Fallback : scroller vers le haut de la page de résultats
          const statsElement = document.querySelector('[data-results-start]')
          if (statsElement) {
            statsElement.scrollIntoView({ 
              behavior: 'smooth', 
              block: 'start' 
            })
          }
        }
      }, 100)
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
      
      // Filtre combiné par type de transaction et status
      // Si transactionType ou status est défini, on filtre
      if ((transactionType.value && transactionType.value !== 'Tous') || (status.value && status.value !== 'Tous')) {
        filtered = filtered.filter(l => {
          const listingTransactionType = getTransactionTypeFromStatus(l.status)
          
          // Vérifier le type de transaction
          if (transactionType.value && transactionType.value !== 'Tous') {
            if (listingTransactionType !== transactionType.value) {
              return false
            }
          }
          
          // Vérifier le status
          if (status.value && status.value !== 'Tous') {
            if (l.status !== status.value) {
              return false
            }
          }
          
          return true
        })
      }
      
      // Le tri est maintenant fait côté serveur, pas besoin de trier ici
      
      return filtered
    })
    
    // Fonction helper pour déterminer le type de transaction depuis le statut
    function getTransactionTypeFromStatus(status: string): string {
      if (status === 'Loué') {
        return 'Location'
      } else if (status === 'Vendu') {
        return 'Vente'
      }
      // Pour les propriétés disponibles, on détermine le type de transaction
      // Par défaut, on retourne "Location" car c'est le plus courant
      return 'Location'
    }
    
    function clearFilters() {
      maxPrice.value = null
      minPrice.value = null
      minSurface.value = null
      maxSurface.value = null
      bedrooms.value = null
      bathrooms.value = null
      query.value = ''
      transactionType.value = 'Tous'
      type.value = 'Tous'
      status.value = 'Tous'
      sortBy.value = 'default'
      dateFilter.value = ''
      country.value = ''
      city.value = ''
      currentPage.value = 0
      fetchProperties(true)
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
      fetchProperties(true)
    }
    
    function handleUserLocationUpdated(location: { lat: number; lng: number }) {
      userLocation.value = location
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
      if (transactionType.value && transactionType.value !== 'Tous') count++
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
      if (country.value && country.value.trim()) count++
      if (city.value && city.value.trim()) count++
      return count
    }

    const hasActiveFilters = computed(() => getActiveFiltersCount() > 0)
    </script>
    
    <template>
      <div class="w-full">
        <!-- Header fixe avec recherche uniquement -->
        <div class="sticky top-0 z-50 bg-white dark:bg-gray-900 shadow-md mb-6 py-4" role="search" aria-label="Recherche de propriétés">
          <div class="max-w-screen-2xl mx-auto px-4 sm:px-6 lg:px-8">
            <div class="flex items-center gap-2 sm:gap-4">
              <!-- Bouton filtres mobile -->
              <button
                class="lg:hidden p-2 text-gray-700 hover:text-gray-900 hover:bg-gray-100 rounded-md transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-1 active:scale-95"
                aria-label="Ouvrir les filtres"
                aria-expanded="false"
                @click="showMobileFilters = !showMobileFilters"
              >
                <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.293A1 1 0 013 6.586V4z" />
                </svg>
              </button>
              <div class="flex-1 min-w-0">
                <SearchAutocomplete
                  :model-value="query || ''"
                  @update:model-value="query = $event"
                  placeholder="Rechercher une ville, un quartier, un type…"
                  aria-label="Champ de recherche"
                />
              </div>
              <ShareSearchButton
                class="hidden sm:flex"
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
          </div>
        </div>
        
        <!-- Filtres mobiles (drawer) -->
        <div
          v-if="showMobileFilters"
          class="lg:hidden fixed inset-0 z-50 bg-black bg-opacity-50"
          @click="showMobileFilters = false"
          aria-label="Fermer les filtres"
        >
          <div
            class="absolute right-0 top-0 h-full w-4/5 max-w-sm bg-white dark:bg-gray-900 shadow-xl overflow-y-auto transform transition-transform duration-300 ease-out"
            @click.stop
            role="dialog"
            aria-modal="true"
            aria-label="Filtres de recherche"
          >
            <div class="sticky top-0 bg-white dark:bg-gray-900 border-b border-gray-200 p-4 flex items-center justify-between z-10">
              <h2 class="text-lg font-semibold text-gray-900 dark:text-gray-100">Filtres</h2>
              <button
                @click="showMobileFilters = false"
                class="p-2 text-gray-500 hover:text-gray-700 rounded-md transition-colors focus:outline-none focus:ring-2 focus:ring-blue-500"
                aria-label="Fermer les filtres"
              >
                <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
            </div>
            <div class="p-4">
              <SidebarFilters
                :transaction-type="transactionType"
                :type="type"
                :status="status"
                :sort-by="sortBy"
                :max-price="maxPrice"
                :min-surface="minSurface"
                :bedrooms="bedrooms"
                :bathrooms="bathrooms"
                :date-filter="dateFilter"
                :country="country"
                :city="city"
                :show-date-filter="true"
                @update:transactionType="transactionType = $event"
                @update:type="type = $event"
                @update:status="status = $event"
                @update:sortBy="sortBy = $event"
                @update:maxPrice="maxPrice = $event"
                @update:minSurface="minSurface = $event"
                @update:bedrooms="bedrooms = $event"
                @update:bathrooms="bathrooms = $event"
                @update:dateFilter="dateFilter = $event"
                @update:country="country = $event"
                @update:city="city = $event"
                @clear-filters="clearFilters"
              />
            </div>
          </div>
        </div>

        <!-- Breadcrumbs -->
        <div class="mb-6">
          <Breadcrumbs :items="breadcrumbs" />
        </div>
        
        <!-- État de chargement initial avec skeleton -->
        <div v-if="loading && initialLoad" class="mt-6 space-y-6">
          <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            <SkeletonLoader v-for="i in 6" :key="i" type="property-card" />
          </div>
        </div>
        
        <!-- Message d'erreur amélioré -->
        <ErrorMessage
          v-else-if="error"
          :title="'Erreur de chargement'"
          :message="error"
          :show-retry="true"
          :show-home="true"
          @retry="fetchProperties"
          @go-home="() => { query = ''; clearFilters(); fetchProperties() }"
        />
        
        <!-- Contenu principal -->
        <template v-else>
          <!-- Overlay de chargement pour les filtres (ne remplace pas le contenu) -->
          <div v-show="filtersLoading" class="fixed inset-0 bg-black bg-opacity-10 z-40 pointer-events-none">
            <div class="absolute top-4 right-4 bg-white dark:bg-gray-800 rounded-lg shadow-lg px-4 py-2 flex items-center gap-2">
              <div class="animate-spin rounded-full h-4 w-4 border-b-2 border-blue-600"></div>
              <span class="text-sm text-gray-700 dark:text-gray-300">Mise à jour...</span>
            </div>
          </div>
          <!-- Filtres rapides -->
          <QuickFilters
            :min-price="minPrice"
            :max-price="maxPrice"
            :min-surface="minSurface"
            :max-surface="maxSurface"
            :bedrooms="bedrooms"
            :type="type"
            @update:minPrice="minPrice = $event"
            @update:maxPrice="maxPrice = $event"
            @update:minSurface="minSurface = $event"
            @update:maxSurface="maxSurface = $event"
            @update:bedrooms="bedrooms = $event"
            @update:type="type = $event"
            @clear="clearFilters"
          />

          <!-- Statistiques de recherche -->
          <div data-results-start>
            <SearchStats
              :stats="{
                totalResults: pagination?.totalElements || 0,
                filtersActive: getActiveFiltersCount()
              }"
              :show-stats="true"
              :show-clear="hasActiveFilters"
              @clear="clearFilters"
            />
          </div>
          
          <!-- 🗺️ MAP APRÈS FILTRES -->
          <div class="mt-6" style="min-height: 500px;">
            <div class="mb-3 flex items-center justify-between">
              <h3 class="text-lg font-semibold text-gray-800 dark:text-gray-200">
                {{ filteredListings.length }} annonce{{ filteredListings.length > 1 ? 's' : '' }} sur la carte
              </h3>
            </div>
            <div style="position: relative; width: 100%; height: 500px;">
              <MapView 
                ref="mapView"
                :listings="filteredListings" 
                :interests="interests"
                :highlighted-listing-id="highlightedListingId"
                @marker-click="handleMarkerClick"
                @user-location-updated="handleUserLocationUpdated"
              />
            </div>
          </div>
        
          <!-- Résultats -->
          <div class="grid grid-cols-1 lg:grid-cols-12 gap-6 lg:gap-8 mt-8 w-full max-w-screen-2xl mx-auto">
            <!-- Filtres à gauche (fixe) - Masqué sur mobile, affiché via bouton -->
            <aside class="hidden lg:block lg:col-span-3 space-y-6" aria-label="Filtres de recherche">
              <SidebarFilters
                :transaction-type="transactionType"
                :type="type"
                :status="status"
                :sort-by="sortBy"
                :max-price="maxPrice"
                :min-surface="minSurface"
                :bedrooms="bedrooms"
                :bathrooms="bathrooms"
                :date-filter="dateFilter"
                :country="country"
                :city="city"
                :show-date-filter="true"
                @update:transactionType="transactionType = $event"
                @update:type="type = $event"
                @update:status="status = $event"
                @update:sortBy="sortBy = $event"
                @update:maxPrice="maxPrice = $event"
                @update:minSurface="minSurface = $event"
                @update:bedrooms="bedrooms = $event"
                @update:bathrooms="bathrooms = $event"
                @update:dateFilter="dateFilter = $event"
                @update:country="country = $event"
                @update:city="city = $event"
                @clear-filters="clearFilters"
              />
            </aside>

            <!-- Contenu principal au centre -->
            <section ref="resultsSectionRef" class="lg:col-span-6 lg:col-start-4" style="min-height: 400px;" role="main" aria-label="Résultats de recherche">
              <!-- En-tête avec toggle vue et compteur -->
              <div class="flex items-center justify-between mb-6">
                <div class="flex items-center gap-4">
                  <h2 class="text-xl font-semibold text-gray-800 dark:text-gray-200">
                    {{ pagination?.totalElements || filteredListings.length }} annonce{{ (pagination?.totalElements || filteredListings.length) > 1 ? 's' : '' }}
                  </h2>
                  <ViewToggle v-model="viewMode" />
                </div>
              </div>
              
              <!-- Vue liste -->
              <div v-show="viewMode === 'list'">
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
              <div v-show="viewMode === 'grid'" class="grid grid-cols-1 sm:grid-cols-2 gap-4 sm:gap-6">
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

            <!-- Panel d'intérêts et annonces à droite (fixe) - Masqué sur mobile -->
            <aside class="hidden lg:block lg:col-span-3 lg:col-start-10">
              <div class="sticky top-24 space-y-6" aria-label="Points d'intérêt et annonces">
                <InterestsPanel 
                  :user-location="userLocation"
                  :max-distance="10"
                  @interest-click="handleInterestClick" 
                />
                
                <!-- Annonces publicitaires -->
                <div>
                  <AdvertisementSlot
                    ad-type="SIDEBAR"
                    position="SIDEBAR_RIGHT"
                    :city="query"
                    page-type="SEARCH"
                  />
                </div>
                
                <!-- Annonce BANNER supplémentaire -->
                <div>
                  <AdvertisementSlot
                    ad-type="BANNER"
                    position="SIDEBAR_RIGHT"
                    :city="query"
                    page-type="SEARCH"
                  />
                </div>
              </div>
            </aside>
          </div>
          
          <!-- Message si aucune propriété -->
          <EmptyState
            v-if="!loading && filteredListings.length === 0"
            title="Aucune propriété trouvée"
            description="Aucun bien ne correspond à vos critères de recherche. Essayez de modifier vos filtres ou d'élargir votre recherche."
            icon="search"
            action-label="Réinitialiser les filtres"
            :show-action="hasActiveFilters"
            @action="clearFilters"
          />
          
          <!-- Suggestions de recherche -->
          <SearchSuggestions
            v-if="!loading && filteredListings.length > 0"
            :suggestions="searchSuggestions"
            @suggestion-click="handleSuggestionClick"
          />

          <!-- Sections dynamiques (affichées seulement si pas de recherche active) -->
          <HomeSections
            v-if="!loading && !hasActiveFilters && (!query || query.trim().length === 0) && filteredListings.length === 0"
          />
        </template>
      </div>
    </template>
    