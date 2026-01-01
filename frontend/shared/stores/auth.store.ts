import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { UserInfo, LoginRequest } from '../types/auth.types'
import { authService } from '../api/auth.service'
import { userService } from '../api/user.service'
import { tokenUtils } from '../utils/token.utils'

/**
 * Store d'authentification
 */
export const useAuthStore = defineStore('auth', () => {
  const user = ref<UserInfo | null>(null)
  const isAuthenticated = ref(false)
  const loading = ref(false)

  // Getters
  const isAdmin = computed(() => {
    return user.value?.roles?.includes('ADMIN') || false
  })

  const isAgent = computed(() => {
    return user.value?.roles?.includes('AGENT') || false
  })

  const userName = computed(() => {
    return user.value?.name || 'User'
  })

  // Actions
  async function login(credentials: LoginRequest): Promise<void> {
    loading.value = true
    try {
      // 1. Se connecter et obtenir le token
      const response = await authService.login(credentials)
      
      // 2. Le token est déjà stocké par authService.login()
      // 3. Marquer comme authentifié immédiatement
      isAuthenticated.value = true
      
      // 4. Récupérer les infos utilisateur depuis le profil
      // Attendre un peu pour que le token soit bien propagé dans les intercepteurs
      await new Promise(resolve => setTimeout(resolve, 300))
      
      try {
        const profile = await userService.getProfile()
        // Mapper UserProfile vers UserInfo
        // Le backend retourne roleNames (Set<String>) dans UserDTO
        const roleNames = (profile as any).roleNames || profile.roles || []
        const rolesArray: string[] = Array.isArray(roleNames) 
          ? roleNames.map((r: any) => String(r))
          : Array.from(roleNames || []).map((r: any) => String(r))
        
        user.value = {
          id: profile.id,
          email: profile.email,
          name: profile.firstName && profile.lastName 
            ? `${profile.firstName} ${profile.lastName}` 
            : profile.name || profile.email,
          roles: rolesArray,
          organizationId: profile.organizationId,
          organizationName: profile.organizationName
        }
      } catch (profileError: any) {
        // Si l'API profile échoue, on garde quand même l'authentification
        // L'utilisateur sera chargé plus tard par checkAuth()
        const status = profileError?.status || profileError?.response?.status
        if (status === 401 || status === 403) {
          // Erreur d'authentification - ne pas continuer
          console.error('Authentication failed when fetching profile:', profileError)
          throw profileError
        } else {
          // Autre erreur (réseau, serveur, etc.) - on continue sans user
          console.warn('Failed to fetch user profile after login (non-auth error):', profileError)
          // On reste authentifié même sans user pour l'instant
        }
      }
    } catch (error) {
      throw error
    } finally {
      loading.value = false
    }
  }

  async function logout(): Promise<void> {
    try {
      await authService.logout()
    } finally {
      user.value = null
      isAuthenticated.value = false
    }
  }

  async function checkAuth(): Promise<void> {
    if (tokenUtils.hasToken()) {
      isAuthenticated.value = true
      // Récupérer les infos utilisateur depuis l'API si pas déjà chargé
      if (!user.value) {
        try {
          const profile = await userService.getProfile()
          // Le backend retourne roleNames (Set<String>) dans UserDTO
          const roleNames = (profile as any).roleNames || profile.roles || []
          const rolesArray: string[] = Array.isArray(roleNames) 
            ? roleNames.map((r: any) => String(r))
            : Array.from(roleNames || []).map((r: any) => String(r))
          
          user.value = {
            id: profile.id,
            email: profile.email,
            name: profile.firstName && profile.lastName 
              ? `${profile.firstName} ${profile.lastName}` 
              : profile.name || profile.email,
            roles: rolesArray,
            organizationId: profile.organizationId
          }
        } catch (error: any) {
          const status = error?.status || error?.response?.status
          if (status === 401 || status === 403) {
            // Token invalide ou expiré
            console.error('Authentication failed in checkAuth:', error)
            tokenUtils.removeToken()
            isAuthenticated.value = false
            user.value = null
          } else {
            // Autre erreur (réseau, serveur, etc.)
            console.warn('Failed to fetch user profile in checkAuth (non-auth error):', error)
            // On garde l'authentification mais sans user
            // Le token est valide, donc on reste authentifié
          }
        }
      }
    } else {
      isAuthenticated.value = false
      user.value = null
    }
  }

  function setUser(userData: UserInfo): void {
    user.value = userData
    isAuthenticated.value = true
  }

  return {
    // State
    user,
    isAuthenticated,
    loading,
    // Getters
    isAdmin,
    isAgent,
    userName,
    // Actions
    login,
    logout,
    checkAuth,
    setUser
  }
})

