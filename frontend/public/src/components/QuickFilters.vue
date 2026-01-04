<template>
  <div class="bg-white rounded-lg shadow-sm p-4 mb-6">
    <div class="flex items-center justify-between mb-4">
      <h3 class="text-sm font-semibold text-gray-900">Filtres rapides</h3>
      <button
        v-if="hasActiveFilters"
        @click="clearAll"
        class="text-xs text-blue-600 hover:text-blue-700 font-medium"
      >
        Réinitialiser
      </button>
    </div>

    <div class="flex flex-wrap gap-2">
      <!-- Prix -->
      <button
        v-for="priceFilter in priceFilters"
        :key="priceFilter.label"
        @click="togglePriceFilter(priceFilter)"
        class="px-3 py-1.5 rounded-full text-xs font-medium transition-colors"
        :class="isPriceFilterActive(priceFilter)
          ? 'bg-blue-600 text-white'
          : 'bg-gray-100 text-gray-700 hover:bg-gray-200'"
      >
        {{ priceFilter.label }}
      </button>

      <!-- Surface -->
      <button
        v-for="surfaceFilter in surfaceFilters"
        :key="surfaceFilter.label"
        @click="toggleSurfaceFilter(surfaceFilter)"
        class="px-3 py-1.5 rounded-full text-xs font-medium transition-colors"
        :class="isSurfaceFilterActive(surfaceFilter)
          ? 'bg-blue-600 text-white'
          : 'bg-gray-100 text-gray-700 hover:bg-gray-200'"
      >
        {{ surfaceFilter.label }}
      </button>

      <!-- Chambres -->
      <button
        v-for="bedroomFilter in bedroomFilters"
        :key="bedroomFilter.label"
        @click="toggleBedroomFilter(bedroomFilter)"
        class="px-3 py-1.5 rounded-full text-xs font-medium transition-colors"
        :class="isBedroomFilterActive(bedroomFilter)
          ? 'bg-blue-600 text-white'
          : 'bg-gray-100 text-gray-700 hover:bg-gray-200'"
      >
        {{ bedroomFilter.label }}
      </button>

      <!-- Type -->
      <button
        v-for="typeFilter in typeFilters"
        :key="typeFilter.label"
        @click="toggleTypeFilter(typeFilter)"
        class="px-3 py-1.5 rounded-full text-xs font-medium transition-colors"
        :class="isTypeFilterActive(typeFilter)
          ? 'bg-blue-600 text-white'
          : 'bg-gray-100 text-gray-700 hover:bg-gray-200'"
      >
        {{ typeFilter.label }}
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Filter {
  label: string
  min?: number
  max?: number
  value?: string | number
}

const props = defineProps<{
  minPrice?: number | null
  maxPrice?: number | null
  minSurface?: number | null
  maxSurface?: number | null
  bedrooms?: number | null
  type?: string
}>()

const emit = defineEmits<{
  'update:minPrice': [value: number | null]
  'update:maxPrice': [value: number | null]
  'update:minSurface': [value: number | null]
  'update:maxSurface': [value: number | null]
  'update:bedrooms': [value: number | null]
  'update:type': [value: string]
  'clear': []
}>()

const priceFilters: Filter[] = [
  { label: 'Moins de 500€', max: 500 },
  { label: '500€ - 1000€', min: 500, max: 1000 },
  { label: '1000€ - 2000€', min: 1000, max: 2000 },
  { label: '2000€ - 3000€', min: 2000, max: 3000 },
  { label: 'Plus de 3000€', min: 3000 },
]

const surfaceFilters: Filter[] = [
  { label: 'Moins de 50m²', max: 50 },
  { label: '50m² - 100m²', min: 50, max: 100 },
  { label: '100m² - 150m²', min: 100, max: 150 },
  { label: 'Plus de 150m²', min: 150 },
]

const bedroomFilters: Filter[] = [
  { label: 'Studio', value: 0 },
  { label: '1 chambre', value: 1 },
  { label: '2 chambres', value: 2 },
  { label: '3+ chambres', value: 3 },
]

const typeFilters: Filter[] = [
  { label: 'Appartement', value: 'APARTMENT' },
  { label: 'Maison', value: 'HOUSE' },
  { label: 'Villa', value: 'VILLA' },
  { label: 'Studio', value: 'STUDIO' },
]

const hasActiveFilters = computed(() => {
  return !!(
    props.minPrice ||
    props.maxPrice ||
    props.minSurface ||
    props.maxSurface ||
    props.bedrooms ||
    props.type
  )
})

function isPriceFilterActive(filter: Filter): boolean {
  if (filter.min !== undefined && filter.max !== undefined) {
    return props.minPrice === filter.min && props.maxPrice === filter.max
  }
  if (filter.max !== undefined) {
    return props.maxPrice === filter.max && !props.minPrice
  }
  if (filter.min !== undefined) {
    return props.minPrice === filter.min && !props.maxPrice
  }
  return false
}

function isSurfaceFilterActive(filter: Filter): boolean {
  if (filter.min !== undefined && filter.max !== undefined) {
    return props.minSurface === filter.min && props.maxSurface === filter.max
  }
  if (filter.max !== undefined) {
    return props.maxSurface === filter.max && !props.minSurface
  }
  if (filter.min !== undefined) {
    return props.minSurface === filter.min && !props.maxSurface
  }
  return false
}

function isBedroomFilterActive(filter: Filter): boolean {
  return props.bedrooms === filter.value
}

function isTypeFilterActive(filter: Filter): boolean {
  return props.type === filter.value
}

function togglePriceFilter(filter: Filter) {
  if (isPriceFilterActive(filter)) {
    emit('update:minPrice', null)
    emit('update:maxPrice', null)
  } else {
    emit('update:minPrice', filter.min ?? null)
    emit('update:maxPrice', filter.max ?? null)
  }
}

function toggleSurfaceFilter(filter: Filter) {
  if (isSurfaceFilterActive(filter)) {
    emit('update:minSurface', null)
    emit('update:maxSurface', null)
  } else {
    emit('update:minSurface', filter.min ?? null)
    emit('update:maxSurface', filter.max ?? null)
  }
}

function toggleBedroomFilter(filter: Filter) {
  if (isBedroomFilterActive(filter)) {
    emit('update:bedrooms', null)
  } else {
    emit('update:bedrooms', filter.value as number)
  }
}

function toggleTypeFilter(filter: Filter) {
  if (isTypeFilterActive(filter)) {
    emit('update:type', 'Tous')
  } else {
    emit('update:type', filter.value as string)
  }
}

function clearAll() {
  emit('update:minPrice', null)
  emit('update:maxPrice', null)
  emit('update:minSurface', null)
  emit('update:maxSurface', null)
  emit('update:bedrooms', null)
  emit('update:type', 'Tous')
  emit('clear')
}
</script>
