/**
 * Types pour les données de marché immobilier (DVF)
 */

export interface MarketData {
  location: string
  propertyType: string
  averagePricePerSquareMeter?: number
  medianPricePerSquareMeter?: number
  minPricePerSquareMeter?: number
  maxPricePerSquareMeter?: number
  transactionCount?: number
  periodStart: string
  periodEnd: string
  priceEvolution?: PriceEvolution[]
  comparison?: PropertyComparison
}

export interface PriceEvolution {
  period: string
  averagePrice?: number
  count?: number
}

export interface PropertyComparison {
  propertyPricePerSquareMeter?: number
  priceDifferencePercent?: number
  priceEvaluation?: 'SURESTIMÉ' | 'SOUS-ESTIMÉ' | 'CORRECT'
  recommendation?: string
}

export interface DVFTransaction {
  id: number
  mutationDate: string
  natureMutation?: string
  valeurFonciere?: number
  typeLocal?: string
  surfaceReelleBati?: number
  nombrePiecesPrincipales?: number
  codePostal?: string
  commune?: string
  prixMetreCarre?: number
  latitude?: number
  longitude?: number
}

export interface DVFImportHistory {
  id: number
  year: string
  department: string
  status: string
  transactionCount?: number
  errorMessage?: string
  startedBy?: number
  createdAt: string
  updatedAt: string
  completedAt?: string
}

export interface DVFStats {
  totalTransactions?: number
  coveredDepartments?: number
  availableYears?: string[]
  averagePricePerSquareMeter?: number
  medianPricePerSquareMeter?: number
  lastUpdate?: string
  completedImports?: number
  inProgressImports?: number
}

