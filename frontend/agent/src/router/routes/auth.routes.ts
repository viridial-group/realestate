import type { RouteRecordRaw } from 'vue-router'

export const authRoutes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'agent-login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/signup',
    name: 'agent-signup',
    component: () => import('@/views/Signup.vue')
  },
  {
    path: '/forgot-password',
    name: 'agent-forgot-password',
    component: () => import('@/views/ForgotPassword.vue')
  }
]


