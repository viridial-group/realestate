/**
 * Constantes pour les endpoints API
 */

export const API_ENDPOINTS = {
  // Authentication
  AUTH: {
    LOGIN: '/api/identity/auth/login',
    REGISTER: '/api/identity/auth/register',
    SUBSCRIBE: '/api/identity/auth/subscribe',
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
    STATS: '/api/properties/stats/dashboard',
    UNREAD_MESSAGES_COUNT: '/api/properties/unread-messages-count',
    PRICE_HISTORY: (id: number) => `/api/properties/${id}/price-history`,
    PRICE_HISTORY_STATS: (id: number) => `/api/properties/${id}/price-history/stats`,
    PRICE_HISTORY_DELETE: (id: number) => `/api/properties/price-history/${id}`
  },
  // Price Alerts
  PRICE_ALERTS: {
    BASE: '/api/price-alerts',
    BY_ID: (id: number) => `/api/price-alerts/${id}`,
    MY_ALERTS: '/api/price-alerts/my-alerts',
    BY_PROPERTY: (propertyId: number) => `/api/price-alerts/property/${propertyId}`,
    DEACTIVATE: (id: number) => `/api/price-alerts/${id}/deactivate`
  },
  // Visit Appointments
  VISITS: {
    BASE: '/api/visits',
    BY_ID: (id: number) => `/api/visits/${id}`,
    BY_PROPERTY: (propertyId: number) => `/api/visits/property/${propertyId}`,
    BY_PROPERTY_UPCOMING: (propertyId: number) => `/api/visits/property/${propertyId}/upcoming`,
    MY_VISITS: '/api/visits/my-visits',
    BY_AGENT: (agentId: number) => `/api/visits/agent/${agentId}`,
    BY_AGENT_UPCOMING: (agentId: number) => `/api/visits/agent/${agentId}/upcoming`,
    CHECK_AVAILABILITY: (agentId: number) => `/api/visits/agent/${agentId}/availability`,
    CONFIRM: (id: number) => `/api/visits/${id}/confirm`,
    CANCEL: (id: number) => `/api/visits/${id}/cancel`,
    COMPLETE: (id: number) => `/api/visits/${id}/complete`,
    EXCHANGE: (id: number) => `/api/visits/${id}/exchange`
  },
  // Market Data (DVF)
  MARKET_DATA: {
    BY_POSTAL_CODE: (codePostal: string) => `/api/market-data/postal-code/${codePostal}`,
    BY_PROPERTY: (propertyId: number) => `/api/market-data/property/${propertyId}`,
    SIMILAR_TRANSACTIONS: (propertyId: number) => `/api/market-data/property/${propertyId}/similar`
  },
  // Users
  USERS: {
    BASE: '/api/identity/users',
    BY_ID: (id: number) => `/api/identity/users/${id}`,
    PROFILE: '/api/identity/users/me', // Endpoint backend: /me
    UPDATE_PROFILE: '/api/identity/users/me',
    UPDATE_PREFERENCES: '/api/identity/users/me/preferences',
    STATS: '/api/identity/users/stats',
    SEARCH: '/api/identity/users/search',
    ACTIVITY: (id: number) => `/api/identity/users/${id}/activity`
  },
  // Roles & Permissions
  ROLES: {
    BASE: '/api/identity/roles',
    BY_ID: (id: number) => `/api/identity/roles/${id}`,
    STATS: '/api/identity/roles/stats',
    PERMISSIONS: '/api/identity/permissions',
    ASSIGN_TO_USER: '/api/identity/users/assign-roles'
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
  },
  // Audit
  AUDIT: {
    BASE: '/api/audit',
    BY_ID: (id: number) => `/api/audit/${id}`,
    BY_TARGET: '/api/audit/target'
  },
  // Workflows
  WORKFLOWS: {
    BASE: '/api/workflows',
    BY_ID: (id: number) => `/api/workflows/${id}`,
    DEFAULT: '/api/workflows/default',
    START: (id: number) => `/api/workflows/${id}/start`,
    TASKS: {
      BASE: '/api/workflows/tasks',
      BY_ID: (id: number) => `/api/workflows/tasks/${id}`,
      APPROVE: (id: number) => `/api/workflows/tasks/${id}/approve`,
      REJECT: (id: number) => `/api/workflows/tasks/${id}/reject`
    }
  },
  // Advertisements
  ADVERTISEMENTS: {
    BASE: '/api/advertisements',
    BY_ID: (id: number) => `/api/advertisements/${id}`,
    STATUS: (id: number) => `/api/advertisements/${id}/status`,
    STATS: (id: number) => `/api/advertisements/${id}/stats`,
    ANALYTICS: '/api/advertisements/analytics',
    PUBLIC: {
      ACTIVE: '/api/advertisements/public/active',
      IMPRESSION: (id: number) => `/api/advertisements/public/${id}/impression`,
      CLICK: (id: number) => `/api/advertisements/public/${id}/click`
    }
  },
  // Contact Messages
  CONTACT_MESSAGES: {
    BASE: '/api/contact-messages',
    BY_ID: (id: number) => `/api/contact-messages/${id}`,
    BY_PROPERTY: (propertyId: number) => `/api/contact-messages/property/${propertyId}`,
    MARK_READ: (id: number) => `/api/contact-messages/${id}/read`,
    MARK_REPLIED: (id: number) => `/api/contact-messages/${id}/replied`,
    UPDATE_NOTES: (id: number) => `/api/contact-messages/${id}/notes`,
    ARCHIVE: (id: number) => `/api/contact-messages/${id}/archive`,
    NEW_COUNT: '/api/contact-messages/stats/new-count'
  },
  // Reviews
  REVIEWS: {
    BASE: '/api/reviews',
    BY_ID: (id: number) => `/api/reviews/${id}`,
    BY_PROPERTY: (propertyId: number) => `/api/reviews/property/${propertyId}`,
    BY_PROPERTY_ALL: (propertyId: number) => `/api/reviews/property/${propertyId}/all`,
    BY_PROPERTY_STATS: (propertyId: number) => `/api/reviews/property/${propertyId}/stats`,
    BY_USER: (userId: number) => `/api/reviews/user/${userId}`,
    BY_STATUS: (status: string) => `/api/reviews/status/${status}`,
    UPDATE_STATUS: (id: number) => `/api/reviews/${id}/status`,
    MARK_HELPFUL: (id: number) => `/api/reviews/${id}/helpful`
  },
  // Organization Reviews
  ORGANIZATION_REVIEWS: {
    SEARCH: '/api/public/organizations/search',
    BY_ORGANIZATION: (id: number) => `/api/public/organizations/${id}/reviews`,
    STATS: (id: number) => `/api/public/organizations/${id}/reviews/stats`,
    CREATE: (id: number) => `/api/public/organizations/${id}/reviews`,
    MARK_HELPFUL: (id: number) => `/api/public/organizations/reviews/${id}/helpful`
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

