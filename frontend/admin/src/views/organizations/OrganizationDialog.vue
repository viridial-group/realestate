<template>
  <Dialog :open="open" @update:open="$emit('update:open', $event)">
    <DialogContent class="max-w-2xl max-h-[90vh] overflow-y-auto">
      <DialogHeader>
        <DialogTitle>{{ isEdit ? 'Modifier l\'organisation' : 'Nouvelle organisation' }}</DialogTitle>
        <DialogDescription>
          {{ isEdit ? 'Modifiez les informations de l\'organisation' : 'Créez une nouvelle organisation' }}
        </DialogDescription>
      </DialogHeader>

      <form @submit.prevent="handleSubmit" class="space-y-4">
        <div class="grid grid-cols-2 gap-4">
          <div class="space-y-2">
            <Label for="name">Nom *</Label>
            <Input id="name" v-model="form.name" placeholder="Nom de l'organisation" required />
          </div>
          <div class="space-y-2">
            <Label for="type">Type *</Label>
            <Select v-model="form.type" required>
              <SelectTrigger>
                <SelectValue placeholder="Sélectionner un type" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="AGENCY">Agence</SelectItem>
                <SelectItem value="FREELANCE">Freelance</SelectItem>
                <SelectItem value="COMPANY">Entreprise</SelectItem>
              </SelectContent>
            </Select>
          </div>
        </div>

        <div class="grid grid-cols-2 gap-4">
          <div class="space-y-2">
            <Label for="email">Email *</Label>
            <Input id="email" v-model="form.email" type="email" placeholder="contact@example.com" required />
          </div>
          <div class="space-y-2">
            <Label for="phone">Téléphone</Label>
            <Input id="phone" v-model="form.phone" placeholder="+33 1 23 45 67 89" />
          </div>
        </div>

        <div class="space-y-2">
          <Label for="address">Adresse</Label>
          <Input id="address" v-model="form.address" placeholder="123 Rue Example, 75001 Paris" />
        </div>

        <div class="grid grid-cols-2 gap-4">
          <div class="space-y-2">
            <Label for="siret">SIRET</Label>
            <Input id="siret" v-model="form.siret" placeholder="12345678901234" />
          </div>
          <div class="space-y-2">
            <Label for="status">Statut</Label>
            <Select v-model="form.status">
              <SelectTrigger>
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="ACTIVE">Actif</SelectItem>
                <SelectItem value="INACTIVE">Inactif</SelectItem>
                <SelectItem value="SUSPENDED">Suspendu</SelectItem>
                <SelectItem value="PENDING">En attente</SelectItem>
              </SelectContent>
            </Select>
          </div>
        </div>

        <DialogFooter>
          <Button type="button" variant="outline" @click="$emit('update:open', false)">Annuler</Button>
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
import { useToast } from '@/components/ui/toast'
import { organizationService, type Organization, type OrganizationCreate, type OrganizationUpdate } from '@viridial/shared'
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from '@/components/ui/dialog'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { Loader2 } from 'lucide-vue-next'

interface Props {
  open: boolean
  organization?: Organization | null
}

const props = withDefaults(defineProps<Props>(), {
  organization: null
})

const emit = defineEmits<{
  'update:open': [value: boolean]
  saved: []
}>()

const { toast } = useToast()
const loading = ref(false)

const isEdit = computed(() => !!props.organization)

const form = ref({
  name: '',
  type: '',
  email: '',
  phone: '',
  address: '',
  siret: '',
  status: 'ACTIVE'
})

watch(() => props.organization, (org) => {
  if (org) {
    form.value = {
      name: org.name || '',
      type: org.type || '',
      email: org.email || '',
      phone: org.phone || '',
      address: org.address || '',
      siret: org.siret || '',
      status: org.status || 'ACTIVE'
    }
  } else {
    form.value = {
      name: '',
      type: '',
      email: '',
      phone: '',
      address: '',
      siret: '',
      status: 'ACTIVE'
    }
  }
}, { immediate: true })

const handleSubmit = async () => {
  loading.value = true
  try {
    if (isEdit.value && props.organization) {
      await organizationService.update(props.organization.id, form.value as OrganizationUpdate)
      toast({
        title: 'Organisation modifiée',
        description: 'Les modifications ont été enregistrées avec succès'
      })
    } else {
      await organizationService.create(form.value as OrganizationCreate)
      toast({
        title: 'Organisation créée',
        description: 'L\'organisation a été créée avec succès'
      })
    }
    emit('saved')
    emit('update:open', false)
  } catch (error: any) {
    console.error('Error saving organization:', error)
    toast({
      title: 'Erreur',
      description: error.message || 'Une erreur est survenue lors de l\'enregistrement',
      variant: 'destructive'
    })
  } finally {
    loading.value = false
  }
}
</script>

