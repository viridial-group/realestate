import { httpClient } from './http.client'
import { API_ENDPOINTS } from '../constants/api.constants'

export interface AuditLog {
  id: number
  actorId?: number
  actorEmail?: string
  organizationId: number
  action: string
  targetType?: string
  targetId?: number
  status: string
  description?: string
  ipAddress?: string
  userAgent?: string
  requestMethod?: string
  requestPath?: string
  metadata?: string
  createdAt: string
  updatedAt?: string
}

export interface AuditLogSearchParams {
  organizationId: number
  actorId?: number
  action?: string
  status?: string
  startDate?: string
  endDate?: string
  page?: number
  size?: number
}

export interface AuditLogPage {
  content: AuditLog[]
  totalElements: number
  totalPages: number
  number: number
  size: number
}

export const auditService = {
  /**
   * Récupère les logs d'audit avec pagination et filtres
   */
  async getAuditLogs(params: AuditLogSearchParams): Promise<AuditLogPage> {
    const response = await httpClient.get<AuditLogPage>(
      API_ENDPOINTS.AUDIT.BASE,
      { params }
    )
    return response.data
  },

  /**
   * Récupère un log d'audit par ID
   */
  async getAuditLogById(id: number): Promise<AuditLog> {
    const response = await httpClient.get<AuditLog>(API_ENDPOINTS.AUDIT.BY_ID(id))
    return response.data
  },

  /**
   * Récupère les logs d'audit récents pour une organisation
   */
  async getRecentActivities(organizationId: number, limit: number = 10): Promise<AuditLog[]> {
    const response = await httpClient.get<AuditLogPage>(
      API_ENDPOINTS.AUDIT.BASE,
      { 
        params: {
          organizationId,
          page: 0,
          size: limit
        }
      }
    )
    return response.data.content || []
  }
}

