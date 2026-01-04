import type { RouteRecordRaw } from 'vue-router'

export const organizationContactRoutes: RouteRecordRaw[] = [
  {
    path: 'organization-contacts',
    name: 'organization-contacts',
    component: () => import('@/views/organization-contacts/Index.vue'),
    meta: { 
      requiresAuth: true,
      title: 'Messages de Contact - Agences'
    }
  },
  {
    path: 'organization-contacts/:id',
    name: 'organization-contact-detail',
    component: () => import('@/views/organization-contacts/Index.vue'),
    meta: { 
      requiresAuth: true,
      title: 'Détails du message'
    },
    props: (route) => ({ messageId: route.params.id })
  }
]

