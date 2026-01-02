/**
 * Types pour les PropertyFeatures
 */

export interface PropertyFeature {
  id: number
  propertyId?: number // May come from backend as property.id
  property?: { id: number } // Backend may return full property object
  key: string
  value: string
  type?: string
  active: boolean
  createdAt: string
  updatedAt: string
}

