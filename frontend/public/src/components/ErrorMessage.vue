<template>
  <div class="bg-white dark:bg-gray-800 border border-red-200 dark:border-red-800 rounded-lg p-6 shadow-sm">
    <div class="flex items-start gap-4">
      <div class="flex-shrink-0">
        <div class="h-10 w-10 rounded-full bg-red-50 dark:bg-red-900/20 flex items-center justify-center">
          <AlertCircle class="h-5 w-5 text-red-600 dark:text-red-400" />
        </div>
      </div>
      <div class="flex-1 min-w-0">
        <h3 class="text-lg font-medium text-gray-900 dark:text-gray-100 mb-1">
          {{ title || 'Une erreur est survenue' }}
        </h3>
        <p class="text-gray-600 dark:text-gray-400 mb-4 break-words">
          {{ message || 'Nous rencontrons un problème technique. Veuillez réessayer dans quelques instants.' }}
        </p>
        <div v-if="errorCode" class="mb-4">
          <span class="inline-flex items-center px-2.5 py-0.5 rounded text-xs font-medium bg-gray-100 dark:bg-gray-700 text-gray-800 dark:text-gray-200">
            Code: {{ errorCode }}
          </span>
        </div>
        <div v-if="showRetry || showHome" class="flex flex-wrap gap-3">
          <button
            v-if="showRetry"
            @click="$emit('retry')"
            class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 dark:bg-blue-500 dark:hover:bg-blue-600 transition-colors text-sm font-medium shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
          >
            Réessayer
          </button>
          <button
            v-if="showHome"
            @click="$emit('go-home')"
            class="px-4 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 text-gray-700 dark:text-gray-200 rounded-md hover:bg-gray-50 dark:hover:bg-gray-600 transition-colors text-sm font-medium shadow-sm focus:outline-none focus:ring-2 focus:ring-gray-500 focus:ring-offset-2"
          >
            Retour à l'accueil
          </button>
        </div>
      </div>
      <button
        v-if="dismissible"
        @click="$emit('dismiss')"
        class="flex-shrink-0 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 transition-colors"
        aria-label="Fermer"
      >
        <X class="h-5 w-5" />
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { AlertCircle, X } from 'lucide-vue-next'

defineProps<{
  title?: string
  message?: string
  errorCode?: string | number
  showRetry?: boolean
  showHome?: boolean
  dismissible?: boolean
}>()

defineEmits<{
  retry: []
  'go-home': []
  dismiss: []
}>()
</script>

