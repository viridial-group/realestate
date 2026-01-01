/**
 * Configuration des variables d'environnement
 */

// Type-safe access to environment variables
const getEnv = (key: string, defaultValue: string = ''): string => {
  return (import.meta.env as Record<string, string>)[key] || defaultValue
}

export const env = {
  // API Configuration
  apiBaseUrl: getEnv('VITE_API_BASE_URL', 'http://localhost:8080'),
  
  // Service URLs
  identityServiceUrl: getEnv('VITE_IDENTITY_SERVICE_URL', 'http://localhost:8081'),
  propertyServiceUrl: getEnv('VITE_PROPERTY_SERVICE_URL', 'http://localhost:8083'),
  resourceServiceUrl: getEnv('VITE_RESOURCE_SERVICE_URL', 'http://localhost:8084'),
  documentServiceUrl: getEnv('VITE_DOCUMENT_SERVICE_URL', 'http://localhost:8085'),
  workflowServiceUrl: getEnv('VITE_WORKFLOW_SERVICE_URL', 'http://localhost:8086'),
  notificationServiceUrl: getEnv('VITE_NOTIFICATION_SERVICE_URL', 'http://localhost:8087'),
  emailingServiceUrl: getEnv('VITE_EMAILING_SERVICE_URL', 'http://localhost:8088'),
  auditServiceUrl: getEnv('VITE_AUDIT_SERVICE_URL', 'http://localhost:8089'),
  billingServiceUrl: getEnv('VITE_BILLING_SERVICE_URL', 'http://localhost:8090'),
  
  // Environment
  env: getEnv('VITE_ENV', 'development'),
  isDevelopment: (import.meta.env as any).DEV ?? true,
  isProduction: (import.meta.env as any).PROD ?? false,
  
  // App Configuration
  appName: getEnv('VITE_APP_NAME', 'Real Estate Admin'),
  appVersion: getEnv('VITE_APP_VERSION', '1.0.0')
} as const

// Validation des variables d'environnement requises
if (!env.apiBaseUrl) {
  console.warn('⚠️ VITE_API_BASE_URL is not set. Using default: http://localhost:8080')
}

export default env

