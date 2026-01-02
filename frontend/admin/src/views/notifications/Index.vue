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
                <SelectItem value="all">Tous</SelectItem>
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
                <SelectItem value="all">Tous</SelectItem>
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
              !isNotificationRead(notification) ? 'bg-primary/5' : ''
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
                      <Badge v-if="!isNotificationRead(notification)" variant="default" class="text-xs">Nouveau</Badge>
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
                  <template v-if="typeof notification.metadata === 'string'">
                    {{ notification.metadata }}
                  </template>
                  <template v-else>
                    <span v-for="(value, key) in notification.metadata" :key="key" class="mr-4">
                      {{ key }}: {{ value }}
                    </span>
                  </template>
                </div>
              </div>
            </div>
          </div>
          <div v-if="filteredNotifications.length === 0" class="p-8 text-center text-muted-foreground">
            <p class="text-lg font-medium mb-2">Aucune notification trouvée</p>
            <p class="text-sm">
              {{ notifications.length === 0 
                ? 'Vous n\'avez pas encore de notifications. Les notifications que vous recevez apparaîtront ici.' 
                : 'Aucune notification ne correspond aux filtres sélectionnés.' }}
            </p>
            <p v-if="notifications.length === 0" class="text-xs mt-2 text-muted-foreground/70">
              Note: Les notifications que vous envoyez à d'autres utilisateurs n'apparaissent pas dans votre liste.
            </p>
          </div>
        </div>
      </CardContent>
    </Card>

    <!-- Dialog pour envoyer une notification -->
    <Dialog :open="showSendDialog" @update:open="showSendDialog = $event">
      <DialogContent class="max-w-2xl max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>Envoyer une notification</DialogTitle>
          <DialogDescription>
            Créez et envoyez une nouvelle notification à un utilisateur
          </DialogDescription>
        </DialogHeader>

        <form @submit.prevent="handleSendNotification" class="space-y-4">
          <div class="space-y-2">
            <Label for="organization">Organisation *</Label>
            <Select v-model="sendForm.organizationId" @update:model-value="(value) => onOrganizationChange(String(value || ''))" required>
              <SelectTrigger>
                <SelectValue placeholder="Sélectionner une organisation" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem
                  v-for="org in organizations"
                  :key="org.id"
                  :value="String(org.id)"
                >
                  {{ org.name }}
                </SelectItem>
              </SelectContent>
            </Select>
          </div>

          <div class="space-y-2">
            <Label for="recipient">Destinataire *</Label>
            <Select v-model="sendForm.recipientId" required :disabled="!sendForm.organizationId || organizationUsers.length === 0">
              <SelectTrigger>
                <SelectValue placeholder="Sélectionner un destinataire" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem
                  v-for="orgUser in organizationUsers"
                  :key="orgUser.id"
                  :value="String(orgUser.userId)"
                >
                  {{ orgUser.userName || orgUser.userEmail || orgUser.user?.name || orgUser.user?.email || `Utilisateur ${orgUser.userId}` }}
                  <span v-if="orgUser.userEmail && !orgUser.userName" class="text-muted-foreground ml-2">
                    ({{ orgUser.userEmail }})
                  </span>
                </SelectItem>
              </SelectContent>
            </Select>
            <p v-if="!sendForm.organizationId" class="text-xs text-muted-foreground">
              Sélectionnez d'abord une organisation
            </p>
            <p v-else-if="organizationUsers.length === 0" class="text-xs text-muted-foreground">
              Aucun utilisateur trouvé dans cette organisation
            </p>
          </div>

          <div class="space-y-2">
            <Label for="type">Type *</Label>
            <Select v-model="sendForm.type" required>
              <SelectTrigger>
                <SelectValue placeholder="Sélectionner un type" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="SYSTEM">Système</SelectItem>
                <SelectItem value="USER">Utilisateur</SelectItem>
                <SelectItem value="ALERT">Alerte</SelectItem>
                <SelectItem value="INFO">Information</SelectItem>
                <SelectItem value="WARNING">Avertissement</SelectItem>
                <SelectItem value="ERROR">Erreur</SelectItem>
                <SelectItem value="SUCCESS">Succès</SelectItem>
              </SelectContent>
            </Select>
          </div>

          <div class="space-y-2">
            <Label for="channel">Canal</Label>
            <Select v-model="sendForm.channel">
              <SelectTrigger>
                <SelectValue placeholder="Sélectionner un canal (optionnel)" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="IN_APP">Dans l'application</SelectItem>
                <SelectItem value="EMAIL">Email</SelectItem>
                <SelectItem value="PUSH">Push</SelectItem>
                <SelectItem value="SMS">SMS</SelectItem>
              </SelectContent>
            </Select>
          </div>

          <div class="space-y-2">
            <Label for="title">Titre *</Label>
            <Input
              id="title"
              v-model="sendForm.title"
              placeholder="Titre de la notification"
              required
            />
          </div>

          <div class="space-y-2">
            <Label for="message">Message *</Label>
            <Textarea
              id="message"
              v-model="sendForm.message"
              placeholder="Message de la notification"
              class="min-h-[100px]"
              required
            />
          </div>

          <div class="grid grid-cols-2 gap-4">
            <div class="space-y-2">
              <Label for="targetType">Type de cible (optionnel)</Label>
              <Input
                id="targetType"
                v-model="sendForm.targetType"
                placeholder="Ex: PROPERTY, USER, etc."
              />
            </div>
            <div class="space-y-2">
              <Label for="targetId">ID de la cible (optionnel)</Label>
              <Input
                id="targetId"
                v-model.number="sendForm.targetId"
                type="number"
                placeholder="ID numérique"
              />
            </div>
          </div>

          <DialogFooter>
            <Button type="button" variant="outline" @click="showSendDialog = false">
              Annuler
            </Button>
            <Button type="submit" :disabled="sendingNotification || !canSendNotification">
              <Loader2 v-if="sendingNotification" class="mr-2 h-4 w-4 animate-spin" />
              <Send v-else class="mr-2 h-4 w-4" />
              {{ sendingNotification ? 'Envoi...' : 'Envoyer' }}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useToast } from '@/components/ui/toast'
import {
  notificationService,
  organizationService,
  useAuthStore,
  type Notification,
  type Organization
} from '@viridial/shared'
import { Card, CardContent, CardHeader, CardDescription } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Textarea } from '@/components/ui/textarea'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { Badge } from '@/components/ui/badge'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle
} from '@/components/ui/dialog'
import { CheckCircle, Send, X, Trash2, Bell, AlertCircle, Info, User as UserIcon, Loader2 } from 'lucide-vue-next'

const { toast } = useToast()
const authStore = useAuthStore()
const loading = ref(false)
const searchQuery = ref('')
const selectedType = ref('all')
const selectedRead = ref('all')

const notifications = ref<Notification[]>([])

// Dialog pour envoyer une notification
const showSendDialog = ref(false)
const sendingNotification = ref(false)
const organizations = ref<Organization[]>([])
const organizationUsers = ref<any[]>([])

const sendForm = ref({
  type: 'INFO',
  title: '',
  message: '',
  recipientId: '',
  organizationId: '',
  channel: 'IN_APP' as 'IN_APP' | 'PUSH' | 'SMS' | 'EMAIL' | undefined,
  targetType: '',
  targetId: undefined as number | undefined
})

const canSendNotification = computed(() => {
  return (
    sendForm.value.type &&
    sendForm.value.title.trim() &&
    sendForm.value.message.trim() &&
    sendForm.value.recipientId &&
    sendForm.value.organizationId
  )
})

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

  if (selectedType.value && selectedType.value !== 'all') {
    filtered = filtered.filter((n) => n.type === selectedType.value)
  }

  if (selectedRead.value && selectedRead.value !== 'all') {
    const isRead = selectedRead.value === 'true'
    filtered = filtered.filter((n) => {
      const read = n.read || !!n.readAt || n.status === 'READ'
      return read === isRead
    })
  }

  // Sort by date (newest first)
  filtered.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())

  return filtered
})

const unreadCount = computed(() => {
  return notifications.value.filter((n) => {
    // Vérifier si la notification est lue via readAt ou status
    return !n.read && !n.readAt && n.status !== 'READ'
  }).length
})
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
  selectedType.value = 'all'
  selectedRead.value = 'all'
  loadNotifications()
}

const filterByRead = (read: boolean) => {
  selectedRead.value = String(read)
  loadNotifications()
}

const markAsRead = async (id: number) => {
  loading.value = true
  try {
    const updated = await notificationService.markAsRead(id)
    // Mettre à jour la notification dans la liste
    const index = notifications.value.findIndex((n) => n.id === id)
    if (index !== -1) {
      notifications.value[index] = updated
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
  if (!authStore.user?.id) {
    toast({
      title: 'Erreur',
      description: 'Utilisateur non connecté',
      variant: 'destructive'
    })
    return
  }
  
  loading.value = true
  try {
    await notificationService.markAllAsRead(authStore.user.id)
    // Recharger les notifications pour avoir les données à jour
    await loadNotifications()
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
  if (!confirm('Êtes-vous sûr de vouloir supprimer cette notification ?')) {
    return
  }
  
  loading.value = true
  try {
    await notificationService.deleteNotification(id)
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
  // Réinitialiser le formulaire
  sendForm.value = {
    type: 'INFO',
    title: '',
    message: '',
    recipientId: '',
    organizationId: authStore.user?.organizationId?.toString() || '',
    channel: 'IN_APP',
    targetType: '',
    targetId: undefined
  }
  organizationUsers.value = []
  showSendDialog.value = true
}

const onOrganizationChange = async (organizationId: string) => {
  if (!organizationId) {
    organizationUsers.value = []
    sendForm.value.recipientId = ''
    return
  }

  try {
    const users = await organizationService.getUsersByOrganization(Number(organizationId))
    organizationUsers.value = users.filter((u) => u.active) // Filtrer uniquement les utilisateurs actifs
    // Réinitialiser le destinataire si l'organisation change
    if (sendForm.value.recipientId) {
      const userExists = users.some((u) => String(u.userId) === sendForm.value.recipientId)
      if (!userExists) {
        sendForm.value.recipientId = ''
      }
    }
  } catch (error: any) {
    console.error('Error loading organization users:', error)
    toast({
      title: 'Erreur',
      description: 'Impossible de charger les utilisateurs de l\'organisation',
      variant: 'destructive'
    })
    organizationUsers.value = []
    sendForm.value.recipientId = ''
  }
}

const handleSendNotification = async () => {
  if (!canSendNotification.value || !authStore.user?.id) {
    return
  }

  sendingNotification.value = true
  try {
    const recipientId = Number(sendForm.value.recipientId)
    const isSendingToSelf = recipientId === authStore.user.id

    await notificationService.sendNotification({
      type: sendForm.value.type,
      title: sendForm.value.title.trim(),
      message: sendForm.value.message.trim(),
      recipientId: recipientId,
      organizationId: Number(sendForm.value.organizationId),
      senderId: authStore.user.id,
      channel: sendForm.value.channel,
      targetType: sendForm.value.targetType || undefined,
      targetId: sendForm.value.targetId
    })

    // Fermer le dialog
    showSendDialog.value = false

    // Recharger les notifications seulement si on envoie à soi-même
    if (isSendingToSelf) {
      await loadNotifications()
      toast({
        title: 'Notification envoyée',
        description: 'La notification a été envoyée et apparaît dans votre liste'
      })
    } else {
      // Si on envoie à quelqu'un d'autre, ne pas recharger mais informer l'utilisateur
      toast({
        title: 'Notification envoyée',
        description: 'La notification a été envoyée au destinataire. Elle apparaîtra dans sa liste de notifications.'
      })
    }
  } catch (error: any) {
    console.error('Error sending notification:', error)
    toast({
      title: 'Erreur',
      description: error.message || 'Impossible d\'envoyer la notification',
      variant: 'destructive'
    })
  } finally {
    sendingNotification.value = false
  }
}

const loadOrganizations = async () => {
  try {
    const result = await organizationService.getAll()
    organizations.value = result.organizations
  } catch (error: any) {
    console.error('Error loading organizations:', error)
  }
}

const isNotificationRead = (notification: Notification): boolean => {
  return notification.read || !!notification.readAt || notification.status === 'READ'
}

const getTypeIcon = (type: string) => {
  const icons: Record<string, any> = {
    SYSTEM: Bell,
    USER: UserIcon,
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
  if (!authStore.user?.id) {
    console.warn('No user ID available for loading notifications')
    notifications.value = []
    return
  }
  
  loading.value = true
  try {
    console.log('Loading notifications for recipientId:', authStore.user.id)
    const allNotifications = await notificationService.getNotifications({
      recipientId: authStore.user.id
    })
    console.log('Loaded notifications:', allNotifications.length, allNotifications)
    notifications.value = allNotifications
  } catch (error: any) {
    console.error('Error loading notifications:', error)
    toast({
      title: 'Erreur',
      description: error.message || 'Impossible de charger les notifications',
      variant: 'destructive'
    })
    notifications.value = []
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadNotifications()
  loadOrganizations()
})

// Charger les utilisateurs quand l'organisation est sélectionnée
watch(
  () => sendForm.value.organizationId,
  (newOrgId) => {
    if (newOrgId) {
      onOrganizationChange(newOrgId)
    }
  }
)
</script>

