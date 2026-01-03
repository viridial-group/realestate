<template>
  <button
    @click="handleToggle"
    :disabled="disabled"
    class="p-2 rounded-full transition-all"
    :class="isInComparison
      ? 'bg-blue-100 text-blue-600 hover:bg-blue-200'
      : disabled
      ? 'bg-gray-100 text-gray-400 cursor-not-allowed'
      : 'bg-gray-100 text-gray-600 hover:bg-gray-200'"
    :title="getTooltip"
  >
    <svg
      class="w-5 h-5"
      fill="none"
      stroke="currentColor"
      viewBox="0 0 24 24"
    >
      <path
        stroke-linecap="round"
        stroke-linejoin="round"
        stroke-width="2"
        d="M9 17V7m0 10a2 2 0 01-2 2H5a2 2 0 01-2-2V7a2 2 0 012-2h2a2 2 0 012 2m0 10a2 2 0 002 2h2a2 2 0 002-2M9 7a2 2 0 012-2h2a2 2 0 012 2m0 10V7m0 10a2 2 0 002 2h2a2 2 0 002-2V7a2 2 0 00-2-2h-2a2 2 0 00-2 2"
      />
    </svg>
  </button>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useComparison } from '@/composables/useComparison'
import { useToast } from '@/composables/useToast'

const props = defineProps<{
  propertyId: number
}>()

const emit = defineEmits<{
  'comparison-toggled': [isInComparison: boolean]
}>()

const { isInComparison: checkComparison, toggleComparison, isFull } = useComparison()
const { success, warning, info } = useToast()

const isInComparison = computed(() => checkComparison(props.propertyId))
const disabled = computed(() => !isInComparison.value && isFull.value)

const getTooltip = computed(() => {
  if (disabled.value) {
    return 'Limite de comparaison atteinte (4 propriétés max)'
  }
  return isInComparison.value ? 'Retirer de la comparaison' : 'Ajouter à la comparaison'
})

function handleToggle() {
  if (disabled.value) {
    warning('Vous ne pouvez comparer que 4 propriétés maximum')
    return
  }

  const newState = toggleComparison(props.propertyId)
  emit('comparison-toggled', newState)

  // Afficher une notification
  if (newState) {
    success('Propriété ajoutée à la comparaison')
  } else {
    info('Propriété retirée de la comparaison')
  }
}
</script>

