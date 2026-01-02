import type { RouteRecordRaw } from 'vue-router'

export const documentationRoutes: RouteRecordRaw[] = [
  {
    path: 'documentation',
    name: 'documentation',
    component: () => import('@/views/documentation/Index.vue'),
    meta: { 
      requiresAuth: true,
      title: 'Documentation'
    }
  },
  {
    path: 'documentation/getting-started',
    name: 'doc-getting-started',
    component: () => import('@/views/documentation/GettingStarted.vue'),
    meta: { 
      requiresAuth: true,
      title: 'Premiers pas'
    }
  },
  {
    path: 'documentation/properties',
    name: 'doc-properties',
    component: () => import('@/views/documentation/Properties.vue'),
    meta: { 
      requiresAuth: true,
      title: 'Gestion des Propriétés'
    }
  },
  {
    path: 'documentation/users',
    name: 'doc-users',
    component: () => import('@/views/documentation/Users.vue'),
    meta: { 
      requiresAuth: true,
      title: 'Gestion des Utilisateurs'
    }
  },
  {
    path: 'documentation/organizations',
    name: 'doc-organizations',
    component: () => import('@/views/documentation/Organizations.vue'),
    meta: { 
      requiresAuth: true,
      title: 'Gestion des Organisations'
    }
  },
  {
    path: 'documentation/workflows',
    name: 'doc-workflows',
    component: () => import('@/views/documentation/Workflows.vue'),
    meta: { 
      requiresAuth: true,
      title: 'Workflows d\'approbation'
    }
  },
  {
    path: 'documentation/billing',
    name: 'doc-billing',
    component: () => import('@/views/documentation/Billing.vue'),
    meta: { 
      requiresAuth: true,
      title: 'Facturation et Abonnements'
    }
  },
  {
    path: 'documentation/notifications',
    name: 'doc-notifications',
    component: () => import('@/views/documentation/Notifications.vue'),
    meta: { 
      requiresAuth: true,
      title: 'Notifications'
    }
  },
  {
    path: 'documentation/documents',
    name: 'doc-documents',
    component: () => import('@/views/documentation/Documents.vue'),
    meta: { 
      requiresAuth: true,
      title: 'Gestion des Documents'
    }
  },
  {
    path: 'documentation/map',
    name: 'doc-map',
    component: () => import('@/views/documentation/Map.vue'),
    meta: { 
      requiresAuth: true,
      title: 'Carte Interactive'
    }
  },
  {
    path: 'documentation/faq',
    name: 'doc-faq',
    component: () => import('@/views/documentation/FAQ.vue'),
    meta: { 
      requiresAuth: true,
      title: 'Questions Fréquentes'
    }
  }
]

