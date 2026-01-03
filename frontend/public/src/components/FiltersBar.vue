    <template>
      <div class="bg-white dark:bg-gray-800 rounded-2xl shadow-sm p-5 space-y-4">
        <!-- Search avec autocomplete -->
        <div class="relative">
          <SearchAutocomplete
            :model-value="props.query || ''"
            @update:model-value="handleQueryInput"
            placeholder="Rechercher une ville, un quartier, un type…"
          />
          <button
            v-if="hasAdvancedSearch"
            class="absolute right-2 top-1/2 -translate-y-1/2 text-xs text-blue-600 dark:text-blue-400 hover:text-blue-700 dark:hover:text-blue-300"
            title="Recherche avancée active"
          >
            ⚡
          </button>
        </div>

    <!-- Filters Row 1: Type, Status, Sort -->
    <div class="flex gap-3 flex-wrap">
      <select
        class="px-4 py-2 rounded-full border border-gray-300 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
        :value="props.type"
        @change="handleTypeChange"
      >
        <option>Tous</option>
        <option>Appartement</option>
        <option>Villa</option>
        <option>Studio</option>
      </select>

      <select
        class="px-4 py-2 rounded-full border border-gray-300 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
        :value="props.status"
        @change="handleStatusChange"
      >
        <option>Tous</option>
        <option>Disponible</option>
        <option>Loué</option>
        <option>Vendu</option>
      </select>

      <select
        class="px-4 py-2 rounded-full border border-gray-300 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
        :value="props.sortBy"
        @change="handleSortByChange"
      >
        <option value="default">Trier par</option>
        <option value="created-desc">Plus récentes</option>
        <option value="created-asc">Plus anciennes</option>
        <option value="price-asc">Prix croissant</option>
        <option value="price-desc">Prix décroissant</option>
        <option value="surface-asc">Surface croissante</option>
        <option value="surface-desc">Surface décroissante</option>
        <option value="rating-desc">Meilleures notes</option>
      </select>
      
      <select
        v-if="showDateFilter"
        class="px-4 py-2 rounded-full border border-gray-300 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
        :value="props.dateFilter || ''"
        @change="handleDateFilterChange"
      >
        <option value="">Toutes les dates</option>
        <option value="today">Aujourd'hui</option>
        <option value="week">Cette semaine</option>
        <option value="month">Ce mois</option>
        <option value="3months">3 derniers mois</option>
      </select>
    </div>

    <!-- Filters Row 2: Price Range, Surface, Bedrooms, Bathrooms -->
    <div class="flex gap-3 flex-wrap items-center">
      <div class="flex items-center gap-2">
        <label class="text-sm text-gray-600 whitespace-nowrap">Prix max:</label>
        <input
          type="number"
          class="px-3 py-2 rounded-full border border-gray-300 text-sm w-24 focus:outline-none focus:ring-2 focus:ring-blue-500"
          :value="props.maxPrice ?? ''"
          @input="handleMaxPriceInput"
          placeholder="€"
          min="0"
        />
      </div>

      <div class="flex items-center gap-2">
        <label class="text-sm text-gray-600 whitespace-nowrap">Surface min:</label>
        <input
          type="number"
          class="px-3 py-2 rounded-full border border-gray-300 text-sm w-24 focus:outline-none focus:ring-2 focus:ring-blue-500"
          :value="props.minSurface ?? ''"
          @input="handleMinSurfaceInput"
          placeholder="m²"
          min="0"
        />
      </div>

      <div class="flex items-center gap-2">
        <label class="text-sm text-gray-600 whitespace-nowrap">Chambres:</label>
        <select
          class="px-3 py-2 rounded-full border border-gray-300 text-sm w-20 focus:outline-none focus:ring-2 focus:ring-blue-500"
          :value="props.bedrooms ?? ''"
          @change="handleBedroomsChange"
        >
          <option value="">Tous</option>
          <option value="1">1+</option>
          <option value="2">2+</option>
          <option value="3">3+</option>
          <option value="4">4+</option>
          <option value="5">5+</option>
        </select>
      </div>

      <div class="flex items-center gap-2">
        <label class="text-sm text-gray-600 whitespace-nowrap">Salles de bain:</label>
        <select
          class="px-3 py-2 rounded-full border border-gray-300 text-sm w-20 focus:outline-none focus:ring-2 focus:ring-blue-500"
          :value="props.bathrooms ?? ''"
          @change="handleBathroomsChange"
        >
          <option value="">Tous</option>
          <option value="1">1+</option>
          <option value="2">2+</option>
          <option value="3">3+</option>
          <option value="4">4+</option>
        </select>
      </div>

      <button
        v-if="hasActiveFilters"
        @click="$emit('clear-filters')"
        class="px-4 py-2 rounded-full border border-gray-300 text-sm text-gray-600 hover:bg-gray-50 transition-colors"
      >
        Réinitialiser
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import SearchAutocomplete from './SearchAutocomplete.vue'

const props = defineProps<{
  query?: string
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
  'update:query': [value: string]
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

const hasActiveFilters = computed(() => {
  return !!(props.maxPrice || props.minSurface || props.bedrooms || props.bathrooms || 
           (props.query && props.query.trim()) || 
           (props.type && props.type !== 'Tous') || 
           (props.status && props.status !== 'Tous') ||
           (props.sortBy && props.sortBy !== 'default'))
})

const hasAdvancedSearch = computed(() => {
  if (!props.query) return false
  return props.query.includes('"') || props.query.includes('-')
})

function handleQueryInput(value: string) {
  emit('update:query', value)
}

function handleTypeChange(event: Event) {
  const target = event.target as HTMLSelectElement
  emit('update:type', target.value)
}

function handleStatusChange(event: Event) {
  const target = event.target as HTMLSelectElement
  emit('update:status', target.value)
}

function handleSortByChange(event: Event) {
  const target = event.target as HTMLSelectElement
  emit('update:sortBy', target.value)
}

function handleMaxPriceInput(event: Event) {
  const target = event.target as HTMLInputElement
  const value = target.value ? Number(target.value) : null
  emit('update:maxPrice', value)
}

function handleMinSurfaceInput(event: Event) {
  const target = event.target as HTMLInputElement
  const value = target.value ? Number(target.value) : null
  emit('update:minSurface', value)
}

function handleBedroomsChange(event: Event) {
  const target = event.target as HTMLSelectElement
  const value = target.value ? Number(target.value) : null
  emit('update:bedrooms', value)
}

function handleBathroomsChange(event: Event) {
  const target = event.target as HTMLSelectElement
  const value = target.value ? Number(target.value) : null
  emit('update:bathrooms', value)
}

function handleDateFilterChange(event: Event) {
  const target = event.target as HTMLSelectElement
  emit('update:dateFilter', target.value)
}
</script>
  