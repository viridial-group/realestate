<template>
  <div class="space-y-6" v-if="organization">
    <!-- Header -->
    <div class="flex items-center justify-between">
      <div class="flex items-center space-x-4">
        <Button variant="ghost" size="icon" @click="$router.push('/organizations')">
          <ArrowLeft class="h-4 w-4" />
        </Button>
        <div>
          <h1 class="text-3xl font-bold">{{ organization.name }}</h1>
          <p class="text-muted-foreground mt-1">{{ organization.domain || 'Sans domaine' }}</p>
        </div>
      </div>
      <div class="flex gap-2">
        <Button variant="outline" @click="editOrganization">
          <Edit class="mr-2 h-4 w-4" />
          Modifier
        </Button>
        <DropdownMenu>
          <DropdownMenuTrigger as-child>
            <Button variant="outline">
              <MoreVertical class="mr-2 h-4 w-4" />
              Actions
            </Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end">
            <DropdownMenuItem
              @click="handleActivate"
              v-if="!organization.active"
            >
              <CheckCircle class="mr-2 h-4 w-4" />
              Activer
            </DropdownMenuItem>
            <DropdownMenuItem
              @click="handleDeactivate"
              v-if="organization.active"
            >
              <XCircle class="mr-2 h-4 w-4" />
              Désactiver
            </DropdownMenuItem>
            <DropdownMenuSeparator />
            <DropdownMenuItem
              @click="handleDelete"
              class="text-destructive"
            >
              <Trash2 class="mr-2 h-4 w-4" />
              Supprimer
            </DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
      </div>
    </div>

    <!-- Organization Info Cards -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
      <Card>
        <CardHeader class="pb-2">
          <CardDescription>Statut</CardDescription>
        </CardHeader>
        <CardContent>
          <Badge :variant="organization.active ? 'default' : 'secondary'" class="text-sm">
            {{ organization.active ? 'Actif' : 'Inactif' }}
          </Badge>
        </CardContent>
      </Card>
      <Card>
        <CardHeader class="pb-2">
          <CardDescription>Utilisateurs</CardDescription>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold">{{ organizationUsers.length }}</div>
        </CardContent>
      </Card>
      <Card>
        <CardHeader class="pb-2">
          <CardDescription>Teams</CardDescription>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold">{{ teams.length }}</div>
        </CardContent>
      </Card>
      <Card>
        <CardHeader class="pb-2">
          <CardDescription>Créée le</CardDescription>
        </CardHeader>
        <CardContent>
          <div class="text-sm font-medium">{{ formatDate(organization.createdAt) }}</div>
        </CardContent>
      </Card>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <!-- Left Column: Organization Details -->
      <div class="lg:col-span-2 space-y-6">
        <!-- Organization Information -->
        <Card>
          <CardHeader>
            <CardTitle>Informations de l'organisation</CardTitle>
          </CardHeader>
          <CardContent class="space-y-4">
            <div class="flex items-center space-x-4">
              <div class="flex-shrink-0 h-16 w-16 rounded-lg bg-primary/10 flex items-center justify-center">
                <Building2 class="h-8 w-8 text-primary" />
              </div>
              <div class="flex-1">
                <div class="text-lg font-semibold">{{ organization.name }}</div>
                <div class="text-sm text-muted-foreground">{{ organization.domain || 'Sans domaine' }}</div>
              </div>
            </div>
            <div class="grid grid-cols-2 gap-4 pt-4 border-t">
              <div>
                <Label class="text-muted-foreground">Description</Label>
                <div class="font-medium">{{ organization.description || 'Aucune description' }}</div>
              </div>
              <div>
                <Label class="text-muted-foreground">Domaine</Label>
                <div class="font-medium">{{ organization.domain || 'N/A' }}</div>
              </div>
              <div>
                <Label class="text-muted-foreground">Organisation parente</Label>
                <div class="font-medium">
                  <span v-if="parentOrganization">{{ parentOrganization.name }}</span>
                  <span v-else class="text-muted-foreground">Organisation racine</span>
                </div>
              </div>
              <div>
                <Label class="text-muted-foreground">Statut</Label>
                <div class="font-medium">
                  <Badge :variant="organization.active ? 'default' : 'secondary'">
                    {{ organization.active ? 'Actif' : 'Inactif' }}
                  </Badge>
                </div>
              </div>
            </div>
          </CardContent>
        </Card>

        <!-- Hierarchy -->
        <Card v-if="childrenOrganizations.length > 0">
          <CardHeader class="flex items-center justify-between">
            <CardTitle>Organisations filiales</CardTitle>
            <Badge variant="secondary">{{ childrenOrganizations.length }}</Badge>
          </CardHeader>
          <CardContent>
            <div class="space-y-2">
              <div
                v-for="child in childrenOrganizations"
                :key="child.id"
                class="flex items-center justify-between p-3 border rounded-lg hover:bg-muted/50 cursor-pointer"
                @click="viewOrganization(child.id)"
              >
                <div>
                  <div class="font-medium">{{ child.name }}</div>
                  <div class="text-sm text-muted-foreground">{{ child.domain || 'Sans domaine' }}</div>
                </div>
                <Badge :variant="child.active ? 'default' : 'secondary'">
                  {{ child.active ? 'Actif' : 'Inactif' }}
                </Badge>
              </div>
            </div>
          </CardContent>
        </Card>

        <!-- Users -->
        <Card>
          <CardHeader class="flex items-center justify-between">
            <CardTitle>Utilisateurs</CardTitle>
            <Button
              variant="outline"
              size="sm"
              @click="showAddUserDialog = true"
            >
              <Plus class="mr-2 h-4 w-4" />
              Ajouter un utilisateur
            </Button>
          </CardHeader>
          <CardContent>
            <div v-if="loadingUsers" class="flex items-center justify-center py-4">
              <Loader2 class="h-5 w-5 animate-spin" />
            </div>
            <div v-else-if="organizationUsers.length === 0" class="text-muted-foreground text-sm text-center py-4">
              Aucun utilisateur assigné
            </div>
            <div v-else class="space-y-2">
              <div
                v-for="orgUser in organizationUsers"
                :key="orgUser.id"
                class="flex items-center justify-between p-3 border rounded-lg"
              >
                <div class="flex items-center space-x-3">
                  <Avatar class="h-10 w-10">
                    <AvatarFallback>
                      {{ (orgUser.user?.name || orgUser.user?.email || 'U').charAt(0).toUpperCase() }}
                    </AvatarFallback>
                  </Avatar>
                  <div>
                    <div class="font-medium">{{ orgUser.user?.name || 'N/A' }}</div>
                    <div class="text-sm text-muted-foreground">{{ orgUser.user?.email || 'N/A' }}</div>
                  </div>
                </div>
                <div class="flex items-center gap-2">
                  <Badge v-if="orgUser.isPrimary" variant="default">Principale</Badge>
                  <Badge v-if="orgUser.teamId" variant="outline">Team</Badge>
                  <Button
                    variant="ghost"
                    size="sm"
                    @click="removeUser(orgUser.userId)"
                  >
                    <X class="h-4 w-4" />
                  </Button>
                </div>
              </div>
            </div>
          </CardContent>
        </Card>

        <!-- Teams -->
        <Card>
          <CardHeader class="flex items-center justify-between">
            <CardTitle>Teams</CardTitle>
            <Button
              variant="outline"
              size="sm"
              @click="showAddTeamDialog = true"
            >
              <Plus class="mr-2 h-4 w-4" />
              Créer une team
            </Button>
          </CardHeader>
          <CardContent>
            <div v-if="loadingTeams" class="flex items-center justify-center py-4">
              <Loader2 class="h-5 w-5 animate-spin" />
            </div>
            <div v-else-if="teams.length === 0" class="text-muted-foreground text-sm text-center py-4">
              Aucune team créée
            </div>
            <div v-else class="space-y-2">
              <div
                v-for="team in teams"
                :key="team.id"
                class="flex items-center justify-between p-3 border rounded-lg hover:bg-muted/50"
              >
                <div>
                  <div class="font-medium">{{ team.name }}</div>
                  <div class="text-sm text-muted-foreground">{{ team.description || 'Aucune description' }}</div>
                </div>
                <div class="flex items-center gap-2">
                  <Badge :variant="team.active ? 'default' : 'secondary'">
                    {{ team.active ? 'Actif' : 'Inactif' }}
                  </Badge>
                  <Button
                    variant="ghost"
                    size="sm"
                    @click="editTeam(team)"
                  >
                    <Edit class="h-4 w-4" />
                  </Button>
                  <Button
                    variant="ghost"
                    size="sm"
                    @click="deleteTeam(team.id)"
                  >
                    <Trash2 class="h-4 w-4" />
                  </Button>
                </div>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>

      <!-- Right Column: Quick Stats & Actions -->
      <div class="space-y-6">
        <!-- Quick Stats -->
        <Card>
          <CardHeader>
            <CardTitle>Statistiques</CardTitle>
          </CardHeader>
          <CardContent class="space-y-4">
            <div>
              <Label class="text-muted-foreground">Total utilisateurs</Label>
              <div class="text-2xl font-bold">{{ organizationUsers.length }}</div>
            </div>
            <div>
              <Label class="text-muted-foreground">Teams actives</Label>
              <div class="text-2xl font-bold">{{ teams.filter((t: Team) => t.active).length }}</div>
            </div>
            <div>
              <Label class="text-muted-foreground">Organisations filiales</Label>
              <div class="text-2xl font-bold">{{ childrenOrganizations.length }}</div>
            </div>
          </CardContent>
        </Card>

        <!-- Quick Actions -->
        <Card>
          <CardHeader>
            <CardTitle>Actions rapides</CardTitle>
          </CardHeader>
          <CardContent class="space-y-2">
            <Button
              variant="outline"
              class="w-full justify-start"
              @click="showAddUserDialog = true"
            >
              <UserPlus class="mr-2 h-4 w-4" />
              Ajouter un utilisateur
            </Button>
            <Button
              variant="outline"
              class="w-full justify-start"
              @click="showAddTeamDialog = true"
            >
              <Users class="mr-2 h-4 w-4" />
              Créer une team
            </Button>
            <Button
              variant="outline"
              class="w-full justify-start"
              @click="viewParent"
              v-if="parentOrganization"
            >
              <Building2 class="mr-2 h-4 w-4" />
              Voir l'organisation parente
            </Button>
          </CardContent>
        </Card>
      </div>
    </div>

    <!-- Add User Dialog -->
    <Dialog v-model:open="showAddUserDialog">
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Ajouter un utilisateur</DialogTitle>
          <DialogDescription>
            Sélectionnez un utilisateur à assigner à cette organisation
          </DialogDescription>
        </DialogHeader>
        <div class="space-y-4 py-4">
          <Select v-model="selectedUserId">
            <SelectTrigger>
              <SelectValue placeholder="Sélectionner un utilisateur" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem
                v-for="user in availableUsers"
                :key="user.id"
                :value="user.id.toString()"
              >
                {{ user.name }} ({{ user.email }})
              </SelectItem>
            </SelectContent>
          </Select>
          <div class="flex items-center space-x-2">
            <Checkbox
              v-model="isPrimaryOnAssign"
              id="primary-org-assign"
            />
            <Label for="primary-org-assign" class="cursor-pointer">
              Définir comme organisation principale
            </Label>
          </div>
        </div>
        <DialogFooter>
          <Button variant="outline" @click="showAddUserDialog = false">Annuler</Button>
          <Button @click="addUser" :disabled="!selectedUserId">Ajouter</Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>

    <!-- Add/Edit Team Dialog -->
    <Dialog v-model:open="showTeamDialog">
      <DialogContent>
        <DialogHeader>
          <DialogTitle>{{ editingTeam ? 'Modifier la team' : 'Créer une team' }}</DialogTitle>
        </DialogHeader>
        <div class="space-y-4 py-4">
          <div class="space-y-2">
            <Label for="teamName">Nom *</Label>
            <Input
              id="teamName"
              v-model="teamForm.name"
              placeholder="Nom de la team"
              required
            />
          </div>
          <div class="space-y-2">
            <Label for="teamDescription">Description</Label>
            <Input
              id="teamDescription"
              v-model="teamForm.description"
              placeholder="Description de la team"
            />
          </div>
          <div class="flex items-center space-x-2">
            <Checkbox
              :model-value="teamForm.active"
              @update:model-value="teamForm.active = Boolean($event)"
              id="teamActive"
            />
            <Label for="teamActive" class="cursor-pointer">
              Team active
            </Label>
          </div>
        </div>
        <DialogFooter>
          <Button variant="outline" @click="showTeamDialog = false">Annuler</Button>
          <Button @click="saveTeam" :disabled="!teamForm.name">Enregistrer</Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  </div>
  <div v-else-if="loading" class="flex items-center justify-center py-12">
    <Loader2 class="h-8 w-8 animate-spin" />
  </div>
  <div v-else class="flex items-center justify-center py-12">
    <div class="text-center">
      <p class="text-muted-foreground">Organisation non trouvée</p>
      <Button variant="outline" @click="$router.push('/organizations')" class="mt-4">
        Retour à la liste
      </Button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useToast } from '@/components/ui/toast'
import { organizationService, userService, type Organization, type Team, type OrganizationUser, type User } from '@viridial/shared'
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { Avatar, AvatarFallback } from '@/components/ui/avatar'
import { Label } from '@/components/ui/label'
import { Input } from '@/components/ui/input'
import { Checkbox } from '@/components/ui/checkbox'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle
} from '@/components/ui/dialog'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger
} from '@/components/ui/dropdown-menu'
import {
  ArrowLeft,
  Edit,
  MoreVertical,
  CheckCircle,
  XCircle,
  Trash2,
  Plus,
  X,
  Loader2,
  Building2,
  UserPlus,
  Users
} from 'lucide-vue-next'

const route = useRoute()
const router = useRouter()
const { toast } = useToast()

const organizationId = computed(() => Number(route.params.id))
const organization = ref<Organization | null>(null)
const parentOrganization = ref<Organization | null>(null)
const childrenOrganizations = ref<Organization[]>([])
const organizationUsers = ref<OrganizationUser[]>([])
const teams = ref<Team[]>([])
const availableUsers = ref<User[]>([])

const loading = ref(false)
const loadingUsers = ref(false)
const loadingTeams = ref(false)

// Dialogs
const showAddUserDialog = ref(false)
const showAddTeamDialog = ref(false)
const showTeamDialog = ref(false)
const selectedUserId = ref<string>('')
const isPrimaryOnAssign = ref(false)
const editingTeam = ref<Team | null>(null)
const teamForm = ref({
  name: '',
  description: '',
  active: true
})

onMounted(async () => {
  await loadOrganizationData()
  await loadChildren()
  await loadUsers()
  await loadTeams()
  await loadAvailableUsers()
})

const loadOrganizationData = async () => {
  loading.value = true
  try {
    organization.value = await organizationService.getById(organizationId.value)
    if (organization.value?.parentId) {
      try {
        parentOrganization.value = await organizationService.getById(organization.value.parentId)
      } catch (error) {
        console.error('Error loading parent organization:', error)
      }
    }
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Impossible de charger les données de l\'organisation',
      variant: 'destructive'
    })
  } finally {
    loading.value = false
  }
}

const loadChildren = async () => {
  try {
    childrenOrganizations.value = await organizationService.getChildren(organizationId.value)
  } catch (error: any) {
    console.error('Error loading children organizations:', error)
  }
}

const loadUsers = async () => {
  loadingUsers.value = true
  try {
    const orgUsers = await organizationService.getUsersByOrganization(organizationId.value)
    // Enrichir avec les données utilisateur si nécessaire
    organizationUsers.value = orgUsers
  } catch (error: any) {
    console.error('Error loading organization users:', error)
    toast({
      title: 'Erreur',
      description: 'Impossible de charger les utilisateurs',
      variant: 'destructive'
    })
  } finally {
    loadingUsers.value = false
  }
}

const loadTeams = async () => {
  loadingTeams.value = true
  try {
    teams.value = await organizationService.getTeamsByOrganization(organizationId.value)
  } catch (error: any) {
    console.error('Error loading teams:', error)
    toast({
      title: 'Erreur',
      description: 'Impossible de charger les teams',
      variant: 'destructive'
    })
  } finally {
    loadingTeams.value = false
  }
}

const loadAvailableUsers = async () => {
  try {
    const result = await userService.getAll()
    // Filtrer les utilisateurs déjà assignés
    const assignedUserIds = organizationUsers.value.map((ou: OrganizationUser) => ou.userId)
    availableUsers.value = result.users.filter(u => !assignedUserIds.includes(u.id))
  } catch (error: any) {
    console.error('Error loading available users:', error)
  }
}

const editOrganization = () => {
  router.push({ name: 'organizations', query: { edit: organizationId.value } })
}

const handleActivate = async () => {
  try {
    await organizationService.update(organizationId.value, { active: true })
    await loadOrganizationData()
    toast({
      title: 'Organisation activée',
      description: 'L\'organisation a été activée avec succès'
    })
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Impossible d\'activer l\'organisation',
      variant: 'destructive'
    })
  }
}

const handleDeactivate = async () => {
  try {
    await organizationService.update(organizationId.value, { active: false })
    await loadOrganizationData()
    toast({
      title: 'Organisation désactivée',
      description: 'L\'organisation a été désactivée avec succès'
    })
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Impossible de désactiver l\'organisation',
      variant: 'destructive'
    })
  }
}

const handleDelete = async () => {
  if (!confirm('Êtes-vous sûr de vouloir supprimer cette organisation ?')) return
  try {
    await organizationService.delete(organizationId.value)
    toast({
      title: 'Organisation supprimée',
      description: 'L\'organisation a été supprimée avec succès'
    })
    router.push('/organizations')
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Impossible de supprimer l\'organisation',
      variant: 'destructive'
    })
  }
}

const addUser = async () => {
  if (!selectedUserId.value) return
  try {
    await organizationService.assignUserToOrganization(
      organizationId.value,
      Number(selectedUserId.value),
      isPrimaryOnAssign.value
    )
    await loadUsers()
    await loadAvailableUsers()
    showAddUserDialog.value = false
    selectedUserId.value = ''
    isPrimaryOnAssign.value = false
    toast({
      title: 'Utilisateur ajouté',
      description: 'L\'utilisateur a été ajouté à l\'organisation avec succès'
    })
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Impossible d\'ajouter l\'utilisateur',
      variant: 'destructive'
    })
  }
}

const removeUser = async (userId: number) => {
  if (!confirm('Êtes-vous sûr de vouloir retirer cet utilisateur de cette organisation ?')) return
  try {
    await organizationService.removeUserFromOrganization(organizationId.value, userId)
    await loadUsers()
    await loadAvailableUsers()
    toast({
      title: 'Utilisateur retiré',
      description: 'L\'utilisateur a été retiré de l\'organisation avec succès'
    })
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Impossible de retirer l\'utilisateur',
      variant: 'destructive'
    })
  }
}

const editTeam = (team: Team) => {
  editingTeam.value = team
  teamForm.value = {
    name: team.name,
    description: team.description || '',
    active: team.active
  }
  showTeamDialog.value = true
}

const deleteTeam = async (teamId: number) => {
  if (!confirm('Êtes-vous sûr de vouloir supprimer cette team ?')) return
  try {
    await organizationService.deleteTeam(organizationId.value, teamId)
    await loadTeams()
    toast({
      title: 'Team supprimée',
      description: 'La team a été supprimée avec succès'
    })
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Impossible de supprimer la team',
      variant: 'destructive'
    })
  }
}

const saveTeam = async () => {
  try {
    if (editingTeam.value) {
      await organizationService.updateTeam(organizationId.value, editingTeam.value.id, teamForm.value)
      toast({
        title: 'Team modifiée',
        description: 'La team a été modifiée avec succès'
      })
    } else {
      await organizationService.createTeam(organizationId.value, teamForm.value)
      toast({
        title: 'Team créée',
        description: 'La team a été créée avec succès'
      })
    }
    await loadTeams()
    showTeamDialog.value = false
    editingTeam.value = null
    teamForm.value = { name: '', description: '', active: true }
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Impossible de sauvegarder la team',
      variant: 'destructive'
    })
  }
}

const viewOrganization = (id: number) => {
  router.push(`/organizations/${id}`)
}

const viewParent = () => {
  if (parentOrganization.value) {
    router.push(`/organizations/${parentOrganization.value.id}`)
  }
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

