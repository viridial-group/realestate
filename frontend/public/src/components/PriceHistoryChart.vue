<template>
  <div class="bg-white rounded-lg shadow-sm p-6">
    <div class="flex items-center gap-2 mb-4">
      <TrendingUp class="h-5 w-5 text-blue-600" />
      <h3 class="text-lg font-semibold text-gray-900">Historique des Prix</h3>
    </div>

    <div v-if="loading" class="flex items-center justify-center p-12">
      <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
    </div>

    <div v-else-if="error" class="text-center p-12">
      <AlertCircle class="h-8 w-8 text-red-500 mx-auto mb-2" />
      <p class="text-sm text-gray-600">{{ error }}</p>
    </div>

    <div v-else-if="!stats || priceHistory.length === 0" class="text-center p-12">
      <TrendingUp class="h-12 w-12 text-gray-400 mx-auto mb-4" />
      <p class="text-sm text-gray-600">Aucun historique de prix disponible</p>
    </div>

    <div v-else class="space-y-6">
      <!-- Statistiques -->
      <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
        <div class="space-y-1">
          <p class="text-xs text-gray-500">Prix actuel</p>
          <p class="text-lg font-bold text-gray-900">{{ formatPrice(stats.currentPrice) }}</p>
        </div>
        <div class="space-y-1">
          <p class="text-xs text-gray-500">Prix initial</p>
          <p class="text-lg font-semibold text-gray-700">{{ formatPrice(stats.initialPrice) }}</p>
        </div>
        <div class="space-y-1">
          <p class="text-xs text-gray-500">Variation</p>
          <div class="flex items-center gap-2">
            <p 
              :class="[
                'text-lg font-semibold',
                stats.totalChangePercent > 0 ? 'text-green-600' : 
                stats.totalChangePercent < 0 ? 'text-red-600' : 'text-gray-600'
              ]"
            >
              {{ stats.totalChangePercent > 0 ? '+' : '' }}{{ stats.totalChangePercent.toFixed(2) }}%
            </p>
            <component 
              :is="stats.totalChangePercent > 0 ? TrendingUp : stats.totalChangePercent < 0 ? TrendingDown : Minus"
              :class="[
                'h-4 w-4',
                stats.totalChangePercent > 0 ? 'text-green-600' : 
                stats.totalChangePercent < 0 ? 'text-red-600' : 'text-gray-400'
              ]"
            />
          </div>
        </div>
        <div class="space-y-1">
          <p class="text-xs text-gray-500">Changements</p>
          <p class="text-lg font-semibold text-gray-700">{{ stats.totalChanges }}</p>
        </div>
      </div>

      <!-- Graphique SVG simple -->
      <div class="relative h-64 bg-gray-50 rounded-lg p-4">
        <svg :width="chartWidth" :height="chartHeight" class="w-full h-full">
          <!-- Ligne de prix -->
          <polyline
            :points="chartPoints"
            fill="none"
            stroke="rgb(59, 130, 246)"
            stroke-width="2"
            class="drop-shadow-sm"
          />
          <!-- Zone remplie -->
          <polygon
            :points="filledAreaPoints"
            fill="rgba(59, 130, 246, 0.1)"
          />
          <!-- Points -->
          <circle
            v-for="(point, index) in chartPointsArray"
            :key="index"
            :cx="point.x"
            :cy="point.y"
            r="4"
            fill="rgb(59, 130, 246)"
            class="cursor-pointer hover:r-6 transition-all"
          >
            <title>{{ formatPrice(priceHistory[index].price) }} - {{ formatDateShort(priceHistory[index].changeDate) }}</title>
          </circle>
        </svg>
        <!-- Labels des axes -->
        <div class="absolute bottom-0 left-0 right-0 flex justify-between text-xs text-gray-500 px-4">
          <span>{{ formatDateShort(priceHistory[priceHistory.length - 1]?.changeDate || '') }}</span>
          <span>{{ formatDateShort(priceHistory[0]?.changeDate || '') }}</span>
        </div>
      </div>

      <!-- Liste des changements -->
      <div class="space-y-2">
        <h4 class="text-sm font-semibold text-gray-900">Historique détaillé</h4>
        <div class="space-y-2 max-h-64 overflow-y-auto">
          <div
            v-for="(entry, index) in priceHistory"
            :key="entry.id"
            class="flex items-center justify-between p-3 border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors"
          >
            <div class="flex items-center gap-4">
              <div class="flex flex-col items-center min-w-[60px]">
                <div 
                  :class="[
                    'w-3 h-3 rounded-full',
                    index === 0 ? 'bg-blue-600' : 
                    getPriceChange(index) > 0 ? 'bg-green-500' : 
                    getPriceChange(index) < 0 ? 'bg-red-500' : 'bg-gray-400'
                  ]"
                />
                <div 
                  v-if="index < priceHistory.length - 1"
                  :class="[
                    'w-0.5 h-8',
                    getPriceChange(index) > 0 ? 'bg-green-500' : 
                    getPriceChange(index) < 0 ? 'bg-red-500' : 'bg-gray-400'
                  ]"
                />
              </div>
              <div>
                <p class="font-semibold text-gray-900">{{ formatPrice(entry.price) }}</p>
                <p class="text-xs text-gray-500">
                  {{ formatDate(entry.changeDate) }}
                </p>
                <p v-if="entry.changeReason" class="text-xs text-gray-500 mt-1">
                  {{ entry.changeReason }}
                </p>
              </div>
            </div>
            <div class="text-right">
              <span 
                v-if="index > 0"
                :class="[
                  'px-2 py-1 rounded-full text-xs font-medium',
                  getPriceChange(index) > 0 ? 'bg-green-100 text-green-700' : 
                  getPriceChange(index) < 0 ? 'bg-red-100 text-red-700' : 'bg-gray-100 text-gray-700'
                ]"
              >
                {{ getPriceChange(index) > 0 ? '+' : '' }}{{ formatPriceChange(index) }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { priceHistoryService } from '@viridial/shared'
import type { PriceHistory, PriceHistoryStats } from '@viridial/shared'
import { TrendingUp, TrendingDown, Minus, AlertCircle } from 'lucide-vue-next'

interface Props {
  propertyId: number
}

const props = defineProps<Props>()

const priceHistory = ref<PriceHistory[]>([])
const stats = ref<PriceHistoryStats | null>(null)
const loading = ref(false)
const error = ref<string | null>(null)

const chartWidth = 800
const chartHeight = 200
const padding = 40

// Données du graphique SVG
const chartData = computed(() => {
  if (priceHistory.value.length === 0) return { points: [], minPrice: 0, maxPrice: 0 }
  
  const sortedHistory = [...priceHistory.value].sort((a, b) => 
    new Date(a.changeDate).getTime() - new Date(b.changeDate).getTime()
  )
  
  const prices = sortedHistory.map(entry => entry.price)
  const minPrice = Math.min(...prices)
  const maxPrice = Math.max(...prices)
  const priceRange = maxPrice - minPrice || 1
  
  const points = sortedHistory.map((entry, index) => {
    const x = padding + (index / (sortedHistory.length - 1 || 1)) * (chartWidth - 2 * padding)
    const y = chartHeight - padding - ((entry.price - minPrice) / priceRange) * (chartHeight - 2 * padding)
    return { x, y, price: entry.price, date: entry.changeDate }
  })
  
  return { points, minPrice, maxPrice }
})

const chartPoints = computed(() => {
  return chartData.value.points.map(p => `${p.x},${p.y}`).join(' ')
})

const chartPointsArray = computed(() => {
  return chartData.value.points
})

const filledAreaPoints = computed(() => {
  const points = chartData.value.points
  if (points.length === 0) return ''
  const firstPoint = `${points[0].x},${chartHeight - padding}`
  const lastPoint = `${points[points.length - 1].x},${chartHeight - padding}`
  return `${firstPoint} ${chartPoints.value} ${lastPoint}`
})

// Methods
const loadPriceHistory = async () => {
  loading.value = true
  error.value = null
  try {
    const [history, statsData] = await Promise.all([
      priceHistoryService.getByProperty(props.propertyId),
      priceHistoryService.getStatsByProperty(props.propertyId)
    ])
    priceHistory.value = history
    stats.value = statsData
  } catch (err: any) {
    console.error('Error loading price history:', err)
    error.value = err.message || 'Impossible de charger l\'historique des prix'
  } finally {
    loading.value = false
  }
}

const formatPrice = (price: number): string => {
  return new Intl.NumberFormat('fr-FR', {
    style: 'currency',
    currency: 'EUR'
  }).format(price)
}

const formatDate = (dateString: string): string => {
  const date = new Date(dateString)
  return date.toLocaleDateString('fr-FR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const formatDateShort = (dateString: string): string => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('fr-FR', {
    month: 'short',
    day: 'numeric'
  })
}

const getPriceChange = (index: number): number => {
  if (index === 0 || index >= priceHistory.value.length) return 0
  if (index + 1 >= priceHistory.value.length) return 0
  const current = priceHistory.value[index]
  const previous = priceHistory.value[index + 1]
  if (!current || !previous) return 0
  return current.price - previous.price
}

const formatPriceChange = (index: number): string => {
  const change = getPriceChange(index)
  return formatPrice(Math.abs(change))
}

onMounted(() => {
  loadPriceHistory()
})
</script>

