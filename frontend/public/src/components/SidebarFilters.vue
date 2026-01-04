<template>
  <Card class="sticky top-24 max-h-[calc(100vh-6rem)] overflow-y-auto">
    <CardHeader class="pb-3">
      <div class="flex items-center justify-between">
        <CardTitle class="text-lg">Filtres</CardTitle>
        <Button
          v-if="hasActiveFilters"
          variant="ghost"
          size="sm"
          @click="$emit('clear-filters')"
        >
          Réinitialiser
        </Button>
      </div>
    </CardHeader>
    <CardContent class="space-y-6">
      <!-- Transaction / Statut avec Select -->
      <div class="space-y-2">
        <Label>Transaction / Statut</Label>
        <Select
          :model-value="combinedFilterValue"
          @update:model-value="handleCombinedFilterChange"
        >
          <SelectTrigger class="w-full">
            <SelectValue :placeholder="getCombinedFilterLabel()" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem
              v-for="option in transactionStatusOptions"
              :key="option.value"
              :value="option.value"
            >
              {{ option.label }}
            </SelectItem>
          </SelectContent>
        </Select>
      </div>

      <!-- Type de bien avec Select -->
      <div class="space-y-2">
        <Label>Type de bien</Label>
        <Select
          :model-value="typeValue"
          @update:model-value="handleTypeChange"
        >
          <SelectTrigger class="w-full">
            <SelectValue :placeholder="typeValue || 'Tous'" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem
              v-for="option in typeOptions"
              :key="option.value"
              :value="option.value"
            >
              {{ option.label }}
            </SelectItem>
          </SelectContent>
        </Select>
      </div>

      <!-- Trier par avec Select -->
      <div class="space-y-2">
        <Label>Trier par</Label>
        <Select
          :model-value="sortByValue"
          @update:model-value="handleSortByChange"
        >
          <SelectTrigger class="w-full">
            <SelectValue :placeholder="getSortLabel()" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem
              v-for="option in sortOptions"
              :key="option.value"
              :value="option.value"
            >
              {{ option.label }}
            </SelectItem>
          </SelectContent>
        </Select>
      </div>

      <!-- Date de publication avec Select -->
      <div v-if="showDateFilter" class="space-y-2">
        <Label>Date de publication</Label>
        <Select
          :model-value="dateFilterValue"
          @update:model-value="handleDateFilterChange"
        >
          <SelectTrigger class="w-full">
            <SelectValue :placeholder="getDateLabel()" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem
              v-for="option in dateOptions"
              :key="option.value"
              :value="option.value"
            >
              {{ option.label }}
            </SelectItem>
          </SelectContent>
        </Select>
      </div>

      <Separator />

      <!-- Prix avec Slider -->
      <div class="space-y-3">
        <div class="flex items-center justify-between">
          <Label>Prix maximum</Label>
          <span class="text-sm text-muted-foreground">{{ maxPriceValue[0] > 0 ? `${maxPriceValue[0].toLocaleString('fr-FR')} €` : 'Illimité' }}</span>
        </div>
        <Slider
          v-model="maxPriceValue"
          :max="10000000"
          :step="10000"
          class="w-full"
        />
        <div class="flex justify-between text-xs text-muted-foreground">
          <span>0 €</span>
          <span>10M €</span>
        </div>
      </div>

      <!-- Surface minimum avec Slider -->
      <div class="space-y-3">
        <div class="flex items-center justify-between">
          <Label>Surface minimum</Label>
          <span class="text-sm text-muted-foreground">{{ minSurfaceValue[0] > 0 ? `${minSurfaceValue[0]} m²` : 'Aucun' }}</span>
        </div>
        <Slider
          v-model="minSurfaceValue"
          :max="500"
          :step="5"
          class="w-full"
        />
        <div class="flex justify-between text-xs text-muted-foreground">
          <span>0 m²</span>
          <span>500 m²</span>
        </div>
      </div>

      <!-- Nombre de chambres avec Checkbox multi-select -->
      <div class="space-y-2">
        <Label>Nombre de chambres</Label>
        <div class="space-y-2">
          <div
            v-for="bedroom in bedroomOptions"
            :key="bedroom.value"
            class="flex items-center space-x-2"
          >
            <Checkbox
              :id="`bedroom-${bedroom.value}`"
              :checked="selectedBedrooms.includes(bedroom.value)"
              @update:checked="handleBedroomToggle(bedroom.value, $event)"
            />
            <Label
              :for="`bedroom-${bedroom.value}`"
              class="text-sm font-normal cursor-pointer"
            >
              {{ bedroom.label }}
            </Label>
          </div>
        </div>
      </div>

      <!-- Salles de bain avec Checkbox multi-select -->
      <div class="space-y-2">
        <Label>Salles de bain</Label>
        <div class="space-y-2">
          <div
            v-for="bathroom in bathroomOptions"
            :key="bathroom.value"
            class="flex items-center space-x-2"
          >
            <Checkbox
              :id="`bathroom-${bathroom.value}`"
              :checked="selectedBathrooms.includes(bathroom.value)"
              @update:checked="handleBathroomToggle(bathroom.value, $event)"
            />
            <Label
              :for="`bathroom-${bathroom.value}`"
              class="text-sm font-normal cursor-pointer"
            >
              {{ bathroom.label }}
            </Label>
          </div>
        </div>
      </div>
    </CardContent>
  </Card>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Label } from '@/components/ui/label'
import { Checkbox } from '@/components/ui/checkbox'
import { Slider } from '@/components/ui/slider'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { Separator } from '@/components/ui/separator'

const props = defineProps<{
  transactionType?: string
  type?: string
  status?: string
  sortBy?: string
  maxPrice?: number | null
  minSurface?: number | null
  bedrooms?: number | null
  bathrooms?: number | null
  dateFilter?: string
  showDateFilter?: boolean
}>()

const emit = defineEmits<{
  'update:transactionType': [value: string]
  'update:type': [value: string]
  'update:status': [value: string]
  'update:sortBy': [value: string]
  'update:maxPrice': [value: number | null]
  'update:minSurface': [value: number | null]
  'update:bedrooms': [value: number | null]
  'update:bathrooms': [value: number | null]
  'update:dateFilter': [value: string]
  'clear-filters': []
}>()

// Valeurs locales
const combinedFilterValue = ref('all')
const typeValue = ref('Tous')
const sortByValue = ref('default')
const dateFilterValue = ref('all')
const maxPriceValue = ref<number[]>([0])
const minSurfaceValue = ref<number[]>([0])
const selectedBedrooms = ref<number[]>([])
const selectedBathrooms = ref<number[]>([])

// Options
const transactionStatusOptions = [
  { value: 'all', label: 'Tous' },
  { value: 'location-available', label: '📍 Location disponible' },
  { value: 'location-rented', label: '📍 Location louée' },
  { value: 'sale-available', label: '💰 Vente disponible' },
  { value: 'sale-sold', label: '💰 Vente vendue' },
]

const typeOptions = [
  { value: 'Tous', label: 'Tous' },
  { value: 'Appartement', label: 'Appartement' },
  { value: 'Villa', label: 'Villa' },
  { value: 'Studio', label: 'Studio' },
]

const sortOptions = [
  { value: 'default', label: 'Par défaut' },
  { value: 'created-desc', label: 'Plus récentes' },
  { value: 'created-asc', label: 'Plus anciennes' },
  { value: 'price-asc', label: 'Prix croissant' },
  { value: 'price-desc', label: 'Prix décroissant' },
  { value: 'surface-asc', label: 'Surface croissante' },
  { value: 'surface-desc', label: 'Surface décroissante' },
  { value: 'rating-desc', label: 'Meilleures notes' },
]

const dateOptions = [
  { value: 'all', label: 'Toutes les dates' },
  { value: 'today', label: 'Aujourd\'hui' },
  { value: 'week', label: 'Cette semaine' },
  { value: 'month', label: 'Ce mois' },
  { value: '3months', label: '3 derniers mois' },
]

const bedroomOptions = [
  { value: 1, label: '1+ chambre' },
  { value: 2, label: '2+ chambres' },
  { value: 3, label: '3+ chambres' },
  { value: 4, label: '4+ chambres' },
  { value: 5, label: '5+ chambres' },
]

const bathroomOptions = [
  { value: 1, label: '1+ salle de bain' },
  { value: 2, label: '2+ salles de bain' },
  { value: 3, label: '3+ salles de bain' },
  { value: 4, label: '4+ salles de bain' },
]


// Synchroniser les valeurs avec les props
watch(() => props.transactionType, () => {
  combinedFilterValue.value = getCombinedFilterValue()
}, { immediate: true })

watch(() => props.status, () => {
  combinedFilterValue.value = getCombinedFilterValue()
}, { immediate: true })

watch(() => props.type, () => {
  typeValue.value = props.type || 'Tous'
}, { immediate: true })

watch(() => props.sortBy, () => {
  sortByValue.value = props.sortBy || 'default'
}, { immediate: true })

watch(() => props.dateFilter, () => {
  dateFilterValue.value = props.dateFilter || 'all'
}, { immediate: true })

watch(() => props.maxPrice, () => {
  maxPriceValue.value = props.maxPrice ? [props.maxPrice] : [0]
}, { immediate: true })

watch(maxPriceValue, (newValue) => {
  emit('update:maxPrice', newValue[0] > 0 ? newValue[0] : null)
})

watch(() => props.minSurface, () => {
  minSurfaceValue.value = props.minSurface ? [props.minSurface] : [0]
}, { immediate: true })

watch(minSurfaceValue, (newValue) => {
  emit('update:minSurface', newValue[0] > 0 ? newValue[0] : null)
})

watch(() => props.bedrooms, () => {
  if (props.bedrooms) {
    selectedBedrooms.value = [props.bedrooms]
  } else {
    selectedBedrooms.value = []
  }
}, { immediate: true })

watch(() => props.bathrooms, () => {
  if (props.bathrooms) {
    selectedBathrooms.value = [props.bathrooms]
  } else {
    selectedBathrooms.value = []
  }
}, { immediate: true })

const hasActiveFilters = computed(() => {
  const hasCombinedFilter = combinedFilterValue.value !== 'all'
  return !!(
    maxPriceValue.value[0] > 0 ||
    minSurfaceValue.value[0] > 0 ||
    selectedBedrooms.value.length > 0 ||
    selectedBathrooms.value.length > 0 ||
    hasCombinedFilter ||
    (typeValue.value && typeValue.value !== 'Tous') ||
    (sortByValue.value && sortByValue.value !== 'default')
  )
})

function getCombinedFilterValue(): string {
  if (!props.transactionType || props.transactionType === 'Tous') {
    if (!props.status || props.status === 'Tous') {
      return 'all'
    }
    if (props.status === 'Loué') return 'location-rented'
    if (props.status === 'Vendu') return 'sale-sold'
    if (props.status === 'Disponible') {
      return 'location-available'
    }
  } else {
    if (props.transactionType === 'Location') {
      if (props.status === 'Loué') return 'location-rented'
      return 'location-available'
    } else if (props.transactionType === 'Vente') {
      if (props.status === 'Vendu') return 'sale-sold'
      return 'sale-available'
    }
  }
  return 'all'
}

function getCombinedFilterLabel(): string {
  const option = transactionStatusOptions.find(opt => opt.value === combinedFilterValue.value)
  return option?.label || 'Tous'
}

function getSortLabel(): string {
  const option = sortOptions.find(opt => opt.value === sortByValue.value)
  return option?.label || 'Par défaut'
}

function getDateLabel(): string {
  const option = dateOptions.find(opt => opt.value === dateFilterValue.value)
  return option?.label || 'Toutes les dates'
}

function handleCombinedFilterChange(value: any) {
  const stringValue = String(value || '')
  if (!stringValue) return
  combinedFilterValue.value = stringValue
  if (stringValue === 'all') {
    emit('update:transactionType', 'Tous')
    emit('update:status', 'Tous')
  } else if (stringValue === 'location-available') {
    emit('update:transactionType', 'Location')
    emit('update:status', 'Disponible')
  } else if (stringValue === 'location-rented') {
    emit('update:transactionType', 'Location')
    emit('update:status', 'Loué')
  } else if (stringValue === 'sale-available') {
    emit('update:transactionType', 'Vente')
    emit('update:status', 'Disponible')
  } else if (stringValue === 'sale-sold') {
    emit('update:transactionType', 'Vente')
    emit('update:status', 'Vendu')
  }
}

function handleTypeChange(value: any) {
  const stringValue = String(value || '')
  if (!stringValue) return
  typeValue.value = stringValue
  emit('update:type', stringValue)
}

function handleSortByChange(value: any) {
  const stringValue = String(value || '')
  if (!stringValue) return
  sortByValue.value = stringValue
  emit('update:sortBy', stringValue)
}

function handleDateFilterChange(value: any) {
  const stringValue = String(value || 'all')
  dateFilterValue.value = stringValue
  // Ne pas émettre si c'est 'all' (toutes les dates)
  emit('update:dateFilter', stringValue === 'all' ? '' : stringValue)
}


function handleBedroomToggle(value: number, checked: boolean) {
  if (checked) {
    selectedBedrooms.value = [value]
    emit('update:bedrooms', value)
  } else {
    selectedBedrooms.value = []
    emit('update:bedrooms', null)
  }
}

function handleBathroomToggle(value: number, checked: boolean) {
  if (checked) {
    selectedBathrooms.value = [value]
    emit('update:bathrooms', value)
  } else {
    selectedBathrooms.value = []
    emit('update:bathrooms', null)
  }
}
</script>
