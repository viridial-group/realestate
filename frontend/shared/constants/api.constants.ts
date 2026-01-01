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
    BY_ID: (id: number) => `/api/properties/${id}`
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
    BASE: '/api/organizations',
    BY_ID: (id: number) => `/api/organizations/${id}`
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

