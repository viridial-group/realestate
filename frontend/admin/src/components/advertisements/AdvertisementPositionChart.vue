<template>
  <div class="w-full h-[300px]">
    <Doughnut v-if="chartData" :data="chartData" :options="chartOptions" />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { Doughnut } from 'vue-chartjs'
import {
  Chart as ChartJS,
  ArcElement,
  Tooltip,
  Legend
} from 'chart.js'
import type { PositionStats } from '@viridial/shared'

ChartJS.register(
  ArcElement,
  Tooltip,
  Legend
)

interface Props {
  data: Record<string, PositionStats>
}

const props = defineProps<Props>()

const positionLabels: Record<string, string> = {
  TOP: 'Haut',
  BOTTOM: 'Bas',
  SIDEBAR_LEFT: 'Barre latérale gauche',
  SIDEBAR_RIGHT: 'Barre latérale droite',
  INLINE: 'Inline',
  HEADER: 'En-tête',
  FOOTER: 'Pied de page'
}

const colors = [
  'rgba(59, 130, 246, 0.8)',
  'rgba(168, 85, 247, 0.8)',
  'rgba(34, 197, 94, 0.8)',
  'rgba(251, 146, 60, 0.8)',
  'rgba(239, 68, 68, 0.8)',
  'rgba(236, 72, 153, 0.8)',
  'rgba(14, 165, 233, 0.8)'
]

const chartData = computed(() => {
  if (!props.data || Object.keys(props.data).length === 0) {
    return null
  }

  const entries = Object.values(props.data).sort((a, b) => b.impressions - a.impressions)

  return {
    labels: entries.map(e => positionLabels[e.position] || e.position),
    datasets: [
      {
        label: 'Impressions',
        data: entries.map(e => e.impressions),
        backgroundColor: entries.map((_, i) => colors[i % colors.length]),
        borderColor: entries.map((_, i) => colors[i % colors.length].replace('0.8', '1')),
        borderWidth: 2
      }
    ]
  }
})

const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      position: 'right' as const,
      labels: {
        color: 'rgb(107, 114, 128)',
        padding: 15,
        usePointStyle: true
      }
    },
    tooltip: {
      backgroundColor: 'rgba(0, 0, 0, 0.8)',
      titleColor: 'rgb(255, 255, 255)',
      bodyColor: 'rgb(255, 255, 255)',
      borderColor: 'rgba(255, 255, 255, 0.1)',
      borderWidth: 1,
      callbacks: {
        afterLabel: (context: any) => {
          const index = context.dataIndex
          const entries = Object.values(props.data).sort((a: any, b: any) => b.impressions - a.impressions)
          const entry = entries[index] as PositionStats
          return [
            `Clics: ${entry.clicks.toLocaleString()}`,
            `CTR: ${entry.ctr?.toFixed(2) || '0.00'}%`,
            `CVR: ${entry.cvr?.toFixed(2) || '0.00'}%`
          ]
        }
      }
    }
  }
}
</script>

