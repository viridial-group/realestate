/**
 * Types pour les pays et villes (SaaS Configuration)
 */

export interface Country {
  id: number
  code: string // Code ISO 3166-1 alpha-2 (ex: FR, US)
  name: string
  code3?: string // Code ISO 3166-1 alpha-3 (ex: FRA, USA)
  phoneCode?: string // Code téléphonique (ex: +33, +1)
  currency?: string // Code devise (ex: EUR, USD)
  currencySymbol?: string // Symbole devise (ex: €, $)
  timezone?: string // Fuseau horaire (ex: Europe/Paris)
  flagEmoji?: string // Emoji du drapeau (ex: 🇫🇷)
  description?: string
  latitude?: number
  longitude?: number
  importantData?: string // JSON string
  active: boolean
  displayOrder: number
  cities?: City[] // Liste des villes (optionnel)
  createdAt?: string
  updatedAt?: string
}

export interface City {
  id: number
  name: string
  postalCode?: string
  countryId: number
  countryCode?: string // Code du pays (ex: FR, US)
  countryName?: string // Nom du pays
  latitude?: number
  longitude?: number
  region?: string // Région/État
  department?: string // Département (pour la France)
  timezone?: string // Fuseau horaire
  description?: string
  importantData?: string // JSON string
  active: boolean
  displayOrder: number
  createdAt?: string
  updatedAt?: string
}

export interface CountryCreate {
  code: string
  name: string
  code3?: string
  phoneCode?: string
  currency?: string
  currencySymbol?: string
  timezone?: string
  flagEmoji?: string
  description?: string
  latitude?: number
  longitude?: number
  importantData?: string
  active?: boolean
  displayOrder?: number
}

export interface CountryUpdate extends Partial<CountryCreate> {
  id: number
}

export interface CityCreate {
  name: string
  postalCode?: string
  countryId: number
  latitude?: number
  longitude?: number
  region?: string
  department?: string
  timezone?: string
  description?: string
  importantData?: string
  active?: boolean
  displayOrder?: number
}

export interface CityUpdate extends Partial<CityCreate> {
  id: number
}

