<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2.5xl font-bold">
          {{ isEdit ? 'Modifier le rôle' : 'Créer un nouveau rôle' }}
        </h1>
        <p class="text-muted-foreground mt-1">
          {{ isEdit ? 'Modifiez les informations du rôle' : 'Définissez un nouveau rôle avec ses permissions' }}
        </p>
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
          <!-- Name -->
          <div class="space-y-2">
            <Label for="name">Nom du rôle *</Label>
            <Input
              id="name"
              v-model="form.name"
              placeholder="Ex: MANAGER, AGENT..."
              required
            />
            <p class="text-xs text-muted-foreground">
              Le nom doit être en majuscules et unique
            </p>
          </div>

          <!-- Description -->
          <div class="space-y-2">
            <Label for="description">Description</Label>
            <Textarea
              id="description"
              v-model="form.description"
              placeholder="Description du rôle et de ses responsabilités..."
              rows="3"
            />
          </div>

          <!-- Permissions -->
          <div class="space-y-4">
            <div class="flex items-center justify-between">
              <Label>Permissions</Label>
              <div class="flex gap-2">
                <Button
                  type="button"
                  variant="outline"
                  size="sm"
                  @click="selectAllPermissions"
                >
                  Tout sélectionner
                </Button>
                <Button
                  type="button"
                  variant="outline"
                  size="sm"
                  @click="deselectAllPermissions"
                >
                  Tout désélectionner
                </Button>
              </div>
            </div>

            <!-- Grouped by Resource -->
            <div class="space-y-4 border rounded-lg p-4">
              <div
                v-for="resource in groupedPermissions"
                :key="resource.name"
                class="space-y-2"
              >
                <div class="flex items-center justify-between pb-2 border-b">
                  <h3 class="font-semibold text-sm">{{ resource.name }}</h3>
                  <div class="flex gap-2">
                    <Button
                      type="button"
                      variant="ghost"
                      size="sm"
                      @click="selectResourcePermissions(resource.name)"
                    >
                      Tout
                    </Button>
                    <Button
                      type="button"
                      variant="ghost"
                      size="sm"
                      @click="deselectResourcePermissions(resource.name)"
                    >
                      Rien
                    </Button>
                  </div>
                </div>
                <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-3 pl-4">
                  <div
                    v-for="permission in resource.permissions"
                    :key="permission.id"
                    class="flex items-center space-x-2"
                  >
                    <Checkbox
                      :id="`perm-${permission.id}`"
                      :checked="selectedPermissions.includes(permission.id)"
                      @update:checked="(checked: boolean) => togglePermission(permission.id, checked)"
                    />
                    <Label
                      :for="`perm-${permission.id}`"
                      class="text-sm font-normal cursor-pointer"
                    >
                      {{ permission.action }}
                    </Label>
                    <span class="text-xs text-muted-foreground">
                      ({{ permission.name }})
                    </span>
                  </div>
                </div>
              </div>
            </div>
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
            <Button type="submit" :disabled="loading">
              <Loader2 v-if="loading" class="mr-2 h-4 w-4 animate-spin" />
              {{ isEdit ? 'Enregistrer' : 'Créer' }}
            </Button>
          </div>
        </form>
      </CardContent>
    </Card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { roleService, type Permission, type RoleCreate, type RoleUpdate } from '@viridial/shared'
import { Card, CardContent } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Textarea } from '@/components/ui/textarea'
import { Checkbox } from '@/components/ui/checkbox'
import { ArrowLeft, Loader2 } from 'lucide-vue-next'
import { useToast } from '@/components/ui/toast'

const route = useRoute()
const router = useRouter()
const { toast } = useToast()

const roleId = computed(() => route.params.id ? Number(route.params.id) : null)
const isEdit = computed(() => !!roleId.value)
const loading = ref(false)

const form = ref({
  name: '',
  description: ''
})

const permissions = ref<Permission[]>([])
const selectedPermissions = ref<number[]>([])

const groupedPermissions = computed(() => {
  const grouped: Record<string, Permission[]> = {}
  permissions.value.forEach(perm => {
    if (!grouped[perm.resource]) {
      grouped[perm.resource] = []
    }
    grouped[perm.resource].push(perm)
  })
  return Object.entries(grouped).map(([name, perms]) => ({
    name,
    permissions: perms
  }))
})

const loadPermissions = async () => {
  try {
    permissions.value = await roleService.getPermissions()
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Erreur lors du chargement des permissions',
      variant: 'destructive'
    })
  }
}

const loadRole = async () => {
  if (!roleId.value) return
  
  loading.value = true
  try {
    const role = await roleService.getById(roleId.value)
    form.value = {
      name: role.name,
      description: role.description || ''
    }
    selectedPermissions.value = role.permissions.map(p => p.id)
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Erreur lors du chargement du rôle',
      variant: 'destructive'
    })
    router.push('/roles')
  } finally {
    loading.value = false
  }
}

const togglePermission = (permissionId: number, checked: boolean) => {
  if (checked) {
    if (!selectedPermissions.value.includes(permissionId)) {
      selectedPermissions.value.push(permissionId)
    }
  } else {
    selectedPermissions.value = selectedPermissions.value.filter(id => id !== permissionId)
  }
}

const selectAllPermissions = () => {
  selectedPermissions.value = permissions.value.map(p => p.id)
}

const deselectAllPermissions = () => {
  selectedPermissions.value = []
}

const selectResourcePermissions = (resource: string) => {
  const resourcePerms = permissions.value.filter(p => p.resource === resource)
  resourcePerms.forEach(perm => {
    if (!selectedPermissions.value.includes(perm.id)) {
      selectedPermissions.value.push(perm.id)
    }
  })
}

const deselectResourcePermissions = (resource: string) => {
  const resourcePerms = permissions.value.filter(p => p.resource === resource)
  selectedPermissions.value = selectedPermissions.value.filter(
    id => !resourcePerms.some(p => p.id === id)
  )
}

const handleSubmit = async () => {
  loading.value = true
  try {
    if (isEdit.value) {
      const data: RoleUpdate = {
        name: form.value.name,
        description: form.value.description || undefined,
        permissionIds: selectedPermissions.value
      }
      await roleService.update(roleId.value!, data)
      toast({
        title: 'Succès',
        description: 'Rôle modifié avec succès'
      })
    } else {
      const data: RoleCreate = {
        name: form.value.name,
        description: form.value.description || undefined,
        permissionIds: selectedPermissions.value
      }
      await roleService.create(data)
      toast({
        title: 'Succès',
        description: 'Rôle créé avec succès'
      })
    }
    router.push('/roles')
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Erreur lors de l\'enregistrement',
      variant: 'destructive'
    })
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await loadPermissions()
  if (isEdit.value) {
    await loadRole()
  }
})
</script>

