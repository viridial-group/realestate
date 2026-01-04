/**
 * Types pour les rendez-vous de visite
 */

export interface VisitAppointment {
  id: number
  propertyId: number
  propertyTitle?: string
  userId?: number
  agentId: number
  agentName?: string
  visitorName?: string
  visitorEmail?: string
  visitorPhone?: string
  appointmentDate: string
  durationMinutes: number
  status: VisitStatus
  notes?: string
  agentNotes?: string
  reminderSent: boolean
  reminderSentAt?: string
  confirmedAt?: string
  cancelledAt?: string
  cancellationReason?: string
  createdAt: string
  updatedAt?: string
}

export interface VisitAppointmentCreate {
  propertyId: number
  agentId: number
  appointmentDate: string
  durationMinutes?: number
  visitorName?: string
  visitorEmail?: string
  visitorPhone?: string
  notes?: string
}

export interface VisitAppointmentUpdate {
  appointmentDate?: string
  durationMinutes?: number
  visitorName?: string
  visitorEmail?: string
  visitorPhone?: string
  notes?: string
  agentNotes?: string
  status?: VisitStatus
  cancellationReason?: string
}

export interface VisitExchangeRequest {
  newAgentId: number
  newAppointmentDate: string
  newDurationMinutes?: number
  message?: string
}

export enum VisitStatus {
  PENDING = 'PENDING',
  CONFIRMED = 'CONFIRMED',
  CANCELLED = 'CANCELLED',
  COMPLETED = 'COMPLETED',
  NO_SHOW = 'NO_SHOW',
  PENDING_EXCHANGE = 'PENDING_EXCHANGE'
}

