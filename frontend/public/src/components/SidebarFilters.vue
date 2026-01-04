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
      <!-- Pays avec Combobox -->
      <div class="space-y-2">
        <Label>Pays</Label>
        <Combobox
          :model-value="countryValue === 'all' ? null : countryValue"
          :search-value="countrySearch"
          @update:search-value="countrySearch = $event"
          @update:model-value="handleCountryChange"
        >
          <ComboboxAnchor as-child class="w-full">
            <ComboboxInput placeholder="Rechercher un pays..." class="w-full" />
          </ComboboxAnchor>
          <ComboboxList class="w-full">
            <ComboboxItem value="all">
              <Check v-if="countryValue === 'all'" class="h-4 w-4" />
              <span>Tous les pays</span>
            </ComboboxItem>
            <ComboboxItem
              v-for="option in filteredCountryOptions"
              :key="option.value"
              :value="option.value"
            >
              <Check v-if="countryValue === option.value" class="h-4 w-4" />
              <span>{{ option.label }}</span>
            </ComboboxItem>
            <ComboboxEmpty>Aucun pays trouvé</ComboboxEmpty>
          </ComboboxList>
        </Combobox>
      </div>

      <!-- Ville avec Combobox -->
      <div class="space-y-2">
        <Label>Ville</Label>
        <Combobox
          :model-value="cityValue === 'all' ? null : cityValue"
          :search-value="citySearch"
          @update:search-value="citySearch = $event"
          @update:model-value="handleCityChange"
        >
          <ComboboxAnchor as-child class="w-full">
            <ComboboxInput placeholder="Rechercher une ville..." class="w-full" />
          </ComboboxAnchor>
          <ComboboxList class="w-full">
            <ComboboxItem value="all">
              <Check v-if="cityValue === 'all'" class="h-4 w-4" />
              <span>Toutes les villes</span>
            </ComboboxItem>
            <ComboboxItem
              v-for="option in filteredCityOptions"
              :key="option.value"
              :value="option.value"
            >
              <Check v-if="cityValue === option.value" class="h-4 w-4" />
              <span>{{ option.label }}</span>
            </ComboboxItem>
            <ComboboxEmpty>Aucune ville trouvée</ComboboxEmpty>
          </ComboboxList>
        </Combobox>
      </div>

      <Separator />

      <!-- Transaction / Statut avec Combobox -->
      <div class="space-y-2">
        <Label>Transaction / Statut</Label>
        <Combobox
          :model-value="combinedFilterValue === 'all' ? null : combinedFilterValue"
          :search-value="transactionStatusSearch"
          @update:search-value="transactionStatusSearch = $event"
          @update:model-value="handleCombinedFilterChange"
        >
          <ComboboxAnchor as-child class="w-full">
            <ComboboxInput :placeholder="getCombinedFilterLabel()" class="w-full" />
          </ComboboxAnchor>
          <ComboboxList class="w-full">
            <ComboboxItem
              v-for="option in filteredTransactionStatusOptions"
              :key="option.value"
              :value="option.value"
            >
              <Check v-if="combinedFilterValue === option.value" class="h-4 w-4" />
              <span>{{ option.label }}</span>
            </ComboboxItem>
            <ComboboxEmpty>Aucun résultat</ComboboxEmpty>
          </ComboboxList>
        </Combobox>
      </div>

      <!-- Type de bien avec Combobox -->
      <div class="space-y-2">
        <Label>Type de bien</Label>
        <Combobox
          :model-value="typeValue === 'Tous' ? null : typeValue"
          :search-value="typeSearch"
          @update:search-value="typeSearch = $event"
          @update:model-value="handleTypeChange"
        >
          <ComboboxAnchor as-child class="w-full">
            <ComboboxInput :placeholder="typeValue || 'Tous'" class="w-full" />
          </ComboboxAnchor>
          <ComboboxList class="w-full">
            <ComboboxItem
              v-for="option in filteredTypeOptions"
              :key="option.value"
              :value="option.value"
            >
              <Check v-if="typeValue === option.value" class="h-4 w-4" />
              <span>{{ option.label }}</span>
            </ComboboxItem>
            <ComboboxEmpty>Aucun type trouvé</ComboboxEmpty>
          </ComboboxList>
        </Combobox>
      </div>

      <!-- Trier par avec Combobox -->
      <div class="space-y-2">
        <Label>Trier par</Label>
        <Combobox
          :model-value="sortByValue === 'default' ? null : sortByValue"
          :search-value="sortSearch"
          @update:search-value="sortSearch = $event"
          @update:model-value="handleSortByChange"
        >
          <ComboboxAnchor as-child class="w-full">
            <ComboboxInput :placeholder="getSortLabel()" class="w-full" />
          </ComboboxAnchor>
          <ComboboxList class="w-full">
            <ComboboxItem
              v-for="option in filteredSortOptions"
              :key="option.value"
              :value="option.value"
            >
              <Check v-if="sortByValue === option.value" class="h-4 w-4" />
              <span>{{ option.label }}</span>
            </ComboboxItem>
            <ComboboxEmpty>Aucun résultat</ComboboxEmpty>
          </ComboboxList>
        </Combobox>
      </div>

      <!-- Date de publication avec Combobox -->
      <div v-if="showDateFilter" class="space-y-2">
        <Label>Date de publication</Label>
        <Combobox
          :model-value="dateFilterValue === 'all' ? null : dateFilterValue"
          :search-value="dateSearch"
          @update:search-value="dateSearch = $event"
          @update:model-value="handleDateFilterChange"
        >
          <ComboboxAnchor as-child class="w-full">
            <ComboboxInput :placeholder="getDateLabel()" class="w-full" />
          </ComboboxAnchor>
          <ComboboxList class="w-full">
            <ComboboxItem
              v-for="option in filteredDateOptions"
              :key="option.value"
              :value="option.value"
            >
              <Check v-if="dateFilterValue === option.value" class="h-4 w-4" />
              <span>{{ option.label }}</span>
            </ComboboxItem>
            <ComboboxEmpty>Aucun résultat</ComboboxEmpty>
          </ComboboxList>
        </Combobox>
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
  Combobox,
  ComboboxAnchor,
  ComboboxInput,
  ComboboxList,
  ComboboxItem,
  ComboboxEmpty,
} from '@/components/ui/combobox'
import { Separator } from '@/components/ui/separator'
import { Check } from 'lucide-vue-next'

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
  country?: string
  city?: string
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
  'update:country': [value: string]
  'update:city': [value: string]
  'clear-filters': []
}>()

// Valeurs locales
const combinedFilterValue = ref('all')
const typeValue = ref('Tous')
const sortByValue = ref('default')
const dateFilterValue = ref('all')
const countryValue = ref('all')
const cityValue = ref('all')
const maxPriceValue = ref<number[]>([0])
const minSurfaceValue = ref<number[]>([0])
const selectedBedrooms = ref<number[]>([])
const selectedBathrooms = ref<number[]>([])

// Valeurs de recherche pour les Combobox
const countrySearch = ref('')
const citySearch = ref('')
const transactionStatusSearch = ref('')
const typeSearch = ref('')
const sortSearch = ref('')
const dateSearch = ref('')

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

const countryOptions = [
  { value: 'FR', label: '🇫🇷 France' },
  { value: 'BE', label: '🇧🇪 Belgique' },
  { value: 'CH', label: '🇨🇭 Suisse' },
  { value: 'LU', label: '🇱🇺 Luxembourg' },
  { value: 'MC', label: '🇲🇨 Monaco' },
  { value: 'ES', label: '🇪🇸 Espagne' },
  { value: 'IT', label: '🇮🇹 Italie' },
  { value: 'PT', label: '🇵🇹 Portugal' },
  { value: 'DE', label: '🇩🇪 Allemagne' },
  { value: 'GB', label: '🇬🇧 Royaume-Uni' },
  { value: 'US', label: '🇺🇸 États-Unis' },
  { value: 'CA', label: '🇨🇦 Canada' },
  { value: 'MA', label: '🇲🇦 Maroc' },
  { value: 'TN', label: '🇹🇳 Tunisie' },
  { value: 'DZ', label: '🇩🇿 Algérie' },
]

const cityOptions = [
  { value: 'Paris', label: 'Paris' },
  { value: 'Lyon', label: 'Lyon' },
  { value: 'Marseille', label: 'Marseille' },
  { value: 'Toulouse', label: 'Toulouse' },
  { value: 'Nice', label: 'Nice' },
  { value: 'Nantes', label: 'Nantes' },
  { value: 'Strasbourg', label: 'Strasbourg' },
  { value: 'Montpellier', label: 'Montpellier' },
  { value: 'Bordeaux', label: 'Bordeaux' },
  { value: 'Lille', label: 'Lille' },
  { value: 'Rennes', label: 'Rennes' },
  { value: 'Reims', label: 'Reims' },
  { value: 'Saint-Étienne', label: 'Saint-Étienne' },
  { value: 'Toulon', label: 'Toulon' },
  { value: 'Le Havre', label: 'Le Havre' },
  { value: 'Grenoble', label: 'Grenoble' },
  { value: 'Dijon', label: 'Dijon' },
  { value: 'Angers', label: 'Angers' },
  { value: 'Nîmes', label: 'Nîmes' },
  { value: 'Villeurbanne', label: 'Villeurbanne' },
  { value: 'Bruxelles', label: 'Bruxelles' },
  { value: 'Anvers', label: 'Anvers' },
  { value: 'Genève', label: 'Genève' },
  { value: 'Zurich', label: 'Zurich' },
  { value: 'Lausanne', label: 'Lausanne' },
  { value: 'Casablanca', label: 'Casablanca' },
  { value: 'Rabat', label: 'Rabat' },
  { value: 'Tunis', label: 'Tunis' },
  { value: 'Alger', label: 'Alger' },
]


// Options filtrées pour les Combobox
const filteredCountryOptions = computed(() => {
  if (!countrySearch.value) return countryOptions
  const search = countrySearch.value.toLowerCase()
  return countryOptions.filter(option => 
    option.label.toLowerCase().includes(search) || 
    option.value.toLowerCase().includes(search)
  )
})

const filteredCityOptions = computed(() => {
  if (!citySearch.value) return cityOptions
  const search = citySearch.value.toLowerCase()
  return cityOptions.filter(option => 
    option.label.toLowerCase().includes(search) || 
    option.value.toLowerCase().includes(search)
  )
})

const filteredTransactionStatusOptions = computed(() => {
  if (!transactionStatusSearch.value) return transactionStatusOptions
  const search = transactionStatusSearch.value.toLowerCase()
  return transactionStatusOptions.filter(option => 
    option.label.toLowerCase().includes(search) || 
    option.value.toLowerCase().includes(search)
  )
})

const filteredTypeOptions = computed(() => {
  if (!typeSearch.value) return typeOptions
  const search = typeSearch.value.toLowerCase()
  return typeOptions.filter(option => 
    option.label.toLowerCase().includes(search) || 
    option.value.toLowerCase().includes(search)
  )
})

const filteredSortOptions = computed(() => {
  if (!sortSearch.value) return sortOptions
  const search = sortSearch.value.toLowerCase()
  return sortOptions.filter(option => 
    option.label.toLowerCase().includes(search) || 
    option.value.toLowerCase().includes(search)
  )
})

const filteredDateOptions = computed(() => {
  if (!dateSearch.value) return dateOptions
  const search = dateSearch.value.toLowerCase()
  return dateOptions.filter(option => 
    option.label.toLowerCase().includes(search) || 
    option.value.toLowerCase().includes(search)
  )
})


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

watch(() => props.country, () => {
  countryValue.value = props.country || 'all'
}, { immediate: true })

watch(() => props.city, () => {
  cityValue.value = props.city || 'all'
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
    (sortByValue.value && sortByValue.value !== 'default') ||
    (countryValue.value && countryValue.value !== 'all') ||
    (cityValue.value && cityValue.value !== 'all')
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
  if (value === null || value === undefined) {
    combinedFilterValue.value = 'all'
    emit('update:transactionType', 'Tous')
    emit('update:status', 'Tous')
    return
  }
  const stringValue = String(value)
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
  if (value === null || value === undefined) {
    typeValue.value = 'Tous'
    emit('update:type', 'Tous')
    return
  }
  const stringValue = String(value)
  typeValue.value = stringValue
  emit('update:type', stringValue)
}

function handleSortByChange(value: any) {
  if (value === null || value === undefined) {
    sortByValue.value = 'default'
    emit('update:sortBy', 'default')
    return
  }
  const stringValue = String(value)
  sortByValue.value = stringValue
  emit('update:sortBy', stringValue)
}

function handleDateFilterChange(value: any) {
  if (value === null || value === undefined || value === 'all') {
    dateFilterValue.value = 'all'
    emit('update:dateFilter', '')
    return
  }
  const stringValue = String(value)
  dateFilterValue.value = stringValue
  emit('update:dateFilter', stringValue)
}

function handleCountryChange(value: any) {
  if (value === null || value === undefined || value === 'all') {
    countryValue.value = 'all'
    emit('update:country', '')
  } else {
    const stringValue = String(value)
    countryValue.value = stringValue
    emit('update:country', stringValue)
  }
}

function handleCityChange(value: any) {
  if (value === null || value === undefined || value === 'all') {
    cityValue.value = 'all'
    emit('update:city', '')
  } else {
    const stringValue = String(value)
    cityValue.value = stringValue
    emit('update:city', stringValue)
  }
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
