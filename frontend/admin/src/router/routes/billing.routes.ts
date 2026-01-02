import type { RouteRecordRaw } from 'vue-router'
import { adminGuard } from '../guards/role.guard'

export const billingRoutes: RouteRecordRaw[] = [
  {
    path: 'billing',
    name: 'billing',
    component: () => import('@/views/billing/Index.vue'),
    meta: { 
      requiresAuth: true,
      requiresAdmin: true,
      title: 'Facturation'
    },
    beforeEnter: adminGuard
  }
]

