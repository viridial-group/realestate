<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2.5xl font-bold">Notifications</h1>
        <p class="text-muted-foreground mt-1">Gérez les notifications système et utilisateurs</p>
      </div>
      <div class="flex gap-2">
        <Button variant="outline" @click="markAllAsRead" :disabled="loading || unreadCount === 0">
          <CheckCircle class="mr-2 h-4 w-4" />
          Tout marquer comme lu
        </Button>
        <Button @click="sendNotification" size="lg">
          <Send class="mr-2 h-4 w-4" />
          Envoyer une notification
        </Button>
      </div>
    </div>

    <!-- Stats Cards -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
      <Card class="cursor-pointer hover:shadow-md transition-shadow" @click="filterByRead(false)">
        <CardHeader class="pb-2">
          <CardDescription>Non lues</CardDescription>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold text-primary">{{ unreadCount }}</div>
          <p class="text-xs text-muted-foreground mt-1">Notifications</p>
        </CardContent>
      </Card>
      <Card>
        <CardHeader class="pb-2">
          <CardDescription>Total</CardDescription>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold">{{ notifications.length }}</div>
          <p class="text-xs text-muted-foreground mt-1">Notifications</p>
        </CardContent>
      </Card>
      <Card>
        <CardHeader class="pb-2">
          <CardDescription>Aujourd'hui</CardDescription>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold">{{ todayCount }}</div>
          <p class="text-xs text-muted-foreground mt-1">Notifications</p>
        </CardContent>
      </Card>
      <Card>
        <CardHeader class="pb-2">
          <CardDescription>Cette Semaine</CardDescription>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold">{{ weekCount }}</div>
          <p class="text-xs text-muted-foreground mt-1">Notifications</p>
        </CardContent>
      </Card>
    </div>

    <!-- Filtres -->
    <Card>
      <CardContent class="p-4">
        <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
          <div class="space-y-2">
            <Label>Recherche</Label>
            <Input v-model="searchQuery" placeholder="Titre, message..." @input="handleSearch" />
          </div>
          <div class="space-y-2">
            <Label>Type</Label>
            <Select v-model="selectedType" @update:model-value="handleFilter">
              <SelectTrigger>
                <SelectValue placeholder="Tous" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="">Tous</SelectItem>
                <SelectItem value="SYSTEM">Système</SelectItem>
                <SelectItem value="USER">Utilisateur</SelectItem>
                <SelectItem value="ALERT">Alerte</SelectItem>
                <SelectItem value="INFO">Information</SelectItem>
              </SelectContent>
            </Select>
          </div>
          <div class="space-y-2">
            <Label>Statut</Label>
            <Select v-model="selectedRead" @update:model-value="handleFilter">
              <SelectTrigger>
                <SelectValue placeholder="Tous" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="">Tous</SelectItem>
                <SelectItem value="false">Non lues</SelectItem>
                <SelectItem value="true">Lues</SelectItem>
              </SelectContent>
            </Select>
          </div>
          <div class="flex items-end">
            <Button variant="outline" @click="resetFilters" class="w-full">
              <X class="mr-2 h-4 w-4" />
              Réinitialiser
            </Button>
          </div>
        </div>
      </CardContent>
    </Card>

    <!-- Liste des Notifications -->
    <Card>
      <CardContent class="p-0">
        <div class="divide-y divide-border">
          <div
            v-for="notification in filteredNotifications"
            :key="notification.id"
            :class="[
              'p-4 hover:bg-muted/50 transition-colors cursor-pointer',
              !notification.read ? 'bg-primary/5' : ''
            ]"
            @click="markAsRead(notification.id)"
          >
            <div class="flex items-start gap-4">
              <div
                :class="[
                  'flex-shrink-0 w-10 h-10 rounded-full flex items-center justify-center',
                  getTypeColorClass(notification.type)
                ]"
              >
                <component :is="getTypeIcon(notification.type)" class="h-5 w-5" />
              </div>
              <div class="flex-1 min-w-0">
                <div class="flex items-start justify-between">
                  <div>
                    <div class="flex items-center gap-2">
                      <h3 class="text-sm font-medium">{{ notification.title }}</h3>
                      <Badge v-if="!notification.read" variant="default" class="text-xs">Nouveau</Badge>
                    </div>
                    <p class="text-sm text-muted-foreground mt-1">{{ notification.message }}</p>
                  </div>
                  <div class="flex items-center gap-2 ml-4">
                    <span class="text-xs text-muted-foreground">{{ formatDateTime(notification.createdAt) }}</span>
                    <Button
                      variant="ghost"
                      size="sm"
                      @click.stop="deleteNotification(notification.id)"
                      class="text-destructive"
                    >
                      <Trash2 class="h-4 w-4" />
                    </Button>
                  </div>
                </div>
                <div v-if="notification.metadata" class="mt-2 text-xs text-muted-foreground">
                  <span v-for="(value, key) in notification.metadata" :key="key" class="mr-4">
                    {{ key }}: {{ value }}
                  </span>
                </div>
              </div>
            </div>
          </div>
          <div v-if="filteredNotifications.length === 0" class="p-8 text-center text-muted-foreground">
            Aucune notification trouvée
          </div>
        </div>
      </CardContent>
    </Card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useToast } from '@/components/ui/toast'
import { Card, CardContent, CardHeader, CardDescription } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { Badge } from '@/components/ui/badge'
import { CheckCircle, Send, X, Trash2, Bell, AlertCircle, Info, User } from 'lucide-vue-next'

const { toast } = useToast()
const loading = ref(false)
const searchQuery = ref('')
const selectedType = ref('')
const selectedRead = ref('')

interface Notification {
  id: number
  title: string
  message: string
  type: 'SYSTEM' | 'USER' | 'ALERT' | 'INFO'
  read: boolean
  createdAt: string
  metadata?: Record<string, any>
}

const notifications = ref<Notification[]>([])

const filteredNotifications = computed(() => {
  let filtered = [...notifications.value]

  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(
      (n) =>
        n.title?.toLowerCase().includes(query) ||
        n.message?.toLowerCase().includes(query)
    )
  }

  if (selectedType.value) {
    filtered = filtered.filter((n) => n.type === selectedType.value)
  }

  if (selectedRead.value !== '') {
    filtered = filtered.filter((n) => n.read === (selectedRead.value === 'true'))
  }

  // Sort by date (newest first)
  filtered.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())

  return filtered
})

const unreadCount = computed(() => notifications.value.filter((n) => !n.read).length)
const todayCount = computed(() => {
  const today = new Date().toDateString()
  return notifications.value.filter((n) => new Date(n.createdAt).toDateString() === today).length
})
const weekCount = computed(() => {
  const weekAgo = new Date()
  weekAgo.setDate(weekAgo.getDate() - 7)
  return notifications.value.filter((n) => new Date(n.createdAt) >= weekAgo).length
})

const handleSearch = () => {
  loadNotifications()
}

const handleFilter = () => {
  loadNotifications()
}

const resetFilters = () => {
  searchQuery.value = ''
  selectedType.value = ''
  selectedRead.value = ''
  loadNotifications()
}

const filterByRead = (read: boolean) => {
  selectedRead.value = String(read)
  loadNotifications()
}

const markAsRead = async (id: number) => {
  loading.value = true
  try {
    // TODO: Implement mark as read
    const notification = notifications.value.find((n) => n.id === id)
    if (notification) {
      notification.read = true
    }
    toast({
      title: 'Notification lue',
      description: 'La notification a été marquée comme lue'
    })
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Une erreur est survenue',
      variant: 'destructive'
    })
  } finally {
    loading.value = false
  }
}

const markAllAsRead = async () => {
  loading.value = true
  try {
    // TODO: Implement mark all as read
    notifications.value.forEach((n) => (n.read = true))
    toast({
      title: 'Toutes les notifications lues',
      description: 'Toutes les notifications ont été marquées comme lues'
    })
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Une erreur est survenue',
      variant: 'destructive'
    })
  } finally {
    loading.value = false
  }
}

const deleteNotification = async (id: number) => {
  loading.value = true
  try {
    // TODO: Implement delete
    notifications.value = notifications.value.filter((n) => n.id !== id)
    toast({
      title: 'Notification supprimée',
      description: 'La notification a été supprimée'
    })
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Une erreur est survenue',
      variant: 'destructive'
    })
  } finally {
    loading.value = false
  }
}

const sendNotification = () => {
  // TODO: Open dialog to send notification
  toast({
    title: 'Envoi de notification',
    description: 'Fonctionnalité à venir'
  })
}

const getTypeIcon = (type: string) => {
  const icons: Record<string, any> = {
    SYSTEM: Bell,
    USER: User,
    ALERT: AlertCircle,
    INFO: Info
  }
  return icons[type] || Bell
}

const getTypeColorClass = (type: string) => {
  const classes: Record<string, string> = {
    SYSTEM: 'bg-blue-100 text-blue-600 dark:bg-blue-900 dark:text-blue-300',
    USER: 'bg-green-100 text-green-600 dark:bg-green-900 dark:text-green-300',
    ALERT: 'bg-red-100 text-red-600 dark:bg-red-900 dark:text-red-300',
    INFO: 'bg-gray-100 text-gray-600 dark:bg-gray-900 dark:text-gray-300'
  }
  return classes[type] || 'bg-gray-100 text-gray-600'
}

const formatDateTime = (date: string) => {
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
  return d.toLocaleDateString('fr-FR')
}

const loadNotifications = async () => {
  loading.value = true
  try {
    // TODO: Load from API
    notifications.value = []
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Impossible de charger les notifications',
      variant: 'destructive'
    })
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadNotifications()
})
</script>

