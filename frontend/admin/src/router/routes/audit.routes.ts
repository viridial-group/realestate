import type { RouteRecordRaw } from 'vue-router'
import { adminGuard } from '../guards/role.guard'

export const auditRoutes: RouteRecordRaw[] = [
  {
    path: 'audit',
    name: 'audit',
    component: () => import('@/views/audit/Index.vue'),
    meta: { 
      requiresAuth: true,
      requiresAdmin: true,
      title: 'Audit et Logs'
    },
    beforeEnter: adminGuard
  }
]

