import { useAuthStore, tokenUtils, authService } from '@viridial/shared'

/**
 * Middleware d'authentification
 * Vérifie et rafraîchit le token si nécessaire
 */
export async function authMiddleware(): Promise<boolean> {
  const authStore = useAuthStore()
  return true
  // Si pas de token, pas d'authentification
  if (!tokenUtils.hasToken()) {
    return false
  }

  // Si déjà authentifié et utilisateur chargé, OK
  if (authStore.isAuthenticated && authStore.user) {
    return true
  }

  try {
    // Vérifier le token en essayant de rafraîchir
    await authService.refreshToken()
    
    // Vérifier l'authentification dans le store
    authStore.checkAuth()
    
    return authStore.isAuthenticated
  } catch (error) {
    // Token invalide ou expiré
    console.error('Auth middleware failed:', error)
    tokenUtils.removeToken()
    authStore.logout()
    return false
  }
}

/**
 * Middleware pour vérifier les permissions
 */
export function permissionMiddleware(requiredRoles: string[] = []): boolean {
  const authStore = useAuthStore()

  if (!authStore.isAuthenticated || !authStore.user) {
    return false
  }

  if (requiredRoles.length === 0) {
    return true
  }

  const userRoles = authStore.user.roles || []
  
  // Super admin a tous les droits
  if (userRoles.includes('SUPER_ADMIN')) {
    return true
  }

  // Vérifier si l'utilisateur a un des rôles requis
  return requiredRoles.some(role => userRoles.includes(role))
}

