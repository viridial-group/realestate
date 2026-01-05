<template>
  <div v-if="suggestions.length > 0" class="mt-6 bg-white rounded-xl shadow-sm p-5">
    <h3 class="text-sm font-semibold text-gray-700 mb-3">Recherches similaires</h3>
    <div class="flex flex-wrap gap-2">
      <button
        v-for="(suggestion, index) in suggestions"
        :key="index"
        @click="handleSuggestionClick(suggestion)"
        class="px-3 py-1.5 rounded-full text-sm text-gray-700 bg-gray-100 hover:bg-gray-200 transition-colors"
      >
        {{ suggestion }}
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  suggestions: string[]
}>()

const emit = defineEmits<{
  'suggestion-click': [suggestion: string]
}>()

function handleSuggestionClick(suggestion: string) {
  emit('suggestion-click', suggestion)
}
</script>

