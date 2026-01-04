import { httpClient } from './http.client'
import { API_ENDPOINTS } from '../constants/api.constants'
import type { Property, PropertyCreate, PropertyUpdate, PropertySearchParams } from '../types/property.types'
import type { PropertyFeature } from '../types/property-feature.types'

/**
 * Service de gestion des propriétés
 */
export const propertyService = {
  /**
   * Récupérer toutes les propriétés
   */
  async getAll(params?: PropertySearchParams): Promise<Property[]> {
    const response = await httpClient.get<Property[]>(API_ENDPOINTS.PROPERTIES.BASE, { params })
    return response.data || []
  },

  /**
   * Récupérer une propriété par ID
   */
  async getById(id: number): Promise<Property> {
    const response = await httpClient.get<Property>(API_ENDPOINTS.PROPERTIES.BY_ID(id))
    return response.data
  },

  /**
   * Créer une propriété
   */
  async create(data: PropertyCreate): Promise<Property> {
    const response = await httpClient.post<Property>(API_ENDPOINTS.PROPERTIES.BASE, data)
    return response.data
  },

  /**
   * Mettre à jour une propriété
   */
  async update(id: number, data: PropertyUpdate): Promise<Property> {
    const response = await httpClient.put<Property>(API_ENDPOINTS.PROPERTIES.BY_ID(id), data)
    return response.data
  },

  /**
   * Supprimer une propriété
   */
  async delete(id: number): Promise<void> {
    await httpClient.delete(API_ENDPOINTS.PROPERTIES.BY_ID(id))
  },

  /**
   * Rechercher des propriétés
   */
  async search(params: PropertySearchParams): Promise<Property[]> {
    const response = await httpClient.get<Property[]>(API_ENDPOINTS.PROPERTIES.SEARCH, { params })
    return response.data || []
  },

  /**
   * Récupérer les features d'une propriété
   */
  async getPropertyFeatures(id: number): Promise<PropertyFeature[]> {
    const response = await httpClient.get<PropertyFeature[]>(API_ENDPOINTS.PROPERTIES.FEATURES(id))
    return response.data || []
  },

  /**
   * Récupérer le nombre de messages non lus pour plusieurs propriétés (batch)
   * Retourne un map avec propertyId comme clé et le nombre de messages non lus comme valeur
   */
  async getUnreadMessagesCount(propertyIds: number[]): Promise<Record<number, number>> {
    if (!propertyIds || propertyIds.length === 0) {
      return {}
    }
    const response = await httpClient.post<Record<string, number>>(
      API_ENDPOINTS.PROPERTIES.UNREAD_MESSAGES_COUNT,
      propertyIds
    )
    // Convertir les clés string en number pour correspondre au type Property.id
    const result: Record<number, number> = {}
    if (response.data) {
      Object.entries(response.data).forEach(([key, value]) => {
        result[Number(key)] = value
      })
    }
    return result
  }
}

