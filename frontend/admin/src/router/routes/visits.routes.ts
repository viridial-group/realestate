import type { RouteRecordRaw } from 'vue-router'

export const visitRoutes: RouteRecordRaw[] = [
  {
    path: 'visits',
    name: 'visits',
    component: () => import('@/views/visits/Index.vue'),
    meta: {
      requiresAuth: true,
      title: 'Gestion des Visites',
      requiresAdmin: true
    }
  }
]

