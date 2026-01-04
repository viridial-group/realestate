<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="flex items-center justify-between flex-wrap gap-4">
      <div class="flex-1">
        <div class="flex items-center gap-3">
          <h1 class="text-2.5xl font-bold">Messages de Contact</h1>
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
          {{ viewMode === 'my' ? 'Messages concernant vos propriétés' : 'Gérez les messages de contact reçus' }}
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

    <!-- Filtres et Vue -->
    <Card>
      <CardContent class="p-4">
        <div class="flex items-center justify-between mb-4">
          <div class="flex items-center gap-2">
            <Label>Mode d'affichage:</Label>
            <div class="flex gap-2">
              <Button 
                :variant="listView === 'table' ? 'default' : 'outline'" 
                size="sm"
                @click="listView = 'table'"
              >
                <Table class="mr-2 h-4 w-4" />
                Tableau
              </Button>
              <Button 
                :variant="listView === 'cards' ? 'default' : 'outline'" 
                size="sm"
                @click="listView = 'cards'"
              >
                <LayoutGrid class="mr-2 h-4 w-4" />
                Cartes
              </Button>
            </div>
          </div>
        </div>
        <div class="grid grid-cols-1 md:grid-cols-5 gap-4">
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
            <Label>Vue</Label>
            <Select v-model="viewMode" @update:model-value="handleFilter">
              <SelectTrigger>
                <SelectValue placeholder="Tous" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">Tous les messages</SelectItem>
                <SelectItem value="my">Mes messages</SelectItem>
              </SelectContent>
            </Select>
          </div>
          <div v-if="isAdmin" class="space-y-2">
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
    <Card v-if="listView === 'table'">
      <CardContent class="p-0">
        <div v-if="loading" class="p-8 text-center">
          <p class="text-muted-foreground">Chargement...</p>
        </div>
        <div v-else-if="error" class="p-8 text-center">
          <p class="text-destructive">Erreur lors du chargement des messages</p>
        </div>
        <div v-else-if="messages.length === 0" class="p-8 text-center">
          <p class="text-muted-foreground">Aucun message trouvé</p>
        </div>
        <TableComponent v-else>
          <TableHeader>
            <TableRow>
              <TableHead>Expéditeur</TableHead>
              <TableHead>Sujet</TableHead>
              <TableHead>Aperçu</TableHead>
              <TableHead>Propriété</TableHead>
              <TableHead>Statut</TableHead>
              <TableHead>Date</TableHead>
              <TableHead class="text-right">Actions</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            <TableRow 
              v-for="message in messages" 
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
                <router-link 
                  v-if="message.propertyId" 
                  :to="`/properties/${message.propertyId}`"
                  @click.stop
                  class="text-blue-600 hover:underline text-sm"
                >
                  <Home class="inline h-4 w-4 mr-1" />
                  {{ message.propertyTitle || `#${message.propertyId}` }}
                </router-link>
                <span v-else class="text-sm text-muted-foreground">-</span>
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
                      Voir détails
                    </DropdownMenuItem>
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

    <!-- Vue Cartes -->
    <div v-else-if="listView === 'cards'" class="space-y-4">
      <div v-if="loading" class="p-8 text-center">
        <p class="text-muted-foreground">Chargement...</p>
      </div>
      <div v-else-if="error" class="p-8 text-center">
        <p class="text-destructive">Erreur lors du chargement des messages</p>
      </div>
      <div v-else-if="messages.length === 0" class="p-8 text-center">
        <p class="text-muted-foreground">Aucun message trouvé</p>
      </div>
      <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        <Card 
          v-for="message in messages" 
          :key="message.id"
          :class="{ 
            'border-l-4 border-l-blue-500 bg-blue-50/50 dark:bg-blue-950/50': message.status === 'NEW',
            'cursor-pointer hover:shadow-lg transition-shadow': true
          }"
          @click="openDetailDialog(message)"
        >
          <CardHeader>
            <div class="flex items-start justify-between">
              <div class="flex-1">
                <CardTitle class="text-lg mb-1">{{ message.name }}</CardTitle>
                <CardDescription class="flex items-center gap-2">
                  <Mail class="h-3 w-3" />
                  {{ message.email }}
                </CardDescription>
                <CardDescription v-if="message.phone" class="flex items-center gap-2 mt-1">
                  <Phone class="h-3 w-3" />
                  {{ message.phone }}
                </CardDescription>
              </div>
              <Badge :variant="getStatusVariant(message.status)" class="ml-2">
                {{ getStatusLabel(message.status) }}
              </Badge>
            </div>
          </CardHeader>
          <CardContent>
            <div class="space-y-3">
              <div>
                <Label class="text-xs text-muted-foreground">Sujet</Label>
                <p class="font-medium text-sm mt-1">{{ message.subject }}</p>
              </div>
              <div>
                <Label class="text-xs text-muted-foreground">Message</Label>
                <p class="text-sm text-muted-foreground line-clamp-3 mt-1">{{ message.message }}</p>
              </div>
              <div v-if="message.propertyId" class="pt-2 border-t">
                <router-link 
                  :to="`/properties/${message.propertyId}`"
                  @click.stop
                  class="flex items-center gap-2 text-blue-600 hover:underline text-sm"
                >
                  <Home class="h-4 w-4" />
                  <span>{{ message.propertyTitle || `Propriété #${message.propertyId}` }}</span>
                </router-link>
              </div>
              <div class="flex items-center justify-between text-xs text-muted-foreground pt-2 border-t">
                <div class="flex items-center gap-1">
                  <Clock class="h-3 w-3" />
                  {{ formatDate(message.createdAt) }}
                </div>
                <div v-if="message.readAt" class="flex items-center gap-1">
                  <MailOpen class="h-3 w-3" />
                  Lu {{ formatRelativeTime(message.readAt) }}
                </div>
              </div>
            </div>
          </CardContent>
          <CardFooter class="flex justify-end gap-2 pt-4" @click.stop>
            <Button variant="outline" size="sm" @click="openDetailDialog(message)">
              <Eye class="mr-2 h-4 w-4" />
              Détails
            </Button>
            <Button 
              v-if="message.status === 'NEW'"
              variant="outline" 
              size="sm"
              @click="markAsRead(message.id)"
            >
              <MailOpen class="h-4 w-4" />
            </Button>
            <Button 
              v-if="message.status !== 'REPLIED'"
              variant="outline" 
              size="sm"
              @click="markAsReplied(message.id)"
            >
              <CheckCircle class="h-4 w-4" />
            </Button>
          </CardFooter>
        </Card>
      </div>
    </div>

    <!-- Pagination -->
    <div v-if="totalPages > 1" class="flex items-center justify-between">
      <p class="text-sm text-muted-foreground">
        Page {{ currentPage + 1 }} sur {{ totalPages }} ({{ totalElements }} messages)
      </p>
      <div class="flex gap-2">
        <Button variant="outline" size="sm" @click="previousPage" :disabled="currentPage === 0">
          Précédent
        </Button>
        <Button variant="outline" size="sm" @click="nextPage" :disabled="currentPage >= totalPages - 1">
          Suivant
        </Button>
      </div>
    </div>

    <!-- Dialog Détails -->
    <Dialog :open="detailDialogOpen" @update:open="detailDialogOpen = $event">
      <DialogContent class="max-w-4xl max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <div class="flex items-center justify-between">
            <DialogTitle class="text-2xl">Détails du message</DialogTitle>
            <Badge :variant="getStatusVariant(selectedMessage?.status || 'NEW')" class="text-sm">
              {{ getStatusLabel(selectedMessage?.status || 'NEW') }}
            </Badge>
          </div>
        </DialogHeader>
        <div v-if="selectedMessage" class="space-y-6">
          <!-- Informations expéditeur -->
          <Card>
            <CardHeader>
              <CardTitle class="text-lg">Expéditeur</CardTitle>
            </CardHeader>
            <CardContent>
              <div class="grid grid-cols-2 gap-4">
                <div>
                  <Label class="text-xs text-muted-foreground">Nom complet</Label>
                  <p class="text-sm font-medium mt-1">{{ selectedMessage.name }}</p>
                </div>
                <div>
                  <Label class="text-xs text-muted-foreground">Email</Label>
                  <div class="flex items-center gap-2 mt-1">
                    <Mail class="h-4 w-4 text-muted-foreground" />
                    <a :href="`mailto:${selectedMessage.email}`" class="text-sm text-blue-600 hover:underline">
                      {{ selectedMessage.email }}
                    </a>
                  </div>
                </div>
                <div v-if="selectedMessage.phone">
                  <Label class="text-xs text-muted-foreground">Téléphone</Label>
                  <div class="flex items-center gap-2 mt-1">
                    <Phone class="h-4 w-4 text-muted-foreground" />
                    <a :href="`tel:${selectedMessage.phone}`" class="text-sm text-blue-600 hover:underline">
                      {{ selectedMessage.phone }}
                    </a>
                  </div>
                </div>
                <div>
                  <Label class="text-xs text-muted-foreground">Date d'envoi</Label>
                  <div class="flex items-center gap-2 mt-1">
                    <Clock class="h-4 w-4 text-muted-foreground" />
                    <p class="text-sm">{{ formatDate(selectedMessage.createdAt) }}</p>
                  </div>
                </div>
                <div v-if="selectedMessage.readAt">
                  <Label class="text-xs text-muted-foreground">Lu le</Label>
                  <p class="text-sm text-muted-foreground mt-1">{{ formatDate(selectedMessage.readAt) }}</p>
                </div>
                <div v-if="selectedMessage.repliedAt">
                  <Label class="text-xs text-muted-foreground">Répondu le</Label>
                  <p class="text-sm text-muted-foreground mt-1">{{ formatDate(selectedMessage.repliedAt) }}</p>
                </div>
              </div>
            </CardContent>
          </Card>

          <!-- Propriété concernée -->
          <Card v-if="selectedMessage.propertyId">
            <CardHeader>
              <CardTitle class="text-lg">Propriété concernée</CardTitle>
            </CardHeader>
            <CardContent>
              <router-link 
                :to="`/properties/${selectedMessage.propertyId}`"
                class="flex items-center gap-2 text-blue-600 hover:underline"
              >
                <Home class="h-5 w-5" />
                <span class="font-medium">{{ selectedMessage.propertyTitle || `Propriété #${selectedMessage.propertyId}` }}</span>
              </router-link>
            </CardContent>
          </Card>

          <!-- Message -->
          <Card>
            <CardHeader>
              <CardTitle class="text-lg">Message</CardTitle>
            </CardHeader>
            <CardContent class="space-y-4">
              <div>
                <Label class="text-xs text-muted-foreground">Sujet</Label>
                <p class="text-base font-medium mt-1">{{ selectedMessage.subject }}</p>
              </div>
              <div>
                <Label class="text-xs text-muted-foreground">Contenu</Label>
                <div class="mt-2 p-4 bg-muted rounded-lg">
                  <p class="text-sm whitespace-pre-wrap leading-relaxed">{{ selectedMessage.message }}</p>
                </div>
              </div>
            </CardContent>
          </Card>

          <!-- Notes internes -->
          <Card>
            <CardHeader>
              <CardTitle class="text-lg">Notes internes</CardTitle>
            </CardHeader>
            <CardContent>
              <Textarea 
                v-model="notesInput" 
                @blur="updateNotes(selectedMessage.id, notesInput)"
                placeholder="Ajoutez des notes internes sur ce message..."
                class="min-h-[100px]"
              />
            </CardContent>
          </Card>
        </div>
        <DialogFooter class="flex justify-between">
          <div class="flex gap-2">
            <Button 
              v-if="selectedMessage && selectedMessage.status === 'NEW'"
              variant="outline"
              @click="markAsRead(selectedMessage.id)"
            >
              <MailOpen class="mr-2 h-4 w-4" />
              Marquer comme lu
            </Button>
            <Button 
              v-if="selectedMessage && selectedMessage.status !== 'REPLIED'"
              variant="outline"
              @click="markAsReplied(selectedMessage.id)"
            >
              <CheckCircle class="mr-2 h-4 w-4" />
              Marquer comme répondu
            </Button>
            <Button 
              v-if="selectedMessage && selectedMessage.status !== 'ARCHIVED'"
              variant="outline"
              @click="archiveMessage(selectedMessage.id)"
            >
              <Archive class="mr-2 h-4 w-4" />
              Archiver
            </Button>
          </div>
          <div class="flex gap-2">
            <Button 
              variant="outline" 
              @click="closeDetailDialog"
            >
              Fermer
            </Button>
            <a 
              v-if="selectedMessage?.email"
              :href="`mailto:${selectedMessage.email}?subject=Re: ${selectedMessage.subject}`"
              class="inline-flex items-center justify-center rounded-md text-sm font-medium ring-offset-background transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:pointer-events-none disabled:opacity-50 bg-primary text-primary-foreground hover:bg-primary/90 h-10 px-4 py-2"
            >
              <Mail class="mr-2 h-4 w-4" />
              Répondre
            </a>
          </div>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { contactService, type ContactMessage, type ContactMessageSearchParams, type PaginatedResponse } from '@viridial/shared'
import { useAuthStore } from '@viridial/shared'
import { organizationService, type Organization } from '@viridial/shared'
import { Card, CardContent, CardDescription, CardHeader, CardTitle, CardFooter } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { Table as TableComponent, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { Badge } from '@/components/ui/badge'
import { Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle } from '@/components/ui/dialog'
import { Textarea } from '@/components/ui/textarea'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger
} from '@/components/ui/dropdown-menu'
import { Mail, MailOpen, CheckCircle, Archive, Trash2, Eye, MoreHorizontal, X, Table, LayoutGrid, Phone, Clock, Home, RefreshCw } from 'lucide-vue-next'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const currentUser = computed(() => authStore.user)

const messages = ref<ContactMessage[]>([])
const loading = ref(false)
const error = ref(false)
const totalElements = ref(0)
const totalPages = ref(0)
const currentPage = ref(0)
const pageSize = 20

const searchQuery = ref('')
const selectedStatus = ref<string | null>(null)
const selectedOrganizationId = ref<number | null>(null)
const viewMode = ref<'all' | 'my'>('my') // Par défaut, afficher "Mes messages"
const listView = ref<'table' | 'cards'>('table') // Mode d'affichage: tableau ou cartes
const organizations = ref<Organization[]>([])

const stats = ref({
  new: 0,
  read: 0,
  replied: 0,
  total: 0
})

const isAdmin = computed(() => {
  const user = currentUser.value as any
  const roles = user?.roles || []
  return roles.some((role: string) => role === 'ADMIN' || role === 'SUPER_ADMIN')
})

const currentUserId = computed(() => {
  const user = currentUser.value as any
  return user?.id
})

const detailDialogOpen = ref(false)
const selectedMessage = ref<ContactMessage | null>(null)
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
        const message = await contactService.getById(id)
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
    const params: ContactMessageSearchParams = {
      page: currentPage.value,
      size: pageSize
    }

    if (selectedStatus.value) {
      params.status = selectedStatus.value as any
    }

    // Filtrer par utilisateur assigné si "Mes messages" est sélectionné
    if (viewMode.value === 'my' && currentUserId.value) {
      params.assignedUserId = currentUserId.value
    }

    if (selectedOrganizationId.value) {
      params.organizationId = selectedOrganizationId.value
    }

    const response: PaginatedResponse<ContactMessage> = await contactService.getAll(params)
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
    // Charger les stats selon le mode de vue
    let newCount: number
    if (viewMode.value === 'my' && currentUserId.value) {
      newCount = await contactService.getNewMessagesCount(undefined, currentUserId.value)
    } else {
      newCount = await contactService.getNewMessagesCount(selectedOrganizationId.value || undefined)
    }
    stats.value.new = newCount
    
    // Charger les stats par statut
    const params: ContactMessageSearchParams = { size: 1000 }
    if (viewMode.value === 'my' && currentUserId.value) {
      params.assignedUserId = currentUserId.value
    }
    if (selectedOrganizationId.value) {
      params.organizationId = selectedOrganizationId.value
    }
    
    const allMessages = await contactService.getAll(params)
    stats.value.total = allMessages.totalElements
    stats.value.read = allMessages.content.filter((m: ContactMessage) => m.status === 'READ').length
    stats.value.replied = allMessages.content.filter((m: ContactMessage) => m.status === 'REPLIED').length
  } catch (err) {
    console.error('Error loading stats:', err)
  }
}

function handleSearch() {
  if (searchTimeout) {
    clearTimeout(searchTimeout)
  }
  searchTimeout = setTimeout(() => {
    currentPage.value = 0
    loadMessages()
  }, 300)
}

function handleFilter() {
  currentPage.value = 0
  loadMessages()
  loadStats()
}

function resetFilters() {
  searchQuery.value = ''
  selectedStatus.value = null
  selectedOrganizationId.value = null
  viewMode.value = 'my' // Réinitialiser à "Mes messages"
  currentPage.value = 0
  loadMessages()
  loadStats()
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

async function openDetailDialog(message: ContactMessage) {
  // Charger le message complet depuis l'API pour avoir toutes les informations
  try {
    const fullMessage = await contactService.getById(message.id)
    selectedMessage.value = { ...fullMessage }
    notesInput.value = fullMessage.notes || ''
    detailDialogOpen.value = true
    
    // Mettre à jour l'URL sans recharger la page
    if (route.name !== 'contact-detail') {
      router.push({ name: 'contact-detail', params: { id: message.id } })
    }
    
    // Marquer comme lu si nouveau
    if (fullMessage.status === 'NEW') {
      await markAsRead(fullMessage.id)
    }
  } catch (err) {
    console.error('Error loading message details:', err)
    // Fallback: utiliser le message fourni
    selectedMessage.value = { ...message }
    notesInput.value = message.notes || ''
    detailDialogOpen.value = true
  }
}

async function markAsRead(id: number) {
  try {
    const userId = (currentUser.value as any)?.id
    await contactService.markAsRead(id, userId)
    await loadMessages()
    await loadStats()
  } catch (err) {
    console.error('Error marking as read:', err)
  }
}

async function markAsReplied(id: number) {
  try {
    const userId = (currentUser.value as any)?.id
    await contactService.markAsReplied(id, userId)
    await loadMessages()
    await loadStats()
  } catch (err) {
    console.error('Error marking as replied:', err)
  }
}

async function archiveMessage(id: number) {
  try {
    await contactService.archive(id)
    await loadMessages()
    await loadStats()
  } catch (err) {
    console.error('Error archiving message:', err)
  }
}

async function deleteMessage(id: number) {
  if (!confirm('Êtes-vous sûr de vouloir supprimer ce message ?')) {
    return
  }
  try {
    await contactService.delete(id)
    await loadMessages()
    await loadStats()
  } catch (err) {
    console.error('Error deleting message:', err)
  }
}

async function updateNotes(id: number, notes: string) {
  try {
    await contactService.updateNotes(id, notes)
    if (selectedMessage.value) {
      selectedMessage.value.notes = notes
    }
    await loadMessages()
  } catch (err) {
    console.error('Error updating notes:', err)
  }
}

function getStatusVariant(status: string) {
  switch (status) {
    case 'NEW':
      return 'default'
    case 'READ':
      return 'secondary'
    case 'REPLIED':
      return 'default'
    case 'ARCHIVED':
      return 'outline'
    default:
      return 'secondary'
  }
}

function getStatusLabel(status: string) {
  switch (status) {
    case 'NEW':
      return 'Nouveau'
    case 'READ':
      return 'Lu'
    case 'REPLIED':
      return 'Répondu'
    case 'ARCHIVED':
      return 'Archivé'
    default:
      return status
  }
}

function formatDate(dateString: string) {
  const date = new Date(dateString)
  return new Intl.DateTimeFormat('fr-FR', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  }).format(date)
}

function formatRelativeTime(dateString: string) {
  const date = new Date(dateString)
  const now = new Date()
  const diffInSeconds = Math.floor((now.getTime() - date.getTime()) / 1000)
  
  if (diffInSeconds < 60) {
    return 'à l\'instant'
  } else if (diffInSeconds < 3600) {
    const minutes = Math.floor(diffInSeconds / 60)
    return `il y a ${minutes} min`
  } else if (diffInSeconds < 86400) {
    const hours = Math.floor(diffInSeconds / 3600)
    return `il y a ${hours}h`
  } else if (diffInSeconds < 604800) {
    const days = Math.floor(diffInSeconds / 86400)
    return `il y a ${days}j`
  } else {
    return formatDate(dateString)
  }
}

function closeDetailDialog() {
  detailDialogOpen.value = false
  // Retourner à la liste si on était sur la route de détail
  if (route.name === 'contact-detail') {
    router.push({ name: 'contacts' })
  }
}
</script>

