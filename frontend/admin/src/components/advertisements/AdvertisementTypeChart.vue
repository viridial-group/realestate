<template>
  <div class="w-full h-[300px]">
    <Bar v-if="chartData" :data="chartData" :options="chartOptions" />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { Bar } from 'vue-chartjs'
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend
} from 'chart.js'
import type { TypeStats } from '@viridial/shared'

ChartJS.register(
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend
)

interface Props {
  data: Record<string, TypeStats>
}

const props = defineProps<Props>()

const typeLabels: Record<string, string> = {
  BANNER: 'Bannière',
  SIDEBAR: 'Barre latérale',
  INLINE: 'Inline',
  POPUP: 'Popup',
  SPONSORED_PROPERTY: 'Propriété sponsorisée'
}

const chartData = computed(() => {
  if (!props.data || Object.keys(props.data).length === 0) {
    return null
  }

  const entries = Object.values(props.data).sort((a, b) => b.impressions - a.impressions)

  return {
    labels: entries.map(e => typeLabels[e.type] || e.type),
    datasets: [
      {
        label: 'Impressions',
        data: entries.map(e => e.impressions),
        backgroundColor: 'rgba(59, 130, 246, 0.8)',
        borderColor: 'rgb(59, 130, 246)',
        borderWidth: 1
      },
      {
        label: 'Clics',
        data: entries.map(e => e.clicks),
        backgroundColor: 'rgba(168, 85, 247, 0.8)',
        borderColor: 'rgb(168, 85, 247)',
        borderWidth: 1
      }
    ]
  }
})

const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      position: 'top' as const,
      labels: {
        color: 'rgb(107, 114, 128)',
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
          const entry = entries[index] as TypeStats
          return [
            `CTR: ${entry.ctr?.toFixed(2) || '0.00'}%`,
            `CVR: ${entry.cvr?.toFixed(2) || '0.00'}%`
          ]
        }
      }
    }
  },
  scales: {
    x: {
      grid: {
        display: false
      },
      ticks: {
        color: 'rgb(107, 114, 128)'
      }
    },
    y: {
      beginAtZero: true,
      grid: {
        color: 'rgba(0, 0, 0, 0.05)'
      },
      ticks: {
        color: 'rgb(107, 114, 128)'
      }
    }
  }
}
</script>

