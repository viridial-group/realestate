import type { RouteRecordRaw } from 'vue-router'

export const clientRoutes: RouteRecordRaw[] = [
  {
    path: 'clients',
    name: 'agent-clients',
    component: () => import('@/views/Clients.vue'),
    meta: {
      requiresAuth: true,
      title: 'Clients & Prospects'
    }
  }
]







