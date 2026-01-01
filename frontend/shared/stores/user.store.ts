import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { User, UserCreate, UserUpdate, UserSearchParams, UserProfile, UserStats } from '../types/user.types'
import { userService } from '../api/user.service'
import { UserStatus, UserRole } from '../types/user.types'

/**
 * Store pour la gestion des utilisateurs SaaS
 */
export const useUserStore = defineStore('user', () => {
  // State
  const users = ref<User[]>([])
  const currentUser = ref<UserProfile | null>(null)
  const selectedUser = ref<User | null>(null)
  const stats = ref<UserStats | null>(null)
  const loading = ref(false)
  const total = ref(0)
  const currentPage = ref(1)
  const pageSize = ref(20)

  // Getters
  const activeUsers = computed(() => {
    return users.value.filter(user => user.status === UserStatus.ACTIVE)
  })

  const inactiveUsers = computed(() => {
    return users.value.filter(user => user.status === UserStatus.INACTIVE)
  })

  const suspendedUsers = computed(() => {
    return users.value.filter(user => user.status === UserStatus.SUSPENDED)
  })

  const usersByRole = computed(() => {
    const grouped: Record<UserRole, User[]> = {} as Record<UserRole, User[]>
    users.value.forEach(user => {
      user.roles.forEach(role => {
        if (!grouped[role]) {
          grouped[role] = []
        }
        grouped[role].push(user)
      })
    })
    return grouped
  })

  const hasRole = computed(() => (userId: number, role: UserRole) => {
    const user = users.value.find(u => u.id === userId)
    return user?.roles.includes(role) || false
  })

  const isCurrentUser = computed(() => (userId: number) => {
    return currentUser.value?.id === userId
  })

  // Actions
  async function fetchUsers(params?: UserSearchParams): Promise<void> {
    loading.value = true
    try {
      const result = await userService.getAll({
        ...params,
        page: currentPage.value - 1,
        size: pageSize.value
      })
      users.value = result.users
      total.value = result.total
    } catch (error) {
      console.error('Error fetching users:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  async function fetchUserById(id: number): Promise<void> {
    loading.value = true
    try {
      const user = await userService.getById(id)
      selectedUser.value = user
      // Mettre à jour dans la liste si présent
      const index = users.value.findIndex(u => u.id === id)
      if (index !== -1) {
        users.value[index] = user
      }
    } catch (error) {
      console.error('Error fetching user:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  async function fetchProfile(): Promise<void> {
    loading.value = true
    try {
      currentUser.value = await userService.getProfile()
    } catch (error) {
      console.error('Error fetching profile:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  async function createUser(data: UserCreate): Promise<User> {
    loading.value = true
    try {
      const user = await userService.create(data)
      users.value.push(user)
      total.value++
      return user
    } catch (error) {
      console.error('Error creating user:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  async function updateUser(id: number, data: UserUpdate): Promise<User> {
    loading.value = true
    try {
      const user = await userService.update(id, data)
      // Mettre à jour dans la liste
      const index = users.value.findIndex(u => u.id === id)
      if (index !== -1) {
        users.value[index] = user
      }
      // Mettre à jour selectedUser si c'est le même
      if (selectedUser.value?.id === id) {
        selectedUser.value = user
      }
      // Mettre à jour currentUser si c'est le même
      if (currentUser.value?.id === id) {
        currentUser.value = { ...currentUser.value, ...user }
      }
      return user
    } catch (error) {
      console.error('Error updating user:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  async function updateProfile(data: UserUpdate): Promise<UserProfile> {
    loading.value = true
    try {
      currentUser.value = await userService.updateProfile(data)
      return currentUser.value
    } catch (error) {
      console.error('Error updating profile:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  async function deleteUser(id: number): Promise<void> {
    loading.value = true
    try {
      await userService.delete(id)
      users.value = users.value.filter(u => u.id !== id)
      total.value--
      if (selectedUser.value?.id === id) {
        selectedUser.value = null
      }
    } catch (error) {
      console.error('Error deleting user:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  async function activateUser(id: number): Promise<User> {
    return updateUserStatus(id, () => userService.activate(id))
  }

  async function deactivateUser(id: number): Promise<User> {
    return updateUserStatus(id, () => userService.deactivate(id))
  }

  async function suspendUser(id: number, reason?: string): Promise<User> {
    return updateUserStatus(id, () => userService.suspend(id, reason))
  }

  async function fetchStats(): Promise<void> {
    try {
      stats.value = await userService.getStats()
    } catch (error) {
      console.error('Error fetching stats:', error)
      throw error
    }
  }

  function setPage(page: number): void {
    currentPage.value = page
  }

  function setPageSize(size: number): void {
    pageSize.value = size
    currentPage.value = 1
  }

  function clearUsers(): void {
    users.value = []
    total.value = 0
    selectedUser.value = null
  }

  function setSelectedUser(user: User | null): void {
    selectedUser.value = user
  }

  // Helper function
  async function updateUserStatus(id: number, action: () => Promise<User>): Promise<User> {
    loading.value = true
    try {
      const user = await action()
      // Mettre à jour dans la liste
      const index = users.value.findIndex(u => u.id === id)
      if (index !== -1) {
        users.value[index] = user
      }
      if (selectedUser.value?.id === id) {
        selectedUser.value = user
      }
      return user
    } catch (error) {
      console.error('Error updating user status:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  return {
    // State
    users,
    currentUser,
    selectedUser,
    stats,
    loading,
    total,
    currentPage,
    pageSize,
    // Getters
    activeUsers,
    inactiveUsers,
    suspendedUsers,
    usersByRole,
    hasRole,
    isCurrentUser,
    // Actions
    fetchUsers,
    fetchUserById,
    fetchProfile,
    createUser,
    updateUser,
    updateProfile,
    deleteUser,
    activateUser,
    deactivateUser,
    suspendUser,
    fetchStats,
    setPage,
    setPageSize,
    clearUsers,
    setSelectedUser
  }
})

