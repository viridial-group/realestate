import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { authGuard, guestGuard } from './guards/auth.guard'
import { adminGuard } from './guards/role.guard'
import { AdminLayout, AuthLayout } from './layouts'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    component: AdminLayout,
    beforeEnter: authGuard,
    children: [
      {
        path: '',
        name: 'home',
        component: () => import('@/views/Dashboard.vue'),
        meta: { 
          requiresAuth: true,
          title: 'Dashboard'
        }
      },
      {
        path: 'users',
        name: 'users',
        component: () => import('@/views/users/Index.vue'),
        meta: { 
          requiresAuth: true,
          requiresAdmin: true,
          title: 'Gestion des Utilisateurs'
        },
        beforeEnter: adminGuard
      },
      {
        path: 'users/:id',
        name: 'user-detail',
        component: () => import('@/views/users/Detail.vue'),
        meta: { 
          requiresAuth: true,
          requiresAdmin: true,
          title: 'Détails de l\'utilisateur'
        },
        beforeEnter: adminGuard
      },
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
      },
      {
        path: 'organizations',
        name: 'organizations',
        component: () => import('@/views/organizations/Index.vue'),
        meta: { 
          requiresAuth: true,
          requiresAdmin: true,
          title: 'Gestion des Organisations'
        },
        beforeEnter: adminGuard
      },
      {
        path: 'organizations/:id',
        name: 'organization-detail',
        component: () => import('@/views/organizations/Detail.vue'),
        meta: { 
          requiresAuth: true,
          requiresAdmin: true,
          title: 'Détails de l\'organisation'
        },
        beforeEnter: adminGuard
      },
      {
        path: 'billing',
        name: 'billing',
        component: () => import('@/views/billing/Index.vue'),
        meta: { 
          requiresAuth: true,
          requiresAdmin: true,
          title: 'Facturation'
        },
        beforeEnter: adminGuard
      },
      {
        path: 'audit',
        name: 'audit',
        component: () => import('@/views/audit/Index.vue'),
        meta: { 
          requiresAuth: true,
          requiresAdmin: true,
          title: 'Audit et Logs'
        },
        beforeEnter: adminGuard
      },
      {
        path: 'notifications',
        name: 'notifications',
        component: () => import('@/views/notifications/Index.vue'),
        meta: { 
          requiresAuth: true,
          requiresAdmin: true,
          title: 'Notifications'
        },
        beforeEnter: adminGuard
      }
    ]
  },
  {
    path: '/',
    component: AuthLayout,
    children: [
      {
        path: 'login',
        name: 'login',
        component: () => import('@/views/Login.vue'),
        meta: { 
          title: 'Connexion',
          layout: 'auth'
        },
        beforeEnter: guestGuard
      },
      {
        path: 'signup',
        name: 'signup',
        component: () => import('@/views/Signup.vue'),
        meta: { 
          title: 'Inscription',
          layout: 'auth'
        },
        beforeEnter: guestGuard
      },
      {
        path: 'forgot-password',
        name: 'forgot-password',
        component: () => import('@/views/ForgotPassword.vue'),
        meta: { 
          title: 'Mot de passe oublié',
          layout: 'auth'
        },
        beforeEnter: guestGuard
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Global navigation guard
router.beforeEach((to, from, next) => {
  // Mettre à jour le titre de la page
  if (to.meta.title) {
    document.title = `${to.meta.title} - Real Estate Admin`
  }

  next()
})

export default router

