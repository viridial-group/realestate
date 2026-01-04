<template>
  <div class="text-center py-12 px-4">
    <div class="max-w-md mx-auto">
      <div class="mb-6">
        <div class="inline-flex items-center justify-center w-20 h-20 rounded-full bg-gray-100 dark:bg-gray-800 mb-4">
          <svg class="w-10 h-10 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" :d="iconPath" />
          </svg>
        </div>
      </div>
      <h3 class="text-xl font-semibold text-gray-900 dark:text-white mb-2">
        {{ title }}
      </h3>
      <p class="text-gray-600 dark:text-gray-400 mb-6">
        {{ description }}
      </p>
      <div v-if="showAction" class="flex flex-col sm:flex-row gap-3 justify-center">
        <button
          v-if="actionLabel"
          @click="$emit('action')"
          class="px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors font-medium focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
        >
          {{ actionLabel }}
        </button>
        <button
          v-if="secondaryActionLabel"
          @click="$emit('secondary-action')"
          class="px-6 py-3 bg-gray-200 dark:bg-gray-700 text-gray-800 dark:text-gray-200 rounded-lg hover:bg-gray-300 dark:hover:bg-gray-600 transition-colors font-medium focus:outline-none focus:ring-2 focus:ring-gray-500 focus:ring-offset-2"
        >
          {{ secondaryActionLabel }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  title: string
  description: string
  icon?: 'search' | 'empty' | 'error' | 'info'
  actionLabel?: string
  secondaryActionLabel?: string
  showAction?: boolean
}>()

defineEmits<{
  action: []
  'secondary-action': []
}>()

const iconPath = computed(() => {
  switch (props.icon) {
    case 'search':
      return 'M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z'
    case 'error':
      return 'M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z'
    case 'info':
      return 'M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z'
    case 'empty':
    default:
      return 'M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4'
  }
})
</script>

