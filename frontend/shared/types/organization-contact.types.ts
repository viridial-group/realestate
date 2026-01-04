/**
 * Types pour les messages de contact d'agences
 */

export interface OrganizationContactMessage {
  id: number
  name: string
  email: string
  phone?: string
  subject: string
  message: string
  status: 'NEW' | 'READ' | 'REPLIED' | 'ARCHIVED'
  organizationId: number
  organizationName?: string
  readAt?: string
  readBy?: number
  readByName?: string
  repliedAt?: string
  repliedBy?: number
  repliedByName?: string
  notes?: string
  active: boolean
  createdAt: string
  updatedAt?: string
}

export interface OrganizationContactMessageCreate {
  name: string
  email: string
  phone?: string
  subject: string
  message: string
  organizationId: number
}

