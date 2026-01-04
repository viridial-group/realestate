import { httpClient } from './http.client'
import { API_ENDPOINTS } from '../constants/api.constants'
import type { PriceAlert, PriceAlertCreate } from '../types/price-alert.types'

/**
 * Service de gestion des alertes de prix
 */
export const priceAlertService = {
  /**
   * Créer une nouvelle alerte de prix
   */
  async create(data: PriceAlertCreate): Promise<PriceAlert> {
    const response = await httpClient.post<PriceAlert>(API_ENDPOINTS.PRICE_ALERTS.BASE, data)
    return response.data
  },

  /**
   * Récupérer toutes les alertes de l'utilisateur connecté
   */
  async getMyAlerts(): Promise<PriceAlert[]> {
    const response = await httpClient.get<PriceAlert[]>(API_ENDPOINTS.PRICE_ALERTS.MY_ALERTS)
    return response.data
  },

  /**
   * Récupérer toutes les alertes pour une propriété
   */
  async getByProperty(propertyId: number): Promise<PriceAlert[]> {
    const response = await httpClient.get<PriceAlert[]>(
      API_ENDPOINTS.PRICE_ALERTS.BY_PROPERTY(propertyId)
    )
    return response.data
  },

  /**
   * Désactiver une alerte
   */
  async deactivate(id: number): Promise<void> {
    await httpClient.put(API_ENDPOINTS.PRICE_ALERTS.DEACTIVATE(id))
  },

  /**
   * Supprimer une alerte
   */
  async delete(id: number): Promise<void> {
    await httpClient.delete(API_ENDPOINTS.PRICE_ALERTS.BY_ID(id))
  }
}

