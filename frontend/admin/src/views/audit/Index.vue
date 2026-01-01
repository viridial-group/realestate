<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-3xl font-bold">Audit et Logs</h1>
        <p class="text-muted-foreground mt-1">Historique des actions et événements système</p>
      </div>
      <div class="flex gap-2">
        <Button variant="outline" @click="exportLogs" :disabled="loading">
          <Download class="mr-2 h-4 w-4" />
          Exporter
        </Button>
        <Button variant="outline" @click="refreshData" :disabled="loading">
          <RefreshCw class="mr-2 h-4 w-4" :class="{ 'animate-spin': loading }" />
          Actualiser
        </Button>
      </div>
    </div>

    <!-- Stats Cards -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
      <Card>
        <CardHeader class="pb-2">
          <CardDescription>Événements Aujourd'hui</CardDescription>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold">{{ stats.today || 0 }}</div>
          <p class="text-xs text-muted-foreground mt-1">Événements</p>
        </CardContent>
      </Card>
      <Card>
        <CardHeader class="pb-2">
          <CardDescription>Cette Semaine</CardDescription>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold">{{ stats.thisWeek || 0 }}</div>
          <p class="text-xs text-muted-foreground mt-1">Événements</p>
        </CardContent>
      </Card>
      <Card>
        <CardHeader class="pb-2">
          <CardDescription>Erreurs</CardDescription>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold text-red-600">{{ stats.errors || 0 }}</div>
          <p class="text-xs text-muted-foreground mt-1">Dernières 24h</p>
        </CardContent>
      </Card>
      <Card>
        <CardHeader class="pb-2">
          <CardDescription>Total</CardDescription>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold">{{ stats.total || 0 }}</div>
          <p class="text-xs text-muted-foreground mt-1">Tous événements</p>
        </CardContent>
      </Card>
    </div>

    <!-- Filtres -->
    <Card>
      <CardContent class="p-4">
        <div class="grid grid-cols-1 md:grid-cols-5 gap-4">
          <div class="space-y-2">
            <Label>Recherche</Label>
            <Input v-model="searchQuery" placeholder="Utilisateur, action..." @input="handleSearch" />
          </div>
          <div class="space-y-2">
            <Label>Type</Label>
            <Select v-model="selectedType" @update:model-value="handleFilter">
              <SelectTrigger>
                <SelectValue placeholder="Tous" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="">Tous</SelectItem>
                <SelectItem value="CREATE">Création</SelectItem>
                <SelectItem value="UPDATE">Modification</SelectItem>
                <SelectItem value="DELETE">Suppression</SelectItem>
                <SelectItem value="LOGIN">Connexion</SelectItem>
                <SelectItem value="ERROR">Erreur</SelectItem>
              </SelectContent>
            </Select>
          </div>
          <div class="space-y-2">
            <Label>Niveau</Label>
            <Select v-model="selectedLevel" @update:model-value="handleFilter">
              <SelectTrigger>
                <SelectValue placeholder="Tous" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="">Tous</SelectItem>
                <SelectItem value="INFO">Info</SelectItem>
                <SelectItem value="WARNING">Avertissement</SelectItem>
                <SelectItem value="ERROR">Erreur</SelectItem>
                <SelectItem value="CRITICAL">Critique</SelectItem>
              </SelectContent>
            </Select>
          </div>
          <div class="space-y-2">
            <Label>Période</Label>
            <Select v-model="dateRange" @update:model-value="handleFilter">
              <SelectTrigger>
                <SelectValue placeholder="Toutes" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="">Toutes</SelectItem>
                <SelectItem value="today">Aujourd'hui</SelectItem>
                <SelectItem value="week">Cette semaine</SelectItem>
                <SelectItem value="month">Ce mois</SelectItem>
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

    <!-- Table des Logs -->
    <Card>
      <CardContent class="p-0">
        <!-- Actions en masse -->
        <div
          v-if="selectedIds.length > 0"
          class="border-b bg-muted/30 p-4 flex items-center justify-between"
        >
          <div class="text-sm text-muted-foreground">
            {{ selectedIds.length }} log(s) sélectionné(s)
          </div>
          <div class="flex gap-2">
            <Button variant="outline" size="sm" @click="bulkExport" :disabled="loading">
              <Download class="mr-2 h-4 w-4" />
              Exporter
            </Button>
            <Button variant="outline" size="sm" @click="bulkDelete" class="text-destructive" :disabled="loading">
              <Trash2 class="mr-2 h-4 w-4" />
              Supprimer
            </Button>
          </div>
        </div>
        <div class="overflow-x-auto">
          <table class="w-full">
            <thead class="bg-muted/50">
              <tr>
                <th class="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase">
                  <Checkbox v-model="selectAll" />
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase">Date</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase">Utilisateur</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase">Action</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase">Entité</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase">Niveau</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase">Message</th>
                <th class="px-6 py-3 text-right text-xs font-medium text-muted-foreground uppercase">Actions</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-border">
              <tr
                v-for="log in filteredLogs"
                :key="log.id"
                class="hover:bg-muted/50 transition-colors"
              >
                <td class="px-6 py-4 whitespace-nowrap" @click.stop>
                  <Checkbox
                    :model-value="selectedIds.includes(log.id)"
                    @update:model-value="(val) => handleLogCheckboxChange(log.id, Boolean(val))"
                  />
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-muted-foreground">
                  {{ formatDateTime(log.timestamp) }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="text-sm font-medium">{{ log.userName || 'Système' }}</div>
                  <div class="text-xs text-muted-foreground">{{ log.userEmail || '' }}</div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <Badge variant="outline">{{ log.action }}</Badge>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm">{{ log.entity }}</td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <Badge :variant="getLevelVariant(log.level)">
                    {{ log.level }}
                  </Badge>
                </td>
                <td class="px-6 py-4">
                  <div class="text-sm max-w-md truncate">{{ log.message }}</div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-right" @click.stop>
                  <Button variant="ghost" size="sm" @click="viewDetails(log.id)">
                    <Eye class="h-4 w-4" />
                  </Button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Pagination -->
        <div class="border-t p-4 flex items-center justify-between">
          <div class="text-sm text-muted-foreground">
            Affichage de {{ startIndex + 1 }} à {{ endIndex }} sur {{ total }}
          </div>
          <div class="flex gap-2">
            <Button variant="outline" size="sm" :disabled="currentPage === 1" @click="previousPage">
              Précédent
            </Button>
            <Button variant="outline" size="sm" :disabled="currentPage === totalPages" @click="nextPage">
              Suivant
            </Button>
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
import { Download, RefreshCw, X, Eye, Trash2 } from 'lucide-vue-next'
import { Checkbox } from '@/components/ui/checkbox'

const { toast } = useToast()
const loading = ref(false)
const searchQuery = ref('')
const selectedType = ref('')
const selectedLevel = ref('')
const dateRange = ref('')
const currentPage = ref(1)
const pageSize = 20

interface AuditLog {
  id: number
  timestamp: string
  userName?: string
  userEmail?: string
  action: string
  entity: string
  entityId?: number
  level: 'INFO' | 'WARNING' | 'ERROR' | 'CRITICAL'
  message: string
  details?: any
}

const logs = ref<AuditLog[]>([])
const stats = ref({
  today: 0,
  thisWeek: 0,
  errors: 0,
  total: 0
})
const selectedIds = ref<number[]>([])

const filteredLogs = computed(() => {
  let filtered = [...logs.value]

  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(
      (log) =>
        log.userName?.toLowerCase().includes(query) ||
        log.action?.toLowerCase().includes(query) ||
        log.message?.toLowerCase().includes(query)
    )
  }

  if (selectedType.value) {
    filtered = filtered.filter((log) => log.action === selectedType.value)
  }

  if (selectedLevel.value) {
    filtered = filtered.filter((log) => log.level === selectedLevel.value)
  }

  // Pagination
  const start = (currentPage.value - 1) * pageSize
  return filtered.slice(start, start + pageSize)
})

const total = computed(() => logs.value.length)
const totalPages = computed(() => Math.ceil(total.value / pageSize))
const startIndex = computed(() => (currentPage.value - 1) * pageSize)
const endIndex = computed(() => Math.min(startIndex.value + pageSize, total.value))

const selectAll = computed({
  get: (): boolean | 'indeterminate' => {
    if (filteredLogs.value.length === 0) return false
    if (selectedIds.value.length === 0) return false
    const allSelected = filteredLogs.value.every(log => selectedIds.value.includes(log.id))
    const someSelected = filteredLogs.value.some(log => selectedIds.value.includes(log.id))
    if (allSelected) return true
    if (someSelected) return 'indeterminate'
    return false
  },
  set: (value: boolean | 'indeterminate') => {
    if (value === true || value === 'indeterminate') {
      filteredLogs.value.forEach(log => {
        if (!selectedIds.value.includes(log.id)) {
          selectedIds.value.push(log.id)
        }
      })
    } else {
      const filteredIds = filteredLogs.value.map(log => log.id)
      selectedIds.value = selectedIds.value.filter(id => !filteredIds.includes(id))
    }
  }
})

const handleLogCheckboxChange = (id: number, checked: boolean) => {
  if (checked) {
    if (!selectedIds.value.includes(id)) {
      selectedIds.value.push(id)
    }
  } else {
    const index = selectedIds.value.indexOf(id)
    if (index > -1) {
      selectedIds.value.splice(index, 1)
    }
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadLogs()
}

const handleFilter = () => {
  currentPage.value = 1
  loadLogs()
}

const resetFilters = () => {
  searchQuery.value = ''
  selectedType.value = ''
  selectedLevel.value = ''
  dateRange.value = ''
  currentPage.value = 1
  loadLogs()
}

const previousPage = () => {
  if (currentPage.value > 1) currentPage.value--
}

const nextPage = () => {
  if (currentPage.value < totalPages.value) currentPage.value++
}

const refreshData = () => {
  loadLogs()
  loadStats()
}

const exportLogs = () => {
  toast({
    title: 'Export en cours',
    description: 'Les logs seront téléchargés sous peu'
  })
}

const bulkExport = () => {
  toast({
    title: 'Export en cours',
    description: `${selectedIds.value.length} log(s) seront téléchargés sous peu`
  })
}

const bulkDelete = async () => {
  if (!confirm(`Êtes-vous sûr de vouloir supprimer ${selectedIds.value.length} log(s) ?`)) return
  loading.value = true
  try {
    // TODO: Implement bulk delete
    toast({
      title: 'Logs supprimés',
      description: `${selectedIds.value.length} log(s) supprimé(s)`
    })
    selectedIds.value = []
    await loadLogs()
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

const viewDetails = (id: number) => {
  const log = logs.value.find((l) => l.id === id)
  if (log) {
    // TODO: Show details in a dialog
    toast({
      title: 'Détails du log',
      description: log.message
    })
  }
}

const getLevelVariant = (level: string) => {
  const variants: Record<string, 'default' | 'secondary' | 'destructive' | 'outline'> = {
    INFO: 'default',
    WARNING: 'outline',
    ERROR: 'destructive',
    CRITICAL: 'destructive'
  }
  return variants[level] || 'default'
}

const formatDateTime = (date: string) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('fr-FR')
}

const loadLogs = async () => {
  loading.value = true
  try {
    // TODO: Load from API
    logs.value = []
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Impossible de charger les logs',
      variant: 'destructive'
    })
  } finally {
    loading.value = false
  }
}

const loadStats = async () => {
  try {
    // TODO: Load from API
    stats.value = {
      today: 0,
      thisWeek: 0,
      errors: 0,
      total: 0
    }
  } catch (error) {
    console.error('Error loading stats:', error)
  }
}

onMounted(() => {
  loadLogs()
  loadStats()
})
</script>

