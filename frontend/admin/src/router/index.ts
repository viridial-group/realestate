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
      ...workflowRoutes
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

