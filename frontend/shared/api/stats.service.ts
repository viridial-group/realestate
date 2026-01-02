/**
 * Service pour les statistiques optimisées
 */

import { httpClient } from './http.client'
import { API_ENDPOINTS } from '../constants/api.constants'
import type { DashboardStats } from '../types/stats.types'

export const statsService = {
  /**
   * Récupère toutes les statistiques du dashboard en une seule requête optimisée
   */
  async getDashboardStats(): Promise<DashboardStats> {
    const response = await httpClient.get<DashboardStats>(API_ENDPOINTS.PROPERTIES.STATS)
    return response.data
  }
}

