import { httpClient } from './http.client'
import { API_ENDPOINTS } from '../constants/api.constants'
import type { City, CityCreate, CityUpdate } from '../types/country.types'

/**
 * Service de gestion des villes (SaaS Configuration)
 */
export const cityService = {
  /**
   * Récupère toutes les villes actives
   */
  async getAllActive(): Promise<City[]> {
    const response = await httpClient.get<City[]>(API_ENDPOINTS.CITIES.BASE)
    return response.data || []
  },

  /**
   * Récupère les villes d'un pays par code
   */
  async getByCountryCode(countryCode: string): Promise<City[]> {
    const response = await httpClient.get<City[]>(API_ENDPOINTS.CITIES.BY_COUNTRY_CODE(countryCode))
    return response.data || []
  },

  /**
   * Récupère les villes d'un pays par ID
   */
  async getByCountryId(countryId: number): Promise<City[]> {
    const response = await httpClient.get<City[]>(API_ENDPOINTS.CITIES.BY_COUNTRY_ID(countryId))
    return response.data || []
  },

  /**
   * Récupère une ville par son ID
   */
  async getById(id: number): Promise<City> {
    const response = await httpClient.get<City>(API_ENDPOINTS.CITIES.BY_ID(id))
    return response.data
  },

  /**
   * Crée une nouvelle ville
   */
  async create(city: CityCreate): Promise<City> {
    const response = await httpClient.post<City>(API_ENDPOINTS.CITIES.BASE, city)
    return response.data
  },

  /**
   * Met à jour une ville
   */
  async update(id: number, city: CityUpdate): Promise<City> {
    const response = await httpClient.put<City>(API_ENDPOINTS.CITIES.BY_ID(id), city)
    return response.data
  },

  /**
   * Supprime une ville
   */
  async delete(id: number): Promise<void> {
    await httpClient.delete(API_ENDPOINTS.CITIES.BY_ID(id))
  }
}

