import type { RouteRecordRaw } from 'vue-router'

export const reviewRoutes: RouteRecordRaw[] = [
  {
    path: 'reviews',
    name: 'reviews',
    component: () => import('@/views/reviews/Index.vue'),
    meta: { 
      requiresAuth: true,
      title: 'Modération des Avis'
    }
  }
]

