<template>
  <button
    @click="handleToggle"
    class="p-2 rounded-full transition-all"
    :class="isFavorite
      ? 'bg-yellow-100 text-yellow-600 hover:bg-yellow-200'
      : 'bg-gray-100 text-gray-600 hover:bg-gray-200'"
    :title="isFavorite ? 'Retirer des favoris' : 'Ajouter aux favoris'"
  >
    <svg
      class="w-5 h-5"
      :class="{ 'fill-current': isFavorite }"
      fill="none"
      stroke="currentColor"
      viewBox="0 0 24 24"
    >
      <path
        stroke-linecap="round"
        stroke-linejoin="round"
        stroke-width="2"
        d="M11.049 2.927c.3-.921 1.603-.921 1.902 0l1.519 4.674a1 1 0 00.95.69h4.915c.969 0 1.371 1.24.588 1.81l-3.976 2.888a1 1 0 00-.363 1.118l1.518 4.674c.3.922-.755 1.688-1.538 1.118l-3.976-2.888a1 1 0 00-1.176 0l-3.976 2.888c-.783.57-1.838-.197-1.538-1.118l1.518-4.674a1 1 0 00-.363-1.118l-3.976-2.888c-.784-.57-.38-1.81.588-1.81h4.914a1 1 0 00.951-.69l1.519-4.674z"
      />
    </svg>
  </button>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useFavorites } from '@/composables/useFavorites'
import { useToast } from '@/composables/useToast'

const props = defineProps<{
  propertyId: number
}>()

const emit = defineEmits<{
  'favorite-toggled': [isFavorite: boolean]
}>()

const { isFavorite: checkFavorite, toggleFavorite } = useFavorites()
const { success, info } = useToast()

const isFavorite = computed(() => checkFavorite(props.propertyId))

function handleToggle() {
  const newState = toggleFavorite(props.propertyId)
  emit('favorite-toggled', newState)
  
  // Afficher une notification
  if (newState) {
    success('Propriété ajoutée aux favoris')
  } else {
    info('Propriété retirée des favoris')
  }
}
</script>

