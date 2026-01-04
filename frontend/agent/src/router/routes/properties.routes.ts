import type { RouteRecordRaw } from 'vue-router'

export const propertyRoutes: RouteRecordRaw[] = [
  {
    path: 'properties',
    name: 'agent-properties',
    component: () => import('@/views/Properties.vue'),
    meta: {
      requiresAuth: true,
      title: 'Mes Propriétés'
    }
  },
  {
    path: 'properties/:id',
    name: 'agent-property-detail',
    component: () => import('@/views/PropertyDetail.vue'),
    meta: {
      requiresAuth: true,
      title: 'Détails de la propriété'
    }
  }
]






