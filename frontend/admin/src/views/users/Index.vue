<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-3xl font-bold">{{ t('users.title') }}</h1>
        <p class="text-muted-foreground mt-1">{{ t('users.description') }}</p>
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
          {{ t('users.newUser') }}
        </Button>
      </div>
    </div>

    <!-- Filters -->
    <Card>
      <CardContent class="p-4">
        <div class="grid grid-cols-1 md:grid-cols-5 gap-4">
          <div class="space-y-2">
            <Label>Recherche</Label>
            <Input
              v-model="searchQuery"
              placeholder="Nom, email..."
              @input="handleSearch"
            />
          </div>
          <div class="space-y-2">
            <Label>Organisation</Label>
            <Combobox
              v-model="selectedOrganization"
              :search-value="organizationSearch"
              @update:search-value="handleSearchValueChange"
              @update:model-value="handleOrganizationChange"
            >
              <ComboboxAnchor as-child>
                <ComboboxInput placeholder="Rechercher une organisation..." />
              </ComboboxAnchor>
              <ComboboxList>
                <ComboboxItem value="all">
                  Toutes les organisations
                </ComboboxItem>
                <ComboboxItem
                  v-for="org in filteredOrganizations"
                  :key="org.id"
                  :value="org.id.toString()"
                >
                  {{ org.name }}
                </ComboboxItem>
                <ComboboxEmpty>
                  Aucune organisation trouvée
                </ComboboxEmpty>
              </ComboboxList>
            </Combobox>
          </div>
          <div class="space-y-2">
            <Label>Statut</Label>
            <Select v-model="selectedStatus" @update:model-value="handleFilter">
              <SelectTrigger>
                <SelectValue placeholder="Tous les statuts" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">Tous</SelectItem>
                <SelectItem value="ACTIVE">Actif</SelectItem>
                <SelectItem value="INACTIVE">Inactif</SelectItem>
                <SelectItem value="SUSPENDED">Suspendu</SelectItem>
                <SelectItem value="PENDING">En attente</SelectItem>
              </SelectContent>
            </Select>
          </div>
          <div class="space-y-2">
            <Label>Rôle</Label>
            <Select v-model="selectedRole" @update:model-value="handleFilter">
              <SelectTrigger>
                <SelectValue placeholder="Tous les rôles" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">Tous</SelectItem>
                <SelectItem value="ADMIN">Admin</SelectItem>
                <SelectItem value="AGENT">Agent</SelectItem>
                <SelectItem value="FREELANCE">Freelance</SelectItem>
                <SelectItem value="AUTO_ENTREPRENEUR">Auto-entrepreneur</SelectItem>
                <SelectItem value="PARTICULAR">Particulier</SelectItem>
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

    <!-- Stats Cards -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
      <Card>
        <CardHeader class="pb-2">
          <CardDescription>Total Utilisateurs</CardDescription>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold">{{ total }}</div>
        </CardContent>
      </Card>
      <Card>
        <CardHeader class="pb-2">
          <CardDescription>Actifs</CardDescription>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold text-green-600">{{ activeUsers.length }}</div>
        </CardContent>
      </Card>
      <Card>
        <CardHeader class="pb-2">
          <CardDescription>Inactifs</CardDescription>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold text-gray-600">{{ inactiveUsers.length }}</div>
        </CardContent>
      </Card>
      <Card>
        <CardHeader class="pb-2">
          <CardDescription>Suspendus</CardDescription>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold text-red-600">{{ suspendedUsers.length }}</div>
        </CardContent>
      </Card>
    </div>

    <!-- Users Table -->
    <Card>
      <CardContent class="p-0">
        <!-- Actions en masse -->
        <div
          v-if="selectedIds.length > 0"
          class="border-b bg-muted/30 p-4 flex items-center justify-between"
        >
          <div class="text-sm text-muted-foreground">
            {{ selectedIds.length }} utilisateur(s) sélectionné(s)
          </div>
          <div class="flex gap-2">
            <Button variant="outline" size="sm" @click="bulkActivate" :disabled="bulkLoading">
              <CheckCircle class="mr-2 h-4 w-4" />
              Activer
            </Button>
            <Button variant="outline" size="sm" @click="bulkDeactivate" :disabled="bulkLoading">
              <XCircle class="mr-2 h-4 w-4" />
              Désactiver
            </Button>
            <Button variant="outline" size="sm" @click="bulkSuspend" :disabled="bulkLoading">
              <Ban class="mr-2 h-4 w-4" />
              Suspendre
            </Button>
            <Button
              variant="outline"
              size="sm"
              @click="bulkDelete"
              class="text-destructive"
              :disabled="bulkLoading"
            >
              <Trash2 class="mr-2 h-4 w-4" />
              Supprimer
            </Button>
          </div>
        </div>
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>
                <Checkbox v-model="selectAll" />
              </TableHead>
              <TableHead>Utilisateur</TableHead>
              <TableHead>Email</TableHead>
              <TableHead>Rôles</TableHead>
              <TableHead>Statut</TableHead>
              <TableHead>Organisation</TableHead>
              <TableHead>Dernière connexion</TableHead>
              <TableHead class="text-right">Actions</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            <TableRow v-if="loading">
              <TableCell colspan="8" class="text-center py-8">
                <div class="flex items-center justify-center">
                  <Loader2 class="h-6 w-6 animate-spin mr-2" />
                  Chargement...
                </div>
              </TableCell>
            </TableRow>
            <TableRow v-else-if="users.length === 0">
              <TableCell colspan="8" class="text-center py-8 text-muted-foreground">
                Aucun utilisateur trouvé
              </TableCell>
            </TableRow>
            <TableRow v-else v-for="user in users" :key="user.id">
              <TableCell @click.stop>
                <Checkbox
                  :model-value="selectedIds.includes(user.id)"
                  @update:model-value="(val) => handleUserCheckboxChange(user.id, val)"
                />
              </TableCell>
              <TableCell>
                <div class="flex items-center space-x-3">
                  <Avatar>
                    <AvatarImage :src="user.avatar" v-if="user.avatar" />
                    <AvatarFallback>{{ (user.name || user.email || 'U').charAt(0).toUpperCase() }}</AvatarFallback>
                  </Avatar>
                  <div>
                    <div class="font-medium">{{ user.name }}</div>
                    <div class="text-sm text-muted-foreground">{{ user.phone || 'N/A' }}</div>
                  </div>
                </div>
              </TableCell>
              <TableCell>{{ user.email }}</TableCell>
              <TableCell>
                <div class="flex flex-wrap gap-1">
                  <Badge v-for="role in user.roles" :key="role" variant="secondary">
                    {{ role }}
                  </Badge>
                </div>
              </TableCell>
              <TableCell>
                <Badge :variant="getStatusVariant(user.status)">
                  {{ getStatusLabel(user.status) }}
                </Badge>
              </TableCell>
              <TableCell>{{ user.organizationName || 'N/A' }}</TableCell>
              <TableCell>
                {{ user.lastLoginAt ? formatDate(user.lastLoginAt) : 'Jamais' }}
              </TableCell>
              <TableCell class="text-right">
                <DropdownMenu>
                  <DropdownMenuTrigger as-child>
                    <Button variant="ghost" size="icon">
                      <MoreVertical class="h-4 w-4" />
                    </Button>
                  </DropdownMenuTrigger>
                  <DropdownMenuContent align="end">
                    <DropdownMenuItem @click="viewUser(user.id)">
                      <Eye class="mr-2 h-4 w-4" />
                      Voir
                    </DropdownMenuItem>
                    <DropdownMenuItem @click="editUser(user.id)" v-if="canEditUser(user.id)">
                      <Edit class="mr-2 h-4 w-4" />
                      Modifier
                    </DropdownMenuItem>
                    <DropdownMenuSeparator />
                    <DropdownMenuItem
                      @click="activateUser(user.id)"
                      v-if="user.status !== 'ACTIVE'"
                    >
                      <CheckCircle class="mr-2 h-4 w-4" />
                      Activer
                    </DropdownMenuItem>
                    <DropdownMenuItem
                      @click="deactivateUser(user.id)"
                      v-if="user.status === 'ACTIVE'"
                    >
                      <XCircle class="mr-2 h-4 w-4" />
                      Désactiver
                    </DropdownMenuItem>
                    <DropdownMenuItem
                      @click="suspendUser(user.id)"
                      v-if="user.status !== 'SUSPENDED'"
                    >
                      <Ban class="mr-2 h-4 w-4" />
                      Suspendre
                    </DropdownMenuItem>
                    <DropdownMenuSeparator />
                    <DropdownMenuItem
                      @click="deleteUser(user.id)"
                      v-if="canDeleteUser(user.id)"
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
        </Table>
      </CardContent>
    </Card>

    <!-- Pagination -->
    <div class="flex items-center justify-between" v-if="total > 0">
      <div class="text-sm text-muted-foreground">
        Affichage de {{ (currentPage - 1) * pageSize + 1 }} à {{ Math.min(currentPage * pageSize, total) }} sur {{ total }}
      </div>
      <div class="flex items-center space-x-2">
        <Button
          variant="outline"
          size="sm"
          @click="setPage(currentPage - 1)"
          :disabled="currentPage === 1"
        >
          <ChevronLeft class="h-4 w-4" />
        </Button>
        <div class="text-sm">
          Page {{ currentPage }} sur {{ Math.ceil(total / pageSize) }}
        </div>
        <Button
          variant="outline"
          size="sm"
          @click="setPage(currentPage + 1)"
          :disabled="currentPage >= Math.ceil(total / pageSize)"
        >
          <ChevronRight class="h-4 w-4" />
        </Button>
      </div>
    </div>

    <!-- Create/Edit Dialog -->
    <UserDialog
      v-model:open="dialogOpen"
      :user="selectedUser"
      @saved="handleUserSaved"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useDebounceFn } from '@vueuse/core'
import { useUser, UserStatus, UserRole, organizationService, type Organization } from '@viridial/shared'

const { t } = useI18n()
const router = useRouter()
const { toast } = useToast()
import { Card, CardContent, CardHeader, CardDescription } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Table, TableHeader, TableRow, TableHead, TableBody, TableCell } from '@/components/ui/table'
import { Badge } from '@/components/ui/badge'
import { Avatar, AvatarImage, AvatarFallback } from '@/components/ui/avatar'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue
} from '@/components/ui/select'
import {
  Combobox,
  ComboboxAnchor,
  ComboboxInput,
  ComboboxList,
  ComboboxItem,
  ComboboxEmpty
} from '@/components/ui/combobox'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger
} from '@/components/ui/dropdown-menu'
import {
  Plus,
  X,
  Loader2,
  MoreVertical,
  Eye,
  Edit,
  CheckCircle,
  XCircle,
  Ban,
  Trash2,
  ChevronLeft,
  ChevronRight,
  Download
} from 'lucide-vue-next'
import { Checkbox } from '@/components/ui/checkbox'
import { useToast } from '@/components/ui/toast'
import { userService } from '@viridial/shared'
import UserDialog from '@/components/users/UserDialog.vue'

const {
  users,
  loading,
  total,
  currentPage,
  pageSize,
  activeUsers,
  inactiveUsers,
  suspendedUsers,
  loadUsers,
  deleteUser,
  activateUser,
  deactivateUser,
  suspendUser,
  setPage,
  canEditUser,
  canDeleteUser
} = useUser()

const searchQuery = ref('')
const selectedOrganization = ref('all')
const selectedStatus = ref('all')
const selectedRole = ref('all')
const dialogOpen = ref(false)
const selectedUser = ref(null)
const organizations = ref<Organization[]>([])
const organizationSearch = ref('')
const selectedIds = ref<number[]>([])
const bulkLoading = ref(false)

// Organisations filtrées par la recherche
const filteredOrganizations = computed(() => {
  if (!organizationSearch.value) {
    return organizations.value
  }
  const search = organizationSearch.value.toLowerCase()
  return organizations.value.filter(org =>
    org.name.toLowerCase().includes(search)
  )
})

onMounted(async () => {
  await loadOrganizations()
  loadUsers()
})

const loadOrganizations = async () => {
  try {
    const result = await organizationService.getAll()
    organizations.value = result.organizations
  } catch (error) {
    console.error('Error loading organizations:', error)
  }
}

const performSearch = () => {
  // Réinitialiser la page à 1 lors d'une nouvelle recherche
  setPage(1)
  loadUsers({
    search: searchQuery.value || undefined,
    organizationId: (selectedOrganization.value && selectedOrganization.value !== 'all' && selectedOrganization.value !== null) 
      ? Number(selectedOrganization.value) 
      : undefined,
    status: (selectedStatus.value && selectedStatus.value !== 'all') ? selectedStatus.value as UserStatus : undefined,
    role: (selectedRole.value && selectedRole.value !== 'all') ? selectedRole.value as UserRole : undefined
  })
}

// Debounce la recherche pour éviter trop d'appels API (300ms de délai)
const handleSearch = useDebounceFn(performSearch, 300)

const handleFilter = () => {
  handleSearch()
}

const handleSearchValueChange = (value: any) => {
  organizationSearch.value = String(value || '')
}

const handleOrganizationChange = (value: any) => {
  selectedOrganization.value = value ? String(value) : 'all'
  organizationSearch.value = '' // Réinitialiser la recherche après sélection
  handleSearch()
}

const resetFilters = () => {
  searchQuery.value = ''
  selectedOrganization.value = 'all'
  organizationSearch.value = ''
  selectedStatus.value = 'all'
  selectedRole.value = 'all'
  setPage(1)
  loadUsers()
}

const openCreateDialog = () => {
  selectedUser.value = null
  dialogOpen.value = true
}

const editUser = (id: number) => {
  const user = users.value.find(u => u.id === id)
  if (user) {
    selectedUser.value = { ...user } as any
  } else {
    selectedUser.value = null
  }
  dialogOpen.value = true
}

const viewUser = (id: number) => {
  router.push(`/users/${id}`)
}

const handleUserSaved = () => {
  dialogOpen.value = false
  loadUsers()
}

const getStatusVariant = (status: string): 'default' | 'secondary' | 'destructive' | 'outline' => {
  const variants: Record<string, 'default' | 'secondary' | 'destructive' | 'outline'> = {
    ACTIVE: 'default',
    INACTIVE: 'secondary',
    SUSPENDED: 'destructive',
    PENDING: 'outline'
  }
  return variants[status] || 'secondary'
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
  return new Date(date).toLocaleDateString('fr-FR', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// toggleSelect supprimé - remplacé par handleUserCheckboxChange

const handleUserCheckboxChange = (id: number, val: boolean | 'indeterminate') => {
  const checked = Boolean(val)
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

const toggleSelectAll = (checked: boolean) => {
  if (checked) {
    selectedIds.value = users.value.map((u) => u.id)
  } else {
    selectedIds.value = []
  }
}

// Computed pour synchroniser selectAll avec selectedIds
// Supporte les états: false, true, 'indeterminate'
const selectAll = computed({
  get: (): boolean | 'indeterminate' => {
    if (users.value.length === 0) return false
    if (selectedIds.value.length === 0) return false
    if (selectedIds.value.length === users.value.length) return true
    return 'indeterminate'
  },
  set: (value: boolean | 'indeterminate') => {
    // Si c'est 'indeterminate', on sélectionne tout
    if (value === 'indeterminate' || value === true) {
      toggleSelectAll(true)
    } else {
      toggleSelectAll(false)
    }
  }
})


// Aligné avec le module organizations
const bulkActivate = async () => {
  if (selectedIds.value.length === 0) return
  bulkLoading.value = true
  try {
    await Promise.all(selectedIds.value.map((id) => activateUser(id)))
    toast({
      title: 'Utilisateurs activés',
      description: `${selectedIds.value.length} utilisateur(s) activé(s) avec succès`
    })
    selectedIds.value = []
    await loadUsers()
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Une erreur est survenue lors de l\'activation',
      variant: 'destructive'
    })
  } finally {
    bulkLoading.value = false
  }
}

const bulkDeactivate = async () => {
  if (selectedIds.value.length === 0) return
  if (!confirm(`Êtes-vous sûr de vouloir désactiver ${selectedIds.value.length} utilisateur(s) ?`)) return
  bulkLoading.value = true
  try {
    await Promise.all(selectedIds.value.map((id) => deactivateUser(id)))
    toast({
      title: 'Utilisateurs désactivés',
      description: `${selectedIds.value.length} utilisateur(s) désactivé(s) avec succès`
    })
    selectedIds.value = []
    await loadUsers()
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Une erreur est survenue lors de la désactivation',
      variant: 'destructive'
    })
  } finally {
    bulkLoading.value = false
  }
}

const bulkSuspend = async () => {
  if (selectedIds.value.length === 0) return
  if (!confirm(`Êtes-vous sûr de vouloir suspendre ${selectedIds.value.length} utilisateur(s) ?`)) return
  bulkLoading.value = true
  try {
    await Promise.all(selectedIds.value.map((id) => suspendUser(id)))
    toast({
      title: 'Utilisateurs suspendus',
      description: `${selectedIds.value.length} utilisateur(s) suspendu(s) avec succès`
    })
    selectedIds.value = []
    await loadUsers()
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Une erreur est survenue lors de la suspension',
      variant: 'destructive'
    })
  } finally {
    bulkLoading.value = false
  }
}

const bulkDelete = async () => {
  if (selectedIds.value.length === 0) return
  if (!confirm(`Êtes-vous sûr de vouloir supprimer ${selectedIds.value.length} utilisateur(s) ? Cette action est irréversible.`)) return
  bulkLoading.value = true
  try {
    await Promise.all(selectedIds.value.map((id) => deleteUser(id)))
    toast({
      title: 'Utilisateurs supprimés',
      description: `${selectedIds.value.length} utilisateur(s) supprimé(s) avec succès`
    })
    selectedIds.value = []
    await loadUsers()
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Une erreur est survenue lors de la suppression',
      variant: 'destructive'
    })
  } finally {
    bulkLoading.value = false
  }
}

// Export functions
const exportToCSV = async () => {
  try {
    // Charger tous les utilisateurs avec les filtres actuels
    const params: any = {
      size: 10000 // Charger un grand nombre pour l'export
    }
    if (searchQuery.value) params.search = searchQuery.value
    if (selectedOrganization.value && selectedOrganization.value !== 'all') {
      params.organizationId = Number(selectedOrganization.value)
    }
    if (selectedStatus.value && selectedStatus.value !== 'all') {
      params.status = selectedStatus.value
    }
    if (selectedRole.value && selectedRole.value !== 'all') {
      params.role = selectedRole.value
    }

    const result = await userService.getAll(params)
    const usersToExport = result.users

    // Créer le contenu CSV
    const headers = ['ID', 'Nom', 'Email', 'Téléphone', 'Statut', 'Rôles', 'Organisation', 'Dernière connexion', 'Créé le']
    const rows = usersToExport.map((user: any) => [
      user.id,
      user.name,
      user.email,
      user.phone || '',
      getStatusLabel(user.status),
      user.roles.join(', '),
      user.organizationName || '',
      user.lastLoginAt ? formatDate(user.lastLoginAt) : '',
      formatDate(user.createdAt)
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
    link.setAttribute('download', `users_export_${new Date().toISOString().split('T')[0]}.csv`)
    link.style.visibility = 'hidden'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)

    toast({
      title: 'Export réussi',
      description: `${usersToExport.length} utilisateur(s) exporté(s) en CSV`
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
    // Charger tous les utilisateurs avec les filtres actuels
    const params: any = {
      size: 10000
    }
    if (searchQuery.value) params.search = searchQuery.value
    if (selectedOrganization.value && selectedOrganization.value !== 'all') {
      params.organizationId = Number(selectedOrganization.value)
    }
    if (selectedStatus.value && selectedStatus.value !== 'all') {
      params.status = selectedStatus.value
    }
    if (selectedRole.value && selectedRole.value !== 'all') {
      params.role = selectedRole.value
    }

    const result = await userService.getAll(params)
    const usersToExport = result.users

    // Créer le contenu Excel (format TSV avec en-têtes)
    const headers = ['ID', 'Nom', 'Email', 'Téléphone', 'Statut', 'Rôles', 'Organisation', 'Dernière connexion', 'Créé le']
    const rows = usersToExport.map((user: any) => [
      user.id,
      user.name,
      user.email,
      user.phone || '',
      getStatusLabel(user.status),
      user.roles.join(', '),
      user.organizationName || '',
      user.lastLoginAt ? formatDate(user.lastLoginAt) : '',
      formatDate(user.createdAt)
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
    link.setAttribute('download', `users_export_${new Date().toISOString().split('T')[0]}.xls`)
    link.style.visibility = 'hidden'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)

    toast({
      title: 'Export réussi',
      description: `${usersToExport.length} utilisateur(s) exporté(s) en Excel`
    })
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Impossible d\'exporter les données',
      variant: 'destructive'
    })
  }
}
</script>

