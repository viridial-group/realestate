<template>
  <div class="fixed top-16 right-4 z-50 w-80 max-h-96 overflow-y-auto bg-white rounded-lg shadow-xl border border-gray-200">
    <div class="p-4 border-b border-gray-200 flex items-center justify-between">
      <h3 class="text-lg font-semibold text-gray-900">Notifications</h3>
      <button
        @click="$emit('close')"
        class="text-gray-400 hover:text-gray-600"
      >
        <X class="h-5 w-5" />
      </button>
    </div>

    <div v-if="loading" class="p-8 text-center">
      <div class="animate-spin rounded-full h-6 w-6 border-b-2 border-blue-600 mx-auto"></div>
    </div>

    <div v-else-if="notifications.length === 0" class="p-8 text-center">
      <Bell class="h-12 w-12 text-gray-400 mx-auto mb-3" />
      <p class="text-sm text-gray-600">Aucune notification</p>
    </div>

    <div v-else class="divide-y divide-gray-200">
      <div
        v-for="notification in notifications"
        :key="notification.id"
        :class="[
          'p-4 hover:bg-gray-50 cursor-pointer transition-colors',
          !notification.read ? 'bg-blue-50' : ''
        ]"
        @click="handleNotificationClick(notification)"
      >
        <div class="flex items-start gap-3">
          <div
            :class="[
              'w-2 h-2 rounded-full mt-2 flex-shrink-0',
              !notification.read ? 'bg-blue-600' : 'bg-gray-300'
            ]"
          />
          <div class="flex-1 min-w-0">
            <p class="text-sm font-medium text-gray-900">{{ notification.title }}</p>
            <p class="text-sm text-gray-600 mt-1 line-clamp-2">{{ notification.message }}</p>
            <p class="text-xs text-gray-500 mt-2">{{ formatDate(notification.createdAt) }}</p>
          </div>
        </div>
      </div>
    </div>

    <div v-if="notifications.length > 0" class="p-4 border-t border-gray-200">
      <button
        @click="markAllAsRead"
        class="w-full px-4 py-2 text-sm text-blue-600 hover:bg-blue-50 rounded-md transition-colors"
      >
        Tout marquer comme lu
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { X, Bell } from 'lucide-vue-next'
import { notificationService } from '@viridial/shared'
import type { Notification } from '@viridial/shared'

defineEmits<{
  close: []
}>()

const notifications = ref<Notification[]>([])
const loading = ref(false)

onMounted(async () => {
  await loadNotifications()
})

async function loadNotifications() {
  loading.value = true
  try {
    // Récupérer l'ID de l'utilisateur depuis le store
    const { useAuthStore } = await import('@viridial/shared')
    const authStore = useAuthStore()
    
    if (!authStore.user?.id) {
      return
    }

    // Récupérer les notifications non lues
    const allNotifications = await notificationService.getNotifications({
      recipientId: authStore.user.id,
      unread: true
    })
    notifications.value = allNotifications.slice(0, 10) // Limiter à 10
  } catch (err) {
    console.error('Error loading notifications:', err)
  } finally {
    loading.value = false
  }
}

async function handleNotificationClick(notification: Notification) {
  if (!notification.read) {
    try {
      await notificationService.markAsRead(notification.id)
      notification.read = true
    } catch (err) {
      console.error('Error marking notification as read:', err)
    }
  }

  // Rediriger selon le type de notification
  if (notification.targetType === 'Property' && notification.targetId) {
    // Rediriger vers la page de détail de la propriété
    window.location.href = `/my-properties/${notification.targetId}`
  }
}

async function markAllAsRead() {
  try {
    const { useAuthStore } = await import('@viridial/shared')
    const authStore = useAuthStore()
    
    if (!authStore.user?.id) return

    const unreadNotifications = notifications.value.filter(n => !n.read)
    await Promise.all(
      unreadNotifications.map(n => notificationService.markAsRead(n.id))
    )
    notifications.value.forEach(n => n.read = true)
  } catch (err) {
    console.error('Error marking all as read:', err)
  }
}

function formatDate(dateString: string): string {
  const date = new Date(dateString)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)

  if (minutes < 1) return 'À l\'instant'
  if (minutes < 60) return `Il y a ${minutes} min`
  if (hours < 24) return `Il y a ${hours} h`
  if (days < 7) return `Il y a ${days} j`
  return date.toLocaleDateString('fr-FR', { day: 'numeric', month: 'short' })
}
</script>

