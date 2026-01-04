<template>
  <div
    v-if="comparisonCount > 0"
    class="fixed bottom-4 right-4 z-50 bg-white rounded-lg shadow-2xl border border-gray-200 max-w-md w-full mx-4 transition-all duration-300"
    :class="isExpanded ? 'max-h-[80vh]' : 'max-h-20'"
  >
    <!-- Header -->
    <div
      class="flex items-center justify-between p-4 cursor-pointer hover:bg-gray-50 transition-colors"
      @click="toggleExpanded"
    >
      <div class="flex items-center gap-3">
        <div class="relative">
          <Scale class="h-5 w-5 text-blue-600" />
          <span
            v-if="comparisonCount > 0"
            class="absolute -top-1 -right-1 bg-blue-600 text-white text-xs font-bold rounded-full w-5 h-5 flex items-center justify-center"
          >
            {{ comparisonCount }}
          </span>
        </div>
        <div>
          <p class="text-sm font-semibold text-gray-900">
            Comparaison ({{ comparisonCount }}/{{ maxComparison }})
          </p>
          <p class="text-xs text-gray-600">
            {{ comparisonCount === 1 ? 'propriété' : 'propriétés' }} sélectionnée{{ comparisonCount > 1 ? 's' : '' }}
          </p>
        </div>
      </div>
      <div class="flex items-center gap-2">
        <button
          v-if="comparisonCount > 0"
          @click.stop="clearAll"
          class="p-1 text-gray-400 hover:text-red-600 transition-colors"
          title="Tout supprimer"
        >
          <Trash2 class="h-4 w-4" />
        </button>
        <ChevronDown
          class="h-5 w-5 text-gray-400 transition-transform"
          :class="{ 'rotate-180': isExpanded }"
        />
      </div>
    </div>

    <!-- Content (expanded) -->
    <div v-if="isExpanded" class="border-t border-gray-200 max-h-[calc(80vh-80px)] overflow-y-auto">
      <!-- Properties list -->
      <div class="p-4 space-y-3">
        <div
          v-for="property in comparisonProperties"
          :key="property.id"
          class="flex items-center gap-3 p-3 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors"
        >
          <img
            :src="getPropertyImage(property)"
            :alt="property.title"
            class="w-16 h-16 object-cover rounded"
            @error="handleImageError"
          />
          <div class="flex-1 min-w-0">
            <p class="text-sm font-medium text-gray-900 truncate">{{ property.title }}</p>
            <p class="text-xs text-gray-600">{{ formatPrice(property.price) }}</p>
            <p class="text-xs text-gray-500">{{ property.city }}</p>
          </div>
          <button
            @click="removeProperty(property.id)"
            class="p-1 text-gray-400 hover:text-red-600 transition-colors"
            title="Retirer"
          >
            <X class="h-4 w-4" />
          </button>
        </div>
      </div>

      <!-- Quick stats comparison -->
      <div v-if="comparisonProperties.length >= 2" class="p-4 border-t border-gray-200 bg-gray-50">
        <h4 class="text-sm font-semibold text-gray-900 mb-3">Comparaison rapide</h4>
        <div class="space-y-2 text-xs">
          <div class="flex items-center justify-between">
            <span class="text-gray-600">Prix moyen</span>
            <span class="font-semibold text-gray-900">{{ formatPrice(averagePrice) }}</span>
          </div>
          <div class="flex items-center justify-between">
            <span class="text-gray-600">Surface moyenne</span>
            <span class="font-semibold text-gray-900">{{ averageSurface }} m²</span>
          </div>
          <div class="flex items-center justify-between">
            <span class="text-gray-600">Prix/m² moyen</span>
            <span class="font-semibold text-gray-900">{{ formatPrice(averagePricePerM2) }}/m²</span>
          </div>
        </div>
      </div>

      <!-- Actions -->
      <div class="p-4 border-t border-gray-200 bg-white">
        <router-link
          to="/compare"
          class="w-full px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors text-sm font-medium text-center block"
        >
          Voir la comparaison détaillée
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { Scale, ChevronDown, Trash2, X } from 'lucide-vue-next'
import { useComparison } from '@/composables/useComparison'
import { publicPropertyService, type PublicProperty } from '@/api/public-property.service'
import { documentService } from '@/api/document.service'
import { getPlaceholderImage } from '@/utils/imageOptimization'
import { useToast } from '@/composables/useToast'

const maxComparison = 4
const isExpanded = ref(false)
const comparisonProperties = ref<PublicProperty[]>([])
const propertyImages = ref<Record<number, any[]>>({})

const { comparison, comparisonCount, removeFromComparison, clearComparison } = useComparison()
const { success } = useToast()

const averagePrice = computed(() => {
  if (comparisonProperties.value.length === 0) return 0
  const sum = comparisonProperties.value.reduce((acc, p) => acc + (p.price || 0), 0)
  return Math.round(sum / comparisonProperties.value.length)
})

const averageSurface = computed(() => {
  if (comparisonProperties.value.length === 0) return 0
  const sum = comparisonProperties.value.reduce((acc, p) => acc + (p.surface || 0), 0)
  return Math.round(sum / comparisonProperties.value.length)
})

const averagePricePerM2 = computed(() => {
  if (averageSurface.value === 0) return 0
  return Math.round(averagePrice.value / averageSurface.value)
})

function toggleExpanded() {
  isExpanded.value = !isExpanded.value
  if (isExpanded.value && comparisonProperties.value.length === 0) {
    loadProperties()
  }
}

async function loadProperties() {
  comparisonProperties.value = []
  propertyImages.value = {}

  for (const propertyId of comparison.value) {
    try {
      const property = await publicPropertyService.getPublishedPropertyById(propertyId)
      comparisonProperties.value.push(property)

      // Charger les images
      const documents = await documentService.getByPropertyId(propertyId)
      propertyImages.value[propertyId] = documentService.filterImages(documents)
    } catch (error) {
      console.error(`Error loading property ${propertyId}:`, error)
    }
  }
}

function getPropertyImage(property: PublicProperty): string {
  const images = propertyImages.value[property.id]
  if (images && images.length > 0) {
    return documentService.getViewUrl(images[0].id)
  }
  return getPlaceholderImage(64, 64)
}

function handleImageError(event: Event) {
  const img = event.target as HTMLImageElement
  img.src = getPlaceholderImage(64, 64)
}

function formatPrice(price: number): string {
  return new Intl.NumberFormat('fr-FR', {
    style: 'currency',
    currency: 'EUR',
    maximumFractionDigits: 0,
  }).format(price)
}

function removeProperty(propertyId: number) {
  removeFromComparison(propertyId)
  comparisonProperties.value = comparisonProperties.value.filter(p => p.id !== propertyId)
  success('Propriété retirée de la comparaison')
}

function clearAll() {
  if (confirm('Êtes-vous sûr de vouloir supprimer toutes les propriétés de la comparaison ?')) {
    clearComparison()
    comparisonProperties.value = []
    isExpanded.value = false
    success('Comparaison vidée')
  }
}

watch(comparison, () => {
  if (isExpanded.value) {
    loadProperties()
  }
}, { deep: true })

onMounted(() => {
  if (comparisonCount.value > 0) {
    loadProperties()
  }
})
</script>

<style scoped>
/* Smooth transitions */
.transition-all {
  transition-property: all;
  transition-timing-function: cubic-bezier(0.4, 0, 0.2, 1);
}
</style>

