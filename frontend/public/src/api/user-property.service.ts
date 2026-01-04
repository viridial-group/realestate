import axios from 'axios'
import { tokenUtils } from '@viridial/shared'
import type { Property, PropertyCreate, PropertyUpdate } from '@viridial/shared'
import { useCache, generateCacheKey } from '@/composables/useCache'

/**
 * Service API pour gérer les propriétés de l'utilisateur connecté
 */
const apiClient = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json',
  },
})

// Intercepteur pour ajouter le token JWT
apiClient.interceptors.request.use(
  (config) => {
    const token = tokenUtils.getToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

export interface UserPropertyListParams {
  status?: string
  page?: number
  size?: number
}

export interface PagedPropertyResponse {
  content: Property[]
  currentPage: number
  totalPages: number
  totalElements: number
  size: number
  first: boolean
  last: boolean
}

export interface PropertyStats {
  views: number
  contacts: number
  favorites: number
  shares: number
  lastViewedAt?: string
}

const cache = useCache()
const CACHE_TTL_LIST = 2 * 60 * 1000 // 2 minutes pour les listes
const CACHE_TTL_DETAIL = 5 * 60 * 1000 // 5 minutes pour les détails
const CACHE_TTL_STATS = 1 * 60 * 1000 // 1 minute pour les stats

export const userPropertyService = {
  /**
   * Récupère toutes les propriétés de l'utilisateur connecté
   * Note: Le backend devrait extraire l'utilisateur depuis le token JWT
   * Si l'endpoint /properties/my n'existe pas, on utilise /properties avec un filtre
   */
  async getMyProperties(params?: UserPropertyListParams): Promise<PagedPropertyResponse> {
    const cacheKey = generateCacheKey('my-properties', params || {})
    
    return cache.cached(
      cacheKey,
      async () => {
        try {
          // Essayer d'abord l'endpoint dédié
          try {
            const response = await apiClient.get<PagedPropertyResponse>('/properties/my', { params })
            return response.data || {
              content: [],
              currentPage: 0,
              totalPages: 0,
              totalElements: 0,
              size: 20,
              first: true,
              last: true,
            }
          } catch (error: any) {
            // Si l'endpoint n'existe pas (404), utiliser l'endpoint standard avec pagination
            if (error.response?.status === 404) {
              const searchParams: any = {
                page: params?.page || 0,
                size: params?.size || 20,
              }
              if (params?.status) {
                searchParams.status = params.status
              }
              // Le backend devrait filtrer automatiquement par createdBy depuis le token
              const response = await apiClient.get<PagedPropertyResponse>('/properties', { params: searchParams })
              return response.data || {
                content: [],
                currentPage: 0,
                totalPages: 0,
                totalElements: 0,
                size: 20,
                first: true,
                last: true,
              }
            }
            throw error
          }
        } catch (error) {
          console.error('Error fetching my properties:', error)
          throw error
        }
      },
      CACHE_TTL_LIST
    )
  },

  /**
   * Récupère une propriété par ID (de l'utilisateur connecté)
   */
  async getMyPropertyById(id: number): Promise<Property> {
    const cacheKey = `my-property-${id}`
    
    return cache.cached(
      cacheKey,
      async () => {
        try {
          // Essayer d'abord l'endpoint dédié
          try {
            const response = await apiClient.get<Property>(`/properties/my/${id}`)
            return response.data
          } catch (error: any) {
            // Si l'endpoint n'existe pas (404), utiliser l'endpoint standard
            if (error.response?.status === 404) {
              const response = await apiClient.get<Property>(`/properties/${id}`)
              return response.data
            }
            throw error
          }
        } catch (error) {
          console.error(`Error fetching property ${id}:`, error)
          throw error
        }
      },
      CACHE_TTL_DETAIL
    )
  },

  /**
   * Récupère les statistiques d'une propriété
   */
  async getPropertyStats(id: number): Promise<PropertyStats> {
    const cacheKey = `property-stats-${id}`
    
    return cache.cached(
      cacheKey,
      async () => {
        try {
          const response = await apiClient.get<PropertyStats>(`/properties/${id}/stats`)
          return response.data
        } catch (error: any) {
          // Si l'endpoint n'existe pas, retourner des stats par défaut
          if (error.response?.status === 404) {
            return {
              views: 0,
              contacts: 0,
              favorites: 0,
              shares: 0,
            }
          }
          console.error(`Error fetching stats for property ${id}:`, error)
          throw error
        }
      },
      CACHE_TTL_STATS
    )
  },

  /**
   * Crée une nouvelle propriété
   */
  async createProperty(data: PropertyCreate): Promise<Property> {
    try {
      const response = await apiClient.post<Property>('/properties', data)
      // Invalider le cache des listes
      this.invalidateCache()
      return response.data
    } catch (error) {
      console.error('Error creating property:', error)
      throw error
    }
  },

  /**
   * Met à jour une propriété
   */
  async updateProperty(id: number, data: PropertyUpdate): Promise<Property> {
    try {
      const response = await apiClient.put<Property>(`/properties/${id}`, data)
      // Invalider le cache pour cette propriété
      this.invalidateCache(id)
      return response.data
    } catch (error) {
      console.error(`Error updating property ${id}:`, error)
      throw error
    }
  },

  /**
   * Supprime une propriété
   */
  async deleteProperty(id: number): Promise<void> {
    try {
      await apiClient.delete(`/properties/${id}`)
      // Invalider le cache
      this.invalidateCache(id)
    } catch (error) {
      console.error(`Error deleting property ${id}:`, error)
      throw error
    }
  },

  /**
   * Invalider le cache pour une propriété
   */
  invalidateCache(propertyId?: number) {
    if (propertyId) {
      cache.remove(`my-property-${propertyId}`)
      cache.remove(`property-stats-${propertyId}`)
    }
    // Invalider toutes les listes avec pattern
    cache.invalidatePattern(/^my-properties:/)
  }
}
