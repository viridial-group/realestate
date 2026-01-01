<template>
  <div class="space-y-6">
    <!-- Header avec Actions Rapides -->
    <div class="flex items-center justify-between flex-wrap gap-4">
      <div>
        <h1 class="text-3xl font-bold">Gestion des Organisations</h1>
        <p class="text-muted-foreground mt-1">Gérez toutes les organisations de la plateforme</p>
      </div>
      <div class="flex gap-2">
        <Button variant="outline" @click="exportToCSV" :disabled="loading">
          <Download class="mr-2 h-4 w-4" />
          Export CSV
        </Button>
        <Button variant="outline" @click="exportToExcel" :disabled="loading">
          <Download class="mr-2 h-4 w-4" />
          Export Excel
        </Button>
        <Button @click="openCreateDialog" size="lg">
          <Plus class="mr-2 h-4 w-4" />
          Nouvelle Organisation
        </Button>
      </div>
    </div>

    <!-- Stats Cards -->
    <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
      <Card class="cursor-pointer hover:shadow-md transition-shadow" @click="filterByStatus('ACTIVE')">
        <CardHeader class="pb-2">
          <CardDescription>Organisations Actives</CardDescription>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold text-green-600">{{ stats.active || 0 }}</div>
          <p class="text-xs text-muted-foreground mt-1">En activité</p>
        </CardContent>
      </Card>
      <Card class="cursor-pointer hover:shadow-md transition-shadow" @click="filterByStatus('INACTIVE')">
        <CardHeader class="pb-2">
          <CardDescription>Inactives</CardDescription>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold text-gray-600">{{ stats.inactive || 0 }}</div>
          <p class="text-xs text-muted-foreground mt-1">Inactives</p>
        </CardContent>
      </Card>
      <Card>
        <CardHeader class="pb-2">
          <CardDescription>Total</CardDescription>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold">{{ stats.total || 0 }}</div>
          <p class="text-xs text-muted-foreground mt-1">Toutes organisations</p>
        </CardContent>
      </Card>
    </div>

    <!-- Filtres Avancés -->
    <Card>
      <CardContent class="p-4">
        <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
          <div class="space-y-2">
            <Label>Recherche</Label>
            <Input
              v-model="searchQuery"
              placeholder="Nom, email, SIRET..."
              @input="handleSearch"
            >
              <template #prefix>
                <Search class="h-4 w-4" />
              </template>
            </Input>
          </div>
          <div class="space-y-2">
            <Label>Statut</Label>
            <Select v-model="selectedStatus" @update:model-value="handleFilter">
              <SelectTrigger>
                <SelectValue placeholder="Tous les statuts" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem :value="null">Tous</SelectItem>
                <SelectItem value="ACTIVE">Actif</SelectItem>
                <SelectItem value="INACTIVE">Inactif</SelectItem>
              </SelectContent>
            </Select>
          </div>
          <div class="space-y-2">
            <Label>Date de création</Label>
            <Select v-model="dateFilter" @update:model-value="handleFilter">
              <SelectTrigger>
                <SelectValue placeholder="Période" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem :value="null">Toutes</SelectItem>
                <SelectItem value="today">Aujourd'hui</SelectItem>
                <SelectItem value="week">Cette semaine</SelectItem>
                <SelectItem value="month">Ce mois</SelectItem>
                <SelectItem value="year">Cette année</SelectItem>
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

    <!-- Table avec Actions Rapides -->
    <Card>
      <CardContent class="p-0">
        <!-- Actions en masse -->
        <div
          v-if="selectedIds.length > 0"
          class="border-b bg-muted/30 p-4 flex items-center justify-between"
        >
          <div class="text-sm text-muted-foreground">
            {{ selectedIds.length }} organisation(s) sélectionnée(s)
          </div>
          <div class="flex gap-2">
            <Button variant="outline" size="sm" @click="bulkActivate" :disabled="loading">
              <CheckCircle class="mr-2 h-4 w-4" />
              Activer
            </Button>
            <Button variant="outline" size="sm" @click="bulkSuspend" :disabled="loading">
              <Ban class="mr-2 h-4 w-4" />
              Désactiver
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
                <th class="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase tracking-wider">
                  <Checkbox v-model="selectAll" />
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase tracking-wider">
                  Organisation
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase tracking-wider">
                  Contact
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase tracking-wider">
                  Utilisateurs
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase tracking-wider">
                  Statut
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase tracking-wider">
                  Créée le
                </th>
                <th class="px-6 py-3 text-right text-xs font-medium text-muted-foreground uppercase tracking-wider">
                  Actions
                </th>
              </tr>
            </thead>
            <tbody class="divide-y divide-border">
              <tr v-if="loading">
                <td colspan="7" class="text-center py-8">
                  <div class="flex items-center justify-center">
                    <Loader2 class="h-6 w-6 animate-spin mr-2" />
                    Chargement...
                  </div>
                </td>
              </tr>
              <tr v-else-if="filteredOrganizations.length === 0">
                <td colspan="7" class="text-center py-8 text-muted-foreground">
                  Aucune organisation trouvée
                </td>
              </tr>
              <tr
                v-else
                v-for="org in paginatedOrganizations"
                :key="org.id"
                class="hover:bg-muted/50 transition-colors cursor-pointer"
                @click="viewOrganization(Number(org.id))"
              >
                <td class="px-6 py-4 whitespace-nowrap" @click.stop>
                  <Checkbox
                    :model-value="selectedIds.includes(Number(org.id))"
                    @update:model-value="(val) => handleOrganizationCheckboxChange(Number(org.id), Boolean(val))"
                  />
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="flex items-center">
                    <div class="flex-shrink-0 h-10 w-10 rounded-lg bg-primary/10 flex items-center justify-center">
                      <Building2 class="h-5 w-5 text-primary" />
                    </div>
                    <div class="ml-4">
                      <div class="text-sm font-medium">{{ org.name }}</div>
                      <div class="text-sm text-muted-foreground">{{ org.domain || 'Sans domaine' }}</div>
                    </div>
                  </div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="text-sm">{{ org.description || 'Aucune description' }}</div>
                  <div class="text-sm text-muted-foreground">{{ org.parentId ? `Parent: ${org.parentId}` : 'Organisation racine' }}</div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="text-sm font-medium">-</div>
                  <div class="text-xs text-muted-foreground">utilisateurs</div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <Badge :variant="org.active ? 'default' : 'secondary'">
                    {{ org.active ? 'Actif' : 'Inactif' }}
                  </Badge>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-muted-foreground">
                  {{ formatDate(org.createdAt) }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium" @click.stop>
                  <DropdownMenu>
                    <DropdownMenuTrigger as-child>
                      <Button variant="ghost" size="sm">
                        <MoreVertical class="h-4 w-4" />
                      </Button>
                    </DropdownMenuTrigger>
                    <DropdownMenuContent align="end">
                      <DropdownMenuItem @click.stop="viewOrganization(Number(org.id))">
                        <Eye class="mr-2 h-4 w-4" />
                        Voir les détails
                      </DropdownMenuItem>
                      <DropdownMenuItem @click.stop="editOrganization(Number(org.id))">
                        <Edit class="mr-2 h-4 w-4" />
                        Modifier
                      </DropdownMenuItem>
                      <DropdownMenuItem
                        v-if="org.active"
                        @click.stop="suspendOrganization(Number(org.id))"
                      >
                        <Ban class="mr-2 h-4 w-4" />
                        Désactiver
                      </DropdownMenuItem>
                      <DropdownMenuItem
                        v-if="!org.active"
                        @click.stop="activateOrganization(Number(org.id))"
                      >
                        <CheckCircle class="mr-2 h-4 w-4" />
                        Activer
                      </DropdownMenuItem>
                      <DropdownMenuSeparator />
                      <DropdownMenuItem
                        @click.stop="deleteOrganization(Number(org.id))"
                        class="text-destructive"
                      >
                        <Trash2 class="mr-2 h-4 w-4" />
                        Supprimer
                      </DropdownMenuItem>
                    </DropdownMenuContent>
                  </DropdownMenu>
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

    <!-- Dialog Création/Modification -->
    <OrganizationDialog
      v-model:open="dialogOpen"
      :organization="selectedOrganization"
      @saved="handleSaved"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useToast } from '@/components/ui/toast'
import { Card, CardContent, CardHeader, CardDescription } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { Badge } from '@/components/ui/badge'
import { Checkbox } from '@/components/ui/checkbox'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger
} from '@/components/ui/dropdown-menu'
import {
  Plus,
  Download,
  Search,
  X,
  Building2,
  MoreVertical,
  Eye,
  Edit,
  Ban,
  CheckCircle,
  Trash2,
  Loader2
} from 'lucide-vue-next'
import OrganizationDialog from './OrganizationDialog.vue'
import { organizationService, type Organization } from '@viridial/shared'

const router = useRouter()
const route = useRoute()
const { toast } = useToast()
const loading = ref(false)
const searchQuery = ref('')
const selectedStatus = ref<string | null>(null)
const dateFilter = ref<string | null>(null)
const selectedIds = ref<number[]>([])
const currentPage = ref(1)
const pageSize = 10

const organizations = ref<Organization[]>([])
const selectedOrganization = ref<Organization | null>(null)
const dialogOpen = ref(false)
const statsData = ref({ total: 0, active: 0, inactive: 0, suspended: 0 })

const stats = computed(() => statsData.value)

const filteredOrganizations = computed(() => {
  let filtered = organizations.value

  if (selectedStatus.value) {
    if (selectedStatus.value === 'ACTIVE') {
      filtered = filtered.filter(org => org.active)
    } else if (selectedStatus.value === 'INACTIVE') {
      filtered = filtered.filter(org => !org.active)
    }
  }

  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(
      org =>
        org.name.toLowerCase().includes(query) ||
        org.description?.toLowerCase().includes(query) ||
        org.domain?.toLowerCase().includes(query)
    )
  }

  // Date filter
  if (dateFilter.value) {
    const now = new Date()
    filtered = filtered.filter(org => {
      const createdAt = new Date(org.createdAt)
      switch (dateFilter.value) {
        case 'today':
          return createdAt.toDateString() === now.toDateString()
        case 'week':
          const weekAgo = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000)
          return createdAt >= weekAgo
        case 'month':
          const monthAgo = new Date(now.getTime() - 30 * 24 * 60 * 60 * 1000)
          return createdAt >= monthAgo
        case 'year':
          const yearAgo = new Date(now.getTime() - 365 * 24 * 60 * 60 * 1000)
          return createdAt >= yearAgo
        default:
          return true
      }
    })
  }

  return filtered
})

const total = computed(() => filteredOrganizations.value.length)
const totalPages = computed(() => Math.ceil(total.value / pageSize))
const startIndex = computed(() => (currentPage.value - 1) * pageSize)
const endIndex = computed(() => Math.min(startIndex.value + pageSize, total.value))
const paginatedOrganizations = computed(() => {
  return filteredOrganizations.value.slice(startIndex.value, endIndex.value)
})

const handleSearch = () => {
  currentPage.value = 1
  loadOrganizations()
}

const handleFilter = () => {
  currentPage.value = 1
  loadOrganizations()
}

const resetFilters = () => {
  searchQuery.value = ''
  selectedStatus.value = null
  dateFilter.value = null
  currentPage.value = 1
  loadOrganizations()
}

const filterByStatus = (status: string) => {
  selectedStatus.value = status || null
  currentPage.value = 1
  loadOrganizations()
}

const selectAll = computed({
  get: (): boolean | 'indeterminate' => {
    if (paginatedOrganizations.value.length === 0) return false
    if (selectedIds.value.length === 0) return false
    const pageIds = paginatedOrganizations.value.map(org => Number(org.id))
    const allSelected = pageIds.every(id => selectedIds.value.includes(id))
    const someSelected = pageIds.some(id => selectedIds.value.includes(id))
    if (allSelected) return true
    if (someSelected) return 'indeterminate'
    return false
  },
  set: (value: boolean | 'indeterminate') => {
    const pageIds = paginatedOrganizations.value.map(org => Number(org.id))
    if (value === true || value === 'indeterminate') {
      // Add all page items that aren't already selected
      pageIds.forEach(id => {
        if (!selectedIds.value.includes(id)) {
          selectedIds.value.push(id)
        }
      })
    } else {
      // Remove all page items from selection
      selectedIds.value = selectedIds.value.filter(id => !pageIds.includes(id))
    }
  }
})

const handleOrganizationCheckboxChange = (id: number, checked: boolean) => {
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

const previousPage = () => {
  if (currentPage.value > 1) currentPage.value--
}

const nextPage = () => {
  if (currentPage.value < totalPages.value) currentPage.value++
}

const openCreateDialog = () => {
  selectedOrganization.value = null
  dialogOpen.value = true
}

const editOrganization = (id: number) => {
  selectedOrganization.value = organizations.value.find((o) => o.id === id) || null
  dialogOpen.value = true
}

const viewOrganization = (id: number) => {
  router.push(`/organizations/${id}`)
}

const suspendOrganization = async (id: number) => {
  loading.value = true
  try {
    await organizationService.update(id, { active: false })
    toast({
      title: 'Organisation désactivée',
      description: 'L\'organisation a été désactivée avec succès'
    })
    await loadOrganizations()
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

const activateOrganization = async (id: number) => {
  loading.value = true
  try {
    await organizationService.update(id, { active: true })
    toast({
      title: 'Organisation activée',
      description: 'L\'organisation a été activée avec succès'
    })
    await loadOrganizations()
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

const deleteOrganization = async (id: number) => {
  if (!confirm('Êtes-vous sûr de vouloir supprimer cette organisation ?')) return
  loading.value = true
  try {
    await organizationService.delete(id)
    toast({
      title: 'Organisation supprimée',
      description: 'L\'organisation a été supprimée avec succès'
    })
    await loadOrganizations()
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

const bulkActivate = async () => {
  loading.value = true
  try {
    await Promise.all(selectedIds.value.map(async (id) => {
      await organizationService.update(id, { active: true })
    }))
    toast({
      title: 'Organisations activées',
      description: `${selectedIds.value.length} organisation(s) activée(s)`
    })
    selectedIds.value = []
    await loadOrganizations()
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

const bulkSuspend = async () => {
  loading.value = true
  try {
    await Promise.all(selectedIds.value.map(async (id) => {
      await organizationService.update(id, { active: false })
    }))
    toast({
      title: 'Organisations désactivées',
      description: `${selectedIds.value.length} organisation(s) désactivée(s)`
    })
    selectedIds.value = []
    await loadOrganizations()
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

const bulkDelete = async () => {
  if (!confirm(`Êtes-vous sûr de vouloir supprimer ${selectedIds.value.length} organisation(s) ?`)) return
  loading.value = true
  try {
    await Promise.all(selectedIds.value.map((id) => organizationService.delete(id)))
    toast({
      title: 'Organisations supprimées',
      description: `${selectedIds.value.length} organisation(s) supprimée(s)`
    })
    selectedIds.value = []
    await loadOrganizations()
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

// Export functions
const exportToCSV = async () => {
  try {
    // Charger toutes les organisations avec les filtres actuels
    const result = await organizationService.getAll()
    const orgsToExport = filteredOrganizations.value.length > 0 
      ? filteredOrganizations.value 
      : result.organizations

    // Créer le contenu CSV
    const headers = ['ID', 'Nom', 'Description', 'Domaine', 'Statut', 'Organisation Parente', 'Créée le']
    const rows = orgsToExport.map((org: any) => [
      org.id,
      org.name,
      org.description || '',
      org.domain || '',
      org.active ? 'Actif' : 'Inactif',
      org.parentId || '',
      formatDate(org.createdAt)
    ])

    const csvContent = [
      headers.join(','),
      ...rows.map((row: any[]) => row.map((cell: any) => `"${String(cell).replace(/"/g, '""')}"`).join(','))
    ].join('\n')

    // Télécharger le fichier
    const blob = new Blob(['\ufeff' + csvContent], { type: 'text/csv;charset=utf-8;' })
    const link = document.createElement('a')
    const url = URL.createObjectURL(blob)
    link.setAttribute('href', url)
    link.setAttribute('download', `organizations_export_${new Date().toISOString().split('T')[0]}.csv`)
    link.style.visibility = 'hidden'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)

    toast({
      title: 'Export réussi',
      description: `${orgsToExport.length} organisation(s) exportée(s) en CSV`
    })
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Impossible d\'exporter les données',
      variant: 'destructive'
    })
  }
}

const exportToExcel = async () => {
  try {
    // Charger toutes les organisations avec les filtres actuels
    const result = await organizationService.getAll()
    const orgsToExport = filteredOrganizations.value.length > 0 
      ? filteredOrganizations.value 
      : result.organizations

    // Créer le contenu Excel (format TSV avec en-têtes)
    const headers = ['ID', 'Nom', 'Description', 'Domaine', 'Statut', 'Organisation Parente', 'Créée le']
    const rows = orgsToExport.map((org: any) => [
      org.id,
      org.name,
      org.description || '',
      org.domain || '',
      org.active ? 'Actif' : 'Inactif',
      org.parentId || '',
      formatDate(org.createdAt)
    ])

    const excelContent = [
      headers.join('\t'),
      ...rows.map((row: any[]) => row.map((cell: any) => String(cell).replace(/\t/g, ' ')).join('\t'))
    ].join('\n')

    // Télécharger le fichier
    const blob = new Blob(['\ufeff' + excelContent], { type: 'application/vnd.ms-excel;charset=utf-8;' })
    const link = document.createElement('a')
    const url = URL.createObjectURL(blob)
    link.setAttribute('href', url)
    link.setAttribute('download', `organizations_export_${new Date().toISOString().split('T')[0]}.xls`)
    link.style.visibility = 'hidden'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)

    toast({
      title: 'Export réussi',
      description: `${orgsToExport.length} organisation(s) exportée(s) en Excel`
    })
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Impossible d\'exporter les données',
      variant: 'destructive'
    })
  }
}

const handleSaved = () => {
  dialogOpen.value = false
  loadOrganizations()
}


const formatDate = (date: string) => {
  if (!date) return '-'
  return new Date(date).toLocaleDateString('fr-FR')
}

const loadOrganizations = async () => {
  loading.value = true
  try {
    const result = await organizationService.getAll()
    organizations.value = result.organizations

    // Calculate stats from organizations
    const activeCount = organizations.value.filter(org => org.active).length
    statsData.value = {
      total: organizations.value.length,
      active: activeCount,
      inactive: organizations.value.length - activeCount,
      suspended: 0
    }
  } catch (error: any) {
    console.error('Error loading organizations:', error)
    toast({
      title: 'Erreur',
      description: error.message || 'Impossible de charger les organisations',
      variant: 'destructive'
    })
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await loadOrganizations()
  
  // Vérifier si un paramètre 'edit' est présent dans la requête
  const editId = route.query.edit
  if (editId) {
    const id = Number(editId)
    if (!isNaN(id)) {
      // Attendre que les organisations soient chargées, puis ouvrir le dialogue
      const org = organizations.value.find((o) => o.id === id)
      if (org) {
        selectedOrganization.value = org
        dialogOpen.value = true
      } else {
        // Si l'organisation n'est pas trouvée, essayer de la charger directement
        try {
          const loadedOrg = await organizationService.getById(id)
          selectedOrganization.value = loadedOrg
          dialogOpen.value = true
        } catch (error) {
          toast({
            title: 'Erreur',
            description: 'Organisation introuvable',
            variant: 'destructive'
          })
        }
      }
      // Nettoyer le paramètre de requête
      router.replace({ query: {} })
    }
  }
})

// Watcher pour détecter les changements de paramètre edit dans la route
watch(() => route.query.edit, async (editId) => {
  if (editId) {
    const id = Number(editId)
    if (!isNaN(id)) {
      // Vérifier si l'organisation est déjà chargée
      let org = organizations.value.find((o) => o.id === id)
      if (!org) {
        // Charger l'organisation si elle n'est pas dans la liste
        try {
          org = await organizationService.getById(id)
        } catch (error) {
          toast({
            title: 'Erreur',
            description: 'Organisation introuvable',
            variant: 'destructive'
          })
          return
        }
      }
      selectedOrganization.value = org
      dialogOpen.value = true
      // Nettoyer le paramètre de requête
      router.replace({ query: {} })
    }
  }
})
</script>
