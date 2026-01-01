<template>
  <div class="space-y-6">
    <!-- Header avec Actions Rapides -->
    <div class="flex items-center justify-between flex-wrap gap-4">
      <div>
        <h1 class="text-3xl font-bold">Gestion des Organisations</h1>
        <p class="text-muted-foreground mt-1">Gérez toutes les organisations de la plateforme</p>
      </div>
      <div class="flex gap-2">
        <Button variant="outline" @click="exportData" :disabled="loading">
          <Download class="mr-2 h-4 w-4" />
          Exporter
        </Button>
        <Button @click="openCreateDialog" size="lg">
          <Plus class="mr-2 h-4 w-4" />
          Nouvelle Organisation
        </Button>
      </div>
    </div>

    <!-- Stats Cards -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
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
      <Card class="cursor-pointer hover:shadow-md transition-shadow" @click="filterByStatus('SUSPENDED')">
        <CardHeader class="pb-2">
          <CardDescription>Suspendues</CardDescription>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold text-red-600">{{ stats.suspended || 0 }}</div>
          <p class="text-xs text-muted-foreground mt-1">Suspendues</p>
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
        <div class="grid grid-cols-1 md:grid-cols-5 gap-4">
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
                <SelectItem value="">Tous</SelectItem>
                <SelectItem value="ACTIVE">Actif</SelectItem>
                <SelectItem value="INACTIVE">Inactif</SelectItem>
                <SelectItem value="SUSPENDED">Suspendu</SelectItem>
                <SelectItem value="PENDING">En attente</SelectItem>
              </SelectContent>
            </Select>
          </div>
          <div class="space-y-2">
            <Label>Type</Label>
            <Select v-model="selectedType" @update:model-value="handleFilter">
              <SelectTrigger>
                <SelectValue placeholder="Tous les types" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="">Tous</SelectItem>
                <SelectItem value="AGENCY">Agence</SelectItem>
                <SelectItem value="FREELANCE">Freelance</SelectItem>
                <SelectItem value="COMPANY">Entreprise</SelectItem>
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
                <SelectItem value="">Toutes</SelectItem>
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
        <div class="overflow-x-auto">
          <table class="w-full">
            <thead class="bg-muted/50">
              <tr>
                <th class="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase tracking-wider">
                  <Checkbox v-model:checked="selectAll" @update:checked="toggleSelectAll" />
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
              <tr
                v-for="org in filteredOrganizations"
                :key="org.id"
                class="hover:bg-muted/50 transition-colors cursor-pointer"
                @click="viewOrganization(Number(org.id))"
              >
                <td class="px-6 py-4 whitespace-nowrap" @click.stop>
                  <Checkbox
                    :checked="selectedIds.includes(Number(org.id))"
                    @update:checked="toggleSelect(Number(org.id))"
                  />
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="flex items-center">
                    <div class="flex-shrink-0 h-10 w-10 rounded-lg bg-primary/10 flex items-center justify-center">
                      <Building2 class="h-5 w-5 text-primary" />
                    </div>
                    <div class="ml-4">
                      <div class="text-sm font-medium">{{ org.name }}</div>
                      <div class="text-sm text-muted-foreground">{{ org.type }}</div>
                    </div>
                  </div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="text-sm">{{ org.email }}</div>
                  <div class="text-sm text-muted-foreground">{{ org.phone }}</div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="text-sm font-medium">{{ org.userCount || 0 }}</div>
                  <div class="text-xs text-muted-foreground">utilisateurs</div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <Badge :variant="getStatusVariant(org.status)">
                    {{ getStatusLabel(org.status) }}
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
                        v-if="org.status === 'ACTIVE'"
                        @click.stop="suspendOrganization(Number(org.id))"
                      >
                        <Ban class="mr-2 h-4 w-4" />
                        Suspendre
                      </DropdownMenuItem>
                      <DropdownMenuItem
                        v-if="org.status === 'SUSPENDED'"
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

        <!-- Actions en masse -->
        <div
          v-if="selectedIds.length > 0"
          class="border-t bg-muted/30 p-4 flex items-center justify-between"
        >
          <div class="text-sm text-muted-foreground">
            {{ selectedIds.length }} organisation(s) sélectionnée(s)
          </div>
          <div class="flex gap-2">
            <Button variant="outline" size="sm" @click="bulkActivate">
              <CheckCircle class="mr-2 h-4 w-4" />
              Activer
            </Button>
            <Button variant="outline" size="sm" @click="bulkSuspend">
              <Ban class="mr-2 h-4 w-4" />
              Suspendre
            </Button>
            <Button variant="outline" size="sm" @click="bulkDelete" class="text-destructive">
              <Trash2 class="mr-2 h-4 w-4" />
              Supprimer
            </Button>
          </div>
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
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
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
  Trash2
} from 'lucide-vue-next'
import OrganizationDialog from './OrganizationDialog.vue'
import { organizationService, type Organization } from '@viridial/shared'

const router = useRouter()
const { toast } = useToast()
const loading = ref(false)
const searchQuery = ref('')
const selectedStatus = ref('')
const selectedType = ref('')
const dateFilter = ref('')
const selectedIds = ref<number[]>([])
const selectAll = ref(false)
const currentPage = ref(1)
const pageSize = 10

const organizations = ref<Organization[]>([])
const selectedOrganization = ref<Organization | null>(null)
const dialogOpen = ref(false)
const statsData = ref({ total: 0, active: 0, inactive: 0, suspended: 0 })

const stats = computed(() => statsData.value)

const filteredOrganizations = computed(() => organizations.value)

const total = computed(() => organizations.value.length)
const totalPages = computed(() => Math.ceil(total.value / pageSize))
const startIndex = computed(() => (currentPage.value - 1) * pageSize)
const endIndex = computed(() => Math.min(startIndex.value + pageSize, total.value))

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
  selectedStatus.value = ''
  selectedType.value = ''
  dateFilter.value = ''
  currentPage.value = 1
}

const filterByStatus = (status: string) => {
  selectedStatus.value = status
  currentPage.value = 1
  loadOrganizations()
}

const toggleSelect = (id: number) => {
  const index = selectedIds.value.indexOf(id)
  if (index > -1) {
    selectedIds.value.splice(index, 1)
  } else {
    selectedIds.value.push(id)
  }
}

const toggleSelectAll = (checked: boolean) => {
  if (checked) {
    selectedIds.value = filteredOrganizations.value.map((org) => org.id)
  } else {
    selectedIds.value = []
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
    await organizationService.suspend(id)
    toast({
      title: 'Organisation suspendue',
      description: 'L\'organisation a été suspendue avec succès'
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
    await organizationService.activate(id)
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
    await Promise.all(selectedIds.value.map((id) => organizationService.activate(id)))
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
    await Promise.all(selectedIds.value.map((id) => organizationService.suspend(id)))
    toast({
      title: 'Organisations suspendues',
      description: `${selectedIds.value.length} organisation(s) suspendue(s)`
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

const exportData = () => {
  // TODO: Implement export
  toast({
    title: 'Export en cours',
    description: 'Les données seront téléchargées sous peu'
  })
}

const handleSaved = () => {
  dialogOpen.value = false
  loadOrganizations()
}

const getStatusVariant = (status: string) => {
  const variants: Record<string, 'default' | 'secondary' | 'destructive' | 'outline'> = {
    ACTIVE: 'default',
    INACTIVE: 'secondary',
    SUSPENDED: 'destructive',
    PENDING: 'outline'
  }
  return variants[status] || 'default'
}

const getStatusLabel = (status: string) => {
  const labels: Record<string, string> = {
    ACTIVE: 'Actif',
    INACTIVE: 'Inactif',
    SUSPENDED: 'Suspendu',
    PENDING: 'En attente'
  }
  return labels[status] || status
}

const formatDate = (date: string) => {
  if (!date) return '-'
  return new Date(date).toLocaleDateString('fr-FR')
}

const loadOrganizations = async () => {
  loading.value = true
  try {
    const params: any = {
      page: currentPage.value - 1,
      size: pageSize
    }
    
    if (selectedStatus.value) params.status = selectedStatus.value
    if (selectedType.value) params.type = selectedType.value
    if (searchQuery.value) params.search = searchQuery.value

    const result = await organizationService.getAll(params)
    organizations.value = result.organizations

    // Load stats
    const stats = await organizationService.getStats()
    statsData.value = stats
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

onMounted(() => {
  loadOrganizations()
})
</script>
