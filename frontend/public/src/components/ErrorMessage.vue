<template>
  <div class="bg-white border border-red-200 rounded-lg p-6">
    <div class="flex items-start gap-4">
      <div class="flex-shrink-0">
        <div class="h-10 w-10 rounded-full bg-red-50 flex items-center justify-center">
          <AlertCircle class="h-5 w-5 text-red-600" />
        </div>
      </div>
      <div class="flex-1">
        <h3 class="text-lg font-medium text-gray-900 mb-1">
          {{ title || 'Une erreur est survenue' }}
        </h3>
        <p class="text-gray-600 mb-4">
          {{ message || 'Nous rencontrons un problème technique. Veuillez réessayer dans quelques instants.' }}
        </p>
        <div v-if="showRetry" class="flex gap-3">
          <button
            @click="$emit('retry')"
            class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors text-sm font-medium shadow-sm"
          >
            Réessayer
          </button>
          <button
            v-if="showHome"
            @click="$emit('go-home')"
            class="px-4 py-2 bg-white border border-gray-300 text-gray-700 rounded-md hover:bg-gray-50 transition-colors text-sm font-medium shadow-sm"
          >
            Retour à l'accueil
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { AlertCircle } from 'lucide-vue-next'

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

