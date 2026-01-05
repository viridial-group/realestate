/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly PROD: boolean
  readonly VITE_ADMIN_URL?: string
  // Add other env variables as needed
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}

