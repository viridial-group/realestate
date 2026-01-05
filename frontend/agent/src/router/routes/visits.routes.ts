import type { RouteRecordRaw } from 'vue-router'

export const visitRoutes: RouteRecordRaw[] = [
  {
    path: 'visits',
    name: 'agent-visits',
    component: () => import('@/views/Visits.vue'),
    meta: {
      requiresAuth: true,
      title: 'Visites'
    }
  }
]







