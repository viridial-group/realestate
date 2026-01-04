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
import type { WeeklyStats } from '@viridial/shared'

ChartJS.register(
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend
)

interface Props {
  data: WeeklyStats[]
}

const props = defineProps<Props>()

const chartData = computed(() => {
  if (!props.data || props.data.length === 0) {
    return null
  }

  const sortedData = [...props.data].sort((a, b) => 
    new Date(a.weekStart).getTime() - new Date(b.weekStart).getTime()
  )

  return {
    labels: sortedData.map(d => {
      const start = new Date(d.weekStart)
      const end = new Date(d.weekEnd)
      return `${start.toLocaleDateString('fr-FR', { day: '2-digit', month: '2-digit' })} - ${end.toLocaleDateString('fr-FR', { day: '2-digit', month: '2-digit' })}`
    }),
    datasets: [
      {
        label: 'Impressions',
        data: sortedData.map(d => d.impressions),
        backgroundColor: 'rgba(59, 130, 246, 0.8)',
        borderColor: 'rgb(59, 130, 246)',
        borderWidth: 1
      },
      {
        label: 'Clics',
        data: sortedData.map(d => d.clicks),
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
      borderWidth: 1
    }
  },
  scales: {
    x: {
      grid: {
        display: false
      },
      ticks: {
        color: 'rgb(107, 114, 128)',
        maxRotation: 45,
        minRotation: 45
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

