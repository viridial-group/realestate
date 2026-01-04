<template>
  <div>
    <aside
      class="fixed left-0 top-14 bottom-0 w-64 bg-white border-r border-gray-200 overflow-y-auto z-40 transition-transform duration-300 md:translate-x-0"
      :class="{ '-translate-x-full md:translate-x-0': !isOpen, 'translate-x-0': isOpen }"
      aria-label="Navigation utilisateur"
    >
    <!-- Header de la sidebar -->
    <div class="p-4 border-b border-gray-200">
      <h2 class="text-sm font-semibold text-gray-900 uppercase tracking-wider">
        Mon Espace
      </h2>
    </div>

    <!-- Navigation -->
    <nav class="p-4 space-y-1">
      <router-link
        v-for="item in menuItems"
        :key="item.path"
        :to="item.path"
        class="flex items-center gap-3 px-3 py-2.5 text-sm font-medium rounded-lg transition-all duration-200 group"
        :class="
          isActive(item.path)
            ? 'bg-blue-50 text-blue-700 border-l-4 border-blue-600'
            : 'text-gray-700 hover:bg-gray-50 hover:text-gray-900'
        "
        @click="handleClick"
      >
        <component
          :is="item.icon"
          class="h-5 w-5 flex-shrink-0"
          :class="isActive(item.path) ? 'text-blue-600' : 'text-gray-400 group-hover:text-gray-600'"
        />
        <span>{{ item.label }}</span>
        <span
          v-if="item.badge"
          class="ml-auto bg-red-600 text-white text-xs font-bold rounded-full w-5 h-5 flex items-center justify-center"
        >
          {{ item.badge > 9 ? '9+' : item.badge }}
        </span>
      </router-link>
    </nav>

    <!-- Section séparée pour les actions -->
    <div class="p-4 border-t border-gray-200 mt-4">
      <h3 class="text-xs font-semibold text-gray-500 uppercase tracking-wider mb-2">
        Actions
      </h3>
      <div class="space-y-1">
        <router-link
          to="/my-properties/new"
          class="flex items-center gap-3 px-3 py-2.5 text-sm font-medium text-gray-700 rounded-lg hover:bg-gray-50 transition-all duration-200 group"
        >
          <Plus class="h-5 w-5 text-gray-400 group-hover:text-blue-600" />
          <span>Nouvelle annonce</span>
        </router-link>
        <router-link
          to="/publish"
          class="flex items-center gap-3 px-3 py-2.5 text-sm font-medium text-gray-700 rounded-lg hover:bg-gray-50 transition-all duration-200 group"
        >
          <FileText class="h-5 w-5 text-gray-400 group-hover:text-blue-600" />
          <span>Publier une annonce</span>
        </router-link>
      </div>
    </div>

    <!-- Section aide -->
    <div class="p-4 border-t border-gray-200 mt-4">
      <router-link
        to="/help"
        class="flex items-center gap-3 px-3 py-2.5 text-sm font-medium text-gray-700 rounded-lg hover:bg-gray-50 transition-all duration-200 group"
      >
        <HelpCircle class="h-5 w-5 text-gray-400 group-hover:text-blue-600" />
        <span>Aide & Support</span>
      </router-link>
    </div>
  </aside>

    <!-- Overlay pour mobile -->
    <div
      v-if="isOpen"
      class="fixed inset-0 bg-black bg-opacity-50 z-30 md:hidden"
      @click="close"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import {
  LayoutDashboard,
  Home,
  User,
  MessageSquare,
  Settings,
  Plus,
  FileText,
  HelpCircle
} from 'lucide-vue-next'
import { useAuthStore } from '@viridial/shared'
import { notificationService } from '@viridial/shared'

interface MenuItem {
  path: string
  label: string
  icon: any
  badge?: number
}

const route = useRoute()
const authStore = useAuthStore()

const isOpen = ref(true)
const unreadCount = ref(0)

const menuItems = computed<MenuItem[]>(() => [
  {
    path: '/dashboard',
    label: 'Tableau de bord',
    icon: LayoutDashboard
  },
  {
    path: '/my-properties',
    label: 'Mes annonces',
    icon: Home
  },
  {
    path: '/my-messages',
    label: 'Messages',
    icon: MessageSquare,
    badge: unreadCount.value > 0 ? unreadCount.value : undefined
  },
  {
    path: '/profile',
    label: 'Mon profil',
    icon: User
  },
  {
    path: '/profile/settings',
    label: 'Paramètres',
    icon: Settings
  }
])

function isActive(path: string): boolean {
  if (path === '/dashboard') {
    return route.path === '/dashboard'
  }
  if (path === '/my-properties') {
    return route.path.startsWith('/my-properties')
  }
  if (path === '/my-messages') {
    return route.path.startsWith('/my-messages')
  }
  if (path === '/profile') {
    return route.path.startsWith('/profile')
  }
  return route.path === path
}

function handleClick() {
  // Fermer la sidebar sur mobile après navigation
  if (window.innerWidth < 768) {
    close()
  }
}

function close() {
  isOpen.value = false
}

function open() {
  isOpen.value = true
}

// Charger le nombre de messages non lus
onMounted(async () => {
  if (authStore.isAuthenticated && authStore.user?.id) {
    try {
      const count = await notificationService.getUnreadCount(authStore.user.id)
      unreadCount.value = count || 0
    } catch (err) {
      console.warn('Could not load unread count:', err)
    }
  }
})

// Exposer les méthodes pour le parent
defineExpose({
  open,
  close,
  toggle: () => { isOpen.value = !isOpen.value }
})
</script>

