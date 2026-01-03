import axios from 'axios'

/**
 * Types pour les documents
 */
export interface Document {
  id: number
  name: string
  description?: string
  type: string
  mimeType: string
  fileSize: number
  filePath: string
  url?: string
  resourceId?: number
  propertyId?: number
  organizationId: number
  createdBy: number
  active: boolean
  metadata?: string
  createdAt: string
  updatedAt: string
}

/**
 * Service API pour les documents (public)
 */
const apiClient = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json',
  },
})

export const documentService = {
  /**
   * Récupère les documents d'une propriété
   */
  async getByPropertyId(propertyId: number): Promise<Document[]> {
    try {
      const response = await apiClient.get<Document[]>('/documents', {
        params: { propertyId }
      })
      return response.data || []
    } catch (error) {
      console.error(`Error fetching documents for property ${propertyId}:`, error)
      return [] // Retourner liste vide en cas d'erreur
    }
  },

  /**
   * Récupère l'URL de téléchargement/affichage d'un document
   */
  getViewUrl(id: number): string {
    return `/api/documents/${id}/download`
  },

  /**
   * Filtre les images d'une liste de documents
   */
  filterImages(documents: Document[]): Document[] {
    return documents.filter(doc => 
      doc.type === 'IMAGE' || 
      doc.mimeType?.startsWith('image/')
    )
  },
}

