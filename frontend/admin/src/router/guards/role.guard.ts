import type { NavigationGuardNext, RouteLocationNormalized } from 'vue-router'
import { useAuthStore, UserRole } from '@viridial/shared'

/**
 * Guard pour protéger les routes nécessitant des rôles spécifiques
 */
export function roleGuard(...allowedRoles: UserRole[]) {
  return (
    to: RouteLocationNormalized,
    _from: RouteLocationNormalized,
    next: NavigationGuardNext
  ): void => {
    const authStore = useAuthStore()

    // Vérifier si l'utilisateur est authentifié
    if (!authStore.isAuthenticated || !authStore.user) {
      next({
        name: 'login',
        query: { redirect: to.fullPath }
      })
      return
    }

    // Vérifier si l'utilisateur a un des rôles requis
    const userRoles = authStore.user.roles || []
    const hasRequiredRole = allowedRoles.some(role => 
      userRoles.includes(role) || userRoles.includes(UserRole.SUPER_ADMIN)
    )

    if (!hasRequiredRole) {
      // Pas les permissions nécessaires, rediriger vers dashboard
      next({
        name: 'home',
        query: { error: 'unauthorized' }
      })
      return
    }

    next()
  }
}

/**
 * Guard pour vérifier si l'utilisateur est admin
 */
export function adminGuard(
  to: RouteLocationNormalized,
  from: RouteLocationNormalized,
  next: NavigationGuardNext
): void {
  return roleGuard(UserRole.ADMIN, UserRole.SUPER_ADMIN)(to, from, next)
}

/**
 * Guard pour vérifier si l'utilisateur est super admin
 */
export function superAdminGuard(
  to: RouteLocationNormalized,
  from: RouteLocationNormalized,
  next: NavigationGuardNext
): void {
  return roleGuard(UserRole.SUPER_ADMIN)(to, from, next)
}

