import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore, UserRole } from '@viridial/shared'

/**
 * Composable pour la gestion des guards de route
 */
export function useRouteGuard() {
  const router = useRouter()
  const route = useRoute()
  const authStore = useAuthStore()

  /**
   * Vérifier si l'utilisateur peut accéder à la route actuelle
   */
  const canAccess = computed(() => {
    if (!authStore.isAuthenticated) {
      return false
    }

    const requiresAdmin = route.meta.requiresAdmin as boolean
    if (requiresAdmin) {
      return authStore.isAdmin || authStore.user?.roles?.includes(UserRole.SUPER_ADMIN)
    }

    return true
  })

  /**
   * Vérifier si l'utilisateur a un rôle spécifique
   */
  const hasRole = (role: UserRole): boolean => {
    if (!authStore.user) return false
    return authStore.user.roles?.includes(role) || 
           authStore.user.roles?.includes(UserRole.SUPER_ADMIN) || 
           false
  }

  /**
   * Vérifier si l'utilisateur a un des rôles spécifiés
   */
  const hasAnyRole = (...roles: UserRole[]): boolean => {
    if (!authStore.user) return false
    return roles.some(role => hasRole(role))
  }

  /**
   * Rediriger si l'utilisateur n'a pas les permissions
   */
  const requireAuth = () => {
    if (!authStore.isAuthenticated) {
      router.push({
        name: 'login',
        query: { redirect: route.fullPath }
      })
    }
  }

  /**
   * Rediriger si l'utilisateur n'est pas admin
   */
  const requireAdmin = () => {
    if (!authStore.isAuthenticated) {
      router.push({
        name: 'login',
        query: { redirect: route.fullPath }
      })
      return
    }

    if (!authStore.isAdmin && !hasRole(UserRole.SUPER_ADMIN)) {
      router.push({
        name: 'home',
        query: { error: 'unauthorized' }
      })
    }
  }

  /**
   * Rediriger si l'utilisateur n'a pas un rôle spécifique
   */
  const requireRole = (...roles: UserRole[]) => {
    if (!authStore.isAuthenticated) {
      router.push({
        name: 'login',
        query: { redirect: route.fullPath }
      })
      return
    }

    if (!hasAnyRole(...roles)) {
      router.push({
        name: 'home',
        query: { error: 'unauthorized' }
      })
    }
  }

  return {
    canAccess,
    hasRole,
    hasAnyRole,
    requireAuth,
    requireAdmin,
    requireRole
  }
}

