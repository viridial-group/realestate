/**
 * Types pour les alertes de prix
 */

export interface PriceAlert {
  id: number
  propertyId: number
  propertyTitle?: string
  userId: number
  alertType: PriceAlertType
  targetPrice?: number
  percentageThreshold?: number
  isPercentage: boolean
  notified: boolean
  notifiedAt?: string
  active: boolean
  emailNotification: boolean
  inAppNotification: boolean
  createdAt: string
  updatedAt?: string
}

export interface PriceAlertCreate {
  propertyId: number
  alertType: PriceAlertType
  targetPrice?: number
  percentageThreshold?: number
  emailNotification?: boolean
  inAppNotification?: boolean
}

export enum PriceAlertType {
  PRICE_DROP = 'PRICE_DROP',
  PRICE_INCREASE = 'PRICE_INCREASE',
  PERCENTAGE_DROP = 'PERCENTAGE_DROP',
  PERCENTAGE_INCREASE = 'PERCENTAGE_INCREASE'
}

