<template>
  <div
    v-if="showStats && stats"
    class="text-sm text-gray-600 mb-4 flex items-center justify-between"
  >
    <div class="flex items-center gap-4">
      <span>
        Environ <strong class="text-gray-900">{{ stats.totalResults }}</strong> résultat{{ stats.totalResults > 1 ? 's' : '' }}
      </span>
      <span v-if="stats.searchTime" class="text-gray-500 flex items-center gap-1">
        <Zap class="h-3 w-3" />
        ({{ stats.searchTime }}ms)
      </span>
      <span v-if="stats.filtersActive" class="text-gray-500 flex items-center gap-1">
        <Filter class="h-3 w-3" />
        {{ stats.filtersActive }} filtre{{ stats.filtersActive > 1 ? 's' : '' }}
      </span>
    </div>
    <button
      v-if="showClear"
      @click="$emit('clear')"
      class="text-sm text-blue-600 hover:text-blue-800 hover:underline"
    >
      Effacer les filtres
    </button>
  </div>
</template>

<script setup lang="ts">
import { Zap, Filter } from 'lucide-vue-next'

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

