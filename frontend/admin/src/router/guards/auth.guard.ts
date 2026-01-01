import type { NavigationGuardNext, RouteLocationNormalized } from 'vue-router'
import { useAuthStore, tokenUtils } from '@viridial/shared'

/**
 * Guard pour protéger les routes nécessitant une authentification
 */
export async function authGuard(
  to: RouteLocationNormalized,
  from: RouteLocationNormalized,
  next: NavigationGuardNext
): Promise<void> {
  const authStore = useAuthStore()

  // Vérifier si un token existe
  if (!tokenUtils.hasToken()) {
    // Pas de token, rediriger vers login
    next({
      name: 'login',
      query: { redirect: to.fullPath }
    })
    return
  }

  // Si l'utilisateur n'est pas chargé, vérifier l'authentification
  if (!authStore.isAuthenticated) {
    try {
      // Vérifier le token et charger l'utilisateur (async maintenant)
      await authStore.checkAuth()
      
      // Si toujours pas authentifié après vérification, rediriger
      if (!authStore.isAuthenticated) {
        // Nettoyer le token seulement si vraiment pas authentifié
        if (!tokenUtils.hasToken()) {
          tokenUtils.removeToken()
        }
        next({
          name: 'login',
          query: { redirect: to.fullPath }
        })
        return
      }
    } catch (error: any) {
      // Erreur lors de la vérification
      console.error('Auth check failed:', error)
      const status = error?.status || error?.response?.status
      // Supprimer le token seulement si c'est une erreur d'authentification
      if (status === 401 || status === 403) {
        tokenUtils.removeToken()
      }
      next({
        name: 'login',
        query: { redirect: to.fullPath }
      })
      return
    }
  }

  // Utilisateur authentifié, continuer
  next()
}

/**
 * Guard pour rediriger les utilisateurs authentifiés (ex: login page)
 */
export function guestGuard(
  to: RouteLocationNormalized,
  from: RouteLocationNormalized,
  next: NavigationGuardNext
): void {
  const authStore = useAuthStore()

  // Si déjà authentifié, rediriger vers le dashboard
  if (authStore.isAuthenticated && tokenUtils.hasToken()) {
    next({ name: 'home' })
    return
  }

  next()
}

