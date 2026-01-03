import { httpClient } from './http.client'
import type { User, UserCreate, UserUpdate, UserSearchParams, UserProfile, UserStats, UserActivity } from '../types/user.types'
import { API_ENDPOINTS } from '../constants/api.constants'
import { UserStatus } from '../types/user.types'

/**
 * Interface pour les données utilisateur retournées par le backend
 */
interface BackendUserDTO {
  id: number
  email: string
  firstName?: string
  lastName?: string
  enabled?: boolean
  accountNonExpired?: boolean
  accountNonLocked?: boolean
  credentialsNonExpired?: boolean
  roleNames?: string[]
  createdAt?: string
  updatedAt?: string
  lastLoginAt?: string | null
  phone?: string
  avatar?: string
  avatarUrl?: string
  organizationId?: number
  organizationName?: string
  emailVerified?: boolean
  language?: string
  timezone?: string
  notificationPreferences?: string | Record<string, boolean>
  metadata?: Record<string, any>
}

/**
 * Mapper les données du backend vers le format User du frontend
 */
function mapBackendUserToUser(backendUser: BackendUserDTO): User {
  // Déterminer le statut basé sur les flags du backend
  let status: UserStatus = UserStatus.ACTIVE
  if (!backendUser.enabled) {
    status = UserStatus.INACTIVE
  } else if (!backendUser.accountNonLocked) {
    status = UserStatus.SUSPENDED
  }

  // Construire le nom complet
  const name = backendUser.firstName && backendUser.lastName
    ? `${backendUser.firstName} ${backendUser.lastName}`
    : backendUser.email

  // Mapper roleNames vers roles
  const roles = backendUser.roleNames || []

  // Parser les préférences de notifications si c'est une string JSON
  let notificationPreferences: Record<string, boolean> | undefined
  if (backendUser.notificationPreferences) {
    if (typeof backendUser.notificationPreferences === 'string') {
      try {
        notificationPreferences = JSON.parse(backendUser.notificationPreferences)
      } catch (e) {
        // Ignore parsing errors
      }
    } else {
      notificationPreferences = backendUser.notificationPreferences
    }
  }

  return {
    id: backendUser.id,
    email: backendUser.email,
    name,
    firstName: backendUser.firstName,
    lastName: backendUser.lastName,
    phone: backendUser.phone,
    avatar: backendUser.avatar || backendUser.avatarUrl,
    avatarUrl: backendUser.avatarUrl || backendUser.avatar,
    status,
    roles: roles as any[], // Les rôles sont des strings, mais le type attend UserRole[]
    organizationId: backendUser.organizationId,
    organizationName: backendUser.organizationName,
    createdAt: backendUser.createdAt || new Date().toISOString(),
    updatedAt: backendUser.updatedAt || new Date().toISOString(),
    lastLoginAt: backendUser.lastLoginAt || undefined,
    emailVerified: backendUser.emailVerified || false,
    language: backendUser.language,
    timezone: backendUser.timezone,
    notificationPreferences,
    metadata: backendUser.metadata
  }
}

/**
 * Note: L'enrichissement avec organizationName n'est plus nécessaire
 * car le backend retourne maintenant organizationName directement dans UserDTO
 */

/**
 * Service de gestion des utilisateurs SaaS
 */
export const userService = {
  /**
   * Récupérer tous les utilisateurs avec filtres et pagination
   */
  async getAll(params?: UserSearchParams): Promise<{ users: User[]; total: number }> {
    const response = await httpClient.get<{ content: BackendUserDTO[]; totalElements: number }>(
      API_ENDPOINTS.USERS.BASE,
      { params }
    )
    // Handle both paginated response (content) and simple list response
    const backendUsers = response.data?.content || response.data || []
    const users = Array.isArray(backendUsers) 
      ? backendUsers.map(mapBackendUserToUser)
      : []
    
    return {
      users,
      total: response.data?.totalElements || users.length
    }
  },

  /**
   * Récupérer un utilisateur par ID
   */
  async getById(id: number): Promise<User> {
    const response = await httpClient.get<BackendUserDTO>(API_ENDPOINTS.USERS.BY_ID(id))
    return mapBackendUserToUser(response.data)
  },

  /**
   * Récupérer le profil de l'utilisateur connecté
   */
  async getProfile(): Promise<UserProfile> {
    const response = await httpClient.get<BackendUserDTO>(API_ENDPOINTS.USERS.PROFILE)
    return mapBackendUserToUser(response.data) as UserProfile
  },

  /**
   * Créer un nouvel utilisateur
   */
  async create(data: UserCreate): Promise<User> {
    const response = await httpClient.post<BackendUserDTO>(API_ENDPOINTS.USERS.BASE, data)
    return mapBackendUserToUser(response.data)
  },

  /**
   * Mettre à jour un utilisateur
   */
  async update(id: number, data: UserUpdate): Promise<User> {
    const response = await httpClient.put<BackendUserDTO>(API_ENDPOINTS.USERS.BY_ID(id), data)
    return mapBackendUserToUser(response.data)
  },

  /**
   * Mettre à jour le profil de l'utilisateur connecté
   */
  async updateProfile(data: { firstName?: string; lastName?: string; email?: string; avatarUrl?: string; language?: string; timezone?: string }): Promise<UserProfile> {
    const response = await httpClient.put<BackendUserDTO>(API_ENDPOINTS.USERS.UPDATE_PROFILE, data)
    return mapBackendUserToUser(response.data) as UserProfile
  },

  /**
   * Mettre à jour les préférences de l'utilisateur connecté
   */
  async updatePreferences(data: { language?: string; timezone?: string; notificationPreferences?: Record<string, boolean> }): Promise<UserProfile> {
    const response = await httpClient.put<BackendUserDTO>(API_ENDPOINTS.USERS.UPDATE_PREFERENCES, data)
    return mapBackendUserToUser(response.data) as UserProfile
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
    const response = await httpClient.patch<BackendUserDTO>(`${API_ENDPOINTS.USERS.BY_ID(id)}/activate`)
    return mapBackendUserToUser(response.data)
  },

  /**
   * Désactiver un utilisateur
   */
  async deactivate(id: number): Promise<User> {
    const response = await httpClient.patch<BackendUserDTO>(`${API_ENDPOINTS.USERS.BY_ID(id)}/deactivate`)
    return mapBackendUserToUser(response.data)
  },

  /**
   * Suspendre un utilisateur
   */
  async suspend(id: number, reason?: string): Promise<User> {
    const response = await httpClient.patch<BackendUserDTO>(`${API_ENDPOINTS.USERS.BY_ID(id)}/suspend`, { reason })
    return mapBackendUserToUser(response.data)
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
    const response = await httpClient.get<BackendUserDTO[]>(`${API_ENDPOINTS.USERS.BASE}/search`, {
      params: { q: query, ...params }
    })
    return (response.data || []).map(mapBackendUserToUser)
  },

  /**
   * Assigner des rôles à un utilisateur
   */
  async assignRoles(userId: number, roles: string[]): Promise<User> {
    const response = await httpClient.post<BackendUserDTO>(`${API_ENDPOINTS.USERS.BY_ID(userId)}/roles`, { roles })
    return mapBackendUserToUser(response.data)
  },

  /**
   * Retirer des rôles d'un utilisateur
   */
  async removeRoles(userId: number, roles: string[]): Promise<User> {
    const response = await httpClient.delete<BackendUserDTO>(`${API_ENDPOINTS.USERS.BY_ID(userId)}/roles`, {
      data: { roles }
    })
    return mapBackendUserToUser(response.data)
  }
}

