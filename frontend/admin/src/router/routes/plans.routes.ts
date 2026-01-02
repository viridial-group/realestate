import type { RouteRecordRaw } from 'vue-router'

export const plansRoutes: RouteRecordRaw[] = [
  {
    path: 'plans',
    name: 'plans',
    component: () => import('@/views/plans/Index.vue'),
    meta: { 
      requiresAuth: true,
      title: 'Gestion des Plans d\'abonnement'
    }
  },
  {
    path: 'plans/:id',
    name: 'plan-detail',
    component: () => import('@/views/plans/Detail.vue'),
    meta: { 
      requiresAuth: true,
      title: 'Détails du plan'
    }
  }
]

