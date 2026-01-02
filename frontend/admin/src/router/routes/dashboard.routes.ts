import type { RouteRecordRaw } from 'vue-router'

export const dashboardRoutes: RouteRecordRaw[] = [
  {
    path: '',
    name: 'home',
    component: () => import('@/views/Dashboard.vue'),
    meta: { 
      requiresAuth: true,
      title: 'Dashboard'
    }
  }
]

