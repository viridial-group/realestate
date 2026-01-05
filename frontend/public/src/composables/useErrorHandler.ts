import { ref } from 'vue'
import { useToast } from './useToast'

export interface ErrorInfo {
  message: string
  code?: string | number
  details?: any
  timestamp: Date
}

const errors = ref<ErrorInfo[]>([])
const { showToast } = useToast()

/**
 * Composable pour gérer les erreurs de manière centralisée
 */
export function useErrorHandler() {
  /**
   * Gère une erreur et affiche un message approprié
   */
  function handleError(error: any, context?: string): ErrorInfo {
    const errorInfo: ErrorInfo = {
      message: getErrorMessage(error),
      code: getErrorCode(error),
      details: error,
      timestamp: new Date(),
    }

    // Ajouter à l'historique des erreurs (limité à 10)
    errors.value.unshift(errorInfo)
    if (errors.value.length > 10) {
      errors.value = errors.value.slice(0, 10)
    }

    // Afficher un toast selon le type d'erreur
    const toastMessage = context 
      ? `${context}: ${errorInfo.message}`
      : errorInfo.message

    if (isNetworkError(error)) {
      showToast('Problème de connexion. Vérifiez votre connexion internet.', 'error', 5000)
    } else if (isAuthError(error)) {
      showToast('Session expirée. Veuillez vous reconnecter.', 'warning', 5000)
    } else if (isServerError(error)) {
      showToast('Erreur serveur. Veuillez réessayer plus tard.', 'error', 5000)
    } else {
      showToast(toastMessage, 'error', 4000)
    }

    // Logger l'erreur en développement
    if (import.meta.env.DEV) {
      console.error(`[ErrorHandler${context ? ` - ${context}` : ''}]`, errorInfo)
    }

    return errorInfo
  }

  /**
   * Extrait un message d'erreur lisible depuis une erreur
   */
  function getErrorMessage(error: any): string {
    if (!error) {
      return 'Une erreur inconnue est survenue'
    }

    // Erreur axios avec réponse
    if (error.response?.data) {
      const data = error.response.data
      
      // Message d'erreur personnalisé de l'API
      if (data.message) {
        return data.message
      }
      
      // Validation errors
      if (data.errors && Array.isArray(data.errors)) {
        return data.errors.map((e: any) => e.message || e.defaultMessage).join(', ')
      }
      
      // Erreur générique avec status
      return `Erreur ${error.response.status}: ${data.error || 'Erreur serveur'}`
    }

    // Erreur réseau
    if (error.request && !error.response) {
      return 'Impossible de contacter le serveur. Vérifiez votre connexion internet.'
    }

    // Message d'erreur standard
    if (error.message) {
      return error.message
    }

    // Fallback
    return 'Une erreur inconnue est survenue'
  }

  /**
   * Extrait le code d'erreur
   */
  function getErrorCode(error: any): string | number | undefined {
    if (error.response?.status) {
      return error.response.status
    }
    if (error.code) {
      return error.code
    }
    return undefined
  }

  /**
   * Vérifie si c'est une erreur réseau
   */
  function isNetworkError(error: any): boolean {
    return (
      error.code === 'NETWORK_ERROR' ||
      error.code === 'ECONNABORTED' ||
      error.message?.includes('Network Error') ||
      (error.request && !error.response)
    )
  }

  /**
   * Vérifie si c'est une erreur d'authentification
   */
  function isAuthError(error: any): boolean {
    return (
      error.response?.status === 401 ||
      error.response?.status === 403 ||
      error.message?.includes('Unauthorized') ||
      error.message?.includes('Forbidden')
    )
  }

  /**
   * Vérifie si c'est une erreur serveur
   */
  function isServerError(error: any): boolean {
    const status = error.response?.status
    return status >= 500 && status < 600
  }

  /**
   * Vérifie si c'est une erreur client (4xx)
   */
  function isClientError(error: any): boolean {
    const status = error.response?.status
    return status >= 400 && status < 500
  }

  /**
   * Nettoie l'historique des erreurs
   */
  function clearErrors() {
    errors.value = []
  }

  /**
   * Retourne les erreurs récentes
   */
  function getRecentErrors(limit: number = 5): ErrorInfo[] {
    return errors.value.slice(0, limit)
  }

  return {
    errors,
    handleError,
    getErrorMessage,
    getErrorCode,
    isNetworkError,
    isAuthError,
    isServerError,
    isClientError,
    clearErrors,
    getRecentErrors,
  }
}

