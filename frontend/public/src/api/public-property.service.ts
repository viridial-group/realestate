import axios from 'axios'

/**
 * Types pour les propriétés publiques
 */
export interface PublicProperty {
  id: number
  reference: string
  title: string
  description?: string
  type: string
  status: string
  transactionType?: string // RENT, SALE - Type de transaction
  price: number
  currency?: string
  surface?: number
  rooms?: number
  bedrooms?: number
  bathrooms?: number
  address?: string
  city?: string
  postalCode?: string
  country?: string
  latitude?: number
  longitude?: number
  yearBuilt?: number
  condition?: string
  dateOnMarket?: string
  createdAt?: string
  updatedAt?: string
  slug?: string // Slug SEO-friendly
  // Zillow-inspired fields
  petFriendly?: boolean
  specialOffer?: string
  officeHours?: string // JSON string
  neighborhood?: string
  walkScore?: number
  transitScore?: number
  bikeScore?: number
  buildingName?: string
  flooring?: string // JSON array string
  unitFeatures?: string // JSON array string
  buildingAmenities?: string // JSON array string
  availableUnits?: string // JSON array string
  petPolicy?: string // JSON string
  parkingPolicy?: string
}

export interface PublicPropertySearchParams {
  organizationId?: number
  type?: string
  city?: string
  country?: string
  minPrice?: number
  maxPrice?: number
  minSurface?: number
  maxSurface?: number
  bedrooms?: number
  bathrooms?: number
  search?: string
  sortBy?: string
  transactionType?: string // Location, Vente, RENT, SALE
  createdAfter?: string // Filtre par date
  page?: number
  size?: number
}

export interface PagedResponse<T> {
  content: T[]
  currentPage: number
  totalPages: number
  totalElements: number
  size: number
  first: boolean
  last: boolean
}

export interface SearchSuggestions {
  cities: string[]
  types: string[]
  addresses: string[]
  titles: string[]
  popularSearches: string[]
}

/**
 * Service API pour les propriétés publiques
 */
const apiClient = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json',
  },
})

export const publicPropertyService = {
  /**
   * Récupère les propriétés publiées avec filtres (paginé)
   */
  async getPublishedProperties(params?: PublicPropertySearchParams): Promise<PagedResponse<PublicProperty>> {
    try {
      const response = await apiClient.get<PagedResponse<PublicProperty>>('/public/properties', { params })
      return response.data || { content: [], currentPage: 0, totalPages: 0, totalElements: 0, size: 20, first: true, last: true }
    } catch (error) {
      console.error('Error fetching published properties:', error)
      throw error
    }
  },

  /**
   * Récupère la liste des villes disponibles (pour autocomplete)
   */
  async getAvailableCities(search?: string): Promise<string[]> {
    try {
      const response = await apiClient.get<string[]>('/public/properties/cities', {
        params: search ? { search } : {}
      })
      return response.data || []
    } catch (error) {
      console.error('Error fetching available cities:', error)
      return [] // Retourner liste vide en cas d'erreur
    }
  },

  /**
   * Récupère une propriété publiée par ID
   */
  async getPublishedPropertyById(id: number): Promise<PublicProperty> {
    try {
      const response = await apiClient.get<PublicProperty>(`/public/properties/${id}`)
      return response.data
    } catch (error) {
      console.error(`Error fetching property ${id}:`, error)
      throw error
    }
  },

  /**
   * Récupère une propriété publiée par référence
   */
  async getPublishedPropertyByReference(reference: string): Promise<PublicProperty> {
    try {
      const response = await apiClient.get<PublicProperty>(`/public/properties/reference/${reference}`)
      return response.data
    } catch (error) {
      console.error(`Error fetching property ${reference}:`, error)
      throw error
    }
  },

  /**
   * Récupère une propriété publiée par slug (SEO-friendly)
   */
  async getPublishedPropertyBySlug(slug: string): Promise<PublicProperty> {
    try {
      const response = await apiClient.get<PublicProperty>(`/public/properties/slug/${slug}`)
      return response.data
    } catch (error) {
      console.error(`Error fetching property by slug ${slug}:`, error)
      throw error
    }
  },

  /**
   * Récupère des suggestions complètes de recherche avec améliorations
   */
  async getSearchSuggestions(
    search?: string,
    options?: { limit?: number; includePopular?: boolean; includeTrending?: boolean }
  ): Promise<SearchSuggestions> {
    try {
      const params: any = {}
      if (search) params.search = search
      if (options?.limit) params.limit = options.limit
      if (options?.includePopular !== undefined) params.includePopular = options.includePopular
      if (options?.includeTrending !== undefined) params.includeTrending = options.includeTrending

      const response = await apiClient.get<SearchSuggestions>('/public/properties/suggestions', {
        params
      })
      return response.data || { cities: [], types: [], addresses: [], titles: [], popularSearches: [] }
    } catch (error) {
      console.error('Error fetching search suggestions:', error)
      // Retourner des suggestions de fallback améliorées
      return this.getFallbackSuggestions(search)
    }
  },

  /**
   * Suggestions de fallback améliorées
   */
  getFallbackSuggestions(search?: string): SearchSuggestions {
    const fallbackCities = [
      'Paris', 'Lyon', 'Marseille', 'Bordeaux', 'Nice', 'Toulouse',
      'Nantes', 'Strasbourg', 'Montpellier', 'Lille', 'Rennes', 'Reims'
    ]
    
    const fallbackTypes = [
      'Appartement', 'Villa', 'Studio', 'Maison', 'Terrain', 'Bureau',
      'Loft', 'Duplex', 'Penthouse', 'Chalet'
    ]

    const fallbackPopular = [
      'Appartement Paris', 'Villa Côte d\'Azur', 'Studio étudiant',
      'Maison avec jardin', 'Appartement T3', 'Villa piscine',
      'Studio meublé', 'Maison familiale', 'Appartement centre-ville'
    ]

    // Filtrer par recherche si fournie
    if (search && search.trim().length > 0) {
      const searchLower = search.toLowerCase()
      return {
        cities: fallbackCities.filter(c => c.toLowerCase().includes(searchLower)).slice(0, 5),
        types: fallbackTypes.filter(t => t.toLowerCase().includes(searchLower)).slice(0, 5),
        addresses: [],
        titles: [],
        popularSearches: fallbackPopular.filter(p => p.toLowerCase().includes(searchLower)).slice(0, 5)
      }
    }

    return {
      cities: fallbackCities.slice(0, 10),
      types: fallbackTypes.slice(0, 10),
      addresses: [],
      titles: [],
      popularSearches: fallbackPopular.slice(0, 10)
    }
  },
}
