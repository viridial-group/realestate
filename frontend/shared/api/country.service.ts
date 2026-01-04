import { httpClient } from './http.client'
import { API_ENDPOINTS } from '../constants/api.constants'
import type { Country, CountryCreate, CountryUpdate } from '../types/country.types'

/**
 * Service de gestion des pays (SaaS Configuration)
 */
export const countryService = {
  /**
   * Récupère tous les pays actifs
   */
  async getAllActive(): Promise<Country[]> {
    const response = await httpClient.get<Country[]>(API_ENDPOINTS.COUNTRIES.BASE)
    return response.data || []
  },

  /**
   * Récupère un pays par son ID
   */
  async getById(id: number): Promise<Country> {
    const response = await httpClient.get<Country>(API_ENDPOINTS.COUNTRIES.BY_ID(id))
    return response.data
  },

  /**
   * Récupère un pays par son code ISO
   */
  async getByCode(code: string): Promise<Country> {
    const response = await httpClient.get<Country>(API_ENDPOINTS.COUNTRIES.BY_CODE(code))
    return response.data
  },

  /**
   * Crée un nouveau pays
   */
  async create(country: CountryCreate): Promise<Country> {
    const response = await httpClient.post<Country>(API_ENDPOINTS.COUNTRIES.BASE, country)
    return response.data
  },

  /**
   * Met à jour un pays
   */
  async update(id: number, country: CountryUpdate): Promise<Country> {
    const response = await httpClient.put<Country>(API_ENDPOINTS.COUNTRIES.BY_ID(id), country)
    return response.data
  },

  /**
   * Supprime un pays
   */
  async delete(id: number): Promise<void> {
    await httpClient.delete(API_ENDPOINTS.COUNTRIES.BY_ID(id))
  }
}

