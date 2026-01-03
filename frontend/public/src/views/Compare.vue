<template>
  <div class="max-w-7xl mx-auto px-4 py-8">
    <div class="mb-8">
      <h1 class="text-3xl font-bold text-gray-900 mb-2">Comparer les propriétés</h1>
      <p class="text-gray-600">
        {{ comparisonCount }} propriété{{ comparisonCount > 1 ? 's' : '' }} sélectionnée{{ comparisonCount > 1 ? 's' : '' }}
      </p>
    </div>

    <!-- Message si aucune propriété -->
    <div v-if="comparisonCount === 0" class="text-center py-16 bg-white rounded-lg shadow-sm">
      <div class="text-6xl mb-4">⚖️</div>
      <h2 class="text-2xl font-semibold text-gray-900 mb-2">Aucune propriété à comparer</h2>
      <p class="text-gray-600 mb-6">
        Ajoutez des propriétés à la comparaison pour les comparer côte à côte.
      </p>
      <router-link
        to="/search"
        class="inline-block px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors font-medium"
      >
        Parcourir les propriétés
      </router-link>
    </div>

    <!-- Tableau de comparaison -->
    <div v-else class="space-y-6">
      <!-- Actions -->
      <div class="flex items-center justify-between">
        <button
          @click="clearAll"
          class="px-4 py-2 bg-red-50 text-red-600 rounded-lg hover:bg-red-100 transition-colors text-sm font-medium"
        >
          Tout supprimer
        </button>
        <div class="text-sm text-gray-600">
          Maximum 4 propriétés
        </div>
      </div>

      <!-- Tableau responsive -->
      <div class="overflow-x-auto bg-white rounded-lg shadow-sm">
        <table class="w-full min-w-[800px]">
          <thead class="bg-gray-50 border-b">
            <tr>
              <th class="px-4 py-3 text-left text-sm font-semibold text-gray-900 sticky left-0 bg-gray-50 z-10">
                Critère
              </th>
              <th
                v-for="property in properties"
                :key="property.id"
                class="px-4 py-3 text-center text-sm font-semibold text-gray-900 relative"
              >
                <button
                  @click="removeFromComparison(property.id)"
                  class="absolute top-2 right-2 text-gray-400 hover:text-red-600 transition-colors"
                  title="Retirer"
                >
                  <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                  </svg>
                </button>
                <div class="space-y-2">
                  <img
                    :src="getImageUrl(property)"
                    :alt="property.title"
                    class="w-full h-32 object-cover rounded-lg"
                    @error="handleImageError"
                  />
                  <div>
                    <h3 class="font-semibold text-gray-900 text-sm">{{ property.title }}</h3>
                    <p class="text-xs text-gray-500 mt-1">{{ property.city }}</p>
                  </div>
                </div>
              </th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-200">
            <!-- Prix -->
            <tr>
              <td class="px-4 py-3 text-sm font-medium text-gray-700 sticky left-0 bg-white z-10">
                Prix
              </td>
              <td
                v-for="property in properties"
                :key="property.id"
                class="px-4 py-3 text-center text-sm text-gray-900"
              >
                <span class="font-semibold text-blue-600">
                  €{{ property.price.toLocaleString('fr-FR') }}
                </span>
              </td>
            </tr>

            <!-- Type -->
            <tr class="bg-gray-50">
              <td class="px-4 py-3 text-sm font-medium text-gray-700 sticky left-0 bg-gray-50 z-10">
                Type
              </td>
              <td
                v-for="property in properties"
                :key="property.id"
                class="px-4 py-3 text-center text-sm text-gray-600"
              >
                {{ property.type }}
              </td>
            </tr>

            <!-- Statut -->
            <tr>
              <td class="px-4 py-3 text-sm font-medium text-gray-700 sticky left-0 bg-white z-10">
                Statut
              </td>
              <td
                v-for="property in properties"
                :key="property.id"
                class="px-4 py-3 text-center"
              >
                <span
                  class="px-2 py-1 rounded-full text-xs font-medium"
                  :class="getStatusClass(property.status)"
                >
                  {{ property.status }}
                </span>
              </td>
            </tr>

            <!-- Surface -->
            <tr class="bg-gray-50">
              <td class="px-4 py-3 text-sm font-medium text-gray-700 sticky left-0 bg-gray-50 z-10">
                Surface
              </td>
              <td
                v-for="property in properties"
                :key="property.id"
                class="px-4 py-3 text-center text-sm text-gray-600"
              >
                {{ property.surface }} m²
              </td>
            </tr>

            <!-- Chambres -->
            <tr>
              <td class="px-4 py-3 text-sm font-medium text-gray-700 sticky left-0 bg-white z-10">
                Chambres
              </td>
              <td
                v-for="property in properties"
                :key="property.id"
                class="px-4 py-3 text-center text-sm text-gray-600"
              >
                {{ property.bedrooms || 'N/A' }}
              </td>
            </tr>

            <!-- Salles de bain -->
            <tr class="bg-gray-50">
              <td class="px-4 py-3 text-sm font-medium text-gray-700 sticky left-0 bg-gray-50 z-10">
                Salles de bain
              </td>
              <td
                v-for="property in properties"
                :key="property.id"
                class="px-4 py-3 text-center text-sm text-gray-600"
              >
                {{ property.bathrooms || 'N/A' }}
              </td>
            </tr>

            <!-- Prix au m² -->
            <tr>
              <td class="px-4 py-3 text-sm font-medium text-gray-700 sticky left-0 bg-white z-10">
                Prix au m²
              </td>
              <td
                v-for="property in properties"
                :key="property.id"
                class="px-4 py-3 text-center text-sm text-gray-600"
              >
                {{ property.surface ? `€${Math.round(property.price / property.surface).toLocaleString('fr-FR')}` : 'N/A' }}
              </td>
            </tr>

            <!-- Année de construction -->
            <tr class="bg-gray-50" v-if="hasYearBuilt">
              <td class="px-4 py-3 text-sm font-medium text-gray-700 sticky left-0 bg-gray-50 z-10">
                Année de construction
              </td>
              <td
                v-for="property in properties"
                :key="property.id"
                class="px-4 py-3 text-center text-sm text-gray-600"
              >
                {{ (property as any).yearBuilt || 'N/A' }}
              </td>
            </tr>

            <!-- Condition -->
            <tr v-if="hasCondition">
              <td class="px-4 py-3 text-sm font-medium text-gray-700 sticky left-0 bg-white z-10">
                État
              </td>
              <td
                v-for="property in properties"
                :key="property.id"
                class="px-4 py-3 text-center text-sm text-gray-600"
              >
                {{ (property as any).condition || 'N/A' }}
              </td>
            </tr>

            <!-- Note -->
            <tr>
              <td class="px-4 py-3 text-sm font-medium text-gray-700 sticky left-0 bg-white z-10">
                Note
              </td>
              <td
                v-for="property in properties"
                :key="property.id"
                class="px-4 py-3 text-center text-sm"
              >
                <div v-if="property.rating" class="flex items-center justify-center gap-1">
                  <span class="text-yellow-500">⭐</span>
                  <span class="text-gray-600">{{ property.rating.toFixed(1) }}</span>
                  <span class="text-gray-400 text-xs">({{ property.reviews || 0 }})</span>
                </div>
                <span v-else class="text-gray-400">N/A</span>
              </td>
            </tr>

            <!-- Description -->
            <tr class="bg-gray-50">
              <td class="px-4 py-3 text-sm font-medium text-gray-700 sticky left-0 bg-gray-50 z-10">
                Description
              </td>
              <td
                v-for="property in properties"
                :key="property.id"
                class="px-4 py-3 text-sm text-gray-600"
              >
                <p class="line-clamp-3">{{ property.description }}</p>
              </td>
            </tr>

            <!-- Actions -->
            <tr>
              <td class="px-4 py-3 sticky left-0 bg-white z-10"></td>
              <td
                v-for="property in properties"
                :key="property.id"
                class="px-4 py-3 text-center"
              >
                <button
                  @click="goToDetails(property.id)"
                  class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors text-sm font-medium"
                >
                  Voir détails
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useComparison } from '@/composables/useComparison'
import { publicPropertyService, type PublicProperty } from '@/api/public-property.service'
import { documentService } from '@/api/document.service'
import { getPlaceholderImage } from '@/utils/imageOptimization'

const router = useRouter()
const { comparison, comparisonCount, removeFromComparison, clearComparison } = useComparison()
const properties = ref<PublicProperty[]>([])
const propertyImages = ref<Record<number, any[]>>({})

const hasRating = computed(() => {
  return properties.value.some(p => (p as any).rating)
})

const hasYearBuilt = computed(() => {
  return properties.value.some(p => (p as any).yearBuilt)
})

const hasCondition = computed(() => {
  return properties.value.some(p => (p as any).condition)
})

onMounted(async () => {
  await loadProperties()
})

watch(comparison, async () => {
  await loadProperties()
}, { deep: true })

async function loadProperties() {
  properties.value = []

  for (const propertyId of comparison.value) {
    try {
      const property = await publicPropertyService.getPublishedPropertyById(propertyId)
      properties.value.push(property)

      // Charger les images
      const documents = await documentService.getByPropertyId(propertyId)
      propertyImages.value[propertyId] = documentService.filterImages(documents)
    } catch (error) {
      console.error(`Error loading property ${propertyId}:`, error)
    }
  }
}

function getImageUrl(property: PublicProperty): string {
  const images = propertyImages.value[property.id]
  if (images && images.length > 0) {
    return documentService.getViewUrl(images[0].id)
  }
  return getPlaceholderImage(400, 300)
}

function handleImageError(event: Event) {
  const img = event.target as HTMLImageElement
  img.src = getPlaceholderImage(400, 300)
}

function getStatusClass(status: string): string {
  switch (status) {
    case 'Disponible':
      return 'bg-green-100 text-green-800'
    case 'Loué':
      return 'bg-yellow-100 text-yellow-800'
    case 'Vendu':
      return 'bg-red-100 text-red-800'
    default:
      return 'bg-gray-100 text-gray-800'
  }
}

function goToDetails(id: number) {
  router.push(`/property/${id}`)
}

function clearAll() {
  if (confirm('Êtes-vous sûr de vouloir supprimer toutes les propriétés de la comparaison ?')) {
    clearComparison()
    properties.value = []
  }
}
</script>

<style scoped>
.sticky {
  position: sticky;
}

.line-clamp-3 {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>

