import type { RouteRecordRaw } from 'vue-router'

export const advertisementRoutes: RouteRecordRaw[] = [
  {
    path: 'advertisements',
    name: 'advertisements',
    component: () => import('@/views/advertisements/Index.vue'),
    meta: {
      requiresAuth: true,
      title: 'Annonces publicitaires'
    }
  },
  {
    path: 'advertisements/analytics',
    name: 'advertisements-analytics',
    component: () => import('@/views/advertisements/Analytics.vue'),
    meta: {
      requiresAuth: true,
      title: 'Analytics des Annonces'
    }
  },
  {
    path: 'advertisements/:id',
    name: 'advertisement-detail',
    component: () => import('@/views/advertisements/Index.vue'),
    meta: {
      requiresAuth: true,
      title: 'Détails Annonce'
    },
    props: (route) => ({ advertisementId: route.params.id })
  }
]

