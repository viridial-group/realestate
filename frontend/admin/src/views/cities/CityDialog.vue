<template>
  <Dialog :open="open" @update:open="$emit('update:open', $event)">
    <DialogContent class="max-w-3xl max-h-[90vh] overflow-y-auto">
      <DialogHeader>
        <DialogTitle>{{ city ? 'Modifier la ville' : 'Créer une nouvelle ville' }}</DialogTitle>
        <DialogDescription>
          {{ city ? 'Modifiez les informations de la ville' : 'Remplissez les informations pour créer une nouvelle ville' }}
        </DialogDescription>
      </DialogHeader>

      <form @submit.prevent="handleSubmit" class="space-y-4">
        <div class="grid grid-cols-2 gap-4">
          <div class="space-y-2">
            <Label for="name">Nom de la ville *</Label>
            <Input
              id="name"
              v-model="form.name"
              placeholder="Paris"
              required
            />
          </div>

          <div class="space-y-2">
            <Label for="postalCode">Code postal</Label>
            <Input
              id="postalCode"
              v-model="form.postalCode"
              placeholder="75001"
            />
          </div>
        </div>

        <div class="space-y-2">
          <Label for="countryId">Pays *</Label>
          <Select v-model="form.countryId" required>
            <SelectTrigger>
              <SelectValue placeholder="Sélectionner un pays" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem
                v-for="country in countries"
                :key="country.id"
                :value="String(country.id)"
              >
                {{ country.flagEmoji || '🌍' }} {{ country.name }} ({{ country.code }})
              </SelectItem>
            </SelectContent>
          </Select>
        </div>

        <div class="grid grid-cols-2 gap-4">
          <div class="space-y-2">
            <Label for="region">Région</Label>
            <Input
              id="region"
              v-model="form.region"
              placeholder="Île-de-France"
            />
          </div>

          <div class="space-y-2">
            <Label for="department">Département</Label>
            <Input
              id="department"
              v-model="form.department"
              placeholder="Paris"
            />
          </div>
        </div>

        <div class="grid grid-cols-2 gap-4">
          <div class="space-y-2">
            <Label for="latitude">Latitude</Label>
            <Input
              id="latitude"
              v-model.number="form.latitude"
              type="number"
              step="0.0000001"
              placeholder="48.856614"
            />
          </div>

          <div class="space-y-2">
            <Label for="longitude">Longitude</Label>
            <Input
              id="longitude"
              v-model.number="form.longitude"
              type="number"
              step="0.0000001"
              placeholder="2.352222"
            />
          </div>
        </div>

        <div class="space-y-2">
          <Label for="timezone">Fuseau horaire</Label>
          <Input
            id="timezone"
            v-model="form.timezone"
            placeholder="Europe/Paris"
          />
        </div>

        <div class="space-y-2">
          <Label for="description">Description</Label>
          <Textarea
            id="description"
            v-model="form.description"
            placeholder="Description de la ville..."
            rows="3"
          />
        </div>

        <div class="space-y-2">
          <Label for="importantData">Données importantes (JSON)</Label>
          <Textarea
            id="importantData"
            v-model="form.importantData"
            placeholder='{"population": 2161000, "area": 105.4, "density": 20499, "tourist_attractions": ["Tour Eiffel", "Louvre"]}'
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
            <Label for="active" class="cursor-pointer">Ville active</Label>
          </div>
        </div>

        <DialogFooter>
          <Button type="button" variant="outline" @click="$emit('update:open', false)">
            Annuler
          </Button>
          <Button type="submit" :disabled="saving">
            <Loader2 v-if="saving" class="mr-2 h-4 w-4 animate-spin" />
            {{ city ? 'Modifier' : 'Créer' }}
          </Button>
        </DialogFooter>
      </form>
    </DialogContent>
  </Dialog>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { cityService } from '@viridial/shared'
import type { City, CityCreate, CityUpdate, Country } from '@viridial/shared'
import { useToast } from '@/components/ui/toast'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Textarea } from '@/components/ui/textarea'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from '@/components/ui/dialog'
import { Loader2 } from 'lucide-vue-next'

const props = defineProps<{
  open: boolean
  city?: City | null
  countries: Country[]
}>()

const emit = defineEmits<{
  'update:open': [value: boolean]
  'saved': []
}>()

const { toast } = useToast()
const saving = ref(false)

const form = ref<CityCreate | CityUpdate>({
  name: '',
  postalCode: '',
  countryId: 0,
  latitude: undefined,
  longitude: undefined,
  region: '',
  department: '',
  timezone: '',
  description: '',
  importantData: '',
  active: true,
  displayOrder: 0
})

watch(() => props.city, (city) => {
  if (city) {
    form.value = {
      name: city.name,
      postalCode: city.postalCode || '',
      countryId: city.countryId,
      latitude: city.latitude,
      longitude: city.longitude,
      region: city.region || '',
      department: city.department || '',
      timezone: city.timezone || '',
      description: city.description || '',
      importantData: city.importantData || '',
      active: city.active ?? true,
      displayOrder: city.displayOrder ?? 0
    }
  } else {
    form.value = {
      name: '',
      postalCode: '',
      countryId: props.countries.length > 0 ? props.countries[0].id : 0,
      latitude: undefined,
      longitude: undefined,
      region: '',
      department: '',
      timezone: '',
      description: '',
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

  // Valider countryId
  if (!form.value.countryId || form.value.countryId === 0) {
    toast({
      title: 'Erreur de validation',
      description: 'Veuillez sélectionner un pays',
      variant: 'destructive'
    })
    return
  }

  saving.value = true
  try {
    if (props.city) {
      await cityService.update(props.city.id, form.value as CityUpdate)
      toast({
        title: 'Succès',
        description: 'Ville modifiée avec succès'
      })
    } else {
      await cityService.create(form.value as CityCreate)
      toast({
        title: 'Succès',
        description: 'Ville créée avec succès'
      })
    }
    emit('saved')
    emit('update:open', false)
  } catch (error: any) {
    console.error('Error saving city:', error)
    toast({
      title: 'Erreur',
      description: error.response?.data?.message || 'Impossible de sauvegarder la ville',
      variant: 'destructive'
    })
  } finally {
    saving.value = false
  }
}
</script>

