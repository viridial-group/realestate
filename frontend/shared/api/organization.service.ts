import { httpClient } from './http.client'
import { API_ENDPOINTS } from '../constants/api.constants'

export interface Organization {
  id: number
  name: string
  type: 'AGENCY' | 'FREELANCE' | 'COMPANY'
  email: string
  phone?: string
  address?: string
  siret?: string
  status: 'ACTIVE' | 'INACTIVE' | 'SUSPENDED' | 'PENDING'
  userCount?: number
  createdAt: string
  updatedAt?: string
}

export interface OrganizationCreate {
  name: string
  type: 'AGENCY' | 'FREELANCE' | 'COMPANY'
  email: string
  phone?: string
  address?: string
  siret?: string
  status?: 'ACTIVE' | 'INACTIVE' | 'SUSPENDED' | 'PENDING'
}

export interface OrganizationUpdate {
  name?: string
  type?: 'AGENCY' | 'FREELANCE' | 'COMPANY'
  email?: string
  phone?: string
  address?: string
  siret?: string
  status?: 'ACTIVE' | 'INACTIVE' | 'SUSPENDED' | 'PENDING'
}

export interface OrganizationSearchParams {
  status?: string
  type?: string
  search?: string
  page?: number
  size?: number
}

export interface OrganizationStats {
  total: number
  active: number
  inactive: number
  suspended: number
}

/**
 * Service de gestion des organisations
 */
export const organizationService = {
  /**
   * Récupérer toutes les organisations
   */
  async getAll(params?: OrganizationSearchParams): Promise<{ organizations: Organization[]; total: number }> {
    const response = await httpClient.get<{ content: Organization[]; totalElements: number }>(
      API_ENDPOINTS.ORGANIZATIONS.BASE,
      { params }
    )
    return {
      organizations: response.data?.content || response.data || [],
      total: response.data?.totalElements || 0
    }
  },

  /**
   * Récupérer une organisation par ID
   */
  async getById(id: number): Promise<Organization> {
    const response = await httpClient.get<Organization>(API_ENDPOINTS.ORGANIZATIONS.BY_ID(id))
    return response.data
  },

  /**
   * Créer une organisation
   */
  async create(data: OrganizationCreate): Promise<Organization> {
    const response = await httpClient.post<Organization>(API_ENDPOINTS.ORGANIZATIONS.BASE, data)
    return response.data
  },

  /**
   * Mettre à jour une organisation
   */
  async update(id: number, data: OrganizationUpdate): Promise<Organization> {
    const response = await httpClient.put<Organization>(API_ENDPOINTS.ORGANIZATIONS.BY_ID(id), data)
    return response.data
  },

  /**
   * Supprimer une organisation
   */
  async delete(id: number): Promise<void> {
    await httpClient.delete(API_ENDPOINTS.ORGANIZATIONS.BY_ID(id))
  },

  /**
   * Activer une organisation
   */
  async activate(id: number): Promise<Organization> {
    const response = await httpClient.patch<Organization>(
      `${API_ENDPOINTS.ORGANIZATIONS.BY_ID(id)}/activate`
    )
    return response.data
  },

  /**
   * Suspendre une organisation
   */
  async suspend(id: number, reason?: string): Promise<Organization> {
    const response = await httpClient.patch<Organization>(
      `${API_ENDPOINTS.ORGANIZATIONS.BY_ID(id)}/suspend`,
      { reason }
    )
    return response.data
  },

  /**
   * Récupérer les statistiques
   */
  async getStats(): Promise<OrganizationStats> {
    const response = await httpClient.get<OrganizationStats>(
      `${API_ENDPOINTS.ORGANIZATIONS.BASE}/stats`
    )
    return response.data
  },

  /**
   * Rechercher des organisations
   */
  async search(query: string, params?: OrganizationSearchParams): Promise<Organization[]> {
    const response = await httpClient.get<Organization[]>(
      `${API_ENDPOINTS.ORGANIZATIONS.BASE}/search`,
      { params: { q: query, ...params } }
    )
    return response.data || []
  }
}

