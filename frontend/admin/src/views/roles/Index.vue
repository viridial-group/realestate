<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2.5xl font-bold">Gestion des Rôles</h1>
        <p class="text-muted-foreground mt-1">Gérer les rôles et leurs permissions</p>
      </div>
      <div class="flex gap-2">
        <Button variant="outline" @click="$router.push('/roles/assign')">
          <UserPlus class="mr-2 h-4 w-4" />
          Affecter des rôles
        </Button>
        <Button @click="openCreateDialog" size="lg">
          <Plus class="mr-2 h-4 w-4" />
          Nouveau rôle
        </Button>
      </div>
    </div>

    <!-- Filters -->
    <Card>
      <CardContent class="p-4">
        <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div class="space-y-2">
            <Label>Recherche</Label>
            <Input
              v-model="searchQuery"
              placeholder="Nom, description..."
              @input="handleSearch"
            />
          </div>
          <div class="space-y-2">
            <Label>Type</Label>
            <Select v-model="selectedType" @update:model-value="handleFilter">
              <SelectTrigger>
                <SelectValue placeholder="Tous les types" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">Tous</SelectItem>
                <SelectItem value="system">Système</SelectItem>
                <SelectItem value="custom">Personnalisé</SelectItem>
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
    <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
      <Card class="relative overflow-hidden border-l-4 border-l-primary">
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardDescription>Total Rôles</CardDescription>
          <div class="h-10 w-10 rounded-lg bg-primary/10 flex items-center justify-center">
            <Shield class="h-5 w-5 text-primary" />
          </div>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold">{{ total }}</div>
        </CardContent>
      </Card>
      
      <Card class="relative overflow-hidden border-l-4 border-l-blue-500">
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardDescription>Rôles Système</CardDescription>
          <div class="h-10 w-10 rounded-lg bg-blue-500/10 flex items-center justify-center">
            <Shield class="h-5 w-5 text-blue-500" />
          </div>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold text-blue-500">{{ systemRolesCount }}</div>
        </CardContent>
      </Card>
      
      <Card class="relative overflow-hidden border-l-4 border-l-green-500">
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardDescription>Rôles Personnalisés</CardDescription>
          <div class="h-10 w-10 rounded-lg bg-green-500/10 flex items-center justify-center">
            <Shield class="h-5 w-5 text-green-500" />
          </div>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold text-green-500">{{ customRolesCount }}</div>
        </CardContent>
      </Card>
    </div>

    <!-- Roles Table -->
    <Card>
      <CardContent class="p-0">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Nom</TableHead>
              <TableHead>Description</TableHead>
              <TableHead>Type</TableHead>
              <TableHead>Permissions</TableHead>
              <TableHead>Utilisateurs</TableHead>
              <TableHead>Créé le</TableHead>
              <TableHead class="text-right">Actions</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            <TableRow v-if="loading">
              <TableCell colspan="7" class="text-center py-8">
                <div class="flex items-center justify-center">
                  <Loader2 class="h-6 w-6 animate-spin mr-2" />
                  Chargement...
                </div>
              </TableCell>
            </TableRow>
            <TableRow v-else-if="roles.length === 0">
              <TableCell colspan="7" class="text-center py-8 text-muted-foreground">
                Aucun rôle trouvé
              </TableCell>
            </TableRow>
            <TableRow v-else v-for="role in roles" :key="role.id">
              <TableCell>
                <div class="font-medium">{{ role.name }}</div>
              </TableCell>
              <TableCell>
                <div class="text-sm text-muted-foreground max-w-md truncate">
                  {{ role.description || 'Aucune description' }}
                </div>
              </TableCell>
              <TableCell>
                <Badge :variant="role.isSystem ? 'default' : 'secondary'">
                  {{ role.isSystem ? 'Système' : 'Personnalisé' }}
                </Badge>
              </TableCell>
              <TableCell>
                <div class="flex flex-wrap gap-1">
                  <Badge
                    v-for="permission in role.permissions.slice(0, 3)"
                    :key="permission.id"
                    variant="outline"
                    class="text-xs"
                  >
                    {{ permission.name }}
                  </Badge>
                  <Badge
                    v-if="role.permissions.length > 3"
                    variant="outline"
                    class="text-xs"
                  >
                    +{{ role.permissions.length - 3 }}
                  </Badge>
                </div>
              </TableCell>
              <TableCell>
                <span class="text-sm">{{ role.userCount || 0 }}</span>
              </TableCell>
              <TableCell>
                {{ formatDate(role.createdAt) }}
              </TableCell>
              <TableCell class="text-right">
                <DropdownMenu>
                  <DropdownMenuTrigger as-child>
                    <Button variant="ghost" size="icon">
                      <MoreVertical class="h-4 w-4" />
                    </Button>
                  </DropdownMenuTrigger>
                  <DropdownMenuContent align="end">
                    <DropdownMenuItem @click="viewRole(role.id)">
                      <Eye class="mr-2 h-4 w-4" />
                      Voir
                    </DropdownMenuItem>
                    <DropdownMenuItem
                      @click="editRole(role.id)"
                      v-if="!role.isSystem"
                    >
                      <Edit class="mr-2 h-4 w-4" />
                      Modifier
                    </DropdownMenuItem>
                    <DropdownMenuSeparator />
                    <DropdownMenuItem
                      @click="deleteRole(role.id)"
                      v-if="!role.isSystem"
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
    <RoleDialog
      v-model:open="dialogOpen"
      :role="selectedRole"
      @saved="handleRoleSaved"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useDebounceFn } from '@vueuse/core'
import { roleService, type Role, type RoleSearchParams } from '@viridial/shared'
import { Card, CardContent, CardHeader, CardDescription } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Table, TableHeader, TableRow, TableHead, TableBody, TableCell } from '@/components/ui/table'
import { Badge } from '@/components/ui/badge'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue
} from '@/components/ui/select'
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
  Trash2,
  ChevronLeft,
  ChevronRight,
  Shield,
  UserPlus
} from 'lucide-vue-next'
import { useToast } from '@/components/ui/toast'
import RoleDialog from '@/components/roles/RoleDialog.vue'

const router = useRouter()
const { toast } = useToast()

const roles = ref<Role[]>([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const searchQuery = ref('')
const selectedType = ref('all')
const dialogOpen = ref(false)
const selectedRole = ref<Role | null>(null)

const systemRolesCount = computed(() => {
  return roles.value.filter(r => r.isSystem).length
})

const customRolesCount = computed(() => {
  return roles.value.filter(r => !r.isSystem).length
})

const loadRoles = async () => {
  loading.value = true
  try {
    const params: RoleSearchParams = {
      page: currentPage.value - 1,
      size: pageSize.value,
      search: searchQuery.value || undefined,
      isSystem: selectedType.value === 'system' ? true : selectedType.value === 'custom' ? false : undefined
    }
    const page = await roleService.getAll(params)
    roles.value = page.content
    total.value = page.totalElements
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Erreur lors du chargement des rôles',
      variant: 'destructive'
    })
  } finally {
    loading.value = false
  }
}

const handleSearch = useDebounceFn(() => {
  currentPage.value = 1
  loadRoles()
}, 300)

const handleFilter = () => {
  currentPage.value = 1
  loadRoles()
}

const resetFilters = () => {
  searchQuery.value = ''
  selectedType.value = 'all'
  currentPage.value = 1
  loadRoles()
}

const setPage = (page: number) => {
  currentPage.value = page
  loadRoles()
}

const openCreateDialog = () => {
  selectedRole.value = null
  dialogOpen.value = true
}

const editRole = (id: number) => {
  router.push(`/roles/${id}/edit`)
}

const viewRole = (id: number) => {
  router.push(`/roles/${id}/edit`)
}

const deleteRole = async (id: number) => {
  if (!confirm('Êtes-vous sûr de vouloir supprimer ce rôle ?')) {
    return
  }
  try {
    await roleService.delete(id)
    toast({
      title: 'Succès',
      description: 'Rôle supprimé avec succès'
    })
    loadRoles()
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Erreur lors de la suppression',
      variant: 'destructive'
    })
  }
}

const handleRoleSaved = () => {
  dialogOpen.value = false
  loadRoles()
}

const formatDate = (date: string) => {
  return new Date(date).toLocaleDateString('fr-FR', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  })
}

onMounted(() => {
  loadRoles()
})
</script>

