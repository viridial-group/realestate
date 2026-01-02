import { httpClient } from './http.client'
import type { Document } from '../types/document.types'
import { API_ENDPOINTS } from '../constants/api.constants'

export interface DocumentUploadParams {
  file: File
  organizationId: number
  createdBy: number
  propertyId?: number
  resourceId?: number
  description?: string
}

/**
 * Service de gestion des documents
 */
export const documentService = {
  /**
   * Uploader un document
   */
  async upload(params: DocumentUploadParams): Promise<Document> {
    const formData = new FormData()
    formData.append('file', params.file)
    formData.append('organizationId', params.organizationId.toString())
    formData.append('createdBy', params.createdBy.toString())
    
    if (params.propertyId) {
      formData.append('propertyId', params.propertyId.toString())
    }
    if (params.resourceId) {
      formData.append('resourceId', params.resourceId.toString())
    }
    if (params.description) {
      formData.append('description', params.description)
    }

    const response = await httpClient.post<Document>(
      API_ENDPOINTS.DOCUMENTS.UPLOAD,
      formData,
      {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      }
    )
    return response.data
  },

  /**
   * Récupérer un document par ID
   */
  async getById(id: number): Promise<Document> {
    const response = await httpClient.get<Document>(API_ENDPOINTS.DOCUMENTS.BY_ID(id))
    return response.data
  },

  /**
   * Télécharger un document
   */
  async download(id: number): Promise<Blob> {
    const response = await httpClient.get<Blob>(
      API_ENDPOINTS.DOCUMENTS.DOWNLOAD(id),
      {
        responseType: 'blob'
      }
    )
    return response.data
  },

  /**
   * Récupérer les documents d'une propriété
   */
  async getByPropertyId(propertyId: number): Promise<Document[]> {
    const response = await httpClient.get<Document[]>(
      API_ENDPOINTS.DOCUMENTS.BASE,
      {
        params: { propertyId }
      }
    )
    return response.data || []
  },

  /**
   * Récupérer les documents d'une ressource
   */
  async getByResourceId(resourceId: number): Promise<Document[]> {
    const response = await httpClient.get<Document[]>(
      API_ENDPOINTS.DOCUMENTS.BASE,
      {
        params: { resourceId }
      }
    )
    return response.data || []
  },

  /**
   * Supprimer un document
   */
  async delete(id: number): Promise<void> {
    await httpClient.delete(API_ENDPOINTS.DOCUMENTS.BY_ID(id))
  },

  /**
   * Obtenir l'URL de téléchargement d'un document
   */
  getDownloadUrl(id: number): string {
    return API_ENDPOINTS.DOCUMENTS.DOWNLOAD(id)
  },

  /**
   * Obtenir l'URL d'affichage d'un document (pour les images)
   */
  getViewUrl(id: number): string {
    return API_ENDPOINTS.DOCUMENTS.DOWNLOAD(id)
  }
}

