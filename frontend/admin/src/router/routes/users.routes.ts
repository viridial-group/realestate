import type { RouteRecordRaw } from 'vue-router'
import { adminGuard } from '../guards/role.guard'

export const userRoutes: RouteRecordRaw[] = [
  {
    path: 'users',
    name: 'users',
    component: () => import('@/views/users/Index.vue'),
    meta: { 
      requiresAuth: true,
      requiresAdmin: true,
      title: 'Gestion des Utilisateurs'
    },
    beforeEnter: adminGuard
  },
  {
    path: 'users/:id',
    name: 'user-detail',
    component: () => import('@/views/users/Detail.vue'),
    meta: { 
      requiresAuth: true,
      requiresAdmin: true,
      title: 'Détails de l\'utilisateur'
    },
    beforeEnter: adminGuard
  },
  {
    path: 'profile',
    name: 'profile',
    component: () => import('@/views/users/Profile.vue'),
    meta: { 
      requiresAuth: true,
      title: 'Mon Profil'
    }
  }
]

