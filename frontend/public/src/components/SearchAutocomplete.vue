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
      v-if="showSuggestions && (suggestions.length > 0 || history.length > 0 || smartSuggestions.length > 0)"
      class="absolute z-50 w-full mt-2 bg-white dark:bg-gray-800 rounded-lg shadow-lg border border-gray-200 dark:border-gray-700 max-h-80 overflow-y-auto"
    >
      <!-- Historique de recherche -->
      <div v-if="history.length > 0 && !modelValue" class="p-2 border-b border-gray-100">
        <div class="text-xs font-semibold text-gray-500 px-3 py-1">Recherches récentes</div>
        <button
          v-for="(item, idx) in history"
          :key="idx"
          @click="selectSuggestion(item)"
          class="w-full text-left px-4 py-2 hover:bg-gray-50 flex items-center justify-between group"
        >
          <span class="text-sm text-gray-700">🔍 {{ item }}</span>
          <button
            @click.stop="removeFromHistory(item)"
            class="opacity-0 group-hover:opacity-100 text-gray-400 hover:text-gray-600 transition-opacity"
          >
            ✕
          </button>
        </button>
      </div>

      <!-- Suggestions de villes -->
      <div v-if="suggestions.length > 0" class="p-2">
        <div class="text-xs font-semibold text-gray-500 px-3 py-1">Villes</div>
        <button
          v-for="(suggestion, idx) in suggestions"
          :key="idx"
          @click="selectSuggestion(suggestion)"
          class="w-full text-left px-4 py-2 hover:bg-gray-50 flex items-center gap-2"
          :class="{ 'bg-blue-50': idx === selectedIndex }"
        >
          <span>📍</span>
          <span class="text-sm text-gray-700">{{ suggestion }}</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { publicPropertyService } from '@/api/public-property.service'
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
const suggestions = ref<string[]>([]) // Suggestions de villes depuis l'API
const showSuggestions = ref(false)
const selectedIndex = ref(-1)
const { history, addToHistory, removeFromHistory } = useSearchHistory()
const { generateSuggestions } = useSearchSuggestions()

// Suggestions intelligentes basées sur la requête
const smartSuggestions = computed(() => {
  if (!props.modelValue || props.modelValue.trim().length < 2) {
    return []
  }
  return generateSuggestions(props.modelValue)
})

let debounceTimer: ReturnType<typeof setTimeout> | null = null

// Charger les suggestions quand la valeur change
watch(() => props.modelValue, async (newValue) => {
  if (debounceTimer) {
    clearTimeout(debounceTimer)
  }

  if (!newValue || newValue.trim().length < 2) {
    suggestions.value = []
    return
  }

  debounceTimer = setTimeout(async () => {
    try {
      suggestions.value = await publicPropertyService.getAvailableCities(newValue.trim())
    } catch (error) {
      console.error('Error fetching city suggestions:', error)
      suggestions.value = []
    }
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
  const allSuggestions = [...smartSuggestions.value, ...suggestions.value]
  const maxIndex = allSuggestions.length - 1
  if (selectedIndex.value < maxIndex) {
    selectedIndex.value++
  }
}

function navigateUp() {
  if (selectedIndex.value > 0) {
    selectedIndex.value--
  }
}

function selectCurrent() {
  const allSuggestions = [...smartSuggestions.value, ...suggestions.value]
  if (selectedIndex.value >= 0 && selectedIndex.value < allSuggestions.length) {
    selectSuggestion(allSuggestions[selectedIndex.value])
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

