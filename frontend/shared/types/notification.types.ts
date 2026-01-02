/**
 * Types pour le module de notifications
 */

export interface Notification {
  id: number
  type: string // INFO, WARNING, ERROR, SUCCESS, SYSTEM, USER, ALERT, etc.
  title: string
  message?: string
  recipientId: number
  organizationId?: number
  senderId?: number
  status: 'PENDING' | 'SENT' | 'READ' | 'ARCHIVED'
  channel?: 'IN_APP' | 'PUSH' | 'SMS' | 'EMAIL'
  targetType?: string
  targetId?: number
  actionUrl?: string
  readAt?: string
  active?: boolean
  metadata?: string | Record<string, any>
  createdAt: string
  updatedAt?: string
  // Computed properties for UI
  read?: boolean
}

export interface NotificationCreate {
  type: string
  title: string
  message?: string
  recipientId: number
  organizationId: number
  senderId?: number
  channel?: 'IN_APP' | 'PUSH' | 'SMS' | 'EMAIL'
  targetType?: string
  targetId?: number
  actionUrl?: string
  metadata?: string | Record<string, any>
}

export interface NotificationSendRequest {
  type: string
  title: string
  message: string
  recipientId: number
  organizationId: number
  senderId?: number
  channel?: 'IN_APP' | 'PUSH' | 'SMS' | 'EMAIL'
  targetType?: string
  targetId?: number
}

export interface UnreadCountResponse {
  count: number
}

