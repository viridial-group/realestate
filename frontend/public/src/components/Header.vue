<template>
  <header class="bg-white border-b border-gray-200 sticky top-0 z-50 shadow-sm" role="banner">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex items-center justify-between h-16">
        <!-- Logo -->
        <div class="flex items-center gap-3 flex-shrink-0">
          <router-link 
            to="/" 
            class="flex items-center gap-2 hover:opacity-80 transition-opacity focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 rounded-md"
            aria-label="Retour à l'accueil"
          >
            <div class="h-9 w-9 rounded-lg bg-blue-600 flex items-center justify-center shadow-sm" aria-hidden="true">
              <Home class="h-5 w-5 text-white" />
            </div>
            <span class="text-xl font-semibold text-gray-900 hidden sm:inline">RealEstate</span>
          </router-link>
        </div>

        <!-- Navigation principale (seulement les plus importants) -->
        <nav class="hidden lg:flex items-center gap-1" role="navigation" aria-label="Navigation principale">
          <router-link
            to="/search"
            class="px-4 py-2 text-sm font-medium text-gray-700 hover:text-gray-900 hover:bg-gray-50 rounded-lg transition-colors focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-1"
            active-class="text-blue-600 bg-blue-50"
          >
            Recherche
          </router-link>
          <router-link
            to="/publish"
            class="px-4 py-2 text-sm font-medium text-gray-700 hover:text-gray-900 hover:bg-gray-50 rounded-lg transition-colors focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-1"
            active-class="text-blue-600 bg-blue-50"
          >
            Publier
          </router-link>
        </nav>

        <!-- Actions rapides (icônes seulement) -->
        <div class="flex items-center gap-1" role="toolbar" aria-label="Actions utilisateur">
          <!-- Actions principales (icônes avec tooltips) -->
          <div class="hidden md:flex items-center gap-1 border-r border-gray-200 pr-2 mr-2">
            <DarkModeToggle />
            
            <router-link
              to="/compare"
              class="relative p-2 text-gray-600 hover:text-gray-900 hover:bg-gray-50 rounded-lg transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-1"
              active-class="text-blue-600 bg-blue-50"
              title="Comparer les propriétés"
            >
              <Scale class="h-5 w-5" />
              <ComparisonBadge />
            </router-link>
            
            <router-link
              to="/favorites"
              class="p-2 text-gray-600 hover:text-gray-900 hover:bg-gray-50 rounded-lg transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-1"
              active-class="text-blue-600 bg-blue-50"
              title="Mes favoris"
            >
              <Star class="h-5 w-5" />
            </router-link>
            
            <button
              v-if="isAuthenticated"
              @click="showNotifications = !showNotifications"
              class="relative p-2 text-gray-600 hover:text-gray-900 hover:bg-gray-50 rounded-lg transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-1"
              title="Notifications"
            >
              <Bell class="h-5 w-5" />
              <span
                v-if="unreadCount > 0"
                class="absolute top-0 right-0 bg-red-600 text-white text-xs font-bold rounded-full w-5 h-5 flex items-center justify-center transform translate-x-1/2 -translate-y-1/2"
              >
                {{ unreadCount > 9 ? '9+' : unreadCount }}
              </span>
            </button>
          </div>

          <!-- Menu utilisateur -->
          <div class="relative" v-if="isAuthenticated">
            <button
              data-user-menu-button
              @click.stop="showUserMenu = !showUserMenu"
              class="flex items-center gap-2 px-3 py-2 text-sm font-medium text-gray-700 hover:text-gray-900 hover:bg-gray-50 rounded-lg transition-colors focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-1"
            >
              <div class="h-8 w-8 rounded-full bg-blue-100 flex items-center justify-center">
                <span class="text-blue-600 text-xs font-semibold">
                  {{ userName.charAt(0).toUpperCase() }}
                </span>
              </div>
              <span class="hidden lg:inline max-w-[120px] truncate">{{ userName }}</span>
              <ChevronDown class="h-4 w-4 text-gray-400 hidden lg:inline" />
            </button>

            <!-- Menu déroulant utilisateur -->
            <Transition
              enter-active-class="transition ease-out duration-100"
              enter-from-class="transform opacity-0 scale-95"
              enter-to-class="transform opacity-100 scale-100"
              leave-active-class="transition ease-in duration-75"
              leave-from-class="transform opacity-100 scale-100"
              leave-to-class="transform opacity-0 scale-95"
            >
              <div
                v-if="showUserMenu"
                v-click-outside="() => showUserMenu = false"
                class="absolute right-0 mt-2 w-56 bg-white rounded-lg shadow-lg border border-gray-200 py-1 z-50"
              >
                <router-link
                  to="/dashboard"
                  class="flex items-center gap-3 px-4 py-2 text-sm text-gray-700 hover:bg-gray-50 transition-colors"
                  @click="handleMenuClick"
                >
                  <LayoutDashboard class="h-4 w-4" />
                  Tableau de bord
                </router-link>
                <router-link
                  to="/my-properties"
                  class="flex items-center gap-3 px-4 py-2 text-sm text-gray-700 hover:bg-gray-50 transition-colors"
                  @click="handleMenuClick"
                >
                  <Home class="h-4 w-4" />
                  Mes annonces
                </router-link>
                <router-link
                  to="/my-messages"
                  class="flex items-center gap-3 px-4 py-2 text-sm text-gray-700 hover:bg-gray-50 transition-colors"
                  @click="handleMenuClick"
                >
                  <MessageSquare class="h-4 w-4" />
                  Messages
                </router-link>
                <router-link
                  to="/profile"
                  class="flex items-center gap-3 px-4 py-2 text-sm text-gray-700 hover:bg-gray-50 transition-colors"
                  @click="handleMenuClick"
                >
                  <User class="h-4 w-4" />
                  Mon profil
                </router-link>
                <router-link
                  to="/help"
                  class="flex items-center gap-3 px-4 py-2 text-sm text-gray-700 hover:bg-gray-50 transition-colors"
                  @click="handleMenuClick"
                >
                  <HelpCircle class="h-4 w-4" />
                  Aide & Support
                </router-link>
                <div class="border-t border-gray-200 my-1"></div>
                <button
                  @click="handleLogout"
                  class="w-full flex items-center gap-3 px-4 py-2 text-sm text-red-600 hover:bg-red-50 transition-colors text-left"
                >
                  <LogOut class="h-4 w-4" />
                  Déconnexion
                </button>
              </div>
            </Transition>
          </div>

          <!-- Bouton connexion -->
          <button
            v-if="!isAuthenticated"
            class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 active:bg-blue-800 transition-all duration-200 text-sm font-medium shadow-sm hover:shadow-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
            @click="router.push('/login')"
          >
            <span class="hidden sm:inline">Connexion</span>
            <span class="sm:hidden">Connexion</span>
          </button>
          
          <!-- Menu mobile -->
          <button
            @click="showMobileMenu = !showMobileMenu"
            class="lg:hidden p-2 text-gray-700 hover:text-gray-900 hover:bg-gray-50 rounded-lg transition-colors focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-1"
            aria-label="Menu de navigation"
            :aria-expanded="showMobileMenu"
          >
            <Menu v-if="!showMobileMenu" class="h-5 w-5" />
            <X v-else class="h-5 w-5" />
          </button>
        </div>
      </div>
    </div>

    <!-- Menu mobile -->
    <div
      v-if="showMobileMenu"
      class="lg:hidden border-t border-gray-200 bg-white"
    >
      <nav class="px-4 py-3 space-y-1">
        <router-link
          to="/search"
          class="block px-3 py-2 text-sm font-medium text-gray-700 hover:text-gray-900 hover:bg-gray-50 rounded-lg transition-colors"
          @click="showMobileMenu = false"
        >
          Recherche
        </router-link>
        <router-link
          to="/publish"
          class="block px-3 py-2 text-sm font-medium text-gray-700 hover:text-gray-900 hover:bg-gray-50 rounded-lg transition-colors"
          @click="showMobileMenu = false"
        >
          Publier
        </router-link>
        <router-link
          to="/about"
          class="block px-3 py-2 text-sm text-gray-700 hover:text-gray-900 hover:bg-gray-50 rounded-lg transition-colors"
          @click="showMobileMenu = false"
        >
          À propos
        </router-link>
        <router-link
          to="/agencies"
          class="block px-3 py-2 text-sm text-gray-700 hover:text-gray-900 hover:bg-gray-50 rounded-lg transition-colors"
          @click="showMobileMenu = false"
        >
          Agences
        </router-link>
        <div class="border-t border-gray-200 my-2"></div>
        <router-link
          to="/compare"
          class="flex items-center gap-2 px-3 py-2 text-sm text-gray-700 hover:text-gray-900 hover:bg-gray-50 rounded-lg transition-colors"
          @click="showMobileMenu = false"
        >
          <Scale class="h-4 w-4" />
          Comparer
        </router-link>
        <router-link
          to="/favorites"
          class="flex items-center gap-2 px-3 py-2 text-sm text-gray-700 hover:text-gray-900 hover:bg-gray-50 rounded-lg transition-colors"
          @click="showMobileMenu = false"
        >
          <Star class="h-4 w-4" />
          Favoris
        </router-link>
        <router-link
          to="/help"
          class="flex items-center gap-2 px-3 py-2 text-sm text-gray-700 hover:text-gray-900 hover:bg-gray-50 rounded-lg transition-colors"
          @click="showMobileMenu = false"
        >
          <HelpCircle class="h-4 w-4" />
          Aide
        </router-link>
      </nav>
    </div>

    <!-- Panel de notifications -->
    <NotificationsPanel
      v-if="showNotifications && isAuthenticated"
      @close="showNotifications = false"
    />
  </header>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, Transition } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { 
  Home, Scale, Star, HelpCircle, Menu, Bell, X, 
  ChevronDown, LayoutDashboard, MessageSquare, User, LogOut 
} from 'lucide-vue-next'
import ComparisonBadge from './ComparisonBadge.vue'
import DarkModeToggle from './DarkModeToggle.vue'
import NotificationsPanel from './NotificationsPanel.vue'
import { useAuthStore, tokenUtils, notificationService } from '@viridial/shared'

const router = useRouter()
const authStore = useAuthStore()

const showNotifications = ref(false)
const showUserMenu = ref(false)
const showMobileMenu = ref(false)
const unreadCount = ref(0)

// Directive pour fermer le menu au clic extérieur
interface ClickOutsideElement extends HTMLElement {
  clickOutsideEvent?: (event: MouseEvent) => void
}

const vClickOutside = {
  mounted(el: ClickOutsideElement, binding: any) {
    el.clickOutsideEvent = (event: MouseEvent) => {
      const target = event.target as Node
      // Ne pas fermer si le clic est dans le menu ou sur le bouton qui l'ouvre
      if (!el.contains(target) && !(target instanceof Element && target.closest('[data-user-menu-button]'))) {
        binding.value()
      }
    }
    // Utiliser capture pour intercepter avant que le clic ne se propage
    document.addEventListener('click', el.clickOutsideEvent, true)
  },
  unmounted(el: ClickOutsideElement) {
    if (el.clickOutsideEvent) {
      document.removeEventListener('click', el.clickOutsideEvent, true)
    }
  }
}

// Vérifier l'authentification au chargement
onMounted(async () => {
  await authStore.checkAuth()
  if (authStore.isAuthenticated) {
    await loadUnreadCount()
  }
})

// Fermer les menus lors de la navigation
router.afterEach(() => {
  showUserMenu.value = false
  showMobileMenu.value = false
  showNotifications.value = false
})

const isAuthenticated = computed(() => {
  return authStore.isAuthenticated || !!tokenUtils.getToken()
})

const userName = computed(() => {
  return authStore.user?.name || authStore.user?.email || 'Utilisateur'
})

function handleMenuClick() {
  // Fermer le menu après un court délai pour permettre la navigation
  setTimeout(() => {
    showUserMenu.value = false
  }, 100)
}

async function handleLogout() {
  await authStore.logout()
  showUserMenu.value = false
  router.push('/')
}

async function loadUnreadCount() {
  try {
    if (!authStore.user?.id) return
    const count = await notificationService.getUnreadCount(authStore.user.id)
    unreadCount.value = count || 0
  } catch (err) {
    console.warn('Could not load unread count:', err)
  }
}
</script>

