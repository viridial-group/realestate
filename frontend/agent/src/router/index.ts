import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { authGuard } from './guards/auth.guard'
import { AgentLayout, AuthLayout } from './layouts'

// Import modular routes
import { dashboardRoutes } from './routes/dashboard.routes'
import { propertyRoutes } from './routes/properties.routes'
import { clientRoutes } from './routes/clients.routes'
import { visitRoutes } from './routes/visits.routes'
import { authRoutes } from './routes/auth.routes'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    component: AgentLayout,
    beforeEnter: authGuard,
    children: [
      ...dashboardRoutes,
      ...propertyRoutes,
      ...clientRoutes,
      ...visitRoutes
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

export default router

