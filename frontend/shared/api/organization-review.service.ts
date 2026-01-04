/**
 * Service pour les avis d'agences immobilières
 */

import { httpClient } from './http.client'
import type {
  OrganizationPerformanceStats,
  OrganizationReview,
  OrganizationReviewCreate,
  OrganizationReviewStats,
  OrganizationWithReviews
} from '../types/organization-review.types'

export const organizationReviewService = {
  /**
   * Rechercher des agences avec leurs statistiques d'avis
   */
  async searchOrganizations(params?: {
    city?: string
    postalCode?: string
    minRating?: number
  }): Promise<OrganizationWithReviews[]> {
    const queryParams = new URLSearchParams()
    if (params?.city) queryParams.append('city', params.city)
    if (params?.postalCode) queryParams.append('postalCode', params.postalCode)
    if (params?.minRating) queryParams.append('minRating', params.minRating.toString())

    const response = await httpClient.get<OrganizationWithReviews[]>(
      `/api/public/organizations/search${queryParams.toString() ? `?${queryParams.toString()}` : ''}`
    )
    return response.data
  },

  /**
   * Obtenir une agence spécifique avec ses statistiques d'avis
   */
  async getOrganizationWithReviewsById(organizationId: number): Promise<OrganizationWithReviews | null> {
    try {
      const response = await httpClient.get<OrganizationWithReviews>(
        `/api/public/organizations/${organizationId}`
      )
      return response.data
    } catch (error: any) {
      if (error.status === 404) {
        return null
      }
      throw error
    }
  },

  /**
   * Obtenir les avis approuvés d'une agence
   */
  async getApprovedReviews(
    organizationId: number,
    page: number = 0,
    size: number = 10
  ): Promise<{ content: OrganizationReview[]; totalElements: number; totalPages: number }> {
    const response = await httpClient.get<{
      content: OrganizationReview[]
      totalElements: number
      totalPages: number
    }>(`/api/public/organizations/${organizationId}/reviews?page=${page}&size=${size}`)
    return response.data
  },

  /**
   * Obtenir les statistiques d'avis d'une agence
   */
  async getReviewStats(organizationId: number): Promise<OrganizationReviewStats> {
    const response = await httpClient.get<OrganizationReviewStats>(
      `/api/public/organizations/${organizationId}/reviews/stats`
    )
    return response.data
  },

  /**
   * Créer un avis pour une agence
   */
  async createReview(organizationId: number, review: OrganizationReviewCreate): Promise<OrganizationReview> {
    const response = await httpClient.post<OrganizationReview>(
      `/api/public/organizations/${organizationId}/reviews`,
      review
    )
    return response.data
  },

  /**
   * Marquer un avis comme utile
   */
  async markAsHelpful(reviewId: number): Promise<void> {
    await httpClient.post(`/api/public/organizations/reviews/${reviewId}/helpful`)
  },

  /**
   * Obtenir les statistiques de performance d'une agence
   */
  async getPerformanceStats(organizationId: number): Promise<OrganizationPerformanceStats> {
    const response = await httpClient.get<OrganizationPerformanceStats>(
      `/api/public/organizations/${organizationId}/performance`
    )
    return response.data
  }
}

