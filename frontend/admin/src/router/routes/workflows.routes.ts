import type { RouteRecordRaw } from 'vue-router'

export const workflowRoutes: RouteRecordRaw[] = [
  {
    path: 'workflows',
    name: 'workflows',
    component: () => import('@/views/workflows/Index.vue'),
    meta: { 
      requiresAuth: true,
      title: 'Gestion des Workflows',
      icon: 'Workflow',
      menu: true
    }
  },
  {
    path: 'workflows/:id',
    name: 'workflow-detail',
    component: () => import('@/views/workflows/Detail.vue'),
    meta: { 
      requiresAuth: true,
      title: 'Détails du Workflow',
      menu: false
    }
  }
]

