<template>
  <div class="flex items-center justify-center" :class="containerClass">
    <div
      class="animate-spin rounded-full border-b-2"
      :class="spinnerClass"
      :style="{ width: size, height: size, borderColor: color }"
      role="status"
      :aria-label="'Chargement en cours'"
    >
      <span class="sr-only">Chargement...</span>
    </div>
    <p v-if="showText" class="ml-3 text-gray-600 dark:text-gray-400">{{ text }}</p>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = withDefaults(defineProps<{
  size?: string
  color?: string
  text?: string
  showText?: boolean
  containerClass?: string
}>(), {
  size: '2rem',
  color: '#2563eb',
  text: 'Chargement...',
  showText: false,
  containerClass: 'py-12'
})

const spinnerClass = computed(() => {
  return 'border-current'
})
</script>

