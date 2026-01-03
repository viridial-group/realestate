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
            <span class="text-sm font-bold text-primary-foreground">A</span>
          </div>
          <span class="text-lg font-bold">{{ t('common.appName') }}</span>
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
            {{ t('navigation.dashboard') }}
          </RouterLink>
          <ChevronRight v-if="route.path !== '/'" class="h-4 w-4 text-muted-foreground" />
          <span v-if="route.path !== '/'" class="font-medium">{{ currentPageTitle }}</span>
        </nav>

        <div class="ml-auto flex items-center gap-4">
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
                <DropdownMenuLabel class="p-0">Notifications</DropdownMenuLabel>
                <Button
                  v-if="notificationCount > 0"
                  variant="ghost"
                  size="sm"
                  class="h-7 text-xs"
                  @click="markAllAsRead"
                >
                  Tout marquer comme lu
                </Button>
              </div>
              <DropdownMenuSeparator />
              <ScrollArea class="h-96">
                <div v-if="loadingNotifications" class="flex items-center justify-center p-4">
                  <Loader2 class="h-6 w-6 animate-spin text-muted-foreground" />
                </div>
                <div v-else-if="notifications.length === 0" class="p-4 text-center text-sm text-muted-foreground">
                  Aucune notification
                </div>
                <div v-else class="divide-y">
                  <div
                    v-for="notification in notifications"
                    :key="notification.id"
                    class="p-3 hover:bg-accent cursor-pointer"
                    :class="{ 'bg-muted': !notification.readAt }"
                    @click="handleNotificationClick(notification)"
                  >
                    <div class="flex items-start gap-3">
                      <div class="mt-0.5">
                        <Bell class="h-4 w-4 text-primary" />
                      </div>
                      <div class="flex-1 space-y-1">
                        <p class="text-sm font-medium leading-none">{{ notification.title }}</p>
                        <p class="text-xs text-muted-foreground line-clamp-2">{{ notification.message }}</p>
                        <p class="text-xs text-muted-foreground">
                          {{ formatDate(notification.createdAt) }}
                        </p>
                      </div>
                    </div>
                  </div>
                </div>
              </ScrollArea>
            </DropdownMenuContent>
          </DropdownMenu>

          <!-- User Menu -->
          <DropdownMenu>
            <DropdownMenuTrigger as-child>
              <Button variant="ghost" class="relative h-10 w-10 rounded-full">
                <Avatar>
                  <AvatarImage :src="currentUser?.avatar" :alt="currentUser?.firstName || 'User'" />
                  <AvatarFallback>
                    {{ getUserInitials(currentUser) }}
                  </AvatarFallback>
                </Avatar>
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end" class="w-56">
              <DropdownMenuLabel class="font-normal">
                <div class="flex flex-col space-y-1">
                  <p class="text-sm font-medium leading-none">
                    {{ getUserFullName(currentUser) }}
                  </p>
                  <p class="text-xs leading-none text-muted-foreground">
                    {{ currentUser?.email }}
                  </p>
                </div>
              </DropdownMenuLabel>
              <DropdownMenuSeparator />
              <DropdownMenuItem @click="router.push('/profile')">
                <User class="mr-2 h-4 w-4" />
                <span>Profil</span>
              </DropdownMenuItem>
              <DropdownMenuSeparator />
              <DropdownMenuItem @click="handleLogout" class="text-destructive">
                <LogOut class="mr-2 h-4 w-4" />
                <span>{{ t('common.logout') }}</span>
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
  Home,
  Users,
  Calendar,
  Bell,
  Menu,
  X,
  ChevronRight,
  LogOut,
  User as UserIcon,
  Loader2
} from 'lucide-vue-next'

const { t } = useI18n()
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

const navigationItems = computed(() => [
  {
    name: 'dashboard',
    label: t('navigation.dashboard'),
    path: '/',
    icon: LayoutDashboard
  },
  {
    name: 'properties',
    label: t('navigation.properties'),
    path: '/properties',
    icon: Home
  },
  {
    name: 'clients',
    label: t('navigation.clients'),
    path: '/clients',
    icon: Users
  },
  {
    name: 'visits',
    label: t('navigation.visits'),
    path: '/visits',
    icon: Calendar
  }
])

const currentPageTitle = computed(() => {
  const item = navigationItems.value.find(item => item.path === route.path)
  return item?.label || t('navigation.dashboard')
})

const isActive = (path: string) => {
  return route.path === path
}

const getUserFullName = (user: any) => {
  if (!user) return 'Utilisateur'
  return `${user.firstName || ''} ${user.lastName || ''}`.trim() || user.email || 'Utilisateur'
}

const getUserInitials = (user: any) => {
  if (!user) return 'U'
  const firstName = user.firstName || ''
  const lastName = user.lastName || ''
  if (firstName && lastName) {
    return `${firstName[0]}${lastName[0]}`.toUpperCase()
  }
  if (user.email) {
    return user.email[0].toUpperCase()
  }
  return 'U'
}

const formatDate = (date: string | Date) => {
  if (!date) return ''
  const d = new Date(date)
  const now = new Date()
  const diffMs = now.getTime() - d.getTime()
  const diffMins = Math.floor(diffMs / 60000)
  const diffHours = Math.floor(diffMs / 3600000)
  const diffDays = Math.floor(diffMs / 86400000)

  if (diffMins < 1) return 'À l\'instant'
  if (diffMins < 60) return `Il y a ${diffMins} min`
  if (diffHours < 24) return `Il y a ${diffHours}h`
  if (diffDays < 7) return `Il y a ${diffDays}j`
  return d.toLocaleDateString('fr-FR', { day: 'numeric', month: 'short' })
}

const loadNotifications = async () => {
  loadingNotifications.value = true
  try {
    const response = await notificationService.getMyNotifications({ page: 0, size: 10 })
    notifications.value = response.content || []
    notificationCount.value = response.unreadCount || 0
  } catch (error) {
    console.error('Error loading notifications:', error)
  } finally {
    loadingNotifications.value = false
  }
}

const markAllAsRead = async () => {
  try {
    await notificationService.markAllAsRead()
    await loadNotifications()
  } catch (error) {
    console.error('Error marking notifications as read:', error)
  }
}

const handleNotificationClick = async (notification: Notification) => {
  if (!notification.readAt) {
    try {
      await notificationService.markAsRead(notification.id)
      await loadNotifications()
    } catch (error) {
      console.error('Error marking notification as read:', error)
    }
  }
}

const handleLogout = async () => {
  try {
    await authStore.logout()
    router.push('/login')
  } catch (error) {
    console.error('Error logging out:', error)
    router.push('/login')
  }
}

onMounted(() => {
  loadNotifications()
  // Refresh notifications every 30 seconds
  notificationRefreshInterval = setInterval(loadNotifications, 30000)
})

onUnmounted(() => {
  if (notificationRefreshInterval) {
    clearInterval(notificationRefreshInterval)
  }
})

watch(() => route.path, () => {
  if (window.innerWidth < 1024) {
    isSidebarOpen.value = false
  }
})
</script>


