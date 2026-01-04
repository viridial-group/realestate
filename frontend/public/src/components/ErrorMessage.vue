<template>
  <div class="bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 rounded-lg p-6">
    <div class="flex items-start gap-4">
      <div class="flex-shrink-0">
        <div class="h-10 w-10 rounded-full bg-red-100 dark:bg-red-900/40 flex items-center justify-center">
          <svg class="h-6 w-6 text-red-600 dark:text-red-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
        </div>
      </div>
      <div class="flex-1">
        <h3 class="text-lg font-semibold text-red-900 dark:text-red-200 mb-1">
          {{ title || 'Une erreur est survenue' }}
        </h3>
        <p class="text-red-800 dark:text-red-300 mb-4">
          {{ message || 'Nous rencontrons un problème technique. Veuillez réessayer dans quelques instants.' }}
        </p>
        <div v-if="showRetry" class="flex gap-3">
          <button
            @click="$emit('retry')"
            class="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors text-sm font-medium focus:outline-none focus:ring-2 focus:ring-red-500 focus:ring-offset-2"
          >
            Réessayer
          </button>
          <button
            v-if="showHome"
            @click="$emit('go-home')"
            class="px-4 py-2 bg-gray-200 dark:bg-gray-700 text-gray-800 dark:text-gray-200 rounded-lg hover:bg-gray-300 dark:hover:bg-gray-600 transition-colors text-sm font-medium focus:outline-none focus:ring-2 focus:ring-gray-500 focus:ring-offset-2"
          >
            Retour à l'accueil
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  title?: string
  message?: string
  showRetry?: boolean
  showHome?: boolean
}>()

defineEmits<{
  retry: []
  'go-home': []
}>()
</script>

