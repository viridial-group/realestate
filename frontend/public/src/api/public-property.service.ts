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
}

export interface PublicPropertySearchParams {
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
}
