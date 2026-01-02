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

export interface DocumentUploadParams {
  file: File
  organizationId: number
  createdBy: number
  propertyId?: number
  resourceId?: number
  description?: string
}

