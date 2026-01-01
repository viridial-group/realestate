import { httpClient } from './http.client'
import type { User, UserCreate, UserUpdate, UserSearchParams, UserProfile, UserStats, UserActivity } from '../types/user.types'
import { API_ENDPOINTS } from '../constants/api.constants'

/**
 * Service de gestion des utilisateurs SaaS
 */
export const userService = {
  /**
   * Récupérer tous les utilisateurs avec filtres et pagination
   */
  async getAll(params?: UserSearchParams): Promise<{ users: User[]; total: number }> {
    const response = await httpClient.get<{ content: User[]; totalElements: number }>(
      API_ENDPOINTS.USERS.BASE,
      { params }
    )
    return {
      users: response.data?.content || response.data || [],
      total: response.data?.totalElements || 0
    }
  },

  /**
   * Récupérer un utilisateur par ID
   */
  async getById(id: number): Promise<User> {
    const response = await httpClient.get<User>(API_ENDPOINTS.USERS.BY_ID(id))
    return response.data
  },

  /**
   * Récupérer le profil de l'utilisateur connecté
   */
  async getProfile(): Promise<UserProfile> {
    const response = await httpClient.get<UserProfile>(API_ENDPOINTS.USERS.PROFILE)
    return response.data
  },

  /**
   * Créer un nouvel utilisateur
   */
  async create(data: UserCreate): Promise<User> {
    const response = await httpClient.post<User>(API_ENDPOINTS.USERS.BASE, data)
    return response.data
  },

  /**
   * Mettre à jour un utilisateur
   */
  async update(id: number, data: UserUpdate): Promise<User> {
    const response = await httpClient.put<User>(API_ENDPOINTS.USERS.BY_ID(id), data)
    return response.data
  },

  /**
   * Mettre à jour le profil de l'utilisateur connecté
   */
  async updateProfile(data: UserUpdate): Promise<UserProfile> {
    const response = await httpClient.put<UserProfile>(API_ENDPOINTS.USERS.PROFILE, data)
    return response.data
  },

  /**
   * Supprimer un utilisateur (soft delete)
   */
  async delete(id: number): Promise<void> {
    await httpClient.delete(API_ENDPOINTS.USERS.BY_ID(id))
  },

  /**
   * Activer un utilisateur
   */
  async activate(id: number): Promise<User> {
    const response = await httpClient.patch<User>(`${API_ENDPOINTS.USERS.BY_ID(id)}/activate`)
    return response.data
  },

  /**
   * Désactiver un utilisateur
   */
  async deactivate(id: number): Promise<User> {
    const response = await httpClient.patch<User>(`${API_ENDPOINTS.USERS.BY_ID(id)}/deactivate`)
    return response.data
  },

  /**
   * Suspendre un utilisateur
   */
  async suspend(id: number, reason?: string): Promise<User> {
    const response = await httpClient.patch<User>(`${API_ENDPOINTS.USERS.BY_ID(id)}/suspend`, { reason })
    return response.data
  },

  /**
   * Réinitialiser le mot de passe d'un utilisateur
   */
  async resetPassword(id: number, newPassword: string): Promise<void> {
    await httpClient.post(`${API_ENDPOINTS.USERS.BY_ID(id)}/reset-password`, { password: newPassword })
  },

  /**
   * Changer le mot de passe de l'utilisateur connecté
   */
  async changePassword(currentPassword: string, newPassword: string): Promise<void> {
    await httpClient.post(`${API_ENDPOINTS.USERS.PROFILE}/change-password`, {
      currentPassword,
      newPassword
    })
  },

  /**
   * Vérifier l'email d'un utilisateur
   */
  async verifyEmail(token: string): Promise<void> {
    await httpClient.post(`${API_ENDPOINTS.USERS.PROFILE}/verify-email`, { token })
  },

  /**
   * Renvoyer l'email de vérification
   */
  async resendVerificationEmail(): Promise<void> {
    await httpClient.post(`${API_ENDPOINTS.USERS.PROFILE}/resend-verification`)
  },

  /**
   * Récupérer les statistiques des utilisateurs
   */
  async getStats(): Promise<UserStats> {
    const response = await httpClient.get<UserStats>(`${API_ENDPOINTS.USERS.BASE}/stats`)
    return response.data
  },

  /**
   * Récupérer l'activité d'un utilisateur
   */
  async getActivity(userId: number, limit?: number): Promise<UserActivity[]> {
    const response = await httpClient.get<UserActivity[]>(
      `${API_ENDPOINTS.USERS.BY_ID(userId)}/activity`,
      { params: { limit } }
    )
    return response.data || []
  },

  /**
   * Rechercher des utilisateurs
   */
  async search(query: string, params?: UserSearchParams): Promise<User[]> {
    const response = await httpClient.get<User[]>(`${API_ENDPOINTS.USERS.BASE}/search`, {
      params: { q: query, ...params }
    })
    return response.data || []
  },

  /**
   * Assigner des rôles à un utilisateur
   */
  async assignRoles(userId: number, roles: string[]): Promise<User> {
    const response = await httpClient.post<User>(`${API_ENDPOINTS.USERS.BY_ID(userId)}/roles`, { roles })
    return response.data
  },

  /**
   * Retirer des rôles d'un utilisateur
   */
  async removeRoles(userId: number, roles: string[]): Promise<User> {
    const response = await httpClient.delete<User>(`${API_ENDPOINTS.USERS.BY_ID(userId)}/roles`, {
      data: { roles }
    })
    return response.data
  }
}

