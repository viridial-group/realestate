import { httpClient } from './http.client'
import { API_ENDPOINTS } from '../constants/api.constants'
import type { VisitAppointment, VisitAppointmentCreate, VisitAppointmentUpdate, VisitExchangeRequest } from '../types/visit-appointment.types'

/**
 * Service de gestion des rendez-vous de visite
 */
export const visitAppointmentService = {
  /**
   * Créer un nouveau rendez-vous de visite
   */
  async create(data: VisitAppointmentCreate): Promise<VisitAppointment> {
    const response = await httpClient.post<VisitAppointment>(API_ENDPOINTS.VISITS.BASE, data)
    return response.data
  },

  /**
   * Récupérer toutes les visites pour une propriété
   */
  async getByProperty(propertyId: number, params?: { page?: number; size?: number }): Promise<VisitAppointment[]> {
    const response = await httpClient.get<VisitAppointment[]>(
      API_ENDPOINTS.VISITS.BY_PROPERTY(propertyId),
      { params }
    )
    return response.data
  },

  /**
   * Récupérer les visites à venir pour une propriété
   */
  async getUpcomingByProperty(propertyId: number): Promise<VisitAppointment[]> {
    const response = await httpClient.get<VisitAppointment[]>(
      API_ENDPOINTS.VISITS.BY_PROPERTY_UPCOMING(propertyId)
    )
    return response.data
  },

  /**
   * Récupérer toutes les visites de l'utilisateur connecté
   */
  async getMyVisits(): Promise<VisitAppointment[]> {
    const response = await httpClient.get<VisitAppointment[]>(API_ENDPOINTS.VISITS.MY_VISITS)
    return response.data
  },

  /**
   * Récupérer toutes les visites pour un agent
   */
  async getByAgent(agentId: number, params?: { page?: number; size?: number }): Promise<VisitAppointment[]> {
    const response = await httpClient.get<VisitAppointment[]>(
      API_ENDPOINTS.VISITS.BY_AGENT(agentId),
      { params }
    )
    return response.data
  },

  /**
   * Récupérer les visites à venir pour un agent
   */
  async getUpcomingByAgent(agentId: number): Promise<VisitAppointment[]> {
    const response = await httpClient.get<VisitAppointment[]>(
      API_ENDPOINTS.VISITS.BY_AGENT_UPCOMING(agentId)
    )
    return response.data
  },

  /**
   * Vérifier la disponibilité d'un créneau pour un agent
   */
  async checkAvailability(agentId: number, startTime: string, durationMinutes: number = 60): Promise<boolean> {
    const response = await httpClient.get<boolean>(
      API_ENDPOINTS.VISITS.CHECK_AVAILABILITY(agentId),
      { params: { startTime, durationMinutes } }
    )
    return response.data
  },

  /**
   * Confirmer un rendez-vous
   */
  async confirm(id: number): Promise<VisitAppointment> {
    const response = await httpClient.put<VisitAppointment>(API_ENDPOINTS.VISITS.CONFIRM(id))
    return response.data
  },

  /**
   * Annuler un rendez-vous
   */
  async cancel(id: number, reason?: string): Promise<VisitAppointment> {
    const response = await httpClient.put<VisitAppointment>(
      API_ENDPOINTS.VISITS.CANCEL(id),
      null,
      { params: reason ? { reason } : {} }
    )
    return response.data
  },

  /**
   * Marquer une visite comme complétée
   */
  async complete(id: number, agentNotes?: string): Promise<VisitAppointment> {
    const response = await httpClient.put<VisitAppointment>(
      API_ENDPOINTS.VISITS.COMPLETE(id),
      null,
      { params: agentNotes ? { agentNotes } : {} }
    )
    return response.data
  },

  /**
   * Récupérer un rendez-vous par son ID
   */
  async getById(id: number): Promise<VisitAppointment> {
    const response = await httpClient.get<VisitAppointment>(API_ENDPOINTS.VISITS.BY_ID(id))
    return response.data
  },

  /**
   * Mettre à jour un rendez-vous
   */
  async update(id: number, data: VisitAppointmentUpdate): Promise<VisitAppointment> {
    const response = await httpClient.put<VisitAppointment>(API_ENDPOINTS.VISITS.BY_ID(id), data)
    return response.data
  },

  /**
   * Proposer un échange de créneau
   */
  async proposeExchange(id: number, data: VisitExchangeRequest): Promise<VisitAppointment> {
    const response = await httpClient.post<VisitAppointment>(API_ENDPOINTS.VISITS.EXCHANGE(id), data)
    return response.data
  },

  /**
   * Supprimer un rendez-vous
   */
  async delete(id: number): Promise<void> {
    await httpClient.delete(API_ENDPOINTS.VISITS.BY_ID(id))
  }
}

