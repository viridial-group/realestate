import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'home',
    component: () => import('@/views/Home.vue')
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/signup',
    name: 'signup',
    component: () => import('@/views/Signup.vue')
  },
  {
    path: '/forgot-password',
    name: 'forgot-password',
    component: () => import('@/views/ForgotPassword.vue')
  },
  {
    path: '/properties',
    name: 'properties',
    component: () => import('@/views/Properties.vue')
  },
  {
    path: '/properties/:id',
    name: 'property-detail',
    component: () => import('@/views/PropertyDetail.vue')
  },
  {
    path: '/publish',
    name: 'publish',
    component: () => import('@/views/PublishProperty.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router

