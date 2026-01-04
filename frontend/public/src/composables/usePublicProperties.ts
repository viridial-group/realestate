import { ref, computed } from 'vue'
import { publicPropertyService, type PublicProperty, type PublicPropertySearchParams, type PagedResponse } from '@/api/public-property.service'

/**
 * Composable pour gérer les propriétés publiques avec l'API
 */
export function usePublicProperties() {
  const properties = ref<PublicProperty[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)
  
  // Pagination
  const pagination = ref({
    currentPage: 0,
    totalPages: 0,
    totalElements: 0,
    size: 20,
    first: true,
    last: true,
  })

  /**
   * Charge les propriétés depuis l'API (paginé)
   */
  async function loadProperties(params?: PublicPropertySearchParams) {
    loading.value = true
    error.value = null
    
    try {
      const response = await publicPropertyService.getPublishedProperties({
        ...params,
        page: params?.page ?? 0,
        size: params?.size || 20, // Taille par défaut : 20
        createdAfter: params?.createdAfter, // Passer le filtre de date
      })
      
      properties.value = response.content || []
      pagination.value = {
        currentPage: response.currentPage,
        totalPages: response.totalPages,
        totalElements: response.totalElements,
        size: response.size,
        first: response.first,
        last: response.last,
      }
    } catch (err: any) {
      error.value = err.response?.data?.message || err.message || 'Erreur lors du chargement des propriétés'
      console.error('Error loading properties:', err)
      properties.value = [] // Fallback sur liste vide en cas d'erreur
      // Réinitialiser la pagination en cas d'erreur
      pagination.value = {
        currentPage: 0,
        totalPages: 0,
        totalElements: 0,
        size: 20,
        first: true,
        last: true,
      }
    } finally {
      loading.value = false
    }
  }

  /**
   * Convertit une propriété API en format attendu par les composants
   */
  function mapToListingFormat(property: PublicProperty) {
    // Mapper le status de l'API vers le format attendu par les composants
    let mappedStatus: 'Disponible' | 'Vendu' | 'Loué' = 'Disponible'
    const apiStatus = property.status?.toUpperCase()
    
    if (apiStatus === 'PUBLISHED' || apiStatus === 'AVAILABLE') {
      mappedStatus = 'Disponible'
    } else if (apiStatus === 'RENTED' || apiStatus === 'LOUÉ' || apiStatus === 'RENTED') {
      mappedStatus = 'Loué'
    } else if (apiStatus === 'SOLD' || apiStatus === 'VENDU') {
      mappedStatus = 'Vendu'
    }
    
    // Utiliser transactionType de l'API si disponible, sinon déduire du statut
    // Le transactionType sera utilisé pour le filtrage et l'affichage
    
    // Gérer les coordonnées (latitude/longitude de l'API -> lat/lng pour les composants)
    const lat = property.latitude != null ? Number(property.latitude) : null
    const lng = property.longitude != null ? Number(property.longitude) : null
    
    // Si pas de coordonnées, utiliser un fallback (Paris par défaut)
    // Mais on filtre ces propriétés pour ne pas les afficher sur la map
    const hasCoordinates = lat != null && lng != null && lat !== 0 && lng !== 0
    
    // Mapper transactionType de l'API (RENT/SALE) vers le format frontend (Location/Vente)
    let transactionType: 'Location' | 'Vente' | null = null
    if (property.transactionType) {
      const apiTransactionType = property.transactionType.toUpperCase()
      if (apiTransactionType === 'RENT') {
        transactionType = 'Location'
      } else if (apiTransactionType === 'SALE') {
        transactionType = 'Vente'
      }
    }
    
    // Si transactionType n'est pas défini dans l'API, déduire du status
    if (!transactionType) {
      if (mappedStatus === 'Loué') {
        transactionType = 'Location'
      } else if (mappedStatus === 'Vendu') {
        transactionType = 'Vente'
      } else {
        // Par défaut pour les propriétés disponibles, on considère que c'est en vente
        // (mais cela devrait être défini dans l'API)
        transactionType = 'Vente'
      }
    }
    
    return {
      id: property.id,
      title: property.title,
      city: property.city || 'Non spécifié',
      type: property.type,
      status: mappedStatus,
      transactionType, // Ajouter le type de transaction
      price: property.price ? Number(property.price) : 0,
      surface: property.surface ? Number(property.surface) : 0,
      lat: hasCoordinates ? lat! : 48.8566, // Fallback Paris si pas de coordonnées
      lng: hasCoordinates ? lng! : 2.3522, // Fallback Paris si pas de coordonnées
      description: property.description || '',
      rating: 0, // Non disponible dans l'API pour l'instant
      reviews: 0, // Non disponible dans l'API pour l'instant
      hasCoordinates, // Flag pour savoir si les coordonnées sont valides
    }
  }

  /**
   * Propriétés formatées pour les composants
   * Le computed de Vue gère automatiquement la memoization
   */
  const formattedProperties = computed(() => {
    return properties.value.map(mapToListingFormat)
  })

  return {
    properties,
    formattedProperties,
    loading,
    error,
    pagination,
    loadProperties,
    mapToListingFormat,
  }
}

