import type { RouteRecordRaw } from 'vue-router'
import { adminGuard } from '../guards/role.guard'

export const roleRoutes: RouteRecordRaw[] = [
  {
    path: 'roles',
    name: 'roles',
    component: () => import('@/views/roles/Index.vue'),
    meta: { 
      requiresAuth: true,
      requiresAdmin: true,
      title: 'Gestion des Rôles'
    },
    beforeEnter: adminGuard
  },
  {
    path: 'roles/new',
    name: 'role-create',
    component: () => import('@/views/roles/CreateEdit.vue'),
    meta: { 
      requiresAuth: true,
      requiresAdmin: true,
      title: 'Créer un Rôle'
    },
    beforeEnter: adminGuard
  },
  {
    path: 'roles/:id/edit',
    name: 'role-edit',
    component: () => import('@/views/roles/CreateEdit.vue'),
    meta: { 
      requiresAuth: true,
      requiresAdmin: true,
      title: 'Modifier un Rôle'
    },
    beforeEnter: adminGuard
  },
  {
    path: 'roles/assign',
    name: 'role-assign',
    component: () => import('@/views/roles/AssignRoles.vue'),
    meta: { 
      requiresAuth: true,
      requiresAdmin: true,
      title: 'Affecter des Rôles'
    },
    beforeEnter: adminGuard
  }
]

