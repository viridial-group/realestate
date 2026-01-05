<template>
  <div class="min-h-screen bg-gray-50 py-8">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- Header -->
      <div class="mb-8">
        <h1 class="text-3xl font-bold text-gray-900">Mes messages</h1>
        <p class="mt-2 text-sm text-gray-600">
          Gérez tous les contacts reçus pour vos annonces
        </p>
      </div>

      <!-- Filtres -->
      <div class="mb-6 bg-white rounded-lg shadow-sm p-4">
        <div class="flex flex-wrap items-center gap-4">
          <div class="flex items-center gap-2">
            <label class="text-sm font-medium text-gray-700">Propriété:</label>
            <select
              v-model="propertyFilter"
              @change="() => loadMessages()"
              class="px-3 py-2 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="">Toutes</option>
              <option
                v-for="property in myProperties"
                :key="property.id"
                :value="property.id"
              >
                {{ property.title }}
              </option>
            </select>
          </div>

          <div class="flex items-center gap-2">
            <label class="text-sm font-medium text-gray-700">Statut:</label>
            <select
              v-model="statusFilter"
              @change="() => loadMessages()"
              class="px-3 py-2 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="">Tous</option>
              <option value="NEW">Non lus</option>
              <option value="READ">Lus</option>
              <option value="REPLIED">Répondus</option>
            </select>
          </div>

          <button
            v-if="propertyFilter || statusFilter"
            @click="clearFilters"
            class="px-3 py-2 text-sm text-gray-700 hover:bg-gray-100 rounded-md transition-colors"
          >
            Réinitialiser
          </button>
        </div>
      </div>

      <!-- Statistiques rapides -->
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
        <div class="bg-white rounded-lg shadow-sm p-4">
          <p class="text-sm text-gray-600 mb-1">Total messages</p>
          <p class="text-2xl font-bold text-gray-900">{{ stats.total }}</p>
        </div>
        <div class="bg-white rounded-lg shadow-sm p-4">
          <p class="text-sm text-gray-600 mb-1">Non lus</p>
          <p class="text-2xl font-bold text-blue-600">{{ stats.unread }}</p>
        </div>
        <div class="bg-white rounded-lg shadow-sm p-4">
          <p class="text-sm text-gray-600 mb-1">Répondus</p>
          <p class="text-2xl font-bold text-green-600">{{ stats.replied }}</p>
        </div>
        <div class="bg-white rounded-lg shadow-sm p-4">
          <p class="text-sm text-gray-600 mb-1">Ce mois</p>
          <p class="text-2xl font-bold text-gray-900">{{ stats.thisMonth }}</p>
        </div>
      </div>

      <!-- Liste des messages -->
      <div v-if="loading" class="flex justify-center py-12">
        <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
      </div>

      <div v-else-if="error" class="bg-red-50 border border-red-200 rounded-lg p-4">
        <p class="text-red-800">{{ error }}</p>
        <button
          @click="() => loadMessages()"
          class="mt-2 text-sm text-red-600 hover:text-red-800 underline"
        >
          Réessayer
        </button>
      </div>

      <div v-else-if="messages.length === 0" class="bg-white rounded-lg shadow-sm p-12 text-center">
        <Mail class="h-12 w-12 text-gray-400 mx-auto mb-4" />
        <h3 class="text-lg font-medium text-gray-900 mb-2">Aucun message</h3>
        <p class="text-gray-600">
          Vous n'avez pas encore reçu de messages pour vos annonces.
        </p>
      </div>

      <div v-else class="space-y-4">
        <div
          v-for="message in messages"
          :key="message.id"
          :class="[
            'bg-white rounded-lg shadow-sm p-6 cursor-pointer hover:shadow-md transition-shadow',
            !message.readAt ? 'border-l-4 border-blue-600' : ''
          ]"
          @click="openMessage(message)"
        >
          <div class="flex items-start justify-between">
            <div class="flex-1">
              <div class="flex items-center gap-3 mb-2">
                <h3 class="text-lg font-semibold text-gray-900">{{ message.subject }}</h3>
                <span
                  v-if="!message.readAt"
                  class="px-2 py-1 text-xs font-medium bg-blue-100 text-blue-800 rounded"
                >
                  Nouveau
                </span>
                <span
                  v-if="message.repliedAt"
                  class="px-2 py-1 text-xs font-medium bg-green-100 text-green-800 rounded"
                >
                  Répondu
                </span>
              </div>
              <div class="flex items-center gap-4 text-sm text-gray-600 mb-2">
                <span class="flex items-center gap-1">
                  <User class="h-4 w-4" />
                  {{ message.name }}
                </span>
                <span class="flex items-center gap-1">
                  <Mail class="h-4 w-4" />
                  {{ message.email }}
                </span>
                <span v-if="message.phone" class="flex items-center gap-1">
                  <Phone class="h-4 w-4" />
                  {{ message.phone }}
                </span>
              </div>
              <p v-if="message.propertyTitle" class="text-sm text-gray-600 mb-2">
                📍 Propriété: {{ message.propertyTitle }}
              </p>
              <p class="text-gray-700 line-clamp-2">{{ message.message }}</p>
              <p class="text-xs text-gray-500 mt-3">
                {{ formatDate(message.createdAt) }}
              </p>
            </div>
            <div class="ml-4 flex items-center gap-2">
              <button
                @click.stop="markAsRead(message)"
                v-if="!message.readAt"
                class="p-2 text-gray-400 hover:text-blue-600 transition-colors"
                title="Marquer comme lu"
              >
                <CheckCircle class="h-5 w-5" />
              </button>
              <button
                @click.stop="replyToMessage(message)"
                class="px-3 py-2 text-sm bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors"
              >
                Répondre
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Pagination -->
      <div v-if="pagination.totalPages > 1" class="mt-6 flex items-center justify-center gap-2">
        <button
          @click="loadMessages(pagination.currentPage - 1)"
          :disabled="pagination.currentPage === 0"
          class="px-4 py-2 border border-gray-300 rounded-md hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          Précédent
        </button>
        <span class="px-4 py-2 text-sm text-gray-700">
          Page {{ pagination.currentPage + 1 }} sur {{ pagination.totalPages }}
        </span>
        <button
          @click="loadMessages(pagination.currentPage + 1)"
          :disabled="pagination.currentPage >= pagination.totalPages - 1"
          class="px-4 py-2 border border-gray-300 rounded-md hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          Suivant
        </button>
      </div>
    </div>

    <!-- Modal de détail du message -->
    <div
      v-if="selectedMessage"
      class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4"
      @click.self="selectedMessage = null"
    >
      <div class="bg-white rounded-lg shadow-xl max-w-2xl w-full max-h-[90vh] overflow-y-auto">
        <div class="p-6">
          <div class="flex items-center justify-between mb-4">
            <h2 class="text-2xl font-bold text-gray-900">{{ selectedMessage.subject }}</h2>
            <button
              @click="selectedMessage = null"
              class="text-gray-400 hover:text-gray-600"
            >
              <X class="h-6 w-6" />
            </button>
          </div>

          <div class="space-y-4">
            <div>
              <p class="text-sm text-gray-600 mb-1">De</p>
              <p class="font-medium text-gray-900">{{ selectedMessage.name }}</p>
              <p class="text-sm text-gray-600">{{ selectedMessage.email }}</p>
              <p v-if="selectedMessage.phone" class="text-sm text-gray-600">{{ selectedMessage.phone }}</p>
            </div>

            <div v-if="selectedMessage.propertyTitle">
              <p class="text-sm text-gray-600 mb-1">Propriété concernée</p>
              <p class="font-medium text-gray-900">{{ selectedMessage.propertyTitle }}</p>
              <router-link
                v-if="selectedMessage.propertyId"
                :to="`/my-properties/${selectedMessage.propertyId}`"
                class="text-sm text-blue-600 hover:text-blue-700"
              >
                Voir l'annonce →
              </router-link>
            </div>

            <div>
              <p class="text-sm text-gray-600 mb-1">Message</p>
              <p class="text-gray-700 whitespace-pre-wrap">{{ selectedMessage.message }}</p>
            </div>

            <div class="flex items-center gap-2 pt-4 border-t">
              <button
                @click="replyToMessage(selectedMessage)"
                class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors"
              >
                Répondre par email
              </button>
              <button
                v-if="!selectedMessage.readAt"
                @click="markAsRead(selectedMessage)"
                class="px-4 py-2 border border-gray-300 text-gray-700 rounded-md hover:bg-gray-50 transition-colors"
              >
                Marquer comme lu
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { Mail, User, Phone, CheckCircle, X } from 'lucide-vue-next'
import { contactService, type ContactMessage } from '@viridial/shared'
import { userPropertyService } from '@/api/user-property.service'
import { useToast } from '@/composables/useToast'

const { showToast } = useToast()

const messages = ref<ContactMessage[]>([])
const myProperties = ref<any[]>([])
const loading = ref(false)
const error = ref<string | null>(null)
const propertyFilter = ref('')
const statusFilter = ref('')
const selectedMessage = ref<ContactMessage | null>(null)

const pagination = ref({
  currentPage: 0,
  totalPages: 0,
  totalElements: 0,
  size: 20,
})

const stats = computed(() => {
  const total = messages.value.length
  const unread = messages.value.filter(m => !m.readAt).length
  const replied = messages.value.filter(m => m.repliedAt).length
  const thisMonth = messages.value.filter(m => {
    const date = new Date(m.createdAt)
    const now = new Date()
    return date.getMonth() === now.getMonth() && date.getFullYear() === now.getFullYear()
  }).length

  return { total, unread, replied, thisMonth }
})

onMounted(async () => {
  await loadMyProperties()
  await loadMessages()
})

async function loadMyProperties() {
  try {
    const response = await userPropertyService.getMyProperties({ page: 0, size: 100 })
    myProperties.value = response.content || []
  } catch (err) {
    console.error('Error loading properties:', err)
  }
}

async function loadMessages(page = 0) {
  loading.value = true
  error.value = null

  try {
    // Charger tous les messages pour toutes les propriétés de l'utilisateur
    const allMessages: ContactMessage[] = []
    
    for (const property of myProperties.value) {
      try {
        const propertyMessages = await contactService.getByProperty(property.id)
        allMessages.push(...propertyMessages)
      } catch (err) {
        console.error(`Error loading messages for property ${property.id}:`, err)
      }
    }

    // Filtrer
    let filtered = allMessages
    if (propertyFilter.value) {
      filtered = filtered.filter(m => m.propertyId === Number(propertyFilter.value))
    }
    if (statusFilter.value) {
      if (statusFilter.value === 'NEW') {
        filtered = filtered.filter(m => !m.readAt)
      } else if (statusFilter.value === 'READ') {
        filtered = filtered.filter(m => m.readAt && !m.repliedAt)
      } else if (statusFilter.value === 'REPLIED') {
        filtered = filtered.filter(m => m.repliedAt)
      }
    }

    // Trier par date (plus récent en premier)
    filtered.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())

    // Pagination
    const start = page * pagination.value.size
    const end = start + pagination.value.size
    messages.value = filtered.slice(start, end)
    
    pagination.value.currentPage = page
    pagination.value.totalPages = Math.ceil(filtered.length / pagination.value.size)
    pagination.value.totalElements = filtered.length
  } catch (err: any) {
    error.value = err.response?.data?.message || err.message || 'Erreur lors du chargement des messages'
    showToast(error.value || 'Erreur lors du chargement', 'error')
  } finally {
    loading.value = false
  }
}

function clearFilters() {
  propertyFilter.value = ''
  statusFilter.value = ''
  loadMessages(0)
}

function openMessage(message: ContactMessage) {
  selectedMessage.value = message
  if (!message.readAt) {
    markAsRead(message)
  }
}

async function markAsRead(message: ContactMessage) {
  try {
    await contactService.markAsRead(message.id)
    message.readAt = new Date().toISOString()
    showToast('Message marqué comme lu', 'success')
  } catch (err: any) {
    showToast('Erreur lors du marquage', 'error')
  }
}

function replyToMessage(message: ContactMessage) {
  const subject = `Re: ${message.subject}`
  const body = `Bonjour ${message.name},\n\n\n\n---\nMessage original:\n${message.message}`
  window.location.href = `mailto:${message.email}?subject=${encodeURIComponent(subject)}&body=${encodeURIComponent(body)}`
}

function formatDate(dateString: string): string {
  const date = new Date(dateString)
  return date.toLocaleDateString('fr-FR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}
</script>

