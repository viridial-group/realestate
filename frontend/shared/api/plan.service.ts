import { httpClient } from './http.client'
import { API_ENDPOINTS } from '../constants/api.constants'
import type { Plan, PlanCreate, PlanUpdate } from '../types/billing.types'

/**
 * Service de gestion des plans d'abonnement
 */
export const planService = {
  /**
   * Récupérer tous les plans
   */
  async getAll(activeOnly?: boolean): Promise<Plan[]> {
    const response = await httpClient.get<Plan[]>(API_ENDPOINTS.BILLING.PLANS.BASE)
    let plans = response.data || []
    
    // Filtrer par actif si demandé
    if (activeOnly) {
      plans = plans.filter(plan => plan.active)
    }
    
    return plans
  },

  /**
   * Récupérer un plan par ID
   */
  async getById(id: number): Promise<Plan> {
    const response = await httpClient.get<Plan>(API_ENDPOINTS.BILLING.PLANS.BY_ID(id))
    return response.data
  },

  /**
   * Récupérer un plan par nom
   */
  async getByName(name: string): Promise<Plan> {
    const response = await httpClient.get<Plan>(API_ENDPOINTS.BILLING.PLANS.BY_NAME(name))
    return response.data
  },

  /**
   * Récupérer le plan par défaut
   */
  async getDefault(): Promise<Plan> {
    const response = await httpClient.get<Plan>(API_ENDPOINTS.BILLING.PLANS.DEFAULT)
    return response.data
  },

  /**
   * Créer un plan
   */
  async create(data: PlanCreate): Promise<Plan> {
    // Convertir features en JSON string si c'est un tableau
    const planData: any = { ...data }
    if (Array.isArray(planData.features)) {
      planData.features = JSON.stringify(planData.features)
    }
    
    const response = await httpClient.post<Plan>(API_ENDPOINTS.BILLING.PLANS.BASE, planData)
    return response.data
  },

  /**
   * Mettre à jour un plan
   */
  async update(id: number, data: PlanUpdate): Promise<Plan> {
    // Convertir features en JSON string si c'est un tableau
    const planData: any = { ...data }
    if (Array.isArray(planData.features)) {
      planData.features = JSON.stringify(planData.features)
    }
    
    const response = await httpClient.put<Plan>(API_ENDPOINTS.BILLING.PLANS.BY_ID(id), planData)
    return response.data
  },

  /**
   * Supprimer un plan
   */
  async delete(id: number): Promise<void> {
    await httpClient.delete(API_ENDPOINTS.BILLING.PLANS.BY_ID(id))
  }
}

