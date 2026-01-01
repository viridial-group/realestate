<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-3xl font-bold">{{ t('users.title') }}</h1>
        <p class="text-muted-foreground mt-1">{{ t('users.description') }}</p>
      </div>
      <Button @click="openCreateDialog" size="lg">
        <Plus class="mr-2 h-4 w-4" />
        {{ t('users.newUser') }}
      </Button>
    </div>

    <!-- Filters -->
    <Card>
      <CardContent class="p-4">
        <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
          <div class="space-y-2">
            <Label>Recherche</Label>
            <Input
              v-model="searchQuery"
              placeholder="Nom, email..."
              @input="handleSearch"
            />
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
            <Label>Rôle</Label>
            <Select v-model="selectedRole" @update:model-value="handleFilter">
              <SelectTrigger>
                <SelectValue placeholder="Tous les rôles" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="">Tous</SelectItem>
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
        <Table>
          <TableHeader>
            <TableRow>
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
              <TableCell colspan="7" class="text-center py-8">
                <div class="flex items-center justify-center">
                  <Loader2 class="h-6 w-6 animate-spin mr-2" />
                  Chargement...
                </div>
              </TableCell>
            </TableRow>
            <TableRow v-else-if="users.length === 0">
              <TableCell colspan="7" class="text-center py-8 text-muted-foreground">
                Aucun utilisateur trouvé
              </TableCell>
            </TableRow>
            <TableRow v-else v-for="user in users" :key="user.id">
              <TableCell>
                <div class="flex items-center space-x-3">
                  <Avatar>
                    <AvatarImage :src="user.avatar" />
                    <AvatarFallback>{{ user.name.charAt(0).toUpperCase() }}</AvatarFallback>
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
import { useI18n } from 'vue-i18n'
import { useUser, UserStatus, UserRole } from '@viridial/shared'

const { t } = useI18n()
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
  ChevronRight
} from 'lucide-vue-next'
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
const selectedStatus = ref('')
const selectedRole = ref('')
const dialogOpen = ref(false)
const selectedUser = ref(null)

onMounted(() => {
  loadUsers()
})

const handleSearch = () => {
  loadUsers({
    search: searchQuery.value || undefined,
    status: selectedStatus.value as UserStatus || undefined,
    role: selectedRole.value as UserRole || undefined
  })
}

const handleFilter = () => {
  handleSearch()
}

const resetFilters = () => {
  searchQuery.value = ''
  selectedStatus.value = ''
  selectedRole.value = ''
  loadUsers()
}

const openCreateDialog = () => {
  selectedUser.value = null
  dialogOpen.value = true
}

const editUser = (id: number) => {
  selectedUser.value = users.value.find(u => u.id === id) || null
  dialogOpen.value = true
}

const viewUser = (id: number) => {
  // TODO: Navigate to user detail page
  console.log('View user:', id)
}

const handleUserSaved = () => {
  dialogOpen.value = false
  loadUsers()
}

const getStatusVariant = (status: string) => {
  const variants: Record<string, string> = {
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
</script>

