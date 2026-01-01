import { httpClient } from './http.client'
import { API_ENDPOINTS } from '../constants/api.constants'
import type { LoginRequest, LoginResponse, SignupRequest, SignupResponse, ForgotPasswordRequest, ResetPasswordRequest } from '../types/auth.types'
import { tokenUtils } from '../utils/token.utils'

/**
 * Service d'authentification
 */
export const authService = {
  /**
   * Connexion
   */
  async login(credentials: LoginRequest): Promise<LoginResponse> {
    const response = await httpClient.post<LoginResponse>(API_ENDPOINTS.AUTH.LOGIN, credentials)
    // L'API retourne accessToken ou token
    const token = response.data?.accessToken || response.data?.token
    if (token) {
      tokenUtils.setToken(token)
    }
    // Stocker aussi le refreshToken si présent
    if (response.data?.refreshToken) {
      tokenUtils.setRefreshToken(response.data.refreshToken)
    }
    // Normaliser la réponse pour avoir toujours 'token'
    return {
      ...response.data,
      token: token || response.data?.token
    }
  },

  /**
   * Inscription
   */
  async signup(data: SignupRequest): Promise<SignupResponse> {
    const response = await httpClient.post<SignupResponse>(API_ENDPOINTS.AUTH.REGISTER, data)
    return response.data
  },

  /**
   * Déconnexion
   */
  async logout(): Promise<void> {
    try {
      await httpClient.post(API_ENDPOINTS.AUTH.LOGOUT)
    } finally {
      tokenUtils.removeToken()
    }
  },

  /**
   * Demande de réinitialisation de mot de passe
   */
  async forgotPassword(data: ForgotPasswordRequest): Promise<void> {
    await httpClient.post(API_ENDPOINTS.AUTH.FORGOT_PASSWORD, data)
  },

  /**
   * Réinitialisation de mot de passe
   */
  async resetPassword(data: ResetPasswordRequest): Promise<void> {
    await httpClient.post(API_ENDPOINTS.AUTH.RESET_PASSWORD, data)
  },

  /**
   * Rafraîchir le token
   */
  async refreshToken(): Promise<LoginResponse> {
    const response = await httpClient.post<LoginResponse>(API_ENDPOINTS.AUTH.REFRESH)
    if (response.data?.token) {
      tokenUtils.setToken(response.data.token)
    }
    return response.data
  },

  /**
   * Vérifier si l'utilisateur est authentifié
   */
  isAuthenticated(): boolean {
    return tokenUtils.hasToken()
  }
}

