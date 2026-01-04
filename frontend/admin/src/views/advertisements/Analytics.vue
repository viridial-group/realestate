<template>
  <div class="p-6 space-y-6">
    <!-- En-tête -->
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-3xl font-bold text-gray-900 dark:text-white">Analytics des Annonces</h1>
        <p class="text-gray-600 dark:text-gray-400 mt-1">Analysez les performances de vos campagnes publicitaires</p>
      </div>
      <div class="flex items-center gap-4">
        <!-- Filtres de période -->
        <div class="flex items-center gap-2">
          <label class="text-sm font-medium text-gray-700 dark:text-gray-300">Du</label>
          <input
            v-model="startDate"
            type="date"
            class="px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg dark:bg-gray-700 dark:text-white"
            @change="loadAnalytics"
          />
        </div>
        <div class="flex items-center gap-2">
          <label class="text-sm font-medium text-gray-700 dark:text-gray-300">Au</label>
          <input
            v-model="endDate"
            type="date"
            class="px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg dark:bg-gray-700 dark:text-white"
            @change="loadAnalytics"
          />
        </div>
        <button
          @click="loadAnalytics"
          class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors font-medium"
          :disabled="loading"
        >
          <span v-if="loading">Chargement...</span>
          <span v-else>Actualiser</span>
        </button>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="flex items-center justify-center py-12">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
    </div>

    <!-- Analytics Content -->
    <div v-else-if="analytics" class="space-y-6">
      <!-- Statistiques Globales -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <div class="bg-white dark:bg-gray-800 rounded-lg shadow-sm p-6">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm text-gray-600 dark:text-gray-400">Total Annonces</p>
              <p class="text-2xl font-bold text-gray-900 dark:text-white mt-1">
                {{ analytics.totalAdvertisements }}
              </p>
              <p class="text-xs text-gray-500 dark:text-gray-500 mt-1">
                {{ analytics.activeAdvertisements }} actives
              </p>
            </div>
            <div class="p-3 bg-blue-100 dark:bg-blue-900 rounded-full">
              <svg class="w-6 h-6 text-blue-600 dark:text-blue-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A1.994 1.994 0 013 12V7a4 4 0 014-4z" />
              </svg>
            </div>
          </div>
        </div>

        <div class="bg-white dark:bg-gray-800 rounded-lg shadow-sm p-6">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm text-gray-600 dark:text-gray-400">Impressions</p>
              <p class="text-2xl font-bold text-gray-900 dark:text-white mt-1">
                {{ formatNumber(analytics.totalImpressions) }}
              </p>
              <p class="text-xs text-gray-500 dark:text-gray-500 mt-1">
                Total
              </p>
            </div>
            <div class="p-3 bg-green-100 dark:bg-green-900 rounded-full">
              <svg class="w-6 h-6 text-green-600 dark:text-green-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
              </svg>
            </div>
          </div>
        </div>

        <div class="bg-white dark:bg-gray-800 rounded-lg shadow-sm p-6">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm text-gray-600 dark:text-gray-400">Clics</p>
              <p class="text-2xl font-bold text-gray-900 dark:text-white mt-1">
                {{ formatNumber(analytics.totalClicks) }}
              </p>
              <p class="text-xs text-gray-500 dark:text-gray-500 mt-1">
                CTR: {{ analytics.averageCTR?.toFixed(2) || '0.00' }}%
              </p>
            </div>
            <div class="p-3 bg-purple-100 dark:bg-purple-900 rounded-full">
              <svg class="w-6 h-6 text-purple-600 dark:text-purple-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 15l-2 5L9 9l11 4-5 2zm0 0l5 5M7.188 2.239l.777 2.897M5.136 7.965l-2.898-.777M13.95 4.05l-2.122 2.122m-5.657 5.656l-2.12 2.122" />
              </svg>
            </div>
          </div>
        </div>

        <div class="bg-white dark:bg-gray-800 rounded-lg shadow-sm p-6">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm text-gray-600 dark:text-gray-400">Dépenses</p>
              <p class="text-2xl font-bold text-gray-900 dark:text-white mt-1">
                {{ formatCurrency(analytics.totalSpent) }}
              </p>
              <p class="text-xs text-gray-500 dark:text-gray-500 mt-1">
                CPC: {{ formatCurrency(analytics.averageCPC) }}
              </p>
            </div>
            <div class="p-3 bg-orange-100 dark:bg-orange-900 rounded-full">
              <svg class="w-6 h-6 text-orange-600 dark:text-orange-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>
          </div>
        </div>
      </div>

      <!-- Graphiques d'évolution -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <!-- Évolution quotidienne -->
        <div class="bg-white dark:bg-gray-800 rounded-lg shadow-sm p-6">
          <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-4">Évolution Quotidienne</h3>
          <AdvertisementDailyChart :data="analytics.dailyStats" />
        </div>

        <!-- Évolution hebdomadaire -->
        <div class="bg-white dark:bg-gray-800 rounded-lg shadow-sm p-6">
          <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-4">Évolution Hebdomadaire</h3>
          <AdvertisementWeeklyChart :data="analytics.weeklyStats" />
        </div>
      </div>

      <!-- Graphique mensuel -->
      <div class="bg-white dark:bg-gray-800 rounded-lg shadow-sm p-6">
        <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-4">Évolution Mensuelle</h3>
        <AdvertisementMonthlyChart :data="analytics.monthlyStats" />
      </div>

      <!-- Statistiques par type et position -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <!-- Par type -->
        <div class="bg-white dark:bg-gray-800 rounded-lg shadow-sm p-6">
          <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-4">Performance par Type</h3>
          <AdvertisementTypeChart :data="analytics.statsByType" />
        </div>

        <!-- Par position -->
        <div class="bg-white dark:bg-gray-800 rounded-lg shadow-sm p-6">
          <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-4">Performance par Position</h3>
          <AdvertisementPositionChart :data="analytics.statsByPosition" />
        </div>
      </div>

      <!-- Top annonces -->
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <!-- Top par impressions -->
        <div class="bg-white dark:bg-gray-800 rounded-lg shadow-sm p-6">
          <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-4">Top par Impressions</h3>
          <div class="space-y-3">
            <div
              v-for="(ad, index) in analytics.topByImpressions"
              :key="ad.id"
              class="flex items-center justify-between p-3 bg-gray-50 dark:bg-gray-700 rounded-lg"
            >
              <div class="flex items-center gap-3">
                <div class="w-8 h-8 rounded-full bg-blue-100 dark:bg-blue-900 flex items-center justify-center text-sm font-bold text-blue-600 dark:text-blue-400">
                  {{ index + 1 }}
                </div>
                <div>
                  <p class="text-sm font-medium text-gray-900 dark:text-white">{{ ad.title }}</p>
                  <p class="text-xs text-gray-500 dark:text-gray-400">{{ formatNumber(ad.impressions) }} impressions</p>
                </div>
              </div>
              <div class="text-right">
                <p class="text-sm font-semibold text-gray-900 dark:text-white">{{ formatNumber(ad.clicks) }}</p>
                <p class="text-xs text-gray-500 dark:text-gray-400">{{ ad.ctr?.toFixed(2) || '0.00' }}% CTR</p>
              </div>
            </div>
          </div>
        </div>

        <!-- Top par clics -->
        <div class="bg-white dark:bg-gray-800 rounded-lg shadow-sm p-6">
          <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-4">Top par Clics</h3>
          <div class="space-y-3">
            <div
              v-for="(ad, index) in analytics.topByClicks"
              :key="ad.id"
              class="flex items-center justify-between p-3 bg-gray-50 dark:bg-gray-700 rounded-lg"
            >
              <div class="flex items-center gap-3">
                <div class="w-8 h-8 rounded-full bg-green-100 dark:bg-green-900 flex items-center justify-center text-sm font-bold text-green-600 dark:text-green-400">
                  {{ index + 1 }}
                </div>
                <div>
                  <p class="text-sm font-medium text-gray-900 dark:text-white">{{ ad.title }}</p>
                  <p class="text-xs text-gray-500 dark:text-gray-400">{{ formatNumber(ad.clicks) }} clics</p>
                </div>
              </div>
              <div class="text-right">
                <p class="text-sm font-semibold text-gray-900 dark:text-white">{{ formatNumber(ad.impressions) }}</p>
                <p class="text-xs text-gray-500 dark:text-gray-400">{{ ad.ctr?.toFixed(2) || '0.00' }}% CTR</p>
              </div>
            </div>
          </div>
        </div>

        <!-- Top par conversions -->
        <div class="bg-white dark:bg-gray-800 rounded-lg shadow-sm p-6">
          <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-4">Top par Conversions</h3>
          <div class="space-y-3">
            <div
              v-for="(ad, index) in analytics.topByConversions"
              :key="ad.id"
              class="flex items-center justify-between p-3 bg-gray-50 dark:bg-gray-700 rounded-lg"
            >
              <div class="flex items-center gap-3">
                <div class="w-8 h-8 rounded-full bg-purple-100 dark:bg-purple-900 flex items-center justify-center text-sm font-bold text-purple-600 dark:text-purple-400">
                  {{ index + 1 }}
                </div>
                <div>
                  <p class="text-sm font-medium text-gray-900 dark:text-white">{{ ad.title }}</p>
                  <p class="text-xs text-gray-500 dark:text-gray-400">{{ formatNumber(ad.conversions) }} conversions</p>
                </div>
              </div>
              <div class="text-right">
                <p class="text-sm font-semibold text-gray-900 dark:text-white">{{ formatNumber(ad.clicks) }}</p>
                <p class="text-xs text-gray-500 dark:text-gray-400">{{ ad.cvr?.toFixed(2) || '0.00' }}% CVR</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 rounded-lg p-4">
      <p class="text-red-800 dark:text-red-200">{{ error }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { advertisementService } from '@viridial/shared'
import type { AdvertisementAnalytics } from '@viridial/shared'
import AdvertisementDailyChart from '@/components/advertisements/AdvertisementDailyChart.vue'
import AdvertisementWeeklyChart from '@/components/advertisements/AdvertisementWeeklyChart.vue'
import AdvertisementMonthlyChart from '@/components/advertisements/AdvertisementMonthlyChart.vue'
import AdvertisementTypeChart from '@/components/advertisements/AdvertisementTypeChart.vue'
import AdvertisementPositionChart from '@/components/advertisements/AdvertisementPositionChart.vue'

const analytics = ref<AdvertisementAnalytics | null>(null)
const loading = ref(false)
const error = ref<string | null>(null)

// Dates par défaut (30 derniers jours)
const startDate = ref<string>(
  new Date(Date.now() - 30 * 24 * 60 * 60 * 1000).toISOString().split('T')[0]
)
const endDate = ref<string>(new Date().toISOString().split('T')[0])

async function loadAnalytics() {
  loading.value = true
  error.value = null
  
  try {
    analytics.value = await advertisementService.getAnalytics({
      startDate: startDate.value,
      endDate: endDate.value
    })
  } catch (err: any) {
    error.value = err.message || 'Erreur lors du chargement des analytics'
    console.error('Error loading analytics:', err)
  } finally {
    loading.value = false
  }
}

function formatNumber(value: number): string {
  if (value >= 1000000) {
    return (value / 1000000).toFixed(1) + 'M'
  }
  if (value >= 1000) {
    return (value / 1000).toFixed(1) + 'K'
  }
  return value.toString()
}

function formatCurrency(value?: number): string {
  if (!value) return '0,00 €'
  return new Intl.NumberFormat('fr-FR', {
    style: 'currency',
    currency: 'EUR'
  }).format(value)
}

onMounted(() => {
  loadAnalytics()
})
</script>

