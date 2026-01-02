<template>
  <div class="min-h-screen bg-background">
    <!-- Sidebar -->
    <aside
      :class="[
        'fixed left-0 top-0 z-40 h-screen w-64 border-r bg-card transition-transform',
        isSidebarOpen ? 'translate-x-0' : '-translate-x-full',
        'lg:translate-x-0'
      ]"
    >
      <!-- Logo -->
      <div class="flex h-16 items-center border-b px-6">
        <div class="flex items-center space-x-2">
          <div class="flex h-8 w-8 items-center justify-center rounded-lg bg-primary">
            <span class="text-sm font-bold text-primary-foreground">RE</span>
          </div>
          <span class="text-lg font-bold">Real Estate</span>
        </div>
      </div>

      <!-- Navigation -->
      <ScrollArea class="h-[calc(100vh-4rem)]">
        <nav class="space-y-1 p-4">
          <RouterLink
            v-for="item in navigationItems"
            :key="item.name"
            :to="item.path"
            :class="[
              'flex items-center space-x-3 rounded-lg px-3 py-2 text-sm font-medium transition-colors',
              isActive(item.path)
                ? 'bg-primary text-primary-foreground'
                : 'text-muted-foreground hover:bg-accent hover:text-accent-foreground'
            ]"
          >
            <component :is="item.icon" class="h-5 w-5" />
            <span>{{ item.label }}</span>
            <Badge v-if="item.badge" variant="secondary" class="ml-auto">
              {{ item.badge }}
            </Badge>
          </RouterLink>
        </nav>
      </ScrollArea>
    </aside>

    <!-- Overlay pour mobile -->
    <div
      v-if="isSidebarOpen"
      class="fixed inset-0 z-30 bg-background/80 backdrop-blur-sm lg:hidden"
      @click="isSidebarOpen = false"
    />

    <!-- Main Content -->
    <div class="lg:pl-64">
      <!-- Header -->
      <header class="sticky top-0 z-20 flex h-16 items-center gap-4 border-b bg-background px-4 lg:px-6">
        <!-- Mobile menu button -->
        <Button
          variant="ghost"
          size="icon"
          class="lg:hidden"
          @click="isSidebarOpen = !isSidebarOpen"
        >
          <Menu v-if="!isSidebarOpen" class="h-6 w-6" />
          <X v-else class="h-6 w-6" />
        </Button>

        <!-- Breadcrumbs -->
        <nav class="flex items-center space-x-2 text-sm">
          <RouterLink to="/" class="text-muted-foreground hover:text-foreground">
            {{ t('dashboard.title') }}
          </RouterLink>
          <ChevronRight class="h-4 w-4 text-muted-foreground" />
          <span class="font-medium">{{ currentPageTitle }}</span>
        </nav>

        <div class="ml-auto flex items-center gap-4">
          <!-- Language Selector -->
          <LanguageSelector />

          <!-- Notifications -->
          <DropdownMenu>
            <DropdownMenuTrigger as-child>
              <Button variant="ghost" size="icon" class="relative">
                <Bell class="h-5 w-5" />
                <span
                  v-if="notificationCount > 0"
                  class="absolute right-1 top-1 flex h-5 w-5 items-center justify-center rounded-full bg-destructive text-[10px] font-bold text-destructive-foreground"
                >
                  {{ notificationCount > 99 ? '99+' : notificationCount }}
                </span>
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end" class="w-80">
              <div class="flex items-center justify-between px-2 py-1.5">
                <DropdownMenuLabel class="p-0">{{ t('notifications.title') }}</DropdownMenuLabel>
                <Button
                  v-if="notificationCount > 0"
                  variant="ghost"
                  size="sm"
                  class="h-7 text-xs"
                  @click.stop="markAllAsRead"
                >
                  Tout marquer comme lu
                </Button>
              </div>
              <DropdownMenuSeparator />
              <ScrollArea class="h-[300px]">
                <div v-if="loadingNotifications" class="p-4 text-center text-sm text-muted-foreground">
                  Chargement...
                </div>
                <div v-else-if="notifications.length === 0" class="p-4 text-center text-sm text-muted-foreground">
                  Aucune notification
                </div>
                <div v-else class="divide-y">
                  <div
                    v-for="notification in notifications"
                    :key="notification.id"
                    :class="[
                      'cursor-pointer px-3 py-2 hover:bg-accent transition-colors',
                      !isNotificationRead(notification) ? 'bg-primary/5' : ''
                    ]"
                    @click="handleNotificationClick(notification)"
                  >
                    <div class="flex items-start gap-2">
                      <div
                        :class="[
                          'flex-shrink-0 mt-0.5 w-2 h-2 rounded-full',
                          !isNotificationRead(notification) ? 'bg-primary' : 'bg-transparent'
                        ]"
                      />
                      <div class="flex-1 min-w-0">
                        <p class="text-sm font-medium truncate">{{ notification.title }}</p>
                        <p v-if="notification.message" class="text-xs text-muted-foreground line-clamp-2 mt-0.5">
                          {{ notification.message }}
                        </p>
                        <p class="text-xs text-muted-foreground mt-1">
                          {{ formatNotificationTime(notification.createdAt) }}
                        </p>
                      </div>
                    </div>
                  </div>
                </div>
              </ScrollArea>
              <DropdownMenuSeparator />
              <DropdownMenuItem @click="router.push('/notifications')" class="cursor-pointer">
                <Bell class="mr-2 h-4 w-4" />
                Voir toutes les notifications
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>

          <!-- User Menu -->
          <DropdownMenu>
            <DropdownMenuTrigger as-child>
              <Button variant="ghost" class="flex items-center space-x-2">
                <Avatar>
                  <AvatarImage :src="currentUser?.avatar" v-if="currentUser?.avatar" />
                  <AvatarFallback>
                    {{ (currentUser?.name || currentUser?.email || 'U').charAt(0).toUpperCase() }}
                  </AvatarFallback>
                </Avatar>
                <div class="hidden flex-col items-start text-left md:flex">
                  <span class="text-sm font-medium">{{ currentUser?.name || 'Utilisateur' }}</span>
                  <span class="text-xs text-muted-foreground">{{ currentUser?.email }}</span>
                </div>
                <ChevronDown class="h-4 w-4" />
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end" class="w-56">
              <DropdownMenuLabel>{{ t('common.view') }}</DropdownMenuLabel>
              <DropdownMenuSeparator />
              <!-- Routes à implémenter plus tard -->
              <!-- <DropdownMenuItem @click="$router.push('/profile')">
                <User class="mr-2 h-4 w-4" />
                {{ t('common.view') }}
              </DropdownMenuItem> -->
              <!-- <DropdownMenuItem @click="$router.push('/settings')">
                <Settings class="mr-2 h-4 w-4" />
                {{ t('common.view') }}
              </DropdownMenuItem> -->
              <DropdownMenuSeparator />
              <DropdownMenuItem @click="handleLogout" class="text-destructive">
                <LogOut class="mr-2 h-4 w-4" />
                {{ t('auth.logout') }}
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
        </div>
      </header>

      <!-- Page Content -->
      <main class="p-4 lg:p-6">
        <RouterView />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore, useUserStore, notificationService, type Notification } from '@viridial/shared'

const { t } = useI18n()
import { Button } from '@/components/ui/button'
import { Avatar, AvatarImage, AvatarFallback } from '@/components/ui/avatar'
import { Badge } from '@/components/ui/badge'
import { ScrollArea } from '@/components/ui/scroll-area'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger
} from '@/components/ui/dropdown-menu'
import {
  LayoutDashboard,
  Users,
  Home,
  Building2,
  Bell,
  Menu,
  X,
  ChevronRight,
  ChevronDown,
  LogOut,
  CreditCard,
  FileSearch,
  Workflow
} from 'lucide-vue-next'
import LanguageSelector from '@/components/shared/LanguageSelector.vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const userStore = useUserStore()

const isSidebarOpen = ref(false)
const notificationCount = ref(0)
const notifications = ref<Notification[]>([])
const loadingNotifications = ref(false)
let notificationRefreshInterval: ReturnType<typeof setInterval> | null = null

const currentUser = computed(() => authStore.user || userStore.currentUser)

const currentUserRoles = computed(() => {
  const user = currentUser.value
  return user?.roles || []
})

const isAdmin = computed(() => {
  const roles = currentUserRoles.value
  return roles.some((role: string) => role === 'ADMIN' || role === 'SUPER_ADMIN')
})

const navigationItems = computed(() => {
  const items: Array<{
    name: string
    label: string
    path: string
    icon: any
    badge?: string | null
    requiresAdmin?: boolean
  }> = [
    {
      name: 'dashboard',
      label: t('dashboard.title'),
      path: '/',
      icon: LayoutDashboard
    },
    {
      name: 'properties',
      label: t('properties.title'),
      path: '/properties',
      icon: Home
    },
    {
      name: 'users',
      label: t('users.title'),
      path: '/users',
      icon: Users,
      requiresAdmin: true
    },
    {
      name: 'organizations',
      label: t('organizations.title'),
      path: '/organizations',
      icon: Building2,
      requiresAdmin: true
    },
    {
      name: 'workflows',
      label: t('workflows.title', 'Workflows'),
      path: '/workflows',
      icon: Workflow
    },
    {
      name: 'billing',
      label: t('billing.title', 'Facturation'),
      path: '/billing',
      icon: CreditCard,
      requiresAdmin: true
    },
    {
      name: 'audit',
      label: t('audit.title', 'Audit et Logs'),
      path: '/audit',
      icon: FileSearch,
      requiresAdmin: true
    },
    {
      name: 'notifications',
      label: t('notifications.title'),
      path: '/notifications',
      icon: Bell,
      requiresAdmin: true
    }
  ]

  // Filter items based on user permissions
  return items.filter(item => {
    if (item.requiresAdmin && !isAdmin.value) {
      return false
    }
    return true
  })
})

const currentPageTitle = computed(() => {
  const item = navigationItems.value.find(item => item.path === route.path)
  return item?.label || t('dashboard.title')
})

const isActive = (path: string) => {
  if (path === '/') {
    return route.path === '/'
  }
  return route.path.startsWith(path)
}

const handleLogout = async () => {
  try {
    // Arrêter le rafraîchissement des notifications
    if (notificationRefreshInterval) {
      clearInterval(notificationRefreshInterval)
      notificationRefreshInterval = null
    }
    await authStore.logout()
    router.push('/login')
  } catch (error) {
    console.error('Logout error:', error)
  }
}

const loadNotifications = async () => {
  if (!authStore.user?.id || !authStore.isAuthenticated) {
    notifications.value = []
    notificationCount.value = 0
    return
  }

  loadingNotifications.value = true
  try {
    // Charger uniquement les notifications non lues pour le header
    const unreadNotifications = await notificationService.getNotifications({
      recipientId: authStore.user.id,
      unread: true
    })
    
    // Limiter à 5 notifications pour le dropdown
    notifications.value = unreadNotifications.slice(0, 5)
    notificationCount.value = unreadNotifications.length
  } catch (error: any) {
    console.error('Error loading notifications:', error)
    notifications.value = []
    notificationCount.value = 0
  } finally {
    loadingNotifications.value = false
  }
}

const markAllAsRead = async () => {
  if (!authStore.user?.id) return
  
  try {
    await notificationService.markAllAsRead(authStore.user.id)
    await loadNotifications()
  } catch (error: any) {
    console.error('Error marking all as read:', error)
  }
}

const handleNotificationClick = async (notification: Notification) => {
  // Marquer comme lu si ce n'est pas déjà fait
  if (!isNotificationRead(notification)) {
    try {
      await notificationService.markAsRead(notification.id)
      // Mettre à jour localement
      const index = notifications.value.findIndex(n => n.id === notification.id)
      if (index !== -1) {
        notifications.value[index] = {
          ...notifications.value[index],
          read: true,
          readAt: new Date().toISOString(),
          status: 'READ'
        }
        notificationCount.value = Math.max(0, notificationCount.value - 1)
      }
    } catch (error: any) {
      console.error('Error marking notification as read:', error)
    }
  }
  
  // Naviguer vers la page de notifications
  router.push('/notifications')
}

const isNotificationRead = (notification: Notification): boolean => {
  return notification.read || !!notification.readAt || notification.status === 'READ'
}

const formatNotificationTime = (date: string) => {
  if (!date) return '-'
  const d = new Date(date)
  const now = new Date()
  const diff = now.getTime() - d.getTime()
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)

  if (minutes < 1) return 'À l\'instant'
  if (minutes < 60) return `Il y a ${minutes} min`
  if (hours < 24) return `Il y a ${hours}h`
  if (days < 7) return `Il y a ${days}j`
  return d.toLocaleDateString('fr-FR', { day: 'numeric', month: 'short' })
}

// Surveiller l'authentification pour charger les notifications
watch(() => authStore.isAuthenticated, (isAuthenticated) => {
  if (isAuthenticated && authStore.user?.id) {
    loadNotifications()
    // Rafraîchir toutes les 30 secondes
    if (notificationRefreshInterval) {
      clearInterval(notificationRefreshInterval)
    }
    notificationRefreshInterval = setInterval(() => {
      loadNotifications()
    }, 30000)
  } else {
    notifications.value = []
    notificationCount.value = 0
    if (notificationRefreshInterval) {
      clearInterval(notificationRefreshInterval)
      notificationRefreshInterval = null
    }
  }
}, { immediate: true })

onMounted(() => {
  // Charger le profil utilisateur si nécessaire
  if (authStore.isAuthenticated && !authStore.user) {
    userStore.fetchProfile()
  }
  
  // Charger les notifications si l'utilisateur est authentifié
  if (authStore.isAuthenticated && authStore.user?.id) {
    loadNotifications()
    // Rafraîchir toutes les 30 secondes
    notificationRefreshInterval = setInterval(() => {
      loadNotifications()
    }, 30000)
  }
})

onUnmounted(() => {
  if (notificationRefreshInterval) {
    clearInterval(notificationRefreshInterval)
    notificationRefreshInterval = null
  }
})
</script>

