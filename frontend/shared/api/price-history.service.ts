import { httpClient } from './http.client'
import { API_ENDPOINTS } from '../constants/api.constants'
import type { PriceHistory, PriceHistoryCreate, PriceHistoryStats } from '../types/price-history.types'

/**
 * Service de gestion de l'historique des prix
 */
export const priceHistoryService = {
  /**
   * Créer une nouvelle entrée d'historique de prix
   */
  async create(propertyId: number, data: PriceHistoryCreate): Promise<PriceHistory> {
    const response = await httpClient.post<PriceHistory>(
      API_ENDPOINTS.PROPERTIES.PRICE_HISTORY(propertyId),
      data
    )
    return response.data
  },

  /**
   * Récupérer l'historique des prix pour une propriété
   */
  async getByProperty(propertyId: number, params?: { page?: number; size?: number }): Promise<PriceHistory[]> {
    const response = await httpClient.get<PriceHistory[]>(
      API_ENDPOINTS.PROPERTIES.PRICE_HISTORY(propertyId),
      { params }
    )
    return response.data
  },

  /**
   * Récupérer les statistiques sur l'historique des prix
   */
  async getStatsByProperty(propertyId: number): Promise<PriceHistoryStats> {
    const response = await httpClient.get<PriceHistoryStats>(
      API_ENDPOINTS.PROPERTIES.PRICE_HISTORY_STATS(propertyId)
    )
    return response.data
  },

  /**
   * Supprimer une entrée d'historique
   */
  async delete(id: number): Promise<void> {
    await httpClient.delete(API_ENDPOINTS.PROPERTIES.PRICE_HISTORY_DELETE(id))
  }
}

