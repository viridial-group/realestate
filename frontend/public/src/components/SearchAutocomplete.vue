<template>
  <div class="relative">
    <div class="relative">
      <Search class="absolute left-4 top-1/2 transform -translate-y-1/2 h-5 w-5 text-gray-400" />
      <input
        ref="inputRef"
        :value="modelValue"
        @input="handleInput"
        @focus="showSuggestions = true"
        @blur="handleBlur"
        @keydown.down.prevent="navigateDown"
        @keydown.up.prevent="navigateUp"
        @keydown.enter.prevent="selectCurrent"
        @keydown.escape="hideSuggestions"
        class="w-full border border-gray-300 rounded-full pl-12 pr-5 py-3 text-base
               bg-white text-gray-900
               focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500
               shadow-sm hover:shadow-md transition-all duration-200
               focus:scale-[1.01] focus:shadow-lg"
        :placeholder="placeholder || 'Rechercher une propriété, une ville, une adresse...'"
      />
    </div>

    <!-- Suggestions style Google -->
    <div
      v-if="showSuggestions && (allSuggestions.length > 0 || history.length > 0 || isLoadingSuggestions)"
      class="absolute z-50 w-full mt-1 bg-white rounded-lg shadow-lg border border-gray-200 max-h-96 overflow-y-auto animate-in fade-in slide-in-from-top-2 duration-200"
    >
      <!-- Loading state -->
      <div v-if="isLoadingSuggestions" class="p-4 text-center text-gray-500 text-sm">
        <Loader2 class="h-5 w-5 animate-spin mx-auto mb-2 text-gray-400" />
        Chargement des suggestions...
      </div>

      <!-- Historique de recherche -->
      <div v-if="history.length > 0 && !modelValue && !isLoadingSuggestions" class="p-2 border-b border-gray-200">
        <div class="text-xs font-medium text-gray-500 px-3 py-2">Recherches récentes</div>
        <div
          v-for="(item, idx) in history"
          :key="idx"
          @click="selectSuggestion(item)"
          class="w-full text-left px-4 py-2.5 hover:bg-gray-100 active:bg-gray-200 flex items-center justify-between group cursor-pointer rounded-md transition-colors duration-150"
        >
          <div class="flex items-center gap-3">
            <Clock class="h-4 w-4 text-gray-400" />
            <span class="text-sm text-gray-700">{{ typeof item === 'string' ? item : item.query }}</span>
            <span v-if="typeof item !== 'string' && item.resultCount" class="text-xs text-gray-400">
              ({{ item.resultCount }} résultats)
            </span>
          </div>
          <button
            @click.stop="removeFromHistory(item)"
            class="opacity-0 group-hover:opacity-100 text-gray-400 hover:text-gray-600 transition-opacity"
            type="button"
          >
            <X class="h-4 w-4" />
          </button>
        </div>
      </div>

      <!-- Suggestions intelligentes style Google -->
      <div v-if="allSuggestions.length > 0 && !isLoadingSuggestions" class="p-2">
        <!-- Villes -->
        <div v-if="suggestionsData.cities.length > 0" class="mb-1">
          <div class="text-xs font-medium text-gray-500 px-3 py-2">Villes</div>
          <button
            v-for="(city, idx) in suggestionsData.cities"
            :key="`city-${idx}`"
            @click="selectSuggestion(city)"
            class="w-full text-left px-4 py-2.5 hover:bg-gray-100 flex items-center gap-3 rounded-md"
            :class="{ 'bg-blue-50': getSuggestionIndex(city) === selectedIndex }"
          >
            <MapPin class="h-4 w-4 text-gray-400" />
            <span class="text-sm text-gray-700">{{ city }}</span>
          </button>
        </div>

        <!-- Types -->
        <div v-if="suggestionsData.types.length > 0" class="mb-1">
          <div class="text-xs font-medium text-gray-500 px-3 py-2">Types</div>
          <button
            v-for="(type, idx) in suggestionsData.types"
            :key="`type-${idx}`"
            @click="selectSuggestion(type)"
            class="w-full text-left px-4 py-2.5 hover:bg-gray-100 flex items-center gap-3 rounded-md"
            :class="{ 'bg-blue-50': getSuggestionIndex(type) === selectedIndex }"
          >
            <Home class="h-4 w-4 text-gray-400" />
            <span class="text-sm text-gray-700">{{ type }}</span>
          </button>
        </div>

        <!-- Adresses -->
        <div v-if="suggestionsData.addresses.length > 0" class="mb-1">
          <div class="text-xs font-medium text-gray-500 px-3 py-2">Adresses</div>
          <button
            v-for="(address, idx) in suggestionsData.addresses"
            :key="`address-${idx}`"
            @click="selectSuggestion(address)"
            class="w-full text-left px-4 py-2.5 hover:bg-gray-100 flex items-center gap-3 rounded-md"
            :class="{ 'bg-blue-50': getSuggestionIndex(address) === selectedIndex }"
          >
            <MapPin class="h-4 w-4 text-gray-400" />
            <span class="text-sm text-gray-700">{{ address }}</span>
          </button>
        </div>

        <!-- Suggestions intelligentes -->
        <div v-if="smartSuggestions.length > 0" class="mb-1">
          <div class="text-xs font-medium text-gray-500 px-3 py-2">Suggestions</div>
          <button
            v-for="(suggestion, idx) in smartSuggestions"
            :key="`smart-${idx}`"
            @click="selectSuggestion(suggestion)"
            class="w-full text-left px-4 py-2.5 hover:bg-gray-100 flex items-center gap-3 rounded-md"
            :class="{ 'bg-blue-50': getSuggestionIndex(suggestion) === selectedIndex }"
          >
            <Lightbulb class="h-4 w-4 text-gray-400" />
            <span class="text-sm text-gray-700">{{ suggestion }}</span>
          </button>
        </div>

        <!-- Recherches populaires -->
        <div v-if="suggestionsData.popularSearches.length > 0 && modelValue" class="mb-1">
          <div class="text-xs font-medium text-gray-500 px-3 py-2">Recherches populaires</div>
          <button
            v-for="(popular, idx) in suggestionsData.popularSearches"
            :key="`popular-${idx}`"
            @click="selectSuggestion(popular)"
            class="w-full text-left px-4 py-2.5 hover:bg-gray-100 flex items-center gap-3 rounded-md"
            :class="{ 'bg-blue-50': getSuggestionIndex(popular) === selectedIndex }"
          >
            <TrendingUp class="h-4 w-4 text-gray-400" />
            <span class="text-sm text-gray-700">{{ popular }}</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { Search, Loader2, Clock, X, MapPin, Home, Lightbulb, TrendingUp } from 'lucide-vue-next'
import { useSearchHistory } from '@/composables/useSearchHistory'
import { useSearchSuggestions } from '@/composables/useSearchSuggestions'

const props = defineProps<{
  modelValue: string
  placeholder?: string
}>()

const emit = defineEmits<{
  'update:modelValue': [value: string]
}>()

const inputRef = ref<HTMLInputElement | null>(null)
const showSuggestions = ref(false)
const selectedIndex = ref(-1)
const { history, addToHistory, removeFromHistory } = useSearchHistory()
const { suggestions: suggestionsData, isLoading: isLoadingSuggestions, loadSuggestions, generateSuggestions } = useSearchSuggestions()

// Charger les suggestions par défaut au montage
onMounted(() => {
  loadSuggestions()
})

// Suggestions intelligentes basées sur la requête
const smartSuggestions = computed(() => {
  if (!props.modelValue || props.modelValue.trim().length < 2) {
    return []
  }
  return generateSuggestions(props.modelValue)
})

// Toutes les suggestions combinées pour la navigation au clavier
const allSuggestions = computed(() => {
  const result: string[] = []
  if (suggestionsData.value.cities.length > 0) {
    result.push(...suggestionsData.value.cities)
  }
  if (suggestionsData.value.types.length > 0) {
    result.push(...suggestionsData.value.types)
  }
  if (suggestionsData.value.addresses.length > 0) {
    result.push(...suggestionsData.value.addresses)
  }
  if (smartSuggestions.value.length > 0) {
    result.push(...smartSuggestions.value)
  }
  if (suggestionsData.value.popularSearches.length > 0 && props.modelValue) {
    result.push(...suggestionsData.value.popularSearches)
  }
  return result
})

// Obtenir l'index d'une suggestion dans la liste complète
function getSuggestionIndex(suggestion: string): number {
  return allSuggestions.value.indexOf(suggestion)
}

let debounceTimer: ReturnType<typeof setTimeout> | null = null

// Charger les suggestions quand la valeur change
watch(() => props.modelValue, async (newValue) => {
  if (debounceTimer) {
    clearTimeout(debounceTimer)
  }

  if (!newValue || newValue.trim().length < 1) {
    // Charger les suggestions par défaut si la recherche est vide
    loadSuggestions()
    return
  }

  // Debounce pour éviter trop de requêtes
  debounceTimer = setTimeout(async () => {
    await loadSuggestions(newValue.trim())
  }, 300) // Debounce de 300ms
})

function handleInput(event: Event) {
  const target = event.target as HTMLInputElement
  emit('update:modelValue', target.value)
  showSuggestions.value = true
  selectedIndex.value = -1
}

function handleBlur() {
  // Délai pour permettre le clic sur une suggestion
  setTimeout(() => {
    showSuggestions.value = false
  }, 200)
}

function hideSuggestions() {
  showSuggestions.value = false
  selectedIndex.value = -1
}

function navigateDown() {
  const maxIndex = allSuggestions.value.length - 1
  if (selectedIndex.value < maxIndex) {
    selectedIndex.value++
  } else {
    selectedIndex.value = 0 // Boucler vers le début
  }
}

function navigateUp() {
  if (selectedIndex.value > 0) {
    selectedIndex.value--
  } else {
    selectedIndex.value = allSuggestions.value.length - 1 // Boucler vers la fin
  }
}

function selectCurrent() {
  if (selectedIndex.value >= 0 && selectedIndex.value < allSuggestions.value.length) {
    selectSuggestion(allSuggestions.value[selectedIndex.value])
  }
}

function selectSuggestion(suggestion: string) {
  emit('update:modelValue', suggestion)
  // L'historique sera ajouté après la recherche avec le nombre de résultats
  showSuggestions.value = false
  selectedIndex.value = -1
  
  // Focus sur l'input pour permettre la navigation
  if (inputRef.value) {
    inputRef.value.focus()
  }
}
</script>

