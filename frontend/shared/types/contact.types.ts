/**
 * Types pour les messages de contact
 */

export interface ContactMessage {
  id: number
  name: string
  email: string
  phone?: string
  subject: string
  message: string
  status: ContactMessageStatus
  propertyId?: number
  propertyTitle?: string
  propertyReference?: string
  organizationId?: number
  readAt?: string
  readBy?: number
  readByName?: string
  repliedAt?: string
  repliedBy?: number
  repliedByName?: string
  notes?: string
  active: boolean
  createdAt: string
  updatedAt: string
}

export interface ContactMessageCreate {
  name: string
  email: string
  phone?: string
  subject: string
  message: string
  propertyId?: number
  organizationId?: number
}

export interface ContactMessageUpdate {
  status?: ContactMessageStatus
  notes?: string
}

export interface ContactMessageSearchParams {
  organizationId?: number
  assignedUserId?: number
  status?: ContactMessageStatus
  propertyId?: number
  page?: number
  size?: number
}

export enum ContactMessageStatus {
  NEW = 'NEW',
  READ = 'READ',
  REPLIED = 'REPLIED',
  ARCHIVED = 'ARCHIVED'
}

