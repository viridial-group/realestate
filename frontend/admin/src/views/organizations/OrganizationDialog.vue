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
        <div class="space-y-2">
          <Label for="name">Nom *</Label>
          <Input id="name" v-model="form.name" placeholder="Nom de l'organisation" required />
        </div>

        <div class="space-y-2">
          <Label for="description">Description</Label>
          <textarea
            id="description"
            v-model="form.description"
            placeholder="Description de l'organisation"
            class="flex min-h-[80px] w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
          />
        </div>

        <div class="space-y-2">
          <Label for="domain">Domaine</Label>
          <Input id="domain" v-model="form.domain" placeholder="example.com" />
        </div>

        <div class="space-y-2">
          <div class="flex items-center space-x-2">
            <Checkbox id="active" v-model:checked="form.active" />
            <Label for="active" class="cursor-pointer">Organisation active</Label>
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
import { Checkbox } from '@/components/ui/checkbox'
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
  description: '',
  domain: '',
  active: true,
  parentId: undefined as number | undefined
})

watch(() => props.organization, (org) => {
  if (org) {
    form.value = {
      name: org.name || '',
      description: org.description || '',
      domain: org.domain || '',
      active: org.active !== undefined ? org.active : true,
      parentId: org.parentId
    }
  } else {
    form.value = {
      name: '',
      description: '',
      domain: '',
      active: true,
      parentId: undefined
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

