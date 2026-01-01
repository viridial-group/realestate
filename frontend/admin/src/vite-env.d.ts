/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_API_BASE_URL?: string
  readonly VITE_IDENTITY_SERVICE_URL?: string
  readonly VITE_PROPERTY_SERVICE_URL?: string
  readonly VITE_RESOURCE_SERVICE_URL?: string
  readonly VITE_DOCUMENT_SERVICE_URL?: string
  readonly VITE_WORKFLOW_SERVICE_URL?: string
  readonly VITE_NOTIFICATION_SERVICE_URL?: string
  readonly VITE_EMAILING_SERVICE_URL?: string
  readonly VITE_AUDIT_SERVICE_URL?: string
  readonly VITE_BILLING_SERVICE_URL?: string
  readonly VITE_ENV?: string
  readonly VITE_APP_NAME?: string
  readonly VITE_APP_VERSION?: string
  readonly DEV?: boolean
  readonly PROD?: boolean
  readonly MODE?: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}

