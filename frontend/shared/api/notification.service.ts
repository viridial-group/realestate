import { httpClient } from './http.client'
import { API_ENDPOINTS } from '../constants/api.constants'
import type { Notification, NotificationCreate, NotificationSendRequest, UnreadCountResponse } from '../types/notification.types'

/**
 * Service de gestion des notifications
 */
export const notificationService = {
  /**
   * Récupérer toutes les notifications d'un destinataire
   */
  async getNotifications(params: {
    recipientId: number
    unread?: boolean
  }): Promise<Notification[]> {
    const queryParams: any = {
      recipientId: params.recipientId
    }
    if (params.unread !== undefined) {
      queryParams.unread = params.unread
    }
    
    const response = await httpClient.get<Notification[]>(
      API_ENDPOINTS.NOTIFICATIONS.BASE,
      { params: queryParams }
    )
    const notifications = response.data || []
    
    // Mapper read depuis readAt pour compatibilité avec l'UI
    return notifications.map(n => ({
      ...n,
      read: !!n.readAt || n.status === 'READ'
    }))
  },

  /**
   * Récupérer une notification par ID
   */
  async getNotificationById(id: number): Promise<Notification> {
    const response = await httpClient.get<Notification>(API_ENDPOINTS.NOTIFICATIONS.BY_ID(id))
    const notification = response.data
    return {
      ...notification,
      read: !!notification.readAt || notification.status === 'READ'
    }
  },

  /**
   * Récupérer le nombre de notifications non lues
   */
  async getUnreadCount(recipientId: number): Promise<number> {
    const response = await httpClient.get<UnreadCountResponse>(
      API_ENDPOINTS.NOTIFICATIONS.UNREAD_COUNT,
      { params: { recipientId } }
    )
    return response.data?.count || 0
  },

  /**
   * Créer une notification
   */
  async createNotification(data: NotificationCreate): Promise<Notification> {
    const response = await httpClient.post<Notification>(
      API_ENDPOINTS.NOTIFICATIONS.BASE,
      data
    )
    const notification = response.data
    return {
      ...notification,
      read: !!notification.readAt || notification.status === 'READ'
    }
  },

  /**
   * Envoyer une notification
   */
  async sendNotification(data: NotificationSendRequest): Promise<Notification> {
    const response = await httpClient.post<Notification>(
      API_ENDPOINTS.NOTIFICATIONS.SEND,
      data
    )
    const notification = response.data
    return {
      ...notification,
      read: !!notification.readAt || notification.status === 'READ'
    }
  },

  /**
   * Marquer une notification comme lue
   */
  async markAsRead(id: number): Promise<Notification> {
    const response = await httpClient.put<Notification>(
      API_ENDPOINTS.NOTIFICATIONS.MARK_READ(id)
    )
    const notification = response.data
    return {
      ...notification,
      read: true
    }
  },

  /**
   * Marquer toutes les notifications comme lues
   * Note: Le backend ne fournit pas d'endpoint pour marquer toutes les notifications comme lues
   * On doit donc marquer chaque notification individuellement
   */
  async markAllAsRead(recipientId: number): Promise<void> {
    // Récupérer toutes les notifications non lues
    const unreadNotifications = await this.getNotifications({
      recipientId,
      unread: true
    })
    
    // Marquer chacune comme lue
    const promises = unreadNotifications.map(n => this.markAsRead(n.id))
    await Promise.all(promises)
  },

  /**
   * Archiver une notification
   */
  async markAsArchived(id: number): Promise<Notification> {
    const response = await httpClient.put<Notification>(
      API_ENDPOINTS.NOTIFICATIONS.MARK_ARCHIVED(id)
    )
    return response.data
  },

  /**
   * Supprimer une notification
   */
  async deleteNotification(id: number): Promise<void> {
    await httpClient.delete(API_ENDPOINTS.NOTIFICATIONS.BY_ID(id))
  },

  /**
   * Mettre à jour une notification
   */
  async updateNotification(id: number, data: Partial<NotificationCreate>): Promise<Notification> {
    const response = await httpClient.put<Notification>(
      API_ENDPOINTS.NOTIFICATIONS.BY_ID(id),
      data
    )
    const notification = response.data
    return {
      ...notification,
      read: !!notification.readAt || notification.status === 'READ'
    }
  }
}

