/**
 * Types pour les workflows et tâches
 */

export interface Workflow {
  id: number
  name: string
  description?: string
  action: string
  organizationId: number
  targetType?: string
  targetId?: number
  steps?: string // JSON string
  requiredRoles?: string // JSON string
  active: boolean
  isDefault: boolean
  status?: string // PENDING, IN_PROGRESS, COMPLETED, CANCELLED
  createdAt: string
  updatedAt?: string
}

export interface WorkflowCreate {
  name: string
  description?: string
  action: string
  organizationId: number
  targetType?: string
  targetId?: number
  steps?: string // JSON string
  requiredRoles?: string // JSON string
  active?: boolean
  isDefault?: boolean
}

export interface WorkflowUpdate {
  name?: string
  description?: string
  action?: string
  targetType?: string
  targetId?: number
  steps?: string
  requiredRoles?: string
  active?: boolean
  isDefault?: boolean
  status?: string
}

export interface WorkflowSearchParams {
  organizationId?: number
  action?: string
  targetType?: string
  targetId?: number
}

export enum TaskStatus {
  PENDING = 'PENDING',
  IN_PROGRESS = 'IN_PROGRESS',
  APPROVED = 'APPROVED',
  REJECTED = 'REJECTED',
  CANCELLED = 'CANCELLED'
}

// Export as value for runtime use
export { TaskStatus as TaskStatusEnum }

export interface Task {
  id: number
  workflowId: number
  workflowName?: string
  title: string
  description?: string
  stepNumber: number
  assignedTo?: number
  assignedRole?: string
  status: TaskStatus | string
  dueDate?: string
  completedAt?: string
  completedBy?: number
  comments?: string
  createdAt: string
  updatedAt?: string
}

export interface TaskCreate {
  workflowId: number
  title: string
  description?: string
  stepNumber: number
  assignedTo?: number
  assignedRole?: string
  status?: TaskStatus | string
  dueDate?: string
}

export interface TaskUpdate {
  title?: string
  description?: string
  stepNumber?: number
  assignedTo?: number
  assignedRole?: string
  status?: TaskStatus | string
  dueDate?: string
  comments?: string
}

export interface TaskApproveParams {
  completedBy: number
  comments?: string
}

export interface TaskRejectParams {
  completedBy: number
  comments?: string
}

export interface TaskSearchParams {
  workflowId?: number
  assignedTo?: number
  overdue?: boolean
}

