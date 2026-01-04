/**
 * Types pour le système d'annonces publicitaires
 */

export interface Advertisement {
  id: number
  organizationId: number
  organizationName?: string
  title: string
  description?: string
  imageUrl?: string
  linkUrl?: string
  adType: 'BANNER' | 'SIDEBAR' | 'INLINE' | 'POPUP' | 'SPONSORED_PROPERTY'
  position: 'TOP' | 'BOTTOM' | 'SIDEBAR_LEFT' | 'SIDEBAR_RIGHT' | 'INLINE' | 'HEADER' | 'FOOTER'
  status: 'DRAFT' | 'ACTIVE' | 'PAUSED' | 'EXPIRED' | 'ARCHIVED'
  startDate: string
  endDate?: string
  budget?: number
  dailyBudget?: number
  costPerClick?: number
  costPerImpression?: number
  targetLocations?: string // JSON
  targetPropertyTypes?: string // JSON
  targetTransactionTypes?: string // JSON
  impressions: number
  clicks: number
  conversions: number
  totalSpent: number
  maxImpressionsPerDay?: number
  priority: number
  active: boolean
  createdAt: string
  updatedAt: string
  createdBy?: number
  clickThroughRate?: number // CTR
  conversionRate?: number // CR
  costPerConversion?: number // CPA
}

export interface AdvertisementCreate {
  organizationId: number
  title: string
  description?: string
  imageUrl?: string
  linkUrl?: string
  adType: 'BANNER' | 'SIDEBAR' | 'INLINE' | 'POPUP' | 'SPONSORED_PROPERTY'
  position: 'TOP' | 'BOTTOM' | 'SIDEBAR_LEFT' | 'SIDEBAR_RIGHT' | 'INLINE' | 'HEADER' | 'FOOTER'
  status?: 'DRAFT' | 'ACTIVE' | 'PAUSED' | 'EXPIRED' | 'ARCHIVED'
  startDate: string
  endDate?: string
  budget?: number
  dailyBudget?: number
  costPerClick?: number
  costPerImpression?: number
  targetLocations?: string
  targetPropertyTypes?: string
  targetTransactionTypes?: string
  maxImpressionsPerDay?: number
  priority?: number
  active?: boolean
  createdBy?: number
}

export interface AdvertisementUpdate {
  title?: string
  description?: string
  imageUrl?: string
  linkUrl?: string
  adType?: 'BANNER' | 'SIDEBAR' | 'INLINE' | 'POPUP' | 'SPONSORED_PROPERTY'
  position?: 'TOP' | 'BOTTOM' | 'SIDEBAR_LEFT' | 'SIDEBAR_RIGHT' | 'INLINE' | 'HEADER' | 'FOOTER'
  status?: 'DRAFT' | 'ACTIVE' | 'PAUSED' | 'EXPIRED' | 'ARCHIVED'
  startDate?: string
  endDate?: string
  budget?: number
  dailyBudget?: number
  costPerClick?: number
  costPerImpression?: number
  targetLocations?: string
  targetPropertyTypes?: string
  targetTransactionTypes?: string
  maxImpressionsPerDay?: number
  priority?: number
  active?: boolean
}

export interface AdvertisementStats {
  advertisementId: number
  title: string
  impressions: number
  clicks: number
  conversions: number
  totalSpent: number
  clickThroughRate?: number
  conversionRate?: number
  costPerClick?: number
  costPerConversion?: number
  startDate: string
  endDate?: string
  status: string
}

export interface AdvertisementSearchParams {
  organizationId?: number
  status?: 'DRAFT' | 'ACTIVE' | 'PAUSED' | 'EXPIRED' | 'ARCHIVED'
  adType?: 'BANNER' | 'SIDEBAR' | 'INLINE' | 'POPUP' | 'SPONSORED_PROPERTY'
  position?: 'TOP' | 'BOTTOM' | 'SIDEBAR_LEFT' | 'SIDEBAR_RIGHT' | 'INLINE' | 'HEADER' | 'FOOTER'
}

export interface AdvertisementAnalytics {
  totalAdvertisements: number
  activeAdvertisements: number
  totalImpressions: number
  totalClicks: number
  totalConversions: number
  totalSpent: number
  averageCTR?: number
  averageCVR?: number
  averageCPC?: number
  averageCPA?: number
  dailyStats: DailyStats[]
  weeklyStats: WeeklyStats[]
  monthlyStats: MonthlyStats[]
  statsByType: Record<string, TypeStats>
  statsByPosition: Record<string, PositionStats>
  topByImpressions: TopAdvertisement[]
  topByClicks: TopAdvertisement[]
  topByConversions: TopAdvertisement[]
}

export interface DailyStats {
  date: string
  impressions: number
  clicks: number
  conversions: number
  spent: number
  ctr?: number
  cvr?: number
}

export interface WeeklyStats {
  weekStart: string
  weekEnd: string
  impressions: number
  clicks: number
  conversions: number
  spent: number
  ctr?: number
  cvr?: number
}

export interface MonthlyStats {
  year: number
  month: number
  impressions: number
  clicks: number
  conversions: number
  spent: number
  ctr?: number
  cvr?: number
}

export interface TypeStats {
  type: string
  count: number
  impressions: number
  clicks: number
  conversions: number
  spent: number
  ctr?: number
  cvr?: number
}

export interface PositionStats {
  position: string
  count: number
  impressions: number
  clicks: number
  conversions: number
  spent: number
  ctr?: number
  cvr?: number
}

export interface TopAdvertisement {
  id: number
  title: string
  impressions: number
  clicks: number
  conversions: number
  spent: number
  ctr?: number
  cvr?: number
}

