<template>
  <Dialog :open="open" @update:open="$emit('update:open', $event)">
    <DialogContent class="max-w-3xl max-h-[90vh] overflow-y-auto">
      <DialogHeader>
        <DialogTitle>{{ role ? 'Modifier le rôle' : 'Créer un nouveau rôle' }}</DialogTitle>
        <DialogDescription>
          {{ role ? 'Modifiez les informations du rôle' : 'Définissez un nouveau rôle avec ses permissions' }}
        </DialogDescription>
      </DialogHeader>

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
        </div>

        <!-- Description -->
        <div class="space-y-2">
          <Label for="description">Description</Label>
          <Textarea
            id="description"
            v-model="form.description"
            placeholder="Description du rôle..."
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

          <ScrollArea class="h-[300px] border rounded-lg p-4">
            <div class="space-y-4">
              <div
                v-for="resource in groupedPermissions"
                :key="resource.name"
                class="space-y-2"
              >
                <div class="flex items-center justify-between pb-2 border-b">
                  <h3 class="font-semibold text-sm">{{ resource.name }}</h3>
                </div>
                <div class="grid grid-cols-2 gap-2 pl-4">
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
                  </div>
                </div>
              </div>
            </div>
          </ScrollArea>
        </div>

        <!-- Actions -->
        <DialogFooter>
          <Button
            type="button"
            variant="outline"
            @click="$emit('update:open', false)"
          >
            Annuler
          </Button>
          <Button type="submit" :disabled="loading">
            <Loader2 v-if="loading" class="mr-2 h-4 w-4 animate-spin" />
            {{ role ? 'Enregistrer' : 'Créer' }}
          </Button>
        </DialogFooter>
      </form>
    </DialogContent>
  </Dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { roleService, type Role, type Permission, type RoleCreate, type RoleUpdate } from '@viridial/shared'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle
} from '@/components/ui/dialog'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Textarea } from '@/components/ui/textarea'
import { Checkbox } from '@/components/ui/checkbox'
import { ScrollArea } from '@/components/ui/scroll-area'
import { Loader2 } from 'lucide-vue-next'
import { useToast } from '@/components/ui/toast'

const props = defineProps<{
  open: boolean
  role?: Role | null
}>()

const emit = defineEmits<{
  'update:open': [value: boolean]
  saved: []
}>()

const { toast } = useToast()

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

watch(() => props.role, (role) => {
  if (role) {
    form.value = {
      name: role.name,
      description: role.description || ''
    }
    selectedPermissions.value = role.permissions.map(p => p.id)
  } else {
    form.value = {
      name: '',
      description: ''
    }
    selectedPermissions.value = []
  }
}, { immediate: true })

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

const handleSubmit = async () => {
  loading.value = true
  try {
    if (props.role) {
      const data: RoleUpdate = {
        name: form.value.name,
        description: form.value.description || undefined,
        permissionIds: selectedPermissions.value
      }
      await roleService.update(props.role.id, data)
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
    emit('update:open', false)
    emit('saved')
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

onMounted(() => {
  loadPermissions()
})
</script>

