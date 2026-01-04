/**
 * Types pour l'authentification
 */

export interface LoginRequest {
  email: string
  password: string
}

export interface LoginResponse {
  accessToken?: string
  token?: string // Alias pour accessToken (pour compatibilité)
  refreshToken?: string
  tokenType?: string
  expiresIn?: number
  user?: UserInfo // Optionnel car l'API ne le retourne pas toujours
}

export interface SignupRequest {
  name: string
  email: string
  password: string
  confirmPassword?: string
}

export interface SignupResponse {
  message: string
  user?: UserInfo
}

export interface ForgotPasswordRequest {
  email: string
}

export interface ResetPasswordRequest {
  token: string
  password: string
  confirmPassword: string
}

export interface SubscribeRequest {
  firstName: string
  lastName: string
  email: string
  password: string
  organizationName: string
  planId: number
  phone?: string
}

export interface SubscribeResponse {
  auth: LoginResponse
  organizationId: number
  subscriptionId: number
  organizationName: string
  planName: string
}

export interface UserInfo {
  id: number
  email: string
  name: string
  roles?: string[]
  organizationId?: number
  organizationName?: string
}

