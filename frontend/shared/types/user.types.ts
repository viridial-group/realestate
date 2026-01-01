/**
 * Types pour la gestion des utilisateurs SaaS
 */

export interface User {
  id: number
  email: string
  name: string
  firstName?: string
  lastName?: string
  phone?: string
  avatar?: string
  status: UserStatus
  roles: UserRole[]
  organizationId?: number
  organizationName?: string
  createdAt: string
  updatedAt: string
  lastLoginAt?: string
  emailVerified: boolean
  metadata?: Record<string, any>
}

export interface UserCreate {
  email: string
  name: string
  firstName?: string
  lastName?: string
  phone?: string
  password: string
  roles?: UserRole[]
  organizationId?: number
  status?: UserStatus
  metadata?: Record<string, any>
}

export interface UserUpdate {
  name?: string
  firstName?: string
  lastName?: string
  phone?: string
  avatar?: string
  status?: UserStatus
  roles?: UserRole[]
  organizationId?: number
  metadata?: Record<string, any>
}

export interface UserSearchParams {
  search?: string
  email?: string
  status?: UserStatus
  role?: UserRole
  organizationId?: number
  page?: number
  size?: number
  sort?: string
}

export interface UserProfile extends User {
  preferences?: UserPreferences
  notifications?: NotificationSettings
}

export interface UserPreferences {
  language: string
  timezone: string
  theme: 'light' | 'dark' | 'auto'
  emailNotifications: boolean
}

export interface NotificationSettings {
  email: boolean
  push: boolean
  sms: boolean
  marketing: boolean
}

export enum UserStatus {
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
  SUSPENDED = 'SUSPENDED',
  PENDING = 'PENDING',
  DELETED = 'DELETED'
}

export enum UserRole {
  SUPER_ADMIN = 'SUPER_ADMIN',
  ADMIN = 'ADMIN',
  AGENT = 'AGENT',
  FREELANCE = 'FREELANCE',
  AUTO_ENTREPRENEUR = 'AUTO_ENTREPRENEUR',
  PARTICULAR = 'PARTICULAR',
  USER = 'USER'
}

export interface UserActivity {
  id: number
  userId: number
  action: string
  description: string
  ipAddress?: string
  userAgent?: string
  createdAt: string
}

export interface UserStats {
  totalUsers: number
  activeUsers: number
  newUsersThisMonth: number
  usersByRole: Record<UserRole, number>
  usersByStatus: Record<UserStatus, number>
}

