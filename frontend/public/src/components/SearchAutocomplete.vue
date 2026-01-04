<template>
  <div class="relative">
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
      class="w-full border border-gray-300 dark:border-gray-700 rounded-full px-5 py-3 text-base
             bg-white dark:bg-gray-800 text-gray-900 dark:text-white
             focus:outline-none focus:ring-2 focus:ring-blue-500 dark:focus:ring-blue-400"
      :placeholder="placeholder"
    />

    <!-- Suggestions -->
    <div
      v-if="showSuggestions && (allSuggestions.length > 0 || history.length > 0 || isLoadingSuggestions)"
      class="absolute z-50 w-full mt-2 bg-white dark:bg-gray-800 rounded-lg shadow-lg border border-gray-200 dark:border-gray-700 max-h-80 overflow-y-auto"
    >
      <!-- Loading state -->
      <div v-if="isLoadingSuggestions" class="p-4 text-center text-gray-500 text-sm">
        Chargement des suggestions...
      </div>

      <!-- Historique de recherche -->
      <div v-if="history.length > 0 && !modelValue && !isLoadingSuggestions" class="p-2 border-b border-gray-100 dark:border-gray-700">
        <div class="text-xs font-semibold text-gray-500 dark:text-gray-400 px-3 py-1">Recherches récentes</div>
        <div
          v-for="(item, idx) in history"
          :key="idx"
          @click="selectSuggestion(item)"
          class="w-full text-left px-4 py-2 hover:bg-gray-50 dark:hover:bg-gray-700 flex items-center justify-between group cursor-pointer"
        >
          <span class="text-sm text-gray-700 dark:text-gray-300">🔍 {{ item }}</span>
          <button
            @click.stop="removeFromHistory(item)"
            class="opacity-0 group-hover:opacity-100 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 transition-opacity"
            type="button"
          >
            ✕
          </button>
        </div>
      </div>

      <!-- Suggestions intelligentes -->
      <div v-if="allSuggestions.length > 0 && !isLoadingSuggestions" class="p-2">
        <!-- Villes -->
        <div v-if="suggestionsData.cities.length > 0" class="mb-2">
          <div class="text-xs font-semibold text-gray-500 dark:text-gray-400 px-3 py-1">Villes</div>
          <button
            v-for="(city, idx) in suggestionsData.cities"
            :key="`city-${idx}`"
            @click="selectSuggestion(city)"
            class="w-full text-left px-4 py-2 hover:bg-gray-50 dark:hover:bg-gray-700 flex items-center gap-2"
            :class="{ 'bg-blue-50 dark:bg-blue-900/20': getSuggestionIndex(city) === selectedIndex }"
          >
            <span>📍</span>
            <span class="text-sm text-gray-700 dark:text-gray-300">{{ city }}</span>
          </button>
        </div>

        <!-- Types -->
        <div v-if="suggestionsData.types.length > 0" class="mb-2">
          <div class="text-xs font-semibold text-gray-500 dark:text-gray-400 px-3 py-1">Types</div>
          <button
            v-for="(type, idx) in suggestionsData.types"
            :key="`type-${idx}`"
            @click="selectSuggestion(type)"
            class="w-full text-left px-4 py-2 hover:bg-gray-50 dark:hover:bg-gray-700 flex items-center gap-2"
            :class="{ 'bg-blue-50 dark:bg-blue-900/20': getSuggestionIndex(type) === selectedIndex }"
          >
            <span>🏠</span>
            <span class="text-sm text-gray-700 dark:text-gray-300">{{ type }}</span>
          </button>
        </div>

        <!-- Adresses -->
        <div v-if="suggestionsData.addresses.length > 0" class="mb-2">
          <div class="text-xs font-semibold text-gray-500 dark:text-gray-400 px-3 py-1">Adresses</div>
          <button
            v-for="(address, idx) in suggestionsData.addresses"
            :key="`address-${idx}`"
            @click="selectSuggestion(address)"
            class="w-full text-left px-4 py-2 hover:bg-gray-50 dark:hover:bg-gray-700 flex items-center gap-2"
            :class="{ 'bg-blue-50 dark:bg-blue-900/20': getSuggestionIndex(address) === selectedIndex }"
          >
            <span>📍</span>
            <span class="text-sm text-gray-700 dark:text-gray-300">{{ address }}</span>
          </button>
        </div>

        <!-- Suggestions intelligentes -->
        <div v-if="smartSuggestions.length > 0" class="mb-2">
          <div class="text-xs font-semibold text-gray-500 dark:text-gray-400 px-3 py-1">Suggestions</div>
          <button
            v-for="(suggestion, idx) in smartSuggestions"
            :key="`smart-${idx}`"
            @click="selectSuggestion(suggestion)"
            class="w-full text-left px-4 py-2 hover:bg-gray-50 dark:hover:bg-gray-700 flex items-center gap-2"
            :class="{ 'bg-blue-50 dark:bg-blue-900/20': getSuggestionIndex(suggestion) === selectedIndex }"
          >
            <span>💡</span>
            <span class="text-sm text-gray-700 dark:text-gray-300">{{ suggestion }}</span>
          </button>
        </div>

        <!-- Recherches populaires -->
        <div v-if="suggestionsData.popularSearches.length > 0 && modelValue" class="mb-2">
          <div class="text-xs font-semibold text-gray-500 dark:text-gray-400 px-3 py-1">Recherches populaires</div>
          <button
            v-for="(popular, idx) in suggestionsData.popularSearches"
            :key="`popular-${idx}`"
            @click="selectSuggestion(popular)"
            class="w-full text-left px-4 py-2 hover:bg-gray-50 dark:hover:bg-gray-700 flex items-center gap-2"
            :class="{ 'bg-blue-50 dark:bg-blue-900/20': getSuggestionIndex(popular) === selectedIndex }"
          >
            <span>🔥</span>
            <span class="text-sm text-gray-700 dark:text-gray-300">{{ popular }}</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
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
  addToHistory(suggestion)
  showSuggestions.value = false
  selectedIndex.value = -1
  
  // Focus sur l'input pour permettre la navigation
  if (inputRef.value) {
    inputRef.value.focus()
  }
}
</script>

