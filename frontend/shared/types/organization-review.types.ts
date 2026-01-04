/**
 * Types pour les avis d'agences immobilières
 */

export interface OrganizationReview {
  id: number
  organizationId: number
  userId?: number
  authorName?: string
  authorEmail?: string
  rating: number
  comment: string
  status: 'PENDING' | 'APPROVED' | 'REJECTED'
  verifiedClient?: boolean
  helpfulCount: number
  transactionType?: 'SALE' | 'RENT'
  createdAt: string
  updatedAt?: string
}

export interface OrganizationReviewCreate {
  organizationId: number
  userId?: number
  authorName?: string
  authorEmail?: string
  rating: number
  comment: string
  verifiedClient?: boolean
  transactionType?: 'SALE' | 'RENT'
}

export interface RatingDistribution {
  fiveStars: number
  fourStars: number
  threeStars: number
  twoStars: number
  oneStar: number
}

export interface OrganizationReviewStats {
  averageRating: number
  totalReviews: number
  ratingDistribution: RatingDistribution
  verifiedClientPercentage: number
}

export interface OrganizationWithReviews {
  id: number
  name: string
  description?: string
  logoUrl?: string
  address?: string
  city?: string
  postalCode?: string
  country?: string
  phone?: string
  email?: string
  domain?: string
  defaultOfficeHours?: string // JSON string
  reviewStats: OrganizationReviewStats
  propertyCount: number
}

export interface OrganizationPerformanceStats {
  totalProperties: number
  availableProperties: number
  soldProperties: number
  rentedProperties: number
  averagePrice: number
  minPrice: number
  maxPrice: number
  averageSurface: number
  newThisMonth: number
  newThisWeek: number
  byType: Record<string, number>
  byCity: Record<string, number>
  byStatus: Record<string, number>
  servedCities: string[]
  propertyTypes: string[]
}

