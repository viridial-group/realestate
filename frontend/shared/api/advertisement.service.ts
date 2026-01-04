import { httpClient } from './http.client'
import { API_ENDPOINTS } from '../constants/api.constants'
import type { Advertisement, AdvertisementCreate, AdvertisementUpdate, AdvertisementStats, AdvertisementSearchParams, AdvertisementAnalytics } from '../types/advertisement.types'

/**
 * Service de gestion des annonces publicitaires
 */
export const advertisementService = {
  /**
   * Récupérer toutes les annonces
   */
  async getAll(params?: AdvertisementSearchParams): Promise<Advertisement[]> {
    const queryParams: any = {}
    if (params?.organizationId) {
      queryParams.organizationId = params.organizationId
    }
    if (params?.status) {
      queryParams.status = params.status
    }

    const response = await httpClient.get<Advertisement[]>(
      API_ENDPOINTS.ADVERTISEMENTS.BASE,
      { params: queryParams }
    )
    return response.data || []
  },

  /**
   * Récupérer une annonce par ID
   */
  async getById(id: number): Promise<Advertisement> {
    const response = await httpClient.get<Advertisement>(API_ENDPOINTS.ADVERTISEMENTS.BY_ID(id))
    return response.data
  },

  /**
   * Créer une annonce
   */
  async create(data: AdvertisementCreate): Promise<Advertisement> {
    const response = await httpClient.post<Advertisement>(API_ENDPOINTS.ADVERTISEMENTS.BASE, data)
    return response.data
  },

  /**
   * Mettre à jour une annonce
   */
  async update(id: number, data: AdvertisementUpdate): Promise<Advertisement> {
    const response = await httpClient.put<Advertisement>(API_ENDPOINTS.ADVERTISEMENTS.BY_ID(id), data)
    return response.data
  },

  /**
   * Supprimer une annonce
   */
  async delete(id: number): Promise<void> {
    await httpClient.delete(API_ENDPOINTS.ADVERTISEMENTS.BY_ID(id))
  },

  /**
   * Mettre à jour le statut d'une annonce
   */
  async updateStatus(id: number, status: string): Promise<Advertisement> {
    const response = await httpClient.put<Advertisement>(
      API_ENDPOINTS.ADVERTISEMENTS.STATUS(id),
      null,
      { params: { status } }
    )
    return response.data
  },

  /**
   * Récupérer les statistiques d'une annonce
   */
  async getStats(id: number): Promise<AdvertisementStats> {
    const response = await httpClient.get<AdvertisementStats>(API_ENDPOINTS.ADVERTISEMENTS.STATS(id))
    return response.data
  },

  /**
   * Récupérer les annonces actives (endpoint public)
   */
  async getActive(params?: {
    adType?: string
    position?: string
    city?: string
    postalCode?: string
    propertyType?: string
  }): Promise<Advertisement[]> {
    const queryParams: any = {}
    if (params?.adType) queryParams.adType = params.adType
    if (params?.position) queryParams.position = params.position
    if (params?.city) queryParams.city = params.city
    if (params?.postalCode) queryParams.postalCode = params.postalCode
    if (params?.propertyType) queryParams.propertyType = params.propertyType

    const response = await httpClient.get<Advertisement[]>(
      API_ENDPOINTS.ADVERTISEMENTS.PUBLIC.ACTIVE,
      { params: queryParams }
    )
    return response.data || []
  },

  /**
   * Enregistrer une impression (endpoint public)
   */
  async recordImpression(
    id: number,
    params?: {
      propertyId?: number
      pageType?: string
      city?: string
      postalCode?: string
    }
  ): Promise<void> {
    const queryParams: any = {}
    if (params?.propertyId) queryParams.propertyId = params.propertyId
    if (params?.pageType) queryParams.pageType = params.pageType
    if (params?.city) queryParams.city = params.city
    if (params?.postalCode) queryParams.postalCode = params.postalCode

    await httpClient.post(
      API_ENDPOINTS.ADVERTISEMENTS.PUBLIC.IMPRESSION(id),
      null,
      { params: queryParams }
    )
  },

  /**
   * Enregistrer un clic (endpoint public)
   */
  async recordClick(
    id: number,
    params?: {
      propertyId?: number
      city?: string
      postalCode?: string
    }
  ): Promise<void> {
    const queryParams: any = {}
    if (params?.propertyId) queryParams.propertyId = params.propertyId
    if (params?.city) queryParams.city = params.city
    if (params?.postalCode) queryParams.postalCode = params.postalCode

    await httpClient.post(
      API_ENDPOINTS.ADVERTISEMENTS.PUBLIC.CLICK(id),
      null,
      { params: queryParams }
    )
  },

  /**
   * Récupérer les analytics complètes
   */
  async getAnalytics(params?: {
    organizationId?: number
    startDate?: string
    endDate?: string
  }): Promise<AdvertisementAnalytics> {
    const queryParams: any = {}
    if (params?.organizationId) {
      queryParams.organizationId = params.organizationId
    }
    if (params?.startDate) {
      queryParams.startDate = params.startDate
    }
    if (params?.endDate) {
      queryParams.endDate = params.endDate
    }

    const response = await httpClient.get<AdvertisementAnalytics>(
      API_ENDPOINTS.ADVERTISEMENTS.ANALYTICS,
      { params: queryParams }
    )
    return response.data
  }
}

