import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user.store'
import type { User, UserCreate, UserUpdate, UserSearchParams } from '../types/user.types'
import { UserRole, UserStatus } from '../types/user.types'

/**
 * Composable pour la gestion des utilisateurs
 */
export function useUser() {
  const router = useRouter()
  const userStore = useUserStore()

  // State
  const users = computed(() => userStore.users)
  const currentUser = computed(() => userStore.currentUser)
  const selectedUser = computed(() => userStore.selectedUser)
  const stats = computed(() => userStore.stats)
  const loading = computed(() => userStore.loading)
  const total = computed(() => userStore.total)
  const currentPage = computed(() => userStore.currentPage)
  const pageSize = computed(() => userStore.pageSize)

  // Getters
  const activeUsers = computed(() => userStore.activeUsers)
  const inactiveUsers = computed(() => userStore.inactiveUsers)
  const suspendedUsers = computed(() => userStore.suspendedUsers)
  const usersByRole = computed(() => userStore.usersByRole)

  // Actions
  async function loadUsers(params?: UserSearchParams) {
    await userStore.fetchUsers(params)
  }

  async function loadUser(id: number) {
    await userStore.fetchUserById(id)
  }

  async function loadProfile() {
    await userStore.fetchProfile()
  }

  async function createUser(data: UserCreate): Promise<User> {
    return await userStore.createUser(data)
  }

  async function updateUser(id: number, data: UserUpdate): Promise<User> {
    return await userStore.updateUser(id, data)
  }

  async function updateProfile(data: UserUpdate) {
    return await userStore.updateProfile(data)
  }

  async function deleteUser(id: number) {
    await userStore.deleteUser(id)
  }

  async function activateUser(id: number): Promise<User> {
    return await userStore.activateUser(id)
  }

  async function deactivateUser(id: number): Promise<User> {
    return await userStore.deactivateUser(id)
  }

  async function suspendUser(id: number, reason?: string): Promise<User> {
    return await userStore.suspendUser(id, reason)
  }

  async function loadStats() {
    await userStore.fetchStats()
  }

  function setPage(page: number) {
    userStore.setPage(page)
  }

  function setPageSize(size: number) {
    userStore.setPageSize(size)
  }

  function selectUser(user: User | null) {
    userStore.setSelectedUser(user)
  }

  function hasRole(userId: number, role: UserRole): boolean {
    return userStore.hasRole(userId, role)
  }

  function isCurrentUser(userId: number): boolean {
    return userStore.isCurrentUser(userId)
  }

  function canEditUser(userId: number): boolean {
    // Un utilisateur peut éditer son propre profil ou être admin
    if (isCurrentUser(userId)) return true
    if (currentUser.value?.roles.includes(UserRole.ADMIN) || 
        currentUser.value?.roles.includes(UserRole.SUPER_ADMIN)) {
      return true
    }
    return false
  }

  function canDeleteUser(userId: number): boolean {
    // Seuls les admins peuvent supprimer, et pas leur propre compte
    if (isCurrentUser(userId)) return false
    return currentUser.value?.roles.includes(UserRole.ADMIN) || 
           currentUser.value?.roles.includes(UserRole.SUPER_ADMIN) || false
  }

  function canManageRoles(userId: number): boolean {
    // Seuls les super admins peuvent gérer les rôles
    return currentUser.value?.roles.includes(UserRole.SUPER_ADMIN) || false
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
    // Actions
    loadUsers,
    loadUser,
    loadProfile,
    createUser,
    updateUser,
    updateProfile,
    deleteUser,
    activateUser,
    deactivateUser,
    suspendUser,
    loadStats,
    setPage,
    setPageSize,
    selectUser,
    // Permissions
    hasRole,
    isCurrentUser,
    canEditUser,
    canDeleteUser,
    canManageRoles
  }
}

