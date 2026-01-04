import { httpClient } from './http.client'
import { API_ENDPOINTS } from '../constants/api.constants'

export interface Organization {
  id: number
  name: string
  description?: string
  domain?: string
  active: boolean
  parentId?: number
  // Paramètres d'organisation
  logoUrl?: string
  address?: string
  city?: string
  postalCode?: string
  country?: string
  phone?: string
  email?: string
  customDomains?: string // JSON array
  quotas?: string // JSON object
  defaultOfficeHours?: string // JSON pour les horaires du bureau par défaut
  createdAt: string
  updatedAt?: string
}

export interface OrganizationSettings {
  logoUrl?: string
  address?: string
  city?: string
  postalCode?: string
  country?: string
  phone?: string
  email?: string
  customDomains?: string
  quotas?: string
  defaultOfficeHours?: string // JSON pour les horaires du bureau par défaut
}

export interface OrganizationCreate {
  name: string
  description?: string
  domain?: string
  active?: boolean
  parentId?: number
}

export interface OrganizationUpdate {
  name?: string
  description?: string
  domain?: string
  active?: boolean
  parentId?: number
}

export interface OrganizationSearchParams {
  page?: number
  size?: number
}

export interface Team {
  id: number
  name: string
  description?: string
  organizationId: number
  active: boolean
  createdAt: string
  updatedAt: string
}

export interface TeamCreate {
  name: string
  description?: string
  active?: boolean
}

export interface TeamUpdate {
  name?: string
  description?: string
  active?: boolean
}

export interface OrganizationUser {
  id: number
  userId: number
  organizationId: number
  organizationName?: string
  teamId?: number
  isPrimary: boolean
  active: boolean
  createdAt: string
  updatedAt: string
  user?: {
    id: number
    name: string
    email: string
  }
  // Direct user fields from DTO
  userEmail?: string
  userFirstName?: string
  userLastName?: string
  userName?: string
}

/**
 * Service de gestion des organisations
 */
export const organizationService = {
  /**
   * Récupérer toutes les organisations
   */
  async getAll(params?: OrganizationSearchParams): Promise<{ organizations: Organization[]; total: number }> {
    const response = await httpClient.get<Organization[]>(
      API_ENDPOINTS.ORGANIZATIONS.BASE,
      { params }
    )
    const organizations = Array.isArray(response.data) ? response.data : []
    return {
      organizations,
      total: organizations.length
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
   * Récupérer les organisations d'un utilisateur
   */
  async getByUserId(userId: number): Promise<Organization[]> {
    const response = await httpClient.get<Organization[]>(
      API_ENDPOINTS.ORGANIZATIONS.BY_USER_ID(userId)
    )
    return Array.isArray(response.data) ? response.data : []
  },

  /**
   * Récupérer les organisations racines (sans parent)
   */
  async getRoot(): Promise<Organization[]> {
    const response = await httpClient.get<Organization[]>(
      API_ENDPOINTS.ORGANIZATIONS.ROOT
    )
    return Array.isArray(response.data) ? response.data : []
  },

  /**
   * Récupérer les organisations enfants d'un parent
   */
  async getChildren(parentId: number): Promise<Organization[]> {
    const response = await httpClient.get<Organization[]>(
      API_ENDPOINTS.ORGANIZATIONS.CHILDREN(parentId)
    )
    return Array.isArray(response.data) ? response.data : []
  },

  /**
   * Assigner un utilisateur à une organisation
   */
  async assignUserToOrganization(organizationId: number, userId: number, isPrimary: boolean = false): Promise<void> {
    await httpClient.post(
      `/api/identity/organizations/${organizationId}/users/${userId}`,
      {},
      { params: { isPrimary } }
    )
  },

  /**
   * Retirer un utilisateur d'une organisation
   */
  async removeUserFromOrganization(organizationId: number, userId: number): Promise<void> {
    await httpClient.delete(`/api/identity/organizations/${organizationId}/users/${userId}`)
  },

  /**
   * Définir une organisation comme principale pour un utilisateur
   */
  async setPrimaryOrganization(organizationId: number, userId: number): Promise<void> {
    await httpClient.put(`/api/identity/organizations/${organizationId}/users/${userId}/primary`)
  },

  /**
   * Récupérer les utilisateurs d'une organisation
   */
  async getUsersByOrganization(organizationId: number): Promise<OrganizationUser[]> {
    const response = await httpClient.get<OrganizationUser[]>(
      `/api/identity/organizations/${organizationId}/users`
    )
    return Array.isArray(response.data) ? response.data : []
  },

  /**
   * Récupérer les utilisateurs d'une team
   */
  async getUsersByTeam(organizationId: number, teamId: number): Promise<OrganizationUser[]> {
    const response = await httpClient.get<OrganizationUser[]>(
      `/api/identity/organizations/${organizationId}/users/teams/${teamId}`
    )
    return Array.isArray(response.data) ? response.data : []
  },

  /**
   * Assigner un utilisateur à une team
   */
  async addUserToTeam(organizationId: number, userId: number, teamId: number): Promise<void> {
    await httpClient.post(
      `/api/identity/organizations/${organizationId}/users/${userId}/teams/${teamId}`
    )
  },

  // Teams Management
  /**
   * Récupérer les teams d'une organisation
   */
  async getTeamsByOrganization(organizationId: number): Promise<Team[]> {
    const response = await httpClient.get<Team[]>(
      `/api/identity/organizations/${organizationId}/teams`
    )
    return Array.isArray(response.data) ? response.data : []
  },

  /**
   * Récupérer une team par ID
   */
  async getTeamById(organizationId: number, teamId: number): Promise<Team> {
    const response = await httpClient.get<Team>(
      `/api/identity/organizations/${organizationId}/teams/${teamId}`
    )
    return response.data
  },

  /**
   * Créer une team
   */
  async createTeam(organizationId: number, data: TeamCreate): Promise<Team> {
    const response = await httpClient.post<Team>(
      `/api/identity/organizations/${organizationId}/teams`,
      data
    )
    return response.data
  },

  /**
   * Mettre à jour une team
   */
  async updateTeam(organizationId: number, teamId: number, data: TeamUpdate): Promise<Team> {
    const response = await httpClient.put<Team>(
      `/api/identity/organizations/${organizationId}/teams/${teamId}`,
      data
    )
    return response.data
  },

  /**
   * Supprimer une team
   */
  async deleteTeam(organizationId: number, teamId: number): Promise<void> {
    await httpClient.delete(`/api/identity/organizations/${organizationId}/teams/${teamId}`)
  },

  /**
   * Récupérer les paramètres d'une organisation
   */
  async getSettings(organizationId: number): Promise<OrganizationSettings> {
    const response = await httpClient.get<OrganizationSettings>(
      `/api/identity/organizations/${organizationId}/settings`
    )
    return response.data
  },

  /**
   * Mettre à jour les paramètres d'une organisation
   */
  async updateSettings(organizationId: number, data: OrganizationSettings): Promise<Organization> {
    const response = await httpClient.put<Organization>(
      `/api/identity/organizations/${organizationId}/settings`,
      data
    )
    return response.data
  }
}

