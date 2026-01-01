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
  propertyType: PropertyType
  status: PropertyStatus
  bedrooms?: number
  bathrooms?: number
  area?: number
  images?: string[]
  createdAt: string
  updatedAt: string
  createdBy?: number
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
  minPrice?: number
  maxPrice?: number
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

