<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="flex items-center justify-between flex-wrap gap-4">
      <div class="flex-1">
        <div class="flex items-center gap-3">
          <h1 class="text-2.5xl font-bold">Messages de Contact - Agences</h1>
          <Badge 
            v-if="stats.new > 0" 
            variant="default" 
            class="bg-blue-500 text-white px-3 py-1 text-sm font-semibold"
          >
            <Mail class="mr-1 h-4 w-4" />
            {{ stats.new }} {{ stats.new === 1 ? 'message non lu' : 'messages non lus' }}
          </Badge>
        </div>
        <p class="text-muted-foreground mt-1">
          Gérez les messages de contact reçus par les agences
          <span v-if="stats.total > 0" class="ml-2 text-sm">
            ({{ stats.total }} {{ stats.total === 1 ? 'message au total' : 'messages au total' }})
          </span>
        </p>
      </div>
      <div class="flex items-center gap-2">
        <Button variant="outline" @click="loadMessages" :disabled="loading">
          <RefreshCw class="mr-2 h-4 w-4" :class="{ 'animate-spin': loading }" />
          Actualiser
        </Button>
      </div>
    </div>

    <!-- Stats Cards -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
      <!-- Nouveaux messages -->
      <Card class="cursor-pointer hover:shadow-md transition-shadow relative overflow-hidden border-l-4 border-l-blue-500" @click="filterByStatus('NEW')">
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardDescription>Nouveaux</CardDescription>
          <div class="h-10 w-10 rounded-lg flex items-center justify-center bg-blue-500/10">
            <Mail class="h-5 w-5 text-blue-500" />
          </div>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold text-blue-500">{{ stats.new || 0 }}</div>
          <p class="text-xs text-muted-foreground mt-1">Non lus</p>
        </CardContent>
      </Card>

      <!-- Messages lus -->
      <Card class="cursor-pointer hover:shadow-md transition-shadow relative overflow-hidden border-l-4 border-l-gray-500" @click="filterByStatus('READ')">
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardDescription>Lus</CardDescription>
          <div class="h-10 w-10 rounded-lg flex items-center justify-center bg-gray-500/10">
            <MailOpen class="h-5 w-5 text-gray-500" />
          </div>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold text-gray-500">{{ stats.read || 0 }}</div>
          <p class="text-xs text-muted-foreground mt-1">Messages lus</p>
        </CardContent>
      </Card>

      <!-- Messages répondus -->
      <Card class="cursor-pointer hover:shadow-md transition-shadow relative overflow-hidden border-l-4 border-l-green-500" @click="filterByStatus('REPLIED')">
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardDescription>Répondus</CardDescription>
          <div class="h-10 w-10 rounded-lg flex items-center justify-center bg-green-500/10">
            <CheckCircle class="h-5 w-5 text-green-500" />
          </div>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold text-green-500">{{ stats.replied || 0 }}</div>
          <p class="text-xs text-muted-foreground mt-1">Messages répondus</p>
        </CardContent>
      </Card>

      <!-- Total -->
      <Card class="relative overflow-hidden border-l-4 border-l-[hsl(var(--chart-1))]">
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardDescription>Total</CardDescription>
          <div class="h-10 w-10 rounded-lg bg-[hsl(var(--chart-1))]/10 flex items-center justify-center">
            <Mail class="h-5 w-5 text-[hsl(var(--chart-1))]" />
          </div>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold">{{ stats.total || 0 }}</div>
          <p class="text-xs text-muted-foreground mt-1">Tous les messages</p>
        </CardContent>
      </Card>
    </div>

    <!-- Filtres -->
    <Card>
      <CardContent class="p-4">
        <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
          <div class="space-y-2">
            <Label>Recherche</Label>
            <Input v-model="searchQuery" placeholder="Nom, email, sujet..." @input="handleSearch" />
          </div>
          <div class="space-y-2">
            <Label>Statut</Label>
            <Select v-model="selectedStatus" @update:model-value="handleFilter">
              <SelectTrigger>
                <SelectValue placeholder="Tous" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem :value="null">Tous</SelectItem>
                <SelectItem value="NEW">Nouveau</SelectItem>
                <SelectItem value="READ">Lu</SelectItem>
                <SelectItem value="REPLIED">Répondu</SelectItem>
                <SelectItem value="ARCHIVED">Archivé</SelectItem>
              </SelectContent>
            </Select>
          </div>
          <div class="space-y-2">
            <Label>Organisation</Label>
            <Select v-model="selectedOrganizationId" @update:model-value="handleFilter">
              <SelectTrigger>
                <SelectValue placeholder="Toutes" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem :value="null">Toutes</SelectItem>
                <SelectItem 
                  v-for="org in organizations" 
                  :key="org.id" 
                  :value="org.id"
                >
                  {{ org.name }}
                </SelectItem>
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

    <!-- Vue Tableau -->
    <Card>
      <CardContent class="p-0">
        <div v-if="loading" class="p-8 text-center">
          <p class="text-muted-foreground">Chargement...</p>
        </div>
        <div v-else-if="error" class="p-8 text-center">
          <p class="text-destructive">Erreur lors du chargement des messages</p>
        </div>
        <div v-else-if="filteredMessages.length === 0" class="p-8 text-center">
          <p class="text-muted-foreground">Aucun message trouvé</p>
        </div>
        <TableComponent v-else>
          <TableHeader>
            <TableRow>
              <TableHead>Expéditeur</TableHead>
              <TableHead>Sujet</TableHead>
              <TableHead>Aperçu</TableHead>
              <TableHead>Agence</TableHead>
              <TableHead>Statut</TableHead>
              <TableHead>Date</TableHead>
              <TableHead class="text-right">Actions</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            <TableRow 
              v-for="message in filteredMessages" 
              :key="message.id"
              :class="{ 'bg-blue-50 dark:bg-blue-950 border-l-4 border-l-blue-500': message.status === 'NEW' }"
              class="cursor-pointer hover:bg-muted/50"
              @click="openDetailDialog(message)"
            >
              <TableCell>
                <div>
                  <div class="font-medium">{{ message.name }}</div>
                  <div class="text-sm text-muted-foreground">{{ message.email }}</div>
                  <div v-if="message.phone" class="text-xs text-muted-foreground">{{ message.phone }}</div>
                </div>
              </TableCell>
              <TableCell class="font-medium max-w-xs">{{ message.subject }}</TableCell>
              <TableCell class="max-w-md">
                <p class="text-sm text-muted-foreground line-clamp-2">{{ message.message }}</p>
              </TableCell>
              <TableCell>
                <span class="text-sm text-muted-foreground">{{ message.organizationName || `#${message.organizationId}` }}</span>
              </TableCell>
              <TableCell>
                <Badge :variant="getStatusVariant(message.status)">
                  {{ getStatusLabel(message.status) }}
                </Badge>
              </TableCell>
              <TableCell class="text-sm text-muted-foreground">
                <div>{{ formatDate(message.createdAt) }}</div>
                <div v-if="message.readAt" class="text-xs mt-1">
                  Lu: {{ formatDate(message.readAt) }}
                </div>
              </TableCell>
              <TableCell class="text-right" @click.stop>
                <DropdownMenu>
                  <DropdownMenuTrigger as-child>
                    <Button variant="ghost" size="sm">
                      <MoreHorizontal class="h-4 w-4" />
                    </Button>
                  </DropdownMenuTrigger>
                  <DropdownMenuContent align="end">
                    <DropdownMenuItem @click="openDetailDialog(message)">
                      <Eye class="mr-2 h-4 w-4" />
                      Voir les détails
                    </DropdownMenuItem>
                    <DropdownMenuSeparator />
                    <DropdownMenuItem 
                      v-if="message.status === 'NEW'" 
                      @click="markAsRead(message.id)"
                    >
                      <MailOpen class="mr-2 h-4 w-4" />
                      Marquer comme lu
                    </DropdownMenuItem>
                    <DropdownMenuItem 
                      v-if="message.status !== 'REPLIED'" 
                      @click="markAsReplied(message.id)"
                    >
                      <CheckCircle class="mr-2 h-4 w-4" />
                      Marquer comme répondu
                    </DropdownMenuItem>
                    <DropdownMenuItem 
                      v-if="message.status !== 'ARCHIVED'" 
                      @click="archiveMessage(message.id)"
                    >
                      <Archive class="mr-2 h-4 w-4" />
                      Archiver
                    </DropdownMenuItem>
                    <DropdownMenuSeparator />
                    <DropdownMenuItem 
                      @click="deleteMessage(message.id)"
                      class="text-destructive"
                    >
                      <Trash2 class="mr-2 h-4 w-4" />
                      Supprimer
                    </DropdownMenuItem>
                  </DropdownMenuContent>
                </DropdownMenu>
              </TableCell>
            </TableRow>
          </TableBody>
        </TableComponent>
      </CardContent>
    </Card>

    <!-- Pagination -->
    <div v-if="totalPages > 1" class="flex items-center justify-between">
      <div class="text-sm text-muted-foreground">
        Page {{ currentPage + 1 }} sur {{ totalPages }} ({{ totalElements }} messages)
      </div>
      <div class="flex gap-2">
        <Button variant="outline" size="sm" @click="previousPage" :disabled="currentPage === 0">
          Précédent
        </Button>
        <Button variant="outline" size="sm" @click="nextPage" :disabled="currentPage >= totalPages - 1">
          Suivant
        </Button>
      </div>
    </div>

    <!-- Dialog de détails -->
    <Dialog :open="detailDialogOpen" @update:open="detailDialogOpen = $event">
      <DialogContent class="max-w-3xl max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>Détails du message</DialogTitle>
        </DialogHeader>
        <div v-if="selectedMessage" class="space-y-4">
          <div class="grid grid-cols-2 gap-4">
            <div>
              <Label>Expéditeur</Label>
              <div class="mt-1">
                <p class="font-medium">{{ selectedMessage.name }}</p>
                <p class="text-sm text-muted-foreground">{{ selectedMessage.email }}</p>
                <p v-if="selectedMessage.phone" class="text-sm text-muted-foreground">
                  <Phone class="inline h-3 w-3 mr-1" />
                  {{ selectedMessage.phone }}
                </p>
              </div>
            </div>
            <div>
              <Label>Agence</Label>
              <p class="mt-1 font-medium">{{ selectedMessage.organizationName || `#${selectedMessage.organizationId}` }}</p>
            </div>
          </div>
          <div>
            <Label>Sujet</Label>
            <p class="mt-1 font-medium">{{ selectedMessage.subject }}</p>
          </div>
          <div>
            <Label>Message</Label>
            <p class="mt-1 whitespace-pre-wrap">{{ selectedMessage.message }}</p>
          </div>
          <div class="grid grid-cols-2 gap-4">
            <div>
              <Label>Statut</Label>
              <div class="mt-1">
                <Badge :variant="getStatusVariant(selectedMessage.status)">
                  {{ getStatusLabel(selectedMessage.status) }}
                </Badge>
              </div>
            </div>
            <div>
              <Label>Date</Label>
              <div class="mt-1 text-sm text-muted-foreground">
                <div>Reçu: {{ formatDate(selectedMessage.createdAt) }}</div>
                <div v-if="selectedMessage.readAt">
                  Lu: {{ formatDate(selectedMessage.readAt) }}
                </div>
                <div v-if="selectedMessage.repliedAt">
                  Répondu: {{ formatDate(selectedMessage.repliedAt) }}
                </div>
              </div>
            </div>
          </div>
          <div>
            <Label>Notes internes</Label>
            <Textarea 
              v-model="notesInput" 
              placeholder="Ajouter des notes internes..."
              class="mt-1"
            />
            <Button 
              @click="updateNotes(selectedMessage.id, notesInput)" 
              class="mt-2"
              size="sm"
            >
              Enregistrer les notes
            </Button>
          </div>
          <div class="flex gap-2 pt-4 border-t">
            <Button 
              v-if="selectedMessage.status === 'NEW'" 
              @click="markAsRead(selectedMessage.id)"
            >
              <MailOpen class="mr-2 h-4 w-4" />
              Marquer comme lu
            </Button>
            <Button 
              v-if="selectedMessage.status !== 'REPLIED'" 
              @click="markAsReplied(selectedMessage.id)"
            >
              <CheckCircle class="mr-2 h-4 w-4" />
              Marquer comme répondu
            </Button>
            <Button 
              v-if="selectedMessage.status !== 'ARCHIVED'" 
              variant="outline"
              @click="archiveMessage(selectedMessage.id)"
            >
              <Archive class="mr-2 h-4 w-4" />
              Archiver
            </Button>
            <Button 
              variant="destructive"
              @click="deleteMessage(selectedMessage.id)"
            >
              <Trash2 class="mr-2 h-4 w-4" />
              Supprimer
            </Button>
          </div>
        </div>
      </DialogContent>
    </Dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { organizationContactService, organizationService, useAuthStore } from '@viridial/shared'
import type { OrganizationContactMessage } from '@viridial/shared'
import type { Organization } from '@viridial/shared'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { Table as TableComponent, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog'
import { Textarea } from '@/components/ui/textarea'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger
} from '@/components/ui/dropdown-menu'
import { Mail, MailOpen, CheckCircle, Archive, Trash2, Eye, MoreHorizontal, X, Phone, RefreshCw } from 'lucide-vue-next'

const route = useRoute()
const authStore = useAuthStore()

const messages = ref<OrganizationContactMessage[]>([])
const loading = ref(false)
const error = ref(false)
const totalElements = ref(0)
const totalPages = ref(0)
const currentPage = ref(0)
const pageSize = 20

const searchQuery = ref('')
const selectedStatus = ref<string | null>(null)
const selectedOrganizationId = ref<number | null>(null)
const organizations = ref<Organization[]>([])

const stats = ref({
  new: 0,
  read: 0,
  replied: 0,
  total: 0
})

const detailDialogOpen = ref(false)
const selectedMessage = ref<OrganizationContactMessage | null>(null)
const notesInput = ref('')

let searchTimeout: ReturnType<typeof setTimeout> | null = null

onMounted(async () => {
  await loadOrganizations()
  await loadMessages()
  await loadStats()
  
  // Ouvrir le dialogue si un messageId est fourni dans la route
  const messageId = route.params.id || route.query.messageId
  if (messageId) {
    const id = typeof messageId === 'string' ? parseInt(messageId, 10) : Number(messageId)
    if (!isNaN(id)) {
      try {
        const message = await organizationContactService.getById(id)
        openDetailDialog(message)
        // Marquer comme lu si nouveau
        if (message.status === 'NEW') {
          await markAsRead(message.id)
        }
      } catch (err) {
        console.error('Error loading message:', err)
      }
    }
  }
})

async function loadOrganizations() {
  try {
    const response = await organizationService.getAll()
    organizations.value = response.organizations
  } catch (err) {
    console.error('Error loading organizations:', err)
  }
}

async function loadMessages() {
  loading.value = true
  error.value = false

  try {
    const params: any = {
      page: currentPage.value,
      size: pageSize
    }

    if (selectedStatus.value) {
      params.status = selectedStatus.value
    }

    if (selectedOrganizationId.value) {
      params.organizationId = selectedOrganizationId.value
    }

    const response = await organizationContactService.getAll(params)
    messages.value = response.content
    totalElements.value = response.totalElements
    totalPages.value = response.totalPages
  } catch (err) {
    console.error('Error loading messages:', err)
    error.value = true
  } finally {
    loading.value = false
  }
}

async function loadStats() {
  try {
    const allMessages = await organizationContactService.getAll({ page: 0, size: 1000 })
    const all = allMessages.content
    
    stats.value = {
      new: all.filter((m: OrganizationContactMessage) => m.status === 'NEW').length,
      read: all.filter((m: OrganizationContactMessage) => m.status === 'READ').length,
      replied: all.filter((m: OrganizationContactMessage) => m.status === 'REPLIED').length,
      total: all.length
    }
  } catch (err) {
    console.error('Error loading stats:', err)
  }
}

const filteredMessages = computed(() => {
  let filtered = messages.value

  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter((m: OrganizationContactMessage) => 
      m.name.toLowerCase().includes(query) ||
      m.email.toLowerCase().includes(query) ||
      m.subject.toLowerCase().includes(query) ||
      m.message.toLowerCase().includes(query)
    )
  }

  return filtered
})

function handleSearch() {
  if (searchTimeout) {
    clearTimeout(searchTimeout)
  }
  searchTimeout = setTimeout(() => {
    // La recherche est déjà gérée par le computed filteredMessages
  }, 300)
}

function handleFilter() {
  currentPage.value = 0
  loadMessages()
}

function resetFilters() {
  searchQuery.value = ''
  selectedStatus.value = null
  selectedOrganizationId.value = null
  handleFilter()
}

function filterByStatus(status: string) {
  selectedStatus.value = status
  handleFilter()
}

function previousPage() {
  if (currentPage.value > 0) {
    currentPage.value--
    loadMessages()
  }
}

function nextPage() {
  if (currentPage.value < totalPages.value - 1) {
    currentPage.value++
    loadMessages()
  }
}

async function openDetailDialog(message: OrganizationContactMessage) {
  try {
    const fullMessage = await organizationContactService.getById(message.id)
    selectedMessage.value = { ...fullMessage }
    notesInput.value = fullMessage.notes || ''
    detailDialogOpen.value = true
    
    // Marquer comme lu si nouveau
    if (fullMessage.status === 'NEW') {
      await markAsRead(fullMessage.id)
    }
  } catch (err) {
    console.error('Error loading message details:', err)
    selectedMessage.value = { ...message }
    notesInput.value = message.notes || ''
    detailDialogOpen.value = true
  }
}

async function markAsRead(id: number) {
  try {
    const userId = (authStore.user as any)?.id
    await organizationContactService.markAsRead(id, userId)
    await loadMessages()
    await loadStats()
    if (selectedMessage.value?.id === id) {
      selectedMessage.value = await organizationContactService.getById(id)
    }
  } catch (err) {
    console.error('Error marking as read:', err)
  }
}

async function markAsReplied(id: number) {
  try {
    const userId = (authStore.user as any)?.id
    await organizationContactService.markAsReplied(id, userId)
    await loadMessages()
    await loadStats()
    if (selectedMessage.value?.id === id) {
      selectedMessage.value = await organizationContactService.getById(id)
    }
  } catch (err) {
    console.error('Error marking as replied:', err)
  }
}

async function archiveMessage(id: number) {
  try {
    await organizationContactService.archive(id)
    await loadMessages()
    await loadStats()
    if (selectedMessage.value?.id === id) {
      selectedMessage.value = await organizationContactService.getById(id)
    }
  } catch (err) {
    console.error('Error archiving message:', err)
  }
}

async function deleteMessage(id: number) {
  if (!confirm('Êtes-vous sûr de vouloir supprimer ce message ?')) {
    return
  }
  try {
    await organizationContactService.delete(id)
    await loadMessages()
    await loadStats()
    if (selectedMessage.value?.id === id) {
      detailDialogOpen.value = false
    }
  } catch (err) {
    console.error('Error deleting message:', err)
  }
}

async function updateNotes(id: number, notes: string) {
  try {
    await organizationContactService.updateNotes(id, notes)
    if (selectedMessage.value?.id === id) {
      selectedMessage.value = await organizationContactService.getById(id)
    }
  } catch (err) {
    console.error('Error updating notes:', err)
  }
}

function getStatusLabel(status: string): string {
  const labels: Record<string, string> = {
    'NEW': 'Nouveau',
    'READ': 'Lu',
    'REPLIED': 'Répondu',
    'ARCHIVED': 'Archivé'
  }
  return labels[status] || status
}

function getStatusVariant(status: string): 'default' | 'secondary' | 'outline' | 'destructive' {
  const variants: Record<string, 'default' | 'secondary' | 'outline' | 'destructive'> = {
    'NEW': 'default',
    'READ': 'secondary',
    'REPLIED': 'default',
    'ARCHIVED': 'outline'
  }
  return variants[status] || 'default'
}

function formatDate(date: string | undefined): string {
  if (!date) return '-'
  return new Date(date).toLocaleString('fr-FR', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}
</script>

