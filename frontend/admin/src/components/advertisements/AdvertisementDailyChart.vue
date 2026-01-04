<template>
  <div class="w-full h-[300px]">
    <Line v-if="chartData" :data="chartData" :options="chartOptions" />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { Line } from 'vue-chartjs'
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  Filler
} from 'chart.js'
import type { DailyStats } from '@viridial/shared'

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  Filler
)

interface Props {
  data: DailyStats[]
}

const props = defineProps<Props>()

const chartData = computed(() => {
  if (!props.data || props.data.length === 0) {
    return null
  }

  const sortedData = [...props.data].sort((a, b) => 
    new Date(a.date).getTime() - new Date(b.date).getTime()
  )

  return {
    labels: sortedData.map(d => {
      const date = new Date(d.date)
      return date.toLocaleDateString('fr-FR', { day: '2-digit', month: '2-digit' })
    }),
    datasets: [
      {
        label: 'Impressions',
        data: sortedData.map(d => d.impressions),
        borderColor: 'rgb(59, 130, 246)',
        backgroundColor: 'rgba(59, 130, 246, 0.1)',
        borderWidth: 2,
        fill: true,
        tension: 0.4,
        yAxisID: 'y'
      },
      {
        label: 'Clics',
        data: sortedData.map(d => d.clicks),
        borderColor: 'rgb(168, 85, 247)',
        backgroundColor: 'rgba(168, 85, 247, 0.1)',
        borderWidth: 2,
        fill: true,
        tension: 0.4,
        yAxisID: 'y'
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
      mode: 'index' as const,
      intersect: false,
      backgroundColor: 'rgba(0, 0, 0, 0.8)',
      titleColor: 'rgb(255, 255, 255)',
      bodyColor: 'rgb(255, 255, 255)',
      borderColor: 'rgba(255, 255, 255, 0.1)',
      borderWidth: 1
    }
  },
  scales: {
    x: {
      grid: {
        color: 'rgba(0, 0, 0, 0.05)'
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

