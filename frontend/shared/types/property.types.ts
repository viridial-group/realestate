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
}

export interface PropertyCreate {
  title: string
  description: string
  price: number
  address: string
  city: string
  country: string
  propertyType: PropertyType
  bedrooms?: number
  bathrooms?: number
  area?: number
  images?: string[]
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

