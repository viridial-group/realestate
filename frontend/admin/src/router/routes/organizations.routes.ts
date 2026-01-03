import type { RouteRecordRaw } from 'vue-router'
import { adminGuard } from '../guards/role.guard'

export const organizationRoutes: RouteRecordRaw[] = [
  {
    path: 'organizations',
    name: 'organizations',
    component: () => import('@/views/organizations/Index.vue'),
    meta: { 
      requiresAuth: true,
      requiresAdmin: true,
      title: 'Gestion des Organisations'
    },
    beforeEnter: adminGuard
  },
  {
    path: 'organizations/:id',
    name: 'organization-detail',
    component: () => import('@/views/organizations/Detail.vue'),
    meta: { 
      requiresAuth: true,
      requiresAdmin: true,
      title: 'Détails de l\'organisation'
    },
    beforeEnter: adminGuard
  },
  {
    path: 'organizations/:id/settings',
    name: 'organization-settings',
    component: () => import('@/views/organizations/Settings.vue'),
    meta: { 
      requiresAuth: true,
      requiresAdmin: true,
      title: 'Paramètres de l\'organisation'
    },
    beforeEnter: adminGuard
  }
]

