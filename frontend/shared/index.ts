/**
 * Exports centralisés pour les modules partagés
 */

// API Services
export { httpClient } from './api/http.client'
export { authService } from './api/auth.service'
export { propertyService } from './api/property.service'
export { userService } from './api/user.service'
export { organizationService } from './api/organization.service'
export { documentService } from './api/document.service'
export { statsService } from './api/stats.service'
export { billingService } from './api/billing.service'
export { planService } from './api/plan.service'
export { notificationService } from './api/notification.service'
export { auditService } from './api/audit.service'
export type { AuditLog, AuditLogSearchParams, AuditLogPage } from './api/audit.service'
export { workflowService } from './api/workflow.service'
export { roleService } from './api/role.service'
export { contactService } from './api/contact.service'
export type { PaginatedResponse } from './api/contact.service'
export { reviewService } from './api/review.service'
export { priceHistoryService } from './api/price-history.service'
export { priceAlertService } from './api/price-alert.service'
export { visitAppointmentService } from './api/visit-appointment.service'
export { marketDataService } from './api/market-data.service'
export { TaskStatusEnum } from './types/workflow.types'
export type {
  Workflow,
  WorkflowCreate,
  WorkflowUpdate,
  WorkflowSearchParams,
  Task,
  TaskCreate,
  TaskUpdate,
  TaskSearchParams,
  TaskApproveParams,
  TaskRejectParams,
  TaskStatus
} from './types/workflow.types'
export type {
  Organization,
  OrganizationCreate,
  OrganizationUpdate,
  OrganizationSearchParams,
  OrganizationSettings,
  Team,
  TeamCreate,
  TeamUpdate,
  OrganizationUser
} from './api/organization.service'

// Stores
export { useAuthStore } from './stores/auth.store'
export { useUserStore } from './stores/user.store'

// Composables
export { useAuth } from './composables/useAuth'
export { useUser } from './composables/useUser'

// Types
export type * from './types/api.types'
export type * from './types/auth.types'
export type * from './types/property.types'
export type * from './types/property-feature.types'
export type * from './types/user.types'
export type * from './types/document.types'
export type * from './types/stats.types'
export type * from './types/billing.types'
export type * from './types/notification.types'
export type * from './types/workflow.types'
export type * from './types/role.types'
export type * from './types/contact.types'
export type * from './types/review.types'
export type * from './types/price-history.types'
export type * from './types/price-alert.types'
export type * from './types/visit-appointment.types'
export type * from './types/market-data.types'
export type { DVFImportHistory, DVFStats } from './types/market-data.types'

// Enums from property.types (exported as values, not types)
export { PropertyType, PropertyStatus } from './types/property.types'

// Utils
export { tokenUtils } from './utils/token.utils'

// Constants
export { API_ENDPOINTS, API_BASE_URL } from './constants/api.constants'

// Enums
export { UserStatus, UserRole } from './types/user.types'

// Organization Reviews
export type {
  OrganizationReview,
  OrganizationReviewCreate,
  OrganizationReviewStats,
  OrganizationWithReviews,
  OrganizationPerformanceStats,
  RatingDistribution
} from './types/organization-review.types'
export { organizationReviewService } from './api/organization-review.service'

// Organization Contact Messages
export type {
  OrganizationContactMessage,
  OrganizationContactMessageCreate
} from './types/organization-contact.types'
export { organizationContactService } from './api/organization-contact.service'
export type {
  Advertisement,
  AdvertisementCreate,
  AdvertisementUpdate,
  AdvertisementStats,
  AdvertisementSearchParams,
  AdvertisementAnalytics,
  DailyStats,
  WeeklyStats,
  MonthlyStats,
  TypeStats,
  PositionStats,
  TopAdvertisement
} from './types/advertisement.types'
export { advertisementService } from './api/advertisement.service'
export { countryService } from './api/country.service'
export { cityService } from './api/city.service'
export type * from './types/country.types'

