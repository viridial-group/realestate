import type { RouteRecordRaw } from 'vue-router'
import { adminGuard } from '../guards/role.guard'

export const notificationRoutes: RouteRecordRaw[] = [
  {
    path: 'notifications',
    name: 'notifications',
    component: () => import('@/views/notifications/Index.vue'),
    meta: { 
      requiresAuth: true,
      requiresAdmin: true,
      title: 'Notifications'
    },
    beforeEnter: adminGuard
  }
]

