import { httpClient } from './http.client'
import type { Property, PropertyCreate, PropertyUpdate, PropertySearchParams } from '../types/property.types'

/**
 * Service de gestion des propriétés
 */
export const propertyService = {
  /**
   * Récupérer toutes les propriétés
   */
  async getAll(params?: PropertySearchParams): Promise<Property[]> {
    const response = await httpClient.get<Property[]>('/property-service/api/properties', { params })
    return response.data || []
  },

  /**
   * Récupérer une propriété par ID
   */
  async getById(id: number): Promise<Property> {
    const response = await httpClient.get<Property>(`/property-service/api/properties/${id}`)
    return response.data
  },

  /**
   * Créer une propriété
   */
  async create(data: PropertyCreate): Promise<Property> {
    const response = await httpClient.post<Property>('/property-service/api/properties', data)
    return response.data
  },

  /**
   * Mettre à jour une propriété
   */
  async update(id: number, data: PropertyUpdate): Promise<Property> {
    const response = await httpClient.put<Property>(`/property-service/api/properties/${id}`, data)
    return response.data
  },

  /**
   * Supprimer une propriété
   */
  async delete(id: number): Promise<void> {
    await httpClient.delete(`/property-service/api/properties/${id}`)
  },

  /**
   * Rechercher des propriétés
   */
  async search(params: PropertySearchParams): Promise<Property[]> {
    const response = await httpClient.get<Property[]>('/property-service/api/properties/search', { params })
    return response.data || []
  }
}

