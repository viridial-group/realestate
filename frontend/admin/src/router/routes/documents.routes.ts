import type { RouteRecordRaw } from 'vue-router'

export const documentRoutes: RouteRecordRaw[] = [
  {
    path: 'documents',
    name: 'documents',
    component: () => import('@/views/documents/Index.vue'),
    meta: { 
      requiresAuth: true,
      title: 'Gestion des Documents'
    }
  },
  {
    path: 'documents/upload',
    name: 'documents-upload',
    component: () => import('@/views/documents/Upload.vue'),
    meta: { 
      requiresAuth: true,
      title: 'Uploader un document'
    }
  }
]

