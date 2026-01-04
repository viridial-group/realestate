import { httpClient } from './http.client'
import { API_ENDPOINTS } from '../constants/api.constants'
import type { ContactMessage, ContactMessageCreate, ContactMessageSearchParams } from '../types/contact.types'

export interface PaginatedResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
}

/**
 * Service de gestion des messages de contact
 */
export const contactService = {
  /**
   * Créer un nouveau message de contact
   */
  async create(data: ContactMessageCreate): Promise<ContactMessage> {
    const response = await httpClient.post<ContactMessage>(API_ENDPOINTS.CONTACT_MESSAGES.BASE, data)
    return response.data
  },

  /**
   * Récupérer tous les messages de contact (paginated)
   */
  async getAll(params?: ContactMessageSearchParams): Promise<PaginatedResponse<ContactMessage>> {
    const response = await httpClient.get<PaginatedResponse<ContactMessage>>(
      API_ENDPOINTS.CONTACT_MESSAGES.BASE,
      { params }
    )
    return response.data
  },

  /**
   * Récupérer un message par ID
   */
  async getById(id: number): Promise<ContactMessage> {
    const response = await httpClient.get<ContactMessage>(API_ENDPOINTS.CONTACT_MESSAGES.BY_ID(id))
    return response.data
  },

  /**
   * Récupérer les messages d'une propriété
   */
  async getByProperty(propertyId: number): Promise<ContactMessage[]> {
    const response = await httpClient.get<ContactMessage[]>(
      API_ENDPOINTS.CONTACT_MESSAGES.BY_PROPERTY(propertyId)
    )
    return response.data || []
  },

  /**
   * Marquer un message comme lu
   */
  async markAsRead(id: number, userId?: number): Promise<ContactMessage> {
    const response = await httpClient.put<ContactMessage>(
      API_ENDPOINTS.CONTACT_MESSAGES.MARK_READ(id),
      null,
      { params: userId ? { userId } : {} }
    )
    return response.data
  },

  /**
   * Marquer un message comme répondu
   */
  async markAsReplied(id: number, userId?: number): Promise<ContactMessage> {
    const response = await httpClient.put<ContactMessage>(
      API_ENDPOINTS.CONTACT_MESSAGES.MARK_REPLIED(id),
      null,
      { params: userId ? { userId } : {} }
    )
    return response.data
  },

  /**
   * Mettre à jour les notes d'un message
   */
  async updateNotes(id: number, notes: string): Promise<ContactMessage> {
    const response = await httpClient.put<ContactMessage>(
      API_ENDPOINTS.CONTACT_MESSAGES.UPDATE_NOTES(id),
      notes
    )
    return response.data
  },

  /**
   * Archiver un message
   */
  async archive(id: number): Promise<ContactMessage> {
    const response = await httpClient.put<ContactMessage>(API_ENDPOINTS.CONTACT_MESSAGES.ARCHIVE(id))
    return response.data
  },

  /**
   * Supprimer un message (soft delete)
   */
  async delete(id: number): Promise<void> {
    await httpClient.delete(API_ENDPOINTS.CONTACT_MESSAGES.BY_ID(id))
  },

  /**
   * Obtenir le nombre de nouveaux messages
   */
  async getNewMessagesCount(organizationId?: number, assignedUserId?: number): Promise<number> {
    const params: Record<string, number> = {}
    if (organizationId) {
      params.organizationId = organizationId
    }
    if (assignedUserId) {
      params.assignedUserId = assignedUserId
    }
    const response = await httpClient.get<number>(API_ENDPOINTS.CONTACT_MESSAGES.NEW_COUNT, {
      params
    })
    return response.data || 0
  }
}

