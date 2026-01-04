/**
 * Service pour les messages de contact d'agences
 */

import { httpClient } from './http.client'
import type {
  OrganizationContactMessage,
  OrganizationContactMessageCreate
} from '../types/organization-contact.types'

export interface PaginatedResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
}

export const organizationContactService = {
  /**
   * Créer un nouveau message de contact pour une agence
   */
  async create(organizationId: number, data: OrganizationContactMessageCreate): Promise<OrganizationContactMessage> {
    const response = await httpClient.post<OrganizationContactMessage>(
      `/api/public/organizations/${organizationId}/contact`,
      data
    )
    return response.data
  },

  /**
   * Récupérer tous les messages de contact (paginated) - Admin
   */
  async getAll(params?: {
    organizationId?: number
    status?: string
    page?: number
    size?: number
  }): Promise<PaginatedResponse<OrganizationContactMessage>> {
    const queryParams = new URLSearchParams()
    if (params?.organizationId) queryParams.append('organizationId', params.organizationId.toString())
    if (params?.status) queryParams.append('status', params.status)
    if (params?.page !== undefined) queryParams.append('page', params.page.toString())
    if (params?.size !== undefined) queryParams.append('size', params.size.toString())

    const response = await httpClient.get<PaginatedResponse<OrganizationContactMessage>>(
      `/api/identity/organization-contact-messages${queryParams.toString() ? `?${queryParams.toString()}` : ''}`
    )
    return response.data
  },

  /**
   * Récupérer un message par ID - Admin
   */
  async getById(id: number): Promise<OrganizationContactMessage> {
    const response = await httpClient.get<OrganizationContactMessage>(
      `/api/identity/organization-contact-messages/${id}`
    )
    return response.data
  },

  /**
   * Marquer un message comme lu - Admin
   */
  async markAsRead(id: number, userId?: number): Promise<OrganizationContactMessage> {
    const queryParams = userId ? `?userId=${userId}` : ''
    const response = await httpClient.put<OrganizationContactMessage>(
      `/api/identity/organization-contact-messages/${id}/read${queryParams}`
    )
    return response.data
  },

  /**
   * Marquer un message comme répondu - Admin
   */
  async markAsReplied(id: number, userId?: number): Promise<OrganizationContactMessage> {
    const queryParams = userId ? `?userId=${userId}` : ''
    const response = await httpClient.put<OrganizationContactMessage>(
      `/api/identity/organization-contact-messages/${id}/replied${queryParams}`
    )
    return response.data
  },

  /**
   * Mettre à jour les notes d'un message - Admin
   */
  async updateNotes(id: number, notes: string): Promise<OrganizationContactMessage> {
    const response = await httpClient.put<OrganizationContactMessage>(
      `/api/identity/organization-contact-messages/${id}/notes`,
      notes
    )
    return response.data
  },

  /**
   * Archiver un message - Admin
   */
  async archive(id: number): Promise<OrganizationContactMessage> {
    const response = await httpClient.put<OrganizationContactMessage>(
      `/api/identity/organization-contact-messages/${id}/archive`
    )
    return response.data
  },

  /**
   * Supprimer un message (soft delete) - Admin
   */
  async delete(id: number): Promise<void> {
    await httpClient.delete(`/api/identity/organization-contact-messages/${id}`)
  },

  /**
   * Obtenir le nombre de nouveaux messages - Admin
   */
  async getNewMessagesCount(organizationId?: number): Promise<number> {
    const queryParams = organizationId ? `?organizationId=${organizationId}` : ''
    const response = await httpClient.get<number>(
      `/api/identity/organization-contact-messages/stats/new-count${queryParams}`
    )
    return response.data || 0
  }
}

