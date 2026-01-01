/**
 * Exports centralisés pour les modules partagés
 */

// API Services
export { httpClient } from './api/http.client'
export { authService } from './api/auth.service'
export { propertyService } from './api/property.service'
export { userService } from './api/user.service'
export { organizationService } from './api/organization.service'
export type {
  Organization,
  OrganizationCreate,
  OrganizationUpdate,
  OrganizationSearchParams,
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
export type * from './types/user.types'

// Enums from property.types (exported as values, not types)
export { PropertyType, PropertyStatus } from './types/property.types'

// Utils
export { tokenUtils } from './utils/token.utils'

// Constants
export { API_ENDPOINTS, API_BASE_URL } from './constants/api.constants'

// Enums
export { UserStatus, UserRole } from './types/user.types'

