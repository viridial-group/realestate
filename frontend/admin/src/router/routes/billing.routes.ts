import type { RouteRecordRaw } from 'vue-router'
import { adminGuard } from '../guards/role.guard'

export const billingRoutes: RouteRecordRaw[] = [
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
    path: 'billing/subscriptions/:id',
    name: 'billing-subscription-detail',
    component: () => import('@/views/billing/SubscriptionDetail.vue'),
    meta: { 
      requiresAuth: true,
      requiresAdmin: true,
      title: 'Détails de l\'abonnement'
    },
    beforeEnter: adminGuard
  },
  {
    path: 'billing/subscriptions/:subscriptionId/invoices',
    name: 'billing-subscription-invoices',
    component: () => import('@/views/billing/Invoices.vue'),
    meta: { 
      requiresAuth: true,
      requiresAdmin: true,
      title: 'Factures de l\'abonnement'
    },
    beforeEnter: adminGuard
  },
  {
    path: 'billing/invoices',
    name: 'billing-invoices',
    component: () => import('@/views/billing/Invoices.vue'),
    meta: { 
      requiresAuth: true,
      requiresAdmin: true,
      title: 'Factures'
    },
    beforeEnter: adminGuard
  },
  {
    path: 'billing/invoices/:id',
    name: 'billing-invoice-detail',
    component: () => import('@/views/billing/InvoiceDetail.vue'),
    meta: { 
      requiresAuth: true,
      requiresAdmin: true,
      title: 'Détails de la facture'
    },
    beforeEnter: adminGuard
  }
]

