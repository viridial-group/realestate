<template>
  <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
    <div class="bg-white dark:bg-gray-800 rounded-lg shadow-xl max-w-3xl w-full max-h-[90vh] overflow-y-auto">
      <div class="p-6">
        <!-- En-tête -->
        <div class="flex items-center justify-between mb-6">
          <h2 class="text-2xl font-bold text-gray-900 dark:text-white">Statistiques de l'annonce</h2>
          <button
            @click="$emit('close')"
            class="text-gray-400 hover:text-gray-600 dark:hover:text-gray-200"
          >
            ✕
          </button>
        </div>

        <!-- Statistiques -->
        <div v-if="loading" class="text-center py-8">
          <div class="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
        </div>
        <div v-else-if="stats" class="space-y-6">
          <!-- Métriques principales -->
          <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
            <div class="bg-blue-50 dark:bg-blue-900/20 rounded-lg p-4">
              <div class="text-sm text-gray-600 dark:text-gray-400">Impressions</div>
              <div class="text-2xl font-bold text-blue-600 dark:text-blue-400">
                {{ stats.impressions.toLocaleString() }}
              </div>
            </div>
            <div class="bg-green-50 dark:bg-green-900/20 rounded-lg p-4">
              <div class="text-sm text-gray-600 dark:text-gray-400">Clics</div>
              <div class="text-2xl font-bold text-green-600 dark:text-green-400">
                {{ stats.clicks.toLocaleString() }}
              </div>
            </div>
            <div class="bg-purple-50 dark:bg-purple-900/20 rounded-lg p-4">
              <div class="text-sm text-gray-600 dark:text-gray-400">Conversions</div>
              <div class="text-2xl font-bold text-purple-600 dark:text-purple-400">
                {{ stats.conversions.toLocaleString() }}
              </div>
            </div>
            <div class="bg-orange-50 dark:bg-orange-900/20 rounded-lg p-4">
              <div class="text-sm text-gray-600 dark:text-gray-400">Dépensé</div>
              <div class="text-2xl font-bold text-orange-600 dark:text-orange-400">
                €{{ stats.totalSpent.toLocaleString() }}
              </div>
            </div>
          </div>

          <!-- Taux de performance -->
          <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div class="bg-white dark:bg-gray-700 rounded-lg p-4 border border-gray-200 dark:border-gray-600">
              <div class="text-sm text-gray-600 dark:text-gray-400 mb-1">Taux de clic (CTR)</div>
              <div class="text-xl font-bold text-gray-900 dark:text-white">
                {{ stats.clickThroughRate ? stats.clickThroughRate.toFixed(2) : '0.00' }}%
              </div>
            </div>
            <div class="bg-white dark:bg-gray-700 rounded-lg p-4 border border-gray-200 dark:border-gray-600">
              <div class="text-sm text-gray-600 dark:text-gray-400 mb-1">Taux de conversion (CR)</div>
              <div class="text-xl font-bold text-gray-900 dark:text-white">
                {{ stats.conversionRate ? stats.conversionRate.toFixed(2) : '0.00' }}%
              </div>
            </div>
            <div class="bg-white dark:bg-gray-700 rounded-lg p-4 border border-gray-200 dark:border-gray-600">
              <div class="text-sm text-gray-600 dark:text-gray-400 mb-1">Coût par conversion (CPA)</div>
              <div class="text-xl font-bold text-gray-900 dark:text-white">
                €{{ stats.costPerConversion ? stats.costPerConversion.toFixed(2) : '0.00' }}
              </div>
            </div>
          </div>

          <!-- Informations -->
          <div class="bg-gray-50 dark:bg-gray-700 rounded-lg p-4">
            <h3 class="font-semibold text-gray-900 dark:text-white mb-2">Informations</h3>
            <div class="grid grid-cols-2 gap-4 text-sm">
              <div>
                <span class="text-gray-600 dark:text-gray-400">Titre:</span>
                <span class="ml-2 text-gray-900 dark:text-white">{{ stats.title }}</span>
              </div>
              <div>
                <span class="text-gray-600 dark:text-gray-400">Statut:</span>
                <span class="ml-2 text-gray-900 dark:text-white">{{ stats.status }}</span>
              </div>
              <div>
                <span class="text-gray-600 dark:text-gray-400">Date de début:</span>
                <span class="ml-2 text-gray-900 dark:text-white">{{ formatDate(stats.startDate) }}</span>
              </div>
              <div v-if="stats.endDate">
                <span class="text-gray-600 dark:text-gray-400">Date de fin:</span>
                <span class="ml-2 text-gray-900 dark:text-white">{{ formatDate(stats.endDate) }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { advertisementService, type AdvertisementStats } from '@viridial/shared'

const props = defineProps<{
  advertisementId: number
}>()

const emit = defineEmits<{
  close: []
}>()

const stats = ref<AdvertisementStats | null>(null)
const loading = ref(false)

watch(() => props.advertisementId, () => {
  if (props.advertisementId) {
    loadStats()
  }
}, { immediate: true })

onMounted(() => {
  if (props.advertisementId) {
    loadStats()
  }
})

async function loadStats() {
  loading.value = true
  try {
    stats.value = await advertisementService.getStats(props.advertisementId)
  } catch (error: any) {
    console.error('Error loading stats:', error)
    alert('Erreur lors du chargement des statistiques')
  } finally {
    loading.value = false
  }
}

function formatDate(date: string): string {
  return new Date(date).toLocaleDateString('fr-FR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}
</script>

