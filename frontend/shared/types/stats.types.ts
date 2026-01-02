/**
 * Types pour les statistiques du dashboard
 */

export interface DashboardStats {
  propertyStats: PropertyStats
  userStats: DashboardUserStats
  organizationStats: OrganizationStats
}

export interface PropertyStats {
  total: number
  available: number
  sold: number
  rented: number
  published: number
  draft: number
  pending: number
  averagePrice: number
  averageSurface: number
  newThisMonth: number
  newThisWeek: number
  byType: Record<string, number>
  byStatus: Record<string, number>
  byCity: Record<string, number>
}

export interface DashboardUserStats {
  total: number
  active: number
  inactive: number
  newThisMonth: number
  newThisWeek: number
}

export interface OrganizationStats {
  total: number
  active: number
  newThisMonth: number
  newThisWeek: number
}

