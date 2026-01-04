import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { authGuard } from './guards/auth.guard'
import { AdminLayout, AuthLayout } from './layouts'

// Import modular routes
import { dashboardRoutes } from './routes/dashboard.routes'
import { userRoutes } from './routes/users.routes'
import { propertyRoutes } from './routes/properties.routes'
import { organizationRoutes } from './routes/organizations.routes'
import { billingRoutes } from './routes/billing.routes'
import { auditRoutes } from './routes/audit.routes'
import { notificationRoutes } from './routes/notifications.routes'
import { workflowRoutes } from './routes/workflows.routes'
import { documentRoutes } from './routes/documents.routes'
import { documentationRoutes } from './routes/documentation.routes'
import { plansRoutes } from './routes/plans.routes'
import { roleRoutes } from './routes/roles.routes'
import { contactRoutes } from './routes/contacts.routes'
import { organizationContactRoutes } from './routes/organization-contacts.routes'
import { advertisementRoutes } from './routes/advertisements.routes'
import { reviewRoutes } from './routes/reviews.routes'
import { visitRoutes } from './routes/visits.routes'
import { dvfRoutes } from './routes/dvf.routes'
import { countriesRoutes } from './routes/countries.routes'
import { authRoutes } from './routes/auth.routes'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    component: AdminLayout,
    beforeEnter: authGuard,
    children: [
      ...dashboardRoutes,
      ...userRoutes,
      ...propertyRoutes,
      ...organizationRoutes,
      ...billingRoutes,
      ...auditRoutes,
      ...notificationRoutes,
      ...workflowRoutes,
      ...documentRoutes,
      ...documentationRoutes,
      ...plansRoutes,
      ...roleRoutes,
      ...contactRoutes,
      ...organizationContactRoutes,
      ...advertisementRoutes,
        ...reviewRoutes,
        ...visitRoutes,
        ...dvfRoutes,
        ...countriesRoutes
    ]
  },
  {
    path: '/',
    component: AuthLayout,
    children: authRoutes
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

