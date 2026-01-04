/**
 * Types pour l'historique des prix des propriétés
 */

export interface PriceHistory {
  id: number
  propertyId: number
  price: number
  currency: string
  changeDate: string
  changeReason?: string
  createdBy?: number
  source?: string
  createdAt: string
  updatedAt?: string
}

export interface PriceHistoryCreate {
  propertyId: number
  price: number
  currency?: string
  changeDate?: string
  changeReason?: string
  source?: string
}

export interface PriceHistoryStats {
  currentPrice: number
  initialPrice: number
  minPrice: number
  maxPrice: number
  totalChange: number
  totalChangePercent: number
  totalChanges: number
  firstPriceDate?: string
  lastPriceDate?: string
  daysSinceFirstPrice?: number
  trend: 'UP' | 'DOWN' | 'STABLE'
}

