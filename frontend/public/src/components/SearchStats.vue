<template>
  <div
    v-if="showStats && stats"
    class="bg-blue-50 dark:bg-blue-900/20 border border-blue-200 dark:border-blue-800 rounded-lg p-4 mb-4"
  >
    <div class="flex items-center justify-between">
      <div class="flex items-center gap-4 text-sm">
        <div class="flex items-center gap-2">
          <span class="text-blue-600 dark:text-blue-400 font-semibold">
            {{ stats.totalResults }}
          </span>
          <span class="text-gray-600 dark:text-gray-400">
            résultat{{ stats.totalResults > 1 ? 's' : '' }}
          </span>
        </div>
        <div v-if="stats.searchTime" class="flex items-center gap-2 text-gray-500 dark:text-gray-400">
          <span>⚡</span>
          <span>{{ stats.searchTime }}ms</span>
        </div>
        <div v-if="stats.filtersActive" class="flex items-center gap-2 text-gray-500 dark:text-gray-400">
          <span>🔍</span>
          <span>{{ stats.filtersActive }} filtre{{ stats.filtersActive > 1 ? 's' : '' }} actif{{ stats.filtersActive > 1 ? 's' : '' }}</span>
        </div>
      </div>
      <button
        v-if="showClear"
        @click="$emit('clear')"
        class="text-xs text-blue-600 dark:text-blue-400 hover:text-blue-700 dark:hover:text-blue-300 underline"
      >
        Effacer
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  stats?: {
    totalResults: number
    searchTime?: number
    filtersActive?: number
  } | null
  showStats?: boolean
  showClear?: boolean
}>()

defineEmits<{
  'clear': []
}>()
</script>

