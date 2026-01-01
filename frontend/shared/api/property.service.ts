import { httpClient } from './http.client'
import { API_ENDPOINTS } from '../constants/api.constants'
import type { Property, PropertyCreate, PropertyUpdate, PropertySearchParams } from '../types/property.types'

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
  }
}

