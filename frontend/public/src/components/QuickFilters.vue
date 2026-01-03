<template>
  <div class="bg-white rounded-xl shadow-sm p-4">
    <div class="flex items-center justify-between mb-3">
      <h3 class="text-sm font-semibold text-gray-700">Filtres rapides</h3>
      <button
        v-if="hasActiveFilters"
        @click="clearAll"
        class="text-xs text-blue-600 hover:text-blue-700 font-medium"
      >
        Tout effacer
      </button>
    </div>
    
    <div class="flex flex-wrap gap-2">
      <!-- Filtres par prix -->
      <button
        v-for="filter in priceFilters"
        :key="filter.key"
        @click="toggleFilter('price', filter)"
        class="px-3 py-1.5 rounded-full text-sm font-medium transition-colors"
        :class="isActive('price', filter.key)
          ? 'bg-blue-600 text-white'
          : 'bg-gray-100 text-gray-700 hover:bg-gray-200'"
      >
        {{ filter.label }}
      </button>
      
      <!-- Filtres par type -->
      <button
        v-for="filter in typeFilters"
        :key="filter.key"
        @click="toggleFilter('type', filter)"
        class="px-3 py-1.5 rounded-full text-sm font-medium transition-colors"
        :class="isActive('type', filter.key)
          ? 'bg-blue-600 text-white'
          : 'bg-gray-100 text-gray-700 hover:bg-gray-200'"
      >
        {{ filter.label }}
      </button>
      
      <!-- Filtres par surface -->
      <button
        v-for="filter in surfaceFilters"
        :key="filter.key"
        @click="toggleFilter('surface', filter)"
        class="px-3 py-1.5 rounded-full text-sm font-medium transition-colors"
        :class="isActive('surface', filter.key)
          ? 'bg-blue-600 text-white'
          : 'bg-gray-100 text-gray-700 hover:bg-gray-200'"
      >
        {{ filter.label }}
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  activeFilters?: {
    maxPrice?: number | null
    type?: string
    minSurface?: number | null
  }
}>()

const emit = defineEmits<{
  'filter-change': [filter: { type: string; key: string; value: any }]
  'clear-all': []
}>()

const priceFilters = [
  { key: '0-500', label: 'Moins de 500€', maxPrice: 500 },
  { key: '500-1000', label: '500€ - 1000€', minPrice: 500, maxPrice: 1000 },
  { key: '1000-2000', label: '1000€ - 2000€', minPrice: 1000, maxPrice: 2000 },
  { key: '2000-3000', label: '2000€ - 3000€', minPrice: 2000, maxPrice: 3000 },
  { key: '3000+', label: 'Plus de 3000€', minPrice: 3000 },
]

const typeFilters = [
  { key: 'Appartement', label: 'Appartement' },
  { key: 'Villa', label: 'Villa' },
  { key: 'Studio', label: 'Studio' },
  { key: 'Maison', label: 'Maison' },
]

const surfaceFilters = [
  { key: '0-50', label: 'Moins de 50m²', maxSurface: 50 },
  { key: '50-100', label: '50m² - 100m²', minSurface: 50, maxSurface: 100 },
  { key: '100-150', label: '100m² - 150m²', minSurface: 100, maxSurface: 150 },
  { key: '150+', label: 'Plus de 150m²', minSurface: 150 },
]

const hasActiveFilters = computed(() => {
  return !!(props.activeFilters?.maxPrice || props.activeFilters?.type || props.activeFilters?.minSurface)
})

function isActive(filterType: string, key: string): boolean {
  if (!props.activeFilters) return false
  
  switch (filterType) {
    case 'price':
      const priceFilter = priceFilters.find(f => f.key === key)
      if (!priceFilter) return false
      if (priceFilter.maxPrice && props.activeFilters.maxPrice === priceFilter.maxPrice) return true
      if (priceFilter.minPrice && props.activeFilters.maxPrice && props.activeFilters.maxPrice >= priceFilter.minPrice) return true
      return false
    case 'type':
      return props.activeFilters.type === key
    case 'surface':
      const surfaceFilter = surfaceFilters.find(f => f.key === key)
      if (!surfaceFilter) return false
      if (surfaceFilter.maxSurface && props.activeFilters.minSurface === surfaceFilter.maxSurface) return true
      if (surfaceFilter.minSurface && props.activeFilters.minSurface && props.activeFilters.minSurface >= surfaceFilter.minSurface) return true
      return false
    default:
      return false
  }
}

function toggleFilter(filterType: string, filter: any) {
  emit('filter-change', { type: filterType, key: filter.key, value: filter })
}

function clearAll() {
  emit('clear-all')
}
</script>

