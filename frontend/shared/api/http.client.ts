import axios, { type AxiosInstance, type AxiosRequestConfig, type AxiosResponse } from 'axios'
import type { ApiResponse, ApiError } from '../types/api.types'
import { tokenUtils } from '../utils/token.utils'

/**
 * Get API base URL from environment or use default
 * Note: In the shared package, we use empty string as default
 * The endpoints already include '/api' prefix for Vite proxy
 * The proxy in vite.config.ts redirects /api to VITE_API_BASE_URL
 */
const getApiBaseUrl = (): string => {
  // Always use empty string - endpoints already include /api prefix
  // The Vite proxy will handle redirecting /api to the gateway
  // Example: /api/identity/auth/login -> proxy -> http://localhost:8080/api/identity/auth/login
  return ''
}

/**
 * Client HTTP configuré avec intercepteurs pour l'authentification
 */
class HttpClient {
  private client: AxiosInstance

  constructor(baseURL?: string) {
    const apiBaseUrl = baseURL || getApiBaseUrl()
    
    this.client = axios.create({
      baseURL: apiBaseUrl,
      timeout: 30000,
      headers: {
        'Content-Type': 'application/json'
      }
    })

    this.setupInterceptors()
  }

  private setupInterceptors(): void {
    // Request interceptor - Ajouter le token
    this.client.interceptors.request.use(
      (config) => {
        const token = tokenUtils.getToken()
        if (token) {
          config.headers.Authorization = `Bearer ${token}`
        }
        return config
      },
      (error) => {
        return Promise.reject(error)
      }
    )

    // Response interceptor - Gérer les erreurs
    this.client.interceptors.response.use(
      (response: AxiosResponse) => {
        return response
      },
      async (error) => {
        const status = error.response?.status
        
        if (status === 401) {
          // Token expiré ou invalide
          const token = tokenUtils.getToken()
          if (token) {
            // Ne pas supprimer le token immédiatement - laisser le guard gérer
            // Le guard vérifiera et supprimera le token si nécessaire
            // Ne rediriger que si on n'est pas déjà sur la page de login
            // et seulement si ce n'est pas une requête de vérification d'auth
            const isAuthCheck = error.config?.url?.includes('/users/me') || 
                               error.config?.url?.includes('/profile')
            if (!isAuthCheck && typeof window !== 'undefined' && !window.location.pathname.includes('/login')) {
              // Utiliser un petit délai pour éviter les redirections multiples
              setTimeout(() => {
                // Ne pas supprimer le token ici - laisser le guard le faire
                // window.location.href = '/login'
              }, 100)
            }
          }
        } else if (status === 403) {
          // Accès refusé - ne pas rediriger automatiquement
          // Laisser les composants gérer cette erreur
          console.warn('Access forbidden:', error.response?.data)
        }
        
        return Promise.reject(this.handleError(error))
      }
    )
  }

  private handleError(error: any): ApiError {
    if (error.response) {
      return {
        message: error.response.data?.message || 'Une erreur est survenue',
        status: error.response.status,
        data: error.response.data
      }
    }
    return {
      message: error.message || 'Erreur de connexion',
      status: 0
    }
  }

  async get<T>(url: string, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
    const response = await this.client.get<T>(url, config)
    return {
      data: response.data,
      status: response.status,
      message: 'Success'
    }
  }

  async post<T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
    const response = await this.client.post<T>(url, data, config)
    return {
      data: response.data,
      status: response.status,
      message: 'Success'
    }
  }

  async put<T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
    const response = await this.client.put<T>(url, data, config)
    return {
      data: response.data,
      status: response.status,
      message: 'Success'
    }
  }

  async patch<T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
    const response = await this.client.patch<T>(url, data, config)
    return {
      data: response.data,
      status: response.status,
      message: 'Success'
    }
  }

  async delete<T>(url: string, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
    const response = await this.client.delete<T>(url, config)
    return {
      data: response.data,
      status: response.status,
      message: 'Success'
    }
  }
}

export const httpClient = new HttpClient()

