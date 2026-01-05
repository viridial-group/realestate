<template>
  <Dialog :open="open" @update:open="$emit('update:open', $event)">
    <DialogContent class="max-w-2xl max-h-[90vh] overflow-y-auto">
      <DialogHeader>
        <DialogTitle>{{ isEdit ? 'Modifier l\'utilisateur' : 'Nouvel utilisateur' }}</DialogTitle>
        <DialogDescription>
          {{ isEdit ? 'Modifiez les informations de l\'utilisateur' : 'Créez un nouvel utilisateur pour la plateforme' }}
        </DialogDescription>
      </DialogHeader>

      <form @submit="onSubmit" class="space-y-4">
        <div class="grid grid-cols-2 gap-4">
          <div class="space-y-2">
            <Label for="name">Nom complet *</Label>
            <Input
              id="name"
              v-model="name"
              v-bind="nameAttrs"
              placeholder="John Doe"
            />
            <p v-if="errors.name" class="text-sm text-destructive">{{ errors.name }}</p>
          </div>
          <div class="space-y-2">
            <Label for="email">Email *</Label>
            <Input
              id="email"
              v-model="email"
              v-bind="emailAttrs"
              type="email"
              placeholder="john@example.com"
              :disabled="isEdit"
            />
            <p v-if="errors.email" class="text-sm text-destructive">{{ errors.email }}</p>
          </div>
        </div>

        <div class="grid grid-cols-2 gap-4">
          <div class="space-y-2">
            <Label for="firstName">Prénom</Label>
            <Input
              id="firstName"
              v-model="firstName"
              v-bind="firstNameAttrs"
              placeholder="John"
            />
            <p v-if="errors.firstName" class="text-sm text-destructive">{{ errors.firstName }}</p>
          </div>
          <div class="space-y-2">
            <Label for="lastName">Nom</Label>
            <Input
              id="lastName"
              v-model="lastName"
              v-bind="lastNameAttrs"
              placeholder="Doe"
            />
            <p v-if="errors.lastName" class="text-sm text-destructive">{{ errors.lastName }}</p>
          </div>
        </div>

        <div class="space-y-2">
          <Label for="phone">Téléphone</Label>
          <Input
            id="phone"
            v-model="phone"
            v-bind="phoneAttrs"
            type="tel"
            placeholder="+33 6 12 34 56 78"
          />
          <p v-if="errors.phone" class="text-sm text-destructive">{{ errors.phone }}</p>
        </div>

        <div v-if="!isEdit" class="space-y-2">
          <Label for="password">Mot de passe *</Label>
          <Input
            id="password"
            v-model="password"
            v-bind="passwordAttrs"
            type="password"
            placeholder="••••••••"
          />
          <p v-if="errors.password" class="text-sm text-destructive">{{ errors.password }}</p>
        </div>

        <div class="grid grid-cols-2 gap-4">
          <div class="space-y-2">
            <Label for="status">Statut</Label>
            <Select v-model="status" v-bind="statusAttrs">
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
            <p v-if="errors.status" class="text-sm text-destructive">{{ errors.status }}</p>
          </div>
          <div class="space-y-2">
            <Label>Rôles *</Label>
            <div class="flex flex-wrap gap-3 p-3 border rounded-lg">
              <div
                v-for="role in availableRoles"
                :key="role.value"
                class="flex items-center space-x-2"
              >
                <Checkbox
                  :id="`role-${role.value}`"
                  :checked="(roles || []).includes(role.value)"
                  @update:checked="(checked: boolean) => toggleRole(role.value, checked)"
                />
                <Label :for="`role-${role.value}`" class="cursor-pointer text-sm">
                  {{ role.label }}
                </Label>
              </div>
            </div>
            <p v-if="errors.roles" class="text-sm text-destructive">{{ errors.roles }}</p>
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
import { computed, watch } from 'vue'
import { useForm } from 'vee-validate'
import { toTypedSchema } from '@vee-validate/zod'
import { useUser, UserStatus, UserRole } from '@viridial/shared'
import type { User, UserCreate, UserUpdate } from '@viridial/shared'
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from '@/components/ui/dialog'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { Checkbox } from '@/components/ui/checkbox'
import { useToast } from '@/components/ui/toast'
import { Loader2 } from 'lucide-vue-next'
import { userCreateSchema, userUpdateSchema } from '@/schemas/user.schema'

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
const { toast } = useToast()

const availableRoles = [
  { value: UserRole.ADMIN, label: 'Admin' },
  { value: UserRole.AGENT, label: 'Agent' },
  { value: UserRole.FREELANCE, label: 'Freelance' },
  { value: UserRole.AUTO_ENTREPRENEUR, label: 'Auto-entrepreneur' },
  { value: UserRole.PARTICULAR, label: 'Particulier' },
  { value: UserRole.USER, label: 'Utilisateur' }
]

const isEdit = computed(() => !!props.user)

const { handleSubmit, defineField, errors, setValues, resetForm } = useForm({
  validationSchema: toTypedSchema(isEdit.value ? userUpdateSchema : userCreateSchema),
  initialValues: {
    name: '',
    email: '',
    firstName: '',
    lastName: '',
    phone: '',
    password: '',
    status: UserStatus.ACTIVE,
    roles: [UserRole.USER]
  }
})

const [name, nameAttrs] = defineField('name')
const [email, emailAttrs] = defineField('email')
const [firstName, firstNameAttrs] = defineField('firstName')
const [lastName, lastNameAttrs] = defineField('lastName')
const [phone, phoneAttrs] = defineField('phone')
const [password, passwordAttrs] = defineField('password')
const [status, statusAttrs] = defineField('status')
const [roles] = defineField('roles')

watch(() => props.user, (user) => {
  if (user) {
    setValues({
      name: user.name,
      email: user.email,
      firstName: user.firstName || '',
      lastName: user.lastName || '',
      phone: user.phone || '',
      status: user.status,
      roles: user.roles
    })
  } else {
    resetForm({
      values: {
        name: '',
        email: '',
        firstName: '',
        lastName: '',
        phone: '',
        password: '',
        status: UserStatus.ACTIVE,
        roles: [UserRole.USER]
      }
    })
  }
}, { immediate: true })

const toggleRole = (role: UserRole, checked: boolean) => {
  const currentRoles = roles.value || []
  if (checked) {
    if (!currentRoles.includes(role)) {
      roles.value = [...currentRoles, role]
    }
  } else {
    roles.value = currentRoles.filter(r => r !== role)
  }
}

const onSubmit = handleSubmit(async (formData) => {
  try {
    if (isEdit.value && props.user) {
      await updateUser(props.user.id, formData as UserUpdate)
      toast({
        title: 'Utilisateur modifié',
        description: 'L\'utilisateur a été modifié avec succès'
      })
    } else {
      await createUser(formData as UserCreate)
      toast({
        title: 'Utilisateur créé',
        description: 'L\'utilisateur a été créé avec succès'
      })
    }
    emit('saved')
    emit('update:open', false)
  } catch (error: any) {
    toast({
      title: 'Erreur',
      description: error.message || 'Une erreur est survenue lors de la sauvegarde',
      variant: 'destructive'
    })
  }
})
</script>

