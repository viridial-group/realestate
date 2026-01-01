/**
 * Types génériques pour les réponses API
 */

export interface ApiResponse<T = any> {
  data: T
  status: number
  message?: string
}

export interface ApiError {
  message: string
  status: number
  data?: any
}

export interface PaginatedResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
}

export interface ApiListResponse<T> {
  items: T[]
  total: number
  page: number
  pageSize: number
}

