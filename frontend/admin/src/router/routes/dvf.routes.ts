import type { RouteRecordRaw } from 'vue-router'

export const dvfRoutes: RouteRecordRaw[] = [
  {
    path: '/dvf',
    name: 'dvf',
    component: () => import('@/views/dvf/Index.vue'),
    meta: {
      title: 'Gestion DVF',
      requiresAuth: true,
      requiresRole: ['ADMIN', 'SUPER_ADMIN']
    }
  }
]

