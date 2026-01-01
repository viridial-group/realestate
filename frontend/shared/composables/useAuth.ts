import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth.store'
import type { LoginRequest } from '../types/auth.types'

/**
 * Composable pour l'authentification
 */
export function useAuth() {
  const router = useRouter()
  const authStore = useAuthStore()

  const user = computed(() => authStore.user)
  const isAuthenticated = computed(() => authStore.isAuthenticated)
  const isLoading = computed(() => authStore.loading)
  const isAdmin = computed(() => authStore.isAdmin)
  const isAgent = computed(() => authStore.isAgent)

  async function login(credentials: LoginRequest) {
    try {
      await authStore.login(credentials)
      router.push('/')
    } catch (error) {
      throw error
    }
  }

  async function logout() {
    await authStore.logout()
    router.push('/login')
  }

  function requireAuth() {
    if (!authStore.isAuthenticated) {
      router.push('/login')
    }
  }

  return {
    user,
    isAuthenticated,
    isLoading,
    isAdmin,
    isAgent,
    login,
    logout,
    requireAuth
  }
}

