/**
 * Service pour les données de marché immobilier (DVF)
 */

import { httpClient } from './http.client'
import { API_ENDPOINTS } from '../constants/api.constants'
import type { MarketData, DVFTransaction } from '../types/market-data.types'

export const marketDataService = {
  /**
   * Obtenir les données de marché pour un code postal
   */
  async getMarketDataByPostalCode(
    codePostal: string,
    propertyType: string = 'APARTMENT',
    startDate?: string,
    endDate?: string
  ): Promise<MarketData> {
    const params = new URLSearchParams({
      propertyType
    })
    if (startDate) params.append('startDate', startDate)
    if (endDate) params.append('endDate', endDate)

    const response = await httpClient.get<MarketData>(
      `${API_ENDPOINTS.MARKET_DATA.BY_POSTAL_CODE(codePostal)}?${params.toString()}`
    )
    return response.data
  },

  /**
   * Obtenir les données de marché pour une propriété avec comparaison
   */
  async getMarketDataForProperty(
    propertyId: number,
    startDate?: string,
    endDate?: string
  ): Promise<MarketData> {
    const params = new URLSearchParams()
    if (startDate) params.append('startDate', startDate)
    if (endDate) params.append('endDate', endDate)

    const url = `${API_ENDPOINTS.MARKET_DATA.BY_PROPERTY(propertyId)}${params.toString() ? `?${params.toString()}` : ''}`
    const response = await httpClient.get<MarketData>(url)
    return response.data
  },

  /**
   * Trouver des transactions similaires
   */
  async findSimilarTransactions(propertyId: number, limit: number = 10): Promise<DVFTransaction[]> {
    const response = await httpClient.get<DVFTransaction[]>(
      `${API_ENDPOINTS.MARKET_DATA.SIMILAR_TRANSACTIONS(propertyId)}?limit=${limit}`
    )
    return response.data
  }
}

