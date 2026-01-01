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
                  class="absolute right-1 top-1 h-2 w-2 rounded-full bg-destructive"
                />
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end" class="w-80">
              <DropdownMenuLabel>{{ t('notifications.title') }}</DropdownMenuLabel>
              <DropdownMenuSeparator />
              <div class="p-4 text-center text-sm text-muted-foreground">
                {{ t('notifications.title') }}
              </div>
            </DropdownMenuContent>
          </DropdownMenu>

          <!-- User Menu -->
          <DropdownMenu>
            <DropdownMenuTrigger as-child>
              <Button variant="ghost" class="flex items-center space-x-2">
                <Avatar>
                  <AvatarImage :src="currentUser?.avatar" />
                  <AvatarFallback>
                    {{ currentUser?.name?.charAt(0).toUpperCase() || 'U' }}
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
              <DropdownMenuItem @click="$router.push('/profile')">
                <User class="mr-2 h-4 w-4" />
                {{ t('common.view') }}
              </DropdownMenuItem>
              <DropdownMenuItem @click="$router.push('/settings')">
                <Settings class="mr-2 h-4 w-4" />
                {{ t('common.view') }}
              </DropdownMenuItem>
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
import { ref, computed, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore, useUserStore } from '@viridial/shared'

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
  FileText,
  Settings,
  Bell,
  Menu,
  X,
  ChevronRight,
  ChevronDown,
  User,
  LogOut
} from 'lucide-vue-next'
import LanguageSelector from '@/components/shared/LanguageSelector.vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const userStore = useUserStore()

const isSidebarOpen = ref(false)
const notificationCount = ref(0)

const currentUser = computed(() => authStore.user || userStore.currentUser)

const navigationItems = computed(() => [
  {
    name: 'dashboard',
    label: t('dashboard.title'),
    path: '/',
    icon: LayoutDashboard
  },
  {
    name: 'users',
    label: t('users.title'),
    path: '/users',
    icon: Users,
    badge: null
  },
  {
    name: 'properties',
    label: t('properties.title'),
    path: '/properties',
    icon: Home
  },
  {
    name: 'organizations',
    label: t('organizations.title'),
    path: '/organizations',
    icon: Building2
  },
  {
    name: 'documents',
    label: t('common.view'),
    path: '/documents',
    icon: FileText
  },
  {
    name: 'settings',
    label: t('common.view'),
    path: '/settings',
    icon: Settings
  }
])

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
    await authStore.logout()
    router.push('/login')
  } catch (error) {
    console.error('Logout error:', error)
  }
}

onMounted(() => {
  // Charger le profil utilisateur si nécessaire
  if (authStore.isAuthenticated && !authStore.user) {
    userStore.fetchProfile()
  }
})
</script>

