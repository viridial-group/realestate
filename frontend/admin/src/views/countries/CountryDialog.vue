<template>
  <Dialog :open="open" @update:open="$emit('update:open', $event)">
    <DialogContent class="max-w-3xl max-h-[90vh] overflow-y-auto">
      <DialogHeader>
        <DialogTitle>{{ country ? 'Modifier le pays' : 'Créer un nouveau pays' }}</DialogTitle>
        <DialogDescription>
          {{ country ? 'Modifiez les informations du pays' : 'Remplissez les informations pour créer un nouveau pays' }}
        </DialogDescription>
      </DialogHeader>

      <form @submit.prevent="handleSubmit" class="space-y-4">
        <div class="grid grid-cols-2 gap-4">
          <div class="space-y-2">
            <Label for="code">Code ISO (2 lettres) *</Label>
            <Input
              id="code"
              v-model="form.code"
              placeholder="FR"
              maxlength="2"
              required
              :disabled="!!country"
            />
            <p class="text-xs text-muted-foreground">Code ISO 3166-1 alpha-2 (ex: FR, US)</p>
          </div>

          <div class="space-y-2">
            <Label for="code3">Code ISO (3 lettres)</Label>
            <Input
              id="code3"
              v-model="form.code3"
              placeholder="FRA"
              maxlength="3"
            />
            <p class="text-xs text-muted-foreground">Code ISO 3166-1 alpha-3 (ex: FRA, USA)</p>
          </div>
        </div>

        <div class="space-y-2">
          <Label for="name">Nom du pays *</Label>
          <Input
            id="name"
            v-model="form.name"
            placeholder="France"
            required
          />
        </div>

        <div class="grid grid-cols-2 gap-4">
          <div class="space-y-2">
            <Label for="phoneCode">Code téléphonique</Label>
            <Input
              id="phoneCode"
              v-model="form.phoneCode"
              placeholder="+33"
            />
          </div>

          <div class="space-y-2">
            <Label for="currency">Devise</Label>
            <Input
              id="currency"
              v-model="form.currency"
              placeholder="EUR"
              maxlength="10"
            />
          </div>
        </div>

        <div class="grid grid-cols-2 gap-4">
          <div class="space-y-2">
            <Label for="currencySymbol">Symbole devise</Label>
            <Input
              id="currencySymbol"
              v-model="form.currencySymbol"
              placeholder="€"
              maxlength="10"
            />
          </div>

          <div class="space-y-2">
            <Label for="timezone">Fuseau horaire</Label>
            <Input
              id="timezone"
              v-model="form.timezone"
              placeholder="Europe/Paris"
            />
          </div>
        </div>

        <div class="space-y-2">
          <Label for="flagEmoji">Emoji drapeau</Label>
          <Input
            id="flagEmoji"
            v-model="form.flagEmoji"
            placeholder="🇫🇷"
          />
        </div>

        <div class="grid grid-cols-2 gap-4">
          <div class="space-y-2">
            <Label for="latitude">Latitude</Label>
            <Input
              id="latitude"
              v-model.number="form.latitude"
              type="number"
              step="0.0000001"
              placeholder="46.603354"
            />
          </div>

          <div class="space-y-2">
            <Label for="longitude">Longitude</Label>
            <Input
              id="longitude"
              v-model.number="form.longitude"
              type="number"
              step="0.0000001"
              placeholder="1.888334"
            />
          </div>
        </div>

        <div class="space-y-2">
          <Label for="description">Description</Label>
          <Textarea
            id="description"
            v-model="form.description"
            placeholder="Description du pays..."
            rows="3"
          />
        </div>

        <div class="space-y-2">
          <Label for="importantData">Données importantes (JSON)</Label>
          <Textarea
            id="importantData"
            v-model="form.importantData"
            placeholder='{"population": 67000000, "area": 643801, "languages": ["fr"], "capital": "Paris"}'
            rows="5"
            class="font-mono text-sm"
          />
          <p class="text-xs text-muted-foreground">Format JSON valide requis</p>
        </div>

        <div class="grid grid-cols-2 gap-4">
          <div class="space-y-2">
            <Label for="displayOrder">Ordre d'affichage</Label>
            <Input
              id="displayOrder"
              v-model.number="form.displayOrder"
              type="number"
              min="0"
            />
          </div>

          <div class="flex items-center space-x-2 pt-8">
            <input
              id="active"
              v-model="form.active"
              type="checkbox"
              class="h-4 w-4 rounded border-gray-300"
            />
            <Label for="active" class="cursor-pointer">Pays actif</Label>
          </div>
        </div>

        <DialogFooter>
          <Button type="button" variant="outline" @click="$emit('update:open', false)">
            Annuler
          </Button>
          <Button type="submit" :disabled="saving">
            <Loader2 v-if="saving" class="mr-2 h-4 w-4 animate-spin" />
            {{ country ? 'Modifier' : 'Créer' }}
          </Button>
        </DialogFooter>
      </form>
    </DialogContent>
  </Dialog>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { countryService } from '@viridial/shared'
import type { Country, CountryCreate, CountryUpdate } from '@viridial/shared'
import { useToast } from '@/components/ui/toast'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Textarea } from '@/components/ui/textarea'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle
} from '@/components/ui/dialog'
import { Loader2 } from 'lucide-vue-next'

const props = defineProps<{
  open: boolean
  country?: Country | null
}>()

const emit = defineEmits<{
  'update:open': [value: boolean]
  'saved': []
}>()

const { toast } = useToast()
const saving = ref(false)

const form = ref<CountryCreate | CountryUpdate>({
  code: '',
  name: '',
  code3: '',
  phoneCode: '',
  currency: '',
  currencySymbol: '',
  timezone: '',
  flagEmoji: '',
  description: '',
  latitude: undefined,
  longitude: undefined,
  importantData: '',
  active: true,
  displayOrder: 0
})

watch(() => props.country, (country) => {
  if (country) {
    form.value = {
      code: country.code,
      name: country.name,
      code3: country.code3 || '',
      phoneCode: country.phoneCode || '',
      currency: country.currency || '',
      currencySymbol: country.currencySymbol || '',
      timezone: country.timezone || '',
      flagEmoji: country.flagEmoji || '',
      description: country.description || '',
      latitude: country.latitude,
      longitude: country.longitude,
      importantData: country.importantData || '',
      active: country.active ?? true,
      displayOrder: country.displayOrder ?? 0
    }
  } else {
    form.value = {
      code: '',
      name: '',
      code3: '',
      phoneCode: '',
      currency: '',
      currencySymbol: '',
      timezone: '',
      flagEmoji: '',
      description: '',
      latitude: undefined,
      longitude: undefined,
      importantData: '',
      active: true,
      displayOrder: 0
    }
  }
}, { immediate: true })

const handleSubmit = async () => {
  // Valider le JSON si importantData est fourni
  if (form.value.importantData) {
    try {
      JSON.parse(form.value.importantData)
    } catch {
      toast({
        title: 'Erreur de validation',
        description: 'Le format JSON des données importantes est invalide',
        variant: 'destructive'
      })
      return
    }
  }

  saving.value = true
  try {
    if (props.country) {
      await countryService.update(props.country.id, form.value as CountryUpdate)
      toast({
        title: 'Succès',
        description: 'Pays modifié avec succès'
      })
    } else {
      await countryService.create(form.value as CountryCreate)
      toast({
        title: 'Succès',
        description: 'Pays créé avec succès'
      })
    }
    emit('saved')
    emit('update:open', false)
  } catch (error: any) {
    console.error('Error saving country:', error)
    toast({
      title: 'Erreur',
      description: error.response?.data?.message || 'Impossible de sauvegarder le pays',
      variant: 'destructive'
    })
  } finally {
    saving.value = false
  }
}
</script>

