import type { RouteRecordRaw } from 'vue-router'

export const countriesRoutes: RouteRecordRaw[] = [
  {
    path: 'countries',
    name: 'countries',
    component: () => import('@/views/countries/Index.vue'),
    meta: { 
      requiresAuth: true,
      requiresAdmin: true,
      title: 'Gestion des Pays'
    }
  },
  {
    path: 'cities',
    name: 'cities',
    component: () => import('@/views/cities/Index.vue'),
    meta: { 
      requiresAuth: true,
      requiresAdmin: true,
      title: 'Gestion des Villes'
    }
  }
]

