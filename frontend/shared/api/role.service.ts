/**
 * Service API pour la gestion des rôles et permissions
 */
import { httpClient } from './http.client'
import { API_ENDPOINTS } from '../constants/api.constants'
import type {
  Role,
  RoleCreate,
  RoleUpdate,
  Permission,
  UserRoleAssignment,
  RoleSearchParams,
  RoleStats
} from '../types/role.types'

interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages?: number
  size?: number
  number?: number
}

export const roleService = {
  /**
   * Récupérer tous les rôles
   */
  async getAll(params?: RoleSearchParams): Promise<PageResponse<Role>> {
    const response = await httpClient.get<PageResponse<Role>>(API_ENDPOINTS.ROLES.BASE, { params })
    return response.data
  },

  /**
   * Récupérer un rôle par ID
   */
  async getById(id: number): Promise<Role> {
    const response = await httpClient.get<Role>(API_ENDPOINTS.ROLES.BY_ID(id))
    return response.data
  },

  /**
   * Créer un nouveau rôle
   */
  async create(data: RoleCreate): Promise<Role> {
    const response = await httpClient.post<Role>(API_ENDPOINTS.ROLES.BASE, data)
    return response.data
  },

  /**
   * Mettre à jour un rôle
   */
  async update(id: number, data: RoleUpdate): Promise<Role> {
    const response = await httpClient.put<Role>(API_ENDPOINTS.ROLES.BY_ID(id), data)
    return response.data
  },

  /**
   * Supprimer un rôle
   */
  async delete(id: number): Promise<void> {
    await httpClient.delete(API_ENDPOINTS.ROLES.BY_ID(id))
  },

  /**
   * Récupérer toutes les permissions
   */
  async getPermissions(): Promise<Permission[]> {
    const response = await httpClient.get<Permission[]>(API_ENDPOINTS.ROLES.PERMISSIONS)
    return response.data
  },

  /**
   * Affecter des rôles à un utilisateur
   */
  async assignRolesToUser(data: UserRoleAssignment): Promise<void> {
    await httpClient.post(API_ENDPOINTS.ROLES.ASSIGN_TO_USER, data)
  },

  /**
   * Récupérer les statistiques des rôles
   */
  async getStats(): Promise<RoleStats> {
    const response = await httpClient.get<RoleStats>(API_ENDPOINTS.ROLES.STATS)
    return response.data
  }
}

