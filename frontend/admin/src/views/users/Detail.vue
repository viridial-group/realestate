<template>
  <div class="space-y-6" v-if="user">
    <!-- Header -->
    <div class="flex items-center justify-between">
      <div class="flex items-center space-x-4">
        <Button variant="ghost" size="icon" @click="$router.push('/users')">
          <ArrowLeft class="h-4 w-4" />
        </Button>
        <div>
          <h1 class="text-2.5xl font-bold">{{ user.name }}</h1>
          <p class="text-muted-foreground mt-1">{{ user.email }}</p>
        </div>
      </div>
      <div class="flex gap-2">
        <Button variant="outline" @click="editUser" v-if="canEditUser(user.id)">
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
              v-if="user.status !== 'ACTIVE'"
            >
              <CheckCircle class="mr-2 h-4 w-4" />
              Activer
            </DropdownMenuItem>
            <DropdownMenuItem
              @click="handleDeactivate"
              v-if="user.status === 'ACTIVE'"
            >
              <XCircle class="mr-2 h-4 w-4" />
              Désactiver
            </DropdownMenuItem>
            <DropdownMenuItem
              @click="handleSuspend"
              v-if="user.status !== 'SUSPENDED'"
            >
              <Ban class="mr-2 h-4 w-4" />
              Suspendre
            </DropdownMenuItem>
            <DropdownMenuSeparator />
            <DropdownMenuItem
              @click="handleDelete"
              v-if="canDeleteUser(user.id)"
              class="text-destructive"
            >
              <Trash2 class="mr-2 h-4 w-4" />
              Supprimer
            </DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
      </div>
    </div>

    <!-- User Info Cards -->
    <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
      <Card>
        <CardHeader class="pb-2">
          <CardDescription>Statut</CardDescription>
        </CardHeader>
        <CardContent>
          <Badge :variant="getStatusVariant(user.status)" class="text-sm">
            {{ getStatusLabel(user.status) }}
          </Badge>
        </CardContent>
      </Card>
      <Card>
        <CardHeader class="pb-2">
          <CardDescription>Dernière connexion</CardDescription>
        </CardHeader>
        <CardContent>
          <div class="text-sm font-medium">
            {{ user.lastLoginAt ? formatDate(user.lastLoginAt) : 'Jamais' }}
          </div>
        </CardContent>
      </Card>
      <Card>
        <CardHeader class="pb-2">
          <CardDescription>Compte créé</CardDescription>
        </CardHeader>
        <CardContent>
          <div class="text-sm font-medium">{{ formatDate(user.createdAt) }}</div>
        </CardContent>
      </Card>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <!-- Left Column: User Details -->
      <div class="lg:col-span-2 space-y-6">
        <!-- Personal Information -->
        <Card>
          <CardHeader>
            <CardTitle>Informations personnelles</CardTitle>
          </CardHeader>
          <CardContent class="space-y-4">
            <div class="flex items-center space-x-4">
              <Avatar class="h-20 w-20">
                <AvatarImage :src="user.avatar" v-if="user.avatar" />
                <AvatarFallback class="text-2xl">
                  {{ (user.name || user.email || 'U').charAt(0).toUpperCase() }}
                </AvatarFallback>
              </Avatar>
              <div class="flex-1">
                <div class="text-lg font-semibold">{{ user.name }}</div>
                <div class="text-sm text-muted-foreground">{{ user.email }}</div>
              </div>
            </div>
            <div class="grid grid-cols-2 gap-4 pt-4 border-t">
              <div>
                <Label class="text-muted-foreground">Prénom</Label>
                <div class="font-medium">{{ user.firstName || 'N/A' }}</div>
              </div>
              <div>
                <Label class="text-muted-foreground">Nom</Label>
                <div class="font-medium">{{ user.lastName || 'N/A' }}</div>
              </div>
              <div>
                <Label class="text-muted-foreground">Téléphone</Label>
                <div class="font-medium">{{ user.phone || 'N/A' }}</div>
              </div>
              <div>
                <Label class="text-muted-foreground">Email vérifié</Label>
                <div class="font-medium">
                  <Badge :variant="user.emailVerified ? 'default' : 'secondary'">
                    {{ user.emailVerified ? 'Oui' : 'Non' }}
                  </Badge>
                </div>
              </div>
            </div>
          </CardContent>
        </Card>

        <!-- Roles & Permissions -->
        <Card>
          <CardHeader class="flex items-center justify-between">
            <CardTitle>Rôles et permissions</CardTitle>
            <Button
              variant="outline"
              size="sm"
              @click="showRoleDialog = true"
              v-if="canManageRoles(user.id)"
            >
              <Edit class="mr-2 h-4 w-4" />
              Modifier
            </Button>
          </CardHeader>
          <CardContent>
            <div class="flex flex-wrap gap-2">
              <Badge
                v-for="role in user.roles"
                :key="role"
                variant="secondary"
                class="text-sm"
              >
                {{ getRoleLabel(role) }}
              </Badge>
              <span v-if="user.roles.length === 0" class="text-muted-foreground text-sm">
                Aucun rôle assigné
              </span>
            </div>
          </CardContent>
        </Card>

        <!-- Organizations -->
        <Card>
          <CardHeader class="flex items-center justify-between">
            <CardTitle>Organisations</CardTitle>
            <Button
              variant="outline"
              size="sm"
              @click="showOrganizationDialog = true"
              v-if="canEditUser(user.id)"
            >
              <Plus class="mr-2 h-4 w-4" />
              Assigner
            </Button>
          </CardHeader>
          <CardContent>
            <div v-if="loadingOrganizations" class="flex items-center justify-center py-4">
              <Loader2 class="h-5 w-5 animate-spin" />
            </div>
            <div v-else-if="userOrganizations.length === 0" class="text-muted-foreground text-sm">
              Aucune organisation assignée
            </div>
            <div v-else class="space-y-2">
              <div
                v-for="org in userOrganizations"
                :key="org.id"
                class="flex items-center justify-between p-3 border rounded-lg"
              >
                <div>
                  <div class="font-medium">{{ org.name }}</div>
                  <div class="text-sm text-muted-foreground">{{ org.domain || 'Sans domaine' }}</div>
                </div>
                <div class="flex items-center gap-2">
                  <Badge v-if="org.isPrimary" variant="default">Principale</Badge>
                  <Button
                    v-if="!org.isPrimary && canEditUser(user.id)"
                    variant="outline"
                    size="sm"
                    @click="setAsPrimary(org.id)"
                  >
                    Définir principale
                  </Button>
                  <Button
                    variant="ghost"
                    size="sm"
                    @click="removeFromOrganization(org.id)"
                    v-if="canEditUser(user.id)"
                  >
                    <X class="h-4 w-4" />
                  </Button>
                </div>
              </div>
            </div>
          </CardContent>
        </Card>

        <!-- Activity Log -->
        <Card>
          <CardHeader>
            <CardTitle>Historique d'activité</CardTitle>
          </CardHeader>
          <CardContent>
            <div v-if="loadingActivity" class="flex items-center justify-center py-4">
              <Loader2 class="h-5 w-5 animate-spin" />
            </div>
            <div v-else-if="activities.length === 0" class="text-muted-foreground text-sm text-center py-4">
              Aucune activité enregistrée
            </div>
            <div v-else class="space-y-3">
              <div
                v-for="activity in activities"
                :key="activity.id"
                class="flex items-start space-x-3 p-3 border rounded-lg hover:bg-muted/50"
              >
                <div class="flex-shrink-0 mt-1">
                  <div class="h-2 w-2 rounded-full bg-primary"></div>
                </div>
                <div class="flex-1 min-w-0">
                  <div class="text-sm font-medium">{{ activity.description }}</div>
                  <div class="text-xs text-muted-foreground mt-1">
                    {{ formatDate(activity.createdAt) }}
                    <span v-if="activity.ipAddress" class="ml-2">
                      • IP: {{ activity.ipAddress }}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>

      <!-- Right Column: Quick Actions & Stats -->
      <div class="space-y-6">
        <!-- Quick Stats -->
        <Card>
          <CardHeader>
            <CardTitle>Statistiques</CardTitle>
          </CardHeader>
          <CardContent class="space-y-4">
            <div>
              <Label class="text-muted-foreground">Total d'activités</Label>
              <div class="text-2xl font-bold">{{ activities.length }}</div>
            </div>
            <div>
              <Label class="text-muted-foreground">Organisations</Label>
              <div class="text-2xl font-bold">{{ userOrganizations.length }}</div>
            </div>
            <div>
              <Label class="text-muted-foreground">Rôles</Label>
              <div class="text-2xl font-bold">{{ user.roles.length }}</div>
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
              @click="showResetPasswordDialog = true"
              v-if="canEditUser(user.id)"
            >
              <Key class="mr-2 h-4 w-4" />
              Réinitialiser le mot de passe
            </Button>
            <Button
              variant="outline"
              class="w-full justify-start"
              @click="resendVerificationEmail"
              v-if="!user.emailVerified"
            >
              <Mail class="mr-2 h-4 w-4" />
              Renvoyer l'email de vérification
            </Button>
          </CardContent>
        </Card>
      </div>
    </div>

    <!-- Role Management Dialog -->
    <Dialog v-model:open="showRoleDialog">
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Gérer les rôles</DialogTitle>
          <DialogDescription>
            Sélectionnez les rôles à assigner à cet utilisateur
          </DialogDescription>
        </DialogHeader>
        <div class="space-y-4 py-4">
          <div
            v-for="role in availableRoles"
            :key="role"
            class="flex items-center space-x-2"
          >
            <Checkbox
              :checked="selectedRoles.includes(role)"
              @update:checked="toggleRole(role)"
              :id="`role-${role}`"
            />
            <Label :for="`role-${role}`" class="cursor-pointer">
              {{ getRoleLabel(role) }}
            </Label>
          </div>
        </div>
        <DialogFooter>
          <Button variant="outline" @click="showRoleDialog = false">Annuler</Button>
          <Button @click="saveRoles">Enregistrer</Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>

    <!-- Organization Assignment Dialog -->
    <Dialog v-model:open="showOrganizationDialog">
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Assigner une organisation</DialogTitle>
          <DialogDescription>
            Sélectionnez une organisation à assigner à cet utilisateur
          </DialogDescription>
        </DialogHeader>
        <div class="space-y-4 py-4">
          <Select v-model="selectedOrgId">
            <SelectTrigger>
              <SelectValue placeholder="Sélectionner une organisation" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem
                v-for="org in availableOrganizations"
                :key="org.id"
                :value="org.id.toString()"
              >
                {{ org.name }}
              </SelectItem>
            </SelectContent>
          </Select>
          <div class="flex items-center space-x-2">
            <Checkbox
              v-model="isPrimaryOnAssign"
              id="primary-org"
            />
            <Label for="primary-org" class="cursor-pointer">
              Définir comme organisation principale
            </Label>
          </div>
        </div>
        <DialogFooter>
          <Button variant="outline" @click="showOrganizationDialog = false">Annuler</Button>
          <Button @click="assignOrganization" :disabled="!selectedOrgId">Assigner</Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>

    <!-- Reset Password Dialog -->
    <Dialog v-model:open="showResetPasswordDialog">
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Réinitialiser le mot de passe</DialogTitle>
          <DialogDescription>
            Entrez un nouveau mot de passe pour cet utilisateur
          </DialogDescription>
        </DialogHeader>
        <div class="space-y-4 py-4">
          <div class="space-y-2">
            <Label for="newPassword">Nouveau mot de passe</Label>
            <Input
              id="newPassword"
              v-model="newPassword"
              type="password"
              placeholder="Entrez le nouveau mot de passe"
            />
          </div>
          <div class="space-y-2">
            <Label for="confirmPassword">Confirmer le mot de passe</Label>
            <Input
              id="confirmPassword"
              v-model="confirmPassword"
              type="password"
              placeholder="Confirmez le mot de passe"
            />
          </div>
        </div>
        <DialogFooter>
          <Button variant="outline" @click="showResetPasswordDialog = false">Annuler</Button>
          <Button @click="resetPassword" :disabled="!newPassword || newPassword !== confirmPassword">
            Réinitialiser
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  </div>
  <div v-else-if="loading" class="flex items-center justify-center py-12">
    <Loader2 class="h-8 w-8 animate-spin" />
  </div>
  <div v-else class="flex items-center justify-center py-12">
    <div class="text-center">
      <p class="text-muted-foreground">Utilisateur non trouvé</p>
      <Button variant="outline" @click="$router.push('/users')" class="mt-4">
        Retour à la liste
      </Button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useToast } from '@/components/ui/toast'
import { useUser, UserRole, userService, organizationService, type Organization } from '@viridial/shared'
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { Avatar, AvatarImage, AvatarFallback } from '@/components/ui/avatar'
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
  Ban,
  Trash2,
  Plus,
  X,
  Loader2,
  Key,
  Mail
} from 'lucide-vue-next'
import type { UserActivity } from '@viridial/shared'

const route = useRoute()
const router = useRouter()
const { toast } = useToast()
const {
  loadUser,
  // updateUser, // Non utilisé directement
  deleteUser,
  activateUser,
  deactivateUser,
  suspendUser,
  canEditUser,
  canDeleteUser,
  canManageRoles
} = useUser()

const userId = computed(() => Number(route.params.id))
const user = ref<any>(null)
const loading = ref(false)
const activities = ref<UserActivity[]>([])
const loadingActivity = ref(false)
const userOrganizations = ref<Array<Organization & { isPrimary?: boolean }>>([])
const loadingOrganizations = ref(false)
const availableOrganizations = ref<Organization[]>([])

// Dialogs
const showRoleDialog = ref(false)
const showOrganizationDialog = ref(false)
const showResetPasswordDialog = ref(false)
const selectedRoles = ref<UserRole[]>([])
const selectedOrgId = ref<string>('')
const isPrimaryOnAssign = ref(false)
const newPassword = ref('')
const confirmPassword = ref('')

const availableRoles = Object.values(UserRole)

onMounted(async () => {
  await loadUserData()
  await loadActivities()
  await loadUserOrganizations()
  await loadAvailableOrganizations()
})

const loadUserData = async () => {
  loading.value = true
  try {
    await loadUser(userId.value)
    // Get user from store or fetch directly
    const userData = await userService.getById(userId.value)
    user.value = userData
    selectedRoles.value = [...userData.roles]
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Impossible de charger les données utilisateur',
      variant: 'destructive'
    })
  } finally {
    loading.value = false
  }
}

const loadActivities = async () => {
  loadingActivity.value = true
  try {
    activities.value = await userService.getActivity(userId.value, 50)
  } catch (error: any) {
    console.error('Error loading activities:', error)
  } finally {
    loadingActivity.value = false
  }
}

const loadUserOrganizations = async () => {
  loadingOrganizations.value = true
  try {
    const orgs = await organizationService.getByUserId(userId.value)
    // Pour l'instant, on considère que la première organisation est principale
    // TODO: Le backend devrait retourner isPrimary dans la réponse
    userOrganizations.value = orgs.map((org, index) => ({ 
      ...org, 
      isPrimary: index === 0 // Temporaire: première organisation = principale
    }))
  } catch (error: any) {
    console.error('Error loading user organizations:', error)
    toast({
      title: 'Erreur',
      description: 'Impossible de charger les organisations',
      variant: 'destructive'
    })
  } finally {
    loadingOrganizations.value = false
  }
}

const setAsPrimary = async (orgId: number) => {
  try {
    await organizationService.setPrimaryOrganization(orgId, userId.value)
    await loadUserOrganizations()
    toast({
      title: 'Organisation principale définie',
      description: 'L\'organisation principale a été mise à jour avec succès'
    })
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Impossible de définir l\'organisation principale',
      variant: 'destructive'
    })
  }
}

const loadAvailableOrganizations = async () => {
  try {
    const result = await organizationService.getAll()
    // Filter out organizations already assigned
    const assignedIds = userOrganizations.value.map(org => org.id)
    availableOrganizations.value = result.organizations.filter(
      org => !assignedIds.includes(org.id)
    )
  } catch (error: any) {
    console.error('Error loading available organizations:', error)
  }
}

const editUser = () => {
  router.push({ name: 'users', query: { edit: userId.value } })
}

const handleActivate = async () => {
  try {
    await activateUser(userId.value)
    await loadUserData()
    toast({
      title: 'Utilisateur activé',
      description: 'L\'utilisateur a été activé avec succès'
    })
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Impossible d\'activer l\'utilisateur',
      variant: 'destructive'
    })
  }
}

const handleDeactivate = async () => {
  try {
    await deactivateUser(userId.value)
    await loadUserData()
    toast({
      title: 'Utilisateur désactivé',
      description: 'L\'utilisateur a été désactivé avec succès'
    })
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Impossible de désactiver l\'utilisateur',
      variant: 'destructive'
    })
  }
}

const handleSuspend = async () => {
  try {
    await suspendUser(userId.value)
    await loadUserData()
    toast({
      title: 'Utilisateur suspendu',
      description: 'L\'utilisateur a été suspendu avec succès'
    })
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Impossible de suspendre l\'utilisateur',
      variant: 'destructive'
    })
  }
}

const handleDelete = async () => {
  if (!confirm('Êtes-vous sûr de vouloir supprimer cet utilisateur ?')) return
  try {
    await deleteUser(userId.value)
    toast({
      title: 'Utilisateur supprimé',
      description: 'L\'utilisateur a été supprimé avec succès'
    })
    router.push('/users')
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Impossible de supprimer l\'utilisateur',
      variant: 'destructive'
    })
  }
}

const toggleRole = (role: UserRole) => {
  const index = selectedRoles.value.indexOf(role)
  if (index > -1) {
    selectedRoles.value.splice(index, 1)
  } else {
    selectedRoles.value.push(role)
  }
}

const saveRoles = async () => {
  try {
    await userService.assignRoles(userId.value, selectedRoles.value.map(r => r.toString()))
    await loadUserData()
    showRoleDialog.value = false
    toast({
      title: 'Rôles mis à jour',
      description: 'Les rôles de l\'utilisateur ont été mis à jour avec succès'
    })
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Impossible de mettre à jour les rôles',
      variant: 'destructive'
    })
  }
}

const assignOrganization = async () => {
  if (!selectedOrgId.value) return
  try {
    await organizationService.assignUserToOrganization(
      Number(selectedOrgId.value),
      userId.value,
      isPrimaryOnAssign.value
    )
    await loadUserOrganizations()
    await loadAvailableOrganizations()
    showOrganizationDialog.value = false
    selectedOrgId.value = ''
    isPrimaryOnAssign.value = false
    toast({
      title: 'Organisation assignée',
      description: 'L\'organisation a été assignée avec succès'
    })
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Impossible d\'assigner l\'organisation',
      variant: 'destructive'
    })
  }
}

const removeFromOrganization = async (orgId: number) => {
  if (!confirm('Êtes-vous sûr de vouloir retirer cet utilisateur de cette organisation ?')) return
  try {
    await organizationService.removeUserFromOrganization(orgId, userId.value)
    await loadUserOrganizations()
    await loadAvailableOrganizations()
    toast({
      title: 'Organisation retirée',
      description: 'L\'utilisateur a été retiré de l\'organisation avec succès'
    })
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Impossible de retirer l\'organisation',
      variant: 'destructive'
    })
  }
}

const resetPassword = async () => {
  if (newPassword.value !== confirmPassword.value) {
    toast({
      title: 'Erreur',
      description: 'Les mots de passe ne correspondent pas',
      variant: 'destructive'
    })
    return
  }
  try {
    await userService.resetPassword(userId.value, newPassword.value)
    showResetPasswordDialog.value = false
    newPassword.value = ''
    confirmPassword.value = ''
    toast({
      title: 'Mot de passe réinitialisé',
      description: 'Le mot de passe a été réinitialisé avec succès'
    })
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Impossible de réinitialiser le mot de passe',
      variant: 'destructive'
    })
  }
}

const resendVerificationEmail = async () => {
  try {
    await userService.resendVerificationEmail()
    toast({
      title: 'Email envoyé',
      description: 'L\'email de vérification a été renvoyé avec succès'
    })
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Impossible de renvoyer l\'email',
      variant: 'destructive'
    })
  }
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

const getRoleLabel = (role: string) => {
  const labels: Record<string, string> = {
    SUPER_ADMIN: 'Super Admin',
    ADMIN: 'Admin',
    AGENT: 'Agent',
    FREELANCE: 'Freelance',
    AUTO_ENTREPRENEUR: 'Auto-entrepreneur',
    PARTICULAR: 'Particulier',
    USER: 'Utilisateur'
  }
  return labels[role] || role
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

