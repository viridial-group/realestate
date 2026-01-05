<template>
  <div class="bg-white rounded-lg shadow-sm p-6">
    <div class="flex items-center justify-between mb-4">
      <div>
        <h3 class="text-lg font-semibold text-gray-900">{{ title }}</h3>
        <p v-if="subtitle" class="text-sm text-gray-600 mt-1">{{ subtitle }}</p>
      </div>
      <div v-if="showLegend" class="flex items-center gap-4 text-xs">
        <div
          v-for="(series, index) in data"
          :key="index"
          class="flex items-center gap-2"
        >
          <div
            class="w-3 h-3 rounded-full"
            :style="{ backgroundColor: getColor(index) }"
          />
          <span class="text-gray-600">{{ series.label }}</span>
        </div>
      </div>
    </div>

    <div v-if="loading" class="flex items-center justify-center h-64">
      <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
    </div>

    <div v-else-if="error" class="flex items-center justify-center h-64 text-center">
      <div>
        <AlertCircle class="h-12 w-12 text-red-400 mx-auto mb-2" />
        <p class="text-sm text-gray-600">{{ error }}</p>
      </div>
    </div>

    <div v-else-if="!hasData" class="flex items-center justify-center h-64 text-center">
      <div>
        <BarChart3 class="h-12 w-12 text-gray-400 mx-auto mb-2" />
        <p class="text-sm text-gray-600">Aucune donnée disponible</p>
      </div>
    </div>

    <div v-else class="space-y-4">
      <!-- Graphique -->
      <div class="relative h-64 bg-gray-50 rounded-lg p-4 overflow-hidden">
        <svg
          :width="chartWidth"
          :height="chartHeight"
          class="w-full h-full"
          viewBox="0 0 800 200"
          preserveAspectRatio="xMidYMid meet"
        >
          <!-- Grille -->
          <defs>
            <pattern id="grid" width="40" height="20" patternUnits="userSpaceOnUse">
              <path d="M 40 0 L 0 0 0 20" fill="none" stroke="#e5e7eb" stroke-width="0.5"/>
            </pattern>
          </defs>
          <rect width="100%" height="100%" fill="url(#grid)" />

          <!-- Axes -->
          <line
            x1="40"
            y1="160"
            x2="760"
            y2="160"
            stroke="#9ca3af"
            stroke-width="1"
          />
          <line
            x1="40"
            y1="160"
            x2="40"
            y2="20"
            stroke="#9ca3af"
            stroke-width="1"
          />

          <!-- Lignes du graphique -->
          <g v-for="(series, seriesIndex) in chartSeries" :key="seriesIndex">
            <polyline
              :points="series.points"
              fill="none"
              :stroke="getColor(seriesIndex)"
              stroke-width="2"
              class="drop-shadow-sm"
            />
            <!-- Points -->
            <circle
              v-for="(point, pointIndex) in series.pointArray"
              :key="pointIndex"
              :cx="point.x"
              :cy="point.y"
              r="4"
              :fill="getColor(seriesIndex)"
              class="cursor-pointer hover:r-6 transition-all"
            >
              <title>{{ formatTooltip(point.value, series.label) }}</title>
            </circle>
          </g>

          <!-- Labels Y (valeurs) -->
          <g v-for="(label, index) in yLabels" :key="index" class="text-xs fill-gray-600">
            <text
              x="35"
              :y="label.y + 4"
              text-anchor="end"
            >
              {{ formatValue(label.value) }}
            </text>
            <line
              x1="40"
              :y1="label.y"
              x2="760"
              :y2="label.y"
              stroke="#e5e7eb"
              stroke-width="0.5"
              stroke-dasharray="2,2"
            />
          </g>

          <!-- Labels X (dates) -->
          <g v-for="(label, index) in xLabels" :key="index" class="text-xs fill-gray-600">
            <text
              :x="label.x"
              y="175"
              text-anchor="middle"
            >
              {{ formatDate(label.date) }}
            </text>
          </g>
        </svg>
      </div>

      <!-- Statistiques sous le graphique -->
      <div v-if="showStats" class="grid grid-cols-2 md:grid-cols-4 gap-4 pt-4 border-t">
        <div
          v-for="(stat, index) in computedStats"
          :key="index"
          class="text-center"
        >
          <p class="text-xs text-gray-600 mb-1">{{ stat.label }}</p>
          <p class="text-lg font-semibold" :style="{ color: getColor(stat.seriesIndex) }">
            {{ formatValue(stat.value) }}
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { BarChart3, AlertCircle } from 'lucide-vue-next'

export interface ChartDataPoint {
  date: string | Date
  value: number
  label?: string
}

export interface ChartSeries {
  label: string
  data: ChartDataPoint[]
  color?: string
}

interface Props {
  title: string
  subtitle?: string
  data: ChartSeries[]
  loading?: boolean
  error?: string | null
  showLegend?: boolean
  showStats?: boolean
  chartType?: 'line' | 'bar' | 'area'
}

const props = withDefaults(defineProps<Props>(), {
  showLegend: true,
  showStats: true,
  chartType: 'line',
  loading: false,
})

const chartWidth = 800
const chartHeight = 200
const padding = { top: 20, right: 40, bottom: 40, left: 60 }

const hasData = computed(() => {
  return props.data.some(series => series.data && series.data.length > 0)
})

const allDates = computed(() => {
  const dates = new Set<string>()
  props.data.forEach(series => {
    series.data.forEach(point => {
      const dateStr = point.date instanceof Date 
        ? point.date.toISOString().split('T')[0]
        : point.date
      dates.add(dateStr)
    })
  })
  return Array.from(dates).sort()
})

const allValues = computed(() => {
  const values: number[] = []
  props.data.forEach(series => {
    series.data.forEach(point => values.push(point.value))
  })
  return values
})

const minValue = computed(() => {
  if (allValues.value.length === 0) return 0
  return Math.min(...allValues.value)
})

const maxValue = computed(() => {
  if (allValues.value.length === 0) return 100
  const max = Math.max(...allValues.value)
  return max === 0 ? 100 : max * 1.1 // 10% de marge en haut
})

const valueRange = computed(() => maxValue.value - minValue.value || 1)

const chartSeries = computed(() => {
  return props.data.map((series, seriesIndex) => {
    const points: string[] = []
    const pointArray: Array<{ x: number; y: number; value: number }> = []
    
    allDates.value.forEach((date, dateIndex) => {
      const point = series.data.find(p => {
        const pointDate = p.date instanceof Date 
          ? p.date.toISOString().split('T')[0]
          : p.date
        return pointDate === date
      })
      
      const value = point ? point.value : 0
      const x = padding.left + (dateIndex / (allDates.value.length - 1 || 1)) * (chartWidth - padding.left - padding.right)
      const y = padding.top + (chartHeight - padding.top - padding.bottom) - ((value - minValue.value) / valueRange.value) * (chartHeight - padding.top - padding.bottom)
      
      points.push(`${x},${y}`)
      pointArray.push({ x, y, value })
    })
    
    return {
      label: series.label,
      points: points.join(' '),
      pointArray,
      color: series.color || getColor(seriesIndex),
    }
  })
})

const yLabels = computed(() => {
  const steps = 5
  const labels = []
  for (let i = 0; i <= steps; i++) {
    const value = minValue.value + (valueRange.value / steps) * i
    const y = padding.top + (chartHeight - padding.top - padding.bottom) - ((value - minValue.value) / valueRange.value) * (chartHeight - padding.top - padding.bottom)
    labels.push({ value, y })
  }
  return labels
})

const xLabels = computed(() => {
  const step = Math.max(1, Math.floor(allDates.value.length / 6))
  return allDates.value
    .filter((_, index) => index % step === 0 || index === allDates.value.length - 1)
    .map((date) => {
      const x = padding.left + ((allDates.value.indexOf(date)) / (allDates.value.length - 1 || 1)) * (chartWidth - padding.left - padding.right)
      return { date, x }
    })
})

const computedStats = computed(() => {
  return props.data.map((series, index) => {
    const values = series.data.map(p => p.value)
    const total = values.reduce((sum, val) => sum + val, 0)
    const average = values.length > 0 ? total / values.length : 0
    const max = values.length > 0 ? Math.max(...values) : 0
    const min = values.length > 0 ? Math.min(...values) : 0
    
    return [
      { label: 'Total', value: total, seriesIndex: index },
      { label: 'Moyenne', value: average, seriesIndex: index },
      { label: 'Max', value: max, seriesIndex: index },
      { label: 'Min', value: min, seriesIndex: index },
    ]
  }).flat()
})

const colors = [
  'rgb(59, 130, 246)', // blue
  'rgb(16, 185, 129)', // green
  'rgb(245, 158, 11)', // yellow
  'rgb(239, 68, 68)', // red
  'rgb(139, 92, 246)', // purple
  'rgb(236, 72, 153)', // pink
]

function getColor(index: number): string {
  return colors[index % colors.length]
}

function formatValue(value: number): string {
  if (value >= 1000000) {
    return `${(value / 1000000).toFixed(1)}M`
  }
  if (value >= 1000) {
    return `${(value / 1000).toFixed(1)}k`
  }
  return Math.round(value).toString()
}

function formatDate(date: string | Date): string {
  const d = date instanceof Date ? date : new Date(date)
  return d.toLocaleDateString('fr-FR', { month: 'short', day: 'numeric' })
}

function formatTooltip(value: number, label: string): string {
  return `${label}: ${formatValue(value)}`
}
</script>

