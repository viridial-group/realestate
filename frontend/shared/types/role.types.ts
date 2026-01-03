/**
 * Types pour la gestion des rôles et permissions
 */

export interface Role {
  id: number
  name: string
  description?: string
  isSystem?: boolean
  permissions: Permission[]
  userCount?: number
  createdAt: string
  updatedAt: string
}

export interface RoleCreate {
  name: string
  description?: string
  permissionIds?: number[]
}

export interface RoleUpdate {
  name?: string
  description?: string
  permissionIds?: number[]
}

export interface Permission {
  id: number
  name: string
  resource: string
  action: string
  description?: string
  createdAt: string
  updatedAt: string
}

export interface RolePermission {
  roleId: number
  permissionId: number
}

export interface UserRoleAssignment {
  userId: number
  roleIds: number[]
  organizationId?: number
}

export interface RoleSearchParams {
  search?: string
  isSystem?: boolean
  page?: number
  size?: number
  sort?: string
}

export interface RoleStats {
  totalRoles: number
  systemRoles: number
  customRoles: number
  rolesByPermission: Record<string, number>
}

