<template>
  <div class="bg-white rounded-xl shadow-sm p-5">
    <button
      @click="isOpen = !isOpen"
      class="w-full flex items-center justify-between text-left"
    >
      <h3 class="text-lg font-semibold text-gray-800">Filtres avancés</h3>
      <span class="text-gray-500 transform transition-transform" :class="{ 'rotate-180': isOpen }">
        ▼
      </span>
    </button>
    
    <div v-if="isOpen" class="mt-4 space-y-4 pt-4 border-t border-gray-200">
      <!-- Plage de prix -->
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-2">Plage de prix</label>
        <div class="flex items-center gap-3">
          <input
            type="number"
            :value="minPrice ?? ''"
            @input="handleMinPriceInput"
            placeholder="Prix min"
            class="flex-1 px-3 py-2 rounded-lg border border-gray-300 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
          <span class="text-gray-500">-</span>
          <input
            type="number"
            :value="maxPrice ?? ''"
            @input="handleMaxPriceInput"
            placeholder="Prix max"
            class="flex-1 px-3 py-2 rounded-lg border border-gray-300 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>
      </div>
      
      <!-- Plage de surface -->
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-2">Plage de surface</label>
        <div class="flex items-center gap-3">
          <input
            type="number"
            :value="minSurface ?? ''"
            @input="handleMinSurfaceInput"
            placeholder="Surface min (m²)"
            class="flex-1 px-3 py-2 rounded-lg border border-gray-300 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
          <span class="text-gray-500">-</span>
          <input
            type="number"
            :value="maxSurface ?? ''"
            @input="handleMaxSurfaceInput"
            placeholder="Surface max (m²)"
            class="flex-1 px-3 py-2 rounded-lg border border-gray-300 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>
      </div>
      
      <!-- Chambres et salles de bain -->
      <div class="grid grid-cols-2 gap-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Chambres</label>
          <select
            :value="bedrooms ?? ''"
            @change="handleBedroomsChange"
            class="w-full px-3 py-2 rounded-lg border border-gray-300 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="">Tous</option>
            <option value="1">1+</option>
            <option value="2">2+</option>
            <option value="3">3+</option>
            <option value="4">4+</option>
            <option value="5">5+</option>
          </select>
        </div>
        
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Salles de bain</label>
          <select
            :value="bathrooms ?? ''"
            @change="handleBathroomsChange"
            class="w-full px-3 py-2 rounded-lg border border-gray-300 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="">Tous</option>
            <option value="1">1+</option>
            <option value="2">2+</option>
            <option value="3">3+</option>
            <option value="4">4+</option>
          </select>
        </div>
      </div>
      
      <!-- Boutons d'action -->
      <div class="flex gap-3 pt-2">
        <button
          @click="applyFilters"
          class="flex-1 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors text-sm font-medium"
        >
          Appliquer
        </button>
        <button
          @click="resetFilters"
          class="px-4 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition-colors text-sm font-medium"
        >
          Réinitialiser
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const props = defineProps<{
  minPrice?: number | null
  maxPrice?: number | null
  minSurface?: number | null
  maxSurface?: number | null
  bedrooms?: number | null
  bathrooms?: number | null
}>()

const emit = defineEmits<{
  'update:minPrice': [value: number | null]
  'update:maxPrice': [value: number | null]
  'update:minSurface': [value: number | null]
  'update:maxSurface': [value: number | null]
  'update:bedrooms': [value: number | null]
  'update:bathrooms': [value: number | null]
  'apply': []
  'reset': []
}>()

const isOpen = ref(false)

const localMinPrice = ref<number | null>(props.minPrice ?? null)
const localMaxPrice = ref<number | null>(props.maxPrice ?? null)
const localMinSurface = ref<number | null>(props.minSurface ?? null)
const localMaxSurface = ref<number | null>(props.maxSurface ?? null)
const localBedrooms = ref<number | null>(props.bedrooms ?? null)
const localBathrooms = ref<number | null>(props.bathrooms ?? null)

function handleMinPriceInput(event: Event) {
  const target = event.target as HTMLInputElement
  localMinPrice.value = target.value ? Number(target.value) : null
}

function handleMaxPriceInput(event: Event) {
  const target = event.target as HTMLInputElement
  localMaxPrice.value = target.value ? Number(target.value) : null
}

function handleMinSurfaceInput(event: Event) {
  const target = event.target as HTMLInputElement
  localMinSurface.value = target.value ? Number(target.value) : null
}

function handleMaxSurfaceInput(event: Event) {
  const target = event.target as HTMLInputElement
  localMaxSurface.value = target.value ? Number(target.value) : null
}

function handleBedroomsChange(event: Event) {
  const target = event.target as HTMLSelectElement
  localBedrooms.value = target.value ? Number(target.value) : null
}

function handleBathroomsChange(event: Event) {
  const target = event.target as HTMLSelectElement
  localBathrooms.value = target.value ? Number(target.value) : null
}

function applyFilters() {
  emit('update:minPrice', localMinPrice.value)
  emit('update:maxPrice', localMaxPrice.value)
  emit('update:minSurface', localMinSurface.value)
  emit('update:maxSurface', localMaxSurface.value)
  emit('update:bedrooms', localBedrooms.value)
  emit('update:bathrooms', localBathrooms.value)
  emit('apply')
  isOpen.value = false
}

function resetFilters() {
  localMinPrice.value = null
  localMaxPrice.value = null
  localMinSurface.value = null
  localMaxSurface.value = null
  localBedrooms.value = null
  localBathrooms.value = null
  emit('reset')
}
</script>

