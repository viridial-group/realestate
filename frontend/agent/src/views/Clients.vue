<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="flex items-center justify-between flex-wrap gap-4">
      <div>
        <h1 class="text-3xl font-bold">{{ t('clients.title') }}</h1>
        <p class="text-muted-foreground mt-1">{{ t('clients.description') }}</p>
      </div>
      <Button @click="openCreateDialog" size="lg">
        <Plus class="mr-2 h-4 w-4" />
        {{ t('clients.createClient') }}
      </Button>
    </div>

    <!-- Filtres -->
    <Card>
      <CardContent class="p-4">
        <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div class="space-y-2">
            <Label>{{ t('common.search') }}</Label>
            <Input v-model="searchQuery" :placeholder="t('clients.searchPlaceholder')" @input="handleSearch" />
          </div>
          <div class="space-y-2">
            <Label>{{ t('clients.type') }}</Label>
            <Select v-model="selectedType" @update:model-value="handleFilter">
              <SelectTrigger>
                <SelectValue :placeholder="t('common.all')" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem :value="null">{{ t('common.all') }}</SelectItem>
                <SelectItem value="CLIENT">{{ t('clients.typeClient') }}</SelectItem>
                <SelectItem value="PROSPECT">{{ t('clients.typeProspect') }}</SelectItem>
              </SelectContent>
            </Select>
          </div>
          <div class="space-y-2">
            <Label>{{ t('common.actions') }}</Label>
            <Button variant="outline" class="w-full" @click="resetFilters">
              <X class="mr-2 h-4 w-4" />
              {{ t('common.reset') }}
            </Button>
          </div>
        </div>
      </CardContent>
    </Card>

    <!-- Liste des clients -->
    <div v-if="loading" class="flex items-center justify-center py-12">
      <Loader2 class="h-8 w-8 animate-spin text-muted-foreground" />
    </div>
    <div v-else-if="clients.length === 0" class="text-center py-12">
      <Users class="h-12 w-12 text-muted-foreground mx-auto mb-4" />
      <p class="text-muted-foreground">{{ t('clients.noClients') }}</p>
      <Button class="mt-4" @click="openCreateDialog">
        <Plus class="mr-2 h-4 w-4" />
        {{ t('clients.createClient') }}
      </Button>
    </div>
    <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
      <Card
        v-for="client in clients"
        :key="client.id"
        class="cursor-pointer hover:shadow-lg transition-shadow"
        @click="viewClient(client.id)"
      >
        <CardHeader>
          <div class="flex items-center justify-between">
            <CardTitle>{{ client.name }}</CardTitle>
            <Badge :variant="client.type === 'CLIENT' ? 'default' : 'outline'">
              {{ client.type === 'CLIENT' ? t('clients.typeClient') : t('clients.typeProspect') }}
            </Badge>
          </div>
        </CardHeader>
        <CardContent>
          <div class="space-y-2">
            <div class="flex items-center gap-2">
              <Mail class="h-4 w-4 text-muted-foreground" />
              <span class="text-sm">{{ client.email }}</span>
            </div>
            <div v-if="client.phone" class="flex items-center gap-2">
              <Phone class="h-4 w-4 text-muted-foreground" />
              <span class="text-sm">{{ client.phone }}</span>
            </div>
            <div v-if="client.lastContact" class="flex items-center gap-2 mt-4">
              <Calendar class="h-4 w-4 text-muted-foreground" />
              <span class="text-xs text-muted-foreground">
                {{ t('clients.lastContact') }}: {{ formatDate(client.lastContact) }}
              </span>
            </div>
          </div>
        </CardContent>
        <CardFooter class="flex justify-between gap-2">
          <Button variant="outline" size="sm" class="flex-1" @click.stop="editClient(client.id)">
            <Edit class="mr-2 h-4 w-4" />
            {{ t('common.edit') }}
          </Button>
          <Button variant="outline" size="sm" class="flex-1 text-destructive" @click.stop="deleteClient(client.id)">
            <Trash2 class="mr-2 h-4 w-4" />
            {{ t('common.delete') }}
          </Button>
        </CardFooter>
      </Card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useToast } from '@/components/ui/toast'
import { Card, CardContent, CardHeader, CardTitle, CardFooter } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { Badge } from '@/components/ui/badge'
import {
  Plus,
  X,
  Users,
  Edit,
  Trash2,
  Mail,
  Phone,
  Calendar,
  Loader2
} from 'lucide-vue-next'

const { t } = useI18n()
const { toast } = useToast()

const loading = ref(false)
const searchQuery = ref('')
const selectedType = ref<string | null>(null)
const clients = ref<any[]>([])

// TODO: Implement client service
const loadClients = async () => {
  loading.value = true
  try {
    // Placeholder - implement with actual client service
    clients.value = []
  } catch (error) {
    console.error('Error loading clients:', error)
    toast({
      title: t('common.error'),
      description: 'Erreur lors du chargement des clients',
      variant: 'destructive'
    })
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  loadClients()
}

const handleFilter = () => {
  loadClients()
}

const resetFilters = () => {
  searchQuery.value = ''
  selectedType.value = null
  loadClients()
}

const openCreateDialog = () => {
  // TODO: Implement create client dialog
  toast({
    title: t('common.info'),
    description: 'Fonctionnalité à venir'
  })
}

const viewClient = (id: number) => {
  // TODO: Navigate to client detail
}

const editClient = (id: number) => {
  // TODO: Open edit dialog
}

const deleteClient = async (id: number) => {
  if (!confirm(t('clients.deleteClient'))) return
  // TODO: Implement delete
}

const formatDate = (date: string | Date) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString('fr-FR')
}

onMounted(() => {
  loadClients()
})
</script>


