import type { RouteRecordRaw } from 'vue-router'

export const propertyRoutes: RouteRecordRaw[] = [
  {
    path: 'properties',
    name: 'properties',
    component: () => import('@/views/properties/Index.vue'),
    meta: { 
      requiresAuth: true,
      title: 'Gestion des Propriétés'
    }
  },
  {
    path: 'properties/:id',
    name: 'property-detail',
    component: () => import('@/views/properties/Detail.vue'),
    meta: { 
      requiresAuth: true,
      title: 'Détails de la propriété'
    }
  },
  {
    path: 'properties/new',
    name: 'property-create',
    component: () => import('@/views/properties/Form.vue'),
    meta: { 
      requiresAuth: true,
      title: 'Nouvelle propriété'
    }
  },
  {
    path: 'properties/:id/edit',
    name: 'property-edit',
    component: () => import('@/views/properties/Form.vue'),
    meta: { 
      requiresAuth: true,
      title: 'Modifier la propriété'
    }
  }
]

