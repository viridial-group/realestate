/**
 * Constantes pour les endpoints API
 */

export const API_ENDPOINTS = {
  // Authentication
  AUTH: {
    LOGIN: '/api/identity/auth/login',
    REGISTER: '/api/identity/auth/register',
    LOGOUT: '/api/identity/auth/logout',
    REFRESH: '/api/identity/auth/refresh',
    FORGOT_PASSWORD: '/api/identity/auth/forgot-password',
    RESET_PASSWORD: '/api/identity/auth/reset-password'
  },
  // Properties
  PROPERTIES: {
    BASE: '/api/properties',
    SEARCH: '/api/properties/search',
    BY_ID: (id: number) => `/api/properties/${id}`,
    FEATURES: (id: number) => `/api/properties/${id}/features`,
    STATS: '/api/properties/stats/dashboard'
  },
  // Users
  USERS: {
    BASE: '/api/identity/users',
    BY_ID: (id: number) => `/api/identity/users/${id}`,
    PROFILE: '/api/identity/users/me', // Endpoint backend: /me
    STATS: '/api/identity/users/stats',
    SEARCH: '/api/identity/users/search',
    ACTIVITY: (id: number) => `/api/identity/users/${id}/activity`
  },
  // Organizations
  ORGANIZATIONS: {
    BASE: '/api/identity/organizations',
    BY_ID: (id: number) => `/api/identity/organizations/${id}`,
    BY_USER_ID: (userId: number) => `/api/identity/organizations/user/${userId}`,
    ROOT: '/api/identity/organizations/root',
    CHILDREN: (parentId: number) => `/api/identity/organizations/${parentId}/children`,
    STATS: '/api/identity/organizations/stats',
    SEARCH: '/api/identity/organizations/search'
  },
  // Documents
  DOCUMENTS: {
    BASE: '/api/documents',
    BY_ID: (id: number) => `/api/documents/${id}`,
    UPLOAD: '/api/documents/upload',
    DOWNLOAD: (id: number) => `/api/documents/${id}/download`
  },
  // Billing
  BILLING: {
    BASE: '/api/billing',
    STATS: '/api/billing/stats',
    SUBSCRIPTIONS: {
      BASE: '/api/billing/subscriptions',
      BY_ID: (id: number) => `/api/billing/subscriptions/${id}`,
      BY_ORGANIZATION: (organizationId: number) => `/api/billing/subscriptions/organization/${organizationId}`,
      ACTIVE_BY_ORGANIZATION: (organizationId: number) => `/api/billing/subscriptions/organization/${organizationId}/active`,
      CANCEL: (id: number) => `/api/billing/subscriptions/${id}/cancel`,
      RENEW: (id: number) => `/api/billing/subscriptions/${id}/renew`,
      EXPIRING: '/api/billing/subscriptions/expiring'
    },
    INVOICES: {
      BASE: '/api/billing/invoices',
      BY_ID: (id: number) => `/api/billing/invoices/${id}`,
      BY_NUMBER: (invoiceNumber: string) => `/api/billing/invoices/number/${invoiceNumber}`,
      BY_SUBSCRIPTION: (subscriptionId: number) => `/api/billing/invoices/subscription/${subscriptionId}`,
      MARK_PAID: (id: number) => `/api/billing/invoices/${id}/mark-paid`,
      UPDATE_STATUS: (id: number) => `/api/billing/invoices/${id}/status`,
      OVERDUE: '/api/billing/invoices/overdue'
    },
    PLANS: {
      BASE: '/api/billing/plans',
      BY_ID: (id: number) => `/api/billing/plans/${id}`,
      BY_NAME: (name: string) => `/api/billing/plans/name/${name}`,
      DEFAULT: '/api/billing/plans/default'
    }
  },
  // Notifications
  NOTIFICATIONS: {
    BASE: '/api/notifications',
    BY_ID: (id: number) => `/api/notifications/${id}`,
    SEND: '/api/notifications/send',
    MARK_READ: (id: number) => `/api/notifications/${id}/read`,
    MARK_ARCHIVED: (id: number) => `/api/notifications/${id}/archive`,
    UNREAD_COUNT: '/api/notifications/unread/count'
  }
} as const

/**
 * Get API base URL from environment
 * Note: This constant is not used by httpClient (which uses empty string)
 * It's kept for backward compatibility and documentation purposes
 * The actual routing works as follows:
 * - Frontend requests: /api/identity/auth/login
 * - Vite proxy: redirects /api to VITE_API_BASE_URL (http://localhost:8080)
 * - Final URL: http://localhost:8080/api/identity/auth/login
 * - Gateway routes: /api/identity/** to identity-service:8081
 */
export const API_BASE_URL = '/api'

