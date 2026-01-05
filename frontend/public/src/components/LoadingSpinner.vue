<template>
  <div class="flex flex-col items-center justify-center" :class="containerClass">
    <div
      class="animate-spin rounded-full border-2 border-gray-200 border-t-blue-600"
      :style="{ width: size, height: size }"
      role="status"
      :aria-label="'Chargement en cours'"
    >
      <span class="sr-only">Chargement...</span>
    </div>
    <p v-if="showText" class="mt-3 text-sm text-gray-600">{{ text }}</p>
  </div>
</template>

<script setup lang="ts">

withDefaults(defineProps<{
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
</script>

