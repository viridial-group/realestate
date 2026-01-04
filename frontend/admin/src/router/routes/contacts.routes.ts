import type { RouteRecordRaw } from 'vue-router'

export const contactRoutes: RouteRecordRaw[] = [
  {
    path: 'contacts',
    name: 'contacts',
    component: () => import('@/views/contacts/Index.vue'),
    meta: { 
      requiresAuth: true,
      title: 'Messages de Contact'
    }
  },
  {
    path: 'contacts/:id',
    name: 'contact-detail',
    component: () => import('@/views/contacts/Index.vue'),
    meta: { 
      requiresAuth: true,
      title: 'Détails du message'
    },
    props: (route) => ({ messageId: route.params.id })
  }
]

