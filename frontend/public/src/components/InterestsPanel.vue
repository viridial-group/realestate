<template>
  <Card class="overflow-hidden">
    <CardHeader class="pb-3">
      <CardTitle class="text-lg font-semibold">Points d'intérêt</CardTitle>
      <div v-if="userLocation" class="mt-2">
        <Badge variant="secondary" class="text-xs">
          <MapPin class="h-3 w-3 mr-1" />
          Points d'intérêt près de vous
        </Badge>
      </div>
    </CardHeader>
    <CardContent class="pt-0">
      <div class="space-y-2 max-h-[600px] overflow-y-auto">
        <Card
          v-for="interest in interestsWithDistance"
          :key="interest.id"
          class="cursor-pointer transition-all hover:shadow-md hover:border-primary/50"
          @click="handleInterestClick(interest)"
        >
          <CardContent class="p-4">
            <div class="flex items-start gap-3">
              <div class="text-2xl flex-shrink-0">{{ interest.icon }}</div>
              <div class="flex-1 min-w-0 space-y-2">
                <div>
                  <p class="font-semibold text-sm text-gray-900 dark:text-gray-100">{{ interest.name }}</p>
                  <Badge variant="outline" class="mt-1 text-xs">
                    {{ interest.type }}
                  </Badge>
                </div>
                <div class="flex items-center gap-2 flex-wrap">
                  <div class="flex items-center gap-1">
                    <Star
                      v-for="i in 5"
                      :key="i"
                      :class="[
                        'h-3 w-3',
                        i <= Math.round(interest.rating) ? 'fill-yellow-400 text-yellow-400' : 'text-gray-300'
                      ]"
                    />
                    <span class="text-xs text-gray-600 dark:text-gray-400 ml-1">{{ interest.rating.toFixed(1) }}</span>
                  </div>
                  <Separator orientation="vertical" class="h-4" />
                  <Badge
                    :variant="getDistanceVariant(interest.calculatedDistance)"
                    :class="[
                      'text-xs font-medium',
                      interest.calculatedDistance < 1 ? 'bg-green-100 text-green-700 border-green-300 hover:bg-green-200' :
                      interest.calculatedDistance < 3 ? 'bg-blue-100 text-blue-700 border-blue-300 hover:bg-blue-200' :
                      ''
                    ]"
                  >
                    <Navigation class="h-3 w-3 mr-1" />
                    {{ interest.calculatedDistance.toFixed(1) }} km
                  </Badge>
                </div>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>

      <div v-if="interestsWithDistance.length === 0" class="text-center text-gray-500 py-8 text-sm">
        <MapPin class="h-8 w-8 mx-auto mb-2 text-gray-400" />
        <p v-if="userLocation && maxDistance">
          Aucun point d'intérêt dans un rayon de {{ maxDistance }} km
        </p>
        <p v-else>
          Aucun point d'intérêt disponible
        </p>
      </div>
    </CardContent>
  </Card>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { interests } from '@/data/interests'
import { calculateDistance } from '@/utils/distance'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { Separator } from '@/components/ui/separator'
import { MapPin, Star, Navigation } from 'lucide-vue-next'

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

function getDistanceVariant(distance: number): 'default' | 'secondary' | 'destructive' | 'outline' {
  if (distance < 1) {
    return 'outline' // Utiliser outline avec classes personnalisées pour vert
  } else if (distance < 3) {
    return 'outline' // Utiliser outline avec classes personnalisées pour bleu
  }
  return 'outline' // Gris par défaut
}
</script>

<style scoped>
/* Scrollbar personnalisée */
.max-h-\[600px\]::-webkit-scrollbar {
  width: 6px;
}

.max-h-\[600px\]::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 10px;
}

.max-h-\[600px\]::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 10px;
}

.max-h-\[600px\]::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
</style>
