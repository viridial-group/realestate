/**
 * Types pour les propriétés
 */

export interface Property {
  id: number
  title: string
  description: string
  price: number
  address: string
  city: string
  country: string
  propertyType?: PropertyType
  type?: string // API returns 'type' field
  status: PropertyStatus
  transactionType?: string // RENT, SALE - Type de transaction
  bedrooms?: number
  bathrooms?: number
  area?: number
  surface?: number // API may return 'surface' instead of 'area'
  images?: string[]
  createdAt: string
  updatedAt: string
  createdBy?: number
  // Additional fields from API
  reference?: string
  postalCode?: string
  latitude?: number
  longitude?: number
  organizationId?: number
  organizationName?: string
  assignedUserId?: number
  assignedUserName?: string
  fullBathrooms?: number
  appliancesIncluded?: string // JSON string
  laundryLocation?: string
  totalStructureArea?: number
  totalInteriorLivableArea?: number
  virtualTourUrl?: string
  parkingFeatures?: string // JSON string
  hasGarage?: boolean
  accessibilityFeatures?: string // JSON string
  patioPorch?: string
  exteriorFeatures?: string // JSON string
  specialConditions?: string
  homeType?: string
  propertySubtype?: string
  condition?: string
  yearBuilt?: number
  subdivision?: string
  hasHOA?: boolean
  hoaAmenities?: string // JSON string
  hoaServices?: string // JSON string
  hoaFee?: number
  hoaFeeFrequency?: string
  region?: string
  pricePerSquareFoot?: number
  dateOnMarket?: string
  currency?: string
  rooms?: number
  metadata?: string // JSON string
  slug?: string // Slug SEO-friendly
  // SEO Meta Tags
  metaTitle?: string // Titre SEO personnalisé
  metaDescription?: string // Description SEO personnalisée
  metaKeywords?: string // Mots-clés SEO
  ogImage?: string // Image pour Open Graph
  // Zillow-inspired fields
  petFriendly?: boolean
  specialOffer?: string
  officeHours?: string // JSON string
  neighborhood?: string
  walkScore?: number
  transitScore?: number
  bikeScore?: number
  buildingName?: string
  flooring?: string // JSON array string
  unitFeatures?: string // JSON array string
  buildingAmenities?: string // JSON array string
  availableUnits?: string // JSON array string
  petPolicy?: string // JSON string
  parkingPolicy?: string
  // Unread messages count (loaded asynchronously)
  unreadMessagesCount?: number
}

export interface PropertyCreate {
  title: string
  description: string
  price: number
  address: string
  city: string
  country: string
  propertyType: PropertyType
  transactionType?: string // RENT, SALE - Type de transaction
  bedrooms?: number
  bathrooms?: number
  area?: number
  images?: string[]
  // Zillow-inspired fields (optional)
  petFriendly?: boolean
  specialOffer?: string
  officeHours?: string
  neighborhood?: string
  walkScore?: number
  transitScore?: number
  bikeScore?: number
  buildingName?: string
  flooring?: string
  unitFeatures?: string
  buildingAmenities?: string
  availableUnits?: string
  petPolicy?: string
  parkingPolicy?: string
}

export interface PropertyUpdate extends Partial<PropertyCreate> {
  status?: PropertyStatus
}

export interface PropertySearchParams {
  city?: string
  country?: string
  propertyType?: PropertyType
  type?: string // API uses 'type' instead of 'propertyType'
  organizationId?: number
  assignedUserId?: number
  teamId?: number
  minPrice?: number
  maxPrice?: number
  minSurface?: number
  maxSurface?: number
  bedrooms?: number
  bathrooms?: number
  status?: PropertyStatus
  search?: string
  page?: number
  size?: number
}

export enum PropertyType {
  APARTMENT = 'APARTMENT',
  HOUSE = 'HOUSE',
  VILLA = 'VILLA',
  LAND = 'LAND',
  COMMERCIAL = 'COMMERCIAL',
  OTHER = 'OTHER'
}

export enum PropertyStatus {
  AVAILABLE = 'AVAILABLE',
  SOLD = 'SOLD',
  RENTED = 'RENTED',
  PENDING = 'PENDING',
  DRAFT = 'DRAFT'
}

