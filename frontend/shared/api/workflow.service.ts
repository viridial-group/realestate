import { httpClient } from './http.client'
import { API_ENDPOINTS } from '../constants/api.constants'
import type {
  Workflow,
  WorkflowCreate,
  WorkflowUpdate,
  WorkflowSearchParams,
  Task,
  TaskCreate,
  TaskUpdate,
  TaskSearchParams,
  TaskApproveParams,
  TaskRejectParams
} from '../types/workflow.types'

export const workflowService = {
  /**
   * Récupère tous les workflows avec filtres optionnels
   */
  async getAll(params?: WorkflowSearchParams): Promise<Workflow[]> {
    const response = await httpClient.get<Workflow[]>(
      API_ENDPOINTS.WORKFLOWS.BASE,
      { params }
    )
    return response.data
  },

  /**
   * Récupère un workflow par ID
   */
  async getById(id: number): Promise<Workflow> {
    const response = await httpClient.get<Workflow>(API_ENDPOINTS.WORKFLOWS.BY_ID(id))
    return response.data
  },

  /**
   * Récupère le workflow par défaut pour une organisation et une action
   */
  async getDefault(organizationId: number, action: string): Promise<Workflow> {
    const response = await httpClient.get<Workflow>(
      API_ENDPOINTS.WORKFLOWS.DEFAULT,
      {
        params: { organizationId, action }
      }
    )
    return response.data
  },

  /**
   * Crée un nouveau workflow
   */
  async create(workflow: WorkflowCreate): Promise<Workflow> {
    const response = await httpClient.post<Workflow>(
      API_ENDPOINTS.WORKFLOWS.BASE,
      workflow
    )
    return response.data
  },

  /**
   * Met à jour un workflow
   */
  async update(id: number, workflow: WorkflowUpdate): Promise<Workflow> {
    const response = await httpClient.put<Workflow>(
      API_ENDPOINTS.WORKFLOWS.BY_ID(id),
      workflow
    )
    return response.data
  },

  /**
   * Démarre un workflow pour une cible
   */
  async start(id: number, targetId: number): Promise<Workflow> {
    const response = await httpClient.post<Workflow>(
      API_ENDPOINTS.WORKFLOWS.START(id),
      null,
      {
        params: { targetId }
      }
    )
    return response.data
  },

  /**
   * Supprime un workflow
   */
  async delete(id: number): Promise<void> {
    await httpClient.delete(API_ENDPOINTS.WORKFLOWS.BY_ID(id))
  },

  // ===== TASKS =====

  /**
   * Récupère toutes les tâches avec filtres optionnels
   */
  async getTasks(params?: TaskSearchParams): Promise<Task[]> {
    const response = await httpClient.get<Task[]>(
      API_ENDPOINTS.WORKFLOWS.TASKS.BASE,
      { params }
    )
    return response.data
  },

  /**
   * Récupère une tâche par ID
   */
  async getTaskById(id: number): Promise<Task> {
    const response = await httpClient.get<Task>(API_ENDPOINTS.WORKFLOWS.TASKS.BY_ID(id))
    return response.data
  },

  /**
   * Crée une nouvelle tâche
   */
  async createTask(task: TaskCreate): Promise<Task> {
    const response = await httpClient.post<Task>(
      API_ENDPOINTS.WORKFLOWS.TASKS.BASE,
      task
    )
    return response.data
  },

  /**
   * Met à jour une tâche
   */
  async updateTask(id: number, task: TaskUpdate): Promise<Task> {
    const response = await httpClient.put<Task>(
      API_ENDPOINTS.WORKFLOWS.TASKS.BY_ID(id),
      task
    )
    return response.data
  },

  /**
   * Approuve une tâche
   */
  async approveTask(id: number, params: TaskApproveParams): Promise<Task> {
    const response = await httpClient.post<Task>(
      API_ENDPOINTS.WORKFLOWS.TASKS.APPROVE(id),
      params
    )
    return response.data
  },

  /**
   * Rejette une tâche
   */
  async rejectTask(id: number, params: TaskRejectParams): Promise<Task> {
    const response = await httpClient.post<Task>(
      API_ENDPOINTS.WORKFLOWS.TASKS.REJECT(id),
      params
    )
    return response.data
  },

  /**
   * Supprime une tâche
   */
  async deleteTask(id: number): Promise<void> {
    await httpClient.delete(API_ENDPOINTS.WORKFLOWS.TASKS.BY_ID(id))
  }
}

