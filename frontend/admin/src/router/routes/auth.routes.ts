import type { RouteRecordRaw } from 'vue-router'
import { guestGuard } from '../guards/auth.guard'

export const authRoutes: RouteRecordRaw[] = [
  {
    path: 'login',
    name: 'login',
    component: () => import('@/views/Login.vue'),
    meta: { 
      title: 'Connexion',
      layout: 'auth'
    },
    beforeEnter: guestGuard
  },
  {
    path: 'signup',
    name: 'signup',
    component: () => import('@/views/Signup.vue'),
    meta: { 
      title: 'Inscription',
      layout: 'auth'
    },
    beforeEnter: guestGuard
  },
  {
    path: 'forgot-password',
    name: 'forgot-password',
    component: () => import('@/views/ForgotPassword.vue'),
    meta: { 
      title: 'Mot de passe oublié',
      layout: 'auth'
    },
    beforeEnter: guestGuard
  }
]

