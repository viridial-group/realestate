<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2.5xl font-bold">Affecter des Rôles</h1>
        <p class="text-muted-foreground mt-1">Affectez des rôles aux utilisateurs</p>
      </div>
      <Button variant="outline" @click="$router.push('/roles')">
        <ArrowLeft class="mr-2 h-4 w-4" />
        Retour
      </Button>
    </div>

    <!-- Form -->
    <Card>
      <CardContent class="p-6">
        <form @submit.prevent="handleSubmit" class="space-y-6">
          <!-- User Selection -->
          <div class="space-y-2">
            <Label for="user">Utilisateur *</Label>
            <Combobox
              v-model="selectedUserId"
              :search-value="userSearch"
              @update:search-value="userSearch = $event"
            >
              <ComboboxAnchor as-child>
                <ComboboxInput placeholder="Rechercher un utilisateur..." />
              </ComboboxAnchor>
              <ComboboxList>
                <ComboboxItem
                  v-for="user in filteredUsers"
                  :key="user.id"
                  :value="user.id.toString()"
                >
                  {{ user.name }} ({{ user.email }})
                </ComboboxItem>
                <ComboboxEmpty>
                  Aucun utilisateur trouvé
                </ComboboxEmpty>
              </ComboboxList>
            </Combobox>
          </div>

          <!-- Roles Selection -->
          <div class="space-y-4">
            <Label>Rôles à affecter *</Label>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4 border rounded-lg p-4">
              <div
                v-for="role in roles"
                :key="role.id"
                class="flex items-center space-x-2"
              >
                <Checkbox
                  :id="`role-${role.id}`"
                  :checked="selectedRoleIds.includes(role.id)"
                  @update:checked="(checked) => toggleRole(role.id, checked)"
                />
                <Label
                  :for="`role-${role.id}`"
                  class="flex-1 cursor-pointer"
                >
                  <div class="font-medium">{{ role.name }}</div>
                  <div class="text-xs text-muted-foreground">
                    {{ role.description || 'Aucune description' }}
                  </div>
                  <div class="text-xs text-muted-foreground mt-1">
                    {{ role.permissions.length }} permission(s)
                  </div>
                </Label>
              </div>
            </div>
          </div>

          <!-- Organization (optional) -->
          <div class="space-y-2">
            <Label for="organization">Organisation (optionnel)</Label>
            <Combobox
              v-model="selectedOrganizationId"
              :search-value="orgSearch"
              @update:search-value="orgSearch = $event"
            >
              <ComboboxAnchor as-child>
                <ComboboxInput placeholder="Rechercher une organisation..." />
              </ComboboxAnchor>
              <ComboboxList>
                <ComboboxItem value="">
                  Aucune organisation
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

          <!-- Actions -->
          <div class="flex justify-end gap-2 pt-4 border-t">
            <Button
              type="button"
              variant="outline"
              @click="$router.push('/roles')"
            >
              Annuler
            </Button>
            <Button type="submit" :disabled="loading || !selectedUserId || selectedRoleIds.length === 0">
              <Loader2 v-if="loading" class="mr-2 h-4 w-4 animate-spin" />
              Affecter les rôles
            </Button>
          </div>
        </form>
      </CardContent>
    </Card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { roleService, userService, organizationService, type Role, type User, type Organization, type UserRoleAssignment } from '@viridial/shared'
import { Card, CardContent } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Label } from '@/components/ui/label'
import { Checkbox } from '@/components/ui/checkbox'
import {
  Combobox,
  ComboboxAnchor,
  ComboboxInput,
  ComboboxList,
  ComboboxItem,
  ComboboxEmpty
} from '@/components/ui/combobox'
import { ArrowLeft, Loader2 } from 'lucide-vue-next'
import { useToast } from '@/components/ui/toast'

const router = useRouter()
const { toast } = useToast()

const loading = ref(false)
const roles = ref<Role[]>([])
const users = ref<User[]>([])
const organizations = ref<Organization[]>([])

const selectedUserId = ref('')
const selectedRoleIds = ref<number[]>([])
const selectedOrganizationId = ref('')
const userSearch = ref('')
const orgSearch = ref('')

const filteredUsers = computed(() => {
  if (!userSearch.value) return users.value.slice(0, 20)
  const search = userSearch.value.toLowerCase()
  return users.value.filter(
    u => u.name.toLowerCase().includes(search) || u.email.toLowerCase().includes(search)
  ).slice(0, 20)
})

const filteredOrganizations = computed(() => {
  if (!orgSearch.value) return organizations.value.slice(0, 20)
  const search = orgSearch.value.toLowerCase()
  return organizations.value.filter(o => o.name.toLowerCase().includes(search)).slice(0, 20)
})

const loadRoles = async () => {
  try {
    const page = await roleService.getAll({ size: 1000 })
    roles.value = page.content
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Erreur lors du chargement des rôles',
      variant: 'destructive'
    })
  }
}

const loadUsers = async () => {
  try {
    const page = await userService.getAll({ size: 1000 })
    users.value = page.content
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Erreur lors du chargement des utilisateurs',
      variant: 'destructive'
    })
  }
}

const loadOrganizations = async () => {
  try {
    const page = await organizationService.getAll({ size: 1000 })
    organizations.value = page.content
  } catch (error: any) {
    // Organizations might not be available, ignore error
  }
}

const toggleRole = (roleId: number, checked: boolean) => {
  if (checked) {
    if (!selectedRoleIds.value.includes(roleId)) {
      selectedRoleIds.value.push(roleId)
    }
  } else {
    selectedRoleIds.value = selectedRoleIds.value.filter(id => id !== roleId)
  }
}

const handleSubmit = async () => {
  if (!selectedUserId.value || selectedRoleIds.value.length === 0) {
    toast({
      title: 'Erreur',
      description: 'Veuillez sélectionner un utilisateur et au moins un rôle',
      variant: 'destructive'
    })
    return
  }

  loading.value = true
  try {
    const data: UserRoleAssignment = {
      userId: Number(selectedUserId.value),
      roleIds: selectedRoleIds.value,
      organizationId: selectedOrganizationId.value ? Number(selectedOrganizationId.value) : undefined
    }
    await roleService.assignRolesToUser(data)
    toast({
      title: 'Succès',
      description: 'Rôles affectés avec succès'
    })
    router.push('/roles')
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Erreur lors de l\'affectation des rôles',
      variant: 'destructive'
    })
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await Promise.all([
    loadRoles(),
    loadUsers(),
    loadOrganizations()
  ])
})
</script>

