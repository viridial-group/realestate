/**
 * Types pour les avis (reviews)
 */

export interface Review {
  id: number
  propertyId: number
  propertyTitle?: string // Titre de la propriété (pour affichage)
  userId?: number
  authorName?: string
  authorEmail?: string
  rating: number // 1-5
  comment: string
  status: ReviewStatus
  verifiedPurchase?: boolean
  helpfulCount?: number
  createdAt: string
  updatedAt?: string
}

export interface ReviewCreate {
  propertyId: number
  userId?: number
  authorName?: string
  authorEmail?: string
  rating: number // 1-5
  comment: string
  verifiedPurchase?: boolean
}

export interface ReviewStats {
  averageRating: number
  totalReviews: number
  ratingDistribution?: Record<number, number> // rating -> count
  ratingPercentages?: Record<number, number> // rating -> percentage
}

export enum ReviewStatus {
  PENDING = 'PENDING',
  APPROVED = 'APPROVED',
  REJECTED = 'REJECTED'
}

export interface ReviewSearchParams {
  propertyId?: number
  userId?: number
  status?: ReviewStatus
  page?: number
  size?: number
}

