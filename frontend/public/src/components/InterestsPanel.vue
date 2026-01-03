<template>
  <div class="bg-white rounded-xl shadow-sm p-5 space-y-4">
    <h2 class="text-lg font-semibold text-gray-800">Points d'intérêt</h2>

    <div v-if="userLocation" class="mb-3 text-xs text-gray-600 bg-blue-50 px-3 py-2 rounded-lg">
      📍 Points d'intérêt près de vous
    </div>

    <div class="space-y-3 max-h-[600px] overflow-y-auto">
      <div
        v-for="interest in interestsWithDistance"
        :key="interest.id"
        class="flex items-start gap-3 p-3 rounded-lg hover:bg-gray-50 transition-colors cursor-pointer border border-transparent hover:border-gray-200"
        @click="handleInterestClick(interest)"
      >
        <div class="text-2xl flex-shrink-0">{{ interest.icon }}</div>
        <div class="flex-1 min-w-0">
          <p class="font-medium text-sm text-gray-900">{{ interest.name }}</p>
          <p class="text-xs text-gray-500 mt-0.5">{{ interest.type }}</p>
          <div class="flex items-center gap-2 mt-2">
            <div class="flex items-center">
              <span
                v-for="i in 5"
                :key="i"
                class="text-xs"
                :class="i <= Math.round(interest.rating) ? 'text-yellow-500' : 'text-gray-300'"
              >
                ★
              </span>
            </div>
            <span class="text-xs text-gray-500">{{ interest.rating.toFixed(1) }}</span>
            <span class="text-xs text-gray-400">•</span>
            <span 
              class="text-xs font-medium"
              :class="interest.calculatedDistance < 1 ? 'text-green-600' : interest.calculatedDistance < 3 ? 'text-blue-600' : 'text-gray-500'"
            >
              {{ interest.calculatedDistance }} km
            </span>
          </div>
        </div>
      </div>
    </div>

    <div v-if="interestsWithDistance.length === 0" class="text-center text-gray-500 py-8 text-sm">
      <p v-if="userLocation && maxDistance">
        Aucun point d'intérêt dans un rayon de {{ maxDistance }} km
      </p>
      <p v-else>
        Aucun point d'intérêt disponible
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { interests } from '@/data/interests'
import { calculateDistance } from '@/utils/distance'

const props = defineProps<{
  userLocation?: { lat: number; lng: number } | null
  maxDistance?: number // Distance maximale en km (optionnel)
}>()

const emit = defineEmits<{
  'interest-click': [interest: { lat: number; lng: number; icon: string }]
}>()

// Calculer les distances et enrichir les POI
const interestsWithDistance = computed(() => {
  if (!props.userLocation) {
    // Si pas de position utilisateur, utiliser les distances statiques
    return interests.map(interest => ({
      ...interest,
      calculatedDistance: interest.distance
    }))
  }
  
  return interests
    .map(interest => {
      const distance = calculateDistance(
        props.userLocation!.lat,
        props.userLocation!.lng,
        interest.lat,
        interest.lng
      )
      return {
        ...interest,
        calculatedDistance: distance
      }
    })
    .filter(interest => {
      // Filtrer par distance maximale si spécifiée
      if (props.maxDistance) {
        return interest.calculatedDistance <= props.maxDistance
      }
      return true
    })
    .sort((a, b) => a.calculatedDistance - b.calculatedDistance) // Trier par distance croissante
})

function handleInterestClick(interest: any) {
  emit('interest-click', interest)
}
</script>

<style scoped>
/* Scrollbar personnalisée */
.space-y-3::-webkit-scrollbar {
  width: 6px;
}

.space-y-3::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 10px;
}

.space-y-3::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 10px;
}

.space-y-3::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
</style>
