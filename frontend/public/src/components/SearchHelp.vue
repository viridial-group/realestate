<template>
  <div class="bg-blue-50 dark:bg-blue-900/20 border border-blue-200 dark:border-blue-800 rounded-lg p-4 mb-4">
    <div class="flex items-start gap-3">
      <div class="text-blue-600 dark:text-blue-400 text-xl">💡</div>
      <div class="flex-1">
        <h3 class="text-sm font-semibold text-blue-900 dark:text-blue-100 mb-2">
          Recherche avancée style Google
        </h3>
        <ul class="text-xs text-blue-800 dark:text-blue-200 space-y-1">
          <li>
            <strong>"phrase exacte"</strong> : Recherche une phrase précise
          </li>
          <li>
            <strong>-mot</strong> : Exclut un mot de la recherche
          </li>
          <li>
            <strong>mot1 mot2</strong> : Recherche les deux mots (tous doivent être présents)
          </li>
        </ul>
        <div class="mt-2 text-xs text-blue-700 dark:text-blue-300">
          <strong>Exemple:</strong> "appartement paris" -studio centre
        </div>
      </div>
      <button
        @click="$emit('close')"
        class="text-blue-400 hover:text-blue-600 dark:hover:text-blue-300 transition-colors"
        aria-label="Fermer"
      >
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
        </svg>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
defineEmits<{
  'close': []
}>()
</script>

