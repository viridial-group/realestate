<template>
  <Dialog :open="open" @update:open="$emit('update:open', $event)">
    <DialogContent class="max-w-2xl max-h-[90vh] overflow-y-auto">
      <DialogHeader>
        <DialogTitle>{{ isEdit ? 'Modifier l\'utilisateur' : 'Nouvel utilisateur' }}</DialogTitle>
        <DialogDescription>
          {{ isEdit ? 'Modifiez les informations de l\'utilisateur' : 'Créez un nouvel utilisateur pour la plateforme' }}
        </DialogDescription>
      </DialogHeader>

      <form @submit.prevent="handleSubmit" class="space-y-4">
        <div class="grid grid-cols-2 gap-4">
          <div class="space-y-2">
            <Label for="name">Nom complet *</Label>
            <Input
              id="name"
              v-model="form.name"
              placeholder="John Doe"
              required
            />
          </div>
          <div class="space-y-2">
            <Label for="email">Email *</Label>
            <Input
              id="email"
              v-model="form.email"
              type="email"
              placeholder="john@example.com"
              required
              :disabled="isEdit"
            />
          </div>
        </div>

        <div class="grid grid-cols-2 gap-4">
          <div class="space-y-2">
            <Label for="firstName">Prénom</Label>
            <Input
              id="firstName"
              v-model="form.firstName"
              placeholder="John"
            />
          </div>
          <div class="space-y-2">
            <Label for="lastName">Nom</Label>
            <Input
              id="lastName"
              v-model="form.lastName"
              placeholder="Doe"
            />
          </div>
        </div>

        <div class="space-y-2">
          <Label for="phone">Téléphone</Label>
          <Input
            id="phone"
            v-model="form.phone"
            type="tel"
            placeholder="+33 6 12 34 56 78"
          />
        </div>

        <div v-if="!isEdit" class="space-y-2">
          <Label for="password">Mot de passe *</Label>
          <Input
            id="password"
            v-model="form.password"
            type="password"
            placeholder="••••••••"
            required
          />
        </div>

        <div class="grid grid-cols-2 gap-4">
          <div class="space-y-2">
            <Label for="status">Statut</Label>
            <Select v-model="form.status">
              <SelectTrigger>
                <SelectValue placeholder="Sélectionner un statut" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="ACTIVE">Actif</SelectItem>
                <SelectItem value="INACTIVE">Inactif</SelectItem>
                <SelectItem value="PENDING">En attente</SelectItem>
                <SelectItem value="SUSPENDED">Suspendu</SelectItem>
              </SelectContent>
            </Select>
          </div>
          <div class="space-y-2">
            <Label>Rôles</Label>
            <div class="flex flex-wrap gap-2">
              <div
                v-for="role in availableRoles"
                :key="role.value"
                class="flex items-center space-x-2"
              >
                <input
                  type="checkbox"
                  :id="`role-${role.value}`"
                  :value="role.value"
                  v-model="form.roles"
                  class="rounded"
                />
                <Label :for="`role-${role.value}`" class="cursor-pointer">
                  {{ role.label }}
                </Label>
              </div>
            </div>
          </div>
        </div>

        <DialogFooter>
          <Button type="button" variant="outline" @click="$emit('update:open', false)">
            Annuler
          </Button>
          <Button type="submit" :disabled="loading">
            <Loader2 v-if="loading" class="mr-2 h-4 w-4 animate-spin" />
            {{ isEdit ? 'Modifier' : 'Créer' }}
          </Button>
        </DialogFooter>
      </form>
    </DialogContent>
  </Dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useUser, UserStatus, UserRole } from '@viridial/shared'
import type { User, UserCreate, UserUpdate } from '@viridial/shared'
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from '@/components/ui/dialog'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { Loader2 } from 'lucide-vue-next'

interface Props {
  open: boolean
  user?: User | null
}

const props = withDefaults(defineProps<Props>(), {
  user: null
})

const emit = defineEmits<{
  'update:open': [value: boolean]
  saved: []
}>()

const { createUser, updateUser, loading } = useUser()

const availableRoles = [
  { value: UserRole.ADMIN, label: 'Admin' },
  { value: UserRole.AGENT, label: 'Agent' },
  { value: UserRole.FREELANCE, label: 'Freelance' },
  { value: UserRole.AUTO_ENTREPRENEUR, label: 'Auto-entrepreneur' },
  { value: UserRole.PARTICULAR, label: 'Particulier' },
  { value: UserRole.USER, label: 'Utilisateur' }
]

const form = ref<UserCreate | UserUpdate>({
  name: '',
  email: '',
  firstName: '',
  lastName: '',
  phone: '',
  password: '',
  status: UserStatus.ACTIVE,
  roles: [UserRole.USER]
})

const isEdit = computed(() => !!props.user)

watch(() => props.user, (user) => {
  if (user) {
    form.value = {
      name: user.name,
      email: user.email,
      firstName: user.firstName,
      lastName: user.lastName,
      phone: user.phone,
      status: user.status,
      roles: user.roles
    }
  } else {
    form.value = {
      name: '',
      email: '',
      firstName: '',
      lastName: '',
      phone: '',
      password: '',
      status: UserStatus.ACTIVE,
      roles: [UserRole.USER]
    }
  }
}, { immediate: true })

const handleSubmit = async () => {
  try {
    if (isEdit.value && props.user) {
      await updateUser(props.user.id, form.value as UserUpdate)
    } else {
      await createUser(form.value as UserCreate)
    }
    emit('saved')
    emit('update:open', false)
  } catch (error) {
    console.error('Error saving user:', error)
  }
}
</script>

