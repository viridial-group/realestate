import { httpClient } from './http.client'
import { API_ENDPOINTS } from '../constants/api.constants'
import type { Review, ReviewCreate, ReviewStats, ReviewSearchParams } from '../types/review.types'

export interface PaginatedResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
}

/**
 * Service de gestion des avis
 */
export const reviewService = {
  /**
   * Créer un nouvel avis
   */
  async create(data: ReviewCreate): Promise<Review> {
    const response = await httpClient.post<Review>(API_ENDPOINTS.REVIEWS.BASE, data)
    return response.data
  },

  /**
   * Récupérer les avis approuvés d'une propriété (public)
   */
  async getApprovedByProperty(propertyId: number, params?: { page?: number; size?: number }): Promise<PaginatedResponse<Review>> {
    const response = await httpClient.get<PaginatedResponse<Review>>(
      API_ENDPOINTS.REVIEWS.BY_PROPERTY(propertyId),
      { params }
    )
    return response.data
  },

  /**
   * Récupérer tous les avis d'une propriété (admin)
   */
  async getAllByProperty(propertyId: number, params?: { page?: number; size?: number }): Promise<PaginatedResponse<Review>> {
    const response = await httpClient.get<PaginatedResponse<Review>>(
      API_ENDPOINTS.REVIEWS.BY_PROPERTY_ALL(propertyId),
      { params }
    )
    return response.data
  },

  /**
   * Récupérer les statistiques des avis d'une propriété
   */
  async getStatsByProperty(propertyId: number): Promise<ReviewStats> {
    const response = await httpClient.get<ReviewStats>(
      API_ENDPOINTS.REVIEWS.BY_PROPERTY_STATS(propertyId)
    )
    return response.data
  },

  /**
   * Récupérer un avis par ID
   */
  async getById(id: number): Promise<Review> {
    const response = await httpClient.get<Review>(API_ENDPOINTS.REVIEWS.BY_ID(id))
    return response.data
  },

  /**
   * Récupérer les avis d'un utilisateur
   */
  async getByUser(userId: number, params?: { page?: number; size?: number }): Promise<PaginatedResponse<Review>> {
    const response = await httpClient.get<PaginatedResponse<Review>>(
      API_ENDPOINTS.REVIEWS.BY_USER(userId),
      { params }
    )
    return response.data
  },

  /**
   * Récupérer les avis par statut (admin)
   */
  async getByStatus(status: string, params?: { page?: number; size?: number }): Promise<PaginatedResponse<Review>> {
    const response = await httpClient.get<PaginatedResponse<Review>>(
      API_ENDPOINTS.REVIEWS.BY_STATUS(status),
      { params }
    )
    return response.data
  },

  /**
   * Mettre à jour le statut d'un avis (admin)
   */
  async updateStatus(id: number, status: string): Promise<Review> {
    const response = await httpClient.put<Review>(
      API_ENDPOINTS.REVIEWS.UPDATE_STATUS(id),
      null,
      { params: { status } }
    )
    return response.data
  },

  /**
   * Marquer un avis comme utile
   */
  async markHelpful(id: number): Promise<Review> {
    const response = await httpClient.post<Review>(API_ENDPOINTS.REVIEWS.MARK_HELPFUL(id))
    return response.data
  },

  /**
   * Supprimer un avis
   */
  async delete(id: number): Promise<void> {
    await httpClient.delete(API_ENDPOINTS.REVIEWS.BY_ID(id))
  }
}

